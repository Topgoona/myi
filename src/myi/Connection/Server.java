package myi.Connection;

import java.io.*;
import java.net.*;

import myi.Main.CreateGui;
import myi.Stream.CreateStreamContainer;

public class Server implements Runnable {
	
	private static int port = 8080;
    private static Socket clientsocket = null;
    private static ServerSocket serversocket = null;
    private static boolean connected = false;
    private OutputStream out;

	@Override
	public void run() {
		connected = false;
		try {
			serversocket = new ServerSocket(port);
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		while(!connected){
			try {
				
				//Accept incoming call
				clientsocket = serversocket.accept();
				
				//Configure input/output streams
				out = clientsocket.getOutputStream();
				
				//Acknowledge connection
				connected = true;

				//Create new container object
				
        		new CreateStreamContainer(out);

			}
			catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}	
		}
	}
	//Close server
	public static void closeServer(){
		try {
			clientsocket.close();
			serversocket.close();
			connected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

