package org.bouncycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/EncryptedValueBuilder.class */
public class EncryptedValueBuilder {
    private KeyWrapper wrapper;
    private OutputEncryptor encryptor;
    private EncryptedValuePadder padder;

    public EncryptedValueBuilder(KeyWrapper keyWrapper, OutputEncryptor outputEncryptor) {
        this(keyWrapper, outputEncryptor, null);
    }

    public EncryptedValueBuilder(KeyWrapper keyWrapper, OutputEncryptor outputEncryptor, EncryptedValuePadder encryptedValuePadder) {
        this.wrapper = keyWrapper;
        this.encryptor = outputEncryptor;
        this.padder = encryptedValuePadder;
    }

    public EncryptedValue build(char[] cArr) throws CRMFException {
        return encryptData(padData(Strings.toUTF8ByteArray(cArr)));
    }

    public EncryptedValue build(X509CertificateHolder x509CertificateHolder) throws CRMFException {
        try {
            return encryptData(padData(x509CertificateHolder.getEncoded()));
        } catch (IOException e) {
            throw new CRMFException("cannot encode certificate: " + e.getMessage(), e);
        }
    }

    public EncryptedValue build(PrivateKeyInfo privateKeyInfo) throws CRMFException {
        try {
            return new EncryptedValue(privateKeyInfo.getPrivateKeyAlgorithm(), this.encryptor.getAlgorithmIdentifier(), new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey())), this.wrapper.getAlgorithmIdentifier(), null, new DERBitString(new PKCS8EncryptedPrivateKeyInfoBuilder(privateKeyInfo).build(this.encryptor).getEncryptedData()));
        } catch (IllegalStateException e) {
            throw new CRMFException("cannot encode key: " + e.getMessage(), e);
        } catch (OperatorException e2) {
            throw new CRMFException("cannot wrap key: " + e2.getMessage(), e2);
        }
    }

    private EncryptedValue encryptData(byte[] bArr) throws CRMFException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStream outputStream = this.encryptor.getOutputStream(byteArrayOutputStream);
        try {
            outputStream.write(bArr);
            outputStream.close();
            AlgorithmIdentifier algorithmIdentifier = this.encryptor.getAlgorithmIdentifier();
            try {
                this.wrapper.generateWrappedKey(this.encryptor.getKey());
                return new EncryptedValue(null, algorithmIdentifier, new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey())), this.wrapper.getAlgorithmIdentifier(), null, new DERBitString(byteArrayOutputStream.toByteArray()));
            } catch (OperatorException e) {
                throw new CRMFException("cannot wrap key: " + e.getMessage(), e);
            }
        } catch (IOException e2) {
            throw new CRMFException("cannot process data: " + e2.getMessage(), e2);
        }
    }

    private byte[] padData(byte[] bArr) {
        return this.padder != null ? this.padder.getPaddedData(bArr) : bArr;
    }
}
