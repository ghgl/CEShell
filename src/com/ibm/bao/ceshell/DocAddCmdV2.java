 /**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.List;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.core.Document;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.DocUtil;


/**
 *  DocAddCmd
 *  
 *  docadd -docclass Gogo -title howdy -folder /glrtest -srcfiles e:/temp/gogo.txt 
 * @author GaryRegier
 * @date   Oct 16, 2010
 *  TODO:  Add support for properties
 */
public class DocAddCmdV2 extends BaseCommand {
	
	
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
		

		return docAddWithUtil(docClass, srcFiles, parentFolderUri, title, propsFile);
	}
	
	

	/**
	 * @param docClass
	 * @param srcFiles
	 * @param parentFolderUri
	 * @param title
	 * @param propsFile
	 * @return
	 */
	protected boolean docAddWithUtil(
			String docClass, 
			List<File> srcFiles,
			String parentFolderUri, 
			String documentTitle, 
			File propsFile) throws Exception {
		DocUtil docUtil = new DocUtil();
		docUtil.setCEShell(this.getShell());
		Document doc = null;
		boolean linkDocToFolder = false;
		
		if (parentFolderUri == null) {
			doc = docUtil.createDocument(docClass, srcFiles, documentTitle, propsFile);
		} else {
			linkDocToFolder = true;
			doc = docUtil.createDocumentAndFile(
					docClass, 
					srcFiles, 
					documentTitle, 
					propsFile, 
					parentFolderUri);
		}
		
		// response
		{
			StringBuffer msg = new StringBuffer();
			msg.append("Document created: ");
			msg.append("(id: ").append(doc.get_Id().toString()).append(") ");
			if (linkDocToFolder) {
				
				msg.append(parentFolderUri).append("/").append(doc.get_Name());
			} else {
				msg.append("Unfiled");
			}
			getResponse().printOut(msg.toString());
		}
		return true;

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

