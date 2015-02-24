/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  ClassDescriptionLsCmd
 *
 * @author GaryRegier
 * @date   Jun 4, 2011
 */
public class ClassDefinitionLsCmd extends BaseCommand {
	
	private static final String 
	CMD = "czdls",
	CMD_DESC = "Class definition listing",
	HELP_TEXT = "\nUsage:" +
		"\nczdls" + 
		"\n\t List all root class definitions" +
		"\nczdls Document"  +
		"\n\tList all subclasses of the Document class definition." +
		"\nczdls -tree" +
		"\n\tList all class definitions as a tree." +
		"\nczdls -tree Document" +
		"\n\tList all class definitions as a tree where the root class definition is Document."; 
	// param names
	private static final String 
			TREE_OPT = "tree",
			ROOT_CLASS_DESCRIPTION_ARG = "rootclass";
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam treeOpt = (BooleanParam) cl.getOption(TREE_OPT);
		StringParam rootClassDescriptionParam = (StringParam) cl.getArg(ROOT_CLASS_DESCRIPTION_ARG);
		Boolean displayAsTree = false;
		String rootDocClass = null;
		
		displayAsTree = treeOpt.getValue();
		if (rootClassDescriptionParam.isSet()) {
			rootDocClass = rootClassDescriptionParam.getValue();
		}
		
