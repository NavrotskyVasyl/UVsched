package com.example.testwebserver;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements ClientMsgsSender.ServerAnswerReceiver {

	Button btnStart, btnStop, btnDbg1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnStart = (Button)findViewById(R.id.buttonStart);
		btnStop  = (Button)findViewById(R.id.buttonStop);
		btnDbg1  = (Button)findViewById(R.id.buttonDbg1);
		
		OnClickListener oclBtnStart = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickStart(v);
			}
		};
		
		OnClickListener oclBtnStop = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickStop(v);
			}
		};
		
		OnClickListener oclBtnDbg = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickDbgButton(v);
			}
		};
		
		btnStart.setOnClickListener(oclBtnStart);
		btnStop.setOnClickListener(oclBtnStop);
		btnDbg1.setOnClickListener(oclBtnDbg);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClickStart(View v) {
		startService(new Intent(this, ScheduleServerService.class));
    }
	
	public void onClickStop(View v) {
		stopService(new Intent(this, ScheduleServerService.class));
    }
	
	public void onClickDbgButton(View v) {
		//ClientMsgsSender sender = new ClientMsgsSender("localhost", 8080); // "127.0.0.1"
		//sender.SendMessage("{ \"version\": 1, \"userId\": 2, \"userPassword\": \"pass\", \"command\" : { " + 
		//		"\"cname\": \"createGroup\", \"name\":\"Second hiden group\", \"viewPassword\":\"pass\", \"editPassword\":\"pass\" } }", this);
		
		Random r = new Random();
		
		for(int i=0; i<20; ++i) {
			String pass = (r.nextBoolean() ? "pass" : "");
			String name = "Test group #" + Integer.toString(i);
			
			ClientMsgsSender sender = new ClientMsgsSender("localhost", 8080); // "127.0.0.1"
			sender.SendMessage("{ \"version\": 1, \"userId\": 2, \"userPassword\": \"mypwd\", \"command\" : { " + 
					"\"cname\": \"createGroup\", \"name\":\"" + name + "\", \"viewPassword\":\"" + pass + "\", \"editPassword\":\"pass\" } }", this);
		}
    }

	@Override
	public void ProcessServerAnswer(String msgString, String answerString, String serverAdress, int serverPort) {
		// TODO Auto-generated method stub
		Log.d("TestWebServer", "Server answer received!");
		Log.d("TestWebServer", "Client meggage was " + msgString);
		Log.d("TestWebServer", "Server answer is " + answerString);
	}

}
