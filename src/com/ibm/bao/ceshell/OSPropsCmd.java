/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Set;
import java.util.TreeSet;

import com.ibm.bao.ceshell.impl.NameValueInfo;
import com.ibm.bao.ceshell.util.StringUtil;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.Properties;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  OSPropsCmd
 *
 * @author regier
 * @date   Oct 13, 2011
 */
public class OSPropsCmd extends BaseCommand {
	
	private static final String 
			CMD = "osprops", 
			CMD_DESC = "List object store properties\n",
			HELP_TEXT = CMD_DESC;

	// param names


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return osProps();

	}

	/**
	 * 
	 */
	private boolean osProps() {
		ObjectStore os = getShell().getObjectStore();
		Set<NameValueInfo> propSet = new TreeSet<NameValueInfo>();
		Properties fnProps = os.getProperties();
		String reservationType = os.get_DefaultReservationType().toString();
		
		propSet.add(new NameValueInfo(fnProps.get(PropertyNames.SYMBOLIC_NAME)));
		propSet.add(new NameValueInfo(fnProps.get(PropertyNames.OBJECT_STORE_ID)));
		propSet.add(new NameValueInfo(fnProps.get(PropertyNames.DESCRIPTIVE_TEXT)));
		propSet.add(new NameValueInfo(fnProps.get(PropertyNames.ID)));
		propSet.add(new NameValueInfo(PropertyNames.CONTENT_ACCESS_RECORDING_LEVEL, 
					os.get_ContentAccessRecordingLevel().toString()));
		propSet.add(new NameValueInfo(PropertyNames.DEFAULT_RESERVATION_TYPE, reservationType));
		propSet.add(new NameValueInfo(PropertyNames.AUDIT_LEVEL, os.get_AuditLevel().toString()));
		
		displayProps(propSet);
		return true;
	}

	/**
	 * @param propSet
	 */
	private void displayProps(Set<NameValueInfo> propSet) {
		for (NameValueInfo nameValueInfo : propSet) {
			String name = nameValueInfo.getName();
			String value = nameValueInfo.getValue();
			getResponse().printOut(StringUtil.formatTwoCols(name, value, 45, " "));
		}
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		
		// options
		
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] {  });
		cl.setDieOnParseError(false);

		return cl;
	}

}
