/**
 * 
 */
package com.ibm.bao.ceshell;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.GroupSet;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.PageIterator;
import com.filenet.api.core.Factory;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.security.Group;
import com.filenet.api.security.User;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  UserPropsCmd
 *
 * @author GaryRegier
 * @date   Oct 2, 2010
 */
public class LdapUserCmd extends BaseCommand {
	
	public static final String 
		CMD = "ldapusr", 
		CMD_DESC = "show user properties",
		HELP_TEXT = "Usage:" +
		"\n\tldapusr" +
		"\n\tShow properties and group memberships of the current user" +
		"\n\n\tldapusr <user>" +
		"\n\tShow properties and group members of user <user>";
	
	// param names
	private static final String 
		EMAIL_ONLY_OPT = "email-only",
		SHORT_LIST_NO_GROUPS = "short",
		USER_ARG = "user";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam emailOpt = (BooleanParam) cl.getOption(EMAIL_ONLY_OPT);
		BooleanParam shortOpt = (BooleanParam) cl.getOption(SHORT_LIST_NO_GROUPS);
		StringParam userNameParam = (StringParam) cl.getArg(USER_ARG);
		
		
		Boolean emailOnly = Boolean.FALSE;
		Boolean shortOnly = Boolean.FALSE;
		Boolean listGroups = Boolean.TRUE;
		String userShortName = null;
		
		if (emailOpt.isSet()) {
			emailOnly = emailOpt.getValue();
		}
		
		if (shortOpt.isSet()) {
			shortOnly = shortOpt.getValue();
			if (shortOnly == Boolean.TRUE) {
				listGroups = Boolean.FALSE;
			}
		}
		
		if (userNameParam.isSet()) {
			userShortName = userNameParam.getValue();
		}
		
		
		return ldapUsr(userShortName, emailOnly, shortOnly, listGroups);
	}

	public boolean ldapUsr(
			String userShortName, 
			boolean emailOnly,
			boolean shortOnly,
			boolean listGroups) throws Exception {
		User user = null;
		
		try {
			if (userShortName != null) {
			user = Factory.User.fetchInstance(getShell().getCEConnection(), 
					userShortName, 
					null);
			} else {
				user = Factory.User.fetchCurrent(
						getShell().getCEConnection(), 
						null);
			}
		} catch (Exception e) {
			getResponse().printErr("Failed to find user");
			return false;
		}
		if (user == null) {
			String msg = "User not found with name ";
			if (userShortName != null) {
				msg = msg + userShortName;
			} else {
				msg = msg + "current user";
			}
			getResponse().printErr(msg);
			return false;
		}
		listUserProps(user, shortOnly, listGroups, emailOnly);
		return true;
	}
	
	private void listUserProps(
			User user, 
			boolean listShortOnly, 
			boolean listGroups, 
			boolean emailOnly) {
		if (emailOnly) {
			String email = null;
			if (user.get_Email() == null) {
				email = "null";
			} else {
				email = user.get_Email();
			}
			String name = user.get_DisplayName();
			String label = name + " email is: ";
			getResponse().printOut(StringUtil.padLeft(label, ".", 25) + email);
			return;
		}
		if (listShortOnly) {
			String userId = user.get_Id();
			String userDisplayName = user.get_DisplayName();
			String userDn = user.get_DistinguishedName();
			String userShortName = user.get_ShortName();
			String email = user.get_Email();
			String[][] props = new String[][] {
					{"Id", userId},
					{"DisplayName", userDisplayName},
					{"Dn", userDn},
					{"ShortName", userShortName},
					{"Email", email}
			};
			
			for(int i = 0; i < props.length; i++) {
				
				String name = props[i][0];
				String strValue = null;
				Object value = props[i][1];
				if (value != null) {
					strValue = value.toString();
				} else {
					strValue = "<null>";
				}
				getResponse().printOut(StringUtil.padLeft(name, ".", 25) + strValue);
			}
		} else if (listGroups) {
			@SuppressWarnings("unused")
			GroupSet groups = user.get_MemberOfGroups();
			getResponse().printOut("member of Groups:\n---------------------------------------------------");
			Property groupProp = user.fetchProperty("MemberOfGroups",
					createGroupPropertyFilter());
			IndependentObjectSet groupSet = groupProp
					.getIndependentObjectSetValue();
			PageIterator groupIter = groupSet.pageIterator();
			groupIter.setPageSize(50);
			listGroups(groupIter);
		}
	}
	
	/**
	 * @param groupIter
	 */
	private void listGroups(PageIterator pageIter) {
		// Cycle through pages
	    
	    while (pageIter.nextPage() == true) {
	        // Get counts
	        // Get elements on page
	        Object[] pageObjects = pageIter.getCurrentPage();                
	        for (int index = 0; index < pageObjects.length; index++) {
	            // Get sub object
	            Object elementObject = pageObjects[index];
	            Group group = (Group) elementObject;
	            getResponse().printOut("\t" + group.get_Name());
	        }
	    }
		
	}
	
	private PropertyFilter createGroupPropertyFilter() {
		PropertyFilter filter = new PropertyFilter();
		filter.addIncludeProperty(
				new FilterElement(null, null, null, "DisplayName, Name", null));
		return filter;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam emailOpt = null;
		BooleanParam shortOpt = null;
		StringParam userArg = null;
		
		// options
		emailOpt = new BooleanParam(EMAIL_ONLY_OPT,"display just the email address");
		emailOpt.setOptional(BooleanParam.OPTIONAL);
		
		shortOpt = new BooleanParam(SHORT_LIST_NO_GROUPS, "display user properties but no groups");
		shortOpt.setOptional(BooleanParam.OPTIONAL);
		
		// cmd args
		userArg = new StringParam(USER_ARG, 
				"user properties",
				StringParam.OPTIONAL);
		userArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {emailOpt, shortOpt }, 
					new Parameter[] { userArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
