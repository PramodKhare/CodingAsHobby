package fun.logicpro.ds;

import sun.awt.DisplayChangedListener;

/**
 * @author Pramod khare
 * Solutions to typical problems which can be solved in both
 * iterative and recursive way
 */
public class RecursionVsIteration {
	public RecursionVsIteration() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*// TODO Auto-generated method stub
		System.out.println("Factorial of 6 - "+factorialIterative(6));
		System.out.println("Factorial of 6 - "+factorialRecursive(6));
		System.out.println("Fibonacci till 50 - \n");
		fibonacciIterative(89);
		System.out.println("");
		
		System.out.println("Fibonacci till 50 - \n");
		fibonacciRecursiveBasic(0, 1, 89);
		System.out.println("");
		
		System.out.println("Fibonacci till 50 - \n");
		fibonacciRecursive(89);
		System.out.println("");
		
		System.out.println("Triangular Series nth Term - \n");
		System.out.println(TriangularSeriesIterative(4));
		System.out.println("");
		
		System.out.println("Triangular Series nth Term - \n");
		System.out.println(TriangularSeriesRecursive(8));
		System.out.println("");
		
		findAnagrams(word.length);
		
		System.out.println("\n2 raise to 10 - "+raisingNumberToPower(2, 10));
		
		System.out.println("\n2 raise to 10 - "+raisingNumberToPowerIterative(2, 10));
		
		System.out.println("\n2 raise to 10 - "+raisingNumberToPower2(2, 10));
		*/
		// Reverse Linked List - Iterative Way
		
		// Reverse Linked List - Recursive Way
		int [] array = {1,	2,	3,	9,	10,	11,	13,	15,	17, 19};
		
		System.out.println("\n Binary Search - "+binarySearchForRotatedArrayIterative(array, 0, 13));
		
	}
	
	
	
	/**
	 * Return factorial of n = n(n-1)(n-2)...3*2*1
	 * @param n
	 */
	public static long factorialIterative(long n){
		long result = 1;
		for(long i=2; i<=n; i++){
			result = result * i;
		}
		return result;
	}
	/**
	 * Recursive Factorial
	 * @param n
	 * @return 
	 */
	public static long factorialRecursive(long n){
		if(n<2)
			return 1;
		return n * factorialRecursive(n-1);
	}
	
	/**
	 * Fibonacci Series till n
	 * 0 1 1 2 3 5 8 13 21 34 55 89
	 * O(r^n) = golden ratio exponential
	 */
	public static void fibonacciRecursiveBasic(long i, long j, long n){
		// This implementation only prints i, so 
		if(i <= n){
			System.out.print("\t"+i);
			if(j <= n){
				fibonacciRecursiveBasic(j , i+j, n);
			}
		}
	}
	
	public static void fibonacciRecursive(long n){
		if(n >= 0){
			System.out.print("\t0");
			fibonacciRecursiveImproved(0, 1, n);
		}
	}
	
	public static void fibonacciRecursiveImproved(long i, long j, long n){
		// We need to print first element i.e. 0 separately because 
		// prints only j
		if(j < n){
			System.out.print("\t"+j);
			fibonacciRecursiveImproved(j , i+j, n);
		}
	}
	
	public static void fibonacciIterative(long n){
		long i = 0, j = 1;
		System.out.print("\t"+i);
		while(j <= n){
			System.out.print("\t"+j);
			long temp = i + j;
			i = j;
			j = temp;	
		}
	}
	
	// The numbers in this series are called triangular numbers 
	// because they can be visualized as a triangular arrangement
	// of objects
	/**
	 * Finding n th term in this series
	 * 1 3 6 10 15 21 28 37 .... i.e. (1), (1+2), (1+2+3), (1+2+3+4), etc.
	 * @param n
	 * @return
	 */
	public static int TriangularSeriesRecursive(int n){
		if(n <= 1)
			return 1;
		return n + TriangularSeriesRecursive(n-1);
	}
	
	public static int TriangularSeriesIterative(int n){
		if(n < 1){
			return 0;
		}
		int i = 1;
		int nthTerm = 0;
		while(i<=n){
			nthTerm = nthTerm + i++;
		}
		return nthTerm;
	}
	
	
	/***********************************************************************/
	// Finding Anagrams --> Only Recursive
	// o(n!) - because n 				- (n-1)				- (n-2) -	... 	- till 1
	// 				  for one letter    - for next letter	- next next letter	- last letter
	
	public static char[] word = {'c', 'a', 't'};
	public static void findAnagrams(int wordSize){
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
				shiftbyone(wordSize);
			}
		}
	}
	
	public static void shiftbyone(int wordsize){
		int wordLength = word.length - wordsize;
		char t = word[wordLength];
		for(int i=wordLength+1; i<word.length; i++){
			word[i-1] = word[i]; 
		}
		word[word.length-1] = t;
	}
	
	public static void display(){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<word.length; i++){
			sb.append(word[i]);
		}
		System.out.print("\t"+sb);
	}
	/***********************************************************************/
	
	public static void binarySearchResursive(int[] inputArray, int intFindValue, int intLb, int intUb){
		int intMidValue = (intLb + intUb)/2;
		if(inputArray[intMidValue] == intFindValue){
			System.out.println("Value found at "+intMidValue);
		}else if(intUb < intLb){
			System.out.println("No Value Found");
		}else if(inputArray[intMidValue] > intFindValue){
			binarySearchResursive(inputArray, intFindValue, intLb, intMidValue-1);
		}else{
			binarySearchResursive(inputArray, intFindValue, intMidValue + 1, intUb);
		}
	}
	
	// Binary search on a sorted array that was rotated once, 
	// make it circular - mid = mid - 1 , if 0 rotate to last element 
	public static int binarySearchForRotatedArrayIterative
		(int[] array, int rotatedBy, int searchValue){
		int mid = 0;
		int lo = 0;
		int hi = array.length-1;
		while(true){
			System.out.println(" - lo - "+lo+" - hi - "+hi);
			mid = ((lo + hi)/2) - rotatedBy;
			System.out.println("Mid - "+mid);
			if(lo<hi){
				System.out.println("Array Exhausted!!!");
				break;
			}else if(array[mid] < searchValue)
				lo = mid+1;
			else if (array[mid] > searchValue)
				hi = mid-1;
			else return array[mid];
		}
		return -1;
	}
		
	public static void binarySearchIterative(int[] inputArray, int findValue){
		int intLb = 0;
		int intUb = inputArray.length-1;
		
		int intMidValue;
		while(true){
			intMidValue = (intUb + intLb)/2;
			if(inputArray[intMidValue] == findValue){
				System.out.println("Value found at "+intMidValue);
				break;
			}else if(intUb < intLb){
				System.out.println("No value Found");
				break;
			}else if(inputArray[intMidValue] > findValue){
				intUb = intMidValue - 1;
			}else{
				intLb = intMidValue + 1;
			}
		}
	}
	
	/***********************************************************************/
	
	// inefficient - n
	public static long raisingNumberToPower(long base, long power){
		if(power == 0)
			return 1;
		return base * raisingNumberToPower(base, power-1);
	}
	
	// Efficient - log n
	public static long raisingNumberToPower2(long base, long power){
		if(power == 1)
			return base;
		if(power%2 == 1)
			return raisingNumberToPower2(base*base, power/2)*base;
		else
			return raisingNumberToPower2(base*base, power/2);
	}
	
	public static long raisingNumberToPowerIterative(long base, long power){
		long result = 1;
		while(power>0){
			result *= base;
			power--;
		}
		return result;
	}
}