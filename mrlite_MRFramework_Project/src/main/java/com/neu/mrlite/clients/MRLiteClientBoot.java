package com.neu.mrlite.clients;

import com.neu.mrlite.common.Constants;

public class MRLiteClientBoot {

    public static void startClient(String ip) {
        Constants.IP = ip;
        new JobClient(ip);
    }

    public static void usageAndExit() {
        System.out
                .println("usage: java jar <mrlite-*-SNAPSHOT.jar> <Master-IP>");
        System.exit(1);
    }

    public static void main(String args[]) {
        if (args.length != 1) {
            usageAndExit();
        }
        startClient(args[1]);
    }
}
