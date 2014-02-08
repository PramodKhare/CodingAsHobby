/**
 * @author Pramod Khare
 */
package fun.logicpro.ds;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Pramod Khare
 * Queue Data-structure implementation using LinkedLists or Arrays
 * http://www.cs.colostate.edu/~anderson/cs200/index.html/doku.php?id=recit:array_based_queue
 * http://oppansource.com/queue-implementation-in-java-using-circular-array/
 * 
 * 	Circular Queue doubt --> 
 * 	https://stackoverflow.com/questions/17239317/circular-queue-without-wasting-an-entry-or-using-counter
 * 
 * Is it possible to implement a circular queue by use of an array, 
 * without having a counter to count the number of items in the queue 
 * or without wasting any entry of the array---->

 * 	It's not possible , let's assume that we have two pointers front and rear, the first one points to the first element of the queue ,
	
 * 	we can define the rear pointer in two ways :
	
 *	1.It points to the last element which was inserted into the queue , so the next entry is the possible place for the next element which will be inserted
	
 *	2.It points to the place where the next element is going to be inserted
	
 *	In either case we cannot distinguish between full & empty queue
 */
public class QueuesImpl {
	public static void main(String[] args) {
		//Default Queue implementation in java
		
		// LinearQueueUsingArray
		/*LinearQueueUsingArray<String> q = new LinearQueueUsingArray<String>(10);
		try {
			q.enqueue("First");
			q.enqueue("Second");
			q.enqueue("Third");
			q.enqueue("Fourth");
			System.out.println(q.nElements);
			System.out.println(q.rear);
			System.out.println(q.front);
			
			q.enqueue("First1");
			q.enqueue("Second1");
			q.enqueue("Third1");
			q.enqueue("Fourth1");
			System.out.println(q.nElements);
			System.out.println(q.rear);
			System.out.println(q.front);
			
			q.enqueue("First2");
			q.enqueue("Second2");
			System.out.println(q.nElements);
			System.out.println(q.rear);
			System.out.println(q.front);
			
			while(!q.isEmpty()){
				System.out.println(q.dequeue());
			}
			System.out.println(q.nElements);
			System.out.println(q.rear);
			System.out.println(q.front);
			q.enqueue("Third2");
			q.enqueue("Fourth2");
			while(!q.isEmpty()){
				System.out.println(q.dequeue());
			}
			System.out.println(q.nElements);
			System.out.println(q.rear);
			System.out.println(q.front);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		// LinearQueueUsingLinkedList
		
		// CircularQueueUsingArray - using counter to keep track, if queue is full or empty
		/*CircularQueueUsingArrayWithCount<String> cq = 
		 					new CircularQueueUsingArrayWithCount<String>(5);
		try{
			cq.enqueue("First");
			System.out.println(cq.nElements+" "+cq.rear+" "+cq.front);
			
			cq.enqueue("Second");
			System.out.println(cq.nElements+" "+cq.rear+" "+cq.front);
			
			System.out.println(cq.dequeue()+" "+cq.nElements+" "+cq.rear+" "+cq.front);
			
			cq.enqueue("Third");
			System.out.println(cq.nElements+" "+cq.rear+" "+cq.front);
			
			cq.enqueue("Fourth");
			System.out.println(cq.nElements+" "+cq.rear+" "+cq.front);
			
			cq.enqueue("Fifth");
			System.out.println(cq.nElements+" "+cq.rear+" "+cq.front);
			
			System.out.println(cq.dequeue()+" "+cq.nElements+" "+cq.rear+" "+cq.front);
			System.out.println(cq.dequeue()+" "+cq.nElements+" "+cq.rear+" "+cq.front);
			System.out.println(cq.dequeue()+" "+cq.nElements+" "+cq.rear+" "+cq.front);
			
			// Wrapping of front pointer as the last element is removed
			System.out.println(cq.dequeue()+" "+cq.nElements+" "+cq.rear+" "+cq.front);
			
			// Wrapping of rear pointer
			cq.enqueue("Sixth");
			System.out.println(cq.nElements+" "+cq.rear+" "+cq.front);
			// Removing the only last remaining element - making queue empty
			System.out.println(cq.dequeue()+" "+cq.nElements+" "+cq.rear+" "+cq.front);
			
			// Trying to remove from an empty queue - should give me exception 
			System.out.println(cq.dequeue()+" "+cq.nElements+" "+cq.rear+" "+cq.front);
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
		// Circular Queue using Array whose size is plus 1 than given,
		// No extra variables, counters, boolean flags used to find if queue is 
		// full or empty
		CircularQueueUsingArrayWithExtraSpace<String> cq1 = 
				new CircularQueueUsingArrayWithExtraSpace<String>(5);
		try{
			System.out.println("Queue Size given - "+cq1.maxItems+" - actual - "+(cq1.maxItems+1));
			System.out.println(cq1.rear+" "+cq1.front);
			
			cq1.enqueue("First");
			System.out.println(cq1.rear+" "+cq1.front);
			
			cq1.enqueue("Second");
			System.out.println(cq1.rear+" "+cq1.front);
			
			cq1.enqueue("Third");
			System.out.println(cq1.rear+" "+cq1.front);
			
			cq1.enqueue("Fourth");
			System.out.println(cq1.rear+" "+cq1.front);
			
			cq1.enqueue("Fifth");
			System.out.println(cq1.rear+" "+cq1.front);
			
			// This enqueue call should result in Exception that queue is full,
			// as all first five elements are already inserted
			cq1.enqueue("Fullth");
			System.out.println(cq1.rear+" "+cq1.front);
			cq1.display();
			
			System.out.println(cq1.dequeue()+" "+cq1.rear+" "+cq1.front);
			
			/*cq1.enqueue("Fullth1");
			System.out.println(cq1.rear+" "+cq1.front);
			
			cq1.display();
			System.out.println(cq1.dequeue()+" "+cq1.rear+" "+cq1.front);
			System.out.println(cq1.dequeue()+" "+cq1.rear+" "+cq1.front);
			System.out.println(cq1.dequeue()+" "+cq1.rear+" "+cq1.front);
			cq1.enqueue("Fullth2");
			System.out.println(cq1.rear+" "+cq1.front);*/
			
			cq1.display();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// Last 2 elements are wasted...
		/*CircularQueue cq = new CircularQueue(5);
		try{			
			cq.enqueue(1);
			System.out.println(cq.tail+" "+cq.head);
			
			cq.enqueue(2);
			System.out.println(cq.tail+" "+cq.head);
			
			cq.enqueue(3);
			System.out.println(cq.tail+" "+cq.head);
			
			cq.enqueue(4);
			System.out.println(cq.tail+" "+cq.head);
			
			cq.enqueue(5);
			System.out.println(cq.tail+" "+cq.head);
		}catch(Exception e){
			e.printStackTrace();
		}*/
		// CircularQueueUsingLinkedList
		
	}
}

