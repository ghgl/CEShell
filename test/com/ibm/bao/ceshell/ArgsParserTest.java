package com.ibm.bao.ceshell;

import junit.framework.TestCase;

public class ArgsParserTest extends TestCase {

	
	public void testParse() throws Exception {
		String 
			t1 = "foo baz bar",
			t2 = "\"foo baz bar\"",
			t3 = "foo \"baz\" bar",
			t4 = "foo \"baz bar\"",
			t5 = "foo    bar",
			t6 = "",
			t7 = "\"\"";
		
		String[] input = new String[] {
				t1,
				t2,
				t3,
				t4,
				t5,
				t6,
				t7
		};
		String[][] expected = new String[][] {
			{"foo",  "baz", "bar"},	// t1 3 tokens
			{"foo baz bar"},		// t2, 1 token
			{"foo", "baz", "bar"},	// t3, 3 tokens
			{"foo", "baz bar"},		// t4 2 tokens
			{"foo", "bar"},         // t5 2 tokens
			{},                     // t6 0 tokens
			{}                      // t7 0 tokens
		};
		
		for (int i = 0; i < input.length; i++) {
			String[] nextExpected = expected[i];
			doTest(i, input[i], nextExpected);
		}
	}

	private void doTest(int testPos, String input, String[] nextExpected) throws Exception {
		System.out.print("testing t" + testPos);
		ArgsParser parser = new ArgsParser(input);
		String[] actual = parser.parse();
		assertEquals(actual.length, nextExpected.length);
		for(int i = 0; i < actual.length; i++) {
			assertEquals(actual[i], nextExpected[i]);
		}
		System.out.println("...matches");
	}
}
