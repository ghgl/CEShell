/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.cmdline.VersionCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.PropertyUtil;
import com.ibm.bao.ceshell.util.StringUtil;

import filenet.vw.api.VWApplicationSpaceDefinition;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWParameter;
import filenet.vw.api.VWParticipant;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWRoleDefinition;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWSystemConfiguration;
import filenet.vw.api.VWWorkBasketDefinition;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 * FooPe
 *
 * @author regier
 * @date Nov 12, 2011
 */
public class FooPeCmd extends BasePECommand {

	public static final String CMD = "foope", CMD_DESC = CMD, FOO_ARG = "foo";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam fooArg = (StringParam) cl.getArg(FOO_ARG);
		String foo = null;
		if (fooArg.isSet()) {
			foo = fooArg.getValue();
		}
		// stTst();
		// doTstSetValues();
		doTestVWSystemConfiguration();
		//doTestMatches();
		//doTestInboxQuery();
		return true;
	}

	private void doTestInboxQuery() throws Exception {
		ColDef[] defs = new ColDef[] { 
				new ColDef("WobNum", 32, StringUtil.ALIGN_LEFT),
				new ColDef("F_BoundUser", 24, StringUtil.ALIGN_LEFT),
				new ColDef("F_CaseFolder", 40, StringUtil.ALIGN_LEFT),
				new ColDef("Name", 30, StringUtil.ALIGN_LEFT), new ColDef("Step Name", 30, StringUtil.ALIGN_LEFT),
				new ColDef("Status", 14, StringUtil.ALIGN_LEFT), new ColDef("Received On", 28, StringUtil.ALIGN_LEFT),
				new ColDef("Workflow name", 33, StringUtil.ALIGN_LEFT),
				new ColDef("LockOwner", 20, StringUtil.ALIGN_LEFT) };
		
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		VWQueue queue = getPEConnection().getQueue("Inbox(0)");
		String queryIndex = "F_Fifo";
		Integer fetchType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
		Object[] minValues = null;
		Object[] maxValues = null;
		int queryFlags = VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_LOCKED;
		Object[] substitutionVars = null;
		String filter = null;

		// TODO. Verify this works if an item is locked.
		VWQueueQuery queueQuery = queue.createQuery(queryIndex, minValues, maxValues, queryFlags, filter,
				substitutionVars, fetchType);
		
		int cnt = 0;
		int maxItems = 100;
		
		while (queueQuery.hasNext()) {
			VWQueueElement elem = (VWQueueElement) queueQuery.next();
			String boundUser = elem.getBoundUserPx().getParticipantName();
			String caseFolder = elem.getFieldValue("F_CaseFolder").toString();
			String lockOwner = elem.getLockedUser();
			String wobNum = elem.getWorkObjectNumber().toString();
			String lockStatusDesc = "In Progress";
			String queueDate = null;

			String name = (elem.getSubject() == null ? "[undefined value]" : elem.getSubject());
			String stepName = elem.getStepName();
			int lockStatus = elem.getLockedStatus();
			if (VWQueueElement.LOCKED_BY_NONE != lockStatus) {
				lockStatusDesc = "Locked";
			}
			String workflowName = elem.getWorkflowName();
			queueDate = elem.getFieldValue("F_EnqueueTime").toString();
			String[] row = new String[] { 
					wobNum, 
					boundUser,
					caseFolder,
					name, 
					stepName, 
					lockStatusDesc, 
					queueDate, 
					workflowName, 
					lockOwner };

			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
			cnt++;
			if ((maxItems > 0) && (cnt > maxItems)) {
				break;
			}
		}
	}

	private void doTestMatches() {
		String raw = "UCM Foo Bar";
		System.out.println(raw.matches("UCM.*"));
	}

	private void doTestVWSystemConfiguration() throws Exception {
		VWSystemConfiguration sysConfig =  getPEConnection()
				.fetchSystemConfiguration();
		VWApplicationSpaceDefinition[] asDefs = sysConfig.getApplicationSpaceDefinitions();
		for (VWApplicationSpaceDefinition vwApplicationSpaceDefinition : asDefs) {
			//doListDef(vwApplicationSpaceDefinition);
			StringBuffer buf = new StringBuffer();
			vwApplicationSpaceDefinition.toXML(buf);
			String name = vwApplicationSpaceDefinition.getName();
			writeToFile(buf, name);
		}

	}

	private void writeToFile(StringBuffer buf, String name) {
		FileWriter fileWriter = null;
		File outDir = new File("C:\\data\\UCM\\as");
		try {
			File file = new File(outDir, name + ".xml");
			fileWriter = new FileWriter(file);
			fileWriter.write(buf.toString());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileWriter = null;
		}
	}

	private void doListDef(VWApplicationSpaceDefinition appSpaceDef) {
		System.out.println("======================================================");
		System.out.println(appSpaceDef.getName());
		System.out.println(appSpaceDef.getDescription());
		VWRoleDefinition[] roleDefs = appSpaceDef.getRoleDefinitions();
		if (roleDefs == null) {
			System.out.println("\t(no role defs)");
			return;
		}
		for (VWRoleDefinition roleDef : roleDefs) {
			roleDef.getName();
			System.out.println("\t" + roleDef.getName());
			VWWorkBasketDefinition[] workkBasketDefs = roleDef.getWorkBasketDefinitions();
			if (workkBasketDefs == null) {
				System.out.println("\t\t(no inbaskets");
				return;
			}
			for (VWWorkBasketDefinition workBasketDef : workkBasketDefs) {
				String wbName = workBasketDef.getName();
				String description = workBasketDef.getDescription();
				String filter = workBasketDef.getQueryFilterString();
				String qeueu = workBasketDef.getQueueName();
				
				System.out.println("\t\tWorkbasket:\t" + wbName);
				System.out.println("\t\t\tDescription:\t" + description);
				System.out.println("\t\t\tquery Filter:\t" + filter);
				System.out.println("\t\t\tqueue name:\t" + qeueu);
				System.out.println("\n");
			}
		}
	}

	/**
		 * 
		 */
	private void stTst() {
		String[] rawstr = new String[] { "[alpha|beta|gamma]", "[alpha||gamma]", "[alpha]", "[a]", "[|beta|gamma]",
				"[||gamma]", "[]" };
		int[] expected = { 3, 3, 1, 1, 3, 3, 0 };
		String delim = "\\|";

		for (int i = 0; i < expected.length; i++) {
			String[] result = new String[] {};
			String raw = rawstr[i];
			if (raw.startsWith("[") && raw.endsWith("]")) {
				if (raw.length() > 2) {
					String strippedArray = raw.substring(1, raw.length() - 1);
					result = strippedArray.split(delim);
				}
				if (expected[i] == result.length) {
					System.out.println("expected number for " + raw);
				} else {
					System.err.println("unexpected result for " + raw + " (expected " + expected[i] + " , actual: "
							+ result.length);
				}
			} else {
				System.err.println("did not find delimiters");
			}
		}

	}

	// /**
	// *
	// */
	private void doTstSetValues() throws Exception {
		Properties props = new PropertyUtil()
				.loadPropertiesFromFile(new File("C:/testdata/demo/scenario-01/tst.props"));
		dispatchStepElement("approve", "test", "last", props);
	}

	//
	// /**
	// * @param responseUri
	// * @param queueName
	// * @param wob
	// */
	public void dispatchStepElement(String responseUri, String queueName, String wobNum, Properties props)
			throws Exception {
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
				throw new IllegalStateException("step locked by " + lockOwner.getDisplayName());
			} else {
				lockedByCurrentUser = true;
			}
		}
		if (!lockedByCurrentUser) {
			stepElement.doLock(true);
		}

		if (props != null) {
			applyProperties(stepElement, props);
		}

		// if (decodedResponse != null) {
		// stepElement.setSelectedResponse(decodedResponse);
		// }
		//// stepElement.doDispatch();
		stepElement.doSave(true);
		StringBuffer msg = new StringBuffer();
		msg.append("saved work item from work queue: ").append(queueName).append(" : ").append(wobNum);
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
	private void applyProperties(VWStepElement stepElement, Properties props) throws Exception {
		StepElementEditUtil seEditUtil = new StepElementEditUtil(this.getShell(), stepElement);
		seEditUtil.setProperties(props);
	}

	//
	// /**
	// * @param stepElement
	// * @param props
	// */
	// private void applyProperties(VWStepElement stepElement, Properties props)
	// throws Exception {
	//
	// for (Iterator iter = props.keySet().iterator();iter.hasNext();) {
	// String key = iter.next().toString();
	// String value = props.getProperty(key);
	// applyPropertyToStep(stepElement, key, value);
	// }
	// }
	//
	// /**
	// * @param stepElement
	// * @param key
	// * @param value
	// */
	// private void applyPropertyToStep(VWStepElement stepElement, String key,
	// String value) throws Exception {
	// int fieldType = -1;
	// VWParameter vwParam = stepElement.getParameter(key);
	// if (vwParam == null) {
	// throw new IllegalArgumentException("No parameter with name " + key);
	// }
	// fieldType = vwParam.getFieldType();
	// switch(fieldType) {
	// case(VWFieldType.FIELD_TYPE_ATTACHMENT) :
	// applyAttachment(vwParam, value);
	// break;
	// case(VWFieldType.FIELD_TYPE_BOOLEAN) :
	// applyBoolean(vwParam, value);
	// break;
	// case(VWFieldType.FIELD_TYPE_FLOAT):
	// applyFloat(vwParam, value);
	// break;
	// case(VWFieldType.FIELD_TYPE_INT):
	// applyInt(vwParam, value);
	// break;
	// case (VWFieldType.FIELD_TYPE_PARTICIPANT) :
	// // todo
	// break;
	// case (VWFieldType.FIELD_TYPE_STRING):
	// applyString(vwParam, value);
	// break;
	// case (VWFieldType.FIELD_TYPE_TIME):
	// // todo
	// case(VWFieldType.FIELD_TYPE_XML):
	// // todo
	// break;
	// default :
	// // todo
	//
	// }
	// }
	//
	// /**
	// * @param vwParam
	// * @param value
	// */
	// private void applyFloat(VWParameter vwParam, String value) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// /**
	// * @param vwParam
	// * @param value
	// */
	// private void applyBoolean(VWParameter vwParam, String value) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// /**
	// * @param vwParam
	// * @param value
	// */
	// private void applyAttachment(VWParameter vwParam, String value) {
	// // TODO Auto-generated method stub
	//
	// }

	/**
	 * @param vwParam
	 * @param value
	 */
	private void applyString(VWParameter vwParam, String value) throws Exception {
		vwParam.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		StringParam fooArgs = new StringParam(FOO_ARG, "any params", StringParam.OPTIONAL);
		fooArgs.setMultiValued(true);
		CmdLineHandler cl = new VersionCmdLineHandler("v1",
				new HelpCmdLineHandler(CMD_DESC, CMD, CMD_DESC, new Parameter[] {}, new Parameter[] { fooArgs }));
		cl.setDieOnParseError(false);

		return cl;
	}

}
