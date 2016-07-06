package myi.Connection;

import java.net.*;

import java.io.*;

import myi.Main.CreateGui;
import myi.Stream.DecodeAndPlayStream;

public class Client implements Runnable{
	
	private static int port = 8080;
	
	/////////////////////////////////////////////////
	//Different host settings dependent on location//
	/////////////////////////////////////////////////
	//public static String host = "192.168.1.13";//flat
	//public static String host = "192.168.1.3";//home
	public static String host = "192.168.1.2";
	
	private static Socket callsocket = null;
	private static boolean connected = false;

	public void run() {
		InputStream in = null;
		while(!connected){
			try {
				callsocket = new Socket(host, port);
				in = callsocket.getInputStream();
				connected = true;
				(new Thread(new DecodeAndPlayStream(in))).start();
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}
			//if(connected == true){
				//CreateGui.newCallAction.setEnabled(false);
				//CreateGui.acceptAction.setEnabled(true);
				//while(CreateGui.acceptAction.isEnabled()){
					//Dummy Loop
				//}
				//CreateGui.server.run();
				
			//}
		}
	}
	
	public static void closeClient(){
		try {
			callsocket.close();
			connected = false;
			
		} catch (IOException e) {
			System.out.println("Couldn't close client socket");
			e.printStackTrace();
		}
	}
}

