package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/AbstractSecurityContextImpl.class */
public class AbstractSecurityContextImpl {
    private final Map content = Collections.synchronizedMap(new HashMap());
    private final List<SecurityEventListener> securityEventListeners = new ArrayList(2);

    public void addSecurityEventListener(SecurityEventListener securityEventListener) {
        if (securityEventListener != null) {
            this.securityEventListeners.add(securityEventListener);
        }
    }

    public synchronized void registerSecurityEvent(SecurityEvent securityEvent) throws XMLSecurityException {
        forwardSecurityEvent(securityEvent);
    }

    protected void forwardSecurityEvent(SecurityEvent securityEvent) throws XMLSecurityException {
        for (SecurityEventListener securityEventListener : this.securityEventListeners) {
            securityEventListener.registerSecurityEvent(securityEvent);
        }
    }

    public <T> void put(String key, T value) {
        this.content.put(key, value);
    }

    public <T> T get(String str) {
        return (T) this.content.get(str);
    }

    public <T> T remove(String str) {
        return (T) this.content.remove(str);
    }

    public <T extends List> void putList(Object key, T value) {
        if (value == null) {
            return;
        }
        List<T> entry = (List) this.content.get(key);
        if (entry == null) {
            entry = new ArrayList<>();
            this.content.put(key, entry);
        }
        entry.addAll(value);
    }

    public <T> void putAsList(Object key, T value) {
        List<T> entry = (List) this.content.get(key);
        if (entry == null) {
            entry = new ArrayList<>();
            this.content.put(key, entry);
        }
        entry.add(value);
    }

    public <T> List<T> getAsList(Object key) {
        return (List) this.content.get(key);
    }

    public <T, U> void putAsMap(Object key, T mapKey, U mapValue) {
        Map<T, U> entry = (Map) this.content.get(key);
        if (entry == null) {
            entry = new HashMap<>();
            this.content.put(key, entry);
        }
        entry.put(mapKey, mapValue);
    }

    public <T, U> Map<T, U> getAsMap(Object key) {
        return (Map) this.content.get(key);
    }
}
