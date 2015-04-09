/**
 * Mapper function to learn different features from given data-set and output 
 * each feature vector value as key-value pair
 * @author Ryan Millay, Nikit Waghela, Pramod Khare
 * CS6240
 * Assignment 3
 */

package airdel.a3;

//Import declarations
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import airdel.a3.util.Parser;


/**
 * Mapper parses the data line by line and generates the emits based
 * on the combinations of attributes and learn the features
 * to reducer
 * 
 * Input Key:  Line in file
 * Input Value:  Text content of the line
 * Output Keys:  Array of fields to consider for pattern detection
 * Output Value:  1 if the flight was delayed by 15 min, 0 otherwise (in DelayValue object format)
 */
public class DelayLearnerMapper 
extends Mapper<LongWritable, Text, Text, DelayValue> {
	private Parser parser = new Parser(',');
	@Override
	public void map(LongWritable offset, Text value, Context context) throws IOException, InterruptedException {
        try {
            // Send the value to the parser to put the data in to a map
            parser.parse(value.toString());


            // only process this line if it's valid
            if (parser.isValid()) {
                // Was this flight delayed?
                int isDelayed = parser.getInt("ArrDel15");

                // time to start writing to the context object
                for (String[] key : Parser.KEYS) {
                    context.write(new Text(parser.getKeyValuePairs(key)), new DelayValue(isDelayed,
                            1));
                }
            }
        } catch (final Exception e) {
            System.out.println("Invalid ArrDel15 value -" + e.getMessage());
        }
    }
}
