package com.ibm.bao.ceshell.cm;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/*
 * <lock>
        <type>5</type>
        <resource>UCM_CreateMPIP</resource>
        <displayName>UCM_CreateMPIP</displayName>
        <caseType>UCM_MPIP</caseType>
        <lockedby>gregier</lockedby>
        <timestamp>2018-04-26T16:22:27.558Z</timestamp>
    </lock>
 */
public class LocksHandler extends DefaultHandler {
	
	public static final String
		E_LOCK = "lock",
		E_TYPE = "type",
		E_RESOURCE = "resource",
		E_DISPLAY_NAME = "displayName",
		E_CASE_TYPE = "caseType",
		E_LOCKED_BY = "lockedby",
		E_TIMESTAMP = "timestamp",
		E_LOGGED_IN_USERS = "loggedInUsers";
	
	private Locks  locks= new Locks();
	private LockVO lockItem = new LockVO();
    
    private StringBuffer sb;
    
    public static Locks parse(String rawXml) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser sp = factory.newSAXParser();
        XMLReader xr = sp.getXMLReader(); 

        LocksHandler handler = new LocksHandler();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(new StringReader(rawXml))); 
        
        return handler.getLocks();
    }

    private Locks getLocks() {
        return locks;
    }

    @Override
    public void startDocument() throws SAXException {
        // TODO Auto-generated method stub
        super.startDocument();

        sb = new StringBuffer();
    }

    @Override
    public void startElement(
    		String uri, 
    		String localName, 
    		String qName,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(qName.equals(E_LOCK)){
        	lockItem = new LockVO();
        }

        sb.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length){
        try {
            super.characters(ch, start, length);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        sb.append(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /**
     * where the real stuff happens
     */
    @Override
    public void endElement(String uri, String localName, String qName)  throws SAXException {

        if(lockItem != null){
            if (qName.equalsIgnoreCase(E_TYPE)) {
                int type = Integer.parseInt(sb.toString());
                lockItem.setType(type);
            } else if (qName.equalsIgnoreCase(E_RESOURCE)) {
                lockItem.setResource(sb.toString());
            } else if (qName.equalsIgnoreCase(E_DISPLAY_NAME)){
                lockItem.setDisplayName(sb.toString());
	        } else if (qName.equalsIgnoreCase(E_CASE_TYPE)){
	        	lockItem.setCaseType(sb.toString());
	        } else if (qName.equalsIgnoreCase(E_LOCKED_BY)){
	        	lockItem.setLockedBy(sb.toString());
	        } else if (qName.equalsIgnoreCase(E_TIMESTAMP)){
	        	lockItem.setTimeStamp(sb.toString());
            } else if (qName.equalsIgnoreCase(E_LOCK)){
            	locks.addLock(lockItem);
            	lockItem = null;
            }
        } else if(qName.equalsIgnoreCase(E_LOGGED_IN_USERS)){
        	parseUsers(sb.toString());
        }
        sb.setLength(0);
    }

	private void parseUsers(String users) {
		
		if (users != null && users.length() > 0) {
			StringTokenizer tokenizer = new StringTokenizer(users, ",");
			while (tokenizer.hasMoreTokens()) {
				locks.addUser(tokenizer.nextToken());
			}
		}
	}
}