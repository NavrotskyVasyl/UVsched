package com.example.schedulerclient;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.os.Build;

public class CreateGroupActivity extends ActionBarActivity implements  OnClickListener, ClientMsgsSender.ServerAnswerReceiver {

	private long userId;
	private String userPassword;
	ClientMsgsSender sender;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_create_group);
		
		sender = ServerInfo.CreateStdMsgsSender(this);
		
		// Get the message from the intent
		// Read user password and id
	    Intent intent = getIntent();
	    userPassword = intent.getStringExtra(LoginActivity.SAVED_USER_PASS);
	    userId = intent.getLongExtra(LoginActivity.SAVED_USER_ID, -1);
		
		findViewById(R.id.buttonCreateGroup).setOnClickListener(this);
		//((ScrollView)findViewById(R.id.scrollViewCreateGroup)).setVisibility(View.GONE);
		findViewById(R.id.progressBarWaitCGr).setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_group, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void ProcessServerAnswer(String msgString, String answerString,
			String serverAdress, int serverPort, Bundle params) {
		finish();
	}

	@Override
	public void onClick(View v) {
		// get login and password
		String groupName 	= ((EditText)findViewById(R.id.editNewGroupName)).getText().toString();
		String groupVPass 	= ((EditText)findViewById(R.id.editNewGroupVPass)).getText().toString();
		String groupEPass 	= ((EditText)findViewById(R.id.editNewGroupEPass)).getText().toString();
		
		((ScrollView)findViewById(R.id.scrollViewCreateGroup)).setVisibility(View.GONE);
		findViewById(R.id.progressBarWaitCGr).setVisibility(View.VISIBLE);
				
		sender.SendMessage(CommandConstructor.getCreateGroupCommand(groupName, groupVPass, groupEPass, userId, userPassword), this, null);
	}

}
