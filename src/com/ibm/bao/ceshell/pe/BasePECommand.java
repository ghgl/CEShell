/**
 * 
 */
package com.ibm.bao.ceshell.pe;


import com.ibm.bao.ceshell.BaseCommand;

import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObjectNumber;

/**
 *  BasePECmd
 *
 * @author GaryRegier
 * @date   Jun 26, 2011
 */
public abstract class BasePECommand extends BaseCommand {
	
	public static final String 
			LAST = "last",
			FIRST = "first";
	

	protected VWSession getPEConnection() throws Exception {
		return getShell().getPEConnection();
	}
	
	protected VWStepElement fetchStepElement(String queueName, String wobNum) 
			throws Exception {
		VWStepElement stepElement = null;
		
		if (wobNum.startsWith("f")) {
			stepElement = fetchStepElementByPos(queueName, true);
		} else if (wobNum.startsWith("l")) {
			stepElement = fetchStepElementByPos(queueName, false);
		} else {
			stepElement = fetchStepElementByWob(queueName, wobNum);
		}
		return stepElement;
	}
	
	protected VWStepElement fetchStepElementByPos(String queueName, boolean returnFirst)
			throws Exception {
		VWStepElement stepElem = null;
		VWQueueQuery queueQuery = null;
		VWQueue queue = null;
		String queryIndex = "F_Fifo";
		int queryFlags = VWQueue.QUERY_MIN_VALUES_INCLUSIVE + VWQueue.QUERY_MAX_VALUES_INCLUSIVE + VWQueue.QUERY_READ_LOCKED;
		int queryType = VWFetchType.FETCH_TYPE_STEP_ELEMENT;
	
		queue = getPEConnection().getQueue(queueName);
		
		queueQuery = queue.createQuery(
				queryIndex, 
				new VWWorkObjectNumber[] { }, 
				new VWWorkObjectNumber[] { }, 
				queryFlags, 
				null, 
				null, 
				queryType);
		if (queueQuery.hasNext()) {
			stepElem = (VWStepElement) queueQuery.next();
		}
		if (returnFirst) {
			return stepElem;
		}
		while (queueQuery.hasNext()) {
			stepElem = (VWStepElement) queueQuery.next();
		}
		return stepElem;
	}
	
	protected VWStepElement fetchStepElementByWob(String queueName, String wobNum)
			throws Exception {
		VWStepElement stepElem = null;
		VWQueueQuery queueQuery = null;
		VWQueue queue = null;
		VWWorkObjectNumber wob = new VWWorkObjectNumber(wobNum);
		String queryIndex = "F_WobNum";
		int queryFlags = VWQueue.QUERY_MIN_VALUES_INCLUSIVE + VWQueue.QUERY_MAX_VALUES_INCLUSIVE;
		int queryType = VWFetchType.FETCH_TYPE_STEP_ELEMENT;
	
		queue = getPEConnection().getQueue(queueName);
		
		queueQuery = queue.createQuery(
				queryIndex, 
				new VWWorkObjectNumber[] {wob}, 
				new VWWorkObjectNumber[] {wob}, 
				queryFlags, 
				null, 
				null, 
				queryType);
		stepElem = (VWStepElement) queueQuery.next();
		return stepElem;
	}

//	protected void displayStepElemProps(VWStepElement stepElem, String sourceName)
//			throws Exception {		
//		String[] responses = null; 
//		SortedSet<VWParameter> paramsSet = null;
//		VWParameter[] params = null;
//		ColDef[] defs = new ColDef[] {
//				new ColDef("Name", 20, StringUtil.ALIGN_LEFT),
//				new ColDef("Type", 10, StringUtil.ALIGN_LEFT),
//				new ColDef("Mode", 10, StringUtil.ALIGN_LEFT),
//				new ColDef("Value", 40, StringUtil.ALIGN_LEFT)
//			};
//		getResponse().printOut("Properties for:");
//		getResponse().printOut("\twob " + sourceName);
//		
//		getResponse().printOut(StringUtil.formatHeader(defs, " "));
//		
//		params = stepElem.getParameters(VWFieldType.ALL_FIELD_TYPES, 
//				VWStepElement.FIELD_USER_AND_SYSTEM_DEFINED);
//		paramsSet = createOrderedParams(params);
//		for (VWParameter p : paramsSet) {
//			String name = p.getName();
//			String type = VWFieldType.getLocalizedString(p.getFieldType());
//			String mode = VWModeType.getLocalizedString(p.getMode());
//			String value = readValue(p);
//			
//			String[] row = new String[] {
//					name,
//					type, 
//					mode,
//					value
//			};
//		
//			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
//		}
//		
//		responses = stepElem.getStepResponses();
//		getResponse().printOut("-------- Responses -----------------------");
//		if (responses == null) {
//			getResponse().printOut("\t(no responses");
//		} else {
//			getResponse().printOut("\nResponses:");
//			for (int i = 0; i < responses.length; i++) {
//				getResponse().printOut("\t" + responses[i]);
//			}
//		}
//	}

//	/**
//	 * @param p
//	 * @return
//	 */
//	private String readValue(VWParameter p) throws Exception {
//		if (p.getValue() == null) {
//			return "";
//		}
//		if (VWFieldType.FIELD_TYPE_TIME == p.getFieldType()) {
//			return StringUtil.fmtDate((java.util.Date) p.getValue(), StringUtil.DATE_FMT_PE);
//		} else {
//			return p.getStringValue();
//		}
//	}

//	/**
//	 * @param params
//	 * @return
//	 */
//	private SortedSet<VWParameter> createOrderedParams(VWParameter[] params) {
//		SortedSet<VWParameter> paramsSet = new TreeSet<VWParameter>(new Comparator<VWParameter>() {
//
//			public int compare(VWParameter lhs, VWParameter rhs) {
//				return lhs.getName().toUpperCase().compareTo(
//						rhs.getName().toUpperCase());
//			}	
//			
//		});
//		for (int i = 0; i < params.length; i++) {
//			paramsSet.add(params[i]);
//		}
//		return paramsSet;
//	}
}
