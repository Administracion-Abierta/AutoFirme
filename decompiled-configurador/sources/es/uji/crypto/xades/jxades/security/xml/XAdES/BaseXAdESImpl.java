package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.List;
import javax.xml.crypto.MarshalException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/BaseXAdESImpl.class */
public abstract class BaseXAdESImpl implements XAdESBase {
    protected abstract void marshalQualifyingProperties(QualifyingProperties qualifyingProperties, String str, List list) throws MarshalException;
}
