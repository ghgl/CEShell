/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.filenet.api.admin.ChoiceList;
import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.collection.ClassDefinitionSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.SecurityProxyType;
import com.filenet.api.core.Factory;
import com.filenet.api.property.Property;
import com.filenet.api.replication.ReplicationGroup;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.QueryHelper;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  PropertyTemplatePropsCmd
 *
 * @author regier
 * @date   Sep 21, 2011
 */
public class PropertyTemplatePropsCmd extends BaseCommand {
	
	public static final String 
		CMD = "ptprops", 
		CMD_DESC = "Property template Properties",
		HELP_TEXT = CMD_DESC + "\n" +
				"Usage:\n" +
				"ptprops <SymbolicName>\n" +
				"Display general properties of a property template.\n" +
				"\nptprops -all <SymbolicName>\n" +
				"Display all properties of a property template";
	
	// param names
	private static final String
		ALL_PROPS_OPT = "all",
		PROPTERTY_TEMPLATE_ARG = "property";
	

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String propName = cl.getArg(PROPTERTY_TEMPLATE_ARG).getValue().toString();
		BooleanParam allPropsOpt = (BooleanParam) cl.getOption(ALL_PROPS_OPT);
		Boolean allProps = Boolean.FALSE;
		
		if (allPropsOpt.isSet()) {
			allProps = allPropsOpt.getValue();
		}
		return propertyTemplateProps(propName, allProps);

	}

	/**
	 * @param propName
	 */
	public boolean propertyTemplateProps(String propName, Boolean allProps) throws Exception {
		PropertyTemplate pt = null;
		propName = this.decodePath(propName);
		pt = fetchPropertyTemplate(propName);
		if (allProps) {
			displayAllProps(pt);
		} else {
			displayGeneralProps(pt);
		}
		return true;
		
	}

	/**
	 * @param pt
	 */
	private void displayAllProps(PropertyTemplate pt) throws Exception {
		getResponse().printOut("All Properties for " +pt.get_SymbolicName());
		String[][] systemProps = getSystemProperties(pt);
		writeResults(systemProps);
	}

	/**
	 * @param pt
	 */
	private void displayGeneralProps(PropertyTemplate pt) {
		getResponse().printOut("General Properties for " +pt.get_SymbolicName());

		String[][] rows = getGeneralProperties(pt);
		
		writeResults(rows);
		
	}

	public void writeResults(String[][] rows) {
		ColDef[] defs = new ColDef[] {
				new ColDef("Name", 30, StringUtil.ALIGN_LEFT),
				new ColDef("Value", 30, StringUtil.ALIGN_LEFT),
			};
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		
		for (String[] row : rows) {
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
		}
	}
	
	/**
	 * @param pt
	 * @return
	 */
	private String[][] getSystemProperties(PropertyTemplate pt) throws Exception {
		String securityProxyType = readSecurityProxyType(pt);
		String allowsForeignObjects = readAllowsForeignObject(pt);
		String replicationGroup = readReplicationGroup(pt);
		String[][] rows = new String[][] {
				{PropertyNames.CLASS_DESCRIPTION, pt.get_ClassDescription().get_SymbolicName()},
				{PropertyNames.DISPLAY_NAME, pt.get_DisplayName()},
				{PropertyNames.DATA_TYPE, pt.get_DataType().toString()},
				{PropertyNames.CARDINALITY, pt.get_Cardinality().toString()},
				{PropertyNames.SETTABILITY, pt.get_Settability().toString()},
				{PropertyNames.PERMISSION_TYPE, pt.get_PersistenceType().toString()},
				{PropertyNames.SYMBOLIC_NAME, pt.get_SymbolicName()},
				{PropertyNames.REPLICATION_GROUP, replicationGroup},
				{PropertyNames.CREATOR, pt.get_Creator()},
				{PropertyNames.DATE_CREATED, StringUtil.fmtDate(pt.get_DateCreated())},
				{PropertyNames.LAST_MODIFIER, StringUtil.fmtDate(pt.get_DateLastModified())},
				{PropertyNames.LAST_MODIFIER, pt.get_LastModifier()},
				{PropertyNames.ID, pt.get_Id().toString()},
				{PropertyNames.NAME, pt.get_Name()},
				{PropertyNames.OWNER, pt.get_Owner()},
				{PropertyNames.DISPLAY_NAME, pt.get_DisplayName()},
				{PropertyNames.DESCRIPTIVE_TEXT, valueCheck(pt.get_DescriptiveText())},
				{PropertyNames.IS_VALUE_REQUIRED, valueCheck(pt.get_IsValueRequired())},
				{PropertyNames.IS_NAME_PROPERTY, pt.get_IsNameProperty().toString()},
				{PropertyNames.REQUIRES_UNIQUE_ELEMENTS, valueCheck(pt.get_RequiresUniqueElements())},
				{PropertyNames.CHOICE_LIST, readChoiceList(pt)},
				{PropertyNames.USED_IN_CLASSES, valueCheck(readUsedInClasses(pt))},
				{PropertyNames.PROPERTY_DISPLAY_CATEGORY, valueCheck(pt.get_PropertyDisplayCategory())},
				{PropertyNames.MODIFICATION_ACCESS_REQUIRED, valueCheck(pt.get_ModificationAccessRequired())},
//				{PropertyNames.ALLOWS_FOREIGN_OBJECT, "@todo: " + PropertyNames.ALLOWS_FOREIGN_OBJECT},
				{PropertyNames.ALLOWS_FOREIGN_OBJECT, allowsForeignObjects},
				{PropertyNames.SECURITY_PROXY_TYPE, securityProxyType}
				
		};
		return rows;
	}

	/**
	 * @param pt
	 * @return
	 */
	private String readReplicationGroup(PropertyTemplate pt) {
		String value = null;
		try {
			ReplicationGroup rg = pt.get_ReplicationGroup();
			if (rg != null) {
				value = rg.get_DisplayName();
			} else {
				value = "<Not in properties collection>";
			}
					
		} catch (Exception e) {
			value =  "<Not in properties collection>";
		}
		return value;
	}

	/**
	 * @param pt
	 * @return
	 */
	private String readAllowsForeignObject(PropertyTemplate pt) {
		try {
			Property prop = pt.getProperties().get(PropertyNames.ALLOWS_FOREIGN_OBJECT);
			Object value = null;
			if (prop == null) {
				return null;
			}
			value = prop.getBooleanValue();
			if (value != null) {
				return value.toString();
			}
		} catch (Exception e) {
			return "<Not in properties collection>";
		}
		return "null";
	}
	

	/**
	 * @param pt
	 * @return
	 */
	private String readUsedInClasses(PropertyTemplate pt) {
		List<String> usedInNames = new ArrayList<String>();
		String result = null;
		ClassDefinitionSet cds = pt.get_UsedInClasses();
		
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = cds.iterator(); iterator.hasNext();) {
			ClassDefinition cd = (ClassDefinition) iterator.next();
			String symbolicName = cd.get_SymbolicName();
			usedInNames.add(symbolicName);
//			if (result == null) {
//				result = symbolicName + " ";
//			} else {
//				result = result + symbolicName + " ";
//			}
		}
		result = StringUtil.listToString(usedInNames, " ");
		if (result != null) {
			result = result.trim();
		}
		return result;
	}

	/**
	 * @param pt
	 * @return
	 */
	private String readSecurityProxyType(PropertyTemplate pt) {
		try {
			Property secProxy = pt.getProperties().get(PropertyNames.SECURITY_PROXY_TYPE);
			Integer value = secProxy.getInteger32Value();
			String label = SecurityProxyType.getInstanceFromInt(value.intValue()).toString();
			
			String result = value.toString() + " (" + label + ")";
			return result;
		} catch (Exception e) {
			return "(n/a)";
		}
	}

	/**
	 * @param pt
	 * @return
	 */
	private String readChoiceList(PropertyTemplate pt) {
		ChoiceList cl = pt.get_ChoiceList();
		if (cl == null) {
			return "<Value not set>";
		} else {
			return cl.get_DisplayName();
		}
	}

	/**
	 * @param get_RequiresUniqueElements
	 * @return
	 */
	private String valueCheck(Object value) {
		if (value == null) {
			return "<Value Not Set>";
		} else {
			return value.toString();
		}
	}

	public String[][] getGeneralProperties(PropertyTemplate pt) {
		String[][] rows = new String[][] {
				{PropertyNames.DISPLAY_NAME, pt.get_DisplayName()},
				{PropertyNames.DESCRIPTIVE_TEXT, pt.get_DescriptiveText()},
				{PropertyNames.DATA_TYPE, pt.get_DataType().toString()},
				{ PropertyNames.CARDINALITY, pt.get_Cardinality().toString()},
				{PropertyNames.ID, pt.get_Id().toString()}
		};
		return rows;
	}

	private PropertyTemplate fetchPropertyTemplate(String propName) throws Exception {
		Id id = null;
		PropertyTemplate pt = null;
		String query = null;
		
		query = String.format("SymbolicName = '%s'", propName);
		id = new QueryHelper(getShell()).fetchId("PropertyTemplate", query);
		if (id == null) {
			throw new IllegalArgumentException("No property template found with symbolic name " + propName);
		}
		pt = Factory.PropertyTemplate.fetchInstance(getShell().getObjectStore(), id, null);
		
		return pt;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		BooleanParam allPropsOpt = null;
		StringParam propNameArg = null;
		
		// options
		allPropsOpt = new BooleanParam(ALL_PROPS_OPT, "display all properties");
		allPropsOpt.setOptional(BooleanParam.OPTIONAL);
		// cmd args
		propNameArg = new StringParam(PROPTERTY_TEMPLATE_ARG, 
				"user properties",
				StringParam.REQUIRED);
		propNameArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {allPropsOpt }, 
					new Parameter[] { propNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
