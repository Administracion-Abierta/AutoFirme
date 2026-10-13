package es.uji.crypto.xades.jxades.security.xml.XAdES;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignerImpl.class */
class SignerImpl implements Signer {
    private String userId;
    private String username;
    private String personName;
    private RoleType roleType;

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.Signer
    public String getUserId() {
        return this.userId;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.Signer
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.Signer
    public String getUsername() {
        return this.username;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.Signer
    public void setUsername(String username) {
        this.username = username;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.Signer
    public String getPersonName() {
        return this.personName;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.Signer
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.Signer
    public RoleType getRoleType() {
        return this.roleType;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.Signer
    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
