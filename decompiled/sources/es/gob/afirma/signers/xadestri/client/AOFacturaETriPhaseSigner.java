package es.gob.afirma.signers.xadestri.client;

import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.CounterSignTarget;
import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.Properties;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xadestri/client/AOFacturaETriPhaseSigner.class */
public final class AOFacturaETriPhaseSigner extends AOXAdESTriPhaseSigner {
    public AOFacturaETriPhaseSigner() {
        super(AOSignConstants.SIGN_FORMAT_FACTURAE);
    }

    @Override // es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner, es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta la multifirma de las firmas FacturaE");
    }

    @Override // es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner, es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta la multifirma de las firmas FacturaE");
    }

    @Override // es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner, es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        return super.isSign(bArr) && isValidDataFile(bArr);
    }

    @Override // es.gob.afirma.signers.xadestri.client.AOXAdESTriPhaseSigner, es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return false;
        }
        try {
            Element documentElement = SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(bArr)).getDocumentElement();
            if (!"Facturae".equals(documentElement.getLocalName())) {
                return false;
            }
            HashSet hashSet = new HashSet(3);
            hashSet.add("FileHeader");
            hashSet.add("Parties");
            hashSet.add("Invoices");
            NodeList childNodes = documentElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                String localName = childNodes.item(i).getLocalName();
                if (hashSet.contains(localName)) {
                    hashSet.remove(localName);
                }
            }
            if (hashSet.size() > 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
