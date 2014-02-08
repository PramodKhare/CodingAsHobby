/**
 * @author Pramod Khare
 */
package fun.logicpro.ds;

/**
 * @author Pramod Khare
 *
 */
public class MatrixSearch {

	/** When both rows and columns are sorted
	 * Two ways to solve the problem
	 * 1) Either use Diagonal binary search and divide the whole 
	 *    matrix into smaller matrices
	 * OR
	 * 2) Use columns and rows removal technique - which leaves 
	 *    columns and rows, whose last row value or first column 
	 *    value is greater than search-value
	 *    
	 *  1 2 8  9
	 *	2 4 9  12
	 *	4 7 10 13
	 *	6 8 11 15
	 */
	public MatrixSearch() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// both column and row are incrementally sorted
		int[][] matrix= {{1, 2, 8, 9}, {2, 4, 9, 12}, {4, 7, 10, 13}, {6, 8, 11, 15}};
		// incrementally sorted matrix for binary search and brute force method of search
		// int[][] matrix= {{1, 2, 3, 5}, {7, 9, 11, 12}, {13, 15, 17, 18}, {19, 21, 24, 25}};
		
		System.out.println("Number of rows - "+matrix.length);
		System.out.println("Number of columns - "+matrix[0].length);
		
		int rows = matrix.length;
		int cols = matrix[0].length;
		
		//pretty print the matrix
		for(int i=0; i<rows; i++){
			//get a row, and print it on new line 
			System.out.println("\n");
			for(int j=0; j<cols; j++){
				System.out.print("\t"+matrix[i][j]);
			}
		}
		System.out.println("\n");
		int valueToSearch = 7;
		boolean found = false;
		// in case of brute-force method of searching for number in matrix
		// there will be 
		/*for(int i=0; i<rows && !found; i++){
			for(int j=0; j<cols && !found; j++){
				if(matrix[i][j] == valueToSearch){
					found = true;
				}
			}
		}*/
		// Time complexity will be O(n) = n^2 or m*n --> m=rows count and n=columns count  
				
		
		// Binary search on matrix
		// Time complexity = O(n) = log (total-no-of-terms) = log (rows*cols)
		/*int start = 0;
		int end = rows*cols - 1;		
		while(start <= end){
			// find out mid index e.g. for array of size 16 mid will be 8 and as its 0-based
			// index you have 7
			int mid = start + (end-start)/2;
			// to get row and column number from this mid index
			int row = mid/cols;	//as rows are incrementally sorted, so divide 
								//mid index by no of columns
			int col = mid%cols;	//percentile will give the column index of midvalue
			int midValue = matrix[row][col];
			//check if midvalue itself is an anwer
			if(valueToSearch == midValue){
				found = true;
			}
			// if valueToSearch is less than midValue change the 
			// end index to midindex -1, 
			// why midindex-1 and not just midindex --> because we
			// have already checked for midindex
			if(valueToSearch < midValue){
				end = mid - 1;
			}else{
				start = mid + 1;
			}
		}
		System.out.println("Status - found =>"+found);
		*/
		
		// First technique to use binary search on diagonal values in
		// given matrix
		System.out.println("Starting Diagonal points - (row1, col1) - (0,0) and (row2, col2) - ("+(rows - 1)+","+(cols - 1)+")");
		found = findCore(matrix, valueToSearch, 0, 0, rows - 1, cols - 1);
		
		// Second technique of Use columns and rows removal technique - which leaves 
		// columns and rows, whose last row value or first column 
		// value is greater than search-value
		// int col = matrix[0].length-1;
		// int row = 0;
		
		// check only one condition for row - as we are starting row count from 0,
		// so need to check the lower bound again in while loop's condition
		
		// about column count it is initialized to max value and might decrement
		// so need to check its lower bound of 0 and no need to check its upper bound
		// in while loops condition
		
		/*while(row < matrix.length && col >=0){
			if(valueToSearch == matrix[row][col]){
				System.out.println("Value found at row - "+row+" and col - "+col);
				found = true;
				break;
			}
			
			if(valueToSearch < matrix[row][col]){
				// we can now remove this col as its start value is
				// greater than value-to-search
				--col;
			}else{
				// else block - meaning 
				// column start is less than value-to-search,
				// its possible that value exists in this 
				// column
				++row;
			}
		}*/
		
		System.out.println("Status --> found => "+found);
	}
	
	// First technique to use binary search on diagonal values in
	// given matrix
	public static boolean findCore(int matrix[][], int value, int row1, int col1, int row2,
			int col2) {
		System.out.println("Recurse - new diagonal points - (row1, col1) - ("+row1+","+col1+") and (row2, col2) - ("+row2+","+col2+")");
		if (value < matrix[row1][col1] || value > matrix[row2][col2])
			return false;
		if (value == matrix[row1][col1] || value == matrix[row2][col2]){
			System.out.println("Diagonal values - (row1, col1) - "+matrix[row1][col1]+" and (row2, col2) - ("+matrix[row2][col2]+")");
			return true;
		}
		int copyRow1 = row1, copyRow2 = row2;
		int copyCol1 = col1, copyCol2 = col2;
		int midRow = (row1 + row2) / 2;
		int midCol = (col1 + col2) / 2;
		// find the last element less than value on diagonal
		while ((midRow != row1 || midCol != col1)
				&& (midRow != row2 || midCol != col2)) {
			if (value == matrix[midRow][midCol]){
				System.out.println("midRow and MidCol value - "+matrix[midRow][midCol]);
				return true;
			}
			if (value < matrix[midRow][midCol]) {
				row2 = midRow;
				col2 = midCol;
			} else {
				row1 = midRow;
				col1 = midCol;
			}
			midRow = (row1 + row2) / 2;
			midCol = (col1 + col2) / 2;
		}
		// find value in two sub-matrices
		boolean found = false;
		if (midRow < matrix.length - 1){
			found = findCore(matrix, value, midRow + 1, copyCol1, copyRow2,
					midCol);
		}
		if (!found && midCol < matrix[0].length - 1)
			found = findCore(matrix, value, copyRow1, midCol + 1, midRow,
					copyCol2);
		return found;
	}
}
