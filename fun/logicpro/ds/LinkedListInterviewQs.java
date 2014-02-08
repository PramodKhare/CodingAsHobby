/**
 * 
 */
package fun.logicpro.ds;

/**
 * @author user
 *
 */
public class LinkedListInterviewQs {

	/**
	 * 
	 */
	public LinkedListInterviewQs() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}

class SingleLinkedList<T>{
	Node<T> startNode;
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
	
	// detect if linkedlist has a loop
	// Solution modifies the list
	public boolean hasLoop(Node startNode){
	  Node previousNode = null;
	  Node currentNode = startNode;
	  Node nextNode;
	  if (currentNode.next == null) return false;
	  while(currentNode != null){
	    nextNode = currentNode.next;
	    currentNode.next = previousNode;
	    previousNode = currentNode;
	    currentNode = nextNode;
	  }
	  return (previousNode == startNode);
	}
	
	// Good solution
	boolean hasLoopBetter(Node startNode){
	  Node currentNode = startNode;
	  Node checkNode = null;
	  int since = 0;
	  int sinceScale = 2;
	  do {
	    if (checkNode == currentNode) return true;
	    if (since >= sinceScale){
	        checkNode = currentNode;
	        since = 0;
	        sinceScale = 2*sinceScale;
	    }
	    since++;
	  } while (currentNode == currentNode.next);
	  return false;
	}
	
	// Best solution - using slow pointer and fast pointer 
	// slow pointer increments by one while fast pointer increments by two 
	// just check if slow and fast are same
	boolean hasLoopBetter2(Node startNode) {
		Node slowNode = startNode;
		Node fastNode1 = startNode;
		Node fastNode2 = startNode;
		while (slowNode != null && fastNode1 == fastNode2.next
				&& fastNode2 == fastNode1.next) {
			if (slowNode == fastNode1 || slowNode == fastNode2)
				return true;
			slowNode = slowNode.next;
		}
		return false;
	}
}
