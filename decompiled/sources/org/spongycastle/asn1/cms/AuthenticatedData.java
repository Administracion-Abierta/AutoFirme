package org.spongycastle.asn1.cms;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cms/AuthenticatedData.class */
public class AuthenticatedData extends ASN1Object {
    private ASN1Integer version;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private AlgorithmIdentifier macAlgorithm;
    private AlgorithmIdentifier digestAlgorithm;
    private ContentInfo encapsulatedContentInfo;
    private ASN1Set authAttrs;
    private ASN1OctetString mac;
    private ASN1Set unauthAttrs;

    public AuthenticatedData(OriginatorInfo originatorInfo, ASN1Set recipientInfos, AlgorithmIdentifier macAlgorithm, AlgorithmIdentifier digestAlgorithm, ContentInfo encapsulatedContent, ASN1Set authAttrs, ASN1OctetString mac, ASN1Set unauthAttrs) {
        if ((digestAlgorithm != null || authAttrs != null) && (digestAlgorithm == null || authAttrs == null)) {
            throw new IllegalArgumentException("digestAlgorithm and authAttrs must be set together");
        }
        this.version = new ASN1Integer(calculateVersion(originatorInfo));
        this.originatorInfo = originatorInfo;
        this.macAlgorithm = macAlgorithm;
        this.digestAlgorithm = digestAlgorithm;
        this.recipientInfos = recipientInfos;
        this.encapsulatedContentInfo = encapsulatedContent;
        this.authAttrs = authAttrs;
        this.mac = mac;
        this.unauthAttrs = unauthAttrs;
    }

    private AuthenticatedData(ASN1Sequence seq) {
        int index = 0 + 1;
        this.version = (ASN1Integer) seq.getObjectAt(0);
        int index2 = index + 1;
        Object tmp = seq.getObjectAt(index);
        if (tmp instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject) tmp, false);
            index2++;
            tmp = seq.getObjectAt(index2);
        }
        this.recipientInfos = ASN1Set.getInstance(tmp);
        int i = index2;
        int index3 = index2 + 1;
        this.macAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(i));
        int index4 = index3 + 1;
        Object tmp2 = seq.getObjectAt(index3);
        if (tmp2 instanceof ASN1TaggedObject) {
            this.digestAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject) tmp2, false);
            index4++;
            tmp2 = seq.getObjectAt(index4);
        }
        this.encapsulatedContentInfo = ContentInfo.getInstance(tmp2);
        int i2 = index4;
        int index5 = index4 + 1;
        Object tmp3 = seq.getObjectAt(i2);
        if (tmp3 instanceof ASN1TaggedObject) {
            this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject) tmp3, false);
            index5++;
            tmp3 = seq.getObjectAt(index5);
        }
        this.mac = ASN1OctetString.getInstance(tmp3);
        if (seq.size() > index5) {
            this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject) seq.getObjectAt(index5), false);
        }
    }

    public static AuthenticatedData getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static AuthenticatedData getInstance(Object obj) {
        if (obj instanceof AuthenticatedData) {
            return (AuthenticatedData) obj;
        }
        if (obj != null) {
            return new AuthenticatedData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public OriginatorInfo getOriginatorInfo() {
        return this.originatorInfo;
    }

    public ASN1Set getRecipientInfos() {
        return this.recipientInfos;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public ContentInfo getEncapsulatedContentInfo() {
        return this.encapsulatedContentInfo;
    }

    public ASN1Set getAuthAttrs() {
        return this.authAttrs;
    }

    public ASN1OctetString getMac() {
        return this.mac;
    }

    public ASN1Set getUnauthAttrs() {
        return this.unauthAttrs;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        if (this.originatorInfo != null) {
            v.add(new DERTaggedObject(false, 0, this.originatorInfo));
        }
        v.add(this.recipientInfos);
        v.add(this.macAlgorithm);
        if (this.digestAlgorithm != null) {
            v.add(new DERTaggedObject(false, 1, this.digestAlgorithm));
        }
        v.add(this.encapsulatedContentInfo);
        if (this.authAttrs != null) {
            v.add(new DERTaggedObject(false, 2, this.authAttrs));
        }
        v.add(this.mac);
        if (this.unauthAttrs != null) {
            v.add(new DERTaggedObject(false, 3, this.unauthAttrs));
        }
        return new BERSequence(v);
    }

    public static int calculateVersion(OriginatorInfo origInfo) {
        if (origInfo == null) {
            return 0;
        }
        int ver = 0;
        Enumeration e = origInfo.getCertificates().getObjects();
        while (e.hasMoreElements()) {
            Object obj = e.nextElement();
            if (obj instanceof ASN1TaggedObject) {
                ASN1TaggedObject tag = (ASN1TaggedObject) obj;
                if (tag.getTagNo() == 2) {
                    ver = 1;
                } else if (tag.getTagNo() == 3) {
                    ver = 3;
                    break;
                }
            }
        }
        if (origInfo.getCRLs() != null) {
            Enumeration e2 = origInfo.getCRLs().getObjects();
            while (e2.hasMoreElements()) {
                Object obj2 = e2.nextElement();
                if ((obj2 instanceof ASN1TaggedObject) && ((ASN1TaggedObject) obj2).getTagNo() == 1) {
                    ver = 3;
                    break;
                }
            }
        }
        return ver;
    }
}
