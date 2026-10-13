package org.apache.xml.security.stax.impl.transformer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.processor.input.XMLEventReaderInputProcessor;
import org.apache.xml.security.utils.UnsyncByteArrayInputStream;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/transformer/TransformIdentity.class */
public class TransformIdentity implements Transformer {
    private static XMLOutputFactory xmlOutputFactory;
    private static XMLInputFactory xmlInputFactory;
    private OutputStream outputStream;
    private XMLEventWriter xmlEventWriterForOutputStream;
    private Transformer transformer;
    private ChildOutputMethod childOutputMethod;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/transformer/TransformIdentity$ChildOutputMethod.class */
    interface ChildOutputMethod {
        void transform(Object obj) throws XMLStreamException;

        void doFinal() throws XMLStreamException;
    }

    protected static XMLOutputFactory getXmlOutputFactory() {
        synchronized (TransformIdentity.class) {
            if (xmlOutputFactory == null) {
                xmlOutputFactory = XMLOutputFactory.newInstance();
            }
        }
        return xmlOutputFactory;
    }

    public static XMLInputFactory getXmlInputFactory() {
        synchronized (TransformIdentity.class) {
            if (xmlInputFactory == null) {
                xmlInputFactory = XMLInputFactory.newInstance();
                xmlInputFactory.setProperty("javax.xml.stream.supportDTD", false);
                xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
            }
        }
        return xmlInputFactory;
    }

    @Override // org.apache.xml.security.stax.ext.Transformer
    public void setOutputStream(OutputStream outputStream) throws XMLSecurityException {
        this.outputStream = outputStream;
    }

    protected OutputStream getOutputStream() {
        return this.outputStream;
    }

