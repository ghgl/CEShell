/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.filenet.api.admin.CodeModule;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.constants.FilteredPropertyType;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.events.EventAction;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.QueryHelper;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 * CodeModuleUpdate
 * 
 * @author regier
 * @date Sep 2, 2011
 */
public class CodeModuleUpdateCmd extends BaseCommand {

	private static final String CMD = "codemodupdate",
			CMD_DESC = "Update a code module with a new version",
			HELP_TEXT = "Usage:" + "\n\tcodemodupdate -file c:\\data\\mylib.jar  /CodeModules/mymod ";
	
	private static final String 
			SRCFILE_OPT = "file",
			DOC_URI_ARG = "docUri";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String codeModuleUri = cl.getArg(DOC_URI_ARG).getValue().toString();
		FileParam srcFileParam = (FileParam) cl.getOption(SRCFILE_OPT);
		Boolean updateEventHandlers = Boolean.TRUE;
		File srcFile = srcFileParam.getValue();
		
		return codeModuleUpdate(codeModuleUri, srcFile, updateEventHandlers);
	}

	/**
	 * @param codeModuleUri
	 * @param localFile
	 * @param updateEventHandlers
	 * @throws Exception 
	 */
	public boolean codeModuleUpdate(String codeModuleUri, File localFile,
			Boolean updateEventHandlers) throws Exception {
		Document originalCodeModule = this.fetchDoc(codeModuleUri);
		CodeModule updatedCodeModule = null;
		List<EventAction> eventActions = null;
		String classDescriptionName = null;

		if (originalCodeModule == null) {
			throw new IllegalArgumentException("Code module not found at "
					+ codeModuleUri);
		}
		classDescriptionName = originalCodeModule.getClassName();
		if (!"CodeModule".equals(classDescriptionName)) {
			throw new IllegalArgumentException("Document at " + codeModuleUri
					+ " is not a CodeModule");
		}
		eventActions = getReferencedEventActions(originalCodeModule);
		updatedCodeModule = versionCodeModule(originalCodeModule.get_Id(),
				localFile);
		updateEventActions(updatedCodeModule, eventActions);
		return true;
	}

	/**
	 * @param updatedCodeModule
	 * @param eventActions
	 */
	private void updateEventActions(CodeModule updatedCodeModule,
			List<EventAction> eventActions) {
		for (Iterator<EventAction> iterator = eventActions.iterator(); iterator.hasNext();) {
			EventAction eventAction = iterator.next();
			eventAction.set_CodeModule(updatedCodeModule);
			eventAction.save(RefreshMode.REFRESH);
		}
	}

	/**
	 * @param originalCodeModule
	 * @param localFile
	 * @return
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	private CodeModule versionCodeModule(Id originalCodeModuleId, File localFile) throws Exception {
		// Create ContentTransfer object from updated JAR content
		CodeModule cm = null;
		CodeModule updatedCodeModule = null;
		ContentElementList contentList = Factory.ContentTransfer.createList();
		ContentTransfer ctNew;
		FileInputStream fileIS;
		String contentType = getMimeTypes().getMimeType(localFile.getName());

		ctNew = Factory.ContentTransfer.createInstance();
		fileIS = new FileInputStream(localFile.getAbsolutePath());
		ctNew.setCaptureSource(fileIS);
		ctNew.set_ContentType(contentType);
		contentList.add(ctNew);

		// Check out current version of CodeModule object
		cm = Factory.CodeModule.getInstance(getShell().getObjectStore(),
				ClassNames.CODE_MODULE,
				originalCodeModuleId);
		cm.checkout(com.filenet.api.constants.ReservationType.EXCLUSIVE, null,
				null, null);
		cm.save(RefreshMode.REFRESH);

		// Get reservation object from the checked-out code module.
		// This will become the new version of the code module.
		updatedCodeModule = (CodeModule) cm.get_Reservation();

		// Set the new content on the reservation object
		updatedCodeModule.set_ContentElements(contentList);

		// Check in the new version of the code module
		updatedCodeModule.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY,
				CheckinType.MAJOR_VERSION);
		updatedCodeModule.save(RefreshMode.REFRESH);
		
		return updatedCodeModule;

	}

	/**
	 * @param codeModule
	 * @return
	 * @throws Exception 
	 */
	private List<EventAction> getReferencedEventActions(Document codeModule) throws Exception {
		List<EventAction> eventsList = new ArrayList<EventAction>();
		List<Map<String, Object>> results = null;
		String id = codeModule.get_Id().toString();
		String query = null; 
		QueryHelper helper = new QueryHelper(getShell());
		PropertyFilter pf = new PropertyFilter();
		
		query = String.format("select e.Id EA_ID from EventAction e INNER JOIN CodeModule cm on e.CodeModule = cm.this where cm.Id = \'%s\'", id);
		pf.addIncludeType(1, null, null, FilteredPropertyType.SINGLETON_OBJECT, new Integer(1) );
		pf.addIncludeProperty(0, null, Boolean.TRUE, PropertyNames.CODE_MODULE, new Integer(1) );

		results = helper.executeQuery(query);
		for (Iterator<Map<String, Object>> iterator = results.iterator(); iterator.hasNext();) {
			Map<String, Object> record = iterator.next();
			String evtId = record.get("EA_ID").toString();
			EventAction event = Factory.EventAction.fetchInstance(getShell().getObjectStore(), new Id(evtId), pf);
			eventsList.add(event);
		}	
		return eventsList;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam docArg = null;
		FileParam srcFileOpt = null;

		// options
		srcFileOpt = new FileParam(SRCFILE_OPT,
				"Src files for import)",
				FileParam.IS_FILE & FileParam.IS_READABLE,
				FileParam.REQUIRED);
		srcFileOpt.setOptionLabel("<srcfile>");
		srcFileOpt.setMultiValued(false);
		// cmd args
		docArg = new StringParam(DOC_URI_ARG, "Code module to update",
				StringParam.REQUIRED);
		docArg.setMultiValued(false);
		
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {srcFileOpt }, 
					new Parameter[] { docArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
