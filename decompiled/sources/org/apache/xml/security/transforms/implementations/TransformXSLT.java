package org.apache.xml.security.transforms.implementations;

import com.aowagie.text.xml.XMLConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/transforms/implementations/TransformXSLT.class */
public class TransformXSLT extends TransformSpi {
    static final String XSLTSpecNS = "http://www.w3.org/1999/XSL/Transform";
    static final String defaultXSLTSpecNSprefix = "xslt";
    static final String XSLTSTYLESHEET = "stylesheet";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) TransformXSLT.class);

    @Override // org.apache.xml.security.transforms.TransformSpi
    protected String engineGetURI() {
        return Transforms.TRANSFORM_XSLT;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v18, types: [java.lang.Throwable, javax.xml.transform.Transformer] */
    /* JADX WARN: Type inference failed for: r1v17, types: [java.lang.AutoCloseable] */
    @Override // org.apache.xml.security.transforms.TransformSpi
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput xMLSignatureInput, OutputStream outputStream, Element element, String str, boolean z) throws TransformationException, IOException {
        Object obj;
        try {
            Element elementSelectNode = XMLUtils.selectNode(element.getFirstChild(), XSLTSpecNS, XSLTSTYLESHEET, 0);
            if (elementSelectNode == null) {
                elementSelectNode = XMLUtils.selectNode(element.getFirstChild(), XSLTSpecNS, "transform", 0);
            }
            if (elementSelectNode == null) {
                throw new TransformationException("xml.WrongContent", new Object[]{"xslt:stylesheet", Constants._TAG_TRANSFORM});
            }
            TransformerFactory transformerFactoryNewInstance = TransformerFactory.newInstance();
            transformerFactoryNewInstance.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
            if (z) {
                try {
                    transformerFactoryNewInstance.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                    transformerFactoryNewInstance.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                } catch (IllegalArgumentException e) {
                }
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Throwable th = null;
            try {
                try {
                    ?? NewTransformer = transformerFactoryNewInstance.newTransformer();
                    NewTransformer.transform(new DOMSource(elementSelectNode), new StreamResult(byteArrayOutputStream));
                    StreamSource streamSource = new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                    $closeResource(null, byteArrayOutputStream);
                    Transformer transformerNewTransformer = transformerFactoryNewInstance.newTransformer(streamSource);
                    try {
                        transformerNewTransformer.setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n");
                        obj = th;
                    } catch (Exception e2) {
                        LOG.warn("Unable to set Xalan line-separator property: " + e2.getMessage());
                        obj = e2;
                    }
                    try {
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xMLSignatureInput.getBytes());
                        StreamSource streamSource2 = new StreamSource(byteArrayInputStream);
                        if (outputStream == null) {
                            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                            try {
                                transformerNewTransformer.transform(streamSource2, new StreamResult(byteArrayOutputStream2));
                                XMLSignatureInput xMLSignatureInput2 = new XMLSignatureInput(byteArrayOutputStream2.toByteArray());
                                xMLSignatureInput2.setSecureValidation(z);
                                $closeResource(null, byteArrayOutputStream2);
                                $closeResource(null, byteArrayInputStream);
                                return xMLSignatureInput2;
                            } catch (Throwable th2) {
                                $closeResource(null, byteArrayOutputStream2);
                                throw th2;
                            }
                        }
                        transformerNewTransformer.transform(streamSource2, new StreamResult(outputStream));
                        $closeResource(null, byteArrayInputStream);
                        XMLSignatureInput xMLSignatureInput3 = new XMLSignatureInput((byte[]) null);
                        xMLSignatureInput3.setSecureValidation(z);
                        xMLSignatureInput3.setOutputStream(outputStream);
                        return xMLSignatureInput3;
                    } catch (Throwable th3) {
                        $closeResource(NewTransformer, obj);
                        throw th3;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    throw th4;
                }
            } catch (Throwable th5) {
                $closeResource(th, byteArrayOutputStream);
                throw th5;
            }
        } catch (TransformerException | XMLSecurityException e3) {
            throw new TransformationException(e3);
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) throws Exception {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }
}
