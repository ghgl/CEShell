/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Collection;
import java.util.HashSet;

import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.impl.DocEditInfo;
import com.ibm.bao.ceshell.impl.EditInfo;
import com.ibm.bao.ceshell.impl.EditInfoImpl;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  EditCmd
 *
 * @author regier
 * @date   Sep 8, 2011
 */
public class EditCmdV2 extends BaseCommand {
	
	public static final String
		CUSTOM_OBJECT = "c",
		DOCUMENT = "d",
		FOLDER = "f";

	protected static final String[][] EDIT_TYPES = new String[][] {
		{CUSTOM_OBJECT, "custom object"},
		{DOCUMENT, "document (default)"},
		{FOLDER, "folder"},
	};
	
	private static final String 
		CMD = "edit", 
		CMD_DESC = "edit a document properties",
		HELP_TEXT=CMD_DESC;

	// param names
	private static final String 
		EDIT_TYPE_OPT = "type",
		URI_ARG = "uri";

	private static Collection<String> ParentTypes = new HashSet<String>();
	static {
		for (int i = 0; i < EDIT_TYPES.length; i++) {
			ParentTypes.add(EDIT_TYPES[i][0]);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String editType = DOCUMENT; // default to doc
		StringParam editTypeOpt = (StringParam) cl.getOption(EDIT_TYPE_OPT);
		StringParam pathUriArg = (StringParam) cl.getArg(URI_ARG);
		String uri = pathUriArg.getValue();
		
		if (editTypeOpt.isSet()) {
			editType = editTypeOpt.getValue();
		}
		return edit(uri, editType);
	}

	/**
	 * @param uri
	 */
	public boolean edit(String uri, String editType) {
		//IndependentlyPersistableObject ceObj = null;
		EditInfo currentEditInfo = null;
		if (DOCUMENT.equals(editType)) {
			Document doc = this.fetchDoc(uri);
			currentEditInfo = new DocEditInfo(this.getShell(), doc, doc.getClassName());
		} else if(FOLDER.equals(editType)) {
			Folder ceObj = fetchFolder(uri);
			currentEditInfo = new EditInfoImpl(this.getShell(), ceObj, ceObj.getClassName());
		} else if (CUSTOM_OBJECT.equals(editType)) {
			// TODO: implement
			getResponse().printOut("TODO: implement edit of custom object");
			return true;
		}
		getShell().setCurrentEditInfo(currentEditInfo);
		getShell().setMode(CEShell.MODE_EDIT);
		getResponse().printOut("edit...(use \'set\', \'save\', and \'cancel\' commmands");
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam editTypeOpt = null;
		StringParam pathUriArg = null;
		
		// options
		editTypeOpt = getEditTypeOpt();
		
		// cmd args
		{
		
			String pathURIDesc = "URI indicating a file or folder. It can also be "+
				"the ID of a document or folder. If the type is a document class, " +
				"the value can be the name of the document class";
				
				
				 pathUriArg = new StringParam(URI_ARG, 
						pathURIDesc,
						StringParam.REQUIRED);
				pathUriArg.setMultiValued(false);
				
		}
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { editTypeOpt }, 
					new Parameter[] { pathUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}
	
	protected StringParam getEditTypeOpt() {
		
		
		String editTypeOptDesc = getEditTypeOptsDesc();
		StringParam editTtypeOpt = new StringParam(EDIT_TYPE_OPT,
				editTypeOptDesc.toString(),
				StringParam.OPTIONAL);
		editTtypeOpt.setAcceptableValues(ParentTypes);
		editTtypeOpt.setMultiValued(false);
		
		return editTtypeOpt;
	}

	/**
	 * @return
	 */
	private String getEditTypeOptsDesc() {
		StringBuffer editTypesDesc = new StringBuffer();
		{
			editTypesDesc.append("Edit CE object type <parentType> is\n");
			for (int i = 0; i < EDIT_TYPES.length; i++) {
				editTypesDesc.append("\t").append(EDIT_TYPES[i][0])
					.append(":\t")
					.append(EDIT_TYPES[i][1])
					.append("\n");
			}		
		}
		return editTypesDesc.toString();
	}

}
