package org.apache.xml.security.stax.impl.transformer;

import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/transformer/TransformEnvelopedSignature.class */
public class TransformEnvelopedSignature extends TransformIdentity {
    private int curLevel;
    private int sigElementLevel = -1;

    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput) {
        switch (forInput) {
            case XMLSecEvent:
                return XMLSecurityConstants.TransformMethod.XMLSecEvent;
            case InputStream:
                return XMLSecurityConstants.TransformMethod.XMLSecEvent;
            default:
                throw new IllegalArgumentException("Unsupported class " + forInput.name());
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        if (1 == xmlSecEvent.getEventType()) {
            this.curLevel++;
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.mo1376asStartElement();
            if (XMLSecurityConstants.TAG_dsig_Signature.equals(xmlSecStartElement.getName())) {
                this.sigElementLevel = this.curLevel;
                return;
            }
        } else if (2 == xmlSecEvent.getEventType()) {
            XMLSecEndElement xmlSecEndElement = xmlSecEvent.mo1375asEndElement();
            if (this.sigElementLevel == this.curLevel && XMLSecurityConstants.TAG_dsig_Signature.equals(xmlSecEndElement.getName())) {
                this.sigElementLevel = -1;
                return;
            }
            this.curLevel--;
        }
        if (this.sigElementLevel == -1) {
            super.transform(xmlSecEvent);
        }
    }

    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void transform(InputStream inputStream) throws XMLStreamException {
        throw new UnsupportedOperationException("transform(InputStream) not supported");
    }
}
