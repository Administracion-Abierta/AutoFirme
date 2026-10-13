package org.spongycastle.asn1.dvcs;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.DigestInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/dvcs/Data.class */
public class Data extends ASN1Object implements ASN1Choice {
    private ASN1OctetString message;
    private DigestInfo messageImprint;
    private ASN1Sequence certs;

    public Data(byte[] messageBytes) {
        this.message = new DEROctetString(messageBytes);
    }

    public Data(ASN1OctetString message) {
        this.message = message;
    }

    public Data(DigestInfo messageImprint) {
        this.messageImprint = messageImprint;
    }

    public Data(TargetEtcChain cert) {
        this.certs = new DERSequence(cert);
    }

    public Data(TargetEtcChain[] certs) {
        this.certs = new DERSequence(certs);
    }

    private Data(ASN1Sequence certs) {
        this.certs = certs;
    }

    public static Data getInstance(Object obj) {
        if (obj instanceof Data) {
            return (Data) obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new Data((ASN1OctetString) obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new Data(DigestInfo.getInstance(obj));
        }
        if (obj instanceof ASN1TaggedObject) {
            return new Data(ASN1Sequence.getInstance((ASN1TaggedObject) obj, false));
        }
        throw new IllegalArgumentException("Unknown object submitted to getInstance: " + obj.getClass().getName());
    }

    public static Data getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(obj.getObject());
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.message != null) {
            return this.message.toASN1Primitive();
        }
        if (this.messageImprint != null) {
            return this.messageImprint.toASN1Primitive();
        }
        return new DERTaggedObject(false, 0, this.certs);
    }

    public String toString() {
        if (this.message != null) {
            return "Data {\n" + this.message + "}\n";
        }
        if (this.messageImprint != null) {
            return "Data {\n" + this.messageImprint + "}\n";
        }
        return "Data {\n" + this.certs + "}\n";
    }

    public ASN1OctetString getMessage() {
        return this.message;
    }

    public DigestInfo getMessageImprint() {
        return this.messageImprint;
    }

    public TargetEtcChain[] getCerts() {
        if (this.certs == null) {
            return null;
        }
        TargetEtcChain[] tmp = new TargetEtcChain[this.certs.size()];
        for (int i = 0; i != tmp.length; i++) {
            tmp[i] = TargetEtcChain.getInstance(this.certs.getObjectAt(i));
        }
        return tmp;
    }
}
