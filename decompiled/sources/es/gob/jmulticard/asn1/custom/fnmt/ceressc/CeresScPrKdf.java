package es.gob.jmulticard.asn1.custom.fnmt.ceressc;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.pkcs15.PrKdf;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/custom/fnmt/ceressc/CeresScPrKdf.class */
public final class CeresScPrKdf extends PrKdf {
    public CeresScPrKdf() {
        super(new OptionalDecoderObjectElement(CeresScPrivateKeyObject.class, false), new OptionalDecoderObjectElement(CeresScPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresScPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresScPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresScPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresScPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresScPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresScPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresScPrivateKeyObject.class, true), new OptionalDecoderObjectElement(CeresScPrivateKeyObject.class, true));
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrKdf, es.gob.jmulticard.asn1.der.pkcs15.Pkcs15PrKdf
    public int getKeyCount() {
        return getElementCount();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrKdf
    public byte[] getKeyIdentifier(int index) {
        return ((CeresScPrivateKeyObject) getElementAt(index)).getKeyIdentifier();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrKdf
    public String getKeyName(int index) {
        return ((CeresScPrivateKeyObject) getElementAt(index)).getKeyName();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrKdf
    public String getKeyPath(int index) {
        return ((CeresScPrivateKeyObject) getElementAt(index)).getKeyPath();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrKdf
    public int getKeyLength(int index) {
        return ((CeresScPrivateKeyObject) getElementAt(index)).getKeyLength();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrKdf
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

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrKdf, es.gob.jmulticard.asn1.der.pkcs15.Pkcs15PrKdf
    public byte getKeyReference(int index) {
        return ((CeresScPrivateKeyObject) getElementAt(index)).getKeyReference();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrKdf, es.gob.jmulticard.asn1.der.pkcs15.Pkcs15PrKdf
    public byte[] getKeyId(int index) {
        return ((CeresScPrivateKeyObject) getElementAt(index)).getKeyIdentifier();
    }

    @Override // es.gob.jmulticard.asn1.der.pkcs15.PrKdf, es.gob.jmulticard.asn1.der.pkcs15.Pkcs15PrKdf
    public X500Principal getKeyPrincipal(int index) {
        return ((CeresScPrivateKeyObject) getElementAt(index)).getKeyPrincipal();
    }
}
