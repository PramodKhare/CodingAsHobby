/**
 * @author Pramod Khare
 */
package fun.logicpro.ds;

import java.util.ArrayList;

import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * @author Pramod Khare
 * Different Stack Implementation using Array and ArrayList
 * 
 * Also contains programming questions generally solved using Stack Data-structures
 */
public class stackImpl {
	public static void main(String[] args) {
		
		/***********************************************************************/
		//Java Stack implementation
		/*java.util.Stack<Integer> t = new java.util.Stack<Integer>();
		Integer int1 = t.push(null);
		t.push(56);
		t.push(32);
		t.isEmpty();
		t.peek();
		t.pop();*/
		
		/***********************************************************************/
		// Stack with array i.e. constant sized array implementation
		/***********************************************************************/
		StackUsingFixedSizedArray theStack = new StackUsingFixedSizedArray(10); // make new stack
		try {
			theStack.push("First");
			theStack.push("Second");
			theStack.push("Third");
			theStack.push("Fourth");
			while(!theStack.isEmpty()){ 	
				// delete item from stack
				String value = theStack.pop();
				System.out.print(value); // display it
				System.out.print(" "+theStack.getSize());
			} // end while
			System.out.println("\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // push items onto stack
		
		/***********************************************************************/
		// Stack ArrayList i.e. dynamic array implementation
		/***********************************************************************/
		// Stack 
		StackUsingArrayList<Integer> stack2 = new StackUsingArrayList<Integer>();
		try {
			stack2.push(44);
			stack2.push(33);
			stack2.push(22);
			stack2.push(11);
			stack2.push(10);
			stack2.push(9);
			stack2.push(8);
			while(!stack2.isEmpty()){ 	
				// delete item from stack
				int value = stack2.pop();
				System.out.print(value); // display it
				System.out.print(" "+stack2.getSize());
			} // end while
			System.out.println("\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // push items onto stack
		/***********************************************************************/
		// Stack linked List implementation
		/***********************************************************************/
		StackUsingLinkedList<String> stack3 = new StackUsingLinkedList<String>();
		try {
			stack3.push("LinkedListNode1");
			stack3.push("LinkedListNode2");
			stack3.push("LinkedListNode3");
			stack3.push("LinkedListNode4");
			stack3.push("LinkedListNode5");
			while(!stack3.isEmpty()){
				System.out.println(" "+stack3.getSize());
				// delete item from stack
				String value = stack3.pop();
				System.out.println(value); // display it
			} // end while
			System.out.println("\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // push items onto stack
		
		/***********************************************************************/
		//Applications of stack --> 
		/***********************************************************************/
		// 1. To reverse the string
		// Push one by one all characters from this string into an empty stack 
		// and pop all the characters one by one until stack is empty, append 
		// these popped characters to get reversed string
		/***********************************************************************/
		
		
		/***********************************************************************/
		// 2. Converting a decimal number into a binary number
		// Modulo with 2 --> push remainder into stack --> divide no by 2 --> 
		// repeat loop until number is greater than 0
		/***********************************************************************/
		
		
		/***********************************************************************/
		// 3. Evaluation of an Infix Expression that is Fully Parenthesized
		// Using Dijkstra's Two Stack Algorithm -->
		// Opening Parenthesis --> ignore it
		// Digits --> push it onto Value Stack
		// Operator --> Push it onto Operator stack
		// Closing Parenthesis --> Pop Two values from Value stack and 
		//                         One Operator from operator stack 
		//                         solve it and put/push the answer
		// 						   onto value stack
		// Repeat the procedure until there operator stack is empty 
		/***********************************************************************/
		
		/***********************************************************************/
		// 3. Tower of Hanoi --> three rods and multiple discs in ascending 
		// order kept in first spindle/rod, move them to second rod such that
		// They are in the same order, third spindle or rod can be used for 
		// temporary swapping
		/***********************************************************************/
		
		// totalDisks, fromPeg, toPeg ---> using recursion
		// solveTowerOfHanoi(3, 1, 2);
		
		// Using Stack --> use 3 stacks -->
		// First Stack will have all the disks at start
		// n == total number of disks
		int n = 5;
		StackUsingFixedSizedArray [] towers = new StackUsingFixedSizedArray[4];
		towers[1] = new StackUsingFixedSizedArray(100);
		towers[2] = new StackUsingFixedSizedArray(100);
		towers[3] = new StackUsingFixedSizedArray(100);
		
		for(int i = n; i > 0; i--){
			try {
				towers[1].push(String.valueOf(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		solveTowerOfHanoiUsingStacks(towers, n, 1, 3);
		displayStacks(towers, n);
		// 4. Check Delimiter matching --> check if opening and closing parenthesis 
		// are correct i.e. they are properly closed or not
		// Using Stack --> 
		// To solve, we read through characters and when any opening brace comes
		// we push it to stack and when any closing brace comes in string we pop
		// the last parenthesis, so in the end the stack should be empty 
		// or if its empty and closing parenthesis is found its an error.
		
		// advanced version --> while popping check if opening braces (to be popped)
		// and the closing braces are the same or not.
		// if mismatch along with upper conditions, there is an error.
	}
	
	@SuppressWarnings("unchecked")
	
	
	public static void solveTowerOfHanoi(int noOfDisks, int fromPeg, int toPeg){
		// Trivial Case -- when noOfDisks == 1 --> then direct move
		if(noOfDisks == 1){
			System.out.println("Move disk from Peg -"+fromPeg+" to Peg - "+toPeg);
		}else{
			int helpPeg = 6 - fromPeg - toPeg;
			System.out.println("HelpPeg - "+helpPeg);
			//To Solve remaining problem split it into two 
			solveTowerOfHanoi (noOfDisks-1, fromPeg, helpPeg);
			System.out.println("Move disk from Peg -"+fromPeg+" to Peg - "+toPeg);
			solveTowerOfHanoi (noOfDisks-1, helpPeg, toPeg);
		}
	}
	
	public static void displayStacks(StackUsingFixedSizedArray[] towers, int N) {
		System.out.println("  1  |  2  |  3");
		System.out.println("---------------");
		System.out.println("  " + towers[1].topOfStack + "  |  " + towers[2].topOfStack + "  |  " + towers[3].topOfStack);
		for (int i = N - 1; i >= 0; i--) {
			try {
				String d1 = " ", d2 = " ", d3 = " ";
				
				if(!towers[1].isEmpty())
					d1 = String.valueOf(towers[1].pop());
				
				if(!towers[2].isEmpty())
					d2 = String.valueOf(towers[2].pop());
				
				if(!towers[3].isEmpty())
					d3 = String.valueOf(towers[3].pop());
				
				System.out.println("  " + d1 + "  |  " + d2 + "  |  " + d3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("\n");
	}
	
	public static void solveTowerOfHanoiUsingStacks(StackUsingFixedSizedArray[] towers, int noOfDisks, int fromPeg, int toPeg){
		// Trivial Case -- when noOfDisks == 1 --> then direct move
		if(noOfDisks > 0){
			int helpPeg = 6 - fromPeg - toPeg;
			System.out.println("HelpPeg - "+helpPeg);
			//To Solve remaining problem split it into two 
			solveTowerOfHanoiUsingStacks (towers, noOfDisks-1, fromPeg, helpPeg);
			System.out.println("Move disk from Peg -"+fromPeg+" to Peg - "+toPeg);
			try {
				String temp = towers[fromPeg].pop();
				towers[toPeg].push(temp);
				System.out.println("Value Pushed onto stack toStack "+towers[toPeg].topOfStack);
			} catch (Exception e) {
				e.printStackTrace();
			}
			solveTowerOfHanoiUsingStacks (towers, noOfDisks-1, helpPeg, toPeg);
		}
	}
}

//First Stack implementation - fixed capacity stack using array
class StackUsingFixedSizedArray {
	public int topOfStack; 	// is a 1-based pointer i.e. not a 0-based pointer i.e. unlike array indexes 
	String[] stack = null; // actual array for storing elements into stack
	int maxSize;   		// maximum array size i.e. maximum stack size possible
	
	/**
	 * Constructor
	 * @param stackSize
	 */
	public StackUsingFixedSizedArray(int stackSize) {
		this.maxSize = stackSize;
		stack = new String[stackSize];
		topOfStack=-1;
	}
	
	// API methods implementation
	/**
	 * remove top of stack element and decrement stack pointer by one
	 * @return - remove top of stack element and decrement stack pointer by one
	 */
	public String pop() throws Exception{
		// Check if stack is empty then throw Exception - EmptyStackException
		if(isEmpty()){
			throw new Exception("Stack Empty Exception");
		}
		// TWO IMP steps:- 
		// First remove the element of the stack
		// then decrement the stack pointer
		String item = stack[topOfStack];
		stack[topOfStack--] = null;
		return item;
	}
	
	/**
	 * Method to push element on top of stack
	 * @param newElement
	 * @return newElement which is pushed on top of stack 
	 * @throws Exception 
	 */
	public String push(String newElement) throws Exception{
		// Check if stack is full then throw Exception - StackOverflowException
		if(isFull()){
			throw new Exception("Stack Overflow Exception");
		}
		// Two IMP Steps - 
		// first increment the top of stack pointer
		// then store the new element at this new top of stack
		stack[++topOfStack]=newElement;
		return stack[topOfStack];
	}
	
	/**
	 * Get top of stack element without removing it of the stack
	 * @return gets the top element from stack
	 */
	public String peek() throws Exception{
		// Check if stack is empty then throw Exception - EmptyStackException
		if(isEmpty()){
			throw new Exception("Stack Empty Exception");
		}
		// Return the top of stack element
		return stack[topOfStack];
	}
	
	/**
	 * Check if stack is empty
	 * @return boolean
	 */
	public boolean isEmpty() {
		return (topOfStack==-1);
	}
	
	/**
	 * Check if stack is full
	 * @return boolean
	 */
	public boolean isFull() {
		return (topOfStack==maxSize-1);
	}
	
	/**
	 * get size of stack - total number of elements on the stack
	 * @return boolean
	 */
	public int getSize() {
		return (topOfStack+1);
	}
}


//Second Stack implementation - dynamic capacity stack using arraylist using generics
class StackUsingArrayList<T> {
	int topOfStack; 			// is a 1-based pointer i.e. not a 0-based pointer i.e. unlike array indexes 
	ArrayList<T> stack = null; 	// actual array for storing elements into stack
	
	/**
	 * Constructor
	 * @param stackSize
	 */
	public StackUsingArrayList() {
		stack = new ArrayList<T>();
		topOfStack=-1;
	}
	
	// API methods implementation
	/**
	 * remove top of stack element and decrement stack pointer by one
	 * @return - remove top of stack element and decrement stack pointer by one
	 */
	public T pop() throws Exception{
		// Check if stack is empty then throw Exception - EmptyStackException
		if(isEmpty()){
			throw new Exception("Stack Empty Exception");
		}
		// TWO IMP steps:- 
		// First remove the element of the stack
		// then decrement the stack pointer
		T item = stack.get(topOfStack);
		stack.set(topOfStack--, null);
		return item;
	}
	
	/**
	 * Method to push element on top of stack
	 * @param newElement
	 * @return newElement which is pushed on top of stack 
	 * @throws Exception 
	 */
	public T push(T newElement) throws Exception{
		// Two IMP Steps - 
		// first increment the top of stack pointer
		// then store the new element at this new top of stack
		stack.add(newElement);
		return stack.get(++topOfStack);
	}
	
	/**
	 * Get top of stack element without removing it of the stack
	 * @return gets the top element from stack
	 */
	public T peek() throws Exception{
		// Check if stack is empty then throw Exception - EmptyStackException
		if(isEmpty()){
			throw new Exception("Stack Empty Exception");
		}
		// Return the top of stack element
		return stack.get(topOfStack);
	}
	
	/**
	 * Check if stack is empty
	 * @return boolean
	 */
	public boolean isEmpty() {
		return topOfStack==-1;
	}
	
	/**
	 * get size of stack - total number of elements on the stack
	 * @return boolean
	 */
	public int getSize() {
		return (topOfStack+1);
	}
}


// Third --> Stack using LinkedList
class StackUsingLinkedList<T>{ 
	Node first; 	//Top of stack element reference
	
	private class Node{
		T value;
		Node next;
		
		Node(T value, Node next){
			this.value = value;
			this.next = next;
		}
		
		Node(){
			this.value = null;
			this.next = null;
		}
	}
	
	/**
	 * Constructor
	 * @param stackSize
	 */
	public StackUsingLinkedList() {
		first = null;
	}
	
	// API methods implementation
	/**
	 * remove top of stack element and decrement stack pointer by one
	 * @return - remove top of stack element and decrement stack pointer by one
	 */
	public T pop() throws Exception{
		// Check if stack is empty then throw Exception - EmptyStackException
		if(isEmpty()){
			throw new Exception("Stack Empty Exception");
		}
		Node oldFirst = first; 
		first = oldFirst.next;
		return oldFirst.value;
	}
	
	/**
	 * Method to push element on top of stack
	 * @param newElement
	 * @return newElement which is pushed on top of stack 
	 * @throws Exception 
	 */
	public T push(T newElement) throws Exception{
		// create a new node with given value 
		Node oldFirst = first;
		Node newFirst = new Node(newElement, first);
		first = newFirst;
		return first.value;
	}
	
	/**
	 * Get top of stack element without removing it of the stack
	 * @return gets the top element from stack
	 */
	public T peek() throws Exception{
		// Check if stack is empty then throw Exception - EmptyStackException
		if(isEmpty()){
			throw new Exception("Stack Empty Exception");
		}
		// Return the top of stack element
		return first.value;
	}
	
	/**
	 * Check if stack is empty
	 * @return boolean
	 */
	public boolean isEmpty() {
		return (first==null);
	}
	
	/**
	 * get size of stack - total number of elements on the stack
	 * @return boolean
	 */
	public int getSize() {
		Node temp = first;
		int totalNodesCount = 0;
		while(temp != null){
			totalNodesCount++;
			temp = temp.next;
		}
		return totalNodesCount;
	}
}