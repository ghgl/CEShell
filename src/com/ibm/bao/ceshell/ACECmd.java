package com.ibm.bao.ceshell;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.IntParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.security.DocLevels;
import com.ibm.bao.ceshell.security.MaskToAR;

public class ACECmd extends BaseCommand {
	
	/**
	 *   ace -doclevels -long
	 *   ace -compare 324, full
	 *   
	 *
	 */
	
	private static final String 
		CMD = "ace", 
		CMD_DESC = "Show the access rights granted or " +
		            "denied by an access control entry.",
		HELP_TEXT = "List the access rights on an access control entry.\n"+
		            "If no arguments or options are given, then a description" +
		            " of all named acess levels is given";
	

	// param names
	private static final String 
		DOCLEVELS = "doclevels",
		LONG = "long",
		MASK = "mask";
	
	private static final Integer EMPTY_MASK = new Integer(-1);

	private DocLevels docLevels = new DocLevels();
	
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam longOpt = (BooleanParam) cl.getOption(LONG);
		IntParam maskArg = (IntParam) cl.getArg(MASK);
		Boolean longList = Boolean.FALSE;
		Integer maskValue = -1;
		
		if (longOpt.isSet()) {
			longList = longOpt.getValue();
		}
		
		if (maskArg.isSet()) {
			maskValue = maskArg.getValue();
		}
		return ace(maskValue, longList);
		
	}
	
	public boolean ace(Integer maskValue, Boolean longFmt)throws Exception {
		String result = null;
		
		if (longFmt) {
			result = docLevels.describeLevels(true);
			getRequest().getResponse().getOut().print(result);
			return true;
		}
		if (! EMPTY_MASK.equals(maskValue)) {
			result = showAces(maskValue.intValue());
			getResponse().printOut(result);
			return true;
		}
		
		// ok. default. show long description
		result = docLevels.describeLevels(true);
		getResponse().printOut(result);
		return true;
	}

	protected String showAces(int mask) {
		MaskToAR maskMapper = new MaskToAR();
		String result = maskMapper.maskToAces(mask);
		return result;
	}
	
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam docLevelsOpt;
		IntParam maskArg;

		// options
		docLevelsOpt = new BooleanParam(DOCLEVELS, 
				"List the named document level ACES");
		BooleanParam longOpt = new BooleanParam(LONG,
				"Do a long listing of the levels");
		
		// cmd args
		maskArg = new IntParam(MASK, "ACE to show permissions",
				StringParam.OPTIONAL);
		maskArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
					HELP_TEXT, CMD, CMD_DESC, 
				new Parameter[] {docLevelsOpt, longOpt}, 
				new Parameter[] { maskArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
