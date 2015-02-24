package com.ibm.bao.ceshell;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.FolderSet;
import com.filenet.api.collection.ReferentialContainmentRelationshipSet;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.property.Properties;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.constants.DP;
import com.ibm.bao.ceshell.view.FEMListLongFormatter;
import com.ibm.bao.ceshell.view.FemLongListingItem;

/**
 * 
 *  LSCommand
 *  List information about a directory
 *  
 *  Unix ls command information:  http://www.thegeekstuff.com/2009/07/linux-ls-command-examples/
 *  <p> <b> Unix Fields:</b>
 *  <Ol>
 *  	<li> first char type of entry:
 *  		<ol><li> - Normal file</li>
 *  	        <li> d directory </li>
 *  			<li> l link</li>
 *              <li> s socket (not applicable )</li>
 *              <li> c custom (unique for CEShell)</li>
 *           </ol>
 *       <li> field 1 file permissions
 *       <li> field 2 number of links
 *       <li> field 3 Owner
 *       <li> field 4 Group
 *       <li> field 5 size
 *       <li> field 6 last modified date time
 *       <li> name
 *  </ol>
 *  <p><b>FEM fields </b>
 *  <br>
 *  <ol>
 *  	<li> Containment name </li>
 *  	<li> size
 *  	<li> Created date
 *  	</li> Creator
 *  	<li> class
 *  	<li> Major version
 *  	<li> Minor version
 *  	<li> Status (LockStatus)
 * </ol>
 * @author GaryRegier
 * @date   Sep 18, 2010
 */
public class LSCmd extends BaseCommand {

	private static final String 
			CMD = "ls", 
			CMD_DESC = "list files",
			HELP_TEXT = "List files and/or directories from the repository\n" +
			"Encode special characters such as spaced by URLEncoding them. For example, " +
			" a space character can be encoded by either a plus-sign (+) or %20\n\n" +
			 "Usage:  \n"  +
			 "\tls                    list the current directory\n" +
			 "\tls -long .               list long the current directory\n" +
			 "\tls ..                 list the parent directory\n" +
			 "\tls -l ./foo/baz/gogo  list long the foo/baz/gogo\n" +
			 "\tls -l gogo+name       list the folder \"gogo name\" (folder name with a space";

	// param names
	private static final String 
			DIRECTORY_OPT = "directory", 
			LONG_OPT = "long",
			FILE = "FILE";
	
	// default values 
	// default path
	private static final String DEFAULT_FILES = ".";
	
	
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam directoryOpt = (BooleanParam) cl.getOption(DIRECTORY_OPT);
		BooleanParam longOpt = (BooleanParam) cl.getOption(LONG_OPT);
		StringParam filePatternArg = (StringParam) cl.getArg(FILE);
		boolean listLong = false;
		boolean listDirectories = false;
		String filePattern = null;
		
		if (longOpt.isSet()) {
			listLong = longOpt.getValue();
		}
		if (directoryOpt.isSet()) {
			listDirectories = directoryOpt.getValue();
		}
		if (filePatternArg.isSet()) {
			filePattern = filePatternArg.getValue();
		}
		
