/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.impl.BulkImportBatch;
import com.ibm.bao.ceshell.impl.BulkLoadConfigInfo;
import com.ibm.bao.ceshell.impl.DocEditInfo;
import com.ibm.bao.ceshell.impl.EditInfo;
import com.ibm.bao.ceshell.util.CSVReader;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  BulkLoaderCmd
 *
 * @author regier
 * @date   Sep 8, 2011
 */
public class BulkLoaderCmd extends BaseCommand {
	
	private static final char DEFAULT_SEPARATOR_CHAR = ',';
	
	private static final String
		CSV_DATAFILE_OPT ="csvfile",
		DOCCLASS_OPT = "docclass",
		CONTENTPROPERTY_OPT = "contentproperty",
		SEPARATORCHAR_OPT  = "separatorchar";
	
	private static final String 
		CMD = "bulkload", 
		CMD_DESC = "bulk import of documents",
		HELP_TEXT = CMD_DESC + "\n" +
		"Bulk import documents into content engine. Metadata should be stored in a CSV file with each column corresponding to a " +
		"symbolic name of a property in the current object store. The default delimiter for the CSV file it a comma. " +
		"The content files associated must be listed as a field inside of the CSV file. The contentproperty opt is used to specify " + 
		"which field that references the content." +
		"The delimiter for the CSV file can be modified by passing the -separatorchar option and specifying a different character\n" +
		"During the import, a log file is written out with the results of the import. A row is added to the output file for each import. " +
		" ID of the created document is prepended to the front of the line.\n" +
		"Errors are written out to an error file. If no errors exist, this will be a zero-sized file\n\n" +
		"Example:\n" +
		"Assume you have a data file that looks like this stored at c:/temp/POC/Batch_HF_ID48.csv:\n" +
		"\tSProp|DateProp|PAGE_IMAGES\n" +
		"\tABC-123|03/15/2006|C:/temp/poc/ABC-123.pdf\n" +
		"\n\nThis can be imported wtih this command" +
		"\n\tbulkload -csv c:/temp/poc/Batch_HF_ID48.csv -d POC -co PAGE_IMAGES -sep |";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		File csvDataFile = ((FileParam) cl.getOption(CSV_DATAFILE_OPT)).getValue();
		String docClass = ((StringParam) cl.getOption(DOCCLASS_OPT)).getValue();
		String contentProperty = ((StringParam) cl.getOption(CONTENTPROPERTY_OPT)).getValue();
		StringParam separatorCharOpt = (StringParam) cl.getOption(SEPARATORCHAR_OPT);
		
		char separatorChar = DEFAULT_SEPARATOR_CHAR;
		if (separatorCharOpt.isSet()) {
			separatorChar = StringUtil.decode( separatorCharOpt.getValue()).charAt(0);
		}
		
