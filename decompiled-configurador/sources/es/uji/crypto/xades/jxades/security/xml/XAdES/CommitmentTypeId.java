package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CommitmentTypeId.class */
public interface CommitmentTypeId {
    String getIdentifier();

    String getQualifier();

    String getDescription();

    ArrayList<String> getDocumentationReferences();
}
