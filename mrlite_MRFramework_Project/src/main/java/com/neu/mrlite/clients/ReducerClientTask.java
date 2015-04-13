package com.neu.mrlite.clients;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.neu.mrlite.common.TaskConf;
import com.neu.mrlite.common.datastructures.Assortment;
import com.neu.mrlite.common.datastructures.POCallback;
import com.neu.mrlite.common.datastructures.Pair;
import com.neu.mrlite.common.datastructures.Writable;

public class ReducerClientTask extends Thread {
    private final TaskConf task;
    private final PrintWriter out;
    // Stores the final results 
    private Assortment<Pair> collection;
    // GroupedResults
    private List<Pair> groupedByKeyResults;

    public ReducerClientTask(final TaskConf task, final PrintWriter out) {
        this.task = task;
        this.out = out;
        start();
    }

    @Override
    public void run() {
        try {
            // STEP 1: First Complete the Shuffle, and Sort and Grouping by Keys phase
            ShuffleSortAndGroup sf = new ShuffleSortAndGroup(this.task);
            this.collection = sf.shuffle();
            this.groupedByKeyResults = sf.getMapResults();

            // Collect user-defined Reducer function into POCallback chain
            this.collection = executeUserReduceFunction(this.collection);

            // STEP 2: Actually execute the POCallback chain from the returned Assortment
            execute(this.collection.getExecChain());

        } catch (final Exception e) {
            e.printStackTrace();
            out.println("Invalid Task configuration provided");
        }
    }

    private Assortment executeUserReduceFunction(
            Assortment shuffledAndGroupedByKeyAssortment)
            throws MalformedURLException, ClassNotFoundException,
            NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        // load user's executable jar
        File file = new File(this.task.getExecutableJar());
        URL url = file.toURI().toURL();
        URL[] urls = new URL[] { url };
        ClassLoader cl = new URLClassLoader(urls);

        // Load Reducer Class
        Class<?> reducerClass = cl.loadClass(this.task.getReducerClass());

        // Get handle of run method using reflection
        Method runMethod = reducerClass.getMethod("run",
                new Class[] { Assortment.class });

        // Invoke run method
        return (Assortment) runMethod.invoke(null,
                shuffledAndGroupedByKeyAssortment);
    }

    /**
     * Reducer Execute Method
     * 
     * @IMP - Here there is no ioHandle - reducer will have the
     *      groupedBykeyAssortment
     * @param exec
     * @param io
     */
    private void execute(List<POCallback> exec) {
        Object outKey;
        Gson gson = new Gson();
        for (POCallback p : exec) {
            System.out.println(p);
            List<Pair> interVal = new ArrayList<Pair>();
            for (Pair val : groupedByKeyResults) {
                p.process(new Writable(val));
                interVal.add(new Pair(p.getKey(), p.getValue()));
            }
            groupedByKeyResults = interVal;
        }
        // TODO Currently sending output back to Master - change it to ioserver
        out.println(gson.toJson(groupedByKeyResults));
    }
}
