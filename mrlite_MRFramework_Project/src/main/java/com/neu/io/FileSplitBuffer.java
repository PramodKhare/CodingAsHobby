package com.neu.io;

import java.io.*;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * High level buffer for reading a given file for a MRLITE job.  The file is separated
 * in to a set of "splits" equal to the number of mappers.  Each split is represented by
 * a queue of lines.  The queue is optimistically populated as lines are retrieved.
 * @author ryanmillay
 *
 */
public class FileSplitBuffer {
	private String filePath;
	private RandomAccessFile input;
	private HashMap<Integer, SplitQueue> buffer;
	
	/**
	 * Initialize the FileSplitBuffer.  Requires a file path and number of splits you
	 * would like to separate this file in to.  Each split is handled separately by it's
	 * own SplitQueue.
	 * @param inputPath	String path to the file
	 * @param numSplits	Number of splits (i.e. number of Mappers)
	 * @throws IOException
	 */
	public FileSplitBuffer(String inputPath, int numSplits) throws IOException {
		filePath = inputPath;
		input = new RandomAccessFile(inputPath, "r");
		initializeBuffer(initializeOffsets(numSplits));
	}
	
	/**
	 * Configures the byte offset from the start of the file for each split.
	 * @param numSplits	Number of splits (i.e. number of Mappers)
	 * @throws IOException
	 */
	private long[] initializeOffsets(int numSplits) throws IOException {
		long[] splitOffsets = new long[numSplits];
		long splitSize = input.length()/numSplits;
		
		// step 1 - set offsets evenly across file
		long offset = 0;
		for(int i = 0; i < numSplits; i++) {
			splitOffsets[i] = offset;
			offset += splitSize;
		}

		// step 2 - tweak offsets to point to beginning of lines
		for(int i = 1; i < numSplits; i++) {
			input.seek(splitOffsets[i]);
			input.readLine();
			splitOffsets[i] = input.getFilePointer();
		}
		
		return splitOffsets;
	}
	
	/**
	 * Initializes a hashmap of SplitQueues, one for each split.
	 * @param offsets	Array of byte offsets for each split
	 * @throws IOException
	 */
	private void initializeBuffer(long[] offsets) throws IOException {
		buffer = new HashMap<Integer, SplitQueue>();
		for(int i = 0; i < offsets.length; i++) {
			long offsetEnd = (i == offsets.length - 1) ? input.length() : offsets[i+1];
			buffer.put(i, new SplitQueue(filePath, offsets[i], offsetEnd));
		}
	}
	
	/**
	 * Retrieve the next set of lines for the given split.
	 * @param split	Integer key corresponding to the split
	 * @return	Array of lines
	 * @throws NoSuchElementException
	 */
	public String[] getNext(int split) throws NoSuchElementException{
		return buffer.get(split).readBlock();
	}
	
	/**
	 * Check whether a given split has completed it's processing.
	 * @param split	Integer key corresponding to the split
	 * @return	true or false
	 */
	public boolean isSplitEmpty(int split) {
		return buffer.get(split).isEmpty();
	}
	
	/**
	 * Check whether all splits have completed.
	 * @return true or false
	 */
	public boolean isComplete() {
		for(int split : buffer.keySet()) {
			if(!buffer.get(split).isEmpty())
				return false;
		}
		return true;
	}
}
