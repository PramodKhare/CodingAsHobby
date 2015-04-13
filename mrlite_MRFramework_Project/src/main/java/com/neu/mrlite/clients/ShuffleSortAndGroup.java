package com.neu.mrlite.clients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.neu.mrlite.common.MapperNodeInfo;
import com.neu.mrlite.common.TaskConf;
import com.neu.mrlite.common.datastructures.Assortment;
import com.neu.mrlite.common.datastructures.Pair;
import com.neu.mrlite.common.exception.ShuffleFailureException;

public class ShuffleSortAndGroup {
    private final TaskConf taskConf;
    private List<Pair> mapResults;

    public ShuffleSortAndGroup(final TaskConf taskConf) throws IOException {
        this.taskConf = taskConf;
        this.mapResults = new ArrayList<Pair>();
    }

    public Assortment<Pair> shuffle() throws ShuffleFailureException {
        Assortment<Pair> result = null;
        try {
            System.out.println("Initiating Shuffle phase ... ");
            System.out.println("Reducer Task Id: " + this.taskConf.getTaskId());

            // First check if we have the mapper nodes information
            if (this.taskConf.getMapperNodeInfo() == null
                    || this.taskConf.getMapperNodeInfo().isEmpty()) {
                System.out
                        .println("Failed to start Shuffle : No Mapper Nodes specified");
                throw new ShuffleFailureException(
                        "No Mapper Nodes specified to copy intermediate results from.");
            }

            // Start the individual threads which will do the copying of partitions
            System.out.println("Started copying parition number: "
                    + this.taskConf.getPartitionToStreamForReducer() + " from "
                    + this.taskConf.getMapperNodeInfo().size() + " map nodes");

            List<ShuffleMapPartition> partitionList = new ArrayList<ShuffleMapPartition>();

            // For each Map Node - create a separate thread, which will do the copying of partition
            for (MapperNodeInfo mapNode : this.taskConf.getMapperNodeInfo()) {
                ShuffleMapPartition smp = new ShuffleMapPartition(mapNode,
                        this.taskConf.getPartitionToStreamForReducer());
                partitionList.add(smp);
            }

            // thread join i.e. wait this main-thread till all of the copying phase completes from all map nodes
            for (ShuffleMapPartition smp : partitionList) {
                if (smp.isAlive()) {
                    smp.join();
                }
            }

            // Once copying phase is complete, collate i.e. merge the sorted partitions
            for (ShuffleMapPartition smp : partitionList) {
                this.mapResults = CollectionUtils.collate(this.mapResults,
                        smp.getPartition());
            }

            // Group by key and modify create new map-results list<Pair>
            groupMapResultsByKey();

            // Create Assortment out this Grouped by Key - map-results
            result = new Assortment<Pair>(this.mapResults);

        } catch (final Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Group Results by Key-Space
     */
    private void groupMapResultsByKey() {
        System.out.println("Grouping By Keys Phase Started ... ");
        List<Pair> groupedByKeyResults = new ArrayList<Pair>();
        Pair pair = null;
        for (Pair p : mapResults) {
            System.out.println(p.toString());
            if (pair == null) {
                pair = p;
                List valueList = new ArrayList();
                valueList.add(pair.getValue());
                pair.setValue(valueList);
                continue;
            }

            // If keys are same then add value into value list
            if (pair.getKey().equals(p.getKey())) {
                List valueList = (List) pair.getValue();
                valueList.add(p.getValue());
            } else {
                // If keys are different then add previous pair into results list
                // new pair will continue as a temporaty pair object now
                groupedByKeyResults.add(pair);
                pair = p;
                // Replace value with a list ()
                List valueList = new ArrayList();
                valueList.add(pair.getValue());
                pair.setValue(valueList);
            }
        }
        // For the last Grouped pairs 
        groupedByKeyResults.add(pair);
        mapResults = groupedByKeyResults;
    }

    public List<Pair> getMapResults() {
        return mapResults;
    }

    public TaskConf getTaskConf() {
        return taskConf;
    }
}
