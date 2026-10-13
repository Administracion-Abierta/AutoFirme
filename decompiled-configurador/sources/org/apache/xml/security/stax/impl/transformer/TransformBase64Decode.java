package org.apache.xml.security.stax.impl.transformer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.processor.input.XMLEventReaderInputProcessor;
import org.apache.xml.security.utils.UnsyncByteArrayInputStream;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/transformer/TransformBase64Decode.class */
public class TransformBase64Decode extends TransformIdentity {
    private TransformIdentity.ChildOutputMethod childOutputMethod;

    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void setOutputStream(OutputStream outputStream) throws XMLSecurityException {
        super.setOutputStream(new Base64OutputStream(new FilterOutputStream(outputStream) { // from class: org.apache.xml.security.stax.impl.transformer.TransformBase64Decode.1
            @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                super.flush();
            }
        }, false));
    }

    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput) {
        switch (forInput) {
            case XMLSecEvent:
                return XMLSecurityConstants.TransformMethod.InputStream;
            case InputStream:
                return XMLSecurityConstants.TransformMethod.InputStream;
            default:
                throw new IllegalArgumentException("Unsupported class " + forInput.name());
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        int eventType = xmlSecEvent.getEventType();
        if (4 == eventType) {
            if (getOutputStream() != null) {
                try {
                    getOutputStream().write(xmlSecEvent.mo456asCharacters().getData().getBytes());
                } catch (IOException e) {
                    throw new XMLStreamException(e);
                }
            } else if (this.childOutputMethod == null) {
                XMLSecurityConstants.TransformMethod preferredChildTransformMethod = getTransformer().getPreferredTransformMethod(XMLSecurityConstants.TransformMethod.XMLSecEvent);
                if (preferredChildTransformMethod == XMLSecurityConstants.TransformMethod.XMLSecEvent) {
                    this.childOutputMethod = new TransformIdentity.ChildOutputMethod() { // from class: org.apache.xml.security.stax.impl.transformer.TransformBase64Decode.2
                        private UnsyncByteArrayOutputStream byteArrayOutputStream;
                        private Base64OutputStream base64OutputStream;

                        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                        @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                        public void transform(Object object) throws XMLStreamException {
                            if (this.base64OutputStream == null) {
                                this.byteArrayOutputStream = new UnsyncByteArrayOutputStream();
                                this.base64OutputStream = new Base64OutputStream(this.byteArrayOutputStream, false);
                            }
                            try {
                                this.base64OutputStream.write((byte[]) object);
                            } catch (IOException e2) {
                                throw new XMLStreamException(e2);
                            }
                        }

                        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                        @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                        public void doFinal() throws XMLStreamException {
                            XMLSecEvent xmlSecEvent2;
                            try {
                                this.base64OutputStream.close();
                                try {
                                    InputStream is = new UnsyncByteArrayInputStream(this.byteArrayOutputStream.toByteArray());
                                    Throwable th = null;
                                    try {
                                        XMLEventReaderInputProcessor xmlEventReaderInputProcessor = new XMLEventReaderInputProcessor(null, TransformIdentity.getXmlInputFactory().createXMLStreamReader(is));
                                        do {
                                            xmlSecEvent2 = xmlEventReaderInputProcessor.processEvent(null);
                                            TransformBase64Decode.this.getTransformer().transform(xmlSecEvent2);
                                        } while (xmlSecEvent2.getEventType() != 8);
                                        if (0 != 0) {
                                            try {
                                                is.close();
                                            } catch (Throwable th2) {
                                                th.addSuppressed(th2);
                                            }
                                        } else {
                                            is.close();
                                        }
                                        TransformBase64Decode.this.getTransformer().doFinal();
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
                                } catch (IOException | XMLSecurityException e2) {
                                    throw new XMLStreamException(e2);
                                }
                            } catch (IOException e3) {
                                throw new XMLStreamException(e3);
                            }
                        }
                    };
                } else if (preferredChildTransformMethod == XMLSecurityConstants.TransformMethod.InputStream) {
                    this.childOutputMethod = new TransformIdentity.ChildOutputMethod() { // from class: org.apache.xml.security.stax.impl.transformer.TransformBase64Decode.3
                        private UnsyncByteArrayOutputStream byteArrayOutputStream;
                        private Base64OutputStream base64OutputStream;

                        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                        @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                        public void transform(Object object) throws XMLStreamException {
                            if (this.base64OutputStream == null) {
                                this.byteArrayOutputStream = new UnsyncByteArrayOutputStream();
                                this.base64OutputStream = new Base64OutputStream(this.byteArrayOutputStream, false);
                            }
                            try {
                                this.base64OutputStream.write((byte[]) object);
                            } catch (IOException e2) {
                                throw new XMLStreamException(e2);
                            }
                        }

                        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
                        @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity.ChildOutputMethod
                        public void doFinal() throws XMLStreamException {
                            try {
                                this.base64OutputStream.close();
                                try {
                                    InputStream is = new UnsyncByteArrayInputStream(this.byteArrayOutputStream.toByteArray());
                                    Throwable th = null;
                                    try {
                                        TransformBase64Decode.this.getTransformer().transform(is);
                                        TransformBase64Decode.this.getTransformer().doFinal();
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
                            } catch (IOException e2) {
                                throw new XMLStreamException(e2);
                            }
                        }
                    };
                }
                if (this.childOutputMethod != null) {
                    this.childOutputMethod.transform(xmlSecEvent.mo456asCharacters().getData().getBytes());
                }
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void transform(InputStream inputStream) throws XMLStreamException {
        if (getOutputStream() != null) {
            super.transform(inputStream);
        } else {
            super.transform(new Base64InputStream(inputStream, false));
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void doFinal() throws XMLStreamException {
        if (getOutputStream() != null) {
            try {
                getOutputStream().close();
            } catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }
        if (this.childOutputMethod != null) {
            this.childOutputMethod.doFinal();
        } else if (getTransformer() != null) {
            getTransformer().doFinal();
        }
    }
}
