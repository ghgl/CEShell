/**
 * 
 */
package com.ibm.bao.ceshell.util;

import junit.framework.TestCase;

/**
 *  MimeTypesUtilTest
 *
 * @author regier
 * @date   Sep 7, 2011
 */
public class MimeTypesUtilTest extends TestCase {

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
	
	public void testMimeTypesUtil() throws Exception {
		MimeTypesUtil mtu = new MimeTypesUtil();
		String mt = null;
		
		mt = mtu.getMimeType("fo.tif");
		mt = mtu.getMimeType("foo.class");
		mt = mtu.getMimeType("foo.jar");
	}

}
