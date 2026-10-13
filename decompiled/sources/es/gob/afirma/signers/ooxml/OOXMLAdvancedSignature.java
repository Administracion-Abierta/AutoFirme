package es.gob.afirma.signers.ooxml;

import es.gob.afirma.core.keystores.AOCancelledSMOperationException;
import es.gob.afirma.core.keystores.AuthenticationException;
import es.gob.afirma.core.keystores.LockedKeyStoreException;
import es.gob.afirma.core.keystores.PinException;
import es.gob.afirma.signers.xml.Utils;
import es.uji.crypto.xades.jxades.security.xml.WrappedKeyStorePlace;
import es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifierImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XAdES_EPES;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XMLAdvancedSignature;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBasicAttributes;
import es.uji.crypto.xades.jxades.security.xml.XmlWrappedKeyInfo;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/OOXMLAdvancedSignature.class */
final class OOXMLAdvancedSignature extends XMLAdvancedSignature {
    private final byte[] ooXmlDocument;

    private OOXMLAdvancedSignature(XAdES_EPES xAdES_EPES, byte[] bArr) {
        super(xAdES_EPES);
        this.ooXmlDocument = (byte[]) bArr.clone();
    }

    static OOXMLAdvancedSignature newInstance(XAdES_EPES xAdES_EPES, byte[] bArr) throws GeneralSecurityException {
        xAdES_EPES.setSignaturePolicyIdentifier(new SignaturePolicyIdentifierImpl(true));
        OOXMLAdvancedSignature oOXMLAdvancedSignature = new OOXMLAdvancedSignature(xAdES_EPES, bArr);
        oOXMLAdvancedSignature.setDigestMethod(xAdES_EPES.getDigestMethod());
        oOXMLAdvancedSignature.setXadesNamespace(xAdES_EPES.getXadesNamespace());
        return oOXMLAdvancedSignature;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    void sign(X509Certificate[] x509CertificateArr, PrivateKey privateKey, String str, List<?> list, String str2) throws Exception {
        ArrayList arrayList = new ArrayList(list);
        if (WrappedKeyStorePlace.SIGNING_CERTIFICATE_PROPERTY.equals(getWrappedKeyStorePlace()) && x509CertificateArr != null && x509CertificateArr.length > 0 && (this.xades instanceof XadesWithBasicAttributes)) {
            ((XadesWithBasicAttributes) this.xades).setSigningCertificate(x509CertificateArr[0]);
        }
        addXMLObject(marshalXMLSignature(this.xadesNamespace, this.signedPropertiesTypeUrl, str2, arrayList, Arrays.asList(Utils.getDOMFactory().newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (TransformParameterSpec) null))));
        XMLSignatureFactory xMLSignatureFactory = getXMLSignatureFactory();
        List<Reference> references = getReferences(arrayList);
        String keyInfoId = getKeyInfoId(str2);
        references.add(xMLSignatureFactory.newReference("#" + keyInfoId, getDigestMethod()));
        this.signature = xMLSignatureFactory.newXMLSignature(xMLSignatureFactory.newSignedInfo(xMLSignatureFactory.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (C14NMethodParameterSpec) null), xMLSignatureFactory.newSignatureMethod(str, (SignatureMethodParameterSpec) null), references), newKeyInfo(x509CertificateArr, keyInfoId), getXMLObjects(), getSignatureId(str2), getSignatureValueId(str2));
        this.signContext = new DOMSignContext(privateKey, this.baseElement != null ? this.baseElement : getBaseDocument());
        this.signContext.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", this.xades.getXmlSignaturePrefix());
        this.signContext.putNamespacePrefix(this.xadesNamespace, this.xades.getXadesPrefix());
        this.signContext.setURIDereferencer(new OOXMLURIDereferencer(this.ooXmlDocument));
        try {
            this.signature.sign(this.signContext);
        } catch (Exception e) {
            if ("es.gob.jmulticard.CancelledOperationException".equals(e.getClass().getName())) {
                throw new AOCancelledSMOperationException("Cancelacion del dialogo de JMulticard");
            }
            throw e;
        } catch (XMLSignatureException e2) {
            Throwable cause = e2.getCause() != null ? e2.getCause() : null;
            if (cause != null && "es.gob.jmulticard.jse.provider.SignatureAuthException".equals(cause.getClass().getName())) {
                String name = cause.getCause() != null ? cause.getCause().getClass().getName() : null;
                if ("es.gob.jmulticard.card.AuthenticationModeLockedException".equals(name)) {
                    throw new LockedKeyStoreException("El almacen de claves esta bloqueado", e2);
                }
                if ("es.gob.jmulticard.card.BadPinException".equals(name)) {
                    throw new PinException("La contrasena del almacen o certificado es incorrecta", e2);
                }
                throw new AuthenticationException("Ocurrio un error de autenticacion al utilizar la clave de firma", cause);
            }
            throw e2;
        }
    }

    private KeyInfo newKeyInfo(X509Certificate[] x509CertificateArr, String str) throws KeyException {
        KeyInfoFactory keyInfoFactory = getXMLSignatureFactory().getKeyInfoFactory();
        ArrayList arrayList = new ArrayList();
        if (!XmlWrappedKeyInfo.PUBLIC_KEY.equals(getXmlWrappedKeyInfo()) && x509CertificateArr != null) {
            for (X509Certificate x509Certificate : x509CertificateArr) {
                arrayList.add(x509Certificate);
            }
        }
        ArrayList arrayList2 = new ArrayList();
        if (x509CertificateArr != null) {
            arrayList2.add(keyInfoFactory.newKeyValue(x509CertificateArr[0].getPublicKey()));
            arrayList2.add(keyInfoFactory.newX509Data(arrayList));
        }
        return keyInfoFactory.newKeyInfo(arrayList2, str);
    }
}
