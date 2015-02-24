/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;

import com.filenet.api.core.Domain;
import com.filenet.api.property.Property;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  DomainProps
 *
 * @author GaryRegier
 * @date   May 21, 2011
 */
public class DomainPropsCmd extends BaseCommand {
	
	private static final String 
		CMD = "domainprops",
		CMD_DESC = "Display domain properties",
		HELP_TEXT = "Usage:  domainprops";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return domainProps();
	}

	/**
	 * 
	 */
	public boolean domainProps() throws Exception {
		StringBuffer buf = new StringBuffer();
		
		Domain dom = null;
		Iterator<?> iter = null;
		
		dom = getShell().getDomain(getShell().getCEConnection());
		iter = dom.getProperties().iterator();
		while (iter.hasNext()) {
			Property prop = (Property) iter.next();
			String name = prop.getPropertyName();
			appendValue(buf, prop, name);
		}
		getResponse().printOut(buf.toString());
		return true;
	}

	private void appendValue(StringBuffer buf, Property prop, String name) {
		Object value;
		try {
			value= prop.getObjectValue();
			buf.append(StringUtil.padLeft(name, ".", 40));
			if (value == null) {
				buf.append("null");
			} else {
				buf.append(value.toString());
			}
			buf.append("\n");
		} catch (Exception e) {
			buf.append(StringUtil.padLeft(name, ".", 40));
			buf.append("Problem");
			buf.append("\n");
		}
	}


	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
			
		// options
		
		// cmd args
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { });
		cl.setDieOnParseError(false);

		return cl;
	}

}
