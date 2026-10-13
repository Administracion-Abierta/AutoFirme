package es.gob.afirma.keystores.filters.rfc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.qualified.QCStatement;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/QCStatements.class */
final class QCStatements extends ASN1Object {
    private final List<QCStatement> qCStatements = new ArrayList();

    private QCStatements(ASN1Sequence aSN1Sequence) {
        for (int i = 0; i < aSN1Sequence.size(); i++) {
            this.qCStatements.add(QCStatement.getInstance(aSN1Sequence.getObjectAt(i)));
        }
    }

    static QCStatements getInstance(Object obj) {
        if (obj instanceof QCStatements) {
            return (QCStatements) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new QCStatements((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("Secuencia invalida");
    }

    List<QCStatement> getQCStatement() {
        return this.qCStatements;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator<QCStatement> it = this.qCStatements.iterator();
        while (it.hasNext()) {
            aSN1EncodableVector.add(it.next().toASN1Primitive());
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
