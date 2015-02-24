/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.PropertyUtil;

import filenet.vw.api.VWParticipant;
import filenet.vw.api.VWStepElement;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  StepElementDispatchCmd
 *
 * @author GaryRegier
 * @date   Jun 29, 2011
 */
public class StepElementDispatchCmd extends BasePECommand {
	
	// param names
	private static final String 
		PROPS_OPT = "file",
		RESPONSE_OPT = "response",
		QUEUE_NAME_ARG = "queue-name",
		WOB_NUM_ARG = "wobnum";
	
	public static final String 
		CMD = "pe.sedisp", 
		CMD_DESC = "step element dispactch -- complete the step",
		HELP_TEXT = "Usage:" +
		"\npe.sedispatch <response> <queue-name> <wobnum>" +
			"\npe.sedispatch -response <response> <queue-name> <wobnum>";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		FileParam propsOpt = (FileParam) cl.getOption(PROPS_OPT);
		File propsFile = null;
		StringParam responseOpt = (StringParam) cl.getOption(RESPONSE_OPT);
		String queueName = ((StringParam) cl.getArg(QUEUE_NAME_ARG)).getValue();
		String wob = ((StringParam) cl.getArg(WOB_NUM_ARG)).getValue();
		String responseUri = null;
		
		if (propsOpt.isSet()) {
			propsFile = propsOpt.getValue();
		}
		
		if (responseOpt.isSet()) {
			responseUri = responseOpt.getValue();
		}
		
		return dispatchStepElement(responseUri, queueName, wob, propsFile);
	}

	/**
	 * @param responseUri
	 * @param queueName
	 * @param wob
	 */
	public boolean dispatchStepElement(
			String responseUri, 
			String queueName,
			String wobNum,
			File propsFile) throws Exception {
		String decodedResponse = null;
		if (responseUri != null) {
			decodedResponse = getShell().urlDecode(responseUri);
		}
		VWStepElement stepElement = this.fetchStepElement(queueName, wobNum);
		StepElementEditUtil seUtil = new StepElementEditUtil(getShell(), stepElement);
		seUtil.doLock();
		try {
			if (propsFile != null) {
				PropertyUtil helper = new PropertyUtil();
				java.util.Properties props = helper.loadPropertiesFromFile(propsFile);
				seUtil.setProperties(props);
			}
			seUtil.doDispatch(decodedResponse);
			displayResult(queueName, wobNum, decodedResponse);
			return true;
		} catch (Exception e) {
			getResponse().printErr("Failed to set dispatch step: "  + e.getMessage());
			return false;
		}
	}

	public void displayResult(
			String queueName, 
			String wobNum,
			String decodedResponse)
			throws Exception {
		StringBuffer msg = new StringBuffer();
		msg.append("Dispatched work item from work queue: ")
			.append(queueName)
			.append(" : ")
			.append(wobNum)
			.append(" with response: ");
		if (decodedResponse == null) {
			msg.append("<null>");
		} else {
			msg.append(decodedResponse);
		}
		getResponse().printOut(msg.toString());
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		FileParam propsOpt = null;
		StringParam responseOpt = null;
		StringParam queueNameArg = null;
		StringParam wobArg = null;
		
		// options
		responseOpt = new StringParam(RESPONSE_OPT, 
				"step response",
				StringParam.OPTIONAL);
		
		propsOpt = new FileParam(PROPS_OPT, 
				"property values to set on dispatch", 
				FileParam.IS_FILE & FileParam.EXISTS,
				FileParam.OPTIONAL
				);
		propsOpt.setOptionLabel("<file>");
		propsOpt.setMultiValued(FileParam.SINGLE_VALUED);
		
		
		// cmd args
		queueNameArg = new StringParam(
				QUEUE_NAME_ARG, 
				"queue to of work object",
				StringParam.REQUIRED);
		
		wobArg = new StringParam(WOB_NUM_ARG, 
				"Work object number", 
				StringParam.REQUIRED);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { propsOpt, responseOpt}, 
					new Parameter[] { queueNameArg, wobArg});
		cl.setDieOnParseError(false);

		return cl;
	}

}
