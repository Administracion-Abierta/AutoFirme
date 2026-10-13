package org.java_websocket.handshake;

import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/handshake/HandshakedataImpl1.class */
public class HandshakedataImpl1 implements HandshakeBuilder {
    private byte[] content;
    private TreeMap<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Override // org.java_websocket.handshake.Handshakedata
    public Iterator<String> iterateHttpFields() {
        return Collections.unmodifiableSet(this.map.keySet()).iterator();
    }

    @Override // org.java_websocket.handshake.Handshakedata
    public String getFieldValue(String name) {
        String s = this.map.get(name);
        if (s == null) {
            return "";
        }
        return s;
    }

    @Override // org.java_websocket.handshake.Handshakedata
    public byte[] getContent() {
        return this.content;
    }

    @Override // org.java_websocket.handshake.HandshakeBuilder
    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override // org.java_websocket.handshake.HandshakeBuilder
    public void put(String name, String value) {
        this.map.put(name, value);
    }

    @Override // org.java_websocket.handshake.Handshakedata
    public boolean hasFieldValue(String name) {
        return this.map.containsKey(name);
    }
}
