package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.Record;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/PrKdf.class */
public class PrKdf extends Record implements Pkcs15PrKdf {
    public PrKdf() {
        super(new OptionalDecoderObjectElement(PrivateKeyObject.class, false), new OptionalDecoderObjectElement(PrivateKeyObject.class, true), new OptionalDecoderObjectElement(PrivateKeyObject.class, true), new OptionalDecoderObjectElement(PrivateKeyObject.class, true), new OptionalDecoderObjectElement(PrivateKeyObject.class, true), new OptionalDecoderObjectElement(PrivateKeyObject.class, true), new OptionalDecoderObjectElement(PrivateKeyObject.class, true), new OptionalDecoderObjectElement(PrivateKeyObject.class, true), new OptionalDecoderObjectElement(PrivateKeyObject.class, true), new OptionalDecoderObjectElement(PrivateKeyObject.class, true));
    }

    protected PrKdf(OptionalDecoderObjectElement... elements) {
        super(elements);
    }

    public int getKeyCount() {
        return getElementCount();
    }

    public byte[] getKeyIdentifier(int index) {
        return ((PrivateKeyObject) getElementAt(index)).getKeyIdentifier();
    }

    public String getKeyName(int index) {
        return ((PrivateKeyObject) getElementAt(index)).getKeyName();
    }

    public String getKeyPath(int index) {
        return ((PrivateKeyObject) getElementAt(index)).getKeyPath();
    }

    public int getKeyLength(int index) {
        return ((PrivateKeyObject) getElementAt(index)).getKeyLength();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fichero de Descripcion de Claves Privadas:\n");
        for (int index = 0; index < getKeyCount(); index++) {
            sb.append(" Clave privada ");
            sb.append(Integer.toString(index));
            sb.append("\n  Nombre de la clave: ");
            sb.append(getKeyName(index));
            if (getKeyPrincipal(index) != null) {
                sb.append("\n  RDN de la clave: ");
                sb.append(getKeyPrincipal(index).toString());
            }
            sb.append("\n  Longitud de la clave: ");
            sb.append(getKeyLength(index));
            sb.append("\n  Ruta hacia la clave: ");
            sb.append(getKeyPath(index));
            if (index != getKeyCount() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    public byte getKeyReference(int index) {
        return ((PrivateKeyObject) getElementAt(index)).getKeyReference();
    }

    public byte[] getKeyId(int index) {
        return ((PrivateKeyObject) getElementAt(index)).getKeyIdentifier();
    }

    public X500Principal getKeyPrincipal(int index) {
        return ((PrivateKeyObject) getElementAt(index)).getKeyPrincipal();
    }
}
