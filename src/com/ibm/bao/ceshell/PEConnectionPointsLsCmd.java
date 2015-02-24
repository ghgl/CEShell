/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;

import com.filenet.api.admin.PEConnectionPoint;
import com.filenet.api.collection.PEConnectionPointSet;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  PEConnectionPointsLs
 *
 * @author GaryRegier
 * @date   Jun 29, 2011
 */
public class PEConnectionPointsLsCmd extends BaseCommand {
	
	private static final String 
	CMD = "peconsls", 
	CMD_DESC = "List the PE connection points listed in the CE Domain",
	HELP_TEXT = CMD_DESC +
		"Usage: \n" +
		"peconsls";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return peConnectionPointsLs();
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public boolean peConnectionPointsLs() throws Exception {
		PEConnectionPointSet peConsSet = 
			getShell().getDomain(getShell().getCEConnection())
					.get_PEConnectionPoints();
		ColDef[] defs = new ColDef[] {
				new ColDef("Name", 10, StringUtil.ALIGN_LEFT),
				new ColDef("Description", 30, StringUtil.ALIGN_LEFT),
			};
		for (Iterator<Object> iterator = peConsSet.iterator(); iterator.hasNext();) {
			PEConnectionPoint peConPt = (PEConnectionPoint) iterator.next();
			String name = peConPt.get_Name();
			String description = peConPt.get_DescriptiveText();
			String[] row = new String[] {
					name,
					description
			};
		
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
		}
		return true;
	}

	/* (non-Javadoc)
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
