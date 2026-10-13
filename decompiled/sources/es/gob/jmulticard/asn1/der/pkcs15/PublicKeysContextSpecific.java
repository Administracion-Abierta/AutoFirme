package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.der.ContextSpecific;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/PublicKeysContextSpecific.class */
public final class PublicKeysContextSpecific extends ContextSpecific {
    private static final byte TAG = -95;

    public PublicKeysContextSpecific() {
        super(Path.class);
    }

    @Override // es.gob.jmulticard.asn1.der.ContextSpecific, es.gob.jmulticard.asn1.DecoderObject
    public void checkTag(byte tag) throws Asn1Exception {
        if (TAG != tag) {
            throw new Asn1Exception("PublicKeysContextSpecific esperaba una etiqueta especifica de contexto " + HexUtils.hexify(new byte[]{TAG}, false) + " pero ha encontrado " + HexUtils.hexify(new byte[]{tag}, false));
        }
    }

    public Path getPublicKeysPath() {
        if (getObject() instanceof Path) {
            return (Path) getObject();
        }
        throw new IllegalStateException("El objeto interno no es de tipo Path PKCS#15");
    }
}
