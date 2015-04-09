/**
 * This class signifies the reason why the given job cannot be run, 
 * as the given JobConf object is invalid
 * @Author Pramod Khare
 */
package com.neu.mrlite.common.exception;

/**
 * Invalid Job Configuration Exception
 * @author Pramod Khare
 */
public class InvalidJobConfException extends Exception {
    private static final long serialVersionUID = 2176283974027753466L;

    // TODO create exception hierarchy for different reasons
    public InvalidJobConfException(String msg) {
        super(msg);
    }
}
