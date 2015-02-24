package com.ibm.bao.ceshell;

import java.util.ArrayList;
import java.util.StringTokenizer;

import jcmdline.*;

import junit.framework.TestCase;
import com.ibm.bao.ceshell.*;

public class CEShellTest extends TestCase {
	
	CEConnectInfo dev1ConnectInfo = null;
	CEConnectInfo p8DemoConnectInfo = null;
	CEShell ceShell = null;

	public CEShellTest(String name) {
		super(name);
	}
	
	protected CEConnectInfo getCurrentConnectInfo() {
//		return dev1ConnectInfo;
		return p8DemoConnectInfo;
	}

	protected void setUp() throws Exception {
		super.setUp();
		{
			String alias = "test";
			String uri = "http://bhd1fnece01.bhdev1.ibm.com:9080/wsi/FNCEWS40DIME/";
			String domain = null;
			String objectStore = "ObjectStore1";
			String username = "fnadmin1";
		    String password = "fnet4ecm";
			dev1ConnectInfo = new CEConnectInfo(alias, uri,domain, objectStore,username, password);;
		}
		
		{
			p8DemoConnectInfo = new CEConnectInfo();
			p8DemoConnectInfo.setConnectUrl("http://192.168.65.20:9080/wsi/FNCEWS40MTOM/");
			p8DemoConnectInfo.setUser("administrator");
			p8DemoConnectInfo.setPass("filenet");
			p8DemoConnectInfo.setObjectStore("ICCMOS");			
		}
		
		ceShell = new CEShell();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConnect() throws Exception {
		ceShell.connect(p8DemoConnectInfo);
//		ceShell.connect(dev1ConnectInfo);
		
	}
	
	public void testSelect() throws Exception {
//		String query = "select -h";
//		String query = "select DocumentTitle from Document";
//		String query = "select SymbolicName, DisplayName from ClassDefinition where SymbolicName like 'C%' order by SymbolicName";
		String query = "select SymbolicName, DisplayName FROM ClassDefinition WHERE SymbolicName LIKE 'C%' ORDER BY SymbolicName";
//		String query = "select count(*) from ClassDescription";
		ceShell.connect(getCurrentConnectInfo());
		ceShell.execute(query);
	}
	
	public void testListTypes() throws Exception {
		String cmd = null;
		
//		cmd = "listTypes -h";
//		cmd = "listTypes C%";
//		cmd = "listTypes -long";
		cmd = "listTypes";		
		ceShell.connect(getCurrentConnectInfo());
		ceShell.execute(cmd);
	}
	
	public void testHelp() throws Exception {
		String cmd = null;
		
//		cmd = "help -h";
//		cmd = "help";
		cmd = "help d";
		ceShell.connect(getCurrentConnectInfo());
		ceShell.execute(cmd);
	}
	
	public void testDescribe() throws Exception {
		String cmd = null;
		
//		cmd = "describe -h";
		cmd = "describe BHDC";
		ceShell.connect(getCurrentConnectInfo());
		ceShell.execute(cmd);
	}
	
	public void testBadParam() throws Exception {
		String[] args = toArray("-h");
		String helpText = "bad param test";
		// create command line handler
		BooleanParam recurseOpt = new BooleanParam(
				"recurse", 
				"recurse the folder structure");
		StringParam filePatternArgs = new StringParam(
				"file",
				"files to remove", 
				StringParam.REQUIRED);
		filePatternArgs.setMultiValued(true);
		
		CmdLineHandler icl =
            new VersionCmdLineHandler("V 0.1",
	            new HelpCmdLineHandler(helpText,
	                "rm",
	                "remove files from the repository",
	                new Parameter[] { recurseOpt},
	                new Parameter[] { filePatternArgs } ));
		icl.setDieOnParseError(false);
		
		ParseResultInfo info = icl.parse(args);
		Parameter gogo = icl.getOption("doesnotexist");
		Parameter interactive = icl.getOption("interactive");
		
		
		assertTrue(gogo.isSet());
		assertFalse("interactive set to false", interactive.isSet());
	}
	
	public void testLsCmdLineargs() {
		String helpText = "List files and/or directories from the repository";
		String lsCmdLine = "-long -dire";
		doTestlsCmdLine(helpText, lsCmdLine);
	}

	private void doTestlsCmdLine(String helpText, String lsCmdLine) {
		// create command line handler
		CmdLineHandler cl = null;
		
		{
			// params
			BooleanParam directoryOpt = new BooleanParam(
					"directory",
					"list directory entries");
			BooleanParam longOpt = new BooleanParam(
					"long",
					"use a long listing format");
			BooleanParam oneOpt = new BooleanParam(
					"1",
					"list one file per line");
			StringParam filePatternArg = new StringParam(
					"FILE",
					"FILEs to list", 
					StringParam.OPTIONAL);
			filePatternArg.setMultiValued(false);
			
			// create command line handler
			cl = new VersionCmdLineHandler("V 0.1",
		            new HelpCmdLineHandler(helpText,
		                "ls",
		                "list files from the repository",
		                new Parameter[] { directoryOpt, longOpt },
		                new Parameter[] { filePatternArg } ));
			cl.setDieOnParseError(false);
		}
		{
			String[] args = toArray(lsCmdLine);
			ParseResultInfo info = cl.parse(args);
			boolean success = info.isSuccess();
			int rc = info.getReturnCode();
			int sc = info.RC_SUCCESS;
		
			Parameter directoryOpt = cl.getOption("directory");
			System.out.println("directoryOpt: " + directoryOpt.getValue());
			Parameter longOpt = cl.getOption("long");
			System.out.println("long opt: " + longOpt.getValue());
			cl.getArg("FILE");
			Parameter filePatternArg = cl.getArg("FILE");
			if (filePatternArg == null) {
				System.out.println("no files");
			} else {
				System.out.println("FilepatternArg: " + filePatternArg.getValue());
			}
		}
	}
	
	public void testRMCmdLineArgs() {
		String[] interactiveCmdline = new String[] {
				"-interactive foo/baz/*.doc",
				"-i foo/baz/*.doc",
				"--interactive foo/baz/*.doc",
				"-i=true foo/baz/*.doc"
		};
		String helpArgs = "-h"; 
		
		String recusreCmd = "rm -r foo/baz/*.*";
		
		{
			BooleanParam recurseOpt = new BooleanParam(
					"recurse", 
					"recurse the folder structure");
			
			BooleanParam interactiveOpt = new BooleanParam(
					"interactive",
					"verify each action before execution");
			assertFalse(interactiveOpt.isSet());
			StringParam filePatternArgs = new StringParam(
					"file",
					"files to remove", 
					StringParam.REQUIRED);
			filePatternArgs.setMultiValued(true);
			
			String helpText = "Remove files and/or directories from the repository";
			
			// create command line handler
			CmdLineHandler cl =
	            new VersionCmdLineHandler("V 0.1",
		            new HelpCmdLineHandler(helpText,
		                "rm",
		                "remove files from the repository",
		                new Parameter[] { recurseOpt, interactiveOpt },
		                new Parameter[] { filePatternArgs } ));
			cl.setDieOnParseError(false);
			// parse command line
			{
				for (int i = 0; i < interactiveCmdline.length; i++) {
					String cmdLine = interactiveCmdline[i];
					String[] args = toArray(cmdLine);
					System.out.println(cmdLine);
					cl.parse(args);
					Parameter interactive = cl.getOption("interactive");
					assertTrue("interactive set to true", interactive.isSet());
					
				}
				
			}
			
			// parse help command line
			{
				assertFalse(interactiveOpt.isSet());
				String[] args = toArray(helpArgs);
				// create command line handler
				CmdLineHandler icl =
//		            new VersionCmdLineHandler("V 0.1",
			            new HelpCmdLineHandler(helpText,
			                "rm",
			                "remove files from the repository",
			                new Parameter[] { recurseOpt, interactiveOpt },
			                new Parameter[] { filePatternArgs } );
				icl.setDieOnParseError(false);
				
				ParseResultInfo info = icl.parse(args);
				boolean success = info.isSuccess();
				int rc = info.getReturnCode();
				int sc = info.RC_SUCCESS;
				
				Parameter gogo = icl.getOption("doesnotexist");
				Parameter interactive = icl.getOption("interactive");
				
				assertNull(gogo);
				assertFalse("interactive set to false", interactive.isSet());	
			}
			
		}
		System.out.println("howdy");
	}
	
	public void testBooleanParam() throws Exception {
		
	}

	public void testCd() throws Exception {
		ceShell.connect(dev1ConnectInfo);
		ceShell.execute("ls ");
	}

	public void testCwd() throws Exception {
		ceShell.connect(dev1ConnectInfo);
		ceShell.execute("cwd");
	}

	public void testLs() throws Exception {
		String cmd = null;
		
//		cmd = "ls";
//		cmd = "ls /glr-search-templates";
		cmd = "ls -l /glrtest";
//		cmd = "ls /glrtest";
		
		ceShell.connect(getCurrentConnectInfo());
		ceShell.execute(cmd);
	}
	
	public void testQuery() throws Exception {
		ceShell.connect(dev1ConnectInfo);
		ceShell.execute("select * from document where DocumentTitle = 'test'");
		
	}

	public void testWho() {
		fail("Not yet implemented");
	}

	public void testCp() {
		fail("Not yet implemented");
	}

	public void testMv() {
		fail("Not yet implemented");
	}

	public void testRm() {
		fail("Not yet implemented");
	}

	public void testDesc() throws Exception {
		//* /CFS-Docs/Stern
		String testId = "{496877B8-FE7B-4BA8-A037-41944E981B19}";
		ceShell.connect(p8DemoConnectInfo);
		ceShell.execute("desc " + testId);
	}

	public void testShow() {
		fail("Not yet implemented");
	}
	
	private String[] toArray(String rawString) {
		StringTokenizer tokenizer = new StringTokenizer(rawString);
		String[] result;
		ArrayList tokens = new ArrayList();
		
		while (tokenizer.hasMoreTokens()) {
			tokens.add(tokenizer.nextToken());
		}
		result = new String[tokens.size()];
		result =(String[]) tokens.toArray(result);
		
		return result;
	}
}
