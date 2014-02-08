package fun.logicpro.ds;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import sun.misc.Queue;

/**
 * Binary Search Tree (BST) Data-structure(DS) Implementation
 * IMP Note - This DS doesn't allow duplicate values.
 * What is BST - A binary tree in which parent node is always greater
 * than left child and less than right child
 * @author Pramod Khare
 */
public class BST {
	Node root = null;
	// create binary heap bottom-up
	// using an arbitrary array input
	private class Node{
		boolean isVisited = false;
		int value;
		Node parent = null;
		Node leftChild = null;
		Node rightChild = null;
		
		Node(int value, Node leftChild, Node rightChild){
			this.value = value;
			this.leftChild = leftChild;
			this.rightChild = rightChild;
		}
		
		Node(){
			this.leftChild = null;
			this.rightChild = null;
		}
	}
	
	// insert 
	public boolean insert(int value){
		Node newNode = new Node(value, null, null);
		if(root == null){
			root = newNode;
			return true;
		}
		Node current = root;
		while(true){
			if(current.value < value){
				if(current.rightChild == null){
					current.rightChild = newNode;
					return true;
				}
				current = current.rightChild;
			}else if(current.value > value){
				if(current.leftChild == null){
					current.leftChild = newNode;
					return true;
				}
				current = current.leftChild;
			}else{
				System.out.println("Equal values are not allowed");
				return false;
			}
		}
	}
	
	// returns you the inserted node, if unsuccessful then returns null
	public Node insertRecursive(Node current, int value){
		if(current == null){
			return new Node(value, null, null);
		}
		
		if(current.value > value){
			current.leftChild = insertRecursive(current.leftChild, value);
		}else if(current.value < value){
			current.rightChild = insertRecursive(current.rightChild, value);
		}else{
			current.value = value;
		}
		return current;
	}
	
	// search 
	public boolean search(int k) {
		Node current = root;
		while(current != null){
			if(current.value > k){
				current = current.leftChild;
			}else if(current.value < k){
				current = current.rightChild;
			}else{
				return true;
			}
		}
		return false;
	}
	
	// Search routine - recursive 
	public boolean searchRecursive(Node current, int k) {
		if(current != null){
			if(current.value > k){
				return searchRecursive(current.leftChild, k);
			}else if(current.value < k){
				return searchRecursive(current.rightChild, k);
			}else{
				return true;
			}
		}
		return false;
	}
	
	// Hibbard deletion technique
	// If value exists then after deletion returns the deleted value
	// else returns -1
	public int delete(int k){
		Node current = root;
		Node successor = null;
		// Special Case - if root itself is deleted
		if(root.value == k){
			successor = findSuccessor(current);
			root = successor;
		}else{
			// First find the node to be deleted
			Node deletedNodeParent = root;
			boolean isLeftChild = false;
			while(current != null){
				if(current.value > k){
					deletedNodeParent = current;
					current = current.leftChild;
					isLeftChild = true;
				}else if(current.value < k){
					deletedNodeParent = current;
					current = current.rightChild;
					isLeftChild = false;
				}else{
					break;
				}
			}
			if(current == null){
				System.out.println("Tree does not contain given value");
				return -1;
			}
			successor = findSuccessor(current);
			if(isLeftChild){
				deletedNodeParent.leftChild = successor;
			}else{
				deletedNodeParent.rightChild = successor;
			}
		}
		return current.value;
	}
	
	public Node findSuccessor(Node nodeToDelete){
		Node current = nodeToDelete;
		// in case both of its children are null
		if(current.leftChild == null && current.rightChild == null){
			return null;
		}
		
		// both children exists
		if(current.leftChild != null && current.rightChild != null){
			Node successorParent = current;
			current = current.rightChild;
			while(current.leftChild!=null){
				successorParent = current;
				current = current.leftChild;
			}
			successorParent.leftChild = current.rightChild;
			current.rightChild = nodeToDelete.rightChild;
			current.leftChild = nodeToDelete.leftChild;
			return current;
		}
		
		if(current.leftChild != null && current.rightChild == null){
			return current.leftChild;
		}else{
			return current.rightChild;
		}
	}
	
