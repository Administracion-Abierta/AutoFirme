package org.spongycastle.crypto.tls;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/DatagramTransport.class */
public interface DatagramTransport {
    int getReceiveLimit() throws IOException;

    int getSendLimit() throws IOException;

    int receive(byte[] bArr, int i, int i2, int i3) throws IOException;

    void send(byte[] bArr, int i, int i2) throws IOException;

    void close() throws IOException;
}
