package org.spongycastle.openssl.jcajce;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.openssl.PEMKeyPair;
import org.spongycastle.openssl.PEMParser;
import org.spongycastle.pkix.jcajce.JcaPKIXIdentity;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/jcajce/JcaPKIXIdentityBuilder.class */
public class JcaPKIXIdentityBuilder {
    private JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();
    private JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();

    public JcaPKIXIdentityBuilder setProvider(Provider provider) {
        this.keyConverter = this.keyConverter.setProvider(provider);
        this.certConverter = this.certConverter.setProvider(provider);
        return this;
    }

    public JcaPKIXIdentityBuilder setProvider(String providerName) {
        this.keyConverter = this.keyConverter.setProvider(providerName);
        this.certConverter = this.certConverter.setProvider(providerName);
        return this;
    }

    public JcaPKIXIdentity build(File keyFile, File certificateFile) throws IOException, CertificateException {
        checkFile(keyFile);
        checkFile(certificateFile);
        FileInputStream keyStream = new FileInputStream(keyFile);
        FileInputStream certificateStream = new FileInputStream(certificateFile);
        JcaPKIXIdentity rv = build(keyStream, certificateStream);
        keyStream.close();
        certificateStream.close();
        return rv;
    }

    public JcaPKIXIdentity build(InputStream keyStream, InputStream certificateStream) throws IOException, CertificateException {
        PrivateKey privKey;
        PEMParser keyParser = new PEMParser(new InputStreamReader(keyStream));
        Object keyObj = keyParser.readObject();
        if (keyObj instanceof PEMKeyPair) {
            PEMKeyPair kp = (PEMKeyPair) keyObj;
            privKey = this.keyConverter.getPrivateKey(kp.getPrivateKeyInfo());
        } else if (keyObj instanceof PrivateKeyInfo) {
            privKey = this.keyConverter.getPrivateKey((PrivateKeyInfo) keyObj);
        } else {
            throw new IOException("unrecognised private key file");
        }
        PEMParser certParser = new PEMParser(new InputStreamReader(certificateStream));
        List certs = new ArrayList();
        while (true) {
            Object certObj = certParser.readObject();
            if (certObj != null) {
                certs.add(this.certConverter.getCertificate((X509CertificateHolder) certObj));
            } else {
                return new JcaPKIXIdentity(privKey, (X509Certificate[]) certs.toArray(new X509Certificate[certs.size()]));
            }
        }
    }

    private void checkFile(File file) throws IOException {
        if (file.canRead()) {
            if (file.exists()) {
                throw new IOException("Unable to open file " + file.getPath() + " for reading.");
            }
            throw new FileNotFoundException("Unable to open " + file.getPath() + ": it does not exist.");
        }
    }
}
