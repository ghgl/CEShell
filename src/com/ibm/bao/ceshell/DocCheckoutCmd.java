/**
 * 
 */
package com.ibm.bao.ceshell;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.constants.ReservationType;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.DocCheckoutDTO;
import com.ibm.bao.ceshell.util.DocUtil;

/**
 *  DocCheckoutCmd
 *
 * @author regier
 * @date   Sep 2, 2011
 */
public class DocCheckoutCmd extends BaseCommand {
	
	private static final String 
		CMD = "docckout", 
		CMD_DESC = "checkout a document",
		HELP_TEXT = "example:  \n" + 
					"\t  docckout <uri> -- checkout a document with default object store lock\n" +
					"\n\tTo checkout a document and set either an exclusive or collaborative lock:\n" +
					"\t  docckout -exclusive <uri> -- checkout a document with exclusive object store lock\n" +
					"\tThe exclusive and collaborative options are mutually exclusive. If both are set, then exclusive takes precedence" +
					"\t  docckout -collaborative <uri> -- checkout a document with collaborative (non-exclusive) object store lock\n" +
					"\n\nTo update a check-out document, use docckin";
	
	private static final String
		CANCEL_OPT = "cancel",
		EXCLUSIVE_lOCK_OPT = "exclusive",
		COLLABORATIVE_LOCK_OPT = "nonexclusive",
		URI_ARG = "URI";

	/* 
	 * Default lock method is ReservationType.OBJECT_STORE_DEFAULT
	 * Exclusive takes precedence over collaborative. 
	 * 
	 * (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam exclusiveLockOpt = (BooleanParam) cl.getOption(EXCLUSIVE_lOCK_OPT);
		BooleanParam collaborativeLockOpt = (BooleanParam) cl.getOption(COLLABORATIVE_LOCK_OPT);
		StringParam docUriParam = (StringParam) cl.getArg(URI_ARG);
		String docUri = null;
		Boolean exclusiveLock = Boolean.FALSE;
		Boolean collaborativeLock = Boolean.FALSE;
		
		if (exclusiveLockOpt.isSet()) {
			exclusiveLock = exclusiveLockOpt.getValue();
		}
		if (collaborativeLockOpt.isSet()) {
			collaborativeLock = collaborativeLockOpt.getValue();
		}
		docUri = docUriParam.getValue();
		
		return checkout(docUri, exclusiveLock, collaborativeLock);
	}

	/**
	 * 
	 * @param docUri
	 * @param collaborativeLock 
	 * @return
	 * @throws Exception 
	 */
	public boolean checkout(String docUri, Boolean exclusiveLock, Boolean collaborativeLock) throws Exception {
		DocUtil docUtil = new DocUtil(this.getShell());
		DocCheckoutDTO docCheckoutInfo = new DocCheckoutDTO(docUri);
		
		/** exclusive has precendence, so only evaluate collaborative if exclusive not set **/
		if (Boolean.TRUE.equals(exclusiveLock)) {
			docCheckoutInfo.setReservationType(ReservationType.EXCLUSIVE);
		} else if(Boolean.TRUE.equals(collaborativeLock)) {
			docCheckoutInfo.setReservationType(ReservationType.COLLABORATIVE);
		}
		
		return docUtil.checkout(docCheckoutInfo);
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		BooleanParam collaborativeLock = null;
		BooleanParam exclusiveLock = null;
		StringParam pathURIArg = null; 
		

		/** options **/
		// exclusive
		{
			exclusiveLock = new BooleanParam(EXCLUSIVE_lOCK_OPT, "Checkout with an exclusive lock");
			exclusiveLock.setOptional(BooleanParam.OPTIONAL);
			
		}
		
		// collaborativeLock
		{
			collaborativeLock = new BooleanParam(COLLABORATIVE_LOCK_OPT, "Checkout with a collaborative lock");
			collaborativeLock.setOptional(BooleanParam.OPTIONAL);
		}
		// cmd args
		{
			pathURIArg = new StringParam(URI_ARG, "URI indicating a document",
					StringParam.REQUIRED);
			pathURIArg.setMultiValued(false);
		}
		{
			
		}
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {exclusiveLock, collaborativeLock}, 
					new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
