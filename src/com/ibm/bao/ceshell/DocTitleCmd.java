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
 *  DocTitleCmd
 *
 * @author GaryRegier
 * @date   May 3, 2011
 */
public class DocTitleCmd extends BaseCommand {
	
	private static final String 
		CMD = "doctitle", 
		CMD_DESC = "Get/set the DocumentTitle property on a document",
		HELP_TEXT = "Get/Set the DocumentTitle property on a document\n" +
		 "A docuri or a document title must be urlencoded.\n" +
		 "Usage:  \n"  +
		 "\tdoctitle /TestFolder/doc1.doc : gets the current document title\n" +
		 "\tdoctitle /TestFolder/doc1.doc Howdy  : sets the title to Howdy\n" +
		 "\tdoctitle /TestFolder/doc1.doc Howdy+World : sets the title to \'Howdy World\'\n" +
		 "\t\tSet doc1.doc\'s DocumentTitle to \'Howdy World\'";

	// param names
	private static final String 
		DOC_ARG = "document",
		TITLE_ARG = "title";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam docArg = null;
		StringParam titleArg = null;
		String docUri;
		String title = null;
		boolean updateTitle = false;
		
		docArg = (StringParam) cl.getArg(DOC_ARG);
		docUri = docArg.getValue();
		titleArg = (StringParam) cl.getArg(TITLE_ARG);
		
		if (titleArg.isSet()) {
			updateTitle = true;
			title = titleArg.getValue();
			if (  (title == null) ||
					(title.length() == 0) )  {
				getResponse().printErr("Document title must be be provided");
				return false;
			}
		}
		
		return docTitle(docUri, title, updateTitle);
		
	}

	/**
	 * @param docUri
	 * @param title
	 */
	public boolean docTitle(String docUri, String title, boolean updateTitle) throws Exception {
		Document doc = null;
		PropertyFilter propFilter = createPropertyFilter();
		String currentTitle = null;
		Properties props = null;
		
		
		if (getShell().isId(docUri)) {
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					new Id(docUri), propFilter);
		} else {
			String decodedUri = getShell().urlDecode(docUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					fullPath, propFilter);
		}
		props = doc.getProperties();
		currentTitle = props.getStringValue(DP.DocumentTitle);
		if (updateTitle) {
			String decodedTitle = getShell().urlDecode(title);	
			props.putValue(DP.DocumentTitle, decodedTitle);
			doc.save(RefreshMode.REFRESH);
			getResponse().printOut("Updated DocumentTile on " + docUri + 
					" from " + currentTitle + " \'" + decodedTitle + "\'");
		} else {
			getResponse().printOut("Document title on " + docUri + " is \'" + currentTitle + "\'");
		}
		return true;
	}

	private PropertyFilter createPropertyFilter() {
		PropertyFilter propFilter = new PropertyFilter();
		
		FilterElement fe = 
			new FilterElement(null, null, null, DP.DocumentTitle, null);
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
		StringParam docArg = null;
		StringParam titleArg = null;

		// options
		
		// cmd args
		docArg = new StringParam(DOC_ARG, "File to set the title on",
				StringParam.REQUIRED);
		docArg.setMultiValued(false);
		
		titleArg = new StringParam(TITLE_ARG, "value of document title property",
				StringParam.OPTIONAL);
		titleArg.setMultiValued(false);
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { docArg, titleArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
