package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignerRoleV2.class */
public interface SignerRoleV2 {
    ArrayList<String> getClaimedRoles();

    void setClaimedRoles(ArrayList<String> arrayList);

    void addClaimedRole(String str);

    ArrayList<String> getCertifiedRolesV2();

    void setCertifiedRolesV2(ArrayList<String> arrayList);

    void addCertifiedRoleV2(String str);

    ArrayList<String> getSignedAssertions();

    void setSignedAssertions(ArrayList<String> arrayList);
}