    protected XMLEventWriter getXmlEventWriterForOutputStream() throws XMLStreamException {
        if (this.xmlEventWriterForOutputStream != null) {
            return this.xmlEventWriterForOutputStream;
        }
        if (this.outputStream != null) {
            XMLEventWriter xMLEventWriterCreateXMLEventWriter = getXmlOutputFactory().createXMLEventWriter(new FilterOutputStream(this.outputStream) { // from class: org.apache.xml.security.stax.impl.transformer.TransformIdentity.1
                @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                    super.flush();
                }
            });
            this.xmlEventWriterForOutputStream = xMLEventWriterCreateXMLEventWriter;
            return xMLEventWriterCreateXMLEventWriter;
        }
        return null;
    }

    @Override // org.apache.xml.security.stax.ext.Transformer
    public void setTransformer(Transformer transformer) throws XMLSecurityException {
        this.transformer = transformer;
    }

    protected Transformer getTransformer() {
        return this.transformer;
    }

    @Override // org.apache.xml.security.stax.ext.Transformer
    public void setProperties(Map<String, Object> properties) throws XMLSecurityException {
        throw new UnsupportedOperationException("no properties supported");
    }

    @Override // org.apache.xml.security.stax.ext.Transformer
    public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput) {
        switch (forInput) {
            case XMLSecEvent:
                return XMLSecurityConstants.TransformMethod.XMLSecEvent;
            case InputStream:
                return XMLSecurityConstants.TransformMethod.InputStream;
            default:
                throw new IllegalArgumentException("Unsupported class " + forInput.name());
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.Transformer
    public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        if (getXmlEventWriterForOutputStream() != null) {
            getXmlEventWriterForOutputStream().add(xmlSecEvent);
            return;
        }
        if (this.childOutputMethod == null) {
            XMLSecurityConstants.TransformMethod preferredChildTransformMethod = getTransformer().getPreferredTransformMethod(XMLSecurityConstants.TransformMethod.XMLSecEvent);
            if (preferredChildTransformMethod == XMLSecurityConstants.TransformMethod.XMLSecEvent) {
                this.childOutputMethod = new ChildOutputMethod() { // from class: org.apache.xml.security.stax.impl.transformer.TransformIdentity.2
                    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                    public void transform(Object object) throws XMLStreamException {
                        TransformIdentity.this.getTransformer().transform((XMLSecEvent) object);
                    }

                    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                    public void doFinal() throws XMLStreamException {
                        TransformIdentity.this.getTransformer().doFinal();
                    }
                };
            } else if (preferredChildTransformMethod == XMLSecurityConstants.TransformMethod.InputStream) {
                this.childOutputMethod = new ChildOutputMethod() { // from class: org.apache.xml.security.stax.impl.transformer.TransformIdentity.3
                    private UnsyncByteArrayOutputStream baos;
                    private XMLEventWriter xmlEventWriter;

                    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                    public void transform(Object object) throws XMLStreamException {
                        if (this.xmlEventWriter == null) {
                            this.baos = new UnsyncByteArrayOutputStream();
                            this.xmlEventWriter = TransformIdentity.getXmlOutputFactory().createXMLEventWriter(this.baos);
                        }
                        this.xmlEventWriter.add((XMLSecEvent) object);
                    }

                    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                    public void doFinal() throws XMLStreamException {
                        this.xmlEventWriter.close();
                        try {
                            InputStream is = new UnsyncByteArrayInputStream(this.baos.toByteArray());
                            Throwable th = null;
                            try {
                                TransformIdentity.this.getTransformer().transform(is);
                                TransformIdentity.this.getTransformer().doFinal();
                                if (0 != 0) {
                                    try {
                                        is.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    is.close();
                                }
                            } catch (Throwable th3) {
                                if (0 != 0) {
                                    try {
                                        is.close();
                                    } catch (Throwable th4) {
                                        th.addSuppressed(th4);
                                    }
                                } else {
                                    is.close();
                                }
                                throw th3;
                            }
                        } catch (IOException ex) {
                            throw new XMLStreamException(ex);
                        }
                    }
                };
            }
        }
        if (this.childOutputMethod != null) {
            this.childOutputMethod.transform(xmlSecEvent);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.Transformer
    public void transform(final InputStream inputStream) throws XMLStreamException {
        if (getOutputStream() != null) {
            try {
                XMLSecurityUtils.copy(inputStream, getOutputStream());
                return;
            } catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }
        if (this.childOutputMethod == null) {
            XMLSecurityConstants.TransformMethod preferredChildTransformMethod = getTransformer().getPreferredTransformMethod(XMLSecurityConstants.TransformMethod.InputStream);
            if (preferredChildTransformMethod == XMLSecurityConstants.TransformMethod.XMLSecEvent) {
                this.childOutputMethod = new ChildOutputMethod() { // from class: org.apache.xml.security.stax.impl.transformer.TransformIdentity.4
                    private XMLEventReaderInputProcessor xmlEventReaderInputProcessor;

                    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                    public void transform(Object object) throws XMLStreamException {
                        XMLSecEvent xmlSecEvent;
                        if (this.xmlEventReaderInputProcessor == null) {
                            this.xmlEventReaderInputProcessor = new XMLEventReaderInputProcessor(null, TransformIdentity.getXmlInputFactory().createXMLStreamReader(inputStream));
                        }
                        do {
                            try {
                                xmlSecEvent = this.xmlEventReaderInputProcessor.processEvent(null);
                                TransformIdentity.this.getTransformer().transform(xmlSecEvent);
                            } catch (XMLSecurityException e2) {
                                throw new XMLStreamException(e2);
                            }
                        } while (xmlSecEvent.getEventType() != 8);
                    }

                    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                    public void doFinal() throws XMLStreamException {
                        TransformIdentity.this.getTransformer().doFinal();
                    }
                };
            } else if (preferredChildTransformMethod == XMLSecurityConstants.TransformMethod.InputStream) {
                this.childOutputMethod = new ChildOutputMethod() { // from class: org.apache.xml.security.stax.impl.transformer.TransformIdentity.5
                    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                    public void transform(Object object) throws XMLStreamException {
                        TransformIdentity.this.getTransformer().transform(inputStream);
                    }

                    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                    public void doFinal() throws XMLStreamException {
                        TransformIdentity.this.getTransformer().doFinal();
                    }
                };
            }
        }
        if (this.childOutputMethod != null) {
            this.childOutputMethod.transform(inputStream);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.Transformer
    public void doFinal() throws XMLStreamException {
        if (this.xmlEventWriterForOutputStream != null) {
            this.xmlEventWriterForOutputStream.close();
        }
        if (this.childOutputMethod != null) {
            this.childOutputMethod.doFinal();
        } else if (this.transformer != null) {
            this.transformer.doFinal();
        }
    }
}
