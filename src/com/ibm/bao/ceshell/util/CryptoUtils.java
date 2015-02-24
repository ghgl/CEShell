/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Based on code from
 * 
 * @link http://www.rgagnon.com/javadetails/java-0400.html CryptoUtils
 * 
 * @author GaryRegier
 * @date Oct 30, 2010
 */
public class CryptoUtils {

	public static final String AES = "AES";
	

	/**
	 * encrypt a value and generate a keyfile if the keyfile is not found then a
	 * new one is created
	 * 
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static String encrypt(String value, File keyFile)
			throws GeneralSecurityException, IOException {
		if (!keyFile.exists()) {
			KeyGenerator keyGen = KeyGenerator.getInstance(CryptoUtils.AES);
			keyGen.init(128);
			SecretKey sk = keyGen.generateKey();
			FileWriter fw = new FileWriter(keyFile);
			fw.write(byteArrayToHexString(sk.getEncoded()));
			fw.flush();
			fw.close();
		}

		SecretKeySpec sks = getSecretKeySpec(keyFile);
		Cipher cipher = Cipher.getInstance(CryptoUtils.AES);
		cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
		byte[] encrypted = cipher.doFinal(value.getBytes());
		return byteArrayToHexString(encrypted);
	}
	
	
	/**
	 * Convert all the passwords in a properties file
	 * @param propsFile
	 * @param keyFile File. If the keyFile does not exist, it is created
	 * @return Keyfile used to encrypt
	 */
	public void encryptPropertyValues(File propsFile, File keyFile) 
	throws Exception {
		Properties props = null;
		PropertyUtil util = new PropertyUtil();
		
		props = util.loadPropertiesFromFile(propsFile);
		for(Enumeration<Object> iter = props.keys(); 
				iter.hasMoreElements(); iter.nextElement()) {
			String key = iter.nextElement().toString();
			String clearText = props.getProperty(key);
			String encryptValue = CryptoUtils.encrypt(clearText, keyFile);
			props.setProperty(key, encryptValue);
		}
	}

	/**
	 * decrypt a value
	 * 
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static String decrypt(String message, File keyFile)
	throws GeneralSecurityException, IOException {
		SecretKeySpec sks = getSecretKeySpec(keyFile);
		Cipher cipher = Cipher.getInstance(CryptoUtils.AES);
		cipher.init(Cipher.DECRYPT_MODE, sks);
		byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
		return new String(decrypted);
	}
	
	/**
	 * Verify that a password matches the cleartext entry
	 * @param key
	 * @param clearText
	 * @return
	 */
	public static boolean verify(String clearText, String cryptText, File keyFile) 
	throws GeneralSecurityException, IOException {
		return (CryptoUtils.encrypt(clearText, keyFile).equals(cryptText));
	}

	private static SecretKeySpec getSecretKeySpec(File keyFile)
	throws NoSuchAlgorithmException, IOException {
		byte[] key = readKeyFile(keyFile);
		SecretKeySpec sks = new SecretKeySpec(key, CryptoUtils.AES);
		return sks;
	}

	private static byte[] readKeyFile(File keyFile)
			throws FileNotFoundException {
		Scanner scanner = new Scanner(keyFile).useDelimiter("\\Z");
		String keyValue = scanner.next();
		scanner.close();
		return hexStringToByteArray(keyValue);
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}
	

}
