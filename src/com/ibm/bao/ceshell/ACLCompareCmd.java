/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.AccessType;
import com.filenet.api.constants.PermissionSource;
import com.filenet.api.security.AccessPermission;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.security.ACLSerializer;
import com.ibm.bao.ceshell.security.AceCompareResult;
import com.ibm.bao.ceshell.security.ComparableAce;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  ACLCompareCmd
 *
 * @author GaryRegier
 * @date   Apr 7, 2011
 */
public class ACLCompareCmd extends AbsAclCmd {
	
	public static final String 
		CMD = "aclcomp", 
		CMD_DESC = "compare acl entries and assigned to entites in CE against an exported ACL list",
		HELP_TEXT = "Usage:" +
		 "\n\taclcomp -srcfile E:/data/bhsecpol/BHDC_Billing_SecPol.properties /glrtest/foo.doc" +
		 "\n\t apply exported entries to object /glrtst/foo.doc";

public static final String
	// URI_ARG in superclass
	SRCFILE_OPT = "file";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam parentTypeParam = (StringParam) cl.getOption(ACL_PARENT_TYPE_OPT);
		FileParam srcFileParam = (FileParam) cl.getOption(SRCFILE_OPT);
		StringParam pathUriParam = (StringParam) cl.getArg(URI_ARG);
		File srcFile = null;
		String pathUri = null;
		String parentType = "d"; // default to doc

		if (pathUriParam.isSet()) {
			pathUri = pathUriParam.getValue();
		}
		if (parentTypeParam.isSet()) {
			parentType = parentTypeParam.getValue()
					.toLowerCase()
					.trim();
		}
		srcFile = srcFileParam.getValue();
		return aclCompare(parentType, pathUri, srcFile);
	}
	
	public boolean aclCompare(
		String parentType, 
		String pathUri,
		File srcFile) 	throws Exception {
		AccessPermissionList objAccessPermissionList = null;
		List<ComparableAce> comparableAccessPermissionList = null;
		String description = null;
		boolean matched = false;
		
		comparableAccessPermissionList = loadComparableList(srcFile);
		if (ACLTYPE.DOCUMENT.equals(parentType)) {
			objAccessPermissionList = fetchDocumentPermissionList(pathUri);
			description = "Document permissions";
		} else if (ACLTYPE.FOLDER.equals(parentType)) {
			objAccessPermissionList = fetchFolderPermissionList(pathUri);
			description = "folder permissions";
		} else if (ACLTYPE.CLASS_DEFINITION.equals(parentType)) {
			objAccessPermissionList = fetchClassPermissionList(pathUri);
			description = "class definition";
		} else if (ACLTYPE.DOMAIN.equals(parentType)) {
			objAccessPermissionList = fetchDomainPermissionList(pathUri);
			description = "Domain permissions";
		} else if (ACLTYPE.CLASS_DEFINITION_DEFAULT.equals(parentType)) {
			objAccessPermissionList = fetchDefaultClassPermissionList(pathUri);
			description = "Default class definition permissions";
		} else if (ACLTYPE.OBJECT_STORE.equals(parentType)){
			objAccessPermissionList = fetchObjectStorePermissionList(pathUri);
			description = "Object store permissions";
		}
		
		if (objAccessPermissionList == null) {
			throw new IllegalArgumentException("object not found");
		}	
		String errorMsg = compareAcls(objAccessPermissionList, comparableAccessPermissionList, description);
		StringBuffer msg = new StringBuffer();
		if (! "".equals(errorMsg)) {
			msg.append("ACL does not match (");
		} else {
			matched = true;
			msg.append("ACL match (");
		}
		msg.append(parentType).append("=").append(pathUri)
			.append(" to ").append(srcFile.toString())
			.append(" - ");
		msg.append(errorMsg);
		
		if (! matched) {
			getResponse().printErr(msg.toString());
			return false;
		} else {
			getResponse().printOut(msg.toString());
			return true;
		}

	}	
	
	/**
	 * compareAcls
	 * When deserializing acls from disk, you can not create 
	 * an AccessPermission and set it's source. In order to look at all
	 * permissions, we:
	 * <ul>
	 * 	<li> Convert the AccessPermissionList to a list of ComparableAce items
	 *  <li> Load the serialized acl list as a list of ComparableAce items.
	 *  <li> Then, comparre both lists to see if they are equivalent.
	 * </ul>
	 * @param objAccessPermissionList
	 * @param comparableAccessPermissionList
	 * @param description
	 */
	private String compareAcls(
			AccessPermissionList objList,
			List<ComparableAce> compList,
			String description) {
		List<ComparableAce> objComparableAceList = createComparableList(objList);
		if (objList.size() != 
			compList.size()) {
			return "Different number of ACE entries";
		}
		TreeSet<ComparableAce> objOrderedSet = 
			new TreeSet<ComparableAce>(new Comparator<ComparableAce>() {

			public int compare(ComparableAce lhs, ComparableAce rhs) {
				return lhs.compareTo(rhs);
			}
			
		});
		objOrderedSet.addAll(objComparableAceList);
		
		TreeSet<ComparableAce> compSet = 
			new TreeSet<ComparableAce>(new Comparator<ComparableAce>() {

			public int compare(ComparableAce lhs, ComparableAce rhs) {
				return lhs.compareTo(rhs);
			}
			
		});
		compSet.addAll(compList);
		
		Iterator<ComparableAce> objIter = objOrderedSet.iterator();
		Iterator<ComparableAce> compIter = compSet.iterator();
		
		for (int i = 0; i < objOrderedSet.size() ; i++) {
			ComparableAce lhs = objIter.next();
			ComparableAce rhs = compIter.next();
			if (! lhs.equals(rhs)) {
				AceCompareResult acr = new AceCompareResult();
				acr.compareAces(lhs, rhs);
				return "ACE does not match\n" + 
				acr.toString();
			}
		}
		return "";
	}
	
