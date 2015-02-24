/**
 * 
 */
package com.ibm.bao.ceshell;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.filenet.api.meta.ClassDescription;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.QueryHelper;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  DocCheckOutListCmd
 *
 * @author GaryRegier
 * @date   Jun 23, 2011
 */
public class DocCheckOutListCmd extends BaseCommand {
	
	public static final String
		CREATOR = "Creator",
		DATE_CREATED = "DateCreated";
	
	public static final String
		ORDERBY_ASC = "asc",
		ORDERBY_DESC = "desc";
	
	public static final String[] ORDERBY_FIELDS = new String[] {
		CREATOR,
		DATE_CREATED
	};
		
	public static final String ORDERBY_DEFAULT_FIELD = DATE_CREATED;
	public static final String ASC_DESC_DEFAULT = ORDERBY_ASC;
	
	// param names
	private static final String 
			ORDER_BY_OPT = "orderby",
			DESCENDING_ORDER_OPT = "descending",
			USER_OPT = "user";
	
	private static final String 
		CMD = "doccols", 
		CMD_DESC = "list documents that are checked out (reserverd)",
		HELP_TEXT = CMD_DESC + 
		 	"\nUsage:\n" +
		 	"\tcd doccols\n" +
		 	"\t list all documents current checked out";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String orderBy = ORDERBY_DEFAULT_FIELD;
		String ascOrDesc = ASC_DESC_DEFAULT;
		String user = null;
		BooleanParam descendingOrderOpt = (BooleanParam) cl.getOption(DESCENDING_ORDER_OPT);
		StringParam orderByOpt= (StringParam) cl.getOption(ORDER_BY_OPT);
		StringParam userOpt = (StringParam) cl.getOption(USER_OPT);
		
		if (descendingOrderOpt.isSet()) {
			ascOrDesc = ORDERBY_DESC;
		}
		if (orderByOpt.isSet()) {
			orderBy = orderByOpt.getValue();
		}
		if (userOpt.isSet()) {
			user = userOpt.getValue();
		}
		return docCheckoutLs(orderBy, ascOrDesc, user);

	}

	/**
	 * 
	 */
	public boolean docCheckoutLs(
			String orderBy, 
			String ascOrDesc, 
			String user) throws Exception {
		String query = null;
//		String query = "select ClassDescription, Id, Creator, DateCreated, " +
//			" MajorVersionNumber, MinorVersionNumber, IsReserved, DocumentTitle " +
//			" from Document where IsReserved = True" +
//			" order by " + orderBy + " " + ascOrDesc;
		StringBuffer qry = new StringBuffer();
		qry.append("select ClassDescription, Id, Creator, DateCreated, ")
			.append(" MajorVersionNumber, MinorVersionNumber, IsReserved, DocumentTitle ")
			.append(" from Document where IsReserved = True ");
		if (user != null) {
			qry.append(" and Creator = \'");
			qry.append(sqlEncode(user));
			qry.append("\'");
		}
		if (orderBy != null) {
			qry.append(" order by " + orderBy + " " + ascOrDesc);
		}
		query = qry.toString();
		QueryHelper helper = new QueryHelper(this.getShell());
		List<Map<String, Object>> results = helper.executeQuery(query);
		
		formatResults(results);
		return true;
		
	}

	/**
	 * @param user
	 * @return
	 */
	private String sqlEncode(String user) {
		// TODO Auto-generated method stub
		return user;
	}

	/**
	 * @param results
	 */
	private void formatResults(List<Map<String, Object>> results) {
		Date today = new Date();
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				StringUtil.DATE_FMT_FEM);
		ColDef[] defs = new ColDef[] {
				new ColDef("Creator", 12, StringUtil.ALIGN_LEFT),
				new ColDef("# days", 5, StringUtil.ALIGN_RIGHT),
				new ColDef("DateCreated", 21, StringUtil.ALIGN_LEFT),
				new ColDef("Ver", 5, StringUtil.ALIGN_RIGHT),
				new ColDef("Title", 37, StringUtil.ALIGN_LEFT),
				new ColDef("ClassName", 20, StringUtil.ALIGN_LEFT),
				new ColDef("Id", 40, StringUtil.ALIGN_LEFT)
			};
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		for(Iterator<Map<String, Object>> iter = results.iterator(); iter.hasNext();) {
			Map<String, Object> nextInfo = iter.next();
			Date creationDate = (Date) nextInfo.get("DateCreated");
			long numDaysCheckedOut = getNumDaysCheckedOut(today, creationDate);
			Integer majorVersion = (Integer) nextInfo.get("MajorVersionNumber");
			Integer minorVersion = (Integer) nextInfo.get("MinorVersionNumber");
			String versionNum = majorVersion.toString() + 
					"." + minorVersion.toString();
			ClassDescription clsDescription = 
					(ClassDescription) nextInfo.get("ClassDescription");
			Id clsDescId = clsDescription.get_Id();
			String clsDescriptionName = fetchClassDescriptionName(clsDescId);
			String docTitle = "";
			{
				
				if (nextInfo.get("DocumentTitle") != null) {
					docTitle = nextInfo.get("DocumentTitle").toString();
				}
			}
			String[] row = new String[] {
					nextInfo.get("Creator").toString(),
					"" + numDaysCheckedOut,
					dateFormatter.format(creationDate),
					versionNum,
					docTitle,
					clsDescriptionName,
					nextInfo.get("Id").toString()
			};
			
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
		}
	}

	/**
	 * Use this to calculate the number of days checked out.
	 * <br>
	 * (don't use this algorithm to calculate do anything important like
	 *  financial calculations -- it's not perfect -:) )
	 * @param todaysDate
	 * @param creationDate
	 * @return
	 */
	private long getNumDaysCheckedOut(Date todaysDate, Date creationDate) {
		Calendar currentDateCal = Calendar.getInstance();
		Calendar createDateCal = Calendar.getInstance();
		createDateCal.setTime(creationDate);
		long currentMillis = currentDateCal.getTimeInMillis();
		long checkoutMillis = createDateCal.getTimeInMillis();
		long diff = currentMillis - checkoutMillis;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return diffDays;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		BooleanParam descendingOrderOpt = null;
		StringParam orderByOpt= null;
		StringParam userOpt = null;
		String orderByDescription = StringUtil.appendArrayToString(
				"field to order by. Valid values are: ", ", ", ORDERBY_FIELDS);
		// options
		descendingOrderOpt = new BooleanParam(DESCENDING_ORDER_OPT,
				"order the results in descending order (ascending is the default");
		descendingOrderOpt.setOptional(BooleanParam.OPTIONAL);
		
		orderByOpt = new StringParam(ORDER_BY_OPT, 
				orderByDescription,
				ORDERBY_FIELDS, StringParam.OPTIONAL);	

		userOpt = new StringParam(USER_OPT, 
				"Just display documents checked out by this user",
				StringParam.OPTIONAL);
		// cmd args
		

		

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {descendingOrderOpt, orderByOpt, userOpt  }, 
					new Parameter[] { });
		cl.setDieOnParseError(false);

		return cl;
	}
	


}
