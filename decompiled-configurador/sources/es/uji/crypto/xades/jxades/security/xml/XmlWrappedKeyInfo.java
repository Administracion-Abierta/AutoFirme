package es.uji.crypto.xades.jxades.security.xml;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XmlWrappedKeyInfo.class */
public enum XmlWrappedKeyInfo {
    PUBLIC_KEY("PublicKey"),
    CERTIFICATE("Certificate");

    private String wrappedKeyInfoName;

    XmlWrappedKeyInfo(String wrappedKeyInfoName) {
        this.wrappedKeyInfoName = wrappedKeyInfoName;
    }

    public String getWrappedKeyInfoName() {
        return this.wrappedKeyInfoName;
    }

    @Override // java.lang.Enum
    public String toString() {
        return getWrappedKeyInfoName();
    }
}
