package com.ibm.bao.ceshell;

import java.util.Date;
import java.util.Iterator;

import com.filenet.api.admin.EventQueueItem;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.CustomObject;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.RetrievingBatch;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.cmdline.VersionCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

public class FooCommand extends BaseCommand {
	
	
	
	private static final String 
		CMD = "foo", 
		VERSION = "v0.1",
		CMD_DESC = "explore the apis",
		HELP_TEXT = "exploratory command.";
	
	public static final String FOO_ARG = "foo";

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		
		Parameter<String> fooArg = (Parameter<String>) cl.getArg(FOO_ARG);
		if (fooArg.isSet()) {
			String foo = fooArg.getValue();
		}
//		describePermissions("BHDC");
//		dumpAccessRights();
		//testAnd();
		//testProperties();
		//testLs();
		//testSpacePath();
		// testDocPropsById();
		//testDocClassDefaultSec();
		// testShowDocAcls();
		//testAddAcl();
//		testUser();
		//testFetchUser();
//		testUserQuery();
//		testGroups();
		//testOsls();
//		testAddDocs();
//		testQueryDocContentElements();
//		 testDcls();
//		testCompareAclLevels();
//		testBatchUpdate();
//		testDateQry();
//		testDocClassDefs();
//		testClear();
		//listQueues();
//		fetchWorkClassNames();
//		propReport();
//		testCdLs();
//		debugDev2();
//		doEdit();
//		doTestEventHandler();
//		doTraceLogging();
//		doLog4jPoc();
//		doTypeIds();
//		doX();
//		doDocSecurityFolder();
//		doDocClsStoragePolicy();
//		doOsAddOns();
//		tstVerify();
//		testPeRavind();
//		testCzdAddPropTmpl();
//		testRedirectOutput();
//		testChoiceListQuery();
//		testAliasIds();
//		testVersionSeries();
//		testEventQueueList();
//		testEventQueueListxx();
// 		testCustomObjectLock();
//		testFilenetFacade();
//		testArgs();
		testFind();
		return true;
	}
	
	private void testFind() {
		//String query = "select top 100 d.ObjectType, d.ClassDescription, d.id, d.Name, d.DateCreated FROM Document d order by DateCreated asc"
				
		
	}

	private void testArgs() {
		getResponse().printOut("Number of args: " + request.getArgs().length);
		getResponse().printOut("Command line: " + request.getCmdLine());
		int cnt = 0;
		for(String arg: request.getArgs() ) {
			getResponse().printOut("" + cnt + " " + arg);
			cnt++;
		}
	}

//	/**
//	 * 
//	 */
//	private void testFilenetFacade() {
//		FileNetFacade facade = new FileNetFacade();
//		String docIdStr = "{D57FC2BF-2075-4103-BCA9-D32DF17F99FD}";
//		String ver2 = "this is test 1",
//			 ver3 = "this is test 2";
//		
//		Document doc = null;
//		
//		ObjectStore os = getObjectStore();
//		boolean result = false;
//		try {
//			
//			{
//				log("start test:  checkout/cancelcheckout");
//				facade.checkout(os, docIdStr);
//				doc = facade.fetchDocument(os, docIdStr);
//				result = assertDocStatus(doc, true, 1, 0);
//				
//				facade.cancelCheckout(os, docIdStr);
//				doc = facade.fetchDocument(os, docIdStr);
//				result = assertDocStatus(doc, false, 1, 0);
//				log("checkout/cancelcheckout: " + result);
//			}
//			
//			{
//				log("start test:  checkout/checkin (no change)");
//				facade.checkout(os, docIdStr);
//				facade.checkin(os, docIdStr, CheckinType.MAJOR_VERSION);
//				doc = facade.fetchDocument(os, docIdStr);
//				result = assertDocStatus(doc, false, 2, 0);
//				log("checkout/checkin (no change): " + result);
//			}
//			
//			{
//				log("start test:  checkout/checkin with update (new version 3)");
//				UpdateDTO updateInfo = new UpdateDTO(ver2, "ver2.txt");
//				
//				facade.checkInNewVersion(os, docIdStr, updateInfo);
//				doc = facade.fetchDocument(os, docIdStr);
//				result = assertDocStatus(doc, false, 3, 0);
//				log("checkout/checkin with update " + result);
//			}
//			
//			{
//				log("start test:  checkout, updateReservation, cancel");
//				UpdateDTO updateInfo = new UpdateDTO(ver3, "ver3.txt");
//				
//				//* checkout
//				facade.checkout(os, docIdStr);
//				doc = facade.fetchDocument(os, docIdStr);
//				result = assertDocStatus(doc, true, 3, 0);
//				
//				//* update
//				facade.updateReservation(os, docIdStr, updateInfo);
//				doc = facade.fetchDocument(os, docIdStr);
//				result = assertDocStatus(doc, true, 3, 0);
//				
//				// cancel checkout
//				facade.cancelCheckout(os, docIdStr);
//				doc = facade.fetchDocument(os, docIdStr);
//				result = assertDocStatus(doc, false, 3, 0);
//			}
//			
//			{
//				log("start test:  checkout, updateReservation, checkin");
//				UpdateDTO updateInfo = new UpdateDTO(ver3, "ver3.txt");
//				
//				//* checkout
//				facade.checkout(os, docIdStr);
//				doc = facade.fetchDocument(os, docIdStr);
//				result = assertDocStatus(doc, true, 3, 0);
//				
//				//* update
//				facade.updateReservation(os, docIdStr, updateInfo);
//				doc = facade.fetchDocument(os, docIdStr);
//				result = assertDocStatus(doc, true, 3, 0);
//				
//				// checkin
//				facade.checkin(os, docIdStr, updateInfo.getCheckinType());
//				doc = facade.fetchDocument(os, docIdStr);
//				result = assertDocStatus(doc, false, 4, 0);
//				
//			}
//				
//			
//		} catch (Exception e) {
//			System.err.print(e.getMessage());
//		}
//			
//		
//	}
	
	boolean assertDocStatus(Document doc, 
			boolean expectedLockStatus, 
			int expectedMajorVersionNum, 
			int expectedMinorVersionNum) throws Exception {
		VersionSeries vs = doc.get_VersionSeries();
		
		boolean actualLockedStatus = vs.get_IsReserved();
		int actualMajorVersionNum = doc.get_MajorVersionNumber().intValue();
		int actualMinorVersion = doc.get_MinorVersionNumber().intValue();
		boolean pass = true;
		
		if (expectedLockStatus != actualLockedStatus) {
			System.err.println("lock status : expected " + expectedLockStatus);
			pass = false;
		}
		if (expectedMajorVersionNum != actualMajorVersionNum) {
			System.err.println("Major version:  expected " + expectedMajorVersionNum + ", actual " + actualMajorVersionNum);
			pass = false;
		}
		if(expectedMinorVersionNum != actualMinorVersion) {
			System.err.println("Major version:  expected " + expectedMajorVersionNum + ", actual " + actualMajorVersionNum);
			pass = false;
		}
		return pass;	
	}
	
	/**
	 * @param string
	 */
	private void log(String msg) {
		getResponse().printOut(msg);
		
	}
	
	/**
	 * @return
	 */
	private ObjectStore getObjectStore() {
		return this.getShell().getObjectStore();
	}
	
	/**
	 * 
	 */
	private void testCustomObjectLock() {
		CustomObject co = 
				    com.filenet.api.core.Factory.CustomObject.createInstance(getShell().getObjectStore(), 
				    		"CustomObject");
		co.lock(10, "foo");
		co.save(RefreshMode.REFRESH);
		co.updateLock(5);
		System.out.println("co saved: " + co.get_Id());
	}

	//	private void testEventQueueListx() {
