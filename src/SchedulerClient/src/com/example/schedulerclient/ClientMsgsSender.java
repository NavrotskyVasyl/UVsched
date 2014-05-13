package com.example.schedulerclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.os.Bundle;

public class ClientMsgsSender {
	private String 	serverAdress;
	private int 	serverPort;
	
	public ClientMsgsSender(String adress, int port) {
		serverAdress = adress;
		serverPort	 = port;
	}
	
	public interface ServerAnswerReceiver {
		public void ProcessServerAnswer(String msgString, String answerString, String serverAdress, int serverPort, Bundle params);
	}
	
	public void SendMessage(String msgString, ServerAnswerReceiver receiver, Bundle params) {
		ClientSendAnsReceiveAnswerTask myClientTask = new ClientSendAnsReceiveAnswerTask(
				serverAdress, 
				serverPort,
				msgString,
				receiver,
				params);
		myClientTask.execute();
	}
	
	public class ClientSendAnsReceiveAnswerTask extends AsyncTask<Void, Void, Void> {
		private String dstAddress;
		private int dstPort;
		private String response = "";
		private String msg;
		private ServerAnswerReceiver receiverObj;
		Bundle params;
		
		private OutputStream os;
		private InputStream  is;
		  
		ClientSendAnsReceiveAnswerTask(String addr, int port, String msgString, ServerAnswerReceiver receiver, Bundle params) {
			dstAddress = addr;
			dstPort = port;
			
			msg = msgString;
			receiverObj = receiver;
			this.params = params;
		}
		
		protected void SendMsg() throws IOException {
			os.write((msg + "\n\n").getBytes());
			//os.write(0);
			os.flush();
		}
		
		protected void ReceiveAnswer() throws UnknownHostException, IOException {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String res = "";
			while(true) {
				String s = br.readLine();
				if(s == null) { // s.trim().length() == 0
					break;
				}
				res += s;
			}
			response = res;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
		   Socket socket = null;
		   
		   try {
			   socket = new Socket(dstAddress, dstPort);
			   this.is = socket.getInputStream();
	           this.os = socket.getOutputStream();
			   
	           SendMsg();
	           ReceiveAnswer();
		
		   	} catch (UnknownHostException e) {
		   		// TODO Auto-generated catch block
		   		e.printStackTrace();
		   		response = "UnknownHostException: " + e.toString();
		   	} catch (IOException e) {
		   		// TODO Auto-generated catch block
		   		e.printStackTrace();
		   		response = "IOException: " + e.toString();
		   	} finally {
		   		if(socket != null) {
		   			try {
		   				socket.close();
		   			} catch (IOException e) {
		   				// TODO Auto-generated catch block
		   				e.printStackTrace();
		   			}
		   		}
		   	}
		   	return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			receiverObj.ProcessServerAnswer(msg, response, serverAdress, serverPort, params);
			super.onPostExecute(result);
		}
		  
	}

}