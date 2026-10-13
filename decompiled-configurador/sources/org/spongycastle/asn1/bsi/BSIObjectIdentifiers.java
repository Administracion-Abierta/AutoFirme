package org.spongycastle.asn1.bsi;

import com.ctc.wstx.cfg.XmlConsts;
import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/bsi/BSIObjectIdentifiers.class */
public interface BSIObjectIdentifiers {
    public static final ASN1ObjectIdentifier bsi_de = new ASN1ObjectIdentifier("0.4.0.127.0.7");
    public static final ASN1ObjectIdentifier id_ecc = bsi_de.branch(XmlConsts.XML_V_11_STR);
    public static final ASN1ObjectIdentifier ecdsa_plain_signatures = id_ecc.branch("4.1");
    public static final ASN1ObjectIdentifier ecdsa_plain_SHA1 = ecdsa_plain_signatures.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier ecdsa_plain_SHA224 = ecdsa_plain_signatures.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier ecdsa_plain_SHA256 = ecdsa_plain_signatures.branch(TsaParams.TS_SIGN_DOC);
    public static final ASN1ObjectIdentifier ecdsa_plain_SHA384 = ecdsa_plain_signatures.branch("4");
    public static final ASN1ObjectIdentifier ecdsa_plain_SHA512 = ecdsa_plain_signatures.branch("5");
    public static final ASN1ObjectIdentifier ecdsa_plain_RIPEMD160 = ecdsa_plain_signatures.branch("6");
}
