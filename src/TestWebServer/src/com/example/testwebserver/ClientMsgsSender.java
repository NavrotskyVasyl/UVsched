package com.example.testwebserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class ClientMsgsSender {
	private String 	serverAdress;
	private int 	serverPort;
	
	public ClientMsgsSender(String adress, int port) {
		serverAdress = adress;
		serverPort	 = port;
	}
	
	public interface ServerAnswerReceiver {
		public void ProcessServerAnswer(String msgString, String answerString, String serverAdress, int serverPort);
	}
	
	public void SendMessage(String msgString, ServerAnswerReceiver receiver) {
		ClientSendAndReceiveAnswerTask myClientTask = new ClientSendAndReceiveAnswerTask(
				serverAdress, 
				serverPort,
				msgString,
				receiver);
		myClientTask.execute();
	}
	
	public class ClientSendAndReceiveAnswerTask extends AsyncTask<Void, Void, Void> {
		private String dstAddress;
		private int dstPort;
		private String response = "";
		private String msg;
		private ServerAnswerReceiver receiverObj;
		
		private OutputStream os;
		private InputStream  is;
		  
		ClientSendAndReceiveAnswerTask(String addr, int port, String msgString, ServerAnswerReceiver receiver) {
			dstAddress = addr;
			dstPort = port;
			
			msg = msgString;
			receiverObj = receiver;
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
			receiverObj.ProcessServerAnswer(msg, response, serverAdress, serverPort);
			super.onPostExecute(result);
		}
		  
	}

}
