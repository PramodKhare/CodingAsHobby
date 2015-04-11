package com.neu.mrlite.common.datastructures;

/**
 * Partitions the key space of Mapper Task output
 */
public interface Partitioner<K2, V2> {

    /**
     * Get the partition number for a given key given the total number of
     * reduce-tasks for the job.
     * 
     * @param key
     * @param value
     * @param numPartitions
     * @return the partition number for the key
     */
    int getPartition(K2 key, V2 value, int numPartitions);
}
