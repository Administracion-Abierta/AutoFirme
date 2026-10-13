package es.uji.crypto.xades.jxades.security.xml.XAdES;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/DataObjectFormat.class */
public interface DataObjectFormat {
    String getDescription();

    ObjectIdentifier getObjectIdentifier();

    String getMimeType();

    String getEncoding();

    String getObjectReference();
}
