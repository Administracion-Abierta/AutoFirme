package com.aowagie.text.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DEROutputStream;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.EncryptedContentInfo;
import org.spongycastle.asn1.cms.EnvelopedData;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.cms.KeyTransRecipientInfo;
import org.spongycastle.asn1.cms.OriginatorInfo;
import org.spongycastle.asn1.cms.RecipientIdentifier;
import org.spongycastle.asn1.cms.RecipientInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.TBSCertificateStructure;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPublicKeySecurityHandler.class */
class PdfPublicKeySecurityHandler {
    private static final int SEED_LENGTH = 20;
    private ArrayList recipients;
    private byte[] seed;

    public PdfPublicKeySecurityHandler() {
        this.recipients = null;
        this.seed = new byte[20];
        try {
            KeyGenerator key = KeyGenerator.getInstance("AES");
            key.init(192, new SecureRandom());
            SecretKey sk = key.generateKey();
            System.arraycopy(sk.getEncoded(), 0, this.seed, 0, 20);
        } catch (NoSuchAlgorithmException e) {
            this.seed = SecureRandom.getSeed(20);
        }
        this.recipients = new ArrayList();
    }

    void addRecipient(PdfPublicKeyRecipient recipient) {
        this.recipients.add(recipient);
    }

    protected byte[] getSeed() {
        return (byte[]) this.seed.clone();
    }

    public int getRecipientsSize() {
        return this.recipients.size();
    }

    public byte[] getEncodedRecipient(int index) throws GeneralSecurityException, IOException {
        PdfPublicKeyRecipient recipient = (PdfPublicKeyRecipient) this.recipients.get(index);
        byte[] cms = recipient.getCms();
        if (cms != null) {
            return cms;
        }
        Certificate certificate = recipient.getCertificate();
        int permission = ((recipient.getPermission() | (-3904)) & (-4)) + 1;
        byte[] pkcs7input = new byte[24];
        byte one = (byte) permission;
        byte two = (byte) (permission >> 8);
        byte three = (byte) (permission >> 16);
        byte four = (byte) (permission >> 24);
        System.arraycopy(this.seed, 0, pkcs7input, 0, 20);
        pkcs7input[20] = four;
        pkcs7input[21] = three;
        pkcs7input[22] = two;
        pkcs7input[23] = one;
        ASN1Primitive obj = createDERForRecipient(pkcs7input, (X509Certificate) certificate);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DEROutputStream k = new DEROutputStream(baos);
        k.writeObject(obj);
        byte[] cms2 = baos.toByteArray();
        recipient.setCms(cms2);
        return cms2;
    }

    public PdfArray getEncodedRecipients() throws GeneralSecurityException, IOException {
        PdfArray EncodedRecipients = new PdfArray();
        for (int i = 0; i < this.recipients.size(); i++) {
            try {
                byte[] cms = getEncodedRecipient(i);
                EncodedRecipients.add(new PdfLiteral(PdfContentByte.escapeString(cms)));
            } catch (IOException e) {
                EncodedRecipients = null;
            } catch (GeneralSecurityException e2) {
                EncodedRecipients = null;
            }
        }
        return EncodedRecipients;
    }

    private ASN1Primitive createDERForRecipient(byte[] in, X509Certificate cert) throws GeneralSecurityException, IOException {
        AlgorithmParameterGenerator algorithmparametergenerator = AlgorithmParameterGenerator.getInstance("1.2.840.113549.3.2");
        AlgorithmParameters algorithmparameters = algorithmparametergenerator.generateParameters();
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(algorithmparameters.getEncoded("ASN.1"));
        ASN1InputStream asn1inputstream = new ASN1InputStream(bytearrayinputstream);
        ASN1Primitive derobject = asn1inputstream.readObject();
        KeyGenerator keygenerator = KeyGenerator.getInstance("1.2.840.113549.3.2");
        keygenerator.init(128);
        SecretKey secretkey = keygenerator.generateKey();
        Cipher cipher = Cipher.getInstance("1.2.840.113549.3.2");
        cipher.init(1, secretkey, algorithmparameters);
        byte[] abyte1 = cipher.doFinal(in);
        DEROctetString deroctetstring = new DEROctetString(abyte1);
        KeyTransRecipientInfo keytransrecipientinfo = computeRecipientInfo(cert, secretkey.getEncoded());
        DERSet derset = new DERSet(new RecipientInfo(keytransrecipientinfo));
        AlgorithmIdentifier algorithmidentifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.3.2"), derobject);
        EncryptedContentInfo encryptedcontentinfo = new EncryptedContentInfo(PKCSObjectIdentifiers.data, algorithmidentifier, deroctetstring);
        EnvelopedData env = new EnvelopedData((OriginatorInfo) null, derset, encryptedcontentinfo, (ASN1Set) null);
        ContentInfo contentinfo = new ContentInfo(PKCSObjectIdentifiers.envelopedData, env);
        return contentinfo.toASN1Primitive();
    }

    private KeyTransRecipientInfo computeRecipientInfo(X509Certificate x509certificate, byte[] abyte0) throws GeneralSecurityException, IOException {
        ASN1InputStream asn1inputstream = new ASN1InputStream(new ByteArrayInputStream(x509certificate.getTBSCertificate()));
        TBSCertificateStructure tbscertificatestructure = TBSCertificateStructure.getInstance(asn1inputstream.readObject());
        AlgorithmIdentifier algorithmidentifier = tbscertificatestructure.getSubjectPublicKeyInfo().getAlgorithm();
        IssuerAndSerialNumber issuerandserialnumber = new IssuerAndSerialNumber(tbscertificatestructure.getIssuer(), tbscertificatestructure.getSerialNumber().getValue());
        Cipher cipher = Cipher.getInstance(algorithmidentifier.getAlgorithm().getId());
        cipher.init(1, x509certificate);
        DEROctetString deroctetstring = new DEROctetString(cipher.doFinal(abyte0));
        RecipientIdentifier recipId = new RecipientIdentifier(issuerandserialnumber);
        return new KeyTransRecipientInfo(recipId, algorithmidentifier, deroctetstring);
    }
}
