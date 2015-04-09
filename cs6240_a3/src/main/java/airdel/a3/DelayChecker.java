/**
 * @author Ryan Millay
 * @author Nikit Waghela
 * @author Pramod Khare
 * CS6240
 * Assignment 3
 */

package airdel.a3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import airdel.a3.util.Parser;

public class DelayChecker {
	
	private final String DELAYHEADER = "ArrDel15";
	private BufferedReader pReader;
	private BufferedReader vReader;
	private Parser pParser = new Parser(',');
	private Parser vParser = new Parser(',');
	
	/**
	 * Custom class to hold the tuple output
	 * @author ryanmillay
	 *
	 */
	public class DelayCheckerResult {
		public int numCorrectPredictions;
		public int numWrongPredictions;
		public float percentageAccuracy = 0.0f;
		
		public DelayCheckerResult(int numCorrect, int numWrong) {
			numCorrectPredictions = numCorrect;
			numWrongPredictions = numWrong;
			percentageAccuracy = ((float)numCorrectPredictions/(numCorrectPredictions+numWrongPredictions))*100;
		}

        public void printResults() {
            System.out.println("Checker Results:");
            System.out.println("Total Correct Predictions\t:\t" + this.numCorrectPredictions);
            System.out.println("Total Incorrect Predictions\t:\t" + this.numWrongPredictions);
            System.out.println("Percentage Accuracy\t:\t" + this.percentageAccuracy);
        }
	}
	
	public DelayChecker() {}
	
	/**
	 * Constructor takes the paths for the prediction file and for the
	 * verification file
	 * @param ppath
	 * @param vpath
	 * @throws FileNotFoundException 
	 */
	public DelayChecker(String ppath, String vpath) throws FileNotFoundException {
		// initialize the readers based on the file paths
		pReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(ppath))));
		vReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(vpath))));
	}
	
	
	/**
	 * Compares the prediction file and verification file.  Returns a data structure
	 * with the number of correct predictions and the number of wrong predictions.
	 * @return DelayCheckerResult
	 * @throws IOException
	 */
	public DelayCheckerResult check() throws IOException {
		int numCorrect = 0;
		int numWrong = 0;
		
		// Iterate over each file in lockstep and compare the DELAYHEADER field
		String pInputLine, vInputLine;
		while((pInputLine = pReader.readLine()) != null && (vInputLine = vReader.readLine()) != null) {
			 pParser.parse(pInputLine);
			 vParser.parse(vInputLine);
			 if(pParser.isValid() && vParser.isValid()) {
				 if(pParser.getText(DELAYHEADER).trim().equals(vParser.getText(DELAYHEADER).trim()))
					 numCorrect++;
				 else
					 numWrong++;
			 }
		}
		return new DelayCheckerResult(numCorrect, numWrong);
	}
}
