/**
 * 
 */
package com.ibm.bao.ceshell.util;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.BaseCommand;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  EncryptTextCmd
 *
 * @author regier
 * @date   Apr 23, 2014
 */
public class EncryptTextCmd extends BaseCommand {
	
	private static final String 
		CMD = "util.encrypt", 
		CMD_DESC = "Turn clear-text into encrypt-text\n" +
				"The encrypted text may be used to store passwords or other sensitive information\n",
		HELP_TEXT = "Usage:\n" + "\tutil.encrypt <clear-text>";

// param names
private static final String 
	DECRYPT_OPT = "decrypt",
	CLEARTEXT_ARG = "cleartext";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam decryptOpt = null;
		StringParam clearTextArg = null;
		String clearText = null;
		Boolean decrypt = false;
		
		decryptOpt = (BooleanParam) cl.getOption(DECRYPT_OPT);
		if (decryptOpt.isSet()) {
			decrypt = decryptOpt.getValue();
		}
		
		clearTextArg = (StringParam) cl.getArg(CLEARTEXT_ARG);
		clearText = clearTextArg.getValue();
		
				
		return encrypt(decrypt, clearText);
		
	}

	/**
	 * @param userInput
	 * @return
	 */
	private boolean encrypt(Boolean decrypt, String userInput) throws Exception {
		String result = "";
		if (decrypt) {
			result = getShell()
					.getConnectionMgr()
					.getCECryptoManager()
					.getClearText(userInput);
		} else {
			result = getShell()
					.getConnectionMgr()
					.getCECryptoManager()
					.encryptText(userInput);
		}
		getResponse().printOut(result);
		
		return true;

	}




	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam decryptOpt = null;
		StringParam clearTextArg = null;
		
		

		// options
		
		decryptOpt = new BooleanParam("decrypt", 
				"cryptText to clearText");
		decryptOpt.setHidden(BooleanParam.HIDDEN);
		decryptOpt.setOptional(BooleanParam.OPTIONAL);
		
		// cmd args
		clearTextArg = new StringParam(CLEARTEXT_ARG, "text to be encrypted",
				StringParam.REQUIRED);
		clearTextArg.setMultiValued(false);
		clearTextArg.setOptionLabel("<cleartxt>");
		
				
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {decryptOpt }, 
					new Parameter[] { clearTextArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
