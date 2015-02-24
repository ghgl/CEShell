/**
 * 
 */
package com.ibm.bao.ceshell;

import java.security.CodeSource;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  WhichCmd
 *
 * @author GaryRegier
 * @date   Dec 22, 2010
 */
public class WhichCmd extends BaseCommand {
	
	private static final String 
		CMD = "which", 
		CMD_DESC = "Show the java class that implements a command",
		HELP_TEXT = "Identify the Java command class that implements a command";
	
	// param names
	private static final String 
		CMD_ARG = "cmd";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam cmdArg = (StringParam) cl.getArg(CMD_ARG);
		String cmdId = cmdArg.getValue();
		
		return which(cmdId);
	}
	
	public boolean which(String cmdId) throws Exception {
		Class<?> cmdClass = this.getShell().getCmdbyId(cmdId);
		String clsName = cmdClass.getCanonicalName();
		String clsLocation = "";
		CodeSource codeSrc = cmdClass.getProtectionDomain().getCodeSource();
		
		if (codeSrc != null) {
			clsLocation = codeSrc.getLocation().toString();
		}
		
		formatResults(cmdId, clsName, clsLocation);
		return true;
	}

	/**
	 * @param cmdId
	 * @param clsName
	 * @param clsLocation
	 */
	private void formatResults(String cmdId, String clsName, String clsLocation) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(StringUtil.formatTwoCols("Cmd Id", cmdId, 20, " ")).append("\n");
		buf.append(StringUtil.formatTwoCols("Class", clsName, 20, " ")).append("\n");
		buf.append(StringUtil.formatTwoCols("Location", clsLocation, 20, " ")).append("\n");
		getResponse().printOut(buf.toString());
	}
		

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam cmdArg = null;

		// options
		
		// cmd args
		cmdArg = new StringParam(CMD_ARG, "cmdId of the command to describe",
				StringParam.REQUIRED);
		cmdArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] { cmdArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
