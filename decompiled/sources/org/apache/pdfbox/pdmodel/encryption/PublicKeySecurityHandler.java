package org.apache.pdfbox.pdmodel.encryption;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/encryption/PublicKeySecurityHandler.class */
public final class PublicKeySecurityHandler extends SecurityHandler {
    public static final String FILTER = "Adobe.PubSec";
    private static final String SUBFILTER4 = "adbe.pkcs7.s4";
    private static final String SUBFILTER5 = "adbe.pkcs7.s5";

    public PublicKeySecurityHandler() {
    }

    public PublicKeySecurityHandler(PublicKeyProtectionPolicy publicKeyProtectionPolicy) {
        setProtectionPolicy(publicKeyProtectionPolicy);
        setKeyLength(publicKeyProtectionPolicy.getEncryptionKeyLength());
    }

    @Override // org.apache.pdfbox.pdmodel.encryption.SecurityHandler
    public void prepareForDecryption(PDEncryption encryption, COSArray documentIDArray, DecryptionMaterial decryptionMaterial) throws IOException {
        byte[] mdResult;
        if (!(decryptionMaterial instanceof PublicKeyDecryptionMaterial)) {
            throw new IOException("Provided decryption material is not compatible with the document - did you pass a null keyStore?");
        }
        PDCryptFilterDictionary defaultCryptFilterDictionary = encryption.getDefaultCryptFilterDictionary();
        if (defaultCryptFilterDictionary != null && defaultCryptFilterDictionary.getLength() != 0) {
            setKeyLength(defaultCryptFilterDictionary.getLength());
            setDecryptMetadata(defaultCryptFilterDictionary.isEncryptMetaData());
        } else if (encryption.getLength() != 0) {
            setKeyLength(encryption.getLength());
            setDecryptMetadata(encryption.isEncryptMetaData());
        }
        PublicKeyDecryptionMaterial material = (PublicKeyDecryptionMaterial) decryptionMaterial;
        try {
            boolean foundRecipient = false;
            X509Certificate certificate = material.getCertificate();
            X509CertificateHolder materialCert = null;
            if (certificate != null) {
                materialCert = new X509CertificateHolder(certificate.getEncoded());
            }
            byte[] envelopedData = null;
            COSArray array = encryption.getCOSObject().getCOSArray(COSName.RECIPIENTS);
            if (array == null && defaultCryptFilterDictionary != null) {
                array = defaultCryptFilterDictionary.getCOSObject().getCOSArray(COSName.RECIPIENTS);
            }
            if (array == null) {
                throw new IOException("/Recipients entry is missing in encryption dictionary");
            }
            byte[][] recipientFieldsBytes = new byte[array.size()][];
            int recipientFieldsLength = 0;
            StringBuilder extraInfo = new StringBuilder();
            for (int i = 0; i < array.size(); i++) {
                COSString recipientFieldString = (COSString) array.getObject(i);
                byte[] recipientBytes = recipientFieldString.getBytes();
                CMSEnvelopedData data = new CMSEnvelopedData(recipientBytes);
                Collection<RecipientInformation> recipCertificatesIt = data.getRecipientInfos().getRecipients();
                int j = 0;
                for (RecipientInformation ri : recipCertificatesIt) {
                    RecipientId rid = ri.getRID();
                    if (!foundRecipient && rid.match(materialCert)) {
                        foundRecipient = true;
                        PrivateKey privateKey = (PrivateKey) material.getPrivateKey();
                        envelopedData = ri.getContent(new JceKeyTransEnvelopedRecipient(privateKey));
                        break;
                    } else {
                        j++;
                        if (certificate != null) {
                            extraInfo.append('\n');
                            extraInfo.append(j);
                            extraInfo.append(": ");
                            if (rid instanceof KeyTransRecipientId) {
                                appendCertInfo(extraInfo, (KeyTransRecipientId) rid, certificate, materialCert);
                            }
                        }
                    }
                }
                recipientFieldsBytes[i] = recipientBytes;
                recipientFieldsLength += recipientBytes.length;
            }
            if (!foundRecipient || envelopedData == null) {
                throw new IOException("The certificate matches none of " + array.size() + " recipient entries" + extraInfo.toString());
            }
            if (envelopedData.length != 24) {
                throw new IOException("The enveloped data does not contain 24 bytes");
            }
            byte[] accessBytes = new byte[4];
            System.arraycopy(envelopedData, 20, accessBytes, 0, 4);
            AccessPermission currentAccessPermission = new AccessPermission(accessBytes);
            currentAccessPermission.setReadOnly();
            setCurrentAccessPermission(currentAccessPermission);
            byte[] sha1Input = new byte[recipientFieldsLength + 20];
            System.arraycopy(envelopedData, 0, sha1Input, 0, 20);
            int sha1InputOffset = 20;
            for (byte[] recipientFieldsByte : recipientFieldsBytes) {
                System.arraycopy(recipientFieldsByte, 0, sha1Input, sha1InputOffset, recipientFieldsByte.length);
                sha1InputOffset += recipientFieldsByte.length;
            }
            if (encryption.getVersion() == 4 || encryption.getVersion() == 5) {
                if (!isDecryptMetadata()) {
                    sha1Input = Arrays.copyOf(sha1Input, sha1Input.length + 4);
                    System.arraycopy(new byte[]{-1, -1, -1, -1}, 0, sha1Input, sha1Input.length - 4, 4);
                }
                if (encryption.getVersion() == 4) {
                    mdResult = MessageDigests.getSHA1().digest(sha1Input);
                } else {
                    mdResult = MessageDigests.getSHA256().digest(sha1Input);
                }
                if (defaultCryptFilterDictionary != null) {
                    COSName cryptFilterMethod = defaultCryptFilterDictionary.getCryptFilterMethod();
                    setAES(COSName.AESV2.equals(cryptFilterMethod) || COSName.AESV3.equals(cryptFilterMethod));
                }
            } else {
                mdResult = MessageDigests.getSHA1().digest(sha1Input);
            }
            setEncryptionKey(new byte[getKeyLength() / 8]);
            System.arraycopy(mdResult, 0, getEncryptionKey(), 0, getKeyLength() / 8);
        } catch (KeyStoreException e) {
            throw new IOException(e);
        } catch (CertificateEncodingException e2) {
            throw new IOException(e2);
        } catch (CMSException e3) {
            throw new IOException(e3);
        }
    }

