package com;
import com.filenet.api.constants.CheckinType;


/**
 * 
 */

/**
 *  UpdateInfo
 *
 * @author regier
 * @date   Mar 21, 2015
 */
public class UpdateDTO {
	
	public static final String MIME_TYPE_DEFAULT = "application/octet-stream";
	public static final String FILE_NAME_DEFAULT = "data.txt";
	public static final String DATA_DEFAULT = "";
	
	
	private String  data;
	private CheckinType checkinType = CheckinType.MAJOR_VERSION;
	private String mimeType = MIME_TYPE_DEFAULT;
	private String retrievalFilename;
	
	
	public UpdateDTO() {
		super();
		this.retrievalFilename = FILE_NAME_DEFAULT;
		this.data = DATA_DEFAULT;
		this.mimeType = MIME_TYPE_DEFAULT;
	}
	
	public UpdateDTO(String data, String retrievalFilename) {
		this();
		this.data = data;
		this.retrievalFilename = retrievalFilename;
	}
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	public CheckinType getCheckinType() {
		return checkinType;
	}

	public void setCheckinType(CheckinType checkinType) {
		this.checkinType = checkinType;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getRetrievalFilename() {
		return retrievalFilename;
	}

	public void setRetrievalFilename(String retrievalFilename) {
		this.retrievalFilename = retrievalFilename;
	}
	
	
}
