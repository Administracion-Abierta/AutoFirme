package es.gob.afirma.signers.odf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import nu.xom.canonical.Canonicalizer;
import nu.xom.converters.DOMConverter;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/odf/OdfXmlUtil.class */
final class OdfXmlUtil {
    private OdfXmlUtil() {
    }

    static byte[] canonicalizeXml(Element element, String str) throws IOException {
        nu.xom.Element elementConvert = DOMConverter.convert(element);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new Canonicalizer(byteArrayOutputStream, str).write(elementConvert);
        return byteArrayOutputStream.toByteArray();
    }
}
