package com.example.schedulerclient;

import org.json.JSONException;
import org.json.JSONObject;

public class CommandConstructor {
	private static final long protocolVersion = 1;
	
	private static String getUserCommand(String commandName, String userName, String userPassword) {
		try {
			JSONObject fullCommand = new JSONObject();
			fullCommand.put("version", 			protocolVersion);
			fullCommand.put("userId", 			-1);
			fullCommand.put("userPassword", 	userPassword);
			
			JSONObject command = new JSONObject();
			command.put("cname", 		commandName);
			command.put("name", 		userName);
			command.put("password", 	userPassword);
	
			fullCommand.put("command", command);
			return fullCommand.toString();
		} catch (JSONException e) {
			return "[getLoginCommand catch JSONException exception = " + e.toString() + "]";
		}
	}
	
	public static String getLoginCommand(String userName, String userPassword) {
		return getUserCommand("getUserId", userName, userPassword);
	}
	
	
	public static String getRegisterCommand(String userName, String userPassword) {
		return getUserCommand("registerUser", userName, userPassword);
	}
	
	public static String getAllGroupsListCommand(long userId, String userPassword) {
		try {
			JSONObject fullCommand = new JSONObject();
			fullCommand.put("version", 			protocolVersion);
			fullCommand.put("userId", 			userId);
			fullCommand.put("userPassword", 	userPassword);
			
			JSONObject command = new JSONObject();
			command.put("cname", 		"groupsList");
			command.put("listType", 	"all");
	
			fullCommand.put("command", command);
			return fullCommand.toString();
		} catch (JSONException e) {
			return "[getLoginCommand catch JSONException exception = " + e.toString() + "]";
		}
	}
	
	public static String getCreateGroupCommand(String newGroupName, 
			String newGroupViewPassword, 
			String newGroupEditPassword, 
			long userId, 
			String userPassword) {
		try {
			JSONObject fullCommand = new JSONObject();
			fullCommand.put("version", 			protocolVersion);
			fullCommand.put("userId", 			userId);
			fullCommand.put("userPassword", 	userPassword);
			
			JSONObject command = new JSONObject();
			command.put("cname", 		"createGroup");
			command.put("name", 		newGroupName);
			command.put("viewPassword", newGroupViewPassword);
			command.put("editPassword", newGroupEditPassword);
	
			fullCommand.put("command", command);
			return fullCommand.toString();
		} catch (JSONException e) {
			return "[getLoginCommand catch JSONException exception = " + e.toString() + "]";
		}
	}
}