		return bulkLoad(csvDataFile, docClass, contentProperty, separatorChar);
	}
	
	public boolean bulkLoad(
			File csvDataFile, 
			String docClass, 
			String contentProperty, 
			char separatorChar) throws Exception {
		BulkImportBatch batch = null;
		BulkLoadConfigInfo configInfo = new BulkLoadConfigInfo();
		
		{
			configInfo.setContentProperty(contentProperty);
			configInfo.setDocClass(docClass);
			configInfo.setSeparatorChar(separatorChar);
		}
		
		try {
			batch = new BulkImportBatch(configInfo, csvDataFile);
			importBatch(batch);
			return true;
		} catch (Exception e) {
			getResponse().printErr(e.getMessage());
			return false;
		} finally {
			batch.getLogger().close();
		}
	}
	
	public void importBatch(BulkImportBatch batch) throws Exception {
		CSVReader reader = new CSVReader(batch);
		List<List<String>> data = reader.load();
		List<String> header = data.remove(0);
		
		int pos = 1;
		
		batch.setHeader(header);
		for (Iterator<List<String>> iterator = data.iterator(); iterator.hasNext();) {
			List<String> record = iterator.next();
			EditInfo editInfo = null;
			try {	
				if (record.size() != batch.getHeaderSize()) {
					throw new Exception("wrong number of fields in record " 
							+ pos + " (header fields: " 
								+ batch.getHeaderSize() + ", record: " + record.size() + ")");
				}
				editInfo = createDoc(pos, batch, record);
			} catch (Exception e) {
				batch.getLogger().logErr("" + pos + "\t" + "Error: " + e.getMessage());
			}
			doAfter(batch, editInfo);
			pos++;
		}
		getResponse().printOut("\ncompleted batch import " + batch.getSrcDataFile().toString());
		getResponse().printOut("Log file written to " + batch.getLogger().getLogFileName());
		getResponse().printOut("\tsuccess documents:\t" + batch.getLogger().getSuccessCnt());
		
		int errCnt = batch.getLogger().getErrCnt();
		if (errCnt > 0) {
			getResponse().printErr("\terror documents:\t" + errCnt);
			getResponse().printErr("Error file written to " + batch.getLogger().getErrFileName());
		}
	}

	/**
	 * @param batch
	 * @param editInfo
	 */
	private void doAfter(BulkImportBatch batch, EditInfo editInfo) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param batch
	 * @param record
	 */
	private EditInfo createDoc(int pos, BulkImportBatch batch, List<String> record) 
			throws Exception{
		String msg = null;
		java.util.Properties props = createPropsFromRecord(batch, record);
		List<File> srcFiles = createSrcFilesListFromRecord(batch, record);
		String targetClassname = batch.getConfigInfo().getDocClass();
		Document doc = Factory.Document.createInstance(getShell()
					.getObjectStore(), targetClassname);
		DocEditInfo docEditInfo = new DocEditInfo(this.getShell(), doc,targetClassname);
		
		docEditInfo.setProperties(props);
		docEditInfo.addContentElements(doc, srcFiles);
		doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		doc.save(RefreshMode.REFRESH);
		
		msg = String.format("%s\t %s (%s) -- %s", 
				"" + pos, doc.get_Id(), 
				targetClassname, 
				StringUtil.listToString(record, "" + batch.getConfigInfo().getSeparatorChar()));
		batch.getLogger().logSuccess(msg);
		getResponse().printOut("document created: " + doc.get_Id().toString());
		
		return docEditInfo;
	}

	/**
	 * @param bulkImportInfo
	 * @param record
	 * @return
	 */
	private List<File> createSrcFilesListFromRecord(
			BulkImportBatch bulkImportInfo, List<String> record) {
		List<File> srcFiles = new ArrayList<File>();
		String filePath = record.get(bulkImportInfo.getContentFilesFieldIdx());
		if (filePath != null && filePath.length() > 1) {
			File nextFile = new File(filePath);
			if (nextFile.exists()) {
				srcFiles.add(nextFile);
			}
		}
		return srcFiles;
		
	}

	/**
	 * @param bulkImportInfo
	 * @param record
	 * @return
	 */
	private Properties createPropsFromRecord(BulkImportBatch bulkImportInfo,
			List<String> record) {
		Map<String, Integer> fieldsInfo = bulkImportInfo.getFieldsIdx();
		java.util.Properties props = new java.util.Properties();
		for (String propName : fieldsInfo.keySet()) {
			Integer pos = fieldsInfo.get(propName);
			String propValue = record.get(pos.intValue());
			props.put(propName, propValue);
		}
		return props;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		FileParam csvDataFileOpt = null;
		StringParam docClassOpt = null; 
		StringParam contentPropertyOpt = null;
		StringParam separatorCharOpt = null;
		

		// options
		csvDataFileOpt = new FileParam(
				CSV_DATAFILE_OPT, 
				"csv data file", 
				FileParam.IS_FILE & FileParam.IS_READABLE,
				FileParam.REQUIRED);
		
		docClassOpt = new StringParam(DOCCLASS_OPT, 
				"target document class", 
				StringParam.REQUIRED);
		contentPropertyOpt = new StringParam(
				CONTENTPROPERTY_OPT, 
				"csv file that references the content files to import", 
				StringParam.REQUIRED);
		
		separatorCharOpt = new StringParam(
				SEPARATORCHAR_OPT, 
				"csv data file separator character", 
				StringParam.OPTIONAL);
		// cmd args

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {csvDataFileOpt, docClassOpt, contentPropertyOpt, separatorCharOpt }, 
					new Parameter[] {  });
		cl.setDieOnParseError(false);

		return cl;
	}
}