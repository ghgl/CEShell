package com.ibm.bao.ceshell.cm;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.ibm.bao.ceshell.util.FileUtil;

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
