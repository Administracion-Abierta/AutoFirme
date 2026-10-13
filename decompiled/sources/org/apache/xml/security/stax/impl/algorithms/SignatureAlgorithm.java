package org.apache.xml.security.stax.impl.algorithms;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/algorithms/SignatureAlgorithm.class */
public interface SignatureAlgorithm {
    void engineUpdate(byte[] bArr) throws XMLSecurityException;

    void engineUpdate(byte b) throws XMLSecurityException;

    void engineUpdate(byte[] bArr, int i, int i2) throws XMLSecurityException;

    void engineInitSign(Key key) throws XMLSecurityException;

    void engineInitSign(Key key, SecureRandom secureRandom) throws XMLSecurityException;

    void engineInitSign(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSecurityException;

    byte[] engineSign() throws XMLSecurityException;

    void engineInitVerify(Key key) throws XMLSecurityException;

    boolean engineVerify(byte[] bArr) throws XMLSecurityException;

    void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) throws XMLSecurityException;
}