		return list(filePattern, listLong, listDirectories);
	}
	
	public boolean list(
			String filePattern, 
			boolean listLong,
			boolean listDirectories) throws Exception {
		String fullPath = null;
		String path = readPathFromArg(filePattern);
		if (path.startsWith(CWD.PATH_DELIM)) {
			fullPath = path;
		} else {
			fullPath = getShell().getCWD().relativePathToFullPath(path);
		}
		if (listLong) {
			listLong(fullPath);
		} else {
			listShort(fullPath);
		}	
		return true;
	}

	private String readPathFromArg(String encodedPath) {
		String decodedPath = null;
	
		if (encodedPath != null) {
			decodedPath = this.decodePath(encodedPath);
		}
		
		if (decodedPath == null) {
			decodedPath = DEFAULT_FILES;
		}
		
		return decodedPath;
	}
	
	/**
	 * Long listing
	 * <type> <name> <created> <creator> <class> <major-version> <minor-version> <status>
	 * 
	 * A folder can have
	 * 	-- child folders
	 * 	-- custom objects
	 *  -- documents
	 *  
	 *  Get subfolders by: currentFolder.get_SubFolders();
	 *  
	 * 
	 * @param fullPath
	 */
	private void listLong(String fullPath) {
		Folder currentFolder;
		FEMListLongFormatter formatter = new FEMListLongFormatter();
		currentFolder = getShell().getFolder(fullPath);
		FolderSet subFolders = currentFolder.get_SubFolders();
		
		listLongFemFolders(formatter, subFolders);
		listLongFemDocs(formatter, fullPath);
		listLongFemCustItems(formatter, fullPath);
	}

	private void listLongFemFolders(FEMListLongFormatter formatter, 
			FolderSet subFolders) {

		SortedSet<FemLongListingItem> folderSet = 
			new TreeSet<FemLongListingItem>(new Comparator<FemLongListingItem>() {

				public int compare(FemLongListingItem o1, FemLongListingItem o2) {
					return o1.getContainmentName().compareTo(o2.getContainmentName());
				}
			});
		String result = null;
		for (Iterator<?> iter = subFolders.iterator(); iter.hasNext();) {
	    	
			Folder child = (Folder) iter.next();
			FemLongListingItem folderItem = readFemFolder(child);
			folderSet.add(folderItem);
		}
		
		for (FemLongListingItem folderItem : folderSet) {
			StringBuffer buf = new StringBuffer();
			formatter.formatDocRow(buf, folderItem);
			result = buf.toString();
			getResponse().printOut(result);
		}
	}

	private FemLongListingItem readFemFolder(Folder child) {
		FemLongListingItem folderItem = new FemLongListingItem();
		folderItem.setItemTypeIndicator(FemLongListingItem.FOLDER);
		
		String name = child.get_FolderName() + "/";
		Date creationDate = child.get_DateCreated();
		String creator = child.get_Creator();
		String clsName = child.getClassName();
		
		folderItem.setContainmentName(name);
		folderItem.setDateCreated(creationDate);
		folderItem.setCreator(creator);
		folderItem.setClassDescriptionName(clsName);
		
		return folderItem;
	}
	
	/**
	 * @param formatter
	 * @param fullPath
	 */
	private void listLongFemCustItems(FEMListLongFormatter formatter,
			String fullPath) {
		String query = "select cust.Id, cust.ClassDescription, r.ContainmentName, cust.DateCreated, cust.Creator FROM CustomObject cust INNER JOIN ReferentialContainmentRelationship r ON cust.This = r.Head  WHERE r.Tail = OBJECT(\'" + fullPath + "\') order by r.ContainmentName asc";
		SearchSQL sqlObject = new SearchSQL();
	    sqlObject.setQueryString(query);
	    SearchScope searchScope = new SearchScope(getShell().getObjectStore());
	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    
	    for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
	    	String result = null;
	    	StringBuffer buf = new StringBuffer();
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	com.filenet.api.property.Properties props = 
				row.getProperties();
	    	FemLongListingItem item = readCustItem(props);
	    	
	    	formatter.formatCustomObjectRow(buf, item);
	    	result = buf.toString();
	    	getResponse().printOut(result);
	    }
		
	}

	private void listLongFemDocs(FEMListLongFormatter formatter, String fullPath) {
		String query = "select d.Id, d.ClassDescription, d.IsReserved, r.ContainmentName, d.ContentSize,  d.DateCreated, d.Creator, d.MajorVersionNumber, d.MinorVersionNumber,d.VersionStatus FROM Document d INNER JOIN ReferentialContainmentRelationship r ON d.This = r.Head  WHERE r.Tail = OBJECT(\'" + fullPath + "\') order by r.ContainmentName asc";
		SearchSQL sqlObject = new SearchSQL();
	    sqlObject.setQueryString(query);
	    SearchScope searchScope = new SearchScope(getShell().getObjectStore());
	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    
	    for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
	    	String result = null;
	    	StringBuffer buf = new StringBuffer();
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	com.filenet.api.property.Properties props = 
				row.getProperties();
	    	FemLongListingItem item = readDocItem(props);
	    	
	    	formatter.formatDocRow(buf, item);
	    	result = buf.toString();
	    	getResponse().printOut(result);
	    }
	}
	
	protected FemLongListingItem readDocItem(com.filenet.api.property.Properties props) {
		FemLongListingItem item = new FemLongListingItem();
		Id cdId = null;
		Integer vs = null;
		
    	item.setItemTypeIndicator(FemLongListingItem.DOCUMNET);
    	item.setIsReserved(props.getBooleanValue("IsReserved"));
		item.setContainmentName(props.getStringValue("ContainmentName"));
		item.setContentSize(props.getFloat64Value("ContentSize"));
		item.setDateCreated(props.getDateTimeValue("DateCreated"));	
		item.setCreator(props.getStringValue("Creator"));
		cdId = props.getIdValue("ClassDescription");
		item.setClassDescriptionName(fetchClassDescriptionName(cdId)); 
		item.setMajorVersionNumber(props.getInteger32Value("MajorVersionNumber"));
		item.setMinorVersionNumber(props.getInteger32Value("MinorVersionNumber"));
		vs = props.getInteger32Value("VersionStatus");
		item.setVersionStatus((String) DP.VSLabels.get(new Integer(vs)));
		return item;
	}

	/**
	 * @param props
	 * @return
	 */
	private FemLongListingItem readCustItem(Properties props) {
		FemLongListingItem item = new FemLongListingItem();
    	item.setItemTypeIndicator(FemLongListingItem.CUSTOM_OBJECT);
    	item.setIsReserved(false);
		item.setContainmentName(props.getStringValue("ContainmentName"));
		item.setContentSize(0d);
		item.setDateCreated(props.getDateTimeValue("DateCreated"));	
		item.setCreator(props.getStringValue("Creator"));
		Id cdId = props.getIdValue("ClassDescription");
		item.setClassDescriptionName(fetchClassDescriptionName(cdId)); 
		item.setMajorVersionNumber(0);
		item.setMinorVersionNumber(0);
		
		item.setVersionStatus("");
		return item;
	}
	
	/**
	 * 
	 * <b>listShort</b>
	 * <p> private void listShort(String fullPath) throws Exception
	 * <p>
	 * List the entries with just the name. Each one is on a separate line. 
	 * The items are listed with folders first. All items are ordered by 
	 * containment name
	 * 
	 * @param fullPath: String -- the directory to list
	 */
	private void listShort(String fullPath) throws Exception {
		//This could be done with a query
		// SELECT f.ObjectType, f.Id, f.FolderName, f.ContainerType, f.ClassDescription, f.DateCreated FROM Folder f WHERE f.This infolder '/TestFolder' ORDER BY FolderName
		SortedSet<String> folderSet = new TreeSet<String>();
		SortedSet<String> containees = new TreeSet<String>();
		Folder currentFolder;
		currentFolder = getShell().getFolder(fullPath);
		FolderSet subFolders = currentFolder.get_SubFolders();
		ReferentialContainmentRelationshipSet children = 
			currentFolder.get_Containees();
		
		for (Iterator<?> iter = subFolders.iterator(); iter.hasNext();) {
			Folder child = (Folder) iter.next();
			folderSet.add(child.get_FolderName());
		}
		for (Iterator<String> iter = folderSet.iterator(); iter.hasNext();) {
			String folderName = iter.next();
			getRequest().getResponse().printOut(folderName + "/");
		}
		
		for (Iterator<?> iter = children.iterator(); iter.hasNext();) {
			ReferentialContainmentRelationship child = 
						(ReferentialContainmentRelationship) iter.next();
			String name = child.get_Name();
			containees.add(name);
		}
		
		for (Iterator<String> iter = containees.iterator(); iter.hasNext();) {
			String name = iter.next();
			getRequest().getResponse().printOut(name);
		}
	}

	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam directoryOpt = null;
		BooleanParam longOpt = null;
		StringParam filePatternArg = null;

		// options
		directoryOpt = new BooleanParam(DIRECTORY_OPT,
				"list directory entries");
		longOpt = new BooleanParam(LONG_OPT,
				"use a long listing format");
		
		// cmd args
		filePatternArg = new StringParam(FILE, "FILEs to list",
				StringParam.OPTIONAL);
		filePatternArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { directoryOpt, longOpt }, 
					new Parameter[] { filePatternArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
