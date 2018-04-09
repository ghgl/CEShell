package com.ibm.bao.ceshell.pe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

import filenet.vw.api.VWApplicationSpaceDefinition;
import filenet.vw.api.VWExposedFieldDefinition;
import filenet.vw.api.VWRoleDefinition;
import filenet.vw.api.VWSystemConfiguration;
import filenet.vw.api.VWWorkBasketDefinition;
import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

public class ApplicationSpaceDefCmd extends BasePECommand {
	
	// definitions
	private static final String
		INBOX_QUEUE = "Inbox";
	
	// cmd constants
	private static final String 
		CMD = "pe.asdef", 
		CMD_DESC = "display the application space definition",
		HELP_TEXT = CMD_DESC +
			"Usage: \n" +
			"pe.asdef <application-space-name>" +
			"pe.qsls Unified Case Manager";
	// Params
	public static final String 
		ROLE_Participant = "participant",
		CSV_OPT = "csv",
		APPLICATION_SPACE_NAME = "Name";

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam paraticipantOpt = (BooleanParam) cl.getOption(ROLE_Participant);
		BooleanParam csvFmtOpt = (BooleanParam) cl.getOption(CSV_OPT);
		StringParam namePatternArg = (StringParam) cl.getArg(APPLICATION_SPACE_NAME);
		Boolean security = Boolean.FALSE;
		Boolean csvFmt = Boolean.FALSE;
		String applicationSpaceRawName = namePatternArg.getValue();
		
		if (paraticipantOpt.isSet()) {
			security = paraticipantOpt.getValue();
		}
		
		if (csvFmtOpt.isSet()) {
			csvFmt = csvFmtOpt.getValue();
		}
		
