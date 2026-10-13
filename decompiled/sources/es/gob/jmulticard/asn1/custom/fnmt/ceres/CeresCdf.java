package es.gob.jmulticard.asn1.custom.fnmt.ceres;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.Record;
import es.gob.jmulticard.asn1.der.pkcs15.CertificateObject;
import es.gob.jmulticard.asn1.der.pkcs15.Pkcs15Cdf;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/custom/fnmt/ceres/CeresCdf.class */
public final class CeresCdf extends Record implements Pkcs15Cdf {
    private static final int BUFFER_SIZE = 150;

    public CeresCdf() {
        super(new OptionalDecoderObjectElement(CeresCertificateObject.class, true), new OptionalDecoderObjectElement(CeresCertificateObject.class, true), new OptionalDecoderObjectElement(CeresCertificateObject.class, true), new OptionalDecoderObjectElement(CeresCertificateObject.class, true), new OptionalDecoderObjectElement(CeresCertificateObject.class, true), new OptionalDecoderObjectElement(CeresCertificateObject.class, true), new OptionalDecoderObjectElement(CeresCertificateObject.class, true), new OptionalDecoderObjectElement(CeresCertificateObject.class, true), new OptionalDecoderObjectElement(CeresCertificateObject.class, true), new OptionalDecoderObjectElement(CeresCertificateObject.class, true));
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.Pkcs15Cdf
    public int getCertificateCount() {
        return getElementCount();
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
    public String getCertificateAlias(int index) {
        byte[] id = getCertificateId(index);
        if (id != null) {
            return HexUtils.hexify(id, false);
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

    public String toString() {
        StringBuilder sb = new StringBuilder(150);
        sb.append("Fichero de Descripcion de Certificados CERES:\n");
        for (int index = 0; index < getCertificateCount(); index++) {
            sb.append(" Certificado ");
            sb.append(Integer.toString(index));
            sb.append("\n  Ruta PKCS#15: ");
            sb.append(getCertificatePath(index));
            sb.append("\n  Identificador del certificado: ");
            sb.append(HexUtils.hexify(getCertificateId(index), false));
            if (index != getCertificateCount() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
