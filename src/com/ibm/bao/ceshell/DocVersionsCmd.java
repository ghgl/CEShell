/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.core.Versionable;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  DocVersionsCmd
 *
 * @author GaryRegier
 * @date   Oct 28, 2010
 */
public class DocVersionsCmd extends BaseCommand {
	
	private static final String 
		CMD = "docversions", 
		CMD_DESC = "@todo list version series",
		HELP_TEXT = "@todo";
	
	private static final String
		URI_ARG = "URI";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam docUriParam = (StringParam) cl.getArg(URI_ARG);
		String docUri = docUriParam.getValue();
		
		return docVersions(docUri);
	}
	
	@SuppressWarnings("unchecked")
	public boolean docVersions(String docUri) {
		Document doc = null;
		VersionSeries vs = null;
		Id vsid = null;
		Iterator<VersionSeries> iter = null;
		
		if (getShell().isId(docUri)) {
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					new Id(docUri), null);
		   
		} else {
			String decodedUri = getShell().urlDecode(docUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					fullPath, null);
		}
		
		vs = doc.get_VersionSeries();
		
		vsid = vs.get_Id();
		iter = vs.get_Versions().iterator();
		getResponse().printOut(StringUtil.padLeft("versionSeries ID", ".", 20) + vsid.toString());
		ColDef[] defs = new ColDef[] {
				new ColDef("Id", 42, StringUtil.ALIGN_LEFT),
				new ColDef("Current", 7, StringUtil.ALIGN_LEFT),
				new ColDef("Reserved", 10, StringUtil.ALIGN_LEFT),
				new ColDef("Version", 7, StringUtil.ALIGN_RIGHT)
		};
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		while (iter.hasNext()) {
			Versionable next = (Versionable) iter.next();
			Boolean isCurrent = next.get_IsCurrentVersion();
			Boolean isReserved = next.get_IsReserved();
			Integer majorVersion = next.get_MajorVersionNumber();
			Integer minorVersion = next.get_MinorVersionNumber();
			Document docVersion = (Document) next;
			Id docVersionId = docVersion.get_Id();
			String[] row = new String[] {
				docVersionId.toString(),
				"" + isReserved,
				isCurrent.toString(),
				majorVersion.toString() + "." + minorVersion.toString()
			};
			
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam pathURIArg = null;

		// options
		

		// cmd args
		pathURIArg = new StringParam(URI_ARG, "URI indicating a document",
				StringParam.REQUIRED);
		pathURIArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
