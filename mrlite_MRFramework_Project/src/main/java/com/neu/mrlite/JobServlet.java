package com.neu.mrlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class JobServlet implements Runnable{
	private static final String TERMINATE = "30";
	private static final int SIGTERM = 30;
	private Socket socket = null;
	private int intr = 0;
	private PrintWriter out;
	private BufferedReader in;
	public JobServlet (Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
	            String inputLine;
	            System.out.println("Started connection from:"+socket.getPort());
	            while ((inputLine = in.readLine()) != null) {
	            	if(inputLine.equals(TERMINATE)) {
	            		this.intr = SIGTERM;
	            		break;
	            	}
	            	System.out.println(inputLine);
	            }
	            System.out.println("Closed connection from:"+socket.getPort());
	            socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void publish(String cmd) {
		out.println(cmd);
	}
	
	public boolean isInterrupted() {
		return intr == 30;
	}
}
