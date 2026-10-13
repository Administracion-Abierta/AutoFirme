package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/AllDataObjectsTimeStamp.class */
public interface AllDataObjectsTimeStamp {
    byte[] generateEncapsulatedTimeStamp(Document document, String str) throws NoSuchAlgorithmException, SignatureException, IOException;
}
