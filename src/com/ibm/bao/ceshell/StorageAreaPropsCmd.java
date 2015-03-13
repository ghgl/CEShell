/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.PrintStream;
import java.util.Iterator;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.property.Property;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 * StorageAreaPropsCmd
 * 
 * @author regier
 * @date Mar 1, 2015
 */
public class StorageAreaPropsCmd extends BaseCommand {
	
	private static final String 
	CMD = "saprops", 
	CMD_DESC = "Print the key properties for storage areas.",
	HELP_TEXT = CMD_DESC +
		"Usage: \n" +
		"saprops";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		storageAreaProsp();
		return true;
	}

	/**
	 * 
	 */
	public boolean storageAreaProsp() throws Exception {
		String query = "select Id, DescriptiveText,DisplayName, ContentElementCount,ContentElementKBytes,ContentElementsCreated,ContentElementsDeleted, MaximumContentElements,MaximumSizeKBytes from StorageArea";
		select(query, ".");
		return true;

	}

	public boolean select(String mySQLString, String delimiter) throws Exception {
		SearchSQL sqlObject = new SearchSQL();
		sqlObject.setQueryString(mySQLString);
		PrintStream out = null;
		SearchScope searchScope = new SearchScope(getShell().getObjectStore());
		RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null,
				new Boolean(true));

		displayStdResults(rowSet, out);
		return true;
	}

	public void displayStdResults(RepositoryRowSet rowSet, PrintStream output) {
		int pos = 0;

		for (Iterator<?> iter = rowSet.iterator(); iter.hasNext(); pos++) {
			RepositoryRow row = (RepositoryRow) iter.next();
			outputLine(output, "#" + pos);
			for (Iterator<?> propIter = row.getProperties().iterator(); propIter
					.hasNext();) {
				StringBuffer buf = new StringBuffer();
				Property prop = (Property) propIter.next();
				String name = prop.getPropertyName();
				Object value = prop.getObjectValue();
				String valueStr = "";
				if (value != null) {
					valueStr = value.toString();
				}
				buf.append("\t").append(StringUtil.padLeft(name, ".", 30))
						.append(valueStr);
				outputLine(output, buf.toString());
			}
		}
		getResponse().printOut(
				"completed processing of " + (pos - 1) + " records");
	}

	public void outputLine(PrintStream output, String header) {
		if (output != null) {
			output.println(header);
		} else {
			getResponse().printOut(header);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		
		// options
		
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {}, 
					new Parameter[] {});
		cl.setDieOnParseError(false);

		return cl;
	}
}
