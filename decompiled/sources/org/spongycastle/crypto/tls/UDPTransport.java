package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/UDPTransport.class */
public class UDPTransport implements DatagramTransport {
    protected static final int MIN_IP_OVERHEAD = 20;
    protected static final int MAX_IP_OVERHEAD = 84;
    protected static final int UDP_OVERHEAD = 8;
    protected final DatagramSocket socket;
    protected final int receiveLimit;
    protected final int sendLimit;

    public UDPTransport(DatagramSocket socket, int mtu) throws IOException {
        if (!socket.isBound() || !socket.isConnected()) {
            throw new IllegalArgumentException("'socket' must be bound and connected");
        }
        this.socket = socket;
        this.receiveLimit = (mtu - 20) - 8;
        this.sendLimit = (mtu - 84) - 8;
    }

    @Override // org.spongycastle.crypto.tls.DatagramTransport
    public int getReceiveLimit() {
        return this.receiveLimit;
    }

    @Override // org.spongycastle.crypto.tls.DatagramTransport
    public int getSendLimit() {
        return this.sendLimit;
    }

    @Override // org.spongycastle.crypto.tls.DatagramTransport
    public int receive(byte[] buf, int off, int len, int waitMillis) throws IOException {
        this.socket.setSoTimeout(waitMillis);
        DatagramPacket packet = new DatagramPacket(buf, off, len);
        this.socket.receive(packet);
        return packet.getLength();
    }

    @Override // org.spongycastle.crypto.tls.DatagramTransport
    public void send(byte[] buf, int off, int len) throws IOException {
        if (len > getSendLimit()) {
            throw new TlsFatalAlert((short) 80);
        }
        DatagramPacket packet = new DatagramPacket(buf, off, len);
        this.socket.send(packet);
    }

    @Override // org.spongycastle.crypto.tls.DatagramTransport
    public void close() throws IOException {
        this.socket.close();
    }
}
