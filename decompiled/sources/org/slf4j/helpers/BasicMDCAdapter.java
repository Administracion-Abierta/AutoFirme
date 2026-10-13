package org.slf4j.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.spi.MDCAdapter;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/helpers/BasicMDCAdapter.class */
public class BasicMDCAdapter implements MDCAdapter {
    private InheritableThreadLocal<Map<String, String>> inheritableThreadLocal = new InheritableThreadLocal<Map<String, String>>() { // from class: org.slf4j.helpers.BasicMDCAdapter.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.InheritableThreadLocal
        public Map<String, String> childValue(Map<String, String> parentValue) {
            if (parentValue == null) {
                return null;
            }
            return new HashMap(parentValue);
        }
    };

    @Override // org.slf4j.spi.MDCAdapter
    public void put(String key, String val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map == null) {
            map = new HashMap();
            this.inheritableThreadLocal.set(map);
        }
        map.put(key, val);
    }

    @Override // org.slf4j.spi.MDCAdapter
    public String get(String key) {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null && key != null) {
            return map.get(key);
        }
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void remove(String key) {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            map.remove(key);
        }
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void clear() {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            map.clear();
            this.inheritableThreadLocal.remove();
        }
    }

    public Set<String> getKeys() {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            return map.keySet();
        }
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public Map<String, String> getCopyOfContextMap() {
        Map<String, String> oldMap = this.inheritableThreadLocal.get();
        if (oldMap != null) {
            return new HashMap(oldMap);
        }
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void setContextMap(Map<String, String> contextMap) {
        this.inheritableThreadLocal.set(new HashMap(contextMap));
    }
}
