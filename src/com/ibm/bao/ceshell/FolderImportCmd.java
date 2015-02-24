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

import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
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
	CMD_DESC = "import a folder recursively to a local folder",
	HELP_TEXT = CMD_DESC +
		"\nUsage:" +
		"\n\tfolderimpot -targetFileNetFolderURI <target-parent-folder>  <src-filesystem-folder>" +
		"\nfolderexport -targetFileNetFolderURI /temp  c:/temp/docs" +
		"\n\timports c:/temp/docs into /temp/docs. " +
		" If any file or folder exists within FileNet with the same name, it is skipped.";
	
	public static final String
		FILENET_FOLDER_URI_OPT = "folder-uris",
		DOCCLASS_DEFAULT = "docclass-default",	// default docclass for new docs
		SRC_DIR = "src-folder";
		

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam fileNetFolderUriOpt = (StringParam) cl.getOption(FILENET_FOLDER_URI_OPT);
		StringParam docClassDefaultOpt = (StringParam) cl.getOption(DOCCLASS_DEFAULT);
		FileParam srcDirArg = (FileParam) cl.getArg(SRC_DIR);
		String fileNetFolderUri = null;
		String docClassDefault = null;
		File srcDir = null;
		
		fileNetFolderUri = fileNetFolderUriOpt.getValue();
		if (docClassDefaultOpt.isSet()) {
			docClassDefault = docClassDefaultOpt.getValue();
		}
		srcDir = srcDirArg.getValue();
		
		
		FolderImportRequestVO requestVO = createImportVO(fileNetFolderUri, docClassDefault, srcDir);
		
		return doFolderImport(requestVO);
		
	}
	
	/**
	 * @param requestVO
	 * @return
	 */
	public boolean doFolderImport(
			FolderImportRequestVO requestVO) throws Exception {
		// TODO Auto-generated method stub
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

//	/* (non-Javadoc)
//	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
//	 */
//	/**
//	 * @param outputDir
//	 * @param fileNetFolderUris
//	 */
//	private boolean doImortFolders(FolderImportRequestVO requestVO) {
//		List<Folder> srcFolders = new ArrayList<Folder>();
//		List<String> fileNetFolderUris = requestVO.getFileNetFolderUris();
//		
//		for (String folderuri : fileNetFolderUris) {
//			String decodedUri = getShell().urlDecode(folderuri);
//			String fullPath = this.getShell().getCWD().relativePathToFullPath(decodedUri);
//			Folder folder = this.getShell().getFolder(fullPath);
//			srcFolders.add(folder);
//		}
//		
//		
//		
//		doExportFolders(requestVO, srcFolders);
//		return true;
//	}
//	
//
//
//	/**
//	 * @param outputDir
//	 * @param srcFolders
//	 */
//	private boolean doImportFolder(FolderImportRequestVO requestVO) {
//		int depth = 0;
//		File inputDir = requestVO.getInputDir();
//		
//		for (Folder srcFolder : srcFolders) {
//			doImportFolder(depth, inputDir, srcFolder);
//		}
//		
//		return true;
//		
//	}

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
			if (child.isDirectory()) {
				childFileSysFolders.add(child);
			} else if (child.isFile()) {
				childFileSysFiles.add(child);
			}
		}
		
		for (File childFileSysFolder : childFileSysFolders) {
			doImportFolder(requestVO, curentDepth + 1, childFileSysFolder, childFNFolder);
		}
		
		// import documents in this file system folder
		importFilesIntoFolder(requestVO, childFNFolder, childFileSysFiles);
	}

	/**
	 * @param childFNFolder
	 * @param childFileSysFiles
	 */
	private void importFilesIntoFolder(FolderImportRequestVO requestVO, Folder childFNFolder,
			List<File> childFileSysFiles) throws Exception {
		
		Set<String> existingChildren = new HashSet<String>();
		for (File childFileSysFile : childFileSysFiles) {
			String childFileName = childFileSysFile.getName();
			if (existingChildren.contains(childFileName)) {
				// notify child already exists
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
	
				return nextChildFolder;
			}
		}
		
		/** 
		 *   OK. Did not find the folder with the same name as in FileNet. 
		 *   Next, create the folder and return
		**/
		Folder newChildFolder = destFNFolder.createSubFolder(srcFileSystemFolderName);
		newChildFolder.save(RefreshMode.REFRESH);
		return newChildFolder;
			
	}

//	/**
//	 * Export just the first  content element
//	 * @param localOutDir
//	 * @param nextDoc
//	 */
//	private void doImportDoc(File localOutDir, Document nextDoc)  throws Exception {
//			ContentElementList ceList = nextDoc.get_ContentElements();
//			String docName = nextDoc.get_Name();
//			
//			if (ceList.size() == 0) {
//				// TODO: empty-size file
//				return;
//			}
//			
//			ContentElement nextContent = (ContentElement) ceList.get(0);
//			ContentTransfer ct = (ContentTransfer) nextContent;
//			Double contentSize = ct.get_ContentSize();
//			File nextOutputFile = new File(localOutDir, docName);
//
//			storeLocalFile(nextOutputFile, contentSize, ct);
//			getResponse().printOut("\t" + nextOutputFile.toString());
//		}
//
//		private void storeLocalFile(
//				File actualFileStored,  
//				Double contentSize,
//				ContentTransfer ct) throws Exception {
//			InputStream inputStream = null;
//			OutputStream outputStream = null;
//			try {
//				inputStream = ct.accessContentStream();
//				outputStream = new FileOutputStream(actualFileStored);
//				byte[] nextBytes = new byte[64000];
//				int nBytesRead;
//				while ((nBytesRead = inputStream.read(nextBytes)) != -1) {
//					outputStream.write(nextBytes, 0, nBytesRead);
//					outputStream.flush();
//				}
//				outputStream.close();
//				inputStream.close();
//			} finally {
//				outputStream = null;
//				inputStream = null;
//			}
//		}
		

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam filenetFolderUriOpt = null;
		StringParam docClassDefaultOpt = null;
		FileParam srcDirArg = null;

		// params
		{
			filenetFolderUriOpt = new StringParam(FILENET_FOLDER_URI_OPT, "folder-uri",
					StringParam.REQUIRED);
			filenetFolderUriOpt.setMultiValued(Boolean.FALSE);
			filenetFolderUriOpt.setOptionLabel("<dest-filenet-folder-uri>");
		}
		{
			docClassDefaultOpt = new StringParam(
					DOCCLASS_DEFAULT,
					"FileNet Documet class to use as default for documents (defaults to \"Document\"",
					StringParam.OPTIONAL);
			docClassDefaultOpt.setMultiValued(StringParam.SINGLE_VALUED);
			docClassDefaultOpt.setOptionLabel("<docclass-default");
		}
	
		// cmd args
		
		srcDirArg = new FileParam(SRC_DIR,
				"local file system folder",
				FileParam.IS_DIR & FileParam.IS_WRITEABLE,
				FileParam.REQUIRED);
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { filenetFolderUriOpt, docClassDefaultOpt }, 
					new Parameter[] {srcDirArg });
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