    private void appendCertInfo(StringBuilder extraInfo, KeyTransRecipientId ktRid, X509Certificate certificate, X509CertificateHolder materialCert) {
        BigInteger ridSerialNumber = ktRid.getSerialNumber();
        if (ridSerialNumber != null) {
            String certSerial = "unknown";
            BigInteger certSerialNumber = certificate.getSerialNumber();
            if (certSerialNumber != null) {
                certSerial = certSerialNumber.toString(16);
            }
            extraInfo.append("serial-#: rid ");
            extraInfo.append(ridSerialNumber.toString(16));
            extraInfo.append(" vs. cert ");
            extraInfo.append(certSerial);
            extraInfo.append(" issuer: rid '");
            extraInfo.append(ktRid.getIssuer());
            extraInfo.append("' vs. cert '");
            extraInfo.append(materialCert == null ? "null" : materialCert.getIssuer());
            extraInfo.append("' ");
        }
    }

    @Override // org.apache.pdfbox.pdmodel.encryption.SecurityHandler
    public void prepareDocumentForEncryption(PDDocument doc) throws IOException {
        byte[] mdResult;
        try {
            PDEncryption dictionary = doc.getEncryption();
            if (dictionary == null) {
                dictionary = new PDEncryption();
            }
            dictionary.setFilter(FILTER);
            dictionary.setLength(getKeyLength());
            int version = computeVersionNumber();
            dictionary.setVersion(version);
            dictionary.removeV45filters();
            byte[] seed = new byte[20];
            try {
                KeyGenerator key = KeyGenerator.getInstance("AES");
                key.init(192, new SecureRandom());
                SecretKey sk = key.generateKey();
                System.arraycopy(sk.getEncoded(), 0, seed, 0, 20);
                byte[][] recipientsFields = computeRecipientsField(seed);
                int shaInputLength = seed.length;
                for (byte[] field : recipientsFields) {
                    shaInputLength += field.length;
                }
                byte[] shaInput = new byte[shaInputLength];
                System.arraycopy(seed, 0, shaInput, 0, 20);
                int shaInputOffset = 20;
                for (byte[] recipientsField : recipientsFields) {
                    System.arraycopy(recipientsField, 0, shaInput, shaInputOffset, recipientsField.length);
                    shaInputOffset += recipientsField.length;
                }
                switch (version) {
                    case 4:
                        dictionary.setSubFilter(SUBFILTER5);
                        mdResult = MessageDigests.getSHA1().digest(shaInput);
                        prepareEncryptionDictAES(dictionary, COSName.AESV2, recipientsFields);
                        break;
                    case 5:
                        dictionary.setSubFilter(SUBFILTER5);
                        mdResult = MessageDigests.getSHA256().digest(shaInput);
                        prepareEncryptionDictAES(dictionary, COSName.AESV3, recipientsFields);
                        break;
                    default:
                        dictionary.setSubFilter(SUBFILTER4);
                        mdResult = MessageDigests.getSHA1().digest(shaInput);
                        dictionary.setRecipients(recipientsFields);
                        break;
                }
                setEncryptionKey(new byte[getKeyLength() / 8]);
                System.arraycopy(mdResult, 0, getEncryptionKey(), 0, getKeyLength() / 8);
                doc.setEncryptionDictionary(dictionary);
                doc.getDocument().setEncryptionDictionary(dictionary.getCOSObject());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        } catch (GeneralSecurityException e2) {
            throw new IOException(e2);
        }
    }

    private void prepareEncryptionDictAES(PDEncryption encryptionDictionary, COSName aesVName, byte[][] recipients) {
        PDCryptFilterDictionary cryptFilterDictionary = new PDCryptFilterDictionary();
        cryptFilterDictionary.setCryptFilterMethod(aesVName);
        cryptFilterDictionary.setLength(getKeyLength());
        COSArray array = new COSArray();
        for (byte[] recipient : recipients) {
            array.add((COSBase) new COSString(recipient));
        }
        cryptFilterDictionary.getCOSObject().setItem(COSName.RECIPIENTS, (COSBase) array);
        array.setDirect(true);
        encryptionDictionary.setDefaultCryptFilterDictionary(cryptFilterDictionary);
        encryptionDictionary.setStreamFilterName(COSName.DEFAULT_CRYPT_FILTER);
        encryptionDictionary.setStringFilterName(COSName.DEFAULT_CRYPT_FILTER);
        cryptFilterDictionary.getCOSObject().setDirect(true);
        setAES(true);
    }

    private byte[][] computeRecipientsField(byte[] seed) throws GeneralSecurityException, IOException {
        PublicKeyProtectionPolicy protectionPolicy = (PublicKeyProtectionPolicy) getProtectionPolicy();
        byte[][] recipientsField = new byte[protectionPolicy.getNumberOfRecipients()][];
        Iterator<PublicKeyRecipient> it = protectionPolicy.getRecipientsIterator();
        int i = 0;
        while (it.hasNext()) {
            PublicKeyRecipient recipient = it.next();
            X509Certificate certificate = recipient.getX509();
            int permission = recipient.getPermission().getPermissionBytesForPublicKey();
            byte[] pkcs7input = new byte[24];
            byte one = (byte) permission;
            byte two = (byte) (permission >>> 8);
            byte three = (byte) (permission >>> 16);
            byte four = (byte) (permission >>> 24);
            System.arraycopy(seed, 0, pkcs7input, 0, 20);
            pkcs7input[20] = four;
            pkcs7input[21] = three;
            pkcs7input[22] = two;
            pkcs7input[23] = one;
            ASN1Primitive obj = createDERForRecipient(pkcs7input, certificate);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            obj.encodeTo(baos, "DER");
            recipientsField[i] = baos.toByteArray();
            i++;
        }
        return recipientsField;
    }

    private ASN1Primitive createDERForRecipient(byte[] in, X509Certificate cert) throws GeneralSecurityException, IOException {
        String algorithm = PKCSObjectIdentifiers.RC2_CBC.getId();
        try {
            Provider provider = SecurityProvider.getProvider();
            AlgorithmParameterGenerator apg = AlgorithmParameterGenerator.getInstance(algorithm, provider);
            KeyGenerator keygen = KeyGenerator.getInstance(algorithm, provider);
            Cipher cipher = Cipher.getInstance(algorithm, provider);
            AlgorithmParameters parameters = apg.generateParameters();
            ASN1InputStream input = new ASN1InputStream(parameters.getEncoded("ASN.1"));
            ASN1Primitive object = input.readObject();
            input.close();
            keygen.init(128);
            SecretKey secretkey = keygen.generateKey();
            cipher.init(1, secretkey, parameters);
            byte[] bytes = cipher.doFinal(in);
            KeyTransRecipientInfo recipientInfo = computeRecipientInfo(cert, secretkey.getEncoded());
            DERSet set = new DERSet(new RecipientInfo(recipientInfo));
            AlgorithmIdentifier algorithmId = new AlgorithmIdentifier(new ASN1ObjectIdentifier(algorithm), object);
            EncryptedContentInfo encryptedInfo = new EncryptedContentInfo(PKCSObjectIdentifiers.data, algorithmId, new DEROctetString(bytes));
            EnvelopedData enveloped = new EnvelopedData((OriginatorInfo) null, set, encryptedInfo, (ASN1Set) null);
            ContentInfo contentInfo = new ContentInfo(PKCSObjectIdentifiers.envelopedData, enveloped);
            return contentInfo.toASN1Primitive();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Could not find a suitable javax.crypto provider for algorithm " + algorithm + "; possible reason: using an unsigned .jar file", e);
        } catch (NoSuchPaddingException e2) {
            throw new RuntimeException("Could not find a suitable javax.crypto provider", e2);
        }
    }

    private KeyTransRecipientInfo computeRecipientInfo(X509Certificate x509certificate, byte[] abyte0) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException, CertificateEncodingException {
        ASN1InputStream input = new ASN1InputStream(x509certificate.getTBSCertificate());
        TBSCertificate certificate = TBSCertificate.getInstance(input.readObject());
        input.close();
        AlgorithmIdentifier algorithmId = certificate.getSubjectPublicKeyInfo().getAlgorithm();
        IssuerAndSerialNumber serial = new IssuerAndSerialNumber(certificate.getIssuer(), certificate.getSerialNumber().getValue());
        try {
            Cipher cipher = Cipher.getInstance(algorithmId.getAlgorithm().getId(), SecurityProvider.getProvider());
            cipher.init(1, x509certificate.getPublicKey());
            DEROctetString octets = new DEROctetString(cipher.doFinal(abyte0));
            RecipientIdentifier recipientId = new RecipientIdentifier(serial);
            return new KeyTransRecipientInfo(recipientId, algorithmId, octets);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not find a suitable javax.crypto provider", e);
        } catch (NoSuchPaddingException e2) {
            throw new RuntimeException("Could not find a suitable javax.crypto provider", e2);
        }
    }
}
