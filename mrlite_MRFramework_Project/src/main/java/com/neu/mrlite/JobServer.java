package com.neu.mrlite;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.ArrayList;

import com.neu.io.FileSplitServer;

public class JobServer implements Runnable {
	private static final int SIGTERM = 30;
	int intr = 0;
	List<JobServlet> servlets;
	private static JobServer jobServer = null;
	private JobServer() {
		servlets = new ArrayList<JobServlet>();
	}
	
	public void run() {
		try
		{
			ServerSocket socket = new ServerSocket(2120);
			System.out.println("Opened server connection:"+socket.getLocalPort());
			while(!isInterrupted()) {
				if(servlets.size() == Constants.NODES)
				{
					boolean fin = true;
					for(JobServlet job: servlets) {
						fin = fin && job.isInterrupted();
					}
					if(fin)
						this.interrupt(SIGTERM);
				} else {
					JobServlet servlet = new JobServlet(socket.accept());
					servlets.add(servlet);
					new Thread(servlet).start();
				}
			}
			System.out.println("Closed server connection:"+socket.getLocalPort());
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void publish(String cmd) {
		for(JobServlet job: servlets) {
			job.publish(cmd);
		}
	}
	
	public boolean isInterrupted() {
		return (intr == 30);
	}
	
	public void interrupt(int SIGNAL) {
		intr = SIGNAL;
	}
	
	public static JobServer startJobServer() {
		if(jobServer == null) {
			jobServer = new JobServer(); 
			new Thread(jobServer).start();
		}
		JobListener.start();
		return jobServer;
	}
	
	public static void publishJob(String cmd) {
		if(jobServer == null) {
			startJobServer();
		}
		jobServer.publish(cmd);
	}
}
