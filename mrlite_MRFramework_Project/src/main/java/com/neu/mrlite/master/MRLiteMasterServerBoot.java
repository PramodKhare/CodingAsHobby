package com.neu.mrlite.master;

/**
 * Bootstrapping the Whole System of JobServer
 * 
 * @author Nikit Waghela
 */
public class MRLiteMasterServerBoot {
    public static void main(String args[]) throws Exception {
        JobServer.startJobServer();
    }
}
