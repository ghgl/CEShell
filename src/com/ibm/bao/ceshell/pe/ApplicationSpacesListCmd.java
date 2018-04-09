package com.ibm.bao.ceshell.pe;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import filenet.vw.api.VWApplicationSpaceDefinition;
import filenet.vw.api.VWSystemConfiguration;
import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

public class ApplicationSpacesListCmd extends BasePECommand {
	
	private static final String 
		CMD = "pe.asls", 
		CMD_DESC = "list the application spaces",
		HELP_TEXT = CMD_DESC +
			"Usage: \n" +
			"pe.asls" +
			"pe.qsls UCM.*";
	// Params
	public static final String 
		Pattern = "pattern";

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String namePattern = ".*";
		StringParam namePatternArg = (StringParam) cl.getArg(Pattern);
		if (namePatternArg.isSet()) {
			namePattern = namePatternArg.getValue();
		}
		return asls(namePattern);
	}

	public boolean asls(String namePattern) throws Exception {
		Boolean ignoreSecurity = true;
		String[] names = getPEConnection().fetchAppSpaceNames(ignoreSecurity);
		for (String name : names) {
			if (name.matches(namePattern)) {
				System.out.println(name);
			}
		}
		
		
		return true;
	}

	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam namePatternArg = null;
		
		// options
		
		
		// cmd args
		namePatternArg = new StringParam(Pattern, "Name pattern of Application Spaces to list",
				StringParam.OPTIONAL);
		namePatternArg.setMultiValued(false);
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {}, 
					new Parameter[] {namePatternArg});
		cl.setDieOnParseError(false);

		return cl;
	}

}
