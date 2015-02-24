/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 *  PropertyTemplateUsageReportCmdTest
 *
 * @author regier
 * @date   Feb 14, 2012
 */
public class PropertyTemplateUsageReportCmdTest extends TestCase {
	
	public static final String
		LEFT = "<\t",
		RIGHT = ">\t",
		EQUAL = "-\t";
	

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
	
	public void testDiff() throws Exception {
		String[] d1 = new String[] {
				"alpha", "beta", "gamma"
		};
		String[] d2 = new String[] {
				"alpha", "beta", "gamma", "zeta", "zzeta", "zzzeta"
		};
		String[] d3 = new String[] {"bbeeta", "gaama"};
		String[] d4 = {"beta"};
		

//		printResults(compare(copyList(d1), this.copyList(d2)));
//		printResults(compare(copyList(d4), copyList(d2)));
		
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

	/**
	 * @param results
	 */
	private void printResults(List<String> results) {
		for (String item : results) {
			System.out.println(item);
		}
		
	}

	/**
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private ArrayList<String> compare(List<String> lhs, List<String> rhs, boolean deltasOnly) {
		ArrayList<String> results = new ArrayList<String>();
		int x = lhs.size();
		int y = rhs.size();
		int xpos = 0;
		int ypos = 0;
		String xstr = "";
		String ystr = "";
		while (true){
			if (xpos >= x) {
				addFinal(results, rhs, ypos, RIGHT);
				break;
			}
			if (ypos >= y) {
				addFinal(results, lhs, xpos, LEFT);
				break;
			}
			xstr = lhs.get(xpos);
			
			ystr = rhs.get(ypos);
			
			int z = xstr.compareTo(ystr);
			if (z < 0) {
				results.add(LEFT + xstr);
				if (xpos < x) {
					xpos++;
				}
			} else if(z > 0) {
				results.add(RIGHT + ystr);
				if (ypos < y) {
					ypos++;
				}
			} else {
				if (! deltasOnly) {
					results.add(EQUAL + xstr);
				}
				xpos++;
				ypos++;
			}
		}
						
		return results;
	}

	/**
	 * @param lhs
	 * @param xpos
	 */
	private void addFinal(ArrayList<String> results, List<String> lhs, int xpos, String indicator) {
		for(;xpos  < lhs.size(); xpos++) {
			results.add(indicator + lhs.get(xpos));
		}
		
	}


	

}