	// traversal
	// Inorder Traversal
	public void inOrder(Node current){
		if(current == null) return;
		inOrder(current.leftChild);
		System.out.print("\t"+current.value);
		inOrder(current.rightChild);
	}
	
	// preorder traversal
	public void preOrder(Node current){
		if(current == null) return;
		System.out.print("\t"+current.value);
		preOrder(current.leftChild);
		preOrder(current.rightChild);
	}
	
	// postorder traversal
	public void postOrder(Node current){
		if(current == null) return;
		postOrder(current.leftChild);
		postOrder(current.rightChild);
		System.out.print("\t"+current.value);
	}
	
	// level order traversal
	public void levelOrder(Node current) {
		LinkedList<Node> queue = new LinkedList<BST.Node>();
		if(current != null)
			queue.add(current);
		else 
			return;
		Node currNode = null;
		while(!queue.isEmpty()){
			currNode = queue.remove();
			System.out.print("\t"+currNode.value);
			if(currNode.leftChild != null)
				queue.add(currNode.leftChild);
			if(currNode.rightChild != null)
				queue.add(currNode.rightChild);
		}
	}
	
	/**
	 * Total number of nodes in Binary tree
	 */
	public static int totalNumberOfNodes(Node node){
		if(node == null){
			return 0;
		}
		return 	1 + 
				totalNumberOfNodes(node.leftChild) + 
				totalNumberOfNodes(node.rightChild);
	}
	
	// Populating array with in-order tree traversal
	// given size of binary tree and array of that size is initialised
	public static int inOrderBSTPopulateToArray(Node node, int[] array, int index){
		if(node == null)
			return index;
		index = inOrderBSTPopulateToArray(node.leftChild, array, index);
		array[index++] = node.value;
		index = inOrderBSTPopulateToArray(node.rightChild, array, index);
		return index;
	}
	
	/**
	 * Wrong Implementation but good implementation
	 * Doesn't consider scenario wherein right grandchild is greater 
	 * than grandparent node 
	 * @param node
	 * @return
	 */
	public static boolean isBinaryTreeBSTWrong(Node node){
		if(node == null)
			return true;
		
		if(node.leftChild == null && (node.value > node.rightChild.value))
			return false;
		 
		if(node.rightChild == null &&  (node.value < node.leftChild.value))
			return false;
		
		if(!isBinaryTreeBSTWrong(node.leftChild) || !isBinaryTreeBSTWrong(node.rightChild))
			return false;
		
		return true;
	}
	
	/**
	 * Best Implementation - 
	 * Considering no duplicates
	 * call this function with starting values of min and max as Integer.MIN_VALUE
	 * and Integer.MAX_VALUE respectively
	 */
	public static boolean isBinaryBST(Node node, int maxValue, int minValue){
		if(null == node)
			return true;
		
		if(node.value > maxValue || node.value < minValue){
			return false;
		}

		if (isBinaryBST(node.leftChild, node.value, minValue)
				&& isBinaryBST(node.rightChild, maxValue, node.value)) {
			return true;
		}
		
		return false;
	}
	
