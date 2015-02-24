/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import junit.framework.TestCase;

/**
 *  StepElementEditUtilTest
 *
 * @author regier
 * @date   Nov 13, 2011
 */
public class StepElementEditUtilTest extends TestCase {

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

	/**
	 * Test method for {@link com.ibm.bao.ceshell.pe.StepElementEditUtil#storePropVales(java.lang.Boolean, java.lang.Object)}.
	 */
	public void testStorePropVales() {
		String[][] src = new String[][] {
				{"alpha", "beta", "gamma"},
				{"alpha", "", "gamma"},
				{}
		};
		String[] expected = new String[] {
				"[alpha|beta|gamma]",
				"[alpha||gamma]",
				"[]"
		};
		StepElementEditUtil seUtil = new StepElementEditUtil();
		
		for(int i = 0; i < expected.length; i++) {
			String expectedValue = expected[i];
			String[] input = src[i];
			String actualValue = seUtil.storePropVales(Boolean.TRUE, input);
			assertTrue(actualValue + ", " + expected, expectedValue.equals(actualValue));
		}
	}
	
	public void testStoreDateValues() {
		
	}

}
