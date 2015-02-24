/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;

import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.impl.ConsoleDone;
import com.ibm.bao.ceshell.impl.PyInteractiveConsole;

/**
 *  http://etutorials.org/Programming/Python+tutorial/Part+V+Extending+and+Embedding/Chapter+25.+Extending+and+Embedding+Jython/25.2+Embedding+Jython+in+Java/
 *  http://www.javalobby.org/articles/jython/index.jsp
 *  PyScriptPOC
 *   
 *
 * @author regier
 * @date   Dec 22, 2011
 */
public class PyScriptPOC extends BaseCommand {
	
	private static final String 
		CMD = "util.python", 
		CMD_DESC = "execute a script",
		HELP_TEXT = "Usage:\n" +
				"foo2 /C:/data-bh/blue-harmony/CEShellPOC/foo.py";
	
	public static final String SCRIP_FILE_OPT = "script";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		File scriptFile = null;
		FileParam scriptOpt = (FileParam) cl.getOption(SCRIP_FILE_OPT);
		if (scriptOpt.isSet()) {
			scriptFile = scriptOpt.getValue();
		}
		
		return python(scriptFile);
	}
	
	public boolean python(File scriptFile) {
		
		if (scriptFile != null) {
			execFile(scriptFile);
		} else {
			startInteractiveShell();
		}
		return true;
	}
	
	private void startInteractiveShell() {
		
    	try {
    		getResponse().printOut("Starting interactive Python inerpreter, type \'exit\' to return");
    		getResponse().printOut("\tContext contains, ceshell, response\n");
    		CEShell ceShell = getShell();
    		
    		PyInteractiveConsole interp = new PyInteractiveConsole();
    		interp.set("ceshell", ceShell);
    		interp.set("response", ceShell.getResponse());
    		interp.setOut(ceShell.getResponse().getOut());
			interp.interact();
			
		} catch (ConsoleDone e) {
			System.out.println("Completed Session");
		}
	}

	/**
	 * @param pi
	 */
	private void execFile(File scriptFile) {
		PySystemState.initialize();
		PythonInterpreter pi = new PythonInterpreter();
		
		pi.set("ceshell", this.getShell());
		pi.set("response", getShell().getResponse());
		
		
		pi.exec("import sys");
		pi.exec("from com.filenet.api.core import *");
		// pi.execfile("/C:/data-bh/blue-harmony/CEShellPOC/foo.py");
		pi.execfile(scriptFile.toString());
		
	}


	protected CmdLineHandler getCommandLine() {
		// create command line handler
		FileParam scriptOpt = null; 
		
		
		// options
		scriptOpt = new FileParam(SCRIP_FILE_OPT, 
				"script file",
				FileParam.IS_FILE & FileParam.IS_READABLE,
				FileParam.OPTIONAL);
		scriptOpt.setMultiValued(false);
		scriptOpt.setOptionLabel("<script-file>");
		CmdLineHandler cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {scriptOpt }, 
					new Parameter[] { });
		cl.setDieOnParseError(false);

		return cl;
	}

}
