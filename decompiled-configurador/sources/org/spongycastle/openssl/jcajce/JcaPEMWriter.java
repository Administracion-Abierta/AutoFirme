package org.spongycastle.openssl.jcajce;

import java.io.IOException;
import java.io.Writer;
import org.spongycastle.openssl.PEMEncryptor;
import org.spongycastle.util.io.pem.PemGenerationException;
import org.spongycastle.util.io.pem.PemObjectGenerator;
import org.spongycastle.util.io.pem.PemWriter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/openssl/jcajce/JcaPEMWriter.class */
public class JcaPEMWriter extends PemWriter {
    public JcaPEMWriter(Writer out) {
        super(out);
    }

    public void writeObject(Object obj) throws IOException {
        writeObject(obj, null);
    }

    public void writeObject(Object obj, PEMEncryptor encryptor) throws IOException {
        try {
            super.writeObject((PemObjectGenerator) new JcaMiscPEMGenerator(obj, encryptor));
        } catch (PemGenerationException e) {
            if (e.getCause() instanceof IOException) {
                throw ((IOException) e.getCause());
            }
            throw e;
        }
    }

    @Override // org.spongycastle.util.io.pem.PemWriter
    public void writeObject(PemObjectGenerator obj) throws IOException {
        super.writeObject(obj);
    }
}
