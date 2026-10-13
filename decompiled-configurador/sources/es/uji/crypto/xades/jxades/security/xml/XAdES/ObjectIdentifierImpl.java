package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/ObjectIdentifierImpl.class */
public class ObjectIdentifierImpl implements ObjectIdentifier {
    private String qualifier;
    private String identifier;
    private String description;
    private ArrayList<String> documentationReferences;

    public ObjectIdentifierImpl(String qualifier, String identifier, String description, ArrayList<String> documentationReferences) {
        this.qualifier = qualifier;
        this.identifier = identifier;
        this.description = description;
        this.documentationReferences = documentationReferences;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifier
    public String getQualifier() {
        return this.qualifier;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifier
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifier
    public String getIdentifier() {
        return this.identifier;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifier
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifier
    public String getDescription() {
        return this.description;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifier
    public void setDescription(String description) {
        this.description = description;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifier
    public ArrayList<String> getDocumentationReferences() {
        return this.documentationReferences;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifier
    public void setDocumentationReferences(ArrayList<String> documentationReferences) {
        this.documentationReferences = documentationReferences;
    }
}
