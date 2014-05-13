package com.example.testwebserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ScheduleServerService extends Service {

	ScheduleServer server;
	
	public void onCreate() {
		super.onCreate();
		server = new ScheduleServer();
		Log.d("TestWebServer", "onCreate");
	}
		  
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("TestWebServer", "onStartCommand");
		someTask();
		return super.onStartCommand(intent, flags, startId);
	}
			
	public void onDestroy() {
		try {
			server.Close();
		} catch (Throwable t) {
	        Log.e("TestWebServer", t.getMessage());
	    }
		
		super.onDestroy();
		Log.d("TestWebServer", "onDestroy");
	}
		
	public IBinder onBind(Intent intent) {
	    Log.d("TestWebServer", "onBind");
	    return null;
	}
		  
	void someTask() {
		try {
			server.Open(this);
		} catch (Throwable t) {
	        Log.e("TestWebServer", t.getMessage());
	    }
	}

}
