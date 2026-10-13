package org.bouncycastle.openssl;

import java.io.IOException;
import java.io.Writer;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMWriter.class */
public class PEMWriter extends PemWriter {
    public PEMWriter(Writer writer) {
        super(writer);
    }

    public void writeObject(Object obj) throws IOException {
        writeObject(obj, null);
    }

    public void writeObject(Object obj, PEMEncryptor pEMEncryptor) throws IOException {
        try {
            super.writeObject((PemObjectGenerator) new JcaMiscPEMGenerator(obj, pEMEncryptor));
        } catch (PemGenerationException e) {
            if (!(e.getCause() instanceof IOException)) {
                throw e;
            }
            throw ((IOException) e.getCause());
        }
    }

    @Override // org.bouncycastle.util.io.pem.PemWriter
    public void writeObject(PemObjectGenerator pemObjectGenerator) throws IOException {
        super.writeObject(pemObjectGenerator);
    }
}
