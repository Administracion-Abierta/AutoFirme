package es.gob.afirma.standalone.configurator.common.xmlwise;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/configurator/common/xmlwise/XmlParseException.class */
public class XmlParseException extends Exception {
    private static final long serialVersionUID = -3246260520113823143L;

    XmlParseException(Throwable th) {
        super(th);
    }

    XmlParseException(String str) {
        super(str);
    }

    XmlParseException(String str, Throwable th) {
        super(str, th);
    }
}
