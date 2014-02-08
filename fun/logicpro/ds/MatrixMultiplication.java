/**
 * 
 */
package fun.logicpro.ds;

/**
 * @author user
 *
 */
public class MatrixMultiplication {

	/**
	 * Given two matrices -- OUTOUT its multiplication
	 * 
	 */
	public static int[][] matrixMultiplication(int[][] matrix1, int[][] matrix2){
		if(matrix1[0].length != matrix2.length){
			System.out.println("Matrices cannot be multiplied");
			return null;
		}
		System.out.println("Matrix 1");
		System.out.println();
		printMatrix(matrix1);
		
		System.out.println("Matrix 2");
		System.out.println();
		printMatrix(matrix2);
		
		int[][] output = new int[matrix1.length][matrix2[0].length];
		
		// Each row gets multiplied with each column
		// to form each row of result matrix
		for(int i=0; i<matrix1.length; i++){
			// for each column - in matrix 2
			for(int k = 0; k<matrix2[0].length; k++){
				// we multiply each term from matrix1 with each term in matrix2
				// sum these products
				int sum = 0;
				for(int j = 0; j<matrix1[i].length; j++){
					sum += matrix1[i][j]*matrix2[j][k];
				}
				// this is our result matrix's element
				output[i][k] = sum;
			}
		}
		
		System.out.println("Result Matrix - ");
		System.out.println();
		printMatrix(output);
		return output;
	}
	
	public static void printMatrix (int[][] matrix1){
		for(int i=0; i<matrix1.length; i++){
			System.out.println();
			for(int j=0; j<matrix1[i].length; j++){
				System.out.print("\t"+matrix1[i][j]);
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		int [][] matrix1 = { {1, 2, 3},
							 {4, 5, 6},
							 {7, 8, 9}
							};
		
		int [][] matrix1 = { {7, 3},
							 {2, 5},
							 {6, 8}, 
							 {9, 0}
							};
		int [][] matrix2 = { {1, 2, 3},
							 {4, 5, 6},
							 {7, 8, 9}
							};
									
		int [][] matrix2 = { {7, 4, 9},
							 {8, 1, 5}
							};
		*/
		
		int [][] matrix1 = { {1, 2},
							 {3, 4},
							 {5, 6}
							};
		
		int [][] matrix2 = { {1, 2, 3, 4},
							 {5, 6, 7, 8}
							};
		
		matrixMultiplication(matrix1, matrix2);
	}

}