/**
 * Queue using already existing LinkedList implementation
 * @author Pramod Khare
 */
class QueueUsingLinkedListInJava<T>{
    public LinkedList<T> list;
    
    public QueueUsingLinkedListInJava(){
        list = new LinkedList<T> (); 
    }
    
    public boolean empty () {
        return list.isEmpty (); 
    }
    
    public void insert (T obj) {
        list.addLast (obj); 
    }
    
    public Object remove () {
        return list.removeFirst (); 
    }
}


/**
 * Important Limitation of Array-based Linear Queue is its limited size,
 * once the front index reaches the end of array, queue is of no use,
 * 
 * Optimization 1 : once the queue front index reaches the end and Queue is empty, 
 * then we reset the pointers of front and head to their initial states 
 * i.e. front points to 0th index and rear to -1 i.e. no item inserted yet 
 * i.e. queue is empty
 *  
 * Optimization 2 : when dequeuing the item null out its reference from array
 * to avoid loitering
 * 
 * @author Pramod Khare
 * @param <T>
 */
class LinearQueueUsingArray<T>{
	// Non circular queue
	T[] queueArray; // Queue array
	int nElements;  // current queue length
	int front; 		// front pointer of queue
	int rear;		// rear pointer of queue
	int maxItems;	// max size of queue
	
	// Constructor
	public LinearQueueUsingArray(int maxQueueLength){
		this.maxItems = maxQueueLength;
		this.nElements = 0;
		this.queueArray = (T[]) new Object[maxQueueLength];
		this.front = 0;
		this.rear = -1;
	}
	
	public T enqueue(T newElement) throws Exception{
		if(nElements == maxItems){	// if queue is full
			throw new Exception("Queue is full, cannot equeue more items");
		}
		// Check if rear and front points to end of array and there is
		// no further space to add in this array,  
		if(nElements == 0 && rear == queueArray.length-1 
				&& front == queueArray.length){
			front = 0;
			rear = -1;
			System.out.println("Queue Pointers Restored");
		}
		queueArray[++rear] = newElement;
		nElements++;
		return newElement;
	}
	
