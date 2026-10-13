package org.apache.xml.security.stax.impl.transformer.canonicalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/transformer/canonicalizer/Canonicalizer11.class */
public class Canonicalizer11 extends CanonicalizerBase {
    public Canonicalizer11(boolean includeComments) {
        super(includeComments);
    }

    @Override // org.apache.xml.security.stax.impl.transformer.canonicalizer.CanonicalizerBase
    protected List<XMLSecAttribute> getInitialUtilizedAttributes(XMLSecStartElement xmlSecStartElement, CanonicalizerBase.C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecAttribute> utilizedAttributes = Collections.emptyList();
        List<XMLSecAttribute> visibleAttributes = new ArrayList<>();
        xmlSecStartElement.getAttributesFromCurrentScope(visibleAttributes);
        for (int i = 0; i < visibleAttributes.size(); i++) {
            XMLSecAttribute comparableAttribute = visibleAttributes.get(i);
            QName comparableAttributeName = comparableAttribute.getName();
            if (org.apache.xml.security.c14n.implementations.CanonicalizerBase.XML.equals(comparableAttributeName.getPrefix()) && !"id".equals(comparableAttributeName.getLocalPart()) && !"base".equals(comparableAttributeName.getLocalPart()) && outputStack.containsOnStack(comparableAttribute) == null) {
                if (utilizedAttributes == Collections.emptyList()) {
                    utilizedAttributes = new ArrayList(2);
                }
                utilizedAttributes.add(comparableAttribute);
                outputStack.peek().add(comparableAttribute);
            }
        }
        List<XMLSecAttribute> elementAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i2 = 0; i2 < elementAttributes.size(); i2++) {
            XMLSecAttribute comparableAttribute2 = elementAttributes.get(i2);
            QName attributeName = comparableAttribute2.getName();
            if (!org.apache.xml.security.c14n.implementations.CanonicalizerBase.XML.equals(attributeName.getPrefix())) {
                if (utilizedAttributes == Collections.emptyList()) {
                    utilizedAttributes = new ArrayList(2);
                }
                utilizedAttributes.add(comparableAttribute2);
            }
        }
        return utilizedAttributes;
    }
}
