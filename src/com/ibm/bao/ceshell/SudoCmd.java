/**
 * 
 */
package com.ibm.bao.ceshell;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  SUCmd
 *  <br>
 *  Substitute User command. This is based on the UNIX sudo command.
 *  <br>Enable users to change to different credentials.
 *
 * @author GaryRegier
 * @date   Sep 30, 2010
 */
public class SudoCmd extends BaseCommand {
	
	private static final String 
		CMD = "sudo", 
		CMD_DESC = "sudo to a different username/password",
		HELP_TEXT = "\nUsers and passwords are kept in a sudo list properties file" +
			"\nthat is loaded during the connect process. If the optional password " +
			" is not provided, then the password for the user is looked up in the " +
			" password file" +
			"\n\nusage:" +
			"\n\tsudo <user>" +
			"\n\nConnect as a user and provide a password:" +
			"\n\t sudo -p <pass> <user>";
			
	

	// param names
	private static final String 

		USER_ARG = "user",
		PASS_OPT = "pass";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam passOpt = (StringParam) cl.getOption(PASS_OPT);
		StringParam userArg = (StringParam) cl.getArg(USER_ARG);
		String user = userArg.getValue();
		String pass = null;
		
		if (passOpt.isSet()) {
			pass = passOpt.getValue();
		}
		return sudo(user, pass);
	}

	/**
	 * @param user
	 * @param pass
	 */
	public boolean sudo(String user, String pass) throws Exception {
		//TODO: bugs in the sudo when it fails
		//       to recreate the problem:
		//        -- change the sudo user password to a bad password
		//        when it fails, the exception prints a stacktrace, but is
		//        not re-thrown. The wrong user is printed out.
		try {
			getShell().changeUserCredentials(user, pass);
			getResponse().printOut("connected as " + user);
			return true;
		} catch (Exception e) {
			String msg = "Failed to sudo to " + user + " -- " + e.getMessage() + 
					". Current user is " + this.getShell().getCurrentUser();
			getResponse().printErr(msg);	
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam passOpt = null;
		StringParam userArg = null;

		// options
		passOpt = new StringParam(
				PASS_OPT,
				"password credentials",
				StringParam.OPTIONAL);
		passOpt.setOptionLabel("<password>");
		
		// cmd args
		userArg = new StringParam(
				USER_ARG, 
				"user to change to",
				StringParam.REQUIRED);
		userArg.setOptionLabel("<user>");
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { passOpt }, 
					new Parameter[] { userArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
