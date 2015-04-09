package com.neu.io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class FileSplitServer implements Runnable{
	public static volatile int splitIndex = 0;
	private static FileSplitServer server;
	private static FileSplitBuffer buffer;
	private FileSplitServer(String inFile, int numSplits) throws IOException {
		buffer = new FileSplitBuffer(inFile, numSplits);
	}
	
	public void run() {
		try {
			ServerSocket socket = new ServerSocket(2122);
			System.out.println("Opened IOServer connection:"+socket.getLocalPort());
			while(!isInterrupted()) {
				new Thread(new IOServer(splitIndex++, socket.accept())).start();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	public boolean isInterrupted() {
		// TODO handle soft interrupts
		return false;
	}
	
	public static FileSplitServer startFileSplitServer(String inFile, int numSplits) throws IOException {
		if(server == null) {
			server = new FileSplitServer(inFile, numSplits);
			new Thread(server).start();
		}
		return server;
	}
	
	public static FileSplitBuffer getFileSplitBuffer() {
		if(server == null) {
			throw new Error("No FileSplitServer instance found!");
		}
		return buffer;
	}
}
