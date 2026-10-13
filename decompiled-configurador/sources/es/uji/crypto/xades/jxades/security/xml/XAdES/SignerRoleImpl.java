package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignerRoleImpl.class */
public class SignerRoleImpl implements SignerRole {
    private ArrayList<String> claimedRole = new ArrayList<>();
    private ArrayList<String> certifiedRole = new ArrayList<>();

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRole
    public ArrayList<String> getCertifiedRole() {
        return this.certifiedRole;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRole
    public void setCertifiedRole(ArrayList<String> certifiedRole) {
        this.certifiedRole = certifiedRole;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRole
    public ArrayList<String> getClaimedRole() {
        return this.claimedRole;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRole
    public void setClaimedRole(ArrayList<String> claimedRole) {
        this.claimedRole = claimedRole;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRole
    public void addClaimedRole(String role) {
        this.claimedRole.add(role);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRole
    public void addCertifiedRole(String role) {
        this.certifiedRole.add(role);
    }
}
