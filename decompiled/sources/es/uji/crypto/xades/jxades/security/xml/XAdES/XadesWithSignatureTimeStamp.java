package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XadesWithSignatureTimeStamp.class */
public interface XadesWithSignatureTimeStamp {
    List<SignatureTimeStamp> getSignatureTimeStamps();

    void setSignatureTimeStamps(List<SignatureTimeStamp> list);
}
