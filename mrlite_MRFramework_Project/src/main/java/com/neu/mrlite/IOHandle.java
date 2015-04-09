package com.neu.mrlite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import com.google.gson.Gson;

/**
 * Input and output handle for reading and writing from parallel workers.
 * This will also abstract the in-memory executions from mapper and reducer.
 * Map and reduce will perform as they read and wrote to HDFS, but it may
 * not be the case always and it completely depends on the implementations
 * of this class.
 *
 * @author nikit
 *
 */
public class IOHandle {
	/**
	 * I/O Handles
	 */
	private static String inFile;
	private static String outDir;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	private String[] inBlock;
	private int splitIndex;
	private Gson gson;
	private int nextLine;
	/**
	 * Input file path and output directory are both HDFS path in future scope 
	 * @param inFile
	 * @param outDir
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException 
	 */
	public IOHandle(String inFile, String outDir) throws FileNotFoundException, UnsupportedEncodingException {
		IOHandle.inFile = inFile;
		IOHandle.outDir = outDir;
		getIOHandle();
	}
	
	public IOHandle(IOHandle io) throws FileNotFoundException, UnsupportedEncodingException {
		getIOHandle();
	}
	
	private void getIOHandle() throws FileNotFoundException {
		gson = new Gson();
		try {
			nextLine = 0;
			inBlock = new String[] {};
			socket = new Socket(Constants.IP, 2122);
		    out =
		        new PrintWriter(socket.getOutputStream(), true);
		    in = new BufferedReader(
		    		new InputStreamReader(socket.getInputStream()));
		    String line = in.readLine();
		    if(line != null) {
		    	try {
		    		System.out.println(splitIndex);
		    		splitIndex = Integer.parseInt(line);
		    	} catch(Exception e) {
		    		System.out.println(e.getMessage());
		    	}
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Close the I/O Handles safely
	 */
	public void close() {
		try {
			socket.close();
		} catch(Exception e) {
			System.out.println("error @ ioHandle.close()");
		}
	}
	
	/**
	 * Read the file line by line, It is just a wrapper function
	 * @return
	 * @throws Exception 
	 */
	public String readLine() throws Exception {
		try {
			if(inBlock.length == 0) {
				out.println("next");
				String line = in.readLine();
				if(line != null) {
					inBlock = gson.fromJson(line, String[].class);
					nextLine = 0;
				} else {
					return null;
				}
			}
			if(nextLine < inBlock.length)
				return inBlock[nextLine++];
			else {
				inBlock = new String[] {};
				return readLine();
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * write the output object to a file
	 * @param o
	 */
	public void write(Object o) {
		
	}
}