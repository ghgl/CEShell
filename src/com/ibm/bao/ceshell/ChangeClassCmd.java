/**
 * 
 */
package com.ibm.bao.ceshell;

import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  ChangeClassCmd
 *
 * @author regier
 * @date   Jul 28, 2011
 */
public class ChangeClassCmd extends BaseCommand {
	
	private static final String 
	CMD = "chgclass",
	CMD_DESC = "Change the class of a document",
	HELP_TEXT = CMD_DESC + "@TODO:";
	//TODO Write helptext 

// param names
private static final String 
		CLASS_NAME = "classname",
		URI = "URI";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam newClassOpt = (StringParam) cl.getOption(CLASS_NAME);
		StringParam docUriParam = (StringParam)cl.getArg(URI);
		String newClassName = newClassOpt.getValue();;
		String docUri = docUriParam.getValue();
		
		return changeClass(newClassName, docUri);
	}

	/**
	 * @param string
	 * @param string2
	 */
	public boolean changeClass(String newClassName, String objUri) 
			throws Exception{
		Document doc = null;
		String oldClass = null;
		
		doc = this.fetchDoc(objUri);
		if (doc == null) {
			throw new IllegalArgumentException("Document with URI " + objUri + 
					" was not found");
		}
		oldClass = doc.getClassName();
		doc.changeClass(newClassName);
		doc.save(RefreshMode.REFRESH);
		getResponse().printOut("changed doc " + objUri + " from " + oldClass + " " + newClassName);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam classNameOpt = null;
		StringParam pathURIArg = null;
		
		
		// options
		classNameOpt = new StringParam(CLASS_NAME,
				"new class name to change to",
				StringParam.REQUIRED);
		classNameOpt.setOptionLabel("<classname>");

		// cmd args
		pathURIArg = new StringParam(URI, "URI indicating a document",
				StringParam.REQUIRED);
		pathURIArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { classNameOpt }, 
					new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
