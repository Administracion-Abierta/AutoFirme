package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OleAuto.class */
public interface OleAuto extends StdCallLibrary {
    public static final OleAuto INSTANCE = (OleAuto) Native.load("OleAut32", OleAuto.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int DISPATCH_METHOD = 1;
    public static final int DISPATCH_PROPERTYGET = 2;
    public static final int DISPATCH_PROPERTYPUT = 4;
    public static final int DISPATCH_PROPERTYPUTREF = 8;
    public static final int FADF_AUTO = 1;
    public static final int FADF_STATIC = 2;
    public static final int FADF_EMBEDDED = 4;
    public static final int FADF_FIXEDSIZE = 16;
    public static final int FADF_RECORD = 32;
    public static final int FADF_HAVEIID = 64;
    public static final int FADF_HAVEVARTYPE = 128;
    public static final int FADF_BSTR = 256;
    public static final int FADF_UNKNOWN = 512;
    public static final int FADF_DISPATCH = 1024;
    public static final int FADF_VARIANT = 2048;
    public static final int FADF_RESERVED = 61448;
    public static final short VARIANT_NOVALUEPROP = 1;
    public static final short VARIANT_ALPHABOOL = 2;
    public static final short VARIANT_NOUSEROVERRIDE = 4;
    public static final short VARIANT_CALENDAR_HIJRI = 8;
    public static final short VARIANT_LOCALBOOL = 16;
    public static final short VARIANT_CALENDAR_THAI = 32;
    public static final short VARIANT_CALENDAR_GREGORIAN = 64;
    public static final short VARIANT_USE_NLS = 128;

    WTypes.BSTR SysAllocString(String str);

    void SysFreeString(WTypes.BSTR bstr);

    int SysStringByteLen(WTypes.BSTR bstr);

    int SysStringLen(WTypes.BSTR bstr);

    void VariantInit(Variant.VARIANT.ByReference byReference);

    void VariantInit(Variant.VARIANT variant);

    WinNT.HRESULT VariantCopy(Pointer pointer, Variant.VARIANT variant);

    WinNT.HRESULT VariantClear(Variant.VARIANT variant);

    WinNT.HRESULT VariantChangeType(Variant.VARIANT variant, Variant.VARIANT variant2, short s, WTypes.VARTYPE vartype);

    WinNT.HRESULT VariantChangeType(Variant.VARIANT.ByReference byReference, Variant.VARIANT.ByReference byReference2, short s, WTypes.VARTYPE vartype);

    OaIdl.SAFEARRAY.ByReference SafeArrayCreate(WTypes.VARTYPE vartype, WinDef.UINT uint, OaIdl.SAFEARRAYBOUND[] safearrayboundArr);

    WinNT.HRESULT SafeArrayPutElement(OaIdl.SAFEARRAY safearray, WinDef.LONG[] longArr, Pointer pointer);

    WinNT.HRESULT SafeArrayGetUBound(OaIdl.SAFEARRAY safearray, WinDef.UINT uint, WinDef.LONGByReference lONGByReference);

    WinNT.HRESULT SafeArrayGetLBound(OaIdl.SAFEARRAY safearray, WinDef.UINT uint, WinDef.LONGByReference lONGByReference);

    WinNT.HRESULT SafeArrayGetElement(OaIdl.SAFEARRAY safearray, WinDef.LONG[] longArr, Pointer pointer);

    WinNT.HRESULT SafeArrayPtrOfIndex(OaIdl.SAFEARRAY safearray, WinDef.LONG[] longArr, PointerByReference pointerByReference);

    WinNT.HRESULT SafeArrayLock(OaIdl.SAFEARRAY safearray);

    WinNT.HRESULT SafeArrayUnlock(OaIdl.SAFEARRAY safearray);

    WinNT.HRESULT SafeArrayDestroy(OaIdl.SAFEARRAY safearray);

    WinNT.HRESULT SafeArrayRedim(OaIdl.SAFEARRAY safearray, OaIdl.SAFEARRAYBOUND safearraybound);

    WinNT.HRESULT SafeArrayGetVartype(OaIdl.SAFEARRAY safearray, WTypes.VARTYPEByReference vARTYPEByReference);

    WinDef.UINT SafeArrayGetDim(OaIdl.SAFEARRAY safearray);

    WinNT.HRESULT SafeArrayAccessData(OaIdl.SAFEARRAY safearray, PointerByReference pointerByReference);

    WinNT.HRESULT SafeArrayUnaccessData(OaIdl.SAFEARRAY safearray);

    WinDef.UINT SafeArrayGetElemsize(OaIdl.SAFEARRAY safearray);

    WinNT.HRESULT GetActiveObject(Guid.GUID guid, WinDef.PVOID pvoid, PointerByReference pointerByReference);

    WinNT.HRESULT LoadRegTypeLib(Guid.GUID guid, int i, int i2, WinDef.LCID lcid, PointerByReference pointerByReference);

    WinNT.HRESULT LoadTypeLib(String str, PointerByReference pointerByReference);

    int SystemTimeToVariantTime(WinBase.SYSTEMTIME systemtime, DoubleByReference doubleByReference);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OleAuto$DISPPARAMS.class */
    @Structure.FieldOrder({"rgvarg", "rgdispidNamedArgs", "cArgs", "cNamedArgs"})
    public static class DISPPARAMS extends Structure {
        public Variant.VariantArg.ByReference rgvarg;
        public Pointer rgdispidNamedArgs;
        public WinDef.UINT cArgs;
        public WinDef.UINT cNamedArgs;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OleAuto$DISPPARAMS$ByReference.class */
        public static class ByReference extends DISPPARAMS implements Structure.ByReference {
        }

        public OaIdl.DISPID[] getRgdispidNamedArgs() {
            OaIdl.DISPID[] namedArgs;
            int count = this.cNamedArgs.intValue();
            if (this.rgdispidNamedArgs != null && count > 0) {
                int[] rawData = this.rgdispidNamedArgs.getIntArray(0L, count);
                namedArgs = new OaIdl.DISPID[count];
                for (int i = 0; i < count; i++) {
                    namedArgs[i] = new OaIdl.DISPID(rawData[i]);
                }
            } else {
                namedArgs = new OaIdl.DISPID[0];
            }
            return namedArgs;
        }

        public void setRgdispidNamedArgs(OaIdl.DISPID[] namedArgs) {
            if (namedArgs == null) {
                namedArgs = new OaIdl.DISPID[0];
            }
            this.cNamedArgs = new WinDef.UINT(namedArgs.length);
            this.rgdispidNamedArgs = new Memory(OaIdl.DISPID.SIZE * namedArgs.length);
            int[] rawData = new int[namedArgs.length];
            for (int i = 0; i < rawData.length; i++) {
                rawData[i] = namedArgs[i].intValue();
            }
            this.rgdispidNamedArgs.write(0L, rawData, 0, namedArgs.length);
        }

        public Variant.VARIANT[] getArgs() {
            if (this.rgvarg != null) {
                this.rgvarg.setArraySize(this.cArgs.intValue());
                return this.rgvarg.variantArg;
            }
            return new Variant.VARIANT[0];
        }

        public void setArgs(Variant.VARIANT[] arguments) {
            if (arguments == null) {
                arguments = new Variant.VARIANT[0];
            }
            this.rgvarg = new Variant.VariantArg.ByReference(arguments);
            this.cArgs = new WinDef.UINT(arguments.length);
        }

        public DISPPARAMS() {
            this.rgdispidNamedArgs = Pointer.NULL;
            this.cArgs = new WinDef.UINT(0L);
            this.cNamedArgs = new WinDef.UINT(0L);
        }

        public DISPPARAMS(Pointer memory) {
            super(memory);
            this.rgdispidNamedArgs = Pointer.NULL;
            this.cArgs = new WinDef.UINT(0L);
            this.cNamedArgs = new WinDef.UINT(0L);
            read();
        }
    }
}
