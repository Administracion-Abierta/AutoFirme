package com.sun.jna.platform.win32;

import com.sun.jna.IntegerType;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.TypeComp;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.PointerByReference;
import es.gob.afirma.standalone.CommandLineParameters;
import java.io.Closeable;
import java.util.Date;
import org.mozilla.javascript.ES6Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl.class */
public interface OaIdl {
    public static final long DATE_OFFSET = new Date(-1, 11, 30, 0, 0, 0).getTime();
    public static final DISPID DISPID_COLLECT = new DISPID(-8);
    public static final DISPID DISPID_CONSTRUCTOR = new DISPID(-6);
    public static final DISPID DISPID_DESTRUCTOR = new DISPID(-7);
    public static final DISPID DISPID_EVALUATE = new DISPID(-5);
    public static final DISPID DISPID_NEWENUM = new DISPID(-4);
    public static final DISPID DISPID_PROPERTYPUT = new DISPID(-3);
    public static final DISPID DISPID_UNKNOWN = new DISPID(-1);
    public static final DISPID DISPID_VALUE = new DISPID(0);
    public static final MEMBERID MEMBERID_NIL = new MEMBERID(DISPID_UNKNOWN.intValue());
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

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$EXCEPINFO.class */
    @Structure.FieldOrder({"wCode", "wReserved", "bstrSource", "bstrDescription", "bstrHelpFile", "dwHelpContext", "pvReserved", "pfnDeferredFillIn", "scode"})
    public static class EXCEPINFO extends Structure {
        public WinDef.WORD wCode;
        public WinDef.WORD wReserved;
        public WTypes.BSTR bstrSource;
        public WTypes.BSTR bstrDescription;
        public WTypes.BSTR bstrHelpFile;
        public WinDef.DWORD dwHelpContext;
        public WinDef.PVOID pvReserved;
        public ByReference pfnDeferredFillIn;
        public WinDef.SCODE scode;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$EXCEPINFO$ByReference.class */
        public static class ByReference extends EXCEPINFO implements Structure.ByReference {
        }

        public EXCEPINFO() {
        }

