package org.bouncycastle.asn1.cms;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/CMSObjectIdentifiers.class */
public interface CMSObjectIdentifiers {
    public static final ASN1ObjectIdentifier data = PKCSObjectIdentifiers.data;
    public static final ASN1ObjectIdentifier signedData = PKCSObjectIdentifiers.signedData;
    public static final ASN1ObjectIdentifier envelopedData = PKCSObjectIdentifiers.envelopedData;
    public static final ASN1ObjectIdentifier signedAndEnvelopedData = PKCSObjectIdentifiers.signedAndEnvelopedData;
    public static final ASN1ObjectIdentifier digestedData = PKCSObjectIdentifiers.digestedData;
    public static final ASN1ObjectIdentifier encryptedData = PKCSObjectIdentifiers.encryptedData;
    public static final ASN1ObjectIdentifier authenticatedData = PKCSObjectIdentifiers.id_ct_authData;
    public static final ASN1ObjectIdentifier compressedData = PKCSObjectIdentifiers.id_ct_compressedData;
    public static final ASN1ObjectIdentifier authEnvelopedData = PKCSObjectIdentifiers.id_ct_authEnvelopedData;
    public static final ASN1ObjectIdentifier timestampedData = PKCSObjectIdentifiers.id_ct_timestampedData;
    public static final ASN1ObjectIdentifier zlibCompress = PKCSObjectIdentifiers.id_alg_zlibCompress;
    public static final ASN1ObjectIdentifier id_ri = X509ObjectIdentifiers.id_pkix.branch("16");
    public static final ASN1ObjectIdentifier id_ri_ocsp_response = id_ri.branch("2");
    public static final ASN1ObjectIdentifier id_ri_scvp = id_ri.branch(TlbConst.TYPELIB_MINOR_VERSION_WORD);
    public static final ASN1ObjectIdentifier id_alg = X509ObjectIdentifiers.pkix_algorithms;
    public static final ASN1ObjectIdentifier id_RSASSA_PSS_SHAKE128 = X509ObjectIdentifiers.id_rsassa_pss_shake128;
    public static final ASN1ObjectIdentifier id_RSASSA_PSS_SHAKE256 = X509ObjectIdentifiers.id_rsassa_pss_shake256;
    public static final ASN1ObjectIdentifier id_ecdsa_with_shake128 = X509ObjectIdentifiers.id_ecdsa_with_shake128;
    public static final ASN1ObjectIdentifier id_ecdsa_with_shake256 = X509ObjectIdentifiers.id_ecdsa_with_shake256;
    public static final ASN1ObjectIdentifier id_ori = PKCSObjectIdentifiers.id_smime.branch("13");
    public static final ASN1ObjectIdentifier id_ori_kem = id_ori.branch("3");
    public static final ASN1ObjectIdentifier id_alg_cek_hkdf_sha256 = PKCSObjectIdentifiers.smime_alg.branch("31");
}
