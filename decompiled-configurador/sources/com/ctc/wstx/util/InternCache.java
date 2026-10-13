package com.ctc.wstx.util;

import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/InternCache.class */
public final class InternCache extends LinkedHashMap<String, String> {
    private static final int DEFAULT_SIZE = 64;
    private static final int MAX_SIZE = 660;
    private static final InternCache sInstance = new InternCache();

    private InternCache() {
        super(64, 0.6666f, false);
    }

    public static InternCache getInstance() {
        return sInstance;
    }

    public String intern(String input) {
        String result;
        synchronized (this) {
            result = get(input);
        }
        if (result == null) {
            result = input.intern();
            synchronized (this) {
                put(result, result);
            }
        }
        return result;
    }

    @Override // java.util.LinkedHashMap
    protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
        return size() > MAX_SIZE;
    }
}
