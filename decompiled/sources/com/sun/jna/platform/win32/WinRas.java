package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas.class */
public interface WinRas {
    public static final int ERROR_BUFFER_TOO_SMALL = 603;
    public static final int ERROR_CANNOT_FIND_PHONEBOOK_ENTRY = 623;
    public static final int MAX_PATH = 260;
    public static final int UNLEN = 256;
    public static final int PWLEN = 256;
    public static final int DNLEN = 15;
    public static final int RAS_MaxEntryName = 256;
    public static final int RAS_MaxPhoneNumber = 128;
    public static final int RAS_MaxCallbackNumber = 128;
    public static final int RAS_MaxDeviceType = 16;
    public static final int RAS_MaxDeviceName = 128;
    public static final int RAS_MaxDnsSuffix = 256;
    public static final int RAS_MaxAreaCode = 10;
    public static final int RAS_MaxX25Address = 200;
    public static final int RAS_MaxIpAddress = 15;
    public static final int RAS_MaxFacilities = 200;
    public static final int RAS_MaxUserData = 200;
    public static final int RAS_MaxPadType = 32;
    public static final int RASCS_Connected = 8192;
    public static final int RASCS_Disconnected = 8193;
    public static final int RASCM_UserName = 1;
    public static final int RASCM_Password = 2;
    public static final int RASCM_Domain = 4;
    public static final int RASTUNNELENDPOINT_IPv4 = 1;
    public static final int RASTUNNELENDPOINT_IPv6 = 2;
    public static final String RASDT_Modem = "modem";

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RasDialFunc2.class */
    public interface RasDialFunc2 extends StdCallLibrary.StdCallCallback {
        int dialNotification(int i, int i2, WinNT.HANDLE handle, int i3, int i4, int i5, int i6);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASEAPINFO.class */
    @Structure.FieldOrder({"dwSizeofEapInfo", "pbEapInfo"})
    public static class RASEAPINFO extends Structure {
        public int dwSizeofEapInfo;
        public Pointer pbEapInfo;

        public RASEAPINFO() {
        }

        public RASEAPINFO(Pointer memory) {
            super(memory);
            read();
        }

        public RASEAPINFO(byte[] data) {
            this.pbEapInfo = new Memory(data.length);
            this.pbEapInfo.write(0L, data, 0, data.length);
            this.dwSizeofEapInfo = data.length;
            allocateMemory();
        }

        public RASEAPINFO(String s) {
            this(Native.toByteArray(s));
        }

