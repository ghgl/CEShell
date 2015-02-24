/**
 * 
 */
package com.ibm.bao.ceshell.connection;

import java.io.File;
import java.util.Properties;

import com.ibm.bao.ceshell.CEConnectInfo;
import com.ibm.bao.ceshell.util.PropertyUtil;

/**
 *  AliasInfo
 *
 * @author GaryRegier
 * @date   Nov 12, 2010
 */
public class ConnectionStorageInfo {
	
	private File connectionInfoFile;
	private File sudoFile;
	private Properties sudoProps;
	private CEConnectInfo connectionInfo;
	
	public ConnectionStorageInfo() {
		
	}
	
	public ConnectionStorageInfo(File connectionInfoFile, File sudoFile) {
		this.connectionInfoFile = connectionInfoFile;
		this.sudoFile = sudoFile;
	}
	
	
	public File getConnectionInfoFile() {
		return connectionInfoFile;
	}

	public void setConnectionInfoFile(File connectionInfoFile) {
		this.connectionInfoFile = connectionInfoFile;
	}

	public File getSudoFile() {
		return sudoFile;
	}

	public void setSudoFile(File sudoFile) {
		this.sudoFile = sudoFile;
	}

	public Properties getSudoProps() {
		return sudoProps;
	}

	public void setSudoProps(Properties sudoProps) {
		this.sudoProps = sudoProps;
	}
	
	public CEConnectInfo getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(CEConnectInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	public void initialize(String alias) throws Exception {
		PropertyUtil util = new PropertyUtil();
		connectionInfo = new CEConnectInfo();
		util.loadProperties(connectionInfo, connectionInfoFile);
		sudoProps = util.loadPropertiesFromFile(sudoFile);
		connectionInfo.setSudoList(sudoProps);
		connectionInfo.setAlias(alias);
		String defaultUser = connectionInfo.getUser();
		String pass = sudoProps.getProperty(defaultUser);
		connectionInfo.setPass(pass);
	}
}
