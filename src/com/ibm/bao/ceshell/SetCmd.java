/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.List;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.impl.EditInfo;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  SetCmd
 *
 * @author regier
 * @date   Sep 8, 2011
 */
public class SetCmd extends BaseCommand {
	
	public static final String 
		CMD = "set", 
		CMD_DESC = "Set the property of an item that has been fetched for editing using the edit cmd",
		HELP_TEXT = CMD_DESC;
	
	// param names
	private static final String 
		PROP_ARG = "property",
		VALUE_ARG = "value";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String propName = cl.getArg(PROP_ARG).getValue().toString();
		StringParam propValuesArg = (StringParam) cl.getArg(VALUE_ARG);
		List<String> propValues = propValuesArg.getValues();
		
		return setProp(propName, propValues);
	}

	/**
	 * @param propName
	 * @param propValue
	 */
	public boolean setProp(String propName, List<String> propValues) throws Exception {
		EditInfo editInfo = null;
		String propValue = StringUtil.listToString(propValues, " ");
		String decodedValue = StringUtil.decode(propValue);
		String decodedName = StringUtil.decode(propName);
		
		editInfo = getShell().getCurrentEditInfo();
		if (editInfo == null) {
			throw new IllegalStateException("Nothing has been fetched for edit");
		}
		
		editInfo.setProp(decodedName, decodedValue);
		getResponse().printOut(String.format("set %s:%s", propName, propValue));
		return true;
	}


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam propArg = null;
		StringParam valueArg = null;

		// options
		
		// cmd args
		propArg = new StringParam(PROP_ARG, "name of the property to set",
				StringParam.REQUIRED);
		propArg.setMultiValued(false);
		propArg.setOptionLabel("<name>");
		
		valueArg = new StringParam(VALUE_ARG, "value of the property to set", 
				StringParam.REQUIRED);
		valueArg.setMultiValued(true);
		valueArg.setOptionLabel("<value>");
		

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] { propArg, valueArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
