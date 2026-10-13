package org.spongycastle.asn1.crmf;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/crmf/CRMFObjectIdentifiers.class */
public interface CRMFObjectIdentifiers {
    public static final ASN1ObjectIdentifier id_pkix = new ASN1ObjectIdentifier("1.3.6.1.5.5.7");
    public static final ASN1ObjectIdentifier id_pkip = id_pkix.branch(TlbConst.TYPELIB_MINOR_VERSION_OFFICE);
    public static final ASN1ObjectIdentifier id_regCtrl = id_pkip.branch("1");
    public static final ASN1ObjectIdentifier id_regCtrl_regToken = id_regCtrl.branch("1");
    public static final ASN1ObjectIdentifier id_regCtrl_authenticator = id_regCtrl.branch("2");
    public static final ASN1ObjectIdentifier id_regCtrl_pkiPublicationInfo = id_regCtrl.branch("3");
    public static final ASN1ObjectIdentifier id_regCtrl_pkiArchiveOptions = id_regCtrl.branch(TlbConst.TYPELIB_MINOR_VERSION_WORD);
    public static final ASN1ObjectIdentifier id_ct_encKeyWithID = PKCSObjectIdentifiers.id_ct.branch("21");
}
