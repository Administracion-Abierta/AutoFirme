package org.apache.xml.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/EncryptionMethod.class */
public interface EncryptionMethod {
    String getAlgorithm();

    int getKeySize();

    void setKeySize(int i);

    byte[] getOAEPparams();

    void setOAEPparams(byte[] bArr);

    void setDigestAlgorithm(String str);

    String getDigestAlgorithm();

    void setMGFAlgorithm(String str);

    String getMGFAlgorithm();

    Iterator<Element> getEncryptionMethodInformation();

    void addEncryptionMethodInformation(Element element);

    void removeEncryptionMethodInformation(Element element);
}
