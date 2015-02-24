/**
 * 
 */
package com.ibm.bao.ceshell;

import jcmdline.CmdLineHandler;
import jcmdline.IntParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.security.Level;
import com.ibm.bao.ceshell.security.ObjectLevels;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  AceCompCmd
 *
 * @author GaryRegier
 * @date   Oct 14, 2010
 */
public class AceCompCmd extends AbsAclCmd {
	public static final String 
		CMD = "aclcomp", 
		CMD_DESC = "Compare a acl mask to standard levels",
		HELP_TEXT = "in development";
	
	// Options:
	// NOTE: some options are inherited from parent class
	public static final String
		MASK_ARG = "mask";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam parentTypeParam = (StringParam) cl.getOption(ACL_PARENT_TYPE_OPT);
		IntParam maskArg = (IntParam) cl.getArg(MASK_ARG);
		String parentType = "d"; // default to doc
		Integer maskToCompare;
		
		if (parentTypeParam.isSet()) {
			parentType = parentTypeParam.getValue();
		}
		maskToCompare = maskArg.getValue();
		return aceComp(parentType, maskToCompare);
	}
	
	/**
	 * @param parentType: String
	 * @param maskToCompare: Integer
	 */
	public boolean aceComp(String parentType, Integer maskToCompare) {
		ColDef[] cols = {
				new ColDef("name", 25, StringUtil.ALIGN_LEFT),
				new ColDef("match?",5, StringUtil.ALIGN_LEFT)
			};
		Level[] levelsToCompare = 
			ObjectLevels.getObjectLevelsListByType(parentType);
		if (levelsToCompare == null) {
			throw new IllegalArgumentException("Type of " + parentType + 
					" is not a recognized type");
		}
		getResponse().printOut(StringUtil.formatHeader(cols, " "));
		for (int i = 0; i < levelsToCompare.length; i++) {
			Level nextLevel = levelsToCompare[i];
			String name = nextLevel.getDescription();
			int srcMask = nextLevel.getMask();
			boolean matches = ( (srcMask & maskToCompare.intValue()) == srcMask);
			String[] rowData = { name, (matches == true) ? "True" : "False" };
			getResponse().printOut(StringUtil.formatRow(cols, rowData, " "));
		}
		return true;
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
		StringParam aclParentTypeOpt = null;
		IntParam maskArg = null;
		
		// options
		aclParentTypeOpt = getAclParentTypeOpt();
		
		// cmd args
		maskArg = new IntParam(MASK_ARG,
				"Mask to compare",false);
		maskArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
					HELP_TEXT, CMD, CMD_DESC, 
				new Parameter[] {aclParentTypeOpt }, 
				new Parameter[] { maskArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
