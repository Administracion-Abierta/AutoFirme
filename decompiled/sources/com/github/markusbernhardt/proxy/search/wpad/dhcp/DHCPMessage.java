package com.github.markusbernhardt.proxy.search.wpad.dhcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/wpad/dhcp/DHCPMessage.class */
public class DHCPMessage {
    public static final byte OP_REQUEST = 1;
    public static final byte OP_REPLY = 2;
    public static final byte DHCPDISCOVER = 1;
    public static final byte DHCPOFFER = 2;
    public static final byte DHCPREQUEST = 3;
    public static final byte DHCPDECLINE = 4;
    public static final byte DHCPACK = 5;
    public static final byte DHCPNAK = 6;
    public static final byte DHCPRELEASE = 7;
    public static final byte DHCPINFORM = 8;
    public static final int CLIENT_PORT = 68;
    public static final int SERVER_PORT = 67;
    public static InetAddress BROADCAST_ADDR;
    private byte op;
    private byte htype;
    private byte hlen;
    private byte hops;
    private int xid;
    private short secs;
    private short flags;
    private byte[] ciaddr;
    private byte[] yiaddr;
    private byte[] siaddr;
    private byte[] giaddr;
    private byte[] chaddr;
    private byte[] sname;
    private byte[] file;
    private DHCPOptions optionsList;
    private int gPort;
    private InetAddress destination_IP;

    static {
        BROADCAST_ADDR = null;
        try {
            BROADCAST_ADDR = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
        }
    }

    public DHCPMessage() {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        this.destination_IP = BROADCAST_ADDR;
        this.gPort = 67;
    }

