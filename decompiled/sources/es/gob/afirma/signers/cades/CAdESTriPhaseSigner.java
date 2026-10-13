package es.gob.afirma.signers.cades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
import es.gob.afirma.signers.pkcs7.SigUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BEROctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.cms.SignedData;
import org.spongycastle.asn1.cms.SignerIdentifier;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.TBSCertificate;
import org.spongycastle.cms.CMSProcessableByteArray;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/CAdESTriPhaseSigner.class */
public final class CAdESTriPhaseSigner {
    private CAdESTriPhaseSigner() {
    }

    public static byte[] preSign(Certificate[] certificateArr, Date date, CAdESParameters cAdESParameters) throws AOException {
        if (certificateArr == null || certificateArr.length == 0) {
            throw new IllegalArgumentException("La cadena de certificados debe contener al menos una entrada");
        }
        try {
            try {
                return SigUtils.getAttributeSet(new AttributeTable(CAdESUtils.generateSignedAttributes(certificateArr[0], cAdESParameters, false))).getEncoded("DER");
            } catch (Exception e) {
                throw new AOException("Error al codificar los datos ASN.1 a firmar finalmente", e);
            }
        } catch (Exception e2) {
            throw new AOException("Error obteniendo los atributos a firmar: " + e2, e2);
        }
    }

    public static byte[] postSign(String str, byte[] bArr, Certificate[] certificateArr, byte[] bArr2, byte[] bArr3) throws AOException {
        String oid;
        ContentInfo contentInfo;
        if (certificateArr == null || certificateArr.length == 0) {
            throw new IllegalArgumentException("La cadena de certificados debe contener al menos una entrada");
        }
        String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(str);
        try {
            TBSCertificate tBSCertificate = TBSCertificate.getInstance(ASN1Primitive.fromByteArray(((X509Certificate) certificateArr[0]).getTBSCertificate()));
            SignerIdentifier signerIdentifier = new SignerIdentifier(new IssuerAndSerialNumber(X500Name.getInstance(tBSCertificate.getIssuer()), tBSCertificate.getSerialNumber().getValue()));
            try {
                AlgorithmIdentifier algorithmIdentifierMakeAlgId = SigUtils.makeAlgId(AOAlgorithmID.getOID(digestAlgorithmName));
                try {
                    if (str.contains("withRSA")) {
                        oid = AOAlgorithmID.getOID("RSA");
                    } else {
                        oid = AOAlgorithmID.getOID(str);
                    }
                    AlgorithmIdentifier algorithmIdentifierMakeAlgId2 = SigUtils.makeAlgId(oid);
                    DEROctetString dEROctetString = new DEROctetString(bArr2);
                    try {
                        ASN1Set aSN1Set = (ASN1Set) ASN1Primitive.fromByteArray(bArr3);
                        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                        aSN1EncodableVector.add(new SignerInfo(signerIdentifier, algorithmIdentifierMakeAlgId, aSN1Set, algorithmIdentifierMakeAlgId2, dEROctetString, (ASN1Set) null));
                        if (bArr != null) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            try {
                                new CMSProcessableByteArray(bArr).write(byteArrayOutputStream);
                                contentInfo = new ContentInfo(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.data.getId()), new BEROctetString(byteArrayOutputStream.toByteArray()));
                            } catch (Exception e) {
                                throw new AOException("Error en la escritura del contenido implicito en el ContentInfo", e);
                            }
                        } else {
                            contentInfo = new ContentInfo(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.data.getId()), null);
                        }
                        ArrayList arrayList = new ArrayList();
                        for (Certificate certificate : certificateArr) {
                            try {
                                arrayList.add(org.spongycastle.asn1.x509.Certificate.getInstance(ASN1Primitive.fromByteArray(certificate.getEncoded())));
                            } catch (Exception e2) {
                                Logger.getLogger("es.gob.afirma").severe("Error insertando el certificado '" + AOUtil.getCN((X509Certificate) certificate) + "' en la cadena de confianza: " + e2);
                            }
                        }
                        ASN1Set aSN1SetCreateBerSetFromList = SigUtils.createBerSetFromList(arrayList);
                        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
                        aSN1EncodableVector2.add(algorithmIdentifierMakeAlgId);
                        try {
                            return new ContentInfo(PKCSObjectIdentifiers.signedData, new SignedData(new DERSet(aSN1EncodableVector2), contentInfo, aSN1SetCreateBerSetFromList, null, new DERSet(aSN1EncodableVector))).getEncoded("DER");
                        } catch (IOException e3) {
                            throw new AOException("Error creando el ContentInfo de CAdES: " + e3, e3);
                        }
                    } catch (IOException e4) {
                        throw new AOException("Error en la inclusion de la recuperacion de los SignedAttibutes", e4);
                    }
                } catch (Exception e5) {
                    throw new AOException("Error al codificar el algoritmo de cifrado: " + e5, e5);
                }
            } catch (Exception e6) {
                throw new AOException("Error obteniendo el OID en ASN.1 del algoritmo de huella digital: " + e6, e6);
            }
        } catch (Exception e7) {
            throw new AOException("No se ha podido crear la estructura de certificados", e7);
        }
    }
}
