package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/validation/Validatable.class */
public interface Validatable {
    XMLValidator validateAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException;

    XMLValidator stopValidatingAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException;

    XMLValidator stopValidatingAgainst(XMLValidator xMLValidator) throws XMLStreamException;

    ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler validationProblemHandler);
}
