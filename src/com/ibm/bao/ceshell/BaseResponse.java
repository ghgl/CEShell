package com.ibm.bao.ceshell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class BaseResponse {
	
	private PrintStream beforeRedirect = null;
	private PrintStream out;
	private PrintStream err;
	
	
	public BaseResponse() {
		super();
	}

	public BaseResponse(PrintStream out, PrintStream err) {
		this.out = out;
		this.err = err;
	}

	public PrintStream getOut() {
		return out;
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}

	public PrintStream getErr() {
		return err;
	}

	public void setErr(PrintStream err) {
		this.err = err;
	}
	
	public void printErr(String msg) {
		this.err.println(msg);
	}
	
	public BaseResponse printOut(String msg) {
		out.println(msg);
		return this;
	}
	
	public void logInfo(String msg) {
		out.println(msg);
	}
	
	public void logWarn(String msg) {
		out.println(msg);
	}
	
	public void logErr(String msg) {
		err.println(msg);
	}
	public void logErr(Throwable e) {
		err.println(e.getMessage());
	}
	
	public void logFatal(String msg) {
		err.println(msg);
	}
	public void logDebug(String msg) {
		out.println(msg);
	}
	
	/**
	 * @param outFile 
	 * @return
	 * @throws FileNotFoundException 
	 */
	public void redirectOutputStream(File outFile) throws IllegalStateException {
		if (this.beforeRedirect != null) {
			throw new IllegalStateException("Output already redirected");
		}
		
		this.beforeRedirect = this.out;
		try {
			PrintStream newOut = new PrintStream(outFile);
			this.out = newOut;
		} catch (Exception e) {
			// restore to original state in case redirect failed  
			//  -- beforeRedirect is null
			//  -- out = original out (stdout)
			this.out = this.beforeRedirect;
			this.beforeRedirect = null;
		}
	}
	
	/**
	 * @param currentOutputStream
	 */
	public void restoreOutputStream() {
		if (this.beforeRedirect == null) {
			throw new IllegalStateException("redirected output is null");
		}
		
		try {
			this.out.close();
			this.out = null;
		} catch (Exception e) {
			getErr().print(e.getMessage());
		} 
		
		this.out = this.beforeRedirect;
		this.beforeRedirect = null;
	}
}
