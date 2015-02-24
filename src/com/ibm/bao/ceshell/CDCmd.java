package com.ibm.bao.ceshell;

import jcmdline.CmdLineHandler;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

public class CDCmd extends BaseCommand {

	private static final String 
		CMD = "cd", 
		CMD_DESC = "list files",
		HELP_TEXT = "Change the working directory to a new directory.\n" +
		"Commands such as ls work from the current working directory. " +
		"To see the current directory, use the \"pwd\" command\n" +
		 	"Usage:\n" +
		 	"\tcd gogo                      cd to the gogo directory\n" +
		 	"\tcd ../gogo                   cd to the gogo using a relative path" +
		 	"\tcd /foo/baz/gogo             cd to a full path in the current object store\n" +
		 "\n\nSpecial characters such as spaces must be URL-encoded\n" +
		 "Example:\n" +
		 "\tcd go+go-goggo         cd to a folder nameed \"go to gogo\"";
	
	// param names
	private static final String 
		DIRECTORY_ARG = "dir"; 
	
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam dirUri = (StringParam) cl.getArg(DIRECTORY_ARG);
		String rawPath = dirUri.getValue();
		
		return cd(rawPath);
	}


	public boolean cd(String rawPath) {
		String decodedPath = decodePath(rawPath);
		String fullPath = null;
		try {
			fullPath = getShell().getCWD().cd(decodedPath);
			getResponse().printOut("new path: " + fullPath);
			return true;
		} catch (Exception e) {
			getResponse().logErr(e);
			getResponse().printErr("Directory does not exist: " + decodedPath);
			return false;
		}
	}


	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam dirUriArg = null;

		// options
		
		// cmd args
		dirUriArg = new StringParam(DIRECTORY_ARG, "directory uri",
				StringParam.REQUIRED);
		dirUriArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] { dirUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
