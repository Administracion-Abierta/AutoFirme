package com.sun.jna.platform.win32;

import com.sun.jna.Structure;
import com.sun.jna.platform.EnumUtils;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LowLevelMonitorConfigurationAPI.class */
public interface LowLevelMonitorConfigurationAPI {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LowLevelMonitorConfigurationAPI$MC_TIMING_REPORT.class */
    @Structure.FieldOrder({"dwHorizontalFrequencyInHZ", "dwVerticalFrequencyInHZ", "bTimingStatusByte"})
    public static class MC_TIMING_REPORT extends Structure {
        public WinDef.DWORD dwHorizontalFrequencyInHZ;
        public WinDef.DWORD dwVerticalFrequencyInHZ;
        public WinDef.BYTE bTimingStatusByte;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LowLevelMonitorConfigurationAPI$MC_VCP_CODE_TYPE.class */
    public enum MC_VCP_CODE_TYPE {
        MC_MOMENTARY,
        MC_SET_PARAMETER;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LowLevelMonitorConfigurationAPI$MC_VCP_CODE_TYPE$ByReference.class */
        public static class ByReference extends com.sun.jna.ptr.ByReference {
            public ByReference() {
                super(4);
            }

            public ByReference(MC_VCP_CODE_TYPE value) {
                super(4);
                setValue(value);
            }

            public void setValue(MC_VCP_CODE_TYPE value) {
                getPointer().setInt(0L, EnumUtils.toInteger(value));
            }

            public MC_VCP_CODE_TYPE getValue() {
                return (MC_VCP_CODE_TYPE) EnumUtils.fromInteger(getPointer().getInt(0L), MC_VCP_CODE_TYPE.class);
            }
        }
    }
}
