package fun.logicpro.ds;
/**
 * @author Pramod khare
 * Implementation of double ended queue, supporting 
 * addition and removal from both sides
 * 
 * ADT deque Interface/API has following operations/methods
 * insertLeft(), InsertRight(), RemoveLeft(), RemoveRight()
 * isFull(), isEmpty(), leftPeek(), rightPeek()
 * 
 * There are at least two common ways to efficiently implement a deque:
 * 1) with a modified dynamic array or 
 * 2) with a doubly linked list.
 */
public class DequeueImpl {
	public DequeueImpl() {}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DequeWithSingleLinkedList<String> dq = 
				new DequeWithSingleLinkedList<String>(5);
		try {
			dq.insertLeft("first");
			System.out.println(dq.frontPeek());
			System.out.println(dq.rearPeek());
			System.out.println("Left - "+ dq.leftEnd.value + " - Right - " + dq.rightEnd.value + " - nElement - " +dq.nElements);
			
			System.out.println(dq.removeLeft());
			System.out.println("Left - "+dq.leftEnd + " - Right - " + dq.rightEnd + " - nElement - " +dq.nElements);
			
			dq.insertLeft("first");
			dq.insertLeft("second");
			dq.insertLeft("Third");
			
			dq.display();
			
			dq.insertRight("Fourth");
			dq.insertRight("fifth");
			System.out.println("Left - "+ dq.leftEnd.value + " - Right - " + dq.rightEnd.value + " - nElement - " +dq.nElements);
			
			
			// Cause Exception
			// dq.insertLeft("Extra");
			// System.out.println("Left - "+ dq.leftEnd.value + " - Right - " + dq.rightEnd.value + " - nElement - " +dq.nElements);
			
			System.out.println(dq.removeLeft());
			System.out.println("Left - "+ dq.leftEnd.value + " - Right - " + dq.rightEnd.value + " - nElement - " +dq.nElements);
			dq.display();
			
			System.out.println(dq.removeRight());
			dq.display();
			System.out.println("Left - "+ dq.leftEnd.value + " - Right - " + dq.rightEnd.value + " - nElement - " +dq.nElements);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}


/**
 * Double Ended Queue using Single Linked list
 * @author Pramod khare
 * @param <T>
 */
class DequeWithSingleLinkedList<T>{
	Node<T> leftEnd;		// left or front
	Node<T> rightEnd;		// right or rear
	int maxSize;			// max size of deque
	int nElements;			// current elements in deque
	
	class Node<T>{
		T value;
		Node<T> next;
		
		Node(T value, Node<T> next){
			this.next = next;
			this.value = value;
		}
		
		Node(){
			value = null;
			next = null;
		}
	}
	
	public DequeWithSingleLinkedList(int maxSize){
		leftEnd = null;
		rightEnd = null;
		nElements = 0;
		this.maxSize = maxSize;
	}
	
	public boolean isFull(){
		return (nElements == maxSize);  
	}
	
	public boolean isEmpty(){
		return (nElements == 0);
	}
	
	public T insertLeft(T newElement) throws Exception{
		if(nElements == maxSize){
			throw new Exception("Dequeue is full, cannot insert new item");
		}
		Node<T> newLeft = new Node<T>(newElement, null);
		if(leftEnd != null){
			newLeft.next = leftEnd;
		}else{
			rightEnd = newLeft;
		}
		nElements++;
		leftEnd = newLeft;
		return newElement;
	}
	
	public T insertRight(T newElement) throws Exception{
		if(nElements == maxSize){
			throw new Exception("Dequeue is full, cannot insert new item");
		}
		Node<T> newRight = new Node<T>(newElement, null);
		if(rightEnd != null){
			rightEnd.next = newRight;
		}else{
			leftEnd = newRight;
		}
		nElements++;
		rightEnd = newRight;
		return newElement;
	}
	
	public T removeLeft() throws Exception{
		if(nElements == 0){
			throw new Exception("Queue is empty, cannot remove elements");
		}
		T oldLeftEndValue = leftEnd.value;
		leftEnd = leftEnd.next;
		if(leftEnd == null){
			rightEnd = null;
		}
		nElements--;
		return oldLeftEndValue;
	}
	
