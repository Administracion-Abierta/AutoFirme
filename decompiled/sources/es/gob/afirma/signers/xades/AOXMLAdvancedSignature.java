package es.gob.afirma.signers.xades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.keystores.AOCancelledSMOperationException;
import es.gob.afirma.core.keystores.AuthenticationException;
import es.gob.afirma.core.keystores.LockedKeyStoreException;
import es.gob.afirma.core.keystores.PinException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.Pkcs1Utils;
import es.gob.afirma.signers.xml.dereference.CustomUriDereferencer;
import es.gob.afirma.signers.xml.style.XmlStyle;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XMLAdvancedSignature;
import es.uji.crypto.xades.jxades.security.xml.XmlWrappedKeyInfo;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/AOXMLAdvancedSignature.class */
final class AOXMLAdvancedSignature extends XMLAdvancedSignature {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String SYSTEM_PROPERTY_SIGNATURE_PROVIDER = "org.jcp.xml.dsig.internal.dom.SignatureProvider";
    private static final String PROVIDER_NAME_SUNMSCAPI = "SunMSCAPI";
    private URIDereferencer uriDereferencer;
    private String canonicalizationMethod;
    private Element styleElement;
    private String styleType;
    private String styleEncoding;
    private String styleId;

    private AOXMLAdvancedSignature(XAdESBase xAdESBase) {
        super(xAdESBase);
        this.uriDereferencer = null;
        this.canonicalizationMethod = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
        this.styleElement = null;
        this.styleType = "text/xsl";
        this.styleEncoding = null;
        this.styleId = null;
    }

    void addStyleSheetEnvelopingOntoSignature(XmlStyle xmlStyle, String str) {
        this.styleElement = xmlStyle.getStyleElement();
        if (xmlStyle.getStyleType() != null) {
            this.styleType = xmlStyle.getStyleType();
        }
        this.styleId = str;
        this.styleEncoding = xmlStyle.getStyleEncoding();
    }

    void setCanonicalizationMethod(String str) {
        if (str != null) {
            this.canonicalizationMethod = str;
        }
    }

    private KeyInfo newKeyInfo(List<Certificate> list, String str, boolean z, boolean z2, boolean z3) throws KeyException {
        List<Certificate> escapedCertificates = EscapeHelper.getEscapedCertificates(list);
        KeyInfoFactory keyInfoFactory = getXMLSignatureFactory().getKeyInfoFactory();
        ArrayList arrayList = new ArrayList();
        if (!XmlWrappedKeyInfo.PUBLIC_KEY.equals(getXmlWrappedKeyInfo())) {
            Iterator<Certificate> it = escapedCertificates.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
        }
        PublicKey publicKey = escapedCertificates.get(0).getPublicKey();
        boolean zEqualsIgnoreCase = "EC".equalsIgnoreCase(publicKey.getAlgorithm());
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(keyInfoFactory.newX509Data(arrayList));
        if (z && !zEqualsIgnoreCase) {
            arrayList2.add(keyInfoFactory.newKeyValue(publicKey));
        }
        if (z2) {
            arrayList2.add(keyInfoFactory.newKeyName(EscapeHelper.escapeLdapName(((X509Certificate) escapedCertificates.get(0)).getSubjectX500Principal().toString())));
        }
        if (z3) {
            arrayList2.add(keyInfoFactory.newX509IssuerSerial(EscapeHelper.escapeLdapName(((X509Certificate) escapedCertificates.get(0)).getIssuerX500Principal().toString()), ((X509Certificate) escapedCertificates.get(0)).getSerialNumber()));
        }
        return keyInfoFactory.newKeyInfo(arrayList2, str);
    }

