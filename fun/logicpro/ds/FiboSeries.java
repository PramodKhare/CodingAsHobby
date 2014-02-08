/**
 * 
 */
package fun.logicpro.ds;

import java.math.BigInteger;

/**
 * @author Pramod Khare
 * To print all fibo series numbers till given n limit
 */
public class FiboSeries {
	public FiboSeries() {}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//FiboSeries.printFibo(100);
		FiboSeries.printBigIntegerFibo(2500);
		//System.out.println("Sum of digits - "+FiboSeries.sumOfDigits(new BigInteger("39088169")));
	}
	
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
		//System.out.println("\t"+i+" - "+sumOfDigits(i)+" - "+i.toString().length()); limit--;
		System.out.println(String.format("\t%85s - %s - %s", i, sumOfDigits(i), i.toString().length())); limit--;
		while(limit>0){
			//System.out.println("\t"+j+" - "+sumOfDigits(j)+" - "+j.toString().length()); 
			System.out.println(String.format("\t%85s - %s - %s", j, sumOfDigits(j), j.toString().length())); 
			limit--;
			temp = BigInteger.ZERO;
			temp = temp.add(i).add(j);
			i = j;
			j = temp;	
		}
	}	
}
