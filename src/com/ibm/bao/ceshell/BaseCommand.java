package com.ibm.bao.ceshell;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyDefinitionObject;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.util.MimeTypesUtil;
import com.ibm.bao.ceshell.util.QueryHelper;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.ParseResultInfo;

public abstract class BaseCommand {
	
	public static Map<Id, String> ClassDescriptionNamesCache = new HashMap<Id, String>();
	private static Map<String, Map<String, PropertyDefinition>> pdsByClass = new HashMap<String, Map<String, PropertyDefinition>>();

	protected boolean success = false;
	protected CEShell ceShell;
	protected BaseRequestInfo request;
	private MimeTypesUtil mimeTypes = null;
	
	
	public BaseRequestInfo getRequest() {
		return request;
	}
	
	public BaseResponse getResponse() {
		return getRequest().getResponse();
	}

	public CEShell getShell() {
		return ceShell;
	}

	public void setShell(CEShell shell) {
		this.ceShell = shell;
	}

	public void setRequest(BaseRequestInfo request) {
		this.request = request;
	}

	public MimeTypesUtil getMimeTypes() throws Exception {
		if (mimeTypes == null) {
			mimeTypes = new MimeTypesUtil();
		}
		return mimeTypes;
	}

	public QueryHelper createQueryHelper() {
		return new QueryHelper(this.ceShell);
	}

	public void run() {
		try {
			CmdLineHandler cl = getCommandLine();
			ParseResultInfo info = cl.parse(request.getArgs());
			
			if (info.isHelp()) {
				return;
			}
			if (info.isVersion()) {
				return;
			}
			if (! info.isSuccess()) {	
				getResponse().printErr(cl.getParseError());
				return;
			}
			
			success = doRun(cl);
				
		} catch (Throwable e) {
//			e.printStackTrace();
			getResponse().printErr(e.getMessage());
		}
	}

	protected abstract boolean doRun(CmdLineHandler cl) throws Exception;
	 
	protected abstract CmdLineHandler getCommandLine();

	@SuppressWarnings("deprecation")
	protected String decodePath(String path) {
		String decodedPath = java.net.URLDecoder.decode(path);
		return decodedPath;
	}
	
	protected String pathUriToFullPath(String pathUri) {
		String fullPath = null;
		String path = decodePath(pathUri);
		if (path.startsWith(CWD.PATH_DELIM)) {
			fullPath = path;
		} else {
			fullPath = ceShell.getCWD().relativePathToFullPath(path);
		}
		return fullPath;
	}

	/**
	 * @param classDescriptionId
	 * @return
	 */
	public String fetchClassDescriptionName(Id classDescriptionId) {
		String className = null;
		if (ClassDescriptionNamesCache.containsKey(classDescriptionId)) {
			return (String) ClassDescriptionNamesCache.get(classDescriptionId);
		}
		// ok. not in cache
		 PropertyFilter pf = new PropertyFilter();
		    {
		    	FilterElement fe = new FilterElement(new Integer(1), null, null, "DisplayName", null);
		    	pf.addIncludeProperty(fe);
		    }
		ClassDescription cd = Factory.ClassDescription.fetchInstance(this.getShell().getObjectStore(), 
				classDescriptionId, pf);
		className = cd.get_DisplayName();
		// add to cache
		ClassDescriptionNamesCache.put(classDescriptionId, className);
		return className;
	}

	/**
	 * @param decodedTargetDirectoryUri
	 * @return
	 */
	protected Folder fetchFolder(String targetDirectoryUri) {
		Folder folder = null;
		folder = fetchFolder(targetDirectoryUri, null);
		return folder;
	}
	
	protected Folder fetchFolder(String targetDirectoryUri, PropertyFilter folderPropFilter) {
		Folder folder = null;
		String decodedUri = getShell().urlDecode(targetDirectoryUri);
		if (decodedUri == null) {
			return folder;
		}
		if (getShell().isId(decodedUri)) {
			folder = Factory.Folder.fetchInstance(
						getShell().getObjectStore(), 
						new Id(decodedUri), 
						folderPropFilter);
		   
		} else {
			String fullPath = ceShell.getCWD().relativePathToFullPath(decodedUri);
			folder = Factory.Folder.fetchInstance(
						getShell().getObjectStore(), 
						fullPath, 
						folderPropFilter);
		}
		return folder;
	}
	
	/**
	 * @param docUri
	 * @return
	 */
	protected Document fetchDoc(String docUri) {
		Document doc = null;
		doc = fetchDoc(docUri, null);
		return doc;
	}
	
