/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.QueryHelper;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  DocFoldersCmd
 *
 * @author GaryRegier
 * @date   Jul 5, 2011
 */
public class DocFoldersCmd extends BaseCommand {
	private static final String 
		CMD = "docfolders",
		CMD_DESC = "Display the folders a document is filed in",
		HELP_TEXT = CMD_DESC;

	// param names
	private static final String 
			URI = "URI";
	
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String docUri = cl.getArg(URI).getValue().toString(); 
		return docFolders(docUri);
	}

	/**
	 * @param docId
	 */
	public boolean docFolders(String docUri) throws Exception {
		Document doc = null;
		PropertyFilter propFilter = null;
		String decodedUri = getShell().urlDecode(docUri);
		
		if (getShell().isId(decodedUri)) {
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					new Id(decodedUri), propFilter);
		   
		} else {
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					fullPath, propFilter);
		} 
		
		readPathsForDoc(doc, decodedUri);
		return true;
	}

	@SuppressWarnings("unchecked")
	private void readPathsForDoc(Document doc, String decodedUri) throws Exception {
		//TODO: Since I have to read the rcr to find out what the containmentname is,
		// I might as well use the rcrs to get the tail properties (folder) to start with.
		int numEntries = 0;
		List<String> fullPaths = new ArrayList<String>();
		StringBuffer msg = new StringBuffer();
		Iterator<Object> folders = doc.get_FoldersFiledIn().iterator();
		while (folders.hasNext()) {
			Folder folder = (Folder) folders.next();
			String rcr_entry = fetchDocEntryInParent(folder, doc);
			String fullPath = readFolderPath(folder);
			fullPath = fullPath + rcr_entry;
			fullPaths.add(fullPath);
			numEntries++;
		}
		
		
		msg.append("Folders filed in for doc with ID ").append(decodedUri);
		msg.append("\n(number of links: ").append(numEntries).append("\n");
		for(String path: fullPaths) {
			msg.append("\t").append(path).append("\n");
		}
		
		getResponse().printOut(msg.toString());
	}

	/**
	 * @param folder
	 * @param doc
	 * @return
	 * @throws Exception 
	 */
	private String fetchDocEntryInParent(Folder folder, Document doc) throws Exception {
		Id docId = doc.get_Id();
		String containmentName = null;
		
		String qry = "select ContainmentName from ReferentialContainmentRelationship r where r.head = Object(\'" +
			docId + "\')";
		
		Object results = new QueryHelper(getShell()).executeQuerySingleValue(qry);
		containmentName = results.toString();
		return containmentName;
	}

	/**
	 * @param folder
	 * @return
	 */
	private String readFolderPath(Folder folder) {
		List<String> folderNames = new ArrayList<String>();
		folderNames.add(0, folder.get_FolderName());
		while ((folder = folder.get_Parent()) != null) {
			folderNames.add(0, folder.get_FolderName());
		}
		
		StringBuffer buf = new StringBuffer();
		for(Iterator<String> iter = folderNames.iterator(); iter.hasNext();) {
			buf.append(iter.next()).append("/");
		}
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam pathURIArg = null;
		
		// options
		
		// cmd args
		pathURIArg = new StringParam(URI, "URI indicating a document",
				StringParam.REQUIRED);
		pathURIArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
