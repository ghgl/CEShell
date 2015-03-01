/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.ReferentialContainmentRelationshipSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.DocUtil;

/**
 *  FolderImportCmd\
 *  
 *  
 *  Assume a file system on disk
 *  
 * 		temp/docs
 *		temp/docs/doca.txt
 *		temp/docs/docb.txt
 *		temp/docs/subfoldera
 *		temp/docs/subfoldera/suba-doc3.txt
 *		temp/docs/subfoldera/suba-doc4.txt
 *		temp/docs/subfolderb
 *		temp/docs/subfolderb/subb-doc5.txt
 *		temp/docs/subfolderb/subb-doc6.txt
 *
 *  In FileNet, there is also a folder called rootfolder/temp/
 *  but not docs folder 
 *  
 *  CEShell$:  folderimport  -dest /temp  c:\temp\docs
 *
 *  Pre-existing files and folders in FileNet
 *  	Assume in FleNet, we have
 *       	rootfolder/temp/docs/doca.txt
 *  	
 *  	Default behavior:  warn on pre-existing docs and folders, but continue
 *
 * @author regier
 * @date   Feb 14, 2015
 */
public class FolderImportCmd extends BaseCommand {
	
	private static final String 
		CMD = "folderimport", 
		CMD_DESC = "import a file system folder recursively to a FileNet folder",
		HELP_TEXT = CMD_DESC +
			"\nUsage:" +
			"\nfolderimpot -src <fs-dir>  -dest <FN-folder-uri> -docclass <SymbolicName>" +
			"\nfolderimport -src c:/temp/docs -dest /MyDocs -docclass MyDoc" +
			"\n\nPre-existing Files in FileNet:" +
			"\n\tFiles or folder with the same name in the target are skipped." +
			"\n\n Hidden Files and folders:\n" +
			"\n\tThis follows the UNIX convention of \"hidden\" folders or files starting with " +
			"\n\ta period character. These files are ignored by default";
	
	public static final String
		DEST_FILENET_FOLDER_URI_OPT = "dest-folder-uris",
		DOCCLASS_DEFAULT_OPT = "docclass-default",	// default docclass for new docs
		SRC_DIR_OPT = "src-fs-folder";
		
	
	/**
	 *   Hidden directories and files start with a '.' character.
	 *   By default, ignore files and folder that start with this character
	 */
	private static final char HIDDEN_FILE_OR_FOLDER_PREFIX = '.';
	
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam fileNetFolderUriOpt = (StringParam) cl.getOption(DEST_FILENET_FOLDER_URI_OPT);
		FileParam srcDirOpt = (FileParam) cl.getOption(SRC_DIR_OPT);
		StringParam docClassDefaultOpt = (StringParam) cl.getOption(DOCCLASS_DEFAULT_OPT);
		
		String fileNetFolderUri = null;
		String docClassDefault = null;
		File srcDir = null;
		
		fileNetFolderUri = fileNetFolderUriOpt.getValue();
		if (docClassDefaultOpt.isSet()) {
			docClassDefault = docClassDefaultOpt.getValue();
		}
		srcDir = srcDirOpt.getValue();
		
		FolderImportRequestVO requestVO = createImportVO(fileNetFolderUri, docClassDefault, srcDir);
		