    void sign(List<Certificate> list, PrivateKey privateKey, String str, List<?> list2, String str2, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) throws GeneralSecurityException, MarshalException, XMLSignatureException, AOException {
        PrivateKey privateKeyEcBc2Jce;
        ArrayList arrayList = new ArrayList(list2);
        addXMLObject(marshalXMLSignature(this.xadesNamespace, this.signedPropertiesTypeUrl, str2, arrayList));
        XMLSignatureFactory xMLSignatureFactory = getXMLSignatureFactory();
        if (this.styleElement != null && this.styleId != null) {
            addXMLObject(xMLSignatureFactory.newXMLObject(Collections.singletonList(new DOMStructure(this.styleElement)), this.styleId, this.styleType, this.styleEncoding));
        }
        List<Reference> references = getReferences(arrayList);
        String keyInfoId = getKeyInfoId(str2);
        if (!z4) {
            references.add(xMLSignatureFactory.newReference("#" + keyInfoId, getDigestMethod()));
        }
        this.signature = xMLSignatureFactory.newXMLSignature(xMLSignatureFactory.newSignedInfo(xMLSignatureFactory.newCanonicalizationMethod(this.canonicalizationMethod, (C14NMethodParameterSpec) null), xMLSignatureFactory.newSignatureMethod(str, (SignatureMethodParameterSpec) null), references), newKeyInfo(list, keyInfoId, z, z2, z3), getXMLObjects(), getSignatureId(str2), getSignatureValueId(str2));
        if (privateKey instanceof BCECPrivateKey) {
            privateKeyEcBc2Jce = KeyUtil.ecBc2Jce((BCECPrivateKey) privateKey);
        } else {
            privateKeyEcBc2Jce = privateKey;
        }
        this.signContext = new DOMSignContext(privateKeyEcBc2Jce, this.baseElement);
        this.signContext.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", this.xades.getXmlSignaturePrefix());
        this.signContext.putNamespacePrefix(this.xadesNamespace, this.xades.getXadesPrefix());
        if (KeyUtil.isMsCapiPrivateKey(privateKeyEcBc2Jce)) {
            this.signContext.setProperty(SYSTEM_PROPERTY_SIGNATURE_PROVIDER, Security.getProvider(PROVIDER_NAME_SUNMSCAPI));
        }
        try {
            if (this.uriDereferencer != null) {
                this.signContext.setURIDereferencer(this.uriDereferencer);
            } else {
                this.signContext.setURIDereferencer(new CustomUriDereferencer());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se ha podido instalar un dereferenciador a medida para la firma, es posible que fallen las firmas de nodos concretos: " + e, (Throwable) e);
        }
        try {
            this.signature.sign(this.signContext);
            if (list != null && !list.isEmpty() && z5) {
                verifySignature(list.get(0).getPublicKey(), str);
            }
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
        } catch (Exception e3) {
            if ("es.gob.jmulticard.CancelledOperationException".equals(e3.getClass().getName())) {
                throw new AOCancelledSMOperationException("Cancelacion del dialogo de JMulticard");
            }
            throw e3;
        }
    }

    public void setUriDereferencer(URIDereferencer uRIDereferencer) {
        this.uriDereferencer = uRIDereferencer;
    }

    public static AOXMLAdvancedSignature newInstance(XAdESBase xAdESBase) throws GeneralSecurityException {
        AOXMLAdvancedSignature aOXMLAdvancedSignature = new AOXMLAdvancedSignature(xAdESBase);
        aOXMLAdvancedSignature.setDigestMethod(xAdESBase.getDigestMethod());
        aOXMLAdvancedSignature.setXadesNamespace(xAdESBase.getXadesNamespace());
        return aOXMLAdvancedSignature;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    private void verifySignature(PublicKey publicKey, String str) throws XMLSignatureException {
        Signature signature;
        byte[] value = this.signature.getSignatureValue().getValue();
        String strComposeSignatureAlgorithmName = AOSignConstants.composeSignatureAlgorithmName(str, publicKey.getAlgorithm());
        byte[] bArrEncodeSignature = value;
        try {
            if (strComposeSignatureAlgorithmName.endsWith("withECDSA") || strComposeSignatureAlgorithmName.endsWith("withDSA")) {
                try {
                    signature = Signature.getInstance(strComposeSignatureAlgorithmName + "inP1363Format");
                } catch (Exception e) {
                    signature = Signature.getInstance(strComposeSignatureAlgorithmName);
                    bArrEncodeSignature = Pkcs1Utils.encodeSignature(value);
                }
            } else {
                signature = Signature.getInstance(strComposeSignatureAlgorithmName);
            }
            signature.initVerify(publicKey);
            InputStream canonicalizedData = this.signature.getSignedInfo().getCanonicalizedData();
            Throwable th = null;
            try {
                try {
                    byte[] dataFromInputStream = AOUtil.getDataFromInputStream(canonicalizedData);
                    if (canonicalizedData != null) {
                        if (0 != 0) {
                            try {
                                canonicalizedData.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            canonicalizedData.close();
                        }
                    }
                    signature.update(dataFromInputStream);
                    if (!signature.verify(bArrEncodeSignature)) {
                        throw new XMLSignatureException("El PKCS#1 de la firma no se ha generado con la clave publica indicada");
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (canonicalizedData != null) {
                    if (th != null) {
                        try {
                            canonicalizedData.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        canonicalizedData.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e2) {
            throw new XMLSignatureException("Error al verificar el PKCS#1 de la firma", e2);
        }
    }
}
