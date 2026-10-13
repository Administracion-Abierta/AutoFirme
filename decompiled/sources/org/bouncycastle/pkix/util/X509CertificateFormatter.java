package org.bouncycastle.pkix.util;

import java.io.FileReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.DefaultSignatureNameFinder;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/util/X509CertificateFormatter.class */
public class X509CertificateFormatter {
    private static Map<ASN1ObjectIdentifier, String> oidMap = new HashMap();
    private static Map<ASN1ObjectIdentifier, String> keyAlgMap = new HashMap();
    private static Map<KeyPurposeId, String> extUsageMap = new HashMap();
    private static Map<Integer, String> usageMap = new HashMap();
    private static final String spaceStr = "                                                              ";

    private static String oidToLabel(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String str = oidMap.get(aSN1ObjectIdentifier);
        return str != null ? str : aSN1ObjectIdentifier.getId();
    }

    private static String keyAlgToLabel(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String str = keyAlgMap.get(aSN1ObjectIdentifier);
        return str != null ? str : aSN1ObjectIdentifier.getId();
    }

    private static String spaces(int i) {
        return spaceStr.substring(0, i);
    }

    private static String indent(String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder();
        String strSubstring = str2.substring(0, str2.length() - str3.length());
        while (true) {
            int iIndexOf = strSubstring.indexOf(str3);
            if (iIndexOf <= 0) {
                break;
            }
            sb.append(strSubstring.substring(0, iIndexOf));
            sb.append(str3);
            sb.append(str);
            if (0 < strSubstring.length()) {
                strSubstring = strSubstring.substring(iIndexOf + str3.length());
            }
        }
        if (sb.length() == 0) {
            return strSubstring;
        }
        sb.append(strSubstring);
        return sb.toString();
    }

    static void prettyPrintData(byte[] bArr, StringBuilder sb, String str) {
        if (bArr.length <= 20) {
            sb.append(Hex.toHexString(bArr)).append(str);
        } else {
            sb.append(Hex.toHexString(bArr, 0, 20)).append(str);
            format(sb, bArr, str);
        }
    }

    static void format(StringBuilder sb, byte[] bArr, String str) {
        for (int i = 20; i < bArr.length; i += 20) {
            if (i < bArr.length - 20) {
                sb.append("                       ").append(Hex.toHexString(bArr, i, 20)).append(str);
            } else {
                sb.append("                       ").append(Hex.toHexString(bArr, i, bArr.length - i)).append(str);
            }
        }
    }

