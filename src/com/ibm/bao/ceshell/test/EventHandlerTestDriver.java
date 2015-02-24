/**
 * 
 */
package com.ibm.bao.ceshell.test;

import com.filenet.api.core.Document;
import com.filenet.api.engine.EventActionHandler;
import com.filenet.api.events.ObjectChangeEvent;
import com.ibm.bao.ceshell.BaseCommand;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  EventHandlerTestDriver
 *
 * @author regier
 * @date   Aug 23, 2011
 */
public class EventHandlerTestDriver extends BaseCommand {
	
	private static final String 
	CMD = "evt_driver",
	CMD_DESC = "Test an event handler",
	HELP_TEXT = "An event handlder for a document event. Used to test the event class." + 
		"\n\nUsage: \n" +
		"evt_driver -className com.ibm.bao.bh.BHDCUpdate_EventHandler /TestFolder/tst/mydoc";


// param names
private static final String 
		EVENT_HANDLER_CLASSNAME = "classname",
		DOC_URI = "URI";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String className = null;
		String docUri = cl.getArg(DOC_URI).getValue().toString();
		Parameter<?> classNameOpt = cl.getOption(EVENT_HANDLER_CLASSNAME);
		
		if (classNameOpt.isSet()) {
			className = classNameOpt.getValue().toString();
		}
		
		return eventHandler(className, docUri);
	}

	/**
	 * @param className
	 * @param docUri
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public boolean eventHandler(String className, String docUri)throws Exception {
		Document sourceDoc = this.fetchDoc(docUri);
		ObjectChangeEvent oce = new ObjectChangeEventAdapter(this.getShell().getObjectStore(), sourceDoc);
		EventActionHandler handler = (EventActionHandler) Class.forName(className).newInstance();
		handler.onEvent(oce, null);
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
		classNameOpt = new StringParam(EVENT_HANDLER_CLASSNAME,
				"className of event handler",
				StringParam.REQUIRED);

		// cmd args
		pathURIArg = new StringParam(DOC_URI, "URI indicating a document",
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
