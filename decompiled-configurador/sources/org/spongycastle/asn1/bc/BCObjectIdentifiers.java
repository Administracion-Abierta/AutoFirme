package org.spongycastle.asn1.bc;

import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/bc/BCObjectIdentifiers.class */
public interface BCObjectIdentifiers {
    public static final ASN1ObjectIdentifier bc = new ASN1ObjectIdentifier("1.3.6.1.4.1.22554");
    public static final ASN1ObjectIdentifier bc_pbe = bc.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier bc_pbe_sha1 = bc_pbe.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier bc_pbe_sha256 = bc_pbe.branch("2.1");
    public static final ASN1ObjectIdentifier bc_pbe_sha384 = bc_pbe.branch("2.2");
    public static final ASN1ObjectIdentifier bc_pbe_sha512 = bc_pbe.branch("2.3");
    public static final ASN1ObjectIdentifier bc_pbe_sha224 = bc_pbe.branch("2.4");
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs5 = bc_pbe_sha1.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12 = bc_pbe_sha1.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs5 = bc_pbe_sha256.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12 = bc_pbe_sha256.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes128_cbc = bc_pbe_sha1_pkcs12.branch("1.2");
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes192_cbc = bc_pbe_sha1_pkcs12.branch("1.22");
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes256_cbc = bc_pbe_sha1_pkcs12.branch("1.42");
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes128_cbc = bc_pbe_sha256_pkcs12.branch("1.2");
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes192_cbc = bc_pbe_sha256_pkcs12.branch("1.22");
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes256_cbc = bc_pbe_sha256_pkcs12.branch("1.42");
    public static final ASN1ObjectIdentifier bc_sig = bc.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier sphincs256 = bc_sig.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier sphincs256_with_BLAKE512 = sphincs256.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier sphincs256_with_SHA512 = sphincs256.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier sphincs256_with_SHA3_512 = sphincs256.branch(TsaParams.TS_SIGN_DOC);
    public static final ASN1ObjectIdentifier xmss = bc_sig.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier xmss_with_SHA256 = xmss.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier xmss_with_SHA512 = xmss.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier xmss_with_SHAKE128 = xmss.branch(TsaParams.TS_SIGN_DOC);
    public static final ASN1ObjectIdentifier xmss_with_SHAKE256 = xmss.branch("4");
    public static final ASN1ObjectIdentifier xmss_mt = bc_sig.branch(TsaParams.TS_SIGN_DOC);
    public static final ASN1ObjectIdentifier xmss_mt_with_SHA256 = xmss_mt.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier xmss_mt_with_SHA512 = xmss_mt.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier xmss_mt_with_SHAKE128 = xmss_mt.branch(TsaParams.TS_SIGN_DOC);
    public static final ASN1ObjectIdentifier xmss_mt_with_SHAKE256 = xmss_mt.branch("4");
    public static final ASN1ObjectIdentifier bc_exch = bc.branch(TsaParams.TS_SIGN_DOC);
    public static final ASN1ObjectIdentifier newHope = bc_exch.branch(TsaParams.TS_SIGN);
}
