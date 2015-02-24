/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

/**
 *  CryptoUtilsTest
 *
 * @author GaryRegier
 * @date   Nov 6, 2010
 */
public class CryptoUtilsTest extends TestCase {
	
	private static final String 
		ORIG = "orig.properties",
		ENCRYPT = "orig.encrypt.properties",
		KEY_FILE = "cryptotest.key";

	private Properties orig = new Properties();
	private File origProps;
	private File encryptProps;
	private File keyFile;
	private File homeDir;
	
	/**
	 * @param name
	 */
	public CryptoUtilsTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		orig.put("usr1", "password");
		orig.put("usr2", "passw0rd");
		orig.put("usr3", "pa!@#$rd");
		String tmpdir = System.getenv("TMP");
		System.out.println("tmpdir is " + tmpdir);
		homeDir = new File(tmpdir);
		if (! homeDir.exists() && 
				homeDir.isDirectory() && 
				homeDir.canWrite()) {
			throw new Exception("Can not write to tmp dir");
		}
		keyFile = new File(homeDir, KEY_FILE);
		origProps = new File(homeDir, ORIG);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(origProps);
			orig.store(out, "");
		} finally {
			out.close();
			out = null;
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		if (encryptProps != null && encryptProps.exists()) {
			encryptProps.delete();
		}
	}
	
	public void testEncrypt() throws Exception {
		// before we execute, the key file should not exist
		String clearText, cryptText, decodedText = null;
		
		assertTrue(! keyFile.exists());
		clearText = "howdy";
		cryptText = CryptoUtils.encrypt(clearText, keyFile);
		assertTrue(keyFile.exists());
		assertNotNull(cryptText);
		decodedText = CryptoUtils.decrypt(cryptText, keyFile);
		assertTrue(decodedText.equals(clearText));
		
		System.out.println("clear:\t" + clearText);
		System.out.println("crypt:\t" + cryptText);
	}

}