//		Integer pageSize = new Integer(100);
//		Boolean continuable = Boolean.FALSE;
//		PropertyFilter filter = null;
//		SearchSQL searchSQL = new SearchSQL();
//	    
//		searchSQL.setQueryString("select * from EventQueueItem");
//	    SearchScope searchScope = new SearchScope(getShell().getObjectStore());
//	    RepositoryRowSet rowSet = searchScope.fetchRows(searchSQL, pageSize, filter, continuable);
//	    Iterator iter = rowSet.iterator();
//	    while (iter.hasNext()) {
//	    	RepositoryRow nextRow = (RepositoryRow) iter.next();
//	    	Properties props = nextRow.getProperties();
//	    	Id id = props.getIdValue(PropertyNames.ID);
//	    	
//	    	Object sourceDoc = props.getObjectValue(PropertyNames.SOURCE_DOCUMENT);
//	    	
//	    	
//	    	getResponse().printOut(id.toString());
//	    	getResponse().printOut(sourceDoc.getClass().getName());
//	    }
//	}
//	
	private PropertyFilter getEventPropertyFilter() {
		PropertyFilter filter = null;
	    
    	String propertyNames =  
    			PropertyNames.NAME + " " + 
    			PropertyNames.CLASS_DESCRIPTION + " " + 
    			PropertyNames.ID + " " +
    			PropertyNames.RETRY_COUNT + " " + 
    			PropertyNames.SOURCE_OBJECT + " " +
    			PropertyNames.QUEUED_OBJECT + " " +
    			PropertyNames.SYMBOLIC_NAME;
    	
    	filter = new PropertyFilter();
    	filter.setLevelDependents(Boolean.TRUE);
    	filter.setMaxRecursion(0);
    	filter.addIncludeProperty(new Integer(0), null, Boolean.FALSE, propertyNames, null );
	   
    	return filter;
	}
	
	/**
	 * 
	 */
	private void testEventQueueListxx() {
		Integer pageSize = new Integer(100);
		Boolean continuable = Boolean.FALSE;
		PropertyFilter filter = null;
		SearchSQL searchSQL = new SearchSQL();
	    
		filter = getEventPropertyFilter();
		searchSQL.setQueryString("select Id from EventQueueItem");
	    SearchScope searchScope = new SearchScope(getShell().getObjectStore());
	    	    
	    IndependentObjectSet objSet = searchScope.fetchObjects(searchSQL, pageSize, filter, continuable);
	    Iterator iter = objSet.iterator();
	    while (iter.hasNext()) {
	    	EventQueueItem nextEvent = (EventQueueItem) iter.next();
	    	Id eventId  = nextEvent.get_Id();
	    	Integer retryCount = nextEvent.get_RetryCount();
	    	Date dateCreated = nextEvent.get_DateCreated();
	    	nextEvent.getClassName();
	    	IndependentObject source = nextEvent.get_SourceObject();
	    	
//	    	IndependentObject queuedObject = nextEvent.get_QueuedObject();
	    	IndependentObject queuedObject = nextEvent.get_SourceObject();
	    	String queuedObjectSymbolicName = queuedObject.get_ClassDescription().get_SymbolicName();
	    	String quuedObjectId = queuedObject.getProperties().getIdValue(PropertyNames.ID).toString();
	    	
	    	getResponse().printOut(eventId.toString());
	    	getResponse().printOut("queuedObjectSymbolicName:\t" + queuedObjectSymbolicName);
	    	getResponse().printOut("queueObjectId:\t" + quuedObjectId);
	    	
	    }
	    
	}
	
	private void testEventQueueList() throws Exception {
		PropertyFilter filter = null;
		ObjectStore os = getShell().getObjectStore();
		filter = getEventPropertyFilter();
		String[] ids = {
				"{77FE7C14-5DB0-4F8D-8E8A-A199BD498F69}",
				"{B63C302A-C519-4326-A0BA-162CD7C54895}",
				"{49E95751-556E-49B3-8F19-D5B3C20674D4}",
				"{5DAA5F82-1E6A-481B-A0B1-6C8E91AA6086}"
		};
		
		RetrievingBatch rb = RetrievingBatch.createRetrievingBatchInstance(
				getShell().getDomain(getShell().getCEConnection()));
		for (String id : ids) {		
			EventQueueItem eventQueueItem = Factory.EventQueueItem.getInstance(os, new Id(id));
			rb.add(eventQueueItem, filter);
		}
		rb.retrieveBatch();
		
		Iterator iter = rb.getBatchItemHandles(null).iterator();
		while (iter.hasNext()) {
			com.filenet.api.core.BatchItemHandle handle = (com.filenet.api.core.BatchItemHandle) iter.next();
			EventQueueItem eventQueueItem = (EventQueueItem) handle.getObject();
			String id = eventQueueItem.get_Id().toString();
			IndependentObject source = eventQueueItem.get_SourceObject();
			String sourceSymbolicName = source.get_ClassDescription().get_SymbolicName();
	    	String sourceObjectId = source.getProperties().getIdValue(PropertyNames.ID).toString();
	    	IndependentObject sub = eventQueueItem.get_QueuedObject();
	    	String subName = sub.getProperties().getStringValue(PropertyNames.NAME);
			getResponse().printOut(id);
			getResponse().printOut(sourceSymbolicName);
			getResponse().printOut(sourceObjectId);
			getResponse().printOut(subName);
		}
	}
	
	

	
//	private void testVersionSeries() {
//		Id expectedid = new Id("{BF41779C-14FC-41C0-A9AA-AE5BDC8EC29D}");
//		Id id = new Id("{597C5DC3-7C85-4C2C-88F2-2688948152EE}");
//		
//		VersionSeries vs = Factory.VersionSeries.fetchInstance(this.getShell().getObjectStore(), id, null);
//		Document doc= (Document) vs.get_CurrentVersion();
//		Id docId = doc.get_Id();
//		System.out.println(docId.toString());
//	}
//	
	/**
//	 * 
//	 */
//	private void testAliasIds() {
//		String query = "select  pt.Id, pt.SymbolicName, pt.AliasIds FROM PropertyTemplate pt";
//		SearchSQL sqlObject = new SearchSQL();
//	    sqlObject.setQueryString(query);
//	    SearchScope searchScope = new SearchScope(ceShell.getObjectStore());
//	   
//	    StringBuffer buf = new StringBuffer();
//	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
//	    for (Iterator iter = rowSet.iterator(); iter.hasNext();) {
//	    	RepositoryRow row = (RepositoryRow) iter.next();
//	    	buf.append( row.getProperties().get("Id").getIdValue()).append("\t");
//	    	buf.append(row.getProperties().getStringValue("SymbolicName")).append("\t");
//	    	Property alias_ids = (Property) row.getProperties().get("AliasIds");
//	    	IdList idlist = alias_ids.getIdListValue();
//	    	if (idlist.isEmpty()) {
//	    		//buf.append("[empty]");
//	    		continue;
//	    	} else {
//	    		Iterator it = idlist.iterator();
//	    		buf.append("Aliases: \t");
//	    		while (it.hasNext()) {
//	    			buf.append(it.next().toString()).append("\t");
//	    		}
//	    	}
//	    	buf.append("\n");
//	    	getResponse().printOut(buf.toString());
	    	
	    	
//	    	Map propMap = new HashMap();
//	    	for (Iterator propIter = row.getProperties().iterator(); propIter.hasNext(); ) {
//	    		Property prop = (Property) propIter.next();
//	    		String name = prop.getPropertyName();
//	    		String value;
//	    		if ("ClassDescription".equals(name)) {
//	    			Id cid = prop.getIdValue();
//	    			value = fetchClassName(cid);
//	    		} else {
//	    			value = prop.getPropertyName().toString();
//	    		}
//	    		System.out.println("Debug: " + name + "\t" + value);
//	    		propMap.put(name, prop);
//	    	}	
//	    	String result = formatFemResults(propMap);
//	    	getResponse().printOut(result);
//	    	
//	    }
//		
//		
//	}
	
//	private void testChoiceListQuery() throws Exception {
//		
//	}
//	
	/**
	 * @throws FileNotFoundException 
	 * 
	 */
//	private void testRedirectOutput() throws FileNotFoundException {
////		PrintStream originalOut = null;
//		File outFile = new File("c:\\temp\\rotest.txt");
////		
////		try {
////			originalOut = redirectOutputStream(outFile);
////			getResponse().printOut("howdy from testRecirectOutput");
////		} finally {
////			restoreOutputStream(originalOut);
////		}
//		
//		this.getResponse().redirectOutputStream(outFile);
//		getResponse().printOut("redirected output");
//		this.getResponse().restoreOutputStream();
//		
//	}


