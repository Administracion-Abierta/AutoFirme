package es.gob.afirma.signers.xades.asic;

import es.gob.afirma.signers.xml.dereference.CustomUriDereferencer;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/asic/UriAndDataDereferencer.class */
public class UriAndDataDereferencer extends CustomUriDereferencer {
    private final Map<String, byte[]> externalDatas = new HashMap();

    public UriAndDataDereferencer(Map<String, byte[]> map) {
        if (map != null) {
            for (String str : (String[]) map.keySet().toArray(new String[0])) {
                this.externalDatas.put(str, map.get(str));
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.URIReferenceException */
    @Override // es.gob.afirma.signers.xml.dereference.CustomUriDereferencer
    public Data dereference(URIReference uRIReference, XMLCryptoContext xMLCryptoContext) throws URIReferenceException {
        try {
            return super.dereference(uRIReference, xMLCryptoContext);
        } catch (Exception e) {
            byte[] bArr = this.externalDatas.get(uRIReference.getURI());
            if (bArr == null) {
                throw new URIReferenceException("No se ha podido dereferenciar la URI ni hay datos asociados a la misma", e);
            }
            return new OctetStreamData(new ByteArrayInputStream(bArr));
        }
    }
}
