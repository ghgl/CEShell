/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import java.io.File;
import java.util.Properties;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.cmdline.VersionCmdLineHandler;
import com.ibm.bao.ceshell.util.PropertyUtil;

import filenet.vw.api.VWParameter;
import filenet.vw.api.VWParticipant;
import filenet.vw.api.VWStepElement;

/**
 *  FooPe
 *
 * @author regier
 * @date   Nov 12, 2011
 */
public class FooPeCmd extends BasePECommand {
	
	public static final String 
		CMD = "foope",
		CMD_DESC = CMD,
		FOO_ARG = "foo";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam fooArg = (StringParam) cl.getArg(FOO_ARG);
		String foo = null;
		if (fooArg.isSet()) {
			foo = fooArg.getValue();
		}
		stTst();
//		doTstSetValues();
		
		return true;
	}

/**
	 * 
	 */
	private void stTst() {
		String[] rawstr = new String[] {
				"[alpha|beta|gamma]",
				"[alpha||gamma]",
				"[alpha]",
				"[a]",
				"[|beta|gamma]",
				"[||gamma]",
				"[]"
		};
		int[] expected = {3,3,1,1,3,3, 0};
		String delim= "\\|";
		
		for(int i = 0; i < expected.length; i++) {
			String[] result = new String[] {};
			String raw =  rawstr[i];
			if (raw.startsWith("[") && raw.endsWith("]")) {
				if (raw.length() > 2) {
					String strippedArray = raw.substring(1, raw.length() - 1);
					result = strippedArray.split(delim);
				}
				if (expected[i] == result.length) {
					System.out.println("expected number for " + raw);
				} else {
					System.err.println("unexpected result for " + raw  + 
							" (expected " + expected[i] + " , actual: " + result.length);
				}
			} else {
				System.err.println("did not find delimiters");
			}
		}
		
	}

//	/**
//	 * 
//	 */
	private void doTstSetValues() throws Exception {
		Properties props = new PropertyUtil().loadPropertiesFromFile
				(new File("C:/testdata/demo/scenario-01/tst.props"));
		dispatchStepElement("approve", "test", "last", props);
	}
//	
//	/**
//	 * @param responseUri
//	 * @param queueName
//	 * @param wob
//	 */
	public void dispatchStepElement(
			String responseUri, 
			String queueName,
			String wobNum,
			Properties props) throws Exception {
		String decodedResponse = null;
		if (responseUri != null) {
			decodedResponse = getShell().urlDecode(responseUri);
		}
		VWStepElement stepElement = this.fetchStepElement(queueName, wobNum);
		boolean lockedByCurrentUser = false;

		VWParticipant lockOwner = stepElement.getLockedUserPx();
		if (lockOwner != null) {
			VWParticipant currentUser = getPEConnection().fetchCurrentUserInfo().getNamePx();
						
			if (lockOwner.getUserId() != currentUser.getUserId()) {
				throw new IllegalStateException("step locked by " + 
						lockOwner.getDisplayName());
			} else {
				lockedByCurrentUser = true;
			}
		}
		if (! lockedByCurrentUser) {
			stepElement.doLock(true);
		}
		
		if (props != null) {
			applyProperties(stepElement, props);
		}
		
		
//		if (decodedResponse != null) {
//			stepElement.setSelectedResponse(decodedResponse);
//		}
////		stepElement.doDispatch();
		stepElement.doSave(true);
		StringBuffer msg = new StringBuffer();
		msg.append("saved work item from work queue: ")
			.append(queueName)
			.append(" : ")
			.append(wobNum);
		if (decodedResponse == null) {
			msg.append("<null>");
		} else {
			msg.append(decodedResponse);
		}
		getResponse().printOut(msg.toString());
	}
	/**
	 * @param stepElement
	 * @param props
	 */
	private void applyProperties(VWStepElement stepElement, Properties props) 
			throws Exception {
		StepElementEditUtil seEditUtil = 
				new StepElementEditUtil(this.getShell(), stepElement);
		seEditUtil.setProperties(props);
	}

//
//	/**
//	 * @param stepElement
//	 * @param props
//	 */
//	private void applyProperties(VWStepElement stepElement, Properties props) 
//			throws Exception {
//		
//		for (Iterator iter = props.keySet().iterator();iter.hasNext();) {
//			String key = iter.next().toString();
//			String value = props.getProperty(key);
//			applyPropertyToStep(stepElement, key, value);
//		}
//	}
//
//	/**
//	 * @param stepElement
//	 * @param key
//	 * @param value
//	 */
//	private void applyPropertyToStep(VWStepElement stepElement, String key,
//			String value) throws Exception {
//		int fieldType = -1;
//		VWParameter vwParam = stepElement.getParameter(key);
//		if (vwParam == null) {
//			throw new IllegalArgumentException("No parameter with name " + key);
//		}
//		fieldType = vwParam.getFieldType();
//		switch(fieldType) {
//		case(VWFieldType.FIELD_TYPE_ATTACHMENT) :
//			applyAttachment(vwParam, value);
//			break;
//		case(VWFieldType.FIELD_TYPE_BOOLEAN) :
//			applyBoolean(vwParam, value);
//			break;
//		case(VWFieldType.FIELD_TYPE_FLOAT):
//			applyFloat(vwParam, value);
//			break;
//		case(VWFieldType.FIELD_TYPE_INT):
//			applyInt(vwParam, value);
//			break;
//		case (VWFieldType.FIELD_TYPE_PARTICIPANT) :
//			// todo
//			break;
//		case (VWFieldType.FIELD_TYPE_STRING):
//			applyString(vwParam, value);
//			break;
//		case (VWFieldType.FIELD_TYPE_TIME):
//			// todo
//		case(VWFieldType.FIELD_TYPE_XML):
//			// todo
//			break;
//		default :
//			// todo
//		
//		}
//	}
//
//	/**
//	 * @param vwParam
//	 * @param value
//	 */
//	private void applyFloat(VWParameter vwParam, String value) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/**
//	 * @param vwParam
//	 * @param value
//	 */
//	private void applyBoolean(VWParameter vwParam, String value) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/**
//	 * @param vwParam
//	 * @param value
//	 */
//	private void applyAttachment(VWParameter vwParam, String value) {
//		// TODO Auto-generated method stub
//		
//	}

	/**
	 * @param vwParam
	 * @param value
	 */
	private void applyString(VWParameter vwParam, String value) throws Exception {
		vwParam.setValue(value);
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		StringParam fooArgs = new StringParam(FOO_ARG,"any params",StringParam.OPTIONAL);
		fooArgs.setMultiValued(true);
		CmdLineHandler cl = new VersionCmdLineHandler("v1", 
				new HelpCmdLineHandler(
						CMD_DESC, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] {fooArgs }));
		cl.setDieOnParseError(false);

		return cl;
	}

}
