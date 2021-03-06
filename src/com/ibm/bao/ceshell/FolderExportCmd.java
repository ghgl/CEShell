/**
 * 
 */
package com.ibm.bao.ceshell;



import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.core.ContentElement;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;




/**
 *  FolderExportCmd
 *
 * @author regier
 * @date   Oct 20, 2014
 */
public class FolderExportCmd extends BaseCommand {
	
	private static final String 
		CMD = "folderexport", 
		CMD_DESC = "export a folder to a local folder. If the recusion flag is set, then recurisvely export",
		HELP_TEXT = CMD_DESC +
			"\nUsage:" +
			"\n\tfolderexport -outdir <out-dir> <fn-folder-uri>" +
			"\nfolderexport -outdir e:/temp  my-folder" +
			"\n\texport foo.txt to e:/temp/mydoc.txt. If e:/temp/mydoc.txt " +
			"\n\texists, then it is overwritten";
		
	public static final String
		OUTPUT_DIR_OPT = "outdir",
		RECURSION_OPT = "recursive",
		FILENET_FOLDER_URIS_ARG = "folder-uris";
		

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		FileParam outputDirOpt = (FileParam) cl.getOption(OUTPUT_DIR_OPT);
		BooleanParam recursionOpt = (BooleanParam) cl.getOption(RECURSION_OPT);
		StringParam fileNetFolderUrisArg = (StringParam) cl.getArg(FILENET_FOLDER_URIS_ARG);
		
		File outputDir = outputDirOpt.getValue();
		Boolean recursion = Boolean.FALSE;
		if (recursionOpt.isSet()) {
			recursion = recursionOpt.getValue();
		}
		List<String> fileNetFolderUris = fileNetFolderUrisArg.getValues();
		
		FolderExportRequestVO requestVO = createExportVO(outputDir, fileNetFolderUris);
		
		return exportFolders(requestVO, recursion);
		
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	/**
	 * @param outputDir
	 * @param fileNetFolderUris
	 */
	public boolean exportFolders(FolderExportRequestVO requestVO, boolean recursive) {
		List<Folder> srcFolders = new ArrayList<Folder>();
		List<String> fileNetFolderUris = requestVO.getFileNetFolderUris();
		
		for (String folderuri : fileNetFolderUris) {
			String decodedUri = getShell().urlDecode(folderuri);
			String fullPath = this.getShell().getCWD().relativePathToFullPath(decodedUri);
			Folder folder = this.getShell().getFolder(fullPath);
			srcFolders.add(folder);
		}
		
		
		
		doExportFolders(requestVO, srcFolders, recursive);
		return true;
	}

	private FolderExportRequestVO createExportVO(File outputDir,
			List<String> fileNetFolderUris) {
		FolderExportRequestVO requestVO = new FolderExportRequestVO();
		requestVO.setOutputDir(outputDir);
		requestVO.setFileNetFolderUris(fileNetFolderUris);
		return requestVO;
	}


	/**
	 * @param outputDir
	 * @param srcFolders
	 */
	private void doExportFolders(FolderExportRequestVO requestVO, List<Folder> srcFolders, boolean recursive) {
		int depth = 0;
		File outputDir = requestVO.getOutputDir();
		
		for (Folder srcFolder : srcFolders) {
			doExportFolder(depth, outputDir, srcFolder, recursive);
		}
		
	}

	/**
	 * @param outputDir
	 * @param srcFolder
	 */
	private void doExportFolder(int curentDepth, File outputDir, Folder srcFolder, boolean recursive) {
		String folderName = srcFolder.get_FolderName();
		File localOutDir = new File(outputDir, folderName);
		localOutDir.mkdir();
		
		if (recursive == true) {
			Iterator<?> iter = srcFolder.get_SubFolders().iterator();
			while (iter.hasNext()) {
				Folder childFolder = (Folder) iter.next();
				doExportFolder(curentDepth + 1, localOutDir, childFolder, recursive);
			}
		}
		
		// export all docs in current director
		
			
		Iterator<?> fileIter = srcFolder.get_ContainedDocuments().iterator();
		while (fileIter.hasNext()) {
			Document nextDoc = (Document) fileIter.next();
			try {
				doExportDoc(localOutDir, nextDoc);
			} catch (Exception e) {
				getResponse().printErr("Failed to export document: " + e.getMessage());
			}
		}
	}
	

	/**
	 * Export just the first  content element
	 * @param localOutDir
	 * @param nextDoc
	 */
	private void doExportDoc(File localOutDir, Document nextDoc)  throws Exception {
			ContentElementList ceList = nextDoc.get_ContentElements();
			String docName = nextDoc.get_Name();
			
			if (ceList.size() == 0) {
				// TODO: empty-size file
				return;
			}
			
			ContentElement nextContent = (ContentElement) ceList.get(0);
			ContentTransfer ct = (ContentTransfer) nextContent;
			Double contentSize = ct.get_ContentSize();
			File nextOutputFile = new File(localOutDir, docName);

			storeLocalFile(nextOutputFile, contentSize, ct);
			getResponse().printOut("\t" + nextOutputFile.toString());
		}

		private void storeLocalFile(
				File actualFileStored,  
				Double contentSize,
				ContentTransfer ct) throws Exception {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				inputStream = ct.accessContentStream();
				outputStream = new FileOutputStream(actualFileStored);
				byte[] nextBytes = new byte[64000];
				int nBytesRead;
				while ((nBytesRead = inputStream.read(nextBytes)) != -1) {
					outputStream.write(nextBytes, 0, nBytesRead);
					outputStream.flush();
				}
				outputStream.close();
				inputStream.close();
			} finally {
				outputStream = null;
				inputStream = null;
			}
		}
		

	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		FileParam outputDirOpt = null;
		BooleanParam recursionOpt = null;
		StringParam filenetFolderUrisArg = null;


		// params
		outputDirOpt = new FileParam(OUTPUT_DIR_OPT,
				"output directory",
				FileParam.IS_DIR & FileParam.IS_WRITEABLE,
				FileParam.REQUIRED);
		outputDirOpt.setOptionLabel("<output dir>");
		
		recursionOpt = new BooleanParam(RECURSION_OPT, "recurively export folder");
		recursionOpt.setOptional(BooleanParam.OPTIONAL);

	
		// cmd args
		filenetFolderUrisArg = new StringParam(FILENET_FOLDER_URIS_ARG, "folder-uris",
				StringParam.REQUIRED);
		filenetFolderUrisArg.setMultiValued(Boolean.TRUE);
		
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { outputDirOpt, recursionOpt }, 
					new Parameter[] { filenetFolderUrisArg });
		cl.setDieOnParseError(false);
		
		return cl;
	}

}

class FolderExportRequestVO {
	private File outputDir;
	private List<String> fileNetFolderUris;
	
	public FolderExportRequestVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FolderExportRequestVO(File outputDir, List<String> fileNetFolderUris) {
		super();
		this.outputDir = outputDir;
		this.fileNetFolderUris = fileNetFolderUris;
	}
	public File getOutputDir() {
		return outputDir;
	}
	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}
	public List<String> getFileNetFolderUris() {
		return fileNetFolderUris;
	}
	public void setFileNetFolderUris(List<String> fileNetFolderUris) {
		this.fileNetFolderUris = fileNetFolderUris;
	}
	
}
