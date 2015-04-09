package airdel.a3.util;

import java.util.Date;

public class Timer {
	private Stats stats;
	
	public Timer() {
		stats = new Stats();
	}
	
	public Timer start() {
		if(stats.start != -1) throw new Error("Trying to restart the timer.");
		stats.update(State.START);
		return this;
	}
	
	public Timer stop() {
		if(stats.finish != -1) throw new Error("Trying to reuse the timer.");
		stats.update(State.FINISH);
		return this;
	}
	
	@Override
	public String toString() {
		return stats.toString();
	}

	private class Stats {
		long start = -1;
		long finish = -1;
		int mb = 1024*1024;
		
		Runtime runtime = Runtime.getRuntime();
		
		private static final String STROUT = "Stats\nStart time\t:\t%s\nEnd time\t:\t%s\nTotal\t\t:\t%d sec\nUsed Memory\t:\t%d Mb\n";
		public void update(State state) {
			switch(state) {
			case START:
				start = System.currentTimeMillis();
				break;
			case FINISH:
				finish = System.currentTimeMillis();
				break;
			}
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(String.format(STROUT, 
					new Date(start).toString(), 
					new Date(finish).toString(), 
					(finish - start)/1000,
					(runtime.totalMemory() - runtime.freeMemory()) / mb));
			return sb.toString();
		}
	}
}