package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.ini4j.Registry;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Advapi32Util.class */
public abstract class Advapi32Util {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Advapi32Util$Account.class */
    public static class Account {
        public String name;
        public String domain;
        public byte[] sid;
        public String sidString;
        public int accountType;
        public String fqn;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Advapi32Util$EventLogType.class */
    public enum EventLogType {
        Error,
        Warning,
        Informational,
        AuditSuccess,
        AuditFailure
    }

    public static String getUserName() {
        char[] buffer = new char[128];
        IntByReference len = new IntByReference(buffer.length);
        boolean result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
        if (!result) {
            switch (Kernel32.INSTANCE.GetLastError()) {
                case 122:
                    buffer = new char[len.getValue()];
                    result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
                    break;
                default:
                    throw new Win32Exception(Native.getLastError());
            }
        }
        if (!result) {
            throw new Win32Exception(Native.getLastError());
        }
        return Native.toString(buffer);
    }

    public static Account getAccountByName(String accountName) {
        return getAccountByName(null, accountName);
    }

    public static Account getAccountByName(String systemName, String accountName) {
        IntByReference pSid = new IntByReference(0);
        IntByReference cchDomainName = new IntByReference(0);
        PointerByReference peUse = new PointerByReference();
        if (Advapi32.INSTANCE.LookupAccountName(systemName, accountName, null, pSid, null, cchDomainName, peUse)) {
            throw new RuntimeException("LookupAccountNameW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
        }
        int rc = Kernel32.INSTANCE.GetLastError();
        if (pSid.getValue() == 0 || rc != 122) {
            throw new Win32Exception(rc);
        }
        Memory sidMemory = new Memory(pSid.getValue());
        WinNT.PSID result = new WinNT.PSID(sidMemory);
        char[] referencedDomainName = new char[cchDomainName.getValue() + 1];
        if (!Advapi32.INSTANCE.LookupAccountName(systemName, accountName, result, pSid, referencedDomainName, cchDomainName, peUse)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        Account account = new Account();
        account.accountType = peUse.getPointer().getInt(0L);
        String[] accountNamePartsBs = accountName.split("\\\\", 2);
        String[] accountNamePartsAt = accountName.split(Registry.Key.DEFAULT_NAME, 2);
        if (accountNamePartsBs.length == 2) {
            account.name = accountNamePartsBs[1];
        } else if (accountNamePartsAt.length == 2) {
            account.name = accountNamePartsAt[0];
        } else {
            account.name = accountName;
        }
        if (cchDomainName.getValue() > 0) {
            account.domain = Native.toString(referencedDomainName);
            account.fqn = account.domain + "\\" + account.name;
        } else {
            account.fqn = account.name;
        }
        account.sid = result.getBytes();
        account.sidString = convertSidToStringSid(new WinNT.PSID(account.sid));
        return account;
    }

    public static Account getAccountBySid(WinNT.PSID sid) {
        return getAccountBySid((String) null, sid);
    }

    public static Account getAccountBySid(String systemName, WinNT.PSID sid) {
        IntByReference cchName = new IntByReference(257);
        IntByReference cchDomainName = new IntByReference(256);
        PointerByReference peUse = new PointerByReference();
        char[] domainName = new char[cchDomainName.getValue()];
        char[] name = new char[cchName.getValue()];
        int rc = 0;
        if (!Advapi32.INSTANCE.LookupAccountSid(systemName, sid, name, cchName, domainName, cchDomainName, peUse)) {
            rc = Kernel32.INSTANCE.GetLastError();
            if (rc != 1332) {
                throw new Win32Exception(rc);
            }
        }
        Account account = new Account();
        if (rc == 1332) {
            account.accountType = 8;
            account.name = "NONE_MAPPED";
        } else {
            account.accountType = peUse.getPointer().getInt(0L);
            account.name = Native.toString(name);
        }
        account.domain = Native.toString(domainName);
        if (account.domain.isEmpty()) {
            account.fqn = account.name;
        } else {
            account.fqn = account.domain + "\\" + account.name;
        }
        account.sid = sid.getBytes();
        account.sidString = convertSidToStringSid(sid);
        return account;
    }

    public static String convertSidToStringSid(WinNT.PSID sid) {
        PointerByReference stringSid = new PointerByReference();
        if (!Advapi32.INSTANCE.ConvertSidToStringSid(sid, stringSid)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        Pointer ptr = stringSid.getValue();
        try {
            return ptr.getWideString(0L);
        } finally {
            Kernel32Util.freeLocalMemory(ptr);
        }
    }

    public static byte[] convertStringSidToSid(String sidString) {
        WinNT.PSIDByReference pSID = new WinNT.PSIDByReference();
        if (!Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        WinNT.PSID value = pSID.getValue();
        try {
            return value.getBytes();
        } finally {
            Kernel32Util.freeLocalMemory(value.getPointer());
        }
    }

    public static boolean isWellKnownSid(String sidString, int wellKnownSidType) {
        WinNT.PSIDByReference pSID = new WinNT.PSIDByReference();
        if (!Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        WinNT.PSID value = pSID.getValue();
        try {
            return Advapi32.INSTANCE.IsWellKnownSid(value, wellKnownSidType);
        } finally {
            Kernel32Util.freeLocalMemory(value.getPointer());
        }
    }

    public static boolean isWellKnownSid(byte[] sidBytes, int wellKnownSidType) {
        WinNT.PSID pSID = new WinNT.PSID(sidBytes);
        return Advapi32.INSTANCE.IsWellKnownSid(pSID, wellKnownSidType);
    }

    public static int alignOnDWORD(int cbAcl) {
        return (cbAcl + 3) & (-4);
    }

    public static int getAceSize(int sidLength) {
        return (Native.getNativeSize(WinNT.ACCESS_ALLOWED_ACE.class, null) + sidLength) - 4;
    }

    public static Account getAccountBySid(String sidString) {
        return getAccountBySid((String) null, sidString);
    }

    public static Account getAccountBySid(String systemName, String sidString) {
        return getAccountBySid(systemName, new WinNT.PSID(convertStringSidToSid(sidString)));
    }

    public static Account[] getTokenGroups(WinNT.HANDLE hToken) {
        Account group;
        IntByReference tokenInformationLength = new IntByReference();
        if (Advapi32.INSTANCE.GetTokenInformation(hToken, 2, null, 0, tokenInformationLength)) {
            throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
        }
        int rc = Kernel32.INSTANCE.GetLastError();
        if (rc != 122) {
            throw new Win32Exception(rc);
        }
        WinNT.TOKEN_GROUPS groups = new WinNT.TOKEN_GROUPS(tokenInformationLength.getValue());
        if (!Advapi32.INSTANCE.GetTokenInformation(hToken, 2, groups, tokenInformationLength.getValue(), tokenInformationLength)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        ArrayList<Account> userGroups = new ArrayList<>();
        for (WinNT.SID_AND_ATTRIBUTES sidAndAttribute : groups.getGroups()) {
            try {
                group = getAccountBySid(sidAndAttribute.Sid);
            } catch (Exception e) {
                group = new Account();
                group.sid = sidAndAttribute.Sid.getBytes();
                group.sidString = convertSidToStringSid(sidAndAttribute.Sid);
                group.name = group.sidString;
                group.fqn = group.sidString;
                group.accountType = 2;
            }
            userGroups.add(group);
        }
        return (Account[]) userGroups.toArray(new Account[0]);
    }

    public static Account getTokenPrimaryGroup(WinNT.HANDLE hToken) {
        Account group;
        IntByReference tokenInformationLength = new IntByReference();
        if (Advapi32.INSTANCE.GetTokenInformation(hToken, 5, null, 0, tokenInformationLength)) {
            throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
        }
        int rc = Kernel32.INSTANCE.GetLastError();
        if (rc != 122) {
            throw new Win32Exception(rc);
        }
        WinNT.TOKEN_PRIMARY_GROUP primaryGroup = new WinNT.TOKEN_PRIMARY_GROUP(tokenInformationLength.getValue());
        if (!Advapi32.INSTANCE.GetTokenInformation(hToken, 5, primaryGroup, tokenInformationLength.getValue(), tokenInformationLength)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        try {
            group = getAccountBySid(primaryGroup.PrimaryGroup);
        } catch (Exception e) {
            group = new Account();
            group.sid = primaryGroup.PrimaryGroup.getBytes();
            group.sidString = convertSidToStringSid(primaryGroup.PrimaryGroup);
            group.name = group.sidString;
            group.fqn = group.sidString;
            group.accountType = 2;
        }
        return group;
    }

    public static Account getTokenAccount(WinNT.HANDLE hToken) {
        IntByReference tokenInformationLength = new IntByReference();
        if (Advapi32.INSTANCE.GetTokenInformation(hToken, 1, null, 0, tokenInformationLength)) {
            throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
        }
        int rc = Kernel32.INSTANCE.GetLastError();
        if (rc != 122) {
            throw new Win32Exception(rc);
        }
        WinNT.TOKEN_USER user = new WinNT.TOKEN_USER(tokenInformationLength.getValue());
        if (!Advapi32.INSTANCE.GetTokenInformation(hToken, 1, user, tokenInformationLength.getValue(), tokenInformationLength)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return getAccountBySid(user.User.Sid);
    }

    public static Account[] getCurrentUserGroups() {
        WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
        Win32Exception err = null;
        try {
            try {
                WinNT.HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
                if (!Advapi32.INSTANCE.OpenThreadToken(threadHandle, 10, true, phToken)) {
                    int rc = Kernel32.INSTANCE.GetLastError();
                    if (rc != 1008) {
                        throw new Win32Exception(rc);
                    }
                    WinNT.HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
                    if (!Advapi32.INSTANCE.OpenProcessToken(processHandle, 10, phToken)) {
                        throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                    }
                }
                Account[] tokenGroups = getTokenGroups(phToken.getValue());
                WinNT.HANDLE hToken = phToken.getValue();
                if (!WinBase.INVALID_HANDLE_VALUE.equals(hToken)) {
                    try {
                        Kernel32Util.closeHandle(hToken);
                    } catch (Win32Exception e) {
                        if (0 == 0) {
                            err = e;
                        } else {
                            err.addSuppressedReflected(e);
                        }
                    }
                }
                if (err == null) {
                    return tokenGroups;
                }
                throw err;
            } catch (Throwable th) {
                WinNT.HANDLE hToken2 = phToken.getValue();
                if (!WinBase.INVALID_HANDLE_VALUE.equals(hToken2)) {
                    try {
                        Kernel32Util.closeHandle(hToken2);
                    } catch (Win32Exception e2) {
                        if (0 == 0) {
                            err = e2;
                        } else {
                            err.addSuppressedReflected(e2);
                        }
                    }
                }
                if (err != null) {
                    throw err;
                }
                throw th;
            }
        } catch (Win32Exception e3) {
            throw e3;
        }
    }

    public static boolean registryKeyExists(WinReg.HKEY root, String key) {
        return registryKeyExists(root, key, 0);
    }

    public static boolean registryKeyExists(WinReg.HKEY root, String key, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
        switch (rc) {
            case 0:
                Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
                return true;
            case 2:
                return false;
            default:
                throw new Win32Exception(rc);
        }
    }

    public static boolean registryValueExists(WinReg.HKEY root, String key, String value) {
        return registryValueExists(root, key, value, 0);
    }

    public static boolean registryValueExists(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
        int rc;
        int rc2;
        int rc3;
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc4 = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
        switch (rc4) {
            case 0:
                try {
                    IntByReference lpcbData = new IntByReference();
                    IntByReference lpType = new IntByReference();
                    int rc5 = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (Pointer) null, lpcbData);
                    switch (rc5) {
                        case 0:
                        case 122:
                        case WinError.ERROR_MORE_DATA /* 234 */:
                            if (WinBase.INVALID_HANDLE_VALUE.equals(phkKey.getValue()) || (rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue())) == 0) {
                                return true;
                            }
                            throw new Win32Exception(rc3);
                        case 2:
                            if (WinBase.INVALID_HANDLE_VALUE.equals(phkKey.getValue()) || (rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue())) == 0) {
                                return false;
                            }
                            throw new Win32Exception(rc2);
                        default:
                            throw new Win32Exception(rc5);
                    }
                } catch (Throwable th) {
                    if (!WinBase.INVALID_HANDLE_VALUE.equals(phkKey.getValue()) && (rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue())) != 0) {
                        throw new Win32Exception(rc);
                    }
                    throw th;
                }
            case 2:
                return false;
            default:
                throw new Win32Exception(rc4);
        }
    }

    public static String registryGetStringValue(WinReg.HKEY root, String key, String value) {
        return registryGetStringValue(root, key, value, 0);
    }

    public static String registryGetStringValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            String strRegistryGetStringValue = registryGetStringValue(phkKey.getValue(), value);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 == 0) {
                return strRegistryGetStringValue;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static String registryGetStringValue(WinReg.HKEY hKey, String value) {
        IntByReference lpcbData = new IntByReference();
        IntByReference lpType = new IntByReference();
        int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer) null, lpcbData);
        if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
        }
        if (lpType.getValue() != 1 && lpType.getValue() != 2) {
            throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ or REG_EXPAND_SZ");
        }
        if (lpcbData.getValue() == 0) {
            return "";
        }
        Memory mem = new Memory(lpcbData.getValue() + Native.WCHAR_SIZE);
        mem.clear();
        int rc2 = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, mem, lpcbData);
        if (rc2 != 0 && rc2 != 122) {
            throw new Win32Exception(rc2);
        }
        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
            return mem.getWideString(0L);
        }
        return mem.getString(0L);
    }

