package org.spongycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.crmf.EncryptedValue;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.KeyWrapper;
import org.spongycastle.operator.OperatorException;
import org.spongycastle.operator.OutputEncryptor;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/EncryptedValueBuilder.class */
public class EncryptedValueBuilder {
    private KeyWrapper wrapper;
    private OutputEncryptor encryptor;
    private EncryptedValuePadder padder;

    public EncryptedValueBuilder(KeyWrapper wrapper, OutputEncryptor encryptor) {
        this(wrapper, encryptor, null);
    }

    public EncryptedValueBuilder(KeyWrapper wrapper, OutputEncryptor encryptor, EncryptedValuePadder padder) {
        this.wrapper = wrapper;
        this.encryptor = encryptor;
        this.padder = padder;
    }

    public EncryptedValue build(char[] revocationPassphrase) throws CRMFException {
        return encryptData(padData(Strings.toUTF8ByteArray(revocationPassphrase)));
    }

    public EncryptedValue build(X509CertificateHolder holder) throws CRMFException {
        try {
            return encryptData(padData(holder.getEncoded()));
        } catch (IOException e) {
            throw new CRMFException("cannot encode certificate: " + e.getMessage(), e);
        }
    }

    private EncryptedValue encryptData(byte[] data) throws CRMFException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        OutputStream eOut = this.encryptor.getOutputStream(bOut);
        try {
            eOut.write(data);
            eOut.close();
            AlgorithmIdentifier symmAlg = this.encryptor.getAlgorithmIdentifier();
            try {
                this.wrapper.generateWrappedKey(this.encryptor.getKey());
                DERBitString encSymmKey = new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey()));
                AlgorithmIdentifier keyAlg = this.wrapper.getAlgorithmIdentifier();
                DERBitString encValue = new DERBitString(bOut.toByteArray());
                return new EncryptedValue(null, symmAlg, encSymmKey, keyAlg, null, encValue);
            } catch (OperatorException e) {
                throw new CRMFException("cannot wrap key: " + e.getMessage(), e);
            }
        } catch (IOException e2) {
            throw new CRMFException("cannot process data: " + e2.getMessage(), e2);
        }
    }

    private byte[] padData(byte[] data) {
        if (this.padder != null) {
            return this.padder.getPaddedData(data);
        }
        return data;
    }
}
