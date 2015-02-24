package com.ibm.bao.ceshell;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.cmdline.VersionCmdLineHandler;

public class HelpCmd extends BaseCommand {
	
	private static final String
		OPTIONS = "Option tags are not case sensitive, and may be truncated as long as they remain " +
		"unambiguous.  Option tags must be separated from their corresponding values by " +
		"whitespace, or by an equal sign.  Boolean options (options that require no " +
		"associated value) may be specified alone (=true), or as 'tag=value' where value " +
		" is 'true' or 'false'. ";
	
	private static final String 
			CMD = "cmds", 
			VERSION = "v0.1",
			CMD_DESC = "Give help usage with the ceShell program",
			HELP_TEXT = "List the commands that are available\n" +
			OPTIONS +
			"Usage:\n" +
			"\tcmds           lists all commands";
			
	
	// param names
	private static final String 
			CMD_NAME_PATTERN = "CMDNAME";
	@Override
	protected boolean doRun(CmdLineHandler cmdLine) throws Exception {
		String pattern = null;
		Parameter<?> cmdNamePattern = cmdLine.getArg(CMD_NAME_PATTERN);
		
		if (cmdNamePattern.isSet()) {
			pattern = cmdNamePattern.getValue().toString();
		}
		return help(pattern);
	}

	public boolean help(String pattern) throws Exception {
		Map<String, String> commandsMap = getShell().getCommands();
		Set<String> cmdIds = commandsMap.keySet();
		SortedSet<String> ss = new TreeSet<String>();
		ss.addAll(cmdIds);
		for (Iterator<String> iter = ss.iterator(); iter.hasNext();) {
			String nextCmd = iter.next();
			if (pattern != null) {
				if (nextCmd.startsWith(pattern)) {
					getResponse().printOut(nextCmd);
				}
			} else {
				getResponse().printOut(nextCmd);
			}
		}
		return true;
	}

	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam helpPatternArg = null;
		
		// options
		
		// cmd args
		helpPatternArg = new StringParam(CMD_NAME_PATTERN, "commands that start wtih this pattern",
				StringParam.OPTIONAL);
		helpPatternArg.setMultiValued(false);

		// create command line handler
		cl = new VersionCmdLineHandler(VERSION, 
				new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { helpPatternArg }));
		cl.setDieOnParseError(false);

		return cl;
	}
}
