package com.neu.mrlite.common.datastructures;

import java.util.ArrayList;
import java.util.List;

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

    public static void main(String[] args) {
        List<Pair<Integer, String>> t = new ArrayList<Pair<Integer, String>>();
        t.add(new Pair<Integer, String>(0, "test1"));
        t.add(new Pair<Integer, String>(1, "test2"));
        t.add(new Pair<Integer, String>(4, "test4"));
        t.add(new Pair<Integer, String>(1, "test3"));
        t.add(new Pair<Integer, String>(5, "test5"));

        HashPartitioner<Integer, String> hp = new HashPartitioner<Integer, String>();
        for (Pair<Integer, String> p : t) {
            System.out.println("Partition for key - " + p.getKey()
                    + " - value - " + p.getValue() + " - partition - "
                    + hp.getPartition(p.getKey(), p.getValue(), 3));
        }
    }
}