    public DHCPMessage(DHCPMessage inMessage) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        this.destination_IP = BROADCAST_ADDR;
        this.gPort = 67;
        this.op = inMessage.getOp();
        this.htype = inMessage.getHtype();
        this.hlen = inMessage.getHlen();
        this.hops = inMessage.getHops();
        this.xid = inMessage.getXid();
        this.secs = inMessage.getSecs();
        this.flags = inMessage.getFlags();
        this.ciaddr = inMessage.getCiaddr();
        this.yiaddr = inMessage.getYiaddr();
        this.siaddr = inMessage.getSiaddr();
        this.giaddr = inMessage.getGiaddr();
        this.chaddr = inMessage.getChaddr();
        this.sname = inMessage.getSname();
        this.file = inMessage.getFile();
        this.optionsList.internalize(inMessage.getOptions());
    }

    public DHCPMessage(DHCPMessage inMessage, InetAddress inServername, int inPort) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        this.destination_IP = inServername;
        this.gPort = inPort;
        this.op = inMessage.getOp();
        this.htype = inMessage.getHtype();
        this.hlen = inMessage.getHlen();
        this.hops = inMessage.getHops();
        this.xid = inMessage.getXid();
        this.secs = inMessage.getSecs();
        this.flags = inMessage.getFlags();
        this.ciaddr = inMessage.getCiaddr();
        this.yiaddr = inMessage.getYiaddr();
        this.siaddr = inMessage.getSiaddr();
        this.giaddr = inMessage.getGiaddr();
        this.chaddr = inMessage.getChaddr();
        this.sname = inMessage.getSname();
        this.file = inMessage.getFile();
        this.optionsList.internalize(inMessage.getOptions());
    }

    public DHCPMessage(DHCPMessage inMessage, InetAddress inServername) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        this.destination_IP = inServername;
        this.gPort = 67;
        this.op = inMessage.getOp();
        this.htype = inMessage.getHtype();
        this.hlen = inMessage.getHlen();
        this.hops = inMessage.getHops();
        this.xid = inMessage.getXid();
        this.secs = inMessage.getSecs();
        this.flags = inMessage.getFlags();
        this.ciaddr = inMessage.getCiaddr();
        this.yiaddr = inMessage.getYiaddr();
        this.siaddr = inMessage.getSiaddr();
        this.giaddr = inMessage.getGiaddr();
        this.chaddr = inMessage.getChaddr();
        this.sname = inMessage.getSname();
        this.file = inMessage.getFile();
        this.optionsList.internalize(inMessage.getOptions());
    }

    public DHCPMessage(InetAddress inServername, int inPort) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        this.destination_IP = inServername;
        this.gPort = inPort;
    }

    public DHCPMessage(InetAddress inServername) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        this.destination_IP = inServername;
        this.gPort = 67;
    }

    public DHCPMessage(int inPort) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        this.destination_IP = BROADCAST_ADDR;
        this.gPort = inPort;
    }

    public DHCPMessage(byte[] ibuf) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        internalize(ibuf);
        this.destination_IP = BROADCAST_ADDR;
        this.gPort = 67;
    }

    public DHCPMessage(byte[] ibuf, InetAddress inServername, int inPort) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        internalize(ibuf);
        this.destination_IP = inServername;
        this.gPort = inPort;
    }

    public DHCPMessage(byte[] ibuf, int inPort) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        internalize(ibuf);
        this.destination_IP = BROADCAST_ADDR;
        this.gPort = inPort;
    }

    public DHCPMessage(byte[] ibuf, InetAddress inServername) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        internalize(ibuf);
        this.destination_IP = inServername;
        this.gPort = 67;
    }

    public DHCPMessage(DataInputStream inStream) {
        this.ciaddr = new byte[4];
        this.yiaddr = new byte[4];
        this.siaddr = new byte[4];
        this.giaddr = new byte[4];
        this.chaddr = new byte[16];
        this.sname = new byte[64];
        this.file = new byte[128];
        this.optionsList = null;
        initialize();
        try {
            this.op = inStream.readByte();
            this.htype = inStream.readByte();
            this.hlen = inStream.readByte();
            this.hops = inStream.readByte();
            this.xid = inStream.readInt();
            this.secs = inStream.readShort();
            this.flags = inStream.readShort();
            inStream.readFully(this.ciaddr, 0, 4);
            inStream.readFully(this.yiaddr, 0, 4);
            inStream.readFully(this.siaddr, 0, 4);
            inStream.readFully(this.giaddr, 0, 4);
            inStream.readFully(this.chaddr, 0, 16);
            inStream.readFully(this.sname, 0, 64);
            inStream.readFully(this.file, 0, 128);
            byte[] options = new byte[312];
            inStream.readFully(options, 0, 312);
            this.optionsList.internalize(options);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void initialize() {
        this.optionsList = new DHCPOptions();
    }

    public synchronized byte[] externalize() {
        ByteArrayOutputStream outBStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outBStream);
        try {
            outStream.writeByte(this.op);
            outStream.writeByte(this.htype);
            outStream.writeByte(this.hlen);
            outStream.writeByte(this.hops);
            outStream.writeInt(this.xid);
            outStream.writeShort(this.secs);
            outStream.writeShort(this.flags);
            outStream.write(this.ciaddr, 0, 4);
            outStream.write(this.yiaddr, 0, 4);
            outStream.write(this.siaddr, 0, 4);
            outStream.write(this.giaddr, 0, 4);
            outStream.write(this.chaddr, 0, 16);
            outStream.write(this.sname, 0, 64);
            outStream.write(this.file, 0, 128);
            byte[] bArr = new byte[312];
            if (this.optionsList == null) {
                initialize();
            }
            byte[] options = this.optionsList.externalize();
            outStream.write(options, 0, 312);
        } catch (IOException e) {
            System.err.println(e);
        }
        byte[] data = outBStream.toByteArray();
        return data;
    }

    public synchronized DHCPMessage internalize(byte[] ibuff) {
        ByteArrayInputStream inBStream = new ByteArrayInputStream(ibuff, 0, ibuff.length);
        DataInputStream inStream = new DataInputStream(inBStream);
        try {
            this.op = inStream.readByte();
            this.htype = inStream.readByte();
            this.hlen = inStream.readByte();
            this.hops = inStream.readByte();
            this.xid = inStream.readInt();
            this.secs = inStream.readShort();
            this.flags = inStream.readShort();
            inStream.readFully(this.ciaddr, 0, 4);
            inStream.readFully(this.yiaddr, 0, 4);
            inStream.readFully(this.siaddr, 0, 4);
            inStream.readFully(this.giaddr, 0, 4);
            inStream.readFully(this.chaddr, 0, 16);
            inStream.readFully(this.sname, 0, 64);
            inStream.readFully(this.file, 0, 128);
            byte[] options = new byte[312];
            inStream.readFully(options, 0, 312);
            if (this.optionsList == null) {
                initialize();
            }
            this.optionsList.internalize(options);
        } catch (IOException e) {
            System.err.println(e);
        }
        return this;
    }

    public void setOp(byte inOp) {
        this.op = inOp;
    }

    public void setHtype(byte inHtype) {
        this.htype = inHtype;
    }

    public void setHlen(byte inHlen) {
        this.hlen = inHlen;
    }

    public void setHops(byte inHops) {
        this.hops = inHops;
    }

    public void setXid(int inXid) {
        this.xid = inXid;
    }

    public void setSecs(short inSecs) {
        this.secs = inSecs;
    }

    public void setFlags(short inFlags) {
        this.flags = inFlags;
    }

    public void setCiaddr(byte[] inCiaddr) {
        this.ciaddr = inCiaddr;
    }

    public void setYiaddr(byte[] inYiaddr) {
        this.yiaddr = inYiaddr;
    }

    public void setSiaddr(byte[] inSiaddr) {
        this.siaddr = inSiaddr;
    }

    public void setGiaddr(byte[] inGiaddr) {
        this.giaddr = inGiaddr;
    }

    public void setChaddr(byte[] inChaddr) {
        this.chaddr = inChaddr;
    }

    public void setSname(byte[] inSname) {
        this.sname = inSname;
    }

    public void setFile(byte[] inFile) {
        this.file = inFile;
    }

    public void setPort(int inPortNum) {
        this.gPort = inPortNum;
    }

    public void setDestinationHost(String inHost) {
        try {
            this.destination_IP = InetAddress.getByName(inHost);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public byte getOp() {
        return this.op;
    }

    public byte getHtype() {
        return this.htype;
    }

    public byte getHlen() {
        return this.hlen;
    }

    public byte getHops() {
        return this.hops;
    }

    public int getXid() {
        return this.xid;
    }

    public short getSecs() {
        return this.secs;
    }

    public short getFlags() {
        return this.flags;
    }

    public byte[] getCiaddr() {
        return this.ciaddr;
    }

    public byte[] getYiaddr() {
        return this.yiaddr;
    }

    public byte[] getSiaddr() {
        return this.siaddr;
    }

    public byte[] getGiaddr() {
        return this.giaddr;
    }

    public byte[] getChaddr() {
        return this.chaddr;
    }

    public byte[] getSname() {
        return this.sname;
    }

    public byte[] getFile() {
        return this.file;
    }

    public byte[] getOptions() {
        if (this.optionsList == null) {
            initialize();
        }
        return this.optionsList.externalize();
    }

    public int getPort() {
        return this.gPort;
    }

    public String getDestinationAddress() {
        return this.destination_IP.getHostAddress();
    }

    public void setOption(int inOptNum, byte[] inOptionData) {
        this.optionsList.setOption((byte) inOptNum, inOptionData);
    }

    public byte[] getOption(int inOptNum) {
        if (this.optionsList == null) {
            initialize();
        }
        return this.optionsList.getOption((byte) inOptNum);
    }

    public void removeOption(int inOptNum) {
        if (this.optionsList == null) {
            initialize();
        }
        this.optionsList.removeOption((byte) inOptNum);
    }

    public boolean IsOptSet(int inOptNum) {
        if (this.optionsList == null) {
            initialize();
        }
        return this.optionsList.contains((byte) inOptNum);
    }
}
