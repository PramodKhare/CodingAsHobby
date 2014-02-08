package fun.logicpro.ds;

/**
 * @author Pramod Khare
 * Implementation of Priority Queue using Binary Tree
 */
public class BinaryHeap {
	/**
	 * Max and Min Binary Heaps - using 0-based index system, 
	 * using N-sized array
	 */
	public static void main(String[] args) {
		System.out.println("Rtesds");
		// TODO Auto-generated method stub
		MaxBinaryHeapUsingArray maxHeap = new MaxBinaryHeapUsingArray(20);
		System.out.println("Display - heap "); maxHeap.display();
		
		maxHeap.insert(12);
		maxHeap.insert(1);
		maxHeap.insert(34);
		maxHeap.insert(23);
		maxHeap.insert(3);
		maxHeap.insert(323);
		maxHeap.insert(2);
		maxHeap.insert(21);
		maxHeap.insert(22);
		maxHeap.insert(22);
		maxHeap.insert(22);
		maxHeap.insert(22);
		maxHeap.insert(52);
		
		System.out.println("Display - heap "); maxHeap.display();
		System.out.println("Display - sorted \n");
		while(maxHeap.len > 0){
			System.out.print("\t"+maxHeap.removeMax());
		}
		
		/*MinBinaryHeapUsingArray minHeap = new MinBinaryHeapUsingArray(10);
		System.out.println("Display - minHeap "); minHeap.display();
		
		minHeap.insert(12);
		minHeap.insert(1);
		minHeap.insert(34);
		minHeap.insert(23);
		
		System.out.println("Display - minHeap "); minHeap.display();
		
		System.out.println("Display - sorted \n");
		while(minHeap.len > 0){
			System.out.print("\t"+minHeap.removeMin());
		}*/
	}
}


// Parent node is always greater than both of its child

//Not Stable - HeapSort --but in-place
//creation of heap from random array takes - 2N compares
//and sorting takes (n* log n) time for n values
class MaxBinaryHeapUsingArray{
	
	// 0-index array --
	// node index = n
	// then parent (n-1)/2
	// then left child (2*n + 1)
	// then right child (2*n + 2)
	
	int[] maxheap = null;
	int len = 0;
	
	public MaxBinaryHeapUsingArray(int size) {
		maxheap = new int[size];
	}
	
	// sink minimum node in binary heap tree
	// to its proper position
	
	// Smaller value settle to bottom down the tree
	// k - index of current item
	public int sink(int k){
		int j, temp;
		while((2*k + 1) < len){
			j = (2*k + 1);
			if((j < len-1) && (maxheap[j] < maxheap[j+1])) j++;
			if(maxheap[k] > maxheap[j]) break;
			// exchange values
			temp = maxheap[j];
			maxheap[j] = maxheap[k];
			maxheap[k] = temp;
			// new k value 
			k = j;
		}
		return maxheap[k];
	}
	
	// swim the element from bottom to its
	// position higher in binary heap tree
	
	// to bubble up the greater values
	public int swim(int k){
		int parentIndex, temp;
		while (k > 0 && maxheap[((k-1)/2)] < maxheap[k]){
			// swap children with greater value with
			// parent at ((k-1)/2)
			parentIndex = ((k-1)/2);
			temp = maxheap[parentIndex];
			maxheap[parentIndex] = maxheap[k];
			maxheap[k] = temp;
			
			// new value of k
			k = parentIndex;
		}
		return maxheap[k];
	}
	
	// create binary heap bottom-up
	// using an arbitrary array input
	
	
	// construct binary heap top-down
	// using arbitrary array input
	
	
	// insert new element into heap
	public int insert(int value){
		maxheap[len] = value;
		return swim(len++);
	}
	
	// get maximum element out of heap
	public int peekMax() {
		return maxheap[0];
	}
	
	public int removeMax() {
		int value = maxheap[0];
		maxheap[0] = maxheap[len-1];
		len--;
 		sink(0);
 		// maxheap[len] = null; -- prevent loitering
 		return value;
	}
	
	public void display() {
		for(int i=0; i<len; i++){
			System.out.print("\t"+maxheap[i]);
		}
	}
}

//Parent node is always smaller than both of its child
class MinBinaryHeapUsingArray{
	int[] minheap = null;
	int len = 0;
	
	public MinBinaryHeapUsingArray(int size) {
		minheap = new int[size];
	}
	
	// sink minimum node in binary heap tree
	// to its proper position
	
	// Smaller value settle to bottom down the tree
	// k - index of current item
	public int sink(int k){
		int j, temp;
		while((2*k + 1) < len){
			j = (2*k + 1);
			if((j < len-1) && (minheap[j] > minheap[j+1])) j++;
			if(minheap[k] < minheap[j]) break;
			// exchange values
			temp = minheap[j];
			minheap[j] = minheap[k];
			minheap[k] = temp;
			// new k value 
			k = j;
		}
		return minheap[k];
	}
	
	// swim the element from bottom to its
	// position higher in binary heap tree
	
	// to bubble up the greater values
	public int swim(int k){
		int parentIndex, temp;
		while (k > 0 && minheap[((k-1)/2)] > minheap[k]){
			// swap children with greater value with
			// parent at ((k-1)/2)
			parentIndex = ((k-1)/2);
			temp = minheap[parentIndex];
			minheap[parentIndex] = minheap[k];
			minheap[k] = temp;
			
			// new value of k
			k = parentIndex;
		}
		return minheap[k];
	}
	
	// create binary heap bottom-up
	// using an arbitrary array input
	
	
	// construct binary heap top-down
	// using arbitrary array input
	
	
	// insert new element into heap
	public int insert(int value){
		minheap[len] = value;
		return swim(len++);
	}
	
	// get maximum element out of heap
	public int peekMin() {
		return minheap[0];
	}
	
	public int removeMin() {
		int value = minheap[0];
		minheap[0] = minheap[len-1];
		len--;
 		sink(0);
 		// maxheap[len] = null; -- prevent loitering
 		return value;
	}
	
	public void display() {
		for(int i=0; i<len; i++){
			System.out.print("\t"+minheap[i]);
		}
	}
}

// Not Stable - HeapSort --but in-place
// creation of heap from random array takes - 2N compares
// and sorting takes n* logn time for n values
class MaxBinaryHeapUsingLinkedList<T>{
	Node<T> root = null;
	// create binary heap bottom-up
	// using an arbitrary array input
	private class Node<T>{
		T value;
		Node<T> parent;
		Node<T> leftChild;
		Node<T> rightChild;
		
		Node(T value, Node<T> parent, Node<T> leftChild, Node<T> rightChild){
			this.value = value;
			this.parent = parent;
			this.leftChild = leftChild;
			this.rightChild = rightChild;
		}
		
		Node(){
			this.value = null;
			this.parent = null;
			this.leftChild = null;
			this.rightChild = null;
		}
	}
	
	
	// construct binary heap top-down
	// using arbitrary array input
	
	
	// insert new element into heap
	
	
	// get maximum element out of heap
	
	
	// sink minimum node in binary heap tree
	// to its proper position
	
	
	// swim the element from bottom to its
	// position higher in binary heap tree
	
	
}