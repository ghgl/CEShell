package com.ibm.bao.ceshell;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.security.User;
import com.filenet.api.util.UserContext;
import com.ibm.bao.ceshell.connection.CECryptoManager;
import com.ibm.bao.ceshell.connection.ConnectionManager;
import com.ibm.bao.ceshell.connection.ConnectionStorageInfo;
import com.ibm.bao.ceshell.impl.EditInfo;
import com.ibm.bao.ceshell.pe.PEConnectionSvc;
import com.ibm.bao.ceshell.util.PropertyUtil;

import filenet.vw.api.VWSession;
public class CEShell {
	
	public static final String
	MODE_CMD = "MODE_CMD",
	MODE_EDIT = "MODE_EDIT",
	MODE_VIEW = "MODE_VIEW";
	
	private String mode;
	private ConnectionManager connectionMgr;
	private CEConnectInfo connectInfo = new CEConnectInfo();
	private PEConnectionSvc peConn;
	private Connection ceConnection;
	private BaseResponse response;
	private CWD cwd;
	private List<BaseCommand> historyList = new ArrayList<BaseCommand>();
	private Env env;
	private int credentialsDepth = 0;
	private EditInfo currentEditInfo = null;
	
	private HashMap<String,String> commandMap = new HashMap<String, String>();
	
	public CEShell() {
		
	}
	
	/**
	 * Copy Ctor:
	 * Create a new history list
	 * Clone the other k
	 * @param parentShell
	 * TODO:  Should probably clone objects from parent to prevent side-effects
	 */
	public CEShell(CEShell parentShell) {
		this();
		this.mode = MODE_CMD;											// set to edit
		this.connectionMgr = parentShell.connectionMgr;					// reference to parent
		this.connectInfo = new CEConnectInfo(parentShell.connectInfo);	// clone
		this.ceConnection = null;										// reference to parent
		this.response = parentShell.response;							// reference to parent
		this.env = parentShell.env;										// reference to parent
		this.commandMap = parentShell.commandMap;						// reference to parent
		
		peConn = new PEConnectionSvc();
		peConn.setCryptoManager(this.getConnectionMgr().getCECryptoManager());
	}
	
	public Env getEnv() {
		return env;
	}

	public void setEnv(Env env) {
		this.env = env;
	}

	public EditInfo getCurrentEditInfo() {
		return currentEditInfo;
	}

	public void setCurrentEditInfo(EditInfo currentEditInfo) {
		this.currentEditInfo = currentEditInfo;
	}

	public List<BaseCommand> getHistoryList() {
		return historyList;
	}
	
	public BaseCommand getLastCommand() {
		return historyList.get(historyList.size() - 1);
	}
	
	public ConnectionManager getConnectionMgr() {
		return connectionMgr;
	}
	

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public boolean isEditMode() {
		return (MODE_EDIT.equals(this.mode));
	}

	public BaseResponse getResponse() {
		return response;
	}

	public void setResponse(BaseResponse response) {
		this.response = response;
	}

	public void connect(String connectionAlias) throws Exception {
		CEConnectInfo connectInfo = null;
		ConnectionStorageInfo alias = connectionMgr.getConnectionStorageInfo(connectionAlias);
		if (alias == null) {
			throw new IllegalArgumentException("Alias " + 
					connectionAlias + "does not exist");
		}
		connectInfo = connectionMgr.getConnectionStorageInfo(connectionAlias).getConnectionInfo();
		this.connect(connectInfo);
	}

	public void connect(CEConnectInfo connectInfo) throws Exception {
		this.connectInfo = connectInfo;
		ceConnection = getCEConnection();
		peConn.connect(connectInfo);
		initCwd();
	}
	
	public String getConnectionAlias() {
		if (connectInfo == null) {
			return "unknown";
		}
		String alias = connectInfo.getAlias();
		if (alias == null ||
				alias.equals("")) {
			alias = "unknown";
		}
		return alias;
	}
	
	public String getCurrentUser() {
		return this.connectInfo.getUser();
	}
	
	public void changeUserCredentials(String user, String pass) throws Exception {
		java.util.Properties sudoList = getSudoList();
		if (pass == null) {
			if (sudoList.containsKey(user)) {
				pass = sudoList.getProperty(user).trim();
			}
		}
		if (pass == null) {
			throw new IllegalArgumentException("Password not found for user " + user);
		}
		if (! verifyConnection(user, pass, this.connectionMgr.getCECryptoManager())) {
			getResponse().printErr("Problem with user credentials");
			return;
		}
		connectInfo.setUser(user);
		connectInfo.setPass(pass);
		this.ceConnection = getCEConnection();
		peConn.connect(connectInfo);
	}
	
