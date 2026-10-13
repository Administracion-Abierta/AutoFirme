package org.apache.xml.security.transforms;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/transforms/TransformSpi.class */
public abstract class TransformSpi {
    protected abstract XMLSignatureInput enginePerformTransform(XMLSignatureInput xMLSignatureInput, OutputStream outputStream, Element element, String str, boolean z) throws CanonicalizationException, InvalidCanonicalizerException, ParserConfigurationException, TransformationException, SAXException, IOException;

    protected abstract String engineGetURI();
}
