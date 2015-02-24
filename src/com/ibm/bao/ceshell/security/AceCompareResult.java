/**
 * 
 */
package com.ibm.bao.ceshell.security;

import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  AceCompareResult
 *
 * @author GaryRegier
 * @date   Jul 12, 2011
 */
public class AceCompareResult {
//	;********************************************************************
//	;.
//	;
//	;       source: inter (0=DIRECT 1=DEFAULT 2=TEMPLATE 3=PARENT 255=SOURCE_PROXY)
//	;       AccessType:  1=allow 2=deny
//	;       mask:  integer
//	;       depth: integer (-1=ALL_CHILDREN  0= NO_INHERITANCE 1=CHILDREN)
//	;       grantee: String
//	;********************************************************************
	
	
	public static final String 	
		SOURCE = "source",
		ACCESS_TYPE = "accessType",
		MASK = "mask",
		DEPTH = "depth",
		GRANTEE = "grantee";
	
	

	private boolean equivalent = false;
	private boolean equal = false;
	AceFieldCompare[] fieldCompare = new AceFieldCompare[5];
	
	
	
	public boolean isEquivalent() {
		return equivalent;
	}

	public boolean isEqual() {
		return equal;
	}

	public void compareAces(ComparableAce rhs, ComparableAce lhs) {
		
		boolean fieldEquals = false;
		boolean sourceEquivalent = false;
		
		fieldEquals = lhs.getSourceAsInt() == rhs.getSourceAsInt();
		{
			int lhsSource = lhs.getSourceAsInt();
			int rhsSource = rhs.getSourceAsInt();
			sourceEquivalent = lhs.areEquivalentSources(lhsSource, rhsSource);
		}
		fieldCompare[0] = new AceFieldCompare(SOURCE, fieldEquals, sourceEquivalent, "" + lhs.getSourceAsInt(), "" + rhs.getSourceAsInt());
		
		fieldEquals = lhs.getAccessTypeAsInt() == rhs.getAccessTypeAsInt();
		fieldCompare[1] = new AceFieldCompare(ACCESS_TYPE, fieldEquals, fieldEquals, "" + lhs.getAccessTypeAsInt(), "" + rhs.getAccessTypeAsInt());
		
		fieldEquals = lhs.getMaskAsInt() == rhs.getMaskAsInt();
		fieldCompare[2] = new AceFieldCompare(MASK, fieldEquals, fieldEquals, "" + lhs.getMaskAsInt(), "" + rhs.getMaskAsInt());
		
		fieldEquals = lhs.getDepthAsInt() == rhs.getDepthAsInt();
		fieldCompare[3] = new AceFieldCompare(DEPTH, fieldEquals, fieldEquals, "" + lhs.getDepthAsInt(), "" + rhs.getDepthAsInt());
		
		fieldEquals = lhs.getGrantee().equals(rhs.getGrantee());
		fieldCompare[4] = new AceFieldCompare(GRANTEE, fieldEquals, fieldEquals, lhs.getGrantee(), rhs.getGrantee());

		updateCompareFields();
	}
	
	/**
	 * 
	 */
	private void updateCompareFields() {
		if (fieldCompare[0].equal && 
				fieldCompare[1].equal &&
				fieldCompare[2].equal &&
				fieldCompare[3].equal && 
				fieldCompare[4].equal) {
			this.equal = true;
		}
		
		if (fieldCompare[0].equivalent && 
				fieldCompare[1].equivalent &&
				fieldCompare[2].equivalent &&
				fieldCompare[3].equivalent && 
				fieldCompare[4].equivalent) {
			this.equivalent = true;
		}
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(StringUtil.formatHeader(AceFieldCompare.AceColDefs, " ")).append("\n");
		for(int i = 0; i < fieldCompare.length; i++) {
			buf.append(fieldCompare[i].toString()).append("\n");
		}
		buf.append("Aces Equal:\t").append(this.equal).append("\n");
		buf.append("ACE equivalent\t").append(equivalent).append("\n");
		buf.append("------------------------------------------------------");
		return buf.toString();
	}
}

