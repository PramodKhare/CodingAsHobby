package com.neu.tests.mrlite;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.List;

import com.neu.mrlite.common.datastructures.Assortment;
import com.neu.mrlite.common.datastructures.IOHandle;
import com.neu.mrlite.common.datastructures.Mapper;
import com.neu.mrlite.common.datastructures.POCallback;
import com.neu.mrlite.common.datastructures.Pair;
import com.neu.mrlite.common.datastructures.Reducer;

public class TestInterface implements Mapper, Reducer {
    public Assortment map(String inFile, String outDir)
            throws FileNotFoundException, UnsupportedEncodingException,
            UnknownHostException {

        IOHandle io = new IOHandle(inFile, outDir);

        Assortment<Pair<Long, String>> col1 = Assortment.readInputFrom(io);

        Assortment<Pair<String, Integer>> col2 = col1
                .parallel(new POCallback<Long, String, String, Integer>() {
                    public void process(Pair<Long, String> data) {
                        String[] parts = data.getValue().split("\\t");
                        emit(parts[0], Integer.parseInt(parts[1]));
                    }
                });

        Assortment<Pair<String, Integer>> col3 = col2
                .parallel(new POCallback<String, Integer, String, Integer>() {
                    public void process(Pair<String, Integer> data) {
                        int val = data.getValue();
                        String key = data.getKey();
                        emit(key, val + 10);
                    }
                });

        Assortment<Pair<String, Integer>> col4 = col3
                .parallel(new POCallback<String, Integer, String, Integer>() {
                    public void process(Pair<String, Integer> data) {
                        int val = data.getValue();
                        String key = data.getKey();
                        emit(key, val + 10);
                    }
                });

        return col4;
    }

    @Override
    public Assortment reduce(Assortment<Pair> collection) throws Exception {
        Assortment<Pair<String, Integer>> col3 = collection
                .parallel(new POCallback<String, List<Integer>, String, Integer>() {
                    public void process(Pair<String, List<Integer>> data) {
                        List<Integer> values = data.getValue();
                        String key = data.getKey();
                        // Emitting Middle Element
                        emit(key, values.get(values.size() / 2));
                    }
                });
        return col3;
    }
}