    public static String asString(X509CertificateHolder x509CertificateHolder) {
        StringBuilder sb = new StringBuilder();
        String strLineSeparator = Strings.lineSeparator();
        String strReplace = new DefaultSignatureNameFinder().getAlgorithmName(x509CertificateHolder.getSignatureAlgorithm()).replace("WITH", "with");
        String strKeyAlgToLabel = keyAlgToLabel(x509CertificateHolder.getSubjectPublicKeyInfo().getAlgorithm().getAlgorithm());
        sb.append("  [0]         Version: ").append(x509CertificateHolder.getVersionNumber()).append(strLineSeparator);
        sb.append("         SerialNumber: ").append(x509CertificateHolder.getSerialNumber()).append(strLineSeparator);
        sb.append("             IssuerDN: ").append(x509CertificateHolder.getIssuer()).append(strLineSeparator);
        sb.append("           Start Date: ").append(x509CertificateHolder.getNotBefore()).append(strLineSeparator);
        sb.append("           Final Date: ").append(x509CertificateHolder.getNotAfter()).append(strLineSeparator);
        sb.append("            SubjectDN: ").append(x509CertificateHolder.getSubject()).append(strLineSeparator);
        sb.append("           Public Key: ").append(strKeyAlgToLabel).append(strLineSeparator);
        sb.append("                       ");
        prettyPrintData(x509CertificateHolder.getSubjectPublicKeyInfo().getPublicKeyData().getOctets(), sb, strLineSeparator);
        Extensions extensions = x509CertificateHolder.getExtensions();
        if (extensions != null) {
            Enumeration enumerationOids = extensions.oids();
            if (enumerationOids.hasMoreElements()) {
                sb.append("           Extensions: ").append(strLineSeparator);
            }
            while (enumerationOids.hasMoreElements()) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) enumerationOids.nextElement();
                Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
                if (extension.getExtnValue() != null) {
                    ASN1InputStream aSN1InputStream = new ASN1InputStream(extension.getExtnValue().getOctets());
                    try {
                        String strOidToLabel = oidToLabel(aSN1ObjectIdentifier);
                        sb.append("                       ").append(strOidToLabel);
                        sb.append(": critical(").append(extension.isCritical()).append(") ").append(strLineSeparator);
                        String str = "                       " + spaces(2 + strOidToLabel.length());
                        if (aSN1ObjectIdentifier.equals((ASN1Primitive) Extension.basicConstraints)) {
                            BasicConstraints basicConstraints = BasicConstraints.getInstance(aSN1InputStream.readObject());
                            sb.append(str).append("isCA : " + basicConstraints.isCA()).append(strLineSeparator);
                            if (basicConstraints.isCA()) {
                                sb.append(spaces(2 + strOidToLabel.length()));
                                sb.append("pathLenConstraint : " + basicConstraints.getPathLenConstraint()).append(strLineSeparator);
                            }
                        } else if (aSN1ObjectIdentifier.equals((ASN1Primitive) Extension.keyUsage)) {
                            KeyUsage keyUsage = KeyUsage.getInstance(aSN1InputStream.readObject());
                            sb.append(str);
                            boolean z = true;
                            Iterator<Integer> it = usageMap.keySet().iterator();
                            while (it.hasNext()) {
                                int iIntValue = it.next().intValue();
                                if (keyUsage.hasUsages(iIntValue)) {
                                    if (z) {
                                        z = false;
                                    } else {
                                        sb.append(", ");
                                    }
                                    sb.append(usageMap.get(Integer.valueOf(iIntValue)));
                                }
                            }
                            sb.append(strLineSeparator);
                        } else if (aSN1ObjectIdentifier.equals((ASN1Primitive) Extension.extendedKeyUsage)) {
                            ExtendedKeyUsage extendedKeyUsage = ExtendedKeyUsage.getInstance(aSN1InputStream.readObject());
                            sb.append(str);
                            boolean z2 = true;
                            for (KeyPurposeId keyPurposeId : extUsageMap.keySet()) {
                                if (extendedKeyUsage.hasKeyPurposeId(keyPurposeId)) {
                                    if (z2) {
                                        z2 = false;
                                    } else {
                                        sb.append(", ");
                                    }
                                    sb.append(extUsageMap.get(keyPurposeId));
                                }
                            }
                            sb.append(strLineSeparator);
                        } else {
                            sb.append(str).append("value = ").append(indent(str + spaces(8), ASN1Dump.dumpAsString(aSN1InputStream.readObject()), strLineSeparator)).append(strLineSeparator);
                        }
                    } catch (Exception e) {
                        sb.append(aSN1ObjectIdentifier.getId());
                        sb.append(" value = ").append("*****").append(strLineSeparator);
                    }
                } else {
                    sb.append(strLineSeparator);
                }
            }
        }
        sb.append("  Signature Algorithm: ").append(strReplace).append(strLineSeparator);
        sb.append("            Signature: ");
        prettyPrintData(x509CertificateHolder.getSignature(), sb, strLineSeparator);
        return sb.toString();
    }

    public static void main(String[] strArr) throws Exception {
        System.out.println(asString((X509CertificateHolder) new PEMParser(new FileReader(strArr[0])).readObject()));
    }

    static {
        oidMap.put(Extension.subjectDirectoryAttributes, "subjectDirectoryAttributes");
        oidMap.put(Extension.subjectKeyIdentifier, "subjectKeyIdentifier");
        oidMap.put(Extension.keyUsage, "keyUsage");
        oidMap.put(Extension.privateKeyUsagePeriod, "privateKeyUsagePeriod");
        oidMap.put(Extension.subjectAlternativeName, "subjectAlternativeName");
        oidMap.put(Extension.issuerAlternativeName, "issuerAlternativeName");
        oidMap.put(Extension.basicConstraints, "basicConstraints");
        oidMap.put(Extension.cRLNumber, "cRLNumber");
        oidMap.put(Extension.reasonCode, "reasonCode");
        oidMap.put(Extension.instructionCode, "instructionCode");
        oidMap.put(Extension.invalidityDate, "invalidityDate");
        oidMap.put(Extension.deltaCRLIndicator, "deltaCRLIndicator");
        oidMap.put(Extension.issuingDistributionPoint, "issuingDistributionPoint");
        oidMap.put(Extension.certificateIssuer, "certificateIssuer");
        oidMap.put(Extension.nameConstraints, "nameConstraints");
        oidMap.put(Extension.cRLDistributionPoints, "cRLDistributionPoints");
        oidMap.put(Extension.certificatePolicies, "certificatePolicies");
        oidMap.put(Extension.policyMappings, "policyMappings");
        oidMap.put(Extension.authorityKeyIdentifier, "authorityKeyIdentifier");
        oidMap.put(Extension.policyConstraints, "policyConstraints");
        oidMap.put(Extension.extendedKeyUsage, "extendedKeyUsage");
        oidMap.put(Extension.freshestCRL, "freshestCRL");
        oidMap.put(Extension.inhibitAnyPolicy, "inhibitAnyPolicy");
        oidMap.put(Extension.authorityInfoAccess, "authorityInfoAccess");
        oidMap.put(Extension.subjectInfoAccess, "subjectInfoAccess");
        oidMap.put(Extension.logoType, "logoType");
        oidMap.put(Extension.biometricInfo, "biometricInfo");
        oidMap.put(Extension.qCStatements, "qCStatements");
        oidMap.put(Extension.auditIdentity, "auditIdentity");
        oidMap.put(Extension.noRevAvail, "noRevAvail");
        oidMap.put(Extension.targetInformation, "targetInformation");
        oidMap.put(Extension.expiredCertsOnCRL, "expiredCertsOnCRL");
        usageMap.put(128, "digitalSignature");
        usageMap.put(64, "nonRepudiation");
        usageMap.put(32, "keyEncipherment");
        usageMap.put(16, "dataEncipherment");
        usageMap.put(8, "keyAgreement");
        usageMap.put(4, "keyCertSign");
        usageMap.put(2, "cRLSign");
        usageMap.put(1, "encipherOnly");
        usageMap.put(32768, "decipherOnly");
        extUsageMap.put(KeyPurposeId.anyExtendedKeyUsage, "anyExtendedKeyUsage");
        extUsageMap.put(KeyPurposeId.id_kp_serverAuth, "id_kp_serverAuth");
        extUsageMap.put(KeyPurposeId.id_kp_clientAuth, "id_kp_clientAuth");
        extUsageMap.put(KeyPurposeId.id_kp_codeSigning, "id_kp_codeSigning");
        extUsageMap.put(KeyPurposeId.id_kp_emailProtection, "id_kp_emailProtection");
        extUsageMap.put(KeyPurposeId.id_kp_ipsecEndSystem, "id_kp_ipsecEndSystem");
        extUsageMap.put(KeyPurposeId.id_kp_ipsecTunnel, "id_kp_ipsecTunnel");
        extUsageMap.put(KeyPurposeId.id_kp_ipsecUser, "id_kp_ipsecUser");
        extUsageMap.put(KeyPurposeId.id_kp_timeStamping, "id_kp_timeStamping");
        extUsageMap.put(KeyPurposeId.id_kp_OCSPSigning, "id_kp_OCSPSigning");
        extUsageMap.put(KeyPurposeId.id_kp_dvcs, "id_kp_dvcs");
        extUsageMap.put(KeyPurposeId.id_kp_sbgpCertAAServerAuth, "id_kp_sbgpCertAAServerAuth");
        extUsageMap.put(KeyPurposeId.id_kp_scvp_responder, "id_kp_scvp_responder");
        extUsageMap.put(KeyPurposeId.id_kp_eapOverPPP, "id_kp_eapOverPPP");
        extUsageMap.put(KeyPurposeId.id_kp_eapOverLAN, "id_kp_eapOverLAN");
        extUsageMap.put(KeyPurposeId.id_kp_scvpServer, "id_kp_scvpServer");
        extUsageMap.put(KeyPurposeId.id_kp_scvpClient, "id_kp_scvpClient");
        extUsageMap.put(KeyPurposeId.id_kp_ipsecIKE, "id_kp_ipsecIKE");
        extUsageMap.put(KeyPurposeId.id_kp_capwapAC, "id_kp_capwapAC");
        extUsageMap.put(KeyPurposeId.id_kp_capwapWTP, "id_kp_capwapWTP");
        extUsageMap.put(KeyPurposeId.id_kp_cmcCA, "id_kp_cmcCA");
        extUsageMap.put(KeyPurposeId.id_kp_cmcRA, "id_kp_cmcRA");
        extUsageMap.put(KeyPurposeId.id_kp_cmKGA, "id_kp_cmKGA");
        extUsageMap.put(KeyPurposeId.id_kp_smartcardlogon, "id_kp_smartcardlogon");
        extUsageMap.put(KeyPurposeId.id_kp_macAddress, "id_kp_macAddress");
        extUsageMap.put(KeyPurposeId.id_kp_msSGC, "id_kp_msSGC");
        extUsageMap.put(KeyPurposeId.id_kp_nsSGC, "id_kp_nsSGC");
        keyAlgMap.put(PKCSObjectIdentifiers.rsaEncryption, "rsaEncryption");
        keyAlgMap.put(X9ObjectIdentifiers.id_ecPublicKey, "id_ecPublicKey");
        keyAlgMap.put(EdECObjectIdentifiers.id_Ed25519, "id_Ed25519");
        keyAlgMap.put(EdECObjectIdentifiers.id_Ed448, "id_Ed448");
    }
}
