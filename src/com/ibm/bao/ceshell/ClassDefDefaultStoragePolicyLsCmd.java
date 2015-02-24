/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.filenet.api.admin.DocumentClassDefinition;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyDefinitionObject;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Factory;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.QueryHelper;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  ClassDefDefaultStoragePolicyLsCmd
 *
 * @author regier
 * @date   Jan 24, 2012
 */
public class ClassDefDefaultStoragePolicyLsCmd extends BaseCommand {
	
	private static final String 
	CMD = "czdspls",
	CMD_DESC = "list default storage policy for Document class definitions ",
	HELP_TEXT = "\nUsage:" +
		"\nczdspls"; 

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return classDefDefaultStoragePolicyLs();
	}

	/**
	 * 
	 */
	public boolean classDefDefaultStoragePolicyLs() throws Exception {
		RepositoryRowSet rowSet = null; 
		Map<String, String> storagePolicies = fetchStoragePolicies();
		ColDef[] defs = new ColDef[] {
				new ColDef("Name", 40, StringUtil.ALIGN_LEFT),
				new ColDef("StoragePolicy", 30, StringUtil.ALIGN_LEFT)
		};
		rowSet = fetchDocClassDefinitions();
	    
	    for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
	    	RepositoryRow record = (RepositoryRow) iter.next();
	    	com.filenet.api.property.Properties props = 
				record.getProperties();
	    	DocClassDDefaultStoragePolicyInfo nextDc = 
	    			readItem(storagePolicies, props);
	    	String[] row = new String[]  {
	    			nextDc.getSymbolicName(),
	    			nextDc.getStoragePolicyName()
			};
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
	    }
		return true;
	}

	/**
	 * @return
	 */
	private Map<String, String> fetchStoragePolicies() throws Exception {
		String query = "Select Id, Name from StoragePolicy"; 
		Map<String, String> storagePolicyMap = new HashMap<String, String>();
		QueryHelper helper = new QueryHelper(getShell());
		List<Map<String, Object>> results = helper.executeQuery(query);
		for (Map<String, Object> row : results) {
			String id = row.get("Id").toString();
			String name = row.get("Name").toString();
			storagePolicyMap.put(id,  name);
		}
		return storagePolicyMap;
	}

	private RepositoryRowSet fetchDocClassDefinitions() {
		RepositoryRowSet rowSet;
		String query;
		
		query = "select Id cid, SymbolicName cname  from DocumentClassDefinition c order by SymbolicName";
	
		SearchSQL sqlObject = new SearchSQL();
	    SearchScope searchScope = new SearchScope(getShell().getObjectStore());
	    
	    
	    PropertyFilter filter = null;
	    {
	    	String propertyNames =  PropertyNames.NAME + " " + PropertyNames.ID + " " + PropertyNames.SYMBOLIC_NAME;
	    	filter = new PropertyFilter();
	    	filter.setLevelDependents(Boolean.FALSE);
	    	filter.setMaxRecursion(1);
	    	filter.addIncludeProperty(new FilterElement(new Integer(1), null, false, propertyNames, null ));
	    }
	    sqlObject.setQueryString(query);
	    rowSet = searchScope.fetchRows(sqlObject, null, filter, new Boolean(true));
		return rowSet;
	}
	
	/**
	 * @param row
	 * @return
	 */
	private DocClassDDefaultStoragePolicyInfo readItem(
			Map<String, String> storagePolicies,
			com.filenet.api.property.Properties props) {
		String cname, cid = null;
		DocClassDDefaultStoragePolicyInfo docclassInfo = new DocClassDDefaultStoragePolicyInfo();
		Id id = null;
		
		cname = props.getStringValue("cname");	
		cid = props.getIdValue("cid").toString();
		id = new Id(cid);
		
		docclassInfo.setSymbolicName(cname);
		DocumentClassDefinition dcd = null;
		 PropertyFilter filter = null;
	    {
	    	String propertyNames = 
					PropertyNames.PROPERTY_DEFINITIONS + " " +
					PropertyNames.PROPERTY_DEFAULT_OBJECT + " " +
					PropertyNames.NAME + " " +
					PropertyNames.ID + " " + 
					PropertyNames.SYMBOLIC_NAME;
	    	filter = new PropertyFilter();
	    	filter.setLevelDependents(Boolean.FALSE);
	    	filter.setMaxRecursion(1);
	    	filter.addIncludeProperty(new FilterElement(new Integer(1), null, true, propertyNames, null ));
	    }
		dcd = Factory.DocumentClassDefinition.fetchInstance(this.getShell().getObjectStore(), id, filter);
		
		Id spid = fetchStoragePolicy(dcd);
		String secPolName = "<null>";
		if (spid != null) {
			if (storagePolicies.containsKey(spid.toString())) {
				secPolName = storagePolicies.get(spid.toString());
			}
		}
		
		docclassInfo.setStoragePolicyName(secPolName);
		return docclassInfo;
	}
	
	
	private Id fetchStoragePolicy(DocumentClassDefinition cd) {
		PropertyDefinitionList pdl = cd.get_PropertyDefinitions();
		PropertyDefinitionObject sp = null;
		for (Object obj : pdl) {
			PropertyDefinition pd = (PropertyDefinition) obj;
			if (pd.get_SymbolicName().equals("StoragePolicy")) {
				sp = (PropertyDefinitionObject) pd;
				break;
			}
		}
		IndependentObject io = sp.get_PropertyDefaultObject();
		Id id = new Id(io.getObjectReference().getObjectIdentity());
		return id;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		
		
		// options
		{
			
		}
		// cmd args
		{
			
		}
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] {  });
		cl.setDieOnParseError(false);

		return cl;
	}
}

class DocClassDDefaultStoragePolicyInfo{
	
	private String symbolicName;
	private String storagePolicyName;

	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public String getStoragePolicyName() {
		return storagePolicyName;
	}

	public void setStoragePolicyName(String storagePolicyName) {
		this.storagePolicyName = storagePolicyName;
	}
}
