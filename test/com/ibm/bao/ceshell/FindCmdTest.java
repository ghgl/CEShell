package com.ibm.bao.ceshell;

import junit.framework.TestCase;

public class FindCmdTest extends TestCase {

	public void testFind() {
		String expected = "select top 100 d.ObjectType, d.id, d.Name, d.DateCreated FROM Document d order by DateCreated asc";
		String findTypeFmt = "select top %d d.ObjectType, d.id, d.Name, d.DateCreated FROM %s d order by DateCreated %s";
		
		Integer max = 100;
		String classType = "Document";
		String order = "asc";
		
		String actual = String.format(findTypeFmt, new Object[] {max, classType, order} );
		System.out.println(actual);
		assertEquals(actual, expected);
	}

}
