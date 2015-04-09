package com.neu.mrlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class ParallelOperation implements Runnable{
	private static final int SIGTERM = 30;
	private IOHandle io;
	private List<POCallback> callbacks;
	public ParallelOperation(IOHandle io, List<POCallback> callbacks) {
		this.io = io;
		this.callbacks = callbacks;
	}
	
	static List<Writable> outVal = null;
	
	@SuppressWarnings("unchecked")
	public void run() {
		Gson gson = new Gson();
		try {
			Socket socket = new Socket("192.168.1.9", 2120);
		    PrintWriter out =
		        new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(
		        new InputStreamReader(socket.getInputStream()));
			
			System.out.println("Opened connection for:"+socket.getLocalPort());
			Object outKey = null;
			for(POCallback p: callbacks) {
				if(p instanceof IOCallback) {
					((IOCallback) p).process(io);
					outVal = (List<Writable>) p.getValue();
					outKey = p.getKey();
					continue;
				} else {
					List<Writable> interVal = new ArrayList<Writable>();
					for(Writable val : outVal) {
						p.process(val);
						if(p.getKey() != null && p.getValue() != null) {
							interVal.add(new Writable(new Pair<Object, Object>(p.getKey(), 
								p.getValue())));
						} else {
							if(p.getValue() != null)
								interVal.add(new Writable(p.getValue()));
						}
					}
					outVal = interVal;
				}
			}
			out.println(gson.toJson(outVal));
			out.println(SIGTERM);
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}