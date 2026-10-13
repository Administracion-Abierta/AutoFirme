package org.spongycastle.jce.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.x509.CertificatePair;
import org.spongycastle.x509.X509CertificatePair;
import org.spongycastle.x509.X509StreamParserSpi;
import org.spongycastle.x509.util.StreamParsingException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/X509CertPairParser.class */
public class X509CertPairParser extends X509StreamParserSpi {
    private InputStream currentStream = null;

    private X509CertificatePair readDERCrossCertificatePair(InputStream in) throws CertificateParsingException, IOException {
        ASN1InputStream dIn = new ASN1InputStream(in);
        ASN1Sequence seq = (ASN1Sequence) dIn.readObject();
        CertificatePair pair = CertificatePair.getInstance(seq);
        return new X509CertificatePair(pair);
    }

    @Override // org.spongycastle.x509.X509StreamParserSpi
    public void engineInit(InputStream in) {
        this.currentStream = in;
        if (!this.currentStream.markSupported()) {
            this.currentStream = new BufferedInputStream(this.currentStream);
        }
    }

    @Override // org.spongycastle.x509.X509StreamParserSpi
    public Object engineRead() throws StreamParsingException {
        try {
            this.currentStream.mark(10);
            int tag = this.currentStream.read();
            if (tag == -1) {
                return null;
            }
            this.currentStream.reset();
            return readDERCrossCertificatePair(this.currentStream);
        } catch (Exception e) {
            throw new StreamParsingException(e.toString(), e);
        }
    }

    @Override // org.spongycastle.x509.X509StreamParserSpi
    public Collection engineReadAll() throws StreamParsingException {
        List certs = new ArrayList();
        while (true) {
            X509CertificatePair pair = (X509CertificatePair) engineRead();
            if (pair != null) {
                certs.add(pair);
            } else {
                return certs;
            }
        }
    }
}
