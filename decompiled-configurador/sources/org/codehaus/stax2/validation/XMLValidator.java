package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/validation/XMLValidator.class */
public abstract class XMLValidator {
    public static final int CONTENT_ALLOW_NONE = 0;
    public static final int CONTENT_ALLOW_WS = 1;
    public static final int CONTENT_ALLOW_WS_NONSTRICT = 2;
    public static final int CONTENT_ALLOW_VALIDATABLE_TEXT = 3;
    public static final int CONTENT_ALLOW_ANY_TEXT = 4;
    public static final int CONTENT_ALLOW_UNDEFINED = 5;

    public abstract XMLValidationSchema getSchema();

    public abstract void validateElementStart(String str, String str2, String str3) throws XMLStreamException;

    public abstract String validateAttribute(String str, String str2, String str3, String str4) throws XMLStreamException;

    public abstract String validateAttribute(String str, String str2, String str3, char[] cArr, int i, int i2) throws XMLStreamException;

    public abstract int validateElementAndAttributes() throws XMLStreamException;

    public abstract int validateElementEnd(String str, String str2, String str3) throws XMLStreamException;

    public abstract void validateText(String str, boolean z) throws XMLStreamException;

    public abstract void validateText(char[] cArr, int i, int i2, boolean z) throws XMLStreamException;

    public abstract void validationCompleted(boolean z) throws XMLStreamException;

    public abstract String getAttributeType(int i);

    public abstract int getIdAttrIndex();

    public abstract int getNotationAttrIndex();

    protected XMLValidator() {
    }

    public String getSchemaType() {
        XMLValidationSchema sch = getSchema();
        if (sch == null) {
            return null;
        }
        return sch.getSchemaType();
    }
}
