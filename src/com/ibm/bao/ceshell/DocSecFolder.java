/**
 * 
 */
package com.ibm.bao.ceshell;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  DocSecFolder
 *
 * @author regier
 * @date   May 9, 2012
 */
public class DocSecFolder extends BaseCommand {
	
	private static final String 
		CMD = "docsecfolder", 
		CMD_DESC = "Get/set the SecurityFolder property on a document",
		HELP_TEXT = "Get/set the SecurityFolder property on a document\n" +
		 "A docuri or a document title must be urlencoded.\n" +
		 "Usage:  \n"  +
		 "\tdocsecfolder /TestFolder/doc1.doc  /TestFolder\n\t\t sets the current SecurityFolder property\n" +
		 "\tdocsecfolder /TestFolder/doc1.doc : \n\t\treturns the value of the security folder\n" +
		 "\tdocsecfolder -unset /Testfolder/doc1.doc\n\t\tSetst the security folder property to null";

// param names
private static final String 
	UNSET_OPT = "unset",
	DOC_ARG = "document",
	FOLDER_ARG = "security-folder";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam unsetOpt = (BooleanParam) cl.getOption(UNSET_OPT);
		StringParam docArg = (StringParam) cl.getArg(DOC_ARG);
		StringParam folderArg = (StringParam) cl.getArg(FOLDER_ARG);
		String docUri = null;
		String folderUri = null;
		Boolean unsetSecurityFolder = Boolean.FALSE;
		
		if (unsetOpt.isSet()) {
			unsetSecurityFolder = unsetOpt.getValue();
		}
		docUri = docArg.getValue();
		if (folderArg.isSet()) {
			folderUri = folderArg.getValue();
		}
		return docSecurityFolder(docUri, folderUri, unsetSecurityFolder);
		
	}

	/**
	 * @param docUri
	 * @param folderUri
	 */
	public boolean docSecurityFolder(String docUri, 
				String folderUri,
				Boolean unsetSecurityFolder) {
		
		if(unsetSecurityFolder) {
			return doUnsetSecurityFolder(docUri);
		} else if (folderUri != null) {
			return doSetDocSecurityFolderUri(docUri, folderUri);
		} else {
			return doGetSecurityFolder(docUri);
		}
	}

	/**
	 * @param docUri
	 * @return
	 */
	private boolean doGetSecurityFolder(String docUri) {
		Document doc = null;
		PropertyFilter docPropFilter = null;
		
		docPropFilter = getDocPropertyFilter();
		doc = fetchDoc(docUri, docPropFilter);

		Folder folder = doc.get_SecurityFolder();
		String msg = "SecurityFolder:\n\tDoc URI:\t" + docUri + "\n\tFolder:\t\t";
		
		if (folder == null) {
			msg = msg + "<null>";
		} else {
			String path = folder.get_PathName();
			msg = msg + path;
		}
		getResponse().printOut(msg);
		return true;
	}
	
	/**
	 * @param docUri
	 */
	private boolean doUnsetSecurityFolder(String docUri) {
		Document doc = null;
		Folder folder = null; 
		
		doc = fetchDoc(docUri, getDocPropertyFilter());
		if (doc == null) {
			getResponse().printErr("Document not found: " + docUri);
			return false;
		}
		
		folder = doc.get_SecurityFolder();
		if (folder == null) {
			getResponse().printOut("Unset security folder: for " + docUri + ": security folder already null");
			return true;
		}
		
		doc.set_SecurityFolder(null);
		doc.save(RefreshMode.NO_REFRESH);
		getResponse().printOut("Security folder unsetset for doc \n\tDoc URI:\t" + 
				docUri + "\n\tfrom " + folder.get_PathName());
	 	
		return true;
	}

	/**
	 * @param docUri
	 * @param folderUri
	 * @return
	 */
	private boolean doSetDocSecurityFolderUri(String docUri, String folderUri) {
		Document doc = fetchDoc(docUri, getDocPropertyFilter());
		Folder folder = fetchFolder(folderUri, getFolderFilter());
		
		if (doc == null) {
			getResponse().printErr("document not found: " + docUri);
			return false;
		}
		if (folder == null) {
			getResponse().printErr("Folder not found:" + folderUri);
			return false;
		}
		
		doc.set_SecurityFolder(folder);
		doc.save(RefreshMode.NO_REFRESH);
		getResponse().printOut("Security folder set for doc " + docUri + " to " + folder.get_PathName());
		return true;
	}
	
	public PropertyFilter getDocPropertyFilter() {
		PropertyFilter filter = null;
	    
    	String propertyNames =  
    			PropertyNames.NAME + " " + 
    			PropertyNames.SECURITY_FOLDER + " " + 
    			PropertyNames.ID + " " +
    			PropertyNames.INHERIT_PARENT_PERMISSIONS + " " +
    			PropertyNames.PATH_NAME;
    	
    	filter = new PropertyFilter();
    	filter.setLevelDependents(Boolean.FALSE);
    	filter.setMaxRecursion(1);
    	filter.addIncludeProperty(new FilterElement(new Integer(1), null, false, propertyNames, null ));
	   
    	return filter;
	}

	private PropertyFilter getFolderFilter() {
		PropertyFilter filter = null;
	    
    	String propertyNames =  
    			PropertyNames.NAME + " " + 
    			PropertyNames.PATH_NAME + " " + 
    			PropertyNames.ID + " " +
    			PropertyNames.INHERIT_PARENT_PERMISSIONS;
    	filter = new PropertyFilter();
    	filter.setLevelDependents(Boolean.FALSE);
    	filter.setMaxRecursion(1);
    	filter.addIncludeProperty(new FilterElement(new Integer(1), null, false, propertyNames, null ));
	   
    	return filter;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		BooleanParam unsetOpt = null;
		StringParam docArg = null;
		StringParam folderArg = null;

		// options
		unsetOpt = new BooleanParam(UNSET_OPT, "unset the security folder");
		unsetOpt.setOptional(BooleanParam.OPTIONAL);
		unsetOpt.setOptionLabel("unset");
		
		
		// cmd args
		docArg = new StringParam(DOC_ARG, "File to set the title on",
				StringParam.REQUIRED);
		docArg.setMultiValued(false);
		
		folderArg = new StringParam(FOLDER_ARG, "value of foder to set as the security folder property",
				StringParam.OPTIONAL);
		folderArg.setMultiValued(false);
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {unsetOpt}, 
					new Parameter[] { docArg, folderArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
