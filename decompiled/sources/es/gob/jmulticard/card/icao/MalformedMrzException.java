package es.gob.jmulticard.card.icao;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/MalformedMrzException.class */
public final class MalformedMrzException extends IcaoException {
    private static final long serialVersionUID = 437348879325857134L;

    MalformedMrzException(String description) {
        super(description);
    }

    MalformedMrzException(String description, Exception cause) {
        super(description, cause);
    }
}
