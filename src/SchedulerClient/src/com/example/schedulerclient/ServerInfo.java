package com.example.schedulerclient;

import android.content.Context;
import android.content.SharedPreferences;

public class ServerInfo {
	private static final String SERVER_CONFIG_FILE = "ServerPrefs";
	
	public static void SetupServer (Context context, String newAdress, long newPort) {
		SharedPreferences settings = context.getSharedPreferences(SERVER_CONFIG_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("serverAdress", newAdress);
		editor.putLong("serverPort", newPort);
		editor.commit();
	}
	
	public static String GetServerAdress(Context context) {
		SharedPreferences settings = context.getSharedPreferences(SERVER_CONFIG_FILE, 0);
		return settings.getString("serverAdress", "localhost");
	}
	
	public static long GetServerPort(Context context) {
		SharedPreferences settings = context.getSharedPreferences(SERVER_CONFIG_FILE, 0);
		return settings.getLong("serverPort", 8080);
	}
	
	public static ClientMsgsSender CreateStdMsgsSender(Context context) {
		return new ClientMsgsSender(GetServerAdress(context), (int)GetServerPort(context));
	}
}