	/**
	 * In case of Single Linked List --> we don't have previous pointer,
	 * So traverse from start till we get next == null and keep both previous
	 * and last and this previous will become our new rearEnd or rightEnd
	 * 
	 * AND that is the main reason for not using single linked list for
	 * Double Ended Queue Implementation
	 * @return
	 * @throws Exception
	 */
	public T removeRight() throws Exception{
		if(nElements == 0){
			throw new Exception("Queue is empty, cannot remove elements");
		}
		// finding last but previous
		Node<T> currentNode = leftEnd;
		Node<T> previous = leftEnd;
		while(currentNode.next != null){
			previous = currentNode;
			currentNode = currentNode.next;
		}
		
		T oldLeftEndValue = rightEnd.value;
		rightEnd = previous;
		if(rightEnd == null){
			leftEnd = null;
		}else{
			rightEnd.next = null;
		}
		nElements--;
		return oldLeftEndValue;
	}
	
	public T frontPeek() throws Exception{
		if(nElements == 0){
			throw new Exception("Queue is empty, cannot peek element");
		}
		return leftEnd.value;
	}
	
	public T rearPeek() throws Exception{
		if(nElements == 0){
			throw new Exception("Queue is empty, cannot peek element");
		}
		return rightEnd.value;
	}
	
	public void display(){
		System.out.println("Elements in this Queue - \n");
		Node<T> currentNode = leftEnd;
		while(currentNode != null){
			System.out.print("\t"+currentNode.value);
			currentNode = currentNode.next;
		}
		System.out.println();
	}
}

/**
 * Double Ended Queue using Double Linked list
 * @author Pramod khare
 * @param <T>
 */
class DequeWithDoubleLinkedList<T>{
	Node<T> leftEnd;		// left or front
	Node<T> rightEnd;		// right or rear
	int maxSize;			// max size of deque
	int nElements;			// current elements in deque
	
	private class Node<T>{
		T value;
		Node<T> next;
		Node<T> previous;
		
		Node(T value){
			this.next = null;
			this.previous = null;
			this.value = value;
		}
		
		Node(){
			this.value = null;
			this.previous = null;
			this.next = null;
		}
	}
	
	DequeWithDoubleLinkedList(int maxSize){
		leftEnd = null;
		rightEnd = null;
		nElements = 0;
		this.maxSize = maxSize;
	}
	
	public boolean isFull(){
		return (nElements == maxSize);  
	}
	
	public boolean isEmpty(){
		return (nElements == 0);
	}
	
	public T insertLeft(T newElement) throws Exception{
		if(nElements == maxSize){
			throw new Exception("Dequeue is full, cannot insert new item");
		}
		Node<T> newLeft = new Node<T>(newElement);
		if(leftEnd != null){
			newLeft.next = leftEnd;
			leftEnd.previous = newLeft;
		}
		nElements++;
		leftEnd = newLeft;
		return newElement;
	}
	
	public T insertRight(T newElement) throws Exception{
		if(nElements == maxSize){
			throw new Exception("Dequeue is full, cannot insert new item");
		}
		Node<T> newRight = new Node<T>(newElement);
		if(rightEnd != null){
			rightEnd.next = newRight;
			newRight.previous = rightEnd;
		}
		nElements++;
		rightEnd = newRight;
		return newElement;
	}
	
	public T removeLeft() throws Exception{
		if(nElements == 0){
			throw new Exception("Queue is empty, cannot remove elements");
		}
		T oldLeftEndValue = leftEnd.value;
		leftEnd = leftEnd.next;
		leftEnd.previous = null;
		nElements--;
		return oldLeftEndValue;
	}
	
	/**
	 * remove last element
	 * @return
	 * @throws Exception
	 */
	public T removeRight() throws Exception{
		if(nElements == 0){
			throw new Exception("Queue is empty, cannot remove elements");
		}
		
		T oldLeftEndValue = rightEnd.value;
		rightEnd = rightEnd.previous;
		rightEnd.next = null;
		nElements--;
		return oldLeftEndValue;
	}
	
	public T frontPeek() throws Exception{
		if(nElements == 0){
			throw new Exception("Queue is empty, cannot peek element");
		}
		return leftEnd.value;
	}
	
	public T rearPeek() throws Exception{
		if(nElements == 0){
			throw new Exception("Queue is empty, cannot peek element");
		}
		return rightEnd.value;
	}
	
	public void display(){
		System.out.println("Elements in this Queue - \n");
		Node<T> currentNode = leftEnd;
		while(currentNode != null){
			System.out.print("\t"+currentNode.value);
			currentNode = currentNode.next;
		}
		System.out.println();
	}
}

/**
 * Double Ended Queue using dynamic array list
 * @author Pramod khare
 * @param <T>
 */
class DequeWithArrayList<T>{
	
}
