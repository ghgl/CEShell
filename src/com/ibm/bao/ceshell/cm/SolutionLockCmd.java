package com.ibm.bao.ceshell.cm;

import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.property.Properties;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.bao.ceshell.BaseCommand;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 * Select Id, CmAcmLockDefinition, DateLastModified from
 * CmAcmSolutionLockControl Each solution has a CmAcmSolutionLockControl object
 * in the folder. The property CmAcmLocDefinitin will have a small XML content
 * that looks like: <code>
 * 
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 *		<lockArrayList>
 *		    <lock>
 *		        <type>5</type>
 *		        <resource>UCM_CreateMPIP</resource>
 *		        <displayName>UCM_CreateMPIP</displayName>
 *		        <caseType>UCM_MPIP</caseType>
 *		        <lockedby>gsmith</lockedby>
 *		        <timestamp>2018-04-26T16:22:27.558Z</timestamp>
 *		    </lock>
 *		    <loggedInUsers>smith,jones,mendoza</loggedInUsers>
 *		</lockArrayList>
 * </code>
 * 
 * 
 * where CmAcmSolutionLockControl.This InSubFolder('/IBM Case
 * Manager/Solutions/Unified Case Manager')
 * 
 * @author gregier
 *
 */
public class SolutionLockCmd extends BaseCommand {

	private static final String CMD = "cm.slocks", CMD_DESC = "list locks on a CaseManager Solution",
			HELP_TEXT = CMD_DESC + "\nExample:\n" + "\tcm.slocks Unified Case Manager";

	// param names
	private static final String SOLUTION_NAME_ARG = "solution";

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam solutionNameArg = (StringParam) cl.getArg(SOLUTION_NAME_ARG);

		String solutionName = null;
		solutionName = solutionNameArg.getValue();

		return solutionLocksProps(solutionName);
	}

	public boolean solutionLocksProps(String solutionName) throws Exception {
		String query = null;
		SearchSQL sqlObject = new SearchSQL();
		SearchScope searchScope = new SearchScope(getShell().getObjectStore());
		RepositoryRowSet rowSet = null;
		// "Select Id, CmAcmLockDefinition, DateLastModified from CmAcmSolutionLockControl where CmAcmSolutionLockControl.This InSubFolder('/IBM Case Manager/Solutions/Unified Case Manager')";
		
		String querytmpl = "Select Id, CmAcmLockDefinition, DateLastModified from CmAcmSolutionLockControl where CmAcmSolutionLockControl.This InSubFolder('/IBM Case Manager/Solutions/%s')";
		query = String.format(querytmpl, solutionName);
		System.out.println(query);
		sqlObject.setQueryString(query);
		rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
		RepositoryRow row = (RepositoryRow) rowSet.iterator().next();
		com.filenet.api.property.Properties props = row.getProperties();
		
		displayLock(solutionName, props);
		// getResponse().printOut(rowStr);

		return true;
	}

	private void displayLock(String solutionName, Properties props) throws Exception {
		String rawXml = props.getStringValue("CmAcmLockDefinition");
		Locks locks = LocksHandler.parse(rawXml);
		int cnt = locks.count();
		getResponse().printOut(String.format("%s (%d locks)", solutionName, cnt));
		if (cnt > 0) {
			ColDef[] defs = new ColDef[] { 
					new ColDef("#", 4, StringUtil.ALIGN_RIGHT),
					new ColDef("T", 2, StringUtil.ALIGN_RIGHT),  // type
					new ColDef("time", 27, StringUtil.ALIGN_LEFT),
					new ColDef("locked By", 15, StringUtil.ALIGN_LEFT),
					new ColDef("Case Type", 35, StringUtil.ALIGN_LEFT),
					new ColDef("Resource", 35, StringUtil.ALIGN_LEFT),
					new ColDef("Display Name", 30, StringUtil.ALIGN_LEFT)
			};
			
			getResponse().printOut(StringUtil.formatHeader(defs, " "));
			int pos = 1;
			for (LockVO lock : locks.getLocks()) {
				String[] data = {
					"" + pos,
					"" + lock.getType(),
					lock.getTimeStamp(),
					lock.getLockedBy(),
					lock.getCaseType(),
					lock.getResource(),
					lock.getDisplayName()
				};
				String row = StringUtil.formatRow(defs, data, " ");
				getResponse().printOut(row);
				pos++;
			}
		}
	}

	private void doListProps(RepositoryRowSet rowSet) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam solutionNameArg = null;

		// options

		// cmd args
		solutionNameArg = new StringParam(SOLUTION_NAME_ARG, "name of solution folder", StringParam.REQUIRED);
		solutionNameArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(HELP_TEXT, CMD, CMD_DESC, new Parameter[] {}, new Parameter[] { solutionNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
