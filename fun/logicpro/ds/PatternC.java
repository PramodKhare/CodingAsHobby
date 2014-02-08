package fun.logicpro.ds;

public class PatternC {
	
	public PatternC() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * By - Pramod khare
	 *           ***
				*   
				*   
				*   
				*   
				 ***
	
	For User input - 6
		
	 * @param lineNo
	 */
	
	public static void getPatternC(int size){
		for(int i=1;i<=size;i++){
			for(int j=1;j<=((size/2)+1);j++){
				if((i==1 && j ==1) || (i==size && j==1)){
					System.out.print(" ");
				}else if((i == size) || (j == 1) || (i == 1)){
					System.out.print("*");
				}else{
					System.out.print(" ");
				}
			}
			System.out.print("\n");
		}
	}
	
	public static void main(String[] args) {
		getPatternC(6);
	}
}