	protected Document fetchDoc(String docUri, PropertyFilter docPropFilter) {
		Document doc = null;
		String decodedUri = getShell().urlDecode(docUri);
		if (decodedUri == null) {

			return doc;
		}
		if (getShell().isId(decodedUri)) {
			doc = Factory.Document.fetchInstance(
					getShell().getObjectStore(), 
					new Id(decodedUri), 
					docPropFilter);
		   
		} else {
			String fullPath = ceShell.getCWD().relativePathToFullPath(decodedUri);
			doc = Factory.Document.fetchInstance(
					getShell().getObjectStore(), 
					fullPath, 
					docPropFilter);
		}
		return doc;
	}

	protected ClassDefinition getClassDefinition(String className) {
		
		ClassDefinition clsDef = Factory.ClassDefinition.fetchInstance(
				ceShell.getObjectStore(), 
				className, 
				null);	
		
		return clsDef;
	}
	
	/**
	 * @param pdl
	 * @return
	 */
	protected Map<String, PropertyDefinition> fetchPropertyDescriptionMap(String docClassSymbolicName) {
		synchronized(pdsByClass) {
			
			if (BaseCommand.pdsByClass.containsKey(docClassSymbolicName)) {
				return BaseCommand.pdsByClass.get(docClassSymbolicName);
			}
			
			PropertyDefinitionList pdl = this.getClassDefinition(docClassSymbolicName).get_PropertyDefinitions();
			Map<String, PropertyDefinition> pds = new HashMap<String, PropertyDefinition>();
			for (Iterator<?> iterator = pdl.iterator(); iterator.hasNext();) {
				PropertyDefinition pd = (PropertyDefinition) iterator.next();
				String name = pd.get_SymbolicName();
				pds.put(name, pd);
			}
			pdsByClass.put(docClassSymbolicName, pds);
			return pds;
		}
		
	}

	/**
	 * @param propName
	 * @return
	 */
	protected String fetchObjectPropertyClassName(String className, String propName) {
		ClassDefinition czd = this.getClassDefinition(className);
		String objClsSymbolicName = null;
		PropertyDefinitionList pdl = czd.get_PropertyDefinitions();
		
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = pdl.iterator(); iterator.hasNext();) {
			PropertyDefinition pd = (PropertyDefinition) iterator.next();
			if (pd.get_SymbolicName().equals(propName)) {
				if (pd instanceof PropertyDefinitionObject) {
					PropertyDefinitionObject pdo = (PropertyDefinitionObject) pd;
					Id objClsId = pdo.get_RequiredClassId();
					ClassDescription ocd = 
							Factory.ClassDescription.fetchInstance(
									ceShell.getObjectStore(), 
									objClsId, 
									null);
					objClsSymbolicName = ocd.get_SymbolicName();	
				}
				break;
			}
		}
		return objClsSymbolicName;
	}

	/**
	 * @param propValue
	 * @return
	 */
	protected Date parseDate(String propValue) throws Exception {
		return StringUtil.parseDate(propValue);
	}

	/**
	 * @param propValue
	 * @return
	 */
	protected Boolean parseBoolean(String propValue) {
		char firstChar = propValue.trim().toLowerCase().charAt(0);
		if ('t' == firstChar) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public ClassDescription fetchClassDescription(String docClass) {
		ClassDescription cd = Factory.ClassDescription
				.fetchInstance(this.getShell().getObjectStore(), 
				docClass, null);
		return cd;
	}

	/**
	 * @param pdMap
	 * @param props
	 */
	public void applyProperty(String parentClassName, Properties props, PropertyDescription pd,
			String propName, String propValue) throws Exception {
					
					TypeID typeId = pd.get_DataType();
					
					switch(typeId.getValue()) {
						case TypeID.STRING_AS_INT:
							props.putValue(propName, propValue);
							break;
						case TypeID.BOOLEAN_AS_INT:
							Boolean bValue = parseBoolean(propValue);
							props.putValue(propName, bValue);
							break;
						case TypeID.DATE_AS_INT:
							Date dateValue = parseDate(propValue);
							props.putValue(propName, dateValue);
							break;
						case TypeID.DOUBLE_AS_INT:
							Double doubleValue = Double.parseDouble(propValue);
							props.putValue(propName, doubleValue);
							break;
						case TypeID.GUID_AS_INT:
							props.putValue(propName, propValue);
							break;
						case TypeID.LONG_AS_INT:
							Integer intValue = Integer.parseInt(propValue);
							props.putValue(propName, intValue);
							break;
						
						default:
							break;
			//			case TypeID.BINARY_AS_INT:
			//				break;	
						case TypeID.OBJECT_AS_INT:
							String objClasName = fetchObjectPropertyClassName(parentClassName, propName);
							IndependentObject obj = ceShell.getObjectStore().
									fetchObject(objClasName, propValue, null);
							props.putValue(propName, obj);
			//				break;
							
					}
					
				}

	
//	protected abstract String getName();
//	protected abstract String getDescription();
//	protected abstract String getHelpText();
//	
	
}
