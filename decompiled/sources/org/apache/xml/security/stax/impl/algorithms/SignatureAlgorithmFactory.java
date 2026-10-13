package org.apache.xml.security.stax.impl.algorithms;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/algorithms/SignatureAlgorithmFactory.class */
public final class SignatureAlgorithmFactory {
    private static SignatureAlgorithmFactory instance;

    private SignatureAlgorithmFactory() {
    }

    public static synchronized SignatureAlgorithmFactory getInstance() {
        if (instance == null) {
            instance = new SignatureAlgorithmFactory();
        }
        return instance;
    }

    public SignatureAlgorithm getSignatureAlgorithm(String algoURI) throws XMLSecurityException, NoSuchAlgorithmException, NoSuchProviderException {
        String algorithmClass = JCEMapper.getAlgorithmClassFromURI(algoURI);
        if (algorithmClass == null) {
            throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{algoURI});
        }
        String jceName = JCEMapper.translateURItoJCEID(algoURI);
        String jceProvider = JCEMapper.getJCEProviderFromURI(algoURI);
        if ("MAC".equalsIgnoreCase(algorithmClass)) {
            return new HMACSignatureAlgorithm(jceName, jceProvider);
        }
        if ("Signature".equalsIgnoreCase(algorithmClass)) {
            return new PKISignatureAlgorithm(jceName, jceProvider);
        }
        return null;
    }
}
