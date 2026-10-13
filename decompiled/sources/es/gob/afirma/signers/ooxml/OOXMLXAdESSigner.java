package es.gob.afirma.signers.ooxml;

import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import es.gob.afirma.signers.xades.XAdESUtil;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signers.xml.XMLConstants;
import es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIndication;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlace;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlaceImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XAdES;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XAdES_EPES;
import es.uji.crypto.xades.jxades.util.DOMOutputImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/OOXMLXAdESSigner.class */
final class OOXMLXAdESSigner {
    private static final String ID_PACKAGE_OBJECT = "idPackageObject";
    private static final String ID_OFFICE_OBJECT = "idOfficeObject";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String XADESNS = "http://uri.etsi.org/01903/v1.3.2#";
    private static final String XADES_SIGNATURE_PREFIX = "xd";
    private static final String XML_SIGNATURE_PREFIX = "ds";

    private OOXMLXAdESSigner() {
    }

    static byte[] getSignedXML(byte[] bArr, String str, PrivateKey privateKey, X509Certificate[] x509CertificateArr, Properties properties) throws Exception {
        if (XMLConstants.SIGN_ALGOS_URI.get(str) == null) {
            throw new IllegalArgumentException("Los formatos de firma XML no soportan el algoritmo de firma '" + str + OperatorName.SHOW_TEXT_LINE);
        }
        Properties properties2 = properties != null ? properties : new Properties();
        Document documentNewDocument = SecureXmlBuilder.getSecureDocumentBuilder().newDocument();
        XAdES_EPES xAdES_EPES = (XAdES_EPES) XAdES.newInstance(XAdES.EPES, "http://uri.etsi.org/01903/v1.3.2#", XADES_SIGNATURE_PREFIX, XML_SIGNATURE_PREFIX, "http://www.w3.org/2001/04/xmlenc#sha512", documentNewDocument, documentNewDocument.getDocumentElement());
        xAdES_EPES.setSigningCertificate(x509CertificateArr[0]);
        SignatureProductionPlace signatureProductionPlace = getSignatureProductionPlace(properties2.getProperty("signatureProductionCity"), properties2.getProperty(XAdESExtraParams.SIGNATURE_PRODUCTION_PROVINCE), properties2.getProperty("signatureProductionPostalCode"), properties2.getProperty("signatureProductionCountry"));
        if (signatureProductionPlace != null) {
            xAdES_EPES.setSignatureProductionPlace(signatureProductionPlace);
        }
        List<CommitmentTypeIndication> commitmentTypeIndications = XAdESUtil.parseCommitmentTypeIndications(properties2, null);
        if (commitmentTypeIndications != null && commitmentTypeIndications.size() > 0) {
            xAdES_EPES.setCommitmentTypeIndications(commitmentTypeIndications);
        }
        String property = properties2.getProperty("signerClaimedRoles");
        if (property != null) {
            SignerRoleImpl signerRoleImpl = new SignerRoleImpl();
            signerRoleImpl.addClaimedRole(property);
            xAdES_EPES.setSignerRole(signerRoleImpl);
        }
        xAdES_EPES.setSigningTime(new Date());
        OOXMLAdvancedSignature oOXMLAdvancedSignatureNewInstance = OOXMLAdvancedSignature.newInstance(xAdES_EPES, bArr);
        ArrayList arrayList = new ArrayList();
        String str2 = "xmldsig-" + UUID.randomUUID().toString();
        XMLSignatureFactory dOMFactory = Utils.getDOMFactory();
        DigestMethod digestMethodNewDigestMethod = dOMFactory.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha512", (DigestMethodParameterSpec) null);
        oOXMLAdvancedSignatureNewInstance.addXMLObject(OOXMLPackageObjectHelper.getPackageObject(ID_PACKAGE_OBJECT, dOMFactory, bArr, documentNewDocument, str2));
        arrayList.add(dOMFactory.newReference("#idPackageObject", digestMethodNewDigestMethod, (List) null, "http://www.w3.org/2000/09/xmldsig#Object", (String) null));
        oOXMLAdvancedSignatureNewInstance.addXMLObject(OOXMLOfficeObjectHelper.getOfficeObject(ID_OFFICE_OBJECT, dOMFactory, documentNewDocument, str2, properties2.getProperty("signatureComments"), properties2.getProperty("signatureAddress1"), properties2.getProperty("signatureAddress2"), (commitmentTypeIndications == null || commitmentTypeIndications.size() <= 0) ? null : "1"));
        arrayList.add(dOMFactory.newReference("#idOfficeObject", digestMethodNewDigestMethod, (List) null, "http://www.w3.org/2000/09/xmldsig#Object", (String) null));
        oOXMLAdvancedSignatureNewInstance.sign(x509CertificateArr, privateKey, XMLConstants.SIGN_ALGOS_URI.get(str), arrayList, str2);
        return writeXML(documentNewDocument.getDocumentElement());
    }

    private static byte[] writeXML(Node node) {
        OutputStreamWriter outputStreamWriter;
        String strName = StandardCharsets.UTF_8.name();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, strName);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warning("La codificacion '" + strName + "' no es valida, se usara la por defecto: " + e);
            outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
        }
        writeXMLwithXALAN(outputStreamWriter, node, strName);
        try {
            SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            try {
                return new String(byteArrayOutputStream.toByteArray(), strName).getBytes(strName);
            } catch (Exception e2) {
                LOGGER.warning("La codificacion '" + strName + "' no es valida, se usara la por defecto del sistema: " + e2);
                return new String(byteArrayOutputStream.toByteArray()).getBytes();
            }
        } catch (Exception e3) {
            LOGGER.severe("No se ha podido recargar el XML para insertar los atributos de la cabecera, quizas la codificacion se vea afectada: " + e3);
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static void writeXMLwithXALAN(Writer writer, Node node, String str) {
        LSSerializer lSSerializerCreateLSSerializer = ((DOMImplementationLS) node.getOwnerDocument().getImplementation()).createLSSerializer();
        lSSerializerCreateLSSerializer.getDomConfig().setParameter("namespaces", Boolean.FALSE);
        DOMOutputImpl dOMOutputImpl = new DOMOutputImpl();
        dOMOutputImpl.setCharacterStream(writer);
        if (str != null) {
            dOMOutputImpl.setEncoding(str);
        }
        lSSerializerCreateLSSerializer.write(node, dOMOutputImpl);
    }

    private static SignatureProductionPlace getSignatureProductionPlace(String str, String str2, String str3, String str4) {
        if (str == null && str2 == null && str3 == null && str4 == null) {
            return null;
        }
        return new SignatureProductionPlaceImpl(str, str2, str3, str4);
    }
}
