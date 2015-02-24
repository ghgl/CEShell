/**
 * 
 */
package com.ibm.bao.ceshell;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  UseCmd
 *
 * @author GaryRegier
 * @date   Sep 22, 2010
 */
public class UseCmd extends BaseCommand {
	private static final String 
		CMD = "use", 
		CMD_DESC = "use <object-store>",
		HELP_TEXT = "Use a different object store.\n" +
		     "The object store needs to be in the same domain. A new \n" +
		     "working context is created when the object store changes\n" +
		     "\nIf parameter <object-store> is omitted, then the current connection" +
		     "information is printed\n\nEx:\n\tuse\n\tprints current connection";

	// param names
	private static final String 
		OS_ARG = "objectStore";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam osArg = (StringParam) cl.getArg(OS_ARG);
		String objectStoreName = null;
		
		if(osArg.isSet()) {
			objectStoreName = osArg.getValue();
		}
		
		return useObjectStore(objectStoreName);

	}

	public boolean  useObjectStore(String objectStoreName) throws Exception {
		if (objectStoreName == null) {
			getResponse().printOut(getShell().getConnectionDescription());
		} else {
			String osName =  getShell().urlDecode(objectStoreName);
			getShell().useOjectStore(osName);
			getResponse().printOut("connected with " + getShell().getConnectionDescription());
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
		StringParam objectStoreArg = null;

		// options
		
		// cmd args
		objectStoreArg = new StringParam(OS_ARG, "object store name",
				StringParam.OPTIONAL);
		objectStoreArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] { objectStoreArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
