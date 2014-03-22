package fun.logicpro.ds;

import java.math.BigInteger;

/**
 * @Author: Pramod Khare
 * @Date: 22nd Feb 2014
 * @Purpose: To print first n numbers in fibonacci series, and print them right and left aligned
 * @Modification: used java.math.BigInteger datatype for numbers 
 * 				 because it can handle numbers with very large number of digits
 */
public class FiboSeries {
	public FiboSeries() {}

	/**
	 * Main function - contains calls to the Fibonacci series 
	 * generator function
	 * @param args
	 */
	public static void main(String[] args) {
		// print first 100 fibonacci numbers
		// FiboSeries.printFibo(100);
		
		// print first 500 fibonacci numbers
		// FiboSeries.printBigIntegerFibo(500);
		
		// print first 2500 fibonacci numbers
		FiboSeries.printBigIntegerFibo(2500);
	}
	
	/**
	 * Function to calculate sum of all digits of a given number t
	 * e.g. 1134 will return output as 9 as sum of its all digits is 9
	 * 3456242 will return 8 because sum of all digits is 26 and sum of 26's digits is 8
	 * @param t
	 * @return integer - sum of all digits of given number
	 */
	public static int sumOfDigits(BigInteger t){
		int sum = 0, count = 0;
		StringBuffer bf = new StringBuffer(t.toString());
		
		if(bf.length() == 1){
			return (bf.charAt(0)-48);
		}
		
		while(count<bf.length()){
			//System.out.println("Char At "+count+" - "+bf.charAt(count)+" - Value - "+(bf.charAt(count)-48));
			sum += (bf.charAt(count++)-48);
		}
		return sumOfDigits(new BigInteger(new Integer(sum).toString()));
	}
	
	/**
	 * Print fibonacci numbers till a given
	 * @param limit
	 */
	public static void printFibo(int limit){
		long i=0, j=1, temp;
		System.out.println("\t"+i); limit--;
		while(limit>0){
			System.out.println("\t"+j); 
			limit--;
			temp = i + j;
			i = j;
			j = temp;	
		}
	}
	
	public static void printBigIntegerFibo(int limit){
		BigInteger i= BigInteger.ZERO;
		BigInteger j= BigInteger.ONE;
		BigInteger temp = BigInteger.ZERO;
		
		String format = "\t%"+limit+"s - %s - %s";
		// print format --> number  -	sum-of-digits-of-number 	- number-of-digits
		// e.g. 		    1346269 - 	4 							- 7
		System.out.println(String.format(format, i, sumOfDigits(i), i.toString().length())); limit--;
		
		while(limit>0){
			// Print numbers right aligned 
			System.out.println(String.format(format, j, sumOfDigits(j), j.toString().length()));
						
			// Print numbers left aligned 
			// System.out.println(String.format("\t%s - %s - %s", j, sumOfDigits(j), j.toString().length())); 
			
			limit--;
			temp = BigInteger.ZERO;
			temp = temp.add(i).add(j);
			i = j;
			j = temp;	
		}
	}	
}
