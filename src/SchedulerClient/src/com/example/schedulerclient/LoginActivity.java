package com.example.schedulerclient;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ScrollView;

public class LoginActivity extends ActionBarActivity implements OnClickListener, ClientMsgsSender.ServerAnswerReceiver {

	ClientMsgsSender sender;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login);
		
		sender = ServerInfo.CreateStdMsgsSender(this);//new ClientMsgsSender("localhost", 8080);
		
		setTitle(R.string.loginTitle);
		findViewById(R.id.buttonLogin).setOnClickListener(this);
		findViewById(R.id.buttonRegisterUser).setOnClickListener(this);
		findViewById(R.id.progressBarWait).setVisibility(View.GONE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
	public void onClick(View v) {
		// get login and password
		String login 	= ((EditText)findViewById(R.id.editUserLogin)).getText().toString();
		String password = ((EditText)findViewById(R.id.editUserPassword)).getText().toString();
		
		((ScrollView)findViewById(R.id.scrollView1)).setVisibility(View.GONE);
		findViewById(R.id.progressBarWait).setVisibility(View.VISIBLE);
		
		Bundle params = new Bundle();
		params.putString("password", password);
		
		switch(v.getId()) {
		case R.id.buttonLogin:
			sender.SendMessage(CommandConstructor.getLoginCommand(login, password), this, params);
			break;
		case R.id.buttonRegisterUser:
			sender.SendMessage(CommandConstructor.getRegisterCommand(login, password), this, params);
			break;
			default: break;
		}
	}

	public final static String SAVED_USER_ID   = "com.example.schedulerclient.SAVED_USER_ID";
	public final static String SAVED_USER_PASS = "com.example.schedulerclient.SAVED_USER_PASSWORD";
	
	@Override
	public void ProcessServerAnswer(String msgString, String answerString,
			String serverAdress, int serverPort, Bundle params) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

		((ScrollView)findViewById(R.id.scrollView1)).setVisibility(View.VISIBLE);
		findViewById(R.id.progressBarWait).setVisibility(View.GONE);
		
		long   userId = -1;
		String passwd = params.getString("password");
		
		// get user id and password
		try {
			JSONObject object = (JSONObject) new JSONTokener(answerString).nextValue();
			JSONObject answ = object.getJSONObject("answer");
			userId = answ.getLong("userId");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(userId == -1) {
			dlgAlert.setMessage(getResources().getString(R.string.login_alert_text));
			dlgAlert.setTitle(getResources().getString(R.string.login_alert_title));
			dlgAlert.setPositiveButton("OK", 
				new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            //dismiss the dialog  
		          }
		      });
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
			
			return;
		}
		
		// intent groups activity
		Intent intent = new Intent(this, GroupsActivity.class);
		intent.putExtra(SAVED_USER_ID,  	userId);
		intent.putExtra(SAVED_USER_PASS, 	passwd);
		startActivity(intent);
	}

}
