/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.ArrayList;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;

/**
 *  ExecScript
 *
 * @author regier
 * @date   Feb 5, 2012
 */
public class ExecScript extends BaseCommand {
	
	public static final String 
			CMD = "execscript", 
			CMD_DESC = "Execute a script",
			HELP_TEXT = "Usage:" +
			 "\nexecscript -file c:/temp/foo.csh";
		
		public static final String
			// URI_ARG in superclass
			SRCFILE_OPT = "file";
		
	private boolean results = true;
	private boolean failFast = true;

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		FileParam scriptFileOpt = (FileParam) cl.getOption(SRCFILE_OPT);
		File scriptFile = scriptFileOpt.getValue();
		boolean failFast = true;
		
		return executeScript(scriptFile, failFast);
	}

	/**
	 * @param scriptFile
	 * @return
	 */
	private boolean executeScript(File scriptFile, boolean failFast) throws Exception {
		this.failFast = failFast;
		
		ArrayList<String> cmds = loadScript(scriptFile);
		CEShell scriptShell = new CEShell(this.getShell()) {
			
			protected void doAfter(BaseCommand cmd) {
				if (! cmd.success) {
					ExecScript.this.results = false;
					getResponse().printErr("\tfailed on cmd");
					if (ExecScript.this.failFast) {
						
					}
				}
			}
		};
		getResponse().printOut("Executing script " + scriptFile.toString() + "...");
		for (String cmdline : cmds) {
			getResponse().printOut("\trun cmd:\t" + cmdline);
			scriptShell.execute(cmdline);
		}
		getResponse().printOut("Completed script " + scriptFile.toString());
		return this.results;
	}

	/**
	 * @param scriptFile
	 * @return
	 */
	private ArrayList<String> loadScript(File scriptFile) throws Exception {
		ArrayList<String> cmds = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
		String line = null;
		while ((line = reader.readLine()) != null) {
			cmds.add(line);
		}
		return cmds;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		FileParam srcFileOpt = null;
		
		// options
		srcFileOpt = new FileParam(SRCFILE_OPT,
				"Src files for import)",
				FileParam.IS_FILE & FileParam.IS_READABLE,
				Parameter.REQUIRED);
		srcFileOpt.setOptionLabel("<srcfile>");
		srcFileOpt.setMultiValued(false);
		srcFileOpt.setOptionLabel("<src-file>");
		
		
		// args
		
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { srcFileOpt }, 
					new Parameter[] {  });
		cl.setDieOnParseError(false);

		return cl;
	}
}
