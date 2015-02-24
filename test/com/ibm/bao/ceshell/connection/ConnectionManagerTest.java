/**
 * 
 */
package com.ibm.bao.ceshell.connection;

import java.util.Iterator;
import java.util.List;

import com.ibm.bao.ceshell.connection.ConnectionStorageInfo;
import com.ibm.bao.ceshell.connection.ConnectionManager;

import junit.framework.TestCase;

/**
 *  ConnectionManagerTest
 *
 * @author GaryRegier
 * @date   Nov 12, 2010
 */
public class ConnectionManagerTest extends TestCase {

	/**
	 * @param name
	 */
	public ConnectionManagerTest(String name) {
		super(name);
	}

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
	
	public void testConnectionManager() throws Exception {
		ConnectionManager mgr = new ConnectionManager();
		mgr.initConnectionStorageInfo();
		List<String> aliases = mgr.getConnectionAliasNames();
		Iterator<String> iter = aliases.iterator();
		while (iter.hasNext()) {
			String alias = iter.next();
			System.out.println(alias);
		}
		ConnectionStorageInfo firstAlias = mgr.getConnectionStorageInfo(aliases.get(0));
		
		mgr.addConnectionAlias("alpha", firstAlias);
		mgr.addConnectionAlias("zeta", firstAlias);
		
		List<String> newAliasList = mgr.getConnectionAliasNames();
		assertTrue(newAliasList.get(0).equals("alpha"));
		assertTrue(newAliasList.get(newAliasList.size() - 1)
				.equals("zeta"));
	}

}
