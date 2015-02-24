/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Date;
import java.util.Iterator;

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

/**
 * EventSubscriptionLsCmd
 * 
 * @author regier
 * @date Sep 27, 2011
 */
public class EventSubscriptionLsCmd extends BaseCommand {

	private static final String CMD = "esls",
			CMD_DESC = "List Event Subscriptions", HELP_TEXT = CMD_DESC + "\n"
					+ "\nUsage:\n" + "esls\n"
					+ "\tlist all Event Subscriptions\n" + "esls -long"
					+ "\t list long all event subscriptions";

	// param names
	private static final String LIST_LONG = "long";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam listLongOpt = (BooleanParam) cl.getOption(LIST_LONG);
		Boolean listLong = false;

		if (listLongOpt.isSet()) {
			listLong = listLongOpt.getValue();
		}

		return listEventSubscriptions(listLong);
	}

	/**
	 * @param listLong
	 */
	public boolean listEventSubscriptions(Boolean listLong) {
		String query = null;
		SearchSQL sqlObject = new SearchSQL();
		SearchScope searchScope = new SearchScope(getShell().getObjectStore());
		RepositoryRowSet rowSet = null;

		if (listLong) {
			query = "select cs.DisplayName, cs.IsEnabled, cs.DescriptiveText, ea.Name EventAction, cs.IsSynchronous Synchronous, cs.DateCreated from ClassSubscription cs left join EventAction ea on cs.EventAction = ea.This  order by cs.DisplayName";
		} else {
			query = "Select DisplayName from ClassSubscription order by DisplayName";
		}

		sqlObject.setQueryString(query);
		rowSet = searchScope
				.fetchRows(sqlObject, null, null, new Boolean(true));
		if (!listLong) {
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
			com.filenet.api.property.Properties props = row.getProperties();
			String rowStr = readRowLong(cols, props);
			getResponse().printOut(rowStr);

		}
	}

	private String readRowLong(ColDef[] cols,
			com.filenet.api.property.Properties props) {
		String[] rowData = null;
		String rowStr = null;
		String displayName = null;
		Boolean enabled = Boolean.FALSE;
		String description = null;
		String eventAction = null;
		Boolean synchronous = Boolean.FALSE;
		Date dateCreated = null;
		String dateCreatedStr = null;
		try {
			displayName = props.getStringValue("DisplayName");
			enabled = props.getBooleanValue("IsEnabled");
			description = props.getStringValue("DescriptiveText");
			eventAction = props.getStringValue("EventAction");
			synchronous = props.getBooleanValue("Synchronous");
			dateCreated = props.getDateTimeValue("DateCreated");
			dateCreatedStr = StringUtil.fmtDate(dateCreated);
			rowData = new String[] { 
					displayName, 
					enabled.toString(),
					(description == null) ? "null" : description, 
					eventAction,
					synchronous.toString(),
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
				new ColDef("Name", 45, StringUtil.ALIGN_LEFT),
				new ColDef("Enabled", 7, StringUtil.ALIGN_LEFT),
				new ColDef("Description", 60, StringUtil.ALIGN_LEFT),
				new ColDef("EventAction", 25, StringUtil.ALIGN_LEFT),
				new ColDef("Synch", 5, StringUtil.ALIGN_LEFT),
				new ColDef("CreateDate", 25, StringUtil.ALIGN_LEFT) };

		return cols;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		BooleanParam longOpt = null;

		// params
		{
			longOpt = new BooleanParam(LIST_LONG,
					"list long the event subscriptions list properties.");
			longOpt.setOptional(true);
			longOpt.setMultiValued(false);

		}

		// cmd args
		{

		}

		// create command line handler
		cl = new HelpCmdLineHandler(HELP_TEXT, CMD, CMD_DESC,
				new Parameter[] { longOpt }, new Parameter[] {});
		cl.setDieOnParseError(false);

		return cl;
	}

}
