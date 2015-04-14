/**
 * @Author Pramod Khare
 */
package temp;

import com.google.gson.Gson;
import com.neu.mrlite.common.JobConf;
import com.neu.mrlite.common.datastructures.Assortment;
import com.neu.mrlite.common.datastructures.Mapper;
import com.neu.mrlite.common.datastructures.Pair;
import com.neu.mrlite.common.datastructures.Reducer;

public class ClassTemp {
    public static void main(String[] args) {
        Gson gson = new Gson();
        System.out.println(gson.toJson(new JobConf()));
    }
}

class MapReduce implements Mapper, Reducer {
    public static void main(String[] args) {
        Gson gson = new Gson();
        
        // Create a new Job Conf 
        JobConf jcfg = new JobConf();
        jcfg.setMapperClass(MapReduce.class);
        jcfg.setReducerClass(MapReduce.class);
        //System.out.println(gson.toJson());
    }

    @Override
    public Assortment reduce(Assortment<Pair> key) throws Exception {
        System.out.println("Reducer function");
        return key;
    }

    @Override
    public Assortment map(String inFile, String outDir) throws Exception {
        System.out.println("Mapper function");
        return null;
    }
}

// TODO things -> 
// 