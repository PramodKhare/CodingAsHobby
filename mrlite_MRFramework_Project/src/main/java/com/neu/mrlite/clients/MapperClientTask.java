package com.neu.mrlite.clients;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.neu.mrlite.common.InMemStore;
import com.neu.mrlite.common.TaskConf;
import com.neu.mrlite.common.datastructures.Assortment;
import com.neu.mrlite.common.datastructures.HashPartitioner;
import com.neu.mrlite.common.datastructures.IOCallback;
import com.neu.mrlite.common.datastructures.IOHandle;
import com.neu.mrlite.common.datastructures.POCallback;
import com.neu.mrlite.common.datastructures.Pair;
import com.neu.mrlite.common.datastructures.Writable;

public class MapperClientTask extends Thread {
    private final TaskConf task;
    private final PrintWriter out;
    private List<Writable> outVal;
    private HashPartitioner partitioner = new HashPartitioner();

    public MapperClientTask(final TaskConf task, final PrintWriter out) {
        this.task = task;
        this.out = out;
        start();
    }

    @Override
    public void run() {
        try {
            // Collect all user mapper functions to get all his POCallback chain
            Assortment collection = executeUserMapperFunction();

            // STEP 2: Actually execute the POCallback chain from the returned Assortment
            execute(collection.getExecChain(), collection.getIOHandle());

            // Partition the outVal i.e. List<Writable> into keyspace and save it into in-memory-map
            partitionMapOut();

        } catch (final Exception e) {
            e.printStackTrace();
            out.println("Invalid Task configuration provided");
        }
    }

    private Assortment executeUserMapperFunction()
            throws MalformedURLException, ClassNotFoundException,
            NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        // load user's executable jar
        File file = new File(this.task.getExecutableJar());
        URL url = file.toURI().toURL();
        URL[] urls = new URL[] { url };
        ClassLoader cl = new URLClassLoader(urls);

        // Load Mapper Class
        Class<?> cls = cl.loadClass(this.task.getMapperClass());

        // Get handle of run method using reflection
        Method runMethod = cls.getMethod("run", new Class[] { String.class,
                String.class });

        // Invoke run method
        return (Assortment) runMethod.invoke(null, task.getInputFilePath(),
                task.getOutDirPath());
    }

    private void execute(List<POCallback> exec, IOHandle io) {
        Object outKey;
        Gson gson = new Gson();
        for (POCallback p : exec) {
            System.out.println(p);
            if (p instanceof IOCallback) {
                ((IOCallback) p).process(io);
                outVal = (List<Writable>) p.getValue();
                outKey = p.getKey();
                continue;
            } else {
                List<Writable> interVal = new ArrayList<Writable>();
                for (Writable val : outVal) {
                    p.process(val);
                    interVal.add(new Writable(
                            new Pair(p.getKey(), p.getValue())));
                }
                outVal = interVal;
            }
        }
        // TODO Currently sending output back to Master - change it to ioserver
        // out.println(gson.toJson(outVal));
    }

    @SuppressWarnings("unchecked")
    private void partitionMapOut() {
        // First sort the outVal - based on Pair's compareTo method i.e. keys
        Collections.sort(outVal, new Comparator<Writable>() {
            @Override
            public int compare(Writable o1, Writable o2) {
                return ((Pair) o1.cast(Pair.class)).compareTo(((Pair) o2
                        .cast(Pair.class)));
            }
        });

        // Do the actual partioning AND save individual partition with keys - as taskID_<partition_no> in InMemoryStore
        for (Writable w : outVal) {
            Pair pair = (Pair) w.cast(Pair.class);
            int partitionNumber = partitioner.getPartition(pair.getKey(),
                    pair.getValue(), task.getNumberOfReduceTasks());
            // partitioner
            String key = task.getTaskId() + "_" + partitionNumber;
            List<Pair> partionPairList = (List<Pair>) InMemStore.getValueForKey(key);
            if(partionPairList != null){
                partionPairList.add(pair);
            }else{
                partionPairList = new ArrayList<Pair>();
                partionPairList.add(pair);
                InMemStore.putKeyValue(key, partionPairList);
            }
        }
    }
}
