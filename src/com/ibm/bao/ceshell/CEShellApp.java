package com.ibm.bao.ceshell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.ParseResultInfo;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.cmdline.VersionCmdLineHandler;

public class CEShellApp {
	
	private static final String 
		CMD = "ceshellapp", 
		VERSION = "v0.1",
		APP_DESC = "Command line client for IBM FileNet Content Engine",
		HELP_TEXT = "" +
		"ceshellapp -homeDir <dirName> <aliasName>\n";
	
	// param names
	private static final String
		DEFAULT_CONN_ALIAS_ARG =	"alias";
	
	protected CEShell ceShell;
	
	public CEShellApp() throws Exception {
		ceShell = new CEShell();
		ceShell.init();
	}
	
	public void init(String[] args) throws Exception {
		CmdLineHandler cl = getCommandLine();
		ParseResultInfo parseResult = cl.parse(args);
		Parameter<?> aliasParam = cl.getArg(DEFAULT_CONN_ALIAS_ARG);
		String defaultConnectionAlias = null;

		if (parseResult.isHelp()) {
			System.exit(0);
		}
		if (parseResult.isVersion()) {
			System.exit(0);
		}
		if (! parseResult.isSuccess()) {
			System.out.println(parseResult.getReturnCode());
			System.exit(1);
		}
		
		if (aliasParam != null && aliasParam.isSet()) {
			defaultConnectionAlias = aliasParam.getValue().toString();
		}
		
		if (defaultConnectionAlias != null) {
			ceShell.connect(defaultConnectionAlias);
		}
	}
	
	public void run() throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while ( true ) {
			String cmdPrompt = getPrompt();
			String cmdLine = null;
			System.out.println();
			System.out.println();
			System.out.print(cmdPrompt);
			cmdLine = br.readLine();
			cmdLine = cmdLine.trim();
			ceShell.execute(cmdLine);
		}
	}

	public String getPrompt() {
		String cmdPrompt = null;
		String osName = null;
		
		osName = ceShell.getOSName();
		if (ceShell.isEditMode()) {
			cmdPrompt = ceShell.getConnectionAlias() + " - " + osName + " (e) $: ";				
		} else {
			cmdPrompt = ceShell.getConnectionAlias() + " - " + osName + " $: ";
		}
		 
		return cmdPrompt;
	}
	
	/**
	 * Run the application
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CEShellApp app = new CEShellApp();
		
		app.init(args);
		app.run();
	}	
	
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;

		// params
		StringParam aliasArg = new StringParam(DEFAULT_CONN_ALIAS_ARG, 
				"alias", 
				StringParam.OPTIONAL);
		aliasArg.setOptionLabel("<alias>");
		
		// cmd args
		
		// create command line handler
		cl = new VersionCmdLineHandler(VERSION, 
				new HelpCmdLineHandler(
						HELP_TEXT, CMD, APP_DESC, 
					new Parameter[] {}, 
					new Parameter[] {aliasArg }));
		cl.setDieOnParseError(false);
		
		return cl;
	}
	
	protected CEShell getCEShell() {
		return this.ceShell;
	}
}
