/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.ObjectStore;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  OSLSCmd
 *  <p>ObjectStore ls: list the object stores in the current domain
 *  <p>Long listing: 
 *  <ol>
 *  	<li>object store name/display name
 *  	<li>state
 *  	<li> JNDI Data Source Name
 *  	<li> JNDI XA Data Source Name
 *  	<li> Database type
 *  	<li> Site name
 * </ol>
 *  
 *
 * @author GaryRegier
 * @date   Sep 22, 2010
 */
public class OSLSCmd extends BaseCommand {
	
	private static final String 
		CMD = "osls", 
		CMD_DESC = "list object stores",
		HELP_TEXT = "List the object stores in the domain";
	// param names
	private static final String 
		LONG_OPT = "long"; 

	// param names 

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam longOpt = (BooleanParam) cl.getOption(LONG_OPT);
		boolean listLong = false;
		
		if (longOpt.isSet()) {
			listLong = longOpt.getValue();
		}
		
		return osls(listLong);
	}

	public boolean osls(boolean listLong) throws Exception {
		Connection con = getShell().getCEConnection();
		Domain domain = getShell().getDomain(con);
		
		if (listLong) {
			listLong(domain);
		} else {
			listShort(domain);
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void listShort(Domain domain) {
		Iterator<ObjectStore> iter = domain.get_ObjectStores().iterator();
		while (iter.hasNext()){
			ObjectStore os = iter.next();
			String name = os.get_Name();
			getResponse().printOut(name);
		}
	}

	@SuppressWarnings("unchecked")
	private void listLong(Domain domain) {
		Iterator<ObjectStore> iter = domain.get_ObjectStores().iterator();
		ColDef[] defs = new ColDef[] { 
				new ColDef("SymbolicName", 20, StringUtil.ALIGN_LEFT),
				new ColDef("Name", 30, StringUtil.ALIGN_LEFT), 
				new ColDef("Description", 40, StringUtil.ALIGN_LEFT)
		};
		
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		
		while (iter.hasNext()){
			StringBuffer buf = new StringBuffer();
			ObjectStore os = iter.next();
			String symbolicName = os.get_SymbolicName();
			String name = os.get_Name();
			String description = os.get_DescriptiveText();
			String[] data = new String[] {
				symbolicName, 
				name, 
				description
			};
			getResponse().printOut(StringUtil.formatRow(defs, data, "."));
		}
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam longOpt = null;

		// options
		longOpt = new BooleanParam(LONG_OPT, 
				"long listing of object stores");
		longOpt.setOptional(true);

		longOpt.setMultiValued(false);
		
		// cmd args
		

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { longOpt }, 
					new Parameter[] { });
		cl.setDieOnParseError(false);

		return cl;
	}

}
