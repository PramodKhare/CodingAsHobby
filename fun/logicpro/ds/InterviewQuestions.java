/**
 * @author Pramod Khare
 */
package fun.logicpro.ds;

import java.util.Arrays;

/**
 * @author Pramod Khare
 */
public class InterviewQuestions {

	/**
	 * 
	 */
	public InterviewQuestions() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Given an integer N and an array of unsorted integers A 
		// find all pairs of numbers within A which add up to N.
		
		// Result is array of pairs 
		int[] testArray = { 19, 11, 13, 2, 28, 9, 46, 3, 27, 19, 31, 2, 21, 1, 1, 25, 47, 17, 11, 24 };
		int[] randomNumbers = { 23, 29, 84, 15, 58, 19, 81, 17, 48, 15, 36, 49, 91, 26, 89, 22, 63, 57, 33 };
		int[] test2 = { 19, 11, 13, 2, 28, 9, 46, 3, 27, 19, 31, 2, 21, 1, 1, 25, 47, 17, 11, 24 };
		Arrays.sort(testArray);
		System.out.println("Input Array - \n");
		for(int i=0; i<testArray.length; i++){
			System.out.print("\t"+testArray[i]);
		}
		//After finding kth element by using partitioning
		System.out.println("kth Element - "+
				randomOrderStatiticKthElement(test2, test2.length-1, 0, 9, randomNumbers, 0));
		
		System.out.println("Array after partitioning - \n");
		for(int i=0; i<test2.length; i++){
			System.out.print("\t"+test2[i]);
		}
		// 1.
		// findSumPairs(testArray, 4);
		
		// 2.
		// bitwiseOperations();
		
		// 5.
		// Array input for approach one - 
		// int[] testNonDuplicateArray = {1, 1, 2, 2, 3, 3, 5, 6, 5, 4, 6};
		// AND
		// Array input for approach two -
		// int[] testNonDuplicateArray = {1, 1, 2, 2, 3, 3, 87, 87, 43, 43, 11, 21, 11, 21, 5, 6, 5, 4, 6};
		// findNonDuplicateFromDuplicateNumberedArray(testNonDuplicateArray, 6);
		
		// 6. 
		// find if array are passed by reference and if any value in array 
		// is changed in the passed function then
		// findPassByReferenceForArray();
		
		// 7. 
		// Given Array find output array which is product of all other values 
		// in given array except at the same index
		// productArrayQuestion();
		
