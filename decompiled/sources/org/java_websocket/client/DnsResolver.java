package org.java_websocket.client;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/client/DnsResolver.class */
public interface DnsResolver {
    InetAddress resolve(URI uri) throws UnknownHostException;
}
