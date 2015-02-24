 /**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.property.Properties;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.PropertyUtil;


/**
 *  DocAddCmd
 *  
 *  docadd -docclass Gogo -title howdy -folder /glrtest -srcfiles e:/temp/gogo.txt 
 * @author GaryRegier
 * @date   Oct 16, 2010
 *  TODO:  Add support for properties
 */
public class DocAddCmd extends BaseCommand {
	
	
	private static final String 
		CMD = "docadd", 
		CMD_DESC = "Add a document to the current object store",
		HELP_TEXT = "Usage:" +
			"\n\tProperties may be stored in a properties file." +
		  	"\n\tdocadd -title foo.txt -folder-uri /test/myfolder -srcfiles e:/temp/foo.txt" +
		  	"\n\t creates document foo.txt in folder /test/myfolder" + 
		  	"\n\n\tdocadd -t bar.txt -f /test/myfolder -d MyDocClass -s e:/temp/bar1.txt -s e:/temp/bar2.txt" +
		  	"\n\tcreates document bar.txt from doc claass MyDocClass with two src files" +
		  	"\n\tdocadd -title foo.txt -folder-uri /test/myfolder -srcfiles e:/temp/foo.txt -propsfile e:/temp/foo.properties" +
		  	"\n\tcreates document foo in folder /tese/myfolder with title foo.txt and applies the properties in e:/temp/foo.properties";
	
	private static final String
		DOCCLASS_OPT = "docclass",
		PROPS_OPT = "propsfile",
		SRC_FILE_OPT = "srcfiles",
		TITLE_OPT = "title",
		FOLDER_URI_OPT = "folder-uri";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam docClassOpt = (StringParam) cl.getOption(DOCCLASS_OPT);
		StringParam titleOpt = (StringParam) cl.getOption(TITLE_OPT);
		StringParam folderUriOpt = (StringParam) cl.getOption(FOLDER_URI_OPT);
		FileParam srcFilesOpt = (FileParam) cl.getOption(SRC_FILE_OPT);
		FileParam propsFileOpt = (FileParam) cl.getOption(PROPS_OPT);
		File propsFile = null;
		String docClass = null;
		List<File> srcFiles = null;
		String parentFolderUri = null;
		String title = null;
		
		if (docClassOpt.isSet()) {
			docClass = docClassOpt.getValue();
		}
		if (folderUriOpt.isSet()) {
			parentFolderUri = folderUriOpt.getValue();
		}
		if (srcFilesOpt.isSet()) {
			srcFiles =  srcFilesOpt.getValues();
		}
		if (propsFileOpt.isSet()) {
			propsFile = propsFileOpt.getValue();
		}
		if (titleOpt.isSet()) {
			title = titleOpt.getValue();
		}

