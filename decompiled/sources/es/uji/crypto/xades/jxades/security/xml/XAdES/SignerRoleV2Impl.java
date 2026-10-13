package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignerRoleV2Impl.class */
public class SignerRoleV2Impl implements SignerRoleV2 {
    private ArrayList<String> claimedRoles = new ArrayList<>();
    private ArrayList<String> certifiedRolesV2 = new ArrayList<>();
    private ArrayList<String> signedAssertions = new ArrayList<>();

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleV2
    public ArrayList<String> getClaimedRoles() {
        return this.claimedRoles;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleV2
    public void setClaimedRoles(ArrayList<String> claimedRole) {
        this.claimedRoles = claimedRole;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleV2
    public void addClaimedRole(String role) {
        this.claimedRoles.add(role);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleV2
    public ArrayList<String> getCertifiedRolesV2() {
        return this.certifiedRolesV2;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleV2
    public void setCertifiedRolesV2(ArrayList<String> certifiedRole) {
        this.certifiedRolesV2 = certifiedRole;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleV2
    public void addCertifiedRoleV2(String role) {
        this.certifiedRolesV2.add(role);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleV2
    public ArrayList<String> getSignedAssertions() {
        return this.signedAssertions;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignerRoleV2
    public void setSignedAssertions(ArrayList<String> signedAssertions) {
        this.signedAssertions = signedAssertions;
    }
}
