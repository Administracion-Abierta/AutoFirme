package org.spongycastle.asn1.x509.qualified;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x509/qualified/QCStatement.class */
public class QCStatement extends ASN1Object implements ETSIQCObjectIdentifiers, RFC3739QCObjectIdentifiers {
    ASN1ObjectIdentifier qcStatementId;
    ASN1Encodable qcStatementInfo;

    public static QCStatement getInstance(Object obj) {
        if (obj instanceof QCStatement) {
            return (QCStatement) obj;
        }
        if (obj != null) {
            return new QCStatement(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private QCStatement(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.qcStatementId = ASN1ObjectIdentifier.getInstance(e.nextElement());
        if (e.hasMoreElements()) {
            this.qcStatementInfo = (ASN1Encodable) e.nextElement();
        }
    }

    public QCStatement(ASN1ObjectIdentifier qcStatementId) {
        this.qcStatementId = qcStatementId;
        this.qcStatementInfo = null;
    }

    public QCStatement(ASN1ObjectIdentifier qcStatementId, ASN1Encodable qcStatementInfo) {
        this.qcStatementId = qcStatementId;
        this.qcStatementInfo = qcStatementInfo;
    }

    public ASN1ObjectIdentifier getStatementId() {
        return this.qcStatementId;
    }

    public ASN1Encodable getStatementInfo() {
        return this.qcStatementInfo;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seq = new ASN1EncodableVector();
        seq.add(this.qcStatementId);
        if (this.qcStatementInfo != null) {
            seq.add(this.qcStatementInfo);
        }
        return new DERSequence(seq);
    }
}
