package org.apache.xml.security.stax.impl.processor.output;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/output/XMLSignatureOutputProcessor.class */
public class XMLSignatureOutputProcessor extends AbstractSignatureOutputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) XMLSignatureOutputProcessor.class);

    @Override // org.apache.xml.security.stax.ext.AbstractOutputProcessor, org.apache.xml.security.stax.ext.OutputProcessor
    public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        super.init(outputProcessorChain);
        XMLSignatureEndingOutputProcessor signatureEndingOutputProcessor = new XMLSignatureEndingOutputProcessor(this);
        signatureEndingOutputProcessor.setXMLSecurityProperties(getSecurityProperties());
        signatureEndingOutputProcessor.setAction(getAction(), getActionOrder());
        signatureEndingOutputProcessor.init(outputProcessorChain);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.processor.output.AbstractSignatureOutputProcessor, org.apache.xml.security.stax.ext.OutputProcessor
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        SecurePart securePart;
        if (xmlSecEvent.getEventType() == 1) {
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.mo1376asStartElement();
            if (getActiveInternalSignatureOutputProcessor() == null && (securePart = securePartMatches(xmlSecStartElement, outputProcessorChain, XMLSecurityConstants.SIGNATURE_PARTS)) != null) {
                LOG.debug("Matched securePart for signature");
                SignaturePartDef signaturePartDef = new SignaturePartDef();
                signaturePartDef.setSecurePart(securePart);
                signaturePartDef.setTransforms(securePart.getTransforms());
                if (signaturePartDef.getTransforms() == null) {
                    signaturePartDef.setTransforms(new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"});
                }
                signaturePartDef.setExcludeVisibleC14Nprefixes(true);
                signaturePartDef.setDigestAlgo(securePart.getDigestMethod());
                if (signaturePartDef.getDigestAlgo() == null) {
                    signaturePartDef.setDigestAlgo(getSecurityProperties().getSignatureDigestAlgorithm());
                }
                if (this.securityProperties.isSignatureGenerateIds()) {
                    if (securePart.getIdToSecure() == null) {
                        signaturePartDef.setGenerateXPointer(securePart.isGenerateXPointer());
                        signaturePartDef.setSigRefId(IDGenerator.generateID(null));
                        Attribute attribute = xmlSecStartElement.getAttributeByName(this.securityProperties.getIdAttributeNS());
                        if (attribute != null) {
                            signaturePartDef.setSigRefId(attribute.getValue());
                        } else {
                            List<XMLSecAttribute> attributeList = new ArrayList<>(1);
                            attributeList.add(createAttribute(this.securityProperties.getIdAttributeNS(), signaturePartDef.getSigRefId()));
                            xmlSecEvent = addAttributes(xmlSecStartElement, attributeList);
                        }
                    } else {
                        signaturePartDef.setSigRefId(securePart.getIdToSecure());
                    }
                }
                getSignaturePartDefList().add(signaturePartDef);
                AbstractSignatureOutputProcessor.InternalSignatureOutputProcessor internalSignatureOutputProcessor = new AbstractSignatureOutputProcessor.InternalSignatureOutputProcessor(signaturePartDef, xmlSecStartElement);
                internalSignatureOutputProcessor.setXMLSecurityProperties(getSecurityProperties());
                internalSignatureOutputProcessor.setAction(getAction(), getActionOrder());
                internalSignatureOutputProcessor.addAfterProcessor(XMLSignatureOutputProcessor.class);
                internalSignatureOutputProcessor.addBeforeProcessor(XMLSignatureEndingOutputProcessor.class);
                internalSignatureOutputProcessor.init(outputProcessorChain);
                setActiveInternalSignatureOutputProcessor(internalSignatureOutputProcessor);
            }
        }
        outputProcessorChain.processEvent(xmlSecEvent);
    }
}
