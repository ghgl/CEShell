/**
 * 
 */
package com.ibm.bao.ceshell.test;

/**
 *  CmdTestInfo
 *
 * @author GaryRegier
 * @date   Apr 27, 2011
 */
public class TestStep {
	
	public static final String
		PASS = "pass",
		FAIL = "fail";
	
	public static final String
		SETUP_PHASE = "setup",
		TEST_PHASE = "test",
		TEARDOW_PHASE = "teardown";
		
	
	private String testSuite;
	private String testCase;
	private String cmd;
	private String phase;
	private boolean expectedOutcome;
	private Boolean actualOutcome = null;
	
	public TestStep() {
		super();
	}
	
	public TestStep(
			String testSuite,
			String testCase,  
			String phase,
			String expectedOutcome,
			String cmd
			) {
		super();
		this.testSuite = testSuite;
		this.testCase = testCase;
		this.phase = phase.toLowerCase();
		this.expectedOutcome = (PASS.equals(expectedOutcome.toLowerCase()) ? true : false);
		
		this.cmd = cmd;
	}
	
	
	
	public String getTestSuite() {
		return testSuite;
	}

	public void setTestSuite(String testSuite) {
		this.testSuite = testSuite;
	}

	public String getTestCase() {
		return testCase;
	}

	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public boolean getExpectedOutcome() {
		return expectedOutcome;
	}
	public void setExpectedOutcome(boolean expectedOutcome) {
		this.expectedOutcome = expectedOutcome;
	}
	
	public boolean getActualOutcome() {
		return actualOutcome;
	}

	public void setActualOutcome(boolean actualOutcome) {
		this.actualOutcome = actualOutcome;
	}

	public boolean passed() {
		return (expectedOutcome == actualOutcome.booleanValue());
	}
	
	public String printResults() {
		
		StringBuffer buf = new StringBuffer();
		buf.append(passed()).append(":\t");
		buf.append(testSuite).append("\t");
		buf.append(testCase).append("\t");
		
		if(! passed()) {
			buf.append("\t(expected: " + getExpectedOutcome())
			.append(", actualy: " + getActualOutcome());
		}
		return buf.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(testSuite).append("\t")
			.append(testCase).append("\t")
			.append(expectedOutcome).append("\t")
			.append(cmd).append("\n");
		
		return buf.toString();
	}
	
	public boolean isSetupCmd() {
		return (SETUP_PHASE.equals(phase));
	}
	
	public boolean isTestCmd() {
		return (TEST_PHASE.equals(phase));
	}
	
	public boolean isTeardownCmd() {
		return (TEARDOW_PHASE.equals(phase));
	}
	
	public String formatResult() {
		StringBuffer buf = new StringBuffer();
		if (passed()) {
			buf.append(PASS);
		} else {
			buf.append(FAIL);
		}
		buf.append("\t");
		buf.append(getPhase()).append("\t");
		buf.append(testSuite).append(".").append(testCase).append("\t");
		buf.append(getCmd());
		
		return buf.toString();
	}
}
