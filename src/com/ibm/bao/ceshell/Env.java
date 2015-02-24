/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 *  Set
 *
 * @author GaryRegier
 * @date   Sep 24, 2010
 */
public class Env {
	
	public static final String 
		CESHELL_HOME = "CESHELL_HOME",
		EXT_DIR_NAME = "ceshell-exts";
	
	private Integer maxrows = new Integer(-1);
	private File ceHomeDir;
	private File ceExtsDir;
	
	public Env() throws Exception {
		initCEShellHome();
	}

	public File getCeHomeDir() {
		return ceHomeDir;
	}

	public void setCeHomeDir(File ceHomeDir) {
		this.ceHomeDir = ceHomeDir;
	}

	public File getCeExtsDir() {
		return ceExtsDir;
	}

	public void setCeExtsDir(File ceExtsDir) {
		this.ceExtsDir = ceExtsDir;
	}

	public Integer getMaxrows() {
		return maxrows;
	}

	public void setMaxrows(Integer maxrows) {
		this.maxrows = maxrows;
	}
	
	void initCEShellHome() throws Exception {
		//debugEnv();
		String ceShellHome = System.getenv(CESHELL_HOME);
		ceHomeDir = new File(ceShellHome);		
		if (! ceHomeDir.exists() &&
				ceHomeDir.isDirectory() &&
				ceHomeDir.canRead()) {
			
			throw new Exception("CEHomeDir does not exist");
		}
		ceExtsDir = new File(ceHomeDir,EXT_DIR_NAME);
		if (! ceExtsDir.exists() &&
				ceExtsDir.isDirectory() &&
				ceExtsDir.canRead()) {
			
			System.out.println("No ceExtsDir found at " + ceExtsDir.toString());
		}
	}

	/**
	 * 
	 */
	private void debugEnv() {
		Set<String> keys = System.getenv().keySet();
		for (String key : keys) {
			String value = System.getenv(key);
			System.out.println(key + "\t" + value);
		}
		System.getenv(CESHELL_HOME);
		
	}

}
