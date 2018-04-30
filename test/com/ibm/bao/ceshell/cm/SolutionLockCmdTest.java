package com.ibm.bao.ceshell.cm;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.ibm.bao.ceshell.util.FileUtil;
import com.ibm.bao.ceshell.util.StringUtil;

import junit.framework.TestCase;

public class SolutionLockCmdTest extends TestCase {

	public void testSolutionLocksProps() throws Exception {
		String xml = loadXml();
		Locks locksDto = LocksHandler.parse(xml);
		List<LockVO> locks = locksDto.getLocks();
		assertEquals(1, locks.size());
		
		List<String> users = locksDto.getLoggedInUsers();
		assertEquals(6, users.size());
	}
	
	public void testParseDate() throws Exception {
		String DATE_FMT_FEM = "MM/dd/yyyy hh:mm:ss a";
		String DATE_FMT = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'";
		
		String raw = "2018-04-27T01:26:31.939Z";
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FMT);
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date d =  dateFormatter.parse(raw);
		
		SimpleDateFormat femFormatter = new SimpleDateFormat(DATE_FMT_FEM);
		femFormatter.setTimeZone(TimeZone.getDefault());
		String localTime = femFormatter.format(d);
		System.out.println(localTime);
		
	}

	private String loadXml() throws Exception {
		URL url = this.getClass().getResource("sample.xml");
		String fileName = url.getFile();
		File inFile = new File(fileName);
		ArrayList<String> raw = FileUtil.load(inFile);
		StringBuffer buf = new StringBuffer();
		for (String line : raw) {
			buf.append(line).append("\n");
		}
		return buf.toString();
	}
	
	
}
