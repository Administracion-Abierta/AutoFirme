package es.gob.afirma.signers.ooxml;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.signers.xml.Utils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/OOXMLURIDereferencer.class */
public final class OOXMLURIDereferencer implements URIDereferencer {
    private final byte[] ooxml;
    private final URIDereferencer baseUriDereferencer;

    OOXMLURIDereferencer(byte[] bArr) {
        if (null == bArr) {
            throw new IllegalArgumentException("El OOXML es nulo");
        }
        this.baseUriDereferencer = Utils.getDOMFactory().getURIDereferencer();
        this.ooxml = (byte[]) bArr.clone();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.URIReferenceException */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x00f3: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:43:0x00f3 */
    /* JADX WARN: Type inference failed for: r10v0, types: [java.io.InputStream, java.io.UnsupportedEncodingException, java.lang.Object] */
    public Data dereference(URIReference uRIReference, XMLCryptoContext xMLCryptoContext) throws URIReferenceException {
        Throwable th;
        if (null == uRIReference) {
            throw new IllegalArgumentException("La referencia no puede ser nula");
        }
        if (null == xMLCryptoContext) {
            throw new IllegalArgumentException("El contexto de firma no puede ser nulo");
        }
        String uri = uRIReference.getURI();
        try {
            uri = URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger("es.gob.afirma").warning("No se puede decodificar la URI '" + uri + "': " + ((Object) e));
        }
        try {
            try {
                InputStream inputStreamFindDataInputStream = findDataInputStream(uri);
                Throwable th2 = null;
                try {
                    if (null == inputStreamFindDataInputStream) {
                        Data dataDereference = this.baseUriDereferencer.dereference(uRIReference, xMLCryptoContext);
                        if (inputStreamFindDataInputStream != null) {
                            if (0 != 0) {
                                try {
                                    inputStreamFindDataInputStream.close();
                                } catch (Throwable th3) {
                                    th2.addSuppressed(th3);
                                }
                            } else {
                                inputStreamFindDataInputStream.close();
                            }
                        }
                        return dataDereference;
                    }
                    byte[] dataFromInputStream = AOUtil.getDataFromInputStream(inputStreamFindDataInputStream);
                    inputStreamFindDataInputStream.close();
                    OctetStreamData octetStreamData = new OctetStreamData(new ByteArrayInputStream(dataFromInputStream), uri, (String) null);
                    if (inputStreamFindDataInputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStreamFindDataInputStream.close();
                            } catch (Throwable th4) {
                                th2.addSuppressed(th4);
                            }
                        } else {
                            inputStreamFindDataInputStream.close();
                        }
                    }
                    return octetStreamData;
                } catch (Throwable th5) {
                    throw th5;
                }
            } catch (IOException e2) {
                throw new URIReferenceException("Error de I/O: " + e2, e2);
            }
        } catch (Throwable th6) {
            if (e != 0) {
                if (th != null) {
                    try {
                        e.close();
                    } catch (Throwable th7) {
                        th.addSuppressed(th7);
                    }
                } else {
                    e.close();
                }
            }
            throw th6;
        }
        throw new URIReferenceException("Error de I/O: " + e2, e2);
    }

    private InputStream findDataInputStream(String str) throws IOException {
        String strSubstring;
        ZipEntry nextEntry;
        if (str.startsWith("/")) {
            strSubstring = str.substring(1);
        } else {
            strSubstring = str;
        }
        if (-1 != strSubstring.indexOf(63)) {
            strSubstring = strSubstring.substring(0, strSubstring.indexOf(63));
        }
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(this.ooxml));
        do {
            nextEntry = zipInputStream.getNextEntry();
            if (null == nextEntry) {
                return null;
            }
        } while (!nextEntry.getName().equals(strSubstring));
        return zipInputStream;
    }
}
