package org.apache.xml.security.c14n;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/c14n/CanonicalizerSpi.class */
public abstract class CanonicalizerSpi {
    public abstract String engineGetURI();

    public abstract void engineCanonicalizeXPathNodeSet(Set<Node> set, OutputStream outputStream) throws CanonicalizationException;

    public abstract void engineCanonicalizeXPathNodeSet(Set<Node> set, String str, OutputStream outputStream) throws CanonicalizationException;

    public abstract void engineCanonicalizeSubTree(Node node, OutputStream outputStream) throws CanonicalizationException;

    public abstract void engineCanonicalizeSubTree(Node node, String str, OutputStream outputStream) throws CanonicalizationException;

    public abstract void engineCanonicalizeSubTree(Node node, String str, boolean z, OutputStream outputStream) throws CanonicalizationException;

    public void engineCanonicalize(byte[] inputBytes, OutputStream writer, boolean secureValidation) throws CanonicalizationException, IOException, XMLParserException {
        InputStream bais = new ByteArrayInputStream(inputBytes);
        Throwable th = null;
        try {
            Document document = XMLUtils.read(bais, secureValidation);
            if (0 != 0) {
                try {
                    bais.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            } else {
                bais.close();
            }
            engineCanonicalizeSubTree(document, writer);
        } catch (Throwable th3) {
            if (0 != 0) {
                try {
                    bais.close();
                } catch (Throwable th4) {
                    th.addSuppressed(th4);
                }
            } else {
                bais.close();
            }
            throw th3;
        }
    }
}
