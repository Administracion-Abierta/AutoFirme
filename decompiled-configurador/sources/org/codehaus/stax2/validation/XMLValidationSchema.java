package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/validation/XMLValidationSchema.class */
public interface XMLValidationSchema {
    public static final String SCHEMA_ID_DTD = "http://www.w3.org/XML/1998/namespace";
    public static final String SCHEMA_ID_RELAXNG = "http://relaxng.org/ns/structure/0.9";
    public static final String SCHEMA_ID_W3C_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    public static final String SCHEMA_ID_TREX = "http://www.thaiopensource.com/trex";

    XMLValidator createValidator(ValidationContext validationContext) throws XMLStreamException;

    String getSchemaType();
}
