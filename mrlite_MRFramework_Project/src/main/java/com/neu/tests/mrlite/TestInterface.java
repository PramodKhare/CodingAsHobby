package com.neu.tests.mrlite;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import com.neu.mrlite.common.datastructures.Assortment;
import com.neu.mrlite.common.datastructures.IOHandle;
import com.neu.mrlite.common.datastructures.POCallback;
import com.neu.mrlite.common.datastructures.Pair;

public class TestInterface {
    public static Assortment run(String inFile, String outDir) throws FileNotFoundException, UnsupportedEncodingException, UnknownHostException {
		
		IOHandle io = new IOHandle(inFile, outDir);
		
		Assortment<Pair<Long, String>> col1 = Assortment.readInputFrom(io);
		
		Assortment<Pair<String, Integer>> col2 = col1.parallel(
			new POCallback<Long, String, String, Integer>() {
				public void process(Pair<Long, String> data) {
					String[] parts = data.getValue().split("\\t");
					emit(parts[0], Integer.parseInt(parts[1]));
				}
			});
		
		Assortment<Pair<String, Integer>> col3 = col2.parallel(
			new POCallback<String, Integer, String, Integer>() {
				public void process(Pair<String, Integer> data) {
					int val = data.getValue();
					String key = data.getKey();
					emit(key, val+10);
				}
			});
		
		Assortment<Pair<String, Integer>> col4 = col3.parallel(
		        new POCallback<String, Integer, String, Integer>() {
	                public void process(Pair<String, Integer> data) {
					int val =data.getValue();
					String key = data.getKey();
					emit(key, val+10);
				}
			});
		
		return col4;
	}
}