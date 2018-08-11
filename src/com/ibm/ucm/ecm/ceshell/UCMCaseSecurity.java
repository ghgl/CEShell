package com.ibm.ucm.ecm.ceshell;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.ACLListCmd;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;
import com.ibm.casemgmt.api.constants.CaseState;

import jcmdline.CmdLineHandler;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

public class UCMCaseSecurity extends ACLListCmd {
	
	
	public static final String 
		CMD = "ucm.csec", 
		CMD_DESC = "Display the security relevant for a UCM Case",
		HELP_TEXT = CMD_DESC;
	
	private static String UcmCasePermissionFilter;
	
	static {
		String[] filterProps = new String[] {
				 //Permissions AccessMask AccessType GranteeName GranteeType InheritableDepth PermissionSource
				PropertyNames.PERMISSIONS,
				PropertyNames.ACCESS_MASK,
				PropertyNames.GRANTEE_NAME,
				PropertyNames.GRANTEE_TYPE,
				PropertyNames.ID,
				PropertyNames.INHERITABLE_DEPTH,
				PropertyNames.PERMISSION_SOURCE,
				PropertyNames.OWNER,
				PropertyNames.PATH_NAME,
				UCM.PropertyNames.CmAcmCaseState,
				UCM.PropertyNames.REC_OWNRSHP_ORG,
				UCM.PropertyNames.UCM_REC_STUS,
				UCM.PropertyNames.UCM_REC_SUB_STUS,
				UCM.PropertyNames.UCM_REFRD_UPIC,
				UCM.PropertyNames.UCM_Security_Proxy
				
		};
		StringBuffer buf = new StringBuffer();
		for (String property : filterProps) {
			buf.append(property).append(" ");
		}
		UcmCasePermissionFilter = buf.toString().trim();
		
	}

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam pathUriParam = (StringParam) cl.getArg(URI_ARG);
		String pathUri = null;
		
		if (pathUriParam.isSet()) {
			pathUri = pathUriParam.getValue();
		}
		
		return ucmCaseSecurityList(pathUri);
	}

	public boolean ucmCaseSecurityList(String pathUri) {
		Folder folder = null;
		if (getShell().isId(pathUri)) {
			folder = Factory.Folder.fetchInstance(getShell().getObjectStore(), new Id(pathUri), null);
		} else if (isCaseId(pathUri)) {
			
		} else {
			String decodedUri = getShell().urlDecode(pathUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			folder = Factory.Folder.fetchInstance(getShell().getObjectStore(), fullPath, null);
		}
		
		
		if (! isUcmCase(folder)) {
			String msg = String.format("Folder with uri %s is not a UCM Case", pathUri);
			getResponse().printErr(msg);
			return false;
		}
		Properties props = folder.getProperties();
		String id = folder.get_Id().toString();
		String className = folder.getClassName();
		String owner = folder.get_Owner();
		String recOwnershipOrg = readString(props, UCM.PropertyNames.REC_OWNRSHP_ORG);
		String secProxyPath = fetchSecProxy(props);
		String caseStateStr = readCaseState(props);
		String ucmRecStus = readString(props, UCM.PropertyNames.UCM_REC_STUS);
		String referredToUpic = readString(props, UCM.PropertyNames.UCM_REFRD_UPIC);
		String ucmRecSubStus = readString(props, UCM.PropertyNames.UCM_REC_SUB_STUS);
		ColDef[] colDefs = new ColDef[] {
				new ColDef("Property", 20, StringUtil.ALIGN_LEFT),
				new ColDef("Value", 60, StringUtil.ALIGN_LEFT)
		};
		String[][] data = new String[][] {
			{PropertyNames.ID, folder.get_Id().toString()},
			{PropertyNames.PATH_NAME, folder.get_PathName()},
			{"className", className},
			{PropertyNames.OWNER, owner},
			{UCM.PropertyNames.REC_OWNRSHP_ORG, recOwnershipOrg},
			{UCM.PropertyNames.CmAcmCaseState, caseStateStr},
			{UCM.PropertyNames.UCM_REC_STUS, ucmRecStus},
			{UCM.PropertyNames.UCM_REC_SUB_STUS, ucmRecSubStus},
			{UCM.PropertyNames.UCM_REFRD_UPIC, referredToUpic},
			{UCM.PropertyNames.UCM_Security_Proxy, secProxyPath}
			
		};
		
		getResponse().printOut(StringUtil.formatHeader(colDefs, " "));
		for (String[] row : data) {
			getResponse().printOut(StringUtil.formatRow(colDefs, row, "."));
		}
		
		System.out.println("");
		
		
		AccessPermissionList accessPermissionList = fetchFolderPermissionList(pathUri);
		listPermissions(accessPermissionList, "f", pathUri);
		return true;
	}

	private boolean isCaseId(String pathUri) {
		// TODO Auto-generated method stub
		return false;
	}

	private String readCaseState(Properties props) {
		// 
		Property prop = props.find(UCM.PropertyNames.CmAcmCaseState);
		if (prop == null) {
			return "";
		}
		
		Integer caseState = prop.getInteger32Value();
		return CaseState.fromIntValue(caseState).stringValue();
	}

	
	private String readString(Properties props, String propName) {
		Property prop = props.find(propName);
		if (prop == null) {
			return "";
		}
		String value = prop.getStringValue();
		return (value == null ? "" : value);
	}

	private String fetchSecProxy(Properties props) {
		Object o = props.getObjectValue("UCM_Security_Proxy");
		if (o == null) {
			return "";
		}
		Folder secProxy = (Folder) o;
		return secProxy.get_PathName();
	}

	private boolean isUcmCase(Folder folder) {
		return (folder.getSuperClasses()[0].equals("CmAcmCaseFolder"));
	}

	private Folder fetchCaseFolder(String pathUri) {
		Folder folder;
		PropertyFilter permissionFilter = createUcmStdPermissionFilter(); 
		
		if (getShell().isId(pathUri)) {
			folder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
					new Id(pathUri), permissionFilter);
		   
		} else {
			String decodedUri = getShell().urlDecode(pathUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			folder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
					fullPath, permissionFilter);
		}
		return folder;
	}
	
	
	private PropertyFilter createUcmStdPermissionFilter() {
		PropertyFilter permissionFilter = new PropertyFilter();
		permissionFilter.setMaxRecursion(2);
		permissionFilter.addIncludeProperty(
				new FilterElement(null, null, null, UcmCasePermissionFilter, null));
		return permissionFilter;
	}

	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;		
		StringParam pathUriArg = null;
		
		// options
		
		
		// cmd args
		pathUriArg = getPathUriArg();

		// create command line handler
		cl = new HelpCmdLineHandler(
					HELP_TEXT, 
					CMD, 
					CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { pathUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
