package com.sun.jna.platform.win32;

import com.aowagie.text.ElementTags;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI.class */
public interface IPHlpAPI extends Library {
    public static final IPHlpAPI INSTANCE = (IPHlpAPI) Native.load("IPHlpAPI", IPHlpAPI.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int IF_MAX_STRING_SIZE = 256;
    public static final int IF_MAX_PHYS_ADDRESS_LENGTH = 32;
    public static final int MAX_INTERFACE_NAME_LEN = 256;
    public static final int MAXLEN_IFDESCR = 256;
    public static final int MAXLEN_PHYSADDR = 8;
    public static final int MAX_HOSTNAME_LEN = 128;
    public static final int MAX_DOMAIN_NAME_LEN = 128;
    public static final int MAX_SCOPE_ID_LEN = 256;
    public static final int AF_UNSPEC = 0;
    public static final int AF_INET = 2;
    public static final int AF_IPX = 6;
    public static final int AF_NETBIOS = 17;
    public static final int AF_INET6 = 23;
    public static final int AF_IRDA = 26;
    public static final int AF_BTH = 32;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$IP_ADDRESS_STRING.class */
    @Structure.FieldOrder({"String"})
    public static class IP_ADDRESS_STRING extends Structure {
        public byte[] String = new byte[16];
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$IP_ADDR_STRING.class */
    @Structure.FieldOrder({"Next", "IpAddress", "IpMask", "Context"})
    public static class IP_ADDR_STRING extends Structure {
        public ByReference Next;
        public IP_ADDRESS_STRING IpAddress;
        public IP_ADDRESS_STRING IpMask;
        public int Context;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$IP_ADDR_STRING$ByReference.class */
        public static class ByReference extends IP_ADDR_STRING implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_IFROW.class */
    @Structure.FieldOrder({"wszName", "dwIndex", "dwType", "dwMtu", "dwSpeed", "dwPhysAddrLen", "bPhysAddr", "dwAdminStatus", "dwOperStatus", "dwLastChange", "dwInOctets", "dwInUcastPkts", "dwInNUcastPkts", "dwInDiscards", "dwInErrors", "dwInUnknownProtos", "dwOutOctets", "dwOutUcastPkts", "dwOutNUcastPkts", "dwOutDiscards", "dwOutErrors", "dwOutQLen", "dwDescrLen", "bDescr"})
    public static class MIB_IFROW extends Structure {
        public int dwIndex;
        public int dwType;
        public int dwMtu;
        public int dwSpeed;
        public int dwPhysAddrLen;
        public int dwAdminStatus;
        public int dwOperStatus;
        public int dwLastChange;
        public int dwInOctets;
        public int dwInUcastPkts;
        public int dwInNUcastPkts;
        public int dwInDiscards;
        public int dwInErrors;
        public int dwInUnknownProtos;
        public int dwOutOctets;
        public int dwOutUcastPkts;
        public int dwOutNUcastPkts;
        public int dwOutDiscards;
        public int dwOutErrors;
        public int dwOutQLen;
        public int dwDescrLen;
        public char[] wszName = new char[256];
        public byte[] bPhysAddr = new byte[8];
        public byte[] bDescr = new byte[256];
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_IF_ROW2.class */
    @Structure.FieldOrder({"InterfaceLuid", "InterfaceIndex", "InterfaceGuid", "Alias", "Description", "PhysicalAddressLength", "PhysicalAddress", "PermanentPhysicalAddress", "Mtu", "Type", "TunnelType", "MediaType", "PhysicalMediumType", "AccessType", "DirectionType", "InterfaceAndOperStatusFlags", "OperStatus", "AdminStatus", "MediaConnectState", "NetworkGuid", "ConnectionType", "TransmitLinkSpeed", "ReceiveLinkSpeed", "InOctets", "InUcastPkts", "InNUcastPkts", "InDiscards", "InErrors", "InUnknownProtos", "InUcastOctets", "InMulticastOctets", "InBroadcastOctets", "OutOctets", "OutUcastPkts", "OutNUcastPkts", "OutDiscards", "OutErrors", "OutUcastOctets", "OutMulticastOctets", "OutBroadcastOctets", "OutQLen"})
    public static class MIB_IF_ROW2 extends Structure {
        public long InterfaceLuid;
        public int InterfaceIndex;
        public Guid.GUID InterfaceGuid;
        public int PhysicalAddressLength;
        public int Mtu;
        public int Type;
        public int TunnelType;
        public int MediaType;
        public int PhysicalMediumType;
        public int AccessType;
        public int DirectionType;
        public byte InterfaceAndOperStatusFlags;
        public int OperStatus;
        public int AdminStatus;
        public int MediaConnectState;
        public Guid.GUID NetworkGuid;
        public int ConnectionType;
        public long TransmitLinkSpeed;
        public long ReceiveLinkSpeed;
        public long InOctets;
        public long InUcastPkts;
        public long InNUcastPkts;
        public long InDiscards;
        public long InErrors;
        public long InUnknownProtos;
        public long InUcastOctets;
        public long InMulticastOctets;
        public long InBroadcastOctets;
        public long OutOctets;
        public long OutUcastPkts;
        public long OutNUcastPkts;
        public long OutDiscards;
        public long OutErrors;
        public long OutUcastOctets;
        public long OutMulticastOctets;
        public long OutBroadcastOctets;
        public long OutQLen;
        public char[] Alias = new char[257];
        public char[] Description = new char[257];
        public byte[] PhysicalAddress = new byte[32];
        public byte[] PermanentPhysicalAddress = new byte[32];
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_TCP6ROW_OWNER_PID.class */
    @Structure.FieldOrder({"LocalAddr", "dwLocalScopeId", "dwLocalPort", "RemoteAddr", "dwRemoteScopeId", "dwRemotePort", "State", "dwOwningPid"})
    public static class MIB_TCP6ROW_OWNER_PID extends Structure {
        public int dwLocalScopeId;
        public int dwLocalPort;
        public int dwRemoteScopeId;
        public int dwRemotePort;
        public int State;
        public int dwOwningPid;
        public byte[] LocalAddr = new byte[16];
        public byte[] RemoteAddr = new byte[16];
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_TCPROW_OWNER_PID.class */
    @Structure.FieldOrder({"dwState", "dwLocalAddr", "dwLocalPort", "dwRemoteAddr", "dwRemotePort", "dwOwningPid"})
    public static class MIB_TCPROW_OWNER_PID extends Structure {
        public int dwState;
        public int dwLocalAddr;
        public int dwLocalPort;
        public int dwRemoteAddr;
        public int dwRemotePort;
        public int dwOwningPid;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_TCPSTATS.class */
    @Structure.FieldOrder({"dwRtoAlgorithm", "dwRtoMin", "dwRtoMax", "dwMaxConn", "dwActiveOpens", "dwPassiveOpens", "dwAttemptFails", "dwEstabResets", "dwCurrEstab", "dwInSegs", "dwOutSegs", "dwRetransSegs", "dwInErrs", "dwOutRsts", "dwNumConns"})
    public static class MIB_TCPSTATS extends Structure {
        public int dwRtoAlgorithm;
        public int dwRtoMin;
        public int dwRtoMax;
        public int dwMaxConn;
        public int dwActiveOpens;
        public int dwPassiveOpens;
        public int dwAttemptFails;
        public int dwEstabResets;
        public int dwCurrEstab;
        public int dwInSegs;
        public int dwOutSegs;
        public int dwRetransSegs;
        public int dwInErrs;
        public int dwOutRsts;
        public int dwNumConns;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_TCP_STATE.class */
    public interface MIB_TCP_STATE {
        public static final int MIB_TCP_STATE_CLOSED = 1;
        public static final int MIB_TCP_STATE_LISTEN = 2;
        public static final int MIB_TCP_STATE_SYN_SENT = 3;
        public static final int MIB_TCP_STATE_SYN_RCVD = 4;
        public static final int MIB_TCP_STATE_ESTAB = 5;
        public static final int MIB_TCP_STATE_FIN_WAIT1 = 6;
        public static final int MIB_TCP_STATE_FIN_WAIT2 = 7;
        public static final int MIB_TCP_STATE_CLOSE_WAIT = 8;
        public static final int MIB_TCP_STATE_CLOSING = 9;
        public static final int MIB_TCP_STATE_LAST_ACK = 10;
        public static final int MIB_TCP_STATE_TIME_WAIT = 11;
        public static final int MIB_TCP_STATE_DELETE_TCB = 12;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_UDP6ROW_OWNER_PID.class */
    @Structure.FieldOrder({"ucLocalAddr", "dwLocalScopeId", "dwLocalPort", "dwOwningPid"})
    public static class MIB_UDP6ROW_OWNER_PID extends Structure {
        public byte[] ucLocalAddr = new byte[16];
        public int dwLocalScopeId;
        public int dwLocalPort;
        public int dwOwningPid;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_UDPROW_OWNER_PID.class */
    @Structure.FieldOrder({"dwLocalAddr", "dwLocalPort", "dwOwningPid"})
    public static class MIB_UDPROW_OWNER_PID extends Structure {
        public int dwLocalAddr;
        public int dwLocalPort;
        public int dwOwningPid;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_UDPSTATS.class */
    @Structure.FieldOrder({"dwInDatagrams", "dwNoPorts", "dwInErrors", "dwOutDatagrams", "dwNumAddrs"})
    public static class MIB_UDPSTATS extends Structure {
        public int dwInDatagrams;
        public int dwNoPorts;
        public int dwInErrors;
        public int dwOutDatagrams;
        public int dwNumAddrs;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$TCP_TABLE_CLASS.class */
    public interface TCP_TABLE_CLASS {
        public static final int TCP_TABLE_BASIC_LISTENER = 0;
        public static final int TCP_TABLE_BASIC_CONNECTIONS = 1;
        public static final int TCP_TABLE_BASIC_ALL = 2;
        public static final int TCP_TABLE_OWNER_PID_LISTENER = 3;
        public static final int TCP_TABLE_OWNER_PID_CONNECTIONS = 4;
        public static final int TCP_TABLE_OWNER_PID_ALL = 5;
        public static final int TCP_TABLE_OWNER_MODULE_LISTENER = 6;
        public static final int TCP_TABLE_OWNER_MODULE_CONNECTIONS = 7;
        public static final int TCP_TABLE_OWNER_MODULE_ALL = 8;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$UDP_TABLE_CLASS.class */
    public interface UDP_TABLE_CLASS {
        public static final int UDP_TABLE_BASIC = 0;
        public static final int UDP_TABLE_OWNER_PID = 1;
        public static final int UDP_TABLE_OWNER_MODULE = 2;
    }

    int GetIfEntry(MIB_IFROW mib_ifrow);

    int GetIfEntry2(MIB_IF_ROW2 mib_if_row2);

    int GetNetworkParams(Pointer pointer, IntByReference intByReference);

    int GetTcpStatistics(MIB_TCPSTATS mib_tcpstats);

    int GetTcpStatisticsEx(MIB_TCPSTATS mib_tcpstats, int i);

    int GetUdpStatistics(MIB_UDPSTATS mib_udpstats);

    int GetUdpStatisticsEx(MIB_UDPSTATS mib_udpstats, int i);

    int GetExtendedTcpTable(Pointer pointer, IntByReference intByReference, boolean z, int i, int i2, int i3);

    int GetExtendedUdpTable(Pointer pointer, IntByReference intByReference, boolean z, int i, int i2, int i3);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$FIXED_INFO.class */
    @Structure.FieldOrder({"HostName", "DomainName", "CurrentDnsServer", "DnsServerList", "NodeType", "ScopeId", "EnableRouting", "EnableProxy", "EnableDns"})
    public static class FIXED_INFO extends Structure {
        public byte[] HostName;
        public byte[] DomainName;
        public IP_ADDR_STRING.ByReference CurrentDnsServer;
        public IP_ADDR_STRING DnsServerList;
        public int NodeType;
        public byte[] ScopeId;
        public int EnableRouting;
        public int EnableProxy;
        public int EnableDns;

        public FIXED_INFO(Pointer p) {
            super(p);
            this.HostName = new byte[132];
            this.DomainName = new byte[132];
            this.ScopeId = new byte[260];
            read();
        }

        public FIXED_INFO() {
            this.HostName = new byte[132];
            this.DomainName = new byte[132];
            this.ScopeId = new byte[260];
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_TCPTABLE_OWNER_PID.class */
    @Structure.FieldOrder({"dwNumEntries", ElementTags.TABLE})
    public static class MIB_TCPTABLE_OWNER_PID extends Structure {
        public int dwNumEntries;
        public MIB_TCPROW_OWNER_PID[] table;

        public MIB_TCPTABLE_OWNER_PID(Pointer buf) {
            super(buf);
            this.table = new MIB_TCPROW_OWNER_PID[1];
            read();
        }

        @Override // com.sun.jna.Structure
        public void read() {
            this.dwNumEntries = getPointer().getInt(0L);
            if (this.dwNumEntries > 0) {
                this.table = (MIB_TCPROW_OWNER_PID[]) new MIB_TCPROW_OWNER_PID().toArray(this.dwNumEntries);
                super.read();
            } else {
                this.table = new MIB_TCPROW_OWNER_PID[0];
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_TCP6TABLE_OWNER_PID.class */
    @Structure.FieldOrder({"dwNumEntries", ElementTags.TABLE})
    public static class MIB_TCP6TABLE_OWNER_PID extends Structure {
        public int dwNumEntries;
        public MIB_TCP6ROW_OWNER_PID[] table;

        public MIB_TCP6TABLE_OWNER_PID(Pointer buf) {
            super(buf);
            this.table = new MIB_TCP6ROW_OWNER_PID[1];
            read();
        }

        @Override // com.sun.jna.Structure
        public void read() {
            this.dwNumEntries = getPointer().getInt(0L);
            if (this.dwNumEntries > 0) {
                this.table = (MIB_TCP6ROW_OWNER_PID[]) new MIB_TCP6ROW_OWNER_PID().toArray(this.dwNumEntries);
                super.read();
            } else {
                this.table = new MIB_TCP6ROW_OWNER_PID[0];
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_UDPTABLE_OWNER_PID.class */
    @Structure.FieldOrder({"dwNumEntries", ElementTags.TABLE})
    public static class MIB_UDPTABLE_OWNER_PID extends Structure {
        public int dwNumEntries;
        public MIB_UDPROW_OWNER_PID[] table;

        public MIB_UDPTABLE_OWNER_PID(Pointer buf) {
            super(buf);
            this.table = new MIB_UDPROW_OWNER_PID[1];
            read();
        }

        @Override // com.sun.jna.Structure
        public void read() {
            this.dwNumEntries = getPointer().getInt(0L);
            if (this.dwNumEntries > 0) {
                this.table = (MIB_UDPROW_OWNER_PID[]) new MIB_UDPROW_OWNER_PID().toArray(this.dwNumEntries);
                super.read();
            } else {
                this.table = new MIB_UDPROW_OWNER_PID[0];
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/IPHlpAPI$MIB_UDP6TABLE_OWNER_PID.class */
    @Structure.FieldOrder({"dwNumEntries", ElementTags.TABLE})
    public static class MIB_UDP6TABLE_OWNER_PID extends Structure {
        public int dwNumEntries;
        public MIB_UDP6ROW_OWNER_PID[] table;

        public MIB_UDP6TABLE_OWNER_PID(Pointer buf) {
            super(buf);
            this.table = new MIB_UDP6ROW_OWNER_PID[1];
            read();
        }

        @Override // com.sun.jna.Structure
        public void read() {
            this.dwNumEntries = getPointer().getInt(0L);
            if (this.dwNumEntries > 0) {
                this.table = (MIB_UDP6ROW_OWNER_PID[]) new MIB_UDP6ROW_OWNER_PID().toArray(this.dwNumEntries);
                super.read();
            } else {
                this.table = new MIB_UDP6ROW_OWNER_PID[0];
            }
        }
    }
}
