package org.java_websocket.protocols;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/protocols/IProtocol.class */
public interface IProtocol {
    boolean acceptProvidedProtocol(String str);

    String getProvidedProtocol();

    IProtocol copyInstance();

    String toString();
}
