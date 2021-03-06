/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.admin.Choice;
import com.filenet.api.admin.ChoiceList;
import com.filenet.api.constants.ChoiceType;
import com.filenet.api.core.Factory;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.QueryHelper;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  ChoiceListProps
 *
 * @author regier
 * @date   Feb 28, 2015
 */
public class ChoiceListPropsCmd extends BaseCommand {
	
	private static final String 
		CMD = "clprops", 
		CMD_DESC = "List Chice properties",
		HELP_TEXT = CMD_DESC + "\n" +
				"\nUsage:\n" +
				"clprops <choice-list>\n";
	
	private static final String
		CHOICE_LIST_URI_ARG = "choiceListUri";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam choiceListUriArg = (StringParam) cl.getArg(CHOICE_LIST_URI_ARG);
		String choiceListUri = choiceListUriArg.getValue();
		return choiceListProps(choiceListUri);
		
	}

	/**
	 * @param string choiceListUri This can be an id or a symbolic name
	 *      
	 */
	private boolean choiceListProps(String choiceListUri) throws Exception {
		ChoiceList cl = null;
		PropertyFilter propFilter = null;
		Id id = null;
		int depth = 0;
		
		if (getShell().isId(choiceListUri)) {
			id = new Id(choiceListUri);
		} else {
			
			id = fetchChoiceListIdByName(choiceListUri);
		}
		cl = Factory.ChoiceList.fetchInstance(
				getShell().getObjectStore(), 
				id, 
				propFilter);
		doShowProps(cl, depth);
		return true;
	}

	/**
	 * @param cl
	 */
	private void doShowProps(ChoiceList cl, int depth) {
		/** this is strange. 
		 * There is a ChoiceList class in com.filenet.api.collection
		 * There is another ChoicList class in com.filenet.api.admin  
		 */
		String idStr = cl.get_Id().toString();
		String name = cl.get_Name();
		String displayName = cl.get_DisplayName();
		String description = cl.get_DescriptiveText();
		//String creator = cl.get_Creator();
		String dataTypeStr = cl.get_DataType().toString();
		Boolean hasHierarchy = cl.get_HasHierarchy();
		
		StringBuffer buf = new StringBuffer();
		
		buf.append(String.format("Choice list properties for %s", name)).append("\n");
		appendValue(buf, idStr, "Id");
		appendValue(buf, name, "Name");
		appendValue(buf, displayName, "DislayName");
		appendValue(buf, description, "DescriptiveText");
		appendValue(buf, dataTypeStr, "DataType");
		appendValue(buf, hasHierarchy.toString(), "HasHierarchy");
		

		buf.append("Choice Values (Name, DisplayName):\n----------------------------------------------------------------------------------------------------\n");
		
		com.filenet.api.collection.ChoiceList clProp= cl.get_ChoiceValues();
		if (hasHierarchy) {
			doDisplayPropertyChoiceValuesHierchical(buf, depth+1, clProp);
		} else {
			doDisplayPropertyChoiceValuesFlat(buf, clProp);
		}
		getResponse().printOut(buf.toString());
	}

	/**
	 * @param clProp
	 */
	private void doDisplayPropertyChoiceValuesFlat(
			StringBuffer buf,
			com.filenet.api.collection.ChoiceList clProp) {
		// TODO Auto-generated method stub
		Iterator<?> iter = clProp.iterator();
		while (iter.hasNext()) {
			Choice nextChoice = (Choice) iter.next();
			ChoiceType ct = nextChoice.get_ChoiceType();
			String choiceValueStr = "??";
			String choiceDesc = nextChoice.get_DisplayName();
			
			/** choiceValueStr should be repalced here. If it's a ?? its a bug **/
			if (ChoiceType.INTEGER.equals(ct)) {
				choiceValueStr = nextChoice.get_ChoiceIntegerValue().toString();
			} else if (ChoiceType.STRING.equals(ct))  {
				choiceValueStr = nextChoice.get_ChoiceStringValue();
			} 
			appendValue(buf, choiceValueStr, "\t" + choiceDesc);
		}
	}

	public void doDisplayPropertyChoiceValuesHierchical(
			StringBuffer buf,
			int depth,
			com.filenet.api.collection.ChoiceList clProp) {
		Iterator<?> iter = clProp.iterator();
		while (iter.hasNext()) {
			Choice nextChoice = (Choice) iter.next();
			/**
			 * The type of data that a Choice object can represent is determined 
			 * by the ChoiceType constant value that you specify with its 
			 * ChoiceType property. This property determines whether a Choice 
			 * object represents 
			 * -- an integer-type choice item, 
			 * -- a string-type choice item, 
			 * -- a group node for a nested collection of integer-type choice items, 
			 * -- or a group node for a nested collection of string-type choice items.
			 *
			 */
			doDisplayChoice(buf, nextChoice, depth);
		}
	}

	/**
	 * 
	 *  A Choice object's display name, which is always a string value, 
	 *  should not be confused with its value, which can be a string, 
	 *  an integer, or a collection of Choice objects 
	 *  (when a Choice object acts as a group node). 
	 *  
	 * @param nextChoice
	 */
	private void doDisplayChoice(
			StringBuffer buf, 
			Choice nextChoice, 
			int depth) {
		String displayName = nextChoice.get_DisplayName();
		ChoiceType ct = nextChoice.get_ChoiceType();
		String choiceValueStr = null;
		boolean hierarchical = false;
		
		if (ChoiceType.INTEGER.equals(ct)) {
			choiceValueStr = nextChoice.get_ChoiceIntegerValue().toString();
		} else if (ChoiceType.STRING.equals(ct))  {
			choiceValueStr = nextChoice.get_ChoiceStringValue();
		} else {
			hierarchical = true;
			if (ChoiceType.MIDNODE_INTEGER.equals(ct)) {
				choiceValueStr = "integer collection";
			
			} else {
				choiceValueStr = "string collection";
			}
		}
		doWriteChoiceItem(buf, displayName, choiceValueStr, hierarchical, depth);
		
		if (hierarchical) {
			com.filenet.api.collection.ChoiceList childChoiceList = nextChoice.get_ChoiceValues();
			doDisplayPropertyChoiceValuesHierchical(buf, depth + 1, childChoiceList);
		}
	}

	
	/**
	 * @param displayName
	 * @param choiceValueStr
	 * @param hierarchical
	 * @param depth
	 */
	private void doWriteChoiceItem(
			StringBuffer buf, 
			String displayName, 
			String choiceValueStr,
			boolean hierarchical, int depth) {
		
		if (depth > 0) {
			for (int i = 0; i < depth; i++) {
				buf.append("|----");
				
			}
		}
		buf.append(displayName);
		buf.append("\t").append(choiceValueStr).append("\n");
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
	 *  
	 * @param choiceListUri
	 * @return Id
	 */
	private Id fetchChoiceListIdByName(String choiceListUri) throws Exception {
		String decodedName = this.decodePath(choiceListUri);
		QueryHelper helper = new QueryHelper(this.getShell());
		String query = String.format("DisplayName = \'%s\'", decodedName);
		
		Id result = helper.fetchId("ChoiceList", query);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam choiceListUriArg = null;
		// create command line handler
		
		{
			choiceListUriArg = new StringParam(CHOICE_LIST_URI_ARG, "choice list (either the id or display name)",
					StringParam.REQUIRED);
			choiceListUriArg.setMultiValued(false);
		}
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] {choiceListUriArg });
		cl.setDieOnParseError(false);

		return cl;
}

}
