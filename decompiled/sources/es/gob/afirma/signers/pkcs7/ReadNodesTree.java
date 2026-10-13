package es.gob.afirma.signers.pkcs7;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSimpleSignInfo;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.ASN1UTCTime;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.cms.SignedData;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pkcs7/ReadNodesTree.class */
public final class ReadNodesTree {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private AOTreeNode rama;
    private AOTreeNode rama2;
    private int[] seleccionados;
    private String stringRetorn = "";
    private final List<String> lista = new ArrayList();
    private final List<X509Certificate[]> listaCert = new ArrayList();

    int[] getSeleccionados() {
        if (this.seleccionados != null) {
            return (int[]) this.seleccionados.clone();
        }
        return null;
    }

    void setSeleccionados(int[] iArr) {
        this.seleccionados = (int[]) iArr.clone();
    }

    String getStringRetorn() {
        return this.stringRetorn;
    }

    void setStringRetorn(String str) {
        this.stringRetorn = str;
    }

    public AOTreeModel readNodesTree(byte[] bArr, boolean z) throws IOException {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(bArr);
        Throwable th = null;
        try {
            try {
                ASN1Sequence aSN1Sequence = (ASN1Sequence) aSN1InputStream.readObject();
                if (aSN1InputStream != null) {
                    if (0 != 0) {
                        try {
                            aSN1InputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        aSN1InputStream.close();
                    }
                }
                Enumeration objects = aSN1Sequence.getObjects();
                objects.nextElement();
                ASN1Set signerInfos = null;
                ASN1Set certificates = null;
                try {
                    SignedData signedData = SignedData.getInstance((ASN1Sequence) ((ASN1TaggedObject) objects.nextElement()).getObject());
                    signerInfos = signedData.getSignerInfos();
                    certificates = signedData.getCertificates();
                } catch (Exception e) {
                    LOGGER.severe("Error obteniendo los SignerInfos del SignedData: " + e);
                }
                AOTreeNode aOTreeNode = new AOTreeNode("Datos");
                if (z && signerInfos != null) {
                    for (int i = 0; i < signerInfos.size(); i++) {
                        ASN1Sequence aSN1Sequence2 = (ASN1Sequence) signerInfos.getObjectAt(i);
                        X509Certificate[] x509CertificateArrSearchCert = searchCert(certificates, IssuerAndSerialNumber.getInstance(aSN1Sequence2.getObjectAt(1)).getSerialNumber());
                        SignerInfo signerInfo = SignerInfo.getInstance(aSN1Sequence2);
                        AOSimpleSignInfo aOSimpleSignInfo = new AOSimpleSignInfo(x509CertificateArrSearchCert, getSigningTime(signerInfo));
                        aOSimpleSignInfo.setPkcs1(signerInfo.getEncryptedDigest().getOctets());
                        aOSimpleSignInfo.setSignAlgorithm(getSignatureAlgorithm(signerInfo));
                        this.rama = new AOTreeNode(aOSimpleSignInfo);
                        this.listaCert.add(x509CertificateArrSearchCert);
                        getUnsignedAtributes(true, signerInfo.getUnauthenticatedAttributes(), this.rama, certificates);
                        aOTreeNode.add(this.rama);
                    }
                } else if (signerInfos != null) {
                    for (int i2 = 0; i2 < signerInfos.size(); i2++) {
                        ASN1Sequence aSN1Sequence3 = (ASN1Sequence) signerInfos.getObjectAt(i2);
                        String strSearchName = searchName(certificates, IssuerAndSerialNumber.getInstance(aSN1Sequence3.getObjectAt(1)).getSerialNumber());
                        SignerInfo signerInfo2 = SignerInfo.getInstance(aSN1Sequence3);
                        this.rama = new AOTreeNode(strSearchName);
                        this.lista.add(strSearchName);
                        getUnsignedAtributes(false, signerInfo2.getUnauthenticatedAttributes(), this.rama, certificates);
                        aOTreeNode.add(this.rama);
                    }
                }
                return new AOTreeModel(aOTreeNode);
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (aSN1InputStream != null) {
                if (th != null) {
                    try {
                        aSN1InputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    aSN1InputStream.close();
                }
            }
            throw th4;
        }
    }

    private static String getSignatureAlgorithm(SignerInfo signerInfo) {
        return translateDigestOid(signerInfo.getDigestAlgorithm().getAlgorithm()) + "with" + translateEncryptionOid(signerInfo.getDigestEncryptionAlgorithm().getAlgorithm());
    }

    private static String translateDigestOid(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = aSN1ObjectIdentifier.toString();
        if (string.equals("2.16.840.1.101.3.4.2.3")) {
            return "SHA512";
        }
        if (string.equals(AOAlgorithmID.OID_SHA256)) {
            return "SHA256";
        }
        if (string.equals("2.16.840.1.101.3.4.2.2")) {
            return AOSignConstants.DIGEST_ALGORITHM_SHA384;
        }
        if (string.equals("1.3.14.3.2.26")) {
            return AOSignConstants.DIGEST_ALGORITHM_SHA1;
        }
        return string;
    }

    private static String translateEncryptionOid(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = aSN1ObjectIdentifier.toString();
        if (AOAlgorithmID.isRSAOID(string)) {
            return "RSA";
        }
        if (AOAlgorithmID.isDSAOID(string)) {
            return "DSA";
        }
        if (AOAlgorithmID.isECDSAOID(string)) {
            return "ECDSA";
        }
        return string;
    }

    private void getUnsignedAtributes(boolean z, ASN1Set aSN1Set, AOTreeNode aOTreeNode, ASN1Set aSN1Set2) {
        if (aSN1Set != null) {
            Enumeration objects = aSN1Set.getObjects();
            while (objects.hasMoreElements()) {
                Attribute attribute = Attribute.getInstance(objects.nextElement());
                if (isValideAttributeType(attribute.getAttrType())) {
                    Enumeration objects2 = attribute.getAttrValues().getObjects();
                    while (objects2.hasMoreElements()) {
                        Object objNextElement = objects2.nextElement();
                        if (objNextElement instanceof ASN1Sequence) {
                            ASN1Sequence aSN1Sequence = (ASN1Sequence) objNextElement;
                            IssuerAndSerialNumber issuerAndSerialNumber = IssuerAndSerialNumber.getInstance(aSN1Sequence.getObjectAt(1));
                            SignerInfo signerInfo = SignerInfo.getInstance(aSN1Sequence);
                            if (z) {
                                X509Certificate[] x509CertificateArrSearchCert = searchCert(aSN1Set2, issuerAndSerialNumber.getSerialNumber());
                                AOSimpleSignInfo aOSimpleSignInfo = new AOSimpleSignInfo(x509CertificateArrSearchCert, getSigningTime(signerInfo));
                                aOSimpleSignInfo.setPkcs1(signerInfo.getEncryptedDigest().getOctets());
                                aOSimpleSignInfo.setSignAlgorithm(getSignatureAlgorithm(signerInfo));
                                this.rama2 = new AOTreeNode(aOSimpleSignInfo);
                                this.listaCert.add(x509CertificateArrSearchCert);
                                aOTreeNode.add(this.rama2);
                                getUnsignedAtributes(true, signerInfo.getUnauthenticatedAttributes(), this.rama2, aSN1Set2);
                            } else {
                                String strSearchName = searchName(aSN1Set2, issuerAndSerialNumber.getSerialNumber());
                                this.rama2 = new AOTreeNode(strSearchName);
                                this.lista.add(strSearchName);
                                aOTreeNode.add(this.rama2);
                                getUnsignedAtributes(false, signerInfo.getUnauthenticatedAttributes(), this.rama2, aSN1Set2);
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isValideAttributeType(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_aa_ets_archiveTimestamp) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_aa_ets_escTimeStamp) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_aa_ets_revocationRefs) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_aa_ets_revocationValues) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_aa_ets_certificateRefs) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_aa_ets_certValues) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_aa.branch("48")) || aSN1ObjectIdentifier.equals(new ASN1ObjectIdentifier("0.4.0.1733.2.4")) || aSN1ObjectIdentifier.equals(new ASN1ObjectIdentifier("0.4.0.1733.2.2"))) ? false : true;
    }

    public int[] readNodesFromSigners(String[] strArr, byte[] bArr) throws IOException {
        readNodesTree(bArr, false);
        List<String> list = this.lista;
        int[] iArr = new int[list.size()];
        int i = 0;
        for (int i2 = 0; i2 < list.size(); i2++) {
            for (String str : strArr) {
                if (list.get(i2).equals(str)) {
                    iArr[i] = i2;
                    i++;
                }
            }
        }
        int[] iArr2 = new int[i];
        System.arraycopy(iArr, 0, iArr2, 0, i);
        int[] iArrSimplyArray = simplyArray(iArr2);
        Arrays.sort(iArrSimplyArray);
        return iArrSimplyArray;
    }

    public static int[] simplyArray(int[] iArr) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < iArr.length; i++) {
            if (!arrayList.contains(Integer.valueOf(iArr[i]))) {
                arrayList.add(Integer.valueOf(iArr[i]));
            }
        }
        int[] iArr2 = new int[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            iArr2[i2] = ((Integer) arrayList.get(i2)).intValue();
        }
        return iArr2;
    }

