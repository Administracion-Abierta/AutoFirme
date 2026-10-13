package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi.class */
public interface NTSecApi {
    public static final int ForestTrustTopLevelName = 0;
    public static final int ForestTrustTopLevelNameEx = 1;
    public static final int ForestTrustDomainInfo = 2;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$LSA_FOREST_TRUST_BINARY_DATA.class */
    @Structure.FieldOrder({"Length", "Buffer"})
    public static class LSA_FOREST_TRUST_BINARY_DATA extends Structure {
        public int Length;
        public Pointer Buffer;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$LSA_FOREST_TRUST_DOMAIN_INFO.class */
    @Structure.FieldOrder({"Sid", "DnsName", "NetbiosName"})
    public static class LSA_FOREST_TRUST_DOMAIN_INFO extends Structure {
        public WinNT.PSID.ByReference Sid;
        public LSA_UNICODE_STRING DnsName;
        public LSA_UNICODE_STRING NetbiosName;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$PLSA_FOREST_TRUST_INFORMATION.class */
    @Structure.FieldOrder({"fti"})
    public static class PLSA_FOREST_TRUST_INFORMATION extends Structure {
        public LSA_FOREST_TRUST_INFORMATION.ByReference fti;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$PLSA_FOREST_TRUST_INFORMATION$ByReference.class */
        public static class ByReference extends PLSA_FOREST_TRUST_INFORMATION implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$PLSA_FOREST_TRUST_RECORD.class */
    @Structure.FieldOrder({"tr"})
    public static class PLSA_FOREST_TRUST_RECORD extends Structure {
        public LSA_FOREST_TRUST_RECORD.ByReference tr;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$PLSA_FOREST_TRUST_RECORD$ByReference.class */
        public static class ByReference extends PLSA_FOREST_TRUST_RECORD implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$PLSA_UNICODE_STRING.class */
    public static class PLSA_UNICODE_STRING {
        public LSA_UNICODE_STRING.ByReference s;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$PLSA_UNICODE_STRING$ByReference.class */
        public static class ByReference extends PLSA_UNICODE_STRING implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$LSA_UNICODE_STRING.class */
    @Structure.FieldOrder({"Length", "MaximumLength", "Buffer"})
    public static class LSA_UNICODE_STRING extends Structure {
        public short Length;
        public short MaximumLength;
        public Pointer Buffer;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$LSA_UNICODE_STRING$ByReference.class */
        public static class ByReference extends LSA_UNICODE_STRING implements Structure.ByReference {
        }

        public String getString() {
            byte[] data = this.Buffer.getByteArray(0L, this.Length);
            if (data.length < 2 || data[data.length - 1] != 0) {
                Memory newdata = new Memory(data.length + 2);
                newdata.write(0L, data, 0, data.length);
                return newdata.getWideString(0L);
            }
            return this.Buffer.getWideString(0L);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$LSA_FOREST_TRUST_RECORD.class */
    @Structure.FieldOrder({"Flags", "ForestTrustType", "Time", "u"})
    public static class LSA_FOREST_TRUST_RECORD extends Structure {
        public int Flags;
        public int ForestTrustType;
        public WinNT.LARGE_INTEGER Time;
        public UNION u;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$LSA_FOREST_TRUST_RECORD$ByReference.class */
        public static class ByReference extends LSA_FOREST_TRUST_RECORD implements Structure.ByReference {
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$LSA_FOREST_TRUST_RECORD$UNION.class */
        public static class UNION extends Union {
            public LSA_UNICODE_STRING TopLevelName;
            public LSA_FOREST_TRUST_DOMAIN_INFO DomainInfo;
            public LSA_FOREST_TRUST_BINARY_DATA Data;

            /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$LSA_FOREST_TRUST_RECORD$UNION$ByReference.class */
            public static class ByReference extends UNION implements Structure.ByReference {
            }
        }

        @Override // com.sun.jna.Structure
        public void read() {
            super.read();
            switch (this.ForestTrustType) {
                case 0:
                case 1:
                    this.u.setType(LSA_UNICODE_STRING.class);
                    break;
                case 2:
                    this.u.setType(LSA_FOREST_TRUST_DOMAIN_INFO.class);
                    break;
                default:
                    this.u.setType(LSA_FOREST_TRUST_BINARY_DATA.class);
                    break;
            }
            this.u.read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$LSA_FOREST_TRUST_INFORMATION.class */
    @Structure.FieldOrder({"RecordCount", "Entries"})
    public static class LSA_FOREST_TRUST_INFORMATION extends Structure {
        public int RecordCount;
        public PLSA_FOREST_TRUST_RECORD.ByReference Entries;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/NTSecApi$LSA_FOREST_TRUST_INFORMATION$ByReference.class */
        public static class ByReference extends LSA_FOREST_TRUST_INFORMATION implements Structure.ByReference {
        }

        public PLSA_FOREST_TRUST_RECORD[] getEntries() {
            return (PLSA_FOREST_TRUST_RECORD[]) this.Entries.toArray(this.RecordCount);
        }
    }
}
