/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.File;
import java.util.List;

import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ReservationType;


/**
 * 
 */

/**
 *  UpdateInfo
 *
 * @author regier
 * @date   Mar 21, 2015
 */
public class DocUpdateDTO extends DocUtilDTO {
	
	public static final String FILE_NAME_DEFAULT = "data.bin";
	public static final String DATA_DEFAULT = "";
	public static final CheckinType CHECKIN_TYPE_DEFAULT = CheckinType.MAJOR_VERSION;
	
	private String docUri;
	private CheckinType checkinType = CHECKIN_TYPE_DEFAULT;
	private String retrievalFilename = null;
	private boolean checkInOnUpdate = true;
	private ReservationType reservationType;
	
	public DocUpdateDTO() {
		super();
		this.retrievalFilename = FILE_NAME_DEFAULT;
	}
	
	public DocUpdateDTO(String docUri, List<File> srcFiles) throws Exception {
		super(srcFiles);
		
	}
	
	public DocUpdateDTO(String docUri, List<File> srcFiles, File docProps) throws Exception {
		super(srcFiles, docProps);
		setDocUri(docUri);
	}
	
	public DocUpdateDTO(String docUri, List<File> srcFiles, java.util.Properties docProps) throws Exception {
		super(srcFiles, docProps);
		setDocUri(docUri);
		
	}
	
	
	public String getDocUri() {
		return docUri;
	}

	public void setDocUri(String docUri) {
		this.docUri = docUri;
	}

	public CheckinType getCheckinType() {
		return checkinType;
	}

	public void setCheckinType(CheckinType checkinType) {
		this.checkinType = checkinType;
	}
	
	public boolean isCheckInOnUpdate() {
		return checkInOnUpdate;
	}

	public void setCheckInOnUpdate(boolean checkInOnUpdate) {
		this.checkInOnUpdate = checkInOnUpdate;
	}
	
	public ReservationType getReservationType() {
		if (reservationType != null) {
			return reservationType;
		} else {
			return ReservationType.OBJECT_STORE_DEFAULT;
		}
	}
	public void setReservationType(ReservationType reservationType) {
		this.reservationType = reservationType;
	}

	/**
	 * Only return the default name if the retrieval name has not been
	 * explicity set and there are no src files. 
	 * @return
	 */
	public String getRetrievalFilename() {
		if (retrievalFilename != null) {
			return retrievalFilename;
		}
		
		if (! srcFiles.isEmpty()) {
			return srcFiles.get(0).getName();
		}
		
		return FILE_NAME_DEFAULT;
	}

	public void setRetrievalFilename(String retrievalFilename) {
		this.retrievalFilename = retrievalFilename;
	}
	
	
}
