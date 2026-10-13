package com.sun.jna.platform.win32;

import com.ctc.wstx.shaded.msv_core.grammar.util.PossibleNamesCollector;
import com.sun.jna.LastErrorException;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Kernel32Util.class */
public abstract class Kernel32Util implements WinDef {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final String VOLUME_GUID_PATH_PREFIX = "\\\\?\\Volume{";
    public static final String VOLUME_GUID_PATH_SUFFIX = "}\\";

    public static String getComputerName() {
        char[] buffer = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
        IntByReference lpnSize = new IntByReference(buffer.length);
        if (!Kernel32.INSTANCE.GetComputerName(buffer, lpnSize)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString(buffer);
    }

    public static void freeLocalMemory(Pointer ptr) {
        Pointer res = Kernel32.INSTANCE.LocalFree(ptr);
        if (res != null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static void freeGlobalMemory(Pointer ptr) {
        Pointer res = Kernel32.INSTANCE.GlobalFree(ptr);
        if (res != null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static void closeHandleRefs(WinNT.HANDLEByReference... refs) {
        Win32Exception err = null;
        for (WinNT.HANDLEByReference r : refs) {
            try {
                closeHandleRef(r);
            } catch (Win32Exception e) {
                if (err == null) {
                    err = e;
                } else {
                    err.addSuppressedReflected(e);
                }
            }
        }
        if (err != null) {
            throw err;
        }
    }

    public static void closeHandleRef(WinNT.HANDLEByReference ref) {
        closeHandle(ref == null ? null : ref.getValue());
    }

    public static void closeHandles(WinNT.HANDLE... handles) {
        Win32Exception err = null;
        for (WinNT.HANDLE h : handles) {
            try {
                closeHandle(h);
            } catch (Win32Exception e) {
                if (err == null) {
                    err = e;
                } else {
                    err.addSuppressedReflected(e);
                }
            }
        }
        if (err != null) {
            throw err;
        }
    }

    public static void closeHandle(WinNT.HANDLE h) {
        if (h != null && !Kernel32.INSTANCE.CloseHandle(h)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static String formatMessage(int code) {
        PointerByReference buffer = new PointerByReference();
        int nLen = Kernel32.INSTANCE.FormatMessage(4864, null, code, 0, buffer, 0, null);
        if (nLen == 0) {
            throw new LastErrorException(Native.getLastError());
        }
        Pointer ptr = buffer.getValue();
        try {
            String s = ptr.getWideString(0L);
            return s.trim();
        } finally {
            freeLocalMemory(ptr);
        }
    }

    public static String formatMessage(WinNT.HRESULT code) {
        return formatMessage(code.intValue());
    }

    public static String formatMessageFromLastErrorCode(int code) {
        return formatMessage(W32Errors.HRESULT_FROM_WIN32(code));
    }

    public static String getLastErrorMessage() {
        return formatMessageFromLastErrorCode(Kernel32.INSTANCE.GetLastError());
    }

    public static String getTempPath() {
        WinDef.DWORD nBufferLength = new WinDef.DWORD(260L);
        char[] buffer = new char[nBufferLength.intValue()];
        if (Kernel32.INSTANCE.GetTempPath(nBufferLength, buffer).intValue() == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString(buffer);
    }

    public static void deleteFile(String filename) {
        if (!Kernel32.INSTANCE.DeleteFile(filename)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static List<String> getLogicalDriveStrings() {
        WinDef.DWORD dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(new WinDef.DWORD(0L), null);
        if (dwSize.intValue() <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        char[] buf = new char[dwSize.intValue()];
        int bufSize = Kernel32.INSTANCE.GetLogicalDriveStrings(dwSize, buf).intValue();
        if (bufSize <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toStringList(buf, 0, bufSize);
    }

    public static int getFileAttributes(String fileName) {
        int fileAttributes = Kernel32.INSTANCE.GetFileAttributes(fileName);
        if (fileAttributes == -1) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return fileAttributes;
    }

    public static int getFileType(String fileName) throws FileNotFoundException {
        File f = new File(fileName);
        if (!f.exists()) {
            throw new FileNotFoundException(fileName);
        }
        Win32Exception err = null;
        try {
            try {
                WinNT.HANDLE hFile = Kernel32.INSTANCE.CreateFile(fileName, Integer.MIN_VALUE, 1, new WinBase.SECURITY_ATTRIBUTES(), 3, 128, new WinNT.HANDLEByReference().getValue());
                if (WinBase.INVALID_HANDLE_VALUE.equals(hFile)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                int type = Kernel32.INSTANCE.GetFileType(hFile);
                switch (type) {
                    case 0:
                        int rc = Kernel32.INSTANCE.GetLastError();
                        switch (rc) {
                            case 0:
                                break;
                            default:
                                throw new Win32Exception(rc);
                        }
                        break;
                }
                try {
                    closeHandle(hFile);
                } catch (Win32Exception e) {
                    if (0 == 0) {
                        err = e;
                    } else {
                        err.addSuppressedReflected(e);
                    }
                }
                if (err == null) {
                    return type;
                }
                throw err;
            } catch (Throwable th) {
                try {
                    closeHandle(null);
                } catch (Win32Exception e2) {
                    if (0 == 0) {
                        err = e2;
                    } else {
                        err.addSuppressedReflected(e2);
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

    public static int getDriveType(String rootName) {
        return Kernel32.INSTANCE.GetDriveType(rootName);
    }

    public static String getEnvironmentVariable(String name) {
        int size = Kernel32.INSTANCE.GetEnvironmentVariable(name, null, 0);
        if (size == 0) {
            return null;
        }
        if (size < 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        char[] buffer = new char[size];
        if (Kernel32.INSTANCE.GetEnvironmentVariable(name, buffer, buffer.length) <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString(buffer);
    }

    public static Map<String, String> getEnvironmentVariables() {
        Pointer lpszEnvironmentBlock = Kernel32.INSTANCE.GetEnvironmentStrings();
        if (lpszEnvironmentBlock == null) {
            throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
        }
        try {
            Map<String, String> environmentVariables = getEnvironmentVariables(lpszEnvironmentBlock, 0L);
            if (Kernel32.INSTANCE.FreeEnvironmentStrings(lpszEnvironmentBlock)) {
                return environmentVariables;
            }
            throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
        } catch (Throwable th) {
            if (!Kernel32.INSTANCE.FreeEnvironmentStrings(lpszEnvironmentBlock)) {
                throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
            }
            throw th;
        }
    }

    public static Map<String, String> getEnvironmentVariables(Pointer lpszEnvironmentBlock, long offset) {
        if (lpszEnvironmentBlock == null) {
            return null;
        }
        Map<String, String> vars = new TreeMap<>();
        boolean asWideChars = isWideCharEnvironmentStringBlock(lpszEnvironmentBlock, offset);
        long stepFactor = asWideChars ? 2L : 1L;
        long j = offset;
        while (true) {
            long curOffset = j;
            String nvp = readEnvironmentStringBlockEntry(lpszEnvironmentBlock, curOffset, asWideChars);
            int len = nvp.length();
            if (len != 0) {
                int pos = nvp.indexOf(61);
                if (pos < 0) {
                    throw new IllegalArgumentException("Missing variable value separator in " + nvp);
                }
                String name = nvp.substring(0, pos);
                String value = nvp.substring(pos + 1);
                vars.put(name, value);
                j = curOffset + (((long) (len + 1)) * stepFactor);
            } else {
                return vars;
            }
        }
    }

    public static String readEnvironmentStringBlockEntry(Pointer lpszEnvironmentBlock, long offset, boolean asWideChars) {
        long endOffset = findEnvironmentStringBlockEntryEnd(lpszEnvironmentBlock, offset, asWideChars);
        int dataLen = (int) (endOffset - offset);
        if (dataLen == 0) {
            return "";
        }
        int charsLen = asWideChars ? dataLen / 2 : dataLen;
        char[] chars = new char[charsLen];
        long curOffset = offset;
        long stepSize = asWideChars ? 2L : 1L;
        ByteOrder byteOrder = ByteOrder.nativeOrder();
        int index = 0;
        while (index < chars.length) {
            byte b = lpszEnvironmentBlock.getByte(curOffset);
            if (asWideChars) {
                byte x = lpszEnvironmentBlock.getByte(curOffset + 1);
                if (ByteOrder.LITTLE_ENDIAN.equals(byteOrder)) {
                    chars[index] = (char) (((x << 8) & 65280) | (b & 255));
                } else {
                    chars[index] = (char) (((b << 8) & 65280) | (x & 255));
                }
            } else {
                chars[index] = (char) (b & 255);
            }
            index++;
            curOffset += stepSize;
        }
        return new String(chars);
    }

    public static long findEnvironmentStringBlockEntryEnd(Pointer lpszEnvironmentBlock, long offset, boolean asWideChars) {
        long curOffset = offset;
        long stepSize = asWideChars ? 2L : 1L;
        while (b != 0) {
            curOffset += stepSize;
        }
        return curOffset;
    }

    public static boolean isWideCharEnvironmentStringBlock(Pointer lpszEnvironmentBlock, long offset) {
        byte b0 = lpszEnvironmentBlock.getByte(offset);
        byte b1 = lpszEnvironmentBlock.getByte(offset + 1);
        ByteOrder byteOrder = ByteOrder.nativeOrder();
        if (ByteOrder.LITTLE_ENDIAN.equals(byteOrder)) {
            return isWideCharEnvironmentStringBlock(b1);
        }
        return isWideCharEnvironmentStringBlock(b0);
    }

    private static boolean isWideCharEnvironmentStringBlock(byte charsetIndicator) {
        if (charsetIndicator != 0) {
            return false;
        }
        return true;
    }

    public static final int getPrivateProfileInt(String appName, String keyName, int defaultValue, String fileName) {
        return Kernel32.INSTANCE.GetPrivateProfileInt(appName, keyName, defaultValue, fileName);
    }

    public static final String getPrivateProfileString(String lpAppName, String lpKeyName, String lpDefault, String lpFileName) {
        char[] buffer = new char[1024];
        Kernel32.INSTANCE.GetPrivateProfileString(lpAppName, lpKeyName, lpDefault, buffer, new WinDef.DWORD(buffer.length), lpFileName);
        return Native.toString(buffer);
    }

    public static final void writePrivateProfileString(String appName, String keyName, String string, String fileName) {
        if (!Kernel32.INSTANCE.WritePrivateProfileString(appName, keyName, string, fileName)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static final WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] getLogicalProcessorInformation() {
        int err;
        int sizePerStruct = new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION().size();
        WinDef.DWORDByReference bufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(sizePerStruct));
        do {
            Memory memory = new Memory(bufferSize.getValue().intValue());
            if (!Kernel32.INSTANCE.GetLogicalProcessorInformation(memory, bufferSize)) {
                err = Kernel32.INSTANCE.GetLastError();
            } else {
                WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION firstInformation = new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION(memory);
                int returnedStructCount = bufferSize.getValue().intValue() / sizePerStruct;
                return (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[]) firstInformation.toArray(new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[returnedStructCount]);
            }
        } while (err == 122);
        throw new Win32Exception(err);
    }

    public static final WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[] getLogicalProcessorInformationEx(int relationshipType) {
        int err;
        WinDef.DWORDByReference bufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(1L));
        do {
            Memory memory = new Memory(bufferSize.getValue().intValue());
            if (!Kernel32.INSTANCE.GetLogicalProcessorInformationEx(relationshipType, memory, bufferSize)) {
                err = Kernel32.INSTANCE.GetLastError();
            } else {
                List<WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX> procInfoList = new ArrayList<>();
                int i = 0;
                while (true) {
                    int offset = i;
                    if (offset < bufferSize.getValue().intValue()) {
                        WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX information = WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX.fromPointer(memory.share(offset));
                        procInfoList.add(information);
                        i = offset + information.size;
                    } else {
                        return (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[]) procInfoList.toArray(new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[0]);
                    }
                }
            }
        } while (err == 122);
        throw new Win32Exception(err);
    }

    public static final String[] getPrivateProfileSection(String appName, String fileName) {
        char[] buffer = new char[32768];
        if (Kernel32.INSTANCE.GetPrivateProfileSection(appName, buffer, new WinDef.DWORD(buffer.length), fileName).intValue() == 0) {
            int lastError = Kernel32.INSTANCE.GetLastError();
            if (lastError == 0) {
                return EMPTY_STRING_ARRAY;
            }
            throw new Win32Exception(lastError);
        }
        return new String(buffer).split(PossibleNamesCollector.MAGIC);
    }

    public static final String[] getPrivateProfileSectionNames(String fileName) {
        char[] buffer = new char[65536];
        if (Kernel32.INSTANCE.GetPrivateProfileSectionNames(buffer, new WinDef.DWORD(buffer.length), fileName).intValue() == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return new String(buffer).split(PossibleNamesCollector.MAGIC);
    }

    public static final void writePrivateProfileSection(String appName, String[] strings, String fileName) {
        StringBuilder buffer = new StringBuilder();
        for (String string : strings) {
            buffer.append(string).append((char) 0);
        }
        buffer.append((char) 0);
        if (!Kernel32.INSTANCE.WritePrivateProfileSection(appName, buffer.toString(), fileName)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public static final List<String> queryDosDevice(String lpszDeviceName, int maxTargetSize) {
        char[] lpTargetPath = new char[maxTargetSize];
        int dwSize = Kernel32.INSTANCE.QueryDosDevice(lpszDeviceName, lpTargetPath, lpTargetPath.length);
        if (dwSize == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toStringList(lpTargetPath, 0, dwSize);
    }

    public static final List<String> getVolumePathNamesForVolumeName(String lpszVolumeName) {
        char[] lpszVolumePathNames = new char[261];
        IntByReference lpcchReturnLength = new IntByReference();
        if (!Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(lpszVolumeName, lpszVolumePathNames, lpszVolumePathNames.length, lpcchReturnLength)) {
            int hr = Kernel32.INSTANCE.GetLastError();
            if (hr != 234) {
                throw new Win32Exception(hr);
            }
            int required = lpcchReturnLength.getValue();
            lpszVolumePathNames = new char[required];
            if (!Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(lpszVolumeName, lpszVolumePathNames, lpszVolumePathNames.length, lpcchReturnLength)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        }
        int bufSize = lpcchReturnLength.getValue();
        return Native.toStringList(lpszVolumePathNames, 0, bufSize);
    }

    public static final String extractVolumeGUID(String volumeGUIDPath) {
        if (volumeGUIDPath == null || volumeGUIDPath.length() <= VOLUME_GUID_PATH_PREFIX.length() + VOLUME_GUID_PATH_SUFFIX.length() || !volumeGUIDPath.startsWith(VOLUME_GUID_PATH_PREFIX) || !volumeGUIDPath.endsWith(VOLUME_GUID_PATH_SUFFIX)) {
            throw new IllegalArgumentException("Bad volume GUID path format: " + volumeGUIDPath);
        }
        return volumeGUIDPath.substring(VOLUME_GUID_PATH_PREFIX.length(), volumeGUIDPath.length() - VOLUME_GUID_PATH_SUFFIX.length());
    }

    public static final String QueryFullProcessImageName(int pid, int dwFlags) {
        Win32Exception we = null;
        try {
            try {
                WinNT.HANDLE hProcess = Kernel32.INSTANCE.OpenProcess(1040, false, pid);
                if (hProcess == null) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                String strQueryFullProcessImageName = QueryFullProcessImageName(hProcess, dwFlags);
                try {
                    closeHandle(hProcess);
                } catch (Win32Exception e) {
                    if (0 == 0) {
                        we = e;
                    } else {
                        we.addSuppressed(e);
                    }
                }
                if (we == null) {
                    return strQueryFullProcessImageName;
                }
                throw we;
            } catch (Throwable th) {
                try {
                    closeHandle(null);
                } catch (Win32Exception e2) {
                    if (0 == 0) {
                        we = e2;
                    } else {
                        we.addSuppressed(e2);
                    }
                }
                if (we != null) {
                    throw we;
                }
                throw th;
            }
        } catch (Win32Exception e3) {
            throw e3;
        }
    }

    public static final String QueryFullProcessImageName(WinNT.HANDLE hProcess, int dwFlags) {
        int size = 260;
        IntByReference lpdwSize = new IntByReference();
        do {
            char[] lpExeName = new char[size];
            lpdwSize.setValue(size);
            if (Kernel32.INSTANCE.QueryFullProcessImageName(hProcess, dwFlags, lpExeName, lpdwSize)) {
                return new String(lpExeName, 0, lpdwSize.getValue());
            }
            size += 1024;
        } while (Kernel32.INSTANCE.GetLastError() == 122);
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    }

    public static byte[] getResource(String path, String type, String name) {
        Pointer t;
        Pointer n;
        WinDef.HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, null, 2);
        if (target == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        Win32Exception err = null;
        byte[] results = null;
        try {
            try {
                t = new Pointer(Long.parseLong(type));
            } catch (NumberFormatException e) {
                t = new Memory(Native.WCHAR_SIZE * (type.length() + 1));
                t.setWideString(0L, type);
            }
            try {
                n = new Pointer(Long.parseLong(name));
            } catch (NumberFormatException e2) {
                n = new Memory(Native.WCHAR_SIZE * (name.length() + 1));
                n.setWideString(0L, name);
            }
            WinDef.HRSRC hrsrc = Kernel32.INSTANCE.FindResource(target, n, t);
            if (hrsrc == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            WinNT.HANDLE loaded = Kernel32.INSTANCE.LoadResource(target, hrsrc);
            if (loaded == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            int length = Kernel32.INSTANCE.SizeofResource(target, hrsrc);
            if (length == 0) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            Pointer start = Kernel32.INSTANCE.LockResource(loaded);
            if (start == null) {
                throw new IllegalStateException("LockResource returned null.");
            }
            results = start.getByteArray(0L, length);
            if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
                Win32Exception we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                if (0 != 0) {
                    we.addSuppressedReflected(null);
                }
                throw we;
            }
            if (err != null) {
                throw err;
            }
            return results;
        } catch (Win32Exception we2) {
            err = we2;
            if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
                Win32Exception we3 = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                if (err != null) {
                    we3.addSuppressedReflected(err);
                }
                throw we3;
            }
        } catch (Throwable th) {
            if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
                Win32Exception we4 = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                if (0 != 0) {
                    we4.addSuppressedReflected(null);
                }
                throw we4;
            }
            throw th;
        }
    }

    public static Map<String, List<String>> getResourceNames(String path) {
        Pointer pointer;
        WinDef.HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, null, 2);
        if (target == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        final List<String> types = new ArrayList<>();
        final Map<String, List<String>> result = new LinkedHashMap<>();
        WinBase.EnumResTypeProc ertp = new WinBase.EnumResTypeProc() { // from class: com.sun.jna.platform.win32.Kernel32Util.1
            @Override // com.sun.jna.platform.win32.WinBase.EnumResTypeProc
            public boolean invoke(WinDef.HMODULE module, Pointer type, Pointer lParam) {
                if (Pointer.nativeValue(type) <= 65535) {
                    types.add(Pointer.nativeValue(type) + "");
                    return true;
                }
                types.add(type.getWideString(0L));
                return true;
            }
        };
        WinBase.EnumResNameProc ernp = new WinBase.EnumResNameProc() { // from class: com.sun.jna.platform.win32.Kernel32Util.2
            @Override // com.sun.jna.platform.win32.WinBase.EnumResNameProc
            public boolean invoke(WinDef.HMODULE module, Pointer type, Pointer name, Pointer lParam) {
                String typeName;
                if (Pointer.nativeValue(type) <= 65535) {
                    typeName = Pointer.nativeValue(type) + "";
                } else {
                    typeName = type.getWideString(0L);
                }
                if (Pointer.nativeValue(name) < 65535) {
                    ((List) result.get(typeName)).add(Pointer.nativeValue(name) + "");
                    return true;
                }
                ((List) result.get(typeName)).add(name.getWideString(0L));
                return true;
            }
        };
        Win32Exception err = null;
        try {
            if (!Kernel32.INSTANCE.EnumResourceTypes(target, ertp, null)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            for (String typeName : types) {
                result.put(typeName, new ArrayList<>());
                try {
                    pointer = new Pointer(Long.parseLong(typeName));
                } catch (NumberFormatException e) {
                    pointer = new Memory(Native.WCHAR_SIZE * (typeName.length() + 1));
                    pointer.setWideString(0L, typeName);
                }
                boolean callResult = Kernel32.INSTANCE.EnumResourceNames(target, pointer, ernp, null);
                if (!callResult) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
            }
            if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
                Win32Exception we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                if (0 != 0) {
                    we.addSuppressedReflected(null);
                }
                throw we;
            }
            if (err != null) {
                throw err;
            }
            return result;
        } catch (Win32Exception e2) {
            err = e2;
            if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
                Win32Exception we2 = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                if (err != null) {
                    we2.addSuppressedReflected(err);
                }
                throw we2;
            }
        } catch (Throwable th) {
            if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
                Win32Exception we3 = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                if (0 != 0) {
                    we3.addSuppressedReflected(null);
                }
                throw we3;
            }
            throw th;
        }
    }

    public static List<Tlhelp32.MODULEENTRY32W> getModules(int processID) {
        WinNT.HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new WinDef.DWORD(processID));
        if (snapshot == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        Win32Exception we = null;
        try {
            try {
                Tlhelp32.MODULEENTRY32W first = new Tlhelp32.MODULEENTRY32W();
                if (!Kernel32.INSTANCE.Module32FirstW(snapshot, first)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                List<Tlhelp32.MODULEENTRY32W> modules = new ArrayList<>();
                modules.add(first);
                for (Tlhelp32.MODULEENTRY32W next = new Tlhelp32.MODULEENTRY32W(); Kernel32.INSTANCE.Module32NextW(snapshot, next); next = new Tlhelp32.MODULEENTRY32W()) {
                    modules.add(next);
                }
                int lastError = Kernel32.INSTANCE.GetLastError();
                if (lastError != 0 && lastError != 18) {
                    throw new Win32Exception(lastError);
                }
                try {
                    closeHandle(snapshot);
                } catch (Win32Exception e) {
                    if (0 == 0) {
                        we = e;
                    } else {
                        we.addSuppressedReflected(e);
                    }
                }
                if (we == null) {
                    return modules;
                }
                throw we;
            } catch (Throwable th) {
                try {
                    closeHandle(snapshot);
                } catch (Win32Exception e2) {
                    if (0 == 0) {
                        we = e2;
                    } else {
                        we.addSuppressedReflected(e2);
                    }
                }
                if (we != null) {
                    throw we;
                }
                throw th;
            }
        } catch (Win32Exception e3) {
            throw e3;
        }
    }

    public static String expandEnvironmentStrings(String input) {
        Memory resultMemory;
        if (input == null) {
            return "";
        }
        int resultChars = Kernel32.INSTANCE.ExpandEnvironmentStrings(input, null, 0);
        if (resultChars == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
            resultMemory = new Memory(resultChars * Native.WCHAR_SIZE);
        } else {
            resultMemory = new Memory(resultChars + 1);
        }
        if (Kernel32.INSTANCE.ExpandEnvironmentStrings(input, resultMemory, resultChars) == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
            return resultMemory.getWideString(0L);
        }
        return resultMemory.getString(0L);
    }
}
