package airdel.a3;

/**
 * DelayLearnerCombiner combines all values for each unique key and emits the merged sum and count
 * 
 * @author Pramod Khare
 * @Created Tue Feb 24 19:35:48 EST 2015
 * @Modified
 */

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DelayLearnerCombiner extends Reducer<Text, DelayValue, Text, DelayValue> {

    @Override
    public void reduce(Text key, Iterable<DelayValue> values, Context context) throws IOException,
            InterruptedException {
        int sum = 0, total = 0;

        // Aggregate all sums and counts
        for (final DelayValue arrDel15 : values) {
            sum += arrDel15.getSum();
            total += arrDel15.getCount();
        }

        // Emit this combined sum and count values separated by pipe char
        context.write(key, new DelayValue(sum, total));
    }
}
