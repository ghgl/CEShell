/**
 * 
 */
package com.ibm.bao.ceshell.connection;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import com.ibm.bao.ceshell.util.CryptoUtils;
import com.ibm.bao.ceshell.util.PropertyUtil;

/**
 *  CECryptoManager
 *
 * @author GaryRegier
 * @date   Dec 15, 2010
 */
public class CECryptoManager {
	private File encryptKey;
	public static final String CRYPT_PREFIX = "crypt:";
	
	private static final String CESHELL_KEY_FILENAME = "ceshell.key";
	
	
	public CECryptoManager(File homeDir) {
		init(homeDir);
	}
	
	/**
	 * Convert all the passwords in a properties file
	 * @param propsFile
	 * @return Keyfile used to encrypt
	 */
	public void encryptPropertyValues(File propsFile) throws Exception {
		Properties props = null;
		PropertyUtil util = new PropertyUtil();
		
		props = util.loadPropertiesFromFile(propsFile);
		for(Enumeration<Object> iter = props.keys(); 
				iter.hasMoreElements(); ) {
			String key = iter.nextElement().toString();
			String cryptTextOrClearText = props.getProperty(key);
			if (cryptTextOrClearText.startsWith(CRYPT_PREFIX)) {
				// already encrypted...just skip
				continue;
			}
				
			String encryptValue = CRYPT_PREFIX + 
					CryptoUtils.encrypt(cryptTextOrClearText, encryptKey);
			props.setProperty(key, encryptValue);
		}
		props.store(new FileOutputStream(propsFile), "");
	}
	
	
	/**
	 * 
	 * @param cryptText: String either clear text or crypt text
	 */
	public String getClearText(String cryptTextOrClearText) 
			throws Exception{
		String raw;
		if (! cryptTextOrClearText.startsWith(CRYPT_PREFIX)) {
			// cleartext. return
			return cryptTextOrClearText;
		}
		int startPos = CRYPT_PREFIX.length();
		raw = cryptTextOrClearText.substring(startPos);
		return CryptoUtils.decrypt(raw, encryptKey);
	}
	
	public String encryptText(String clearText) throws Exception {
		String encryptValue = CRYPT_PREFIX +
				CryptoUtils.encrypt(clearText, encryptKey);
		return encryptValue;
	}
	
	/**
	 * Assume it's encrypted
	 * @param cryptText
	 * @return
	 */
	public String decrypt(String cryptText) throws Exception {
		return CryptoUtils.decrypt(cryptText, encryptKey);
	}
	
	
	private void init(File homeDir) {		
		encryptKey = new File(homeDir, CESHELL_KEY_FILENAME);
	}
}