		return docAdd(docClass, srcFiles, parentFolderUri, title, propsFile);
	}

	/**
	 * @param docClass
	 * @param srcDir
	 * @param srcFilename
	 * @param parentFolderUri
	 * @param propsName
	 */
	public boolean docAdd(
			String docClass, 
			List<File> srcFiles, 
			String parentFolderUri, 
			String documentTitle,
			File propsFile) throws Exception {
		Document doc = null;
		java.util.Properties docProps = null;
		boolean linkDocToFolder = (parentFolderUri == null) ? false : true;
		String docName = null;
		
		if (docClass == null) {
			docClass = ClassNames.DOCUMENT;
		}
		
		doc = Factory.Document.createInstance(getShell().getObjectStore(), docClass);
		
		// Set document properties
		if (propsFile != null) {
			docProps = new PropertyUtil().loadPropertiesFromFile(propsFile);
			doSetProps(docClass, doc, docProps);
		}
		if (documentTitle != null) {
			doc.getProperties().putValue("DocumentTitle", documentTitle);
		}
		
		if (srcFiles != null) {
			addContentElements(doc, srcFiles);
		}

		// Check in the document
		doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		doc.save(RefreshMode.REFRESH);
		docName = doc.get_Name();
		if (linkDocToFolder) {
			fileInFolder(doc, parentFolderUri, docName);
		}
		
		// response
		{
			StringBuffer msg = new StringBuffer();
			msg.append("Document created: ");
			msg.append("(id: ").append(doc.get_Id().toString()).append(") ");
			if (linkDocToFolder) {
				
				msg.append(parentFolderUri).append("/").append(docName);
			} else {
				msg.append("Unfiled");
			}
			getResponse().printOut(msg.toString());
		}
		return true;
	}
	
	private void doSetProps(
			String className, 
			Document doc, 
			java.util.Properties props) throws Exception {
		Map<String, PropertyDescription> pdMap = new HashMap<String, PropertyDescription>();
		Properties docProps = doc.getProperties();
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

	@SuppressWarnings("unchecked")
	protected void addContentElements(Document doc, List<File> srcFiles) 
				throws Exception {
		ContentElementList contentList = Factory.ContentTransfer.createList();

		for (Iterator<File> iter = srcFiles.iterator(); iter.hasNext();) {
			File nextFile = iter.next();
			String name = nextFile.getName();
			String mimeType = this.getMimeTypes().getMimeType(name);
		    // First, add a ContentTransfer object.
		    ContentTransfer ctObject = Factory.ContentTransfer.createInstance();
		    FileInputStream fileIS = new FileInputStream(nextFile.getAbsolutePath());
		    ctObject.setCaptureSource(fileIS);
		    // Add ContentTransfer object to list
		    if (mimeType != null) {
				ctObject.set_ContentType(mimeType);
			}
		    ctObject.set_RetrievalName(nextFile.getName());
		    contentList.add(ctObject);
		}
		
	    doc.set_ContentElements(contentList);
	}

	private void fileInFolder(Document doc, String parentFolderUri, String title) {
		Folder parentFolder = null;
		if (parentFolderUri == null) {
			return;
		}
		if (getShell().isId(parentFolderUri)) {
			parentFolder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
					new Id(parentFolderUri), null);
		   
		} else {
			String decodedUri = getShell().urlDecode(parentFolderUri);
			String fullPath = this.getShell().getCWD().relativePathToFullPath(decodedUri);
			parentFolder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
					fullPath, null);
		}
		// File the document
		ReferentialContainmentRelationship rcr = parentFolder.file(doc,
		        AutoUniqueName.AUTO_UNIQUE, title,
		        DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
		rcr.save(RefreshMode.NO_REFRESH);
	}


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam folderUriOpt = null;
		StringParam titleOpt = null;
		FileParam srcFilesOpt = null;
		FileParam propsFileOpt = null;

		// options
		folderUriOpt = new StringParam(FOLDER_URI_OPT, 
				"folder uri");
		folderUriOpt.setOptional(StringParam.OPTIONAL);
		folderUriOpt.setMultiValued(StringParam.SINGLE_VALUED);
		
		StringParam docClassParam = new StringParam(DOCCLASS_OPT,
				"Document class");
		docClassParam.setOptional(StringParam.OPTIONAL);
		docClassParam.setMultiValued(StringParam.SINGLE_VALUED);
		docClassParam.setOptionLabel("<doc-class>");
		
		titleOpt = new StringParam(TITLE_OPT,
				"document title");
		titleOpt.setOptional(StringParam.OPTIONAL);
		titleOpt.setMultiValued(false);
		titleOpt.setOptionLabel("<title>");
		
		srcFilesOpt = new FileParam(SRC_FILE_OPT,
				"Src files for import",
				FileParam.IS_FILE & FileParam.IS_READABLE,
				FileParam.OPTIONAL);
		srcFilesOpt.setOptionLabel("<srcFiles>");
		srcFilesOpt.setMultiValued(true);
		
		propsFileOpt = new FileParam(PROPS_OPT, 
				"properties file",
				FileParam.IS_FILE & FileParam.IS_READABLE,
				FileParam.OPTIONAL);
		
		propsFileOpt.setOptionLabel("<propsFile>");
		
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {titleOpt, docClassParam, folderUriOpt, srcFilesOpt, propsFileOpt }, 
					new Parameter[] { });
		cl.setDieOnParseError(false);

		return cl;
	}
}

