/**
 * 
 */
package com.ibm.bao.ceshell.constants;

/**
 *  ACL
 *
 * @author GaryRegier
 * @date   Oct 9, 2010
 */
public class ACL {
	public static final Integer
	D_NO_INHERITANCE = 0,
	D_CHILDREN = 1,
	D_ALL_CHILDREN = -1;

	public static final String
		NO_INHERITANCE = "NO_INHERITANCE",
		CHILDREN = "CHILDREN",
		ALL_CHILDREN = "ALL_CHILDREN";
	
	/**
	 * Access Types
	 */
	public static final String 
		ALLOW = "allow",
		DENY = "deny";
	public static final String[] ACCESS_TYPES_LABELS = new String[] {
		ALLOW,
		DENY
	};
	

}
