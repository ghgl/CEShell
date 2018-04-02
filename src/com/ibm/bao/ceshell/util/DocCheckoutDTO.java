/**
 * 
 */
package com.ibm.bao.ceshell.util;

import com.filenet.api.constants.ReservationType;

/**
 *  DocCheckoutDTO
 *
 * @author regier
 * @date   Mar 22, 2015
 */
public class DocCheckoutDTO {
	
	public static final String
		ACTION_CHECKOUT = "checkout",
		ACTION_CANCEL = "cancel";

	
	
	public static final String[] ACTIONS = {
		ACTION_CHECKOUT,
		ACTION_CANCEL
	};

	
	private ReservationType reservationType;
	private String docUri;
	
	
	public DocCheckoutDTO() {
		
	}
	
	public DocCheckoutDTO(String docUri) {
		this.docUri = docUri;
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
	public String getDocUri() {
		return docUri;
	}
	public void setDocUri(String docUri) {
		this.docUri = docUri;
	}
	
	

}
