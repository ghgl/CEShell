/**
 * 
 */
package com.ibm.bao.ceshell.test;

import java.util.ArrayList;
import java.util.List;

/**
 *  UnitTest
 *
 * @author GaryRegier
 * @date   Apr 30, 2011
 */
public class TestCase {
	
	@SuppressWarnings("unused")
	private static final int 
		SETUP = 0,
		TESTING = 1,
		TEARDOWN = 2;
	
	private List<TestStep> setUpCmds;
	private List<TestStep> testCmds;
	private List<TestStep> tearDownCmds;
	
	private String name;
	private boolean allPassed = false;
	@SuppressWarnings("unused")
	private int state;
	
	public TestCase(String name) {
		this.name = name;
		setUpCmds = new ArrayList<TestStep>();
		testCmds = new ArrayList<TestStep>();
		tearDownCmds = new ArrayList<TestStep>();
		state = SETUP;
	}
	
	public TestCase(String name,
			List<TestStep> setupCmds,
			List<TestStep> testCmds,
			List<TestStep> tearDownCmds) {
		this.name = name;
		this.setUpCmds = setupCmds;
		this.tearDownCmds = tearDownCmds;
	}

	public String getName() {
		return name;
	}
	
	public boolean isAllPassed() {
		return allPassed;
	}

	public void setAllPassed(boolean allPassed) {
		this.allPassed = allPassed;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TestStep> getSetUpCmds() {
		return setUpCmds;
	}
	public List<TestStep> getTestCmds() {
		return testCmds;
	}

	public List<TestStep> getTearDownCmds() {
		return tearDownCmds;
	}
	
	public void addCmd(TestStep cmd) {
		if (cmd.isSetupCmd()) {
			addSetupCmd(cmd);
		} else if (cmd.isTestCmd()) {
			addTest(cmd);
		} else if (cmd.isTeardownCmd()) {
			addTearDownCmd(cmd);
		} else {
			throw new IllegalArgumentException("Command is non a setup, test, or teardown command");
		}
	}

	public void addSetupCmd(TestStep cmd) {
		setUpCmds.add(cmd);
	}
	
	public void addTest(TestStep cmd) {
		testCmds.add(cmd);
	}
	
	public void addTearDownCmd(TestStep cmd) {
		tearDownCmds.add(cmd);
	}

}
