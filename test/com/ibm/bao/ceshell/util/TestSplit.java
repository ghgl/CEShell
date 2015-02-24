/**
 * 
 */
package com.ibm.bao.ceshell.util;

import junit.framework.TestCase;

/**
 *  TestSplit
 *
 * @author regier
 * @date   Mar 23, 2014
 */
public class TestSplit extends TestCase {
	
	String[] inputs = new String[] {
			"foo=bar",
			"foo=b",
			"foo="
	};
	String[][] expected = new String[][] {
			{"foo", "bar"},
			{"foo", "b"},
			{"foo", ""}
	};

	/**
	 * @param name
	 */
	public TestSplit(String name) {
		super(name);
	}

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
	
	public void TestSplitEquals() throws Exception {
		for (int i = 0; i < inputs.length; i++) {
			String namevalue = inputs[i];
			String[] expect = expected[i]; 
			int pos = namevalue.indexOf("=");
			String name = namevalue.substring(0, pos);
			String value = namevalue.substring(pos + 1);
			assertTrue(name.equals(expect[0]));
			assertTrue(value.equals(expect[1]));
		}
	}
	
	

}