	public T dequeue() throws Exception {
		if(nElements == 0){
			throw new Exception("Queue is empty, cannot dequeue items");
		}
		T temp = queueArray[front];
		queueArray[front++] = null;
		nElements--;
		return temp;
	}
	
	public boolean isFull(){
		return (nElements == maxItems);
	}
	
	public boolean isEmpty(){
		return (nElements == 0);
	}
	
	public T frontPeek() throws Exception {
		if(nElements == 0){
			throw new Exception("Queue is empty, no items peek");
		}
		return queueArray[front];
	}
	
	public T rearPeek() throws Exception {
		if(nElements == 0){
			throw new Exception("Queue is empty, no items peek");
		}
		return queueArray[rear];
	}
}

/**
 * Linear Queue using linked list
 * @author Pramod Khare
 * @param <T>
 */
class LinearQueueUsingLinkedList<T>{
	// Non circular queue
	Node<T> front; 		// Queue front node
	Node<T> rear;
	int nElements;
	int maxItems;		// max size of queue
	
	private class Node<T>{
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
	
	public LinearQueueUsingLinkedList(int maxSize) {
		front = null;
		rear = null;
		nElements = 0;
		maxItems = maxSize;
	}
	
	public boolean isFull(){
		return (nElements == maxItems);  
	}
	
	public boolean isEmpty(){
		return (nElements == 0);
	}
	
	// IMP Steps -
	// first check if queue is full
	// then check if rear itself is null, meaning queue itself is empty
	// create new Node with next pointer as null
	// point old rear's next pointer to this newly created node
	// increment number of elements in queue by one
	public T enqueue(T newElement) throws Exception{
		if(nElements == maxItems){
			throw new Exception("Queue is empty, cannot dequeue items");
		}
		Node<T> newRear = new Node<T>(newElement, null);
		if(rear != null){
			rear.next = newRear;
		}
		nElements++;
		rear = newRear;
		return newElement;
	}
	
	// IMP Steps -
	// first check if front is null, meaning queue itself is empty
	// get old front's value, which will be returned
	// get oldfront's next node and make it as new front
	// decrement the number of elements in queue by one
	public T dequeue() throws Exception{
		if(front == null || nElements == 0){
			throw new Exception("Queue is empty, cannot dequeue elements");
		}
		T oldFrontValue = front.value;
		front = front.next;
		nElements--;
		return oldFrontValue;
	}
	
	public T frontPeek() throws Exception{
		if(front == null){
			throw new Exception("Queue is empty, cannot peek element");
		}
		return front.value;
	}
	
	public T rearPeek() throws Exception{
		if(rear == null){
			throw new Exception("Queue is empty, cannot peek element");
		}
		return rear.value;
	}
}

/**
 * Circular Queue using array - where pointers gets wrapped around when they reach
 * the end of an underlying array --> overwriting elements is not possible in this
 * implementation
 * 
 * A counter is used to implement a track of if queue is empty or full
 * @author Pramod Khare
 * @param <T>
 */
class CircularQueueUsingArrayWithCount<T>{

	// In circular queue, rear and front pointers wrap around when they reach the
	// max array size
	T[] queueArray; // Queue array
	int front; 		// front pointer of queue
	int rear;		// rear pointer of queue
	int maxItems;	// max size of queue
	int nElements;
	
	// Constructor
	public CircularQueueUsingArrayWithCount(int maxQueueLength){
		this.maxItems = maxQueueLength;
		this.queueArray = (T[]) new Object[maxQueueLength];
		this.front = 0;				// To dequeue 0th element in first dequeue operation
		this.rear = maxQueueLength-1;	// So, first element gets inserted at 0th position
		nElements = 0;				// Keeps count of total number of elements in queue
	}
	
	public void initialize() {
        front = 0;
        rear = maxItems-1;
    }
	
	public T enqueue(T newElement) throws Exception{
		// Approach 1 - Check if queue is full, then donot allow any further item 
		// to be inserted into this queue
		if(nElements == maxItems){
			throw new Exception("Queue is full, remove items to insert new");
		}
		// Approach 2 - Even when its full we just overwrite the oldest elements
		// from wrapped around indexes
		rear = (rear+1 == maxItems)?0:rear+1;
		queueArray[rear] = newElement;
		nElements++;
		return newElement;
	}
	
