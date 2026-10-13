package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CommitmentTypeIndication.class */
public interface CommitmentTypeIndication {
    void setCommitmentTypeId(CommitmentTypeId commitmentTypeId);

    void setObjectReference(String str);

    void setCommitmentTypeQualifiers(ArrayList<String> arrayList);

    CommitmentTypeId getCommitmentTypeId();

    String getObjectReference();

    ArrayList<String> getCommitmentTypeQualifiers();
}
