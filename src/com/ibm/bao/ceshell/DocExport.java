/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.List;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  DocExport
 *
 * @author regier
 * @date   Sep 17, 2013
 */
public class DocExport extends BaseCommand {
	
	private static final String 
	CMD = "docexpot", 
	CMD_DESC = "export documents based on a query criteria",
	HELP_TEXT = CMD_DESC +
		"\nUsage:" +
		"\n\tdocexport -outdir <out-dir> -docclass <doc-class>  <where-clause> " +
		"\ndocexport -outdir e:/temp -docclass BHDC_ECC_Billing Source = 'ZROD'" +
		"\n\texport foo.txt to e:/temp/mydoc.txt. If e:/temp/mydoc.txt " +
		"\n\texists, then it is overwritten";

	
	public static final String
		OUTPUT_DIR_OPT = "outdir",
		DOCCLASS_NAME_OPT = "docclass",
		WHERE_CONDITION = "where-cond";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam whereArgParam = (StringParam) cl.getArg(WHERE_CONDITION);
		FileParam outputFileOpt = (FileParam) cl.getOption(OUTPUT_DIR_OPT);
		File outputFile = null;
		StringParam docClassOpt = (StringParam) cl.getOption(DOCCLASS_NAME_OPT);
		List<String> whereArgs = whereArgParam.getValues();
		String query = null;
		
		if (outputFileOpt.isSet()) {
			outputFile = outputFileOpt.getValue();
		}
		
//		query = createQuery(queryArgs);
		
		return false;
	}
	
	public boolean docExport(File outDir, String docClass, String whereClause) throws Exception {
		
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		FileParam localDirOpt = null;
		StringParam docClassOpt = null;
		StringParam whereCondArgs = null;

		// params
		localDirOpt = new FileParam(OUTPUT_DIR_OPT,
				"output directory (system temp dir by default)",
				FileParam.IS_DIR & FileParam.IS_WRITEABLE,
				FileParam.OPTIONAL);
		localDirOpt.setOptionLabel("<output dir>");
		
		docClassOpt = new StringParam(DOCCLASS_NAME_OPT, "document classname",
				StringParam.REQUIRED);
		docClassOpt.setMultiValued(false);
		docClassOpt.setOptionLabel("docclass");

	
		// cmd args
		whereCondArgs = new StringParam(WHERE_CONDITION, "Select statement",
				StringParam.REQUIRED);
		whereCondArgs.setMultiValued(true);
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { localDirOpt, docClassOpt }, 
					new Parameter[] { whereCondArgs });
		cl.setDieOnParseError(false);

		return cl;
	}
}
