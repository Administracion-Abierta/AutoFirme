package org.apache.xml.security.stax.impl.processor.input;

import java.io.InputStream;
import java.util.List;
import javax.crypto.Cipher;
import javax.xml.namespace.QName;
import org.apache.xml.security.binding.xmlenc.EncryptedDataType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.DocumentContext;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.securityEvent.ContentEncryptedElementSecurityEvent;
import org.apache.xml.security.stax.securityEvent.EncryptedElementSecurityEvent;
import org.apache.xml.security.stax.securityEvent.TokenSecurityEvent;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/input/XMLDecryptInputProcessor.class */
public class XMLDecryptInputProcessor extends AbstractDecryptInputProcessor {
    public XMLDecryptInputProcessor(XMLSecurityProperties securityProperties) throws XMLSecurityException {
        super(securityProperties);
    }

    @Override // org.apache.xml.security.stax.impl.processor.input.AbstractDecryptInputProcessor
    protected AbstractDecryptInputProcessor.AbstractDecryptedEventReaderInputProcessor newDecryptedEventReaderInputProcessor(boolean encryptedHeader, XMLSecStartElement xmlSecStartElement, EncryptedDataType currentEncryptedDataType, InboundSecurityToken inboundSecurityToken, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException {
        return new DecryptedEventReaderInputProcessor(getSecurityProperties(), SecurePart.Modifier.getModifier(currentEncryptedDataType.getType()), encryptedHeader, xmlSecStartElement, currentEncryptedDataType, this, inboundSecurityToken);
    }

    @Override // org.apache.xml.security.stax.impl.processor.input.AbstractDecryptInputProcessor
    protected void handleSecurityToken(InboundSecurityToken inboundSecurityToken, InboundSecurityContext inboundSecurityContext, EncryptedDataType encryptedDataType) throws XMLSecurityException {
        inboundSecurityToken.addTokenUsage(SecurityTokenConstants.TokenUsage_Encryption);
        TokenSecurityEvent<?> tokenSecurityEvent = XMLSecurityUtils.createTokenSecurityEvent(inboundSecurityToken, encryptedDataType.getId());
        inboundSecurityContext.registerSecurityEvent(tokenSecurityEvent);
    }

    @Override // org.apache.xml.security.stax.impl.processor.input.AbstractDecryptInputProcessor
    protected void handleEncryptedContent(InputProcessorChain inputProcessorChain, XMLSecStartElement parentXMLSecStartElement, InboundSecurityToken inboundSecurityToken, EncryptedDataType encryptedDataType) throws XMLSecurityException {
        DocumentContext documentContext = inputProcessorChain.getDocumentContext();
        List<QName> elementPath = parentXMLSecStartElement.getElementPath();
        ContentEncryptedElementSecurityEvent contentEncryptedElementSecurityEvent = new ContentEncryptedElementSecurityEvent(inboundSecurityToken, true, documentContext.getProtectionOrder());
        contentEncryptedElementSecurityEvent.setElementPath(elementPath);
        contentEncryptedElementSecurityEvent.setXmlSecEvent(parentXMLSecStartElement);
        contentEncryptedElementSecurityEvent.setSecurityToken(inboundSecurityToken);
        contentEncryptedElementSecurityEvent.setCorrelationID(encryptedDataType.getId());
        inputProcessorChain.getSecurityContext().registerSecurityEvent(contentEncryptedElementSecurityEvent);
    }

    @Override // org.apache.xml.security.stax.impl.processor.input.AbstractDecryptInputProcessor
    protected void handleCipherReference(InputProcessorChain inputProcessorChain, EncryptedDataType encryptedDataType, Cipher cipher, InboundSecurityToken inboundSecurityToken) throws XMLSecurityException {
        throw new XMLSecurityException("errorMessages.NotYetImplementedException");
    }

    @Override // org.apache.xml.security.stax.impl.processor.input.AbstractDecryptInputProcessor
    protected InputStream handleXOPInclude(InputProcessorChain inputProcessorChain, EncryptedDataType encryptedDataType, String href, Cipher cipher, InboundSecurityToken inboundSecurityToken) throws XMLSecurityException {
        throw new XMLSecurityException("errorMessages.NotYetImplementedException");
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/input/XMLDecryptInputProcessor$DecryptedEventReaderInputProcessor.class */
    public class DecryptedEventReaderInputProcessor extends AbstractDecryptInputProcessor.AbstractDecryptedEventReaderInputProcessor {
        public DecryptedEventReaderInputProcessor(XMLSecurityProperties securityProperties, SecurePart.Modifier encryptionModifier, boolean encryptedHeader, XMLSecStartElement xmlSecStartElement, EncryptedDataType encryptedDataType, XMLDecryptInputProcessor decryptInputProcessor, InboundSecurityToken inboundSecurityToken) {
            super(securityProperties, encryptionModifier, encryptedHeader, xmlSecStartElement, encryptedDataType, decryptInputProcessor, inboundSecurityToken);
        }

        @Override // org.apache.xml.security.stax.impl.processor.input.AbstractDecryptInputProcessor.AbstractDecryptedEventReaderInputProcessor
        protected void handleEncryptedElement(InputProcessorChain inputProcessorChain, XMLSecStartElement xmlSecStartElement, InboundSecurityToken inboundSecurityToken, EncryptedDataType encryptedDataType) throws XMLSecurityException {
            DocumentContext documentContext = inputProcessorChain.getDocumentContext();
            List<QName> elementPath = xmlSecStartElement.getElementPath();
            EncryptedElementSecurityEvent encryptedElementSecurityEvent = new EncryptedElementSecurityEvent(inboundSecurityToken, true, documentContext.getProtectionOrder());
            encryptedElementSecurityEvent.setElementPath(elementPath);
            encryptedElementSecurityEvent.setXmlSecEvent(xmlSecStartElement);
            encryptedElementSecurityEvent.setSecurityToken(inboundSecurityToken);
            encryptedElementSecurityEvent.setCorrelationID(encryptedDataType.getId());
            inputProcessorChain.getSecurityContext().registerSecurityEvent(encryptedElementSecurityEvent);
        }
    }
}
