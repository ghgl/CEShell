/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Date;
import java.util.Iterator;

import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.TypeID;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  ChoiceListListCmd
 *
 * @author regier
 * @date   Sep 25, 2011
 */
public class ChoiceListsListCmd extends BaseCommand {
	
	private static final String 
		CMD = "clls", 
		CMD_DESC = "List Chice Lists",
		HELP_TEXT = CMD_DESC + "\n" +
				"\nUsage:\n" +
				"clls\n" +
				"\tlist all choice lists\n" + 
				"clls -long" +
				"\t list long all choice lists";
	
	// param names
	private static final String
		LIST_LONG = "long";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam listLongOpt = (BooleanParam) cl.getOption(LIST_LONG);
		Boolean listLong = false;
		
		if (listLongOpt.isSet()) {
			listLong = listLongOpt.getValue();
		}
		
		return listChoiceLists(listLong);
	}

	/**
	 * @param listLong
	 */
	public boolean listChoiceLists(Boolean listLong) {
		String query = null;
		SearchSQL sqlObject = new SearchSQL();
	    SearchScope searchScope = new SearchScope(getShell().getObjectStore());
	    RepositoryRowSet rowSet = null; 
	    
	    if (listLong) {
			query =  "Select Id, Name, DisplayName, DescriptiveText, DataType, Creator, DateCreated from ChoiceList order by DisplayName";
		} else {
			query = "Select DisplayName from ChoiceList order by DisplayName";
		}
	    
	    sqlObject.setQueryString(query);
	    rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    if (! listLong) {
			doListPropsShort(rowSet);
		} else {
			doListLong(rowSet);
		}
	    return true;
	}
	
	private void doListPropsShort(RepositoryRowSet rowSet) {
		for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	String rowStr = row.getProperties().getStringValue("DisplayName");
	    	getResponse().printOut(rowStr);
	    }
	}
	
	/**
	 * @param rowSet
	 */
	private void doListLong(RepositoryRowSet rowSet) {
		ColDef[] cols = createColDefs();
		getResponse().printOut(StringUtil.formatHeader(cols, " "));	
	    for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	com.filenet.api.property.Properties props = 
				row.getProperties();
	    	String rowStr = readRowLong(cols, props);
	    	getResponse().printOut(rowStr);
	    	
	    }
	}
	
	
	private String readRowLong(ColDef[] cols, 
			com.filenet.api.property.Properties props) {
		String[] rowData = null;
		
		String rowStr = null;
		String idStr = null;
		String choiceListName = null;
		String choiceListDisplayName = null;
		String description = null;
		Integer dataType = null;
		String creator;
		Date dateCreated = null;
		String dateCreatedStr = null;
		String dataTypeStr = null;
		try {
			idStr = props.getIdValue("Id").toString();
			choiceListName = props.getStringValue("Name");
			choiceListDisplayName = props.getStringValue("DisplayName");
			description = props.getStringValue("DescriptiveText");
			creator = props.getStringValue("Creator");
			dateCreated = props.getDateTimeValue("DateCreated");
			dataType = props.getInteger32Value("DataType");
			dataTypeStr = TypeID.getInstanceFromInt(dataType.intValue()).toString();
			dateCreatedStr = StringUtil.fmtDate(dateCreated);
			rowData = new String[] {
					idStr,
					choiceListName,
					choiceListDisplayName,
					description,
					dataTypeStr,
					creator,
					dateCreatedStr
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
				new ColDef("Id", 41, StringUtil.ALIGN_LEFT),
				new ColDef("Name", 32, StringUtil.ALIGN_LEFT),
				new ColDef("Display Name", 32, StringUtil.ALIGN_LEFT),
				new ColDef("Description", 40, StringUtil.ALIGN_LEFT),
				new ColDef("Data Type", 9, StringUtil.ALIGN_LEFT),
				new ColDef("Created By", 13, StringUtil.ALIGN_LEFT),
				new ColDef("Create Date", 23, StringUtil.ALIGN_LEFT)
		};
		
		return cols;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		BooleanParam longOpt = null;
				
		// params
		{
			longOpt = new BooleanParam(LIST_LONG,
					"list long the choice list properties.");
			longOpt.setOptional(true);
			longOpt.setMultiValued(false);
			
		}
	
		// cmd args
		{
			
		}
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {longOpt }, 
					new Parameter[] { });
		cl.setDieOnParseError(false);

		return cl;
	}

}
