package com.ibm.bao.ceshell;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.StringUtil;

public class SelectCommand extends BaseCommand {
	
	private static final String
		DEFAULT_DEMIMITER = "\t";
	
	public static final String 
			CSV_OPT = "csv",
			OUTPUT_FILE_OPT = "file",
			QUERY_ARG = "query";
	
	private static final String 
		CMD = "select", 
		CMD_DESC = "execute a query",
		HELP_TEXT = "select <att-list | *> from <doctype> where <criteria>";
		
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam queryArgParam = (StringParam) cl.getArg(QUERY_ARG);
		BooleanParam csvFmtOpt = (BooleanParam) cl.getOption(CSV_OPT);
		FileParam outputFileOpt = (FileParam) cl.getOption(OUTPUT_FILE_OPT);
		File outputFile = null;
		Boolean csvFmt = Boolean.FALSE;
		String delimiter = DEFAULT_DEMIMITER;
		String query = null;
		
		if (csvFmtOpt.isSet()) {
			csvFmt = csvFmtOpt.getValue();
		}
		
		if (outputFileOpt.isSet()) {
			outputFile = outputFileOpt.getValue();
		}
		List<String> queryArgs = queryArgParam.getValues();
		query = createQuery(queryArgs);
		return select(query, outputFile, csvFmt, delimiter);
	}

//	@Override
//	protected void doRun(CmdLineHandler cmdLine) throws Exception {
//		String mySQLString = request.getCmdLine();
//	    select(mySQLString);
//	}

	public boolean select(String mySQLString, File outputFile, Boolean csvFmt, String delimiter) throws Exception {
		SearchSQL sqlObject = new SearchSQL();
	    sqlObject.setQueryString(mySQLString);
	    PrintStream out = null;
	    SearchScope searchScope = new SearchScope(getShell().getObjectStore());
	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    
	    try {
	    	if (outputFile != null) {
	    		out = openOutput(outputFile);
	    	}
		    if (csvFmt) {
		    	displayCsvResults(rowSet, out, delimiter);
		    } else {
		    	displayStdResults(rowSet, out);
		    }
		    return true;
	    } finally {
	    	if (out != null) {
	    		try {
	    			out.close();
	    			out = null;
	    		} catch (Exception e) {
	    			// no-op
	    		}
	    	}
	    }
	}

	/**
	 * @param queryArgs
	 * @return
	 */
	private String createQuery(List<String> queryArgs) {
		StringBuffer buf = new StringBuffer();
		buf.append("select ");
		for (String arg : queryArgs) {
			buf.append(arg).append(" ");
		}
		return buf.toString();
	}
	
	public PrintStream openOutput(File outputFile) throws Exception {
		PrintStream out = null;
		
		out = new PrintStream(outputFile);
		return out;
	}

	/**
	 * @param rowSet
	 */
	private void displayCsvResults(RepositoryRowSet rowSet, PrintStream output, String delimiter) {
		String[] fieldNames = null;
		int pos = 0;
		
		for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();pos++) {
			RepositoryRow row = (RepositoryRow) iter.next();
			String[] fieldValues = null;
			Properties props =  row.getProperties();
			String rowStr = null;
			
			if (fieldNames == null) {
				String header = null;
				
				fieldNames = readFieldNames(props);
				header = StringUtil.appendArrayToString("", delimiter, fieldNames);
				
				outputLine(output, header);
			}
			fieldValues = new String[fieldNames.length];
			for (int i = 0; i < fieldValues.length; i++ ) {
				
				Property prop = props.get(fieldNames[i]);
				Object value = prop.getObjectValue();
				String valueStr = "";
				
				if (value != null) {
					valueStr = value.toString();
				}
				fieldValues[i] = valueStr;
			}
			rowStr = StringUtil.appendArrayToString("", delimiter, fieldValues);
			outputLine(output, rowStr);
		}
		getResponse().printOut("completed processing of " + pos + " records");

	}

	public void outputLine(PrintStream output, String header) {
		if (output != null) {
			output.println(header);
		} else {
			getResponse().printOut(header);
		}
	}

	/**
	 * @param props
	 */
	private String[] readFieldNames(Properties props) {
		String[] fieldNames = new String[props.size()];
		int pos = 0;
		
		for (Iterator<?> propIter = props.iterator(); propIter.hasNext(); ) {
			Property prop = (Property) propIter.next();
			fieldNames[pos++] = prop.getPropertyName();
		}
		
		return fieldNames;
	}

	public void displayStdResults(RepositoryRowSet rowSet, PrintStream output) {
		int pos = 0;
		
		for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();pos++) {
			RepositoryRow row = (RepositoryRow) iter.next();
			outputLine(output, "#" + pos);
			for (Iterator<?> propIter = row.getProperties().iterator(); propIter.hasNext(); ) {
				StringBuffer buf = new StringBuffer();
				Property prop = (Property) propIter.next();
				String name = prop.getPropertyName();
				Object value = prop.getObjectValue();
				String valueStr = "";
				if (value != null) {
					valueStr = value.toString();
				}
				buf.append("\t").append(StringUtil.padLeft(name, ".",30)).append(valueStr);
				outputLine(output, buf.toString());
			}	
		}
		getResponse().printOut("completed processing of " + (pos - 1) + " records");
	} 


	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam queryArgs = null;
		BooleanParam csvOpt = null;
		FileParam outputFileOpt = null;
		
		// options
		csvOpt = new BooleanParam(CSV_OPT, "output in csv");
		csvOpt.setOptional(BooleanParam.OPTIONAL);
		csvOpt.setMultiValued(BooleanParam.SINGLE_VALUED);
		
		outputFileOpt = new FileParam(OUTPUT_FILE_OPT, 
				"output file",
				FileParam.OPTIONAL);
		outputFileOpt.setMultiValued(FileParam.SINGLE_VALUED);
		
		
		// cmd args
		queryArgs = new StringParam(QUERY_ARG, "Select statement",
				StringParam.REQUIRED);
		queryArgs.setMultiValued(true);
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {csvOpt, outputFileOpt}, 
					new Parameter[] {queryArgs});
		cl.setDieOnParseError(false);

		return cl;
		
	}
}
