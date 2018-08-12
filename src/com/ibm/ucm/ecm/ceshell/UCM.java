package com.ibm.ucm.ecm.ceshell;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.bao.ceshell.CEShell;

public class UCM {
	
	private CEShell ceShell = null;
	
	public static final class PropertyNames {
		public static final String
		CmAcmCaseState = "CmAcmCaseState",
		REC_OWNRSHP_ORG = "REC_OWNRSHP_ORG",
		UCM_REC_STUS = "UCM_REC_STUS",
		UCM_REC_SUB_STUS = "UCM_REC_SUB_STUS",
		UCM_REFRD_UPIC = "UCM_REFRD_UPIC",
		UCM_Security_Proxy = "UCM_Security_Proxy";
	}
	
	UCM() {
		
	}
	
	public UCM(CEShell ceShell) {
		this();
		this.ceShell = ceShell;
	}
	
	/** 
	 * verify the path passed in matches patter CSE-180809-00005
	 * @param pathUri
	 * @return
	 */
	public boolean isCaseId(String pathUri) {
		
		if (pathUri.length() != 16) {
			return false;
		}
		
		Pattern pattern = Pattern.compile("[A-Z]{3}-[0-9]{6}-[0-9]{5}");
		Matcher matcher = pattern.matcher(pathUri);
		
		return matcher.find();
	}

}
