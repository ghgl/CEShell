/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.PropertyUtil;

/**
 *  SourceCmd
 *
 * @author GaryRegier
 * @date   Dec 30, 2010
 */
public class SourceCmd extends BaseCommand {
	
	private static final String 
		CMD = "source", 
		CMD_DESC = "Add new command ids to the CEShell by reading in the mappings between the commands and the command classes",
		HELP_TEXT = "The source command reads a properties file\n" +
			"The format of the properties file is <cmdId>=<cmdClass>\n" +
			"The classes must be in the CEShell class path. Once they\n" +
			"have been loaded, they can be executed just like a native command";
	
	// param names
	private static final String 
			CMD_PROPS_FILE_ARG = "CMDS";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		FileParam srcFileArg = (FileParam) cl.getArg(CMD_PROPS_FILE_ARG);
		File srcFile = null;
		
		srcFile = srcFileArg.getValue();
		return source(srcFile);
	}

	/**
	 * @param srcFile
	 */
	public boolean source(File srcFile) throws Exception {
		Properties cmdProps = new PropertyUtil().loadPropertiesFromFile(srcFile);
		ArrayList<String> results = null;
		
		results = getShell().addCommands(cmdProps);
		Iterator<String> iter = results.iterator();
		while (iter.hasNext()) {
			String nextResult = (String) iter.next();
			getResponse().printOut(nextResult);
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
		FileParam srcPropsArg = null;

		// options
		
		// cmd args
		srcPropsArg = new FileParam(CMD_PROPS_FILE_ARG,
				"Local output directory (system temp dir by default)",
				FileParam.IS_FILE & FileParam.IS_READABLE,
				FileParam.REQUIRED);
		srcPropsArg.setOptionLabel("<cmdPropsFile>");

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { srcPropsArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
