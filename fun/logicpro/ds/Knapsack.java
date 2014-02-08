package fun.logicpro.ds;

import java.util.Arrays;

/**
 * Class for solving the 0-1 Knapsack problem.
 * 
 * @author michaelwascher
 */
public class Knapsack {

	public static void main(String[] args) {
		
		int[] weights = {2, 3, 4, 3};
		int[] values = {2, 3, 4, 5};
		int capacity = 3;
		
		int[][] solution = getSolutionMatrix(values, weights, capacity);
		int[] optimalSubset = getOptimalSubset(solution, weights);
		
		printMatrix(solution, "Solution Matrix");
		printArray(optimalSubset, "Optimal Subset");
		
	}

	/**
	 * 
	 * Returns the solution matrix for the Knapsack problem associated with the
	 * given values, weights and knapsack capacity.
	 * 
	 * @param values The values of the items.
	 * @param weights The weights of the items.
	 * @param capacity The capacity of the knapsack.
	 * 
	 */
	private static int[][] getSolutionMatrix(int[] values, int[] weights, int capacity) {
		int[][] matrix =  new int[values.length + 1][capacity + 1];
		for(int i = 1; i <= values.length; i++) {
			for (int j = 0; j <= capacity; j++) {
				if (j - weights[i-1]  >= 0) {
					matrix[i][j] = Math.max(matrix[i-1][j], values[i-1] + matrix[i-1][j-weights[i-1]]);
				} else {
					matrix[i][j] = matrix[i-1][j];
				}
			}
		}
		return matrix;
	}
	
	/**
	 * 
	 * Returns the optimal subset of items that should be included in the knapsack
	 * given a completed solution matrix.
	 * 
	 * @param solutionMatrix An N by W matrix, where N is the number of items and W
	 *  	  is the capacity of the knapsack.
	 * @param weights An array of size N containing the weights of each of the items.
	 * 
	 */
	private static int[] getOptimalSubset(int[][] solutionMatrix, int[] weights) {
		int[] subset = new int[weights.length];
		int numItems = 0;
		int i = solutionMatrix.length - 1;
			for (int j = solutionMatrix[0].length - 1; j >= 0 && i > 0;i--) {
				// If the item is in the optimal subset, add it and subtract its weight
				// from the column we are checking.
				if (solutionMatrix[i][j] != solutionMatrix[i-1][j]) {
					subset[numItems] = i;
					j -= weights[i-1];
					numItems++;
				}
			}
		return Arrays.copyOfRange(subset, 0, numItems);
	}
	
	/**
	 * Prints an array to the console, applying the given title.
	 */
	private static void printArray(int[] array, String title) {
		StringBuilder builder = new StringBuilder();
		builder.append("\n").append(title).append(":\n{");
		
		if (array.length != 0)
			builder.append(array[0]);
		
		for (int i = 1; i < array.length; i++) {
			builder.append(", ").append(array[i]);
		}
		
		builder.append("}");
		
		System.out.println(builder.toString());
		
	}

	/**
	 * Prints a matrix (2-dimensional array) to the console, applying the given title.
	 */
	private static void printMatrix(int[][] matrix, String title) {
		StringBuilder builder = new StringBuilder();
		builder.append(title).append(":\n");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				builder.append(matrix[i][j]).append("\t");
			}
			builder.append("\n");
		}
		System.out.print(builder.toString());
	}
}