		return classDefinitionLs(displayAsTree, rootDocClass);
	}

	/**
	 * 
	 * @param displayAsTree
	 * @param rootDocClass
	 */
	public boolean classDefinitionLs(Boolean displayAsTree, String rootClassName) 
			throws Exception {
		int startingLevel = 0;
		Map<String, ClassDefinitionInfo> allClasses = 
			new HashMap<String,	ClassDefinitionInfo>();
		
		if (rootClassName == null) {
			rootClassName = ClassDefinitionInfo.ROOT_NAME;
		}
		fetchClassDefinitionInfo(startingLevel,	allClasses);
	    
	    // verify whether we find the root doc class we're looking for
	    if (! allClasses.containsKey(rootClassName)) {
	    	throw new IllegalArgumentException("class with symbolic name " + 
	    			rootClassName + " was not found");
	    }

		ClassDefinitionInfo startingInfo = allClasses.get(rootClassName);
		
		if (displayAsTree) {
			formatTreeResults(rootClassName, allClasses);
		} else {
			formatFlatResults(startingInfo.getChildren());
		}
		return true;
	}

	private void configureClassDefsWithParents(
			Map<String, ClassDefinitionInfo> allClasses) {
		// put the info objects into their child sets 
		for (Iterator<ClassDefinitionInfo> iter = allClasses.values().iterator(); 
				iter.hasNext();) {
			ClassDefinitionInfo next = null;
			String parentName = null;
	
			next = iter.next();
			parentName = next.getParentName();
			if (parentName != null) {
				ClassDefinitionInfo parent = allClasses.get(parentName); 
				parent.addChild(next);
			} 
		}
	}
	
	private void formatTreeResults(String rootClassName,
			Map<String, ClassDefinitionInfo> allClasses) {
		List<ClassDefinitionInfo> orderedCDs = new ArrayList<ClassDefinitionInfo>();
		ClassDefinitionInfo startingInfo = allClasses.get(rootClassName);
		orderedCDs.add(startingInfo);
		initChildLevels(startingInfo, orderedCDs);
		formatResults(orderedCDs, true);
	}
	
	/*
	 * 
	 */
	private void fetchClassDefinitionInfo(int startingLevel,
			Map<String, ClassDefinitionInfo> allClasses) {
		String query;
		ClassDefinitionInfo rootInfo = null;
		
		rootInfo = new ClassDefinitionInfo("", 
				ClassDefinitionInfo.ROOT_NAME, startingLevel);
		allClasses.put("root", rootInfo);
		
		query = "select p.Id pId, p.SymbolicName pName, c.Id cId, c.SymbolicName cName, c.Creator creator, c.DateCreated, c.IsSystemOwned, c.IsHidden from ClassDefinition c Left JOIN ClassDefinition p ON c.SuperclassDefinition = p.this";
	
		SearchSQL sqlObject = new SearchSQL();
	    SearchScope searchScope = new SearchScope(getShell().getObjectStore());
	    RepositoryRowSet rowSet = null; 
	    
	    sqlObject.setQueryString(query);
	    rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    
	    for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	com.filenet.api.property.Properties props = 
				row.getProperties();
	    	ClassDefinitionInfo nextDc = readItem(props);
	    	allClasses.put(nextDc.getSymbolicName(), nextDc);
	    }
	    configureClassDefsWithParents(allClasses);
	}

	private void formatResults(List<ClassDefinitionInfo> orderedDcs, 
			boolean hierarchy) {
		ColDef[] defs = new ColDef[] {
			new ColDef("Name", 50, StringUtil.ALIGN_LEFT),
			new ColDef("Creator", 20, StringUtil.ALIGN_LEFT),
			new ColDef("Hidden", 6, StringUtil.ALIGN_LEFT),
			new ColDef("System", 6, StringUtil.ALIGN_RIGHT)
		};
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		for(Iterator<ClassDefinitionInfo> iter = orderedDcs.iterator(); iter.hasNext();) {
			String padChars = "";
			ClassDefinitionInfo nextInfo = iter.next();
			if (hierarchy) {
				padChars = createpadChars(nextInfo.getLevel());
			}
			String[] row = new String[] {
					padChars + nextInfo.getSymbolicName(),
					"".equals(nextInfo.getCreator()) ? "null" : nextInfo.getCreator(),
					nextInfo.getHidden().toString(),
					nextInfo.getSystemOwned().toString()
			};
			
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
		}
	}
	
	private void formatFlatResults(SortedSet<ClassDefinitionInfo> children) { 
		Iterator<ClassDefinitionInfo> iter = children.iterator();
		
		ColDef[] defs = new ColDef[] {
				new ColDef("Name", 50, StringUtil.ALIGN_LEFT),
				new ColDef("Creator", 20, StringUtil.ALIGN_LEFT),
				new ColDef("Hidden", 6, StringUtil.ALIGN_LEFT),
				new ColDef("System", 6, StringUtil.ALIGN_RIGHT)
			};
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		while (iter.hasNext()) {
			ClassDefinitionInfo nextInfo = iter.next();
			String[] row = new String[] {
					nextInfo.getSymbolicName(),
					"".equals(nextInfo.getCreator()) ? "null" : nextInfo.getCreator(),
					nextInfo.getHidden().toString(),
					nextInfo.getSystemOwned().toString()
			};
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
		}
		
	}
	
	/**
	 * Pattern:
	 * 		Level 1			|--	
	 * 		Level 2			|	|--
	 * 		Level 3			|	|	|--
	 * 		etc
	 * @param level
	 * @return
	 */
	private String createpadChars(int level) {
		StringBuffer buf = new StringBuffer();
		while (level-- >= 1) {
			buf.append("|  ");
		}
		buf.append("|--");
		return buf.toString();
	}
	
	/**
	 * This does a depth-first traversal of the tree structure and 
	 * adds each of the items to a list.
	 * @param parent
	 * @param clsDefList
	 */
	private void initChildLevels(ClassDefinitionInfo parent, 
			List<ClassDefinitionInfo> clsDefList) {
		int childLevel = parent.getLevel() + 1;
		for (Iterator<ClassDefinitionInfo> iter = parent.getChildren().iterator(); iter.hasNext();) {
			ClassDefinitionInfo child = (ClassDefinitionInfo) iter.next();
			child.setLevel(childLevel);
			clsDefList.add(child);
			if (! child.getChildren().isEmpty()) {
				initChildLevels(child, clsDefList);
			}
		}
	}


	/**
	 * @param row
	 * @return
	 */
	private ClassDefinitionInfo readItem(
			com.filenet.api.property.Properties props) {
		String pName, cId, cName, creator = null;
		Date createDate;
		Boolean isSystemOwned;
		Boolean isHidden;
		ClassDefinitionInfo docclassInfo = new ClassDefinitionInfo();
		
		pName = props.getStringValue("pName");	
	
		cId = props.getIdValue("cId").toString();
		cName = props.getStringValue("cName");
		creator = props.getStringValue("creator");
		
		docclassInfo.setId(cId);
		docclassInfo.setSymbolicName(cName);
		docclassInfo.setParentName(pName);
		docclassInfo.setCreator((creator == null) ? "" : creator);
		
		isSystemOwned = props.getBooleanValue("IsSystemOwned");
		docclassInfo.setSystemOwned(isSystemOwned);
		
		isHidden = props.getBooleanValue("IsHidden");
		docclassInfo.setHidden(isHidden);
		
		createDate = props.getDateTimeValue("DateCreated");
		docclassInfo.setDateCreated(createDate);
		
		return docclassInfo;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam treeOpt = null;
		StringParam rootClassDescriptionParam = null;
		
		// options
		{
			treeOpt = new BooleanParam(TREE_OPT,
					"list the class definitions as a tree");
			treeOpt.setOptional(BooleanParam.OPTIONAL);
			treeOpt.setMultiValued(false);
		}
		// cmd args
		{
			rootClassDescriptionParam = new StringParam(ROOT_CLASS_DESCRIPTION_ARG, 
					"Root class definition to list subtypes",
					StringParam.OPTIONAL);
			rootClassDescriptionParam.setMultiValued(false);
		}
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {treeOpt }, 
					new Parameter[] { rootClassDescriptionParam });
		cl.setDieOnParseError(false);

		return cl;
	}
}


