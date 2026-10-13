package es.gob.afirma.signers.xmldsig;

import com.ctc.wstx.cfg.XmlConsts;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import es.gob.afirma.signers.cades.CAdESExtraParams;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import es.gob.afirma.signers.xml.InvalidXMLException;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signers.xml.XMLConstants;
import es.gob.afirma.signers.xml.XmlDSigProviderHelper;
import es.gob.afirma.signers.xml.style.CannotDereferenceException;
import es.gob.afirma.signers.xml.style.IsInnerlException;
import es.gob.afirma.signers.xml.style.ReferenceIsNotXmlException;
import es.gob.afirma.signers.xml.style.XmlStyle;
import es.uji.crypto.xades.jxades.util.DOMOutputImpl;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xmldsig/AOXMLDSigSigner.class */
public final class AOXMLDSigSigner implements AOSigner {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String XADESNS = "http://uri.etsi.org/01903#";
    private static final String MIMETYPE_STR = "MimeType";
    private static final String ENCODING_STR = "Encoding";
    private static final String REFERENCE_STR = "Reference";
    private static final String ID_IDENTIFIER = "Id";
    private static final String HTTP_PROTOCOL_PREFIX = "http://";
    private static final String HTTPS_PROTOCOL_PREFIX = "https://";
    private static final String STYLE_REFERENCE_PREFIX = "StyleReference-";
    private static final String CSURI = "http://uri.etsi.org/01903#CountersignedSignature";
    private static final String AFIRMA = "AFIRMA";
    private static final String XML_SIGNATURE_PREFIX = "ds";
    private static final String DETACHED_CONTENT_ELEMENT_NAME = "CONTENT";
    private static final String DETACHED_STYLE_ELEMENT_NAME = "STYLE";
    private static final String DIGEST_METHOD = "http://www.w3.org/2000/09/xmldsig#sha1";
    private static final String SIGNATURE_VALUE = "SignatureValue";
    private static final String URI_STR = "URI";
    private String algo;
    private Document doc;

    static {
        XmlDSigProviderHelper.configureXmlDSigProvider();
    }

