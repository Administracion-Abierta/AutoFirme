package es.gob.jmulticard.asn1;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/OptionalDecoderObjectElement.class */
public final class OptionalDecoderObjectElement {
    private final Class<? extends DecoderObject> elementType;
    private final boolean optional;

    public OptionalDecoderObjectElement(Class<? extends DecoderObject> type, boolean opt) {
        if (type == null && !opt) {
            throw new IllegalArgumentException("El tipo de elemento ASN.1 no puede ser nulo cuando el elemento no es opcional");
        }
        this.elementType = type;
        this.optional = opt;
    }

    public Class<? extends DecoderObject> getElementType() {
        return this.elementType;
    }

    public boolean isOptional() {
        return this.optional;
    }
}