		return folderImport(requestVO);
		
	}
	
	/**
	 * @param requestVO
	 * @return
	 */
	public boolean folderImport(
			FolderImportRequestVO requestVO) throws Exception {
		String fileNetFolderUri = requestVO.getFileNetFolderUri();
		Folder parentFolder = fetchFileNetFolder(fileNetFolderUri);
		if (parentFolder == null) {
			throw new Exception("Folder not found at " + fileNetFolderUri);
		}
		
		// ok to import
		int depth = 0;
		doImportFolder(requestVO, depth, requestVO.getSrcDir(), parentFolder);
		
		return true;
	}

	public FolderImportRequestVO createImportVO(
			String fileNetFolderUri,
			String docClassDefault,
			File srcDir) {
		FolderImportRequestVO requestVO = new FolderImportRequestVO();
		requestVO.setSrcDir(srcDir);
		requestVO.setFileNetFolderUri(fileNetFolderUri);
		requestVO.setDefaultDocClass(docClassDefault);
		return requestVO;
	}
	


	private Folder fetchFileNetFolder(String fileNetFolderUri) {
		String decodedUri = getShell().urlDecode(fileNetFolderUri);
		String fullPath = this.getShell().getCWD().relativePathToFullPath(decodedUri);
		Folder folder = this.getShell().getFolder(fullPath);
		
		return folder;
	}

	/**
	 * @param srcFileSystemFolder
	 * @param destFNFolder
	 * @throws Exception 
	 */
	private void doImportFolder(
			FolderImportRequestVO requestVO, 
			int curentDepth, 
			File srcFileSystemFolder, 
			Folder destFNFolder) throws Exception {
		String srcFileSystemFolderName = srcFileSystemFolder.getName();
		Folder childFNFolder = fetchOrCreateFNFolder(destFNFolder, srcFileSystemFolderName);
		List<File> childFileSysFolders = new ArrayList<File>();
		List<File> childFileSysFiles = new ArrayList<File>();
		
		File[] childFiles = srcFileSystemFolder.listFiles();
		for (File child : childFiles) {
			if (shouldIgnore(child)) {
				System.out.println("Ignoring " + child.getName() + " as hidden or ignore criteria");
			} else {
				if (child.isDirectory()) {
					childFileSysFolders.add(child);
				} else if (child.isFile()) {
					childFileSysFiles.add(child);
				}
			}
		}
		
		for (File childFileSysFolder : childFileSysFolders) {
			doImportFolder(requestVO, curentDepth + 1, childFileSysFolder, childFNFolder);
		}
		
		// import documents in this file system folder
		importFilesIntoFolder(requestVO, childFNFolder, childFileSysFiles);
	}

	/**
	 * @param child
	 * @return
	 */
	private boolean shouldIgnore(File child) {
		String name = child.getName();;
		if (name.charAt(0) == HIDDEN_FILE_OR_FOLDER_PREFIX) {
			return true;
		}
		return false;
	}

	/**
	 * @param childFNFolder
	 * @param childFileSysFiles
	 */
	private void importFilesIntoFolder(FolderImportRequestVO requestVO, Folder childFNFolder,
			List<File> childFileSysFiles) throws Exception {
		
		Set<String> existingChildren = fetchExistingChildren(childFNFolder);
		//childFNFolder.get_
		for (File childFileSysFile : childFileSysFiles) {
			String childFileName = childFileSysFile.getName();
			if (existingChildren.contains(childFileName)) {
				getResponse().printOut("found file in folder: " + childFileName);
			} else {
				try {
					importFileToFolder(requestVO, childFNFolder, childFileSysFile, childFileName);
				} catch (Exception e) {
					String childPath = childFileSysFile.getPath();
							
					System.err.println("Failed to import file " + childPath + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public Set<String> fetchExistingChildren(Folder childFNFolder) throws Exception {
		Set<String> existingChildren = new HashSet<String>();
		ReferentialContainmentRelationshipSet rcrSet =  childFNFolder.get_Containees();
		Iterator<?> iter = rcrSet.iterator();
		while (iter.hasNext()) {
			ReferentialContainmentRelationship rcr = (ReferentialContainmentRelationship) iter.next();
			String rcrName = rcr.get_ContainmentName();
			existingChildren.add(rcrName);
		}
		
		return existingChildren;
	}

	/**
	 * @param childFNFolder
	 * @param childFileSysFile
	 */
	private void importFileToFolder(
			FolderImportRequestVO requestVO,
			Folder childFNFolder, 
			File childFileSysFile, 
			String documentTitle) throws Exception {
		DocUtil docUtil = new DocUtil();
		docUtil.setCEShell(this.getShell());
		Document doc = null;
		List<File> srcFiles = new ArrayList<File>();
		srcFiles.add(childFileSysFile);
		doc = docUtil.createDocumentAndFile(
				requestVO.getDefaultDocClass(), 
				srcFiles, 
				documentTitle, 
				null,
				childFNFolder.get_PathName());
		
		String fullPath = childFNFolder.get_PathName() + "/" + doc.get_Name();
		getResponse().printOut("imported " + fullPath);
	}
	

	/**
	 * @param destFNFolder
	 * @param srcFileSystemFolderName
	 * @return
	 */
	private Folder fetchOrCreateFNFolder(Folder destFNFolder,
			String srcFileSystemFolderName) {
		
		Iterator<?> iter = destFNFolder.get_SubFolders().iterator();
		while (iter.hasNext()) {
			Folder nextChildFolder = (Folder) iter.next();
			String childName = nextChildFolder.get_FolderName();
			if (srcFileSystemFolderName.equals(childName)) {
				getResponse().printOut("found existing child folder " + nextChildFolder.get_PathName());
				return nextChildFolder;
			}
		}
		
		/** 
		 *   OK. Did not find the folder with the same name as in FileNet. 
		 *   Next, create the folder and return
		**/
		Folder newChildFolder = destFNFolder.createSubFolder(srcFileSystemFolderName);
		newChildFolder.save(RefreshMode.REFRESH);
		getResponse().printOut("Created child folder " + newChildFolder.get_PathName());
		return newChildFolder;
			
	}
		

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam destFilenetFolderUriOpt = null;
		StringParam docClassDefaultOpt = null;
		FileParam srcDirOpt = null;

		// Options
		{
			srcDirOpt = new FileParam(SRC_DIR_OPT,
					"local file system folder",
					FileParam.IS_DIR & FileParam.IS_READABLE,
					FileParam.REQUIRED);
		}
		
		{
			destFilenetFolderUriOpt = new StringParam(DEST_FILENET_FOLDER_URI_OPT, "folder-uri",
					StringParam.REQUIRED);
			destFilenetFolderUriOpt.setMultiValued(Boolean.FALSE);
			destFilenetFolderUriOpt.setOptionLabel("<dest-filenet-folder-uri>");
		}
		{
			docClassDefaultOpt = new StringParam(
					DOCCLASS_DEFAULT_OPT,
					"FileNet Documet class to use as default for documents (defaults to \"Document\"",
					StringParam.OPTIONAL);
			docClassDefaultOpt.setMultiValued(StringParam.SINGLE_VALUED);
			docClassDefaultOpt.setOptionLabel("<docclass-default");
		}
		
		
		// cmd args
		
		
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {srcDirOpt, destFilenetFolderUriOpt, docClassDefaultOpt}, 
					new Parameter[] { });
		cl.setDieOnParseError(false);
		
		return cl;
	}

}

class FolderImportRequestVO {
	static final String 
		DEFALT_DOCCLASS = "Document",
		DEFALT_FOLDER_CLASS = "Folder";	
	
	private String defaultDocClass = "Document";
	private String defaultFolderClass = "Folder";
	
	private File srcDir;
	private String fileNetFolderUri;
	
	
	public FolderImportRequestVO() {
		super();
		this.defaultDocClass = FolderImportRequestVO.DEFALT_DOCCLASS;
		this.defaultFolderClass = FolderImportRequestVO.DEFALT_FOLDER_CLASS;
	}
	
	public FolderImportRequestVO(File inputDir, String fileNetFolderUri) {
		super();
		this.srcDir = inputDir;
		this.fileNetFolderUri = fileNetFolderUri;
	}
	public File getSrcDir() {
		return srcDir;
	}
	public void setSrcDir(File outputDir) {
		this.srcDir = outputDir;
	}
	public String getFileNetFolderUri() {
		return fileNetFolderUri;
	}
	public void setFileNetFolderUri(String fileNetFolderUri) {
		this.fileNetFolderUri = fileNetFolderUri;
	}

	public String getDefaultDocClass() {
		return defaultDocClass;
	}

	public void setDefaultDocClass(String defaultDocClass) {
		if (defaultDocClass != null) {
			this.defaultDocClass = defaultDocClass;
		}
	}

	public String getDefaultFolderClass() {
		return defaultFolderClass;
	}

	public void setDefaultFolderClass(String defaultFolderClass) {
		this.defaultFolderClass = defaultFolderClass;
	}
	
}

