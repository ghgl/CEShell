/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.core.ContentElement;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  DocViewCmd
 *
 * @author GaryRegier
 * @date   Oct 3, 2010
 */
public class DocViewCmd extends BaseCommand {
	
	private static final String 
		CMD = "docview", 
		CMD_DESC = "Fetch a document content elements for viewing",
		HELP_TEXT = "Fetch a document content elements for viewing\n" +
			"The document can be specified by a relative path, " +
			"a full path, or a document ID" +
			"\nOn output, if a file with the same name already exists, then " +
			"\na new file is created with a random name." +
			"\nThe default output name is the same as the document name." +
			"\n\nUsage:\n\t" +
			"\n\nUsage:" +
			"\n\tdocview -outdir e:/temp -name foo.txt foo" +
			"\n\tDocument foo locally as e:/temp/foo.txt" +
			"\ndocview -outdir e:/temp -noclobber false -n mydoc.txt foo.txt" +
			"\n\texport foo.txt to e:/temp/mydoc.txt. If e:/temp/mydoc.txt " +
			"\n\texists, then it is overwritten";

	// param names
	private static final String 
			LOCAL_NAME_OPT = "name",
			NOCLOBBER_OPT = "noclobber",
			GENERATE_VALID_NAMES_OPT = "generate-names",
			LOCAL_OUTPUT_DIR_OPT = "outdir",
			URI_ARG = "URI";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		FileParam rootOutputDirOpt = (FileParam) cl.getOption(LOCAL_OUTPUT_DIR_OPT); 
		StringParam nameOpt = (StringParam) cl.getOption(LOCAL_NAME_OPT);
		StringParam remoteDocArg = (StringParam) cl.getArg(URI_ARG);
		BooleanParam noClobberOpt = (BooleanParam) cl.getOption(NOCLOBBER_OPT);
		BooleanParam generateValidNameOpt = (BooleanParam) cl.getOption(GENERATE_VALID_NAMES_OPT);
		Boolean noClobber = Boolean.TRUE;
		Boolean generateValidName = Boolean.TRUE;
		File rootOutputDir = null;
		String docUri = null;
		String localName = null;
		
		if (rootOutputDirOpt.isSet()) {
			rootOutputDir = rootOutputDirOpt.getValue();
		} else {
			rootOutputDir = new File(System.getProperty("java.io.tmpdir"));
		}
		if (nameOpt.isSet()) {
			localName = nameOpt.getValue();
		}
		
		if (generateValidNameOpt.isSet()) {
			generateValidName = generateValidNameOpt.getValue();
		}
		
		if (noClobberOpt.isSet()) {
			noClobber = noClobberOpt.getValue();
		}
		docUri = remoteDocArg.getValue();
		return docView(docUri, rootOutputDir, localName, noClobber, generateValidName);
	}
	
	public boolean docView(String docUri, 
			File outputDir, 
			String localName,
			Boolean noClobber,
			Boolean generateValidName)throws Exception {
		PropertyFilter docFilter = createDocViewFilter();
		Document doc = null;
		NameMgr nameMgr = null;
		
		localName = this.decodePath(localName);
		if (getShell().isId(docUri)) {
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					new Id(docUri), docFilter);
		   
		} else {
			String decodedUri = getShell().urlDecode(docUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					fullPath, docFilter);
		} 
		if (localName == null) {
			localName = doc.get_Name();
		}
		nameMgr = new NameMgr(outputDir, localName, noClobber, generateValidName);
		fetchDocContents(docUri, doc, nameMgr);
		return true;
	}
	
	/**
	 * @param rootOutputDir
	 * @param doc
	 * @param docName
	 */
	private void fetchDocContents(
			String docUri,
			Document doc,
			NameMgr nameMgr) throws Exception {
		ContentElementList ceList = doc.get_ContentElements();
		int pos = 0;
		int totalElements = ceList.size();
		File[] names = nameMgr.createNames(totalElements);
		getResponse().printOut(docUri + " saved as:");

		for (int i = 0; i < totalElements; i++) {
			ContentElement nextContent = (ContentElement) ceList.get(i);
			ContentTransfer ct = (ContentTransfer) nextContent;
			Double contentSize = ct.get_ContentSize();
			File nextOutputFile = names[i];

			storeLocalFile(nextOutputFile, contentSize, ct);
			getResponse().printOut("\t" + nextOutputFile.toString());
			pos++;
		}
		// no content elements
		if (pos == 0) {
			getResponse().printOut("\tno content elements");
		}
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

	private PropertyFilter createDocViewFilter() {
		PropertyFilter propFilter = new PropertyFilter();
		
		FilterElement fe = new FilterElement(
				new Integer(2), null, null, "Name ContentElementsPresent ContentElements ContentType ElementSequenceNumber ContentSize RetrievalName", null);
		propFilter.addIncludeProperty(fe);

		return propFilter;
	}


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam nameOpt = null;
		FileParam localDirOpt = null;
		BooleanParam noClobberOpt = null;
		BooleanParam generateValidNamesOpt = null;
		StringParam pathURIArg = null;

		// params
		nameOpt = new StringParam(LOCAL_NAME_OPT,
				"local name of file to save (doc name property by default)",
				StringParam.OPTIONAL);
		localDirOpt = new FileParam(LOCAL_OUTPUT_DIR_OPT,
				"Local output directory (system temp dir by default)",
				FileParam.IS_DIR & FileParam.IS_WRITEABLE,
				FileParam.OPTIONAL);
		localDirOpt.setOptionLabel("<output dir>");
		
		noClobberOpt = new BooleanParam(NOCLOBBER_OPT,"do not overwrite existing files");
		noClobberOpt.setOptional(BooleanParam.OPTIONAL);
		
		generateValidNamesOpt = new BooleanParam(GENERATE_VALID_NAMES_OPT, "generate valid names");
		generateValidNamesOpt.setOptional(BooleanParam.OPTIONAL);
		
		// cmd args
		pathURIArg = new StringParam(URI_ARG, "URI indicating a document",
				StringParam.REQUIRED);
		pathURIArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { nameOpt, localDirOpt, generateValidNamesOpt, noClobberOpt }, 
					new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}


