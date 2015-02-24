/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import jcmdline.StringParam;

import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.AccessType;
import com.filenet.api.constants.PermissionSource;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.constants.ACL;
import com.ibm.bao.ceshell.security.ACLSerializer;
import com.ibm.bao.ceshell.security.DocLevels;
import com.ibm.bao.ceshell.security.Level;

/**
 *  AbsAclCmd
 *  Abstract superclass for ACL commands. Some common functionality
 *  is in this class
 *
 * @author GaryRegier
 * @date   Oct 9, 2010
 */
public abstract class AbsAclCmd extends BaseCommand {
	
	public static final class ACLTYPE {
		public static final String
			CLASS_DEFINITION = "cd",
			CLASS_DEFINITION_DEFAULT = "cdd",
			DOCUMENT = "d",
			FOLDER = "f",
			OBJECT_STORE = "os",
			DOMAIN = "dom",
			PROPERTY_TEMPLATE = "pt";
			
	}
	
	// param names
	protected static final String 
			ACL_PARENT_TYPE_OPT = "type",
			URI_ARG = "URI";
		
	
	protected static final String[][] ACL_PARENT_TYPES = new String[][] {
			{"d", "document (default)"},
			{"cd","class definition"},
			{"cdd", "class definition default instance properties"},
			{"dom", "domain"},
			{"f", "folder"},
			{"os", "ObjectStore"},
			{"pt", "PropertyTemplate"}
		};
	
	private static Collection<String> ParentTypes = new HashSet<String>();
	static {
		for (int i = 0; i < ACL_PARENT_TYPES.length; i++) {
			ParentTypes.add(ACL_PARENT_TYPES[i][0]);
		}
	}
	
	protected DocLevels docLevels = new DocLevels();

	/**
	 * @param pathUri
	 * @return
	 */
	protected AccessPermissionList fetchDomainPermissionList(String pathUri) 
			throws Exception {
		AccessPermissionList accessPermissionList = null;
		Domain dom = getShell().getDomain(getShell().getCEConnection());
		accessPermissionList = dom.get_Permissions();
		return accessPermissionList;
	}
	
	/**
	 * @param pathUri
	 * @return
	 */
	protected AccessPermissionList fetchPropertyTemplatePermissionList(String pathUri) 
			throws Exception {
		PropertyTemplate propTemplate = null;
		AccessPermissionList permissions = null;
		
		propTemplate = fetchPropertyTemplateByUri(pathUri); 
		permissions = propTemplate.get_Permissions();
		
		return permissions;
	}

	protected PropertyTemplate fetchPropertyTemplateByUri(String pathUri) throws Exception {
		Id id = null;
		PropertyTemplate propertyTemplate = null;
		
		if (getShell().isId(pathUri)) {
			id = new Id(pathUri);
		} else {
			id = createQueryHelper().fetchId("PropertyTemplate", "SymbolicName = \'" + pathUri + "\'");
		}
		
		if (id == null) {
			throw new IllegalArgumentException("PropertyTemplate with name " + pathUri + " not found");
		}
		propertyTemplate = Factory.PropertyTemplate.fetchInstance(
				getShell().getObjectStore(), 
				id,
				null);
		
		return propertyTemplate;
	}
	

	/**
	 * @param pathUri
	 * @return
	 */
	protected AccessPermissionList fetchObjectStorePermissionList(String pathUri) 
			throws Exception{
		ObjectStore os = null;
		Domain domain = null;
		AccessPermissionList accessPermissionList = null;
		if (pathUri != null) {
			Connection con = getShell().getCEConnection();
			domain = getShell().getDomain(con);
			os = Factory.ObjectStore.fetchInstance(domain, pathUri, null);
			accessPermissionList = os.get_Permissions();
		}
		return accessPermissionList;
	}

	/**
	 * * <br> A class has two permission lists:
	 * <ol> 
	 * 	<li>Class definition permissions: permissions on the class entity
	 * 	<li> Default Instance permissions: permissions applied to new instances
	 *       created from the class definition.
	 *  </ol>
	 *  <br> This method returns the list default instance list. 
	 * @param pathUri
	 * @return
	 */
	protected AccessPermissionList fetchDefaultClassPermissionList(String pathUri) {
		String docClassName = pathUri;
		AccessPermissionList permissions = null;
		PropertyFilter permissionFilter = 
				createDefaultClassSecurityFilter();
		com.filenet.api.admin.ClassDefinition czd = 
			Factory.ClassDefinition.fetchInstance(
					getShell().
					getObjectStore(), 
					docClassName,
					permissionFilter);
		permissions = czd.get_DefaultInstancePermissions();
		
		return permissions;
	}

