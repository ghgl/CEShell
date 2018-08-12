package com.ibm.ucm.ecm.ceshell;

import junit.framework.TestCase;

public class UCMTest extends TestCase {

	public void testIsCaseId() {
		UCM ucm = new UCM();
		String input;
		boolean expected, actual;
		
		input = "CSE-180809-00005";
		expected = true;
		actual = ucm.isCaseId(input);
		assertEquals(input, expected, actual);
		
		input = input.toLowerCase();
		expected = false;
		actual = ucm.isCaseId(input);
		assertEquals(input, expected, actual);
		
	}

}
