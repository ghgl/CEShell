package com.ibm.bao.ceshell;


public class BaseRequestInfo {
	
	private String cmdLine;
	private String[] args;
	private String cmdId;
	private BaseResponse response;
	
	/**
	 * Bean ctor
	 */
	public BaseRequestInfo() {
		super();
	}
	
	/**
	 * Copy ctor
	 * @return
	 */
	public BaseRequestInfo(BaseRequestInfo originalRequestInfo) {
		this();
		this.setCmdLine(originalRequestInfo.getCmdLine());
		this.setCmdId(originalRequestInfo.getCmdId());
		this.setArgs(originalRequestInfo.getArgs());
	}
	
	public String getCmdId() {
		return cmdId;
	}

	public void setCmdId(String cmdId) {
		this.cmdId = cmdId;
	}

	public String getCmdLine() {
		return cmdLine;
	}
	public void setCmdLine(String cmdLine) {
		this.cmdLine = cmdLine;
	}
	public BaseResponse getResponse() {
		return response;
	}
	public void setResponse(BaseResponse response) {
		this.response = response;
	}
	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}
	
}
