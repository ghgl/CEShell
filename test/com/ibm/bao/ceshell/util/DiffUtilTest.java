/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

/**
 *  DiffUtilTest
 *
 * @author regier
 * @date   Feb 17, 2012
 */
public class DiffUtilTest extends TestCase {
	
	String[] d1;
	String[] d2;
	String[] d3;
	String[] d4;
	String[] d1equals;
	String[] d1_to_d2_diff;
	String[] d2_to_d1_diff;
	
	

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		d1 = new String[] {
				"alpha", "beta", "gamma"
		};
		d2 = new String[] {
				"alpha", "beta", "gamma", "zeta", "zzeta", "zzzeta"
		};
		d3 = new String[] {"bbeeta", "gaama"};
		d4 = new String[] {"beta"};
		
		d1equals = new String[] {
				DiffUtil.EQUAL + d1[0],
				DiffUtil.EQUAL + d1[1],
				DiffUtil.EQUAL + d1[2]
		};
		d1_to_d2_diff = new String[] {
			DiffUtil.RIGHT + d2[3],
			DiffUtil.RIGHT + d2[4],
			DiffUtil.RIGHT + d2[5]
		};
		d2_to_d1_diff = new String[] {
				DiffUtil.LEFT + d2[3],
				DiffUtil.LEFT + d2[4],
				DiffUtil.LEFT + d2[5]
			};
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link com.ibm.bao.ceshell.util.DiffUtil#compare(java.util.List, java.io.File, boolean)}.
	 */
	public void testCompareListOfStringToFile() throws Exception {
		File tstFile = new File("C:/temp/difftest_d1.txt");
		ArrayList<String> d1Data = copyList(d1);
		ArrayList<String> emptyList = new ArrayList<String>();
		ArrayList<String> actualResults = null;
		DiffUtil diffUtil = new DiffUtil();
		
		FileUtil.store(d1Data, tstFile);
		actualResults = diffUtil.compare(d1Data, tstFile, true);
		System.out.println("\n\nD1Data:");
		printArray(d1Data);
		
		System.out.println("\nresults:");
		printArray(actualResults);
		assertTrue(arraysEqual(emptyList, actualResults));
		
	}

	/**
	 * Test method for {@link com.ibm.bao.ceshell.util.DiffUtil#compare(java.io.File, java.io.File, boolean)}.
	 */
	public void testCompareTwoFiles() throws Exception {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.ibm.bao.ceshell.util.DiffUtil#compare(java.util.List, java.util.List, boolean)}.
	 */
	public void testCompareListOfStringListOfStringBoolean()  throws Exception {
		
		ArrayList<String> emptyList = new ArrayList<String>();
		
		compare(copyList(d1), copyList(d1), false, copyList(d1equals));	
		compare(copyList(d1), copyList(d1), true, emptyList);
		compare(copyList(d1), copyList(d2), true, copyList(d1_to_d2_diff));
		compare(copyList(d2), copyList(d1), true, copyList(d2_to_d1_diff));
	}
	
	public void compare(
			ArrayList<String> lhs, 
			ArrayList<String> rhs, 
			boolean deltasOnly, 
			ArrayList<String> expected) throws Exception {
		DiffUtil diffUtil = new DiffUtil();
		ArrayList<String> results = diffUtil.compare(lhs, rhs, deltasOnly);
		assertTrue(arraysEqual(results, expected));
	}

	/**
	 * @param results
	 * @param expected
	 * @return
	 */
	private boolean arraysEqual(ArrayList<String> results,
			ArrayList<String> expected) {
		if (results.size() != expected.size()) {
			printArray(results);
			printArray(expected);
			fail("size of results and expected do not match");
		}
		
		for(int i = 0; i < results.size(); i++) {
			String lhs = results.get(i);
			String rhs = results.get(i);
			if (! lhs.equals(rhs)) {
				System.err.println("items do to not match at pos " + i + " -- lhs = " + lhs + ", rhs = " + rhs);
				return false;
			}
		}
		return true;
	}

	/**
	 * @param results
	 */
	private void printArray(ArrayList<String> results) {
		for (String item : results) {
			System.out.println(item);
		}
		
	}

	/**
	 * @param data
	 * @return
	 */
	private ArrayList<String> copyList(String[] data) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < data.length; i++) {
			list.add(data[i]);
		}
		return list;
	}

}
