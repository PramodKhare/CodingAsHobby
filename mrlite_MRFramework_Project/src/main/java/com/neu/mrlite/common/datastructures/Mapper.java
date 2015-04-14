/**
 * @Author Pramod Khare
 */
package com.neu.mrlite.common.datastructures;

/**
 * Interface which constraints - user to implement the map() function
 * 
 * @author Pramod Khare
 */
public interface Mapper {
    public Assortment map(String inFile, String outDir) throws Exception;
}
