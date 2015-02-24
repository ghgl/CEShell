/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.CEShell;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.PropertyComparer;
import com.ibm.bao.ceshell.util.StringUtil;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFieldType;
import filenet.vw.api.VWModeType;
import filenet.vw.api.VWParameter;
import filenet.vw.api.VWParticipant;
import filenet.vw.api.VWStepElement;

/**
 *  StepEditEditUtil
 *
 * @author regier
 * @date   Nov 12, 2011
 */
public class StepElementEditUtil {
	public static final String ARRAY_DEMILM_PATTERN = "\\|",
			ARRAY_DEMIM = "|";
			
	protected VWStepElement	 stepElement;
	protected CEShell ceShell;

	StepElementEditUtil() {
		
	}
	public StepElementEditUtil(CEShell ceShell, VWStepElement stepElement) {
		this.ceShell = ceShell;
		this.stepElement = stepElement;
	}

	public void setProperties(java.util.Properties props) throws Exception {
		Iterator<?> iter = props.keySet().iterator();
		while (iter.hasNext()) {
			String propName = iter.next().toString();
			String propValue = props.getProperty(propName);
			setProp(propName, propValue);
		}
	}
	
	public String getWob() throws Exception {
		return stepElement.getWorkObjectNumber();
	}
	
	public void doLock() throws Exception {
		boolean lockedByCurrentUser = false;

		VWParticipant lockOwner = stepElement.getLockedUserPx();
		if (lockOwner != null) {
			VWParticipant currentUser = this.ceShell.getPEConnection().fetchCurrentUserInfo().getNamePx();
						
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
	}
	
	public void doDispatch(String selectedResponse) throws Exception {
		try {
			if (selectedResponse != null) {
				stepElement.setSelectedResponse(selectedResponse);
			}
			stepElement.doDispatch();
		} catch (Exception e) {
			try {
				stepElement.doAbort();
			} catch (Exception e2) {
				// no-op
			}
			throw e; // re-throw original exception
		}
	}

	public void setProp(String propName, String propValue) throws Exception {
		int fieldType = -1;
		VWParameter vwParam = stepElement.getParameter(propName);
		boolean readOnly = false;
		if (vwParam == null) {
			throw new IllegalArgumentException("No parameter with name " + propName);
		}
		readOnly = (vwParam.getMode() == VWModeType.MODE_TYPE_IN);
		if (readOnly) {
			throw new IllegalStateException("Can not set a value of a read-only property");
		}
		fieldType = vwParam.getFieldType();
		switch(fieldType) {
		case VWFieldType.FIELD_TYPE_ATTACHMENT :
			applyAttachment(vwParam, propValue);
			break;
		case VWFieldType.FIELD_TYPE_BOOLEAN :
			applyBooleanValue(stepElement, vwParam, propName, propValue);
			break;
		case VWFieldType.FIELD_TYPE_FLOAT:
			applyFloatValue(stepElement, vwParam, propName, propValue);
			break;
		case VWFieldType.FIELD_TYPE_INT:
			applyIntegerValue(stepElement, vwParam, propName, propValue);
			break;
		case VWFieldType.FIELD_TYPE_PARTICIPANT :
			applyParticipantValue(vwParam, propValue);
			break;
		case VWFieldType.FIELD_TYPE_STRING:
			applyStringValue(stepElement, vwParam, propName, propValue);
			break;
		case VWFieldType.FIELD_TYPE_TIME:
			applyTimeValue(stepElement, vwParam, propName, propValue);
			break;
		case VWFieldType.FIELD_TYPE_XML:
			// todo
			throw new UnsupportedOperationException("TODO");
		default :
			throw new IllegalArgumentException("Unexpected parameter type for " + 
					vwParam.getName() + ": " + fieldType);
		
		}
	}
	
	public void displayStepElemProps(String sourceName)
			throws Exception {		
		String[] responses = null; 
		SortedSet<VWParameter> paramsSet = null;
		VWParameter[] params = null;
		java.util.Properties props = stepElementToProps();
		ColDef[] defs = new ColDef[] {
				new ColDef("Name", 20, StringUtil.ALIGN_LEFT),
				new ColDef("Type", 10, StringUtil.ALIGN_LEFT),
				new ColDef("Mode", 10, StringUtil.ALIGN_LEFT),
				new ColDef("Value", 40, StringUtil.ALIGN_LEFT)
			};
		this.ceShell.getResponse().printOut("Properties for:");
		this.ceShell.getResponse().printOut("\twob " + sourceName);
		this.ceShell.getResponse().printOut(StringUtil.formatHeader(defs, " "));
		
		params = stepElement.getParameters(VWFieldType.ALL_FIELD_TYPES, 
				VWStepElement.FIELD_USER_AND_SYSTEM_DEFINED);
		paramsSet = createOrderedParams(params);
		for (VWParameter p : paramsSet) {
			String name = p.getName();
			String type = VWFieldType.getLocalizedString(p.getFieldType());
			String mode = VWModeType.getLocalizedString(p.getMode());
			String value = props.getProperty(name);
			
			if (p.getFieldType() ==  VWFieldType.FIELD_TYPE_ATTACHMENT) {
				AttachmentInfo attachmentInfo  = fetchAttachmentInfo(value);
				value = attachmentInfo.getAttachmentValue();
			}
			String[] row = new String[] {
					name,
					type, 
					mode,
					value
			};
		
			this.ceShell.getResponse().printOut(StringUtil.formatRow(defs, row, " "));
			
		}
		
		responses = stepElement.getStepResponses();
		this.ceShell.getResponse().printOut("-------- Responses -----------------------");
		if (responses == null) {
			this.ceShell.getResponse().printOut("\t(no responses");
		} else {
			this.ceShell.getResponse().printOut("\nResponses:");
			for (int i = 0; i < responses.length; i++) {
				this.ceShell.getResponse().printOut("\t" + responses[i]);
			}
		}
	}
	
	/**
	 * @param value
	 * @return
	 */
	private AttachmentInfo fetchAttachmentInfo(String rawValue) {
		
		AttachmentInfo attachmentInfo = new AttachmentInfo(rawValue);
		updateAttachmentInfoWithDoc(attachmentInfo);
		
		return attachmentInfo;
	}
	
	/**
	 * @param attachmentInfo
	 */
	private void updateAttachmentInfoWithDoc(AttachmentInfo attachmentInfo) {
		// only fetch the doc id if the object store associated with the attachment matches 
		// the current object store.
		// TODO: Add support for attachments from different object stores.
		try {
			String objectStoreName = attachmentInfo.getObjectStoreName();
			if (this.ceShell.getOSName().equals(
					objectStoreName)) {
				VersionSeries vs = Factory.VersionSeries.fetchInstance(
						this.ceShell.getObjectStore(), 
						new Id(attachmentInfo.getAttachemntGuid()), 
						null);
				Document doc= (Document) vs.get_CurrentVersion();
				Id docId = doc.get_Id();
				attachmentInfo.setDocGuid(docId.toString());
			} else {
				attachmentInfo.setDocGuid("(TODO: in separate object store)");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			attachmentInfo.setDocGuid("Unknown");
		}
	}
	
	/**
	 * @param stepElem
	 * @return
	 */
	public java.util.Properties stepElementToProps() throws Exception {
		VWParameter[] params = stepElement.getParameters(VWFieldType.ALL_FIELD_TYPES, 
				VWStepElement.FIELD_USER_AND_SYSTEM_DEFINED);
		java.util.Properties props = new java.util.Properties();
		for (VWParameter param : params) {
			String name = param.getName();
			String value = "";
			Object paramValue = param.getValue();
			int fieldType = param.getFieldType();
			switch(fieldType) {
			case VWFieldType.FIELD_TYPE_TIME:
				value = storeDateValues(param.isArray(), paramValue);
				break;
			default :
				value = storePropVales(param.isArray(), paramValue);
			}

			props.put(name, value);
		}
		return props;
	}
	
	protected String storePropVales(Boolean isArray, Object paramValue) {
		String result = "";
		if (isArray) {
			Object[] propValues = (Object[]) paramValue;
			int num = propValues.length;
			if (num == 0) {
				result = "[]";
			} else {
				StringBuffer buf = new StringBuffer();
				buf.append("[");
				int pos = 0;
				for (Object propValue : propValues) {
					buf.append(propValue.toString());
					if (pos++ < num - 1) {
						buf.append(ARRAY_DEMIM);
					}
				}
				buf.append("]");
				result = buf.toString();
			}
		} else {
			if (paramValue != null) {
				result = paramValue.toString();
			}
		}
		return result;
	}

	protected String storeDateValues(Boolean isArray, Object paramValue) throws VWException {
		String result = "";
		if (isArray) {
			Date[] dateValues = (Date[]) paramValue;
			int num = dateValues.length;
			if (num == 0) {
				result = "[]";
			} else {
				StringBuffer buf = new StringBuffer();
				buf.append("[");
				int pos = 0;
				for (Date nextDate : dateValues) {
					buf.append(StringUtil.fmtDate(nextDate, StringUtil.DATE_FMT_PE));
					if (pos++ < num - 1) {
						buf.append(ARRAY_DEMIM);
					}
				}
				buf.append("]");
				result = buf.toString();
			}
		} else {
			if (paramValue != null) {
				java.util.Date d = (java.util.Date) paramValue;
				result = StringUtil.fmtDate(d, StringUtil.DATE_FMT_PE);
			}
		}
		return result;
	}
	
	/**
	 * @param stepElem
	 * @param compareExpected
	 */
	public PropertyComparer compareStepElementPropsToExpected(
			java.util.Properties expected) throws Exception {
		java.util.Properties actual = stepElementToProps();
		PropertyComparer propComparer = new PropertyComparer(expected, actual);
		return propComparer;
	}
	
	/**
	 * @param params
	 * @return
	 */
	private SortedSet<VWParameter> createOrderedParams(VWParameter[] params) {
		SortedSet<VWParameter> paramsSet = new TreeSet<VWParameter>(new Comparator<VWParameter>() {

			public int compare(VWParameter lhs, VWParameter rhs) {
				return lhs.getName().toUpperCase().compareTo(
						rhs.getName().toUpperCase());
			}	
			
		});
		for (int i = 0; i < params.length; i++) {
			paramsSet.add(params[i]);
		}
		return paramsSet;
	}
	
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
	
	/**
	 * @param stepElement2
	 * @param vwParam
	 * @param propName
	 * @param propValue
	 */
	private void applyTimeValue(
			VWStepElement stepElem,
			VWParameter vwParam, 
			String propName, 
			String propValue) throws Exception {
		Date timeValue = StringUtil.parseDate(propValue);
		if (vwParam.isArray()) {
			Date[] values = new Date[] { timeValue };
			stepElem.setParameterValue(propName, values, true);
		} else {
			stepElem.setParameterValue(propName, timeValue, true);
		}
	}

	/**
	 * @param stepElem
	 * @param vwParam
	 * @param propName
	 * @param propValue
	 */
	private void applyBooleanValue(
			VWStepElement stepElem,
			VWParameter vwParam, 
			String propName, 
			String propValue) throws Exception {
		Boolean boolValue = StringUtil.parseBoolean(propValue);
		if (vwParam.isArray()) {
			Boolean[] values = new Boolean[] { boolValue };
			stepElem.setParameterValue(propName, values, true);
		} else {
			stepElem.setParameterValue(propName, boolValue, true);
		}
	}

	/**
	 * @param stepElem
	 * @param vwParam
	 * @param propName
	 * @param propValue
	 */
	private void applyFloatValue(
			VWStepElement stepElem,
			VWParameter vwParam, 
			String propName, 
			String propValue) throws Exception {
		Float floatValue = Float.parseFloat(propValue);
		if (vwParam.isArray()) {
			Float[] values = new Float[] { floatValue };
			stepElem.setParameterValue(propName, values, true);
		} else {
			stepElem.setParameterValue(propName, floatValue, true);
		}
	}

	/**
	 * @param stepElem
	 * @param vwParam
	 * @param propName
	 * @param propValue
	 */
	private void applyIntegerValue(
			VWStepElement stepElem,
			VWParameter vwParam, String propName, String propValue) throws Exception {
		Integer intValue = Integer.parseInt(propValue);
		if (vwParam.isArray()) {
			Integer[] values = new Integer[] { intValue };
			stepElem.setParameterValue(propName, values, true);
		} else {
			stepElem.setParameterValue(propName, intValue, true);
		}
		
	}

	private void applyStringValue(
			VWStepElement stepElem, 
			VWParameter vwParam, 
			String propName, 
			String propValue) throws Exception {
		if (vwParam.isArray()) {
			String[] values = parseArrayValues(propValue);
			stepElem.setParameterValue(propName, values, true);
		} else {
			stepElem.setParameterValue(propName, propValue, true);
		}
	}

	/**
	 * @param vwParam
	 * @param propValue
	 */
	private void applyParticipantValue(VWParameter vwParam, String propValue) {
		throw new UnsupportedOperationException("TODO");
		
	}

	/**
	 * @param vwParam
	 * @param propValue
	 */
	private void applyAttachment(VWParameter vwParam, String propValue) {
		throw new UnsupportedOperationException("TODO");
		
	}
	
	private String[] parseArrayValues(String raw) {
		String[] result = new String[] {};
		if (raw == null) {
			return result;
		}
		if (raw.startsWith("[") && raw.endsWith("]")) {
			if (raw.length() > 2) {
				String strippedArray = raw.substring(1, raw.length() - 1);
				result = strippedArray.split(ARRAY_DEMILM_PATTERN);
			}
		} else {
			result = new String[] {raw};
		}
		
		return result;
	}

	
}
