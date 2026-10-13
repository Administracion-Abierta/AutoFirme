package org.java_websocket.handshake;

import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/handshake/Handshakedata.class */
public interface Handshakedata {
    Iterator<String> iterateHttpFields();

    String getFieldValue(String str);

    boolean hasFieldValue(String str);

    byte[] getContent();
}
