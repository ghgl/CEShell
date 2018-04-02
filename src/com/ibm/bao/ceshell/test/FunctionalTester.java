package com.ibm.bao.ceshell.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;

import com.ibm.bao.ceshell.BaseCommand;
import com.ibm.bao.ceshell.BaseResponse;
import com.ibm.bao.ceshell.CEShell;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 * 
 */

/**
 *  SecurityVerifier
 *
 * @author GaryRegier
 * @date   Mar 27, 2011
 */
public class FunctionalTester extends BaseCommand {

	private static final String 
		CMD = "util.sectestsuite", 
		CMD_DESC = "Run a security test suite",
		HELP_TEXT = CMD_DESC;
		//TODO: Complete the help text
		
	// param names
	private static final String 
		SRC_FILE_OPT = "scriptfile",
		RESULTS_FILE_OPT = "resultsfile",
		VERBOSE_OPT = "verbose";
	
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		FileParam scriptFileParam = (FileParam) cl.getOption(SRC_FILE_OPT);
		FileParam resultsFileParam = (FileParam) cl.getOption(RESULTS_FILE_OPT);
		BooleanParam verboseParam =  (BooleanParam) cl.getOption(VERBOSE_OPT);
		File scriptFile = scriptFileParam.getValue();
		File resultsFile = null;
		boolean verbose = false;
		BufferedWriter writer = null;
		
