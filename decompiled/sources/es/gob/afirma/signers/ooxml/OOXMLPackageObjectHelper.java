package es.gob.afirma.signers.ooxml;

import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.signers.ooxml.relprovider.RelationshipTransformParameterSpec;
import es.gob.afirma.signers.ooxml.relprovider.RelationshipTransformService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/OOXMLPackageObjectHelper.class */
final class OOXMLPackageObjectHelper {
    private static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
    private static final String DIGITAL_SIGNATURE_SCHEMA = "http://schemas.openxmlformats.org/package/2006/digital-signature";
    private static final String PACKAGE_REL_CONTENT_TYPE = "application/vnd.openxmlformats-package.relationships+xml";
    private static final String[] CONTENT_DIRS = {"word", "excel", "xl", "powerpoint"};
    private static final Set<String> EXCLUDED_RELATIONSHIPS = new HashSet(6);
    private static final int THRESHOLD_FILE_SIZE = 1000000000;

    static {
        EXCLUDED_RELATIONSHIPS.add("http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties");
        EXCLUDED_RELATIONSHIPS.add("http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties");
        EXCLUDED_RELATIONSHIPS.add("http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/origin");
        EXCLUDED_RELATIONSHIPS.add("http://schemas.openxmlformats.org/package/2006/relationships/metadata/thumbnail");
        EXCLUDED_RELATIONSHIPS.add("http://schemas.openxmlformats.org/officeDocument/2006/relationships/presProps");
        EXCLUDED_RELATIONSHIPS.add("http://schemas.openxmlformats.org/officeDocument/2006/relationships/viewProps");
    }

    private OOXMLPackageObjectHelper() {
    }

    static XMLObject getPackageObject(String str, XMLSignatureFactory xMLSignatureFactory, byte[] bArr, Document document, String str2) throws ParserConfigurationException, NoSuchAlgorithmException, SAXException, IOException, InvalidAlgorithmParameterException {
        LinkedList linkedList = new LinkedList();
        linkedList.add(constructManifest(xMLSignatureFactory, bArr));
        addSignatureTime(xMLSignatureFactory, document, str2, linkedList);
        return xMLSignatureFactory.newXMLObject(linkedList, str, (String) null, (String) null);
    }

