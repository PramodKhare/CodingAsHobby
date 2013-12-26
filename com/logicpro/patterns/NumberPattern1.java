package com.logicpro.patterns;

public class NumberPattern1 {
	
	public NumberPattern1() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * By - Pramod khare
	 * 	1
		2	3
		4	5	6
		7	8	9	10
		11	12	13	14	15
		...
		...
		
	 * @param lineNo
	 */
	
	public static void getPattern1(int lineNo){
		int m=1;
		for(int i=1;i<=lineNo;i++){
			for(int j=0;j<i;j++){
				System.out.print("\t"+m++);
			}
			System.out.println("");
		}
	}
	

	public static void main(String ar[]){
		getPattern1(5);
	}
}
