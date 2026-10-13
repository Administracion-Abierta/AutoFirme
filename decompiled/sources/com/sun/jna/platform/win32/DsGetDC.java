package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.W32APITypeMapper;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DsGetDC.class */
public interface DsGetDC {
    public static final int DS_DOMAIN_IN_FOREST = 1;
    public static final int DS_DOMAIN_DIRECT_OUTBOUND = 2;
    public static final int DS_DOMAIN_TREE_ROOT = 4;
    public static final int DS_DOMAIN_PRIMARY = 8;
    public static final int DS_DOMAIN_NATIVE_MODE = 16;
    public static final int DS_DOMAIN_DIRECT_INBOUND = 32;
    public static final int DS_DOMAIN_VALID_FLAGS = 63;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DsGetDC$PDOMAIN_CONTROLLER_INFO.class */
    @Structure.FieldOrder({"dci"})
    public static class PDOMAIN_CONTROLLER_INFO extends Structure {
        public DOMAIN_CONTROLLER_INFO.ByReference dci;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DsGetDC$PDOMAIN_CONTROLLER_INFO$ByReference.class */
        public static class ByReference extends PDOMAIN_CONTROLLER_INFO implements Structure.ByReference {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DsGetDC$DOMAIN_CONTROLLER_INFO.class */
    @Structure.FieldOrder({"DomainControllerName", "DomainControllerAddress", "DomainControllerAddressType", "DomainGuid", "DomainName", "DnsForestName", "Flags", "DcSiteName", "ClientSiteName"})
    public static class DOMAIN_CONTROLLER_INFO extends Structure {
        public String DomainControllerName;
        public String DomainControllerAddress;
        public int DomainControllerAddressType;
        public Guid.GUID DomainGuid;
        public String DomainName;
        public String DnsForestName;
        public int Flags;
        public String DcSiteName;
        public String ClientSiteName;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DsGetDC$DOMAIN_CONTROLLER_INFO$ByReference.class */
        public static class ByReference extends DOMAIN_CONTROLLER_INFO implements Structure.ByReference {
        }

        public DOMAIN_CONTROLLER_INFO() {
            super(W32APITypeMapper.DEFAULT);
        }

        public DOMAIN_CONTROLLER_INFO(Pointer memory) {
            super(memory, 0, W32APITypeMapper.DEFAULT);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DsGetDC$DS_DOMAIN_TRUSTS.class */
    @Structure.FieldOrder({"NetbiosDomainName", "DnsDomainName", "Flags", "ParentIndex", "TrustType", "TrustAttributes", "DomainSid", "DomainGuid"})
    public static class DS_DOMAIN_TRUSTS extends Structure {
        public String NetbiosDomainName;
        public String DnsDomainName;
        public int Flags;
        public int ParentIndex;
        public int TrustType;
        public int TrustAttributes;
        public WinNT.PSID.ByReference DomainSid;
        public Guid.GUID DomainGuid;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DsGetDC$DS_DOMAIN_TRUSTS$ByReference.class */
        public static class ByReference extends DS_DOMAIN_TRUSTS implements Structure.ByReference {
        }

        public DS_DOMAIN_TRUSTS() {
            super(W32APITypeMapper.DEFAULT);
        }

        public DS_DOMAIN_TRUSTS(Pointer p) {
            super(p, 0, W32APITypeMapper.DEFAULT);
            read();
        }
    }
}
