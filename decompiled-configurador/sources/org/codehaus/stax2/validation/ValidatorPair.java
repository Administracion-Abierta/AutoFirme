package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/validation/ValidatorPair.class */
public class ValidatorPair extends XMLValidator {
    public static final String ATTR_TYPE_DEFAULT = "CDATA";
    protected XMLValidator mFirst;
    protected XMLValidator mSecond;

    public ValidatorPair(XMLValidator first, XMLValidator second) {
        this.mFirst = first;
        this.mSecond = second;
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public XMLValidationSchema getSchema() {
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public void validateElementStart(String localName, String uri, String prefix) throws XMLStreamException {
        this.mFirst.validateElementStart(localName, uri, prefix);
        this.mSecond.validateElementStart(localName, uri, prefix);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public String validateAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        String retVal = this.mFirst.validateAttribute(localName, uri, prefix, value);
        if (retVal != null) {
            value = retVal;
        }
        return this.mSecond.validateAttribute(localName, uri, prefix, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public String validateAttribute(String localName, String uri, String prefix, char[] valueChars, int valueStart, int valueEnd) throws XMLStreamException {
        String retVal = this.mFirst.validateAttribute(localName, uri, prefix, valueChars, valueStart, valueEnd);
        if (retVal != null) {
            return this.mSecond.validateAttribute(localName, uri, prefix, retVal);
        }
        return this.mSecond.validateAttribute(localName, uri, prefix, valueChars, valueStart, valueEnd);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public int validateElementAndAttributes() throws XMLStreamException {
        int textType1 = this.mFirst.validateElementAndAttributes();
        int textType2 = this.mSecond.validateElementAndAttributes();
        return textType1 < textType2 ? textType1 : textType2;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public int validateElementEnd(String localName, String uri, String prefix) throws XMLStreamException {
        int textType1 = this.mFirst.validateElementEnd(localName, uri, prefix);
        int textType2 = this.mSecond.validateElementEnd(localName, uri, prefix);
        return textType1 < textType2 ? textType1 : textType2;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public void validateText(String text, boolean lastTextSegment) throws XMLStreamException {
        this.mFirst.validateText(text, lastTextSegment);
        this.mSecond.validateText(text, lastTextSegment);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public void validateText(char[] cbuf, int textStart, int textEnd, boolean lastTextSegment) throws XMLStreamException {
        this.mFirst.validateText(cbuf, textStart, textEnd, lastTextSegment);
        this.mSecond.validateText(cbuf, textStart, textEnd, lastTextSegment);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.XMLValidator
    public void validationCompleted(boolean eod) throws XMLStreamException {
        this.mFirst.validationCompleted(eod);
        this.mSecond.validationCompleted(eod);
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public String getAttributeType(int index) {
        String type2;
        String type = this.mFirst.getAttributeType(index);
        if ((type == null || type.length() == 0 || type.equals("CDATA")) && (type2 = this.mSecond.getAttributeType(index)) != null && type2.length() > 0) {
            return type2;
        }
        return type;
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public int getIdAttrIndex() {
        int index = this.mFirst.getIdAttrIndex();
        if (index < 0) {
            return this.mSecond.getIdAttrIndex();
        }
        return index;
    }

    @Override // org.codehaus.stax2.validation.XMLValidator
    public int getNotationAttrIndex() {
        int index = this.mFirst.getNotationAttrIndex();
        if (index < 0) {
            return this.mSecond.getNotationAttrIndex();
        }
        return index;
    }

    public static boolean removeValidator(XMLValidator root, XMLValidationSchema schema, XMLValidator[] results) {
        if (root instanceof ValidatorPair) {
            return ((ValidatorPair) root).doRemoveValidator(schema, results);
        }
        if (root.getSchema() == schema) {
            results[0] = root;
            results[1] = null;
            return true;
        }
        return false;
    }

    public static boolean removeValidator(XMLValidator root, XMLValidator vld, XMLValidator[] results) {
        if (root == vld) {
            results[0] = root;
            results[1] = null;
            return true;
        }
        if (root instanceof ValidatorPair) {
            return ((ValidatorPair) root).doRemoveValidator(vld, results);
        }
        return false;
    }

    private boolean doRemoveValidator(XMLValidationSchema schema, XMLValidator[] results) {
        if (removeValidator(this.mFirst, schema, results)) {
            XMLValidator newFirst = results[1];
            if (newFirst == null) {
                results[1] = this.mSecond;
                return true;
            }
            this.mFirst = newFirst;
            results[1] = this;
            return true;
        }
        if (removeValidator(this.mSecond, schema, results)) {
            XMLValidator newSecond = results[1];
            if (newSecond == null) {
                results[1] = this.mFirst;
                return true;
            }
            this.mSecond = newSecond;
            results[1] = this;
            return true;
        }
        return false;
    }

    private boolean doRemoveValidator(XMLValidator vld, XMLValidator[] results) {
        if (removeValidator(this.mFirst, vld, results)) {
            XMLValidator newFirst = results[1];
            if (newFirst == null) {
                results[1] = this.mSecond;
                return true;
            }
            this.mFirst = newFirst;
            results[1] = this;
            return true;
        }
        if (removeValidator(this.mSecond, vld, results)) {
            XMLValidator newSecond = results[1];
            if (newSecond == null) {
                results[1] = this.mFirst;
                return true;
            }
            this.mSecond = newSecond;
            results[1] = this;
            return true;
        }
        return false;
    }
}
