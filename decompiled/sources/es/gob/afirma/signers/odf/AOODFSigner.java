package es.gob.afirma.signers.odf;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.cfg.XmlConsts;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOFormatFileException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.keystores.AOCancelledSMOperationException;
import es.gob.afirma.core.keystores.AuthenticationException;
import es.gob.afirma.core.keystores.LockedKeyStoreException;
import es.gob.afirma.core.keystores.PinException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.core.misc.SecureXmlTransformer;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signers.xml.XmlDSigProviderHelper;
import es.gob.afirma.standalone.CommandLineParameters;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/odf/AOODFSigner.class */
public final class AOODFSigner implements AOSigner {
    private static final String EXTENSION_ODT = ".odt";
    private static final String EXTENSION_ODP = ".odp";
    private static final String EXTENSION_ODS = ".ods";
    private static final String EXTENSION_ODF = ".odf";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String OPENOFFICE = "urn:oasis:names:tc:opendocument:xmlns:digitalsignature:1.0";
    private static final String MANIFEST_PATH = "META-INF/manifest.xml";
    private static final String SIGNATURES_PATH = "META-INF/documentsignatures.xml";
    private static final String XMLDSIG_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";
    private static final Set<String> SUPPORTED_FORMATS;
    private static final String CANONICAL_XML_ALGORITHM = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    private static final String DEFAULT_DIGEST_METHOD = "http://www.w3.org/2000/09/xmldsig#sha1";
    private static final String DIGEST_METHOD_ALGORITHM_NAME = "SHA1";
    private static final String ENTRY_MIMETYPE = "mimetype";
    private static final int THRESHOLD_FILE_SIZE = 1000000000;

