/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import junit.framework.TestCase;

/**
 *  NameMgrTest
 *
 * @author GaryRegier
 * @date   Jul 3, 2011
 */
public class NameMgrTest extends TestCase {
	
	private File outputDir = null;
	private String testName = "namemanagertest.txt";
	private File testFile;

	/**
	 * @param name
	 */
	public NameMgrTest(String name) {
		super(name);
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		outputDir = new File("c:/temp");
		testFile = new File(outputDir, testName);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(testFile));
			writer.write("howdy");
			writer.close();
		} finally {
			writer = null;
		}
	}
	
	public void testDontOverwrite() throws Exception {
		NameMgr mgr = new NameMgr(outputDir, testName);
		assertTrue(mgr.isDontOverwrite() == true);
		assertTrue(mgr.isCreateUniqueName() == true);
		
		
		File[] outputFiles = mgr.createNames(1);
		assertTrue(outputFiles.length == 1);
		assertNotSame(testFile.toString(), outputFiles[0].toString());
		
		// ok to overwrite
		mgr.setDontOverwrite(false);
		File[] sameName = mgr.createNames(1);
		assertTrue(sameName.length == 1);
		File expectedFile = sameName[0];
		
		assertTrue("overwrite the file", expectedFile.equals(testFile));
		
		// dont overwrite, don't create, should throw exception
		Exception ee = null;
		try {
			mgr.setCreateUniqueName(false);
			mgr.setDontOverwrite(true);
			File[] dupeFiles = mgr.createNames(1);
		}catch (Exception e) {
			ee = e;
		}
		assertTrue("dont overwrite and dont create", ee != null);		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
