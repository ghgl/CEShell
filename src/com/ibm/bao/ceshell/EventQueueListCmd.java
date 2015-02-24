/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.filenet.api.admin.EventQueueItem;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Factory;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.RetrievingBatch;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.QueryHelper;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  EventQueueListCmd
 *
 * @author regier
 * @date   Jan 29, 2013
 */
public class EventQueueListCmd extends BaseCommand {
	
	private static final String 
		CMD = "eqls", 
		CMD_DESC = "Event QueueListCmd",
		HELP_TEXT = "List the contents of the CE object store event queue";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return eventQueueList();
		
	}

	/**
	 * @return
	 */
	public boolean eventQueueList() throws Exception {
		// TODO: Add support for a short list that gets the scalar 
		// values out of the event queue quickly
		doListEventsLong();
		
		return true;
	}

	/**
	 * 
	 */
	private void doListEventsLong() throws Exception {
		PropertyFilter filter = null;
		RetrievingBatch rb = null;
		ObjectStore os = getShell().getObjectStore();
		filter = getEventPropertyFilter();
		String[] ids = fetchEventQueueIds();
		if (ids.length <= 0) {
			getResponse().printOut("No items in the event queue");
			return; 		
		}
		rb = RetrievingBatch.createRetrievingBatchInstance(
				getShell().getDomain(getShell().getCEConnection()));
		for (String id : ids) {		
			EventQueueItem eventQueueItem = Factory.EventQueueItem.getInstance(os, new Id(id));
			rb.add(eventQueueItem, filter);
		}
		rb.retrieveBatch();
		
		displayResults(rb);
		
	}

	public void displayResults(RetrievingBatch rb) throws Exception {
		Iterator<?> iter = rb.getBatchItemHandles(null).iterator();
		ColDef[] defs = new ColDef[] {
			new ColDef("EventId", 40, StringUtil.ALIGN_LEFT),
			new ColDef("SubName", 35, StringUtil.ALIGN_LEFT),
			new ColDef("SourceId", 40, StringUtil.ALIGN_LEFT),
			new ColDef("Source Class", 40, StringUtil.ALIGN_LEFT),
			new ColDef("SourceName", 40, StringUtil.ALIGN_LEFT)
		};
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		
		while (iter.hasNext()) {
			com.filenet.api.core.BatchItemHandle handle = (com.filenet.api.core.BatchItemHandle) iter.next();
			EventQueueItem eventQueueItem = (EventQueueItem) handle.getObject();
			String id = eventQueueItem.get_Id().toString();
			IndependentObject source = eventQueueItem.get_SourceObject();
			String sourceSymbolicName = source.get_ClassDescription().get_SymbolicName();
	    	String sourceObjectId = source.getProperties().getIdValue(PropertyNames.ID).toString();
	    	String souceName = source.getProperties().getStringValue(PropertyNames.NAME);
	    	IndependentObject sub = eventQueueItem.get_QueuedObject();
	    	String subName = sub.getProperties().getStringValue(PropertyNames.NAME);
	    	
	    	
			String[] row = new String[] {
				id,
				subName,
				sourceObjectId,
				sourceSymbolicName,
				souceName
			};
		
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
		}
	}
	
	
	
	/**
	 * @return
	 */
	private String[] fetchEventQueueIds() throws Exception {
		String[] ids;
		int size;
		QueryHelper helper = new QueryHelper(getShell());
		List<Map<String, Object>> results = helper.executeQuery(
				"select Id from EventQueueItem");
		
		size = results.size();
		ids = new String[size];
		for (int i = 0; i < ids.length; i++) {
			Map props = results.get(i);
			String nextId = props.get("Id").toString();
			ids[i] = nextId;
		}
		return ids;
	}

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

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
	CmdLineHandler cl = null;
		
		// options
		
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {}, 
					new Parameter[] {});
		cl.setDieOnParseError(false);

		return cl;
	}

}
