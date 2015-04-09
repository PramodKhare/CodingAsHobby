package airdel.a3;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Delay learner class - Creates a MapReduce job and executes it
 * 
 * @author Ryan Millay, Nikit Waghela, Pramod Khare
 */
public class DelayLearner {

    /**
     * Create and execute learner hadoop job based on the input arguments
     * 
     * @param args
     * @return A hadoop job
     * @throws Exception
     * @throws IllegalArgumentException
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    public int executeLearnerJob(String[] args) throws Exception {
        try {
            // create a new hadoop job
            Job j = new Job();
            j.setJarByClass(A3_Driver.class);
            j.setJobName("Delay Learner");

            // configure the input and output paths
            FileInputFormat.addInputPath(j, new Path(args[1]));
            FileOutputFormat.setOutputPath(j, new Path(args[2]));

            // configure the map and reduce tasks
            j.setMapperClass(DelayLearnerMapper.class);
            j.setCombinerClass(DelayLearnerCombiner.class);
            j.setReducerClass(DelayLearnerReducer.class);

            // set map output
            j.setMapOutputKeyClass(Text.class);
            j.setMapOutputValueClass(DelayValue.class);

            // configure the output settings
            j.setOutputKeyClass(Text.class);
            j.setOutputValueClass(IntWritable.class);

            // Execute job and return status
            return j.waitForCompletion(true) ? 0 : 1;
        } catch (Exception e) {
            System.out.println("Failed to configure Hadoop learning job!");
            throw e;
        }
    }
}
