package com.neu.mrlite;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.neu.io.FileSplitServer;

public class JobListener implements Runnable{
	private int intr = 0;
	private static JobListener jobListener;
	
	private JobListener() {
		intr = 0;
	}
	
	public void run () {
		try {
			ServerSocket socket = new ServerSocket(2121);
			System.out.println("Opened JobListener connection:"+socket.getLocalPort());
			while(!isInterrupted()) {
				Socket client = socket.accept();
				System.out.println("request connected:"+client.getPort());
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				String line;
				while((line = in.readLine()) != null) {
					String[] cmd = line.split("\\s+");
					if(cmd.length > 3) {
						FileSplitServer.startFileSplitServer(cmd[2], Constants.NODES);
						JobServer.publishJob(line);
						out.println("jar to run: "+line);
					} else
						out.println("ERROR: invalid job command!\n USAGE: <JAR> <MAINCLASS> <INFILE> <OUTDIR>");
				}
				client.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void interrupt(int SIGNAL) {
		intr = SIGNAL;
	}
	
	public boolean isInterrupted() {
		return intr == 30;
	}
	
	public static JobListener start() {
		if(jobListener == null) {
			jobListener = new JobListener();
			new Thread(jobListener).start();
		}
		return jobListener;
	}
}
