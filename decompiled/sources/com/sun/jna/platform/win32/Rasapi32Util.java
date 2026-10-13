package com.sun.jna.platform.win32;

import com.sun.jna.ptr.IntByReference;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Rasapi32Util.class */
public abstract class Rasapi32Util {
    private static final int RASP_PppIp = 32801;
    private static Object phoneBookMutex = new Object();
    public static final Map CONNECTION_STATE_TEXT = new HashMap();

    static {
        CONNECTION_STATE_TEXT.put(0, "Opening the port...");
        CONNECTION_STATE_TEXT.put(1, "Port has been opened successfully");
        CONNECTION_STATE_TEXT.put(2, "Connecting to the device...");
        CONNECTION_STATE_TEXT.put(3, "The device has connected successfully.");
        CONNECTION_STATE_TEXT.put(4, "All devices in the device chain have successfully connected.");
        CONNECTION_STATE_TEXT.put(5, "Verifying the user name and password...");
        CONNECTION_STATE_TEXT.put(6, "An authentication event has occurred.");
        CONNECTION_STATE_TEXT.put(7, "Requested another validation attempt with a new user.");
        CONNECTION_STATE_TEXT.put(8, "Server has requested a callback number.");
        CONNECTION_STATE_TEXT.put(9, "The client has requested to change the password");
        CONNECTION_STATE_TEXT.put(10, "Registering your computer on the network...");
        CONNECTION_STATE_TEXT.put(11, "The link-speed calculation phase is starting...");
        CONNECTION_STATE_TEXT.put(12, "An authentication request is being acknowledged.");
        CONNECTION_STATE_TEXT.put(13, "Reauthentication (after callback) is starting.");
        CONNECTION_STATE_TEXT.put(14, "The client has successfully completed authentication.");
        CONNECTION_STATE_TEXT.put(15, "The line is about to disconnect for callback.");
        CONNECTION_STATE_TEXT.put(16, "Delaying to give the modem time to reset for callback.");
        CONNECTION_STATE_TEXT.put(17, "Waiting for an incoming call from server.");
        CONNECTION_STATE_TEXT.put(18, "Projection result information is available.");
        CONNECTION_STATE_TEXT.put(19, "User authentication is being initiated or retried.");
        CONNECTION_STATE_TEXT.put(20, "Client has been called back and is about to resume authentication.");
        CONNECTION_STATE_TEXT.put(21, "Logging on to the network...");
        CONNECTION_STATE_TEXT.put(22, "Subentry has been connected");
        CONNECTION_STATE_TEXT.put(23, "Subentry has been disconnected");
        CONNECTION_STATE_TEXT.put(4096, "Terminal state supported by RASPHONE.EXE.");
        CONNECTION_STATE_TEXT.put(4097, "Retry authentication state supported by RASPHONE.EXE.");
        CONNECTION_STATE_TEXT.put(4098, "Callback state supported by RASPHONE.EXE.");
        CONNECTION_STATE_TEXT.put(4099, "Change password state supported by RASPHONE.EXE.");
        CONNECTION_STATE_TEXT.put(Integer.valueOf(WinError.ERROR_DHCP_ADDRESS_CONFLICT), "Displaying authentication UI");
        CONNECTION_STATE_TEXT.put(8192, "Connected to remote server successfully");
        CONNECTION_STATE_TEXT.put(8193, "Disconnected");
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Rasapi32Util$Ras32Exception.class */
    public static class Ras32Exception extends RuntimeException {
        private static final long serialVersionUID = 1;
        private final int code;

        public int getCode() {
            return this.code;
        }

        public Ras32Exception(int code) {
            super(Rasapi32Util.getRasErrorString(code));
            this.code = code;
        }
    }

    public static String getRasErrorString(int code) {
        char[] msg = new char[1024];
        int err = Rasapi32.INSTANCE.RasGetErrorString(code, msg, msg.length);
        if (err != 0) {
            return "Unknown error " + code;
        }
        int len = 0;
        while (len < msg.length && msg[len] != 0) {
            len++;
        }
        return new String(msg, 0, len);
    }

    public static String getRasConnectionStatusText(int connStatus) {
        return !CONNECTION_STATE_TEXT.containsKey(Integer.valueOf(connStatus)) ? Integer.toString(connStatus) : (String) CONNECTION_STATE_TEXT.get(Integer.valueOf(connStatus));
    }

    public static WinNT.HANDLE getRasConnection(String connName) throws Ras32Exception {
        IntByReference lpcb = new IntByReference(0);
        IntByReference lpcConnections = new IntByReference();
        int err = Rasapi32.INSTANCE.RasEnumConnections(null, lpcb, lpcConnections);
        if (err != 0 && err != 603) {
            throw new Ras32Exception(err);
        }
        if (lpcb.getValue() == 0) {
            return null;
        }
        WinRas.RASCONN[] connections = new WinRas.RASCONN[lpcConnections.getValue()];
        for (int i = 0; i < lpcConnections.getValue(); i++) {
            connections[i] = new WinRas.RASCONN();
        }
        int err2 = Rasapi32.INSTANCE.RasEnumConnections(connections, new IntByReference(connections[0].dwSize * lpcConnections.getValue()), lpcConnections);
        if (err2 != 0) {
            throw new Ras32Exception(err2);
        }
        for (int i2 = 0; i2 < lpcConnections.getValue(); i2++) {
            if (new String(connections[i2].szEntryName).equals(connName)) {
                return connections[i2].hrasconn;
            }
        }
        return null;
    }

    public static void hangupRasConnection(String connName) throws Ras32Exception {
        int err;
        WinNT.HANDLE hrasConn = getRasConnection(connName);
        if (hrasConn != null && (err = Rasapi32.INSTANCE.RasHangUp(hrasConn)) != 0) {
            throw new Ras32Exception(err);
        }
    }

    public static void hangupRasConnection(WinNT.HANDLE hrasConn) throws Ras32Exception {
        int err;
        if (hrasConn != null && (err = Rasapi32.INSTANCE.RasHangUp(hrasConn)) != 0) {
            throw new Ras32Exception(err);
        }
    }

    public static WinRas.RASPPPIP getIPProjection(WinNT.HANDLE hrasConn) throws Ras32Exception {
        WinRas.RASPPPIP pppIpProjection = new WinRas.RASPPPIP();
        IntByReference lpcb = new IntByReference(pppIpProjection.size());
        pppIpProjection.write();
        int err = Rasapi32.INSTANCE.RasGetProjectionInfo(hrasConn, RASP_PppIp, pppIpProjection.getPointer(), lpcb);
        if (err != 0) {
            throw new Ras32Exception(err);
        }
        pppIpProjection.read();
        return pppIpProjection;
    }

    public static WinRas.RASENTRY.ByReference getPhoneBookEntry(String entryName) throws Ras32Exception {
        WinRas.RASENTRY.ByReference rasEntry;
        synchronized (phoneBookMutex) {
            rasEntry = new WinRas.RASENTRY.ByReference();
            IntByReference lpdwEntryInfoSize = new IntByReference(rasEntry.size());
            int err = Rasapi32.INSTANCE.RasGetEntryProperties(null, entryName, rasEntry, lpdwEntryInfoSize, null, null);
            if (err != 0) {
                throw new Ras32Exception(err);
            }
        }
        return rasEntry;
    }

    public static void setPhoneBookEntry(String entryName, WinRas.RASENTRY.ByReference rasEntry) throws Ras32Exception {
        synchronized (phoneBookMutex) {
            int err = Rasapi32.INSTANCE.RasSetEntryProperties(null, entryName, rasEntry, rasEntry.size(), null, 0);
            if (err != 0) {
                throw new Ras32Exception(err);
            }
        }
    }

    public static WinRas.RASDIALPARAMS getPhoneBookDialingParams(String entryName) throws Ras32Exception {
        WinRas.RASDIALPARAMS.ByReference rasDialParams;
        synchronized (phoneBookMutex) {
            rasDialParams = new WinRas.RASDIALPARAMS.ByReference();
            System.arraycopy(rasDialParams.szEntryName, 0, entryName.toCharArray(), 0, entryName.length());
            WinDef.BOOLByReference lpfPassword = new WinDef.BOOLByReference();
            int err = Rasapi32.INSTANCE.RasGetEntryDialParams(null, rasDialParams, lpfPassword);
            if (err != 0) {
                throw new Ras32Exception(err);
            }
        }
        return rasDialParams;
    }

    public static WinNT.HANDLE dialEntry(String entryName) throws Ras32Exception {
        WinRas.RASCREDENTIALS.ByReference credentials = new WinRas.RASCREDENTIALS.ByReference();
        synchronized (phoneBookMutex) {
            credentials.dwMask = 7;
            int err = Rasapi32.INSTANCE.RasGetCredentials(null, entryName, credentials);
            if (err != 0) {
                throw new Ras32Exception(err);
            }
        }
        WinRas.RASDIALPARAMS.ByReference rasDialParams = new WinRas.RASDIALPARAMS.ByReference();
        System.arraycopy(entryName.toCharArray(), 0, rasDialParams.szEntryName, 0, entryName.length());
        System.arraycopy(credentials.szUserName, 0, rasDialParams.szUserName, 0, credentials.szUserName.length);
        System.arraycopy(credentials.szPassword, 0, rasDialParams.szPassword, 0, credentials.szPassword.length);
        System.arraycopy(credentials.szDomain, 0, rasDialParams.szDomain, 0, credentials.szDomain.length);
        WinNT.HANDLEByReference hrasConn = new WinNT.HANDLEByReference();
        int err2 = Rasapi32.INSTANCE.RasDial(null, null, rasDialParams, 0, null, hrasConn);
        if (err2 != 0) {
            if (hrasConn.getValue() != null) {
                Rasapi32.INSTANCE.RasHangUp(hrasConn.getValue());
            }
            throw new Ras32Exception(err2);
        }
        return hrasConn.getValue();
    }

    public static WinNT.HANDLE dialEntry(String entryName, WinRas.RasDialFunc2 func2) throws Ras32Exception {
        WinRas.RASCREDENTIALS.ByReference credentials = new WinRas.RASCREDENTIALS.ByReference();
        synchronized (phoneBookMutex) {
            credentials.dwMask = 7;
            int err = Rasapi32.INSTANCE.RasGetCredentials(null, entryName, credentials);
            if (err != 0) {
                throw new Ras32Exception(err);
            }
        }
        WinRas.RASDIALPARAMS.ByReference rasDialParams = new WinRas.RASDIALPARAMS.ByReference();
        System.arraycopy(entryName.toCharArray(), 0, rasDialParams.szEntryName, 0, entryName.length());
        System.arraycopy(credentials.szUserName, 0, rasDialParams.szUserName, 0, credentials.szUserName.length);
        System.arraycopy(credentials.szPassword, 0, rasDialParams.szPassword, 0, credentials.szPassword.length);
        System.arraycopy(credentials.szDomain, 0, rasDialParams.szDomain, 0, credentials.szDomain.length);
        WinNT.HANDLEByReference hrasConn = new WinNT.HANDLEByReference();
        int err2 = Rasapi32.INSTANCE.RasDial(null, null, rasDialParams, 2, func2, hrasConn);
        if (err2 != 0) {
            if (hrasConn.getValue() != null) {
                Rasapi32.INSTANCE.RasHangUp(hrasConn.getValue());
            }
            throw new Ras32Exception(err2);
        }
        return hrasConn.getValue();
    }
}
