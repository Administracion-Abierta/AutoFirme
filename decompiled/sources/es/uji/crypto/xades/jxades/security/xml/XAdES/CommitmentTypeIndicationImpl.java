package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CommitmentTypeIndicationImpl.class */
public class CommitmentTypeIndicationImpl implements CommitmentTypeIndication {
    private CommitmentTypeId commitmentTypeId;
    private String objectReference;
    private ArrayList<String> commitmentTypeQualifiers;

    public CommitmentTypeIndicationImpl() {
    }

    public CommitmentTypeIndicationImpl(CommitmentTypeId commitmentTypeId, String objectReference, ArrayList<String> commitmentTypeQualifiers) {
        this.commitmentTypeId = commitmentTypeId;
        this.objectReference = objectReference;
        this.commitmentTypeQualifiers = commitmentTypeQualifiers;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIndication
    public CommitmentTypeId getCommitmentTypeId() {
        return this.commitmentTypeId;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIndication
    public void setCommitmentTypeId(CommitmentTypeId commitmentTypeId) {
        this.commitmentTypeId = commitmentTypeId;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIndication
    public String getObjectReference() {
        return this.objectReference;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIndication
    public void setObjectReference(String objectReference) {
        this.objectReference = objectReference;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIndication
    public ArrayList<String> getCommitmentTypeQualifiers() {
        return this.commitmentTypeQualifiers;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIndication
    public void setCommitmentTypeQualifiers(ArrayList<String> commitmentTypeQualifiers) {
        this.commitmentTypeQualifiers = commitmentTypeQualifiers;
    }
}