	public T dequeue() throws Exception {
		// Check if queue is not empty before removing any items from front
		if(nElements == 0){
			throw new Exception("Queue is empty, cannot remove items");
		}
		T value = queueArray[front];
		// nulling out the references to avoid loitering of unused references
		// lying in array even after the elements are removed
		queueArray[front] = null;
		//Wrap around the front pointer
		System.out.println("Old front  - "+front);
		front = (front+1 == maxItems)?0:front+1;
		System.out.println("New front  - "+front);
		nElements--;
		return value;
	}
	
	public boolean isFull(){
		return (nElements == maxItems);
	}
	
	public boolean isEmpty(){
		return (nElements == 0);
	}
	
	public T frontPeek() throws Exception {
		// Condition of empty queue 
		if(nElements == 0){
			throw new Exception("Queue is empty, no items peek");
		}
		return queueArray[front];
	}
	
	public T rearPeek() throws Exception {
		// Condition of empty queue
		if(nElements == 0){
			throw new Exception("Queue is empty, no items peek");
		}
		return queueArray[rear];
	}
	
	public void display() {
		for(int i=0; i<queueArray.length; i++){
			System.out.println("\t"+queueArray[i]+" i - "+i);
		}
	}
}

/**
 * Implementation from CareerCup
 */
class CircularQueue {
    public int size;
    public int head;
    public int tail;
    public int q[];
    public CircularQueue(int s) {
        size = s;
        q = new int[s+1];
        head = 0;
        tail = 0;
    }
    public void initialize() {
        head = 0;
        tail = 0;
    }
    public boolean enqueue(int v) {
        int tmp = (tail+1) % size;
        if (tmp == head) return false;
        q[tail] = v;
        tail = tmp;
        return true;
    }
    public int dequeue() throws Exception{
        if (head == tail) throw new Exception("queue underflow!");
        int tmp = q[head];
        head = (head + 1) % size;
        return tmp;
    }
    public void display() {
		for(int i=0; i<q.length; i++){
			System.out.println("\t"+q[i]+" i - "+i);
		}
	}
}

/**
 * Circular Queue using array - where pointers gets wrapped around when they reach
 * the end of an underlying array --> overwriting elements is possible in this
 * implementation, because there is no way of knowing if queue is either empty
 * or full, so when overwritten --- front pointer is properly incremented to point
 * to oldest element from array
 * 
 * @author Pramod Khare
 * @param <T>
 */


/**
 * Circular Queue using array - where pointers gets wrapped around when they reach
 * the end of an underlying array --> overwriting elements is not possible in this
 * implementation when queue is either empty
 * or full
 *  
 * To check if queue is empty or full we use, an extra array space
 * Initialize array with queueSize +1 space
 * 
 * Definition of the rear pointer:
 * It points to the place where the next element is going to be inserted
 * 
 * Drawback --> Last one element space of array is wasted
 * @author Pramod Khare
 * @param <T>
 * https://en.wikipedia.org/wiki/Circular_buffer#Always_Keep_One_Slot_Open
 */
class CircularQueueUsingArrayWithExtraSpace<T>{
	// In circular queue, rear and front pointers wrap around when they reach the
	// max array size
	T[] queueArray; // Queue array
	int front; 		// front pointer of queue
	int rear;		// rear pointer of queue
	int maxItems;	// max size of queue
	
	// Constructor
	public CircularQueueUsingArrayWithExtraSpace(int maxQueueLength){
		this.maxItems = maxQueueLength+1;
		this.queueArray = (T[]) new Object[maxItems];
		this.front = 0;					// To dequeue 0th element in first dequeue operation
		this.rear = 0;					// So, first element gets inserted at 0th position
	}
	
	public void initialize() {
        front = 0;
        rear = 0;
    }
	
	public boolean isFull(){
		int newRear = (rear+1 == maxItems)?0:rear+1;
		return newRear == front;
	}
	
	public boolean isEmpty(){
		return rear == front;
	}
	
	public T enqueue(T newElement) throws Exception{
		// No Overwrite of elements 
		/*int newRear = (rear+1 == maxItems)?0:rear+1;
		if(newRear == front){
			throw new Exception("Queue is full");
		}*/
		
		// In case of Overwrite is possible, 
		queueArray[rear] = newElement;
		int newRear = (rear+1 == maxItems)?0:rear+1;
		rear = newRear;
		if(rear == front){
			front = (front+1 == maxItems)?0:front+1;
		}
		return newElement;
	}
	
