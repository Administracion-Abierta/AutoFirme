package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignerRole.class */
public interface SignerRole {
    ArrayList<String> getCertifiedRole();

    void setCertifiedRole(ArrayList<String> arrayList);

    ArrayList<String> getClaimedRole();

    void setClaimedRole(ArrayList<String> arrayList);

    void addClaimedRole(String str);

    void addCertifiedRole(String str);
}
