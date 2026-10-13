package org.bouncycastle.asn1.x509;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/KeyPurposeId.class */
public class KeyPurposeId extends ASN1Object {
    private static final ASN1ObjectIdentifier id_kp = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.3");
    public static final KeyPurposeId anyExtendedKeyUsage = new KeyPurposeId(Extension.extendedKeyUsage.branch("0"));
    public static final KeyPurposeId id_kp_serverAuth = new KeyPurposeId(id_kp.branch("1"));
    public static final KeyPurposeId id_kp_clientAuth = new KeyPurposeId(id_kp.branch("2"));
    public static final KeyPurposeId id_kp_codeSigning = new KeyPurposeId(id_kp.branch("3"));
    public static final KeyPurposeId id_kp_emailProtection = new KeyPurposeId(id_kp.branch(TlbConst.TYPELIB_MINOR_VERSION_WORD));
    public static final KeyPurposeId id_kp_ipsecEndSystem = new KeyPurposeId(id_kp.branch(TlbConst.TYPELIB_MINOR_VERSION_OFFICE));
    public static final KeyPurposeId id_kp_ipsecTunnel = new KeyPurposeId(id_kp.branch("6"));
    public static final KeyPurposeId id_kp_ipsecUser = new KeyPurposeId(id_kp.branch("7"));
    public static final KeyPurposeId id_kp_timeStamping = new KeyPurposeId(id_kp.branch(TlbConst.TYPELIB_MAJOR_VERSION_WORD));
    public static final KeyPurposeId id_kp_OCSPSigning = new KeyPurposeId(id_kp.branch("9"));
    public static final KeyPurposeId id_kp_dvcs = new KeyPurposeId(id_kp.branch("10"));
    public static final KeyPurposeId id_kp_sbgpCertAAServerAuth = new KeyPurposeId(id_kp.branch("11"));
    public static final KeyPurposeId id_kp_scvp_responder = new KeyPurposeId(id_kp.branch("12"));
    public static final KeyPurposeId id_kp_eapOverPPP = new KeyPurposeId(id_kp.branch("13"));
    public static final KeyPurposeId id_kp_eapOverLAN = new KeyPurposeId(id_kp.branch("14"));
    public static final KeyPurposeId id_kp_scvpServer = new KeyPurposeId(id_kp.branch("15"));
    public static final KeyPurposeId id_kp_scvpClient = new KeyPurposeId(id_kp.branch("16"));
    public static final KeyPurposeId id_kp_ipsecIKE = new KeyPurposeId(id_kp.branch("17"));
    public static final KeyPurposeId id_kp_capwapAC = new KeyPurposeId(id_kp.branch("18"));
    public static final KeyPurposeId id_kp_capwapWTP = new KeyPurposeId(id_kp.branch("19"));
    public static final KeyPurposeId id_kp_cmcCA = new KeyPurposeId(id_kp.branch("27"));
    public static final KeyPurposeId id_kp_cmcRA = new KeyPurposeId(id_kp.branch("28"));
    public static final KeyPurposeId id_kp_cmKGA = new KeyPurposeId(id_kp.branch("32"));
    public static final KeyPurposeId id_kp_smartcardlogon = new KeyPurposeId(new ASN1ObjectIdentifier("1.3.6.1.4.1.311.20.2.2"));
    public static final KeyPurposeId id_kp_macAddress = new KeyPurposeId(new ASN1ObjectIdentifier("1.3.6.1.1.1.1.22"));
    public static final KeyPurposeId id_kp_msSGC = new KeyPurposeId(new ASN1ObjectIdentifier("1.3.6.1.4.1.311.10.3.3"));
    public static final KeyPurposeId id_kp_nsSGC = new KeyPurposeId(new ASN1ObjectIdentifier("2.16.840.1.113730.4.1"));
    private ASN1ObjectIdentifier id;

    private KeyPurposeId(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.id = aSN1ObjectIdentifier;
    }

    public static KeyPurposeId getInstance(Object obj) {
        if (obj instanceof KeyPurposeId) {
            return (KeyPurposeId) obj;
        }
        if (obj != null) {
            return new KeyPurposeId(ASN1ObjectIdentifier.getInstance(obj));
        }
        return null;
    }

    public ASN1ObjectIdentifier toOID() {
        return this.id;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.id;
    }

    public String getId() {
        return this.id.getId();
    }

    public String toString() {
        return this.id.toString();
    }
}
