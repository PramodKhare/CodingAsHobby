package airdel.a3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.hadoop.io.WritableComparable;

/**
 * This is a value class outputed from Mapper function. It contains two parts - (1) sum - Sum of 1s
 * indicating total number of delays (2) count - represents total observations till this point
 * 
 * @author Pramod Khare
 * @Created Wed 25 Feb 2015 23:22:21 PM EST
 */

public class DelayValue implements WritableComparable<DelayValue> {

    private int sum;
    private int count;

    public DelayValue() {
        this.sum = 0;
        this.count = 0;
    }

    public DelayValue(final int sum, final int count) {
        this.sum = sum;
        this.count = count;
    }

    @Override
    public String toString() {
        return MessageFormat.format("'{'{0},{1}'}'", this.sum, this.count);
    }

    public void readFields(final DataInput in) throws IOException {
        this.sum = in.readInt();
        this.count = in.readInt();
    }

    public void write(final DataOutput out) throws IOException {
        out.writeInt(this.sum);
        out.writeInt(this.count);
    }

    public int compareTo(final DelayValue key2) {
        return (this.sum < key2.sum) ? -1 : ((this.sum == key2.sum) ? 0 : 1);
    }

    /********************************************************
     * Getters and Setters
     ********************************************************/

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public int getSum() {
        return this.sum;
    }

    public void setSum(final int sum) {
        this.sum = sum;
    }
}
