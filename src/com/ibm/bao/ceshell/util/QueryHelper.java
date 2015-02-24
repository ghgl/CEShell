/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.property.Property;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.CEShell;

/**
 *  QueryUtil
 *
 * @author GaryRegier
 * @date   May 27, 2011
 */
public class QueryHelper {
	
	private CEShell ceShell;
	
	public QueryHelper(CEShell shell) {
		this.ceShell = shell;
	}
	
	@SuppressWarnings("unchecked")
	public Id fetchId(String from, String whereClause) throws Exception {
	    SearchSQL sqlObject = new SearchSQL();
	    String query = "select Id from " + from + " where " + whereClause;
	    sqlObject.setQueryString(query);
	    Id id = null;
	    SearchScope searchScope = new SearchScope(ceShell.getObjectStore());
	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    for (Iterator iter = rowSet.iterator(); iter.hasNext();) {
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	
	    	for (Iterator propIter = row.getProperties().iterator(); propIter.hasNext(); ) {
	    		Property prop = (Property) propIter.next();
	    		Object value = prop.getObjectValue();
	    		id = new Id(value.toString());
	    		break;
	    	}
	    }
	    return id;
	}
	
	
	@SuppressWarnings("unchecked")
	public Object executeQuerySingleValue(String query) 
			throws Exception {
		Object result = null;
		SearchSQL sqlObject = new SearchSQL();    
	    sqlObject.setQueryString(query);
	    SearchScope searchScope = new SearchScope(ceShell.getObjectStore());
	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    
	    outer:
	    for (Iterator<RepositoryRow> iter = rowSet.iterator(); iter.hasNext();) {
	    	RepositoryRow row =  iter.next();
	    	for (Iterator<Property> propIter = row.getProperties().iterator(); propIter.hasNext(); ) {
	    		Property prop = propIter.next();
	    		result = prop.getObjectValue();
	    		break outer;
	    	}	
	    }
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> executeQuery(String query) throws Exception {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		SearchSQL sqlObject = new SearchSQL();    
	    sqlObject.setQueryString(query);
	    SearchScope searchScope = new SearchScope(ceShell.getObjectStore());
	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    for (Iterator<Object> iter = rowSet.iterator(); iter.hasNext();) {
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	Map<String, Object> propMap = new HashMap<String, Object>();
	    	for (Iterator<Object> propIter = row.getProperties().iterator(); propIter.hasNext(); ) {
	    		Property prop = (Property) propIter.next();
	    		String name = prop.getPropertyName();
	    		Object value = prop.getObjectValue();
	    		propMap.put(name, value);
	    	}	
	    	results.add(propMap);
	    }
		return results;
	}
}
