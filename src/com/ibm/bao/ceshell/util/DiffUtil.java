/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *  DiffUtil
 *  Compare two ordered Lists of Strings
 *
 * @author regier
 * @date   Feb 17, 2012
 */
public class DiffUtil {
	
	
	public static final String
			LEFT = "<\t",
			RIGHT = ">\t",
			EQUAL = "-\t";
			
	public ArrayList<String> compare(List<String> lhs, File rhsFile, boolean deltasOnly) throws Exception {
		ArrayList<String> rhs = FileUtil.load(rhsFile);
		return compare(lhs, rhs, deltasOnly);
	}
	
	public ArrayList<String> compare(File lhsFile, File rhsFile, boolean deltasOnly) throws Exception {
		ArrayList<String> lhs = FileUtil.load(lhsFile);
		ArrayList<String> rhs = FileUtil.load(rhsFile);
		return compare(lhs, rhs, deltasOnly);
	}
	
	/**
	 * Compare two ordered lists
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public ArrayList<String> compare(List<String> lhs, List<String> rhs, boolean deltasOnly) {
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

