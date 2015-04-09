package com.neu.mrlite;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class JobClient implements Runnable{
	
	static List<Writable> outVal = null;
	
	private PrintWriter out;
	private BufferedReader in;
	private String masterIp;
	public JobClient(String masterIp) {
		this.masterIp = masterIp;
	}
	
	@Override
	public void run() {
		try {
			Socket socket = new Socket(masterIp, 2120);
		    out =
		        new PrintWriter(socket.getOutputStream(), true);
		    in = new BufferedReader(
		    		new InputStreamReader(socket.getInputStream()));
			String line;
			while((line = in.readLine()) != null) {
				String args[] = line.split("\\s+");
				File file  = new File(args[0]);
				URL url = file.toURI().toURL();
				URL[] urls = new URL[]{url};
				ClassLoader cl = new URLClassLoader(urls);
				Class<?> cls = cl.loadClass(args[1]);
				Method[] m = cls.getDeclaredMethods();
				
				for(Method method: m) {
					if(method.getName().equals("run")) {
						Object o = method.invoke(null, args[2], args[3]);
						execute(Assortment.getExecChain(), Assortment.getIOHandle());
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void execute(List<POCallback> exec, IOHandle io) {
		Object outKey;
		Gson gson = new Gson();
		for(POCallback p: exec) {
			System.out.println(p);
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
	}
	
	public static void startJobClient(String ip) {
		new Thread(new JobClient(ip)).start();
	}
}
