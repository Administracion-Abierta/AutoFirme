package com.sun.jna.platform.win32;

import com.ctc.wstx.io.CharsetNames;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;
import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes.class */
public interface WTypes {
    public static final int CLSCTX_INPROC_SERVER = 1;
    public static final int CLSCTX_INPROC_HANDLER = 2;
    public static final int CLSCTX_LOCAL_SERVER = 4;
    public static final int CLSCTX_INPROC_SERVER16 = 8;
    public static final int CLSCTX_REMOTE_SERVER = 16;
    public static final int CLSCTX_INPROC_HANDLER16 = 32;
    public static final int CLSCTX_RESERVED1 = 64;
    public static final int CLSCTX_RESERVED2 = 128;
    public static final int CLSCTX_RESERVED3 = 256;
    public static final int CLSCTX_RESERVED4 = 512;
    public static final int CLSCTX_NO_CODE_DOWNLOAD = 1024;
    public static final int CLSCTX_RESERVED5 = 2048;
    public static final int CLSCTX_NO_CUSTOM_MARSHAL = 4096;
    public static final int CLSCTX_ENABLE_CODE_DOWNLOAD = 8192;
    public static final int CLSCTX_NO_FAILURE_LOG = 16384;
    public static final int CLSCTX_DISABLE_AAA = 32768;
    public static final int CLSCTX_ENABLE_AAA = 65536;
    public static final int CLSCTX_FROM_DEFAULT_CONTEXT = 131072;
    public static final int CLSCTX_ACTIVATE_32_BIT_SERVER = 262144;
    public static final int CLSCTX_ACTIVATE_64_BIT_SERVER = 524288;
    public static final int CLSCTX_ENABLE_CLOAKING = 1048576;
    public static final int CLSCTX_APPCONTAINER = 4194304;
    public static final int CLSCTX_ACTIVATE_AAA_AS_IU = 8388608;
    public static final int CLSCTX_PS_DLL = Integer.MIN_VALUE;
    public static final int CLSCTX_SERVER = 21;
    public static final int CLSCTX_ALL = 7;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes$BSTR.class */
    public static class BSTR extends PointerType {
        public BSTR() {
            super(Pointer.NULL);
        }

        public BSTR(Pointer pointer) {
            super(pointer);
        }

        @Deprecated
        public BSTR(String value) {
            setValue(value);
        }

        @Deprecated
        public void setValue(String value) {
            if (value == null) {
                value = "";
            }
            try {
                byte[] encodedValue = value.getBytes(CharsetNames.CS_UTF16LE);
                Memory mem = new Memory(4 + encodedValue.length + 2);
                mem.clear();
                mem.setInt(0L, encodedValue.length);
                mem.write(4L, encodedValue, 0, encodedValue.length);
                setPointer(mem.share(4L));
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException("UTF-16LE charset is not supported", ex);
            }
        }

        public String getValue() {
            try {
                Pointer pointer = getPointer();
                if (pointer == null) {
                    return "";
                }
                int stringLength = pointer.getInt(-4L);
                return new String(pointer.getByteArray(0L, stringLength), CharsetNames.CS_UTF16LE);
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException("UTF-16LE charset is not supported", ex);
            }
        }

        @Override // com.sun.jna.PointerType
        public String toString() {
            return getValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes$BSTRByReference.class */
    public static class BSTRByReference extends ByReference {
        public BSTRByReference() {
            super(Native.POINTER_SIZE);
        }

        public BSTRByReference(BSTR value) {
            this();
            setValue(value);
        }

        public void setValue(BSTR value) {
            getPointer().setPointer(0L, value.getPointer());
        }

        public BSTR getValue() {
            return new BSTR(getPointer().getPointer(0L));
        }

        public String getString() {
            BSTR b = getValue();
            if (b == null) {
                return null;
            }
            return b.getValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes$LPSTR.class */
    public static class LPSTR extends PointerType {

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes$LPSTR$ByReference.class */
        public static class ByReference extends LPSTR implements Structure.ByReference {
        }

        public LPSTR() {
            super(Pointer.NULL);
        }

        public LPSTR(Pointer pointer) {
            super(pointer);
        }

        public LPSTR(String value) {
            this(new Memory(((long) value.length()) + 1));
            setValue(value);
        }

        public void setValue(String value) {
            getPointer().setString(0L, value);
        }

        public String getValue() {
            Pointer pointer = getPointer();
            String str = null;
            if (pointer != null) {
                str = pointer.getString(0L);
            }
            return str;
        }

        @Override // com.sun.jna.PointerType
        public String toString() {
            return getValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes$LPWSTR.class */
    public static class LPWSTR extends PointerType {

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes$LPWSTR$ByReference.class */
        public static class ByReference extends LPWSTR implements Structure.ByReference {
        }

        public LPWSTR() {
            super(Pointer.NULL);
        }

        public LPWSTR(Pointer pointer) {
            super(pointer);
        }

        public LPWSTR(String value) {
            this(new Memory((((long) value.length()) + 1) * ((long) Native.WCHAR_SIZE)));
            setValue(value);
        }

        public void setValue(String value) {
            getPointer().setWideString(0L, value);
        }

        public String getValue() {
            Pointer pointer = getPointer();
            String str = null;
            if (pointer != null) {
                str = pointer.getWideString(0L);
            }
            return str;
        }

        @Override // com.sun.jna.PointerType
        public String toString() {
            return getValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes$LPOLESTR.class */
    public static class LPOLESTR extends PointerType {

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes$LPOLESTR$ByReference.class */
        public static class ByReference extends LPOLESTR implements Structure.ByReference {
        }

        public LPOLESTR() {
            super(Pointer.NULL);
        }

        public LPOLESTR(Pointer pointer) {
            super(pointer);
        }

        public LPOLESTR(String value) {
            super(new Memory((((long) value.length()) + 1) * ((long) Native.WCHAR_SIZE)));
            setValue(value);
        }

        public void setValue(String value) {
            getPointer().setWideString(0L, value);
        }

        public String getValue() {
            Pointer pointer = getPointer();
            String str = null;
            if (pointer != null) {
                str = pointer.getWideString(0L);
            }
            return str;
        }

        @Override // com.sun.jna.PointerType
        public String toString() {
            return getValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes$VARTYPE.class */
    public static class VARTYPE extends WinDef.USHORT {
        private static final long serialVersionUID = 1;

        public VARTYPE() {
            this(0);
        }

        public VARTYPE(int value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WTypes$VARTYPEByReference.class */
    public static class VARTYPEByReference extends ByReference {
        public VARTYPEByReference() {
            super(2);
        }

        public VARTYPEByReference(VARTYPE type) {
            super(2);
            setValue(type);
        }

        public VARTYPEByReference(short type) {
            super(2);
            getPointer().setShort(0L, type);
        }

        public void setValue(VARTYPE value) {
            getPointer().setShort(0L, value.shortValue());
        }

        public VARTYPE getValue() {
            return new VARTYPE(getPointer().getShort(0L));
        }
    }
}
