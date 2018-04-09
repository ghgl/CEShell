package com.ibm.bao.ceshell.pe;

import junit.framework.TestCase;

public class ApplicationSpaceDefCmdTest extends TestCase {
	
	public ApplicationSpaceDefCmdTest(String name) {
		super(name);
	}
	
	public void testToCSV() throws Exception {
		
		System.out.println(String.format("%s,%s,%s", "foo", "baz", "bar"));
		
		
		RoleInbasketQueue rib = new RoleInbasketQueue("foo", "baz", "bar");
		String expected = "foo,baz,bar";
		String actual = rib.toCSV();
		assertEquals(expected, actual);
				
	}

}
