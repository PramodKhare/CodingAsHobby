package com.neu.mrlite.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemStore {
    private static final Map<String, Object> store = new ConcurrentHashMap<String, Object>();

    private InMemStore() {
    }

    /**
     * Puts the key and value into InMemoryStore
     * 
     * @param key
     * @param value
     * @return the previous value associated with key, or null if there was no
     *         mapping for key.
     */
    public static Object putKeyValue(final String key, final Object value) {
        if (key == null) {
            return null;
        }
        return store.put(key, value);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     * 
     * @param key
     * @return Returns the value to which the specified key is mapped, or null
     *         if this map contains no mapping for the key.
     */
    public static Object getValueForKey(final String key) {
        if (key == null) {
            return null;
        }
        return store.get(key);
    }

    /**
     * Returns boolean results if the key is present in the store
     * 
     * @param key
     * @return true if this map contains a mapping for the specified key
     */
    public static Object containsKey(final String key) {
        if (key == null) {
            return false;
        }
        return store.containsKey(key);
    }
}
