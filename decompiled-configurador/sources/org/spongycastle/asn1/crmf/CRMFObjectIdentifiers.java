package org.spongycastle.asn1.crmf;

import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/crmf/CRMFObjectIdentifiers.class */
public interface CRMFObjectIdentifiers {
    public static final ASN1ObjectIdentifier id_pkix = new ASN1ObjectIdentifier("1.3.6.1.5.5.7");
    public static final ASN1ObjectIdentifier id_pkip = id_pkix.branch("5");
    public static final ASN1ObjectIdentifier id_regCtrl = id_pkip.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier id_regCtrl_regToken = id_regCtrl.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier id_regCtrl_authenticator = id_regCtrl.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier id_regCtrl_pkiPublicationInfo = id_regCtrl.branch(TsaParams.TS_SIGN_DOC);
    public static final ASN1ObjectIdentifier id_regCtrl_pkiArchiveOptions = id_regCtrl.branch("4");
    public static final ASN1ObjectIdentifier id_ct_encKeyWithID = PKCSObjectIdentifiers.id_ct.branch("21");
}
