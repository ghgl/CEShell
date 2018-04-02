/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.List;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.DocUpdateDTO;
import com.ibm.bao.ceshell.util.DocUtil;

/**
 *  DocVersionCmd
 *
 * @author regier
 * @date   Mar 20, 2015
 */
public class DocCheckinCmd extends BaseCommand {
	
	
	private static final String 
	CMD = "docversion", 
	CMD_DESC = "Create a new version of a document",
	HELP_TEXT = "Usage:" +
		"\n\tProperties may be stored in a properties file." +
	  	"\n\tdocversion -srcfiles e:/temp/foo.txt /test/myfolder/foo.pdf" +
	  	"\n\t creates a new version of  foo.txt in folder /test/myfolder" + 
	  	"\n\n\tdocversion -srcfiles e:/temp/foo.txt -propsfile e:/temp/foo.properties /test/myfolder/foo.pdf" +
	  	"\n\tcreates foo.pdf foo in folder /test/myfolder with title foo.txt and applies the properties in e:/temp/foo.properties";

	
	private static final String
		
		PROPS_OPT = "propsfile",
		SRC_FILE_OPT = "srcfiles",
		CHECKIN_ON_UPDATE = "checkin",
		URI_ARG = "URI";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		FileParam srcFilesOpt = (FileParam) cl.getOption(SRC_FILE_OPT);
		FileParam propsFileOpt = (FileParam) cl.getOption(PROPS_OPT);
		BooleanParam checkinOnUpdateOpt = (BooleanParam) cl.getOption(CHECKIN_ON_UPDATE);
		StringParam targetFNDocUriArg = (StringParam) cl.getArg(URI_ARG);
		
		List<File> srcFiles = null;
		File propsFile = null;
		Boolean checkinOnUpdate = true;
		String targetFNDocUri = null;
		
		if (srcFilesOpt.isSet()) {
			srcFiles = srcFilesOpt.getValues();
		}
		
		if (propsFileOpt.isSet()) {
			propsFile = propsFileOpt.getValue();
		}
		if (checkinOnUpdateOpt.isSet()) {
			checkinOnUpdate = checkinOnUpdateOpt.getValue();
		}
		
		targetFNDocUri = targetFNDocUriArg.getValue();
		return docVersion(targetFNDocUri, srcFiles, propsFile, checkinOnUpdate);
	}
	
	public boolean docVersion(
			String docUri, 
			List<File> srcFiles, 
			File propsFile,
			Boolean checkinOnUpdate)  throws Exception{
		
		DocUpdateDTO updateInfo = new DocUpdateDTO(docUri, srcFiles, propsFile);
		updateInfo.setCheckInOnUpdate(checkinOnUpdate);
		DocUtil util = new DocUtil(this.getShell());
		
		util.updateReservation(updateInfo);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		
		FileParam srcFilesOpt = null;
		FileParam propsFileOpt = null;
		BooleanParam checkinOnUpdateOpt = null;
		StringParam pathURIArg = null;


		// options
		{
			srcFilesOpt = new FileParam(SRC_FILE_OPT,
			"Src file for import",
			FileParam.IS_FILE & FileParam.IS_READABLE,
			FileParam.OPTIONAL,
			FileParam.MULTI_VALUED);
			srcFilesOpt.setOptionLabel("<srcFile>");
		}
		
		{
			checkinOnUpdateOpt = new BooleanParam(CHECKIN_ON_UPDATE, "Checkin doc on update (default to true");
			checkinOnUpdateOpt.setOptional(true);
		}
		
		{
			propsFileOpt = new FileParam(PROPS_OPT, 
					"properties file",
					FileParam.IS_FILE & FileParam.IS_READABLE,
					FileParam.OPTIONAL);
			
			propsFileOpt.setOptionLabel("<propsFile>");
		}
		
		// cmd args
		{
			pathURIArg = new StringParam(URI_ARG, "URI indicating a document",
					StringParam.REQUIRED);
			pathURIArg.setMultiValued(false);
		}
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {checkinOnUpdateOpt, srcFilesOpt, propsFileOpt }, 
					new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
