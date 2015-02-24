/**
 * 
 */
package com.ibm.bao.ceshell;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  ConnectCmd
 *
 * @author GaryRegier
 * @date   Sep 21, 2010
 */
public class ConnectCmd extends BaseCommand {
	public
	static final String 
		CMD = "connect", 
		CMD_DESC = "connect to an object store",
		HELP_TEXT = CMD_DESC +"\n" +
				"connect using options passed at the command line or " +
				"values stored in a file. If a file option is provided, then " +
				"the values are expected to be in a properties file like\n\n" +
				"\tuser=<user>\n\tpassword=<password>\n\turl=<url>\n\tos=<object-store>\n\n" +
				"If a file option is provided, the options from the properties file are " +
				"loaded first, and then the other options are processes\n\n" +
				"Sample:\n\tconnect -user fnadmin -pass fnpass -os bhosecc \n\t" + 
				"-url http://bhd1fnece01.bhdev1.ibm.com:9080/wsi/FNCEWS40MTOM\n\n" +
				"Connect using an alias:\n" +
				"\t <alias-name>";

	// param names
	private static final String
		URL_OPT = "connectionUrl",
		USER_OPT = "user",
		OBJECTSTORE_OPT = "objectStore",
		PASWORD_OPT = "pass", 
		ALIAS_ARG = "alias";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		CEConnectInfo connectInfo = readCmdLine(cl);
		return connect(connectInfo);
	}

	public boolean connect(CEConnectInfo connectInfo) throws Exception {
		this.getShell().connect(connectInfo);
		getResponse().printOut("Connected to " + 
				connectInfo.getObjectStore() +
				" as " + connectInfo.getUser() +
				" at " + connectInfo.getConnectUrl());
		return true;
	}

	private CEConnectInfo readCmdLine(CmdLineHandler cl) throws Exception {
		CEConnectInfo connectInfo = null;
		StringParam objectStoreParam = (StringParam) cl.getOption(OBJECTSTORE_OPT);
		StringParam urlParam = (StringParam) cl.getOption(URL_OPT);
		StringParam userParam = (StringParam) cl.getOption(USER_OPT);
		StringParam passwordParam = (StringParam) cl.getOption(PASWORD_OPT);
		StringParam aliasParam = (StringParam) cl.getArg(ALIAS_ARG);
		String alias = null;
		
		alias = aliasParam.getValue();
		connectInfo = getShell().getConnectionMgr().getConnectionStorageInfo(alias).getConnectionInfo();
		
		if (userParam != null &&
				userParam.isSet()) {
			String user = userParam.getValue();
			connectInfo.setUser(user);
		}
		
		if (passwordParam != null &&
				passwordParam.isSet()) {
			String pass = passwordParam.getValue();
			connectInfo.setPass(pass);
		}
		
		if ( objectStoreParam != null &&
				objectStoreParam.isSet()){
			String os = objectStoreParam.getValue();
			connectInfo.setObjectStore(os);
		}
		
		if (urlParam != null &&
				urlParam.isSet()){
			String url = urlParam.getValue();
			connectInfo.setConnectUrl(url);
		}
		
		return connectInfo;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam userOpt = null;
		StringParam passwordOpt = null;
		StringParam objectStoreOpt = null;
		StringParam urlOpt = null;
		StringParam aliasArg = null;

		// options
		userOpt = new StringParam(USER_OPT,
				"CE user credentials",
				true);
		
		passwordOpt = new StringParam(PASWORD_OPT,
				"password for connecting",
				true);
		
		objectStoreOpt = new StringParam(OBJECTSTORE_OPT,
				"Object store to connect to",
				true);
		
		urlOpt = new StringParam(URL_OPT,
				"URL to the content engine: ",
				StringParam.OPTIONAL);
		
		// cmd args
		aliasArg = new StringParam(ALIAS_ARG,
				"connection alias",
				StringParam.REQUIRED);
		

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { objectStoreOpt, urlOpt, userOpt, passwordOpt }, 
					new Parameter[] {  aliasArg });
		cl.setDieOnParseError(false);
		
		return cl;
	}
}
