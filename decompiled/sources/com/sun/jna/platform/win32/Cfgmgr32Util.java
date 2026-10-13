package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APITypeMapper;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Cfgmgr32Util.class */
public abstract class Cfgmgr32Util {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Cfgmgr32Util$Cfgmgr32Exception.class */
    public static class Cfgmgr32Exception extends RuntimeException {
        private final int errorCode;

        public Cfgmgr32Exception(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return this.errorCode;
        }

        @Override // java.lang.Throwable
        public String toString() {
            return super.toString() + String.format(" [errorCode: 0x%08x]", Integer.valueOf(this.errorCode));
        }
    }

    public static String CM_Get_Device_ID(int devInst) throws Cfgmgr32Exception {
        int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
        IntByReference pulLen = new IntByReference();
        int ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
        if (ret != 0) {
            throw new Cfgmgr32Exception(ret);
        }
        Memory buffer = new Memory((pulLen.getValue() + 1) * charToBytes);
        buffer.clear();
        int ret2 = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, buffer, pulLen.getValue(), 0);
        if (ret2 == 26) {
            int ret3 = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
            if (ret3 != 0) {
                throw new Cfgmgr32Exception(ret3);
            }
            buffer = new Memory((pulLen.getValue() + 1) * charToBytes);
            buffer.clear();
            ret2 = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, buffer, pulLen.getValue(), 0);
        }
        if (ret2 != 0) {
            throw new Cfgmgr32Exception(ret2);
        }
        if (charToBytes == 1) {
            return buffer.getString(0L);
        }
        return buffer.getWideString(0L);
    }

    public static Object CM_Get_DevNode_Registry_Property(int devInst, int ulProperty) throws Cfgmgr32Exception {
        IntByReference size = new IntByReference();
        IntByReference type = new IntByReference();
        int ret = Cfgmgr32.INSTANCE.CM_Get_DevNode_Registry_Property(devInst, ulProperty, type, null, size, 0);
        if (ret == 37) {
            return null;
        }
        if (ret != 26) {
            throw new Cfgmgr32Exception(ret);
        }
        Memory buffer = null;
        if (size.getValue() > 0) {
            buffer = new Memory(size.getValue());
            int ret2 = Cfgmgr32.INSTANCE.CM_Get_DevNode_Registry_Property(devInst, ulProperty, type, buffer, size, 0);
            if (ret2 != 0) {
                throw new Cfgmgr32Exception(ret2);
            }
        }
        switch (type.getValue()) {
            case 0:
                return null;
            case 1:
                if (buffer == null) {
                    return "";
                }
                return W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? buffer.getWideString(0L) : buffer.getString(0L);
            case 2:
            case 3:
            case 5:
            case 6:
            default:
                if (buffer == null) {
                    return new byte[0];
                }
                return buffer.getByteArray(0L, (int) buffer.size());
            case 4:
                if (buffer == null) {
                    return 0;
                }
                return Integer.valueOf(buffer.getInt(0L));
            case 7:
                if (buffer == null) {
                    return new String[0];
                }
                return Advapi32Util.regMultiSzBufferToStringArray(buffer);
        }
    }
}
