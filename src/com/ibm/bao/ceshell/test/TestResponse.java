/**
 * 
 */
package com.ibm.bao.ceshell.test;

import java.io.PrintStream;

import com.ibm.bao.ceshell.BaseResponse;

/**
 *  TestResponse
 *
 * @author GaryRegier
 * @date   Apr 28, 2011
 */
public class TestResponse extends BaseResponse {
	
	private BaseResponse realResponse;
	private boolean acutalOutcome = true;
	private String errMsg = null;
	
	public TestResponse() {
		
	}
	
	public TestResponse(BaseResponse parent) {
		realResponse = parent;
	}
	
	
	
	public BaseResponse getRealResponse() {
		return realResponse;
	}

	public void setRealResponse(BaseResponse realResponse) {
		this.realResponse = realResponse;
	}

	public boolean getAcutalOutcome() {
		return acutalOutcome;
	}

	public void setAcutalOutcome(boolean acutalOutcome) {
		this.acutalOutcome = acutalOutcome;
	}
	
	@Override
	public void printErr(String msg) {
		acutalOutcome = false;
		errMsg = msg;
//		realResponse.printErr(msg);
	}

	@Override
	public void logErr(String msg) {
		acutalOutcome = false;
		errMsg = msg;
		realResponse.logErr(msg);
	}

	public boolean isFailed() {
		return acutalOutcome;
	}

	public void setFailed(boolean failed) {
		this.acutalOutcome = failed;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
	public PrintStream getOut() {
		return realResponse.getOut();
	}

	public void setOut(PrintStream out) {
		realResponse.setOut(out);
	}

	public PrintStream getErr() {
		return realResponse.getErr();
	}

	public void setErr(PrintStream err) {
		realResponse.setErr(err);
	}
	
	public TestResponse printOut(String msg) {
//		realResponse.printOut(msg);
		return this;
	}
	
	public void logInfo(String msg) {
		// realResponse.logInfo(msg);
	}
	
	public void logWarn(String msg) {
		// realResponse.logWarn(msg);
	}
	
	public void logErr(Throwable e) {
		// realResponse.logErr(e);
	}
	
	public void logFatal(String msg) {
		realResponse.logFatal(msg);
	}
	public void logDebug(String msg) {
		realResponse.logDebug(msg);
	}

}
