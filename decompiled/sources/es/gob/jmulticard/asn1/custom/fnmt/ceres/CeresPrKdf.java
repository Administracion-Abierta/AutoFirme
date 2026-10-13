package es.gob.jmulticard.asn1.custom.fnmt.ceres;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.Record;
import es.gob.jmulticard.asn1.der.pkcs15.Pkcs15PrKdf;
import es.gob.jmulticard.asn1.der.pkcs15.PrivateKeyObject;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/custom/fnmt/ceres/CeresPrKdf.class */
public final class CeresPrKdf extends Record implements Pkcs15PrKdf {
    public CeresPrKdf() {
        super(new OptionalDecoderObjectElement(CeresPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresPrivateKeyObject.class, true));
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.Pkcs15PrKdf
    public int getKeyCount() {
        return getElementCount();
    }

    public String getKeyPath(int index) {
        return ((PrivateKeyObject) getElementAt(index)).getKeyPath();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.Pkcs15PrKdf
    public byte[] getKeyId(int index) {
        return ((PrivateKeyObject) getElementAt(index)).getKeyIdentifier();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.Pkcs15PrKdf
    public byte getKeyReference(int index) {
        return ((PrivateKeyObject) getElementAt(index)).getKeyReference();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fichero de Descripcion de Claves Privadas CERES:\n");
        for (int index = 0; index < getKeyCount(); index++) {
            sb.append(" Clave privada ");
            sb.append(Integer.toString(index));
            sb.append("\n  Ruta hacia la clave: ");
            sb.append(getKeyPath(index));
            sb.append("\n  Identificador de la clave: ");
            sb.append(HexUtils.hexify(getKeyId(index), false));
            sb.append("\n  Referencia de la clave: 0x");
            sb.append(HexUtils.hexify(new byte[]{getKeyReference(index)}, false));
            if (index != getKeyCount() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.Pkcs15PrKdf
    public X500Principal getKeyPrincipal(int index) {
        return null;
    }
}
