package org.bouncycastle.asn1.cmp;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/CMPObjectIdentifiers.class */
public interface CMPObjectIdentifiers {
    public static final ASN1ObjectIdentifier passwordBasedMac = CRMFObjectIdentifiers.passwordBasedMac;
    public static final ASN1ObjectIdentifier kemBasedMac = MiscObjectIdentifiers.entrust.branch("66.16");
    public static final ASN1ObjectIdentifier dhBasedMac = MiscObjectIdentifiers.entrust.branch("66.30");
    public static final ASN1ObjectIdentifier id_it = X509ObjectIdentifiers.id_pkix.branch(TlbConst.TYPELIB_MINOR_VERSION_WORD);
    public static final ASN1ObjectIdentifier it_caProtEncCert = id_it.branch("1");
    public static final ASN1ObjectIdentifier it_signKeyPairTypes = id_it.branch("2");
    public static final ASN1ObjectIdentifier it_encKeyPairTypes = id_it.branch("3");
    public static final ASN1ObjectIdentifier it_preferredSymAlg = id_it.branch(TlbConst.TYPELIB_MINOR_VERSION_WORD);
    public static final ASN1ObjectIdentifier it_caKeyUpdateInfo = id_it.branch(TlbConst.TYPELIB_MINOR_VERSION_OFFICE);
    public static final ASN1ObjectIdentifier it_currentCRL = id_it.branch("6");
    public static final ASN1ObjectIdentifier it_unsupportedOIDs = id_it.branch("7");
    public static final ASN1ObjectIdentifier it_keyPairParamReq = id_it.branch("10");
    public static final ASN1ObjectIdentifier it_keyPairParamRep = id_it.branch("11");
    public static final ASN1ObjectIdentifier it_revPassphrase = id_it.branch("12");
    public static final ASN1ObjectIdentifier it_implicitConfirm = id_it.branch("13");
    public static final ASN1ObjectIdentifier it_confirmWaitTime = id_it.branch("14");
    public static final ASN1ObjectIdentifier it_origPKIMessage = id_it.branch("15");
    public static final ASN1ObjectIdentifier it_suppLangTags = id_it.branch("16");
    public static final ASN1ObjectIdentifier id_it_caCerts = id_it.branch("17");
    public static final ASN1ObjectIdentifier id_it_rootCaKeyUpdate = id_it.branch("18");
    public static final ASN1ObjectIdentifier id_it_certReqTemplate = id_it.branch("19");
    public static final ASN1ObjectIdentifier id_it_rootCaCert = id_it.branch("20");
    public static final ASN1ObjectIdentifier id_it_certProfile = id_it.branch("21");
    public static final ASN1ObjectIdentifier id_it_crlStatusList = id_it.branch("22");
    public static final ASN1ObjectIdentifier id_it_crls = id_it.branch("23");
    public static final ASN1ObjectIdentifier id_pkip = CRMFObjectIdentifiers.id_pkip;
    public static final ASN1ObjectIdentifier id_regCtrl = CRMFObjectIdentifiers.id_regCtrl;
    public static final ASN1ObjectIdentifier id_regInfo = CRMFObjectIdentifiers.id_regInfo;
    public static final ASN1ObjectIdentifier regCtrl_regToken = CRMFObjectIdentifiers.id_regCtrl_regToken;
    public static final ASN1ObjectIdentifier regCtrl_authenticator = CRMFObjectIdentifiers.id_regCtrl_authenticator;
    public static final ASN1ObjectIdentifier regCtrl_pkiPublicationInfo = CRMFObjectIdentifiers.id_regCtrl_pkiPublicationInfo;
    public static final ASN1ObjectIdentifier regCtrl_pkiArchiveOptions = CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions;
    public static final ASN1ObjectIdentifier regCtrl_oldCertID = CRMFObjectIdentifiers.id_regCtrl_oldCertID;
    public static final ASN1ObjectIdentifier regCtrl_protocolEncrKey = CRMFObjectIdentifiers.id_regCtrl_protocolEncrKey;
    public static final ASN1ObjectIdentifier regCtrl_altCertTemplate = id_regCtrl.branch("7");
    public static final ASN1ObjectIdentifier id_regCtrl_algId = id_regCtrl.branch("11");
    public static final ASN1ObjectIdentifier id_regCtrl_rsaKeyLen = id_regCtrl.branch("12");
    public static final ASN1ObjectIdentifier regInfo_utf8Pairs = CRMFObjectIdentifiers.id_regInfo_utf8Pairs;
    public static final ASN1ObjectIdentifier regInfo_certReq = CRMFObjectIdentifiers.id_regInfo_certReq;
    public static final ASN1ObjectIdentifier ct_encKeyWithID = CRMFObjectIdentifiers.id_ct_encKeyWithID;
}
