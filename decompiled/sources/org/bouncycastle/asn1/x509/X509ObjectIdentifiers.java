package org.bouncycastle.asn1.x509;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/X509ObjectIdentifiers.class */
public interface X509ObjectIdentifiers {
    public static final ASN1ObjectIdentifier attributeType = new ASN1ObjectIdentifier("2.5.4").intern();
    public static final ASN1ObjectIdentifier commonName = attributeType.branch("3").intern();
    public static final ASN1ObjectIdentifier countryName = attributeType.branch("6").intern();
    public static final ASN1ObjectIdentifier localityName = attributeType.branch("7").intern();
    public static final ASN1ObjectIdentifier stateOrProvinceName = attributeType.branch(TlbConst.TYPELIB_MAJOR_VERSION_WORD).intern();
    public static final ASN1ObjectIdentifier organization = attributeType.branch("10").intern();
    public static final ASN1ObjectIdentifier organizationalUnitName = attributeType.branch("11").intern();
    public static final ASN1ObjectIdentifier id_at_telephoneNumber = attributeType.branch("20").intern();
    public static final ASN1ObjectIdentifier id_at_name = attributeType.branch("41").intern();
    public static final ASN1ObjectIdentifier id_at_organizationIdentifier = attributeType.branch("97").intern();
    public static final ASN1ObjectIdentifier id_SHA1 = new ASN1ObjectIdentifier("1.3.14.3.2.26").intern();
    public static final ASN1ObjectIdentifier ripemd160 = new ASN1ObjectIdentifier("1.3.36.3.2.1").intern();
    public static final ASN1ObjectIdentifier ripemd160WithRSAEncryption = new ASN1ObjectIdentifier("1.3.36.3.3.1.2").intern();
    public static final ASN1ObjectIdentifier id_ea_rsa = new ASN1ObjectIdentifier("2.5.8.1.1").intern();
    public static final ASN1ObjectIdentifier id_pkix = new ASN1ObjectIdentifier("1.3.6.1.5.5.7");
    public static final ASN1ObjectIdentifier id_pe = id_pkix.branch("1");
    public static final ASN1ObjectIdentifier pkix_algorithms = id_pkix.branch("6");
    public static final ASN1ObjectIdentifier id_rsassa_pss_shake128 = pkix_algorithms.branch("30");
    public static final ASN1ObjectIdentifier id_rsassa_pss_shake256 = pkix_algorithms.branch("31");
    public static final ASN1ObjectIdentifier id_ecdsa_with_shake128 = pkix_algorithms.branch("32");
    public static final ASN1ObjectIdentifier id_ecdsa_with_shake256 = pkix_algorithms.branch("33");
    public static final ASN1ObjectIdentifier id_pda = id_pkix.branch("9");
    public static final ASN1ObjectIdentifier id_ad = id_pkix.branch("48");
    public static final ASN1ObjectIdentifier id_ad_caIssuers = id_ad.branch("2").intern();
    public static final ASN1ObjectIdentifier id_ad_ocsp = id_ad.branch("1").intern();
    public static final ASN1ObjectIdentifier ocspAccessMethod = id_ad_ocsp;
    public static final ASN1ObjectIdentifier crlAccessMethod = id_ad_caIssuers;
    public static final ASN1ObjectIdentifier id_ce = new ASN1ObjectIdentifier("2.5.29");
    public static final ASN1ObjectIdentifier id_PasswordBasedMac = MiscObjectIdentifiers.entrust.branch("66.13");
}
