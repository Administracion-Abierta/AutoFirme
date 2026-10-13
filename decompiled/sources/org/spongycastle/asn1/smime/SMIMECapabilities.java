package org.spongycastle.asn1.smime;

import java.util.Enumeration;
import java.util.Vector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.cms.CMSEnvelopedGenerator;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/smime/SMIMECapabilities.class */
public class SMIMECapabilities extends ASN1Object {
    public static final ASN1ObjectIdentifier preferSignedData = PKCSObjectIdentifiers.preferSignedData;
    public static final ASN1ObjectIdentifier canNotDecryptAny = PKCSObjectIdentifiers.canNotDecryptAny;
    public static final ASN1ObjectIdentifier sMIMECapabilitesVersions = PKCSObjectIdentifiers.sMIMECapabilitiesVersions;
    public static final ASN1ObjectIdentifier aes256_CBC = NISTObjectIdentifiers.id_aes256_CBC;
    public static final ASN1ObjectIdentifier aes192_CBC = NISTObjectIdentifiers.id_aes192_CBC;
    public static final ASN1ObjectIdentifier aes128_CBC = NISTObjectIdentifiers.id_aes128_CBC;
    public static final ASN1ObjectIdentifier idea_CBC = new ASN1ObjectIdentifier(CMSEnvelopedGenerator.IDEA_CBC);
    public static final ASN1ObjectIdentifier cast5_CBC = new ASN1ObjectIdentifier(CMSEnvelopedGenerator.CAST5_CBC);
    public static final ASN1ObjectIdentifier dES_CBC = new ASN1ObjectIdentifier("1.3.14.3.2.7");
    public static final ASN1ObjectIdentifier dES_EDE3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC;
    public static final ASN1ObjectIdentifier rC2_CBC = PKCSObjectIdentifiers.RC2_CBC;
    private ASN1Sequence capabilities;

    public static SMIMECapabilities getInstance(Object o) {
        if (o == null || (o instanceof SMIMECapabilities)) {
            return (SMIMECapabilities) o;
        }
        if (o instanceof ASN1Sequence) {
            return new SMIMECapabilities((ASN1Sequence) o);
        }
        if (o instanceof Attribute) {
            return new SMIMECapabilities((ASN1Sequence) ((Attribute) o).getAttrValues().getObjectAt(0));
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }

    public SMIMECapabilities(ASN1Sequence seq) {
        this.capabilities = seq;
    }

    public Vector getCapabilities(ASN1ObjectIdentifier capability) {
        Enumeration e = this.capabilities.getObjects();
        Vector list = new Vector();
        if (capability == null) {
            while (e.hasMoreElements()) {
                list.addElement(SMIMECapability.getInstance(e.nextElement()));
            }
        } else {
            while (e.hasMoreElements()) {
                SMIMECapability cap = SMIMECapability.getInstance(e.nextElement());
                if (capability.equals(cap.getCapabilityID())) {
                    list.addElement(cap);
                }
            }
        }
        return list;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.capabilities;
    }
}
