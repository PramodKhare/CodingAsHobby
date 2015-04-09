package com.neu.mrlite;

public class MRLiteBoot {
	public static void startMaster() {
		JobServer.startJobServer();
	}
	
	public static void startClient(String ip) {
		Constants.IP = ip;
		JobClient.startJobClient(ip);
	}
	
	public static void usageAndExit() {
		System.out.println("usage: java jar <mrlite-*-SNAPSHOT.jar> -[server, client] [client<MasterIP>]");
		System.exit(1);
	}
	
	public static void main(String args[]) {
		if(args.length == 0) {
			usageAndExit();
		}
		if (args[0].equals("-server"))
			startMaster();
		else if (args[0].equals("-client") && args.length == 2)
			startClient(args[1]);
		else
			usageAndExit();
	}
}