	// Given a sorted integer Array - create a binary search tree
	public Node createBSTFromSortedArray(int[] sortedArray, int start, int end){
		// or condition as end < start
		if(start == end)
			return new Node(sortedArray[start], null, null);
		
		int mid = (start + end) / 2;
		
		Node right = null;
		if(mid<end){
			right = createBSTFromSortedArray(sortedArray, mid+1, end);
		}
		
		Node left = null;
		if(mid>start){
			left = createBSTFromSortedArray(sortedArray, start, mid-1);
		}
		
		return new Node(sortedArray[mid], left, right);
	}
	
	
	/**
	 * isEqual - compare two BST trees
	 * @param root1
	 * @param root2
	 * @return true is both trees are equal else return false
	 */
	public static boolean isBSTEqual(Node root1, Node root2){ 
		if(root1 == null && root2 == null){
			return true;
		}else if((root1 == null && root2 != null) 
				|| (root1 != null && root2 == null)){
			return false;
		}else if(root1.value != root2.value){
			return false;
		}
		
		if(isBSTEqual(root1.leftChild, root2.leftChild) 
				&& isBSTEqual(root1.rightChild, root2.rightChild)){
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * Get Avg Depth of tree, considering avg depth at a node = 
	 * (left height + right height)/2
	 * Recursive solution ->
	 */
	public static int avgDepth(Node node){
		if(node == null)
			return 0;
		return 1 + (avgDepth(node.leftChild) + avgDepth(node.rightChild))/2;
	}
	
	/**
	 * Get Minimum depth of tree
	 * @param current
	 * @return
	 */
	public static int minDepthRecursive(Node current){
		if(current == null){
			// height of tree is sometimes taken as number of nodes 
			// along the path from root to lowest leaf -- then return 0
			
			// It is also referred sometimes, as no of links/edges in the path
			// so return -1
			return 0;
		}
		return 1 + Math.min(minDepthRecursive(current.leftChild), 
				minDepthRecursive(current.rightChild));
	}
	
	/**
	 * Recursive Way of finding max depth of any type of tree
	 * @param current
	 * @return
	 */
	public static int maxDepthRecursive(Node current){
		if(current == null){
			return 0;
		}
		return 1 + Math.max(maxDepthRecursive(current.leftChild), 
				maxDepthRecursive(current.rightChild));
	}
	
	// Without using parent pointer
	public int getNextInOrderSuccessor(Node root, Node n){
		// if node has right child
		// In order successor will be first right child's leftmost child
		if(n.rightChild != null){
			Node successor = n.rightChild;
			while(successor.leftChild != null){
				successor = successor.leftChild;
			}
			return successor.value;
		}
		// if node has no right child
		else{
			// using parent reference in Node object
			/*
			Node p = n.parent;
			while(p != null && n == p.rightChild){
				n = p;
				p = p.parent;
			}
			return p;
			*/
			// without using parent pointer reference -- but need to use
			// root node reference though
			Node successor = null;
			while(root.value != n.value){
				if(n.value < root.value){
					successor = root;
					root = root.leftChild;
				}else{
					root = root.rightChild;
				}
			}
			return successor.value;
		}
	}
	
	public LinkedList<LinkedList<Node>> getLevelWiseNodesList(Node root){
		LinkedList<LinkedList<Node>> finalNodesList = new LinkedList<LinkedList<Node>>();
		int levelNumber = 0;
		// Inserting nodes for the first level
		LinkedList<Node> levelNodes = new LinkedList<Node>();
		levelNodes.add(root);
		finalNodesList.add(levelNodes);
		
		while(levelNumber < finalNodesList.size()){
			levelNodes = finalNodesList.get(levelNumber);
			LinkedList<Node> newList = new LinkedList<Node>(); 
			for(Node n : levelNodes){
				if(n.leftChild != null)
					newList.add(n.leftChild);
				if(n.rightChild != null)
					newList.add(n.rightChild);
			}
			if(!newList.isEmpty())
				finalNodesList.add(newList);
			levelNumber++;
		}
		
		for(int i=0; i<finalNodesList.size(); i++){
			levelNodes = finalNodesList.get(i);
			System.out.println("\n"+i+" - ");
			for(int j=0; j < levelNodes.size(); j++){
				System.out.print("\t"+levelNodes.get(j).value);
			}
		}
		return finalNodesList;
	}
	
	/**
	 * Iterative solutions
	 */
	public int maxDepth(Node root){
		if(root == null)
			return 0;
		int maxDepth = 0;
		Stack<Node> stack = new Stack<Node>();
		stack.push(root);
		int depth = 1;
		Node n = null;
		
		while(!stack.isEmpty()){
			n = stack.peek();
			if(depth > maxDepth){
				maxDepth = depth;
			}
			n.isVisited = true;
			if (n.leftChild != null 
					&& !n.leftChild.isVisited){
				stack.push(n.leftChild);
				depth++;
			} else if (n.rightChild != null 
					&& !n.rightChild.isVisited){
				stack.push(n.rightChild);
				depth++;
			} else {
				stack.pop();
				depth--;
			}
		}
		return maxDepth;
	}
	
	/**
	 * Iterative Tree Traversal
	 * @param args
	 */
	public int iterativeInOrderTraversalWtStack(Node root){
		if(root == null)
			return 0;
		int maxDepth = 0;
		Stack<Node> stack = new Stack<Node>();
		stack.push(root);
		int depth = 1;
		Node n = null;
		
		while(!stack.isEmpty()){
			n = stack.peek();
			if(depth > maxDepth){
				maxDepth = depth;
			}
			n.isVisited = true;
			if (n.leftChild != null 
					&& !n.leftChild.isVisited){
				stack.push(n.leftChild);
				depth++;
			} else if (n.rightChild != null 
					&& !n.rightChild.isVisited){
				stack.push(n.rightChild);
				depth++;
			} else {
				stack.pop();
				depth--;
			}
		}
		return maxDepth;
	}
	
	public static void main(String[] args){
		BST tree = new BST();
		tree.insert(50);
		tree.insert(55);
		tree.insert(54);
		tree.insert(45);
		tree.insert(42);
		tree.insert(22);
		tree.insert(2);
		tree.insert(98);
		
		System.out.println("\nInOrder Display - \n");
		tree.inOrder(tree.root);
		
		tree.getLevelWiseNodesList(tree.root);
		
		System.out.println("MaxDepth - "+tree.maxDepth(tree.root));
		/*
		System.out.println("Max Depth - "+maxDepthRecursive(tree.root));
		
		int total = totalNumberOfNodes(tree.root);
		System.out.println("Total Nodes - "+total);
		
		System.out.println("Min Depth - "+minDepthRecursive(tree.root));
		
		System.out.println("Avg Depth - "+avgDepth(tree.root));
		
		int[] treeValues = new int[total];
		int maxFilled = inOrderBSTPopulateToArray(tree.root, treeValues, 0);
		
		System.out.println("Total Values Filled - "+maxFilled);
		
		System.out.println("Tree values in ascending order - ");
		for(int i=0; i < treeValues.length; i++){
			System.out.print("\t"+treeValues[i]);
		}
		
		// Transform array to BST
		System.out.println("Transform array to BST - ");
		int[] temp = {1, 23, 34, 45, 56, 67, 69, 200};
		Node root = tree.createBSTFromSortedArray(temp, 0, temp.length-1);
		
		System.out.println("\nInOrder Display - \n");
		tree.inOrder(root);
		
		System.out.println("\nLevel Order Display - \n");
		tree.levelOrder(root);
		
		System.out.println("\nLevel Order Display - \n");
		tree.levelOrder(tree.root);
		
		System.out.println("\nPre Order Display - \n");
		tree.preOrder(tree.root);
		
		System.out.println("\nPost Order Display - \n");
		tree.postOrder(tree.root);
		
		System.out.println("\nSearch 44 - "+tree.search(44));
		
		System.out.println("\n Recursive Search = 45 "+tree.searchRecursive(tree.root, 45));
		
		tree.root = tree.insertRecursive(tree.root, 34);
		tree.root = tree.insertRecursive(tree.root, 35);
		tree.root = tree.insertRecursive(tree.root, 64);
		tree.root = tree.insertRecursive(tree.root, 24);
		System.out.println("\nInOrder Display - \n");
		tree.inOrder(tree.root);
		
		System.out.println("\nLevel Order Display - \n");
		tree.levelOrder(tree.root);
		
		System.out.println("Node to be deleted - "+tree.delete(45));
		System.out.println("\nInOrder Display - \n");
		tree.inOrder(tree.root);
		
		System.out.println("\nLevel Order Display - \n");
		tree.levelOrder(tree.root);
		
		System.out.println("Node to be deleted - "+tree.delete(50));
		System.out.println("\nInOrder Display - \n");
		tree.inOrder(tree.root);
		
		System.out.println("\nLevel Order Display - \n");
		tree.levelOrder(tree.root);
		
		tree.insert(13);
		tree.insert(24);
		tree.insert(45);
		System.out.println("\nLevel Order Display - \n");
		tree.levelOrder(tree.root);
		System.out.println("\nIn Order Display - \n");
		tree.inOrder(tree.root);*/
	}
}
