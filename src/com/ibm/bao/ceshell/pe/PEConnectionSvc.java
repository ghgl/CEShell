/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import com.ibm.bao.ceshell.CEConnectInfo;
import com.ibm.bao.ceshell.connection.CECryptoManager;

import filenet.vw.api.VWSession;

/**
 *  PEConnectionSvc
 *
 * @author GaryRegier
 * @date   Jun 25, 2011
 */
public class PEConnectionSvc {
	private CEConnectInfo connectionInfo;
	private VWSession peSession = null;
	private CECryptoManager cryptoManager = null;
	
	public PEConnectionSvc() {
		
	}
	
	public void setCryptoManager(CECryptoManager cryptoManager) {
		this.cryptoManager = cryptoManager;
	}



	/**
	 * VWSession is opened lazily with the request to connect.
	 * Here we just change the connector information.
	 * @param connectInfo
	 */
	public void connect(CEConnectInfo connectInfo) {
		if (peSession != null) {
			logOff(peSession);
			peSession = null;
		}
		connectionInfo = connectInfo;
	}
	
	/**
	 * A tad weak, but make our best guess whether it's supported.
	 * @return
	 */
	public boolean peSupported() {
		String peConnectionPt = connectionInfo.getPeConnectionPoint();
		return (peConnectionPt != null &&
					peConnectionPt.length() > 0);
	}
	
	/**
	 * If the user has not logged on, then log them off.
	 * @return
	 * @throws Exception
	 */
	public VWSession getPEConnection() throws Exception {
		
		if (connectionInfo == null) {
			throw new IllegalStateException("No connection information");
		}
		if (peSession == null) {
			initSession();
		}
		
		return this.peSession;
	}
	/**
	 * 
	 */
	private void initSession() throws Exception {
		if (connectionInfo.getPeConnectionPoint() == null) {
			throw new IllegalStateException("PE Connection point not configured");
		}
		peSession = new VWSession();
		peSession.setBootstrapCEURI(connectionInfo.getConnectUrl());
		peSession.logon(connectionInfo.getUser(), 
				cryptoManager.getClearText(connectionInfo.getPass()),
				connectionInfo.getPeConnectionPoint()); 
	}

	/**
	 * @param peSession2
	 */
	private void logOff(VWSession peSession) {
		try {
			peSession.logoff();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