        public byte[] getData() {
            if (this.pbEapInfo == null) {
                return null;
            }
            return this.pbEapInfo.getByteArray(0L, this.dwSizeofEapInfo);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASDEVSPECIFICINFO.class */
    @Structure.FieldOrder({"dwSize", "pbDevSpecificInfo"})
    public static class RASDEVSPECIFICINFO extends Structure {
        public int dwSize;
        public Pointer pbDevSpecificInfo;

        public RASDEVSPECIFICINFO() {
        }

        public RASDEVSPECIFICINFO(Pointer memory) {
            super(memory);
            read();
        }

        public RASDEVSPECIFICINFO(byte[] data) {
            this.pbDevSpecificInfo = new Memory(data.length);
            this.pbDevSpecificInfo.write(0L, data, 0, data.length);
            this.dwSize = data.length;
            allocateMemory();
        }

        public RASDEVSPECIFICINFO(String s) {
            this(Native.toByteArray(s));
        }

        public byte[] getData() {
            if (this.pbDevSpecificInfo == null) {
                return null;
            }
            return this.pbDevSpecificInfo.getByteArray(0L, this.dwSize);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASDIALEXTENSIONS.class */
    @Structure.FieldOrder({"dwSize", "dwfOptions", "hwndParent", "reserved", "reserved1", "RasEapInfo", "fSkipPppAuth", "RasDevSpecificInfo"})
    public static class RASDIALEXTENSIONS extends Structure {
        public int dwSize;
        public int dwfOptions;
        public WinDef.HWND hwndParent;
        public BaseTSD.ULONG_PTR reserved;
        public BaseTSD.ULONG_PTR reserved1;
        public RASEAPINFO RasEapInfo;
        public WinDef.BOOL fSkipPppAuth;
        public RASDEVSPECIFICINFO RasDevSpecificInfo;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASDIALEXTENSIONS$ByReference.class */
        public static class ByReference extends RASDIALEXTENSIONS implements Structure.ByReference {
        }

        public RASDIALEXTENSIONS() {
            this.dwSize = size();
        }

        public RASDIALEXTENSIONS(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASDIALPARAMS.class */
    @Structure.FieldOrder({"dwSize", "szEntryName", "szPhoneNumber", "szCallbackNumber", "szUserName", "szPassword", "szDomain"})
    public static class RASDIALPARAMS extends Structure {
        public int dwSize;
        public char[] szEntryName;
        public char[] szPhoneNumber;
        public char[] szCallbackNumber;
        public char[] szUserName;
        public char[] szPassword;
        public char[] szDomain;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASDIALPARAMS$ByReference.class */
        public static class ByReference extends RASDIALPARAMS implements Structure.ByReference {
        }

        public RASDIALPARAMS() {
            this.szEntryName = new char[257];
            this.szPhoneNumber = new char[129];
            this.szCallbackNumber = new char[129];
            this.szUserName = new char[257];
            this.szPassword = new char[257];
            this.szDomain = new char[16];
            this.dwSize = size();
        }

        public RASDIALPARAMS(Pointer memory) {
            super(memory);
            this.szEntryName = new char[257];
            this.szPhoneNumber = new char[129];
            this.szCallbackNumber = new char[129];
            this.szUserName = new char[257];
            this.szPassword = new char[257];
            this.szDomain = new char[16];
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASCONN.class */
    @Structure.FieldOrder({"dwSize", "hrasconn", "szEntryName", "szDeviceType", "szDeviceName", "szPhonebook", "dwSubEntry", "guidEntry", "dwFlags", "luid", "guidCorrelationId"})
    public static class RASCONN extends Structure {
        public int dwSize;
        public WinNT.HANDLE hrasconn;
        public char[] szEntryName;
        public char[] szDeviceType;
        public char[] szDeviceName;
        public char[] szPhonebook;
        public int dwSubEntry;
        public Guid.GUID guidEntry;
        public int dwFlags;
        public WinNT.LUID luid;
        public Guid.GUID guidCorrelationId;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASCONN$ByReference.class */
        public static class ByReference extends RASCONN implements Structure.ByReference {
        }

        public RASCONN() {
            this.szEntryName = new char[257];
            this.szDeviceType = new char[17];
            this.szDeviceName = new char[129];
            this.szPhonebook = new char[260];
            this.dwSize = size();
        }

        public RASCONN(Pointer memory) {
            super(memory);
            this.szEntryName = new char[257];
            this.szDeviceType = new char[17];
            this.szDeviceName = new char[129];
            this.szPhonebook = new char[260];
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RAS_STATS.class */
    @Structure.FieldOrder({"dwSize", "dwBytesXmited", "dwBytesRcved", "dwFramesXmited", "dwFramesRcved", "dwCrcErr", "dwTimeoutErr", "dwAlignmentErr", "dwHardwareOverrunErr", "dwFramingErr", "dwBufferOverrunErr", "dwCompressionRatioIn", "dwCompressionRatioOut", "dwBps", "dwConnectDuration"})
    public static class RAS_STATS extends Structure {
        public int dwSize;
        public int dwBytesXmited;
        public int dwBytesRcved;
        public int dwFramesXmited;
        public int dwFramesRcved;
        public int dwCrcErr;
        public int dwTimeoutErr;
        public int dwAlignmentErr;
        public int dwHardwareOverrunErr;
        public int dwFramingErr;
        public int dwBufferOverrunErr;
        public int dwCompressionRatioIn;
        public int dwCompressionRatioOut;
        public int dwBps;
        public int dwConnectDuration;

        public RAS_STATS() {
            this.dwSize = size();
        }

        public RAS_STATS(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASIPV4ADDR.class */
    @Structure.FieldOrder({"addr"})
    public static class RASIPV4ADDR extends Structure {
        public byte[] addr;

        public RASIPV4ADDR() {
            this.addr = new byte[8];
        }

        public RASIPV4ADDR(Pointer memory) {
            super(memory);
            this.addr = new byte[8];
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASIPV6ADDR.class */
    @Structure.FieldOrder({"addr"})
    public static class RASIPV6ADDR extends Structure {
        public byte[] addr;

        public RASIPV6ADDR() {
            this.addr = new byte[16];
        }

        public RASIPV6ADDR(Pointer memory) {
            super(memory);
            this.addr = new byte[16];
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASPPPIP.class */
    @Structure.FieldOrder({"dwSize", "dwError", "szIpAddress", "szServerIpAddress", "dwOptions", "dwServerOptions"})
    public static class RASPPPIP extends Structure {
        public int dwSize;
        public int dwError;
        public char[] szIpAddress;
        public char[] szServerIpAddress;
        public int dwOptions;
        public int dwServerOptions;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASPPPIP$ByReference.class */
        public static class ByReference extends RASPPPIP implements Structure.ByReference {
        }

        public RASPPPIP() {
            this.szIpAddress = new char[16];
            this.szServerIpAddress = new char[16];
            this.dwSize = size();
        }

        public RASPPPIP(Pointer memory) {
            super(memory);
            this.szIpAddress = new char[16];
            this.szServerIpAddress = new char[16];
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASTUNNELENDPOINT.class */
    @Structure.FieldOrder({"dwType", "u"})
    public static class RASTUNNELENDPOINT extends Structure {
        public int dwType;
        public UNION u;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASTUNNELENDPOINT$UNION.class */
        public static class UNION extends Union {
            public RASIPV4ADDR ipv4;
            public RASIPV6ADDR ipv6;

            /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASTUNNELENDPOINT$UNION$ByReference.class */
            public static class ByReference extends UNION implements Structure.ByReference {
            }
        }

        public RASTUNNELENDPOINT() {
        }

        public RASTUNNELENDPOINT(Pointer memory) {
            super(memory);
            read();
        }

        @Override // com.sun.jna.Structure
        public void read() {
            super.read();
            switch (this.dwType) {
                case 1:
                    this.u.setType(RASIPV4ADDR.class);
                    break;
                case 2:
                    this.u.setType(RASIPV6ADDR.class);
                    break;
                default:
                    this.u.setType(RASIPV4ADDR.class);
                    break;
            }
            this.u.read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASCONNSTATUS.class */
    @Structure.FieldOrder({"dwSize", "rasconnstate", "dwError", "szDeviceType", "szDeviceName", "szPhoneNumber", "localEndPoint", "remoteEndPoint", "rasconnsubstate"})
    public static class RASCONNSTATUS extends Structure {
        public int dwSize;
        public int rasconnstate;
        public int dwError;
        public char[] szDeviceType;
        public char[] szDeviceName;
        public char[] szPhoneNumber;
        public RASTUNNELENDPOINT localEndPoint;
        public RASTUNNELENDPOINT remoteEndPoint;
        public int rasconnsubstate;

        public RASCONNSTATUS() {
            this.szDeviceType = new char[17];
            this.szDeviceName = new char[129];
            this.szPhoneNumber = new char[129];
            this.dwSize = size();
        }

        public RASCONNSTATUS(Pointer memory) {
            super(memory);
            this.szDeviceType = new char[17];
            this.szDeviceName = new char[129];
            this.szPhoneNumber = new char[129];
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASCREDENTIALS.class */
    @Structure.FieldOrder({"dwSize", "dwMask", "szUserName", "szPassword", "szDomain"})
    public static class RASCREDENTIALS extends Structure {
        public int dwSize;
        public int dwMask;
        public char[] szUserName;
        public char[] szPassword;
        public char[] szDomain;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASCREDENTIALS$ByReference.class */
        public static class ByReference extends RASCREDENTIALS implements Structure.ByReference {
        }

        public RASCREDENTIALS() {
            this.szUserName = new char[257];
            this.szPassword = new char[257];
            this.szDomain = new char[16];
            this.dwSize = size();
        }

        public RASCREDENTIALS(Pointer memory) {
            super(memory);
            this.szUserName = new char[257];
            this.szPassword = new char[257];
            this.szDomain = new char[16];
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASIPADDR.class */
    @Structure.FieldOrder({"addr"})
    public static class RASIPADDR extends Structure {
        public byte[] addr;

        public RASIPADDR() {
            this.addr = new byte[4];
        }

        public RASIPADDR(Pointer memory) {
            super(memory);
            this.addr = new byte[4];
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASENTRY.class */
    @Structure.FieldOrder({"dwSize", "dwfOptions", "dwCountryID", "dwCountryCode", "szAreaCode", "szLocalPhoneNumber", "dwAlternateOffset", "ipaddr", "ipaddrDns", "ipaddrDnsAlt", "ipaddrWins", "ipaddrWinsAlt", "dwFrameSize", "dwfNetProtocols", "dwFramingProtocol", "szScript", "szAutodialDll", "szAutodialFunc", "szDeviceType", "szDeviceName", "szX25PadType", "szX25Address", "szX25Facilities", "szX25UserData", "dwChannels", "dwReserved1", "dwReserved2", "dwSubEntries", "dwDialMode", "dwDialExtraPercent", "dwDialExtraSampleSeconds", "dwHangUpExtraPercent", "dwHangUpExtraSampleSeconds", "dwIdleDisconnectSeconds", "dwType", "dwEncryptionType", "dwCustomAuthKey", "guidId", "szCustomDialDll", "dwVpnStrategy", "dwfOptions2", "dwfOptions3", "szDnsSuffix", "dwTcpWindowSize", "szPrerequisitePbk", "szPrerequisiteEntry", "dwRedialCount", "dwRedialPause", "ipv6addrDns", "ipv6addrDnsAlt", "dwIPv4InterfaceMetric", "dwIPv6InterfaceMetric", "ipv6addr", "dwIPv6PrefixLength", "dwNetworkOutageTime"})
    public static class RASENTRY extends Structure {
        public int dwSize;
        public int dwfOptions;
        public int dwCountryID;
        public int dwCountryCode;
        public char[] szAreaCode;
        public char[] szLocalPhoneNumber;
        public int dwAlternateOffset;
        public RASIPADDR ipaddr;
        public RASIPADDR ipaddrDns;
        public RASIPADDR ipaddrDnsAlt;
        public RASIPADDR ipaddrWins;
        public RASIPADDR ipaddrWinsAlt;
        public int dwFrameSize;
        public int dwfNetProtocols;
        public int dwFramingProtocol;
        public char[] szScript;
        public char[] szAutodialDll;
        public char[] szAutodialFunc;
        public char[] szDeviceType;
        public char[] szDeviceName;
        public char[] szX25PadType;
        public char[] szX25Address;
        public char[] szX25Facilities;
        public char[] szX25UserData;
        public int dwChannels;
        public int dwReserved1;
        public int dwReserved2;
        public int dwSubEntries;
        public int dwDialMode;
        public int dwDialExtraPercent;
        public int dwDialExtraSampleSeconds;
        public int dwHangUpExtraPercent;
        public int dwHangUpExtraSampleSeconds;
        public int dwIdleDisconnectSeconds;
        public int dwType;
        public int dwEncryptionType;
        public int dwCustomAuthKey;
        public Guid.GUID guidId;
        public char[] szCustomDialDll;
        public int dwVpnStrategy;
        public int dwfOptions2;
        public int dwfOptions3;
        public char[] szDnsSuffix;
        public int dwTcpWindowSize;
        public char[] szPrerequisitePbk;
        public char[] szPrerequisiteEntry;
        public int dwRedialCount;
        public int dwRedialPause;
        public RASIPV6ADDR ipv6addrDns;
        public RASIPV6ADDR ipv6addrDnsAlt;
        public int dwIPv4InterfaceMetric;
        public int dwIPv6InterfaceMetric;
        public RASIPV6ADDR ipv6addr;
        public int dwIPv6PrefixLength;
        public int dwNetworkOutageTime;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinRas$RASENTRY$ByReference.class */
        public static class ByReference extends RASENTRY implements Structure.ByReference {
        }

        public RASENTRY() {
            this.szAreaCode = new char[11];
            this.szLocalPhoneNumber = new char[129];
            this.szScript = new char[260];
            this.szAutodialDll = new char[260];
            this.szAutodialFunc = new char[260];
            this.szDeviceType = new char[17];
            this.szDeviceName = new char[129];
            this.szX25PadType = new char[33];
            this.szX25Address = new char[201];
            this.szX25Facilities = new char[201];
            this.szX25UserData = new char[201];
            this.szCustomDialDll = new char[260];
            this.szDnsSuffix = new char[256];
            this.szPrerequisitePbk = new char[260];
            this.szPrerequisiteEntry = new char[257];
            this.dwSize = size();
        }

        public RASENTRY(Pointer memory) {
            super(memory);
            this.szAreaCode = new char[11];
            this.szLocalPhoneNumber = new char[129];
            this.szScript = new char[260];
            this.szAutodialDll = new char[260];
            this.szAutodialFunc = new char[260];
            this.szDeviceType = new char[17];
            this.szDeviceName = new char[129];
            this.szX25PadType = new char[33];
            this.szX25Address = new char[201];
            this.szX25Facilities = new char[201];
            this.szX25UserData = new char[201];
            this.szCustomDialDll = new char[260];
            this.szDnsSuffix = new char[256];
            this.szPrerequisitePbk = new char[260];
            this.szPrerequisiteEntry = new char[257];
            read();
        }
    }
}
