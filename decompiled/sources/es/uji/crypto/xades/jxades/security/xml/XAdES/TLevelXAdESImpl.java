package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/TLevelXAdESImpl.class */
public class TLevelXAdESImpl extends BLevelXAdESImpl implements XAdES_T_Level {
    public TLevelXAdESImpl(Document document, Element baseElement, boolean readOnlyMode, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix, String digestMethod) {
        super(document, baseElement, readOnlyMode, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithSignatureTimeStamp
    public List<SignatureTimeStamp> getSignatureTimeStamps() {
        return (List) this.data.get(XAdES.Element.SIGNATURE_TIME_STAMP);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithSignatureTimeStamp
    public void setSignatureTimeStamps(List<SignatureTimeStamp> signatureTimeStamps) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (signatureTimeStamps != null && signatureTimeStamps.size() > 0) {
            this.data.put(XAdES.Element.SIGNATURE_TIME_STAMP, signatureTimeStamps);
        } else {
            this.data.remove(XAdES.Element.SIGNATURE_TIME_STAMP);
        }
    }
}
