package com.ibm.bao.ceshell;

import java.util.Properties;

import com.ibm.bao.ceshell.connection.ConnectionStorageInfo;
import com.ibm.bao.ceshell.util.PropertyUtil;

public class CEConnectInfo {
	
	private String
		alias,
		connectUrl,
		domain,
		objectStore,
		user,
		pass = null,
		peConnectionPoint;

		
	private Properties sudoPropsList;
	private ConnectionStorageInfo connectionStorageInfo;
	
	/**
	 * Bean Ctor
	 */
	public CEConnectInfo() {
		super();
	}
	
	public CEConnectInfo(CEConnectInfo orig) {
		this();
		this.alias = orig.alias;
		this.connectUrl = orig.connectUrl;
		this.domain = orig.domain;
		this.objectStore = orig.objectStore;
		this.user = orig.user;
		this.pass = orig.pass;
		this.peConnectionPoint = orig.peConnectionPoint;
		this.sudoPropsList = orig.sudoPropsList;
		this.connectionStorageInfo = orig.connectionStorageInfo;
	}

	public CEConnectInfo(
				String alias,
				String connectUrl, 
				String domain, 
				String objectStore,
				String user,
				String pass) {
		super();
		this.connectUrl = connectUrl;
		this.domain = domain;
		this.objectStore = objectStore;
		this.user = user;
		this.pass = pass;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getConnectUrl() {
		return connectUrl;
	}

	public void setConnectUrl(String connectUrl) {
		this.connectUrl = connectUrl;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getObjectStore() {
		return objectStore;
	}

	public void setObjectStore(String objectStore) {
		this.objectStore = objectStore;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	public Properties getSudoList() throws Exception {
		return sudoPropsList;
	}
	
	
	
	public String getPeConnectionPoint() {
		return peConnectionPoint;
	}
	
	public void setPeConnectionPoint(String peConnectionPoint) {
		this.peConnectionPoint = peConnectionPoint;
	}

	public void setSudoList(Properties sudoPropsList) throws Exception {
		this.sudoPropsList = sudoPropsList;
	}
	
	public void init(ConnectionStorageInfo connectionStorageInfo, String alias) 
	throws Exception {
		this.connectionStorageInfo = connectionStorageInfo;
		PropertyUtil util = new PropertyUtil();
		
		util.loadProperties(this, connectionStorageInfo.getConnectionInfoFile());
		setSudoList(util.loadPropertiesFromFile(connectionStorageInfo.getSudoFile()));
		setAlias(alias);
		String pass = getSudoList().getProperty(getUser());
		setPass(pass);
	}
	
	public boolean readyToConnect() {
		if (user == null ||
			pass == null ||
			connectUrl == null ||
			objectStore == null) {
			return false;
		}
		return true;
	}
}
