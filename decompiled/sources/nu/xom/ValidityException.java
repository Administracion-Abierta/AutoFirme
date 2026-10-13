package nu.xom;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/ValidityException.class */
public class ValidityException extends ParsingException {
    private static final long serialVersionUID = 8950434465665278751L;
    private List saxExceptions;
    private transient Document document;

    public ValidityException(String str, Throwable th) {
        super(str, th);
        this.saxExceptions = new ArrayList();
    }

    public ValidityException(String str, int i, int i2) {
        super(str, i, i2);
        this.saxExceptions = new ArrayList();
    }

    public ValidityException(String str, int i, int i2, Throwable th) {
        super(str, i, i2, th);
        this.saxExceptions = new ArrayList();
    }

    public ValidityException(String str, String str2, int i, int i2) {
        super(str, str2, i, i2);
        this.saxExceptions = new ArrayList();
    }

    public ValidityException(String str, String str2, int i, int i2, Throwable th) {
        super(str, str2, i, i2, th);
        this.saxExceptions = new ArrayList();
    }

    public ValidityException(String str) {
        super(str);
        this.saxExceptions = new ArrayList();
    }

    public Document getDocument() {
        return this.document;
    }

    void setDocument(Document document) {
        this.document = document;
    }

    void addError(SAXParseException sAXParseException) {
        this.saxExceptions.add(sAXParseException);
    }

    public int getErrorCount() {
        return this.saxExceptions.size();
    }

    public String getValidityError(int i) {
        return ((Exception) this.saxExceptions.get(i)).getMessage();
    }

    public int getLineNumber(int i) {
        return ((SAXParseException) this.saxExceptions.get(i)).getLineNumber();
    }

    public int getColumnNumber(int i) {
        return ((SAXParseException) this.saxExceptions.get(i)).getColumnNumber();
    }
}
