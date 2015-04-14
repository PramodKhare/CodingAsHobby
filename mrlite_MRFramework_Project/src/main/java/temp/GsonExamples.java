package temp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.google.gson.Gson;
import com.neu.mrlite.common.datastructures.Pair;

public class GsonExamples {

    public GsonExamples() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        /*        JobConf conf = new JobConf();
                conf.setExecutableJar("xys.jar");
                conf.setInputFilePath("input-path");
                conf.setMapperClass("com.x.y.Map");
                conf.setOutDirPath("output");
                conf.setReducerClass("com.x.y.Reduce");

                Gson gson = new Gson();
                String jsonStr = gson.toJson(conf);
                System.out.println(jsonStr);

                JobConf j = gson.fromJson(jsonStr, JobConf.class);
                jsonStr = gson.toJson(j);
                System.out.println(jsonStr);
        */
        My<String, String> obj = new My<String, String>("XXXXXXXXXX",
                "VVVVVVVVVVVV");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(obj);
        System.out.println(jsonStr);

        System.out.println("-------------------------------------------------");

        My<String, String> obj2 = gson.fromJson(jsonStr, My.class);
        jsonStr = gson.toJson(obj2);
        System.out.println(jsonStr);

        System.out.println("-------------------------------------------------");

        List<String> t = new ArrayList<String>();
        t.add("test1");
        t.add("test2");
        t.add("test3");
        Writable wr = new Writable<List<String>>(t);
        jsonStr = wr.toString();
        System.out.println(" Serial Writable - " + jsonStr);

        System.out.println("-------------------------------------------------");

        wr = Writable.deserializeFromJson(jsonStr);
        jsonStr = wr.toString();
        System.out.println(" Serial Again Writable - " + jsonStr);

        System.out
                .println("=====================================================");

        List<Pair<Integer, String>> t2 = new ArrayList<Pair<Integer, String>>();
        t2.add(new Pair<Integer, String>(0, "test1"));
        t2.add(new Pair<Integer, String>(1, "test2"));
        t2.add(new Pair<Integer, String>(1, "test3"));
        t2.add(new Pair<Integer, String>(4, "test4"));
        t2.add(new Pair<Integer, String>(5, "test5"));

        List<Pair<Integer, String>> t3 = new ArrayList<Pair<Integer, String>>();
        t3.add(new Pair<Integer, String>(0, "test1"));
        t3.add(new Pair<Integer, String>(1, "test2"));
        t3.add(new Pair<Integer, String>(1, "test3"));
        t3.add(new Pair<Integer, String>(4, "test4"));
        t3.add(new Pair<Integer, String>(5, "test5"));

        List<Pair<Integer, String>> t4 = CollectionUtils.collate(t2, t3);
        // Merged Results
        for (Pair<Integer, String> tre : t4) {
            System.out.println(tre.toString());
        }

        System.out.println("-------------------------------------------------");

        List<Pair<Integer, String>> groupedByKeyResults = new ArrayList<Pair<Integer, String>>();
        Pair pair = null;
        // Group by Key
        for (Pair<Integer, String> p : t4) {
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

        // Grouped Results
        for (Pair<Integer, String> tre : groupedByKeyResults) {
            System.out.println(tre.toString());
        }

        System.out.println("-------------------------------------------------");
    }
}

class My<K, V> implements Serializable {
    private static final long serialVersionUID = 683004095329451520L;
    private K key;
    private V value;

    public My(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