    public static String registryGetExpandableStringValue(WinReg.HKEY root, String key, String value) {
        return registryGetExpandableStringValue(root, key, value, 0);
    }

    public static String registryGetExpandableStringValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            String strRegistryGetExpandableStringValue = registryGetExpandableStringValue(phkKey.getValue(), value);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 == 0) {
                return strRegistryGetExpandableStringValue;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static String registryGetExpandableStringValue(WinReg.HKEY hKey, String value) {
        IntByReference lpcbData = new IntByReference();
        IntByReference lpType = new IntByReference();
        int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[]) null, lpcbData);
        if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
        }
        if (lpType.getValue() != 2) {
            throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ");
        }
        if (lpcbData.getValue() == 0) {
            return "";
        }
        Memory mem = new Memory(lpcbData.getValue() + Native.WCHAR_SIZE);
        mem.clear();
        int rc2 = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, mem, lpcbData);
        if (rc2 != 0 && rc2 != 122) {
            throw new Win32Exception(rc2);
        }
        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
            return mem.getWideString(0L);
        }
        return mem.getString(0L);
    }

    public static String[] registryGetStringArray(WinReg.HKEY root, String key, String value) {
        return registryGetStringArray(root, key, value, 0);
    }

    public static String[] registryGetStringArray(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            String[] strArrRegistryGetStringArray = registryGetStringArray(phkKey.getValue(), value);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 == 0) {
                return strArrRegistryGetStringArray;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static String[] registryGetStringArray(WinReg.HKEY hKey, String value) {
        IntByReference lpcbData = new IntByReference();
        IntByReference lpType = new IntByReference();
        int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[]) null, lpcbData);
        if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
        }
        if (lpType.getValue() != 7) {
            throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ");
        }
        Memory data = new Memory(lpcbData.getValue() + (2 * Native.WCHAR_SIZE));
        data.clear();
        int rc2 = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, data, lpcbData);
        if (rc2 != 0 && rc2 != 122) {
            throw new Win32Exception(rc2);
        }
        return regMultiSzBufferToStringArray(data);
    }

    static String[] regMultiSzBufferToStringArray(Memory data) {
        String s;
        ArrayList<String> result = new ArrayList<>();
        int offset = 0;
        while (offset < data.size()) {
            if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                s = data.getWideString(offset);
                offset = offset + (s.length() * Native.WCHAR_SIZE) + Native.WCHAR_SIZE;
            } else {
                s = data.getString(offset);
                offset = offset + s.length() + 1;
            }
            if (s.length() == 0) {
                break;
            }
            result.add(s);
        }
        return (String[]) result.toArray(new String[0]);
    }

    public static byte[] registryGetBinaryValue(WinReg.HKEY root, String key, String value) {
        return registryGetBinaryValue(root, key, value, 0);
    }

    public static byte[] registryGetBinaryValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            byte[] bArrRegistryGetBinaryValue = registryGetBinaryValue(phkKey.getValue(), value);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 == 0) {
                return bArrRegistryGetBinaryValue;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static byte[] registryGetBinaryValue(WinReg.HKEY hKey, String value) {
        IntByReference lpcbData = new IntByReference();
        IntByReference lpType = new IntByReference();
        int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer) null, lpcbData);
        if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
        }
        if (lpType.getValue() != 3) {
            throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_BINARY");
        }
        byte[] data = new byte[lpcbData.getValue()];
        int rc2 = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, data, lpcbData);
        if (rc2 != 0 && rc2 != 122) {
            throw new Win32Exception(rc2);
        }
        return data;
    }

    public static int registryGetIntValue(WinReg.HKEY root, String key, String value) {
        return registryGetIntValue(root, key, value, 0);
    }

    public static int registryGetIntValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            int iRegistryGetIntValue = registryGetIntValue(phkKey.getValue(), value);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 == 0) {
                return iRegistryGetIntValue;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static int registryGetIntValue(WinReg.HKEY hKey, String value) {
        IntByReference lpcbData = new IntByReference();
        IntByReference lpType = new IntByReference();
        int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[]) null, lpcbData);
        if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
        }
        if (lpType.getValue() != 4) {
            throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_DWORD");
        }
        IntByReference data = new IntByReference();
        int rc2 = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, data, lpcbData);
        if (rc2 != 0 && rc2 != 122) {
            throw new Win32Exception(rc2);
        }
        return data.getValue();
    }

    public static long registryGetLongValue(WinReg.HKEY root, String key, String value) {
        return registryGetLongValue(root, key, value, 0);
    }

    public static long registryGetLongValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            long jRegistryGetLongValue = registryGetLongValue(phkKey.getValue(), value);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 == 0) {
                return jRegistryGetLongValue;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static long registryGetLongValue(WinReg.HKEY hKey, String value) {
        IntByReference lpcbData = new IntByReference();
        IntByReference lpType = new IntByReference();
        int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[]) null, lpcbData);
        if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
        }
        if (lpType.getValue() != 11) {
            throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_QWORD");
        }
        LongByReference data = new LongByReference();
        int rc2 = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, data, lpcbData);
        if (rc2 != 0 && rc2 != 122) {
            throw new Win32Exception(rc2);
        }
        return data.getValue();
    }

    public static Object registryGetValue(WinReg.HKEY hkKey, String subKey, String lpValueName) {
        Object result = null;
        IntByReference lpType = new IntByReference();
        IntByReference lpcbData = new IntByReference();
        int rc = Advapi32.INSTANCE.RegGetValue(hkKey, subKey, lpValueName, 65535, lpType, (Pointer) null, lpcbData);
        if (lpType.getValue() == 0) {
            return null;
        }
        if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
        }
        Memory byteData = new Memory(lpcbData.getValue() + Native.WCHAR_SIZE);
        byteData.clear();
        int rc2 = Advapi32.INSTANCE.RegGetValue(hkKey, subKey, lpValueName, 65535, lpType, byteData, lpcbData);
        if (rc2 != 0) {
            throw new Win32Exception(rc2);
        }
        if (lpType.getValue() == 4) {
            result = Integer.valueOf(byteData.getInt(0L));
        } else if (lpType.getValue() == 11) {
            result = Long.valueOf(byteData.getLong(0L));
        } else if (lpType.getValue() == 3) {
            result = byteData.getByteArray(0L, lpcbData.getValue());
        } else if (lpType.getValue() == 1 || lpType.getValue() == 2) {
            if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                result = byteData.getWideString(0L);
            } else {
                result = byteData.getString(0L);
            }
        }
        return result;
    }

    public static boolean registryCreateKey(WinReg.HKEY hKey, String keyName) {
        return registryCreateKey(hKey, keyName, 0);
    }

    public static boolean registryCreateKey(WinReg.HKEY hKey, String keyName, int samDesiredExtra) {
        WinReg.HKEYByReference phkResult = new WinReg.HKEYByReference();
        IntByReference lpdwDisposition = new IntByReference();
        int rc = Advapi32.INSTANCE.RegCreateKeyEx(hKey, keyName, 0, null, 0, 131097 | samDesiredExtra, null, phkResult, lpdwDisposition);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        int rc2 = Advapi32.INSTANCE.RegCloseKey(phkResult.getValue());
        if (rc2 != 0) {
            throw new Win32Exception(rc2);
        }
        return 1 == lpdwDisposition.getValue();
    }

    public static boolean registryCreateKey(WinReg.HKEY root, String parentPath, String keyName) {
        return registryCreateKey(root, parentPath, keyName, 0);
    }

    public static boolean registryCreateKey(WinReg.HKEY root, String parentPath, String keyName, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, parentPath, 0, 4 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            boolean zRegistryCreateKey = registryCreateKey(phkKey.getValue(), keyName);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 == 0) {
                return zRegistryCreateKey;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static void registrySetIntValue(WinReg.HKEY hKey, String name, int value) {
        byte[] data = {(byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255)};
        int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 4, data, 4);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
    }

    public static void registrySetIntValue(WinReg.HKEY root, String keyPath, String name, int value) {
        registrySetIntValue(root, keyPath, name, value, 0);
    }

    public static void registrySetIntValue(WinReg.HKEY root, String keyPath, String name, int value, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            registrySetIntValue(phkKey.getValue(), name, value);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static void registrySetLongValue(WinReg.HKEY hKey, String name, long value) {
        byte[] data = {(byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255), (byte) ((value >> 32) & 255), (byte) ((value >> 40) & 255), (byte) ((value >> 48) & 255), (byte) ((value >> 56) & 255)};
        int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 11, data, 8);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
    }

    public static void registrySetLongValue(WinReg.HKEY root, String keyPath, String name, long value) {
        registrySetLongValue(root, keyPath, name, value, 0);
    }

    public static void registrySetLongValue(WinReg.HKEY root, String keyPath, String name, long value, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            registrySetLongValue(phkKey.getValue(), name, value);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static void registrySetStringValue(WinReg.HKEY hKey, String name, String value) {
        Memory data;
        if (value == null) {
            value = "";
        }
        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
            data = new Memory((value.length() + 1) * Native.WCHAR_SIZE);
            data.setWideString(0L, value);
        } else {
            data = new Memory(value.length() + 1);
            data.setString(0L, value);
        }
        int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 1, data, (int) data.size());
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
    }

    public static void registrySetStringValue(WinReg.HKEY root, String keyPath, String name, String value) {
        registrySetStringValue(root, keyPath, name, value, 0);
    }

    public static void registrySetStringValue(WinReg.HKEY root, String keyPath, String name, String value, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            registrySetStringValue(phkKey.getValue(), name, value);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static void registrySetExpandableStringValue(WinReg.HKEY hKey, String name, String value) {
        Memory data;
        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
            data = new Memory((value.length() + 1) * Native.WCHAR_SIZE);
            data.setWideString(0L, value);
        } else {
            data = new Memory(value.length() + 1);
            data.setString(0L, value);
        }
        int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 2, data, (int) data.size());
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
    }

    public static void registrySetExpandableStringValue(WinReg.HKEY root, String keyPath, String name, String value) {
        registrySetExpandableStringValue(root, keyPath, name, value, 0);
    }

    public static void registrySetExpandableStringValue(WinReg.HKEY root, String keyPath, String name, String value, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            registrySetExpandableStringValue(phkKey.getValue(), name, value);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static void registrySetStringArray(WinReg.HKEY hKey, String name, String[] arr) {
        int charwidth = W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? Native.WCHAR_SIZE : 1;
        int size = 0;
        for (String str : arr) {
            size = size + (str.length() * charwidth) + charwidth;
        }
        int size2 = size + charwidth;
        int offset = 0;
        Memory data = new Memory(size2);
        data.clear();
        for (String s : arr) {
            if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                data.setWideString(offset, s);
            } else {
                data.setString(offset, s);
            }
            offset = offset + (s.length() * charwidth) + charwidth;
        }
        int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 7, data, size2);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
    }

    public static void registrySetStringArray(WinReg.HKEY root, String keyPath, String name, String[] arr) {
        registrySetStringArray(root, keyPath, name, arr, 0);
    }

    public static void registrySetStringArray(WinReg.HKEY root, String keyPath, String name, String[] arr, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            registrySetStringArray(phkKey.getValue(), name, arr);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static void registrySetBinaryValue(WinReg.HKEY hKey, String name, byte[] data) {
        int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 3, data, data.length);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
    }

    public static void registrySetBinaryValue(WinReg.HKEY root, String keyPath, String name, byte[] data) {
        registrySetBinaryValue(root, keyPath, name, data, 0);
    }

    public static void registrySetBinaryValue(WinReg.HKEY root, String keyPath, String name, byte[] data, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            registrySetBinaryValue(phkKey.getValue(), name, data);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static void registryDeleteKey(WinReg.HKEY hKey, String keyName) {
        int rc = Advapi32.INSTANCE.RegDeleteKey(hKey, keyName);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
    }

    public static void registryDeleteKey(WinReg.HKEY root, String keyPath, String keyName) {
        registryDeleteKey(root, keyPath, keyName, 0);
    }

    public static void registryDeleteKey(WinReg.HKEY root, String keyPath, String keyName, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            registryDeleteKey(phkKey.getValue(), keyName);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static void registryDeleteValue(WinReg.HKEY hKey, String valueName) {
        int rc = Advapi32.INSTANCE.RegDeleteValue(hKey, valueName);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
    }

    public static void registryDeleteValue(WinReg.HKEY root, String keyPath, String valueName) {
        registryDeleteValue(root, keyPath, valueName, 0);
    }

    public static void registryDeleteValue(WinReg.HKEY root, String keyPath, String valueName, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            registryDeleteValue(phkKey.getValue(), valueName);
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static String[] registryGetKeys(WinReg.HKEY hKey) {
        IntByReference lpcSubKeys = new IntByReference();
        IntByReference lpcMaxSubKeyLen = new IntByReference();
        int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, null, null, null, lpcSubKeys, lpcMaxSubKeyLen, null, null, null, null, null, null);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        ArrayList<String> keys = new ArrayList<>(lpcSubKeys.getValue());
        char[] name = new char[lpcMaxSubKeyLen.getValue() + 1];
        for (int i = 0; i < lpcSubKeys.getValue(); i++) {
            IntByReference lpcchValueName = new IntByReference(lpcMaxSubKeyLen.getValue() + 1);
            int rc2 = Advapi32.INSTANCE.RegEnumKeyEx(hKey, i, name, lpcchValueName, null, null, null, null);
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
            keys.add(Native.toString(name));
        }
        return (String[]) keys.toArray(new String[0]);
    }

    public static String[] registryGetKeys(WinReg.HKEY root, String keyPath) {
        return registryGetKeys(root, keyPath, 0);
    }

    public static String[] registryGetKeys(WinReg.HKEY root, String keyPath, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131097 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            String[] strArrRegistryGetKeys = registryGetKeys(phkKey.getValue());
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 == 0) {
                return strArrRegistryGetKeys;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static WinReg.HKEYByReference registryGetKey(WinReg.HKEY root, String keyPath, int samDesired) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, samDesired, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        return phkKey;
    }

    public static WinReg.HKEYByReference registryLoadAppKey(String fileName, int samDesired, int dwOptions) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegLoadAppKey(fileName, phkKey, samDesired, dwOptions, 0);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        return phkKey;
    }

    public static void registryCloseKey(WinReg.HKEY hKey) {
        int rc = Advapi32.INSTANCE.RegCloseKey(hKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
    }

    public static TreeMap<String, Object> registryGetValues(WinReg.HKEY hKey) {
        String s;
        IntByReference lpcValues = new IntByReference();
        IntByReference lpcMaxValueNameLen = new IntByReference();
        IntByReference lpcMaxValueLen = new IntByReference();
        int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, null, null, null, null, null, null, lpcValues, lpcMaxValueNameLen, lpcMaxValueLen, null, null);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        TreeMap<String, Object> keyValues = new TreeMap<>();
        char[] name = new char[lpcMaxValueNameLen.getValue() + 1];
        Memory byteData = new Memory(lpcMaxValueLen.getValue() + (2 * Native.WCHAR_SIZE));
        for (int i = 0; i < lpcValues.getValue(); i++) {
            byteData.clear();
            IntByReference lpcchValueName = new IntByReference(lpcMaxValueNameLen.getValue() + 1);
            IntByReference lpcbData = new IntByReference(lpcMaxValueLen.getValue());
            IntByReference lpType = new IntByReference();
            int rc2 = Advapi32.INSTANCE.RegEnumValue(hKey, i, name, lpcchValueName, (IntByReference) null, lpType, byteData, lpcbData);
            if (rc2 != 0) {
                throw new Win32Exception(rc2);
            }
            String nameString = Native.toString(name);
            if (lpcbData.getValue() == 0) {
                switch (lpType.getValue()) {
                    case 0:
                        keyValues.put(nameString, null);
                        break;
                    case 1:
                    case 2:
                        keyValues.put(nameString, new char[0]);
                        break;
                    case 3:
                        keyValues.put(nameString, new byte[0]);
                        break;
                    case 4:
                    case 5:
                    case 6:
                    default:
                        throw new RuntimeException("Unsupported empty type: " + lpType.getValue());
                    case 7:
                        keyValues.put(nameString, new String[0]);
                        break;
                }
            } else {
                switch (lpType.getValue()) {
                    case 1:
                    case 2:
                        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                            keyValues.put(nameString, byteData.getWideString(0L));
                        } else {
                            keyValues.put(nameString, byteData.getString(0L));
                        }
                        break;
                    case 3:
                        keyValues.put(nameString, byteData.getByteArray(0L, lpcbData.getValue()));
                        break;
                    case 4:
                        keyValues.put(nameString, Integer.valueOf(byteData.getInt(0L)));
                        break;
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    default:
                        throw new RuntimeException("Unsupported type: " + lpType.getValue());
                    case 7:
                        ArrayList<String> result = new ArrayList<>();
                        int offset = 0;
                        while (offset < byteData.size()) {
                            if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                                s = byteData.getWideString(offset);
                                offset = offset + (s.length() * Native.WCHAR_SIZE) + Native.WCHAR_SIZE;
                            } else {
                                s = byteData.getString(offset);
                                offset = offset + s.length() + 1;
                            }
                            if (s.length() != 0) {
                                result.add(s);
                            } else {
                                keyValues.put(nameString, result.toArray(new String[0]));
                            }
                            break;
                        }
                        keyValues.put(nameString, result.toArray(new String[0]));
                        break;
                    case 11:
                        keyValues.put(nameString, Long.valueOf(byteData.getLong(0L)));
                        break;
                }
            }
        }
        return keyValues;
    }

    public static TreeMap<String, Object> registryGetValues(WinReg.HKEY root, String keyPath) {
        return registryGetValues(root, keyPath, 0);
    }

    public static TreeMap<String, Object> registryGetValues(WinReg.HKEY root, String keyPath, int samDesiredExtra) {
        WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
        int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131097 | samDesiredExtra, phkKey);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        try {
            TreeMap<String, Object> treeMapRegistryGetValues = registryGetValues(phkKey.getValue());
            int rc2 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc2 == 0) {
                return treeMapRegistryGetValues;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            int rc3 = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static InfoKey registryQueryInfoKey(WinReg.HKEY hKey, int lpcbSecurityDescriptor) {
        InfoKey infoKey = new InfoKey(hKey, lpcbSecurityDescriptor);
        int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, infoKey.lpClass, infoKey.lpcClass, null, infoKey.lpcSubKeys, infoKey.lpcMaxSubKeyLen, infoKey.lpcMaxClassLen, infoKey.lpcValues, infoKey.lpcMaxValueNameLen, infoKey.lpcMaxValueLen, infoKey.lpcbSecurityDescriptor, infoKey.lpftLastWriteTime);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        return infoKey;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Advapi32Util$InfoKey.class */
    public static class InfoKey {
        public WinReg.HKEY hKey;
        public char[] lpClass;
        public IntByReference lpcClass;
        public IntByReference lpcSubKeys;
        public IntByReference lpcMaxSubKeyLen;
        public IntByReference lpcMaxClassLen;
        public IntByReference lpcValues;
        public IntByReference lpcMaxValueNameLen;
        public IntByReference lpcMaxValueLen;
        public IntByReference lpcbSecurityDescriptor;
        public WinBase.FILETIME lpftLastWriteTime;

        public InfoKey() {
            this.lpClass = new char[260];
            this.lpcClass = new IntByReference(260);
            this.lpcSubKeys = new IntByReference();
            this.lpcMaxSubKeyLen = new IntByReference();
            this.lpcMaxClassLen = new IntByReference();
            this.lpcValues = new IntByReference();
            this.lpcMaxValueNameLen = new IntByReference();
            this.lpcMaxValueLen = new IntByReference();
            this.lpcbSecurityDescriptor = new IntByReference();
            this.lpftLastWriteTime = new WinBase.FILETIME();
        }

        public InfoKey(WinReg.HKEY hKey, int securityDescriptor) {
            this.lpClass = new char[260];
            this.lpcClass = new IntByReference(260);
            this.lpcSubKeys = new IntByReference();
            this.lpcMaxSubKeyLen = new IntByReference();
            this.lpcMaxClassLen = new IntByReference();
            this.lpcValues = new IntByReference();
            this.lpcMaxValueNameLen = new IntByReference();
            this.lpcMaxValueLen = new IntByReference();
            this.lpcbSecurityDescriptor = new IntByReference();
            this.lpftLastWriteTime = new WinBase.FILETIME();
            this.hKey = hKey;
            this.lpcbSecurityDescriptor = new IntByReference(securityDescriptor);
        }
    }

    public static EnumKey registryRegEnumKey(WinReg.HKEY hKey, int dwIndex) {
        EnumKey enumKey = new EnumKey(hKey, dwIndex);
        int rc = Advapi32.INSTANCE.RegEnumKeyEx(hKey, enumKey.dwIndex, enumKey.lpName, enumKey.lpcName, null, enumKey.lpClass, enumKey.lpcbClass, enumKey.lpftLastWriteTime);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        return enumKey;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Advapi32Util$EnumKey.class */
    public static class EnumKey {
        public WinReg.HKEY hKey;
        public int dwIndex;
        public char[] lpName;
        public IntByReference lpcName;
        public char[] lpClass;
        public IntByReference lpcbClass;
        public WinBase.FILETIME lpftLastWriteTime;

        public EnumKey() {
            this.dwIndex = 0;
            this.lpName = new char[255];
            this.lpcName = new IntByReference(255);
            this.lpClass = new char[255];
            this.lpcbClass = new IntByReference(255);
            this.lpftLastWriteTime = new WinBase.FILETIME();
        }

        public EnumKey(WinReg.HKEY hKey, int dwIndex) {
            this.dwIndex = 0;
            this.lpName = new char[255];
            this.lpcName = new IntByReference(255);
            this.lpClass = new char[255];
            this.lpcbClass = new IntByReference(255);
            this.lpftLastWriteTime = new WinBase.FILETIME();
            this.hKey = hKey;
            this.dwIndex = dwIndex;
        }
    }

    public static String getEnvironmentBlock(Map<String, String> environment) {
        StringBuilder out = new StringBuilder(environment.size() * 32);
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null) {
                out.append(key).append("=").append(value).append((char) 0);
            }
        }
        return out.append((char) 0).toString();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Advapi32Util$EventLogRecord.class */
    public static class EventLogRecord {
        private WinNT.EVENTLOGRECORD _record;
        private String _source;
        private byte[] _data;
        private String[] _strings;

        public WinNT.EVENTLOGRECORD getRecord() {
            return this._record;
        }

        public int getInstanceId() {
            return this._record.EventID.intValue();
        }

        @Deprecated
        public int getEventId() {
            return this._record.EventID.intValue();
        }

        public String getSource() {
            return this._source;
        }

        public int getStatusCode() {
            return this._record.EventID.intValue() & 65535;
        }

        public int getRecordNumber() {
            return this._record.RecordNumber.intValue();
        }

        public int getLength() {
            return this._record.Length.intValue();
        }

        public String[] getStrings() {
            return this._strings;
        }

        public EventLogType getType() {
            switch (this._record.EventType.intValue()) {
                case 0:
                case 4:
                    return EventLogType.Informational;
                case 1:
                    return EventLogType.Error;
                case 2:
                    return EventLogType.Warning;
                case 3:
                case 5:
                case 6:
                case 7:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                default:
                    throw new RuntimeException("Invalid type: " + this._record.EventType.intValue());
                case 8:
                    return EventLogType.AuditSuccess;
                case 16:
                    return EventLogType.AuditFailure;
            }
        }

        public byte[] getData() {
            return this._data;
        }

        public EventLogRecord(Pointer pevlr) {
            this._record = new WinNT.EVENTLOGRECORD(pevlr);
            this._source = pevlr.getWideString(this._record.size());
            if (this._record.DataLength.intValue() > 0) {
                this._data = pevlr.getByteArray(this._record.DataOffset.intValue(), this._record.DataLength.intValue());
            }
            if (this._record.NumStrings.intValue() > 0) {
                ArrayList<String> strings = new ArrayList<>();
                long offset = this._record.StringOffset.intValue();
                for (int count = this._record.NumStrings.intValue(); count > 0; count--) {
                    String s = pevlr.getWideString(offset);
                    strings.add(s);
                    offset = offset + ((long) (s.length() * Native.WCHAR_SIZE)) + ((long) Native.WCHAR_SIZE);
                }
                this._strings = (String[]) strings.toArray(new String[0]);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Advapi32Util$EventLogIterator.class */
    public static class EventLogIterator implements Iterable<EventLogRecord>, Iterator<EventLogRecord> {
        private WinNT.HANDLE _h;
        private Memory _buffer;
        private boolean _done;
        private int _dwRead;
        private Pointer _pevlr;
        private int _flags;

        public EventLogIterator(String sourceName) {
            this(null, sourceName, 4);
        }

        public EventLogIterator(String serverName, String sourceName, int flags) {
            this._buffer = new Memory(65536L);
            this._done = false;
            this._dwRead = 0;
            this._pevlr = null;
            this._flags = flags;
            this._h = Advapi32.INSTANCE.OpenEventLog(serverName, sourceName);
            if (this._h == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        }

        private boolean read() {
            if (this._done || this._dwRead > 0) {
                return false;
            }
            IntByReference pnBytesRead = new IntByReference();
            IntByReference pnMinNumberOfBytesNeeded = new IntByReference();
            if (!Advapi32.INSTANCE.ReadEventLog(this._h, 1 | this._flags, 0, this._buffer, (int) this._buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {
                int rc = Kernel32.INSTANCE.GetLastError();
                if (rc == 122) {
                    this._buffer = new Memory(pnMinNumberOfBytesNeeded.getValue());
                    if (!Advapi32.INSTANCE.ReadEventLog(this._h, 1 | this._flags, 0, this._buffer, (int) this._buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {
                        throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                    }
                } else {
                    close();
                    if (rc != 38) {
                        throw new Win32Exception(rc);
                    }
                    return false;
                }
            }
            this._dwRead = pnBytesRead.getValue();
            this._pevlr = this._buffer;
            return true;
        }

        public void close() {
            this._done = true;
            if (this._h != null) {
                if (!Advapi32.INSTANCE.CloseEventLog(this._h)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                this._h = null;
            }
        }

        @Override // java.lang.Iterable
        public Iterator<EventLogRecord> iterator() {
            return this;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            read();
            return !this._done;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public EventLogRecord next() {
            read();
            EventLogRecord record = new EventLogRecord(this._pevlr);
            this._dwRead -= record.getLength();
            this._pevlr = this._pevlr.share(record.getLength());
            return record;
        }

        @Override // java.util.Iterator
        public void remove() {
        }
    }

    public static WinNT.ACE_HEADER[] getFileSecurity(String fileName, boolean compact) {
        boolean repeat;
        Memory memory;
        int nLength = 1024;
        do {
            repeat = false;
            memory = new Memory(nLength);
            IntByReference lpnSize = new IntByReference();
            boolean succeded = Advapi32.INSTANCE.GetFileSecurity(fileName, 4, memory, nLength, lpnSize);
            if (!succeded) {
                int lastError = Kernel32.INSTANCE.GetLastError();
                memory.clear();
                if (122 != lastError) {
                    throw new Win32Exception(lastError);
                }
            }
            int lengthNeeded = lpnSize.getValue();
            if (nLength < lengthNeeded) {
                repeat = true;
                nLength = lengthNeeded;
                memory.clear();
            }
        } while (repeat);
        WinNT.SECURITY_DESCRIPTOR_RELATIVE sdr = new WinNT.SECURITY_DESCRIPTOR_RELATIVE(memory);
        WinNT.ACL dacl = sdr.getDiscretionaryACL();
        WinNT.ACE_HEADER[] aceStructures = dacl.getACEs();
        if (compact) {
            List<WinNT.ACE_HEADER> result = new ArrayList<>();
            Map<String, WinNT.ACCESS_ACEStructure> aceMap = new HashMap<>();
            for (WinNT.ACE_HEADER aceStructure : aceStructures) {
                if (aceStructure instanceof WinNT.ACCESS_ACEStructure) {
                    WinNT.ACCESS_ACEStructure accessACEStructure = (WinNT.ACCESS_ACEStructure) aceStructure;
                    boolean inherted = (aceStructure.AceFlags & 31) != 0;
                    String key = accessACEStructure.getSidString() + "/" + inherted + "/" + aceStructure.getClass().getName();
                    WinNT.ACCESS_ACEStructure aceStructure2 = aceMap.get(key);
                    if (aceStructure2 != null) {
                        int accessMask = aceStructure2.Mask;
                        aceStructure2.Mask = accessMask | accessACEStructure.Mask;
                    } else {
                        aceMap.put(key, accessACEStructure);
                        result.add(aceStructure2);
                    }
                } else {
                    result.add(aceStructure);
                }
            }
            return (WinNT.ACE_HEADER[]) result.toArray(new WinNT.ACE_HEADER[0]);
        }
        return aceStructures;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Advapi32Util$AccessCheckPermission.class */
    public enum AccessCheckPermission {
        READ(Integer.MIN_VALUE),
        WRITE(1073741824),
        EXECUTE(536870912);

        final int code;

        AccessCheckPermission(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    private static Memory getSecurityDescriptorForFile(String absoluteFilePath) {
        int lastError;
        IntByReference lpnSize = new IntByReference();
        boolean succeeded = Advapi32.INSTANCE.GetFileSecurity(absoluteFilePath, 7, null, 0, lpnSize);
        if (!succeeded && 122 != (lastError = Kernel32.INSTANCE.GetLastError())) {
            throw new Win32Exception(lastError);
        }
        int nLength = lpnSize.getValue();
        Memory securityDescriptorMemoryPointer = new Memory(nLength);
        boolean succeeded2 = Advapi32.INSTANCE.GetFileSecurity(absoluteFilePath, 7, securityDescriptorMemoryPointer, nLength, lpnSize);
        if (!succeeded2) {
            securityDescriptorMemoryPointer.clear();
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return securityDescriptorMemoryPointer;
    }

    public static Memory getSecurityDescriptorForObject(String absoluteObjectPath, int objectType, boolean getSACL) {
        int infoType = 7 | (getSACL ? 8 : 0);
        PointerByReference ppSecurityDescriptor = new PointerByReference();
        int lastError = Advapi32.INSTANCE.GetNamedSecurityInfo(absoluteObjectPath, objectType, infoType, null, null, null, null, ppSecurityDescriptor);
        if (lastError != 0) {
            throw new Win32Exception(lastError);
        }
        int nLength = Advapi32.INSTANCE.GetSecurityDescriptorLength(ppSecurityDescriptor.getValue());
        Memory memory = new Memory(nLength);
        Pointer secValue = ppSecurityDescriptor.getValue();
        try {
            byte[] data = secValue.getByteArray(0L, nLength);
            memory.write(0L, data, 0, nLength);
            return memory;
        } finally {
            Kernel32Util.freeLocalMemory(secValue);
        }
    }

    public static void setSecurityDescriptorForObject(String absoluteObjectPath, int objectType, WinNT.SECURITY_DESCRIPTOR_RELATIVE securityDescriptor, boolean setOwner, boolean setGroup, boolean setDACL, boolean setSACL, boolean setDACLProtectedStatus, boolean setSACLProtectedStatus) {
        WinNT.PSID psidOwner = securityDescriptor.getOwner();
        WinNT.PSID psidGroup = securityDescriptor.getGroup();
        WinNT.ACL dacl = securityDescriptor.getDiscretionaryACL();
        WinNT.ACL sacl = securityDescriptor.getSystemACL();
        int infoType = 0;
        if (setOwner) {
            if (psidOwner == null) {
                throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain owner");
            }
            if (!Advapi32.INSTANCE.IsValidSid(psidOwner)) {
                throw new IllegalArgumentException("Owner PSID is invalid");
            }
            infoType = 0 | 1;
        }
        if (setGroup) {
            if (psidGroup == null) {
                throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain group");
            }
            if (!Advapi32.INSTANCE.IsValidSid(psidGroup)) {
                throw new IllegalArgumentException("Group PSID is invalid");
            }
            infoType |= 2;
        }
        if (setDACL) {
            if (dacl == null) {
                throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain DACL");
            }
            if (!Advapi32.INSTANCE.IsValidAcl(dacl.getPointer())) {
                throw new IllegalArgumentException("DACL is invalid");
            }
            infoType |= 4;
        }
        if (setSACL) {
            if (sacl == null) {
                throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain SACL");
            }
            if (!Advapi32.INSTANCE.IsValidAcl(sacl.getPointer())) {
                throw new IllegalArgumentException("SACL is invalid");
            }
            infoType |= 8;
        }
        if (setDACLProtectedStatus) {
            if ((securityDescriptor.Control & 4096) != 0) {
                infoType |= Integer.MIN_VALUE;
            } else if ((securityDescriptor.Control & 4096) == 0) {
                infoType |= 536870912;
            }
        }
        if (setSACLProtectedStatus) {
            if ((securityDescriptor.Control & 8192) != 0) {
                infoType |= 1073741824;
            } else if ((securityDescriptor.Control & 8192) == 0) {
                infoType |= 268435456;
            }
        }
        int lastError = Advapi32.INSTANCE.SetNamedSecurityInfo(absoluteObjectPath, objectType, infoType, setOwner ? psidOwner.getPointer() : null, setGroup ? psidGroup.getPointer() : null, setDACL ? dacl.getPointer() : null, setSACL ? sacl.getPointer() : null);
        if (lastError != 0) {
            throw new Win32Exception(lastError);
        }
    }

    public static boolean accessCheck(File file, AccessCheckPermission permissionToCheck) {
        Memory securityDescriptorMemoryPointer = getSecurityDescriptorForFile(file.getAbsolutePath().replace('/', '\\'));
        WinNT.HANDLEByReference openedAccessToken = new WinNT.HANDLEByReference();
        WinNT.HANDLEByReference duplicatedToken = new WinNT.HANDLEByReference();
        Win32Exception err = null;
        try {
            try {
                WinNT.HANDLE hProcess = Kernel32.INSTANCE.GetCurrentProcess();
                if (!Advapi32.INSTANCE.OpenProcessToken(hProcess, 131086, openedAccessToken)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                if (!Advapi32.INSTANCE.DuplicateToken(openedAccessToken.getValue(), 2, duplicatedToken)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                WinNT.GENERIC_MAPPING mapping = new WinNT.GENERIC_MAPPING();
                mapping.genericRead = new WinDef.DWORD(1179785L);
                mapping.genericWrite = new WinDef.DWORD(1179926L);
                mapping.genericExecute = new WinDef.DWORD(1179808L);
                mapping.genericAll = new WinDef.DWORD(2032127L);
                WinDef.DWORDByReference rights = new WinDef.DWORDByReference(new WinDef.DWORD(permissionToCheck.getCode()));
                Advapi32.INSTANCE.MapGenericMask(rights, mapping);
                WinNT.PRIVILEGE_SET privileges = new WinNT.PRIVILEGE_SET(1);
                privileges.PrivilegeCount = new WinDef.DWORD(0L);
                WinDef.DWORDByReference privilegeLength = new WinDef.DWORDByReference(new WinDef.DWORD(privileges.size()));
                WinDef.DWORDByReference grantedAccess = new WinDef.DWORDByReference();
                WinDef.BOOLByReference result = new WinDef.BOOLByReference();
                if (!Advapi32.INSTANCE.AccessCheck(securityDescriptorMemoryPointer, duplicatedToken.getValue(), rights.getValue(), mapping, privileges, privilegeLength, grantedAccess, result)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                boolean zBooleanValue = result.getValue().booleanValue();
                try {
                    Kernel32Util.closeHandleRefs(openedAccessToken, duplicatedToken);
                } catch (Win32Exception e) {
                    if (0 == 0) {
                        err = e;
                    } else {
                        err.addSuppressedReflected(e);
                    }
                }
                if (securityDescriptorMemoryPointer != null) {
                    securityDescriptorMemoryPointer.clear();
                }
                if (err == null) {
                    return zBooleanValue;
                }
                throw err;
            } catch (Win32Exception e2) {
                throw e2;
            }
        } catch (Throwable th) {
            try {
                Kernel32Util.closeHandleRefs(openedAccessToken, duplicatedToken);
            } catch (Win32Exception e3) {
                if (0 == 0) {
                    err = e3;
                } else {
                    err.addSuppressedReflected(e3);
                }
            }
            if (securityDescriptorMemoryPointer != null) {
                securityDescriptorMemoryPointer.clear();
            }
            if (err != null) {
                throw err;
            }
            throw th;
        }
    }

    public static WinNT.SECURITY_DESCRIPTOR_RELATIVE getFileSecurityDescriptor(File file, boolean getSACL) {
        Memory securityDesc = getSecurityDescriptorForObject(file.getAbsolutePath().replaceAll("/", "\\"), 1, getSACL);
        WinNT.SECURITY_DESCRIPTOR_RELATIVE sdr = new WinNT.SECURITY_DESCRIPTOR_RELATIVE(securityDesc);
        return sdr;
    }

    public static void setFileSecurityDescriptor(File file, WinNT.SECURITY_DESCRIPTOR_RELATIVE securityDescriptor, boolean setOwner, boolean setGroup, boolean setDACL, boolean setSACL, boolean setDACLProtectedStatus, boolean setSACLProtectedStatus) {
        setSecurityDescriptorForObject(file.getAbsolutePath().replaceAll("/", "\\"), 1, securityDescriptor, setOwner, setGroup, setDACL, setSACL, setDACLProtectedStatus, setSACLProtectedStatus);
    }

    public static void encryptFile(File file) {
        String lpFileName = file.getAbsolutePath();
        if (!Advapi32.INSTANCE.EncryptFile(lpFileName)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static void decryptFile(File file) {
        String lpFileName = file.getAbsolutePath();
        if (!Advapi32.INSTANCE.DecryptFile(lpFileName, new WinDef.DWORD(0L))) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static int fileEncryptionStatus(File file) {
        WinDef.DWORDByReference status = new WinDef.DWORDByReference();
        String lpFileName = file.getAbsolutePath();
        if (!Advapi32.INSTANCE.FileEncryptionStatus(lpFileName, status)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return status.getValue().intValue();
    }

    public static void disableEncryption(File directory, boolean disable) {
        String dirPath = directory.getAbsolutePath();
        if (!Advapi32.INSTANCE.EncryptionDisable(dirPath, disable)) {
            throw new Win32Exception(Native.getLastError());
        }
    }

    public static void backupEncryptedFile(File src, File destDir) {
        if (!destDir.isDirectory()) {
            throw new IllegalArgumentException("destDir must be a directory.");
        }
        WinDef.ULONG readFlag = new WinDef.ULONG(0L);
        WinDef.ULONG writeFlag = new WinDef.ULONG(1L);
        if (src.isDirectory()) {
            writeFlag.setValue(3L);
        }
        String srcFileName = src.getAbsolutePath();
        PointerByReference pvContext = new PointerByReference();
        if (Advapi32.INSTANCE.OpenEncryptedFileRaw(srcFileName, readFlag, pvContext) != 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        WinBase.FE_EXPORT_FUNC pfExportCallback = new WinBase.FE_EXPORT_FUNC() { // from class: com.sun.jna.platform.win32.Advapi32Util.1
            @Override // com.sun.jna.platform.win32.WinBase.FE_EXPORT_FUNC
            public WinDef.DWORD callback(Pointer pbData, Pointer pvCallbackContext, WinDef.ULONG ulLength) {
                byte[] arr = pbData.getByteArray(0L, ulLength.intValue());
                try {
                    outputStream.write(arr);
                    return new WinDef.DWORD(0L);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        if (Advapi32.INSTANCE.ReadEncryptedFileRaw(pfExportCallback, null, pvContext.getValue()) != 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        try {
            outputStream.close();
            Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext.getValue());
            String destFileName = destDir.getAbsolutePath() + File.separator + src.getName();
            PointerByReference pvContext2 = new PointerByReference();
            if (Advapi32.INSTANCE.OpenEncryptedFileRaw(destFileName, writeFlag, pvContext2) != 0) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            final IntByReference elementsReadWrapper = new IntByReference(0);
            WinBase.FE_IMPORT_FUNC pfImportCallback = new WinBase.FE_IMPORT_FUNC() { // from class: com.sun.jna.platform.win32.Advapi32Util.2
                @Override // com.sun.jna.platform.win32.WinBase.FE_IMPORT_FUNC
                public WinDef.DWORD callback(Pointer pbData, Pointer pvCallbackContext, WinDef.ULONGByReference ulLength) {
                    int elementsRead = elementsReadWrapper.getValue();
                    int remainingElements = outputStream.size() - elementsRead;
                    int length = Math.min(remainingElements, ulLength.getValue().intValue());
                    pbData.write(0L, outputStream.toByteArray(), elementsRead, length);
                    elementsReadWrapper.setValue(elementsRead + length);
                    ulLength.setValue(new WinDef.ULONG(length));
                    return new WinDef.DWORD(0L);
                }
            };
            if (Advapi32.INSTANCE.WriteEncryptedFileRaw(pfImportCallback, null, pvContext2.getValue()) != 0) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext2.getValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Advapi32Util$Privilege.class */
    public static class Privilege implements Closeable {
        private boolean currentlyImpersonating = false;
        private boolean privilegesEnabled = false;
        private final WinNT.LUID[] pLuids;

        public Privilege(String... privileges) throws Win32Exception, IllegalArgumentException {
            this.pLuids = new WinNT.LUID[privileges.length];
            int i = 0;
            for (String p : privileges) {
                this.pLuids[i] = new WinNT.LUID();
                if (!Advapi32.INSTANCE.LookupPrivilegeValue(null, p, this.pLuids[i])) {
                    throw new IllegalArgumentException("Failed to find privilege \"" + privileges[i] + "\" - " + Kernel32.INSTANCE.GetLastError());
                }
                i++;
            }
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            disable();
        }

        public Privilege enable() throws Win32Exception {
            if (this.privilegesEnabled) {
                return this;
            }
            WinNT.HANDLEByReference phThreadToken = new WinNT.HANDLEByReference();
            try {
                try {
                    phThreadToken.setValue(getThreadToken());
                    WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);
                    for (int i = 0; i < this.pLuids.length; i++) {
                        tp.Privileges[i] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[i], new WinDef.DWORD(2L));
                    }
                    if (!Advapi32.INSTANCE.AdjustTokenPrivileges(phThreadToken.getValue(), false, tp, 0, null, null)) {
                        throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                    }
                    this.privilegesEnabled = true;
                    if (!WinBase.INVALID_HANDLE_VALUE.equals(phThreadToken.getValue()) && phThreadToken.getValue() != null) {
                        Kernel32.INSTANCE.CloseHandle(phThreadToken.getValue());
                        phThreadToken.setValue(null);
                    }
                    return this;
                } catch (Win32Exception ex) {
                    if (this.currentlyImpersonating) {
                        Advapi32.INSTANCE.SetThreadToken(null, null);
                        this.currentlyImpersonating = false;
                    } else if (this.privilegesEnabled) {
                        WinNT.TOKEN_PRIVILEGES tp2 = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);
                        for (int i2 = 0; i2 < this.pLuids.length; i2++) {
                            tp2.Privileges[i2] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[i2], new WinDef.DWORD(0L));
                        }
                        Advapi32.INSTANCE.AdjustTokenPrivileges(phThreadToken.getValue(), false, tp2, 0, null, null);
                        this.privilegesEnabled = false;
                    }
                    throw ex;
                }
            } catch (Throwable th) {
                if (!WinBase.INVALID_HANDLE_VALUE.equals(phThreadToken.getValue()) && phThreadToken.getValue() != null) {
                    Kernel32.INSTANCE.CloseHandle(phThreadToken.getValue());
                    phThreadToken.setValue(null);
                }
                throw th;
            }
        }

        public void disable() throws Win32Exception {
            boolean zEquals;
            WinNT.HANDLEByReference phThreadToken = new WinNT.HANDLEByReference();
            try {
                phThreadToken.setValue(getThreadToken());
                if (this.currentlyImpersonating) {
                    Advapi32.INSTANCE.SetThreadToken(null, null);
                } else if (this.privilegesEnabled) {
                    WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);
                    for (int i = 0; i < this.pLuids.length; i++) {
                        tp.Privileges[i] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[i], new WinDef.DWORD(0L));
                    }
                    Advapi32.INSTANCE.AdjustTokenPrivileges(phThreadToken.getValue(), false, tp, 0, null, null);
                    this.privilegesEnabled = false;
                }
                if (zEquals) {
                    return;
                }
            } finally {
                if (!WinBase.INVALID_HANDLE_VALUE.equals(phThreadToken.getValue()) && phThreadToken.getValue() != null) {
                    Kernel32.INSTANCE.CloseHandle(phThreadToken.getValue());
                    phThreadToken.setValue(null);
                }
            }
        }

        private WinNT.HANDLE getThreadToken() throws Win32Exception {
            WinNT.HANDLEByReference phThreadToken = new WinNT.HANDLEByReference();
            WinNT.HANDLEByReference phProcessToken = new WinNT.HANDLEByReference();
            try {
                try {
                    if (!Advapi32.INSTANCE.OpenThreadToken(Kernel32.INSTANCE.GetCurrentThread(), 32, false, phThreadToken)) {
                        int lastError = Kernel32.INSTANCE.GetLastError();
                        if (1008 != lastError) {
                            throw new Win32Exception(lastError);
                        }
                        if (!Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 2, phProcessToken)) {
                            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                        }
                        if (!Advapi32.INSTANCE.DuplicateTokenEx(phProcessToken.getValue(), 36, null, 2, 2, phThreadToken)) {
                            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                        }
                        if (!Advapi32.INSTANCE.SetThreadToken(null, phThreadToken.getValue())) {
                            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                        }
                        this.currentlyImpersonating = true;
                    }
                    if (!WinBase.INVALID_HANDLE_VALUE.equals(phProcessToken.getValue()) && phProcessToken.getValue() != null) {
                        Kernel32.INSTANCE.CloseHandle(phProcessToken.getValue());
                        phProcessToken.setValue(null);
                    }
                    return phThreadToken.getValue();
                } catch (Win32Exception ex) {
                    if (!WinBase.INVALID_HANDLE_VALUE.equals(phThreadToken.getValue()) && phThreadToken.getValue() != null) {
                        Kernel32.INSTANCE.CloseHandle(phThreadToken.getValue());
                        phThreadToken.setValue(null);
                    }
                    throw ex;
                }
            } catch (Throwable th) {
                if (!WinBase.INVALID_HANDLE_VALUE.equals(phProcessToken.getValue()) && phProcessToken.getValue() != null) {
                    Kernel32.INSTANCE.CloseHandle(phProcessToken.getValue());
                    phProcessToken.setValue(null);
                }
                throw th;
            }
        }
    }
}
