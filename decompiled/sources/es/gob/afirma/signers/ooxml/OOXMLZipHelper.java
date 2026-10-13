package es.gob.afirma.signers.ooxml;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/OOXMLZipHelper.class */
final class OOXMLZipHelper {
    private static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
    private static final String RELATIONSHIPS_SCHEMA = "http://schemas.openxmlformats.org/package/2006/relationships";

    private OOXMLZipHelper() {
    }

    static byte[] outputSignedOfficeOpenXMLDocument(byte[] bArr, byte[] bArr2) throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String str = "_xmlsignatures/sig-" + UUID.randomUUID().toString() + ".xml";
        ZipOutputStream zipOutputStreamCopyOOXMLContent = copyOOXMLContent(bArr, str, byteArrayOutputStream);
        Throwable th = null;
        try {
            try {
                zipOutputStreamCopyOOXMLContent.putNextEntry(new ZipEntry(str));
                if (bArr2 != null) {
                    zipOutputStreamCopyOOXMLContent.write(bArr2);
                }
                if (zipOutputStreamCopyOOXMLContent != null) {
                    if (0 != 0) {
                        try {
                            zipOutputStreamCopyOOXMLContent.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        zipOutputStreamCopyOOXMLContent.close();
                    }
                }
                return byteArrayOutputStream.toByteArray();
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (zipOutputStreamCopyOOXMLContent != null) {
                if (th != null) {
                    try {
                        zipOutputStreamCopyOOXMLContent.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    zipOutputStreamCopyOOXMLContent.close();
                }
            }
            throw th4;
        }
    }

    /* JADX WARN: Code duplicated, block: B:41:0x0293  */
    /* JADX WARN: Code duplicated, block: B:46:0x02ac  */
    /* JADX WARN: Code duplicated, block: B:53:0x0298 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static ZipOutputStream copyOOXMLContent(byte[] bArr, String str, OutputStream outputStream) throws XPathExpressionException, TransformerException, ParserConfigurationException, SAXException, IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
        Throwable th = null;
        boolean z = false;
        while (true) {
            try {
                try {
                    ZipEntry nextEntry = zipInputStream.getNextEntry();
                    if (null == nextEntry) {
                        break;
                    }
                    zipOutputStream.putNextEntry(new ZipEntry(nextEntry.getName()));
                    if ("[Content_Types].xml".equals(nextEntry.getName())) {
                        Document documentLoadDocumentNoClose = loadDocumentNoClose(zipInputStream);
                        Element documentElement = documentLoadDocumentNoClose.getDocumentElement();
                        Element elementCreateElementNS = documentLoadDocumentNoClose.createElementNS("http://schemas.openxmlformats.org/package/2006/content-types", "Override");
                        elementCreateElementNS.setAttribute("PartName", "/" + str);
                        elementCreateElementNS.setAttribute("ContentType", "application/vnd.openxmlformats-package.digital-signature-xmlsignature+xml");
                        documentElement.appendChild(elementCreateElementNS);
                        XPath xPathNewXPath = XPathFactory.newInstance().newXPath();
                        xPathNewXPath.setNamespaceContext(new NamespaceContext() { // from class: es.gob.afirma.signers.ooxml.OOXMLZipHelper.1
                            @Override // javax.xml.namespace.NamespaceContext
                            public Iterator getPrefixes(String str2) {
                                throw new UnsupportedOperationException();
                            }

                            @Override // javax.xml.namespace.NamespaceContext
                            public String getPrefix(String str2) {
                                throw new UnsupportedOperationException();
                            }

                            @Override // javax.xml.namespace.NamespaceContext
                            public String getNamespaceURI(String str2) {
                                if (str2 == null) {
                                    throw new IllegalArgumentException("El prefijo no puede ser nulo");
                                }
                                if ("xml".equals(str2)) {
                                    return "http://www.w3.org/XML/1998/namespace";
                                }
                                if ("tns".equals(str2)) {
                                    return "http://schemas.openxmlformats.org/package/2006/content-types";
                                }
                                return "";
                            }
                        });
                        if (0 == ((NodeList) xPathNewXPath.compile("/tns:Types/tns:Default[@Extension='sigs']").evaluate(documentLoadDocumentNoClose, XPathConstants.NODESET)).getLength()) {
                            Element elementCreateElementNS2 = documentLoadDocumentNoClose.createElementNS("http://schemas.openxmlformats.org/package/2006/content-types", "Default");
                            elementCreateElementNS2.setAttribute("Extension", "sigs");
                            elementCreateElementNS2.setAttribute("ContentType", "application/vnd.openxmlformats-package.digital-signature-origin");
                            documentElement.appendChild(elementCreateElementNS2);
                        }
                        writeDocumentNoClosing(documentLoadDocumentNoClose, zipOutputStream, false);
                    } else if ("_rels/.rels".equals(nextEntry.getName())) {
                        Document documentLoadDocumentNoClose2 = loadDocumentNoClose(zipInputStream);
                        XPath xPathNewXPath2 = XPathFactory.newInstance().newXPath();
                        xPathNewXPath2.setNamespaceContext(new NamespaceContext() { // from class: es.gob.afirma.signers.ooxml.OOXMLZipHelper.2
                            @Override // javax.xml.namespace.NamespaceContext
                            public Iterator getPrefixes(String str2) {
                                throw new UnsupportedOperationException();
                            }

                            @Override // javax.xml.namespace.NamespaceContext
                            public String getPrefix(String str2) {
                                throw new UnsupportedOperationException();
                            }

                            @Override // javax.xml.namespace.NamespaceContext
                            public String getNamespaceURI(String str2) {
                                if (str2 == null) {
                                    throw new IllegalArgumentException("El prefijo no puede ser nulo");
                                }
                                if ("xml".equals(str2)) {
                                    return "http://www.w3.org/XML/1998/namespace";
                                }
                                if ("tns".equals(str2)) {
                                    return OOXMLZipHelper.RELATIONSHIPS_SCHEMA;
                                }
                                return "";
                            }
                        });
                        if (0 == ((NodeList) xPathNewXPath2.compile("/tns:Relationships/tns:Relationship[@Type='http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/origin']").evaluate(documentLoadDocumentNoClose2, XPathConstants.NODESET)).getLength()) {
                            Element elementCreateElementNS3 = documentLoadDocumentNoClose2.createElementNS(RELATIONSHIPS_SCHEMA, "Relationship");
                            elementCreateElementNS3.setAttribute("Id", "rel-id-" + UUID.randomUUID().toString());
                            elementCreateElementNS3.setAttribute("Type", "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/origin");
                            elementCreateElementNS3.setAttribute("Target", "_xmlsignatures/origin.sigs");
                            documentLoadDocumentNoClose2.getDocumentElement().appendChild(elementCreateElementNS3);
                        }
                        writeDocumentNoClosing(documentLoadDocumentNoClose2, zipOutputStream, false);
                    } else if (nextEntry.getName().startsWith("_xmlsignatures/_rels/") && nextEntry.getName().endsWith(".rels")) {
                        z = true;
                        Document documentLoadDocumentNoClose3 = loadDocumentNoClose(zipInputStream);
                        Element elementCreateElementNS4 = documentLoadDocumentNoClose3.createElementNS(RELATIONSHIPS_SCHEMA, "Relationship");
                        elementCreateElementNS4.setAttribute("Id", "rel-" + UUID.randomUUID().toString());
                        elementCreateElementNS4.setAttribute("Type", "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/signature");
                        elementCreateElementNS4.setAttribute("Target", new File(str).getName());
                        documentLoadDocumentNoClose3.getDocumentElement().appendChild(elementCreateElementNS4);
                        writeDocumentNoClosing(documentLoadDocumentNoClose3, zipOutputStream, false);
                    } else {
                        zipOutputStream.write(AOUtil.getDataFromInputStream(zipInputStream));
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                if (zipInputStream != null) {
                    if (th != null) {
                        try {
                            zipInputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        zipInputStream.close();
                    }
                }
                throw th3;
            }
            if (zipInputStream != null) {
                if (th != null) {
                    zipInputStream.close();
                } else {
                    zipInputStream.close();
                }
            }
            throw th3;
        }
        if (!z) {
            addOriginSigsRels(str, zipOutputStream);
            addOriginSigs(zipOutputStream);
        }
        if (zipInputStream != null) {
            if (0 != 0) {
                try {
                    zipInputStream.close();
                } catch (Throwable th5) {
                    th.addSuppressed(th5);
                }
            } else {
                zipInputStream.close();
            }
        }
        return zipOutputStream;
    }

    private static void addOriginSigs(ZipOutputStream zipOutputStream) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry("_xmlsignatures/origin.sigs"));
    }

    private static void addOriginSigsRels(String str, ZipOutputStream zipOutputStream) throws TransformerException, ParserConfigurationException, IOException {
        Document documentNewDocument = SecureXmlBuilder.getSecureDocumentBuilder().newDocument();
        Element elementCreateElementNS = documentNewDocument.createElementNS(RELATIONSHIPS_SCHEMA, "Relationships");
        elementCreateElementNS.setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, RELATIONSHIPS_SCHEMA);
        documentNewDocument.appendChild(elementCreateElementNS);
        Element elementCreateElementNS2 = documentNewDocument.createElementNS(RELATIONSHIPS_SCHEMA, "Relationship");
        elementCreateElementNS2.setAttribute("Id", "rel-" + UUID.randomUUID().toString());
        elementCreateElementNS2.setAttribute("Type", "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/signature");
        elementCreateElementNS2.setAttribute("Target", new File(str).getName());
        elementCreateElementNS.appendChild(elementCreateElementNS2);
        zipOutputStream.putNextEntry(new ZipEntry("_xmlsignatures/_rels/origin.sigs.rels"));
        writeDocumentNoClosing(documentNewDocument, zipOutputStream, false);
    }

    static Document loadDocumentNoClose(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        NoCloseInputStream noCloseInputStream = new NoCloseInputStream(inputStream);
        Throwable th = null;
        try {
            try {
                Document document = SecureXmlBuilder.getSecureDocumentBuilder().parse(new InputSource(noCloseInputStream));
                if (noCloseInputStream != null) {
                    if (0 != 0) {
                        try {
                            noCloseInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        noCloseInputStream.close();
                    }
                }
                return document;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (noCloseInputStream != null) {
                if (th != null) {
                    try {
                        noCloseInputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    noCloseInputStream.close();
                }
            }
            throw th4;
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0067 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static void writeDocumentNoClosing(org.w3c.dom.Document r4, java.io.OutputStream r5, boolean r6) throws javax.xml.transform.TransformerException {
        /*
            es.gob.afirma.signers.ooxml.OOXMLZipHelper$NoCloseOutputStream r0 = new es.gob.afirma.signers.ooxml.OOXMLZipHelper$NoCloseOutputStream
            r1 = r0
            r2 = r5
            r1.<init>(r2)
            r7 = r0
            r0 = 0
            r8 = r0
            javax.xml.transform.stream.StreamResult r0 = new javax.xml.transform.stream.StreamResult     // Catch: java.lang.Throwable -> L5e java.lang.Throwable -> L67
            r1 = r0
            r2 = r7
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L5e java.lang.Throwable -> L67
            r9 = r0
            javax.xml.transform.Transformer r0 = es.gob.afirma.core.misc.SecureXmlTransformer.getSecureTransformer()     // Catch: java.lang.Throwable -> L5e java.lang.Throwable -> L67
            r10 = r0
            r0 = r6
            if (r0 == 0) goto L28
            r0 = r10
            java.lang.String r1 = "omit-xml-declaration"
            java.lang.String r2 = "yes"
            r0.setOutputProperty(r1, r2)     // Catch: java.lang.Throwable -> L5e java.lang.Throwable -> L67
        L28:
            javax.xml.transform.dom.DOMSource r0 = new javax.xml.transform.dom.DOMSource     // Catch: java.lang.Throwable -> L5e java.lang.Throwable -> L67
            r1 = r0
            r2 = r4
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L5e java.lang.Throwable -> L67
            r11 = r0
            r0 = r10
            r1 = r11
            r2 = r9
            r0.transform(r1, r2)     // Catch: java.lang.Throwable -> L5e java.lang.Throwable -> L67
            r0 = r7
            if (r0 == 0) goto L8c
            r0 = r8
            if (r0 == 0) goto L57
            r0 = r7
            r0.close()     // Catch: java.lang.Throwable -> L4b
            goto L8c
        L4b:
            r9 = move-exception
            r0 = r8
            r1 = r9
            r0.addSuppressed(r1)
            goto L8c
        L57:
            r0 = r7
            r0.close()
            goto L8c
        L5e:
            r9 = move-exception
            r0 = r9
            r8 = r0
            r0 = r9
            throw r0     // Catch: java.lang.Throwable -> L67
        L67:
            r12 = move-exception
            r0 = r7
            if (r0 == 0) goto L89
            r0 = r8
            if (r0 == 0) goto L85
            r0 = r7
            r0.close()     // Catch: java.lang.Throwable -> L79
            goto L89
        L79:
            r13 = move-exception
            r0 = r8
            r1 = r13
            r0.addSuppressed(r1)
            goto L89
        L85:
            r0 = r7
            r0.close()
        L89:
            r0 = r12
            throw r0
        L8c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.signers.ooxml.OOXMLZipHelper.writeDocumentNoClosing(org.w3c.dom.Document, java.io.OutputStream, boolean):void");
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/OOXMLZipHelper$NoCloseOutputStream.class */
    private static final class NoCloseOutputStream extends FilterOutputStream {
        NoCloseOutputStream(OutputStream outputStream) {
            super(outputStream);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }
    }
}
