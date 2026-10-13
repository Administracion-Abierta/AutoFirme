package org.apache.jcp.xml.dsig.internal.dom;

import java.io.IOException;
import javax.xml.crypto.OctetStreamData;
import org.apache.xml.security.signature.XMLSignatureInput;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/ApacheOctetStreamData.class */
public class ApacheOctetStreamData extends OctetStreamData implements ApacheData {
    private XMLSignatureInput xi;

    public ApacheOctetStreamData(XMLSignatureInput xi) throws IOException {
        super(xi.getOctetStream(), xi.getSourceURI(), xi.getMIMEType());
        this.xi = xi;
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.ApacheData
    public XMLSignatureInput getXMLSignatureInput() {
        return this.xi;
    }
}
