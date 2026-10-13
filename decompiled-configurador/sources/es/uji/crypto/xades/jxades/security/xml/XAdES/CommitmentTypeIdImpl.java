package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CommitmentTypeIdImpl.class */
public class CommitmentTypeIdImpl implements CommitmentTypeId {
    private String qualifier;
    private String identifier;
    private String description;
    private ArrayList<String> documentationReferences;

    public CommitmentTypeIdImpl(String qualifier, String identifier, String description, ArrayList<String> documentationReferences) {
        this.qualifier = qualifier;
        this.identifier = identifier;
        this.description = description;
        this.documentationReferences = documentationReferences;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeId
    public String getQualifier() {
        return this.qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeId
    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeId
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeId
    public ArrayList<String> getDocumentationReferences() {
        return this.documentationReferences;
    }

    public void setDocumentationReferences(ArrayList<String> documentationReferences) {
        this.documentationReferences = documentationReferences;
    }
}
