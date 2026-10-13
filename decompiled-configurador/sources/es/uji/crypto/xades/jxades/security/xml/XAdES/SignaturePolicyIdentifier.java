package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignaturePolicyIdentifier.class */
public interface SignaturePolicyIdentifier {
    boolean isImplied();

    void setImplied(boolean z);

    String getIdentifier();

    void setIdentifier(String str, String str2, String str3) throws NoSuchAlgorithmException, IOException;

    void setIdentifier(String str) throws NoSuchAlgorithmException, IOException;

    String getHashBase64();

    String getHashAlgorithm();

    String getDescription();

    void setDescription(String str);

    String getQualifier();

    void setQualifier(String str);
}
