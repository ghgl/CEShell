/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import org.apache.log4j.Logger;

import com.filenet.api.core.Document;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.DocUtil;
import com.ibm.bao.ceshell.util.PropertyUtil;

/**
 *  DocImportCmd
 *
 * @author regier
 * @date   Apr 22, 2014
 */
public class DocImportCmd extends BaseCommand {
	
	/**
	 * By default the processed dir is a subdir of the inputdir called "processed
	 * So, if inputdir = <code>c:\data\mail\data</code, then the default processed dir is
	 *  <code>c:\data\mail\data\processed</code>
	 */
	public static final String
		DEFUALT_PROCESSED_DIR = "processed";
	
	public static final String 
		META_PROPERTIES_EXTENSION = ".meta.properties";
	
	
	private static final String 
	CMD = "docimport", 
	CMD_DESC = "Import documents ",
	HELP_TEXT = CMD_DESC +
		"\nUsage:" +
		"\n\tdocimport -srcdir <src-dir> -processed-dir <processed-dir> -docclass <doc-class>  ";
		
	public static final String
		INPUT_DIR_OPT = "indir",
		PROCESSED_DIR_OPT = "processeddir",
		DOCCLASS_NAME_OPT = "docclass";

	static Logger logger = Logger.getLogger(DocImportCmd.class);
	
	private File processedDir = null;
	private File inputDir = null;
	
	
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		logger.debug("test log debug DocImportCmd.doRun");
		logger.info("test log info DocImportCmd.doRun");
		logger.warn("test log warn DocImportCmd.doRun");
		logger.error("test log error DocImportCmd.doRun");
		
		File inputDir = ((FileParam) cl.getOption(INPUT_DIR_OPT)).getValue();
		FileParam processedDirOpt = (FileParam) cl.getOption(PROCESSED_DIR_OPT);
		File processedDir = null;
		String docClass = ((StringParam) cl.getOption(DOCCLASS_NAME_OPT)).getValue();
		
