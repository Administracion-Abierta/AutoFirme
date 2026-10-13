package org.apache.xml.security.transforms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.implementations.TransformBase64Decode;
import org.apache.xml.security.transforms.implementations.TransformC14N;
import org.apache.xml.security.transforms.implementations.TransformC14N11;
import org.apache.xml.security.transforms.implementations.TransformC14N11_WithComments;
import org.apache.xml.security.transforms.implementations.TransformC14NExclusive;
import org.apache.xml.security.transforms.implementations.TransformC14NExclusiveWithComments;
import org.apache.xml.security.transforms.implementations.TransformC14NWithComments;
import org.apache.xml.security.transforms.implementations.TransformEnvelopedSignature;
import org.apache.xml.security.transforms.implementations.TransformXPath;
import org.apache.xml.security.transforms.implementations.TransformXPath2Filter;
import org.apache.xml.security.transforms.implementations.TransformXSLT;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/transforms/Transform.class */
public final class Transform extends SignatureElementProxy {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Transform.class);
    private static Map<String, TransformSpi> transformSpiHash = new ConcurrentHashMap();
    private final TransformSpi transformSpi;

    public Transform(Document doc, String algorithmURI) throws InvalidTransformException {
        this(doc, algorithmURI, (NodeList) null);
    }

    public Transform(Document doc, String algorithmURI, Element contextChild) throws InvalidTransformException {
        super(doc);
        setLocalAttribute("Algorithm", algorithmURI);
        this.transformSpi = initializeTransform(algorithmURI);
        if (contextChild != null) {
            HelperNodeList contextNodes = new HelperNodeList();
            XMLUtils.addReturnToElement(doc, contextNodes);
            contextNodes.appendChild(contextChild);
            XMLUtils.addReturnToElement(doc, contextNodes);
            int length = contextNodes.getLength();
            for (int i = 0; i < length; i++) {
                appendSelf(contextNodes.item(i).cloneNode(true));
            }
            LOG.debug("The NodeList is {}", contextNodes);
        }
    }

    public Transform(Document doc, String algorithmURI, NodeList contextNodes) throws InvalidTransformException {
        super(doc);
        setLocalAttribute("Algorithm", algorithmURI);
        this.transformSpi = initializeTransform(algorithmURI);
        if (contextNodes != null) {
            int length = contextNodes.getLength();
            for (int i = 0; i < length; i++) {
                appendSelf(contextNodes.item(i).cloneNode(true));
            }
            LOG.debug("The NodeList is {}", contextNodes);
        }
    }

    public Transform(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
        String algorithmURI = element.getAttributeNS(null, "Algorithm");
        if (algorithmURI == null || algorithmURI.length() == 0) {
            Object[] exArgs = {"Algorithm", Constants._TAG_TRANSFORM};
            throw new TransformationException("xml.WrongContent", exArgs);
        }
        this.transformSpi = initializeTransform(algorithmURI);
    }

    public static void register(String algorithmURI, String implementingClass) throws InvalidTransformException, AlgorithmAlreadyRegisteredException, ClassNotFoundException {
        JavaUtils.checkRegisterPermission();
        TransformSpi transformSpi = transformSpiHash.get(algorithmURI);
        if (transformSpi != null) {
            Object[] exArgs = {algorithmURI, transformSpi};
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }
        try {
            transformSpiHash.put(algorithmURI, (TransformSpi) JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(implementingClass, Transform.class)));
        } catch (IllegalAccessException | InstantiationException ex) {
            Object[] exArgs2 = {algorithmURI};
            throw new InvalidTransformException(ex, "signature.Transform.UnknownTransform", exArgs2);
        }
    }

    public static void register(String algorithmURI, Class<? extends TransformSpi> implementingClass) throws InvalidTransformException, AlgorithmAlreadyRegisteredException {
        JavaUtils.checkRegisterPermission();
        TransformSpi transformSpi = transformSpiHash.get(algorithmURI);
        if (transformSpi != null) {
            Object[] exArgs = {algorithmURI, transformSpi};
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }
        try {
            transformSpiHash.put(algorithmURI, (TransformSpi) JavaUtils.newInstanceWithEmptyConstructor(implementingClass));
        } catch (IllegalAccessException | InstantiationException ex) {
            Object[] exArgs2 = {algorithmURI};
            throw new InvalidTransformException(ex, "signature.Transform.UnknownTransform", exArgs2);
        }
    }

    public static void registerDefaultAlgorithms() {
        transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#base64", new TransformBase64Decode());
        transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", new TransformC14N());
        transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", new TransformC14NWithComments());
        transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11", new TransformC14N11());
        transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11#WithComments", new TransformC14N11_WithComments());
        transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#", new TransformC14NExclusive());
        transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#WithComments", new TransformC14NExclusiveWithComments());
        transformSpiHash.put(Transforms.TRANSFORM_XPATH, new TransformXPath());
        transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#enveloped-signature", new TransformEnvelopedSignature());
        transformSpiHash.put(Transforms.TRANSFORM_XSLT, new TransformXSLT());
        transformSpiHash.put("http://www.w3.org/2002/06/xmldsig-filter2", new TransformXPath2Filter());
    }

    public String getURI() {
        return getLocalAttribute("Algorithm");
    }

    public XMLSignatureInput performTransform(XMLSignatureInput input, boolean secureValidation) throws CanonicalizationException, InvalidCanonicalizerException, TransformationException, IOException {
        return performTransform(input, null, secureValidation);
    }

    public XMLSignatureInput performTransform(XMLSignatureInput input, OutputStream os, boolean secureValidation) throws CanonicalizationException, InvalidCanonicalizerException, TransformationException, IOException {
        try {
            XMLSignatureInput result = this.transformSpi.enginePerformTransform(input, os, getElement(), this.baseURI, secureValidation);
            return result;
        } catch (ParserConfigurationException ex) {
            Object[] exArgs = {getURI(), "ParserConfigurationException"};
            throw new CanonicalizationException(ex, "signature.Transform.ErrorDuringTransform", exArgs);
        } catch (SAXException ex2) {
            Object[] exArgs2 = {getURI(), "SAXException"};
            throw new CanonicalizationException(ex2, "signature.Transform.ErrorDuringTransform", exArgs2);
        }
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_TRANSFORM;
    }

    private TransformSpi initializeTransform(String algorithmURI) throws InvalidTransformException {
        TransformSpi newTransformSpi = transformSpiHash.get(algorithmURI);
        if (newTransformSpi == null) {
            Object[] exArgs = {algorithmURI};
            throw new InvalidTransformException("signature.Transform.UnknownTransform", exArgs);
        }
        LOG.debug("Create URI \"{}\" class \"{}\"", algorithmURI, newTransformSpi.getClass());
        return newTransformSpi;
    }
}
