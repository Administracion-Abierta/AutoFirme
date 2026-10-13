package es.uji.crypto.xades.jxades.security.xml.XAdES;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/Signer.class */
public interface Signer {
    String getUserId();

    void setUserId(String str);

    String getUsername();

    void setUsername(String str);

    String getPersonName();

    void setPersonName(String str);

    RoleType getRoleType();

    void setRoleType(RoleType roleType);
}
