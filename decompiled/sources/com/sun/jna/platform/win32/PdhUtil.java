package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/PdhUtil.class */
public abstract class PdhUtil {
    private static final int CHAR_TO_BYTES;
    private static final String ENGLISH_COUNTER_KEY = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009";
    private static final String ENGLISH_COUNTER_VALUE = "Counter";

    static {
        CHAR_TO_BYTES = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
    }

    public static String PdhLookupPerfNameByIndex(String szMachineName, int dwNameIndex) {
        WinDef.DWORDByReference pcchNameBufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
        int result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, null, pcchNameBufferSize);
        Memory mem = null;
        if (result != -1073738819) {
            if (result != 0 && result != -2147481646) {
                throw new PdhException(result);
            }
            if (pcchNameBufferSize.getValue().intValue() < 1) {
                return "";
            }
            mem = new Memory(pcchNameBufferSize.getValue().intValue() * CHAR_TO_BYTES);
            result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, mem, pcchNameBufferSize);
        } else {
            int i = 32;
            while (true) {
                int bufferSize = i;
                if (bufferSize > 1024) {
                    break;
                }
                WinDef.DWORDByReference pcchNameBufferSize2 = new WinDef.DWORDByReference(new WinDef.DWORD(bufferSize));
                mem = new Memory(bufferSize * CHAR_TO_BYTES);
                result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, mem, pcchNameBufferSize2);
                if (result != -1073738819 && result != -1073738814) {
                    break;
                }
                i = bufferSize * 2;
            }
        }
        if (result != 0) {
            throw new PdhException(result);
        }
        if (CHAR_TO_BYTES == 1) {
            return mem.getString(0L);
        }
        return mem.getWideString(0L);
    }

    public static int PdhLookupPerfIndexByEnglishName(String szNameBuffer) {
        String[] counters = Advapi32Util.registryGetStringArray(WinReg.HKEY_LOCAL_MACHINE, ENGLISH_COUNTER_KEY, ENGLISH_COUNTER_VALUE);
        for (int i = 1; i < counters.length; i += 2) {
            if (counters[i].equals(szNameBuffer)) {
                try {
                    return Integer.parseInt(counters[i - 1]);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    public static PdhEnumObjectItems PdhEnumObjectItems(String szDataSource, String szMachineName, String szObjectName, int dwDetailLevel) {
        String s;
        String s2;
        List<String> counters = new ArrayList<>();
        List<String> instances = new ArrayList<>();
        WinDef.DWORDByReference pcchCounterListLength = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
        WinDef.DWORDByReference pcchInstanceListLength = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
        int result = Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, null, pcchCounterListLength, null, pcchInstanceListLength, dwDetailLevel, 0);
        if (result != 0 && result != -2147481646) {
            throw new PdhException(result);
        }
        Memory mszCounterList = null;
        Memory mszInstanceList = null;
        if (pcchCounterListLength.getValue().intValue() > 0) {
            mszCounterList = new Memory(pcchCounterListLength.getValue().intValue() * CHAR_TO_BYTES);
        }
        if (pcchInstanceListLength.getValue().intValue() > 0) {
            mszInstanceList = new Memory(pcchInstanceListLength.getValue().intValue() * CHAR_TO_BYTES);
        }
        int result2 = Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, mszCounterList, pcchCounterListLength, mszInstanceList, pcchInstanceListLength, dwDetailLevel, 0);
        if (result2 != 0) {
            throw new PdhException(result2);
        }
        if (mszCounterList != null) {
            int length = 0;
            while (true) {
                int offset = length;
                if (offset >= mszCounterList.size()) {
                    break;
                }
                if (CHAR_TO_BYTES == 1) {
                    s2 = mszCounterList.getString(offset);
                } else {
                    s2 = mszCounterList.getWideString(offset);
                }
                if (s2.isEmpty()) {
                    break;
                }
                counters.add(s2);
                length = offset + ((s2.length() + 1) * CHAR_TO_BYTES);
            }
        }
        if (mszInstanceList != null) {
            int length2 = 0;
            while (true) {
                int offset2 = length2;
                if (offset2 >= mszInstanceList.size()) {
                    break;
                }
                if (CHAR_TO_BYTES == 1) {
                    s = mszInstanceList.getString(offset2);
                } else {
                    s = mszInstanceList.getWideString(offset2);
                }
                if (s.isEmpty()) {
                    break;
                }
                instances.add(s);
                length2 = offset2 + ((s.length() + 1) * CHAR_TO_BYTES);
            }
        }
        return new PdhEnumObjectItems(counters, instances);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/PdhUtil$PdhEnumObjectItems.class */
    public static class PdhEnumObjectItems {
        private final List<String> counters;
        private final List<String> instances;

        public PdhEnumObjectItems(List<String> counters, List<String> instances) {
            this.counters = copyAndEmptyListForNullList(counters);
            this.instances = copyAndEmptyListForNullList(instances);
        }

        public List<String> getCounters() {
            return this.counters;
        }

        public List<String> getInstances() {
            return this.instances;
        }

        private List<String> copyAndEmptyListForNullList(List<String> inputList) {
            if (inputList == null) {
                return new ArrayList();
            }
            return new ArrayList(inputList);
        }

        public String toString() {
            return "PdhEnumObjectItems{counters=" + this.counters + ", instances=" + this.instances + '}';
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/PdhUtil$PdhException.class */
    public static final class PdhException extends RuntimeException {
        private final int errorCode;

        public PdhException(int errorCode) {
            super(String.format("Pdh call failed with error code 0x%08X", Integer.valueOf(errorCode)));
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return this.errorCode;
        }
    }
}
