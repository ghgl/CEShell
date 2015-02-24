package com.ibm.bao.ceshell;

import junit.framework.TestCase;

public class CWDTest extends TestCase {

	public CWDTest(String name) {
		super(name);
	}

	
	public static final String[][] CTOR_TEST_DATA = {
		{"/foo", "/foo"},
		{"/foo/bar", "/foo/bar"}
	};
	
	/** start dir, input, expected **/
	public static final String[][][] CD_TEST_DATA = {
		{ {"/foo/bar", "..", "/foo"} },
		{{"/foo/bar", "../baz", "/foo/baz"}},
		{{"/foo/bar", ".", "/foo/bar"}},
		
		// bad input should not change the current working directory
		{{"/foo", "../../", "/foo" }}	
		
	};


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCtor() {
		for (int i = 0; i < CTOR_TEST_DATA.length; i++) {
			String[] testdata = CTOR_TEST_DATA[i];
			String input = testdata[0];
			String expected = testdata[1];
			
			CWD cwd = new CWD(input);
			String output = cwd.pwd();
			assertTrue("Failed on " + input + "/" + expected + "/" + output, expected.equals(output));
		}
		
		/** test empty ctor **/
		{
			CWD cwd = new CWD();
			String expected = "/";
			String pwd = cwd.pwd();
			assertTrue(expected.equals(pwd));
		}	
	}

	/**
	 * Test method for {@link com.ibm.bao.ceshell.CWD#cd(java.lang.String)}.
	 */
	public void testCd() throws Exception {
		for (int i = 0; i < CD_TEST_DATA.length; i++) {
			String[] testdata = CD_TEST_DATA[i][0];
			String start = testdata[0];
			String input = testdata[1];
			String expected = testdata[2];
			
			CWD cwd = new CWD(start);
			cwd.cd(input);
			String output = cwd.pwd();
			String msg = "start:input:expected -- ouput";
			msg = msg + start + ":" + input + ":" + expected + " -- " + output;
			assertTrue(msg, expected.equals(output));
			System.out.println(msg);
		}
	}
	
	public void testGetName() throws Exception {
		String[][] TEST_DATA = {
			{"/foo/bar", "bar"},
			{"../foo/bar", "bar"},
			{"bar", "bar"}
		};
		CWD cwd = new CWD();
		for (int i = 0; i < TEST_DATA.length; i++) {
			String input = TEST_DATA[i][0];
			String expected = TEST_DATA[i][1];
			String actual = cwd.getName(input);
			assertTrue(expected.equals(actual));
		}
	}
	
	public void testGetPath() throws Exception {
		CWD cwd = new CWD();
		String[][] TEST_DATA = {
				{"/foo/", "/foo/"},
				{"/foo", "/"},
				{"/foo/bar", "/foo"},
				{"/", "/"}
			};
		
		for (int i = 0; i < TEST_DATA.length; i++) {
			String input = TEST_DATA[i][0];
			String expected = TEST_DATA[i][1];
			String actual = cwd.getPath(input);
			assertTrue(expected.equals(actual));
		}
	}
}
