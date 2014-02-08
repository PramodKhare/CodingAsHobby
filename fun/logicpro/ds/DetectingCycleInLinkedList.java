package fun.logicpro.ds;

/**
 * @author Pramod Khare
 * Find Cycle in a linked list and Remove the loop
 * 
 * http://ostermiller.org/find_loop_singly_linked_list.html
 * http://www.geeksforgeeks.org/detect-and-remove-loop-in-a-linked-list/
 * 
 * Multiple Ways to Find Loop :- 
 * 1) Use Hashmap/Hashset - when any previously added node, found loop detected - but O(n) space and time
 * 2) Brute Force - O(n^2) - Check each element if it is linked to itself again
 * 3) Using Double Linked List - O(n) - if we can trust backlinks - and it is not just a malformed linked list
 * 4) Using 2 pointers - one slow and one fast pointer
 * 5) How to measure length of loop ---> using fast and slow
 * 
 */
public class DetectingCycleInLinkedList {
	public DetectingCycleInLinkedList() {}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}