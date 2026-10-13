package org.spongycastle.cert.crmf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.crmf.EncryptedValue;
import org.spongycastle.asn1.x509.Certificate;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.InputDecryptor;
import org.spongycastle.util.Strings;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/crmf/EncryptedValueParser.class */
public class EncryptedValueParser {
    private EncryptedValue value;
    private EncryptedValuePadder padder;

    public EncryptedValueParser(EncryptedValue value) {
        this.value = value;
    }

    public EncryptedValueParser(EncryptedValue value, EncryptedValuePadder padder) {
        this.value = value;
        this.padder = padder;
    }

    private byte[] decryptValue(ValueDecryptorGenerator decGen) throws CRMFException {
        if (this.value.getIntendedAlg() != null) {
            throw new UnsupportedOperationException();
        }
        if (this.value.getValueHint() != null) {
            throw new UnsupportedOperationException();
        }
        InputDecryptor decryptor = decGen.getValueDecryptor(this.value.getKeyAlg(), this.value.getSymmAlg(), this.value.getEncSymmKey().getBytes());
        InputStream dataIn = decryptor.getInputStream(new ByteArrayInputStream(this.value.getEncValue().getBytes()));
        try {
            byte[] data = Streams.readAll(dataIn);
            if (this.padder != null) {
                return this.padder.getUnpaddedData(data);
            }
            return data;
        } catch (IOException e) {
            throw new CRMFException("Cannot parse decrypted data: " + e.getMessage(), e);
        }
    }

    public X509CertificateHolder readCertificateHolder(ValueDecryptorGenerator decGen) throws CRMFException {
        return new X509CertificateHolder(Certificate.getInstance(decryptValue(decGen)));
    }

    public char[] readPassphrase(ValueDecryptorGenerator decGen) throws CRMFException {
        return Strings.fromUTF8ByteArray(decryptValue(decGen)).toCharArray();
    }
}
