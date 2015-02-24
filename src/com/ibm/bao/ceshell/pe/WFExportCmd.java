/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import java.io.File;
import java.io.FileWriter;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import filenet.vw.api.VWWorkflowDefinition;

/**
 *  FectchWFDefinitionCmd
 *
 * @author regier
 * @date   Nov 5, 2011
 */
public class WFExportCmd extends BasePECommand {

	// param names
	private static final String 
			WORKFLOW_NAME_ARG = "workflow-name",	
			EXP_FILE_OPT = "file";
	
	public static final String 
		CMD = "pe.wfexport", 
		CMD_DESC = "Export a workflow definition as XML",
		HELP_TEXT = "Usage:" +
			"\npe.wfexport <workflow-name> -- writes workflow to STDOUT" +
			"\npe.wfexport -f <exp-file> <workflow-name> -- writes workflow to <exp-file>";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		FileParam expFileParam = (FileParam) cl.getOption(EXP_FILE_OPT);
		String workflowName = cl.getArg(WORKFLOW_NAME_ARG).getValue().toString();
		File exportFile = null;
		
		if (expFileParam.isSet()) {
			exportFile = expFileParam.getValue();
		}
		return wfexport(workflowName, exportFile);
	}
	/**
	 * @param workflowName
	 */
	public boolean wfexport(String workflowName, File exportFile) {
		String decodedName = this.decodePath(workflowName);
		String def = null;
		try {
			VWWorkflowDefinition wfDef = getPEConnection()
					.fetchWorkflowDefinition(-1, decodedName, false);
			def = wfDef.AsXMLString();
		} catch (Exception e) {
			getResponse().printErr("Failed to fetch workflow definition " 
					+ decodedName);
			return false;
		}
		if (exportFile != null) {
			try {
				writeDefToFile(exportFile, def);
				getResponse().printOut("Worfkow definition  "+ decodedName + 
						" written to file " + exportFile.toString());
			} catch (Exception e) {
				getResponse().printErr("Failed to write workflow definition to file " +  
						decodedName);
				return false;
			}
			
		} else {
			getResponse().printOut("Workflow Definition " + decodedName + 
					"\n" + def);
		}
		return true;
	}
		
	/**
	 * @param exportFile
	 * @param def
	 */
	private void writeDefToFile(File exportFile, String def) throws Exception {
		FileWriter writer = new FileWriter(exportFile);
		writer.write(def);
		writer.close();
		writer = null;
		
	}
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam workflowNameArg = null;
		FileParam expFileOpt = null;
		
		// options
		expFileOpt = new FileParam(EXP_FILE_OPT, 
				"Ouput file",
				FileParam.OPTIONAL);
		expFileOpt.setMultiValued(false);
		expFileOpt.setOptionLabel("<exp-file>");
		
		// cmd args
		workflowNameArg = new StringParam(
				WORKFLOW_NAME_ARG,
				"workflow launch step to examine",
				StringParam.REQUIRED);
		


		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { expFileOpt }, 
					new Parameter[] {workflowNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}


}