//	/**
//	 * @param objOrderedSet
//	 * @param compSet
//	 */
//	private void debug(TreeSet<ComparableAce> objOrderedSet,
//			TreeSet<ComparableAce> compSet) {
//		Iterator<ComparableAce> iter = null;
//		
//		iter = objOrderedSet.iterator();
//		while (iter.hasNext()) {
//			System.out.println(iter.next().toString());
//		}
//		iter = compSet.iterator();
//		while (iter.hasNext()) {
//			System.out.println(iter.next().toString());
//		}
//	}

	/**
	 * @param objList
	 * @return
	 */
	private List<ComparableAce> createComparableList(
			AccessPermissionList accessPermissionList) { 
		List<ComparableAce> list = new ArrayList<ComparableAce>();
		@SuppressWarnings("rawtypes")
		Iterator iter = accessPermissionList.iterator();
		while (iter.hasNext()) {
			AccessPermission permission = (AccessPermission) iter.next();
			ComparableAce comparableAce = new ComparableAce(permission);
			list.add(comparableAce);
		}
		return list;
		
	}

	String permissionToString(AccessPermission permission) {
		AccessType accessType = permission.get_AccessType();
		Integer mask = permission.get_AccessMask();
		Integer depth = permission.get_InheritableDepth();
		String grantee = permission.get_GranteeName();
		PermissionSource source = permission.get_PermissionSource();
			
		StringBuffer buf = new StringBuffer();
		
		buf.append(accessType.getValue()).append("\t");
		buf.append(mask).append("\t");
		buf.append(depth).append("\t");
		buf.append(grantee).append("\t");
		buf.append(source.toString());
		
		return buf.toString();
	}

	protected List<ComparableAce> loadComparableList(File srcFile) throws Exception {
		ACLSerializer serializer = new ACLSerializer();
		List<ComparableAce>  list = null;
		
		list = serializer.loadComparableList(srcFile);
		
		return list;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		FileParam srcFileOpt = null;
		StringParam aclParentTypeOpt = null;
		StringParam pathUriArg = null;
		
		// options
		srcFileOpt = new FileParam(SRCFILE_OPT,
				"Src files for compare)",
				FileParam.IS_FILE & FileParam.IS_READABLE,
				FileParam.REQUIRED);
		srcFileOpt.setOptionLabel("<srcfile>");
		srcFileOpt.setMultiValued(false);
		
		aclParentTypeOpt = getAclParentTypeOpt();
		
		// args
		{
			pathUriArg = getPathUriArg();
		}
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { srcFileOpt, aclParentTypeOpt }, 
					new Parameter[] { pathUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
