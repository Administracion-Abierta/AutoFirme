package org.apache.xml.security.stax.impl.processor.output;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;
import org.apache.xml.security.stax.securityEvent.SignatureValueSecurityEvent;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/output/XMLSignatureEndingOutputProcessor.class */
public class XMLSignatureEndingOutputProcessor extends AbstractSignatureEndingOutputProcessor {
    private AbstractSignatureEndingOutputProcessor.SignedInfoProcessor signedInfoProcessor;

    public XMLSignatureEndingOutputProcessor(XMLSignatureOutputProcessor signatureOutputProcessor) throws XMLSecurityException {
        super(signatureOutputProcessor);
        addAfterProcessor(XMLSignatureOutputProcessor.class);
    }

    @Override // org.apache.xml.security.stax.impl.processor.output.AbstractSignatureEndingOutputProcessor
    protected AbstractSignatureEndingOutputProcessor.SignedInfoProcessor newSignedInfoProcessor(SignatureAlgorithm signatureAlgorithm, String signatureId, XMLSecStartElement xmlSecStartElement, OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        this.signedInfoProcessor = new AbstractSignatureEndingOutputProcessor.SignedInfoProcessor(signatureAlgorithm, signatureId, xmlSecStartElement);
        this.signedInfoProcessor.setXMLSecurityProperties(getSecurityProperties());
        this.signedInfoProcessor.setAction(getAction(), getActionOrder());
        this.signedInfoProcessor.addAfterProcessor(XMLSignatureEndingOutputProcessor.class);
        this.signedInfoProcessor.init(outputProcessorChain);
        return this.signedInfoProcessor;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.processor.output.AbstractSignatureEndingOutputProcessor, org.apache.xml.security.stax.ext.AbstractBufferingOutputProcessor
    public void processHeaderEvent(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        super.processHeaderEvent(outputProcessorChain);
        SignatureValueSecurityEvent signatureValueSecurityEvent = new SignatureValueSecurityEvent();
        signatureValueSecurityEvent.setSignatureValue(this.signedInfoProcessor.getSignatureValue());
        signatureValueSecurityEvent.setCorrelationID(this.signedInfoProcessor.getSignatureId());
        outputProcessorChain.getSecurityContext().registerSecurityEvent(signatureValueSecurityEvent);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.AbstractBufferingOutputProcessor
    protected void flushBufferAndCallbackAfterHeader(OutputProcessorChain outputProcessorChain, Deque<XMLSecEvent> xmlSecEventDeque) throws XMLSecurityException, XMLStreamException {
        XMLSecEvent xmlSecEvent;
        XMLSecEvent xMLSecEventPop = xmlSecEventDeque.pop();
        while (true) {
            xmlSecEvent = xMLSecEventPop;
            if (xmlSecEvent.isStartElement()) {
                break;
            }
            outputProcessorChain.reset();
            outputProcessorChain.processEvent(xmlSecEvent);
            xMLSecEventPop = xmlSecEventDeque.pop();
        }
        outputProcessorChain.reset();
        outputProcessorChain.processEvent(xmlSecEvent);
        int depth = 0;
        QName signaturePositionQName = getSecurityProperties().getSignaturePositionQName();
        boolean start = getSecurityProperties().isSignaturePositionStart();
        if (signaturePositionQName != null) {
            while (!xmlSecEventDeque.isEmpty() && ((!start || !xmlSecEvent.isStartElement() || !xmlSecEvent.mo1376asStartElement().getName().equals(signaturePositionQName)) && (start || !xmlSecEvent.isEndElement() || !xmlSecEvent.mo1375asEndElement().getName().equals(signaturePositionQName)))) {
                xmlSecEvent = xmlSecEventDeque.pop();
                if (xmlSecEvent.isStartElement()) {
                    depth++;
                } else if (xmlSecEvent.isEndElement()) {
                    depth--;
                    if (depth < 0) {
                        xmlSecEventDeque.push(xmlSecEvent);
                        break;
                    }
                } else {
                    continue;
                }
                outputProcessorChain.reset();
                outputProcessorChain.processEvent(xmlSecEvent);
            }
        } else {
            int signaturePosition = getSecurityProperties().getSignaturePosition();
            if (signaturePosition < 0) {
                signaturePosition = 0;
            }
            int position = 0;
            while (position != signaturePosition) {
                XMLSecEvent xmlSecEvent2 = xmlSecEventDeque.pop();
                if (xmlSecEvent2.isStartElement()) {
                    depth++;
                } else if (xmlSecEvent2.isEndElement()) {
                    depth--;
                    if (depth == 0) {
                        position++;
                    } else if (depth < 0) {
                        xmlSecEventDeque.push(xmlSecEvent2);
                        break;
                    }
                } else {
                    continue;
                }
                outputProcessorChain.reset();
                outputProcessorChain.processEvent(xmlSecEvent2);
            }
        }
        super.flushBufferAndCallbackAfterHeader(outputProcessorChain, xmlSecEventDeque);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.processor.output.AbstractSignatureEndingOutputProcessor
    protected void createKeyInfoStructureForSignature(OutputProcessorChain outputProcessorChain, OutboundSecurityToken securityToken, boolean useSingleCertificate) throws XMLSecurityException, XMLStreamException {
        X509Certificate[] x509Certificates = securityToken.getX509Certificates();
        if (x509Certificates != null) {
            if (getSecurityProperties().getSignatureKeyIdentifiers().isEmpty()) {
                XMLSecurityUtils.createX509IssuerSerialStructure(this, outputProcessorChain, x509Certificates);
                return;
            }
            List<SecurityTokenConstants.KeyIdentifier> keyIdentifiers = getSecurityProperties().getSignatureKeyIdentifiers();
            if (keyIdentifiers.remove(SecurityTokenConstants.KeyIdentifier_KeyName)) {
                String keyName = getSecurityProperties().getSignatureKeyName();
                XMLSecurityUtils.createKeyNameTokenStructure(this, outputProcessorChain, keyName);
            }
            if (keyIdentifiers.remove(SecurityTokenConstants.KeyIdentifier_KeyValue)) {
                XMLSecurityUtils.createKeyValueTokenStructure(this, outputProcessorChain, x509Certificates);
            }
            if (!keyIdentifiers.isEmpty()) {
                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data, true, (List<XMLSecAttribute>) null);
                for (SecurityTokenConstants.KeyIdentifier keyIdentifier : keyIdentifiers) {
                    if (SecurityTokenConstants.KeyIdentifier_IssuerSerial.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509IssuerSerialStructure(this, outputProcessorChain, x509Certificates, false);
                    } else if (SecurityTokenConstants.KeyIdentifier_SkiKeyIdentifier.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509SubjectKeyIdentifierStructure(this, outputProcessorChain, x509Certificates, false);
                    } else if (SecurityTokenConstants.KeyIdentifier_X509KeyIdentifier.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509CertificateStructure(this, outputProcessorChain, x509Certificates, false);
                    } else if (SecurityTokenConstants.KeyIdentifier_X509SubjectName.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509SubjectNameStructure(this, outputProcessorChain, x509Certificates, false);
                    } else if (!SecurityTokenConstants.KeyIdentifier_KeyName.equals(keyIdentifier) && !SecurityTokenConstants.KeyIdentifier_KeyValue.equals(keyIdentifier)) {
                        throw new XMLSecurityException("stax.unsupportedToken", new Object[]{keyIdentifier});
                    }
                }
                createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data);
                return;
            }
            return;
        }
        if (securityToken.getPublicKey() != null) {
            XMLSecurityUtils.createKeyValueTokenStructure(this, outputProcessorChain, securityToken.getPublicKey());
        }
    }

    @Override // org.apache.xml.security.stax.impl.processor.output.AbstractSignatureEndingOutputProcessor
    protected void createTransformsStructureForSignature(OutputProcessorChain subOutputProcessorChain, SignaturePartDef signaturePartDef) throws XMLSecurityException, XMLStreamException {
        if (signaturePartDef.getTransforms() != null) {
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Transforms, false, (List<XMLSecAttribute>) null);
            String[] transforms = signaturePartDef.getTransforms();
            for (String transform : transforms) {
                if (shouldIncludeTransform(transform)) {
                    List<XMLSecAttribute> attributes = new ArrayList<>(1);
                    attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, transform));
                    createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Transform, false, attributes);
                    if (getSecurityProperties().isAddExcC14NInclusivePrefixes()) {
                        List<XMLSecAttribute> attributes2 = new ArrayList<>(1);
                        attributes2.add(createAttribute(XMLSecurityConstants.ATT_NULL_PrefixList, signaturePartDef.getInclusiveNamespacesPrefixes()));
                        createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_c14nExcl_InclusiveNamespaces, true, attributes2);
                        createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_c14nExcl_InclusiveNamespaces);
                    }
                    createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Transform);
                }
            }
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Transforms);
        }
    }

    private boolean shouldIncludeTransform(String transform) {
        boolean include = true;
        if (!this.securityProperties.isSignatureIncludeDigestTransform() && !transform.equals("http://www.w3.org/2000/09/xmldsig#enveloped-signature")) {
            include = false;
        }
        return include;
    }
}
