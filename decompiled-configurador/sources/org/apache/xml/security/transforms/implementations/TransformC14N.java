package org.apache.xml.security.transforms.implementations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/transforms/implementations/TransformC14N.class */
public class TransformC14N extends TransformSpi {
    @Override // org.apache.xml.security.transforms.TransformSpi
    protected String engineGetURI() {
        return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    }

    @Override // org.apache.xml.security.transforms.TransformSpi
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws CanonicalizationException {
        Canonicalizer20010315 c14n = getCanonicalizer();
        if (os == null && (input.isOctetStream() || input.isElement() || input.isNodeSet())) {
            try {
                ByteArrayOutputStream writer = new ByteArrayOutputStream();
                Throwable th = null;
                try {
                    try {
                        c14n.engineCanonicalize(input, writer, secureValidation);
                        writer.flush();
                        XMLSignatureInput output = new XMLSignatureInput(writer.toByteArray());
                        output.setSecureValidation(secureValidation);
                        if (0 != 0) {
                            try {
                                writer.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            writer.close();
                        }
                        return output;
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (th != null) {
                        try {
                            writer.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        writer.close();
                    }
                    throw th4;
                }
            } catch (IOException ex) {
                throw new CanonicalizationException("empty", new Object[]{ex.getMessage()});
            }
        }
        c14n.engineCanonicalize(input, os, secureValidation);
        XMLSignatureInput output2 = new XMLSignatureInput((byte[]) null);
        output2.setSecureValidation(secureValidation);
        output2.setOutputStream(os);
        return output2;
    }

    protected Canonicalizer20010315 getCanonicalizer() {
        return new Canonicalizer20010315OmitComments();
    }
}
