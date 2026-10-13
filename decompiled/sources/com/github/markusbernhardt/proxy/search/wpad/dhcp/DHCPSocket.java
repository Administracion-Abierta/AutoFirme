package com.github.markusbernhardt.proxy.search.wpad.dhcp;

import com.github.markusbernhardt.proxy.util.Logger;
import com.sun.jna.platform.win32.WinError;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/wpad/dhcp/DHCPSocket.class */
public class DHCPSocket extends DatagramSocket {
    private static final int SOCKET_TIMEOUT = 1000;
    private int mtu;

    public DHCPSocket(int inPort) throws SocketException {
        super(inPort);
        this.mtu = WinError.ERROR_EVENTLOG_FILE_CORRUPT;
        setSoTimeout(1000);
    }

    public DHCPSocket(int inPort, InetAddress lAddr) throws SocketException {
        super(inPort, lAddr);
        this.mtu = WinError.ERROR_EVENTLOG_FILE_CORRUPT;
        setSoTimeout(1000);
    }

    public void setMTU(int inSize) {
        this.mtu = inSize;
    }

    public int getMTU() {
        return this.mtu;
    }

    public synchronized void send(DHCPMessage inMessage) throws IOException {
        byte[] data = inMessage.externalize();
        InetAddress dest = null;
        try {
            dest = InetAddress.getByName(inMessage.getDestinationAddress());
        } catch (UnknownHostException e) {
            Logger.log(getClass(), Logger.LogLevel.ERROR, "Host {} not found", inMessage.getDestinationAddress());
        }
        DatagramPacket outgoing = new DatagramPacket(data, data.length, dest, inMessage.getPort());
        send(outgoing);
    }

    public synchronized boolean receive(DHCPMessage outMessage) {
        try {
            DatagramPacket incoming = new DatagramPacket(new byte[this.mtu], this.mtu);
            receive(incoming);
            outMessage.internalize(incoming.getData());
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