		// 8.
		// SUM of three numbers from a group of numbers
		// SUM3Problem();
	}
	
	public static void bitwiseOperations() {
		// find ith bit in a number
		int n = 4;
		int value = 1;
		String bit = (value & (1 << n))== 0 ?"0":"1";
		System.out.println("ith bit is one - "+bit);
		
		// swap 2 numbers without using any temp variable
		int x = 23, y = 23;
		//x = x ^ y;
		//y = x ^ y;
		//x = x ^ y;
		
		//order of x and y doesn't matter in XORing
		x = y ^ x;
		y = x ^ y;
		x = y ^ x;
		
		// in a single line, but buggy, when both are same, doomed 
		// x ^= y ^= x ^= y;
		
		// swap 2 numbers without using any temp variable
		// using addition and subtraction
		/*
		x = x+y;
		y = x-y;
		x = x-y;
		
		// swap in a single line - 
		x=(x+y)-(y=x);
		*/
		
		// swap 2 numbers without using any temp variable
		// using multiplication and division
		/*
		x = x*y;
		y = x/y;
		x = x/y;
		*/
		
		System.out.println("x - "+x);
		System.out.println("y - "+y);
		
		// swapping 2 string values without using temp
		// variable
		String a = "tttt", b = "mmmm";
		// either use a separator and do substring or don't 
		// but it totally not required
		a = a + b;
		System.out.println("a+b - "+a);
		b = a.substring(0, (a.length() - b.length()));
		System.out.println("b - "+b);
		a = a.substring(b.length());
		System.out.println("a - "+a);
		
		
	}
	
	public static void findSumPairs(int[] inputArray, int result) {
		int count = 0;
		//brute-force method
		for(int i=0; i<inputArray.length; i++){
			for(int j=0; j<inputArray.length; j++){
				if(i!=j && result == (inputArray[i] + inputArray[j])){
					System.out.println("Pair is - "+inputArray[i]+" at "+i+" and "+inputArray[j]+" at "+j);
					count++;
				}
			}
		}
		System.out.println("Total Pairs - "+count);
	}
	
	/**
	 *	Given an array of numbers where each number has a duplicate except one, 
	 *	write a program to return the lone number. 
	 * 	@param inputArray
	 *	@param n
	 */
	public static void findNonDuplicateFromDuplicateNumberedArray(int[] inputArray, int n){
		// Approach one - when numbers are from 1 to n
		// and one of them is not duplicate
		// Total sum for 1 to n numbers = n(n+1)/2
		
		/*int sum = 0;
		for(int i=0; i<inputArray.length; i++){
			sum+=inputArray[i];
		}
		int totalExpectedSum = n*(n+1);
		int nonDuplicateNum = totalExpectedSum - sum;
		System.out.println("Non Duplicate Number = "+nonDuplicateNum);
		*/
		// Approach two - 
		// If numbers are not in a series meaning array contains any random
		// numbers, then
		// XOR of same number is zero
		// so we will XOR all the numbers and final result will be the number
		// which is not duplicate
		int nonDuplicateNum=inputArray[0];
		for(int i=1; i<inputArray.length; i++){
			nonDuplicateNum^=inputArray[i];
		}
		System.out.println("Non Duplicate Number = "+nonDuplicateNum);
		
		// Approach three - 
		// Sort the array and then using condition search for non-duplicate
		// number
		// if(array[i] != array [i+1])
		// Time Complexity = O(n * log n * n)
		// Space Complexity = O(n)
	}
	
	/**
	 *	The input to a function is an array of n elements.
	 	Output of that function is also an array where each element is
		product of all elements in the input array except the one with same index.
		
		Input ::::: 1 2 3 4
		Output ::::: 2*3*4 1*3*4 1*2*4 1*2*3
	*/
	public static void productArrayQuestion(){
		int[] test = {1,2,3,4};
		int product = 1;
		int[] output = new int[test.length];
		// Variation 1 
		// Array can contain random numbers
		// Brute-force solution = O(n^2) - time and O(n) - space
		/*
		for(int i=0; i<test.length; i++){
			product = 1;
			for(int j=0; j<test.length; j++){
				if(i!=j)
					product*=test[j];
			}
			output[i] = product;
		}
		System.out.println("Output Array - ");
		for(int i=0; i<output.length; i++){
			System.out.print("\t"+output[i]);
		}
		*/
		
		// One time product and just divide by given number
		// O(2n) ==> O(n) - time and space - O(1)
 		/*
		product = 1;
 		for(int i=0;i<test.length;i++){
 			product *= test[i];
 		}
 		// now replace the same given array with
 		for(int i=0;i<test.length;i++){
 			test[i] = product/test[i];
 		}
		System.out.println("Output Array - ");
		for(int i=0; i<test.length; i++){
			System.out.print("\t"+test[i]);
		}
		*/
		
		// Variation 2 
		// given array is sorted and starts from 1 till n
		// Output array is equal to n!/array[i]
		product = factorial(test[test.length-1]);
 		
 		// now replace the same given array with
 		for(int i=0;i<test.length;i++){
 			test[i] = product/test[i];
 		}
		System.out.println("Output Array - ");
		for(int i=0; i<test.length; i++){
			System.out.print("\t"+test[i]);
		}
	}
	
	public static void findPassByReferenceForArray(){
		int[] test = {4,4,4,4};
		System.out.println("Before - ");
		for(int i=0; i<test.length; i++){
			System.out.print("\t"+test[i]);
		}
		arrayPassByReference(test);
		System.out.println("After - ");
		for(int i=0; i<test.length; i++){
			System.out.print("\t"+test[i]);
		}
	}
	public static void arrayPassByReference(int[] inputArray){
		inputArray[0] = 34;
	}
	
	private static int factorial(int n) {
		if (n<= 1)
			return 1;
		// Approach 1 - Recursive
		return n*factorial(n-1);
		
		// Approach 2 - Iterative
		/*
		int result = 1;
		while(n > 1){
			result *=n--;
		}
		return result;
		*/
	}
	
	/**
	 * Given a series of numbers - find all the possible combinations
	 * of any three numbers whose sum is N
	 * Brute Force - O(n^3) - worst case and O(1) - space complexity
	 * Current best implementation by Wikipedia  - O(n^2)
	 */
	public static void SUM3Problem(){
		int[] test = {1, 2, 3, 2, 1, 4, 7, 8, 1, 3, 4};
		int sum = 6, tempsum=0, i=0,j=0,k=0, a,b,c;
		
		//brute force approach- O(n^3)
		/** 
		 *  Output for input set
		 *  int[] test = {1, 2, 3, 2, 1, 4, 7, 8, 1, 3, 4};
			int sum = 6,
			
		 	0 1 2
			0 1 9
			0 2 3
			0 3 9
			0 4 5
			0 4 10
			0 5 8
			0 8 10
			1 2 4
			1 2 8
			1 4 9
			1 8 9
			2 3 4
			2 3 8
			3 4 9
			3 8 9
			4 5 8
			4 8 10
		/*
		for(i=0;i<test.length;i++){
			for(j=i+1;j<test.length;j++){
				for(k=j+1;k<test.length;k++){
					if(test[i]+test[j]+test[k] == sum){
						System.out.println(i +" "+j+" "+k);
					}
				}
			}
		}
		*/
		
		// O(n^2) solution - for sorted set
		//
		Arrays.sort(test); //nlogn - quicksort 
		System.out.println("After Sorting- ");
		for(i=0; i<test.length; i++){
			System.out.print("\t"+test[i]);
		}
		// O(n^2) to check
		// but this might miss few cases 
		// this algorithm is good for searching if there is any 
		// triplet whose sum equals to required sum
		/*for(i=0;i<test.length-3;i++){
			a = test[i];
			j=i+1;
			k=test.length-1;
			while(k>j){
				b = test[j];
				c = test[k];
				
				tempsum = a+b+c;
				if(tempsum == sum){
					System.out.println(i +" "+j+" "+k);
					k--;
				}else if (tempsum > sum){
					k--;
				}else{
					j++;
				}
			}
		}*/
		
		// First sort the input array using any sort 
		// and then
		// O(n^2 * log n) --< worst case performance
		// two loops and a binary search on a sorted 
		// array to get third term which equals 
		// = (SUM - (array[i] + array[j]))
		
		// Total Time complexity = n^2 + n^2*log n --> 
		// approximates to n^2
		
		for(i=0;i<test.length-3;i++){
			a = test[i];
			j=i+1;
			k=test.length-1;
			while(k>j){
				b = test[j];
				c = test[k];
				
				tempsum = a+b+c;
				if(tempsum == sum){
					System.out.println(i +" "+j+" "+k);
					k--;
				}else if (tempsum > sum){
					k--;
				}else{
					j++;
				}
			}
		}
	}
	
	// partition around pivot and return pivots final position
	public static int partition(int[] array, int hi, int lo){
		int i = lo+1, j = hi, temp;
		while(true){
			while(array[i]<array[lo]){
				i++;
				if(i == hi)
					break;
			}
			
			while(array[j]>array[lo]){
				j--;
				if(j==lo)
					break;
			}
			// Break if lo and hi are same
			if(i >= j) break;
			
			//swap array[lo] with array[hi]
			temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}
		
		// swap pivot with lo==hi value
		temp = array[lo];
		array[lo] = array[j];
		array[j] = temp;
		
		System.out.println("\nArray after partitioning - \n");
		for(i=0; i<array.length; i++){
			System.out.print("\t"+array[i]);
		}
		
		return j;
	}
	
	// Finding the k-th smallest element
	public static int randomOrderStatiticKthElement(int[] arr, int hi, int lo, 
			int k, int[] random, int randomIndex){
		System.out.println("\nArray of Size - "+(hi-lo+1)+" - Find kth element - "+k);
		// Find pivot position using random array and modular operation
		int pivot = (random[randomIndex] % (hi - lo + 1));
		System.out.println("Random Number from random number array - "+random[randomIndex]);
		System.out.println("Calculated Pivot - "+pivot+" - pivot value - "+arr[pivot]);
		//Swap pivot with 0th position
		int temp = arr[0];
		arr[0] = arr[pivot];
		arr[pivot] = temp;
		
		int j = partition(arr, hi, lo);
		if(j<k)	randomOrderStatiticKthElement(arr, hi, j+1, k, random, ++randomIndex);
		else if (j>k) randomOrderStatiticKthElement(arr, j-1, lo, k, random, ++randomIndex);
		return arr[k];
	}
	
}