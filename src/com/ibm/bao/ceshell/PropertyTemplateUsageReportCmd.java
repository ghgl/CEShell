/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.FilteredPropertyType;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.QueryHelper;

/**
 *  PropertyTemplateReportCmd
 *
 * @author GaryRegier
 * @date   Jul 17, 2011
 */
public class PropertyTemplateUsageReportCmd extends BaseCommand {
	
	private static final String
		PROPERTY_NAME_ARG = "property",
		OUTFILE_OPT = "file";
	
	private static final String 
		REPORT_FMT_GRID = "grid",
		REPORT_FMT_LIST = "list";
	
	private static final String
		LIST_STYLE_OPT = "liststyle";
	
	private static final String 
		CMD = "ptusage <pat>", 
		CMD_DESC = "display property template usage by class definition",
		HELP_TEXT = CMD_DESC + 
			"\nusage:\n" +
			"ptusage\n" +
			"\tdisplay all properties with class definitions in a grid format" +
			"\nptusage -list" +
			"\n\tdisplay all properties and class definitions in a list format";
	

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam propNamePatternArg = (StringParam) cl.getArg(PROPERTY_NAME_ARG);
		BooleanParam listStyleOpt = (BooleanParam) cl.getOption(LIST_STYLE_OPT);
		FileParam outFileOpt = (FileParam) cl.getOption(OUTFILE_OPT);
		Boolean listStyle = Boolean.FALSE;
		String propNamePattern = null;
		File outfile = null;
		
		if (listStyleOpt.isSet()) {
			listStyle = listStyleOpt.getValue();
		}
		if (outFileOpt.isSet()) {
			outfile = outFileOpt.getValue();
		}
		if (propNamePatternArg.isSet()) {
			propNamePattern = propNamePatternArg.getValue();
		}
		return propertyTemplateUsageReport(propNamePattern, listStyle, outfile);
	}

	/**
	 * 
	 */
	public boolean propertyTemplateUsageReport(String propNamePattern, Boolean listStyle, File outfile) throws Exception {
		String reportFormat = REPORT_FMT_GRID;
		Set<String> clsNames = createClassNamesSet();
		Map<String, PropertyTemplateInfo> propertyTemplateInfoMap = createPropertyTemplateInfoSet(propNamePattern);
		if (Boolean.TRUE.equals(listStyle)) {
			reportFormat = REPORT_FMT_LIST;
		}
		
		if (REPORT_FMT_GRID.equals(reportFormat)) {
			displayResultsAsGrid(propertyTemplateInfoMap, clsNames);
		} else {
			Set<String> results = displayResultsAsList(propertyTemplateInfoMap, clsNames);
			writeResults(results, outfile);
		}
		return true;
	}
	
	/**
	 * @param results
	 * @param outfile
	 */
	private void writeResults(Set<String> results, File outfile) throws Exception {
		if (outfile != null) {
			try {
				getResponse().redirectOutputStream(outfile);
				writeResultsToOutStream(results);
				getResponse().printOut("wrote results to " + outfile.getAbsolutePath());
			} finally {
				getResponse().restoreOutputStream();
			}
			
		} else {		
			writeResultsToOutStream(results);
		}
	}

	public void writeResultsToOutStream(Set<String> results) {
		for (String row : results) {
			getResponse().printOut(row);
		}
	}