		if (processedDirOpt.isSet()) {
			processedDir = processedDirOpt.getValue();
		} else {
			processedDir = new File(inputDir, DEFUALT_PROCESSED_DIR);
			
		}
		if (! processedDir.exists()) {
			processedDir.mkdir();
		}
		return docInport(inputDir, processedDir, docClass);
	}

	/**
	 * @param inputDir
	 * @param processedDir
	 * @param docClass
	 * @return
	 */
	private boolean docInport(File inputDir, File processedDir, String docClass) 
			throws Exception {
		logger.debug("docInport: src directory=" + inputDir.getAbsolutePath());
		this.processedDir = processedDir;
		this.inputDir = inputDir;
		
		List<DocImportVO> docsToImport = new ArrayList<DocImportVO>();
		
		initializeImports(docsToImport, inputDir);
		logger.info("docInport - documents found for import: " + docsToImport.size()); 
		executeImports(docsToImport, docClass);
			
		return true;
	}

	/**
	 * @param docsToImport
	 * @throws Exception 
	 */
	private void executeImports(List<DocImportVO> docsToImport, String docClass) throws Exception {
		logger.debug("executeImports() enter");
		
		try {
			DocUtil docUtil = new DocUtil();
			docUtil.setCEShell(this.getShell());
			getResponse().printOut("Found " + docsToImport.size() + " documents to import...");
			for (DocImportVO docImportVO : docsToImport) {
				createFNDoc(docClass, docUtil, docImportVO);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	public void createFNDoc(String docClass, DocUtil docUtil,
			DocImportVO docImportVO) throws Exception {
		logger.debug("executeImports() add document");
		
		Document doc = docUtil.createDocument(
				docClass, 
				docImportVO.getSrcFilesAsListList(), 
				docImportVO.getMetaProperties());
		com.filenet.api.util.Id id = doc.get_Id();
		String idStr = id.toString();
		docImportVO.setFnDocId(idStr);
		StringBuffer msg = new StringBuffer();
		msg.append("createFNDoc -- Imported ")
			.append(docImportVO.getMetadataFile().getName())
			.append(" as FN document ")
			.append(idStr);
		logger.info(msg.toString());
		cleanupSrcFiles(docImportVO);
		getResponse().printOut("\t" + msg);
	}

	/**
	 * @param docImportVO
	 */
	private void cleanupSrcFiles(DocImportVO docImportVO) {
		moveFile(processedDir, docImportVO.getMetadataFile());
		for (File contentFile : docImportVO.getSrcFiles()) {
			moveFile(processedDir, contentFile);
		}
	}

	/**
	 * @param processedDir
	 * @param srcFile
	 */
	private void moveFile(File processedDir, File srcFile) {
		File destFile = new File(processedDir, srcFile.getName());
		/** If the destFile exists, it has to be deleted before the rename will work **/
		if (destFile.exists()) {
			destFile.delete();
		}
		logger.info("DocImportCmd.moveFile - moving file to processed:" + srcFile.getAbsolutePath());
		boolean ok = srcFile.renameTo(destFile);
		if (ok) {
			logger.info("DocImportCmd.moveFile -- file moved to " + destFile.getAbsolutePath());
		} else {
			/** try to rename with a ".err" extension **/
			logger.error("DocImportCmd.moveFile - failed to delete srcFile " + srcFile.getAbsolutePath());
			logger.error("DocImportCmd.moveFile - ...try to rename wtih a .err extension...");
			String errFilename = srcFile.getName() + ".err";
			File errFile = new File(inputDir, errFilename);
			ok = srcFile.renameTo(errFile);
			
			if (ok) {
				logger.warn("DocImportCmd.moveFile - rename with .err success: " + errFile.getAbsolutePath());
			} else {
				logger.error("DocImportCmd.moveFile - rename with .err failed!!!: " + errFile.getAbsolutePath());
			}
		}
		
	}

	/**
	 * @param docsToImport
	 * @throws Exception 
	 */
	private void initializeImports(List<DocImportVO> docsToImport, File inputDir) 
			throws Exception {
		File[] metaFiles = inputDir.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith(META_PROPERTIES_EXTENSION)) {
					logger.debug("initializeImports: metadata file found: " + name);
					return true;
				} else {
					return false;
				}
			}
			
		});
		if (metaFiles.length == 0) {
			logger.info("No files found for import");
			return;
		}
		
		
		for (File metaPropsFile : metaFiles) {
			String metaFileName = metaPropsFile.getName();
			int pos = metaFileName.lastIndexOf(META_PROPERTIES_EXTENSION);
			String contentIdentifier = metaFileName.substring(0, pos);
						
			DocImportVO docImportVO = new DocImportVO(contentIdentifier);
			docImportVO.loadMetaProperties(metaPropsFile);
			File[] srcFiles = inputDir.listFiles(new ContentFileFilter(metaFileName, contentIdentifier));
			docImportVO.setSrcFile(srcFiles);
			
			docsToImport.add(docImportVO);
		}
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		FileParam inputDirOpt = null;
		FileParam processedDirOpt = null;
		StringParam docClassOpt = null;

		// params
		inputDirOpt = new FileParam(INPUT_DIR_OPT,
				"input directory -- location of source files",
				FileParam.IS_DIR & FileParam.IS_WRITEABLE,
				FileParam.REQUIRED);
		inputDirOpt.setOptionLabel("<input dir>");
		
		processedDirOpt = new FileParam(PROCESSED_DIR_OPT,
				"processed directory -- location to move processed files (default is \"processed\" subdir of input-dir",
				FileParam.IS_DIR & FileParam.IS_WRITEABLE,
				FileParam.OPTIONAL);
		processedDirOpt.setOptionLabel("<processed-files-dir>");
		
		docClassOpt = new StringParam(DOCCLASS_NAME_OPT, "document classname",
				StringParam.REQUIRED);
		docClassOpt.setMultiValued(false);
		docClassOpt.setOptionLabel("docclass");

	
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { inputDirOpt, processedDirOpt, docClassOpt }, 
					new Parameter[] { });
		cl.setDieOnParseError(false);

		return cl;
	}

}

class DocImportVO {
	
	@SuppressWarnings("unused")
	private String contentIdentifier;
	private File[] srcFiles;
	private File metadataFile;
	private String fnDocId;
	private Properties metaProps;
	
	public DocImportVO(String contentIdentifier) {
		this.contentIdentifier = contentIdentifier;
	}
	
	public Properties getMetaProperties() {
		return metaProps;
	}
	
	public File[] getSrcFiles() {
		return srcFiles;
	}
	public File getMetadataFile() {
		return metadataFile;
	}
	public void setSrcFile(File[] srcFiles) {
		this.srcFiles = srcFiles;
	}
	
	public String getFnDocId() {
		return fnDocId;
	}
	public void setFnDocId(String fnDocId) {
		this.fnDocId = fnDocId;
	}
	
	public void loadMetaProperties(File metaPropertiesFile) throws Exception {
		this.metadataFile = metaPropertiesFile;
		PropertyUtil util = new PropertyUtil();
		
		this.metaProps = util.loadPropertiesFromFile(metaPropertiesFile);
	}
	
	public List<File> getSrcFilesAsListList() {
		List<File> files = new ArrayList<File>();
		for (File file : srcFiles) {
			files.add(file);
		}
		return files;
	}
	
}

class ContentFileFilter implements FilenameFilter {
	
	private String metaPropertiesFilename;
	private String contentIdentifier;
	
	
	public ContentFileFilter(String metaPropertiesFilename, String contentIdentifier) {
		this.metaPropertiesFilename = metaPropertiesFilename;
		this.contentIdentifier = contentIdentifier.toLowerCase();
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		if (name.equals(metaPropertiesFilename)) return false;
		if (name.toLowerCase().startsWith(contentIdentifier)) return true;
		return false;
	}
}
