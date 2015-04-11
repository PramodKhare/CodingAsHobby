package com.neu.mrlite.clients;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.neu.mrlite.common.TaskConf;
import com.neu.mrlite.common.datastructures.Assortment;
import com.neu.mrlite.common.datastructures.IOCallback;
import com.neu.mrlite.common.datastructures.IOHandle;
import com.neu.mrlite.common.datastructures.POCallback;
import com.neu.mrlite.common.datastructures.Pair;
import com.neu.mrlite.common.datastructures.Writable;

public class ReducerClientTask extends Thread {
    private final TaskConf task;
    private final PrintWriter out;
    private List<Writable> outVal;

    public ReducerClientTask(final TaskConf task, final PrintWriter out) {
        this.task = task;
        this.out = out;
        start();
    }

    @Override
    public void run() {
        try {
            File file = new File(this.task.getExecutableJar());
            URL url = file.toURI().toURL();
            URL[] urls = new URL[] { url };
            ClassLoader cl = new URLClassLoader(urls);
            Class<?> cls = cl.loadClass(this.task.getMapperClass());
            Method[] m = cls.getDeclaredMethods();

            for (Method method : m) {
                if (method.getName().equals("run")) {
                    Object o = method.invoke(null, task.getInputFilePath(),
                            task.getOutDirPath());
                    execute(Assortment.getExecChain(), Assortment.getIOHandle());
                    break;
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
            out.println("Invalid Task configuration provided");
        }
    }

    public void execute(List<POCallback> exec, IOHandle io) {
        Object outKey;
        Gson gson = new Gson();
        for (POCallback p : exec) {
            System.out.println(p);
            if (p instanceof IOCallback) {
                ((IOCallback) p).process(io);
                outVal = (List<Writable>) p.getValue();
                outKey = p.getKey();
                continue;
            } else {
                List<Writable> interVal = new ArrayList<Writable>();
                for (Writable val : outVal) {
                    p.process(val);
                    if (p.getKey() != null && p.getValue() != null) {
                        interVal.add(new Writable(new Pair<Object, Object>(p
                                .getKey(), p.getValue())));
                    } else {
                        if (p.getValue() != null)
                            interVal.add(new Writable(p.getValue()));
                    }
                }
                outVal = interVal;
            }
        }
        out.println(gson.toJson(outVal));
    }
}