	public T dequeue() throws Exception {
		if(rear == front){
			throw new Exception("Queue is empty, no items remove");
		}
		// Check if queue is not empty before removing any items from front
		T value = queueArray[front];
		// nulling out the references to avoid loitering of unused references
		// lying in array even after the elements are removed
		queueArray[front] = null;
		//Wrap around the front pointer
		front = (front+1 == maxItems)?0:front+1;
		return value;
	}
	
	public T frontPeek() throws Exception {
		// Condition of empty queue 
		if(rear == front){
			throw new Exception("Queue is empty, no items peek");
		}
		return queueArray[front];
	}
	
	public T rearPeek() throws Exception {
		// Condition of empty queue
		if(rear == front){
			throw new Exception("Queue is empty, no items peek");
		}
		return queueArray[rear];
	}
	public void display() {
		for(int i=0; i<queueArray.length; i++){
			System.out.print("\t"+queueArray[i]+" i - "+i);
		}
	}
}

class CicularQueueUsingSingleLinkedList<T>{
	Node<T> front; 		// Queue front node
	Node<T> rear;
	int nElements;
	int maxItems;		// max size of queue
	
	private class Node<T>{
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
	
	public CicularQueueUsingSingleLinkedList(int maxSize) {
		front = null;
		rear = null;
		nElements = 0;
		maxItems = maxSize;
	}
	
	public boolean isFull(){
		return (nElements == maxItems);  
	}
	
	public boolean isEmpty(){
		return (nElements == 0);
	}
	
	// IMP Steps -
	// first check if queue is full
	// then check if rear and front itself is null, meaning queue itself is empty,
	// then this new node will be front as well as rear and will point to itself 
	// create new Node with next pointer as null, which then set to itself (circular)
	// in case of non-empty queue, newRear should point back to front and
	// old rear's next pointer should point to this newly created node
	// increment number of elements in queue by one
	public T enqueue(T newElement) throws Exception{
		if(nElements == maxItems){
			throw new Exception("Queue is empty, cannot dequeue items");
		}
		Node<T> newRear = new Node<T>(newElement, null);
		if(nElements == 0 || 
				(rear == null && front == null)){
			// Queue was empty so initialize 
			// both front and rear to this first node
			newRear.next = newRear;
			front = newRear;
		}else{
			newRear.next = rear.next;
			rear.next = newRear;
		}
		nElements++;
		rear = newRear;
		return newElement;
	}
	
	// IMP Steps -
	// first check if front and rear is null, meaning queue itself is empty
	// get old front's value, which will be returned
	// get oldfront's next node and make it as new front -- added special 
	// condition in case of only single node which points to itself--> make both
	// point to null
	// decrement the number of elements in queue by one
	public T dequeue() throws Exception{
		if(nElements == 0 || 
				(rear == null && front == null)){
			throw new Exception("Queue is empty, cannot dequeue elements");
		}
		T oldFrontValue = front.value;
		if(nElements == 1){
			front = rear = null;
		}else{
			front = front.next; //front now points to next node
			rear.next = front;  // rear now points to this new front node
		}
		nElements--;
		return oldFrontValue;
	}
	
	public T frontPeek() throws Exception{
		if(front == null){
			throw new Exception("Queue is empty, cannot peek element");
		}
		return front.value;
	}
	
	public T rearPeek() throws Exception{
		if(rear == null){
			throw new Exception("Queue is empty, cannot peek element");
		}
		return rear.value;
	}
}

// A Queue implementation using Stack
class QueueUsingDoubleStack{
	StackUsingLinkedList<String> inbox = new StackUsingLinkedList<String>();
	StackUsingLinkedList<String> outbox = new StackUsingLinkedList<String>();
	
	public String enqueue(String newElement) throws Exception{
		inbox.push(newElement);
		return newElement;
	}
	
	public String dequeque() throws Exception{
		if(outbox.isEmpty()){
			while(!inbox.isEmpty()){
				outbox.push(inbox.pop());
			}
		}
		return outbox.pop();
	}	
}

// Using Recursion - Reverse the stack every time you insert new element
class QueueUsingSingleStack{
	StackUsingLinkedList<String> inbox = new StackUsingLinkedList<String>();
	
	public String enqueue(String newElement) throws Exception{
		if(!inbox.isEmpty()){
			String topElement = inbox.pop();
			enqueue(newElement);
			inbox.push(topElement);
		}else{
			inbox.push(newElement);
		}
		return newElement;
	}
	
	public String dequeque() throws Exception{
		return inbox.pop();
	}	
}