        public EXCEPINFO(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$VARIANT_BOOL.class */
    public static class VARIANT_BOOL extends IntegerType {
        private static final long serialVersionUID = 1;
        public static final int SIZE = 2;

        public VARIANT_BOOL() {
            this(0L);
        }

        public VARIANT_BOOL(long value) {
            super(2, value);
        }

        public VARIANT_BOOL(boolean value) {
            this(value ? 65535L : 0L);
        }

        public boolean booleanValue() {
            return shortValue() != 0;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$_VARIANT_BOOL.class */
    public static class _VARIANT_BOOL extends VARIANT_BOOL {
        private static final long serialVersionUID = 1;

        public _VARIANT_BOOL() {
            this(0L);
        }

        public _VARIANT_BOOL(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$VARIANT_BOOLByReference.class */
    public static class VARIANT_BOOLByReference extends ByReference {
        public VARIANT_BOOLByReference() {
            this(new VARIANT_BOOL(0L));
        }

        public VARIANT_BOOLByReference(VARIANT_BOOL value) {
            super(2);
            setValue(value);
        }

        public void setValue(VARIANT_BOOL value) {
            getPointer().setShort(0L, value.shortValue());
        }

        public VARIANT_BOOL getValue() {
            return new VARIANT_BOOL(getPointer().getShort(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$_VARIANT_BOOLByReference.class */
    public static class _VARIANT_BOOLByReference extends ByReference {
        public _VARIANT_BOOLByReference() {
            this(new VARIANT_BOOL(0L));
        }

        public _VARIANT_BOOLByReference(VARIANT_BOOL value) {
            super(2);
            setValue(value);
        }

        public void setValue(VARIANT_BOOL value) {
            getPointer().setShort(0L, value.shortValue());
        }

        public VARIANT_BOOL getValue() {
            return new VARIANT_BOOL(getPointer().getShort(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DATE.class */
    @Structure.FieldOrder({"date"})
    public static class DATE extends Structure {
        private static final long MICRO_SECONDS_PER_DAY = 86400000;
        public double date;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DATE$ByReference.class */
        public static class ByReference extends DATE implements Structure.ByReference {
        }

        public DATE() {
        }

        public DATE(double date) {
            this.date = date;
        }

        public DATE(Date javaDate) {
            setFromJavaDate(javaDate);
        }

        public Date getAsJavaDate() {
            long days = (((long) this.date) * 86400000) + OaIdl.DATE_OFFSET;
            double timePart = 24.0d * Math.abs(this.date - ((long) this.date));
            int hours = (int) timePart;
            double timePart2 = 60.0d * (timePart - ((double) ((int) timePart)));
            int minutes = (int) timePart2;
            double timePart3 = 60.0d * (timePart2 - ((double) ((int) timePart2)));
            int seconds = (int) timePart3;
            int milliseconds = (int) (1000.0d * (timePart3 - ((double) ((int) timePart3))));
            Date baseDate = new Date(days);
            baseDate.setHours(hours);
            baseDate.setMinutes(minutes);
            baseDate.setSeconds(seconds);
            baseDate.setTime(baseDate.getTime() + ((long) milliseconds));
            return baseDate;
        }

        public void setFromJavaDate(Date javaDate) {
            double msSinceOrigin = javaDate.getTime() - OaIdl.DATE_OFFSET;
            double daysAsFract = msSinceOrigin / 8.64E7d;
            Date dayDate = new Date(javaDate.getTime());
            dayDate.setHours(0);
            dayDate.setMinutes(0);
            dayDate.setSeconds(0);
            dayDate.setTime((dayDate.getTime() / 1000) * 1000);
            double integralPart = Math.floor(daysAsFract);
            double fractionalPart = Math.signum(daysAsFract) * ((javaDate.getTime() - dayDate.getTime()) / 8.64E7d);
            this.date = integralPart + fractionalPart;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DISPID.class */
    public static class DISPID extends WinDef.LONG {
        private static final long serialVersionUID = 1;

        public DISPID() {
            this(0);
        }

        public DISPID(int value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DISPIDByReference.class */
    public static class DISPIDByReference extends ByReference {
        public DISPIDByReference() {
            this(new DISPID(0));
        }

        public DISPIDByReference(DISPID value) {
            super(DISPID.SIZE);
            setValue(value);
        }

        public void setValue(DISPID value) {
            getPointer().setInt(0L, value.intValue());
        }

        public DISPID getValue() {
            return new DISPID(getPointer().getInt(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$MEMBERID.class */
    public static class MEMBERID extends DISPID {
        private static final long serialVersionUID = 1;

        public MEMBERID() {
            this(0);
        }

        public MEMBERID(int value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$MEMBERIDByReference.class */
    public static class MEMBERIDByReference extends ByReference {
        public MEMBERIDByReference() {
            this(new MEMBERID(0));
        }

        public MEMBERIDByReference(MEMBERID value) {
            super(MEMBERID.SIZE);
            setValue(value);
        }

        public void setValue(MEMBERID value) {
            getPointer().setInt(0L, value.intValue());
        }

        public MEMBERID getValue() {
            return new MEMBERID(getPointer().getInt(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$TYPEKIND.class */
    @Structure.FieldOrder({ES6Iterator.VALUE_PROPERTY})
    public static class TYPEKIND extends Structure {
        public int value;
        public static final int TKIND_ENUM = 0;
        public static final int TKIND_RECORD = 1;
        public static final int TKIND_MODULE = 2;
        public static final int TKIND_INTERFACE = 3;
        public static final int TKIND_DISPATCH = 4;
        public static final int TKIND_COCLASS = 5;
        public static final int TKIND_ALIAS = 6;
        public static final int TKIND_UNION = 7;
        public static final int TKIND_MAX = 8;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$TYPEKIND$ByReference.class */
        public static class ByReference extends TYPEKIND implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(int value) {
                super(value);
            }

            public ByReference(TYPEKIND typekind) {
                super(typekind.getPointer());
                this.value = typekind.value;
            }
        }

        public TYPEKIND() {
        }

        public TYPEKIND(int value) {
            this.value = value;
        }

        public TYPEKIND(Pointer pointer) {
            super(pointer);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DESCKIND.class */
    @Structure.FieldOrder({ES6Iterator.VALUE_PROPERTY})
    public static class DESCKIND extends Structure {
        public int value;
        public static final int DESCKIND_NONE = 0;
        public static final int DESCKIND_FUNCDESC = 1;
        public static final int DESCKIND_VARDESC = 2;
        public static final int DESCKIND_TYPECOMP = 3;
        public static final int DESCKIND_IMPLICITAPPOBJ = 4;
        public static final int DESCKIND_MAX = 5;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DESCKIND$ByReference.class */
        public static class ByReference extends DESCKIND implements Structure.ByReference {
        }

        public DESCKIND() {
        }

        public DESCKIND(int value) {
            this.value = value;
        }

        public DESCKIND(Pointer pointer) {
            super(pointer);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$SAFEARRAY.class */
    @Structure.FieldOrder({"cDims", "fFeatures", "cbElements", "cLocks", "pvData", "rgsabound"})
    public static class SAFEARRAY extends Structure implements Closeable {
        public WinDef.USHORT cDims;
        public WinDef.USHORT fFeatures;
        public WinDef.ULONG cbElements;
        public WinDef.ULONG cLocks;
        public WinDef.PVOID pvData;
        public SAFEARRAYBOUND[] rgsabound;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$SAFEARRAY$ByReference.class */
        public static class ByReference extends SAFEARRAY implements Structure.ByReference {
        }

        public SAFEARRAY() {
            this.rgsabound = new SAFEARRAYBOUND[]{new SAFEARRAYBOUND()};
        }

        public SAFEARRAY(Pointer pointer) {
            super(pointer);
            this.rgsabound = new SAFEARRAYBOUND[]{new SAFEARRAYBOUND()};
            read();
        }

        @Override // com.sun.jna.Structure
        public void read() {
            super.read();
            if (this.cDims.intValue() > 0) {
                this.rgsabound = (SAFEARRAYBOUND[]) this.rgsabound[0].toArray(this.cDims.intValue());
            } else {
                this.rgsabound = new SAFEARRAYBOUND[]{new SAFEARRAYBOUND()};
            }
        }

        public static ByReference createSafeArray(int... size) {
            return createSafeArray(new WTypes.VARTYPE(12), size);
        }

        public static ByReference createSafeArray(WTypes.VARTYPE vartype, int... size) {
            SAFEARRAYBOUND[] rgsabound = (SAFEARRAYBOUND[]) new SAFEARRAYBOUND().toArray(size.length);
            for (int i = 0; i < size.length; i++) {
                rgsabound[i].lLbound = new WinDef.LONG(0L);
                rgsabound[i].cElements = new WinDef.ULONG(size[(size.length - i) - 1]);
            }
            ByReference data = OleAuto.INSTANCE.SafeArrayCreate(vartype, new WinDef.UINT(size.length), rgsabound);
            return data;
        }

        public void putElement(Object arg, int... indices) {
            WinDef.LONG[] paramIndices = new WinDef.LONG[indices.length];
            for (int i = 0; i < indices.length; i++) {
                paramIndices[i] = new WinDef.LONG(indices[(indices.length - i) - 1]);
            }
            switch (getVarType().intValue()) {
                case 2:
                case 18:
                    Memory mem = new Memory(2L);
                    mem.setShort(0L, ((Number) arg).shortValue());
                    WinNT.HRESULT hr = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, mem);
                    COMUtils.checkRC(hr);
                    return;
                case 3:
                case 19:
                case 22:
                case 23:
                    Memory mem2 = new Memory(4L);
                    mem2.setInt(0L, ((Number) arg).intValue());
                    WinNT.HRESULT hr2 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, mem2);
                    COMUtils.checkRC(hr2);
                    return;
                case 4:
                    Memory mem3 = new Memory(4L);
                    mem3.setFloat(0L, ((Number) arg).floatValue());
                    WinNT.HRESULT hr3 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, mem3);
                    COMUtils.checkRC(hr3);
                    return;
                case 5:
                    Memory mem4 = new Memory(8L);
                    mem4.setDouble(0L, ((Number) arg).doubleValue());
                    WinNT.HRESULT hr4 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, mem4);
                    COMUtils.checkRC(hr4);
                    return;
                case 6:
                    WinNT.HRESULT hr5 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((CURRENCY) arg).getPointer());
                    COMUtils.checkRC(hr5);
                    return;
                case 7:
                    Memory mem5 = new Memory(8L);
                    mem5.setDouble(0L, ((DATE) arg).date);
                    WinNT.HRESULT hr6 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, mem5);
                    COMUtils.checkRC(hr6);
                    return;
                case 8:
                    if (arg instanceof String) {
                        WTypes.BSTR bstr = OleAuto.INSTANCE.SysAllocString((String) arg);
                        WinNT.HRESULT hr7 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, bstr.getPointer());
                        OleAuto.INSTANCE.SysFreeString(bstr);
                        COMUtils.checkRC(hr7);
                        return;
                    }
                    WinNT.HRESULT hr8 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((WTypes.BSTR) arg).getPointer());
                    COMUtils.checkRC(hr8);
                    return;
                case 9:
                    WinNT.HRESULT hr9 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((Dispatch) arg).getPointer());
                    COMUtils.checkRC(hr9);
                    return;
                case 10:
                    Memory mem6 = new Memory(4L);
                    mem6.setInt(0L, ((Number) arg).intValue());
                    WinNT.HRESULT hr10 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, mem6);
                    COMUtils.checkRC(hr10);
                    return;
                case 11:
                    Memory mem7 = new Memory(2L);
                    if (arg instanceof Boolean) {
                        mem7.setShort(0L, (short) (((Boolean) arg).booleanValue() ? 65535 : 0));
                    } else {
                        mem7.setShort(0L, (short) (((Number) arg).intValue() > 0 ? 65535 : 0));
                    }
                    WinNT.HRESULT hr11 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, mem7);
                    COMUtils.checkRC(hr11);
                    return;
                case 12:
                    WinNT.HRESULT hr12 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((Variant.VARIANT) arg).getPointer());
                    COMUtils.checkRC(hr12);
                    return;
                case 13:
                    WinNT.HRESULT hr13 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((Unknown) arg).getPointer());
                    COMUtils.checkRC(hr13);
                    return;
                case 14:
                    WinNT.HRESULT hr14 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, ((DECIMAL) arg).getPointer());
                    COMUtils.checkRC(hr14);
                    return;
                case 15:
                case 20:
                case 21:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                default:
                    throw new IllegalStateException("Can't parse array content - type not supported: " + getVarType().intValue());
                case 16:
                case 17:
                    Memory mem8 = new Memory(1L);
                    mem8.setByte(0L, ((Number) arg).byteValue());
                    WinNT.HRESULT hr15 = OleAuto.INSTANCE.SafeArrayPutElement(this, paramIndices, mem8);
                    COMUtils.checkRC(hr15);
                    return;
            }
        }

        public Object getElement(int... indices) {
            Object result;
            WinDef.LONG[] paramIndices = new WinDef.LONG[indices.length];
            for (int i = 0; i < indices.length; i++) {
                paramIndices[i] = new WinDef.LONG(indices[(indices.length - i) - 1]);
            }
            switch (getVarType().intValue()) {
                case 2:
                case 18:
                    Memory mem = new Memory(2L);
                    WinNT.HRESULT hr = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, mem);
                    COMUtils.checkRC(hr);
                    result = Short.valueOf(mem.getShort(0L));
                    break;
                case 3:
                case 19:
                case 22:
                case 23:
                    Memory mem2 = new Memory(4L);
                    WinNT.HRESULT hr2 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, mem2);
                    COMUtils.checkRC(hr2);
                    result = Integer.valueOf(mem2.getInt(0L));
                    break;
                case 4:
                    Memory mem3 = new Memory(4L);
                    WinNT.HRESULT hr3 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, mem3);
                    COMUtils.checkRC(hr3);
                    result = Float.valueOf(mem3.getFloat(0L));
                    break;
                case 5:
                    Memory mem4 = new Memory(8L);
                    WinNT.HRESULT hr4 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, mem4);
                    COMUtils.checkRC(hr4);
                    result = Double.valueOf(mem4.getDouble(0L));
                    break;
                case 6:
                    CURRENCY currency = new CURRENCY();
                    WinNT.HRESULT hr5 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, currency.getPointer());
                    COMUtils.checkRC(hr5);
                    result = currency;
                    break;
                case 7:
                    Memory mem5 = new Memory(8L);
                    WinNT.HRESULT hr6 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, mem5);
                    COMUtils.checkRC(hr6);
                    result = new DATE(mem5.getDouble(0L));
                    break;
                case 8:
                    PointerByReference pbr = new PointerByReference();
                    WinNT.HRESULT hr7 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, pbr.getPointer());
                    COMUtils.checkRC(hr7);
                    WTypes.BSTR bstr = new WTypes.BSTR(pbr.getValue());
                    result = bstr.getValue();
                    OleAuto.INSTANCE.SysFreeString(bstr);
                    break;
                case 9:
                    PointerByReference pbr2 = new PointerByReference();
                    WinNT.HRESULT hr8 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, pbr2.getPointer());
                    COMUtils.checkRC(hr8);
                    result = new Dispatch(pbr2.getValue());
                    break;
                case 10:
                    Memory mem6 = new Memory(4L);
                    WinNT.HRESULT hr9 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, mem6);
                    COMUtils.checkRC(hr9);
                    result = new WinDef.SCODE(mem6.getInt(0L));
                    break;
                case 11:
                    Memory mem7 = new Memory(2L);
                    WinNT.HRESULT hr10 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, mem7);
                    COMUtils.checkRC(hr10);
                    result = Boolean.valueOf(mem7.getShort(0L) != 0);
                    break;
                case 12:
                    Variant.VARIANT holder = new Variant.VARIANT();
                    WinNT.HRESULT hr11 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, holder.getPointer());
                    COMUtils.checkRC(hr11);
                    result = holder;
                    break;
                case 13:
                    PointerByReference pbr3 = new PointerByReference();
                    WinNT.HRESULT hr12 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, pbr3.getPointer());
                    COMUtils.checkRC(hr12);
                    result = new Unknown(pbr3.getValue());
                    break;
                case 14:
                    DECIMAL decimal = new DECIMAL();
                    WinNT.HRESULT hr13 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, decimal.getPointer());
                    COMUtils.checkRC(hr13);
                    result = decimal;
                    break;
                case 15:
                case 20:
                case 21:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                default:
                    throw new IllegalStateException("Can't parse array content - type not supported: " + getVarType().intValue());
                case 16:
                case 17:
                    Memory mem8 = new Memory(1L);
                    WinNT.HRESULT hr14 = OleAuto.INSTANCE.SafeArrayGetElement(this, paramIndices, mem8);
                    COMUtils.checkRC(hr14);
                    result = Byte.valueOf(mem8.getByte(0L));
                    break;
            }
            return result;
        }

        public Pointer ptrOfIndex(int... indices) {
            WinDef.LONG[] paramIndices = new WinDef.LONG[indices.length];
            for (int i = 0; i < indices.length; i++) {
                paramIndices[i] = new WinDef.LONG(indices[(indices.length - i) - 1]);
            }
            PointerByReference pbr = new PointerByReference();
            WinNT.HRESULT hr = OleAuto.INSTANCE.SafeArrayPtrOfIndex(this, paramIndices, pbr);
            COMUtils.checkRC(hr);
            return pbr.getValue();
        }

        public void destroy() {
            WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayDestroy(this);
            COMUtils.checkRC(res);
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            destroy();
        }

        public int getLBound(int dimension) {
            int targetDimension = getDimensionCount() - dimension;
            WinDef.LONGByReference bound = new WinDef.LONGByReference();
            WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayGetLBound(this, new WinDef.UINT(targetDimension), bound);
            COMUtils.checkRC(res);
            return bound.getValue().intValue();
        }

        public int getUBound(int dimension) {
            int targetDimension = getDimensionCount() - dimension;
            WinDef.LONGByReference bound = new WinDef.LONGByReference();
            WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayGetUBound(this, new WinDef.UINT(targetDimension), bound);
            COMUtils.checkRC(res);
            return bound.getValue().intValue();
        }

        public int getDimensionCount() {
            return OleAuto.INSTANCE.SafeArrayGetDim(this).intValue();
        }

        public Pointer accessData() {
            PointerByReference pbr = new PointerByReference();
            WinNT.HRESULT hr = OleAuto.INSTANCE.SafeArrayAccessData(this, pbr);
            COMUtils.checkRC(hr);
            return pbr.getValue();
        }

        public void unaccessData() {
            WinNT.HRESULT hr = OleAuto.INSTANCE.SafeArrayUnaccessData(this);
            COMUtils.checkRC(hr);
        }

        public void lock() {
            WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayLock(this);
            COMUtils.checkRC(res);
        }

        public void unlock() {
            WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayUnlock(this);
            COMUtils.checkRC(res);
        }

        public void redim(int cElements, int lLbound) {
            WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayRedim(this, new SAFEARRAYBOUND(cElements, lLbound));
            COMUtils.checkRC(res);
        }

        public WTypes.VARTYPE getVarType() {
            WTypes.VARTYPEByReference resultHolder = new WTypes.VARTYPEByReference();
            WinNT.HRESULT res = OleAuto.INSTANCE.SafeArrayGetVartype(this, resultHolder);
            COMUtils.checkRC(res);
            return resultHolder.getValue();
        }

        public long getElemsize() {
            return OleAuto.INSTANCE.SafeArrayGetElemsize(this).longValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$SAFEARRAYByReference.class */
    @Structure.FieldOrder({"pSAFEARRAY"})
    public static class SAFEARRAYByReference extends Structure implements Structure.ByReference {
        public SAFEARRAY.ByReference pSAFEARRAY;

        public SAFEARRAYByReference() {
        }

        public SAFEARRAYByReference(Pointer p) {
            super(p);
            read();
        }

        public SAFEARRAYByReference(SAFEARRAY.ByReference safeArray) {
            this.pSAFEARRAY = safeArray;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$SAFEARRAYBOUND.class */
    @Structure.FieldOrder({"cElements", "lLbound"})
    public static class SAFEARRAYBOUND extends Structure {
        public WinDef.ULONG cElements;
        public WinDef.LONG lLbound;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$SAFEARRAYBOUND$ByReference.class */
        public static class ByReference extends SAFEARRAYBOUND implements Structure.ByReference {
        }

        public SAFEARRAYBOUND() {
        }

        public SAFEARRAYBOUND(Pointer pointer) {
            super(pointer);
            read();
        }

        public SAFEARRAYBOUND(int cElements, int lLbound) {
            this.cElements = new WinDef.ULONG(cElements);
            this.lLbound = new WinDef.LONG(lLbound);
            write();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$CURRENCY.class */
    public static class CURRENCY extends Union {
        public _CURRENCY currency;
        public WinDef.LONGLONG int64;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$CURRENCY$ByReference.class */
        public static class ByReference extends CURRENCY implements Structure.ByReference {
        }

        public CURRENCY() {
        }

        public CURRENCY(Pointer pointer) {
            super(pointer);
            read();
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$CURRENCY$_CURRENCY.class */
        @Structure.FieldOrder({"Lo", "Hi"})
        public static class _CURRENCY extends Structure {
            public WinDef.ULONG Lo;
            public WinDef.LONG Hi;

            public _CURRENCY() {
            }

            public _CURRENCY(Pointer pointer) {
                super(pointer);
                read();
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DECIMAL.class */
    @Structure.FieldOrder({"wReserved", "decimal1", "Hi32", "decimal2"})
    public static class DECIMAL extends Structure {
        public short wReserved;
        public _DECIMAL1 decimal1;
        public NativeLong Hi32;
        public _DECIMAL2 decimal2;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DECIMAL$ByReference.class */
        public static class ByReference extends DECIMAL implements Structure.ByReference {
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DECIMAL$_DECIMAL1.class */
        public static class _DECIMAL1 extends Union {
            public WinDef.USHORT signscale;
            public _DECIMAL1_DECIMAL decimal1_DECIMAL;

            public _DECIMAL1() {
                setType("signscale");
            }

            public _DECIMAL1(Pointer pointer) {
                super(pointer);
                setType("signscale");
                read();
            }

            /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DECIMAL$_DECIMAL1$_DECIMAL1_DECIMAL.class */
            @Structure.FieldOrder({"scale", CommandLineParameters.MASSIVE_OP_SIGN})
            public static class _DECIMAL1_DECIMAL extends Structure {
                public WinDef.BYTE scale;
                public WinDef.BYTE sign;

                public _DECIMAL1_DECIMAL() {
                }

                public _DECIMAL1_DECIMAL(Pointer pointer) {
                    super(pointer);
                }
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DECIMAL$_DECIMAL2.class */
        public static class _DECIMAL2 extends Union {
            public WinDef.ULONGLONG Lo64;
            public _DECIMAL2_DECIMAL decimal2_DECIMAL;

            public _DECIMAL2() {
                setType("Lo64");
            }

            public _DECIMAL2(Pointer pointer) {
                super(pointer);
                setType("Lo64");
                read();
            }

            /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$DECIMAL$_DECIMAL2$_DECIMAL2_DECIMAL.class */
            @Structure.FieldOrder({"Lo32", "Mid32"})
            public static class _DECIMAL2_DECIMAL extends Structure {
                public WinDef.BYTE Lo32;
                public WinDef.BYTE Mid32;

                public _DECIMAL2_DECIMAL() {
                }

                public _DECIMAL2_DECIMAL(Pointer pointer) {
                    super(pointer);
                }
            }
        }

        public DECIMAL() {
        }

        public DECIMAL(Pointer pointer) {
            super(pointer);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$SYSKIND.class */
    @Structure.FieldOrder({ES6Iterator.VALUE_PROPERTY})
    public static class SYSKIND extends Structure {
        public int value;
        public static final int SYS_WIN16 = 0;
        public static final int SYS_WIN32 = 1;
        public static final int SYS_MAC = 2;
        public static final int SYS_WIN64 = 3;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$SYSKIND$ByReference.class */
        public static class ByReference extends SYSKIND implements Structure.ByReference {
        }

        public SYSKIND() {
        }

        public SYSKIND(int value) {
            this.value = value;
        }

        public SYSKIND(Pointer pointer) {
            super(pointer);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$LIBFLAGS.class */
    @Structure.FieldOrder({ES6Iterator.VALUE_PROPERTY})
    public static class LIBFLAGS extends Structure {
        public int value;
        public static final int LIBFLAG_FRESTRICTED = 1;
        public static final int LIBFLAG_FCONTROL = 2;
        public static final int LIBFLAG_FHIDDEN = 4;
        public static final int LIBFLAG_FHASDISKIMAGE = 8;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$LIBFLAGS$ByReference.class */
        public static class ByReference extends LIBFLAGS implements Structure.ByReference {
        }

        public LIBFLAGS() {
        }

        public LIBFLAGS(int value) {
            this.value = value;
        }

        public LIBFLAGS(Pointer pointer) {
            super(pointer);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$TLIBATTR.class */
    @Structure.FieldOrder({"guid", "lcid", "syskind", "wMajorVerNum", "wMinorVerNum", "wLibFlags"})
    public static class TLIBATTR extends Structure {
        public Guid.GUID guid;
        public WinDef.LCID lcid;
        public SYSKIND syskind;
        public WinDef.WORD wMajorVerNum;
        public WinDef.WORD wMinorVerNum;
        public WinDef.WORD wLibFlags;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$TLIBATTR$ByReference.class */
        public static class ByReference extends TLIBATTR implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer pointer) {
                super(pointer);
                read();
            }
        }

        public TLIBATTR() {
        }

        public TLIBATTR(Pointer pointer) {
            super(pointer);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$BINDPTR.class */
    public static class BINDPTR extends Union {
        public FUNCDESC lpfuncdesc;
        public VARDESC lpvardesc;
        public TypeComp lptcomp;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$BINDPTR$ByReference.class */
        public static class ByReference extends BINDPTR implements Structure.ByReference {
        }

        public BINDPTR() {
        }

        public BINDPTR(VARDESC lpvardesc) {
            this.lpvardesc = lpvardesc;
            setType(VARDESC.class);
        }

        public BINDPTR(TypeComp lptcomp) {
            this.lptcomp = lptcomp;
            setType(TypeComp.class);
        }

        public BINDPTR(FUNCDESC lpfuncdesc) {
            this.lpfuncdesc = lpfuncdesc;
            setType(FUNCDESC.class);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$FUNCDESC.class */
    @Structure.FieldOrder({"memid", "lprgscode", "lprgelemdescParam", "funckind", "invkind", "callconv", "cParams", "cParamsOpt", "oVft", "cScodes", "elemdescFunc", "wFuncFlags"})
    public static class FUNCDESC extends Structure {
        public MEMBERID memid;
        public ScodeArg.ByReference lprgscode;
        public ElemDescArg.ByReference lprgelemdescParam;
        public FUNCKIND funckind;
        public INVOKEKIND invkind;
        public CALLCONV callconv;
        public WinDef.SHORT cParams;
        public WinDef.SHORT cParamsOpt;
        public WinDef.SHORT oVft;
        public WinDef.SHORT cScodes;
        public ELEMDESC elemdescFunc;
        public WinDef.WORD wFuncFlags;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$FUNCDESC$ByReference.class */
        public static class ByReference extends FUNCDESC implements Structure.ByReference {
        }

        public FUNCDESC() {
        }

        public FUNCDESC(Pointer pointer) {
            super(pointer);
            read();
            if (this.cParams.shortValue() > 1) {
                this.lprgelemdescParam.elemDescArg = new ELEMDESC[this.cParams.shortValue()];
                this.lprgelemdescParam.read();
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$ElemDescArg.class */
    @Structure.FieldOrder({"elemDescArg"})
    public static class ElemDescArg extends Structure {
        public ELEMDESC[] elemDescArg;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$ElemDescArg$ByReference.class */
        public static class ByReference extends ElemDescArg implements Structure.ByReference {
        }

        public ElemDescArg() {
            this.elemDescArg = new ELEMDESC[]{new ELEMDESC()};
        }

        public ElemDescArg(Pointer pointer) {
            super(pointer);
            this.elemDescArg = new ELEMDESC[]{new ELEMDESC()};
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$ScodeArg.class */
    @Structure.FieldOrder({"scodeArg"})
    public static class ScodeArg extends Structure {
        public WinDef.SCODE[] scodeArg;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$ScodeArg$ByReference.class */
        public static class ByReference extends ScodeArg implements Structure.ByReference {
        }

        public ScodeArg() {
            this.scodeArg = new WinDef.SCODE[]{new WinDef.SCODE()};
        }

        public ScodeArg(Pointer pointer) {
            super(pointer);
            this.scodeArg = new WinDef.SCODE[]{new WinDef.SCODE()};
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$VARDESC.class */
    @Structure.FieldOrder({"memid", "lpstrSchema", "_vardesc", "elemdescVar", "wVarFlags", "varkind"})
    public static class VARDESC extends Structure {
        public MEMBERID memid;
        public WTypes.LPOLESTR lpstrSchema;
        public _VARDESC _vardesc;
        public ELEMDESC elemdescVar;
        public WinDef.WORD wVarFlags;
        public VARKIND varkind;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$VARDESC$ByReference.class */
        public static class ByReference extends VARDESC implements Structure.ByReference {
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$VARDESC$_VARDESC.class */
        public static class _VARDESC extends Union {
            public NativeLong oInst;
            public Variant.VARIANT.ByReference lpvarValue;

            /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$VARDESC$_VARDESC$ByReference.class */
            public static class ByReference extends _VARDESC implements Structure.ByReference {
            }

            public _VARDESC() {
                setType("lpvarValue");
                read();
            }

            public _VARDESC(Pointer pointer) {
                super(pointer);
                setType("lpvarValue");
                read();
            }

            public _VARDESC(Variant.VARIANT.ByReference lpvarValue) {
                this.lpvarValue = lpvarValue;
                setType("lpvarValue");
            }

            public _VARDESC(NativeLong oInst) {
                this.oInst = oInst;
                setType("oInst");
            }
        }

        public VARDESC() {
        }

        public VARDESC(Pointer pointer) {
            super(pointer);
            this._vardesc.setType("lpvarValue");
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$ELEMDESC.class */
    @Structure.FieldOrder({"tdesc", "_elemdesc"})
    public static class ELEMDESC extends Structure {
        public TYPEDESC tdesc;
        public _ELEMDESC _elemdesc;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$ELEMDESC$ByReference.class */
        public static class ByReference extends ELEMDESC implements Structure.ByReference {
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$ELEMDESC$_ELEMDESC.class */
        public static class _ELEMDESC extends Union {
            public IDLDESC idldesc;
            public PARAMDESC paramdesc;

            /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$ELEMDESC$_ELEMDESC$ByReference.class */
            public static class ByReference extends _ELEMDESC implements Structure.ByReference {
            }

            public _ELEMDESC() {
            }

            public _ELEMDESC(Pointer pointer) {
                super(pointer);
                setType("paramdesc");
                read();
            }

            public _ELEMDESC(PARAMDESC paramdesc) {
                this.paramdesc = paramdesc;
                setType("paramdesc");
            }

            public _ELEMDESC(IDLDESC idldesc) {
                this.idldesc = idldesc;
                setType("idldesc");
            }
        }

        public ELEMDESC() {
        }

        public ELEMDESC(Pointer pointer) {
            super(pointer);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$FUNCKIND.class */
    @Structure.FieldOrder({ES6Iterator.VALUE_PROPERTY})
    public static class FUNCKIND extends Structure {
        public static final int FUNC_VIRTUAL = 0;
        public static final int FUNC_PUREVIRTUAL = 1;
        public static final int FUNC_NONVIRTUAL = 2;
        public static final int FUNC_STATIC = 3;
        public static final int FUNC_DISPATCH = 4;
        public int value;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$FUNCKIND$ByReference.class */
        public static class ByReference extends FUNCKIND implements Structure.ByReference {
        }

        public FUNCKIND() {
        }

        public FUNCKIND(int value) {
            this.value = value;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$INVOKEKIND.class */
    @Structure.FieldOrder({ES6Iterator.VALUE_PROPERTY})
    public static class INVOKEKIND extends Structure {
        public static final INVOKEKIND INVOKE_FUNC = new INVOKEKIND(1);
        public static final INVOKEKIND INVOKE_PROPERTYGET = new INVOKEKIND(2);
        public static final INVOKEKIND INVOKE_PROPERTYPUT = new INVOKEKIND(4);
        public static final INVOKEKIND INVOKE_PROPERTYPUTREF = new INVOKEKIND(8);
        public int value;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$INVOKEKIND$ByReference.class */
        public static class ByReference extends INVOKEKIND implements Structure.ByReference {
        }

        public INVOKEKIND() {
        }

        public INVOKEKIND(int value) {
            this.value = value;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$CALLCONV.class */
    @Structure.FieldOrder({ES6Iterator.VALUE_PROPERTY})
    public static class CALLCONV extends Structure {
        public static final int CC_FASTCALL = 0;
        public static final int CC_CDECL = 1;
        public static final int CC_MSCPASCAL = 2;
        public static final int CC_PASCAL = 2;
        public static final int CC_MACPASCAL = 3;
        public static final int CC_STDCALL = 4;
        public static final int CC_FPFASTCALL = 5;
        public static final int CC_SYSCALL = 6;
        public static final int CC_MPWCDECL = 7;
        public static final int CC_MPWPASCAL = 8;
        public static final int CC_MAX = 9;
        public int value;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$CALLCONV$ByReference.class */
        public static class ByReference extends CALLCONV implements Structure.ByReference {
        }

        public CALLCONV() {
        }

        public CALLCONV(int value) {
            this.value = value;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$VARKIND.class */
    @Structure.FieldOrder({ES6Iterator.VALUE_PROPERTY})
    public static class VARKIND extends Structure {
        public static final int VAR_PERINSTANCE = 0;
        public static final int VAR_STATIC = 1;
        public static final int VAR_CONST = 2;
        public static final int VAR_DISPATCH = 3;
        public int value;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$VARKIND$ByReference.class */
        public static class ByReference extends VARKIND implements Structure.ByReference {
        }

        public VARKIND() {
        }

        public VARKIND(int value) {
            this.value = value;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$TYPEDESC.class */
    @Structure.FieldOrder({"_typedesc", "vt"})
    public static class TYPEDESC extends Structure {
        public _TYPEDESC _typedesc;
        public WTypes.VARTYPE vt;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$TYPEDESC$ByReference.class */
        public static class ByReference extends TYPEDESC implements Structure.ByReference {
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$TYPEDESC$_TYPEDESC.class */
        public static class _TYPEDESC extends Union {
            public ByReference lptdesc;
            public ARRAYDESC.ByReference lpadesc;
            public HREFTYPE hreftype;

            public _TYPEDESC() {
                setType("hreftype");
                read();
            }

            public _TYPEDESC(Pointer pointer) {
                super(pointer);
                setType("hreftype");
                read();
            }

            public ByReference getLptdesc() {
                setType("lptdesc");
                read();
                return this.lptdesc;
            }

            public ARRAYDESC.ByReference getLpadesc() {
                setType("lpadesc");
                read();
                return this.lpadesc;
            }

            public HREFTYPE getHreftype() {
                setType("hreftype");
                read();
                return this.hreftype;
            }
        }

        public TYPEDESC() {
            read();
        }

        public TYPEDESC(Pointer pointer) {
            super(pointer);
            read();
        }

        public TYPEDESC(_TYPEDESC _typedesc, WTypes.VARTYPE vt) {
            this._typedesc = _typedesc;
            this.vt = vt;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$IDLDESC.class */
    @Structure.FieldOrder({"dwReserved", "wIDLFlags"})
    public static class IDLDESC extends Structure {
        public BaseTSD.ULONG_PTR dwReserved;
        public WinDef.USHORT wIDLFlags;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$IDLDESC$ByReference.class */
        public static class ByReference extends IDLDESC implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(IDLDESC idldesc) {
                super(idldesc.dwReserved, idldesc.wIDLFlags);
            }
        }

        public IDLDESC() {
        }

        public IDLDESC(Pointer pointer) {
            super(pointer);
            read();
        }

        public IDLDESC(BaseTSD.ULONG_PTR dwReserved, WinDef.USHORT wIDLFlags) {
            this.dwReserved = dwReserved;
            this.wIDLFlags = wIDLFlags;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$ARRAYDESC.class */
    @Structure.FieldOrder({"tdescElem", "cDims", "rgbounds"})
    public static class ARRAYDESC extends Structure {
        public TYPEDESC tdescElem;
        public short cDims;
        public SAFEARRAYBOUND[] rgbounds;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$ARRAYDESC$ByReference.class */
        public static class ByReference extends ARRAYDESC implements Structure.ByReference {
        }

        public ARRAYDESC() {
            this.rgbounds = new SAFEARRAYBOUND[]{new SAFEARRAYBOUND()};
        }

        public ARRAYDESC(Pointer pointer) {
            super(pointer);
            this.rgbounds = new SAFEARRAYBOUND[]{new SAFEARRAYBOUND()};
            read();
        }

        public ARRAYDESC(TYPEDESC tdescElem, short cDims, SAFEARRAYBOUND[] rgbounds) {
            this.rgbounds = new SAFEARRAYBOUND[]{new SAFEARRAYBOUND()};
            this.tdescElem = tdescElem;
            this.cDims = cDims;
            if (rgbounds.length != this.rgbounds.length) {
                throw new IllegalArgumentException("Wrong array size !");
            }
            this.rgbounds = rgbounds;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$PARAMDESC.class */
    @Structure.FieldOrder({"pparamdescex", "wParamFlags"})
    public static class PARAMDESC extends Structure {
        public Pointer pparamdescex;
        public WinDef.USHORT wParamFlags;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$PARAMDESC$ByReference.class */
        public static class ByReference extends PARAMDESC implements Structure.ByReference {
        }

        public PARAMDESC() {
        }

        public PARAMDESC(Pointer pointer) {
            super(pointer);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$PARAMDESCEX.class */
    @Structure.FieldOrder({"cBytes", "varDefaultValue"})
    public static class PARAMDESCEX extends Structure {
        public WinDef.ULONG cBytes;
        public Variant.VariantArg varDefaultValue;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$PARAMDESCEX$ByReference.class */
        public static class ByReference extends PARAMDESCEX implements Structure.ByReference {
        }

        public PARAMDESCEX() {
        }

        public PARAMDESCEX(Pointer pointer) {
            super(pointer);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$HREFTYPE.class */
    public static class HREFTYPE extends WinDef.DWORD {
        private static final long serialVersionUID = 1;

        public HREFTYPE() {
        }

        public HREFTYPE(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$HREFTYPEByReference.class */
    public static class HREFTYPEByReference extends WinDef.DWORDByReference {
        public HREFTYPEByReference() {
            this(new HREFTYPE(0L));
        }

        public HREFTYPEByReference(WinDef.DWORD value) {
            super(value);
        }

        public void setValue(HREFTYPE value) {
            getPointer().setInt(0L, value.intValue());
        }

        @Override // com.sun.jna.platform.win32.WinDef.DWORDByReference
        public HREFTYPE getValue() {
            return new HREFTYPE(getPointer().getInt(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$TYPEATTR.class */
    @Structure.FieldOrder({"guid", "lcid", "dwReserved", "memidConstructor", "memidDestructor", "lpstrSchema", "cbSizeInstance", "typekind", "cFuncs", "cVars", "cImplTypes", "cbSizeVft", "cbAlignment", "wTypeFlags", "wMajorVerNum", "wMinorVerNum", "tdescAlias", "idldescType"})
    public static class TYPEATTR extends Structure {
        public Guid.GUID guid;
        public WinDef.LCID lcid;
        public WinDef.DWORD dwReserved;
        public MEMBERID memidConstructor;
        public MEMBERID memidDestructor;
        public WTypes.LPOLESTR lpstrSchema;
        public WinDef.ULONG cbSizeInstance;
        public TYPEKIND typekind;
        public WinDef.WORD cFuncs;
        public WinDef.WORD cVars;
        public WinDef.WORD cImplTypes;
        public WinDef.WORD cbSizeVft;
        public WinDef.WORD cbAlignment;
        public WinDef.WORD wTypeFlags;
        public WinDef.WORD wMajorVerNum;
        public WinDef.WORD wMinorVerNum;
        public TYPEDESC tdescAlias;
        public IDLDESC idldescType;
        public static final int TYPEFLAGS_FAPPOBJECT = 1;
        public static final int TYPEFLAGS_FCANCREATE = 2;
        public static final int TYPEFLAGS_FLICENSED = 4;
        public static final int TYPEFLAGS_FPREDECLID = 8;
        public static final int TYPEFLAGS_FHIDDEN = 16;
        public static final int TYPEFLAGS_FCONTROL = 32;
        public static final int TYPEFLAGS_FDUAL = 64;
        public static final int TYPEFLAGS_FNONEXTENSIBLE = 128;
        public static final int TYPEFLAGS_FOLEAUTOMATION = 256;
        public static final int TYPEFLAGS_FRESTRICTED = 512;
        public static final int TYPEFLAGS_FAGGREGATABLE = 1024;
        public static final int TYPEFLAGS_FREPLACEABLE = 2048;
        public static final int TYPEFLAGS_FDISPATCHABLE = 4096;
        public static final int TYPEFLAGS_FREVERSEBIND = 8192;
        public static final int TYPEFLAGS_FPROXY = 16384;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdl$TYPEATTR$ByReference.class */
        public static class ByReference extends TYPEATTR implements Structure.ByReference {
        }

        public TYPEATTR() {
        }

        public TYPEATTR(Pointer pointer) {
            super(pointer);
            read();
        }
    }
}
