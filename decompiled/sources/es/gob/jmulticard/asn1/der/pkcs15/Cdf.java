package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.Record;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/Cdf.class */
public final class Cdf extends Record implements Pkcs15Cdf {
    private static final int BUFFER_SIZE = 150;

    public Cdf() {
        super(new OptionalDecoderObjectElement(CertificateObject.class, false), new OptionalDecoderObjectElement(CertificateObject.class, true), new OptionalDecoderObjectElement(CertificateObject.class, true), new OptionalDecoderObjectElement(CertificateObject.class, true), new OptionalDecoderObjectElement(CertificateObject.class, true), new OptionalDecoderObjectElement(CertificateObject.class, true), new OptionalDecoderObjectElement(CertificateObject.class, true), new OptionalDecoderObjectElement(CertificateObject.class, true), new OptionalDecoderObjectElement(CertificateObject.class, true), new OptionalDecoderObjectElement(CertificateObject.class, true));
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.Pkcs15Cdf
    public int getCertificateCount() {
        return getElementCount();
    }

    public String getCertificateIssuerPrincipal(int index) {
        CertificateObject tmpCo = (CertificateObject) getElementAt(index);
        if (tmpCo != null) {
            return tmpCo.getIssuer();
        }
        return null;
    }

    public String getCertificateSubjectPrincipal(int index) {
        CertificateObject tmpCo = (CertificateObject) getElementAt(index);
        if (tmpCo != null) {
            return tmpCo.getSubject();
        }
        return null;
    }

    public BigInteger getCertificateSerialNumber(int index) {
        CertificateObject tmpCo = (CertificateObject) getElementAt(index);
        if (tmpCo != null) {
            return tmpCo.getSerialNumber();
        }
        return null;
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.Pkcs15Cdf
    public byte[] getCertificateId(int index) {
        CertificateObject tmpCo = (CertificateObject) getElementAt(index);
        if (tmpCo != null) {
            return tmpCo.getIdentifier();
        }
        return null;
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.Pkcs15Cdf
    public String getCertificatePath(int index) {
        CertificateObject tmpCo = (CertificateObject) getElementAt(index);
        if (tmpCo != null) {
            return tmpCo.getPath();
        }
        return null;
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.Pkcs15Cdf
    public String getCertificateAlias(int index) {
        CertificateObject tmpCo = (CertificateObject) getElementAt(index);
        if (tmpCo != null) {
            return tmpCo.getAlias();
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(150);
        sb.append("Fichero de Descripcion de Certificados:\n");
        for (int index = 0; index < getCertificateCount(); index++) {
            sb.append(" Certificado ");
            sb.append(Integer.toString(index));
            sb.append("\n  Alias: ");
            sb.append(getCertificateAlias(index));
            sb.append("\n  Titular: ");
            sb.append(getCertificateSubjectPrincipal(index));
            sb.append("\n  Emisor: ");
            sb.append(getCertificateIssuerPrincipal(index));
            sb.append("\n  Numero de serie: ");
            sb.append(getCertificateSerialNumber(index));
            sb.append("\n  Identificador: ");
            sb.append(HexUtils.hexify(getCertificateId(index), false));
            sb.append("\n  Ruta PKCS#15: ");
            sb.append(getCertificatePath(index));
            if (index != getCertificateCount() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
