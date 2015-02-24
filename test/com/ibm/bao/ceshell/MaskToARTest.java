package com.ibm.bao.ceshell;

import com.ibm.bao.ceshell.security.*;

import junit.framework.TestCase;

public class MaskToARTest extends TestCase {

	public MaskToARTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void test2d() throws Exception {
		String[][] DocPrivilegeLevelNames = new String[][] {
			{"alph", "beta"},
			{"foo","bar"}
			};
		
		for (int i = 0; i < DocPrivilegeLevelNames.length; i++) {
			for (int j = 0; j < DocPrivilegeLevelNames[i].length; j++) {
				System.out.print(DocPrivilegeLevelNames[i][j]);
				System.out.println(",");
			}
			System.out.println();
		}
	}
	
	public void testCompareMasks() throws Exception {
		MaskToAR masks = new MaskToAR();
		StringBuffer buf = new StringBuffer();
		buf.append(padRight("name", 20));
		buf.append("Pub\tVC\tVP\tModP\tMiV\tMaV\tF\n");
		buf.append("--------------------\t\t\t\t\t\t\t\n");
		for (int i = 0; i < masks.ARs.length; i++) {
			ARInfo nextInfo = masks.ARs[i];
			String name = nextInfo.getArName();
//			buf.append(padRight(name, 20));
//			for (int j = 0; j < masks.DocPrivilegeLevels.length; j++) {
//				if (nextInfo.isSet(masks.DocPrivilegeLevels[j])) {
//					buf.append("X");
//				} else {
//					buf.append("-");
//				}
//				buf.append("\t");
//				
//			}
			buf.append("\n");
		}
		System.out.println(buf.toString());
	}
	
	public void testMaskToAces() throws Exception {
		int docAllPrivileges = 998871;
		int docReadPrivileges = 131201;
		MaskToAR maskToAR = new MaskToAR();
		System.out.println("docAllPrivileges");
		maskToAR.maskToAces(docAllPrivileges);
		
		System.out.println("------------------------------");
		System.out.println("docReadPrivileges");
		maskToAR.maskToAces(docReadPrivileges);
	}
	
	public String padRight(String input, int len) {
		int inputLen = input.length();
		int padLen = len - inputLen;
		String result = input;
		if (padLen > 0) {
			for (int i = 0; i < padLen; i++) {
				result = result + " ";
			}
		}
		return result;
	}

}
