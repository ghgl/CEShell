/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.Iterator;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  PWCryptCmd
 *
 * @author GaryRegier
 * @date   Dec 15, 2010
 */
public class PWCryptCmd extends BaseCommand {
	
	private static final String 
		CMD = "util.pwcrypt", 
		CMD_DESC = "Encrypt the passwords in the sudo properties files",
		HELP_TEXT = "Encrypt the passwords in the sudo properties files\n" +
			"If a password has already been encrypted, it is passed over during the encrypt process." +
			"Therefore it is safe to reencrypt a file that has been fully or partially " +
			" encrypted before.\n" +
			"Usage:\n" + 
			"\tutil.python                   encrypt the passwords used by the current connection alias" +
			 "\tutil.python <alias>          encrypt the passwords used by the current alias\n"  +
			 "\tutil.python -all             encrypt all the password files associated with the current connection aliases";
	
			 
	// param names
	private static final String 
			ALL_OPT = "all",
			ALIAS_ARG = "alias";
	
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam allOpt = (BooleanParam) cl.getOption(ALL_OPT);
		StringParam aliasArg = (StringParam) cl.getArg(ALIAS_ARG);
		String alias = getShell().getConnectionAlias();
		boolean all = Boolean.FALSE;
		
		if (allOpt.isSet()) {
			all = Boolean.TRUE;
		}
		if (aliasArg.isSet()) {
			alias = aliasArg.getValue().toString();
		}
		return doPWCrypt(all, alias);
		
	}
	
	private boolean doPWCrypt(boolean all, String alias) throws Exception {
		String result = null;
		if (all) {
			encryptAllConnectionPasswords();
		} else {
			result = encryptPasswords(alias);
			getResponse().printOut(result);
		}
		return true;
	}

	/**
	 * 
	 */
	private void encryptAllConnectionPasswords() throws Exception {
		String result = null;
		Iterator<String> iter = getShell().getConnectionMgr()
				.getConnectionAliasNames().iterator();
		while (iter.hasNext()) {
			String nextAlias = iter.next();
			result = encryptPasswords(nextAlias);
			getResponse().printOut(result);
		}		
	}

	private String encryptPasswords(String alias) throws Exception {
		CEConnectInfo connectInfo = getShell().getConnectionMgr()
				.getConnectionStorageInfo(alias).getConnectionInfo();
		if (connectInfo == null) {
			throw new IllegalArgumentException("Connection with alias " + alias +
					" does not exist");
		}
		
		File sudoFile = getShell().getConnectionMgr().getConnectionStorageInfo(alias).getSudoFile();
		getShell().getConnectionMgr().getCECryptoManager().encryptPropertyValues(sudoFile);
		return "Encrypted passwords for alias " + alias + " in file " + sudoFile.toString();
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam allOpt = null;
		StringParam aliasArg = null;
		
		// params
		allOpt = new BooleanParam(ALL_OPT,"encrypt all password files");
		// cmd args
		{
			aliasArg = new StringParam(ALIAS_ARG, 
					"connection alias to encrypt",
					StringParam.OPTIONAL);
			aliasArg.setMultiValued(false);
		}
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { allOpt }, 
					new Parameter[] { aliasArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
