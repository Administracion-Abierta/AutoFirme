package org.spongycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.NoSuchProviderException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.security.auth.x500.X500Principal;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.openssl.PEMParser;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.pkcs.ContentInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.SignedData;
import org.spongycastle.jcajce.util.BCJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/x509/PKIXCertPath.class */
public class PKIXCertPath extends CertPath {
    private final JcaJceHelper helper;
    static final List certPathEncodings;
    private List certificates;

    static {
        List encodings = new ArrayList();
        encodings.add("PkiPath");
        encodings.add("PEM");
        encodings.add("PKCS7");
        certPathEncodings = Collections.unmodifiableList(encodings);
    }

    private List sortCerts(List certs) {
        if (certs.size() < 2) {
            return certs;
        }
        X500Principal issuer = ((X509Certificate) certs.get(0)).getIssuerX500Principal();
        boolean okay = true;
        for (int i = 1; i != certs.size(); i++) {
            if (issuer.equals(((X509Certificate) certs.get(i)).getSubjectX500Principal())) {
                issuer = ((X509Certificate) certs.get(i)).getIssuerX500Principal();
            } else {
                okay = false;
                break;
            }
        }
        if (okay) {
            return certs;
        }
        List retList = new ArrayList(certs.size());
        List orig = new ArrayList(certs);
        for (int i2 = 0; i2 < certs.size(); i2++) {
            X509Certificate cert = (X509Certificate) certs.get(i2);
            boolean found = false;
            X500Principal subject = cert.getSubjectX500Principal();
            for (int j = 0; j != certs.size(); j++) {
                if (((X509Certificate) certs.get(j)).getIssuerX500Principal().equals(subject)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                retList.add(cert);
                certs.remove(i2);
            }
        }
        if (retList.size() > 1) {
            return orig;
        }
        for (int i3 = 0; i3 != retList.size(); i3++) {
            X500Principal issuer2 = ((X509Certificate) retList.get(i3)).getIssuerX500Principal();
            for (int j2 = 0; j2 < certs.size(); j2++) {
                X509Certificate c = (X509Certificate) certs.get(j2);
                if (issuer2.equals(c.getSubjectX500Principal())) {
                    retList.add(c);
                    certs.remove(j2);
                    break;
                }
            }
        }
        if (certs.size() > 0) {
            return orig;
        }
        return retList;
    }

    PKIXCertPath(List certificates) {
        super(XMLX509Certificate.JCA_CERT_ID);
        this.helper = new BCJcaJceHelper();
        this.certificates = sortCerts(new ArrayList(certificates));
    }

    PKIXCertPath(InputStream inStream, String encoding) throws CertificateException {
        super(XMLX509Certificate.JCA_CERT_ID);
        this.helper = new BCJcaJceHelper();
        try {
            if (encoding.equalsIgnoreCase("PkiPath")) {
                ASN1InputStream derInStream = new ASN1InputStream(inStream);
                ASN1Primitive derObject = derInStream.readObject();
                if (!(derObject instanceof ASN1Sequence)) {
                    throw new CertificateException("input stream does not contain a ASN1 SEQUENCE while reading PkiPath encoded data to load CertPath");
                }
                Enumeration e = ((ASN1Sequence) derObject).getObjects();
                this.certificates = new ArrayList();
                java.security.cert.CertificateFactory certFactory = this.helper.createCertificateFactory(XMLX509Certificate.JCA_CERT_ID);
                while (e.hasMoreElements()) {
                    ASN1Encodable element = (ASN1Encodable) e.nextElement();
                    byte[] encoded = element.toASN1Primitive().getEncoded("DER");
                    this.certificates.add(0, certFactory.generateCertificate(new ByteArrayInputStream(encoded)));
                }
            } else if (encoding.equalsIgnoreCase("PKCS7") || encoding.equalsIgnoreCase("PEM")) {
                InputStream inStream2 = new BufferedInputStream(inStream);
                this.certificates = new ArrayList();
                java.security.cert.CertificateFactory certFactory2 = this.helper.createCertificateFactory(XMLX509Certificate.JCA_CERT_ID);
                while (true) {
                    Certificate cert = certFactory2.generateCertificate(inStream2);
                    if (cert == null) {
                        break;
                    } else {
                        this.certificates.add(cert);
                    }
                }
            } else {
                throw new CertificateException("unsupported encoding: " + encoding);
            }
            this.certificates = sortCerts(this.certificates);
        } catch (IOException ex) {
            throw new CertificateException("IOException throw while decoding CertPath:\n" + ex.toString());
        } catch (NoSuchProviderException ex2) {
            throw new CertificateException("BouncyCastle provider not found while trying to get a CertificateFactory:\n" + ex2.toString());
        }
    }

    @Override // java.security.cert.CertPath
    public Iterator getEncodings() {
        return certPathEncodings.iterator();
    }

    @Override // java.security.cert.CertPath
    public byte[] getEncoded() throws CertificateEncodingException {
        Iterator iter = getEncodings();
        if (iter.hasNext()) {
            Object enc = iter.next();
            if (enc instanceof String) {
                return getEncoded((String) enc);
            }
            return null;
        }
        return null;
    }

    @Override // java.security.cert.CertPath
    public byte[] getEncoded(String encoding) throws CertificateEncodingException {
        if (encoding.equalsIgnoreCase("PkiPath")) {
            ASN1EncodableVector v = new ASN1EncodableVector();
            ListIterator iter = this.certificates.listIterator(this.certificates.size());
            while (iter.hasPrevious()) {
                v.add(toASN1Object((X509Certificate) iter.previous()));
            }
            return toDEREncoded(new DERSequence(v));
        }
        if (encoding.equalsIgnoreCase("PKCS7")) {
            ContentInfo encInfo = new ContentInfo(PKCSObjectIdentifiers.data, null);
            ASN1EncodableVector v2 = new ASN1EncodableVector();
            for (int i = 0; i != this.certificates.size(); i++) {
                v2.add(toASN1Object((X509Certificate) this.certificates.get(i)));
            }
            SignedData sd = new SignedData(new ASN1Integer(1L), new DERSet(), encInfo, new DERSet(v2), null, new DERSet());
            return toDEREncoded(new ContentInfo(PKCSObjectIdentifiers.signedData, sd));
        }
        if (encoding.equalsIgnoreCase("PEM")) {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            PemWriter pWrt = new PemWriter(new OutputStreamWriter(bOut));
            for (int i2 = 0; i2 != this.certificates.size(); i2++) {
                try {
                    pWrt.writeObject(new PemObject(PEMParser.TYPE_CERTIFICATE, ((X509Certificate) this.certificates.get(i2)).getEncoded()));
                } catch (Exception e) {
                    throw new CertificateEncodingException("can't encode certificate for PEM encoded path");
                }
            }
            pWrt.close();
            return bOut.toByteArray();
        }
        throw new CertificateEncodingException("unsupported encoding: " + encoding);
    }

    @Override // java.security.cert.CertPath
    public List getCertificates() {
        return Collections.unmodifiableList(new ArrayList(this.certificates));
    }

    private ASN1Primitive toASN1Object(X509Certificate cert) throws CertificateEncodingException {
        try {
            return new ASN1InputStream(cert.getEncoded()).readObject();
        } catch (Exception e) {
            throw new CertificateEncodingException("Exception while encoding certificate: " + e.toString());
        }
    }

    private byte[] toDEREncoded(ASN1Encodable obj) throws CertificateEncodingException {
        try {
            return obj.toASN1Primitive().getEncoded("DER");
        } catch (IOException e) {
            throw new CertificateEncodingException("Exception thrown: " + e);
        }
    }
}
