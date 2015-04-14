/**
 * @Author Pramod Khare
 */
package com.neu.mrlite.common.datastructures;

/**
 * Interface which constraints - user to implement the reduce() function
 * 
 * @author Pramod Khare
 */
public interface Reducer {
    public Assortment reduce(Assortment<Pair> key) throws Exception;
}
