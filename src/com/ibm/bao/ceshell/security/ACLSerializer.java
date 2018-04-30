/**
 * 
 */
package com.ibm.bao.ceshell.security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.AccessType;
import com.filenet.api.core.Factory;
import com.filenet.api.security.AccessPermission;

/**
 *  ACLSerializer
 *
 * @author GaryRegier
 * @date   Oct 9, 2010
 */
public class ACLSerializer {
	
	public static final String
		COMMENT_CHAR = ";",
		DELIMITER = "\t";
	
	public void storeAcls(File destFile, 
			List<AccessPermission> permissions, 
			String comment) throws Exception {
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(destFile));
			writer.append(appendHeader(comment));
			appendHeader(comment);
			writePermissions(writer, permissions);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					// no-op
				} finally {
					writer = null;
				}
			}
		}
	}
	
	public String permissionsToString(List<AccessPermission> permissions, 
			boolean includeHeader,
			String comment) throws Exception {
		StringBuffer buf = new StringBuffer();
		String results = null;
		Iterator<AccessPermission> iter = permissions.iterator();
		
		if (includeHeader) {
			buf.append(appendHeader(comment));
		}
		while (iter.hasNext()) {
			AccessPermission nextPermission = iter.next();
			ComparableAce comparableAce = new ComparableAce(nextPermission);
			String line = comparableAce.toString();
			buf.append(line).append("\n");
		}
		results = buf.toString();
		return results;
	}
	
	/**
	 * Write out direct and default permissions to a file
	 * 
	 * @param writer
	 * @param permissions
	 */
	private void writePermissions(BufferedWriter writer, List<AccessPermission> permissions) 
			throws Exception {
		Iterator<AccessPermission> iter = permissions.iterator();
		while (iter.hasNext()) {
			AccessPermission nextPermission = iter.next();
			ComparableAce comparableAce = new ComparableAce(nextPermission);
			String line = comparableAce.toString();
			writer.write(line);
			writer.write("\n");
		}
	}

	
	/**
	 * @param comment If the comment has newline characters in it, they must
	 *  have been properly turned into comments
	 */
	private String appendHeader(String comment)	throws Exception {
		StringBuffer header = new StringBuffer();
		header.append(";***********************************************************************************************\n");
		header.append(";" + comment);
		header.append("\n;");
		header.append("\n;\tsource: inter (0=DIRECT 1=DEFAULT 2=TEMPLATE 3=PARENT 255=SOURCE_PROXY) ");
		header.append("\n;\tAccessType:  1=allow 2=deny");
		header.append("\n;\tmask:  integer");
		header.append("\n;\tdepth: integer (-2=ALL_CHILDREN_BUT_NOT_THIS -1=ALL_CHILDREN  0=NO_INHERITANCE 1=CHILDREN)");
		header.append("\n;\tgrantee: String");
		header.append("\n;**********************************************************************************************\n");

		return header.toString();
	}
	
	public List<ComparableAce> loadComparableList(File srcFile) throws Exception {
		List<ComparableAce> comparableAceList = new ArrayList<ComparableAce>();
		BufferedReader reader = null;
		
		if (! srcFile.exists()) {
			throw new IllegalArgumentException("src file not found: " + 
					srcFile.toString());
		}
		try {
			reader = new BufferedReader(new FileReader(srcFile));
			String nextLine;
			
			while ((nextLine = reader.readLine()) != null) {
				ComparableAce comparableAce = null;
				nextLine.trim();
				if (nextLine.startsWith(COMMENT_CHAR)) {
					continue;
				}
				comparableAce = ComparableAce.createComparableAce(nextLine);
				comparableAceList.add(comparableAce);
			}
		} finally {
			if (reader!= null) {
				try {
					reader.close();
				} catch (IOException e) {
					// no-op
				} finally {
					reader = null;
				}
			}
		}
		return comparableAceList;
	}

	/**
	 * loadAcls
	 * AccessPermissions can be inherited from parents, proxies, or templates.
	 * These permissions can not be set directly on an ACL. 
	 * The ACL list returned only consists of direct or default ACLs
	 * 
	 * @param srcFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public AccessPermissionList loadAcls(File srcFile) throws Exception {
		AccessPermissionList apl = Factory.AccessPermission.createList();
		BufferedReader reader = null;
		
		if (! srcFile.exists()) {
			throw new IllegalArgumentException("src file not found: " + 
					srcFile.toString());
		}
		try {
			reader = new BufferedReader(new FileReader(srcFile));
			String nextLine;
			
			while ((nextLine = reader.readLine()) != null) {
				AccessPermission permission = null;
				nextLine.trim();
				if (nextLine.startsWith(COMMENT_CHAR)) {
					continue;
				}
				permission = createPermission(nextLine);
				if (permission != null) {
					apl.add(permission);
				}
			}
		} finally {
			if (reader!= null) {
				try {
					reader.close();
				} catch (IOException e) {
					// no-op
				} finally {
					reader = null;
				}
			}
		}
		return apl;
	}
	

	/**
	 * @param nextLine
	 * @return
	 */
	public AccessPermission createPermission(String nextLine) throws Exception {
		AccessPermission permission = null;
		ComparableAce comparableAce = ComparableAce.createComparableAce(nextLine);
		
		if (! comparableAce.isDirectSource()) {
			return null;
		}
		permission = createPermissionFromComparableAce(comparableAce);
		return permission;
	}

	/**
	 * @param accessType
	 * @param mask
	 * @param depth
	 * @param grantee
	 * @return
	 */
	public AccessPermission createPermissionFromComparableAce(ComparableAce comparableAce) {
		AccessPermission permission = Factory.AccessPermission.createInstance();
		
		if (! comparableAce.isDirectSource()) {
			return null;
		}
		
		{
			//Integer sourceAsInt = comparableAce.getSourceAsInt();
			// NOTE: source is not a settable property (or so it appears
			Integer accessTypeAsInt = comparableAce.getAccessTypeAsInt();
			Integer maskAsInt = comparableAce.getMaskAsInt();
			Integer depthAsInt = comparableAce.getDepthAsInt();
			String grantee = comparableAce.getGrantee();
			permission.set_AccessType(AccessType.getInstanceFromInt(accessTypeAsInt));
			permission.set_AccessMask(maskAsInt);
			permission.set_InheritableDepth(depthAsInt);
			permission.set_GranteeName(grantee);
		}
		return permission;
	}
}
