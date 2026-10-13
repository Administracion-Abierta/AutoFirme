package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignatureTimeStamp.class */
public interface SignatureTimeStamp {
    byte[] generateEncapsulatedTimeStamp(Document document, String str) throws NoSuchAlgorithmException, SignatureException, IOException;
}
