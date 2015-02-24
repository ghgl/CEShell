/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;

import com.filenet.api.admin.AddOnInstallationRecord;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  osaddons
 *
 * @author regier
 * @date   Oct 13, 2011
 */
public class OSAddOnsCmd extends BaseCommand {
	
	private static final String 
		CMD = "osaddons", 
		CMD_DESC = "osaddons <object-store>",
		HELP_TEXT = "List addons for an object store.\n";
	
	// param names
	

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return osAddOns();
	}

	/**
	 * 
	 */
	public boolean osAddOns() {
		@SuppressWarnings("rawtypes")
		Iterator iter = this.getShell().getObjectStore().get_AddOnInstallationRecords().iterator();
		ColDef[] defs = new ColDef[] {
				new ColDef("Id", 40, StringUtil.ALIGN_LEFT),
				new ColDef("Name", 50, StringUtil.ALIGN_LEFT),
				new ColDef("Status", 25, StringUtil.ALIGN_LEFT),
				new ColDef("Install date", 30, StringUtil.ALIGN_LEFT),
				new ColDef("Installer", 30, StringUtil.ALIGN_LEFT)
			};
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		while (iter.hasNext()) {
			AddOnInstallationRecord aoir = (AddOnInstallationRecord) iter.next();
			String id = aoir.get_Id().toString();
			String name = aoir.get_AddOnName();
			String status = aoir.get_InstallationStatus().toString();
			String installDate = aoir.get_InstallationDate().toString();
			String installer = aoir.get_Installer();
			String[] row = new String[] {	id, name, status, installDate, installer };
			
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		
		// options
		
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] {  });
		cl.setDieOnParseError(false);

		return cl;
	}

}
