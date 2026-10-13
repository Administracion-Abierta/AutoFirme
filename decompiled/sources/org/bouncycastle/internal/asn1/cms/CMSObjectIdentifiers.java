package org.bouncycastle.internal.asn1.cms;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/internal/asn1/cms/CMSObjectIdentifiers.class */
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
    public static final ASN1ObjectIdentifier id_ri = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.16");
    public static final ASN1ObjectIdentifier id_ri_ocsp_response = id_ri.branch("2");
    public static final ASN1ObjectIdentifier id_ri_scvp = id_ri.branch(TlbConst.TYPELIB_MINOR_VERSION_WORD);
    public static final ASN1ObjectIdentifier id_alg = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.6");
    public static final ASN1ObjectIdentifier id_RSASSA_PSS_SHAKE128 = id_alg.branch("30");
    public static final ASN1ObjectIdentifier id_RSASSA_PSS_SHAKE256 = id_alg.branch("31");
    public static final ASN1ObjectIdentifier id_ecdsa_with_shake128 = id_alg.branch("32");
    public static final ASN1ObjectIdentifier id_ecdsa_with_shake256 = id_alg.branch("33");
}
