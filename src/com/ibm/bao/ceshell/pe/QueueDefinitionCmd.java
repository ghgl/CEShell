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
import filenet.vw.api.VWIndexDefinition;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueDefinition;
import filenet.vw.api.VWWorkBasketColumnDefinition;
import filenet.vw.api.VWWorkBasketDefinition;
import filenet.vw.api.VWWorkBasketFilterDefinition;
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
			"\n\tpe.qdef <queue-name>\n" +
			"\nTo print just fields:\n\tpe.qdef -fields <queue-name>";
	
	// param names
	protected static final String 
		FIELDS_OPT = "fields",
		SUMMARY_OPT = "summary",
		QUEUE_NAME_ARG = "queue-name";
	

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam fieldsOpt = (BooleanParam) cl.getOption(FIELDS_OPT);
		BooleanParam summaryOpt = (BooleanParam) cl.getOption(SUMMARY_OPT);
		
		String queueName = cl.getArg(QUEUE_NAME_ARG).getValue().toString();
		Boolean fieldsDetails = false;
		Boolean summary = false;
		
		if (fieldsOpt.isSet()) {
			fieldsDetails = fieldsOpt.getValue();
		}
		
		if (summaryOpt.isSet()) {
			summary = summaryOpt.getValue();
		}
		return queueDefinition(queueName, fieldsDetails, summary);

	}

	/**
	 * @param queueName
	 */
	public boolean queueDefinition(String queueName, Boolean fieldsDetails, Boolean summary) throws Exception {
		VWQueue q = getPEConnection().getQueue(queueName);
		VWQueueDefinition qd = null;
		
		if (q == null) {
			throw new IllegalArgumentException("Queue with name " + queueName + "not found");
		}
		qd = q.fetchQueueDefinition();
		
		if (summary == Boolean.TRUE) {
			displaySummary(queueName, qd);
		} else if (fieldsDetails == Boolean.TRUE) {
			displayQueueFields(queueName, qd);
		} else {
			displayXmlResults(qd);
		} 
		return true;
	}

	private void displaySummary(String queueName, VWQueueDefinition qd) {
		displayQueueFields(queueName, qd);
		displayInbaskets(queueName, qd);
		displayIndexDefinitions(queueName, qd);
	}

	private void displayIndexDefinitions(String queueName, VWQueueDefinition qd) {
		getResponse().printOut("\n\nIndex Definitions for queue" + queueName);
		VWIndexDefinition[] indexDefs = qd.getIndexes();
		if (indexDefs == null) {
			getResponse().printOut("\t(No index definitions)");
			return;
		}
		
		for (VWIndexDefinition indexDef : indexDefs) {
			getResponse().printOut("\tIndex Name:\t" + indexDef.getName());
			getResponse().printOut("\t\tFields:");
			String[] fieldNames = indexDef.getAuthoredFieldNames();
			if (fieldNames == null) {
				getResponse().printOut("\t\t(No fields)");
			} else {
				for (String fieldName : fieldNames) {
					getResponse().printOut("\t\t\t" + fieldName);
				}
			}
		}
	}


	private void displayInbaskets(String queueName, VWQueueDefinition qd) {
		getResponse().printOut("\n\nWorkbasket Definitions for queue" + queueName);
		VWWorkBasketDefinition[] wbDefs = qd.getWorkBasketDefinitions();
		if (wbDefs == null) {
			getResponse().printOut("\t(none)");
			return;
		}
		
		for (VWWorkBasketDefinition wbDef : wbDefs) {
			doDisplayWorkbasket(queueName, wbDef);
		}
	}

	private void doDisplayWorkbasket(String queueName, VWWorkBasketDefinition wbDef) {
		getResponse().printOut("\tWorkbasket" + queueName + "." + wbDef.getName());
		getResponse().printOut("\t\tQueryFilter: " + wbDef.getQueryFilterString());
		getResponse().printOut("\t\tCol Defs:");
		VWWorkBasketColumnDefinition[] wbColDefs = wbDef.getWorkBasketColumnDefinitions();
		if (wbColDefs == null) {
			getResponse().printOut("\t(no columns");
			return;
		}
		
		ColDef[] cols = {
				new ColDef("Prompt", 40, StringUtil.ALIGN_LEFT),
				new ColDef("Index name", 20, StringUtil.ALIGN_LEFT),
				new ColDef("Sortable", 5, StringUtil.ALIGN_RIGHT)
		};
		getResponse().printOut(StringUtil.formatHeader(cols, " "));
		SortedSet<VWWorkBasketColumnDefinition> sortedWBDefs = new TreeSet<VWWorkBasketColumnDefinition>( new Comparator<VWWorkBasketColumnDefinition>() {

			public int compare(VWWorkBasketColumnDefinition lhs, VWWorkBasketColumnDefinition rhs) {
				return (lhs.getPrompt().compareTo(rhs.getPrompt()));
			}
		});
		
		for (VWWorkBasketColumnDefinition vwWorkBasketColumnDefinition : wbColDefs) {
			sortedWBDefs.add(vwWorkBasketColumnDefinition);
		}
		
		for (VWWorkBasketColumnDefinition vwWorkBasketColumnDefinition : sortedWBDefs) {
			String[] fields = new String[] {
				vwWorkBasketColumnDefinition.getPrompt(),
				vwWorkBasketColumnDefinition.getIndexName(),
				new Boolean(vwWorkBasketColumnDefinition.isSortable()).toString()
			};
			getResponse().printOut(StringUtil.formatRow(cols, fields, " "));
		}
		
		/** show filter definitions for inbasket **/
		getResponse().printOut("\tFilter definitions for inbasket "  + queueName + "." + wbDef.getName());
		VWWorkBasketFilterDefinition[] filterDefs = wbDef.getWorkBasketFilterDefinitions();
		if (filterDefs == null) {
			getResponse().printOut("\t\t(no Filter definitions)");
		}
		
		ColDef[] filterCols = {
				new ColDef("name", 5, StringUtil.ALIGN_LEFT),
				new ColDef("Description", 20, StringUtil.ALIGN_LEFT),
				new ColDef("Prompt", 30, StringUtil.ALIGN_LEFT),
				new ColDef("fieldName", 20, StringUtil.ALIGN_LEFT),
				new ColDef("type", 8, StringUtil.ALIGN_LEFT),
				new ColDef("oper", 5, StringUtil.ALIGN_RIGHT)
		};
		getResponse().printOut(StringUtil.formatHeader(filterCols, " "));
		
		
		for (VWWorkBasketFilterDefinition filterDef : filterDefs) {
			String fieldName = "null";
			{
				VWExposedFieldDefinition efd = filterDef.getSearchField();
				if (efd != null) {
					String efdname = efd.getName();
					if (efdname != null) {
						fieldName = efdname;
					}
				}
			}
			
			
			String name = filterDef.getName();
			String desc = filterDef.getDescription();
			String prompt = filterDef.getPrompt();
			//String type = "" + filterDef.getType();
			String type = VWFieldType.getLocalizedString(filterDef.getType());
			String operator = "" + filterDef.getOperator();
			
			String[] filterData = {
					name,
					desc,
					prompt,
					fieldName,
					type,
					operator
			};
			getResponse().printOut(StringUtil.formatRow(filterCols, filterData, "."));
		}
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
				new ColDef("Type", 8, StringUtil.ALIGN_LEFT),
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
		BooleanParam summaryOpt = null;
		StringParam queueNameArg = null;
		
		
		// options
		fieldsOpt = new BooleanParam(FIELDS_OPT, "Display field definitions");
		fieldsOpt.setOptional(BooleanParam.OPTIONAL);
		
		summaryOpt = new BooleanParam(SUMMARY_OPT, "Queus Summary");
		summaryOpt.setOptional(BooleanParam.OPTIONAL);
		
		// cmd args
		queueNameArg = new StringParam(QUEUE_NAME_ARG, 
				"queue to describe",
				StringParam.REQUIRED);
		queueNameArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { fieldsOpt, summaryOpt }, 
					new Parameter[] { queueNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
