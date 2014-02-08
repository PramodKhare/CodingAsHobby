/**
 * @author Pramod Khare
 */
package fun.logicpro.ds;

/**
 * @author Pramod Khare
 * Spaces in string is replaced with '%20'
 * As in java there is no pointers, so using char[]
 */
public class StringReplaceUsingCharArray {

	/**
	 * As in Java, Strings are separate objects, we will simulate the intention
	 * of this question using char[] and with'\0' as ending character
	 * 
	 * Steps:- 	1) given array is of limited size
	 * 			2) given array has more space and can contain new
	 * 			   new string after for replacement chars
	 * 
	 * 1) --> solution steps -->
	 *  	first iterate through char[] to find out total number of
	 *  	spaces i.e. ' ' characters, then
	 *  	allocate new char[] of size = old char array-size + 2 * no of space chars
	 *  	create to pointers or counters one keeping counts of chars in old array
	 *  	and other keeping counts in new array -both start at 0
	 *  	then, iterate over old char array, 
	 *  	copy all chars from old array to new array until you get space char
	 *  	and instead of space char then put '%', '2', '0' chars into new array. 
	 *  	repeat until you get '\0' end char to end the loop 
	 */
	public StringReplaceUsingCharArray() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Solution 1 
	 * @param args
	 */
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		*//**
		 * Type 1 Problem - old array is of limited size
		 *//*
		char [] ch = {' ','h', 'a', ' ', 'p', 'p', ' ', 'y', ' ', '\0'};
		//Display char array values
		for(int i=0; i<ch.length;){
			System.out.print("\t"+ch[i++]);
		}
		
		int intTotalArrayLenth = 0;
		int intNumOfSpaces = 0;
		
		while(ch[intTotalArrayLenth]!='\0'){
			if(ch[intTotalArrayLenth++] ==' ')
				intNumOfSpaces++;
		}
		System.out.println("\nintNumOfSpaces - "+intNumOfSpaces);
		System.out.println("intTotalArrayLenth - "+(++intTotalArrayLenth));
		
		int intNewArrayLength = intTotalArrayLenth + intNumOfSpaces*2;
		System.out.println(intNewArrayLength);
		
		char[] chNew = new char[intNewArrayLength];
		int counter = 0;
		int counter2 = 0;
		while(counter < intTotalArrayLenth){
			if(ch[counter] == ' '){
				chNew[counter2++] = '%';
				chNew[counter2++] = '2';
				chNew[counter2++] = '0';
				counter++;
			}else{
				chNew[counter2++] = ch[counter++];
			}
		}
		
		System.out.println("\nChNew length - "+chNew.length+" and Array - \n");
		//Display char array values
		for(int i=0; i<chNew.length;){
			System.out.print("\t"+chNew[i++]);
		}
	}*/
	
	/**
	 * Solution 2 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/**
		 * Type 1 Problem - same array with two pointers
		 */
		char [] ch = {	'h', 'a', ' ', 'p', 'p', ' ', 'y', ' ', '\0', 
						' ', ' ', ' ', ' ', ' ', ' '};
		
		//Display char array values
		for(int i=0; i<ch.length;){
			System.out.print("\t"+ch[i++]);
		}
		
		int intTotalArrayLenth = 0;
		int intNumOfSpaces = 0;
		
		while(ch[intTotalArrayLenth]!='\0'){
			if(ch[intTotalArrayLenth++] ==' ')
				intNumOfSpaces++;
		}
		System.out.println("\nintNumOfSpaces - "+intNumOfSpaces);
		System.out.println("intTotalArrayLenth - "+(++intTotalArrayLenth));
		
		int intNewArrayLength = intTotalArrayLenth + intNumOfSpaces*2;
		System.out.println(intNewArrayLength--);
		
		while(intTotalArrayLenth-- > 0){
			if(ch[intTotalArrayLenth] == ' '){
				ch[intNewArrayLength--] = '0';
				ch[intNewArrayLength--] = '2';
				ch[intNewArrayLength--] = '%';
			}else{
				ch[intNewArrayLength--] = ch[intTotalArrayLenth];
			}
		}
		
		System.out.println("\nch length - "+ch.length+" and Array - \n");
		//Display char array values
		for(int i=0; i<ch.length;){
			System.out.print("\t"+ch[i++]);
		}
	}
}
