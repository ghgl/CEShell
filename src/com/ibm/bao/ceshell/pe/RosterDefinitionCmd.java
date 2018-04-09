package com.ibm.bao.ceshell.pe;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import filenet.vw.api.IVWtoXML;
import filenet.vw.api.VWRosterDefinition;
import filenet.vw.api.VWXMLConfiguration;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

public class RosterDefinitionCmd extends BasePECommand {
	
	public static final String 
		CMD = "pe.rdef", 
		CMD_DESC = "Display the roster definition",
		HELP_TEXT = "Usage:" +
			"\npe.qdef <roster-name>";

	// param names
	protected static final String 
		ROSTER_NAME_ARG = "queue-name";

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String rosterName = cl.getArg(ROSTER_NAME_ARG).getValue().toString();
		
		return rosterDefinition(rosterName);
	}

	public boolean rosterDefinition(String rosterName) throws Exception {
		VWRosterDefinition rosterDef = getPEConnection().
				fetchSystemConfiguration().
						getRosterDefinition(rosterName);
		StringBuffer buf = new StringBuffer();
		IVWtoXML rosterXml = (IVWtoXML) rosterDef;
		rosterDef.toXML(buf);
		System.out.println(buf.toString());
		return true;
	}

	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam rosterNameArg = null;
		
		// options
		
		// cmd args
		rosterNameArg = new StringParam(ROSTER_NAME_ARG, 
				"roster to describe",
				StringParam.REQUIRED);
		rosterNameArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { rosterNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
