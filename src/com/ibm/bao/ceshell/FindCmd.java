package com.ibm.bao.ceshell;

import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;
import com.ibm.bao.ceshell.view.DateFieldFormatter;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.HelpCmdLineHandler;
import jcmdline.IntParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

public class FindCmd extends BaseCommand {
	
	
	
	public static final String 
		CMD = "find", 
		CMD_DESC = "Find objects by type",
		HELP_TEXT = CMD_DESC + "\n\nExample:\n\tfind -type Document -max 10\n\n\tfind -type MyType -last";
	
	public static final String
		DIRECTORY_ARG = "directory",	
		TYPE_OPT = "type",
		LAST_OPT = "last",
		MAX_OPT = "max",
		WHERE_OPT = "where";
	
	public static final Integer 
		DEFAULT_MAX = 100,
		DEFAULT_LAST = 1;

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam typeOpt = (StringParam) cl.getOption(FindCmd.TYPE_OPT);
		IntParam maxOpt = (IntParam) cl.getOption(FindCmd.MAX_OPT);
		BooleanParam lastOpt = (BooleanParam) cl.getOption(FindCmd.LAST_OPT);
		StringParam whereOpt = (StringParam) cl.getOption(FindCmd.WHERE_OPT);
		StringParam pathUriArg = (StringParam) cl.getArg(FindCmd.DIRECTORY_ARG);
		
		String typeSearch = "";
		if (typeOpt.isSet()) {
			typeSearch = typeOpt.getValue();
		}
		
		Integer max = FindCmd.DEFAULT_MAX;
		if (maxOpt.isSet()) {
			max = maxOpt.getValue();
		}
		
		Boolean last = false;
		if (lastOpt.isSet()) {
			last = lastOpt.getValue();
		}
		
		String pathUri = null;
		if (pathUriArg.isSet()) {
			pathUri = pathUriArg.getValue();
		}
		
		String where = null;
		if (whereOpt.isSet()) {
			where = whereOpt.getValue();
		}
		
