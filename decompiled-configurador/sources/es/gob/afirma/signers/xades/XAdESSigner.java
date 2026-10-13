package es.gob.afirma.signers.xades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOFileUtils;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.misc.http.SSLErrorProcessor;
import es.gob.afirma.core.misc.http.UrlHttpManager;
import es.gob.afirma.core.misc.http.UrlHttpManagerFactory;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.xml.InvalidXMLException;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signers.xml.XMLConstants;
import es.gob.afirma.signers.xml.dereference.CustomUriDereferencer;
import es.gob.afirma.signers.xml.style.CannotDereferenceException;
import es.gob.afirma.signers.xml.style.IsInnerlException;
import es.gob.afirma.signers.xml.style.ReferenceIsNotXmlException;
import es.gob.afirma.signers.xml.style.XmlStyle;
import es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIndication;
import es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormat;
import es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormatImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifierImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import org.apache.xml.security.transforms.Transforms;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xades/XAdESSigner.class */
public final class XAdESSigner {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String FILE_PROTOCOL_PREFIX = "file://";
    private static final String HTTP_PROTOCOL_PREFIX = "http://";
    private static final String HTTPS_PROTOCOL_PREFIX = "https://";

    private XAdESSigner() {
    }

    public static byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return sign(bArr, str, privateKey, certificateArr, properties, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public static byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, URIDereferencer uRIDereferencer) throws MarshalException, XMLSignatureException, AOException {
        Element elementCreateElement;
        String mimeType;
        String mimeType2;
        Transform transformNewTransform;
        String attribute;
        Reference referenceNewReference;
        String str2 = str != null ? str : "SHA512withRSA";
        Properties properties2 = properties != null ? properties : new Properties();
        checkParams(str2, properties2);
        String str3 = XMLConstants.SIGN_ALGOS_URI.get(str2);
        if (str3 == null) {
            throw new IllegalArgumentException("Los formatos de firma XML no soportan el algoritmo de firma '" + str2 + "'");
        }
        boolean z = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.AVOID_XPATH_EXTRA_TRANSFORMS_ON_ENVELOPED, Boolean.FALSE.toString()));
        boolean z2 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.INCLUDE_ONLY_SIGNNING_CERTIFICATE, Boolean.FALSE.toString()));
        boolean z3 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.USE_MANIFEST, Boolean.FALSE.toString()));
        String property = properties2.getProperty(XAdESExtraParams.INSERT_ENVELOPED_SIGNATURE_ON_NODE_BY_XPATH);
        String property2 = properties2.getProperty(XAdESExtraParams.NODE_TOSIGN);
        boolean z4 = property2 == null ? false : Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.AVOID_ENVELOPED_TRANSFORM_WHEN_SIGNING_NODE, Boolean.FALSE.toString()));
        String property3 = properties2.getProperty(XAdESExtraParams.FORMAT, AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING);
        String property4 = properties2.getProperty(XAdESExtraParams.REFERENCES_DIGEST_METHOD, "http://www.w3.org/2001/04/xmlenc#sha512");
        String property5 = properties2.getProperty(XAdESExtraParams.PRECALCULATED_HASH_ALGORITHM, property4);
        String property6 = properties2.getProperty(XAdESExtraParams.CANONICALIZATION_ALGORITHM, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        if ("none".equalsIgnoreCase(property6)) {
            property6 = null;
        }
        String property7 = properties2.getProperty(XAdESExtraParams.XADES_NAMESPACE, "http://uri.etsi.org/01903/v1.3.2#");
        String property8 = properties2.getProperty(XAdESExtraParams.SIGNED_PROPERTIES_TYPE_URL, "http://uri.etsi.org/01903#SignedProperties");
        boolean z5 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.IGNORE_STYLE_SHEETS, Boolean.FALSE.toString()));
        boolean z6 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.AVOID_BASE64_TRANSFORMS, Boolean.FALSE.toString()));
        boolean z7 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.HEADLESS, Boolean.TRUE.toString()));
        boolean z8 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.ADD_KEY_INFO_KEY_VALUE, Boolean.TRUE.toString()));
        boolean z9 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.ADD_KEY_INFO_KEY_NAME, Boolean.FALSE.toString()));
        boolean z10 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.ADD_KEY_INFO_X509_ISSUER_SERIAL, Boolean.FALSE.toString()));
        boolean z11 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.FACTURAE_SIGN, Boolean.FALSE.toString()));
        String property9 = properties2.getProperty(XAdESExtraParams.OUTPUT_XML_ENCODING);
        String property10 = properties2.getProperty("mimeType");
        String property11 = properties2.getProperty(XAdESExtraParams.CONTENT_TYPE_OID);
        String property12 = properties2.getProperty("encoding");
        if ("base64".equalsIgnoreCase(property12)) {
            property12 = "http://www.w3.org/2000/09/xmldsig#base64";
        }
        if (property12 != null && !property12.isEmpty()) {
            try {
                new URI(property12);
            } catch (Exception e) {
                throw new AOException("La codificacion indicada en 'encoding' debe ser una URI: " + e, e);
            }
        }
        boolean z12 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.INTERNAL_VALIDATE_PKCS1, Boolean.TRUE.toString()));
        String property13 = properties2.getProperty(XAdESExtraParams.PROFILE, "advanced");
        boolean z13 = AOSignConstants.SIGN_PROFILE_BASELINE.equalsIgnoreCase(property13) || Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.KEEP_KEYINFO_UNSIGNED, Boolean.FALSE.toString()));
        if (z3) {
            property3 = AOSignConstants.SIGN_FORMAT_XADES_EXTERNALLY_DETACHED;
        }
        URI uriCreateURI = null;
        try {
            uriCreateURI = properties2.containsKey("uri") ? AOUtil.createURI(properties2.getProperty("uri")) : null;
        } catch (Exception e2) {
            LOGGER.warning("Se ha pasado una URI invalida como referencia a los datos a firmar: " + e2);
        }
        Utils.checkIllegalParams(property3, properties2.getProperty("mode"), z3, uriCreateURI, property5, true);
        if ((bArr == null || bArr.length == 0) && !AOSignConstants.SIGN_FORMAT_XADES_EXTERNALLY_DETACHED.equals(property3)) {
            throw new AOException("No se han podido leer los datos a firmar");
        }
        Map<String, String> hashtable = new Hashtable();
        try {
            DocumentBuilder newDocumentBuilder = Utils.getNewDocumentBuilder();
            Document documentNewDocument = null;
            String str4 = "CONTENT-" + generateUUID();
            String str5 = "STYLE-" + generateUUID();
            boolean z14 = false;
            boolean z15 = false;
            boolean z16 = false;
            XmlStyle xmlStyle = new XmlStyle();
            Element elementById = null;
            Document documentLoadDataAsXml = loadDataAsXml(newDocumentBuilder, bArr);
            if (documentLoadDataAsXml != null) {
                if (!z5) {
                    try {
                        xmlStyle = new XmlStyle(bArr, z7, false);
                    } catch (CannotDereferenceException e3) {
                        LOGGER.warning("La hoja de estilo no ha podido dereferenciar, probablemente sea un enlace relativo local: " + e3);
                    } catch (IsInnerlException e4) {
                        LOGGER.info("La hoja de estilo esta referenciada internamente, por lo que no se necesita dereferenciar: " + e4);
                    } catch (ReferenceIsNotXmlException e5) {
                        LOGGER.warning("La hoja de estilo referenciada no es XML o no se ha dereferenciado apropiadamente: " + e5);
                    } catch (Exception e6) {
                        LOGGER.severe("Error intentando dereferenciar la hoja de estilo: " + e6);
                    }
                }
                if (property10 == null || "application/octet-stream".equals(property10)) {
                    property10 = "text/xml";
                }
                hashtable = XAdESUtil.getOriginalXMLProperties(documentLoadDataAsXml, property9);
                if (AOSignConstants.SIGN_FORMAT_XADES_DETACHED.equals(property3)) {
                    if (property2 != null) {
                        elementCreateElement = CustomUriDereferencer.getElementById(documentLoadDataAsXml, property2);
                        if (!elementCreateElement.isSameNode(documentLoadDataAsXml.getDocumentElement())) {
                            documentNewDocument = documentLoadDataAsXml;
                            z16 = true;
                            elementById = (Element) elementCreateElement.getParentNode();
                        }
                    } else {
                        elementCreateElement = documentLoadDataAsXml.createElement("CONTENT");
                        elementCreateElement.setAttributeNS(null, "Id", str4);
                        elementCreateElement.setAttributeNS(null, "MimeType", property10);
                        if (property12 != null && !"".equals(property12)) {
                            elementCreateElement.setAttributeNS(null, "Encoding", property12);
                        }
                        elementCreateElement.appendChild(documentLoadDataAsXml.getDocumentElement());
                    }
                    if (xmlStyle.getStyleElement() != null) {
                        try {
                            Element elementCreateElement2 = documentLoadDataAsXml.createElement("STYLE");
                            elementCreateElement2.setAttributeNS(null, "Id", str5);
                            if (xmlStyle.getStyleType() != null) {
                                elementCreateElement2.setAttributeNS(null, "MimeType", xmlStyle.getStyleType());
                            }
                            elementCreateElement2.setAttributeNS(null, "Encoding", xmlStyle.getStyleEncoding());
                            elementCreateElement2.appendChild(documentLoadDataAsXml.adoptNode(xmlStyle.getStyleElement().cloneNode(true)));
                            xmlStyle.setStyleElement(elementCreateElement2);
                        } catch (Exception e7) {
                            LOGGER.warning("No ha sido posible crear el elemento DOM para incluir la hoja de estilo del XML como Internally Detached: " + e7);
                            xmlStyle.setStyleElement(null);
                        }
                    }
                } else {
                    elementCreateElement = documentLoadDataAsXml.getDocumentElement();
                }
            } else {
                if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equals(property3) || property2 != null) {
                    throw new InvalidXMLException("Las firmas XAdES Enveloped solo pueden realizarse sobre datos XML");
                }
                if (AOSignConstants.SIGN_FORMAT_XADES_DETACHED.equals(property3) || AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING.equals(property3)) {
                    LOGGER.fine("El documento se convertira a Base64");
                    try {
                        elementCreateElement = newDocumentBuilder.newDocument().createElement("CONTENT");
                        elementCreateElement.setAttributeNS(null, "Id", str4);
                        if (bArr != null && Base64.isBase64(bArr) && "http://www.w3.org/2000/09/xmldsig#base64".equals(property12)) {
                            LOGGER.info("El documento se ha indicado como Base64, se insertara como tal en el XML");
                            byte[] bArrDecode = Base64.decode(bArr, 0, bArr.length, false);
                            if (property10 == null && (mimeType2 = new MimeHelper(bArrDecode).getMimeType()) != null) {
                                property10 = mimeType2;
                            }
                            elementCreateElement.setAttributeNS(null, "MimeType", property10 != null ? property10 : "application/octet-stream");
                            elementCreateElement.setTextContent(Base64.encode(bArrDecode));
                        } else {
                            if ("http://www.w3.org/2000/09/xmldsig#base64".equals(property12)) {
                                LOGGER.warning("El documento se ha indicado como Base64, pero no es un Base64 valido. Se convertira a Base64 antes de insertarlo en el XML y se declarara la transformacion");
                            } else {
                                LOGGER.fine("El documento se considera binario, se convertira a Base64 antes de insertarlo en el XML y se declarara la transformacion");
                            }
                            if (property10 == null && (mimeType = new MimeHelper(bArr).getMimeType()) != null) {
                                property10 = mimeType;
                            }
                            elementCreateElement.setAttributeNS(null, "MimeType", property10 != null ? property10 : "application/octet-stream");
                            elementCreateElement.setTextContent(Base64.encode(bArr));
                            z15 = true;
                        }
                        z14 = true;
                        property12 = "http://www.w3.org/2000/09/xmldsig#base64";
                        elementCreateElement.setAttributeNS(null, "Encoding", property12);
                    } catch (Exception e8) {
                        throw new AOException("Error al convertir los datos a Base64", e8);
                    }
                } else {
                    elementCreateElement = null;
                }
            }
            String str6 = "#" + (property2 != null ? property2 : str4);
            String str7 = "#" + str5;
            if (documentNewDocument == null) {
                try {
                    documentNewDocument = newDocumentBuilder.newDocument();
                    if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equals(property3)) {
                        documentNewDocument.appendChild(documentNewDocument.adoptNode(elementCreateElement));
                    } else {
                        documentNewDocument.appendChild(XAdESUtil.getRootElement(documentNewDocument, properties2));
                    }
                } catch (Exception e9) {
                    throw new AOException("Error al crear la firma en formato " + property3 + ": " + e9, e9);
                }
            }
            ArrayList arrayList = new ArrayList();
            XMLSignatureFactory dOMFactory = Utils.getDOMFactory();
            try {
                DigestMethod digestMethodNewDigestMethod = dOMFactory.newDigestMethod(property4, (DigestMethodParameterSpec) null);
                String str8 = "Reference-" + generateUUID();
                String str9 = "StyleReference-" + generateUUID();
                ArrayList arrayList2 = new ArrayList();
                Utils.addCustomTransforms(arrayList2, properties2, "ds");
                if (property6 != null) {
                    try {
                        transformNewTransform = dOMFactory.newTransform(property6, (TransformParameterSpec) null);
                    } catch (Exception e10) {
                        throw new AOException("No se ha podido crear el canonizador para el algoritmo indicado (" + property6 + "): " + e10, e10);
                    }
                } else {
                    transformNewTransform = null;
                }
                if (z14 && z15 && !z6) {
                    try {
                        arrayList2.add(dOMFactory.newTransform("http://www.w3.org/2000/09/xmldsig#base64", (TransformParameterSpec) null));
                    } catch (Exception e11) {
                        throw new AOException("No se puede encontrar el algoritmo transformacion Base64: " + e11, e11);
                    }
                }
                List<ExternalReferenceData> listLoadManifestReferencesData = null;
                XMLObject xMLObjectNewXMLObject = null;
                XMLObject xMLObjectNewXMLObject2 = null;
                if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING.equals(property3)) {
                    if (!z14 && transformNewTransform != null) {
                        arrayList2.add(transformNewTransform);
                    }
                    try {
                        ArrayList arrayList3 = new ArrayList(1);
                        if (elementCreateElement == null) {
                            throw new IllegalStateException("El elemento (nodo) de datos no puede ser nulo en este punto");
                        }
                        arrayList3.add(new DOMStructure(z14 ? elementCreateElement.getFirstChild() : elementCreateElement));
                        String str10 = "Object-" + generateUUID();
                        xMLObjectNewXMLObject = dOMFactory.newXMLObject(arrayList3, str10, property10 != null ? property10 : "application/octet-stream", property12);
                        arrayList.add(dOMFactory.newReference("#" + (property2 != null ? property2 : str10), digestMethodNewDigestMethod, arrayList2, property2 != null ? null : "http://www.w3.org/2000/09/xmldsig#Object", str8));
                        if (xmlStyle.getStyleElement() != null) {
                            String str11 = "StyleObject-" + generateUUID();
                            xMLObjectNewXMLObject2 = dOMFactory.newXMLObject(Collections.singletonList(new DOMStructure(xmlStyle.getStyleElement())), str11, xmlStyle.getStyleType(), xmlStyle.getStyleEncoding());
                            arrayList.add(dOMFactory.newReference("#" + str11, digestMethodNewDigestMethod, Collections.singletonList(transformNewTransform), "http://www.w3.org/2000/09/xmldsig#Object", str9));
                        }
                        if (xmlStyle.getStyleHref() != null && xmlStyle.getStyleElement() == null && (xmlStyle.getStyleHref().startsWith(HTTP_PROTOCOL_PREFIX) || xmlStyle.getStyleHref().startsWith(HTTPS_PROTOCOL_PREFIX))) {
                            try {
                                arrayList.add(dOMFactory.newReference(xmlStyle.getStyleHref(), digestMethodNewDigestMethod, transformNewTransform != null ? Collections.singletonList(transformNewTransform) : new ArrayList(0), (String) null, str9));
                            } catch (Exception e12) {
                                LOGGER.severe("No ha sido posible anadir la referencia a la hoja de estilo externa del XML para Enveloping, esta no se firmara: " + e12);
                            }
                        }
                    } catch (Exception e13) {
                        throw new AOException("Error al generar la firma en formato enveloping", e13);
                    }
                } else if (AOSignConstants.SIGN_FORMAT_XADES_DETACHED.equals(property3)) {
                    if (!z14 && transformNewTransform != null) {
                        arrayList2.add(transformNewTransform);
                    }
                    if (elementCreateElement != null) {
                        if (!z16) {
                            try {
                                documentNewDocument.getDocumentElement().appendChild(documentNewDocument.adoptNode(elementCreateElement));
                            } catch (Exception e14) {
                                throw new AOException("Error al generar la firma en formato detached: " + e14, e14);
                            }
                        }
                        arrayList.add(dOMFactory.newReference(str6, digestMethodNewDigestMethod, arrayList2, (String) null, str8));
                    }
                    if (xmlStyle.getStyleElement() != null) {
                        documentNewDocument.getDocumentElement().appendChild(documentNewDocument.adoptNode(xmlStyle.getStyleElement()));
                        arrayList.add(dOMFactory.newReference(str7, digestMethodNewDigestMethod, Collections.singletonList(transformNewTransform), (String) null, str9));
                    }
                    if (xmlStyle.getStyleHref() != null && xmlStyle.getStyleElement() == null && (xmlStyle.getStyleHref().startsWith(HTTP_PROTOCOL_PREFIX) || xmlStyle.getStyleHref().startsWith(HTTPS_PROTOCOL_PREFIX))) {
                        try {
                            arrayList.add(dOMFactory.newReference(xmlStyle.getStyleHref(), digestMethodNewDigestMethod, transformNewTransform != null ? Collections.singletonList(transformNewTransform) : new ArrayList(0), (String) null, str9));
                        } catch (Exception e15) {
                            LOGGER.severe("No ha sido posible anadir la referencia a la hoja remota de estilo del XML para firma Detached, esta no se firmara: " + e15);
                        }
                    }
                } else if (AOSignConstants.SIGN_FORMAT_XADES_EXTERNALLY_DETACHED.equals(property3)) {
                    String str12 = property5 != null ? property5 : property4;
                    try {
                        DigestMethod digestMethodNewDigestMethod2 = dOMFactory.newDigestMethod(XAdESUtil.getDigestMethodByCommonName(str12), (DigestMethodParameterSpec) null);
                        if (z3) {
                            if (!(property5 != null)) {
                                LOGGER.warning("No se ha proporcionado la huella asociada a la referencia del parametro 'uri'. Compruebe proporcionar la huella como datos y el parametro 'precalculatedHashAlgorithm' con algoritmo de hash");
                                throw new AOException("No se ha proporcionado la huella asociada a la referencia");
                            }
                            try {
                                listLoadManifestReferencesData = loadManifestReferencesData(bArr, str8, properties2);
                                for (ExternalReferenceData externalReferenceData : listLoadManifestReferencesData) {
                                    try {
                                        arrayList.add(dOMFactory.newReference(externalReferenceData.getUri(), digestMethodNewDigestMethod2, (List) null, (String) null, externalReferenceData.getId(), externalReferenceData.getMessageDigest()));
                                    } catch (Exception e16) {
                                        throw new AOException("Error al generar la firma Manifest al no poder crear una de las referencias externas", e16);
                                    }
                                }
                            } catch (NoSuchAlgorithmException e17) {
                                throw new AOException("No se ha podido calcular la huella de los datos proporcionados", e17);
                            } catch (Exception e18) {
                                LOGGER.severe("No se han indicado los parametros necesarios ('uri1', 'uri2'... y 'md1', 'md2'...) para construir las referencias a los datos");
                                throw new AOException("No se han indicado los parametros necesarios para construir las referencias a los datos", e18);
                            }
                        } else {
                            try {
                                ExternalReferenceData externalReferenceDataLoadExternalReferenceData = loadExternalReferenceData(properties2.getProperty("uri"), bArr, property5 != null, str12);
                                if (externalReferenceDataLoadExternalReferenceData.getMessageDigest() != null) {
                                    try {
                                        referenceNewReference = dOMFactory.newReference(externalReferenceDataLoadExternalReferenceData.getUri(), digestMethodNewDigestMethod2, (List) null, (String) null, str8, externalReferenceDataLoadExternalReferenceData.getMessageDigest());
                                    } catch (Exception e19) {
                                        throw new AOException("Error crear la referencia externa de la firma Externally Detached", e19);
                                    }
                                } else {
                                    referenceNewReference = createExternalReferenceFromUri(externalReferenceDataLoadExternalReferenceData.getUri(), dOMFactory, digestMethodNewDigestMethod, str8, properties2);
                                }
                                arrayList.add(referenceNewReference);
                            } catch (NoSuchAlgorithmException e20) {
                                throw new AOException("Error al cargar la referencia externa de la firma Externally Detached", e20);
                            }
                        }
                    } catch (Exception e21) {
                        throw new AOException("No se ha podido crear el metodo de huella digital para la referencia a datos externos: " + e21, e21);
                    }
                } else if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equals(property3)) {
                    if (!z4) {
                        try {
                            arrayList2.add(dOMFactory.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (TransformParameterSpec) null));
                        } catch (Exception e22) {
                            throw new AOException("Error al generar la firma en formato enveloped: " + e22, e22);
                        }
                    }
                    if (!z11 && transformNewTransform != null) {
                        arrayList2.add(transformNewTransform);
                    }
                    if (property2 == null && (attribute = documentNewDocument.getDocumentElement().getAttribute("Id")) != null && !attribute.isEmpty()) {
                        property2 = attribute;
                    }
                    if (!z11 && !z) {
                        arrayList2.add(dOMFactory.newTransform(Transforms.TRANSFORM_XPATH, new XPathFilterParameterSpec("not(ancestor-or-self::ds:Signature)", Collections.singletonMap("ds", "http://www.w3.org/2000/09/xmldsig#"))));
                    }
                    arrayList.add(dOMFactory.newReference(property2 != null ? "#" + property2 : "", digestMethodNewDigestMethod, arrayList2, (String) null, str8));
                    if (xmlStyle.getStyleHref() != null && xmlStyle.getStyleElement() == null && (xmlStyle.getStyleHref().startsWith(HTTP_PROTOCOL_PREFIX) || xmlStyle.getStyleHref().startsWith(HTTPS_PROTOCOL_PREFIX))) {
                        try {
                            arrayList.add(dOMFactory.newReference(xmlStyle.getStyleHref(), digestMethodNewDigestMethod, transformNewTransform != null ? Collections.singletonList(transformNewTransform) : new ArrayList(0), (String) null, str9));
                        } catch (Exception e23) {
                            LOGGER.severe("No ha sido posible anadir la referencia a la hoja de estilo remota del XML para firma Enveloped, esta no se firmara: " + e23);
                        }
                    }
                }
                if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equals(property3)) {
                    if (property != null) {
                        elementById = XAdESUtil.getFirstElementFromXPath(property, documentNewDocument.getDocumentElement());
                    } else if (property2 != null) {
                        elementById = CustomUriDereferencer.getElementById(documentNewDocument, property2);
                    }
                }
                XAdESBase xAdESBaseNewInstance = XAdESUtil.newInstance(property13, property7, "xades", "ds", property4, documentNewDocument, elementById != null ? elementById : documentNewDocument.getDocumentElement(), (X509Certificate) certificateArr[0]);
                XAdESCommonMetadataUtil.addCommonMetadata(xAdESBaseNewInstance, properties2);
                ArrayList arrayList4 = new ArrayList();
                if (listLoadManifestReferencesData != null) {
                    for (ExternalReferenceData externalReferenceData2 : listLoadManifestReferencesData) {
                        arrayList4.add(createDataObjectFormat(externalReferenceData2.getId(), externalReferenceData2.getMimeType(), externalReferenceData2.getOid(), externalReferenceData2.getEncoding()));
                    }
                } else {
                    arrayList4.add(createDataObjectFormat(str8, property10, property11, property12));
                }
                xAdESBaseNewInstance.setDataObjectFormats(arrayList4);
                List<CommitmentTypeIndication> commitmentTypeIndications = XAdESUtil.parseCommitmentTypeIndications(properties2, str8);
                if (commitmentTypeIndications != null && commitmentTypeIndications.size() > 0) {
                    xAdESBaseNewInstance.setCommitmentTypeIndications(commitmentTypeIndications);
                }
                AOXMLAdvancedSignature xmlAdvancedSignature = XAdESUtil.getXmlAdvancedSignature(xAdESBaseNewInstance, property8, property4, property6 != null ? property6 : "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", uRIDereferencer);
                if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING.equals(property3)) {
                    xmlAdvancedSignature.addXMLObject(xMLObjectNewXMLObject);
                    if (xMLObjectNewXMLObject2 != null) {
                        xmlAdvancedSignature.addXMLObject(xMLObjectNewXMLObject2);
                    }
                }
                if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equals(property3) && xmlStyle.getStyleElement() != null) {
                    xmlAdvancedSignature.addStyleSheetEnvelopingOntoSignature(xmlStyle, str5);
                    try {
                        arrayList.add(dOMFactory.newReference(str7, digestMethodNewDigestMethod, transformNewTransform != null ? Collections.singletonList(transformNewTransform) : new ArrayList(0), "http://www.w3.org/2000/09/xmldsig#Object", str9));
                    } catch (Exception e24) {
                        LOGGER.severe("No se ha podido anadir una referencia a la hoja de estilo, esta se incluira dentro de la firma, pero no estara firmada: " + e24);
                    }
                }
                if (z3) {
                    XAdESUtil.createManifest(arrayList, dOMFactory, xmlAdvancedSignature, digestMethodNewDigestMethod, transformNewTransform, str8);
                }
                try {
                    xmlAdvancedSignature.sign(z2 ? Arrays.asList(certificateArr[0]) : Arrays.asList(certificateArr), privateKey, str3, arrayList, "Signature-" + generateUUID(), z8, z9, z10, z13, z12);
                    if (AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING.equals(property3)) {
                        try {
                            if (documentNewDocument.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").getLength() == 1) {
                                Document documentNewDocument2 = newDocumentBuilder.newDocument();
                                documentNewDocument2.appendChild(documentNewDocument2.adoptNode(documentNewDocument.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").item(0)));
                                documentNewDocument = documentNewDocument2;
                            }
                        } catch (Exception e25) {
                            LOGGER.info("No se ha eliminado el nodo padre '<AFIRMA>': " + e25);
                        }
                    }
                    return Utils.writeXML(documentNewDocument.getDocumentElement(), hashtable, AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equals(property3) ? xmlStyle.getStyleHref() : null, AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED.equals(property3) ? xmlStyle.getStyleType() : null);
                } catch (AOException e26) {
                    throw e26;
                } catch (NoSuchAlgorithmException e27) {
                    throw new IllegalArgumentException("Los formatos de firma XML no soportan el algoritmo de firma '" + str2 + "':" + e27, e27);
                } catch (Exception e28) {
                    throw new AOException("Error al generar la firma XAdES: " + e28, e28);
                }
            } catch (Exception e29) {
                throw new AOException("No se ha podido obtener un generador de huellas digitales para el algoritmo '" + property4 + "'", e29);
            }
        } catch (Exception e30) {
            throw new AOException("No se han podido componer la factoria para la construccion de la firma", e30);
        }
    }

    private static void checkParams(String str, Properties properties) {
        if (str.toUpperCase(Locale.US).startsWith("MD")) {
            throw new IllegalArgumentException("XAdES no permite huellas digitales MD2 o MD5 (Decision 130/2011 CE)");
        }
        String property = properties.getProperty(XAdESExtraParams.PROFILE);
        if (AOSignConstants.SIGN_PROFILE_BASELINE.equalsIgnoreCase(property)) {
            if (AOSignConstants.isSHA1SignatureAlgorithm(str)) {
                LOGGER.warning("El algoritmo '" + str + "' no esta recomendado para su uso en las firmas baseline");
            }
            if ("http://www.w3.org/2000/09/xmldsig#sha1".equals(properties.getProperty(XAdESExtraParams.REFERENCES_DIGEST_METHOD))) {
                LOGGER.warning("El algoritmo SHA1 no esta recomendado para generar referencias en las firmas baseline");
            }
        }
        if (AOSignConstants.SIGN_PROFILE_BASELINE.equalsIgnoreCase(property) && properties.containsKey(XAdESExtraParams.XADES_NAMESPACE) && !XAdESUtil.isBaselineCompatible(properties.getProperty(XAdESExtraParams.XADES_NAMESPACE))) {
            LOGGER.warning("Se ha indicado realizar una firma baseline con un espacio de nombres que no lo soporta. Se ignorara el espacio de nombres indicado");
            properties.remove(XAdESExtraParams.XADES_NAMESPACE);
            properties.remove(XAdESExtraParams.SIGNED_PROPERTIES_TYPE_URL);
        }
    }

    private static DataObjectFormat createDataObjectFormat(String str, String str2, String str3, String str4) {
        String strTransformMimeTypeToOid = str3;
        if (strTransformMimeTypeToOid == null && str2 != null) {
            try {
                strTransformMimeTypeToOid = MimeHelper.transformMimeTypeToOid(str2);
            } catch (Exception e) {
                LOGGER.warning("Error en la obtencion del OID del tipo de datos a partir del MimeType: " + e);
            }
            if (!"application/octet-stream".equals(str2) && MimeHelper.DEFAULT_CONTENT_OID_DATA.equals(strTransformMimeTypeToOid)) {
                strTransformMimeTypeToOid = null;
            }
        }
        ObjectIdentifierImpl objectIdentifierImpl = null;
        if (strTransformMimeTypeToOid != null) {
            if (!strTransformMimeTypeToOid.startsWith("urn:oid:")) {
                strTransformMimeTypeToOid = "urn:oid:" + strTransformMimeTypeToOid;
            }
            objectIdentifierImpl = new ObjectIdentifierImpl("OIDAsURN", strTransformMimeTypeToOid, null, new ArrayList(0));
        }
        return new DataObjectFormatImpl(null, objectIdentifierImpl, str2 != null ? str2 : "application/octet-stream", str4, "#" + str);
    }

    private static Document loadDataAsXml(DocumentBuilder documentBuilder, byte[] bArr) {
        Document document;
        if (bArr != null && AOFileUtils.isXML(bArr)) {
            try {
                document = documentBuilder.parse(new ByteArrayInputStream(bArr));
            } catch (Exception e) {
                LOGGER.fine("Los datos proporcionados no son XML: " + e);
                document = null;
            }
        } else {
            LOGGER.info("Se firman datos externos");
            document = null;
        }
        return document;
    }

    private static ExternalReferenceData loadExternalReferenceData(String str, byte[] bArr, boolean z, String str2) throws NoSuchAlgorithmException {
        ExternalReferenceData externalReferenceData;
        if (bArr != null) {
            if (z) {
                externalReferenceData = new ExternalReferenceData(str, bArr);
            } else {
                externalReferenceData = new ExternalReferenceData(str, MessageDigest.getInstance(str2).digest(bArr));
            }
        } else {
            externalReferenceData = new ExternalReferenceData(str, null);
        }
        return externalReferenceData;
    }

    private static List<ExternalReferenceData> loadManifestReferencesData(byte[] bArr, String str, Properties properties) throws NoSuchAlgorithmException, AOException {
        ArrayList arrayList = new ArrayList();
        if (properties.containsKey("uri")) {
            if (bArr == null) {
                throw new AOException("No se ha proporcionado en el parametro de datos la huella correspondiente a los datos referenciados por la URI");
            }
            String property = properties.getProperty("uri");
            String property2 = properties.getProperty("mimeType");
            String property3 = properties.getProperty(XAdESExtraParams.CONTENT_TYPE_OID);
            String property4 = properties.getProperty("encoding");
            ExternalReferenceData externalReferenceData = new ExternalReferenceData(property, bArr);
            externalReferenceData.setId(str);
            externalReferenceData.setMimeType(property2);
            externalReferenceData.setOid(property3);
            externalReferenceData.setEncoding(property4);
            arrayList.add(externalReferenceData);
        } else {
            for (int i = 1; properties.containsKey("uri" + i); i++) {
                if (!properties.containsKey(XAdESExtraParams.MD_PREFIX + i)) {
                    throw new AOException(String.format("No se ha indicado la huella de la referencia %d del manifest", Integer.valueOf(i)));
                }
                try {
                    ExternalReferenceData externalReferenceData2 = new ExternalReferenceData(properties.getProperty("uri" + i), Base64.decode(properties.getProperty(XAdESExtraParams.MD_PREFIX + i)));
                    externalReferenceData2.setId("Reference-" + generateUUID());
                    externalReferenceData2.setMimeType(properties.getProperty("mimeType" + i));
                    externalReferenceData2.setOid(properties.getProperty(XAdESExtraParams.CONTENT_TYPE_OID + i));
                    externalReferenceData2.setEncoding(properties.getProperty("encoding" + i));
                    arrayList.add(externalReferenceData2);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Se ha indicado un base 64 no valido como huella de la referencia " + i, (Throwable) e);
                    throw new AOException(String.format("Se ha indicado un base 64 no valido como huella de la referencia %d del manifest", Integer.valueOf(i)), e);
                }
            }
            if (arrayList.size() == 0) {
                throw new AOException("No se han proporcionado las referencias y huellas de los datos a firmar");
            }
        }
        return arrayList;
    }

    private static Reference createExternalReferenceFromUri(String str, XMLSignatureFactory xMLSignatureFactory, DigestMethod digestMethod, String str2, Properties properties) throws AOException {
        Reference referenceNewReference;
        if (FILE_PROTOCOL_PREFIX.equalsIgnoreCase(str.substring(0, FILE_PROTOCOL_PREFIX.length()))) {
            try {
                InputStream inputStreamLoadFile = AOUtil.loadFile(AOUtil.createURI(str));
                Throwable th = null;
                try {
                    try {
                        referenceNewReference = xMLSignatureFactory.newReference(str, digestMethod, (List) null, (String) null, str2, MessageDigest.getInstance(AOSignConstants.getDigestAlgorithmName(digestMethod.getAlgorithm())).digest(AOUtil.getDataFromInputStream(inputStreamLoadFile)));
                        if (inputStreamLoadFile != null) {
                            if (0 != 0) {
                                try {
                                    inputStreamLoadFile.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                inputStreamLoadFile.close();
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (inputStreamLoadFile != null) {
                        if (th != null) {
                            try {
                                inputStreamLoadFile.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            inputStreamLoadFile.close();
                        }
                    }
                    throw th4;
                }
            } catch (Exception e) {
                throw new AOException("No se ha podido crear la referencia XML a partir de la URI local (" + str + "): " + e, e);
            }
        } else if (HTTP_PROTOCOL_PREFIX.equalsIgnoreCase(str.substring(0, HTTP_PROTOCOL_PREFIX.length())) || HTTPS_PROTOCOL_PREFIX.equalsIgnoreCase(str.substring(0, HTTPS_PROTOCOL_PREFIX.length()))) {
            try {
                UrlHttpManager installedManager = UrlHttpManagerFactory.getInstalledManager();
                SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor(properties);
                try {
                    referenceNewReference = xMLSignatureFactory.newReference(str, digestMethod, (List) null, (String) null, str2, MessageDigest.getInstance(AOSignConstants.getDigestAlgorithmName(digestMethod.getAlgorithm())).digest(installedManager.readUrl(str, UrlHttpMethod.GET, sSLErrorProcessor)));
                } catch (IOException e2) {
                    if (sSLErrorProcessor.isCancelled()) {
                        LOGGER.info("El usuario no permite la importacion del certificado SSL de confianza de un recurso externo en: " + LoggerUtil.getTrimStr(str));
                    }
                    throw new AOException("Error en la recuperacion de un recurso externo: " + e2, e2);
                }
            } catch (Exception e3) {
                throw new AOException("No se ha podido crear la referencia XML a partir de la URI local (" + str + "): " + e3, e3);
            }
        } else {
            try {
                referenceNewReference = xMLSignatureFactory.newReference(str, digestMethod, (List) null, (String) null, str2);
            } catch (Exception e4) {
                throw new AOException("No se ha podido crear la referencia Externally Detached, probablemente por no obtenerse el metodo de digest: " + e4, e4);
            }
        }
        return referenceNewReference;
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
