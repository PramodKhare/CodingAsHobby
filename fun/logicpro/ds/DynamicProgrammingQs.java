/**
 * 
 */
package fun.logicpro.ds;

import java.io.InputStreamReader;

/**
 * @author user
 *
 */
public class DynamicProgrammingQs {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	// Knapsack problem - solution using recursion
	public static void solveKnapsack(int goal, int[] input){
		// find the combinations from input which give me goal
		
	}
	
	
	public static void findAnagrams(int goal, int wordSize, int[] input){
		if(wordSize == 1){
			return ;
		}else{
			for(int i=0; i<wordSize; i++){
				// now keep current letter - constant 
				// and shift next letters to find
				// combinations
				findAnagrams(wordSize - 1);
				if(wordSize == 2){
					display();
				}
				shiftbyone(wordSize, input);
			}
		}
	}
	
	public static int[] shiftbyone(int wordsize, int[] input){
		int wordLength = input.length - wordsize;
		int t = input[wordLength];
		for(int i=wordLength+1; i<input.length; i++){
			input[i-1] = input[i]; 
		}
		input[input.length-1] = t;
		return input;
	}
}