	/**
	 * fetchClassPermissionList
	 * <br> A class definition has two permission lists:
	 * <ol> 
	 * 	<li>class permissions: permissions on the doc class entity
	 * 	<li> Default Instance permissions: permissions applied on new instances
	 *       of the class.
	 *  </ol>
	 *  <br> This method returns the list associated directly with the class.
	 *  
	 * @param pathUri
	 * @return
	 */
	protected AccessPermissionList fetchClassPermissionList(String pathUri) {
		String className = pathUri;
		AccessPermissionList permissions = null;
		PropertyFilter classPermissionFilter = 
				createStdPermissionFilter();
//		com.filenet.api.admin.DocumentClassDefinition dcd = 
//			Factory.DocumentClassDefinition.fetchInstance(
//					getShell().
//					getObjectStore(), 
//					docClassName,
//					defaultDocClassPermissionFilter);
		
		com.filenet.api.admin.ClassDefinition cd = 
			Factory.ClassDefinition.fetchInstance(
						getShell().getObjectStore(), 
						className, 
						classPermissionFilter);
		permissions = cd.get_Permissions();
		
		return permissions;
	}

	/**
	 * @param pathUri
	 * @return
	 */
	protected AccessPermissionList fetchFolderPermissionList(String pathUri) {
		Folder folder = null;
		PropertyFilter permissionFilter = createStdPermissionFilter(); 
		
		if (getShell().isId(pathUri)) {
			folder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
					new Id(pathUri), permissionFilter);
		   
		} else {
			String decodedUri = getShell().urlDecode(pathUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			folder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
					fullPath, permissionFilter);
		}
		
