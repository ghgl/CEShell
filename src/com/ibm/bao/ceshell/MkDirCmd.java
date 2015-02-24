/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.property.Properties;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.constants.ObjTypes;
import com.ibm.bao.ceshell.util.PropertyUtil;

/**
 *  MkDirCmd
 *  
 *  mkdir gogo				creates gogo in the current folder
 *  mkdir ../gogo			createa folder gogo in the parent folder
 *  mkdir ./foo/baz/gogo	creates gogo in the foo/baz folder
 *  mkdir /gogo				creates gogo as a top-level folder
 *  
 * Create a new folder in the object store
 *
 * @author GaryRegier
 * @date   Oct 7, 2010
 */
public class MkDirCmd extends BaseCommand {
	
	private static final String 
		CMD = "mkdir", 
		CMD_DESC = "Make new folders in the object store",
		HELP_TEXT = "Create new folders in the object store\n" +
			"Usage:\n" + 
			"\tmkdir gogo                creates folder gogo in the current folder\n" +
			 "\tmkdir ../gogo            creates folder gogo in the parent folder\n" +
			 "\tmkdir ./foo/baz/gogo     creates folder gogo in the foo/baz folder\n" +
			 "\tmkdir /gogo              creates gogo as a top-level folder\n" +
			 "\tmkdir -parents ./foo/baz/gogo  creates the parent folders \n" +
			 "\t                         foo and foo/baz and foo/baz/gogo as needed\n" + 
			 "\tmkdir -class MyCustomType gogo   creates a folder from type MyCustomType in the current directory" +
			 "\tmkdir -c MyCustomType -p foo/baz/gogo  creates folders of type MyCustomType and parent folders as needed";
			
	// param names
	private static final String 
			PARENTS_OPT = "parents",
			FOLDER_CLASS_OPT = "class",
			PROPS_OPT = "propsfile",
			FOLDER_NAME_ARG = "folder-name";		 

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam parentsOpt = (BooleanParam) cl.getOption(PARENTS_OPT);
		StringParam folderClassOpt = (StringParam) cl.getOption(FOLDER_CLASS_OPT);
		StringParam folderNameArg = (StringParam) cl.getArg(FOLDER_NAME_ARG);
		FileParam propsFileOpt = (FileParam) cl.getOption(PROPS_OPT);
		String folderClass = "Folder";
		Boolean makeParents = false;
		String folderName = null;
		File propsFile = null;

		if (parentsOpt.isSet()) {
			makeParents = parentsOpt.getValue();
		}
		if (folderClassOpt.isSet()) {
			folderClass = folderClassOpt.getValue();
		}
		if (propsFileOpt.isSet()) {
			propsFile = propsFileOpt.getValue();
		}
		
		folderName = folderNameArg.getValue();
		
		return mkdir(folderClass, makeParents, folderName, propsFile);
	}

	public boolean mkdir(
			String folderClass, 
			Boolean makeParents,
			String folderName,
			File propsFile) throws Exception {
		String fullPathOfNewFolder;
		fullPathOfNewFolder = mkDir(folderName, folderClass, makeParents, propsFile);
		getResponse().printOut("New folder created:  " + fullPathOfNewFolder);
		return true;
	}
	
	

	/**
	 * @param folderName
	 * @param folderClass
	 * @param parentsOpt
	 * @throws Exception 
	 */
	private String mkDir(
			String folderNameURI, 
			String folderClass,
			boolean makeParents,
			File propsFile) throws Exception {
		String newFolderClassName = ObjTypes.FOLDER;
		String decodedName = getShell().urlDecode(folderNameURI);
		String fullPath;
		String folderName;
		String parentPath;
		CWD cwd = getShell().getCWD();
		java.util.Properties folderProps = null;
		
		if (folderClass != null) {
			newFolderClassName = folderClass;
		}
		if (decodedName.startsWith(CWD.PATH_DELIM)) {
			fullPath = decodedName;
		} else {
			fullPath = getShell().getCWD().relativePathToFullPath(decodedName);
		}
		if (makeParents == true) {
			throw new UnsupportedOperationException(
					"@todo: makeParents not yet implemented");
		}
		folderName = cwd.getName(fullPath);
		parentPath = cwd.getPath(fullPath);
		makeFolder(newFolderClassName, folderName, parentPath, cwd, propsFile);
		return fullPath;
	}
	
	

	private Folder makeFolder(
			String newFolderClassName, 
			String folderName,
			String parentPath, 
			CWD cwd,
			File propsFile) throws Exception {
		Folder parentFolder = null;
		Folder folder = null;
		java.util.Properties props = null;
		
		if (cwd.isRootDir(parentPath)) {
			parentFolder = getShell().getObjectStore().get_RootFolder();
		} else {
			// NOTE: this causes a round-trip. Could use getInstance
			parentFolder = Factory.Folder.fetchInstance(
					getShell().getObjectStore(), 
					parentPath, null);
		}
		
		folder = Factory.Folder.createInstance(getShell().getObjectStore(),
				newFolderClassName);
		// Set properties
		if (propsFile != null) {
			props = new PropertyUtil().loadPropertiesFromFile(propsFile);
			doSetProps(newFolderClassName, folder, props);
		} 
		folder.set_FolderName(folderName);
		folder.set_Parent(parentFolder);
		folder.save(RefreshMode.REFRESH);
		return folder;
	}
	
	private void doSetProps(
			String className, 
			Folder folder, 
			java.util.Properties props) throws Exception {
		Map<String, PropertyDescription> pdMap = new HashMap<String, PropertyDescription>();
		Properties docProps = folder.getProperties();
		ClassDescription cd = fetchClassDescription(className);
		PropertyDescriptionList pdl = cd.get_PropertyDescriptions();
		
		for (Iterator<?> iterator = pdl.iterator(); iterator.hasNext();) {
			PropertyDescription pd = (PropertyDescription) iterator.next();
			pd.get_DataType();
			String pdName = pd.get_SymbolicName();
			pdMap.put(pdName, pd);
		}
		for (Object propName : props.keySet()) {
			String propValue = props.getProperty(propName.toString());
			if (! pdMap.containsKey(propName))  {
				throw new IllegalArgumentException("property with name " + propName + " was not found.");
			}
			PropertyDescription pd = pdMap.get(propName);
			if (pd.get_IsReadOnly()) {
				throw new Exception(String.format("Property %s is read-only", propName));
			}
			applyProperty(className, docProps, pd, propName.toString(), propValue);
		}	
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam parentsOpt = null;
		StringParam folderClassOpt = null;
		StringParam folderNameArg = null;
		FileParam propsFileOpt = null;
		
		// options
		{
			parentsOpt = new BooleanParam(PARENTS_OPT,
					"create the parent directorys (as needed)",
					BooleanParam.OPTIONAL);
			parentsOpt.setMultiValued(false);
		}
		{
			propsFileOpt = new FileParam(PROPS_OPT, 
					"properties file",
					FileParam.IS_FILE & FileParam.IS_READABLE,
					FileParam.OPTIONAL);
			
			propsFileOpt.setOptionLabel("<propsFile>");
		}
		{
			folderClassOpt = new StringParam(FOLDER_CLASS_OPT,
					"creates folders from this folder class",
					StringParam.OPTIONAL);
			folderClassOpt.setMultiValued(false);
		}
		// cmd args
		{
			folderNameArg = new StringParam(FOLDER_NAME_ARG, 
					"name of folder to create",
					StringParam.REQUIRED);
			folderNameArg.setMultiValued(false);
		}
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { parentsOpt, folderClassOpt, propsFileOpt }, 
					new Parameter[] { folderNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
