package com.ibm.bao.ceshell;

import junit.framework.TestCase;

public class ACECommandTest extends TestCase {

	public ACECommandTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Results are printed out looking line this:
	 * <pre>
	 * 3	false	MAJOR_VERSION
	 * 4	true	LINK
	 * 5	false	UNLINK
	 * </pre>
	 * <p>For a result, we look for the value "true" next to the name
	 */
	public void testShowAces() {
		ACECmd cmd = new ACECmd();
		String testname = com.filenet.api.constants.AccessRight.LINK.toString();
		String expected = "true\t" + testname;
		
		int testmask = com.filenet.api.constants.AccessRight.LINK_AS_INT;
		String result = cmd.showAces(testmask);
		System.out.println(result);
		int pos = result.indexOf(expected);
		assertTrue(pos > 0);
	}

}
