package com.example.testwebserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.util.Log;

public class ScheduleServer {
	Thread serverThread;
	ServerSocket ss;
	ScheduleServerProcessor processor;
	ScheduleDbManager		dbManager;
	
	public void Open(final Context context) throws Throwable {
		serverThread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		        try {
		        	dbManager = new ScheduleDbManager(context);
		        	processor = new ScheduleServerProcessor(dbManager);
		        	ss = new ServerSocket(8080);
		        	//ss.setSoTimeout(1000);
		            while (true) {
		                Socket s = ss.accept();
		                Log.i("TestWebServer", "Client accepted");
		                new Thread(new SocketProcessor(s, processor)).start();
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        } catch (Throwable t) {
		        	t.printStackTrace();
		        }
		    }
		});

		serverThread.start(); 
	}
   
	public void Close() throws Throwable {
		ss.close();
	}

	private static class SocketProcessor implements Runnable {
		private Socket s;
		private InputStream is;
		private OutputStream os;
		ScheduleServerProcessor processor;

		private SocketProcessor(Socket s, ScheduleServerProcessor processor) throws Throwable {
			this.s = s;
			this.is = s.getInputStream();
			this.os = s.getOutputStream();
			this.processor = processor;
		}

		public void run() {
			try {
				String clientMessage = readClientMsg();
				String respound = processor.ProcessMsg(clientMessage);
				writeResponse(respound);
				
				Log.i("TestWebServer", "Client msg = " + clientMessage);
				Log.i("TestWebServer", "Server answ = " + respound);
			} catch (Throwable t) {
				/* do nothing */
			} finally {
				try {
					s.close();
				} catch (Throwable t) {
					/*do nothing*/
				}
			}
			Log.i("TestWebServer", "Client processing finished");
		}

		private void writeResponse(String s) throws Throwable {
			String title = ""; // TODO Add answer title
			String result = title + s + "\r\n\r\n\n";
			os.write(result.getBytes());
			os.flush();
		}

		private String readClientMsg() throws Throwable {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String res = "";
			while(true) {
				String s = br.readLine();
				if(s == null || s.trim().length() == 0) {
					break;
				}
				res += s;
			}
			return res;
		}
	}
}
