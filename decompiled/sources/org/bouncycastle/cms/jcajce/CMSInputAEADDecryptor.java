package org.bouncycastle.cms.jcajce;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.InputStreamWithMAC;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputAEADDecryptor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/CMSInputAEADDecryptor.class */
class CMSInputAEADDecryptor implements InputAEADDecryptor {
    private final AlgorithmIdentifier contentEncryptionAlgorithm;
    private final Cipher dataCipher;
    private InputStream inputStream;

    CMSInputAEADDecryptor(AlgorithmIdentifier algorithmIdentifier, Cipher cipher) {
        this.contentEncryptionAlgorithm = algorithmIdentifier;
        this.dataCipher = cipher;
    }

    @Override // org.bouncycastle.operator.InputDecryptor
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.contentEncryptionAlgorithm;
    }

    @Override // org.bouncycastle.operator.InputDecryptor
    public InputStream getInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return new CipherInputStream(inputStream, this.dataCipher);
    }

    @Override // org.bouncycastle.operator.AADProcessor
    public OutputStream getAADStream() {
        if (checkForAEAD()) {
            return new JceAADStream(this.dataCipher);
        }
        return null;
    }

    @Override // org.bouncycastle.operator.AADProcessor
    public byte[] getMAC() {
        if (this.inputStream instanceof InputStreamWithMAC) {
            return ((InputStreamWithMAC) this.inputStream).getMAC();
        }
        return null;
    }

    private static boolean checkForAEAD() {
        return ((Boolean) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.bouncycastle.cms.jcajce.CMSInputAEADDecryptor.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                try {
                    return Boolean.valueOf(Cipher.class.getMethod("updateAAD", byte[].class) != null);
                } catch (Exception e) {
                    return Boolean.FALSE;
                }
            }
        })).booleanValue();
    }
}
