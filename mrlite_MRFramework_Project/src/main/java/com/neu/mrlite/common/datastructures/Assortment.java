package com.neu.mrlite.common.datastructures;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Generic Collection type, that also holds the contract of operations that it
 * can execute in parallel. ParallelOperations needs a callback to execute the
 * task. This callback is provided by POCallback abstract class. map, reduce and
 * combine methods are specific to their mode and purpose of operation. It holds
 * an IOHandle to read inputs and generate output. The collection is an
 * ArrayList of type 'T' internally. Every operation is a deferred execution to
 * run in parallel once the job is ready to execute.
 * 
 * It implements Collection&lt;ANY&gt; interface just to adhere to standards.
 * here ANY type is a strict set of DataTypes including [java autoboxing
 * classes, Pair&lt;ANY,ANY&gt;]
 * 
 * @author nikit
 *
 * @param <T>
 */
public class Assortment<T> implements Collection<T>, ParallelOperations<T>,
        Serializable {
    private static final long serialVersionUID = -7209166331043646984L;

    private List<T> list;
    private IOHandle IOHANDLE;
    private List<POCallback> exec = new ArrayList<POCallback>();

    public Assortment() {
        this.list = new ArrayList<T>();
    }

    public Assortment(final List<T> list) {
        this.list = list;
    }

    /**
     * Map Operation
     */
    public <K extends Comparable<K>, V> Assortment<Pair<K, V>> parallel(
            POCallback<K, V> callback) {
        Assortment<Pair<K, V>> res = new Assortment<Pair<K, V>>();
        this.exec.add(callback);
        return res;
    }

    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean contains(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    public Iterator<T> iterator() {
        // TODO Auto-generated method stub
        return list.iterator();
    }

    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(Object[] a) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean add(T e) {
        // TODO Auto-generated method stub
        return list.add(e);
    }

    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean addAll(Collection<? extends T> c) {
        return list.addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    public void clear() {
        // TODO Auto-generated method stub

    }

    /**
     * Reads the input file and performs the split (future scope). It collects
     * the String Assortment and returns it to caller for more operations.
     * 
     * @param io
     * @return Assortment&lt;String&gt;
     */
    public static Assortment<String> readInputFrom(final IOHandle io) {
        Assortment<String> newAssort = new Assortment<String>();
        newAssort.IOHANDLE = io;
        newAssort.exec.add(new IOCallback() {
            @Override
            public void process(Writable data) {
                // dummy
            }

            @Override
            public void process(IOHandle io) {
                List<Writable> lines = new ArrayList<Writable>();
                String line;
                try {
                    while ((line = io.readLine()) != null) {
                        lines.add(new Writable(line));
                    }
                    emit("INPUT_DATA", lines);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return newAssort;
    }

    public List<POCallback> getExecChain() {
        return this.exec;
    }

    public IOHandle getIOHandle() throws FileNotFoundException,
            UnsupportedEncodingException {
        return this.IOHANDLE;
    }

    public String toString() {
        return this.list.toString();
    }
}