    /* JADX WARN: Code duplicated, block: B:101:0x0480 A[Catch: Exception -> 0x04cf, TryCatch #11 {Exception -> 0x04cf, blocks: (B:77:0x0398, B:80:0x03d5, B:82:0x03e8, B:88:0x03fe, B:90:0x0409, B:94:0x0439, B:107:0x04b9, B:95:0x0459, B:97:0x0463, B:99:0x0476, B:101:0x0480, B:106:0x049f, B:98:0x046e), top: B:415:0x0398 }] */
    /* JADX WARN: Code duplicated, block: B:103:0x0496  */
    /* JADX WARN: Code duplicated, block: B:104:0x049b  */
    /* JADX WARN: Code duplicated, block: B:95:0x0459 A[Catch: Exception -> 0x04cf, TryCatch #11 {Exception -> 0x04cf, blocks: (B:77:0x0398, B:80:0x03d5, B:82:0x03e8, B:88:0x03fe, B:90:0x0409, B:94:0x0439, B:107:0x04b9, B:95:0x0459, B:97:0x0463, B:99:0x0476, B:101:0x0480, B:106:0x049f, B:98:0x046e), top: B:415:0x0398 }] */
    /* JADX WARN: Code duplicated, block: B:97:0x0463 A[Catch: Exception -> 0x04cf, TryCatch #11 {Exception -> 0x04cf, blocks: (B:77:0x0398, B:80:0x03d5, B:82:0x03e8, B:88:0x03fe, B:90:0x0409, B:94:0x0439, B:107:0x04b9, B:95:0x0459, B:97:0x0463, B:99:0x0476, B:101:0x0480, B:106:0x049f, B:98:0x046e), top: B:415:0x0398 }] */
    /* JADX WARN: Code duplicated, block: B:98:0x046e A[Catch: Exception -> 0x04cf, TryCatch #11 {Exception -> 0x04cf, blocks: (B:77:0x0398, B:80:0x03d5, B:82:0x03e8, B:88:0x03fe, B:90:0x0409, B:94:0x0439, B:107:0x04b9, B:95:0x0459, B:97:0x0463, B:99:0x0476, B:101:0x0480, B:106:0x049f, B:98:0x046e), top: B:415:0x0398 }] */
    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        Element elementCreateElement;
        String mimeType;
        String str2;
        String systemId;
        Transform transformNewTransform;
        List listSingletonList;
        String str3 = XMLConstants.SIGN_ALGOS_URI.get(str);
        if (str3 == null) {
            throw new UnsupportedOperationException("La URI de definicion del algoritmo de firma no puede ser nula");
        }
        Properties properties2 = properties != null ? properties : new Properties();
        String property = properties2.getProperty(XAdESExtraParams.FORMAT, AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPING);
        String property2 = properties2.getProperty(CAdESExtraParams.MODE, AOSignConstants.SIGN_MODE_IMPLICIT);
        String property3 = properties2.getProperty(XAdESExtraParams.REFERENCES_DIGEST_METHOD, "http://www.w3.org/2000/09/xmldsig#sha1");
        String property4 = properties2.getProperty(XAdESExtraParams.CANONICALIZATION_ALGORITHM, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        boolean z = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.IGNORE_STYLE_SHEETS, Boolean.FALSE.toString()));
        boolean z2 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.AVOID_BASE64_TRANSFORMS, Boolean.FALSE.toString()));
        boolean z3 = Boolean.parseBoolean(properties2.getProperty("headless", Boolean.TRUE.toString()));
        boolean z4 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.AVOID_XPATH_EXTRA_TRANSFORMS_ON_ENVELOPED, Boolean.FALSE.toString()));
        String property5 = properties2.getProperty("xmlSignaturePrefix", XML_SIGNATURE_PREFIX);
        String property6 = properties2.getProperty("mimeType");
        String property7 = properties2.getProperty("encoding");
        if ("base64".equalsIgnoreCase(property7)) {
            property7 = "http://www.w3.org/2000/09/xmldsig#base64";
        }
        URI uriCreateURI = null;
        try {
            uriCreateURI = AOUtil.createURI(properties2.getProperty("uri"));
        } catch (Exception e) {
        }
        String property8 = properties2.getProperty("precalculatedHashAlgorithm");
        Utils.checkIllegalParams(property, property2, false, uriCreateURI, property8, false);
        if ((bArr == null || bArr.length == 0) && (!property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_EXTERNALLY_DETACHED) || uriCreateURI == null)) {
            throw new AOException("No se han podido leer los datos a firmar");
        }
        Hashtable hashtable = new Hashtable();
        String str4 = "CONTENT-" + UUID.randomUUID().toString() + "-" + DETACHED_CONTENT_ELEMENT_NAME;
        String str5 = "STYLE-" + UUID.randomUUID().toString() + "-" + DETACHED_STYLE_ELEMENT_NAME;
        boolean z5 = false;
        boolean z6 = false;
        XmlStyle xmlStyle = new XmlStyle();
        if (property2.equals(AOSignConstants.SIGN_MODE_IMPLICIT)) {
            try {
                Document document = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr));
                if (!z) {
                    try {
                        try {
                            xmlStyle = new XmlStyle(bArr, z3);
                        } catch (IsInnerlException e2) {
                            LOGGER.info("La hoja de estilo esta referenciada internamente, por lo que no se necesita dereferenciar: " + e2);
                        } catch (ReferenceIsNotXmlException e3) {
                            LOGGER.warning("La hoja de estilo referenciada no es XML o no se ha dereferenciado apropiadamente: " + e3);
                        }
                    } catch (CannotDereferenceException e4) {
                        LOGGER.warning("La hoja de estilo no ha podido dereferenciar, probablemente sea un enlace relativo local: " + e4);
                    } catch (Exception e5) {
                        LOGGER.severe("Error intentando dereferenciar la hoja de estilo: " + e5);
                    }
                }
                if (property6 == null || "application/octet-stream".equals(property6)) {
                    property6 = "text/xml";
                }
                if (property7 == null) {
                    property7 = document.getXmlEncoding();
                }
                if (property7 != null && !"http://www.w3.org/2000/09/xmldsig#base64".equals(property7)) {
                    hashtable.put("encoding", property7);
                }
                String xmlVersion = document.getXmlVersion();
                if (xmlVersion != null) {
                    hashtable.put(XmlConsts.XML_DECL_KW_VERSION, xmlVersion);
                }
                DocumentType doctype = document.getDoctype();
                if (doctype != null && (systemId = doctype.getSystemId()) != null) {
                    hashtable.put("doctype-system", systemId);
                }
                if (property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_DETACHED)) {
                    elementCreateElement = document.createElement(DETACHED_CONTENT_ELEMENT_NAME);
                    elementCreateElement.setAttributeNS(null, "Id", str4);
                    elementCreateElement.setAttributeNS(null, "MimeType", property6);
                    elementCreateElement.setAttributeNS(null, "Encoding", property7);
                    elementCreateElement.appendChild(document.getDocumentElement());
                    if (xmlStyle.getStyleElement() != null) {
                        try {
                            Element elementCreateElement2 = document.createElement(DETACHED_STYLE_ELEMENT_NAME);
                            elementCreateElement2.setAttributeNS(null, "Id", str5);
                            if (xmlStyle.getStyleType() != null) {
                                elementCreateElement2.setAttributeNS(null, "MimeType", xmlStyle.getStyleType());
                            }
                            elementCreateElement2.setAttributeNS(null, "Encoding", xmlStyle.getStyleEncoding());
                            elementCreateElement2.appendChild(document.adoptNode(xmlStyle.getStyleElement().cloneNode(true)));
                            xmlStyle.setStyleElement(elementCreateElement2);
                        } catch (Exception e6) {
                            LOGGER.warning("No ha sido posible crear el elemento DOM para incluir la hoja de estilo del XML como Internally Detached: " + e6);
                            xmlStyle.setStyleElement(null);
                        }
                    }
                } else {
                    elementCreateElement = document.getDocumentElement();
                }
            } catch (Exception e7) {
                if (property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED)) {
                    throw new InvalidXMLException(e7);
                }
                try {
                    LOGGER.info("El documento no es un XML valido. Se convertira a Base64: " + e7);
                    elementCreateElement = Utils.getNewDocumentBuilder().newDocument().createElement(DETACHED_CONTENT_ELEMENT_NAME);
                    uriCreateURI = null;
                    if (property6 == null) {
                        property6 = "application/octet-stream";
                    }
                    elementCreateElement.setAttributeNS(null, "Id", str4);
                    if (Base64.isBase64(bArr)) {
                        if (!"http://www.w3.org/2000/09/xmldsig#base64".equals("http://www.w3.org/2000/09/xmldsig#base64")) {
                            if (!("http://www.w3.org/2000/09/xmldsig#base64" != 0 ? "http://www.w3.org/2000/09/xmldsig#base64" : "").toLowerCase().equals("base64")) {
                                if ("http://www.w3.org/2000/09/xmldsig#base64".equals("http://www.w3.org/2000/09/xmldsig#base64")) {
                                    LOGGER.info("El documento se ha indicado como Base64, pero no es un Base64 valido. Se convertira a Base64 antes de insertarlo en el XML y se declarara la transformacion");
                                } else {
                                    LOGGER.info("El documento se considera binario, se convertira a Base64 antes de insertarlo en el XML y se declarara la transformacion");
                                }
                                if ("application/octet-stream".equals(property6)) {
                                    mimeType = new MimeHelper(bArr).getMimeType();
                                    if (mimeType != null) {
                                        str2 = mimeType;
                                    } else {
                                        str2 = "application/octet-stream";
                                    }
                                    property6 = str2;
                                }
                                elementCreateElement.setAttributeNS(null, "MimeType", property6);
                                elementCreateElement.setTextContent(Base64.encode(bArr));
                                z6 = true;
                            }
                        }
                        LOGGER.info("El documento se ha indicado como Base64, se insertara como tal en el XML");
                        String mimeType2 = new MimeHelper(Base64.decode(bArr, 0, bArr.length, false)).getMimeType();
                        property6 = mimeType2 != null ? mimeType2 : "application/octet-stream";
                        elementCreateElement.setAttributeNS(null, "MimeType", property6);
                        elementCreateElement.setTextContent(new String(bArr));
                    } else {
                        if ("http://www.w3.org/2000/09/xmldsig#base64".equals("http://www.w3.org/2000/09/xmldsig#base64")) {
                            LOGGER.info("El documento se ha indicado como Base64, pero no es un Base64 valido. Se convertira a Base64 antes de insertarlo en el XML y se declarara la transformacion");
                        } else {
                            LOGGER.info("El documento se considera binario, se convertira a Base64 antes de insertarlo en el XML y se declarara la transformacion");
                        }
                        if ("application/octet-stream".equals(property6)) {
                            mimeType = new MimeHelper(bArr).getMimeType();
                            if (mimeType != null) {
                                str2 = mimeType;
                            } else {
                                str2 = "application/octet-stream";
                            }
                            property6 = str2;
                        }
                        elementCreateElement.setAttributeNS(null, "MimeType", property6);
                        elementCreateElement.setTextContent(Base64.encode(bArr));
                        z6 = true;
                    }
                    z5 = true;
                    property7 = "http://www.w3.org/2000/09/xmldsig#base64";
                    elementCreateElement.setAttributeNS(null, "Encoding", property7);
                } catch (Exception e8) {
                    throw new AOException("Error al convertir los datos a base64", e8);
                }
            }
        } else {
            byte[] bArrDigest = null;
            if (uriCreateURI != null) {
                try {
                    byte[] dataFromInputStream = AOUtil.getDataFromInputStream(AOUtil.loadFile(uriCreateURI));
                    if (dataFromInputStream != null && dataFromInputStream.length > 0) {
                        try {
                            bArrDigest = MessageDigest.getInstance(AOSignConstants.DIGEST_ALGORITHM_SHA1).digest(dataFromInputStream);
                        } catch (Exception e9) {
                            throw new AOException("No se ha podido obtener el SHA1 de los datos de la URI externa", e9);
                        }
                    }
                } catch (Exception e10) {
                    throw new AOException("No se han podido obtener los datos de la URI externa", e10);
                }
            } else if (property8 != null) {
                bArrDigest = bArr;
            } else {
                try {
                    bArrDigest = MessageDigest.getInstance(AOSignConstants.DIGEST_ALGORITHM_SHA1).digest(bArr);
                } catch (Exception e11) {
                    throw new AOException("No se ha podido obtener el SHA1 de los datos proporcionados: " + e11, e11);
                }
            }
            if (bArrDigest == null || bArrDigest.length < 1) {
                throw new AOException("Error al obtener la huella SHA1 de los datos");
            }
            try {
                elementCreateElement = Utils.getNewDocumentBuilder().newDocument().createElement(DETACHED_CONTENT_ELEMENT_NAME);
                property7 = "http://www.w3.org/2000/09/xmldsig#base64";
                property6 = property8 != null ? "hash/" + property8.toLowerCase() : "hash/sha1";
                elementCreateElement.setAttributeNS(null, "Id", str4);
                elementCreateElement.setAttributeNS(null, "MimeType", property6);
                elementCreateElement.setAttributeNS(null, "Encoding", property7);
                elementCreateElement.setTextContent(Base64.encode(bArrDigest));
                z5 = true;
            } catch (Exception e12) {
                throw new AOException("No se ha podido crear el documento XML contenedor: " + e12, e12);
            }
        }
        String str6 = "#" + str4;
        String str7 = "#" + str5;
        try {
            Document documentNewDocument = Utils.getNewDocumentBuilder().newDocument();
            if (property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED)) {
                documentNewDocument.appendChild(documentNewDocument.adoptNode(elementCreateElement));
            } else {
                documentNewDocument.appendChild(documentNewDocument.createElement(AFIRMA));
            }
            ArrayList arrayList = new ArrayList();
            XMLSignatureFactory dOMFactory = Utils.getDOMFactory();
            try {
                DigestMethod digestMethodNewDigestMethod = dOMFactory.newDigestMethod(property3, (DigestMethodParameterSpec) null);
                String str8 = "Reference-" + UUID.randomUUID().toString();
                String str9 = STYLE_REFERENCE_PREFIX + UUID.randomUUID().toString();
                ArrayList arrayList2 = new ArrayList();
                Utils.addCustomTransforms(arrayList2, properties2, property5);
                if ("none".equalsIgnoreCase(property4)) {
                    transformNewTransform = null;
                } else {
                    try {
                        transformNewTransform = dOMFactory.newTransform(property4, (TransformParameterSpec) null);
                    } catch (Exception e13) {
                        throw new AOException("No se ha podido crear la transformacion de canonicalizacion para el algoritmo '" + property4 + "': " + e13);
                    }
                }
                if (z5) {
                    if (z6 && !z2) {
                        try {
                            arrayList2.add(dOMFactory.newTransform("http://www.w3.org/2000/09/xmldsig#base64", (TransformParameterSpec) null));
                        } catch (Exception e14) {
                            LOGGER.severe("No se puede encontrar el algoritmo transformacion Base64, esta no se declarara: " + e14);
                        }
                    }
                } else if (transformNewTransform != null) {
                    try {
                        arrayList2.add(transformNewTransform);
                    } catch (Exception e15) {
                        LOGGER.severe("No se puede encontrar el algoritmo de canonicalizacion, la referencia no se canonicalizara: " + e15);
                    }
                }
                XMLObject xMLObjectNewXMLObject = null;
                XMLObject xMLObjectNewXMLObject2 = null;
                if (property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPING)) {
                    try {
                        ArrayList arrayList3 = new ArrayList(1);
                        if (z5) {
                            arrayList3.add(new DOMStructure(elementCreateElement.getFirstChild()));
                        } else {
                            arrayList3.add(new DOMStructure(elementCreateElement));
                        }
                        String str10 = "Object-" + UUID.randomUUID().toString();
                        xMLObjectNewXMLObject = dOMFactory.newXMLObject(arrayList3, str10, property6, property7);
                        arrayList.add(dOMFactory.newReference("#" + str10, digestMethodNewDigestMethod, arrayList2, "http://www.w3.org/2000/09/xmldsig#Object", str8));
                        if (xmlStyle.getStyleElement() != null) {
                            String str11 = "StyleObject-" + UUID.randomUUID().toString();
                            xMLObjectNewXMLObject2 = dOMFactory.newXMLObject(Collections.singletonList(new DOMStructure(xmlStyle.getStyleElement())), str11, xmlStyle.getStyleType(), xmlStyle.getStyleEncoding());
                            arrayList.add(dOMFactory.newReference("#" + str11, digestMethodNewDigestMethod, transformNewTransform != null ? Collections.singletonList(transformNewTransform) : null, "http://www.w3.org/2000/09/xmldsig#Object", str9));
                        }
                        if (xmlStyle.getStyleHref() != null && xmlStyle.getStyleElement() == null && (xmlStyle.getStyleHref().startsWith(HTTP_PROTOCOL_PREFIX) || xmlStyle.getStyleHref().startsWith(HTTPS_PROTOCOL_PREFIX))) {
                            try {
                                arrayList.add(dOMFactory.newReference(xmlStyle.getStyleHref(), digestMethodNewDigestMethod, transformNewTransform != null ? Collections.singletonList(transformNewTransform) : null, "http://www.w3.org/2000/09/xmldsig#Object", str9));
                            } catch (Exception e16) {
                                LOGGER.severe("No ha sido posible anadir la referencia a la hoja de estilo del XML en la firma Externally Detached, esta no se firmara: " + e16);
                            }
                        }
                    } catch (Exception e17) {
                        throw new AOException("Error al generar la firma en formato enveloping", e17);
                    }
                } else if (property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_DETACHED)) {
                    if (elementCreateElement != null) {
                        try {
                            documentNewDocument.getDocumentElement().appendChild(documentNewDocument.adoptNode(elementCreateElement));
                            arrayList.add(dOMFactory.newReference(str6, digestMethodNewDigestMethod, arrayList2, "http://www.w3.org/2000/09/xmldsig#Object", str8));
                        } catch (Exception e18) {
                            throw new AOException("Error al generar la firma en formato detached implicito", e18);
                        }
                    }
                    if (xmlStyle.getStyleElement() != null) {
                        documentNewDocument.getDocumentElement().appendChild(documentNewDocument.adoptNode(xmlStyle.getStyleElement()));
                        arrayList.add(dOMFactory.newReference(str7, digestMethodNewDigestMethod, transformNewTransform != null ? Collections.singletonList(transformNewTransform) : null, "http://www.w3.org/2000/09/xmldsig#Object", str9));
                    }
                    if (xmlStyle.getStyleHref() != null && xmlStyle.getStyleElement() == null && (xmlStyle.getStyleHref().startsWith(HTTP_PROTOCOL_PREFIX) || xmlStyle.getStyleHref().startsWith(HTTPS_PROTOCOL_PREFIX))) {
                        try {
                            arrayList.add(dOMFactory.newReference(xmlStyle.getStyleHref(), digestMethodNewDigestMethod, Collections.singletonList(dOMFactory.newTransform(property4, (TransformParameterSpec) null)), "http://www.w3.org/2000/09/xmldsig#Object", str9));
                        } catch (Exception e19) {
                            LOGGER.severe("No ha sido posible anadir la referencia a la hoja de estilo del XML en la firma Detached Implicita, esta no se firmara: " + e19);
                        }
                    }
                } else if (property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_EXTERNALLY_DETACHED)) {
                    Reference referenceNewReference = null;
                    if (property8 != null && (uriCreateURI == null || uriCreateURI.getScheme().equals("") || uriCreateURI.getScheme().equals("file"))) {
                        DigestMethod digestMethodNewDigestMethod2 = null;
                        try {
                            if (AOSignConstants.getDigestAlgorithmName(property8).equalsIgnoreCase(AOSignConstants.DIGEST_ALGORITHM_SHA1)) {
                                digestMethodNewDigestMethod2 = dOMFactory.newDigestMethod("http://www.w3.org/2000/09/xmldsig#sha1", (DigestMethodParameterSpec) null);
                            } else if (AOSignConstants.getDigestAlgorithmName(property8).equalsIgnoreCase("SHA-256")) {
                                digestMethodNewDigestMethod2 = dOMFactory.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256", (DigestMethodParameterSpec) null);
                            } else if (AOSignConstants.getDigestAlgorithmName(property8).equalsIgnoreCase("SHA-512")) {
                                digestMethodNewDigestMethod2 = dOMFactory.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha512", (DigestMethodParameterSpec) null);
                            } else if (AOSignConstants.getDigestAlgorithmName(property8).equalsIgnoreCase("RIPEMD160")) {
                                digestMethodNewDigestMethod2 = dOMFactory.newDigestMethod("http://www.w3.org/2001/04/xmlenc#ripemd160", (DigestMethodParameterSpec) null);
                            }
                            if (digestMethodNewDigestMethod2 == null) {
                                throw new AOException("Metodo de Message Digest para la referencia Externally Detached no soportado: " + property8);
                            }
                            referenceNewReference = dOMFactory.newReference("", digestMethodNewDigestMethod2, (List) null, "http://www.w3.org/2000/09/xmldsig#Object", str8, bArr);
                        } catch (Exception e20) {
                            throw new AOException("No se ha podido crear el metodo de huella digital para la referencia Externally Detached", e20);
                        }
                    } else if (uriCreateURI != null && uriCreateURI.getScheme().equals("file")) {
                        try {
                            referenceNewReference = dOMFactory.newReference("", digestMethodNewDigestMethod, (List) null, "http://www.w3.org/2000/09/xmldsig#Object", str8, MessageDigest.getInstance(AOSignConstants.getDigestAlgorithmName(property3)).digest(AOUtil.getDataFromInputStream(AOUtil.loadFile(uriCreateURI))));
                        } catch (Exception e21) {
                            throw new AOException("No se ha podido crear la referencia XML a partir de la URI local (" + uriCreateURI.toASCIIString() + ")", e21);
                        }
                    } else if (uriCreateURI != null) {
                        try {
                            referenceNewReference = dOMFactory.newReference(uriCreateURI.toASCIIString(), digestMethodNewDigestMethod);
                        } catch (Exception e22) {
                            throw new AOException("No se ha podido crear la referencia Externally Detached, probablemente por no obtenerse el metodo de digest", e22);
                        }
                    }
                    if (referenceNewReference == null) {
                        throw new AOException("Error al generar la firma Externally Detached, no se ha podido crear la referencia externa");
                    }
                    arrayList.add(referenceNewReference);
                    if (xmlStyle.getStyleHref() != null && xmlStyle.getStyleElement() == null) {
                        if (xmlStyle.getStyleHref().startsWith(HTTP_PROTOCOL_PREFIX) || xmlStyle.getStyleHref().startsWith(HTTPS_PROTOCOL_PREFIX)) {
                            try {
                                arrayList.add(dOMFactory.newReference(xmlStyle.getStyleHref(), digestMethodNewDigestMethod, transformNewTransform != null ? Collections.singletonList(transformNewTransform) : null, "http://www.w3.org/2000/09/xmldsig#Object", str9));
                            } catch (Exception e23) {
                                LOGGER.severe("No ha sido posible anadir la referencia a la hoja de estilo remota del XML en la firma Externally Detached, esta no se firmara: " + e23);
                            }
                        } else {
                            LOGGER.warning("Se necesita una referencia externa HTTP o HTTPS a la hoja de estilo para referenciarla en firmas XML Externally Detached");
                        }
                    }
                } else if (property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED)) {
                    try {
                        arrayList2.add(dOMFactory.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (TransformParameterSpec) null));
                        if (!z4) {
                            arrayList2.add(dOMFactory.newTransform(Transforms.TRANSFORM_XPATH, new XPathFilterParameterSpec("not(ancestor-or-self::" + property5 + ":Signature)", Collections.singletonMap(property5, "http://www.w3.org/2000/09/xmldsig#"))));
                        }
                        arrayList.add(dOMFactory.newReference("", digestMethodNewDigestMethod, arrayList2, "http://www.w3.org/2000/09/xmldsig#Object", str8));
                        if (xmlStyle.getStyleHref() != null && xmlStyle.getStyleElement() == null && (xmlStyle.getStyleHref().startsWith(HTTP_PROTOCOL_PREFIX) || xmlStyle.getStyleHref().startsWith(HTTPS_PROTOCOL_PREFIX))) {
                            try {
                                arrayList.add(dOMFactory.newReference(xmlStyle.getStyleHref(), digestMethodNewDigestMethod, transformNewTransform != null ? Collections.singletonList(transformNewTransform) : null, "http://www.w3.org/2000/09/xmldsig#Object", str9));
                            } catch (Exception e24) {
                                LOGGER.severe("No ha sido posible anadir la referencia a la hoja de estilo remota del XML en la firma Enveloped, esta no se firmara: " + e24);
                            }
                        }
                    } catch (Exception e25) {
                        throw new AOException("Error al generar la firma en formato enveloped", e25);
                    }
                }
                String string = UUID.randomUUID().toString();
                String str12 = "KeyInfo-" + string;
                try {
                    arrayList.add(dOMFactory.newReference("#" + str12, digestMethodNewDigestMethod, arrayList2, (String) null, (String) null));
                    KeyInfoFactory keyInfoFactory = dOMFactory.getKeyInfoFactory();
                    ArrayList arrayList4 = new ArrayList();
                    X509Certificate x509Certificate = (X509Certificate) certificateArr[0];
                    arrayList4.add(keyInfoFactory.newKeyValue(x509Certificate.getPublicKey()));
                    Certificate[] certificateArr2 = null;
                    if (!Boolean.parseBoolean(properties2.getProperty("includeOnlySignningCertificate", Boolean.FALSE.toString()))) {
                        certificateArr2 = certificateArr;
                    }
                    if (certificateArr2 == null) {
                        certificateArr2 = new Certificate[]{x509Certificate};
                    }
                    arrayList4.add(keyInfoFactory.newX509Data(Arrays.asList(certificateArr2)));
                    ArrayList arrayList5 = new ArrayList();
                    if (property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPING) && xMLObjectNewXMLObject != null) {
                        arrayList5.add(xMLObjectNewXMLObject);
                        if (xMLObjectNewXMLObject2 != null) {
                            arrayList5.add(xMLObjectNewXMLObject2);
                        }
                    }
                    if (property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED) && xmlStyle.getStyleElement() != null) {
                        arrayList5.add(dOMFactory.newXMLObject(Collections.singletonList(new DOMStructure(xmlStyle.getStyleElement())), str5, xmlStyle.getStyleType(), xmlStyle.getStyleEncoding()));
                        if (transformNewTransform != null) {
                            try {
                                listSingletonList = Collections.singletonList(transformNewTransform);
                            } catch (Exception e26) {
                                LOGGER.severe("No se ha podido anadir una referencia a la hoja de estilo, esta se incluira dentro de la firma, pero no estara firmada: " + e26);
                            }
                        } else {
                            listSingletonList = null;
                        }
                        arrayList.add(dOMFactory.newReference(str7, digestMethodNewDigestMethod, listSingletonList, "http://www.w3.org/2000/09/xmldsig#Object", str9));
                    }
                    XMLSignature xMLSignatureNewXMLSignature = dOMFactory.newXMLSignature(dOMFactory.newSignedInfo(dOMFactory.newCanonicalizationMethod(transformNewTransform != null ? property4 : "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (C14NMethodParameterSpec) null), dOMFactory.newSignatureMethod(str3, (SignatureMethodParameterSpec) null), XmlDSigUtil.cleanReferencesList(arrayList)), keyInfoFactory.newKeyInfo(arrayList4, str12), arrayList5, "Signature-" + string, "SignatureValue-" + string);
                    DOMSignContext dOMSignContext = new DOMSignContext(privateKey, documentNewDocument.getDocumentElement());
                    dOMSignContext.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", property5);
                    try {
                        dOMSignContext.setURIDereferencer(new CustomUriDereferencer(CustomUriDereferencer.getDefaultDereferencer()));
                    } catch (Exception e27) {
                        LOGGER.warning("No se ha podido instalar un dereferenciador a medida, es posible que fallen las firmas de nodos concretos: " + e27);
                    }
                    xMLSignatureNewXMLSignature.sign(dOMSignContext);
                    String str13 = ((property5 == null || property5.isEmpty()) ? "" : property5 + ":") + "Signature";
                    if (property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPING)) {
                        try {
                            if (documentNewDocument.getElementsByTagName(str13).getLength() == 1) {
                                Document documentNewDocument2 = Utils.getNewDocumentBuilder().newDocument();
                                documentNewDocument2.appendChild(documentNewDocument2.adoptNode(documentNewDocument.getElementsByTagName(str13).item(0)));
                                documentNewDocument = documentNewDocument2;
                            }
                        } catch (Exception e28) {
                            LOGGER.info("No se ha eliminado el nodo padre '<AFIRMA>': " + e28);
                        }
                    }
                    return Utils.writeXML(documentNewDocument.getDocumentElement(), hashtable, property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED) ? xmlStyle.getStyleHref() : null, property.equals(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED) ? xmlStyle.getStyleType() : null);
                } catch (NoSuchAlgorithmException e29) {
                    throw new UnsupportedOperationException("Hay al menos un algoritmo no soportado: " + e29, e29);
                } catch (Exception e30) {
                    throw new AOException("Error al generar la firma XMLdSig: " + e30, e30);
                }
            } catch (Exception e31) {
                throw new AOException("No se ha podido obtener un generador de huellas digitales para el algoritmo '" + property3 + OperatorName.SHOW_TEXT_LINE, e31);
            }
        } catch (Exception e32) {
            throw new AOException("Error al crear la firma en formato " + property + ", modo " + property2, e32);
        }
    }

    private static boolean isDetached(Element element) {
        if (element != null && element.getFirstChild().getLocalName() != null && element.getFirstChild().getLocalName().equals(DETACHED_CONTENT_ELEMENT_NAME)) {
            return true;
        }
        return false;
    }

    private static boolean isEnveloped(Element element) {
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_TRANSFORM);
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            if (((Element) elementsByTagNameNS.item(i)).getAttribute("Algorithm").equals("http://www.w3.org/2000/09/xmldsig#enveloped-signature")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEnveloping(Element element) {
        if (element == null) {
            return false;
        }
        return "Signature".equals(element.getLocalName()) || (AFIRMA.equals(element.getNodeName()) && "Signature".equals(element.getFirstChild().getLocalName()));
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws AOInvalidFormatException {
        return getData(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws AOInvalidFormatException {
        DocumentBuilderFactory.newInstance().setNamespaceAware(true);
        Element element = null;
        try {
            if (!isSign(bArr)) {
                throw new AOInvalidFormatException("El documento no es un documento de firmas valido.");
            }
            Element documentElement = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)).getDocumentElement();
            if (isDetached(documentElement)) {
                Element element2 = (Element) documentElement.getFirstChild();
                if (element2.getAttribute("MimeType").equals("text/xml")) {
                    element = (Element) element2.getFirstChild();
                } else {
                    return Base64.decode(element2.getTextContent());
                }
            } else if (isEnveloped(documentElement)) {
                NodeList elementsByTagNameNS = documentElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
                int length = elementsByTagNameNS.getLength();
                for (int i = 0; i < length; i++) {
                    if (elementsByTagNameNS.item(i) != null) {
                        documentElement.removeChild(elementsByTagNameNS.item(i));
                    }
                }
                element = documentElement;
            } else if (isEnveloping(documentElement)) {
                Element element3 = (Element) documentElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_OBJECT).item(0);
                if (element3.getAttribute("MimeType").equals("text/xml")) {
                    element = (Element) element3.getFirstChild();
                } else {
                    return Base64.decode(element3.getTextContent());
                }
            }
            if (element == null) {
                return null;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            writeXML(new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream)), element);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new AOInvalidFormatException("Error al leer el fichero de firmas", e);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        String str2;
        String str3 = XMLConstants.SIGN_ALGOS_URI.get(str);
        if (str3 == null) {
            throw new UnsupportedOperationException("La URI de definicion del algoritmo de firma no puede ser nula");
        }
        Properties properties2 = properties != null ? properties : new Properties();
        String property = properties2.getProperty(XAdESExtraParams.REFERENCES_DIGEST_METHOD, "http://www.w3.org/2000/09/xmldsig#sha1");
        String property2 = properties2.getProperty(XAdESExtraParams.CANONICALIZATION_ALGORITHM, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        String property3 = properties2.getProperty("xmlSignaturePrefix", XML_SIGNATURE_PREFIX);
        DocumentBuilderFactory.newInstance().setNamespaceAware(true);
        Hashtable hashtable = new Hashtable();
        try {
            Document documentInsertarNodoAfirma = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr2));
            Element documentElement = documentInsertarNodoAfirma.getDocumentElement();
            if ("Signature".equals(documentElement.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(documentElement.getNamespaceURI())) {
                documentInsertarNodoAfirma = insertarNodoAfirma(documentInsertarNodoAfirma);
                documentElement = documentInsertarNodoAfirma.getDocumentElement();
            }
            ArrayList arrayList = new ArrayList();
            XMLSignatureFactory dOMFactory = Utils.getDOMFactory();
            try {
                DigestMethod digestMethodNewDigestMethod = dOMFactory.newDigestMethod(property, (DigestMethodParameterSpec) null);
                ArrayList arrayList2 = new ArrayList();
                NodeList elementsByTagNameNS = ((Element) documentInsertarNodoAfirma.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").item(0)).getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference");
                XMLObject xMLObjectNewXMLObject = null;
                for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
                    Node nodeItem = elementsByTagNameNS.item(i);
                    NamedNodeMap attributes = nodeItem.getAttributes();
                    if (i == 0 || (attributes.getNamedItem("Id") != null && attributes.getNamedItem("Id").getNodeValue().startsWith(STYLE_REFERENCE_PREFIX))) {
                        try {
                            List<Transform> objectReferenceTransforms = Utils.getObjectReferenceTransforms(nodeItem, property3);
                            if (attributes.getNamedItem("Id") != null && attributes.getNamedItem("Id").getNodeValue().startsWith(STYLE_REFERENCE_PREFIX)) {
                                str2 = STYLE_REFERENCE_PREFIX + UUID.randomUUID().toString();
                            } else {
                                str2 = "Reference-" + UUID.randomUUID().toString();
                            }
                            arrayList2.add(str2);
                            String attribute = ((Element) nodeItem).getAttribute("URI");
                            if (isEnveloping(documentElement) && attribute != null) {
                                Element elementSearchDataElement = searchDataElement(attribute, documentElement);
                                if (elementSearchDataElement == null) {
                                    LOGGER.severe("No se ha identificado el nodo de datos a firmar");
                                    throw new AOException("No se ha identificado el nodo de datos a firmar");
                                }
                                ArrayList arrayList3 = new ArrayList(1);
                                arrayList3.add(new DOMStructure(elementSearchDataElement.getFirstChild().cloneNode(true)));
                                String attribute2 = elementSearchDataElement.getAttribute("MimeType");
                                String attribute3 = elementSearchDataElement.getAttribute("Encoding");
                                String str4 = "Object-" + UUID.randomUUID().toString();
                                xMLObjectNewXMLObject = dOMFactory.newXMLObject(arrayList3, str4, attribute2, attribute3);
                                arrayList.add(dOMFactory.newReference("#" + str4, digestMethodNewDigestMethod, objectReferenceTransforms, "http://www.w3.org/2000/09/xmldsig#Object", str2));
                            } else {
                                arrayList.add(dOMFactory.newReference(((Element) nodeItem).getAttribute("URI"), digestMethodNewDigestMethod, objectReferenceTransforms, "http://www.w3.org/2000/09/xmldsig#Object", str2));
                            }
                        } catch (InvalidAlgorithmParameterException e) {
                            Logger.getLogger("Se han especificado parametros erroneos para una transformacion personalizada: " + e);
                            throw new AOException("Se han especificado parametros erroneos para una transformacion personalizada", e);
                        } catch (NoSuchAlgorithmException e2) {
                            Logger.getLogger("Se ha declarado una transformacion personalizada de un tipo no soportado: " + e2);
                            throw new AOException("Se ha declarado una transformacion personalizada de un tipo no soportado", e2);
                        }
                    }
                }
                String string = UUID.randomUUID().toString();
                String str5 = "Signature-" + string;
                String str6 = "SignatureValue-" + string;
                String str7 = "KeyInfo-" + string;
                try {
                    CanonicalizationMethod canonicalizationMethodNewCanonicalizationMethod = dOMFactory.newCanonicalizationMethod(property2, (C14NMethodParameterSpec) null);
                    ArrayList arrayList4 = new ArrayList();
                    arrayList4.add(dOMFactory.newTransform(property2, (TransformParameterSpec) null));
                    arrayList.add(dOMFactory.newReference("#" + str7, digestMethodNewDigestMethod, arrayList4, (String) null, (String) null));
                    SignatureMethod signatureMethodNewSignatureMethod = dOMFactory.newSignatureMethod(str3, (SignatureMethodParameterSpec) null);
                    KeyInfoFactory keyInfoFactory = dOMFactory.getKeyInfoFactory();
                    X509Certificate x509Certificate = (X509Certificate) certificateArr[0];
                    ArrayList arrayList5 = new ArrayList();
                    arrayList5.add(keyInfoFactory.newKeyValue(x509Certificate.getPublicKey()));
                    Certificate[] certificateArr2 = null;
                    if (!Boolean.parseBoolean(properties2.getProperty("includeOnlySignningCertificate", Boolean.FALSE.toString()))) {
                        certificateArr2 = certificateArr;
                    }
                    if (certificateArr2 == null) {
                        certificateArr2 = new Certificate[]{x509Certificate};
                    }
                    arrayList5.add(keyInfoFactory.newX509Data(Arrays.asList(certificateArr2)));
                    DOMSignContext dOMSignContext = new DOMSignContext(privateKey, documentElement);
                    dOMSignContext.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", property3);
                    try {
                        dOMSignContext.setURIDereferencer(new CustomUriDereferencer(CustomUriDereferencer.getDefaultDereferencer()));
                    } catch (Exception e3) {
                        LOGGER.warning("No se ha podido instalar un dereferenciador a medida, es posible que fallen las firmas de nodos concretos: " + e3);
                    }
                    ArrayList arrayList6 = new ArrayList();
                    if (isEnveloping(documentElement) && xMLObjectNewXMLObject != null) {
                        arrayList6.add(xMLObjectNewXMLObject);
                    }
                    dOMFactory.newXMLSignature(dOMFactory.newSignedInfo(canonicalizationMethodNewCanonicalizationMethod, signatureMethodNewSignatureMethod, arrayList), keyInfoFactory.newKeyInfo(arrayList5, str7), arrayList6, str5, str6).sign(dOMSignContext);
                    return Utils.writeXML(documentElement, hashtable, null, null);
                } catch (NoSuchAlgorithmException e4) {
                    throw new UnsupportedOperationException("Hay al menos un algoritmo no soportado: " + e4, e4);
                } catch (Exception e5) {
                    throw new AOException("Error al generar la cofirma XMLdSig: " + e5, e5);
                }
            } catch (Exception e6) {
                throw new AOException("No se ha podido obtener un generador de huellas digitales para el algoritmo '" + property + OperatorName.SHOW_TEXT_LINE, e6);
            }
        } catch (IOException e7) {
            throw new AOException("Error al leer el documento de firmas", e7);
        } catch (IllegalArgumentException e8) {
            throw new AOException("Parametro de entrada incorrecto", e8);
        } catch (ParserConfigurationException e9) {
            throw new AOException("Error en el amalizador XML: " + e9, e9);
        } catch (SAXException e10) {
            throw new AOException("Formato de documento de firmas (XML firmado de entrada) incorrecto: " + e10, e10);
        } catch (Exception e11) {
            throw new AOException("No se ha podido leer el documento XML de firmas", e11);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        DocumentBuilderFactory.newInstance().setNamespaceAware(true);
        try {
            Element documentElement = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)).getDocumentElement();
            Document documentNewDocument = Utils.getNewDocumentBuilder().newDocument();
            Element element = (Element) documentNewDocument.adoptNode(documentElement.cloneNode(true));
            for (NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature"); elementsByTagNameNS.getLength() > 0; elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature")) {
                element.removeChild(elementsByTagNameNS.item(0));
            }
            documentNewDocument.appendChild(element);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            writeXML(new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream)), documentElement);
            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            writeXML(new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream2)), element);
            return cosign(byteArrayOutputStream2.toByteArray(), byteArrayOutputStream.toByteArray(), str, privateKey, certificateArr, properties);
        } catch (IOException e) {
            throw new AOException("Error al leer el documento de firmas: " + e, e);
        } catch (IllegalArgumentException e2) {
            throw new AOException("Parametro de entrada incorrecto: " + e2, e2);
        } catch (ParserConfigurationException e3) {
            throw new AOException("Error en el analizador XML: " + e3, e3);
        } catch (SAXException e4) {
            throw new AOException("Formato de documento de firmas (XML firmado de entrada) incorrecto: " + e4, e4);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        String systemId;
        if (XMLConstants.SIGN_ALGOS_URI.get(str) == null) {
            throw new UnsupportedOperationException("La URI de definicion del algoritmo de firma no puede ser nula");
        }
        Properties properties2 = properties != null ? properties : new Properties();
        String property = properties2.getProperty(XAdESExtraParams.REFERENCES_DIGEST_METHOD, "http://www.w3.org/2000/09/xmldsig#sha1");
        String property2 = properties2.getProperty(XAdESExtraParams.CANONICALIZATION_ALGORITHM, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        String property3 = properties2.getProperty("encoding");
        if ("base64".equalsIgnoreCase(property3)) {
            property3 = "http://www.w3.org/2000/09/xmldsig#base64";
        }
        String property4 = properties2.getProperty("xmlSignaturePrefix", XML_SIGNATURE_PREFIX);
        boolean z = Boolean.parseBoolean(properties2.getProperty("includeOnlySignningCertificate", Boolean.FALSE.toString()));
        this.algo = str;
        DocumentBuilderFactory.newInstance().setNamespaceAware(true);
        Hashtable hashtable = new Hashtable();
        try {
            this.doc = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr));
            if (property3 == null) {
                property3 = this.doc.getXmlEncoding();
            }
            if (property3 != null && !"http://www.w3.org/2000/09/xmldsig#base64".equalsIgnoreCase(property3)) {
                hashtable.put("encoding", property3);
            }
            String xmlVersion = this.doc.getXmlVersion();
            if (xmlVersion != null) {
                hashtable.put(XmlConsts.XML_DECL_KW_VERSION, xmlVersion);
            }
            DocumentType doctype = this.doc.getDoctype();
            if (doctype != null && (systemId = doctype.getSystemId()) != null) {
                hashtable.put("doctype-system", systemId);
            }
            Element documentElement = this.doc.getDocumentElement();
            if ("Signature".equals(documentElement.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(documentElement.getNamespaceURI())) {
                this.doc = insertarNodoAfirma(this.doc);
                documentElement = this.doc.getDocumentElement();
            }
            if (counterSignTarget == CounterSignTarget.TREE) {
                countersignTree(documentElement, privateKey, certificateArr, z, property, property2, property4);
            } else if (counterSignTarget == CounterSignTarget.LEAFS) {
                countersignLeafs(documentElement, privateKey, certificateArr, z, property, property2, property4);
            } else if (counterSignTarget == CounterSignTarget.NODES) {
                countersignNodes(documentElement, objArr, privateKey, certificateArr, z, property, property2, property4);
            } else if (counterSignTarget == CounterSignTarget.SIGNERS) {
                countersignSigners(documentElement, objArr, privateKey, certificateArr, z, property, property2, property4);
            }
            return Utils.writeXML(this.doc.getDocumentElement(), hashtable, null, null);
        } catch (Exception e) {
            throw new AOException("No se ha podido realizar la contrafirma: " + e, e);
        }
    }

    private void countersignTree(Element element, PrivateKey privateKey, Certificate[] certificateArr, boolean z, String str, String str2, String str3) throws AOException {
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        Element[] elementArr = new Element[elementsByTagNameNS.getLength()];
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            elementArr[i] = (Element) elementsByTagNameNS.item(i);
        }
        int length = elementArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            Element element2 = elementArr[i2];
            try {
                cs(element2, privateKey, certificateArr, z, str, str2, str3);
            } catch (Exception e) {
                throw new AOException("No se ha podido realizar la contrafirma del nodo '" + (element2 != null ? element2.getNodeName() : "nulo") + "': " + e, e);
            }
        }
    }

    private void countersignLeafs(Element element, PrivateKey privateKey, Certificate[] certificateArr, boolean z, String str, String str2, String str3) throws AOException {
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        NodeList elementsByTagNameNS2 = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference");
        String[] strArr = new String[elementsByTagNameNS.getLength()];
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            strArr[i] = ((Element) elementsByTagNameNS.item(i)).getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue").item(0).getAttributes().getNamedItem("Id").getNodeValue();
        }
        for (int i2 = 0; i2 < strArr.length; i2++) {
            try {
                String str4 = "#" + strArr[i2];
                boolean z2 = true;
                for (int i3 = 0; i3 < elementsByTagNameNS2.getLength(); i3++) {
                    if (((Element) elementsByTagNameNS2.item(i3)).getAttribute("URI").equals(str4)) {
                        z2 = false;
                    }
                }
                if (z2) {
                    cs((Element) elementsByTagNameNS.item(i2), privateKey, certificateArr, z, str, str2, str3);
                }
            } catch (Exception e) {
                throw new AOException("No se ha podido realizar la contrafirma: " + e, e);
            }
        }
    }

    private void countersignNodes(Element element, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, boolean z, String str, String str2, String str3) throws AOException {
        if (objArr == null) {
            throw new IllegalArgumentException("La lista de nodos a contrafirmar no puede ser nula");
        }
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        String[] strArr = new String[elementsByTagNameNS.getLength()];
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            strArr[i] = ((Element) elementsByTagNameNS.item(i)).getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue").item(0).getAttributes().getNamedItem("Id").getNodeValue();
        }
        ArrayList arrayList = new ArrayList(elementsByTagNameNS.getLength());
        for (int i2 = 0; i2 < elementsByTagNameNS.getLength(); i2++) {
            boolean z2 = true;
            NodeList elementsByTagNameNS2 = ((Element) elementsByTagNameNS.item(i2)).getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference");
            for (int i3 = 0; i3 < elementsByTagNameNS2.getLength(); i3++) {
                if (CSURI.equals(((Element) elementsByTagNameNS2.item(i3)).getAttribute("Type"))) {
                    z2 = false;
                    break;
                }
            }
            if (z2) {
                arrayList.add((Element) elementsByTagNameNS.item(i2));
                addSubNodes(strArr[i2], elementsByTagNameNS, strArr, arrayList);
            }
        }
        List listAsList = Arrays.asList(objArr);
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            if (listAsList.contains(Integer.valueOf(i4))) {
                cs(arrayList.get(i4), privateKey, certificateArr, z, str, str2, str3);
            }
        }
    }

    private void addSubNodes(String str, NodeList nodeList, String[] strArr, List<Element> list) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList elementsByTagNameNS = ((Element) nodeList.item(i)).getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference");
            for (int i2 = 0; i2 < elementsByTagNameNS.getLength(); i2++) {
                if (("#" + str).equals(((Element) elementsByTagNameNS.item(i2)).getAttribute("URI"))) {
                    if (!strArr[i].equals(str)) {
                        list.add((Element) nodeList.item(i));
                        addSubNodes(strArr[i], nodeList, strArr, list);
                        break;
                    }
                    break;
                }
            }
        }
    }

    private void countersignSigners(Element element, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, boolean z, String str, String str2, String str3) throws AOException {
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        int length = elementsByTagNameNS.getLength();
        List listAsList = Arrays.asList(objArr);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < length; i++) {
            Element element2 = (Element) elementsByTagNameNS.item(i);
            if (listAsList.contains(AOUtil.getCN(Utils.getCertificate(element2.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate").item(0))))) {
                arrayList.add(element2);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            cs((Element) it.next(), privateKey, certificateArr, z, str, str2, str3);
        }
    }

    private void cs(Element element, PrivateKey privateKey, Certificate[] certificateArr, boolean z, String str, String str2, String str3) throws AOException {
        Element element2 = (Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue").item(0);
        ArrayList arrayList = new ArrayList();
        XMLSignatureFactory dOMFactory = Utils.getDOMFactory();
        try {
            DigestMethod digestMethodNewDigestMethod = dOMFactory.newDigestMethod(str, (DigestMethodParameterSpec) null);
            String str4 = "Reference-" + UUID.randomUUID().toString();
            try {
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(dOMFactory.newTransform(str2, (TransformParameterSpec) null));
                arrayList.add(dOMFactory.newReference("#" + element2.getAttribute("Id"), digestMethodNewDigestMethod, arrayList2, CSURI, str4));
                String string = UUID.randomUUID().toString();
                String str5 = "Signature-" + string;
                String str6 = "SignatureValue-" + string;
                String str7 = "KeyInfo-" + string;
                try {
                    arrayList.add(dOMFactory.newReference("#" + str7, digestMethodNewDigestMethod));
                    KeyInfoFactory keyInfoFactory = dOMFactory.getKeyInfoFactory();
                    X509Certificate x509Certificate = (X509Certificate) certificateArr[0];
                    ArrayList arrayList3 = new ArrayList();
                    arrayList3.add(keyInfoFactory.newKeyValue(x509Certificate.getPublicKey()));
                    Certificate[] certificateArr2 = null;
                    if (!z) {
                        certificateArr2 = certificateArr;
                    }
                    if (certificateArr2 == null) {
                        certificateArr2 = new Certificate[]{x509Certificate};
                    }
                    arrayList3.add(keyInfoFactory.newX509Data(Arrays.asList(certificateArr2)));
                    XMLSignature xMLSignatureNewXMLSignature = dOMFactory.newXMLSignature(dOMFactory.newSignedInfo(dOMFactory.newCanonicalizationMethod(str2, (C14NMethodParameterSpec) null), dOMFactory.newSignatureMethod(XMLConstants.SIGN_ALGOS_URI.get(this.algo), (SignatureMethodParameterSpec) null), arrayList), keyInfoFactory.newKeyInfo(arrayList3, str7), (List) null, str5, str6);
                    DOMSignContext dOMSignContext = new DOMSignContext(privateKey, element.getOwnerDocument().getDocumentElement());
                    dOMSignContext.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", str3);
                    try {
                        dOMSignContext.setURIDereferencer(new CustomUriDereferencer(CustomUriDereferencer.getDefaultDereferencer()));
                    } catch (Exception e) {
                        LOGGER.warning("No se ha podido instalar un dereferenciador a medida, es posible que fallen las firmas de nodos concretos: " + e);
                    }
                    xMLSignatureNewXMLSignature.sign(dOMSignContext);
                } catch (NoSuchAlgorithmException e2) {
                    throw new UnsupportedOperationException("Hay al menos un algoritmo no soportado: " + e2, e2);
                } catch (Exception e3) {
                    throw new AOException("No se ha podido realizar la contrafirma: " + e3, e3);
                }
            } catch (Exception e4) {
                throw new AOException("No se ha podido anadir la transformacion de canonizacion en la contrafirma: " + e4, e4);
            }
        } catch (Exception e5) {
            throw new AOException("No se ha podido obtener un generador de huellas digitales para el algoritmo '" + str + "': " + e5, e5);
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) {
        return getSignersStructure(bArr, null, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) {
        DocumentBuilderFactory.newInstance().setNamespaceAware(true);
        try {
            this.doc = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr));
            Element documentElement = this.doc.getDocumentElement();
            String strGuessXmlDSigNamespacePrefix = XmlDSigUtil.guessXmlDSigNamespacePrefix(documentElement);
            String str = "".equals(strGuessXmlDSigNamespacePrefix) ? "" : strGuessXmlDSigNamespacePrefix + ":";
            if (documentElement.getNodeName().equals(str + "Signature")) {
                this.doc = insertarNodoAfirma(this.doc);
                documentElement = this.doc.getDocumentElement();
            }
            AOTreeNode aOTreeNode = new AOTreeNode("Datos");
            NodeList elementsByTagName = documentElement.getElementsByTagName(str + "Signature");
            NodeList elementsByTagName2 = documentElement.getElementsByTagName(str + "SignatureValue");
            int length = elementsByTagName.getLength();
            String[] strArr = new String[length];
            String[] strArr2 = new String[length];
            AOTreeNode[] aOTreeNodeArr = new AOTreeNode[length];
            for (int i = 0; i < length; i++) {
                Element element = (Element) elementsByTagName.item(i);
                strArr[i] = element.getAttribute("Id");
                aOTreeNodeArr[i] = new AOTreeNode(z ? Utils.getSimpleSignInfoNode("http://uri.etsi.org/01903#", element) : Utils.getStringInfoNode(element));
                if (((Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference").item(0)).getAttribute("Type").equals(CSURI)) {
                    strArr2[i] = Utils.getCounterSignerReferenceId(element, elementsByTagName2);
                } else {
                    strArr2[i] = "";
                }
            }
            for (int i2 = 0; i2 < length; i2++) {
                if ("".equals(strArr2[i2])) {
                    aOTreeNode.add(generaArbol(i2, length - 1, aOTreeNodeArr, strArr, strArr2)[i2]);
                }
            }
            return new AOTreeModel(aOTreeNode);
        } catch (Exception e) {
            LOGGER.warning("Se ha producido un error al obtener la estructura de firmas: " + e);
            return null;
        }
    }

    private AOTreeNode[] generaArbol(int i, int i2, AOTreeNode[] aOTreeNodeArr, String[] strArr, String[] strArr2) {
        int length = strArr.length;
        if (i < length && i2 > 0) {
            if (strArr[i].equals(strArr2[i2])) {
                generaArbol(i + 1, i2 - 1, aOTreeNodeArr, strArr, strArr2);
            }
            if (i < i2) {
                generaArbol(i, i2 - 1, aOTreeNodeArr, strArr, strArr2);
            }
            if (!strArr[i].equals(strArr2[i2])) {
                return aOTreeNodeArr;
            }
            generaArbol(i2, length - 1, aOTreeNodeArr, strArr, strArr2);
            aOTreeNodeArr[i].add(aOTreeNodeArr[i2]);
        }
        return aOTreeNodeArr;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        return isSign(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        try {
            Element documentElement = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)).getDocumentElement();
            ArrayList arrayList = new ArrayList();
            if ("Signature".equals(documentElement.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(documentElement.getNamespaceURI())) {
                arrayList.add(documentElement);
            }
            NodeList elementsByTagNameNS = documentElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
            for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
                arrayList.add(elementsByTagNameNS.item(i));
            }
            if (arrayList.size() == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        return true;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        return str + (str2 != null ? str2 : "") + ".xsig";
    }

    private static Document insertarNodoAfirma(Document document) throws ParserConfigurationException {
        DocumentBuilderFactory.newInstance().setNamespaceAware(true);
        Document documentNewDocument = Utils.getNewDocumentBuilder().newDocument();
        Element elementCreateElement = documentNewDocument.createElement(AFIRMA);
        elementCreateElement.appendChild(documentNewDocument.adoptNode(document.getDocumentElement()));
        documentNewDocument.appendChild(elementCreateElement);
        return documentNewDocument;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws AOException {
        return getSignInfo(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws AOException {
        Element documentElement;
        if (bArr == null) {
            throw new IllegalArgumentException("No se han introducido datos para analizar");
        }
        if (!isSign(bArr)) {
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un objeto de firma");
        }
        AOSignInfo aOSignInfo = new AOSignInfo(AOSignConstants.SIGN_FORMAT_XMLDSIG);
        DocumentBuilderFactory.newInstance().setNamespaceAware(true);
        try {
            documentElement = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)).getDocumentElement();
        } catch (Exception e) {
            LOGGER.warning("Error al analizar la firma: " + e);
            documentElement = null;
        }
        if (documentElement != null) {
            if (isDetached(documentElement)) {
                aOSignInfo.setVariant(AOSignConstants.SIGN_FORMAT_XMLDSIG_DETACHED);
            } else if (isEnveloped(documentElement)) {
                aOSignInfo.setVariant(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED);
            } else if (isEnveloping(documentElement)) {
                aOSignInfo.setVariant(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPING);
            }
        }
        return aOSignInfo;
    }

    private static void writeXML(Writer writer, Node node) {
        LSSerializer lSSerializerCreateLSSerializer = ((DOMImplementationLS) node.getOwnerDocument().getImplementation()).createLSSerializer();
        lSSerializerCreateLSSerializer.getDomConfig().setParameter("namespaces", Boolean.FALSE);
        DOMOutputImpl dOMOutputImpl = new DOMOutputImpl();
        dOMOutputImpl.setCharacterStream(writer);
        lSSerializerCreateLSSerializer.write(node, dOMOutputImpl);
    }

    private static Element searchDataElement(String str, Element element) {
        String strSubstring = str.substring(str.startsWith("#") ? 1 : 0);
        Element element2 = null;
        Node namedItem = element.getAttributes() != null ? element.getAttributes().getNamedItem("Id") : null;
        if (namedItem != null && strSubstring.equals(namedItem.getNodeValue())) {
            element2 = element;
        } else {
            NodeList childNodes = element.getChildNodes();
            for (int length = childNodes.getLength() - 1; length >= 0; length--) {
                Node namedItem2 = childNodes.item(length).getAttributes() != null ? childNodes.item(length).getAttributes().getNamedItem("Id") : null;
                if (namedItem2 != null && strSubstring.equals(namedItem2.getNodeValue())) {
                    element2 = (Element) childNodes.item(length);
                    break;
                }
                if ("Signature".equals(childNodes.item(length).getLocalName())) {
                    NodeList childNodes2 = childNodes.item(length).getChildNodes();
                    for (int length2 = childNodes2.getLength() - 1; length2 >= 0; length2--) {
                        Node namedItem3 = childNodes2.item(length2).getAttributes() != null ? childNodes2.item(length2).getAttributes().getNamedItem("Id") : null;
                        if (namedItem3 != null && strSubstring.equals(namedItem3.getNodeValue())) {
                            element2 = (Element) childNodes2.item(length2);
                            break;
                        }
                    }
                    if (element2 != null) {
                        break;
                    }
                }
            }
        }
        return element2;
    }
}
