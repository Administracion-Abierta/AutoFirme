package es.gob.afirma.signers.pkcs7;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pkcs7/DigestedData.class */
public final class DigestedData implements ASN1Encodable {
    private final ASN1Integer version;
    private final AlgorithmIdentifier digestAlgorithm;
    private final ContentInfo contentInfo;
    private final ASN1OctetString digest;

    static DigestedData getInstance(Object obj) {
        if (obj instanceof DigestedData) {
            return (DigestedData) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new DigestedData((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("Objeto desconocido: " + obj.getClass().getName());
    }

    public DigestedData(AlgorithmIdentifier algorithmIdentifier, ContentInfo contentInfo, ASN1OctetString aSN1OctetString) {
        this.version = new ASN1Integer(0L);
        this.digestAlgorithm = algorithmIdentifier;
        this.contentInfo = contentInfo;
        this.digest = aSN1OctetString;
    }

    public DigestedData(ASN1Sequence aSN1Sequence) {
        Enumeration objects = aSN1Sequence.getObjects();
        this.version = (ASN1Integer) objects.nextElement();
        this.digestAlgorithm = AlgorithmIdentifier.getInstance(objects.nextElement());
        this.contentInfo = ContentInfo.getInstance(objects.nextElement());
        this.digest = (ASN1OctetString) objects.nextElement();
    }

    public String getVersion() {
        return this.version.toString();
    }

    public String getDigestAlgorithm() {
        return this.digestAlgorithm.getAlgorithm().toString();
    }

    ASN1OctetString getDigest() {
        return this.digest;
    }

    public String getContentType() {
        return this.contentInfo.getContentType().toString();
    }

    @Override // org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(this.version);
        aSN1EncodableVector.add(this.digestAlgorithm);
        aSN1EncodableVector.add(this.contentInfo);
        aSN1EncodableVector.add(this.digest);
        return new BERSequence(aSN1EncodableVector);
    }
}