		if (folder != null) {
			AccessPermissionList permissions = folder.get_Permissions();
			return permissions;
		}
		return null;
	}

	protected PropertyFilter createStdPermissionFilter() {
		PropertyFilter permissionFilter = new PropertyFilter();
		permissionFilter.setMaxRecursion(2);
		permissionFilter.addIncludeProperty(
				new FilterElement(null, null, null, "Permissions AccessMask AccessType GranteeName GranteeType InheritableDepth PermissionSource", null));
		return permissionFilter;
	}

	protected String getDocNamedAccessLevel(AccessType accessType, Integer accessMask) {
		String name = "Custom";
		if (accessType == AccessType.ALLOW) {
			Level docLevel = docLevels.getLevelByMask(accessMask);
			if (docLevel != null) {
				name = (String) docLevel.getDescription();
			}
		}
		return name;
	}

	protected String getDepthDescription(Integer depth) {
		int d = depth.intValue();
		String depthStr = "unknown";
		if (ACL.D_ALL_CHILDREN.equals(d)) {
			depthStr = ACL.ALL_CHILDREN;
		} else if(ACL.D_CHILDREN.equals(d)) {
			depthStr = ACL.CHILDREN;
		} else if(ACL.D_NO_INHERITANCE.equals(d)) {
			depthStr = ACL.NO_INHERITANCE;
		}
		return depthStr;
	}

	/**
	 * @return
	 */
	protected PropertyFilter createDefaultClassSecurityFilter() {
		PropertyFilter filter = new PropertyFilter();
		
		String[] names = new String[] {
				"DefaultInstancePermissions",
				"AccessType",
				"AccessMask",
				"GranteeName",
				"GranteeType",
				"DisplayName",
				"DescriptiveText",
				"InheritableDepth",
				"PermissionSource",
				"PermissionType"
		};
		for (int i = 0; i < names.length; i++) {
			FilterElement fe = new FilterElement(new Integer(1), 
					null, null, names[i], null);
	
			filter.addIncludeProperty(
					fe);
		}
		return filter;
	}
	
	protected AccessPermissionList fetchDocumentPermissionList(String pathUri) {
		AccessPermissionList permissions = null;
		Document doc = null;
		PropertyFilter permissionFilter = createStdPermissionFilter(); 
		
		if (getShell().isId(pathUri)) {
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					new Id(pathUri), permissionFilter);
		} else {
			String decodedUri = getShell().urlDecode(pathUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					fullPath, permissionFilter);
		}
		
		if (doc != null) {
			permissions = doc.get_Permissions();
		}
		return permissions;
	}

	protected String getAclTypeOptsDesc() {
		StringBuffer aclTypeOptsDesc = new StringBuffer();
		{
			aclTypeOptsDesc.append("Access Control List entries for type <parentType> is\n");
			for (int i = 0; i < ACL_PARENT_TYPES.length; i++) {
				aclTypeOptsDesc.append("\t").append(ACL_PARENT_TYPES[i][0])
					.append(":\t")
					.append(ACL_PARENT_TYPES[i][1])
					.append("\n");
			}		
		}
		return aclTypeOptsDesc.toString();
	}

	/**
	 * Get the resource URI to report on.
	 * <p>
	 * NOTE: This is optional because if the type is domain, then the 
	 * resource URI does not need to be specified (uses the default domain).
	 * @return
	 */
	protected StringParam getPathUriArg() {
		String pathURIDesc = "URI indicating a file or folder. It can also be "+
		"the ID of a document or folder. If the type is a document class, " +
		"the value can be the name of the document class";
		
		// cmd args
		StringParam pathUriArg = new StringParam(URI_ARG, 
				pathURIDesc,
				StringParam.OPTIONAL);
		pathUriArg.setMultiValued(false);
		
		return pathUriArg;
	}

	protected StringParam getAclParentTypeOpt() {
		
		
		String aclTypeOptsDesc = getAclTypeOptsDesc();
		StringParam aclParentTypeOpt = new StringParam(ACL_PARENT_TYPE_OPT,
				aclTypeOptsDesc.toString(),
				StringParam.OPTIONAL);
		aclParentTypeOpt.setAcceptableValues(AbsAclCmd.ParentTypes);
		aclParentTypeOpt.setMultiValued(false);
		
		return aclParentTypeOpt;
	}

	protected AccessPermissionList loadAcls(File srcFile) throws Exception {
		ACLSerializer serializer = new ACLSerializer();
		AccessPermissionList list = null;
		
		list = serializer.loadAcls(srcFile);
		
		return list;
	}

	protected void applyPermissionsToFolder(String pathUri, AccessPermissionList apl) {
		Folder folder = null;
		PropertyFilter permissionFilter = createStdPermissionFilter(); 
		
		if (getShell().isId(pathUri)) {
			folder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
					new Id(pathUri), permissionFilter);
		} else {
			String decodedUri = getShell().urlDecode(pathUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			folder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
					fullPath, permissionFilter);
		}
		if (folder == null) {
			throw new IllegalArgumentException("No folder found at " + pathUri);
		}
	
		folder.set_Permissions(apl);
		folder.save(RefreshMode.REFRESH);
	}

	protected void applyPermissionsToClassDefinition(String pathUri, AccessPermissionList apl) {
		String className = pathUri;
		PropertyFilter aclPermissionFilter = 
				createStdPermissionFilter();
		com.filenet.api.admin.ClassDefinition cd = 
			Factory.ClassDefinition.fetchInstance(
					getShell().
					getObjectStore(), 
					className,
					aclPermissionFilter);
		if (cd == null) {
			throw new IllegalArgumentException("No document class with name " + pathUri);
		}
		cd.set_Permissions(apl);
		cd.save(RefreshMode.REFRESH);
	}

	protected void applyPermissionsToClassDefaultInstance(String pathUri,
			AccessPermissionList apl) {
				String docClassName = pathUri;
				PropertyFilter aclPermissionFilter = 
						createStdPermissionFilter();
				com.filenet.api.admin.ClassDefinition cd = 
					Factory.ClassDefinition.fetchInstance(
							getShell().
							getObjectStore(), 
							docClassName,
							aclPermissionFilter);
				if (cd == null) {
					throw new IllegalArgumentException("No class with name " + pathUri);
				}
				cd.set_DefaultInstancePermissions(apl);
				cd.save(RefreshMode.REFRESH);
			}

	protected void applyPermissionsToObjectStore(String pathUri, AccessPermissionList apl) {
		throw new UnsupportedOperationException(
		"Applying security to an object store is not supported");
	}

	protected void applyPermissionsToDomain(String pathUri, AccessPermissionList apl) {
		throw new UnsupportedOperationException(
				"Applying security to a domain is not supported");
	}

	/**
	 * @param docUri
	 * @param accessListFile
	 */
	protected void applyPermissionsToDoc(String pathUri, AccessPermissionList apl)
			throws Exception {
				Document doc = null;
				
				if (getShell().isId(pathUri)) {
					doc = Factory.Document.getInstance(
							getShell().getObjectStore(),
							null,
							new Id(pathUri));
				} else {
					String decodedUri = getShell().urlDecode(pathUri);
					String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
					doc = Factory.Document.getInstance(
							getShell().getObjectStore(),
							null,
							fullPath);
				}
				if (doc == null) {
					throw new IllegalArgumentException("No document found at " + pathUri);
				}
				doc.set_Permissions(apl);
				doc.save(RefreshMode.REFRESH);
			}

	/**
	 * @param pathUri
	 * @param accessPermissionList
	 */
	protected void applyPermissionsToPropertyTemplate(String pathUri,
			AccessPermissionList accessPermissionList) throws Exception {
				PropertyTemplate propTemplate = null;
				
				propTemplate = fetchPropertyTemplateByUri(pathUri);
				propTemplate.set_Permissions(accessPermissionList);
				propTemplate.save(RefreshMode.REFRESH);
			}

	/**
	 * Inherited permissions can not be set
	 * @param srcPermissionList
	 */
	@SuppressWarnings("unchecked")
	protected AccessPermissionList removeInheritedSecurity(AccessPermissionList srcPermissionList) {
		AccessPermissionList directPermissionList = 
				Factory.AccessPermission.createList();
		@SuppressWarnings("rawtypes")
		Iterator srcACLIter = srcPermissionList.iterator();
		while (srcACLIter.hasNext()) {
			AccessPermission nextPermission = (AccessPermission) srcACLIter.next();
			PermissionSource nextPermSrc = nextPermission.get_PermissionSource();
			if (PermissionSource.SOURCE_DIRECT.equals(nextPermSrc) ||
						PermissionSource.SOURCE_DEFAULT.equals(nextPermSrc) ) {
				directPermissionList.add(nextPermission);
			}
		}
		return directPermissionList;
	}

	

}
