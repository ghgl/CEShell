/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.collection.ClassDefinitionSet;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.Factory;
import com.filenet.api.property.Properties;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  PropLSCmd
 *
 * @author GaryRegier
 * @date   May 16, 2011
 */
public class PropertyTemplatesLSCmd extends BaseCommand {
	
//	public static final String[] DATA_TYPES = new String[] {
//		"undef",	//	0
//		"Binary",	// 	1
//		"Boolean", 	//	2
//		"DateTime",		// 	3
//		"Double",	// 	4
//		"ID", 		// 	5  -- GUID
//		"Long",		// 	6
//		"Object",	// 	7
//		"String"	// 	8
//	};
	
	private static final String 
		CMD = "ptsls", 
		CMD_DESC = "List property template definitions",
		HELP_TEXT = CMD_DESC + "\n" +
				"\nUsage:\n" +
				"ptsls\n" +
				"\tlist all properties\n" + 
				"ptsls B%\n" +
				"\t list all properties starting with B";

	// param names
	private static final String
		LONG_OPT = "long",
		OUTFILE_OPT = "file",
		PROP_NAME_ARG = "propname";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam longOpt = (BooleanParam) cl.getOption(LONG_OPT);
		FileParam outFileOpt = (FileParam) cl.getOption(OUTFILE_OPT);
		File outFile = null;
		StringParam propNameArg = (StringParam) cl.getArg(PROP_NAME_ARG);
		 
		String propName = null;
		boolean listLong = false;
		