//	/**
//	 * @param currentOutputStream
//	 */
//	private void restoreOutputStream(PrintStream originalOut) {
//		PrintStream currentOut = getResponse().getOut();
//		currentOut.close();
//		currentOut = null;
//		getResponse().setOut(originalOut);
//	}
//
//
//	/**
//	 * @param outFile 
//	 * @return
//	 * @throws FileNotFoundException 
//	 */
//	private PrintStream redirectOutputStream(File outFile) throws FileNotFoundException {
//		PrintStream beforeRedirect = this.getResponse().getOut();
//		PrintStream newOutput = new PrintStream(outFile);
//		this.getResponse().setOut(newOutput);
//		
//		return beforeRedirect;
//	}


	/**
	 * 
	 */
//	private void testCzdAddPropTmpl() {
//		String classDefSymbolicName = "Alpha";
//		String propTemplateId = "{7DC80088-8588-4B86-868B-CCCCC5BFEE65}";
//		// Construct property filter to ensure PropertyDefinitions property of CD is returned as evaluated
//		PropertyFilter pf = new PropertyFilter();
//		pf.addIncludeType(0, null, Boolean.TRUE, FilteredPropertyType.ANY, null); 
//
//		// Fetch selected class definition from the server
//		ClassDefinition objClassDef = com.filenet.api.core.Factory.ClassDefinition.fetchInstance(getShell().getObjectStore(), classDefSymbolicName, pf); 					
////		PropertyTemplateString objPropTemplate = Factory.PropertyTemplateString.getInstance(getShell().getObjectStore(), new Id(propTemplateId));
////		PropertyDefinitionString objPropDef = (PropertyDefinitionString)objPropTemplate.createClassProperty();       	
////		PropertyDefinitionList objPropDefs = objClassDef.get_PropertyDefinitions(); 
////		objPropDefs.add(objPropDef);
////		objClassDef.save(RefreshMode.REFRESH);	 
//		
//		PropertyTemplate objPropTemplate = Factory.PropertyTemplate.fetchInstance(getShell().getObjectStore(), new Id(propTemplateId), null);     						
//		PropertyDefinition objPropDef = objPropTemplate.createClassProperty();
//		PropertyDefinitionList objPropDefs = objClassDef.get_PropertyDefinitions(); 
//		objPropDefs.add(objPropDef);
//		objClassDef.save(RefreshMode.REFRESH);
//		
//	}



//	/**
//	 * 
//	 */
//	private void testPeRavind() {
//		getResponse().printOut("Hello, Ravind");
//		
//	}


	/**
	 * 
	 */
