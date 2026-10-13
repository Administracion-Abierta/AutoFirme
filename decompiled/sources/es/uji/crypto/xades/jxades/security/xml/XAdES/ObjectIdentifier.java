package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/ObjectIdentifier.class */
public interface ObjectIdentifier {
    String getQualifier();

    void setQualifier(String str);

    String getIdentifier();

    void setIdentifier(String str);

    String getDescription();

    void setDescription(String str);

    ArrayList<String> getDocumentationReferences();

    void setDocumentationReferences(ArrayList<String> arrayList);
}
