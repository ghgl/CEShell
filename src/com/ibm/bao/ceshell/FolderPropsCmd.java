package com.ibm.bao.ceshell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.constants.DP;
import com.ibm.bao.ceshell.constants.FP;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

public class FolderPropsCmd extends BaseCommand {

	private static final String CMD = "folderprops", CMD_DESC = "Display properties from a folder",
			HELP_TEXT = "Display properties from a folder\n"
					+ "The properties are placed into categories that match the FileNet "
					+ "Enterprise Manager document properties page filters -- general, "
					+ " custom, custom and system, all, and diagnostic.\n"
					+ "\nThe default if no options is specified is general." + "\n\nUsage: "
					+ "\n\nfolderprops  /some/path.txt               display general properties for folder /some/path (default)"
					+ "\n\nfolderprops -p custom /some/path.txt      display general and custom properties for folder /some/path"
					+ "\n\nfolderprops -p all foo                    display all properties for folder foo";
	// param names
	private static final String PROPS = "props", URI = "URI";

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam propsOpt = (StringParam) cl.getOption(PROPS);
		StringParam docUriParam = (StringParam) cl.getArg(URI);
		String folderUri = null;
		String category = null;

		folderUri = docUriParam.getValue();
		if (propsOpt.isSet()) {
			category = propsOpt.getValue();
		}

		return folderProps(folderUri, category);
	}

	public boolean folderProps(String folderUri, String category) {
		StringBuffer buf = new StringBuffer();
		PropertyFilter propFilter = createPropCategoryFilter(category);
		Folder folder = null;
		SortedSet<Property> propsSet = new TreeSet<Property>(new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return (o1.getPropertyName().compareTo(o2.getPropertyName()));
			}
		});

		if (getShell().isId(folderUri)) {
			folder = Factory.Folder.fetchInstance(getShell().getObjectStore(), new Id(folderUri), propFilter);
		} else {
			String decodedUri = getShell().urlDecode(folderUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			folder = Factory.Folder.fetchInstance(getShell().getObjectStore(), fullPath, propFilter);
		}
		for (Iterator<?> iterator = folder.getProperties().iterator(); iterator.hasNext();) {
			Property property = (Property) iterator.next();
			propsSet.add(property);

		}

		// reaFProps(buf, doc, propsSet);
		readPropsV2(buf, folder, propsSet);
		getResponse().printOut(buf.toString());
		return true;
	}
	
	protected void readPropsV2(StringBuffer buf, Folder folder,
			SortedSet<Property> propsSet) {
		String folderClassSymbolicName = folder.get_ClassDescription().get_SymbolicName();
		
		Map<String, PropertyDefinition> propDefinitions = fetchPropertyDescriptionMap(folderClassSymbolicName);
		
		for (Iterator<Property> iterator = propsSet.iterator(); iterator.hasNext();) {
			Property prop = iterator.next();
			
			Object value = null;
			String name = prop.getPropertyName();
			Class<?> c = prop.getClass();
			
			String className = c.getCanonicalName();
			//ClassDefinition czd = this.getClassDefinition(className);
			
			String cShortName = className.substring(className.lastIndexOf(".") + 1);
			Boolean inout = (Boolean) DP.PropTypes.get(cShortName);
			if (name.equals(DP.ClassDescription)) {
				ClassDescription cd = folder.get_ClassDescription();
				String description = cd.get_DisplayName();
				cd.get_DisplayName();
				appendValue(buf, description, DP.ClassDescription);
				
			} else 	if (Boolean.TRUE.equals(inout)) {
				PropertyDefinition pd = propDefinitions.get(name);
				if (pd != null) {
					Cardinality cardinality = pd.get_Cardinality();
					TypeID typeId = pd.get_DataType();
					if (Cardinality.LIST.equals(cardinality)) {
						appendListValues(buf, name, prop, typeId);
					} else {
						value = prop.getObjectValue();
						appendValue(buf, value, name);
					}
				}
				
			} else {
				buf.append(StringUtil.padLeft(name, ".", 40)) 
						.append("<")
						.append(cShortName)
						.append(">\n");
			}
			
		}
	}

	private void appendValue(StringBuffer buf, Object value, String name) {
		buf.append(StringUtil.padLeft(name, ".", 40));
		if (value == null) {
			buf.append("null");
		} else {
			buf.append(value.toString());
		}
		buf.append("\n");
	}
	
	/**
	 * @param buf
	 * @param name
	 * @param prop
	 */
	private void appendListValues(StringBuffer buf, String name, Property prop, TypeID typeId) {
		List<?> values = new ArrayList<String>();
		if (typeId.equals(TypeID.STRING)) {
			values = prop.getStringListValue();
		} else if (typeId.equals(TypeID.BINARY)) {
			values = prop.getBinaryListValue();
		} else if (typeId.equals(TypeID.DATE)) {
			values = prop.getDateTimeListValue();
		} else if (typeId.equals(TypeID.DOUBLE)) {
			values = prop.getFloat64ListValue();
		} else if (typeId.equals(TypeID.LONG)) {
			values = prop.getInteger32ListValue();
		} else if (typeId.equals(TypeID.GUID)) {
			values = prop.getIdListValue();
		} else if (typeId.equals(TypeID.OBJECT)) {
			// what to do?? 
		}
		
		for (int i = 0; i < values.size(); i++) {
			String nextValStr = values.get(i).toString();
			String propName = name + "[" + i + "]";
			appendValue(buf, nextValStr, propName);
		}		
	}


	private PropertyFilter createPropCategoryFilter(String category) {
		PropertyFilter filter = null;

		if (category == null) {
			filter = createGeneralPropertyFilter();
		} else {
			if (FP.CAT_CUSTOM_AND_GENERAL.equals(category)) {
				filter = createGeneralAndCustomAndSystemFilter();
			} else {
				filter = null;
			}
		}
		return filter;
	}
	
	private PropertyFilter createGeneralAndCustomAndSystemFilter() {
		PropertyFilter propFilter = new PropertyFilter();
		for (int i = 0; i < DP.CUSTOM_AND_SYSTEM_EXCLUDE_PROPS.length; i++) {
			propFilter.addExcludeProperty(DP.CUSTOM_AND_SYSTEM_EXCLUDE_PROPS[i]);
		}
		return propFilter;
	}

	private PropertyFilter createGeneralPropertyFilter() {
		PropertyFilter propFilter = new PropertyFilter();
		for (int i = 0; i < FP.GENERAL_PROPS.length; i++) {
			FilterElement fe = new FilterElement(null, null, null, FP.GENERAL_PROPS[i], null);
			propFilter.addIncludeProperty(fe);
		}
		return propFilter;
	}

	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam propsOpt = null;
		StringParam pathURIArg = null;
		StringBuffer propsOptDesc = new StringBuffer();

		{
			propsOptDesc.append("Properties category to list where <propList> is\n");
			for (int i = 0; i < FP.PropCategoriesList.length; i++) {
				propsOptDesc.append("\t").append(FP.PropCategoriesList[i]).append("\n");
			}
		}

		// options
		propsOpt = new StringParam(PROPS, propsOptDesc.toString(), StringParam.OPTIONAL);
		propsOpt.setOptionLabel("<propList>");

		// cmd args
		pathURIArg = new StringParam(URI, "URI indicating a document", StringParam.REQUIRED);
		pathURIArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(HELP_TEXT, CMD, CMD_DESC, new Parameter[] { propsOpt },
				new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
