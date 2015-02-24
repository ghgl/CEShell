/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import filenet.vw.api.VWStepElement;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  WFLaunchStepProps
 *
 * @author regier
 * @date   Nov 5, 2011
 */
public class WFLaunchPropsCmd extends BasePECommand {
	
	// param names
	private static final String 
		WORKFLOW_NAME_ARG = "workflow-name";
	
	public static final String 
		CMD = "pe.wflaunchprops", 
		CMD_DESC = "Describe a workflow launch step",
		HELP_TEXT = "pe.wflaunchprops:" +
			"\npe.wflaunchprops <workflow-name>";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String workflowName = cl.getArg(WORKFLOW_NAME_ARG).getValue().toString();
		
		
		return launchWorkflowProps(workflowName);
	}

	/**
	 * @param workflowName
	 * @param response
	 */
	public boolean launchWorkflowProps(String workflowName) 
			throws Exception {
		String decodedName = this.decodePath(workflowName);
		VWStepElement launchStep = getPEConnection().createWorkflow(decodedName);
		if (launchStep == null) {
			getResponse().printErr("No workflow found with name " + workflowName);
		}
		String msg = "Workfkow launch step properties " + workflowName;
		getResponse().printOut(msg);
		StepElementEditUtil seUtil = new StepElementEditUtil(getShell(), launchStep);
		seUtil.displayStepElemProps(decodedName);
		return true;
//		displayStepElemProps(launchStep, decodedName);
	}
	
	

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam workflowNameArg = null;
		
		// options
		
		// cmd args
		workflowNameArg = new StringParam(
				WORKFLOW_NAME_ARG,
				"workflow launch step to examine",
				StringParam.REQUIRED);
		


		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] {workflowNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