		try {
			if (resultsFileParam.isSet()) {
				resultsFile = resultsFileParam.getValue();
				writer = new BufferedWriter(new FileWriter(resultsFile,true));
			}
			if (verboseParam != null) {
				verbose = verboseParam.isTrue();
			}
			return functionaTest(scriptFile, writer, verbose);
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
				writer = null;
			}
		}
	}

	/**
	 * 
	 */
	public boolean functionaTest(File scriptFile, BufferedWriter writer, boolean verbose) throws Exception {
		String suiteName = scriptFile.getName();
		List<TestStep> cmds = loadTests(suiteName, scriptFile);
		List<TestCase> unitTests = createTests(cmds);
		Iterator<TestCase> iter = unitTests.iterator();
		while (iter.hasNext()) {
			TestCase testCase = iter.next();
			runTestCase(testCase);
			if (writer != null) {
				recordResults(testCase, writer);
			}
		}
		return true;
	}
	
	/**
	 * @param testCase
	 * @param writer
	 */
	private void recordResults(TestCase testCase, BufferedWriter writer) {
		try {
			String result = (testCase.isAllPassed() ? "pass" : "fail");
			String name = testCase.getName();
			writer.append(result + "\t" + name);
			writer.append("\t" + new Date(System.currentTimeMillis()).toString());
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * runTestCase.
	 * If setup success, then run tests. The teardown only happens if setup succeeds. 
	 * 
	 * @param testCase
	 */
	protected void runTestCase(TestCase testCase) {
		CEShell theShell = getShell();
		BaseResponse currentResponse = theShell.getResponse();
		boolean setUpSuccess = false;
		boolean testsSuccess = false;
		boolean tearDownSuccess = false;
		
		try {
			setUpSuccess = doRunTests(testCase.getSetUpCmds(), currentResponse);
			if (setUpSuccess) {
				try {
					testsSuccess = doRunTests(testCase.getTestCmds(), currentResponse);
				} finally {
					tearDownSuccess =doRunTests(testCase.getTearDownCmds(), currentResponse);
				}
			}
			
			if (setUpSuccess && testsSuccess && tearDownSuccess) {
				testCase.setAllPassed(true);
				getResponse().printOut("Test case passed: " + testCase.getName());
			} else {
				getResponse().printOut("Test case failed: " + testCase.getName());
			}
		} finally {
			theShell.setResponse(currentResponse);
		}
	}
	
	private boolean doRunTests(List<TestStep> cmds, BaseResponse currentResponse) {
		Iterator<TestStep> iter = cmds.iterator();
		while (iter.hasNext()) {
			TestResponse testResponse = new TestResponse(currentResponse);
			TestStep nextCmd = iter.next();
			executeCmd(testResponse, nextCmd);
			logResults(currentResponse, testResponse, nextCmd);
			if (! nextCmd.passed()) {
				return false;
			}
		}
		return true;
	}


	/**
	 * @param cmds
	 * @return
	 */
	private List<TestCase> createTests(List<TestStep> cmds) {
		String currentTestName = "";
		TestCase currentTest = null;
		List<TestCase> unitTests = new ArrayList<TestCase>();
		Iterator<TestStep> iter = cmds.iterator();
		while(iter.hasNext()) {
			TestStep cmd = iter.next();
			if (! currentTestName.equals(cmd.getTestCase())) {
				currentTestName = cmd.getTestCase();
				currentTest = new TestCase(currentTestName);
				unitTests.add(currentTest);
			}
			currentTest.addCmd(cmd);
		}
		return unitTests;
	}
	
	protected void logResults(BaseResponse currentResponse, TestResponse testResponse, TestStep nextCmd) {
		currentResponse.printOut(nextCmd.formatResult());
	}

	/**
	 * @param nextCmd
	 */
	private void executeCmd(TestResponse testResponse, TestStep nextCmd) {
		CEShell theShell = getShell();
		
		try {
			theShell.setResponse(testResponse);
			getShell().execute(nextCmd.getCmd());
			nextCmd.setActualOutcome(testResponse.getAcutalOutcome());
		} catch (Exception e) {
			// no-op
		}
	}

	/**
	 * @return
	 */
	private List<TestStep> loadTests(String suiteName, File scriptFile) throws Exception {
		List<TestStep> tests = new ArrayList<TestStep>();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(scriptFile));
			String nextLine;
			
			while ((nextLine = reader.readLine()) != null) {
				nextLine = nextLine.trim();
				if (nextLine.startsWith("#")) {
					continue;
				}
				TestStep cmdTestInfo = parseCmdInfo(suiteName, nextLine);
				if (cmdTestInfo != null) {
					tests.add(cmdTestInfo);
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
				reader = null;
			}
		}
		
		return tests;
	}

	/**
	 * @param nextLine
	 * @return CmdTestInfo, null if there is a problem
	 */
	private TestStep parseCmdInfo(String testSuite, String nextLine) {
		TestStep results = null;
		StringTokenizer t = new StringTokenizer(nextLine, "\t");
		String testCase;
		String phase;
		String expectedOutcome;
		String cmd;
		
		try {
			int tokens = t.countTokens();
			if (tokens != 4) {
				throw new IllegalArgumentException("Wrong number of tokens");
			}
		
			testCase = t.nextToken();
			phase = t.nextToken();
			expectedOutcome = t.nextToken();
			cmd = t.nextToken();
			results = new TestStep(
					testSuite,
					testCase,
					phase,
					expectedOutcome,
					cmd);
		} catch (Exception e) {
			getResponse().printErr("Failed to load test: " + nextLine);			
		}
		
		return results;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		FileParam srcFilesOpt = null;
		FileParam resultsFileOpt = null;
		BooleanParam verboseOpt = null;

		// options
		srcFilesOpt = new FileParam(
				SRC_FILE_OPT,
				"Functional test script file",
				FileParam.IS_FILE & FileParam.IS_READABLE);
		srcFilesOpt.setOptionLabel("<scriptFile>");
		srcFilesOpt.setOptional(false);
		srcFilesOpt.setMultiValued(false);
		
		resultsFileOpt = new FileParam(
				RESULTS_FILE_OPT,
				"files to write results");
		resultsFileOpt.setOptionLabel("<resultsFile>");
		resultsFileOpt.setMultiValued(false);
		
		verboseOpt = new BooleanParam(VERBOSE_OPT, "verbose output");
		
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {srcFilesOpt, resultsFileOpt, verboseOpt}, 
					new Parameter[] { });
		cl.setDieOnParseError(false);

		return cl;
	}
}
