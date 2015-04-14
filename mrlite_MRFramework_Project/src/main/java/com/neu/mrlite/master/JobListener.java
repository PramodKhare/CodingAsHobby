package com.neu.mrlite.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

public class JobListener implements Runnable {
    private boolean intr = false;
    private static JobListener jobListenerSingleton;
    private ServerSocket listenerSocket;

    private JobListener() throws IOException {
        intr = false;
        listenerSocket = new ServerSocket(2121);
    }

    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        Socket client = null;
        try {
            System.out.println("Listening for jobs on port :"
                    + listenerSocket.getLocalPort());
            while (!isInterrupted()) {
                client = listenerSocket.accept();
                System.out.println("request connected:" + client.getPort());
                in = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);
                String line;
                while ((line = in.readLine().trim()) != null) {
                    try {
                        // this is serialized JobConf object, deserialize it
                        String[] cmd = line.split("\\s+");
                        if (cmd.length > 3) {
                            // execute the main class - main method directly from here
                            loadAndExecuteMainClass(cmd);
                            out.println("Job scheduled successfully!");
                        } else
                            out.println("ERROR: Invalid job command!\nUSAGE: mrlite -jar <jar_name> <main_class> <input_file_path> <output_directory_path>");
                    } /*catch (final IllegalStateException e) {
                        e.printStackTrace();
                        out.println("JobQueue is full, can't schedule the job now, try again");
                      } */catch (final Exception e) {
                        e.printStackTrace();
                        out.println(e.getMessage());
                        System.out
                                .println("Invalid job configuration provided, please provide valid JobConf object");
                    }
                }
                out.close();
                in.close();
                client.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (client != null) {
                    client.close();
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Execute the static main method of main-class of MR job executable jar
     * which will do the scheduling of current JobConf object
     * 
     * @param args
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void loadAndExecuteMainClass(String[] args)
            throws MalformedURLException, ClassNotFoundException,
            NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        // load user's executable jar
        File file = new File(args[2]);
        URL url = file.toURI().toURL();
        URL[] urls = new URL[] { url };
        ClassLoader cl = new URLClassLoader(urls);

        // Load Mapper Class
        Class<?> cls = cl.loadClass(args[3]);

        // Copy the Input String Arguments and execute the main method
        String argsForMainClass[] = new String[(args.length - 4)];
        System.arraycopy(args, 4, argsForMainClass, 0, (args.length - 4));

        // Get handle of run method using reflection
        Method runMethod = cls.getMethod("main", String[].class);

        // Invoke run method
        runMethod.invoke(null, (Object) argsForMainClass);
    }

    public void interrupt() {
        intr = true;
    }

    public boolean isInterrupted() {
        return intr == true;
    }

    public static void startJobListener() throws IOException {
        if (jobListenerSingleton == null) {
            jobListenerSingleton = new JobListener();
            new Thread(jobListenerSingleton).start();
        }
    }

    public static void stopJobListener() throws IOException {
        if (jobListenerSingleton != null) {
            jobListenerSingleton.interrupt();
            if (jobListenerSingleton.listenerSocket != null
                    && !jobListenerSingleton.listenerSocket.isClosed()) {
                jobListenerSingleton.listenerSocket.close();
            }
            jobListenerSingleton = null;
        }
    }
}
