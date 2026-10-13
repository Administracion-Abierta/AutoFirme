package es.gob.afirma.signers.xml.style;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xml/style/StyleException.class */
public abstract class StyleException extends Exception {
    private static final long serialVersionUID = 1922584585798106575L;

    StyleException(String str) {
        super(str);
    }

    StyleException(String str, Throwable th) {
        super(str, th);
    }

    StyleException(Throwable th) {
        super(th);
    }
}
