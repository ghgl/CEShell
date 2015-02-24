package com.ibm.bao.ceshell;
import java.util.Iterator;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.property.Properties;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

public class ListTypesCmd extends BaseCommand {
	
	private static final String 
		CMD = "listTypes", 
		CMD_DESC = "List object types",
		HELP_TEXT = "List object types in the repository. A pattern can be used";
	
	// SELECT <FIELD_LIST> from ClassDefinition where SymbolicName like 'C%' order by SymbolicName"
	private static final String 
		SYMBOLIC_NAME = "SymbolicName",
		DISPLAY_NAME = "DisplayName",
		DEFAULT_INSTANCE_OWNER = "DefaultInstanceOwner",
		SHORT_QRY_FIELDS = SYMBOLIC_NAME, 
		LONG_QRY_FIELDS = SHORT_QRY_FIELDS + "," + DISPLAY_NAME + ", " + DEFAULT_INSTANCE_OWNER;

	// param names
	private static final String 
			LONG = "long",
			TYPE = "TYPE";

	@Override
	protected boolean doRun(CmdLineHandler cmdLine) throws Exception {
		BooleanParam longOpt = (BooleanParam) cmdLine.getOption(LONG);
		StringParam typePatternArg = (StringParam)cmdLine.getArg(TYPE);
		boolean listLong = (longOpt.isSet() ? true : false);
		String typePattern = typePatternArg.getValue();
		
		return listTypes(listLong, typePattern);
	}

	public boolean listTypes(boolean listLong,String typePattern) {
		String query = createQuery(listLong, typePattern);
		SearchSQL sqlObject = new SearchSQL();
	    sqlObject.setQueryString(query);
	    
	    SearchScope searchScope = new SearchScope(this.getShell().getObjectStore());
	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    
	    for (Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	Properties props = row.getProperties();
	    	String result = getResults(listLong, props);
	    	System.out.println(result);
	    }
	    return true;
	}

	private String getResults(boolean listLong, Properties props) {
		StringBuffer results = new StringBuffer();
		results.append(props.getStringValue(SYMBOLIC_NAME));
		if (listLong) {
			results.append("\t");
			results.append(props.getStringValue(DISPLAY_NAME));
			results.append("\t").append(props.getStringValue(DEFAULT_INSTANCE_OWNER));
		}
		return results.toString();
	}

	private String createQuery(boolean listLong, String typePattern) {
		StringBuffer query = new StringBuffer();
		String result = null;
		query.append("SELECT ");
		if (listLong) {
			query.append(LONG_QRY_FIELDS);
		} else {
			query.append(SHORT_QRY_FIELDS);
		}
		query.append(" FROM ClassDefinition ");
		if (typePattern != null) {	
				query.append(" WHERE SymbolicName LIKE \'" + typePattern.toString() + "\'");
		}
		query.append(" ORDER BY SymbolicName");
		result = query.toString();
		return result; 
	}

	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam longOpt = null;
		StringParam typePatternArg = null;
		
		// options
		longOpt = new BooleanParam(LONG,
				"use a long listing format");
		
		// cmd args
		typePatternArg = new StringParam(TYPE, "TYPEs to list",
				StringParam.OPTIONAL);
		typePatternArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { longOpt }, 
					new Parameter[] { typePatternArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
