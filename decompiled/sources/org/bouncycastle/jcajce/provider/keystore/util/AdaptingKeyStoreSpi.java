package org.bouncycastle.jcajce.provider.keystore.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/util/AdaptingKeyStoreSpi.class */
public class AdaptingKeyStoreSpi extends KeyStoreSpi {
    public static final String COMPAT_OVERRIDE = "keystore.type.compat";
    private final JKSKeyStoreSpi jksStore;
    private final KeyStoreSpi primaryStore;
    private KeyStoreSpi keyStoreSpi;

    public AdaptingKeyStoreSpi(JcaJceHelper jcaJceHelper, KeyStoreSpi keyStoreSpi) {
        this.jksStore = new JKSKeyStoreSpi(jcaJceHelper);
        this.primaryStore = keyStoreSpi;
        this.keyStoreSpi = keyStoreSpi;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineProbe(InputStream inputStream) throws IOException {
        if (this.keyStoreSpi instanceof PKCS12KeyStoreSpi) {
            return ((PKCS12KeyStoreSpi) this.keyStoreSpi).engineProbe(inputStream);
        }
        return false;
    }

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        return this.keyStoreSpi.engineGetKey(str, cArr);
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String str) {
        return this.keyStoreSpi.engineGetCertificateChain(str);
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String str) {
        return this.keyStoreSpi.engineGetCertificate(str);
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String str) {
        return this.keyStoreSpi.engineGetCreationDate(str);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
        this.keyStoreSpi.engineSetKeyEntry(str, key, cArr, certificateArr);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
        this.keyStoreSpi.engineSetKeyEntry(str, bArr, certificateArr);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
        this.keyStoreSpi.engineSetCertificateEntry(str, certificate);
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String str) throws KeyStoreException {
        this.keyStoreSpi.engineDeleteEntry(str);
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration<String> engineAliases() {
        return this.keyStoreSpi.engineAliases();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String str) {
        return this.keyStoreSpi.engineContainsAlias(str);
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return this.keyStoreSpi.engineSize();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String str) {
        return this.keyStoreSpi.engineIsKeyEntry(str);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String str) {
        return this.keyStoreSpi.engineIsCertificateEntry(str);
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate certificate) {
        return this.keyStoreSpi.engineGetCertificateAlias(certificate);
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        this.keyStoreSpi.engineStore(outputStream, cArr);
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
        this.keyStoreSpi.engineStore(loadStoreParameter);
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream inputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        if (inputStream == null) {
            this.keyStoreSpi = this.primaryStore;
            this.keyStoreSpi.engineLoad(null, cArr);
            return;
        }
        if (Properties.isOverrideSet(COMPAT_OVERRIDE) || !(this.primaryStore instanceof PKCS12KeyStoreSpi)) {
            if (!inputStream.markSupported()) {
                inputStream = new BufferedInputStream(inputStream);
            }
            inputStream.mark(8);
            if (this.jksStore.engineProbe(inputStream)) {
                this.keyStoreSpi = this.jksStore;
            } else {
                this.keyStoreSpi = this.primaryStore;
            }
            inputStream.reset();
        } else {
            this.keyStoreSpi = this.primaryStore;
        }
        this.keyStoreSpi.engineLoad(inputStream, cArr);
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
        this.keyStoreSpi.engineLoad(loadStoreParameter);
    }
}