    private static String searchName(ASN1Set aSN1Set, ASN1Integer aSN1Integer) {
        Enumeration objects = aSN1Set.getObjects();
        while (objects.hasMoreElements()) {
            try {
                X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(((ASN1Sequence) objects.nextElement()).getEncoded()));
                if (x509Certificate.getSerialNumber().equals(aSN1Integer.getValue())) {
                    return AOUtil.getCN(x509Certificate);
                }
            } catch (Exception e) {
                LOGGER.severe("Error extrayendo los certificados del Set ASN.1, puede que se haya omitido un elemento valido" + e);
            }
        }
        LOGGER.info("No se ha encontrado el certificado indicado, se devolvera una cadena vacia");
        return "";
    }

    private static X509Certificate[] searchCert(ASN1Set aSN1Set, ASN1Integer aSN1Integer) {
        if (aSN1Set != null) {
            Enumeration objects = aSN1Set.getObjects();
            while (objects.hasMoreElements()) {
                try {
                    X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(((ASN1Sequence) objects.nextElement()).getEncoded()));
                    if (x509Certificate.getSerialNumber().equals(aSN1Integer.getValue())) {
                        return new X509Certificate[]{x509Certificate};
                    }
                } catch (Exception e) {
                    LOGGER.severe("Error extrayendo los certificados del Set ASN.1, puede que se haya omitido un elemento valido" + e);
                }
            }
        }
        LOGGER.severe("El certificados pedido no estaba en la lista, se devolvera un array vacio");
        return new X509Certificate[0];
    }

    private static Date getSigningTime(SignerInfo signerInfo) {
        Date date = null;
        if (signerInfo.getAuthenticatedAttributes() != null) {
            Enumeration objects = signerInfo.getAuthenticatedAttributes().getObjects();
            while (objects.hasMoreElements()) {
                Attribute attribute = Attribute.getInstance(objects.nextElement());
                if (attribute.getAttrType().equals(CMSAttributes.signingTime)) {
                    ASN1Encodable objectAt = attribute.getAttrValues().getObjectAt(0);
                    if (objectAt == null) {
                        LOGGER.severe("El objeto no contiene una fecha");
                    } else if (objectAt instanceof ASN1GeneralizedTime) {
                        try {
                            date = ((ASN1GeneralizedTime) objectAt).getDate();
                        } catch (ParseException e) {
                            LOGGER.severe("No es posible convertir la fecha: " + e);
                        }
                    } else if (objectAt instanceof ASN1UTCTime) {
                        try {
                            date = ((ASN1UTCTime) objectAt).getDate();
                        } catch (ParseException e2) {
                            LOGGER.severe("No es posible convertir la fecha: " + e2);
                        }
                    } else {
                        LOGGER.severe("Formato de fecha deconocido: " + objectAt.getClass().getName());
                    }
                }
            }
        }
        return date;
    }
}
