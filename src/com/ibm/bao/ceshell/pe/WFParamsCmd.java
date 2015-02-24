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

import filenet.vw.api.VWFieldDefinition;
import filenet.vw.api.VWFieldType;
import filenet.vw.api.VWWorkflowDefinition;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  WFParamsCmd
 *
 * @author regier
 * @date   Nov 5, 2011
 */
public class WFParamsCmd extends BasePECommand {

	// param names
	private static final String 
		WORKFLOW_NAME_ARG = "workflow-name";
	
	public static final String 
		CMD = "pe.wfparams", 
		CMD_DESC = "Fecth a workflow definition as XML",
		HELP_TEXT = "pe.wfparams:" +
			"\npe.wfparams <workflow-name>";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String workflowName = cl.getArg(WORKFLOW_NAME_ARG).getValue().toString();
		
		return workflowParams(workflowName);
	}
	/**
	 * @param workflowName
	 */
	public boolean workflowParams(String workflowName) {
		String decodedName = this.decodePath(workflowName);
		try {
			VWWorkflowDefinition wfDef = getPEConnection()
					.fetchWorkflowDefinition(-1, decodedName, false);
			
			
			VWFieldDefinition[] fieldDefs = wfDef.getFields();
			getResponse().printOut("Propertis for " + decodedName);
			displayWorkflowParams(fieldDefs);
			
		} catch (Exception e) {
			getResponse().printErr("Failed to fetch workflow definition " 
					+ decodedName);
			return false;
		}
		return true;
	}
	
	/**
	 * @param fieldDefs
	 */
	private void displayWorkflowParams(VWFieldDefinition[] fieldDefs) throws Exception {
		SortedSet<VWFieldDefinition> wfFields = new TreeSet<VWFieldDefinition>(new Comparator<VWFieldDefinition>() {

			public int compare(VWFieldDefinition lhs,
					VWFieldDefinition rhs) {
				return lhs.getName().toLowerCase().compareTo(
						rhs.getName().toLowerCase());
			}

			
		}); 
		
		ColDef[] defs = new ColDef[] {
				new ColDef("Name", 20, StringUtil.ALIGN_LEFT),
				new ColDef("Type", 10, StringUtil.ALIGN_LEFT),
				new ColDef("Description", 40, StringUtil.ALIGN_LEFT),
				new ColDef("Value", 20, StringUtil.ALIGN_LEFT)
			};
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		
		// add to sorted set
		for (VWFieldDefinition fieldDef : fieldDefs) {
			wfFields.add(fieldDef);
		}
		
		// go through each field in the sorted set
		for (VWFieldDefinition fieldDef : wfFields) {
			String name = fieldDef.getName();
			String type = VWFieldType.getLocalizedString(fieldDef.getFieldType());
			String description = fieldDef.getDescription();
			String value = fieldDef.getStringValue();
			
			String[] row = new String[] {
					name,
					type, 
					description,
					value
			};
			
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
		}
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
