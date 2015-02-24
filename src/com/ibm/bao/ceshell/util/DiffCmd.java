/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.File;
import java.util.List;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;

import com.ibm.bao.ceshell.BaseCommand;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  DiffCmd
 *
 * @author regier
 * @date   May 17, 2012
 */
public class DiffCmd extends BaseCommand {
	
	private static final String 
	CMD = "util.diff", 
	CMD_DESC = "diff two files -- the left-hand side and the right hand side",
	HELP_TEXT = CMD_DESC + "\n" +
			"\nUsage:\n" +
			"util.diff c:\temp\foo.txt c:\temp\bar.txt" +  "\n" +
			"\tdiff two file\n";

	// param names
	private static final String
		DELTAS_ONLY_OPT = "deltas-only",
		RESULTS_FILE_OPT = "file",
		LHS_FILE_ARG = "lhs",
		RHS_FILE_ARG = "rhs";
	

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam deltasOnlyOpt = (BooleanParam) cl.getOption(DELTAS_ONLY_OPT);
		FileParam lhsFileArg = (FileParam) cl.getArg(LHS_FILE_ARG);
		FileParam rhsFileArg = (FileParam) cl.getArg(RHS_FILE_ARG);
		FileParam resultsFileOpt = (FileParam) cl.getOption(RESULTS_FILE_OPT);
		Boolean deltasOnly = deltasOnlyOpt.getValue();
		File resultsFile = null;
		File lhsFile = lhsFileArg.getValue();
		File rhsFile = rhsFileArg.getValue();
		
		if (resultsFileOpt.isSet()) {
			resultsFile = resultsFileOpt.getValue();
		}
		return diff(lhsFile, rhsFile, deltasOnly, resultsFile);
	}

	/**
	 * 
	 * @param lhsFile
	 * @param rhsFile
	 * @param deltasOnly
	 * @param resultsFile optional null if results are printed to stdout
	 * @return
	 * @throws Exception
	 */
	public boolean diff(
				File lhsFile, 
				File rhsFile, 
				Boolean deltasOnly, 
				File resultsFile) throws Exception {
		List<String> results = new DiffUtil().compare(lhsFile, rhsFile, deltasOnly.booleanValue());
		if (resultsFile != null) {
			writeResultsToFile(results, resultsFile);
			getResponse().printOut("wrote results to " + resultsFile.toString());
		} else {
			displayResults(results);
		}
		
		return true;
	}

	/**
	 * @param results
	 */
	private void displayResults(List<String> results) {
		for (String row : results) {
			getResponse().printOut(row);
		}
		
	}

	/**
	 * 
	 * @param results
	 * @param resultsFile
	 */
	private void writeResultsToFile(List<String> results, File resultsFile) throws Exception {
		FileUtil.store(results, resultsFile);
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam deltasOnlyOpt = null;
		FileParam lhsFileArg = null;
		FileParam rhsFileArg = null;
		FileParam resultsFileOpt = null;
		 
		
		// options
		{
			deltasOnlyOpt = new BooleanParam(DELTAS_ONLY_OPT,
					"return only the delta list");
			deltasOnlyOpt.setOptional(Boolean.TRUE);
			deltasOnlyOpt.setMultiValued(false);
			//deltasOnlyOpt.setTag("deltas-only");
			
			resultsFileOpt = new FileParam(RESULTS_FILE_OPT,
					"results file", 
					FileParam.OPTIONAL);
		}
	
		// cmd args
		{
			lhsFileArg = new FileParam(LHS_FILE_ARG,
					"Left-hand file )",
					FileParam.IS_FILE & 
					FileParam.IS_READABLE,
					FileParam.REQUIRED);
			lhsFileArg.setOptionLabel("<lhs>");
			lhsFileArg.setMultiValued(false);
			lhsFileArg.setOptionLabel("<lefh-hand-side file>");
		}
		{
			rhsFileArg = new FileParam(RHS_FILE_ARG,
					"Right-hand file )",
					FileParam.IS_FILE & 
					FileParam.IS_READABLE,
					FileParam.REQUIRED);
			rhsFileArg.setOptionLabel("<rhs>");
			rhsFileArg.setMultiValued(false);
			rhsFileArg.setOptionLabel("<righ-hand-side file>");
		}
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { deltasOnlyOpt, resultsFileOpt }, 
					new Parameter[] {lhsFileArg, rhsFileArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
