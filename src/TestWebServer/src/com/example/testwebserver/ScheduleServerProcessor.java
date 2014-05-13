package com.example.testwebserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/*
 * Process client messages.
 * 
 * TODO Make protocol version control
 * 
 * Possible commands
 * 1) ...
 */
public class ScheduleServerProcessor {
	
	private long protocolVersion;
	private ScheduleDbManager dbManager;
	private Map<String, CommandProcessor> processMap;
	
	protected interface CommandProcessor {
		 public JSONObject Process(String commandName, JSONObject command) throws JSONException;
	}
	
	protected void AddCommandProcessor(String commandName, CommandProcessor processor) {
		processMap.put(commandName, processor);
	} 
	
	public ScheduleServerProcessor(ScheduleDbManager manager) {
		this.dbManager = manager;
		protocolVersion = 1;
		
		processMap = new HashMap<String, CommandProcessor>();
		
		// User operations
		AddCommandProcessor("registerUser", 			new RegisterUserCommand()			);
		AddCommandProcessor("getUserId", 				new GetUserCommand()				);
		AddCommandProcessor("changeUserPassword", 		new ChangeUserPasswordCommand()		);
		
		// Groups
		AddCommandProcessor("createGroup", 				new RegisterGroupCommand()			);
		AddCommandProcessor("groupsList", 				new GroupsListCommand()				);
	}
	
	public String ProcessMsg(String msg) {
		try {
			JSONObject object = (JSONObject) new JSONTokener(msg).nextValue();
			
			long version 		= object.getLong("version");
			long userId  		= object.getLong("userId");
			String userPassword = object.getString("userPassword");
			
			JSONObject command = object.getJSONObject("command");
			String cname = command.getString("cname");
			
			if((cname.equals("registerUser") == false) && (cname.equals("getUserId") == false))
				if(dbManager.IsUserPasswordIsCorrect(userId, userPassword) == false)
					return "[ERROR: User ID or/and password is incorrect]";
			
			CommandProcessor cp = processMap.get(cname);
			if(cp == null) {
				// TODO Bad command error here
				return "[ERROR: Unknown command]";
			}
			JSONObject answer = cp.Process(cname, command);
			
			JSONObject fullAnswer = new JSONObject();
			fullAnswer.put("version", protocolVersion);
			// TODO Put correct server address and port 
			fullAnswer.put("serverAdress", 	"empty");
			fullAnswer.put("serverPort", 	0);
			fullAnswer.put("answer", answer);
			
			return fullAnswer.toString();
		} catch (JSONException e) {
			return "[ERROR: JSONException = " + e.toString() + "]";
		} catch (Throwable e) {
			return "[ERROR: Unknown exception = " + e.toString() + "]";
		}
	}
	
	/* 
	 * Register new user if user with that name is not registered yet.
	 * Return user internal id (-1 if failed) and command status.
	 * 
	 * command format:
	 * 		registerUser name:[userName] password:[userPassword]
	 * answer format:
	 *		userOperation state:[failed/success] userId:[userId] 
	 */
	private class RegisterUserCommand implements CommandProcessor {
		public JSONObject Process(String commandName, JSONObject command) throws JSONException {
			String newUserName 		= command.getString("name");
			String newUserPassword 	= command.getString("password");
			
			JSONObject answer = new JSONObject();
			
			long userId = dbManager.CreateUser(newUserName, newUserPassword);
			
			answer.put("userId", userId);
			if(userId == -1) answer.put("state", "failed");
			else 			 answer.put("state", "success");
			
			return answer;
		}
	}
	
	
	/* 
	 * Get user internal ID.
	 * 
	 * command format:
	 * 		getUserId name:[userName] password:[userPassword]
	 * answer format:
	 *		userOperation state:[failed/success] userId:[userId]
	 */
	private class GetUserCommand implements CommandProcessor {
		public JSONObject Process(String commandName, JSONObject command) throws JSONException {
			String userName 		= command.getString("name");
			String userPassword 	= command.getString("password");
			
			JSONObject answer = new JSONObject();
			
			long userId = dbManager.IsUserPasswordIsCorrect(userName, userPassword);
			
			answer.put("userId", userId);
			if(userId == -1) answer.put("state", "failed");
			else 			 answer.put("state", "success");
			
			return answer;
		}
	}
	
	
	/* 
	 * Change user password.
	 * 
	 * command format:
	 * 		changeUserPassword userId:[userName] oldPassword:[oldPassword] newPassword:[newPassword]
	 * answer format:
	 *		userOperation state:[failed/success] userId:[userId]
	 */
	private class ChangeUserPasswordCommand implements CommandProcessor {
		public JSONObject Process(String commandName, JSONObject command) throws JSONException {
			long   userId 			= command.getLong("userId");
			String oldPassword 		= command.getString("oldPassword");
			String newPassword 		= command.getString("newPassword");
			
			JSONObject answer = new JSONObject();
			
			long retId = dbManager.ChangeUserPassword(userId, oldPassword, newPassword);
			
			answer.put("userId", retId);
			if(retId == -1)  answer.put("state", "failed");
			else 			 answer.put("state", "success");
			
			return answer;
		}
	}
	
	
	/* 
	 * Create new group.
	 * Return group internal id (-1 if failed) and command status.
	 * 
	 * command format:
	 * 		createGroup name:[userName] viewPassword:[viewPassword] editPassword:[editPassword]
	 * answer format:
	 *		groupOperation state:[failed/success] groupId:[userId] 
	 */
	private class RegisterGroupCommand implements CommandProcessor {
		public JSONObject Process(String commandName, JSONObject command) throws JSONException {
			String newGroupName 			= command.getString("name");
			String newGroupViewPassword 	= command.getString("viewPassword");
			String newGroupEditPassword 	= command.getString("editPassword");
			
			JSONObject answer = new JSONObject();
			
			long groupId = dbManager.CreateGroup(newGroupName, newGroupViewPassword, newGroupEditPassword);
			
			answer.put("groupId", groupId);
			if(groupId == -1) 	answer.put("state", "failed");
			else 			 	answer.put("state", "success");
			
			return answer;
		}
	}
	
	
	/* 
	 * Get groups list.
	 * Return the list of groups.
	 * 
	 * TODO Add to group additional information
	 * TODO Add support of the interval list
	 * 
	 * command format:
	 * 		groupsList listType:[all/interval] [firstIndex:[long] lastIndex:[long]]
	 * answer format:
	 *		groupsListArray count:[long] list:{[name visibleToAll groupId], [ -//- ], ...} 
	 */
	private class GroupsListCommand implements CommandProcessor {
		public JSONObject Process(String commandName, JSONObject command) throws JSONException {
			String listType = command.getString("listType");
			
			Vector<ScheduleDbManager.GroupUnit> v = dbManager.GetAllGroups();
			
			JSONObject answer = new JSONObject();
			answer.put("count", v.size());
			JSONArray groupsArray = new JSONArray();
			
			for (ScheduleDbManager.GroupUnit gr : v) {
				JSONObject grObj = new JSONObject();
				grObj.put("name", 		  gr.name);
				grObj.put("visibleToAll", gr.visibleToAll);
				grObj.put("groupId", 	  gr.id);
				groupsArray.put(grObj);
			}
			
			answer.put("list", groupsArray);
			return answer;
		}
	}
	
}
