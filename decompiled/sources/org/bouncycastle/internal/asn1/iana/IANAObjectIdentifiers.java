package org.bouncycastle.internal.asn1.iana;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/internal/asn1/iana/IANAObjectIdentifiers.class */
public interface IANAObjectIdentifiers {
    public static final ASN1ObjectIdentifier internet = new ASN1ObjectIdentifier("1.3.6.1");
    public static final ASN1ObjectIdentifier directory = internet.branch("1");
    public static final ASN1ObjectIdentifier mgmt = internet.branch("2");
    public static final ASN1ObjectIdentifier experimental = internet.branch("3");
    public static final ASN1ObjectIdentifier _private = internet.branch(TlbConst.TYPELIB_MINOR_VERSION_WORD);
    public static final ASN1ObjectIdentifier security = internet.branch(TlbConst.TYPELIB_MINOR_VERSION_OFFICE);
    public static final ASN1ObjectIdentifier SNMPv2 = internet.branch("6");
    public static final ASN1ObjectIdentifier mail = internet.branch("7");
    public static final ASN1ObjectIdentifier security_mechanisms = security.branch(TlbConst.TYPELIB_MINOR_VERSION_OFFICE);
    public static final ASN1ObjectIdentifier security_nametypes = security.branch("6");
    public static final ASN1ObjectIdentifier pkix = security_mechanisms.branch("6");
    public static final ASN1ObjectIdentifier ipsec = security_mechanisms.branch(TlbConst.TYPELIB_MAJOR_VERSION_WORD);
    public static final ASN1ObjectIdentifier isakmpOakley = ipsec.branch("1");
    public static final ASN1ObjectIdentifier hmacMD5 = isakmpOakley.branch("1");
    public static final ASN1ObjectIdentifier hmacSHA1 = isakmpOakley.branch("2");
    public static final ASN1ObjectIdentifier hmacTIGER = isakmpOakley.branch("3");
    public static final ASN1ObjectIdentifier hmacRIPEMD160 = isakmpOakley.branch(TlbConst.TYPELIB_MINOR_VERSION_WORD);
}
