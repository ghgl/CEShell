/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import java.io.File;
import java.util.Properties;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.PropertyComparer;
import com.ibm.bao.ceshell.util.PropertyUtil;

import filenet.vw.api.VWStepElement;

/**
 *  StepElemementPropsCmd
 *
 * @author GaryRegier
 * @date   Jun 26, 2011
 */
public class StepElemementPropsCmd extends com.ibm.bao.ceshell.pe.BasePECommand {
	
	// param names
	private static final String 
		COMPARE_EXPCECTED_OPT = "compare-expected",
		EXPORT_PROPS_OPT = "file",
		QUEUE_NAME_ARG = "queue-name",
		WOB_NUM_ARG = "wobnum";
	
	public static final String 
		CMD = "pe.seprops", 
		CMD_DESC = "display step element properties",
		HELP_TEXT = "Usage:" +
			"\\npe.seprops <queue-name> <wobnum>";

	
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		FileParam compareExpectedOpt = (FileParam) cl.getOption(COMPARE_EXPCECTED_OPT);
		FileParam exportFileOpt = (FileParam) cl.getOption(EXPORT_PROPS_OPT);
		File compareExpected = null; 
		File exportFile = null;
		String queueName = cl.getArg(QUEUE_NAME_ARG).getValue().toString();
		String wobNum = cl.getArg(WOB_NUM_ARG).getValue().toString();
		
		if (compareExpectedOpt.isSet()) {
			compareExpected = compareExpectedOpt.getValue();
		}
		if (exportFileOpt.isSet()) {
			exportFile = exportFileOpt.getValue();
		}
		return stepElementProps(queueName, wobNum, compareExpected, exportFile);
	}
	
	/**
	 * @param queueName
	 * @param wobNum
	 * @param expectedPropsFile
	 */
	public boolean stepElementProps(
			String queueName, 
			String wobNum,
			File expectedPropsFile,
			File exportFile) throws Exception {
		VWStepElement stepElem =  null;
		StepElementEditUtil seUtil = null;
		
		stepElem = fetchStepElement(queueName, wobNum);
		if (stepElem == null) {
			getResponse().printErr("No work item found in queue " + queueName +
					"with wob " + wobNum);
			return false;
		}
		seUtil = new StepElementEditUtil(this.getShell(), stepElem);
		
		if (exportFile != null) {
			exportStepElemParams(seUtil, exportFile);
		} else if (expectedPropsFile != null) {
			compareStepElementPropsToExpected(seUtil, expectedPropsFile);
		} else {
			seUtil.displayStepElemProps(wobNum);
		}
		return true;
	}

	/**
	 * @param seUtil
	 */
	private void exportStepElemParams(StepElementEditUtil seUtil, File exportFile) 
			throws Exception {
		Properties actual = seUtil.stepElementToProps();
		PropertyUtil propUtil = new PropertyUtil(); 
		propUtil.storePropertiesToFile(actual, exportFile, seUtil.getWob());
	}

	/**
	 * @param stepElem
	 * @param compareExpected
	 */
	private void compareStepElementPropsToExpected(StepElementEditUtil seUtil,
			File compareExpectedProps) throws Exception {
		Properties expected = new PropertyUtil().loadPropertiesFromFile(compareExpectedProps);
		PropertyComparer propComparer = seUtil.compareStepElementPropsToExpected(expected);
		if (propComparer.isAllMatched()) {
			String msg  = "Step element properties compare: property values matched expected on " + 
					propComparer.getMatchedCount() + " properties";
			getResponse().printOut(msg);
		} else {
			getResponse().printErr("Properties do not match expected");
			getResponse().printErr(propComparer.getErrorResults());
		}
	}

//	/**
//	 * @param stepElem
//	 * @return
//	 */
//	protected Properties stepElementToProps(VWStepElement stepElem) throws Exception {
//		VWParameter[] params = stepElem.getParameters(VWFieldType.ALL_FIELD_TYPES, 
//				VWStepElement.FIELD_USER_AND_SYSTEM_DEFINED);
//		Properties props = new Properties();
//		for (VWParameter param : params) {
//			String name = param.getName();
//			String value = "";
//			Object paramValue = param.getValue();
//			if (paramValue != null) {
//				if (VWFieldType.FIELD_TYPE_TIME ==  param.getFieldType()) {
//					java.util.Date d = (java.util.Date) paramValue;
//					value = StringUtil.fmtDate(d, StringUtil.DATE_FMT_PE);
//				}
//				value = paramValue.toString();
//			}
//			props.put(name, value);
//		}
//		return props;
//	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam queueNameArg = null;
		StringParam wobArg = null;
		FileParam compareExpectedOpt = null;
		FileParam exportPropsOpt = null;
		
		// options
		compareExpectedOpt = new FileParam(
				COMPARE_EXPCECTED_OPT, 
				"compare to expected values", 
				FileParam.IS_FILE & FileParam.IS_READABLE,
				FileParam.OPTIONAL);
		compareExpectedOpt.setOptionLabel("<props-file>");
		compareExpectedOpt.setMultiValued(FileParam.SINGLE_VALUED);
		
		exportPropsOpt = new FileParam(EXPORT_PROPS_OPT, 
				"export properties", 
				FileParam.OPTIONAL);
		exportPropsOpt.setOptionLabel("<file>");
		exportPropsOpt.setMultiValued(FileParam.SINGLE_VALUED);
		
		// cmd args
		queueNameArg = new StringParam(
				QUEUE_NAME_ARG, 
				"queue to list",
				StringParam.REQUIRED);
		
		wobArg = new StringParam(WOB_NUM_ARG, 
				"Work object number", 
				StringParam.REQUIRED);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {compareExpectedOpt, exportPropsOpt}, 
					new Parameter[] { queueNameArg, wobArg});
		cl.setDieOnParseError(false);

		return cl;
	}

}