		if (longOpt.isSet()) {
			listLong = longOpt.isTrue();
		}
		if (outFileOpt.isSet()) {
			outFile = outFileOpt.getValue();
		}
		if (propNameArg.isSet()) {
			propName = propNameArg.getValue();
		}
		return propertyTemplatesLs(listLong, propName, outFile);
	}

	/**
	 * @param listLong
	 */
	public boolean propertyTemplatesLs(
			boolean listLong, 
			String propName, 
			File outFile) throws Exception {
		String query = "select ID, SymbolicName, DisplayName, DescriptiveText, DataType, Creator, DateCreated, PropertyDisplayCategory from PropertyTemplate ";
		if (propName != null) {
			String where = String.format("where SymbolicName like '%s' ", propName);
			query = query + where;
		}
		query = query + " order by SymbolicName";
		SearchSQL sqlObject = new SearchSQL();
	    SearchScope searchScope = new SearchScope(getShell().getObjectStore());
	    RepositoryRowSet rowSet = null; 
	    
	    sqlObject.setQueryString(query);
	    rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    if (listLong) {
	    	doListPropsLong(rowSet, outFile);
		} else {
			doListPropsShort(rowSet, outFile);
		}
	    return true;
	}

	/**
	 * @param rowSet
	 * @param outFile 
	 * @throws Exception 
	 */
	private void doListPropsShort(RepositoryRowSet rowSet, File outFile) throws Exception {
		try {
			if (outFile != null) {
				this.getResponse().redirectOutputStream(outFile);
			} 
			printRowsShort(rowSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (outFile != null) {
				getResponse().restoreOutputStream();
				getResponse().printOut("Completed listing of properties to file " + outFile.toString());
			}
		}
	}

	private void printRowsShort(RepositoryRowSet rowSet) {
		ColDef[] cols = createColDefs();
		int cnt = 1;
		getResponse().printOut(StringUtil.formatHeader(cols, " "));	
	    for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	com.filenet.api.property.Properties props = 
				row.getProperties();
	    	String rowStr = readRowShort(cols, cnt++, props);
	    	getResponse().printOut(rowStr);
	    }
	}

	private String readRowShort(ColDef[] cols, int cnt,
		com.filenet.api.property.Properties props) {
		String[] rowData = null;
		String rowStr = null;
		String symbolicName = null;
		Integer dataType = null;
		String creator = null;
		Date dateCreated = null;
		String dateCreatedStr = null;
		String propertyDisplayCategory = null; 
		String dataTypeStr = null;
		try {
			symbolicName = props.getStringValue("SymbolicName");
			dataType = props.getInteger32Value("DataType");
			creator = props.getStringValue("Creator");
			dateCreated = props.getDateTimeValue("DateCreated");
			propertyDisplayCategory = props.getStringValue("PropertyDisplayCategory");
			if (propertyDisplayCategory == null) {
				propertyDisplayCategory = "";
			}
			
			dataTypeStr = TypeID.getInstanceFromInt(dataType.intValue()).toString();
			dateCreatedStr = StringUtil.fmtDate(dateCreated);
			rowData = new String[] {
					//"" + cnt,
					symbolicName,
					dataTypeStr,
					//creator,
					//dateCreatedStr,
					propertyDisplayCategory
					
			};
			rowStr = StringUtil.formatRow(cols, rowData, " ");
		} catch (Exception e) {
			rowStr = e.getMessage();
		}
		return rowStr;
	}

	/**
	 * @return
	 */
	private ColDef[] createColDefs() {
		ColDef[] cols = {
				//new ColDef("Cnt", 4, StringUtil.ALIGN_RIGHT),
				new ColDef("SymbolicName", 42, StringUtil.ALIGN_LEFT),
				new ColDef("Type", 9, StringUtil.ALIGN_LEFT),
				//new ColDef("Created By", 13, StringUtil.ALIGN_LEFT),
				//new ColDef("Create date", 23, StringUtil.ALIGN_LEFT),
				new ColDef("Category", 20, StringUtil.ALIGN_LEFT)
		};
		
		return cols;
	}

	/**
	 * @param rowSet
	 * @param outFile 
	 */
	private void doListPropsLong(RepositoryRowSet rowSet, File outFile) {
		int cnt = 1;
	    for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	com.filenet.api.property.Properties props = 
				row.getProperties();
	    	readItem(props, cnt++);
	    }
	}

	/**
	 * select ID, DisplayName, DescriptiveText, DataType, Creator, DateCreated, PropertyDisplayCategory 
	 * @param props
	 * @return
	 */
	private void readItem(Properties props, int cnt) {
		String usedInClasses = null; 
		Id id = null;
		String symbolicName = null;
		String displayName = null;
		String description = null;
		Integer dataType = null;
		String creator;
		Date dateCreated = null;
		String propertyDisplayCategory = null; 
		String dataTypeStr = null;
		
		id = props.getIdValue("ID");
		usedInClasses = getUsedInClassNames(id);
		symbolicName = props.getStringValue("SymbolicName");
		displayName = props.getStringValue("DisplayName");
		description = props.getStringValue("DescriptiveText");
		dataType = props.getInteger32Value("DataType");
		creator = props.getStringValue("Creator");
		dateCreated = props.getDateTimeValue("DateCreated");
		propertyDisplayCategory = props.getStringValue("PropertyDisplayCategory");
		
//		dataTypeStr = DATA_TYPES[dataType];
		dataTypeStr = TypeID.getInstanceFromInt(dataType.intValue()).toString();
		formatResults(cnt, 
				id, 
				symbolicName,
				displayName, 
				description, 
				dataTypeStr, 
				creator, 
				dateCreated, 
				propertyDisplayCategory, 
				usedInClasses);
	}

	@SuppressWarnings("unchecked")
	private String getUsedInClassNames(Id id) {
		StringBuffer buf = new StringBuffer();
		PropertyTemplate propertyTemplate;
		ClassDefinitionSet cds;
		propertyTemplate = Factory.PropertyTemplate.fetchInstance(getShell().getObjectStore(), id, null);
		cds = propertyTemplate.get_UsedInClasses();
		for(Iterator<Object> iter = cds.iterator(); iter.hasNext();) {
			ClassDefinition nextClass = (ClassDefinition) iter.next();
			String nextClassSymbolicName = nextClass.get_SymbolicName();
			buf.append(nextClassSymbolicName).append("\t");
		}
		if (buf.length() == 0) {
			buf.append("(not used)");
		}
		return buf.toString();
	}

	/**
	 * @param symbolicName
	 * @param displayName
	 * @param description
	 * @param dataType
	 * @param creator
	 * @param dateCreated
	 * @param propertyDisplayCategory
	 */
	private void formatResults(int cnt,
				Id id, 
				String symbolicName, 
				String displayName,
				String description, 
				
				String dataTypeStr, 
				String creator,
				Date dateCreated, 
				String propertyDisplayCategory,
				String usedInClasses) {

		StringBuffer buf = new StringBuffer();
		buf.append(cnt).append("\n");
		buf.append("\tID:\t").append(id).append("\n");
		buf.append("\tSymbolicName:\t").append(symbolicName).append("\n");
		buf.append("\tDescription:\t").append(description).append("\n");
		buf.append("\tCreator:\t").append(creator).append("\n");
		buf.append("\tDateCreated:\t").append(dateCreated).append("\n");
		buf.append("\tType:\t").append(dataTypeStr).append("\n");
		buf.append("\tCategory:\t").append(propertyDisplayCategory).append("\n");
		buf.append("\tUsed in:\t").append(usedInClasses).append("\n");
		buf.append("--------------------------------------------------------\n");
		
		buf.append(symbolicName).append("\t").append(usedInClasses);
		getShell().getResponse().printOut(buf.toString());
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam longOpt = null;
		FileParam outFileOpt = null;
		StringParam propNameArg = null;
		 
		
		// options
		{
			longOpt = new BooleanParam(LONG_OPT,
					"list properties in long format");
			longOpt.setOptional(true);
			longOpt.setMultiValued(false);
			
		}
		{
			outFileOpt = new FileParam(OUTFILE_OPT,
					"Export file )",
					FileParam.OPTIONAL);
			outFileOpt.setOptionLabel("<outFile>");
			outFileOpt.setMultiValued(false);
			outFileOpt.setOptionLabel("<output-file>");
		}
	
		// cmd args
		{
			propNameArg = new StringParam(PROP_NAME_ARG, "properties to list", StringParam.OPTIONAL);
			propNameArg.setMultiValued(StringParam.SINGLE_VALUED);
		}
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {longOpt, outFileOpt }, 
					new Parameter[] {propNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
