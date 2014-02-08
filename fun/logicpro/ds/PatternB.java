package fun.logicpro.ds;

public class PatternB {

	public PatternB() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * By - Pramod khare
	 *          **** 
				*   *
				*   *
				*   *
				**** 
				*   *
				*   *
				*   *
				**** 
	
	For User input - 9
		
	 * @param lineNo
	 */
	
	public static void getPatternB(int size){
		for(int i=1;i<=size;i++){
			for(int j=1;j<=((size/2)+1);j++){
				if((i==1) && (j == ((size/2)+1))){
					System.out.print(" ");
				}else if((i==((size/2)+1)) && (j == ((size/2)+1))){
					System.out.print(" ");
				}else if((i == size) && (j ==((size/2)+1))){
					System.out.print(" ");
				}else{
					if(i == 1 || j ==1 || i == ((size/2)+1) || j == ((size/2)+1) || i == size){
						System.out.print("*");
					}else{
						System.out.print(" ");
					}
				}
			}
			System.out.print("\n");
		}
	}
	
	public static void main(String[] args) {
		getPatternB(9);
	}

}
