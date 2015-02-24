/**
 * 
 */
package com.ibm.bao.ceshell.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 *  BulkLoadLogger
 *
 * @author regier
 * @date   Sep 9, 2011
 */
public class BulkLoadLogger {
	private int successCnt = 0;
	private int errCnt = 0;
	private String logFileName;
	private String errFileName;
	private BufferedWriter err;
	private BufferedWriter log;
	
	public BulkLoadLogger(File logDir, String batchName) throws Exception {
		String errFileName = batchName + ".err";
		String logFileName = batchName + ".log";
		if (! logDir.exists()) {
			throw new IllegalArgumentException("Log directory does not exist: " + logDir.toString());
		}
		File errFile = new File(logDir, errFileName);
		File logFile = new File(logDir, logFileName);
		this.logFileName = logFile.toString();
		this.errFileName = errFile.toString();
		this.err = new BufferedWriter(new FileWriter(errFile));
		this.log = new BufferedWriter(new FileWriter(logFile));
	}
	
	
	public String getLogFileName() {
		return logFileName;
	}



	public String getErrFileName() {
		return errFileName;
	}



	public void logSuccess(String msg) throws Exception {
		log.write(msg);
		log.newLine();
		successCnt++;
	}
	
	public void logErr(String msg) throws Exception {
		err.write(msg);
		err.newLine();
		errCnt++;
	}
	
	
	public int getSuccessCnt() {
		return successCnt;
	}

	public int getErrCnt() {
		return errCnt;
	}

	public void close() throws Exception {
		if (err != null) {
			try {
				err.close();
			} catch(Exception e) {
				// no-op
			}
		}
		err = null;
		if (log != null) {
			try {
				log.close();
			} catch (Exception e) {
				// no-op
			}
		}
		log = null;
		
	}
}
