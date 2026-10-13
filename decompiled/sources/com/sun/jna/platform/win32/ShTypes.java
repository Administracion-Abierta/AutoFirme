package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/ShTypes.class */
public interface ShTypes {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/ShTypes$STRRET.class */
    @Structure.FieldOrder({"uType", "u"})
    public static class STRRET extends Structure {
        public static final int TYPE_WSTR = 0;
        public static final int TYPE_OFFSET = 1;
        public static final int TYPE_CSTR = 2;
        public int uType;
        public UNION u;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/ShTypes$STRRET$UNION.class */
        public static class UNION extends Union {
            public WTypes.LPWSTR pOleStr;
            public int uOffset;
            public byte[] cStr = new byte[260];

            /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/ShTypes$STRRET$UNION$ByReference.class */
            public static class ByReference extends UNION implements Structure.ByReference {
            }
        }

        public STRRET() {
        }

        public STRRET(Pointer p) {
            super(p);
            read();
        }

        @Override // com.sun.jna.Structure
        public void read() {
            super.read();
            switch (this.uType) {
                case 0:
                default:
                    this.u.setType("pOleStr");
                    break;
                case 1:
                    this.u.setType("uOffset");
                    break;
                case 2:
                    this.u.setType("cStr");
                    break;
            }
            this.u.read();
        }
    }
}
