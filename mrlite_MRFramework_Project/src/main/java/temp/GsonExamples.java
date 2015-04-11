package temp;

import com.google.gson.Gson;
import com.neu.mrlite.common.JobConf;

public class GsonExamples {

    public GsonExamples() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        JobConf conf = new JobConf();
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
    }
}
