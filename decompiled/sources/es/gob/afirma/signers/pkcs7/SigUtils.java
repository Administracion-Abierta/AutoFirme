package es.gob.afirma.signers.pkcs7;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BERSet;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pkcs7/SigUtils.class */
public final class SigUtils {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private SigUtils() {
    }

    public static AlgorithmIdentifier makeAlgId(String str) {
        return new AlgorithmIdentifier(new ASN1ObjectIdentifier(str), DERNull.INSTANCE);
    }

    public static ASN1Set createBerSetFromList(List<ASN1Encodable> list) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator<ASN1Encodable> it = list.iterator();
        while (it.hasNext()) {
            aSN1EncodableVector.add(it.next());
        }
        return new BERSet(aSN1EncodableVector);
    }

    public static ASN1Set getAttributeSet(AttributeTable attributeTable) {
        if (attributeTable == null) {
            LOGGER.warning("Los atributos eran nulos, se devolvera null");
            return null;
        }
        return new DERSet(attributeTable.toASN1EncodableVector());
    }

    public static ASN1Set fillRestCerts(List<ASN1Encodable> list, ASN1EncodableVector aSN1EncodableVector) {
        Iterator<ASN1Encodable> it = list.iterator();
        while (it.hasNext()) {
            aSN1EncodableVector.add(it.next());
        }
        return new BERSet(aSN1EncodableVector);
    }
}