    private static boolean startsWithAnyOfThose(String str, String[] strArr) {
        for (String str2 : strArr) {
            if (str.startsWith(str2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean alreadyContains(List<Reference> list, Reference reference) {
        if (reference == null || list == null) {
            return true;
        }
        Iterator<Reference> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().getURI().equals(reference.getURI())) {
                return true;
            }
        }
        return false;
    }

    private static void addParts(XMLSignatureFactory xMLSignatureFactory, ContentTypeManager contentTypeManager, List<Reference> list, byte[] bArr, String[] strArr, DigestMethod digestMethod) throws IOException {
        String contentType;
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
        while (true) {
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            if (null != nextEntry) {
                if (startsWithAnyOfThose(nextEntry.getName(), strArr) && (contentType = contentTypeManager.getContentType(nextEntry.getName())) != null) {
                    Reference referenceNewReference = xMLSignatureFactory.newReference("/" + nextEntry.getName() + "?ContentType=" + contentType, digestMethod);
                    if (!alreadyContains(list, referenceNewReference)) {
                        list.add(referenceNewReference);
                    }
                }
            } else {
                return;
            }
        }
    }

    private static InputStream getContentTypesXMLInputStream(byte[] bArr) throws IOException {
        ZipEntry nextEntry;
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
        do {
            nextEntry = zipInputStream.getNextEntry();
            if (null == nextEntry) {
                throw new IllegalStateException("El documento OOXML es invalido ya que no contiene el fichero [Content_Types].xml");
            }
        } while (!"[Content_Types].xml".equals(nextEntry.getName()));
        return zipInputStream;
    }

    private static Document loadDocumentNoClose(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        NoCloseInputStream noCloseInputStream = new NoCloseInputStream(inputStream);
        Throwable th = null;
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
            if (noCloseInputStream != null) {
                if (0 != 0) {
                    try {
                        noCloseInputStream.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    noCloseInputStream.close();
                }
            }
            throw th3;
        }
    }

    private static void addRelationshipsReference(XMLSignatureFactory xMLSignatureFactory, String str, Document document, List<Reference> list, String str2, DigestMethod digestMethod) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        RelationshipTransformParameterSpec relationshipTransformParameterSpec = new RelationshipTransformParameterSpec();
        NodeList childNodes = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1) {
                Element element = (Element) nodeItem;
                if (!EXCLUDED_RELATIONSHIPS.contains(element.getAttribute("Type"))) {
                    relationshipTransformParameterSpec.addRelationshipReference(element.getAttribute("Id"));
                }
            }
        }
        LinkedList linkedList = new LinkedList();
        linkedList.add(xMLSignatureFactory.newTransform(RelationshipTransformService.TRANSFORM_URI, relationshipTransformParameterSpec));
        linkedList.add(xMLSignatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (TransformParameterSpec) null));
        list.add(xMLSignatureFactory.newReference("/" + str + "?ContentType=" + str2, digestMethod, linkedList, (String) null, (String) null));
    }

    private static void addRelationshipsReferences(XMLSignatureFactory xMLSignatureFactory, List<Reference> list, byte[] bArr, DigestMethod digestMethod) throws ParserConfigurationException, SAXException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException {
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
        while (true) {
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            if (null != nextEntry) {
                if (nextEntry.getName().endsWith(".rels")) {
                    addRelationshipsReference(xMLSignatureFactory, nextEntry.getName(), loadDocumentNoClose(zipInputStream), list, PACKAGE_REL_CONTENT_TYPE, digestMethod);
                }
            } else {
                return;
            }
        }
    }

    private static Manifest constructManifest(XMLSignatureFactory xMLSignatureFactory, byte[] bArr) throws ParserConfigurationException, SAXException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException {
        DigestMethod digestMethodNewDigestMethod = xMLSignatureFactory.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256", (DigestMethodParameterSpec) null);
        LinkedList linkedList = new LinkedList();
        addRelationshipsReferences(xMLSignatureFactory, linkedList, bArr, digestMethodNewDigestMethod);
        InputStream contentTypesXMLInputStream = getContentTypesXMLInputStream(bArr);
        Throwable th = null;
        try {
            try {
                addParts(xMLSignatureFactory, new ContentTypeManager(contentTypesXMLInputStream), linkedList, bArr, CONTENT_DIRS, digestMethodNewDigestMethod);
                if (contentTypesXMLInputStream != null) {
                    if (0 != 0) {
                        try {
                            contentTypesXMLInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        contentTypesXMLInputStream.close();
                    }
                }
                return xMLSignatureFactory.newManifest(linkedList);
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (contentTypesXMLInputStream != null) {
                if (th != null) {
                    try {
                        contentTypesXMLInputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    contentTypesXMLInputStream.close();
                }
            }
            throw th4;
        }
    }

    private static void addSignatureTime(XMLSignatureFactory xMLSignatureFactory, Document document, String str, List<XMLStructure> list) {
        Element elementCreateElementNS = document.createElementNS(DIGITAL_SIGNATURE_SCHEMA, "mdssi:SignatureTime");
        elementCreateElementNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:mdssi", DIGITAL_SIGNATURE_SCHEMA);
        Element elementCreateElementNS2 = document.createElementNS(DIGITAL_SIGNATURE_SCHEMA, "mdssi:Format");
        elementCreateElementNS2.setTextContent("YYYY-MM-DDThh:mm:ssTZD");
        elementCreateElementNS.appendChild(elementCreateElementNS2);
        Element elementCreateElementNS3 = document.createElementNS(DIGITAL_SIGNATURE_SCHEMA, "mdssi:Value");
        elementCreateElementNS3.setTextContent(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").format(new Date()));
        elementCreateElementNS.appendChild(elementCreateElementNS3);
        LinkedList linkedList = new LinkedList();
        linkedList.add(new DOMStructure(elementCreateElementNS));
        SignatureProperty signaturePropertyNewSignatureProperty = xMLSignatureFactory.newSignatureProperty(linkedList, "#" + str, "idSignatureTime");
        LinkedList linkedList2 = new LinkedList();
        linkedList2.add(signaturePropertyNewSignatureProperty);
        list.add(xMLSignatureFactory.newSignatureProperties(linkedList2, "id-signature-time-" + UUID.randomUUID().toString()));
    }
}
