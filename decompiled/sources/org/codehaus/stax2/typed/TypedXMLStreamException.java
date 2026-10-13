package org.codehaus.stax2.typed;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/typed/TypedXMLStreamException.class */
public class TypedXMLStreamException extends XMLStreamException {
    private static final long serialVersionUID = 1;
    protected String mLexical;

    public TypedXMLStreamException(String lexical, String msg) {
        super(msg);
        this.mLexical = lexical;
    }

    public TypedXMLStreamException(String lexical, IllegalArgumentException rootCause) {
        super(rootCause);
        this.mLexical = lexical;
    }

    public TypedXMLStreamException(String lexical, String msg, IllegalArgumentException rootCause) {
        super(msg, rootCause);
        this.mLexical = lexical;
    }

    public TypedXMLStreamException(String lexical, String msg, Location location, IllegalArgumentException rootCause) {
        super(msg, location, rootCause);
        this.mLexical = lexical;
    }

    public TypedXMLStreamException(String lexical, String msg, Location location) {
        super(msg, location);
        this.mLexical = lexical;
    }

    public String getLexical() {
        return this.mLexical;
    }
}
