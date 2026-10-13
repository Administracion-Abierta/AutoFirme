package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/VerRsrc.class */
public interface VerRsrc {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/VerRsrc$VS_FIXEDFILEINFO.class */
    @Structure.FieldOrder({"dwSignature", "dwStrucVersion", "dwFileVersionMS", "dwFileVersionLS", "dwProductVersionMS", "dwProductVersionLS", "dwFileFlagsMask", "dwFileFlags", "dwFileOS", "dwFileType", "dwFileSubtype", "dwFileDateMS", "dwFileDateLS"})
    public static class VS_FIXEDFILEINFO extends Structure {
        public WinDef.DWORD dwSignature;
        public WinDef.DWORD dwStrucVersion;
        public WinDef.DWORD dwFileVersionMS;
        public WinDef.DWORD dwFileVersionLS;
        public WinDef.DWORD dwProductVersionMS;
        public WinDef.DWORD dwProductVersionLS;
        public WinDef.DWORD dwFileFlagsMask;
        public WinDef.DWORD dwFileFlags;
        public WinDef.DWORD dwFileOS;
        public WinDef.DWORD dwFileType;
        public WinDef.DWORD dwFileSubtype;
        public WinDef.DWORD dwFileDateMS;
        public WinDef.DWORD dwFileDateLS;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/VerRsrc$VS_FIXEDFILEINFO$ByReference.class */
        public static class ByReference extends VS_FIXEDFILEINFO implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public VS_FIXEDFILEINFO() {
        }

        public VS_FIXEDFILEINFO(Pointer memory) {
            super(memory);
            read();
        }

        public int getFileVersionMajor() {
            return this.dwFileVersionMS.intValue() >>> 16;
        }

        public int getFileVersionMinor() {
            return this.dwFileVersionMS.intValue() & 65535;
        }

        public int getFileVersionRevision() {
            return this.dwFileVersionLS.intValue() >>> 16;
        }

        public int getFileVersionBuild() {
            return this.dwFileVersionLS.intValue() & 65535;
        }

        public int getProductVersionMajor() {
            return this.dwProductVersionMS.intValue() >>> 16;
        }

        public int getProductVersionMinor() {
            return this.dwProductVersionMS.intValue() & 65535;
        }

        public int getProductVersionRevision() {
            return this.dwProductVersionLS.intValue() >>> 16;
        }

        public int getProductVersionBuild() {
            return this.dwProductVersionLS.intValue() & 65535;
        }
    }
}
