package com.neu.mrlite.master;


public class MRLiteMasterServerBoot {
	public static void startMaster() {
		JobServer.startJobServer();
	}
	
	public static void usageAndExit() {
		System.out.println("usage: java jar <mrlite-*-SNAPSHOT.jar>");
		System.exit(1);
	}
	
	public static void main(String args[]) {
        /*
         * if(args.length == 0) { usageAndExit(); }
         */
        startMaster();
	}
}
