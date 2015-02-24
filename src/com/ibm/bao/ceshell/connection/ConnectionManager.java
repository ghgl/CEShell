/**
 * 
 */
package com.ibm.bao.ceshell.connection;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ibm.bao.ceshell.util.PropertyUtil;

/**
 *  ConnectionManager
 *
 * @author GaryRegier
 * @date   Nov 12, 2010
 */
public class ConnectionManager {
	
	public static final String 
		CESHELL_HOME = "CESHELL_HOME";
	
	private File ceHomeDir = null;
	private Map<String,ConnectionStorageInfo> connectionStorageItems = 
			new TreeMap<String,ConnectionStorageInfo>();
	private CECryptoManager cryptoMgr;
	PropertyUtil propUtil = new PropertyUtil();
	
	
	public ConnectionManager(){
		String ceShellHome = System.getenv("CESHELL_HOME");
		File homeDir = new File(ceShellHome);
		if (homeDir.exists() &&
				homeDir.isDirectory() &&
						homeDir.canRead()) {
			ceHomeDir = homeDir;
			cryptoMgr = new CECryptoManager(ceHomeDir);
		}
	}
	
	public CECryptoManager getCECryptoManager() {
		return cryptoMgr;
	}

	public void initConnectionStorageInfo() throws Exception {
		this.initConnectionStorageInfo(ceHomeDir);
	}

	public void initConnectionStorageInfo(File parentFolder) throws Exception {
		String[] connectionAliasNames = parentFolder.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.contains(".connection.properties"));
			}
		});
		
		for(int i = 0; i < connectionAliasNames.length; i++) {
			ConnectionStorageInfo connectionStorageInfo = null;
			try {
				String nextFilename = connectionAliasNames[i];
				File aliasFile = new File(parentFolder, nextFilename);
				String alias = nextFilename.substring(0, 
						nextFilename.length() - ".connection.properties".length());
				String sudoFilename = alias + ".sudo.properties";
				
				File sudoFile = new File(parentFolder, sudoFilename);

				connectionStorageInfo = new ConnectionStorageInfo(aliasFile, sudoFile);
				connectionStorageInfo.initialize(alias);
				connectionStorageItems.put(alias, connectionStorageInfo);
			} catch (Exception e) {
				// in case of a exception, we continue
				e.printStackTrace();
			}
		}
	}
	
	public List<String> getConnectionAliasNames() {
		List<String> aliases = new ArrayList<String>();
		Iterator<String> iter = connectionStorageItems.keySet().iterator();
		while (iter.hasNext()) {
			aliases.add(iter.next());
		}
		return aliases;
	}
	
	public void addConnectionAlias(String aliasName, ConnectionStorageInfo alias) {
		connectionStorageItems.put(aliasName, alias);
	}
	
	public ConnectionStorageInfo getConnectionStorageInfo(String aliasName) {
		return connectionStorageItems.get(aliasName);
	}
}
