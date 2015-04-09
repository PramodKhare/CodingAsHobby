package com.neu.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

public class IOServer implements Runnable{
	private int splitIndex;
	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;
	private FileSplitBuffer buffer;
	public IOServer(int splitIndex, Socket socket) {
		this.splitIndex = splitIndex;
		this.socket = socket;
		this.buffer = FileSplitServer.getFileSplitBuffer();
	} 
	
	public void run() {
		Gson gson = new Gson();
		try {
			System.out.println("IORequest from: "+socket.getPort());
			out = new PrintWriter(socket.getOutputStream(), true);
		    in = new BufferedReader(
		        new InputStreamReader(
		            socket.getInputStream()));
		    out.println(splitIndex);
		    String line;
			while((line = in.readLine()) != null) {
				if(line.equals("next")) {
					try {
						System.out.println("io requested");
						out.println(gson.toJson(buffer.getNext(splitIndex)));
					}catch (Exception e) {
						out.println("ERROR: "+e.getMessage());
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
