/**
 * 
 */
package com.ibm.bao.ceshell.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyDefinitionObject;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.constants.FilteredPropertyType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.CEShell;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  EditInfoImpl
 *
 * @author regier
 * @date   Aug 20, 2012
 */
public class EditInfoImpl implements EditInfo {

	protected IndependentlyPersistableObject ceObj;
	protected Map<String, PropertyDescription> pdMap = new HashMap<String, PropertyDescription>();
	private ClassDefinition clsDef = null;
	protected CEShell ceShell;
	protected String className;
	
	protected EditInfoImpl() {
		
	}

	/**
	 * 
	 */
	public EditInfoImpl(CEShell ceShell, IndependentlyPersistableObject ceObj, String className) {
		this.ceShell = ceShell;
		this.ceObj = ceObj;
		this.className = className;
		ClassDescription cd = fetchClassDescription(ceShell, className);
		
		PropertyDescriptionList pdl = cd.get_PropertyDescriptions();

		for (Iterator<?> iterator = pdl.iterator(); iterator.hasNext();) {
			PropertyDescription pd = (PropertyDescription) iterator.next();
			pd.get_DataType();
			String pdName = pd.get_SymbolicName();
			pdMap.put(pdName, pd);
		}
	}

	public void setProperties(java.util.Properties props) throws Exception {
		Iterator<?> iter = props.keySet().iterator();
		while (iter.hasNext()) {
			String propName = iter.next().toString();
			String propValue = props.getProperty(propName);
			setProp(propName, propValue);
		}
	}

	public void setProp(String propName, String propValue) throws Exception {
	
		if (! pdMap.containsKey(propName)) {
			throw new IllegalArgumentException("propert with name " + propName
					+ " was not found.");
		}
		PropertyDescription pd = pdMap.get(propName);
		if (pd.get_IsReadOnly()) {
			throw new Exception(String.format("Property %s is read-only",
					propName));
		}
		applyProperty(ceObj.getProperties(), pd, propName, propValue);
	}

	public void save() {
		ceObj.save(RefreshMode.REFRESH);
	}

	protected ClassDefinition getClassDefinition() {
		if (this.clsDef == null) {
			clsDef = Factory.ClassDefinition.fetchInstance(
					ceShell.getObjectStore(), 
					this.className, 
					null);	
		}
		return clsDef;
	}

	/**
	 * @param pdMap
	 * @param props
	 */
	protected void applyProperty(Properties props, PropertyDescription pd, String propName,
			String propValue) throws Exception {
			
				TypeID typeId = pd.get_DataType();
			
				switch (typeId.getValue()) {
				case TypeID.STRING_AS_INT:
					props.putValue(propName, propValue);
					break;
				case TypeID.BOOLEAN_AS_INT:
					Boolean bValue = parseBoolean(propValue);
					props.putValue(propName, bValue);
					break;
				case TypeID.DATE_AS_INT:
					Date dateValue = parseDate(propValue);
					props.putValue(propName, dateValue);
					break;
				case TypeID.DOUBLE_AS_INT:
					Double doubleValue = Double.parseDouble(propValue);
					props.putValue(propName, doubleValue);
					break;
				case TypeID.GUID_AS_INT:
					props.putValue(propName, propValue);
					break;
				case TypeID.LONG_AS_INT:
					Integer intValue = Integer.parseInt(propValue);
					props.putValue(propName, intValue);
					break;
				case TypeID.OBJECT_AS_INT:
					String objClasName = fetchObjectPropertyClassName(propName);
					IndependentObject obj = ceShell.getObjectStore().
							fetchObject(objClasName, propValue, null);
					props.putValue(propName, obj);
				default:
					break;
				}
			}

	/**
	 * @param propName
	 * @return
	 */
	private String fetchObjectPropertyClassName(String propName) {
		ClassDefinition czd = this.getClassDefinition();
		String objClsSymbolicName = null;
		PropertyDefinitionList pdl = czd.get_PropertyDefinitions();
		
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = pdl.iterator(); iterator.hasNext();) {
			PropertyDefinition pd = (PropertyDefinition) iterator.next();
			if (pd.get_SymbolicName().equals(propName)) {
				if (pd instanceof PropertyDefinitionObject) {
					PropertyDefinitionObject pdo = (PropertyDefinitionObject) pd;
					Id objClsId = pdo.get_RequiredClassId();
					ClassDescription ocd = 
							Factory.ClassDescription.fetchInstance(
									ceShell.getObjectStore(), 
									objClsId, 
									null);
					objClsSymbolicName = ocd.get_SymbolicName();	
				}
				break;
			}
		}
		return objClsSymbolicName;
	}

	/**
	 * @param propValue
	 * @return
	 */
	private Date parseDate(String propValue) throws Exception {
		return StringUtil.parseDate(propValue);
	}

	/**
	 * @param propValue
	 * @return
	 */
	private Boolean parseBoolean(String propValue) {
		char firstChar = 'f';
		
		if (propValue == null || propValue.length() == 0) {
			return Boolean.FALSE;
		}
		firstChar = propValue.trim().toLowerCase().charAt(0);
		if ('t' == firstChar) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	protected ClassDescription fetchClassDescription(CEShell ceShell, String docClass) {
		PropertyFilter pf = null;
		pf = createPropertyFilter();
		ClassDescription cd = Factory.ClassDescription.fetchInstance(
				ceShell.getObjectStore(), docClass, pf);
		return cd;
	}

	/**
	 * @return
	 */
	private PropertyFilter createPropertyFilter() {
		PropertyFilter pf = null;
		
		pf = new PropertyFilter();
		pf.addIncludeType(0, null, Boolean.TRUE, FilteredPropertyType.ANY, null); 
				
		return pf;
	}

}