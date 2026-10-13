package com.sun.jna.platform.win32;

import com.aowagie.text.ElementTags;
import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;
import java.awt.Rectangle;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef.class */
public interface WinDef {
    public static final int MAX_PATH = 260;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HINSTANCE.class */
    public static class HINSTANCE extends WinNT.HANDLE {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HMODULE.class */
    public static class HMODULE extends HINSTANCE {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$WORD.class */
    public static class WORD extends IntegerType implements Comparable<WORD> {
        public static final int SIZE = 2;

        public WORD() {
            this(0L);
        }

        public WORD(long value) {
            super(2, value, true);
        }

        @Override // java.lang.Comparable
        public int compareTo(WORD other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$WORDByReference.class */
    public static class WORDByReference extends ByReference {
        public WORDByReference() {
            this(new WORD(0L));
        }

        public WORDByReference(WORD value) {
            super(2);
            setValue(value);
        }

        public void setValue(WORD value) {
            getPointer().setShort(0L, value.shortValue());
        }

        public WORD getValue() {
            return new WORD(getPointer().getShort(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$DWORD.class */
    public static class DWORD extends IntegerType implements Comparable<DWORD> {
        public static final int SIZE = 4;

        public DWORD() {
            this(0L);
        }

        public DWORD(long value) {
            super(4, value, true);
        }

        public WORD getLow() {
            return new WORD(longValue() & 65535);
        }

        public WORD getHigh() {
            return new WORD((longValue() >> 16) & 65535);
        }

        @Override // java.lang.Comparable
        public int compareTo(DWORD other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$DWORDByReference.class */
    public static class DWORDByReference extends ByReference {
        public DWORDByReference() {
            this(new DWORD(0L));
        }

        public DWORDByReference(DWORD value) {
            super(4);
            setValue(value);
        }

        public void setValue(DWORD value) {
            getPointer().setInt(0L, value.intValue());
        }

        public DWORD getValue() {
            return new DWORD(getPointer().getInt(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$LONG.class */
    public static class LONG extends IntegerType implements Comparable<LONG> {
        public static final int SIZE = Native.LONG_SIZE;

        public LONG() {
            this(0L);
        }

        public LONG(long value) {
            super(SIZE, value);
        }

        @Override // java.lang.Comparable
        public int compareTo(LONG other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$LONGByReference.class */
    public static class LONGByReference extends ByReference {
        public LONGByReference() {
            this(new LONG(0L));
        }

        public LONGByReference(LONG value) {
            super(LONG.SIZE);
            setValue(value);
        }

        public void setValue(LONG value) {
            getPointer().setInt(0L, value.intValue());
        }

        public LONG getValue() {
            return new LONG(getPointer().getInt(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$LONGLONG.class */
    public static class LONGLONG extends IntegerType implements Comparable<LONGLONG> {
        public static final int SIZE = Native.LONG_SIZE * 2;

        public LONGLONG() {
            this(0L);
        }

        public LONGLONG(long value) {
            super(8, value, false);
        }

        @Override // java.lang.Comparable
        public int compareTo(LONGLONG other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$LONGLONGByReference.class */
    public static class LONGLONGByReference extends ByReference {
        public LONGLONGByReference() {
            this(new LONGLONG(0L));
        }

        public LONGLONGByReference(LONGLONG value) {
            super(LONGLONG.SIZE);
            setValue(value);
        }

        public void setValue(LONGLONG value) {
            getPointer().setLong(0L, value.longValue());
        }

        public LONGLONG getValue() {
            return new LONGLONG(getPointer().getLong(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HDC.class */
    public static class HDC extends WinNT.HANDLE {
        public HDC() {
        }

        public HDC(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HICON.class */
    public static class HICON extends WinNT.HANDLE {
        public HICON() {
        }

        public HICON(WinNT.HANDLE handle) {
            this(handle.getPointer());
        }

        public HICON(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HCURSOR.class */
    public static class HCURSOR extends HICON {
        public HCURSOR() {
        }

        public HCURSOR(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HMENU.class */
    public static class HMENU extends WinNT.HANDLE {
        public HMENU() {
        }

        public HMENU(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HPEN.class */
    public static class HPEN extends WinNT.HANDLE {
        public HPEN() {
        }

        public HPEN(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HRSRC.class */
    public static class HRSRC extends WinNT.HANDLE {
        public HRSRC() {
        }

        public HRSRC(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HPALETTE.class */
    public static class HPALETTE extends WinNT.HANDLE {
        public HPALETTE() {
        }

        public HPALETTE(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HBITMAP.class */
    public static class HBITMAP extends WinNT.HANDLE {
        public HBITMAP() {
        }

        public HBITMAP(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HRGN.class */
    public static class HRGN extends WinNT.HANDLE {
        public HRGN() {
        }

        public HRGN(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HWND.class */
    public static class HWND extends WinNT.HANDLE {
        public HWND() {
        }

        public HWND(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HFONT.class */
    public static class HFONT extends WinNT.HANDLE {
        public HFONT() {
        }

        public HFONT(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HKL.class */
    public static class HKL extends WinNT.HANDLE {
        public HKL() {
        }

        public HKL(Pointer p) {
            super(p);
        }

        public HKL(int i) {
            super(Pointer.createConstant(i));
        }

        public int getLanguageIdentifier() {
            return (int) (Pointer.nativeValue(getPointer()) & 65535);
        }

        public int getDeviceHandle() {
            return (int) ((Pointer.nativeValue(getPointer()) >> 16) & 65535);
        }

        @Override // com.sun.jna.platform.win32.WinNT.HANDLE, com.sun.jna.PointerType
        public String toString() {
            return String.format("%08x", Long.valueOf(Pointer.nativeValue(getPointer())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$LPARAM.class */
    public static class LPARAM extends BaseTSD.LONG_PTR {
        public LPARAM() {
            this(0L);
        }

        public LPARAM(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$LRESULT.class */
    public static class LRESULT extends BaseTSD.LONG_PTR {
        public LRESULT() {
            this(0L);
        }

        public LRESULT(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$INT_PTR.class */
    public static class INT_PTR extends IntegerType {
        public INT_PTR() {
            super(Native.POINTER_SIZE);
        }

        public INT_PTR(long value) {
            super(Native.POINTER_SIZE, value);
        }

        public Pointer toPointer() {
            return Pointer.createConstant(longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$UINT_PTR.class */
    public static class UINT_PTR extends IntegerType {
        public UINT_PTR() {
            super(Native.POINTER_SIZE);
        }

        public UINT_PTR(long value) {
            super(Native.POINTER_SIZE, value, true);
        }

        public Pointer toPointer() {
            return Pointer.createConstant(longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$WPARAM.class */
    public static class WPARAM extends UINT_PTR {
        public WPARAM() {
            this(0L);
        }

        public WPARAM(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$RECT.class */
    @Structure.FieldOrder({ElementTags.LEFT, ElementTags.TOP, ElementTags.RIGHT, ElementTags.BOTTOM})
    public static class RECT extends Structure {
        public int left;
        public int top;
        public int right;
        public int bottom;

        public Rectangle toRectangle() {
            return new Rectangle(this.left, this.top, this.right - this.left, this.bottom - this.top);
        }

        @Override // com.sun.jna.Structure
        public String toString() {
            return "[(" + this.left + "," + this.top + ")(" + this.right + "," + this.bottom + ")]";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$ULONG.class */
    public static class ULONG extends IntegerType implements Comparable<ULONG> {
        public static final int SIZE = Native.LONG_SIZE;

        public ULONG() {
            this(0L);
        }

        public ULONG(long value) {
            super(SIZE, value, true);
        }

        @Override // java.lang.Comparable
        public int compareTo(ULONG other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$ULONGByReference.class */
    public static class ULONGByReference extends ByReference {
        public ULONGByReference() {
            this(new ULONG(0L));
        }

        public ULONGByReference(ULONG value) {
            super(ULONG.SIZE);
            setValue(value);
        }

        public void setValue(ULONG value) {
            getPointer().setInt(0L, value.intValue());
        }

        public ULONG getValue() {
            return new ULONG(getPointer().getInt(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$ULONGLONG.class */
    public static class ULONGLONG extends IntegerType implements Comparable<ULONGLONG> {
        public static final int SIZE = 8;

        public ULONGLONG() {
            this(0L);
        }

        public ULONGLONG(long value) {
            super(8, value, true);
        }

        @Override // java.lang.Comparable
        public int compareTo(ULONGLONG other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$ULONGLONGByReference.class */
    public static class ULONGLONGByReference extends ByReference {
        public ULONGLONGByReference() {
            this(new ULONGLONG(0L));
        }

        public ULONGLONGByReference(ULONGLONG value) {
            super(8);
            setValue(value);
        }

        public void setValue(ULONGLONG value) {
            getPointer().setLong(0L, value.longValue());
        }

        public ULONGLONG getValue() {
            return new ULONGLONG(getPointer().getLong(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$DWORDLONG.class */
    public static class DWORDLONG extends IntegerType implements Comparable<DWORDLONG> {
        public static final int SIZE = 8;

        public DWORDLONG() {
            this(0L);
        }

        public DWORDLONG(long value) {
            super(8, value, true);
        }

        @Override // java.lang.Comparable
        public int compareTo(DWORDLONG other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HBRUSH.class */
    public static class HBRUSH extends WinNT.HANDLE {
        public HBRUSH() {
        }

        public HBRUSH(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$ATOM.class */
    public static class ATOM extends WORD {
        public ATOM() {
            this(0L);
        }

        public ATOM(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$PVOID.class */
    public static class PVOID extends PointerType {
        public PVOID() {
        }

        public PVOID(Pointer pointer) {
            super(pointer);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$LPVOID.class */
    public static class LPVOID extends PointerType {
        public LPVOID() {
        }

        public LPVOID(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$POINT.class */
    @Structure.FieldOrder({"x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT})
    public static class POINT extends Structure {
        public int x;
        public int y;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$POINT$ByReference.class */
        public static class ByReference extends POINT implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }

            public ByReference(int x, int y) {
                super(x, y);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$POINT$ByValue.class */
        public static class ByValue extends POINT implements Structure.ByValue {
            public ByValue() {
            }

            public ByValue(Pointer memory) {
                super(memory);
            }

            public ByValue(int x, int y) {
                super(x, y);
            }
        }

        public POINT() {
        }

        public POINT(Pointer memory) {
            super(memory);
            read();
        }

        public POINT(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$USHORT.class */
    public static class USHORT extends IntegerType implements Comparable<USHORT> {
        public static final int SIZE = 2;

        public USHORT() {
            this(0L);
        }

        public USHORT(long value) {
            super(2, value, true);
        }

        @Override // java.lang.Comparable
        public int compareTo(USHORT other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$USHORTByReference.class */
    public static class USHORTByReference extends ByReference {
        public USHORTByReference() {
            this(new USHORT(0L));
        }

        public USHORTByReference(USHORT value) {
            super(2);
            setValue(value);
        }

        public USHORTByReference(short value) {
            super(2);
            setValue(new USHORT(value));
        }

        public void setValue(USHORT value) {
            getPointer().setShort(0L, value.shortValue());
        }

        public USHORT getValue() {
            return new USHORT(getPointer().getShort(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$SHORT.class */
    public static class SHORT extends IntegerType implements Comparable<SHORT> {
        public static final int SIZE = 2;

        public SHORT() {
            this(0L);
        }

        public SHORT(long value) {
            super(2, value, false);
        }

        @Override // java.lang.Comparable
        public int compareTo(SHORT other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$UINT.class */
    public static class UINT extends IntegerType implements Comparable<UINT> {
        public static final int SIZE = 4;

        public UINT() {
            this(0L);
        }

        public UINT(long value) {
            super(4, value, true);
        }

        @Override // java.lang.Comparable
        public int compareTo(UINT other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$UINTByReference.class */
    public static class UINTByReference extends ByReference {
        public UINTByReference() {
            this(new UINT(0L));
        }

        public UINTByReference(UINT value) {
            super(4);
            setValue(value);
        }

        public void setValue(UINT value) {
            getPointer().setInt(0L, value.intValue());
        }

        public UINT getValue() {
            return new UINT(getPointer().getInt(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$SCODE.class */
    public static class SCODE extends ULONG {
        public SCODE() {
            this(0L);
        }

        public SCODE(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$SCODEByReference.class */
    public static class SCODEByReference extends ByReference {
        public SCODEByReference() {
            this(new SCODE(0L));
        }

        public SCODEByReference(SCODE value) {
            super(SCODE.SIZE);
            setValue(value);
        }

        public void setValue(SCODE value) {
            getPointer().setInt(0L, value.intValue());
        }

        public SCODE getValue() {
            return new SCODE(getPointer().getInt(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$LCID.class */
    public static class LCID extends DWORD {
        public LCID() {
            super(0L);
        }

        public LCID(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$BOOL.class */
    public static class BOOL extends IntegerType implements Comparable<BOOL> {
        public static final int SIZE = 4;
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !WinDef.class.desiredAssertionStatus();
        }

        public BOOL() {
            this(0L);
        }

        public BOOL(boolean value) {
            this(value ? 1L : 0L);
        }

        public BOOL(long value) {
            super(4, value, false);
            if (!$assertionsDisabled && value != 0 && value != 1) {
                throw new AssertionError();
            }
        }

        public boolean booleanValue() {
            if (intValue() > 0) {
                return true;
            }
            return false;
        }

        @Override // com.sun.jna.IntegerType
        public String toString() {
            return Boolean.toString(booleanValue());
        }

        @Override // java.lang.Comparable
        public int compareTo(BOOL other) {
            return compare(this, other);
        }

        public static int compare(BOOL v1, BOOL v2) {
            if (v1 == v2) {
                return 0;
            }
            if (v1 == null) {
                return 1;
            }
            if (v2 == null) {
                return -1;
            }
            return compare(v1.booleanValue(), v2.booleanValue());
        }

        public static int compare(BOOL v1, boolean v2) {
            if (v1 == null) {
                return 1;
            }
            return compare(v1.booleanValue(), v2);
        }

        public static int compare(boolean v1, boolean v2) {
            if (v1 == v2) {
                return 0;
            }
            if (v1) {
                return 1;
            }
            return -1;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$BOOLByReference.class */
    public static class BOOLByReference extends ByReference {
        public BOOLByReference() {
            this(new BOOL(0L));
        }

        public BOOLByReference(BOOL value) {
            super(4);
            setValue(value);
        }

        public void setValue(BOOL value) {
            getPointer().setInt(0L, value.intValue());
        }

        public BOOL getValue() {
            return new BOOL(getPointer().getInt(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$UCHAR.class */
    public static class UCHAR extends IntegerType implements Comparable<UCHAR> {
        public static final int SIZE = 1;

        public UCHAR() {
            this(0L);
        }

        public UCHAR(char ch) {
            this(ch & 255);
        }

        public UCHAR(long value) {
            super(1, value, true);
        }

        @Override // java.lang.Comparable
        public int compareTo(UCHAR other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$BYTE.class */
    public static class BYTE extends UCHAR {
        public BYTE() {
            this(0L);
        }

        public BYTE(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$CHAR.class */
    public static class CHAR extends IntegerType implements Comparable<CHAR> {
        public static final int SIZE = 1;

        public CHAR() {
            this(0L);
        }

        public CHAR(byte ch) {
            this(ch & 255);
        }

        public CHAR(long value) {
            super(1, value, false);
        }

        @Override // java.lang.Comparable
        public int compareTo(CHAR other) {
            return compare(this, other);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$CHARByReference.class */
    public static class CHARByReference extends ByReference {
        public CHARByReference() {
            this(new CHAR(0L));
        }

        public CHARByReference(CHAR value) {
            super(1);
            setValue(value);
        }

        public void setValue(CHAR value) {
            getPointer().setByte(0L, value.byteValue());
        }

        public CHAR getValue() {
            return new CHAR(getPointer().getByte(0L));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HGLRC.class */
    public static class HGLRC extends WinNT.HANDLE {
        public HGLRC() {
        }

        public HGLRC(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinDef$HGLRCByReference.class */
    public static class HGLRCByReference extends WinNT.HANDLEByReference {
        public HGLRCByReference() {
        }

        public HGLRCByReference(HGLRC h) {
            super(h);
        }
    }
}
