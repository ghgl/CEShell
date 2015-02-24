package com.ibm.bao.ceshell;

import java.util.List;

import jcmdline.CmdLineHandler;
import jcmdline.IntParam;
import jcmdline.Parameter;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

public class HistoryCmd extends BaseCommand {
	
	private static final String 
		CMD = "history", 
		CMD_DESC = "List the command history",
		HELP_TEXT = "Usage:" +
		"\n\thistory" +
		"\n\t Show the history commands with a command number" +
		"\n\tHistorical commands can be reexecuted withe the bang (!) commands";
	
	// options names
	private static final String 
			LINES = "Number of Lines";
	
	private static final int SHOW_ALL = -1;

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		IntParam cntArg = (IntParam) cl.getArg(LINES);
		int maxHistoryToShow = -1;
		
		if (cntArg.isSet()) {
			maxHistoryToShow = cntArg.getValue();
		}
		return history(maxHistoryToShow);
	}

	public boolean history(Integer maxHistoryToShow) {
		List<BaseCommand> historyList = this.getShell().getHistoryList();
		int currentHistorySize = historyList.size();
		int startPos = 0;
		if (maxHistoryToShow != SHOW_ALL) {
			if (maxHistoryToShow < currentHistorySize) {
				startPos = currentHistorySize - maxHistoryToShow;
			}
		}
		
		for (int i = startPos; i < historyList.size(); i++ ) {
			BaseCommand cmd = (BaseCommand) historyList.get(i);
			String cmdLine = cmd.getRequest().getCmdLine();
			getResponse().getOut().print(i + 1 + ":\t");	// 1-based numbering
			getResponse().getOut().println(cmdLine);
		}
		return true;
	}

	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		IntParam cntArg = null;

		// options
				
		// cmd args
		cntArg = new IntParam(LINES, "Number of history lines to show",
				IntParam.OPTIONAL);
		cntArg.setMultiValued(false);
		cntArg.setMin(0);
		cntArg.setMax(100);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { cntArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