	/**
	 * @return
	 */
	private boolean verifyConnection(String name, String pass, CECryptoManager cryptoMgr) {
		int depth = 0;
		User user = null;
		UserContext uc = null;
		// TODO Auto-generated method stub
		try {
			
			Connection tstConn = Factory.Connection.getConnection(connectInfo.getConnectUrl());
			Subject subject = UserContext.createSubject(
					tstConn, 
					name,
					cryptoMgr.getClearText(pass),
					"FileNetP8WSI");
			uc = UserContext.get();
			
			uc.pushSubject(subject);
			depth++;
			user = Factory.User.fetchCurrent(
							tstConn, 
							null);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if (depth > 0	) {
				uc.popSubject();
			}
		}
		return (user == null) ? false: true;
	}

	public CWD getCWD() {
		return cwd;
	}
	
	/**
	 * Determine whether an identifier argument is a GUID
	 * {97BD340E-6E8F-4BD8-9907-707F556FD41E}
	 * @param uri
	 * @return
	 */
	public boolean isId(String uri){
		if ( (uri.length() == 38) &&
				(uri.startsWith("{")) &&
					(uri.endsWith("}")) ) {
						return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public String urlDecode(String uri){
		return java.net.URLDecoder.decode(uri);
	}
	
	public boolean isCommand(String cmdId) {
		return (commandMap.containsKey(cmdId));
	}
	
	public void useOjectStore(String objectStoreName) throws Exception {
		if (! isValidObjectStoreName(objectStoreName)){
			throw new IllegalArgumentException("Object store does not exist: " + 
						objectStoreName);
		}
		this.connectInfo.setObjectStore(objectStoreName);
		this.initCwd();
	}
	
	public Class<?> getCmdbyId(String cmdId) throws Exception {
		String cmdClassName = null;
		Class<?> cmdClass = null;
		
		if (! commandMap.containsKey(cmdId)) {
			String msg = "No command with command ID : " + cmdId;
			throw new IllegalArgumentException(msg);
		}
		cmdClassName = (String) commandMap.get(cmdId);
		cmdClass = Class.forName(cmdClassName);
		return cmdClass;
	}
	
	/**
	 * @param objectStoreName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isValidObjectStoreName(String objectStoreName) throws Exception {
		Domain domain = getDomain(getCEConnection());
		Iterator<ObjectStore> iter = domain.get_ObjectStores().iterator();
		boolean found = false;
		while (iter.hasNext()){
			ObjectStore nextOs = iter.next();
			if ( (nextOs.get_Name().equals(objectStoreName)) ||
					(nextOs.get_SymbolicName().equals(objectStoreName)) ){
				found = true;
				break;
			}
		}
		return (found == true);
	}
	
	public CEConnectInfo getCEConnectInfo() {
		return this.connectInfo;
	}
	
	public String getConnectionDescription() {
		return this.connectInfo.getUser() + 
			":" + this.connectInfo.getObjectStore() + 
			"@" + this.connectInfo.getConnectUrl();
	}
	
	public String getOSName() {
		return this.connectInfo.getObjectStore();
	}


	public void execute(String cmdLine) throws Exception {
		try {
			cmdLine = cmdLine.trim();
			String[] parseLine = parseCmd(cmdLine);
			String cmdId = parseLine[0];
			if (cmdId.startsWith("#")) {
				response.printOut(cmdLine);
				return;
			}
			if (! isCommand(cmdId)) {
				response.printOut(cmdId + ": command not found");
			}
			doExecute(cmdId, parseLine[1], cmdLine);
		} catch (Exception e) {
			response.logErr(e);
		}
	}
	
	protected void doExecute(String cmdId, String args, String origCmdLine) throws Exception {
		BaseCommand cmd = null;
		BaseRequestInfo requestInfo = createRequestInfo(cmdId, args, origCmdLine);
		
		if (! isConnectedOrWillConnect(cmdId)) {
			throw new IllegalStateException("Not connected");
		}
		cmd = createCommand(requestInfo.getCmdId());
		try {
			cmd.setShell(this);	
			cmd.setRequest(requestInfo);
			cmd.run();
			
		} finally {
			doAfter(cmd);
			getHistoryList().add(cmd);
		}
	}


	/**
	 * @param cmd
	 */
	protected void doAfter(BaseCommand cmd) {
		//no-op
		
	}

	/**
	 * @param cmdId
	 * @return
	 */
	protected boolean isConnectedOrWillConnect(String cmdId) {
		if ( connectInfo.readyToConnect() ||
				cmdId.equalsIgnoreCase(ConnectCmd.CMD)) {
			return true;
		}
		return false;
	}

	protected void initCwd() {
		cwd = new CWD();
		cwd.setShell(this);
	}

	protected BaseRequestInfo createRequestInfo(
			String cmdId, 
			String args, 
			String origCmdLine) throws Exception {
		BaseRequestInfo requestInfo = null;
		
		if ("quit".equals(cmdId)) {
			System.exit(0);
		}
		
		if ( "!".equals(cmdId) || "!!".equals(cmdId) ) {
			requestInfo = cloneHistoricalRequestInfo( cmdId, args);
		} else {
			requestInfo = new BaseRequestInfo();
			requestInfo.setArgs(parseArgs(args));
			requestInfo.setCmdLine(origCmdLine);
			requestInfo.setCmdId(cmdId);				
		}
		requestInfo.setResponse(response);
		return requestInfo;
	}
	
	private String[] parseArgs(String args) throws Exception {
		return new ArgsParser(args).parse();
	}

	protected BaseRequestInfo cloneHistoricalRequestInfo(String cmdId, String args) {
		BaseRequestInfo oldRequest =  null;
		BaseRequestInfo  copy = null;
		BaseCommand oldCommand = null;
		
		int historyPos = -1;
		
		if (historyList.size() <= 0) {
			throw new IllegalArgumentException("No items in history list");
		}
		if ("!!".equals(cmdId)) {
			historyPos = historyList.size() - 1;
		} else {
			if (args == null) {
				throw new IllegalArgumentException("cloneHistoricalRequestInfo: requires an integer parameter");
			}
			try {
				historyPos = Integer.parseInt(args) - 1; // decrement by one  
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("cloneHistoricalRequestInfo: "  + args + e.getMessage());
			}
			
			if (isOutOfRange(historyPos, historyList.size())) {
				String msg = "cloneHistoricalRequestInfo: pos " + 
						historyPos + " is out of range";
				throw new IllegalArgumentException(msg);
			}
		}
		
		oldCommand = fetchOldCommandFromHistory(historyPos);
		if (oldCommand != null) {
			oldRequest = oldCommand.getRequest();
			copy = new BaseRequestInfo(oldRequest);
		}
		
		return copy;
	}
	
	protected boolean isOutOfRange(int pos, int size) {
		return (pos < 0 || pos >= size);
	}

	protected BaseCommand fetchOldCommandFromHistory(int historyPos) {
		BaseCommand oldCommand = null;
		oldCommand = (BaseCommand) historyList.get(historyPos);
		
		return oldCommand;
	}

	protected BaseCommand createCommand(String cmdId) throws Exception {
		String cmdClass;
		BaseCommand cmd = null;
		
		if (! commandMap.containsKey(cmdId)) {
			String msg = "No command with command ID : " + cmdId;
			throw new IllegalArgumentException(msg);
		}
		cmdClass = (String) commandMap.get(cmdId);
		cmd = (BaseCommand) Class.forName(cmdClass).newInstance();
		return cmd;
	}
	
	protected String[] parseCmd(String line) {
		String cmd;
		String args;
		
		int pos = line.indexOf(" ");
		if (pos >= 1) {
			cmd = line.substring(0, pos).trim().toLowerCase();
			args = line.substring(pos).trim();
		} else {
			cmd = line;
			args = null;
		}
		
		return new String[] {cmd, args};
	}	
	
	/**
	 * 
	 */
	private void initEnv() throws Exception {
		env = new Env();
		try {
			new PropertyUtil().loadProperties(env, "/env.properties");
		} catch (Exception e) {
			System.err.println("Failed to load env");
		}
	}
	
/** operations that are common to other commands **/	
	
	Folder getFolder(String folderName){
		
		Folder folder = getFolder(folderName, null);  
		
		return folder; 
	}
	
	public Folder getFolder(String fullPath, PropertyFilter filter) {
		ObjectStore store = getObjectStore();
		Folder folder = Factory.Folder.fetchInstance(store,fullPath, null);  
		
		return folder; 
	}
	
	public Document getDocument(String fullPath) throws Exception {
		Document doc = getDocument(fullPath, null);
		
		return doc;
	}
	
	Document getDocument(String fullPath, PropertyFilter filter) throws Exception {
		ObjectStore store = getObjectStore();
		Document doc = Factory.Document.fetchInstance(store, fullPath, filter);
		
		return doc;
	}
	
	public Connection getCEConnection() throws Exception {
		String rawPassword = connectInfo.getPass();
	    Connection conn = Factory.Connection.getConnection(connectInfo.getConnectUrl());
	    Subject subject = UserContext.createSubject(
	    		conn, 
	    		connectInfo.getUser(), 
	    		getConnectionMgr().getCECryptoManager().getClearText(rawPassword), 
	    		"FileNetP8WSI");
	    UserContext uc = UserContext.get();
	    if (this.credentialsDepth > 0) {
	    	uc.popSubject();
	    	this.credentialsDepth--;
	    }
	    uc.pushSubject(subject);
	    this.credentialsDepth++;
	    
	    return conn;
	}
	
	public VWSession  getPEConnection() throws Exception {
		return peConn.getPEConnection();
	}
	
	public ObjectStore getObjectStore () {
		String osName = connectInfo.getObjectStore();
		Domain domain = getDomain(this.ceConnection);
		ObjectStore os = null;
		os = Factory.ObjectStore.fetchInstance(domain, osName, null);
		return os;
	}
	
	public Domain getDomain(Connection conn) {
		String domainName = this.connectInfo.getDomain();
		Domain domain = Factory.Domain.fetchInstance(conn, domainName, null);
	    return domain;
	}
	
	@SuppressWarnings("unchecked")
	Map<String, String> getCommands() throws Exception {
		Map<String, String> newMap = (Map<String, String>) commandMap.clone();
		return newMap;
	}
	
	java.util.Properties getSudoList() throws Exception {
		return connectInfo.getSudoList();
	}

	/**
	 * 
	 */
	public void init() throws Exception {
		initEnv();
		initCommands();
		response = new BaseResponse(System.out, System.err);
		connectionMgr  = new ConnectionManager();
		connectionMgr.initConnectionStorageInfo();
		peConn = new PEConnectionSvc();
		peConn.setCryptoManager(this.getConnectionMgr().getCECryptoManager());
		
	}
	
	public ArrayList<String> addCommands(java.util.Properties cmdProps) {
		return loadCommandsFromProperties(cmdProps);
	}
	
	public void initCommands() throws Exception {
		initCEShellCommands();
		initCmdExtensions();
	}
	
	/**
	 * 
	 */
	private void initCmdExtensions() throws Exception {
		ArrayList<String> results = null;
		File extensionsFolder = getEnv().getCeExtsDir();
		if ( (extensionsFolder == null) ||
				(! extensionsFolder.exists())) {
			return;
		}
		PropertyUtil propUtil = new PropertyUtil();
		String[] cmdPropertiesList = 
				extensionsFolder.list(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return (name.endsWith("ceshell.cmds.properties")); 
					}
				});
		
		for (int i = 0; i < cmdPropertiesList.length; i++) {
			String propName = cmdPropertiesList[i];
			File nextCmdPropsFile = new File(extensionsFolder, propName);
			java.util.Properties cmdIds = 
					propUtil.loadPropertiesFromFile(nextCmdPropsFile);
			results = loadCommandsFromProperties(cmdIds);
			showResults(propName, results);
		}
	}

