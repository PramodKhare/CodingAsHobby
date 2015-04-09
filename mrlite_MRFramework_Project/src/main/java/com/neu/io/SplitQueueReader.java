package com.neu.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Low level reader for a file split.  Uses a LinkedBlockingQueue for populating lines.
 * Attempts to read just enough lines in to the queue to stay ahead of the client.
 * @author ryanmillay
 *
 */
public class SplitQueueReader implements Runnable {
	private BlockingQueue<String> lazyBuffer;
	private RandomAccessFile chunk;
	private long offsetEnd;
	private int lazy_buffer_min_length = 15;
	private int lazy_buffer_max_length = 30;
	
	/**
	 * Initialize the reader with the file and the offset boundaries.  Sets the min and max
	 * lengths of the queue to 15 and 30 (lines) respectively.
	 * @param filePath	String path to file
	 * @param start	byte offset to begin processing
	 * @param end	byte offset to finish processing
	 * @throws IOException
	 */
	public SplitQueueReader(String filePath, long start, long end) throws IOException {
		chunk = new RandomAccessFile(filePath, "r");
		chunk.seek(start);
		offsetEnd = end;
		lazyBuffer = new LinkedBlockingQueue<String>();
	}
	
	/**
	 * Initialize the reader with the file and the offset boundaries.  Sets the min and max
	 * lengths of the queue to queueMin and queueMax respectively.
	 * @param filePath	String path to file
	 * @param start	byte offset to begin processing
	 * @param end	byte offset to finish processing
	 * @param queueMin	minimum number of lines to keep in the queue
	 * @param queueMax	maximum number of lines to keep in the queue
	 * @throws IOException
	 */
	public SplitQueueReader(String filePath, long start, long end, int queueMin, int queueMax) throws IOException {
		chunk = new RandomAccessFile(filePath, "r");
		chunk.seek(start);
		offsetEnd = end;
		lazy_buffer_min_length = queueMin;
		lazy_buffer_max_length = queueMax;
		lazyBuffer = new LinkedBlockingQueue<String>();
	}
	
	/**
	 * Fills the queue until the max length or end of the chunk has been reached.
	 * @throws IOException
	 */
	private void fillQueue() throws IOException {
		while(lazyBuffer.size() < lazy_buffer_max_length && chunk.getFilePointer() < offsetEnd) {
			lazyBuffer.add(chunk.readLine());
		}
	}
	
	/**
	 * Read the next line in the chunk.  Waits indefinitely until something is available.
	 * @return	Next line or null
	 * @throws InterruptedException 
	 */
	public String readLine() throws InterruptedException {
		return lazyBuffer.take();
	}
	
	/**
	 * Does the queue have any elements?
	 * @return true or false
	 */
	public boolean isEmpty() {
		return lazyBuffer.isEmpty();
	}
	
	/**
	 * Does the queue have the minimum number of elements?
	 * @return true or false
	 */
	public boolean isReady() {
		return (lazyBuffer.size() >= lazy_buffer_min_length);
	}
	
	/**
	 * Endless loop to keep the queue filled with lines.
	 */
	public void run() {
		while(true) {		
			try {
				// break if we've reached the end of the chunk
				if(chunk.getFilePointer() >= offsetEnd) {
					// add a poison pill and break
					lazyBuffer.add(SplitQueue.POISON_PILL);
					break;
				}
				
				// fill the queue if we're below the threshold
				if(lazyBuffer.size() < lazy_buffer_min_length)
					fillQueue();
			} catch (IOException e) {
				// TODO Determine what would make sense to do here
				e.printStackTrace();
				break;
			}
		}
	}
}
