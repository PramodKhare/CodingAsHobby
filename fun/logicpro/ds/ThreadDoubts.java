/**
 * @author Pramod Khare
 */
package fun.logicpro.ds;

/**
 *  @author from book Code Interviews
 * 	
 *	There are four threads in the code above. 
 *	The first is the main thread in the Java application, which
 *	acts as the scheduler, and it creates three printing threads
 *	and stores them into an array. The main thread awakens threads
 *	one by one according to their index in the array via the method
 * 	notify. Once a thread wakes up, it prints a number and then 
 * 	sleeps again to wait for another notification.
 */

class SimpleThread extends Thread {
	private int value;
	
	public SimpleThread(int num) {
		this.value = num;
		start();
	}
	
	public void run() {
		while (true) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				System.out.print(value + " ");
			}
		}
	}
}

public class ThreadDoubts {
	static final int COUNT = 3;
	static final int SLEEP = 37;

	public static void main(String args[]) {
		SimpleThread threads[] = new SimpleThread[COUNT];
		
		for (int i = 0; i < COUNT; ++i)
			threads[i] = new SimpleThread(i + 1);
		
		int index = 0;
		
		while (true) {
			synchronized (threads[index]) {
				threads[index].notify();
			}
			
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			
			index = (++index) % COUNT;
		}
		//System.out.println("6*7 >> 1 - "+ ((6*7)>>1));
	}
}