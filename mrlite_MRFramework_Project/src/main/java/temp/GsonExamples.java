package temp;

import java.io.Serializable;

import com.google.gson.Gson;
import com.neu.mrlite.common.JobConf;

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
        My<String, String> obj = new My<String, String>("XXXXXXXXXX", "VVVVVVVVVVVV");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(obj);
        System.out.println(jsonStr);
        
        System.out.println("-------------------------------------------------");
        
        My<String, String> obj2 = gson.fromJson(jsonStr, My.class);
        jsonStr = gson.toJson(obj2);
        System.out.println(jsonStr);
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
