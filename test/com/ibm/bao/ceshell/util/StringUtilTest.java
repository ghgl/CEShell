/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ibm.bao.ceshell.util.StringUtil;

import junit.framework.TestCase;

/**
 *  StringUtilTest
 *
 * @author GaryRegier
 * @date   Sep 19, 2010
 */
public class StringUtilTest extends TestCase {

	/**
	 * @param name
	 */
	public StringUtilTest(String name) {
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

	/**
	 * Test method for {@link com.ibm.bao.ceshell.util.StringUtil#fmtDate(java.util.Date)}.
	 */
	public void testFmtDate() throws Exception {
		
		String expected = "01/24/2010 3:30:59 PM";
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		Date parsedDate = sdf.parse(expected);
		String actual = StringUtil.fmtDate(parsedDate);
		assertTrue(expected.equals(actual));
	}
}
