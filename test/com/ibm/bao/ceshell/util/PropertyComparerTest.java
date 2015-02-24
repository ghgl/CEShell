/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.util.Properties;

import junit.framework.TestCase;

/**
 *  PropertyComparerTest
 *
 * @author regier
 * @date   Nov 6, 2011
 */
public class PropertyComparerTest extends TestCase {
	
	public static final String[][] srcValues = new String[][] {
		{"first", "alpha"},
		{"second", "beta"},
		{"third", "gamma"}
	};
	
	

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

	/**
	 * Test method for {@link com.ibm.bao.ceshell.util.PropertyComparer#compareProperties(java.util.Properties, java.util.Properties)}.
	 */
	public void testCompareProperties() {
		Properties expected = createProps();
		Properties actual = createProps();
		
		{
			PropertyComparer pc = new PropertyComparer(expected, actual);
			assertTrue(pc.isAllMatched());
		}
		
		{
			actual.remove("second");
			PropertyComparer pc = new PropertyComparer(expected, actual);
			assertTrue(! pc.isAllMatched());
			String xx = pc.getErrorResults();
			System.out.println(xx);
			assertNotNull(xx);
		}
		
	}

	/**
	 * @return
	 */
	private Properties createProps() {
		Properties props = new Properties();
		
		for (int i = 0; i < srcValues.length; i++) {
			String[] row = srcValues[i];
			props.put(row[0], row[1]);
		}
		return props;
	}

}
