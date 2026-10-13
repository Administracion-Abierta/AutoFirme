package org.apache.xml.security.stax.impl.processor.input;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xml.security.binding.xmldsig.ReferenceType;
import org.apache.xml.security.binding.xmldsig.SignatureType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.DocumentContext;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.securityEvent.SignedElementSecurityEvent;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/input/XMLSignatureReferenceVerifyInputProcessor.class */
public class XMLSignatureReferenceVerifyInputProcessor extends AbstractSignatureReferenceVerifyInputProcessor {
    public XMLSignatureReferenceVerifyInputProcessor(InputProcessorChain inputProcessorChain, SignatureType signatureType, InboundSecurityToken inboundSecurityToken, XMLSecurityProperties securityProperties) throws XMLSecurityException {
        super(inputProcessorChain, signatureType, inboundSecurityToken, securityProperties);
        addAfterProcessor(XMLSignatureReferenceVerifyInputProcessor.class.getName());
    }

    @Override // org.apache.xml.security.stax.impl.processor.input.AbstractSignatureReferenceVerifyInputProcessor
    protected void processElementPath(List<QName> elementPath, InputProcessorChain inputProcessorChain, XMLSecEvent xmlSecEvent, ReferenceType referenceType) throws XMLSecurityException {
        DocumentContext documentContext = inputProcessorChain.getDocumentContext();
        SignedElementSecurityEvent signedElementSecurityEvent = new SignedElementSecurityEvent(getInboundSecurityToken(), true, documentContext.getProtectionOrder());
        signedElementSecurityEvent.setElementPath(elementPath);
        signedElementSecurityEvent.setXmlSecEvent(xmlSecEvent);
        signedElementSecurityEvent.setCorrelationID(referenceType.getId());
        inputProcessorChain.getSecurityContext().registerSecurityEvent(signedElementSecurityEvent);
    }
}