    static {
        XmlDSigProviderHelper.configureXmlDSigProvider();
        SUPPORTED_FORMATS = new HashSet();
        SUPPORTED_FORMATS.add("application/vnd.oasis.opendocument.text");
        SUPPORTED_FORMATS.add("application/vnd.oasis.opendocument.spreadsheet");
        SUPPORTED_FORMATS.add("application/vnd.oasis.opendocument.presentation");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 54, insn: 0x08dd: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r54 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:275:0x08dd */
    /* JADX WARN: Not initialized variable reg: 55, insn: 0x08e2: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r55 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:277:0x08e2 */
    /* JADX WARN: Type inference failed for: r0v23 */
    /* JADX WARN: Type inference failed for: r0v24, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r26v0, types: [java.lang.Throwable, java.util.zip.ZipFile] */
    /* JADX WARN: Type inference failed for: r26v1 */
    /* JADX WARN: Type inference failed for: r26v2 */
    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        Element elementCreateElement;
        ZipOutputStream zipOutputStream;
        Throwable th;
        Reference referenceNewReference;
        if (bArr.length >= THRESHOLD_FILE_SIZE) {
            throw new AOException("Los datos tienen un tamano superior al permitido");
        }
        Properties properties2 = properties != null ? properties : new Properties();
        String property = properties2.getProperty(XAdESExtraParams.REFERENCES_DIGEST_METHOD, "http://www.w3.org/2000/09/xmldsig#sha1");
        boolean z = Boolean.parseBoolean(properties2.getProperty("useOpenOffice31Mode"));
        if (!isValidDataFile(bArr)) {
            throw new AOFormatFileException("Los datos introducidos no se corresponden con un documento ODF");
        }
        boolean z2 = false;
        try {
            File fileCreateTempFile = File.createTempFile(CommandLineParameters.MASSIVE_OP_SIGN, ".zip");
            FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFile);
            ?? r26 = 0;
            try {
                try {
                    fileOutputStream.write(bArr);
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        if (0 != 0) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable th2) {
                                r26.addSuppressed(th2);
                            }
                        } else {
                            fileOutputStream.close();
                        }
                    }
                    try {
                        fileCreateTempFile.deleteOnExit();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ZipFile zipFile = new ZipFile(fileCreateTempFile);
                        Throwable th3 = null;
                        InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(MANIFEST_PATH));
                        Throwable th4 = null;
                        try {
                            try {
                                byte[] dataFromInputStream = AOUtil.getDataFromInputStream(inputStream);
                                if (inputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            inputStream.close();
                                        } catch (Throwable th5) {
                                            th4.addSuppressed(th5);
                                        }
                                    } else {
                                        inputStream.close();
                                    }
                                }
                                Document document = SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(dataFromInputStream));
                                Element documentElement = document.getDocumentElement();
                                NodeList elementsByTagName = documentElement.getElementsByTagName("manifest:file-entry");
                                try {
                                    MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
                                    XMLSignatureFactory xMLSignatureFactory = XMLSignatureFactory.getInstance("DOM");
                                    try {
                                        DigestMethod digestMethodNewDigestMethod = xMLSignatureFactory.newDigestMethod(property, (DigestMethodParameterSpec) null);
                                        ArrayList arrayList = new ArrayList(1);
                                        arrayList.add(xMLSignatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (TransformParameterSpec) null));
                                        ArrayList arrayList2 = new ArrayList();
                                        if (!z) {
                                            InputStream inputStream2 = zipFile.getInputStream(zipFile.getEntry(ENTRY_MIMETYPE));
                                            Throwable th6 = null;
                                            try {
                                                try {
                                                    arrayList2.add(xMLSignatureFactory.newReference(ENTRY_MIMETYPE, digestMethodNewDigestMethod, (List) null, (String) null, (String) null, messageDigest.digest(AOUtil.getDataFromInputStream(inputStream2))));
                                                    if (inputStream2 != null) {
                                                        if (0 != 0) {
                                                            try {
                                                                inputStream2.close();
                                                            } catch (Throwable th7) {
                                                                th6.addSuppressed(th7);
                                                            }
                                                        } else {
                                                            inputStream2.close();
                                                        }
                                                    }
                                                    arrayList2.add(xMLSignatureFactory.newReference(MANIFEST_PATH, digestMethodNewDigestMethod, arrayList, (String) null, (String) null, messageDigest.digest(OdfXmlUtil.canonicalizeXml(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(dataFromInputStream)).getDocumentElement(), "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"))));
                                                } catch (Throwable th8) {
                                                    th6 = th8;
                                                    throw th8;
                                                }
                                            } catch (Throwable th9) {
                                                if (inputStream2 != null) {
                                                    if (th6 != null) {
                                                        try {
                                                            inputStream2.close();
                                                        } catch (Throwable th10) {
                                                            th6.addSuppressed(th10);
                                                        }
                                                    } else {
                                                        inputStream2.close();
                                                    }
                                                }
                                                throw th9;
                                            }
                                        }
                                        for (int i = 0; i < elementsByTagName.getLength(); i++) {
                                            String attribute = ((Element) elementsByTagName.item(i)).getAttribute("manifest:full-path");
                                            if (!attribute.endsWith("/")) {
                                                if (attribute.equals("content.xml") || attribute.equals("meta.xml") || attribute.equals("styles.xml") || attribute.equals("settings.xml")) {
                                                    InputStream inputStream3 = zipFile.getInputStream(zipFile.getEntry(attribute));
                                                    Throwable th11 = null;
                                                    try {
                                                        try {
                                                            referenceNewReference = xMLSignatureFactory.newReference(attribute.replace(" ", "%20"), digestMethodNewDigestMethod, arrayList, (String) null, (String) null, messageDigest.digest(OdfXmlUtil.canonicalizeXml(Utils.getNewDocumentBuilder().parse(inputStream3).getDocumentElement(), "http://www.w3.org/TR/2001/REC-xml-c14n-20010315")));
                                                            if (inputStream3 != null) {
                                                                if (0 != 0) {
                                                                    try {
                                                                        inputStream3.close();
                                                                    } catch (Throwable th12) {
                                                                        th11.addSuppressed(th12);
                                                                    }
                                                                } else {
                                                                    inputStream3.close();
                                                                }
                                                            }
                                                        } catch (Throwable th13) {
                                                            if (inputStream3 != null) {
                                                                if (th11 != null) {
                                                                    try {
                                                                        inputStream3.close();
                                                                    } catch (Throwable th14) {
                                                                        th11.addSuppressed(th14);
                                                                    }
                                                                } else {
                                                                    inputStream3.close();
                                                                }
                                                            }
                                                            throw th13;
                                                        }
                                                    } catch (Throwable th15) {
                                                        th11 = th15;
                                                        throw th15;
                                                    }
                                                } else {
                                                    InputStream inputStream4 = zipFile.getInputStream(zipFile.getEntry(attribute));
                                                    Throwable th16 = null;
                                                    try {
                                                        try {
                                                            referenceNewReference = xMLSignatureFactory.newReference(attribute.replace(" ", "%20"), digestMethodNewDigestMethod, (List) null, (String) null, (String) null, messageDigest.digest(AOUtil.getDataFromInputStream(inputStream4)));
                                                            if (inputStream4 != null) {
                                                                if (0 != 0) {
                                                                    try {
                                                                        inputStream4.close();
                                                                    } catch (Throwable th17) {
                                                                        th16.addSuppressed(th17);
                                                                    }
                                                                } else {
                                                                    inputStream4.close();
                                                                }
                                                            }
                                                        } catch (Throwable th18) {
                                                            th16 = th18;
                                                            throw th18;
                                                        }
                                                    } catch (Throwable th19) {
                                                        if (inputStream4 != null) {
                                                            if (th16 != null) {
                                                                try {
                                                                    inputStream4.close();
                                                                } catch (Throwable th20) {
                                                                    th16.addSuppressed(th20);
                                                                }
                                                            } else {
                                                                inputStream4.close();
                                                            }
                                                        }
                                                        throw th19;
                                                    }
                                                }
                                                if (!attribute.equals(SIGNATURES_PATH)) {
                                                    arrayList2.add(referenceNewReference);
                                                } else {
                                                    z2 = true;
                                                }
                                            }
                                        }
                                        if (!z2 && zipFile.getEntry(SIGNATURES_PATH) != null) {
                                            z2 = true;
                                        }
                                        if (z2) {
                                            InputStream inputStream5 = zipFile.getInputStream(zipFile.getEntry(SIGNATURES_PATH));
                                            Throwable th21 = null;
                                            try {
                                                Document document2 = Utils.getNewDocumentBuilder().parse(inputStream5);
                                                if (inputStream5 != null) {
                                                    if (0 != 0) {
                                                        try {
                                                            inputStream5.close();
                                                        } catch (Throwable th22) {
                                                            th21.addSuppressed(th22);
                                                        }
                                                    } else {
                                                        inputStream5.close();
                                                    }
                                                }
                                                elementCreateElement = document2.getDocumentElement();
                                            } catch (Throwable th23) {
                                                if (inputStream5 != null) {
                                                    if (0 != 0) {
                                                        try {
                                                            inputStream5.close();
                                                        } catch (Throwable th24) {
                                                            th21.addSuppressed(th24);
                                                        }
                                                    } else {
                                                        inputStream5.close();
                                                    }
                                                }
                                                throw th23;
                                            }
                                        } else {
                                            Document documentNewDocument = Utils.getNewDocumentBuilder().newDocument();
                                            elementCreateElement = documentNewDocument.createElement("document-signatures");
                                            elementCreateElement.setAttribute(CanonicalizerBase.XMLNS, OPENOFFICE);
                                            documentNewDocument.appendChild(elementCreateElement);
                                            if (z) {
                                                Element elementCreateElement2 = document.createElement("manifest:file-entry");
                                                elementCreateElement2.setAttribute("manifest:media-type", "");
                                                elementCreateElement2.setAttribute("manifest:full-path", SIGNATURES_PATH);
                                                documentElement.appendChild(elementCreateElement2);
                                                Element elementCreateElement3 = document.createElement("manifest:file-entry");
                                                elementCreateElement3.setAttribute("manifest:media-type", "");
                                                elementCreateElement3.setAttribute("manifest:full-path", "META-INF/");
                                                documentElement.appendChild(elementCreateElement3);
                                            }
                                        }
                                        String string = UUID.randomUUID().toString();
                                        String string2 = UUID.randomUUID().toString();
                                        arrayList2.add(xMLSignatureFactory.newReference("#" + string2, digestMethodNewDigestMethod));
                                        Element documentElement2 = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(("<dc:date xmlns:dc=\"http://purl.org/dc/elements/1.1/\">" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SS").format(new Date()) + "</dc:date>").getBytes())).getDocumentElement();
                                        ArrayList arrayList3 = new ArrayList();
                                        arrayList3.add(new DOMStructure(documentElement2));
                                        ArrayList arrayList4 = new ArrayList();
                                        arrayList4.add(xMLSignatureFactory.newSignatureProperty(arrayList3, "#" + string, string2));
                                        ArrayList arrayList5 = new ArrayList();
                                        arrayList5.add(xMLSignatureFactory.newSignatureProperties(arrayList4, (String) null));
                                        ArrayList arrayList6 = new ArrayList();
                                        arrayList6.add(xMLSignatureFactory.newXMLObject(arrayList5, (String) null, (String) null, (String) null));
                                        KeyInfoFactory keyInfoFactory = xMLSignatureFactory.getKeyInfoFactory();
                                        ArrayList arrayList7 = new ArrayList();
                                        X509Certificate x509Certificate = (X509Certificate) certificateArr[0];
                                        arrayList7.add(x509Certificate.getSubjectX500Principal().getName());
                                        arrayList7.add(x509Certificate);
                                        try {
                                            xMLSignatureFactory.newXMLSignature(xMLSignatureFactory.newSignedInfo(xMLSignatureFactory.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (C14NMethodParameterSpec) null), xMLSignatureFactory.newSignatureMethod("http://www.w3.org/2000/09/xmldsig#rsa-sha1", (SignatureMethodParameterSpec) null), arrayList2), keyInfoFactory.newKeyInfo(Collections.singletonList(keyInfoFactory.newX509Data(arrayList7)), (String) null), arrayList6, string, (String) null).sign(new DOMSignContext(privateKey, elementCreateElement));
                                            try {
                                                ZipOutputStream zipOutputStream2 = new ZipOutputStream(byteArrayOutputStream);
                                                Throwable th25 = null;
                                                Enumeration<? extends ZipEntry> enumerationEntries = zipFile.entries();
                                                while (enumerationEntries.hasMoreElements()) {
                                                    ZipEntry zipEntryNextElement = enumerationEntries.nextElement();
                                                    ZipEntry zipEntry = new ZipEntry(zipEntryNextElement.getName());
                                                    if (!zipEntryNextElement.getName().equals(SIGNATURES_PATH) && !zipEntryNextElement.getName().equals(MANIFEST_PATH)) {
                                                        zipOutputStream2.putNextEntry(zipEntry);
                                                        InputStream inputStream6 = zipFile.getInputStream(zipEntryNextElement);
                                                        Throwable th26 = null;
                                                        try {
                                                            try {
                                                                zipOutputStream2.write(AOUtil.getDataFromInputStream(inputStream6));
                                                                if (inputStream6 != null) {
                                                                    if (0 != 0) {
                                                                        try {
                                                                            inputStream6.close();
                                                                        } catch (Throwable th27) {
                                                                            th26.addSuppressed(th27);
                                                                        }
                                                                    } else {
                                                                        inputStream6.close();
                                                                    }
                                                                }
                                                            } catch (Throwable th28) {
                                                                th26 = th28;
                                                                throw th28;
                                                            }
                                                        } catch (Throwable th29) {
                                                            if (inputStream6 != null) {
                                                                if (th26 != null) {
                                                                    try {
                                                                        inputStream6.close();
                                                                    } catch (Throwable th30) {
                                                                        th26.addSuppressed(th30);
                                                                    }
                                                                } else {
                                                                    inputStream6.close();
                                                                }
                                                            }
                                                            throw th29;
                                                        }
                                                    }
                                                }
                                                zipOutputStream2.putNextEntry(new ZipEntry(SIGNATURES_PATH));
                                                ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                                                writeXML((OutputStream) byteArrayOutputStream2, (Node) elementCreateElement, false);
                                                zipOutputStream2.write(byteArrayOutputStream2.toByteArray());
                                                zipOutputStream2.closeEntry();
                                                zipOutputStream2.putNextEntry(new ZipEntry(MANIFEST_PATH));
                                                ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
                                                writeXML((OutputStream) byteArrayOutputStream3, (Node) documentElement, false);
                                                zipOutputStream2.write(byteArrayOutputStream3.toByteArray());
                                                zipOutputStream2.closeEntry();
                                                if (zipOutputStream2 != null) {
                                                    if (0 != 0) {
                                                        try {
                                                            zipOutputStream2.close();
                                                        } catch (Throwable th31) {
                                                            th25.addSuppressed(th31);
                                                        }
                                                    } else {
                                                        zipOutputStream2.close();
                                                    }
                                                }
                                                if (zipFile != null) {
                                                    if (0 != 0) {
                                                        try {
                                                            zipFile.close();
                                                        } catch (Throwable th32) {
                                                            th3.addSuppressed(th32);
                                                        }
                                                    } else {
                                                        zipFile.close();
                                                    }
                                                }
                                                return byteArrayOutputStream.toByteArray();
                                            } catch (Throwable th33) {
                                                if (zipOutputStream != null) {
                                                    if (th != null) {
                                                        try {
                                                            zipOutputStream.close();
                                                        } catch (Throwable th34) {
                                                            th.addSuppressed(th34);
                                                        }
                                                    } else {
                                                        zipOutputStream.close();
                                                    }
                                                }
                                                throw th33;
                                            }
                                        } catch (XMLSignatureException e) {
                                            Throwable cause = e.getCause() != null ? e.getCause() : null;
                                            if (cause != null && "es.gob.jmulticard.jse.provider.SignatureAuthException".equals(cause.getClass().getName())) {
                                                String name = cause.getCause() != null ? cause.getCause().getClass().getName() : null;
                                                if ("es.gob.jmulticard.card.AuthenticationModeLockedException".equals(name)) {
                                                    throw new LockedKeyStoreException("El almacen de claves esta bloqueado", e);
                                                }
                                                if ("es.gob.jmulticard.card.BadPinException".equals(name)) {
                                                    throw new PinException("La contrasena del almacen o certificado es incorrecta", e);
                                                }
                                                throw new AuthenticationException("Ocurrio un error de autenticacion al utilizar la clave de firma", cause);
                                            }
                                            throw e;
                                        } catch (Exception e2) {
                                            if ("es.gob.jmulticard.CancelledOperationException".equals(e2.getClass().getName())) {
                                                throw new AOCancelledSMOperationException("Cancelacion del dialogo de JMulticard");
                                            }
                                            throw e2;
                                        }
                                    } catch (Exception e3) {
                                        throw new AOException("No se ha podido obtener un generador de huellas digitales con el algoritmo: " + property, e3);
                                    }
                                } catch (Exception e4) {
                                    throw new AOException("No se ha podido obtener un generador de huellas digitales con el algoritmo SHA1: " + e4, e4);
                                }
                            } catch (Throwable th35) {
                                th4 = th35;
                                throw th35;
                            }
                        } catch (Throwable th36) {
                            if (inputStream != null) {
                                if (th4 != null) {
                                    try {
                                        inputStream.close();
                                    } catch (Throwable th37) {
                                        th4.addSuppressed(th37);
                                    }
                                } else {
                                    inputStream.close();
                                }
                            }
                            throw th36;
                        }
                    } catch (Throwable th38) {
                        if (0 != 0) {
                            if (th2 != null) {
                                try {
                                    r26.close();
                                } catch (Throwable th39) {
                                    th2.addSuppressed(th39);
                                }
                            } else {
                                r26.close();
                            }
                        }
                        throw th38;
                    }
                } catch (Throwable th40) {
                    r26 = th40;
                    throw th40;
                }
            } catch (Throwable th41) {
                if (fileOutputStream != null) {
                    if (r26 != 0) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th42) {
                            r26.addSuppressed(th42);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                throw th41;
            }
        } catch (AOException e5) {
            throw e5;
        } catch (SAXException e6) {
            throw new AOFormatFileException("Estructura de archivo no valida '" + MANIFEST_PATH + "': " + e6);
        } catch (Exception e7) {
            throw new AOException("No ha sido posible generar la firma ODF: " + e7, e7);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return sign(bArr2, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return sign(bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No es posible realizar contrafirmas de ficheros ODF");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) throws IOException, AOInvalidFormatException {
        return getSignersStructure(bArr, null, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws IOException, AOInvalidFormatException {
        AOTreeNode aOTreeNode;
        if (bArr.length >= THRESHOLD_FILE_SIZE) {
            throw new IOException("Los datos tienen un tamano superior al permitido.");
        }
        if (!isSign(bArr)) {
            throw new AOInvalidFormatException("Los datos indicados no se corresponden con un ODF firmado");
        }
        try {
            AOTreeNode aOTreeNode2 = new AOTreeNode("Datos");
            ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
            Throwable th = null;
            try {
                if (getEntry(zipInputStream, SIGNATURES_PATH) == null) {
                    AOTreeModel aOTreeModel = new AOTreeModel(aOTreeNode2);
                    if (zipInputStream != null) {
                        if (0 != 0) {
                            try {
                                zipInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            zipInputStream.close();
                        }
                    }
                    return aOTreeModel;
                }
                NodeList elementsByTagNameNS = SecureXmlBuilder.getSecureDocumentBuilder().parse(zipInputStream).getDocumentElement().getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
                int length = elementsByTagNameNS.getLength();
                String[] strArr = new String[length];
                String[] strArr2 = new String[length];
                AOTreeNode[] aOTreeNodeArr = new AOTreeNode[length];
                for (int i = 0; i < length; i++) {
                    Element element = (Element) elementsByTagNameNS.item(i);
                    String textContent = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate").item(0).getTextContent();
                    if (z) {
                        aOTreeNode = new AOTreeNode(Utils.getSimpleSignInfoNode(null, element));
                    } else {
                        aOTreeNode = new AOTreeNode(AOUtil.getCN(Utils.createCert(textContent)));
                    }
                    strArr[i] = element.getAttribute("Id");
                    aOTreeNodeArr[i] = aOTreeNode;
                    if ("http://uri.etsi.org/01903#CountersignedSignature".equals(((Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference").item(0)).getAttribute("Type"))) {
                        String attribute = ((Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference").item(0)).getAttribute("URI");
                        strArr2[i] = attribute.substring(1, attribute.length() - 5);
                    } else {
                        strArr2[i] = "";
                    }
                }
                for (int i2 = length - 1; i2 > 0; i2--) {
                    for (int i3 = 0; i3 < length; i3++) {
                        if (strArr2[i2].equals(strArr[i3])) {
                            aOTreeNodeArr[i3].add(aOTreeNodeArr[i2]);
                        }
                    }
                }
                for (int i4 = 0; i4 < length; i4++) {
                    if ("".equals(strArr2[i4])) {
                        aOTreeNode2.add(aOTreeNodeArr[i4]);
                    }
                }
                if (zipInputStream != null) {
                    if (0 != 0) {
                        try {
                            zipInputStream.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        zipInputStream.close();
                    }
                }
                return new AOTreeModel(aOTreeNode2);
            } catch (Throwable th4) {
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
                throw th4;
            }
        } catch (Exception e) {
            LOGGER.warning("Se ha producido un error al obtener la estructura de firmas: " + e);
            return new AOTreeModel(new AOTreeNode("Raíz"));
        }
        LOGGER.warning("Se ha producido un error al obtener la estructura de firmas: " + e);
        return new AOTreeModel(new AOTreeNode("Raíz"));
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) throws IOException {
        return isSign(bArr, null);
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0057 */
    @Override // es.gob.afirma.core.signers.AOSigner
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean isSign(byte[] r7, java.util.Properties r8) throws java.io.IOException {
        /*
            r6 = this;
            r0 = r6
            r1 = r7
            boolean r0 = r0.isValidDataFile(r1)
            if (r0 != 0) goto La
            r0 = 0
            return r0
        La:
            r0 = 0
            r9 = r0
            java.util.zip.ZipInputStream r0 = new java.util.zip.ZipInputStream
            r1 = r0
            java.io.ByteArrayInputStream r2 = new java.io.ByteArrayInputStream
            r3 = r2
            r4 = r7
            r3.<init>(r4)
            r1.<init>(r2)
            r10 = r0
            r0 = 0
            r11 = r0
            r0 = r10
            java.lang.String r1 = "META-INF/documentsignatures.xml"
            java.util.zip.ZipEntry r0 = getEntry(r0, r1)     // Catch: java.lang.Throwable -> L4e java.lang.Throwable -> L57
            r9 = r0
            r0 = r10
            if (r0 == 0) goto L7f
            r0 = r11
            if (r0 == 0) goto L46
            r0 = r10
            r0.close()     // Catch: java.lang.Throwable -> L3a
            goto L7f
        L3a:
            r12 = move-exception
            r0 = r11
            r1 = r12
            r0.addSuppressed(r1)
            goto L7f
        L46:
            r0 = r10
            r0.close()
            goto L7f
        L4e:
            r12 = move-exception
            r0 = r12
            r11 = r0
            r0 = r12
            throw r0     // Catch: java.lang.Throwable -> L57
        L57:
            r13 = move-exception
            r0 = r10
            if (r0 == 0) goto L7c
            r0 = r11
            if (r0 == 0) goto L77
            r0 = r10
            r0.close()     // Catch: java.lang.Throwable -> L6b
            goto L7c
        L6b:
            r14 = move-exception
            r0 = r11
            r1 = r14
            r0.addSuppressed(r1)
            goto L7c
        L77:
            r0 = r10
            r0.close()
        L7c:
            r0 = r13
            throw r0
        L7f:
            r0 = r9
            if (r0 == 0) goto L87
            r0 = 1
            goto L88
        L87:
            r0 = 0
        L88:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.signers.odf.AOODFSigner.isSign(byte[], java.util.Properties):boolean");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (!new MimeHelper(bArr).isZipData()) {
            return false;
        }
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
            Throwable th = null;
            try {
                try {
                    String oDFMimeType = getODFMimeType(zipInputStream);
                    if (zipInputStream != null) {
                        if (0 != 0) {
                            try {
                                zipInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            zipInputStream.close();
                        }
                    }
                    return oDFMimeType != null && SUPPORTED_FORMATS.contains(oDFMimeType);
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (zipInputStream != null) {
                    if (th != null) {
                        try {
                            zipInputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        zipInputStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        String str3 = str2 != null ? str2 : "";
        if (str == null) {
            return str3 + EXTENSION_ODF;
        }
        String lowerCase = str.toLowerCase();
        if (lowerCase.length() <= EXTENSION_ODF.length()) {
            return str + str3 + EXTENSION_ODF;
        }
        if (lowerCase.endsWith(EXTENSION_ODT)) {
            return str.substring(0, str.length() - EXTENSION_ODT.length()) + str3 + EXTENSION_ODT;
        }
        if (lowerCase.endsWith(EXTENSION_ODP)) {
            return str.substring(0, str.length() - EXTENSION_ODP.length()) + str3 + EXTENSION_ODP;
        }
        if (lowerCase.endsWith(EXTENSION_ODS)) {
            return str.substring(0, str.length() - EXTENSION_ODS.length()) + str3 + EXTENSION_ODS;
        }
        return str + str3 + EXTENSION_ODF;
    }

    private static void writeXML(OutputStream outputStream, Node node, boolean z) {
        writeXML(new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)), node, z);
    }

    private static void writeXML(Writer writer, Node node, boolean z) {
        try {
            Transformer secureTransformer = SecureXmlTransformer.getSecureTransformer();
            secureTransformer.setOutputProperty("encoding", StandardCharsets.UTF_8.name());
            if (z) {
                secureTransformer.setOutputProperty(ElementTags.INDENT, XmlConsts.XML_SA_YES);
            }
            secureTransformer.transform(new DOMSource(node), new StreamResult(writer));
        } catch (Exception e) {
            LOGGER.severe("Error al escribir el cuerpo del XML: " + e);
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws IOException, AOException {
        return getData(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws IOException, AOInvalidFormatException {
        if (!isSign(bArr)) {
            throw new AOInvalidFormatException("El documento introducido no contiene una firma valida");
        }
        return bArr;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws IOException, AOException {
        return getSignInfo(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws IOException, AOException {
        if (bArr == null) {
            throw new IllegalArgumentException("No se han introducido datos para analizar");
        }
        if (!isSign(bArr)) {
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un objeto de firma");
        }
        return new AOSignInfo(AOSignConstants.SIGN_FORMAT_ODF);
    }

    private static String getODFMimeType(ZipInputStream zipInputStream) throws IOException {
        String str = null;
        if (getEntry(zipInputStream, ENTRY_MIMETYPE) != null) {
            byte[] dataFromInputStream = AOUtil.getDataFromInputStream(zipInputStream);
            str = dataFromInputStream != null ? new String(dataFromInputStream) : "";
        }
        return str;
    }

    private static ZipEntry getEntry(ZipInputStream zipInputStream, String str) throws IOException {
        ZipEntry zipEntry = null;
        boolean z = false;
        while (!z) {
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            zipEntry = nextEntry;
            if (nextEntry == null) {
                break;
            }
            if (zipEntry.getName().equals(str)) {
                z = true;
            }
        }
        if (z) {
            return zipEntry;
        }
        return null;
    }
}
