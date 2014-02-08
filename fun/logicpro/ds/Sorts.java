/**
 * @author Pramod Khare
 */
package fun.logicpro.ds;

import java.util.Arrays;
import java.util.Hashtable;

/**
 * @author Pramod Khare
 * Different types of sorts
 * Merge, Insertion, Bubble, Selection, Quick, Heap, Shell, Counting, Bucket, Radix, bin sorts
 */
public class Sorts {

	/**
	 * All major sorting algorithms
	 */
	public Sorts() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] test = {5, 4, 3, 12, 29, 12, 1, 1, 165};
		System.out.println("Array before sort - \n");
		for(int i=0;i<test.length;i++){
			System.out.print("\t"+test[i]);
		}
		
		//test = insertionSort(test);
		//test = bubbleSort(test);
		//test = selectionSort(test);
		//test = mergeSort2(test);
		
		// merging 2 already sorted arrays
		//int[] merge1 = {21, 26, 29, 165};
		//int[] merge2 = {1, 4, 13, 25, 100, 300};
		
		/*int[] merge2 = {1, 2, 9, 16};
		int[] merge1 = {25, 100, 300};
		
		int[] mergetest = mergeSortedArrays(merge1, merge2);
		System.out.println("\nArray After merging 2 arrays - \n");
		for(int i=0;i<mergetest.length;i++){
			System.out.print("\t"+mergetest[i]);
		}
		
		System.out.println("\nArray After sort - \n");
		for(int i=0;i<test.length;i++){
			System.out.print("\t"+test[i]);
		}
		
		insertionSortEasy(test);
		*/
		quickSort(test, 0 , test.length-1);
		System.out.println("\nAfter sort - \n");
		for(int i=0;i<test.length;i++){
			System.out.print("\t"+test[i]);
		}
	}
	
	/**
	 * Insertion Sort  - we split the array into two sections one on left
	 * which is in order or sorted, and on right yet to be examined for sorting
	 * 
	 * At start, we split array into two, on left only one element, i.e. first
	 * element and on right remaining elements
	 * 
	 * Shifting of elements might happen n times in each iteration, but 
	 * only when there is any smaller element on right-hand side
	 * 
	 * @param inputArray - array to be sorted
	 * @return int[] - sorted array using insertion sort
	 */
	public static int[] insertionSort(int [] inputArray){
		int valueToCompare;
		int j;
		for(int i=1; i<inputArray.length; i++){
			valueToCompare = inputArray[i];
			j=i;
			for(; j>0 && inputArray[j-1]>valueToCompare; --j){
				inputArray[j] = inputArray[j-1];
				System.out.println("\n");
				for(int k=0;k<inputArray.length;k++){
					System.out.print("\t"+inputArray[k]);
				}
			}
			inputArray[j] = valueToCompare;
			System.out.println("\n");
			for(int k=0;k<inputArray.length;k++){
				System.out.print("\t"+inputArray[k]);
			}
		}
		return inputArray;
	}
	
	/**
	 * Easier To Understand Insertion Sort
	 * @param inputArray
	 * @return sorted array 
	 */
	public static int[] insertionSortEasy(int[] inputArray){
		System.out.println("Before Sort - \n");
		for(int k=0;k<inputArray.length;k++){
			System.out.print("\t"+inputArray[k]);
		}
		
		for(int i=0; i<inputArray.length; i++){
			for(int j=i; j>0; j--){
				if(inputArray[j] < inputArray[j-1]){
					int temp = inputArray[j-1];
					inputArray[j-1] = inputArray[j];
					inputArray[j] = temp;
				}else
					break;
			}
		}
		
		System.out.println("After Sort - \n");
		for(int k=0;k<inputArray.length;k++){
			System.out.print("\t"+inputArray[k]);
		}
		return inputArray;
	}
	
	
	
	public static int[] bubbleSort(int [] inputArray){
		int temp;
		boolean swapped = false;
		for(int i=0; i<inputArray.length; i++){
			swapped = false;
			for(int j=i+1; j<inputArray.length; j++){
				if(inputArray[j]<inputArray[i]){
					temp = inputArray[j];
					inputArray[j] = inputArray[i];
					inputArray[i] = temp;
					swapped = true;
				}
			}
			if(!swapped){
				break;
			}
			System.out.println("\n");
			for(int k=0;k<inputArray.length;k++){
				System.out.print("\t"+inputArray[k]);
			}
		}
		return inputArray;
	}
	
	
	public static int[] selectionSort(int [] inputArray){
		int min, temp;
		for(int i=0; i<inputArray.length; i++){
			min = i;
			for(int j=i+1; j<inputArray.length;j++){
				if(inputArray[j]<inputArray[min]){
					min = j;
				}
			}
			if(min != i){
				//swap elements
				temp = inputArray[i];
				inputArray[i] = inputArray[min];
				inputArray[min] = temp;
			}
			System.out.println("\n");
			for(int k=0;k<inputArray.length;k++){
				System.out.print("\t"+inputArray[k]);
			}
		}
		return inputArray;
	}
	
	
	/**
	 * Partition the array and find the mid or pivot such that
	 * right of array to pivot is higher and to left its lower
	 * 
	 * Starts with 2 pointers one from left and other from right 
	 * checks if ith value is less than lo th element and 
	 * jth value is greater than hi th element from input array
	 * till it either crosses j and i or bounds of array i.e. lo and hi
	 * 
	 * in case when i!=j then swap ith element with jth 
	 * 
	 * in the end we have found the pivot element and now swap it
	 * with jth or ith (because in the end, i==j) element
	 * 
	 * returns the pivots index position back
	 * @param inputArray
	 * @param lo
	 * @param hi
	 * @return 
	 */
	public static int partition(int[] inputArray, int lo, int hi){
		int i = lo+1, j = hi, temp;
		while(true){
			while(inputArray[i] < inputArray[lo]){
				i++;
				if(i==hi)	
					break;
			}
			while(inputArray[j] > inputArray[lo]){
				j--;
				if(j==lo)
					break;
			}
			if(i>=j)
				break;
			// swap i th and j th item
			temp = inputArray[i];
			inputArray[i] = inputArray [j];
			inputArray[j] = temp;
		}
		// swap j and lo items
		temp = inputArray[lo];
		inputArray[lo] = inputArray[j];
		inputArray[j] = temp;
		
		return j;
	}
	
	public static void quickSort(int [] inputArray, int lo, int hi){
		//System.out.println("Quicksort with - Low - "+lo+" - high - "+hi);
		if(lo >= hi) return ;
		int j = partition(inputArray, lo, hi);
		quickSort(inputArray, lo, j-1);
		// j th element is considered to be in correct position
		quickSort(inputArray, j+1, hi);
	}
	
	/**
	 * Merge sorted arrays to return a bigger array
	 * @param inputArray1
	 * @param inputArray2
	 * @return a merged array of size = (size of inputArray1) + (size of inputArray2)
	 */
	public static int[] mergeSortedArrays(int [] inputArray1, int [] inputArray2){
		int mergedArraySize = inputArray1.length + inputArray2.length;
		int [] mergedArray = null;
		
		//performance optimization
		if(inputArray1[inputArray1.length-1] < inputArray2[0]){
			System.out.println("Shortcut merge happening");
			mergedArray = Arrays.copyOf(inputArray1, mergedArraySize);
			System.arraycopy(inputArray2, 0, mergedArray, inputArray1.length, inputArray2.length);
			return mergedArray;
		}else if(inputArray2[inputArray2.length-1] < inputArray1[0]){
			System.out.println("Shortcut merge happening");
			mergedArray = Arrays.copyOf(inputArray2, mergedArraySize);
			System.arraycopy(inputArray1, 0, mergedArray, inputArray2.length, inputArray1.length);
			return mergedArray;
		}
		
		mergedArray = new int[mergedArraySize];
		
		int i=0, j=0, k=0;
		for(; j<inputArray1.length && k<inputArray2.length; i++){
			if(inputArray1[j] < inputArray2[k]){
				mergedArray[i] = inputArray1[j++];
			}else{
				mergedArray[i] = inputArray2[k++];
			}
		}
		while(j<inputArray1.length){
			mergedArray[i++] = inputArray1[j++];
		}
		while(k<inputArray2.length){
			mergedArray[i++] = inputArray2[k++];
		}
		return mergedArray;
	}
	
	/**
	 * Merge sort using many small arrays while sorting, this is really inefficient
	 * @param inputArray
	 * @return sorted array - sorted using merge sort
	 */
	public static int[] mergeSort2(int[] inputArray){
		// this function gets called recursively until the passed array is of length one
		
		//the base case
		if(inputArray.length <= 1)
			return inputArray;
		
		int mid = inputArray.length / 2;
		int[] left = new int[mid];
		int[] right = new int[inputArray.length - mid];
		
		//Improvised code for assignment of left and right array
		int i=0, j=0;
		while(i < inputArray.length){
			if(i < left.length)
				left[i] = inputArray[i++];
			else
				right[j++] = inputArray[i++];
		}
		
		/*	
		 * 	First code - which uses two for loops and two extra counters
		 * 	
			while(i < mid){
				left[i] = inputArray[i++];
			}
			int j=0;
			while(j < right.length && i < inputArray.length){
				right[j++] = inputArray[i++];
		}*/
		
		left = mergeSort2(left);
		right = mergeSort2(right);
		return mergeSortedArrays(left, right);
	}
	
	/**
	 * Merge sort using only one extra array of same size n
	 * So, space complexity = o(n)
	 * @param inputArray
	 * @return sorted array - sorted using merge sort
	 */
	public static int[] mergeSort(int[] inputArray){
		// this function gets called recursively until the passed array is of length one
		
		//the base case
		if(inputArray.length <= 1)
			return inputArray;
		
		int mid = inputArray.length / 2;
		int[] left = new int[mid];
		int[] right = new int[inputArray.length - mid];
		
		int i=0;
		while(i < mid){
			left[i] = inputArray[i++];
		}
		int j=0;
		while(j < right.length && i < inputArray.length){
			right[j++] = inputArray[i++];
		}
		
		left = mergeSort(left);
		right = mergeSort(right);
		return mergeSortedArrays(left, right);
	}
	
	/**
	 * Radix Sort using given base
	 * @param inputArray
	 * @return
	 */
	public static int[] radixSort(int[] inputArray, int radixBase){
		return inputArray;
	}
	
	/**
	 * bin sort -- using bins at every value/range
	 * @param inputArray
	 * @return 
	 */
	public static int[] binSort(int[] inputArray){
		int range = inputArray.length;
		// Space for all bins each bin must be of size inputArray.length,
		// for worst case scenario
		int[][] bins = new int[range+1][range];
		// So each bin is of size of inputArray
		
		// First get go through the input array's elements and
		// put them into respective bins
		for(int i=0; i<inputArray.length; i++){
			
		}
		return inputArray;
	}
	
	/**
	 * Binary Heap - min or max heap is used and all elements are 
	 * first inserted into heap first which is O(n), then 
	 * take one by one min or max element out and put it into 
	 * array
	 * @return 
	 */
	public static int[] heapSort(int[] inputArray){
		
		return inputArray;
	}
	
	/**
	 * Shell sort is a variation of insertion just bigger length swaps
	 * @param inputArray
	 * @return
	 */
	public static int[] shellSort(int[] inputArray){
		
		return inputArray;
	}
}
