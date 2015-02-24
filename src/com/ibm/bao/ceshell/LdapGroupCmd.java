/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.GroupSet;
import com.filenet.api.collection.UserSet;
import com.filenet.api.core.Factory;
import com.filenet.api.security.Group;
import com.filenet.api.security.User;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  GroupPropsCmd
 *
 * @author GaryRegier
 * @date   Oct 2, 2010
 */
public class LdapGroupCmd extends BaseCommand {
	public static final String 
		CMD = "ldapgrp", 
		CMD_DESC = "show user properties",
		HELP_TEXT = "Usage:" +
		"\n\tldapgrp <group-name>" +
		"\n\tprints the properties of the group <group-name> plus memberships";
	
	// param names
	private static final String 
		EXISTS_OPT = "exists",
		GROUP_ARG = "group";


	/* 
	 * (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam groupNameParam = (StringParam) cl.getArg(GROUP_ARG);
		BooleanParam existsOpt = (BooleanParam) cl.getOption(EXISTS_OPT);
		String groupShortName = null;
		boolean bExists = false;
		
		if (existsOpt.isSet()) {
			bExists = existsOpt.getValue();
		}
		boolean bShowChildUsers = true;
		boolean bShowChildGroups = true;
		boolean bShowGroupMemberships = true;
		
		//@TODO: add cl options/parameters to determine what to show
		groupShortName = groupNameParam.getValue();
		return ldapGroup(groupShortName, bExists, bShowChildUsers, bShowChildGroups,
				bShowGroupMemberships);
	}

	public boolean ldapGroup(
			String groupShortName, 
			boolean bExists,
			boolean bShowChildUsers, 
			boolean bShowChildGroups,
			boolean bShowGroupMemberships) {
		Group group = null;
		
		groupShortName = getShell().urlDecode(groupShortName);
		try {
			group = Factory.Group.fetchInstance(getShell().getCEConnection(), 
					groupShortName, 
					null);
		} catch (Exception e) {
			getResponse().printOut("Group not found with name " + groupShortName);
			return false;
		}
		
		if (group == null) {
			return false;
		}
		if (bExists) {
			getResponse().printOut("Group exists: " + groupShortName);
			return true;
		}
		
		showGroupProperties(group, bShowChildUsers, bShowChildGroups, bShowGroupMemberships);
		return true;
	}
	
	/**
	 * @param group
	 */
	private void showGroupProperties(Group group, 
			boolean bShowChildUsers, 
			boolean bShowChildGroups, 
			boolean bShowGroupMemberships) {
		String[] labels = new String[] {
				"Id", "Name", "DistinguishedName", "DisplayName", "ShortName"
		};
		String id = group.get_Id();
		String name = group.get_Name();
		String dn = group.get_DistinguishedName();
		String displayName = group.get_DisplayName();
		String shortName = group.get_ShortName();
		String[] values = new String[] {
				id, name, dn, displayName, shortName
		};
		for(int i = 0; i < labels.length; i++) {
			String strValue;
			if (values[i] == null) {
				strValue = "<null>";
			} else {
				strValue = values[i].toString();
			}
			getResponse().printOut(
					StringUtil.padLeft(labels[i], ".", 20) + strValue);
		}
		
		if (bShowChildUsers) {
			listGroupUsers(group);
		}
		if (bShowChildGroups) {
			listChildGroups(group);
		}
		if (bShowGroupMemberships) {
			listGroupMemberships(group);
		}
		
	}

	/**
	 * @param group
	 */
	@SuppressWarnings("unchecked")
	private void listGroupMemberships(Group group) {
		GroupSet groups = group.get_MemberOfGroups();
		Iterator<Group> iter = groups.iterator();
		getResponse().printOut("\n\tGroup Memberships\n\t------------------------------------");
		while (iter.hasNext()) {
			Group nextGroup = iter.next();
			String shortName = nextGroup.get_ShortName();
//			String dn = nextGroup.get_DistinguishedName();
			getResponse().printOut("\t\t" + shortName);
		}
		
	}

	/**
	 * @param group
	 */
	@SuppressWarnings("unchecked")
	private void listChildGroups(Group group) {
		GroupSet groups = group.get_Groups();
		Iterator<Group> iter = groups.iterator();
		getResponse().printOut("\n\tGroup Members\n\t------------------------------------");
		while (iter.hasNext()) {
			Group nextGroup = iter.next();
			String shortName = nextGroup.get_ShortName();
			getResponse().printOut("\t\t" + shortName);
		}
	}

	/**
	 * @param group
	 */
	private void listGroupUsers(Group group) {
		UserSet users = group.get_Users();
		getResponse().printOut("\n\tUsers\n\t----------------------------------------------");
		SortedSet<String> usersSet = createUsersSet(users);
		Iterator<String> iter = usersSet.iterator();
		while (iter.hasNext()) {
			String msg = iter.next();
			getResponse().printOut("\t" + msg);
		}
	}

	/**
	 * @param users
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SortedSet<String> createUsersSet(UserSet users) {
		Iterator<User> iter = users.iterator();
		SortedSet<String> ss = new TreeSet<String>();
		while (iter.hasNext()) {
			User nextUser = iter.next();
			String displayName = nextUser.get_DisplayName();
			String name = nextUser.get_Name();
			String msg = "\t" + StringUtil.padLeft(displayName, " ", 30) + name;
			ss.add(msg);
		}
		return ss;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam groupArg = null;
		BooleanParam existsOpt = null;
		
		
		// options
		existsOpt = new BooleanParam(EXISTS_OPT,"confirm whether the group exists");
		existsOpt.setOptional(BooleanParam.OPTIONAL);
		
		// cmd args
		groupArg = new StringParam(GROUP_ARG, 
				"group properties",
				StringParam.REQUIRED);
		groupArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { existsOpt }, 
					new Parameter[] { groupArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}
