package es.gob.jmulticard.asn1.bertlv;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/bertlv/BerParsingException.class */
public final class BerParsingException extends RuntimeException {
    private static final long serialVersionUID = 4729535660890694828L;

    BerParsingException(String message) {
        super(message);
    }

    public BerParsingException(Throwable cause) {
        super(cause);
    }
}
