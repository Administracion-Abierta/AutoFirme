package org.apache.xml.security.stax.impl.processor.input;

import org.apache.xml.security.binding.xmldsig.SignatureType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SignatureValueSecurityEvent;
import org.apache.xml.security.stax.securityEvent.TokenSecurityEvent;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/input/XMLSignatureInputHandler.class */
public class XMLSignatureInputHandler extends AbstractSignatureInputHandler {
    @Override // org.apache.xml.security.stax.impl.processor.input.AbstractSignatureInputHandler
    protected AbstractSignatureInputHandler.SignatureVerifier newSignatureVerifier(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties, SignatureType signatureType) throws XMLSecurityException {
        InboundSecurityContext inboundSecurityContext = inputProcessorChain.getSecurityContext();
        AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
        algorithmSuiteSecurityEvent.setAlgorithmURI(signatureType.getSignedInfo().getCanonicalizationMethod().getAlgorithm());
        algorithmSuiteSecurityEvent.setAlgorithmUsage(XMLSecurityConstants.SigC14n);
        algorithmSuiteSecurityEvent.setCorrelationID(signatureType.getId());
        inboundSecurityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
        SignatureValueSecurityEvent signatureValueSecurityEvent = new SignatureValueSecurityEvent();
        signatureValueSecurityEvent.setSignatureValue(signatureType.getSignatureValue().getValue());
        signatureValueSecurityEvent.setCorrelationID(signatureType.getId());
        inboundSecurityContext.registerSecurityEvent(signatureValueSecurityEvent);
        return new XMLSignatureVerifier(signatureType, inboundSecurityContext, securityProperties);
    }

    @Override // org.apache.xml.security.stax.impl.processor.input.AbstractSignatureInputHandler
    protected void addSignatureReferenceInputProcessorToChain(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties, SignatureType signatureType, InboundSecurityToken inboundSecurityToken) throws XMLSecurityException {
        inputProcessorChain.addProcessor(new XMLSignatureReferenceVerifyInputProcessor(inputProcessorChain, signatureType, inboundSecurityToken, securityProperties));
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/input/XMLSignatureInputHandler$XMLSignatureVerifier.class */
    public class XMLSignatureVerifier extends AbstractSignatureInputHandler.SignatureVerifier {
        public XMLSignatureVerifier(SignatureType signatureType, InboundSecurityContext inboundSecurityContext, XMLSecurityProperties securityProperties) throws XMLSecurityException {
            super(signatureType, inboundSecurityContext, securityProperties);
        }

        @Override // org.apache.xml.security.stax.impl.processor.input.AbstractSignatureInputHandler.SignatureVerifier
        protected InboundSecurityToken retrieveSecurityToken(SignatureType signatureType, XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException {
            InboundSecurityToken inboundSecurityToken = SecurityTokenFactory.getInstance().getSecurityToken(signatureType.getKeyInfo(), SecurityTokenConstants.KeyUsage_Signature_Verification, securityProperties, inboundSecurityContext);
            inboundSecurityToken.verify();
            inboundSecurityToken.addTokenUsage(SecurityTokenConstants.TokenUsage_Signature);
            TokenSecurityEvent<?> tokenSecurityEvent = XMLSecurityUtils.createTokenSecurityEvent(inboundSecurityToken, signatureType.getId());
            inboundSecurityContext.registerSecurityEvent(tokenSecurityEvent);
            return inboundSecurityToken;
        }
    }
}
