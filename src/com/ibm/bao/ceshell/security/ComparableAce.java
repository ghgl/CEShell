/**
 * 
 */
package com.ibm.bao.ceshell.security;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.filenet.api.constants.PermissionSource;
import com.filenet.api.security.AccessPermission;

/**
 *  ComparableAce
 *  
 *  In the standard AccessPermission class, the source property
 *  is not settable. If you want to compare AccessPermissions from
 *  storage, it is not possible. 
 *
 * @author GaryRegier
 * @date   Apr 7, 2011
 */
public class ComparableAce implements Comparable<ComparableAce> {
	public static final String
		COMMENT_CHAR = ";",
		DELIMITER = "\t";
		
	private int sourceAsInt;
	private int maskAsInt;
	private int depthAsInt;
	private int accessTypeAsInt;
	private String grantee;
	
	public static ComparableAce createComparableAce(String line) {
		ArrayList<String> tokens = parseLine(line);
		String source;
		String accessType;
		String mask;
		String depth;
		String grantee;
		
		if (tokens.size() != 5) {
			String msg = "Line with wrong number of tokens: " + line;
			throw new IllegalArgumentException(msg);
		}
		source = tokens.get(0);
		accessType = tokens.get(1);
		mask = tokens.get(2);
		depth = tokens.get(3);
		grantee = tokens.get(4);
		ComparableAce comparableAce = new ComparableAce(source, accessType, mask, depth, grantee);
		return comparableAce;
	}
	
	public ComparableAce(AccessPermission accessPermission) {
		this(
			accessPermission.get_PermissionSource().getValue(),
			accessPermission.get_AccessType().getValue(),
			accessPermission.get_AccessMask().intValue(),
			accessPermission.get_InheritableDepth().intValue(),
			accessPermission.get_GranteeName());
	}
	
	public ComparableAce(
			String source, 
			String accessType, 
			String mask,
			String depth, 
			String grantee) {
		this(
			Integer.parseInt(source),
			Integer.parseInt(accessType),
			Integer.parseInt(mask),
			Integer.parseInt(depth),
			grantee);
	}
	
	public ComparableAce(
			Integer sourceAsInt, 
			Integer accessTypeAsInt,
			Integer maskAsInt,
			Integer depthAsInt, 
			String grantee) {
		super();
		this.sourceAsInt = sourceAsInt;
		this.maskAsInt = maskAsInt;
		this.depthAsInt = depthAsInt;
		this.accessTypeAsInt = accessTypeAsInt;
		this.grantee = grantee;
	}
	

	public int getSourceAsInt() {
		return sourceAsInt;
	}

	public void setSourceAsInt(int sourceAsInt) {
		this.sourceAsInt = sourceAsInt;
	}

	public int getMaskAsInt() {
		return maskAsInt;
	}

	public void setMaskAsInt(int maskAsInt) {
		this.maskAsInt = maskAsInt;
	}

	public int getDepthAsInt() {
		return depthAsInt;
	}

	public void setDepthAsInt(int depthAsInt) {
		this.depthAsInt = depthAsInt;
	}

	public int getAccessTypeAsInt() {
		return accessTypeAsInt;
	}

	public void setAccessTypeAsInt(int accessTypeAsInt) {
		this.accessTypeAsInt = accessTypeAsInt;
	}

	public String getGrantee() {
		return grantee;
	}

	public void setGrantee(String grantee) {
		this.grantee = grantee;
	}

	public static String getCOMMENT_CHAR() {
		return COMMENT_CHAR;
	}

	public static String getDELIMITER() {
		return DELIMITER;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ComparableAce ca) {
		return this.toComparableString().compareTo(ca.toComparableString());
	}
	
	
	public String toString() {
		return toString("\t");
	}
	
	/**
	 * @param sourceAsInt
	 * @return
	 */
	public boolean isDirectSource() {
		return ( this.sourceAsInt == PermissionSource.SOURCE_DEFAULT_AS_INT || 
					this.sourceAsInt == PermissionSource.SOURCE_DIRECT_AS_INT);
	}
	
	public String toString(String delimiter) {
		/*
		 * source = tokens.get(0);
		accessType = tokens.get(1);
		mask = tokens.get(2);
		depth = tokens.get(3);
		grantee = tokens.get(4);
		 */
		
		StringBuffer buf = new StringBuffer();
		buf.append(sourceAsInt).append(delimiter);
		buf.append(this.accessTypeAsInt).append(delimiter);
		buf.append(this.maskAsInt).append(delimiter);
		buf.append(depthAsInt).append(delimiter);
		buf.append(grantee);
		
		return buf.toString();
	}
	
	/**
	 * When comparing ACEs, it's better to compare the 
	 * grantee first so that two ordered lists will be
	 * in the same order as the grantees;
	 * @return
	 */
	String toComparableString() {
		StringBuffer buf = new StringBuffer();
//		buf.append(grantee);
//		buf.append(sourceAsInt);
//		buf.append(this.accessTypeAsInt);
//		buf.append(this.maskAsInt);
//		buf.append(depthAsInt);
		
		buf.append(grantee);
		buf.append(this.accessTypeAsInt);
		buf.append(depthAsInt);
		buf.append(this.maskAsInt);
		buf.append(sourceAsInt);
	
		return buf.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + accessTypeAsInt;
		result = prime * result + depthAsInt;
		result = prime * result + ((grantee == null) ? 0 : grantee.hashCode());
		result = prime * result + maskAsInt;
		result = prime * result + sourceAsInt;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		//return this.toString().toLowerCase().equals(obj.toString().toLowerCase());
		ComparableAce ceObj = (ComparableAce) obj;
		if ( this.grantee.toLowerCase().equals(ceObj.grantee.toLowerCase()) &&
				this.accessTypeAsInt == ceObj.accessTypeAsInt &&
					this.maskAsInt == ceObj.maskAsInt &&
					this.depthAsInt == ceObj.depthAsInt &&
					areEquivalentSources(this.sourceAsInt, ceObj.sourceAsInt)) {
			return true;
		}else {
			return false;
		}			
	}

	/**
	 * @param sourceAsInt2
	 * @param maskAsInt2
	 * @return
	 */
	public boolean areEquivalentSources(int src1, int src2) {
		if (src1 == src2) {
			return true;
		}
		if ( (src1 <= 1) && 
				(src2 <= 1)) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param nextLine
	 * @return
	 */
	private static ArrayList<String> parseLine(String nextLine) {
		
		StringTokenizer tokenizer = new StringTokenizer(nextLine, DELIMITER);
		ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken();
			tokens.add(nextToken);
		}
		return tokens;
	}
}
