package org.codehaus.stax2.validation;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/validation/ValidationContext.class */
public interface ValidationContext {
    String getXmlVersion();

    QName getCurrentElementName();

    String getNamespaceURI(String str);

    int getAttributeCount();

    String getAttributeLocalName(int i);

    String getAttributeNamespace(int i);

    String getAttributePrefix(int i);

    String getAttributeValue(int i);

    String getAttributeValue(String str, String str2);

    String getAttributeType(int i);

    int findAttributeIndex(String str, String str2);

    boolean isNotationDeclared(String str);

    boolean isUnparsedEntityDeclared(String str);

    String getBaseUri();

    Location getValidationLocation();

    void reportProblem(XMLValidationProblem xMLValidationProblem) throws XMLStreamException;

    int addDefaultAttribute(String str, String str2, String str3, String str4) throws XMLStreamException;
}
