package airdel.a3;
/**
 * Reducer class to evaluate the delay pecentage for
 * ever group of attribute received from mapper 
 * @author Nikit Waghela
 * @author Ryan Millay
 * @author Pramod Khare
 */
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DelayLearnerReducer extends Reducer<Text, DelayValue, Text, IntWritable> {
	
	/**
	 * Learns the features and puts down the learnings in
	 * model
	 * @author nikit
	 * 
	 * produces <Text, IntWritable>
	 */
    @Override
    public void reduce(Text key, Iterable<DelayValue> values, Context context) throws IOException,
            InterruptedException {
        int sum = 0, total = 0;

        // Aggregate all sums and counts
        for (final DelayValue arrDel15 : values) {
            sum += arrDel15.getSum();
            total += arrDel15.getCount();
        }
        // Calculate the percentage of delays for current key value
        // As per our implementation, average equals the percentage,
        // because we emit 1 for delay and 0 for no delay from map-function
        int delPercentage = 0;
        if (total > 0 && sum > 0) {
            // We only take the 2-digits from fraction - from percentage value
            // In order to keep the values in integer range - multiply by 10000
            // So delPercentage will be in range 0 (0%) to 10000 (100.00%)
            delPercentage = (int) (((float) sum / total) * 10000);
        }

        context.write(key, new IntWritable(delPercentage));
    }
}