	protected void initCEShellCommands() throws Exception {
		ArrayList<String> results = null;
		PropertyUtil util = new PropertyUtil();
//		File propFile = util.fetchResourceAsFile("/ceshell.cmds.properties");
//		java.util.Properties ceshellCmds = util.loadPropertiesFromFile(propFile);
		java.util.Properties ceshellCmds = util.loadProperties("/ceshell.cmds.properties");
		results = loadCommandsFromProperties(ceshellCmds);
		showResults("Core CE Commands", results);
	}

	private void showResults(String cmdSrc, ArrayList<String> results) {
		Iterator<String> iter = results.iterator();
		while (iter.hasNext()) {
			String nextCmdResult = (String) iter.next();
			if (nextCmdResult.indexOf("fail") != -1) {
				System.out.println(cmdSrc + ":\t" + nextCmdResult);
			}
		}
	}

	private ArrayList<String> loadCommandsFromProperties(java.util.Properties ceshellCmds) {
		ArrayList<String> results = new ArrayList<String>();
		Enumeration<Object> enumer = ceshellCmds.keys();
		while (enumer.hasMoreElements()) {
			String cmdId = enumer.nextElement().toString();
			String cmdClass = ceshellCmds.getProperty(cmdId).trim();
			
			try {
				@SuppressWarnings("unused")
				BaseCommand cmd = (BaseCommand)Class.forName(cmdClass).newInstance();
				commandMap.put(cmdId, cmdClass);
				results.add(cmdId + "=success");
			} catch (Throwable e) {
				results.add(cmdId + "=fail");
			}	 
		}
		return results;
	}
}