//	/**
//	 * @param results
//	 * @param outfile
//	 */
//	private void writeResultsToFile(Set<String> results, File outfile) throws Exception {
//		BufferedWriter writer = null;
//		
//		try {
//			writer = new BufferedWriter(new FileWriter(outfile));
//			for (String row : results) {
//				writer.write(row);
//				writer.write("\n");
//			}
//			writer.close();
//		} finally {
//			if (writer != null) {
//					writer = null;
//			}
//		}
//		
//	}

	private Set<String> displayResultsAsList(
			Map<String, PropertyTemplateInfo> propertyTemplateInfoMap,
			Set<String> clsNames) {
		Set<String> results = new TreeSet<String>(new Comparator<String>() {
			public int compare(String o1, String o2) {
				//return o1.toUpperCase().compareTo(o2.toUpperCase());
				return o1.compareTo(o2);
			}
		});
		Set<String> propTemplateNames = new TreeSet<String>(new Comparator<String>() {

			public int compare(String o1, String o2) {
//				return o1.toUpperCase().compareTo(o2.toUpperCase());
				return o1.toUpperCase().compareTo(o2);
			}
			
		});
		propTemplateNames.addAll(propertyTemplateInfoMap.keySet());
		
		for(String propName : propTemplateNames) {
			PropertyTemplateInfo nextInfo = propertyTemplateInfoMap.get(propName);
			Set<String> clsDefs = nextInfo.getUsedInClassDefinitions();
			if (clsDefs.size() == 0) {
				String row = propName + "\t" + "**unused**";
				results.add(row);
			} else {
				for(String clsDefSymbolicName : nextInfo.getUsedInClassDefinitions()) {
					String row = propName + "\t" + clsDefSymbolicName;
					results.add(row);
				}
			}
		}
		return results;
	}
	
	/**
	 * @return
	 */
	private Set<String> createClassNamesSet() throws Exception {
		Set<String>	clsNames = new TreeSet<String>();
		QueryHelper helper = new QueryHelper(this.getShell());
		List<Map<String, Object>> resultSet = 
				helper.executeQuery("select SymbolicName from ClassDefinition");
		
		for(Map<String, Object> record : resultSet) {
			String name = record.get(PropertyNames.SYMBOLIC_NAME).toString();
			clsNames.add(name);
		}
		return clsNames;
	}

	/**
	 * @param propertyTemplateInfoMap
	 */
	private void displayResultsAsGrid(
			Map<String, PropertyTemplateInfo> propertyTemplateInfoMap,
			Set<String> clsNames) {
		int pos = 1;
		for(String clsSymbolicName : clsNames) {
			getResponse().printOut(pos++ + "\t" + clsSymbolicName);
		}
		
		// header
		{
			StringBuffer header = new StringBuffer();
			header.append("PT_Name").append("\t");
			header.append("cnt").append("\t");
			
			for(int i = 0; i < clsNames.size(); i++) {
				int clsNameCtr = i + 1;
				header.append("" + clsNameCtr++).append("\t");
			}
			getResponse().printOut(header.toString());
		}
		for (PropertyTemplateInfo nextInfo : propertyTemplateInfoMap.values()) {
			StringBuffer row = new StringBuffer();
			row.append(nextInfo.getSymbolicName()).append("\t");
			row.append(nextInfo.getUsageCount()).append("\t");
			
			for(String clsName: clsNames) {
				if (nextInfo.usedIn(clsName)){
					row.append("Y");
				} else {
					row.append("N");
				}
				row.append("\t");
			}
			getResponse().printOut(row.toString());
		}	
	}

	/**
	 * @param propNamePattern 
	 * @param propertyTemplateInfoMap
	 */
	

	private Map<String, PropertyTemplateInfo> createPropertyTemplateInfoSet(String propNamePattern) {
		Map<String, PropertyTemplateInfo> propTemplateInfoMap = new HashMap<String, PropertyTemplateInfo>();
		Boolean continuable = new Boolean(true);
		SearchSQL sqlObject = new SearchSQL();
		SearchScope search = new SearchScope(getShell().getObjectStore());
	    Integer myPageSize = new Integer(300);
	    PropertyFilter myFilter = new PropertyFilter();
	    IndependentObjectSet myObjects = null;
	    	    
	    sqlObject.setSelectList("pt.SymbolicName, pt.Id, pt.UsedInClasses"); 
	    sqlObject.setFromClauseInitialValue("PropertyTemplate", "pt", true);
	    if (propNamePattern != null) {
	    	String where = String.format("SymbolicName like '%s'", new Object[] {propNamePattern});
//	    	String where = "SymbolicName like \'A%\'";
	    	sqlObject.setWhereClause(where);
	    }
	    int myFilterLevel = 1;
	    myFilter.setMaxRecursion(myFilterLevel);
	    myFilter.addIncludeType(new FilterElement(null, null, null, FilteredPropertyType.ANY, null)); 
	        
	    myObjects = search.fetchObjects(sqlObject, myPageSize, myFilter, continuable);
	    
	    for (Iterator iter = myObjects.iterator(); iter.hasNext();) {
	    	PropertyTemplateInfo propTemplateInfo = null;
	    	IndependentObject obj = (IndependentObject) iter.next();
	    	String symbolicName = obj.getProperties().get(PropertyNames.SYMBOLIC_NAME).getStringValue();
	    	IndependentObjectSet cdSet = obj.getProperties().getIndependentObjectSetValue("UsedInClasses");
	    	
	    	propTemplateInfo = new PropertyTemplateInfo(symbolicName);
	    	addUsedInClassnamesToPropertyTemplate(cdSet, propTemplateInfo);
	    	propTemplateInfoMap.put(propTemplateInfo.getSymbolicName(), propTemplateInfo);
	    }
	    return propTemplateInfoMap;
	}


	/**
	 * @param cdSet
	 */
	private void addUsedInClassnamesToPropertyTemplate(IndependentObjectSet cdSet, 
			PropertyTemplateInfo propTemplateInfo) {
			
		for (@SuppressWarnings("rawtypes")
		Iterator iter = cdSet.iterator(); iter.hasNext();) {
			IndependentObject obj = (IndependentObject) iter.next();
			String id = obj.getProperties().getIdValue(PropertyNames.ID).toString();
			String clsSymbolicName = obj.getProperties().getStringValue(PropertyNames.SYMBOLIC_NAME);
			propTemplateInfo.addUsedInClass(clsSymbolicName);
		}
	}
	

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam listStyleOpt = null;
		FileParam outFileOpt = null;
		StringParam propNameArg = null;
		
		// params
		{
			listStyleOpt = new BooleanParam(LIST_STYLE_OPT, "report in a list style (default is grid");
			listStyleOpt.setOptional(BooleanParam.OPTIONAL);
			
			outFileOpt = new FileParam(OUTFILE_OPT,
					"Export file )",
					FileParam.OPTIONAL);
			outFileOpt.setOptionLabel("<outFile>");
			outFileOpt.setMultiValued(false);
			outFileOpt.setOptionLabel("<output-file>");
		}
	
		// cmd args
		{
			propNameArg = new StringParam(PROPERTY_NAME_ARG, 
					"user properties",
					StringParam.OPTIONAL);
			propNameArg.setMultiValued(false);
		}
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {listStyleOpt, outFileOpt}, 
					new Parameter[] { propNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}

class PropertyTemplateInfo implements Comparable<PropertyTemplateInfo> {
	private String symbolicName;
	
	private Set<String> usedInClassDefinitions = new HashSet<String>();
	
	public PropertyTemplateInfo(String symbolicName) {
		this.symbolicName = symbolicName;
	}
	
	public void addUsedInClass(String clsSymbolicName) {
		usedInClassDefinitions.add(clsSymbolicName);
	}
	
	public int getUsageCount() {
		return usedInClassDefinitions.size();
	}
	
	public boolean usedIn(String clsSymbolicName) {
		return (usedInClassDefinitions.contains(clsSymbolicName));
	}	

	public Set<String> getUsedInClassDefinitions() {
		return usedInClassDefinitions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public int compareTo(PropertyTemplateInfo o) {
		return this.getSymbolicName().compareTo(o.getSymbolicName());
	}
}

