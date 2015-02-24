/**
 * 
 */
package com.ibm.bao.ceshell;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

import com.filenet.api.admin.StorageArea;
import com.filenet.api.collection.StorageAreaSet;
import com.filenet.api.core.ObjectStore;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  StorageAreaLs
 *
 * @author GaryRegier
 * @date   Oct 2, 2010
 */
public class StorageAreaLsCmd extends BaseCommand {
	public static final String 
		CMD = "sals", 
		CMD_DESC = "Storage Area listing",
		HELP_TEXT = "List the storage areas";

	// param names
	

	/*
	 *
	 * (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return storageAreaLs();
	}


	public boolean storageAreaLs() {
		//TODO: Refactor with ColDef
		ObjectStore os = getShell().getObjectStore();
		StorageAreaSet storeageAreas = os.get_StorageAreas();
		Iterator<?> iter = storeageAreas.iterator();
		NumberFormat fmt = new DecimalFormat("#,##0");
		String[] headers = new String[] {
				"DisplayName", "# elem", "created", "dels", "size (kb)"
		};
		int[] widths = new int[] {
			35, 13, 13, 13, 15	
		};
		StringBuffer headerBuf = new StringBuffer();
		for (int i = 0; i < widths.length; i++) {
			headerBuf.append(StringUtil.padLeft(headers[i], ".", widths[i]));
		}
		StringBuffer border = new StringBuffer();
		for (int i = 0; i < widths.length; i++) {
			border.append(StringUtil.padLeft("", "-", widths[i]));
		}
		getResponse().printOut(headerBuf.toString());
		getResponse().printOut(border.toString());
		while (iter.hasNext()) {
			StringBuffer row = new StringBuffer();
			StorageArea nextArea = (StorageArea) iter.next();
			String name = nextArea.get_DisplayName();
		
			Double contentElementCnt = nextArea.get_ContentElementCount();
			Double created = nextArea.get_ContentElementsCreated();
			Double dels = nextArea.get_ContentElementsDeleted();
			Double totalSize = nextArea.get_ContentElementKBytes();
			
			row.append(StringUtil.padLeft(name, ".", widths[0]));
			row.append(StringUtil.padLeft(
					fmt.format(contentElementCnt.doubleValue()), 
					".", widths[1]));
			row.append(StringUtil.padLeft(
					fmt.format(created.doubleValue()),
					".", widths[2]));
			row.append(StringUtil.padLeft(
					fmt.format(dels.doubleValue()),
					".", widths[3]));
			row.append(fmt.format(totalSize.doubleValue()));
			getResponse().printOut(row.toString());
		}
		return true;
	}


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		
		
		// options
		
		// cmd args
		

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] {  });
		cl.setDieOnParseError(false);

		return cl;
	}

}
