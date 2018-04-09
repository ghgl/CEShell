/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

import filenet.vw.api.VWException;
import filenet.vw.api.VWExposedFieldDefinition;
import filenet.vw.api.VWFieldType;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueDefinition;
import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  QueueDefinitionCmd
 *
 * @author GaryRegier
 * @date   Jun 30, 2011
 */
public class QueueDefinitionCmd extends BasePECommand {
	
	public static final String 
		CMD = "pe.qdef", 
		CMD_DESC = "Display the queue definition",
		HELP_TEXT = "Usage:" +
			"\npe.qdef <queue-name>";
	
	// param names
	protected static final String 
		FIELDS_OPT = "fields",
		QUEUE_NAME_ARG = "queue-name";
	

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam fieldsOpt = (BooleanParam) cl.getOption(FIELDS_OPT);
		String queueName = cl.getArg(QUEUE_NAME_ARG).getValue().toString();
		Boolean fieldsDetails = false;
		
		if (fieldsOpt.isSet()) {
			fieldsDetails = fieldsOpt.getValue();
		}
		return queueDefinition(queueName, fieldsDetails);

	}

	/**
	 * @param queueName
	 */
	public boolean queueDefinition(String queueName, Boolean fieldsDetails) throws Exception {
		VWQueue q = getPEConnection().getQueue(queueName);
		VWQueueDefinition qd = null;
		
		if (q == null) {
			throw new IllegalArgumentException("Queue with name " + queueName + "not found");
		}
		qd = q.fetchQueueDefinition();
		
		if (fieldsDetails == Boolean.FALSE) {
			displayXmlResults(qd);
		} else {
			displayQueueFields(queueName, qd);
		}
		return true;
	}

	private void displayQueueFields(String queueName, VWQueueDefinition qd) {
		VWExposedFieldDefinition[] fieldDefs = qd.getFields();
		SortedSet<VWExposedFieldDefinition> sysFields = new TreeSet<VWExposedFieldDefinition>(new Comparator<VWExposedFieldDefinition>() {

			public int compare(VWExposedFieldDefinition o1, VWExposedFieldDefinition o2) {
				return (o1.getName().compareTo(o2.getName()));
			}
			
		});
		
		SortedSet<VWExposedFieldDefinition> custFields = new TreeSet<VWExposedFieldDefinition>(new Comparator<VWExposedFieldDefinition>() {

			public int compare(VWExposedFieldDefinition o1, VWExposedFieldDefinition o2) {
				return (o1.getName().compareTo(o2.getName()));
			}
			
		});
		
		for (VWExposedFieldDefinition fieldDef : fieldDefs) {
			if (fieldDef.isSystemField()) {
				sysFields.add(fieldDef);
			} else {
				custFields.add(fieldDef);
			}
		}
		
		getResponse().printOut("Fields for queue " + queueName);
		ColDef[] cols = {
				new ColDef("Man", 3, StringUtil.ALIGN_LEFT),
				new ColDef("Name", 30, StringUtil.ALIGN_LEFT),
				new ColDef("Type", 8, StringUtil.ALIGN_RIGHT),
				new ColDef("Len", 5, StringUtil.ALIGN_RIGHT),
				new ColDef("SourceName", 25, StringUtil.ALIGN_LEFT),
				new ColDef("ValueProvider", 35, StringUtil.ALIGN_LEFT)
		};
		
		getResponse().printOut("\nSystem fields:");
		getResponse().printOut(StringUtil.formatHeader(cols, " "));
		for (VWExposedFieldDefinition fieldDef : sysFields) {
			getResponse().printOut(formatRow(cols, fieldDef));
		}
		
		getResponse().printOut("\nCustomm fields:");
		getResponse().printOut(StringUtil.formatHeader(cols, " "));
		for (VWExposedFieldDefinition fieldDef : custFields) {
			getResponse().printOut(formatRow(cols, fieldDef));
		}
	}

	private String formatRow(ColDef[] cols, VWExposedFieldDefinition fieldDef) {
		int flen = fieldDef.getLength();
		String lenStr = (flen == 0) ? "" : "" + flen;
		boolean mandatory = fieldDef.isMandatorySystemField();
		String mandatoryStr = (mandatory == true) ? "T" : "F";
		String[] rowData = new String[] {
				mandatoryStr,
				fieldDef.getName(),
				VWFieldType.getLocalizedString(fieldDef.getFieldType()),
				lenStr,
				fieldDef.getSourceName(),
				fieldDef.getValueProvider()
		};
		String row = StringUtil.formatRow(cols, rowData, " ");
		return row;
	}


	@SuppressWarnings("deprecation")
	private void displayXmlResults(VWQueueDefinition qd) throws VWException {
		StringBuffer buf = new StringBuffer();
		qd.toXML(buf);
		getResponse().printOut(buf.toString());
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam fieldsOpt = null;
		StringParam queueNameArg = null;
		
		
		// options
		fieldsOpt = new BooleanParam(FIELDS_OPT, "Display field definitions");
		fieldsOpt.setOptional(BooleanParam.OPTIONAL);
		
		// cmd args
		queueNameArg = new StringParam(QUEUE_NAME_ARG, 
				"queue to describe",
				StringParam.REQUIRED);
		queueNameArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { fieldsOpt }, 
					new Parameter[] { queueNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
