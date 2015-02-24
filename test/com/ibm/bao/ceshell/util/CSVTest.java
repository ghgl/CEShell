/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.util.List;

import junit.framework.TestCase;

/**
 *  CSVTest
 *
 * @author regier
 * @date   Oct 12, 2011
 */
public class CSVTest extends TestCase {

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
	
	public void testParse() throws Exception {
		CSV csv = new CSV('|');
		
		String[] tests = new String[] {
				"alpah|beta|gamma",
				"alpah||gamma",
				"|beta|gamma",
				"alpah|beta|",
				"alpah||",
				"||gamma",
				"||"
		};
		for (String test : tests) {
			List<String> tokens = csv.parse(test);
			int result = tokens.size();
			assertTrue(test, result == 3);
		}
	}

}
