package fun.logicpro.ds;

import java.util.Random;

/**
 * Brute force solution to the "knapsack problem" (see pages 115-116
 * in Levitin).
 *
 * Given n items, each having a positive weight and a positive value,
 * and given a knapsack with a maximum weight capacity, find a subset
 * of the items of maximum total value whose total weight does not
 * exceed the knapsack capacity.
 *
 * @author Bob Roos
 */

//////////////////////////////////////////////////////////////////////


/**
 * A simple container class for knapsack items; see comments above for
 * explanation.
 */

class Item {
  private int weight, value; // must be positive

  // Constructor:
  public Item(int w, int v) {
    weight = w; value = v;
  }


  // Accessor methods:
  public int weight() { return weight; }
  public int value() { return value; }
}


//////////////////////////////////////////////////////////////////////


public class KnapsackBruteForce{
  private Item itemList[];    // items to choose from
  private int n;              // number of items
  private int capacity;       // capacity of the knapsack
  private int bestSoFar;      // best value found so far
  private boolean solution[]; // true entries = items giving best value
  private boolean current[];  // true entries = items in current candidate


  /**
   * Constructor -- create n random items and solve knapsack with
   * given capacity:
   */
  public KnapsackBruteForce(int n, int capacity) {
    this.n = n;
    this.capacity = capacity;

    // Create and display random items. We restrict weights to be 
    // no more than half the capacity (this generates more 
    // "interesting" problem instances)
    
    Random rng = new Random();
    itemList = new Item[n];

    System.out.println(" Item Weight Value");
    for (int i = 0; i < n; i++) { 
      int w = 1 + rng.nextInt(capacity/2);
      int v = 1 + rng.nextInt(500);
      itemList[i] = new Item(w, v);
      System.out.printf("%4d %5d %5d\n", i, w, v);
    }

    // Get ready to solve -- initializations
    bestSoFar = Integer.MIN_VALUE;
    solution = new boolean[n];
    current = new boolean[n];
    
    solve(n-1);

    printSolution();
  }


  /**
   * Solve a knapsack problem in which all items from k+1 up have
   * been either selected or rejected.
   */
  public void solve(int k) {

    // Base case: All items have been considered, so check result:
    if (k < 0) {
      // Find total weight and total value:
      int wt = 0;
      int val = 0;
      for (int i = 0; i < n; i++) {
        if (current[i]) {
          wt += itemList[i].weight();
          val += itemList[i].value();
        }
      }

      // Check to see if we've got a better solution:
      if (wt <= capacity && val > bestSoFar) {
        bestSoFar = val;
        copySolution();
      }

      return;
    }

    // Recursive case: there are two possibilities for item k -- either
    // we select it for the knapsack or we don't. Try each possibility:
    current[k] = true;
    solve(k-1);

    current[k] = false;
    solve(k-1);
  }

  /**
   * Prints only the items whose entries in "solution" are marked "true"
   */
  public void printSolution() {
    System.out.println("Best value: "+bestSoFar);
    System.out.println(" Item Weight Value");
    for (int i = 0; i < n; i++) {
      if (solution[i]) {
        System.out.printf("%4d %5d %5d\n", i, itemList[i].weight(), 
                  itemList[i].value());
      }
    }
  }


  /**
   * Used to preserve a newly-found improved solution:
   */
  public void copySolution() {
    for (int i = 0; i < n; i++)
      solution[i] = current[i];
  }



  /**
   * Get number of items and capacity from command line and start the
   * process:
   */
  public static void main(String[] args) {
    /*if (args.length != 2) {
      System.out.println("Usage: java Knapsack #items capacity");
      System.exit(1);
    }
    int n = Integer.parseInt(args[0]);
    int capacity = Integer.parseInt(args[1]);
    */
	
    int n = 5;
    int capacity = 20;
    new KnapsackBruteForce(n,capacity);
  }
}