		find(pathUri, typeSearch, last, max, where);
		return true;
	}
	
	public boolean find(
			String pathUri, 
			String typeSearch, 
			boolean last,
			int max,
			String where) throws Exception {
		
		FindOpts findOpts = new FindOpts(pathUri, max, last, typeSearch, where);
		dofind(findOpts);
		return true;
	}
	
	/**
	 * Find items by Type
	 * @param findOpts
	 */
	void dofind(FindOpts findOpts) throws Exception {
		// SELECT top 1000 f.ObjectType, f.Id, f.FolderName, f.ContainerType, f.ClassDescription, f.DateCreated FROM Folder f  ORDER BY DateCreated asc";
		// Select top 100 f.ObjectType, f.id, f.Name, f.DateCreated FROM Document d oder by DateCreated asc"
		
		String order = (findOpts.last) ? "desc" : "asc";
		String query = "";
		String classType = findOpts.typeOpt;

		
		int maxRecords = findOpts.max;
		if ( findOpts.last && 
				maxRecords == FindCmd.DEFAULT_MAX) {
			maxRecords = FindCmd.DEFAULT_LAST;
		}
		
		
		String whereConstraint = "";
		whereConstraint = constructWhereConstraint(findOpts);
		String findTypeFmt = "select top %d  d.id, d.Name, d.DateCreated, d.Creator, d.ClassDescription FROM %s d  %s order by DateCreated %s";
		query = String.format(findTypeFmt, maxRecords, classType, whereConstraint, order );
			

		getResponse().printOut("DEBUG: " + query);
		RepositoryRowSet rowSet = runQuery(query);
		
		ColDef[] defs = new ColDef[] { 
				new ColDef("#", 4, StringUtil.ALIGN_RIGHT),
				new ColDef("T", 2, StringUtil.ALIGN_LEFT),
				new ColDef("Id", 40, StringUtil.ALIGN_LEFT),
				new ColDef("Name", 60, StringUtil.ALIGN_LEFT),
				new ColDef("DateCreated", 30, StringUtil.ALIGN_LEFT),
				new ColDef("Creator", 20, StringUtil.ALIGN_LEFT),
				new ColDef("SymbolicName", 20, StringUtil.ALIGN_LEFT)
				
		};
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		int pos = 1;
		RepositoryRow row = null;
		DateFieldFormatter dateFormatter = new DateFieldFormatter();
		for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();pos++) {
			row = (RepositoryRow) iter.next();
			Properties props = row.getProperties();
			String posStr, typeFlag, idStr, name, dateCreated, creator, className;
			
			posStr = "" + pos;
			typeFlag = "";
			idStr = readProperty(props, "Id");
			name = readProperty(props, "Name");
			dateCreated = readDateProperty(props, "DateCreated", dateFormatter);
			creator = readProperty(props, "Creator");
			className = "";
			Object cdValue = props.getObjectValue("ClassDescription");
			if (cdValue instanceof ClassDescription) {
				ClassDescription cd = (ClassDescription) cdValue;
				className = cd.get_SymbolicName();
				if (cd.describedIsOfClass("Folder")) {
					typeFlag = "d";
				} else if (cd.describedIsOfClass("CustomObject")) {
					typeFlag = "c";
				} else {
					typeFlag = "-";
				}
			}
			
			String[] data = {
				posStr,
				typeFlag,
				idStr,
				name,
				dateCreated,
				creator,
				className
			};
			getResponse().printOut(StringUtil.formatRow(defs, data, " "));

		}
		getResponse().printOut("completed processing of " + (pos - 1) + " records");
		
	}

	private String constructWhereConstraint(FindOpts findOpts) {
		String andOperator = "";
		
		/** both null, return empty string **/
		if (findOpts.whereOpt == null && findOpts.pathArg == null) {
			return "";
		}
		if (findOpts.whereOpt != null && findOpts.pathArg != null) {
			// use AND to join together
			andOperator = " and ";
		}
		String whereConstraint = (findOpts.whereOpt == null) ? "" : findOpts.whereOpt;
		String pathConstraint = "";
		if (findOpts.pathArg != null) {
			String fullPath = this.pathUriToFullPath(findOpts.pathArg);
			pathConstraint = String.format("d.this InSubFolder('%s')", fullPath);
		}
		String fullWhereConstraint = String.format("where %s %s %s", whereConstraint, andOperator, pathConstraint);
		return fullWhereConstraint;
	}

	private String readDateProperty(Properties props, String datePropName, DateFieldFormatter dateFormatter) {
		Property prop = props.get(datePropName);
		Object value = prop.getObjectValue();
		if (value instanceof Date) {
			return dateFormatter.format(value);
		}
		return value.toString();
	}

	private String readProperty(Properties props, String colName) {
		Property prop = props.get(colName);
		Object value = prop.getObjectValue();
		String valueStr = "";
		if (value != null) {
			valueStr = value.toString();
		}
		return valueStr;
	}
	
	private RepositoryRowSet runQuery(String mySQLString) throws Exception {
		SearchSQL sqlObject = new SearchSQL();
	    sqlObject.setQueryString(mySQLString);
	    SearchScope searchScope = new SearchScope(getShell().getObjectStore());
	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    return rowSet;
	}

	private boolean isFolderType(String typeOpt) {
		// TODO Auto-generated method stub
		return false;
	}

	

	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam typeOpt = null;
		IntParam maxOpt = null;
		BooleanParam lastOpt = null;
		StringParam whereOpt= null;
		StringParam pathUriArg = null;;
		
		// options
		typeOpt = new StringParam(FindCmd.TYPE_OPT, "type to find (symbolic name");
		typeOpt.setOptional(StringParam.REQUIRED);
		typeOpt.setMultiValued(StringParam.SINGLE_VALUED);
		
		maxOpt = new IntParam(FindCmd.MAX_OPT, "maximum number to return (default 10");
		maxOpt.setOptional(IntParam.OPTIONAL);
		
		lastOpt = new BooleanParam(FindCmd.LAST_OPT, "find last items by dateCreated");
		lastOpt.setOptional(BooleanParam.OPTIONAL);
		
		whereOpt = new StringParam(FindCmd.WHERE_OPT, "where condition to add to the search");
		whereOpt.setOptional(StringParam.OPTIONAL);
		whereOpt.setMultiValued(StringParam.SINGLE_VALUED);
		
		// cmd args
		pathUriArg = getPathUriArg();

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {typeOpt, maxOpt,  lastOpt, whereOpt}, 
					new Parameter[] { pathUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}
	
	
	
	/**
	 * Get the resource URI to report on.
	 * <p>
	 * NOTE: This is optional because if the type is domain, then the 
	 * resource URI does not need to be specified (uses the default domain).
	 * @return
	 */
	protected StringParam getPathUriArg() {
		String pathURIDesc = "URI indicating a file or folder. It can also be "+
		"the ID of a document or folder. If the type is a document class, " +
		"the value can be the name of the document class";
		
		// cmd args
		StringParam pathUriArg = new StringParam(FindCmd.DIRECTORY_ARG, 
				pathURIDesc,
				StringParam.OPTIONAL);
		pathUriArg.setMultiValued(false);
		
		return pathUriArg;
	}
}

class FindOpts {
	public String pathArg;
	public int max;
	public boolean last;
	public String typeOpt;
	public String whereOpt;
	
	public FindOpts(String pathArg, int max, boolean last, String typeOpt, String whereOpt) {
		this.pathArg = pathArg;
		this.max = max;
		this.last = last;
		this.typeOpt = typeOpt;
		this.whereOpt = whereOpt;
	}
	
}