		return appSpaceDef(applicationSpaceRawName, security, csvFmt);
	}

	public boolean appSpaceDef(String applicationSpaceRawName, boolean security, boolean csvFmt) throws Exception {
		String appSpaceName = this.decodePath(applicationSpaceRawName);
		if (security == false) {
			doAppSpaceDef(appSpaceName, csvFmt);
		} else {
			doListParticipant(appSpaceName, csvFmt);
		}
		return true;
	}

	private void doListParticipant(String appSpaceName, boolean csvFmt) throws Exception {
		VWSystemConfiguration sysConfig =  
				this.getPEConnection().fetchSystemConfiguration();
		VWApplicationSpaceDefinition appSpaceDef = 
				sysConfig.getApplicationSpaceDefinition(appSpaceName);
		if (appSpaceDef == null) {
			throw new IllegalArgumentException("No application space nameed " + appSpaceName);
		}
		
		getResponse().printOut("Application Space " + appSpaceDef.getName());
		
		VWRoleDefinition[] roleDefs = appSpaceDef.getRoleDefinitions();
		if (roleDefs == null) {
			getResponse().printOut("\t(no role defs)");
			return;
		}
		SortedSet<RoleParticipant> rps = new TreeSet<RoleParticipant>(new Comparator<RoleParticipant>() {

			public int compare(RoleParticipant o1, RoleParticipant o2) {
				return ( (o1.roleName + o1.participantName).compareTo((o2.roleName + o2.participantName)));
			}
			
		});
		for (VWRoleDefinition roleDef : roleDefs) {
			//rps.add(new RoleParticipant())
			String roleName = roleDef.getName();
			String[] participantNames = roleDef.getRoleParticipantNames();
			if (participantNames == null) {
				RoleParticipant rp = new RoleParticipant(roleName, "(No participants");
				rps.add(rp);
				continue;
			}
			for(String participantName: participantNames) {
				RoleParticipant rp = new RoleParticipant(roleName, participantName);
				rps.add(rp);
			}
		}
		if (csvFmt) {
			getResponse().printOut("Role,Participant");
			for (RoleParticipant rp : rps) {
				String row = rp.roleName + "," + rp.participantName;
				getResponse().printOut(row);
			}
		} else {
			ColDef[] cols = {
					new ColDef("Role", 35, StringUtil.ALIGN_LEFT),
					new ColDef("Participant", 35, StringUtil.ALIGN_LEFT)
			};
			getResponse().printOut(StringUtil.formatHeader(cols, " "));
			for (RoleParticipant rp : rps) {
				String[] data = new String[] {
						rp.roleName,
						rp.participantName
				};
				String row = StringUtil.formatRow(cols, data, " ");
				getResponse().printOut(row);
			}
		}
	}

	protected void doAppSpaceDef(String appSpaceName, boolean csvFmt) throws Exception {
		ArrayList<RoleInbasketQueue> roleDefDtos = new ArrayList<RoleInbasketQueue>();
		VWSystemConfiguration sysConfig =  
				this.getPEConnection().fetchSystemConfiguration();
		VWApplicationSpaceDefinition appSpaceDef = 
				sysConfig.getApplicationSpaceDefinition(appSpaceName);
		if (appSpaceDef == null) {
			throw new IllegalArgumentException("No application space nameed " + appSpaceName);
		}
		
		getResponse().printOut("Application Space " + appSpaceDef.getName());
		
		
		VWRoleDefinition[] roleDefs = appSpaceDef.getRoleDefinitions();
		if (roleDefs == null) {
			getResponse().printOut("\t(no role defs)");
			return;
		}
		for (VWRoleDefinition roleDef : roleDefs) {
			readRoleDefs(roleDef, roleDefDtos);
		}
		if (csvFmt == false) {
			ColDef[] cols = {
					new ColDef("Roles", 35, StringUtil.ALIGN_LEFT),
					new ColDef("Work basket", 35, StringUtil.ALIGN_LEFT),
					new ColDef("Queue", 35, StringUtil.ALIGN_LEFT)
			};
			getResponse().printOut(StringUtil.formatHeader(cols, " "));
			for (RoleInbasketQueue ribDto : roleDefDtos) {
				String row = StringUtil.formatRow(cols, 
						new String[] {ribDto.role, ribDto.inbasket, ribDto.queue },
						" ");
				getResponse().printOut(row);
			}
		} else {
			for (RoleInbasketQueue rib : roleDefDtos) {
				getResponse().printOut(rib.toCSV());
			}
		}
	}

	private void readRoleDefs(VWRoleDefinition roleDef, ArrayList<RoleInbasketQueue> roleDefDtos) {
		String roleName = roleDef.getName();
//		getResponse().printOut("\t" + roleDef.getName());
		
		VWWorkBasketDefinition[] workkBasketDefs = roleDef.getWorkBasketDefinitions();
		if (workkBasketDefs == null) {
			RoleInbasketQueue rib = new RoleInbasketQueue(roleName, "(none)", "(none)");
			roleDefDtos.add(rib);
			return;
		}
		for (VWWorkBasketDefinition workBasketDef : workkBasketDefs) {
			String wbName = workBasketDef.getName();
			String queueName = workBasketDef.getQueueName();
			RoleInbasketQueue rib = new RoleInbasketQueue(roleName, wbName, queueName);
			roleDefDtos.add(rib);
		}
		
	}

	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		BooleanParam csvOpt = null;
		BooleanParam participantOpt = null;
		StringParam applicationSpaceNameArg = null;
		
		// options
		{
			participantOpt = new BooleanParam(ROLE_Participant, "display participants associated with a role");
			participantOpt.setOptional(BooleanParam.OPTIONAL);
		}
		
		{
			csvOpt = new BooleanParam(CSV_OPT, "output in csv");
			csvOpt.setOptional(BooleanParam.OPTIONAL);
			csvOpt.setMultiValued(BooleanParam.SINGLE_VALUED);
		}
		
		// cmd args
		applicationSpaceNameArg = new StringParam(APPLICATION_SPACE_NAME, "Name pattern of Application Spaces to display",
				StringParam.REQUIRED);
		applicationSpaceNameArg.setMultiValued(false);
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { participantOpt, csvOpt }, 
					new Parameter[] { applicationSpaceNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

class RoleParticipant {
	
	public String roleName;
	public String participantName;
	
	public RoleParticipant(String roleName, String participantName) {
		this.roleName = roleName;
		this.participantName = participantName;
	}
}
	
class RoleInbasketQueue {
	public String role;
	public String inbasket;
	public String queue;
	
	
	public RoleInbasketQueue(String role, String inbasket, String queue) {
		this.role = role;
		this.inbasket = inbasket;
		this.queue = queue;
	}
	
	
	public String toCSV() {
		return String.format( "%s,%s,%s", new String[] {role, inbasket, queue});
		
	}
	
}
