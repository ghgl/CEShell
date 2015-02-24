/**
 * 
 */
package com.ibm.bao.ceshell;

import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.constants.DP;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 * TouchCmd
 * 
 * @author regier
 * @date Oct 21, 2011
 */
public class TouchCmd extends BaseCommand {

	private static final String CMD = "touch",
			CMD_DESC = "touch",
			HELP_TEXT = CMD + "\n" +
					"Usage:  \n" +
					"\touch <doc-uri>";

	// param names
	private static final String DOC_ARG = "document";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam docArg = null;
		String docUri;
		
		docArg = (StringParam) cl.getArg(DOC_ARG);
		docUri = docArg.getValue();

		return touch(docUri);
	}

	/**
	 * @param docUri
	 * @param title
	 */
	public boolean touch(String docUri) throws Exception {
		Document doc = null;
		PropertyFilter propFilter = createPropertyFilter();
		String currentTitle = null;
		Properties props = null;

		if (getShell().isId(docUri)) {
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(),
					new Id(docUri), propFilter);
		} else {
			String fullPath = this.pathUriToFullPath(docUri);
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(),
					fullPath, propFilter);
		}
		props = doc.getProperties();
		currentTitle = props.getStringValue(DP.DocumentTitle);
		props.putValue(DP.DocumentTitle, currentTitle);
		doc.save(RefreshMode.REFRESH);
		getResponse().printOut("touch " + docUri);
		return true;
	}

	private PropertyFilter createPropertyFilter() {
		PropertyFilter propFilter = new PropertyFilter();

		FilterElement fe = new FilterElement(null, null, null,
				DP.DocumentTitle, null);
		propFilter.addIncludeProperty(fe);

		return propFilter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {

		// create command line handler
		CmdLineHandler cl = null;
		StringParam docArg = null;
		
		// options

		// cmd args
		docArg = new StringParam(DOC_ARG, "File to touch",
				StringParam.REQUIRED);
		docArg.setMultiValued(false);

		
		// create command line handler
		cl = new HelpCmdLineHandler(HELP_TEXT, CMD, CMD_DESC,
				new Parameter[] {}, new Parameter[] { docArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
