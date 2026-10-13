package org.bouncycastle.openssl.jcajce;

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
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkix.jcajce.JcaPKIXIdentity;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/jcajce/JcaPKIXIdentityBuilder.class */
public class JcaPKIXIdentityBuilder {
    private JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();
    private JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();

    public JcaPKIXIdentityBuilder setProvider(Provider provider) {
        this.keyConverter = this.keyConverter.setProvider(provider);
        this.certConverter = this.certConverter.setProvider(provider);
        return this;
    }

    public JcaPKIXIdentityBuilder setProvider(String str) {
        this.keyConverter = this.keyConverter.setProvider(str);
        this.certConverter = this.certConverter.setProvider(str);
        return this;
    }

    public JcaPKIXIdentity build(File file, File file2) throws IOException, CertificateException {
        checkFile(file);
        checkFile(file2);
        FileInputStream fileInputStream = new FileInputStream(file);
        FileInputStream fileInputStream2 = new FileInputStream(file2);
        JcaPKIXIdentity jcaPKIXIdentityBuild = build(fileInputStream, fileInputStream2);
        fileInputStream.close();
        fileInputStream2.close();
        return jcaPKIXIdentityBuild;
    }

    public JcaPKIXIdentity build(InputStream inputStream, InputStream inputStream2) throws IOException, CertificateException {
        PrivateKey privateKey;
        Object object = new PEMParser(new InputStreamReader(inputStream)).readObject();
        if (object instanceof PEMKeyPair) {
            privateKey = this.keyConverter.getPrivateKey(((PEMKeyPair) object).getPrivateKeyInfo());
        } else {
            if (!(object instanceof PrivateKeyInfo)) {
                throw new IOException("unrecognised private key file");
            }
            privateKey = this.keyConverter.getPrivateKey((PrivateKeyInfo) object);
        }
        PEMParser pEMParser = new PEMParser(new InputStreamReader(inputStream2));
        ArrayList arrayList = new ArrayList();
        while (true) {
            Object object2 = pEMParser.readObject();
            if (object2 == null) {
                return new JcaPKIXIdentity(privateKey, (X509Certificate[]) arrayList.toArray(new X509Certificate[arrayList.size()]));
            }
            arrayList.add(this.certConverter.getCertificate((X509CertificateHolder) object2));
        }
    }

    private void checkFile(File file) throws IOException {
        if (file.canRead()) {
            return;
        }
        if (!file.exists()) {
            throw new FileNotFoundException("Unable to open " + file.getPath() + ": it does not exist.");
        }
        throw new IOException("Unable to open file " + file.getPath() + " for reading.");
    }
}