//	private void tstVerify() {
//		
//		String name = "6d3397897";
//		String badName = "foobar";
//		String badPass = "badpass";
//		String goodPass = "test131pass";
//		int depth = 0;
//		User user = null;
//		UserContext uc = null;
//		// TODO Auto-generated method stub
//		try {
//			CEConnectInfo connectInfo = this.getShell().
//					getConnectionMgr().
//					getConnectionStorageInfo(getShell().getConnectionAlias()).
//					getConnectionInfo();
//			Connection tstConn = Factory.Connection.getConnection(connectInfo.getConnectUrl());
//			Subject subject = UserContext.createSubject(
//					tstConn, 
//					badName, 
//					goodPass,
//					"FileNetP8WSI");
//			uc = UserContext.get();
//			
//			uc.pushSubject(subject);
//			depth++;
//			user = Factory.User.fetchCurrent(
//							tstConn, 
//							null);
//			
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		} finally {
//			if (depth > 0	) {
//				uc.popSubject();
//			}
//		}
//		if (user == null) {
//			getResponse().printOut("user is null");
//		} else {
//			getResponse().printOut("user valid: " + user.get_Name());
//		}
//		
//	}


//	/**
//	 * 
//	 */
//	private void doOsAddOns() {
//		Iterator iter = this.getShell().getObjectStore().get_AddOnInstallationRecords().iterator();
//		
//		while (iter.hasNext()) {
//			AddOnInstallationRecord aoir = (AddOnInstallationRecord) iter.next();
//			String name = aoir.get_AddOnName();
//			String status = aoir.get_InstallationStatus().toString();
//			getResponse().printOut(name + "\t" + status);
//		}
//		
//	}


	/**
	 * 
	 */
//	private void doDocClsStoragePolicy() {
//		
//		/**
//		 * When a document is created, the order of precedence for setting the storage on the Document instance is (from highest to lowest):
//		 * 
//	    * 	-- instance value for the StorageArea property
//	    * 	-- class default for the StorageArea property
//	    * 	-- instance value for the StoragePolicy property
//	    * 	-- class default for the StoragePolicy property
//		 */
//		com.filenet.api.admin.DocumentClassDefinition cd = 
//				(com.filenet.api.admin.DocumentClassDefinition) Factory.DocumentClassDefinition.fetchInstance(
//						getShell().getObjectStore(), "Document", null);
//	
////		Property storagePolicy = cd.getProperties().get("StoragePolicy");
////		String name = storagePolicy.getPropertyName();
////		for (Iterator iterator = cd.getProperties().iterator(); iterator.hasNext();) {
////			com.filenet.api.property.Property prop = (com.filenet.api.property.Property) iterator.next();
////			getResponse().printOut(prop.getPropertyName());
////		}
//		PropertyDefinitionList pdl = cd.get_PropertyDefinitions();
//		PropertyDefinitionObject sp = null;
//		for (Object obj : pdl) {
//			PropertyDefinition pd = (PropertyDefinition) obj;
//			if (pd.get_SymbolicName().equals("StoragePolicy")) {
//				sp = (PropertyDefinitionObject) pd;
//				break;
//			}
//		}
//		getResponse().printOut(sp.get_DataType().toString());
//		StoragePolicy storagePolicy = (StoragePolicy) sp.get_PropertyDefaultObject();
//		if (storagePolicy != null) {
//			getResponse().printOut(storagePolicy.get_Name());
//		}
//		
//	}


	/**
	 * 
	 */
//	private void doDocSecurityFolder() {
//		String id = "{81D80C22-6E68-4ED2-9E7A-A3F007E2A23A}";
//		String folderUri = "/TestFolder/secfoldertest/DE/0161";
//		Document doc = fetchDoc(id);
//		
//		Folder f = Factory.Folder.fetchInstance(getShell().getObjectStore(), folderUri, null);
////		Id folderId = f.get_Id();
//		doc.set_SecurityFolder(f);
//		doc.save(RefreshMode.REFRESH);
//	}


	
	
	
/**
//	 * 
//	 */
//	private void doX() {
//		String xx = String.format("" +
//				"" +
//				"" +
//				"" +
//				", args)
//		
//	}
//


/**
	 * 
	 */
//	private void doTypeIds() {
//		TypeID[] DATA_TYPE = new TypeID[] {
//			
//			TypeID.BINARY,		// 1
//			TypeID.BOOLEAN,		// 2
//			TypeID.DATE,		// 3
//			TypeID.DOUBLE,		// 4
//			TypeID.GUID,		// 5
//			TypeID.LONG,		// 6
//			TypeID.OBJECT,		// 7
//			TypeID.STRING		// 8
//		};
		
//		public static final String[] DATA_TYPES = new String[] {
//			"undef",	//	0
//			"Binary",	// 	1
//			"Boolean", 	//	2
//			"DateTime",		// 	3
//			"Double",	// 	4
//			"ID", 		// 	5  -- GUID
//			"Long",		// 	6
//			"Object",	// 	7
//			"String"	// 	8
//		};
		
//		for(int i = 1; i <= 8; i++) {
//			String dataTypeStr = TypeID.getInstanceFromInt(i).toString();
//			getResponse().printOut(i + "\t" + dataTypeStr);
//		}
//	}



//	/**
//	 * 
//	 */
//	private void doLog4jPoc() {
//		Logger logger = Logger.getLogger(FooCommand.class);
//		logger.debug("this is a debug message");
//		logger.info("info from foo");
//		logger.warn("warn from Foo");
//		
//	}



//	/**
//	 * 
//	 */
//	private void doTraceLogging() throws Exception {
//		Domain dom = this.getShell().getDomain(this.getShell().getCEConnection());
//		SubsystemConfigurationList scl = dom.get_SubsystemConfigurations();
//		Iterator iter = scl.iterator();
//		while (iter.hasNext()) {
//			EngineObject eo = (EngineObject) iter.next();
//			String className = eo.getClassName();
//			if(className.equals("TraceLoggingConfiguration")) {
//				TraceLoggingConfiguration tlc = (TraceLoggingConfiguration) eo;
//				displayTraceLoggingConfiguration(tlc);
//			}
//		}
//	}



//	/**
//	 * @param tlc
//	 */
//	private void displayTraceLoggingConfiguration(TraceLoggingConfiguration tlc) {
//		String[][] config = new String[][] {
//				{"appenders", tlc.get_AppenderNames()}
//				,{"api", tlc.get_APITraceFlags().toString()}
//				,{"async", tlc.get_AsynchronousProcessingTraceFlags().toString()}
//				,{"codeModule", tlc.get_CodeModuleTraceFlags().toString()}
//				,{"errorTrace", tlc.get_ErrorTraceFlags().toString()}
//				,{"engineTrace", tlc.get_EngineTraceFlags().toString()}	
//				,{"events", tlc.get_EventsTraceFlags().toString()}
//		};
//		
//		
//		
//		for (int i = 0; i < config.length; i++) {
//			String msg = config[i][0] + "\t" + config[i][1];
//			getResponse().printOut(msg);
//		}
//		
//	}



//	/**
//	 * 
//	 */
//	private void doTestEventHandler() {
//		
//	}



//	/**
//	 * 
//	 */
//	private void doEdit() throws Exception {
//		String docId = "{4CFEC9D2-82CD-4D8D-ACE8-45942AC921FA}";
//		String propFile = "c:\\temp\\pocprops.properties";
//		java.util.Properties props = null;
//		
//		props= new PropertyUtil().loadPropertiesFromFile(new File(propFile));
////		props = new java.util.Properties();
////		{
////			props.setProperty("SProp", "test string");
////			props.setProperty("BoolProp", "true");
////			props.setProperty("FProp", "10.05");
////			props.setProperty("IProp", "1000");
////		}
//		
//		Document doc = this.fetchDoc(docId);
//		doSetProps(doc, props);
//		doc.save(RefreshMode.REFRESH);
//	}
//	
//	/**
//	 * @param doc
//	 * @param props
//	 */
//	private void doSetProps(Document doc, java.util.Properties props) throws Exception {
//		Map<String, PropertyDescription> pdMap = new HashMap<String, PropertyDescription>();
//		Properties docProps = doc.getProperties();
//		ClassDescription cd = doc.get_ClassDescription();
//		PropertyDescriptionList pdl = cd.get_PropertyDescriptions();
//		
//		for (Iterator<?> iterator = pdl.iterator(); iterator.hasNext();) {
//			PropertyDescription pd = (PropertyDescription) iterator.next();
//			pd.get_DataType();
//			String pdName = pd.get_Name();
//			pdMap.put(pdName, pd);
//		}
//		for (Object propName : props.keySet()) {
//			String propValue = props.getProperty(propName.toString());
//			if (! pdMap.containsKey(propName))  {
//				throw new IllegalArgumentException("propert with name " + propName + " was not found.");
//			}
//			PropertyDescription pd = pdMap.get(propName);
//			if (pd.get_IsReadOnly()) {
//				throw new Exception(String.format("Property %s is read-only", propName));
//			}
//			applyProperty(docProps, pd, propName.toString(), propValue);
//		}	
//		doc.save(RefreshMode.REFRESH);
//	}

//	/**
//	 * @param pdMap
//	 * @param props
//	 */
//	private void applyProperty(
//			Properties props, 
//			PropertyDescription pd,
//			String propName, 
//			String propValue) throws Exception {
//		
//		TypeID typeId = pd.get_DataType();
//		
//		switch(typeId.getValue()) {
//			case TypeID.STRING_AS_INT:
//				props.putValue(propName, propValue);
//				break;
//			case TypeID.BOOLEAN_AS_INT:
//				Boolean bValue = parseBoolean(propValue);
//				props.putValue(propName, bValue);
//				break;
//			case TypeID.DATE_AS_INT:
//				Date dateValue = parseDate(propValue);
//				props.putValue(propName, dateValue);
//				break;
//			case TypeID.DOUBLE_AS_INT:
//				Double doubleValue = Double.parseDouble(propValue);
//				props.putValue(propName, doubleValue);
//				break;
//			case TypeID.GUID_AS_INT:
//				props.putValue(propName, propValue);
//				break;
//			case TypeID.LONG_AS_INT:
//				Integer intValue = Integer.parseInt(propValue);
//				props.putValue(propName, intValue);
//				break;
//			
//			default:
//				break;
////			case TypeID.BINARY_AS_INT:
////				break;	
////			case TypeID.OBJECT_AS_INT:
////				break;
//				
//		}
//		
//	}



//	/**
//	 * @param propValue
//	 * @return
//	 */
//	private Date parseDate(String propValue) throws Exception {
//		return StringUtil.parseDate(propValue);
//	}



//	/**
//	 * @param propValue
//	 * @return
//	 */
//	private Boolean parseBoolean(String propValue) {
//		char firstChar = propValue.trim().toLowerCase().charAt(0);
//		if ('t' == firstChar) {
//			return Boolean.TRUE;
//		}
//		return Boolean.FALSE;
//	}



//	/**
//	 * 
//	 */
//	private void debugDev2() {
//		ClassDescription cd = 
//			Factory.ClassDescription.fetchInstance(
//					null,new Id("B0D92FCB-25F2-4979-B66A-3527A63704B5"), null);
//		Iterator iter = cd.getProperties().iterator();
//		while (iter.hasNext()) {
//			Property prop = (Property) iter.next();
//			String name = prop.getPropertyName();
//			getResponse().printOut(name + "\t" + prop.toString());
//		}
//	}

//	private void testCdLs() {
//		Map<String, ClassDefinitionInfo> allClasses = 
//			new HashMap<String,	ClassDefinitionInfo>();
//		int startingLevel = 0;
//		fetchClassDefinitionInfo(startingLevel, allClasses);
//		getResponse().printOut("fetched " + allClasses.keySet().size() + 
//		" class definitions");
//		
//		for (Iterator<ClassDefinitionInfo> iterator = allClasses.values().iterator(); iterator.hasNext();) {
//			ClassDefinitionInfo cdInfo = iterator.next();
//			getResponse().printOut(cdInfo.getSymbolicName());
//			
//		}
//	}
//	
//	private void fetchClassDefinitionInfo(int startingLevel,
//			Map<String, ClassDefinitionInfo> allClasses) {
//		String query;
//		ClassDefinitionInfo rootInfo = null;
//		int pageSize = 400;
//		boolean continuable = true;
//		PropertyFilter myFilter = null; 
//		int maxRecursion = 2;
//		Long maxSize = null;
//		Boolean levelDependents = Boolean.FALSE;
//		String[] propsToQuery = new String[] {
//				PropertyNames.ID,
//				PropertyNames.SYMBOLIC_NAME,
//				PropertyNames.CREATOR,
//				PropertyNames.DATE_CREATED,
//				PropertyNames.IS_SYSTEM_OWNED,
//				PropertyNames.SUPERCLASS_DEFINITION,
//				PropertyNames.IS_HIDDEN,
//				PropertyNames.SUPERCLASS_DEFINITION
//		};
//		
//		String propNamesFilter = StringUtil.appendArrayToString(null, " ", propsToQuery);
//		myFilter = new PropertyFilter();
//		myFilter.addIncludeProperty(
//				maxRecursion,
//				null, 
//				levelDependents,
//				propNamesFilter);
//		
//		rootInfo = new ClassDefinitionInfo("", 
//				ClassDefinitionInfo.ROOT_NAME, startingLevel);
//		allClasses.put("root", rootInfo);
//		
//		query = "select Id, SymbolicName, Creator, DateCreated, IsSystemOwned, IsHidden, SuperclassDefinition from ClassDefinition";
//	
//		SearchSQL sqlObject = new SearchSQL();
//	    SearchScope searchScope = new SearchScope(ceShell.getObjectStore());
//	    IndependentObjectSet myObjects = null; 
//	    
//	    sqlObject.setQueryString(query);
//	    myObjects = searchScope.fetchObjects(sqlObject, pageSize, myFilter, continuable);
//	    
//	    for (Iterator iter = myObjects.iterator(); iter.hasNext();) {
//	    	IndependentObject nextObj = (IndependentObject) iter.next();
//	    	com.filenet.api.property.Properties props = 
//				nextObj.getProperties();
//	    	ClassDefinitionInfo nextDc = readItem(props);
//	    	allClasses.put(nextDc.getSymbolicName(), nextDc);
//	    }
//	    
//	    configureClassDefsWithParents(allClasses);
//	    
//	}
//	
//	private void configureClassDefsWithParents(
//			Map<String, ClassDefinitionInfo> allClasses) {
//		// put the info objects into their child sets 
//		for (Iterator<ClassDefinitionInfo> iter = allClasses.values().iterator(); 
//				iter.hasNext();) {
//			ClassDefinitionInfo next = null;
//			String parentName = null;
//	
//			next = iter.next();
//			parentName = next.getParentName();
//			if (parentName != null) {
//				ClassDefinitionInfo parent = allClasses.get(parentName); 
//				parent.addChild(next);
//			} 
//		}
//	}
//	

	/**
	 * 
	 */
//	private void propReport() {
//		// CDInfo {
//		//	-- name
//		//  -- ordered map:  propNames
//		//}
//		//
//		// create a set of class definition names
//		// create a set of prop definition name
//		//
//		// Print out name with props in left column and
//		// class defs in top row.
//		// Print out matrix on membership
//		
//		
//		//UsedInClasses
//		Boolean continuable = new Boolean(true);
//		SearchSQL sqlObject = new SearchSQL();
//		SearchScope search = new SearchScope(getShell().getObjectStore());
//	    Integer myPageSize = new Integer(300);
//	    PropertyFilter myFilter = new PropertyFilter();
//	    IndependentObjectSet myObjects = null;
//	    Iterator iter = null;
//	    int pos = 0;
//	    
//	    sqlObject.setSelectList("cd.SymbolicName, pt.Id, cd.UsedInClasses"); 
//	    sqlObject.setFromClauseInitialValue("PropertyTemplate", "pt", true);
//	    int myFilterLevel = 1;
//	    myFilter.setMaxRecursion(myFilterLevel);
//	    myFilter.addIncludeType(new FilterElement(null, null, null, FilteredPropertyType.ANY, null)); 
//	        
//	    // Execute the fetchObjects method using the specified parameters.
//	    myObjects = search.fetchObjects(sqlObject, myPageSize, myFilter, continuable);
//	    iter = myObjects.iterator();
//	    pos = 1;
//	    while (iter.hasNext()) {
//	    	IndependentObject obj = (IndependentObject) iter.next();
//	    	String symbolicName = obj.getProperties().get("SymbolicName").getStringValue();
//	    	String id = obj.getProperties().getIdValue("Id").toString();
//	    	IndependentObjectSet cdSet = obj.getProperties().getIndependentObjectSetValue("UsedInClasses");
//	    	String classNames = calcClassnames(cdSet);
//	    	getResponse().printOut(pos++ + "\t" + symbolicName + "\t" + classNames);
//	    }
//	}
//
//	/**
//	 * @param cdSet
//	 * @return
//	 */
//	private String calcClassnames(IndependentObjectSet cdSet) {
//		StringBuffer buf = new StringBuffer();
//		Iterator iter = cdSet.iterator();
//		while (iter.hasNext()) {
//			IndependentObject obj = (IndependentObject) iter.next();
//			String id = obj.getProperties().getIdValue("Id").toString();
//			String name = obj.getProperties().getStringValue("symbolicName");
//			buf.append(name).append("\t");
//		}
//		return buf.toString();
//	}

//	private ClassDefinitionInfo readItem(
//			com.filenet.api.property.Properties props) {
//		String cId, cName, creator = null;
//		String pName = "root";
//		Date createDate;
//		Boolean isSystemOwned;
//		Boolean isHidden;
//		ClassDefinitionInfo docclassInfo = new ClassDefinitionInfo();
//		IndependentObject parent = (IndependentObject) props.getObjectValue("SuperclassDefinition");
//		if (parent != null) {
//			pName = parent.getProperties().getStringValue(PropertyNames.SYMBOLIC_NAME);
//		}
//		cId = props.getIdValue(PropertyNames.ID).toString();
//		cName = props.getStringValue(PropertyNames.SYMBOLIC_NAME);
//		creator = props.getStringValue(PropertyNames.CREATOR);
//		
//		docclassInfo.setId(cId);
//		docclassInfo.setSymbolicName(cName);
//		docclassInfo.setParentName(pName);
//		docclassInfo.setCreator((creator == null) ? "" : creator);
//		
//		isSystemOwned = props.getBooleanValue(PropertyNames.IS_SYSTEM_OWNED);
//		docclassInfo.setSystemOwned(isSystemOwned);
//		
//		isHidden = props.getBooleanValue(PropertyNames.IS_HIDDEN);
//		docclassInfo.setHidden(isHidden);
//		
//		createDate = props.getDateTimeValue("DateCreated");
//		docclassInfo.setDateCreated(createDate);
//		
//		return docclassInfo;
//	}
//
//	
	
//	/**
//	 * 
//	 */
////	private void fetchWorkClassNames() throws Exception {
//		String[] names = getShell().getPEConnection()
//			.fetchWorkClassNames(true,null);
//		getResponse().printOut(
//				StringUtil.appendArrayToString("", "\n", names));
//	}
//
//	public void listQueues() throws Exception {
//		VWSession session = getShell().getPEConnection();
//		int allFlags = (VWSession.QUEUE_PROCESS | VWSession.QUEUE_SYSTEM | VWSession.QUEUE_IGNORE_SECURITY);
//		int sysFlgs = (VWSession.QUEUE_SYSTEM | VWSession.QUEUE_IGNORE_SECURITY);
//		int procFlags = (VWSession.QUEUE_PROCESS | VWSession.QUEUE_IGNORE_SECURITY);
//
//		String[] queues = null;
//		
//
//		queues = session.fetchQueueNames(procFlags);
//		Arrays.sort(queues);
//		
//		System.out.println("Work queues:");
//		for (int i = 0; i < queues.length; i++) {
//			System.out.println("\t" + queues[i]);
//		}
//		
//		System.out.println("-----------------------------");
//		System.out.println("System queues");
//		queues = session.fetchQueueNames(sysFlgs);
//		Arrays.sort(queues);
//		for (int i = 0; i < queues.length; i++) {
//			System.out.println("\t" + queues[i]);
//		}
//	}
//
//	/**
//	 * 
//	 */
//	private void testClear() {
//		getResponse().printOut((char)27 + "[2J");
//		List x = new ArrayList<String>();
//		x.add("foo");
//		x.add("bar");
//		x.add("gogo");
//		
//		String g = (String) x.get(1);
//		int size = x.size();
//		
//	}
//
//	/**
//	 * 
//	 */
//	private void testDocClassDefs() {
//		com.filenet.api.admin.DocumentClassDefinition dcd = Factory.DocumentClassDefinition.fetchInstance(getShell().getObjectStore(), "Document", null);
//		System.out.println(dcd.get_Name());
//		listDcdChildren(dcd, 1);
//
//		
//	}
//	
//	void listDcdChildren(com.filenet.api.admin.DocumentClassDefinition parent,
//			int level) {
//		ClassDefinitionSet cds = parent.get_ImmediateSubclassDefinitions();
//		String prefix = createPrefix(level);
//		for (Iterator iter = cds.iterator(); iter.hasNext(); ) {
//			com.filenet.api.admin.DocumentClassDefinition child = 
//				(com.filenet.api.admin.DocumentClassDefinition) iter.next();
//			String childName = child.get_Name();
//			System.out.println(prefix + childName);
//			listDcdChildren(child, level + 1);
//		}
//	}
// 
//	/**
//	 * @param level
//	 * @return
//	 */
//	private String createPrefix(int level) {
//		StringBuffer buf = new StringBuffer();
//		for (int i = 0; i < level; i++) {
//			buf.append(".....");
//		}
//		return buf.toString();
//	}
//
//	/**
//	 * 
//	 */
//	private void testDateQry() {
//		
//		
//	}
//
//	/**
//	 * 
//	 */
//	private void testBatchUpdate() throws Exception {
//		String[] btsIds = {
//				"{EC96FBEA-D8FA-4EDF-BF50-F137439F2CBA}", 
//				"{D4F569EE-8228-45BC-8674-20A97DB27C47}", 
//				"{127855EF-B654-4604-B643-A01CF2806021}", 
//				"{FA2C68EF-7385-4BB7-88F0-0B5FC17A50D5}", 
//				"{C91592F1-9592-4E2E-9E02-F1D06CC66B0F}", 
//				"{239E64F7-F1B4-43E8-B866-1E689200127E}", 
//				"{6AE7D1FC-07D5-44C4-8CEB-CAAE3789B633}", 
//				};	
//		UpdatingBatch ub = UpdatingBatch.createUpdatingBatchInstance(
//				getShell().getDomain(getShell().getCEConnection()), 
//				RefreshMode.NO_REFRESH);
//		
//		for(String id : btsIds) {
//			Document doc = Factory.Document.getInstance(
//				getShell().getObjectStore(), "BHDC_Billing", new Id(id));
//			doc.getProperties().putValue("DocumentTitle", "batchupdate");
//			ub.add(doc, null);
//		}
////		Document doc1 = 
////	    Document doc2 = Factory.Document.fetchInstance(os, new Id("{35026B90-B443-40CA-B5C3-66BEAD13E2B7}"), null);
//
////	    // First update to be included in batch.
////	    doc1 = (Document) doc1.get_Reservation();
////	    doc1.checkin(null, CheckinType.MAJOR_VERSION);
////	    
////	    // Second update to be included in batch.
////	    doc2 = (Document) doc2.get_Reservation();
////	    doc2.checkin(null, CheckinType.MAJOR_VERSION); 
//
//	    // Third update to be included in batch. Sets the document title and assigns the 
//	    // specified property values (Properties.putValue) to the retrieved properties for the 
//	    // doc (the inherited EngineObject.getProperties). 
////	    doc1.getProperties().putValue("DocumentTitle", "doc1"); 
//	    
//	    // Fourth update to be included in batch.
////	    doc2.getProperties().putValue("DocumentTitle", "doc2"); 
//
//	    // Adds all four updates (to the two Document objects) to the UpdatingBatch instance. 
////	    ub.add(doc1, null);  
////	    ub.add(doc2, null);  
//
//	    // Execute the batch update operation.
//	    ub.updateBatch();
//
//	}
//
//	/**
//	 * 
//	 */
//	private void testCompareAclLevels() {
//		
//		
//	}
//
//	/**
//	 * 
//	 */
//	private void testDcls() {
//		// Document class ls -l properties
////		String query = "select d.Id, d.ClassDescription, d.IsReserved, r.ContainmentName, d.ContentSize,  d.DateCreated, d.Creator, d.MajorVersionNumber, d.MinorVersionNumber,d.VersionStatus FROM Document d INNER JOIN ReferentialContainmentRelationship r ON d.This = r.Head  WHERE r.Tail = OBJECT(\'" + fullPath + "\') order by r.ContainmentName asc";
//		 String query = "select c.name, d.name from DocumentClassDefinition c where c.name = \'Document\'";
//		SearchSQL sqlObject = new SearchSQL();
//	    sqlObject.setQueryString(query);
//	    SearchScope searchScope = new SearchScope(ceShell.getObjectStore());
//	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
//	    
//		
//	}
//
//	/**
//	 * 
//	 */
//	private void testFetchUser() throws Exception {
//		User user = Factory.User.fetchInstance(getShell().getCEConnection(), 
//				"alice",
//				null);
//		listUserProps(user, true, true);
//	}
//
//	/**
//	 * 
//	 */
//
//	private void testGroups() throws Exception {
//		Group group = Factory.Group.fetchInstance(getShell().getCEConnection(), "peadmingrp", null);
//		showGroup(group);
//	}
//	
//	/**
//	 * @param group
//	 */
//	private void showGroup(Group group) {
//		String[] labels = new String[] {
//				"Id", "Name", "DistinguishedName", "DisplayName", "ShortName"
//		};
//		String id = group.get_Id();
//		String name = group.get_Name();
//		String dn = group.get_DistinguishedName();
//		String displayName = group.get_DisplayName();
//		String shortName = group.get_ShortName();
//		String[] values = new String[] {
//				id, name, dn, displayName, shortName
//		};
//		for(int i = 0; i < labels.length; i++) {
//			String strValue;
//			if (values[i] == null) {
//				strValue = "<null>";
//			} else {
//				strValue = values[i].toString();
//			}
//			getResponse().printOut(StringUtil.padLeft(labels[i], ".", 20) + strValue);
//		}
//		listGroupUsers(group);
//	}
//
//	/**
//	 * @param group
//	 */
//	private void listGroupUsers(Group group) {
//		UserSet users = group.get_Users();
//		Iterator iter = users.iterator();
//		getResponse().printOut("Users\n----------------------------------------------");
//		while (iter.hasNext()) {
//			User user = (User) iter.next();
//			String name = user.get_Name();
//			System.out.println("\t" + name);
//		}
//	}
//
//	/**
//	 * 
//	 */
//	private void testOsls() {
//		ObjectStore os = this.getShell().getObjectStore();
//		os.get_Permissions();
////		Factory.ObjectStore.fetchInstance(arg0, arg1, arg2)
//		
//	}
//
//	/**
//	 * 
//	 */
//	private void testUser() throws Exception {
//		PropertyFilter userFilter = new PropertyFilter();
//		FilterElement fe = new FilterElement(null,
//				null,
//				Boolean.TRUE, // levelDependents
//				FilteredPropertyType.SINGLETON_STRING, //types
//				new Integer(100));  // page size
//		userFilter.addIncludeType(fe);
//		User user = Factory.User.fetchCurrent(getShell().getCEConnection(), userFilter);
//		listUserProps(user, true, true);
//	}
//
//	private void listUserProps(User user, boolean listUserProps, boolean listGroups) 
//			throws Exception {
//		if (listUserProps) {
//			String userId = user.get_Id();
//			String userMame = user.get_Name();
//			String userDisplayName = user.get_DisplayName();
//			String userDn = user.get_DistinguishedName();
//			String userShortName = user.get_ShortName();
//			String email = user.get_Email();
//			Properties props = user.getProperties();
//			Iterator iter = props.iterator();
//			while (iter.hasNext()) {
//				Property nextProp = (Property) iter.next();
//				String name = nextProp.getPropertyName();
//				String strValue = null;
//				Object value = nextProp.getObjectValue();
//				if (value != null) {
//					strValue = value.toString();
//				} else {
//					strValue = "<null>";
//				}
//				System.out
//						.println(StringUtil.padLeft(name, ".", 25) + strValue);
//			}
//		}
//		if (listGroups) {
//			GroupSet groups = user.get_MemberOfGroups();
//			System.out.println("member of Groups:--------------------------");
//			Property groupProp = user.fetchProperty("MemberOfGroups",
//					createGroupPropertyFilter());
//			IndependentObjectSet groupSet = groupProp
//					.getIndependentObjectSetValue();
//			PageIterator groupIter = groupSet.pageIterator();
//			groupIter.setPageSize(50);
//			listGroups(groupIter);
//		}
//	}
//	
//	/**
//	 * @param groupIter
//	 */
//	private void listGroups(PageIterator pageIter) {
//		// Cycle through pages
//	    int pageCount = 0;
//	    while (pageIter.nextPage() == true) {
//	        // Get counts
//	        pageCount++;
//	        int elementCount = pageIter.getElementCount();
//
//	        // Display number of objects on this page
//	        System.out.println("Page: " 
//	            + pageCount + "  Element count: " + elementCount);
//
//	        // Get elements on page
//	        Object[] pageObjects = pageIter.getCurrentPage();                
//	        for (int index = 0; index < pageObjects.length; index++) {
//	            // Get sub object
//	            Object elementObject = pageObjects[index];
//	            Group group = (Group) elementObject;
//	            System.out.println(group.get_DisplayName());
//	        }
//	    }
//		
//	}
//
//	private PropertyFilter createGroupPropertyFilter() {
//		PropertyFilter filter = new PropertyFilter();
//		filter.addIncludeProperty(
//				new FilterElement(null, null, null, "DisplayName, Name", null));
//		return filter;
//	}
//
//	/**
//	 * 
//	 */
//	private void testAddAcl() {
//		
//	}
//
//	/**
//	 * 
//	 */
//	private void testShowDocAcls() {
//		PropertyFilter docSecurityFilter = createDocSecurityFilter();
//		
//		Document doc = Factory.Document.fetchInstance(
//				getShell().getObjectStore(), "/glr-test/testglr.txt", docSecurityFilter);
//		AccessPermissionList apl = doc.get_Permissions();
//		Iterator iter = apl.iterator();
//		while (iter.hasNext()) {
//			com.filenet.api.security.AccessPermission ap =  
//				(com.filenet.api.security.AccessPermission) iter.next();
//			Integer mask = ap.get_AccessMask();
//			AccessType accessType = ap.get_AccessType();
//			String grantee = ap.get_GranteeName();
//			PermissionSource src = ap.get_PermissionSource();
//			Integer inheritableDepth = ap.get_InheritableDepth();
//			
//			System.out.println("Grantee\t: " + grantee);
//			System.out.println("\tsource:\t" + src.toString());
//			System.out.println("\tlevel:\t" + mask);
//			int id = inheritableDepth.intValue();
//			String idDesc;
//			if (id == 0) {
//				idDesc = "This object only";
//			} else if (id == -1) {
//				idDesc = "This object and all children";
//			} else {
//				idDesc = "This object and immediate children only";
//			}
//			System.out.println("\tApply to:\t" + idDesc);
//			
//		}
//	}
//
//	/**
//	 * @return
//	 */
//	private PropertyFilter createDocSecurityFilter() {
//		PropertyFilter filter = new PropertyFilter();
//		
//		String[] names = new String[] {
//				"Permissions",
//				"AccessType",
//				"AccessMask",
//				"GranteeName",
//				"DisplayName",
//				"DescriptiveText",
//				"InheritableDepth",
//				"PermissionSource",
//				"PermissionType"
//		};
//		for (int i = 0; i < names.length; i++) {
//			FilterElement fe = new FilterElement(new Integer(1), 
//					null, null, names[i], null);
//	
//			filter.addIncludeProperty(
//					fe);
//		}
//		return filter;
//	}
//
//	/**
//	 * 
//	 */
//	private void testDocClassDefaultSec() {
//		PropertyFilter defaultSecurityFilter = createDefaultSecurityFilter();
//		com.filenet.api.admin.DocumentClassDefinition dcd = Factory.DocumentClassDefinition.fetchInstance(getShell().getObjectStore(), 
//				"glrdcb", defaultSecurityFilter);
//		AccessPermissionList apl = dcd.get_DefaultInstancePermissions();
//		Iterator iter = apl.iterator();
//		while (iter.hasNext()) {
//			com.filenet.api.security.AccessPermission ap =  
//				(com.filenet.api.security.AccessPermission) iter.next();
//			Integer mask = ap.get_AccessMask();
//			AccessType accessType = ap.get_AccessType();
//			String grantee = ap.get_GranteeName();
//			PermissionSource src = ap.get_PermissionSource();
//			Integer inheritableDepth = ap.get_InheritableDepth();
//			
//			System.out.println("Grantee\t: " + grantee);
//			System.out.println("\tsource:\t" + src.toString());
//			System.out.println("\tlevel:\t" + mask);
//			int id = inheritableDepth.intValue();
//			String idDesc;
//			if (id == 0) {
//				idDesc = "This object only";
//			} else if (id == -1) {
//				idDesc = "This object and all children";
//			} else {
//				idDesc = "This object and immediate children only";
//			}
//			System.out.println("\tApply to:\t" + idDesc);
//			
//		}
//
//	}
//
//	/**
//	 * @return
//	 */
//	private PropertyFilter createDefaultSecurityFilter() {
//		PropertyFilter filter = new PropertyFilter();
//		
//		String[] names = new String[] {
//				"DefaultInstancePermissions",
//				"AccessType",
//				"AccessMask",
//				"GranteeName",
//				"DisplayName",
//				"DescriptiveText",
//				"InheritableDepth",
//				"PermissionSource",
//				"PermissionType"
//		};
//		for (int i = 0; i < names.length; i++) {
//			FilterElement fe = new FilterElement(new Integer(1), 
//					null, null, names[i], null);
//	
//			filter.addIncludeProperty(
//					fe);
//		}
//		return filter;
//	}
//
//	/**
//	 * 
//	 */
//	private void testDocPropsById() {
//		String id = "{97BD340E-6E8F-4BD8-9907-707F556FD41E} ";
//		Document doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
//				new Id(id), null);
//		Iterator iter = doc.getProperties().iterator();
//		while (iter.hasNext()){
//			Property nextProp = (Property) iter.next();
//			String name = nextProp.getPropertyName();
//			Object value = nextProp.getObjectValue();
//			String strValue = (value == null) ? "" : value.toString();
//			System.out.println(name + ", " + strValue);
//
//		}
//	}
//
//
//
//
//	/**
//	 * 
//	 */
//	@SuppressWarnings("deprecation")
//	private void testSpacePath() {
//		getResponse().printOut("There are " + getRequest().getArgs().length + " args.");
//		Folder currentFolder;
//		String encodedPath = getRequest().getArgs()[0];
//		String decodedPath = java.net.URLDecoder.decode(encodedPath);
//		getResponse().printOut("Encoded: " + encodedPath);
//		getResponse().printOut("Decoded: " + decodedPath);
//		currentFolder = ceShell.getFolder(decodedPath);
//		FolderSet subFolders = currentFolder.get_SubFolders();
//		ReferentialContainmentRelationshipSet children = 
//			currentFolder.get_Containees();
//		
//		for (Iterator iter = subFolders.iterator(); iter.hasNext();) {
//			Folder child = (Folder) iter.next();
//			getRequest().getResponse().printOut(child.get_FolderName() + "/");
//		}
//		
//	}
//
//
//	/**
//	 * 
//	 */
//	private void testLs() {
//		// class description DisplayName
//		String query = "select  d.Id, d.ClassDescription FROM Document d INNER JOIN ReferentialContainmentRelationship r ON d.This = r.Head  WHERE r.Tail = OBJECT(\'/glr-test\')";
//		SearchSQL sqlObject = new SearchSQL();
//	    sqlObject.setQueryString(query);
//	    SearchScope searchScope = new SearchScope(ceShell.getObjectStore());
//	   
//	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
//	    for (Iterator iter = rowSet.iterator(); iter.hasNext();) {
//	    	RepositoryRow row = (RepositoryRow) iter.next();
//	    	Map propMap = new HashMap();
//	    	for (Iterator propIter = row.getProperties().iterator(); propIter.hasNext(); ) {
//	    		Property prop = (Property) propIter.next();
//	    		String name = prop.getPropertyName();
//	    		String value;
//	    		if ("ClassDescription".equals(name)) {
//	    			Id cid = prop.getIdValue();
//	    			value = fetchClassName(cid);
//	    		} else {
//	    			value = prop.getPropertyName().toString();
//	    		}
//	    		System.out.println("Debug: " + name + "\t" + value);
//	    		propMap.put(name, prop);
//	    	}	
////	    	String result = formatFemResults(propMap);
////	    	getResponse().printOut(result);
//	    	
//	    }
//		
//	}
//	
//	/**
//	 * @param value
//	 * @return
//	 */
//	private String fetchClassName(Id value) {
//		String className = null;
//		 PropertyFilter pf = new PropertyFilter();
//		    {
//		    	FilterElement fe = new FilterElement(new Integer(1), null, null, "DisplayName", null);
//		    	pf.addIncludeProperty(fe);
//		    }
//		ClassDescription cd = Factory.ClassDescription.fetchInstance(this.getShell().getObjectStore(), 
//				value, pf);
//		className = cd.get_DisplayName();
//		return className;
//	}
//
//	private String formatFemResults(Map propMap) {
//		StringBuffer buf = new StringBuffer();
////		Property cd = (Property) propMap.get("ClassDescription");
////		
////		buf.append("cd." + cd.getPropertyName() + ", " + cd.getObjectValue().toString());
//		return buf.toString();
//	}
//
//
//
//
//	private void testProperties() {
//		Map propTypeMap = createPropTypeMap();
//		String path = "/glr-test/testglr.txt";
//		PropertyFilter filter = null;
//		Document d = Factory.Document.fetchInstance(getShell().getObjectStore(), 
//				path, filter);
//		Iterator iter = d.getProperties().iterator();
//		
//		while (iter.hasNext()) {
//			Property prop = (Property) iter.next();
//			
//			String name = prop.getPropertyName();
//			Class c = prop.getClass();
//			String className = c.getCanonicalName();
//			String cShortName = className.substring(className.lastIndexOf(".") + 1);
//			Boolean inout = (Boolean) propTypeMap.get(cShortName);
//			if (Boolean.TRUE.equals(inout)) {
//				Object value = prop.getObjectValue();
//				System.out.println(StringUtil.padRight(name, ".", 40) + 
//						
//						//cShortName + "\t" + 
//						(value == null ? "null" : value.toString()));
//			} else {
//				System.out.println(StringUtil.padRight(name, ".", 40) + 
//						"<" + cShortName + ">");
//			}
//			
//		}
//	}
//
//	private Map createPropTypeMap() {
//		
//		
//		Map propTypes = new HashMap();
//		
//		propTypes.put("PropertyBinaryImpl", Boolean.FALSE);
//		propTypes.put("PropertyBinaryListImpl", Boolean.TRUE);
//		propTypes.put("PropertyBooleanImpl", Boolean.FALSE);
//		propTypes.put("PropertyBooleanListImpl",Boolean.TRUE);
//		propTypes.put("PropertyContentImpl", Boolean.TRUE);
//		propTypes.put("PropertyDateTimeImpl", Boolean.TRUE);
//		propTypes.put("PropertyDateTimeListImpl", Boolean.TRUE);
//		propTypes.put("PropertyDependentObjectListImpl", Boolean.FALSE);
//		propTypes.put("PropertyEngineObjectImpl", Boolean.FALSE);
//		propTypes.put("PropertyFloat64Impl", Boolean.TRUE);
//		propTypes.put("PropertyFloat64ListImpl", Boolean.TRUE);
//		propTypes.put("PropertyIdImpl", Boolean.TRUE);
//		propTypes.put("PropertyIdListImpl", Boolean.TRUE);
//		propTypes.put("PropertyIndependentObjectSetImpl", Boolean.FALSE);
//		propTypes.put("PropertyInteger32Impl", Boolean.TRUE);
//		propTypes.put("PropertyInteger32ListImpl", Boolean.TRUE);
//		propTypes.put("PropertyStringImpl", Boolean.TRUE);
//		propTypes.put("PropertyStringListImpl", Boolean.TRUE);
//		
//		return propTypes;
//	}
//
//
//	private void testAnd() {
//		int test = 0;
//		for (test = 0; test < 16; test++) {
//			boolean readAsInt = ((AccessRight.READ_AS_INT & test) == AccessRight.READ_AS_INT);
//			System.out.println(test + ", " +  Integer.toBinaryString(test) + ",  " + readAsInt);
//		}
//		
//	}
//
//
//	private void dumpAccessRights() {
//		System.out.println(describe("ADD_MARKING_AS_INT"      , AccessRight.ADD_MARKING_AS_INT));
//		System.out.println(describe("CHANGE_STATE_AS_INT"     , AccessRight.CHANGE_STATE_AS_INT));
//		System.out.println(describe("CONNECT_AS_INT"          , AccessRight.CONNECT_AS_INT));
//		System.out.println(describe("CREATE_CHILD_AS_INT"     , AccessRight.CREATE_CHILD_AS_INT));
//		System.out.println(describe("CREATE_INSTANCE_AS_INT"  , AccessRight.CREATE_INSTANCE_AS_INT));
//		System.out.println(describe("DELETE_AS_INT"           , AccessRight.DELETE_AS_INT));
//		System.out.println(describe("LINK_AS_INT"             , AccessRight.LINK_AS_INT));
//		System.out.println(describe("MAJOR_VERSION_AS_INT"    , AccessRight.MAJOR_VERSION_AS_INT));
//		System.out.println(describe("MINOR_VERSION_AS_INT"    , AccessRight.MINOR_VERSION_AS_INT));
//		System.out.println(describe("MODIFY_OBJECTS_AS_INT"   , AccessRight.MODIFY_OBJECTS_AS_INT));
//		System.out.println(describe("NONE_AS_INT"             , AccessRight.NONE_AS_INT));
//		System.out.println(describe("PRIVILEGED_WRITE_AS_INT" , AccessRight.PRIVILEGED_WRITE_AS_INT));
//		System.out.println(describe("PUBLISH_AS_INT"          , AccessRight.PUBLISH_AS_INT));
//		System.out.println(describe("READ_ACL_AS_INT"         , AccessRight.READ_ACL_AS_INT));
//		System.out.println(describe("READ_AS_INT"             , AccessRight.READ_AS_INT));
//		System.out.println(describe("REMOVE_MARKING_AS_INT"   , AccessRight.REMOVE_MARKING_AS_INT));
//		System.out.println(describe("REMOVE_OBJECTS_AS_INT"   , AccessRight.REMOVE_OBJECTS_AS_INT));
//		System.out.println(describe("RESERVED12_AS_INT"       , AccessRight.RESERVED12_AS_INT));
//		System.out.println(describe("RESERVED13_AS_INT"       , AccessRight.RESERVED13_AS_INT));
//		System.out.println(describe("STORE_OBJECTS_AS_INT"    , AccessRight.STORE_OBJECTS_AS_INT));
//		System.out.println(describe("UNLINK_AS_INT"           , AccessRight.UNLINK_AS_INT));
//		System.out.println(describe("USE_MARKING_AS_INT"      , AccessRight.USE_MARKING_AS_INT));
//		System.out.println(describe("VIEW_CONTENT_AS_INT"     , AccessRight.VIEW_CONTENT_AS_INT));
//		System.out.println(describe("WRITE_ACL_AS_INT"        , AccessRight.WRITE_ACL_AS_INT));
//		System.out.println(describe("WRITE_ANY_OWNER_AS_INT"  , AccessRight.WRITE_ANY_OWNER_AS_INT));
//		System.out.println(describe("WRITE_AS_INT"            , AccessRight.WRITE_AS_INT));
//		System.out.println(describe("WRITE_OWNER_AS_INT"      , AccessRight.WRITE_OWNER_AS_INT));
//
//	}
//	
//	protected String describe(String accessRight, Integer accesssValue) {
//		String bin = Integer.toBinaryString(accesssValue.intValue());
//		String msg = accessRight + "\t" + accesssValue.toString() + "\t" + bin;
//		return msg;
//		
//	}
//
//	protected void describePermissions(String typeToDescribe) throws Exception {
//			
//		ClassDefinition cd = Factory.ClassDefinition.fetchInstance(
//				this.getShell().getObjectStore(), typeToDescribe, null);
//		
//		
//		this.getResponse().printOut("\n-----------------------------\nPermissions");
//		AccessPermissionDescriptionList apdl = 
//				cd.get_DefaultInstancePermissionDescriptions();
//		
//		AccessPermissionList apl = cd.get_Permissions();
//		Iterator permissionIter = apl.iterator();
//		
//			
//		for (;	permissionIter.hasNext();) {
//			AccessPermission permission = 
//				(AccessPermission) permissionIter.next();
//			Integer accessMask = permission.get_AccessMask();
//			AccessType accessType = permission.get_AccessType();
//			String name = permission.get_GranteeName();
//			SecurityPrincipalType spType = permission.get_GranteeType();
//			Integer depth = permission.get_InheritableDepth();
//			String depthStr = getDepthDescription(depth);
//			PermissionSource source = permission.get_PermissionSource();
//			
//			{
//				StringBuffer buff = new StringBuffer();
//				buff.append(name).append("\n");
//				buff.append("\tSource:\t").append(source.toString()).append("\n");
//				buff.append("\tApply To:\t").append(depthStr).append("\n");
//				buff.append("\taccessType:\t").append(accessType.toString()).append("\n");
//				buff.append("\tType:\t").append(spType.toString()).append("\n");
//				
//				buff.append("\tMask:\t").append(accessMask).append("\n");
//				
//				getResponse().printOut(name + ":\t" + buff.toString());
//			}
//		}
//			
//	}
//
//	private String getDepthDescription(Integer depth) {
//		int d = depth.intValue();
//		String depthStr = "unknown";
//		if (D_ALL_CHILDREN.equals(d)) {
//			depthStr = ALL_CHILDREN;
//		} else if(D_CHILDREN.equals(d)) {
//			depthStr = CHILDREN;
//		} else if(D_NO_INHERITANCE.equals(d)) {
//			depthStr = NO_INHERITANCE;
//		}
//		return depthStr;
//	}
//
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		StringParam fooArgs = new StringParam(FOO_ARG,"any params",StringParam.OPTIONAL);
		fooArgs.setMultiValued(true);
		CmdLineHandler cl = new VersionCmdLineHandler(VERSION, 
				new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] {fooArgs }));
		cl.setDieOnParseError(false);

		return cl;
	}

}
