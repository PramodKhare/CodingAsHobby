package com.neu.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.NoSuchElementException;

/**
 * Provides simple access to a file split.  Allows clients to retrieve the next block of
 * lines.  Starts a separate thread for reading the split and buffering the lines in
 * to a queue.
 * @author ryanmillay
 *
 */
public class SplitQueue {
	private SplitQueueReader reader;
	private boolean empty = false;
	public static int lines_per_block = 5;
	public static final String POISON_PILL = "__END_OF_SPLIT__"; 
	
	/**
	 * Constructor.  Sets the file and the offsets in bytes.  Starts a stand alone
	 * reader thread for retrieving lines on demand.
	 * @param file	File to process for this job
	 * @param start	byte offset to begin processing
	 * @param end	byte offset to finish processing
	 * @throws IOException 
	 */
	public SplitQueue(String filePath, long start, long end) throws IOException {
		reader = new SplitQueueReader(filePath, start, end);
		Thread tReader = new Thread(reader);
		tReader.start();
	}
	
	/**
	 * Constructor.  Sets the file, the offsets in bytes, and the block size in lines.  
	 * Starts a stand alone reader thread for retrieving lines on demand.
	 * @param filePath	File to process for this job
	 * @param start	byte offset to begin processing
	 * @param end	byte offset to finish processing
	 * @param blockSize	number of lines for a given block read
	 * @throws IOException
	 */
	public SplitQueue(String filePath, long start, long end, int blockSize) throws IOException {
		lines_per_block = blockSize;
		reader = new SplitQueueReader(filePath, start, end);
		Thread tReader = new Thread(reader);
		tReader.start();
	}
	
	/**
	 * Retrieve the next block of lines to send to a client.  If the split is empty, throw
	 * a NoSuchElement exception.
	 * @return	String[] of lines
	 * @throws NoSuchElementException
	 */
	public String[] readBlock() throws NoSuchElementException {
		if(empty) {
			throw new NoSuchElementException("Reached end of split!");
		}
		
		String[] block = new String[lines_per_block];
		for(int i = 0; i < lines_per_block; i++) {
			try {
				String line = reader.readLine();
				if(line.equals(POISON_PILL)) {
					empty = true;
					break;
				}
				block[i] = line;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return block;
	}
	
	/**
	 * Has the end of the split been reached?
	 * @return	true or false
	 */
	public boolean isEmpty() {
		return empty;
	}
}
