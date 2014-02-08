/**
 * @author Pramod Khare
 */
package fun.logicpro.ds;

/**
 * @author Pramod Khare
 * Single Linked List
 */
public class LinkedList<T> {
	/**
	 * @param Pramod Khare
	 */
	public static void main(String[] args) {
		System.out.println("Linked list - ");
		SingleLinkedList2<String> sl = new SingleLinkedList2<String>();
		sl.insertAtStart("First");
		sl.insertAfter("First", "Third");
		sl.insertAfter("Third", "Fourth");
		sl.insertBefore("Third", "Second");
		sl.printList();
		
		//sl.deleteAt("Third");
		//sl.deleteBefore("Third");
		//sl.printList();
		
		//sl.reverseIterative();
		//sl.printList();
		sl.reverseRecursiveMain();
		sl.printList();
	}
}

class SingleLinkedList2<T>{
	Node<T> first; 	//Top of stack element reference
	
	private class Node<T>{
		T value;
		Node<T> next;
		
		Node(T value, Node<T> next){
			this.value = value;
			this.next = next;
		}
		
		Node(){
			this.value = null;
			this.next = null;
		}
	}
	
	public void printList(){
		Node<T> current = first;
		System.out.println("Linked List Values - ");
		while(current != null){
			System.out.print("\t"+current.value);
			current = current.next;
		}
	}
	// insert - O(n) - 
	public boolean insertBefore(T value, T newValue){
		// first find the element with value
		// and then insert after this element
		if(first == null){
			return false;
		}
		// Check if its first element
		if(first.value.equals(value)){
			Node<T> newNode = new Node<T>(newValue, first);
			first = newNode;
			return true;
		}
		
		Node<T> current = first;
		while(current.next != null){
			if(current.next.value.equals(value)){
				Node<T> newNode = new Node<T>(newValue, current.next);
				current.next = newNode;
				return true;
			}
			current = current.next;
		}
		return false;
	}
	// O(n)
	public boolean insertAfter(T value, T newValue){
		// first find the element with value
		// and then insert after this element
		Node<T> current = first;
		while(current != null){
			if(current.value.equals(value)){
				Node<T> newNode = new Node<T>(newValue, current.next);
				current.next = newNode;
				return true;
			}
			current = current.next;
		}
		return false;
	}

	public T insertAtStart(T value){
		Node<T> newNode = new Node<T>(value, first);
		first = newNode;
		return newNode.value;
	}
	
	// delete - O(1) - 
	public T deleteFirst(){
		// Special Case - for first node
		if(first != null){
			T value = first.value;
			first = first.next;
			return value;
		}
		return null;
	}
	
	// delete - O(n) - 
	public boolean deleteAt(T value){
		Node<T> current = first;
		// Special Case - for first node
		if(current.value.equals(value)){ 
			first = first.next;
			return true;
		}
		
		while(current.next!= null){
			if(current.next.value.equals(value)){
				current.next = current.next.next;
				return true;
			}
			current = current.next;
		}
		// meaning no such element found
		return false;
	}
	// O(n) 
	public boolean deleteBefore(T value){
		// Either list is empty or
		// first search matches to first element
		// there is no element to delete
		if(first == null 
				|| first.value.equals(value) 
				|| first.next == null){
			return false;
		}
		// If search matches to second element, 
		// then first element should be deleted
		// meaning first pointer should change
		// so this is a special condition
		if(first.next.value.equals(value)){
			first = first.next;
			return true;
		}
		
		Node<T> current = first.next;
		Node<T> previous = first;
		while(current.next != null){
			if(current.next.value.equals(value)){
				previous.next = current.next;
				return true;
			}
			current = current.next;
		}
		return false;
	}
	
	// search - iterate through list till you get null
	// O(n)
	public boolean search(T value){
		Node<T> current = first;
		while(current!= null){
			if(current.value.equals(value))
				return true;
			current = current.next;
		}
		return false;
	}
	
	
	// Reverse Linked List
	// Take 2 pointers, one at start and one at last-but-one
	public void reverseIterative(){
		// Special Cases, if its empty or has only one element 
		if(first == null || first.next == null){
			return;
		}
		Node<T> current = first;
		Node<T> nextNode = null;
		Node<T> newFirst = null;
		while(current != null){
			// Old List's next reference saved temporarily 
			nextNode = current.next;
			// current element added to new reversed list's front
			current.next = newFirst;
			newFirst = current;
			// now current points old lists next element
			current = nextNode;
		}
		first = newFirst;
	}
	public void reverseRecursiveMain(){
		first = reverseRecursive(first, null);
	}
	// Reverse String recursively
	public Node<T> reverseRecursive(Node<T> current, Node<T> previous){
		// In case of last and last-but-one node,
		// my new head will be last node, which is returned
		if(current.next == null){
			previous.next = null;
			current.next = previous;
			return current;
		}else{
			// In other scenario, new head will be returned again
			Node<T> newHead = reverseRecursive(current.next, current);
			current.next = previous;
			return newHead;
		}
	}
}