package com.ibm.bao.ceshell;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.Factory;
import com.filenet.api.property.PropertyFilter;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

public class DescribeCmd extends BaseCommand {
	
	private static final String 
			CMD = "describe", 
			CMD_DESC = "Show properties associated with the type",
			HELP_TEXT = "Describe an object types in the repository.\n" +
			            "Use listtypes command to get a list of types that can be described\n" +
			            "The flags column shows an \"s\"=system and \"h\"=hidden\n" +
			            "\nUsage:\n" +
			            "XX $: describe Dcoument\n-- List standard, non-system, non-hidden properties on the Document class\n" +
			            "\nXX$: describe -all StorageArea\n--List all properties associated wtih a storage Area\n";
	
	// param names
	private static final String 
			LONG = "long",
			TYPE = "TYPE",
			HIDDEN = "hidden",
			SYSTEM = "system",
			ALL = "all";

	
	@Override
	protected boolean doRun(CmdLineHandler cmdLine) 
	throws Exception {
		BooleanParam longOpt = (BooleanParam) cmdLine.getOption(LONG);
		BooleanParam hiddenOpt = (BooleanParam) cmdLine.getOption(HIDDEN);
		BooleanParam systemOpt = (BooleanParam) cmdLine.getOption(SYSTEM);
		BooleanParam allOpt = (BooleanParam) cmdLine.getOption(ALL);
		StringParam typePatternArg = (StringParam) cmdLine.getArg(TYPE);
		String typeToDesc = typePatternArg.getValue().toString();
		boolean showSystemProps = systemOpt.getValue();
		boolean showHiddenProps = hiddenOpt.getValue();
		boolean showAllProps = allOpt.getValue();
		boolean listLong = false;
		listLong = longOpt.getValue();
		
		return describe(typeToDesc, listLong, showSystemProps, showHiddenProps, showAllProps);	
	}

	public boolean describe(
			String typeToDescribe,
			boolean listLong, 
			boolean showSystemProps,
			boolean showHiddenProps, 
			boolean showAllProps)
			throws Exception {
		ClassDefinition cd = Factory.ClassDefinition.fetchInstance(
				this.getShell().getObjectStore(), typeToDescribe, null);
		if (showAllProps) {
			showSystemProps = true;
			showHiddenProps = true;
		}
		if (listLong) {
			describeLong(cd, showSystemProps, showHiddenProps,showAllProps);
		} else {
			describeShort(cd, showSystemProps, showHiddenProps, showAllProps);
		}
		return true;
	}
	
	private void describeShort(
			ClassDefinition cd, 
			boolean showSystemProps,
			boolean showHiddenProps,
			boolean showAllProps
			) throws Exception {
		ColDef[] cols = createColDefs();
		PropertyFilter pf = new PropertyFilter();
		SortedSet<PropertyDefinition> sortedSet = 
				new TreeSet<PropertyDefinition>();
		pf.setLevelDependents(true);
		
		sortedSet = getOrderedPropertyDefinitions(cd);
		getResponse().printOut(StringUtil.formatHeader(cols, " "));
		
		for (Iterator<PropertyDefinition> iter = sortedSet.iterator(); 
				iter.hasNext();) {
			PropertyDefinition pd = iter.next();
			boolean sysOwned = pd.get_IsSystemOwned();
			boolean hidden = pd.get_IsHidden();
			String formattedRow;
			
			if (sysOwned && hidden) {
				if (! (showSystemProps || showHiddenProps)) { 
					continue;
				}
			}
			if (sysOwned && ! hidden) {
				if (!showSystemProps) {
					continue;
				}
			}
			if (hidden && ! sysOwned) {
				if (! showHiddenProps) {
					continue;
				}
			}
			formattedRow = formatRow(cols, pd);
			
			getResponse().printOut(formattedRow);
		}
	}

	private String formatRow(ColDef[] cols, PropertyDefinition pd) {
		String sysFlag = (pd.get_IsSystemOwned() ? "s" : " ");
		String hiddenFlag = (pd.get_IsHidden() ? "h" : " ");
		String name = pd.get_SymbolicName();
		TypeID typeID = pd.get_DataType();
		Cardinality cd = pd.get_Cardinality();
		String cdStr = cd.toString();
		String displayName = pd.get_DisplayName();
		String[] rowData = new String[] {
				sysFlag + hiddenFlag, name, typeID.toString(), cdStr, displayName
		};
		String row = StringUtil.formatRow(cols, rowData, " ");
		return row;
	}

	/**
	 * @param cd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SortedSet<PropertyDefinition> getOrderedPropertyDefinitions(
			ClassDefinition cd) {
		SortedSet<PropertyDefinition> sortedSet = 
				new TreeSet<PropertyDefinition>(new Comparator<PropertyDefinition>(){
			public int compare(PropertyDefinition lhs, PropertyDefinition rhs) {
				return (lhs.get_SymbolicName().compareTo(rhs.get_SymbolicName()));
			}
		});
		sortedSet.addAll(cd.get_PropertyDefinitions());		
		return sortedSet;
	}

	/**
	 * @return
	 */
	private ColDef[] createColDefs() {
		ColDef[] cols = {
				new ColDef("Flags", 6, StringUtil.ALIGN_LEFT),
				new ColDef("Name", 35, StringUtil.ALIGN_LEFT),
				new ColDef("Type", 20, StringUtil.ALIGN_LEFT),
				new ColDef("Card", 15, StringUtil.ALIGN_LEFT),
				new ColDef("Display Name", 20, StringUtil.ALIGN_LEFT)
		};
		
		return cols;
	}

	private void describeLong(ClassDefinition cd, 
			boolean showSystemProps,
			boolean showHiddenProps,
			boolean showAllProps
			) throws Exception {
		// implement describeLong method stub
		
	}
	
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam longOpt = null;
		BooleanParam hiddenOpt = null;
		BooleanParam systemOpt = null;
		BooleanParam allOpt = null;
		StringParam typePatternArg = null;
		
		// options
		longOpt = new BooleanParam(LONG,
				"use a long listing format");
		hiddenOpt = new BooleanParam(HIDDEN,
				"include hidden properties");
		systemOpt = new BooleanParam(SYSTEM,
				"include system properties");
		allOpt = new BooleanParam(ALL,
				"include all properties -- hidden and system");
		// cmd args
		typePatternArg = new StringParam(TYPE, "TYPE to describe",
				StringParam.REQUIRED);
		typePatternArg.setMultiValued(false);
		
		cl = new HelpCmdLineHandler(
				HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { longOpt, hiddenOpt, systemOpt,allOpt }, 
					new Parameter[] { typePatternArg });
		
		cl.setDieOnParseError(false);

		return cl;
	}
}
