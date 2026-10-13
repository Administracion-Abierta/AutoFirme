package org.spongycastle.openssl;

import java.io.IOException;
import java.io.Writer;
import org.spongycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.spongycastle.util.io.pem.PemGenerationException;
import org.spongycastle.util.io.pem.PemObjectGenerator;
import org.spongycastle.util.io.pem.PemWriter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMWriter.class */
public class PEMWriter extends PemWriter {
    public PEMWriter(Writer out) {
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
