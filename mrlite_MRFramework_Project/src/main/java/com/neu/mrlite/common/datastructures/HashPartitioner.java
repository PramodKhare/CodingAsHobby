package com.neu.mrlite.common.datastructures;

/**
 * Basic hash Partitioner for partitioning the Map output keys - so that reducer
 * can stream them easily
 * 
 * @author Pramod Khare - taken as is from Hadoop src code
 *
 * @param <K2>
 * @param <V2>
 */
public class HashPartitioner<K2, V2> implements Partitioner<K2, V2> {
    public int getPartition(K2 key, V2 value, int numReduceTasks) {
        return (key.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
    }
}
