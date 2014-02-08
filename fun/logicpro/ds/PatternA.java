package fun.logicpro.ds;

public class PatternA {
	
	public PatternA() {
	}

	/**
	 * By - Pramod khare
	 *         **      
		      *  *     
		     *    *    
		    ********   
		   *        *  
		  *          * 
		 *            *
	
	For User input - 7
		
	 * @param size
	 */
	
	public static void getPatternA(int size){
		for(int i=1;i<=size;i++){
			for(int j=0;j<=(2*size);j++){
				if((j==(i+size)) || (j == (size-(i-1)))){
					System.out.print("*");
				}else if(i==((size/2)+1)){
					if((j>= i) && (j<=(i+size-1))){
						System.out.print("*");
					}else{
						System.out.print(" ");
					}
				}else{
					System.out.print(" ");
				}
			}
			System.out.print("\n");
		}
	}
	
	public static void main(String[] args) {
		getPatternA(6);
	}
}
