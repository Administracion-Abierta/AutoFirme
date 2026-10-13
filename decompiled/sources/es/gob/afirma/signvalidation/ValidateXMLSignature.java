package es.gob.afirma.signvalidation;

import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signers.xml.dereference.CustomUriDereferencer;
import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/ValidateXMLSignature.class */
public final class ValidateXMLSignature extends SignValider {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    @Override // es.gob.afirma.signvalidation.SignValider
    public List<SignValidity> validate(byte[] bArr) {
        return validate(bArr, true);
    }

    @Override // es.gob.afirma.signvalidation.SignValider
    public List<SignValidity> validate(byte[] bArr, Properties properties) {
        return validate(bArr, true);
    }

    @Override // es.gob.afirma.signvalidation.SignValider
    public List<SignValidity> validate(byte[] bArr, boolean z) {
        ArrayList arrayList = new ArrayList();
        try {
            NodeList elementsByTagNameNS = SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(bArr)).getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
            if (elementsByTagNameNS.getLength() == 0) {
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_SIGN));
                return arrayList;
            }
            boolean z2 = false;
            boolean z3 = false;
            for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
                try {
                    DOMValidateContext dOMValidateContext = new DOMValidateContext(new KeyValueKeySelector(), elementsByTagNameNS.item(i));
                    dOMValidateContext.setURIDereferencer(new CustomUriDereferencer());
                    XMLSignature xMLSignatureUnmarshalXMLSignature = Utils.getDOMFactory().unmarshalXMLSignature(dOMValidateContext);
                    if (!xMLSignatureUnmarshalXMLSignature.validate(dOMValidateContext) && !z2) {
                        LOGGER.info("La firma es invalida");
                        arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_MATCH_DATA));
                        z2 = true;
                    }
                    if (!xMLSignatureUnmarshalXMLSignature.getSignatureValue().validate(dOMValidateContext) && !z2) {
                        LOGGER.info("El valor de la firma es invalido");
                        arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_MATCH_DATA));
                        z2 = true;
                    }
                    for (Reference reference : xMLSignatureUnmarshalXMLSignature.getSignedInfo().getReferences()) {
                        if (!reference.validate(dOMValidateContext)) {
                            LOGGER.info("La referencia '" + reference.getURI() + "' de la firma es invalida");
                            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_MATCH_DATA));
                        }
                    }
                    if (z) {
                        for (X509Data x509Data : XMLSignatureFactory.getInstance("DOM").unmarshalXMLSignature(dOMValidateContext).getKeyInfo().getContent()) {
                            if (x509Data instanceof X509Data) {
                                List content = x509Data.getContent();
                                for (int i2 = 0; i2 < content.size(); i2++) {
                                    if (content.get(i2) instanceof X509Certificate) {
                                        try {
                                            ((X509Certificate) content.get(i2)).checkValidity();
                                        } catch (CertificateExpiredException e) {
                                            if (!z3) {
                                                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CERTIFICATE_EXPIRED, e));
                                                z3 = true;
                                            }
                                        } catch (CertificateNotYetValidException e2) {
                                            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CERTIFICATE_NOT_VALID_YET, e2));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    String strResolveSignerXAdESFormat = SignatureFormatDetectorXades.resolveSignerXAdESFormat((Element) elementsByTagNameNS.item(i));
                    if (!ISignatureFormatDetector.FORMAT_XADES_B_LEVEL.equals(strResolveSignerXAdESFormat) && !ISignatureFormatDetector.FORMAT_UNRECOGNIZED.equals(strResolveSignerXAdESFormat) && !ISignatureFormatDetector.FORMAT_XADES_B_B_LEVEL.equals(strResolveSignerXAdESFormat) && !ISignatureFormatDetector.FORMAT_XADES_BES.equals(strResolveSignerXAdESFormat) && !ISignatureFormatDetector.FORMAT_XADES_EPES.equals(strResolveSignerXAdESFormat)) {
                        arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.SIGN_PROFILE_NOT_CHECKED));
                    }
                } catch (XMLSignatureException e3) {
                    LOGGER.log(Level.WARNING, "La firma no esta formada correctamente: " + e3, (Throwable) e3);
                    arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.BAD_BUILD_SIGN, e3));
                } catch (Exception e4) {
                    LOGGER.log(Level.WARNING, "No se ha podido validar la firma: " + e4, (Throwable) e4);
                    arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.UNKOWN_ERROR, e4));
                }
            }
            List<SignValidity> listCheckLongStandingValiditySign = checkLongStandingValiditySign(arrayList);
            if (listCheckLongStandingValiditySign.size() == 0) {
                listCheckLongStandingValiditySign.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.OK, null));
            }
            return listCheckLongStandingValiditySign;
        } catch (Exception e5) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_SIGN));
            return arrayList;
        }
    }

    public static List<SignValidity> validateSign(Element element, String str, boolean z) {
        ArrayList arrayList = new ArrayList();
        if (!z) {
            boolean z2 = true;
            try {
                DOMValidateContext dOMValidateContext = new DOMValidateContext(new KeyValueKeySelector(), element);
                dOMValidateContext.setURIDereferencer(new CustomUriDereferencer());
                boolean z3 = false;
                XMLSignature xMLSignatureUnmarshalXMLSignature = Utils.getDOMFactory().unmarshalXMLSignature(dOMValidateContext);
                if (!xMLSignatureUnmarshalXMLSignature.validate(dOMValidateContext)) {
                    LOGGER.info("La firma es invalida");
                    z2 = false;
                    z3 = true;
                }
                if (!xMLSignatureUnmarshalXMLSignature.getSignatureValue().validate(dOMValidateContext)) {
                    LOGGER.info("El valor de la firma es invalido");
                    z2 = false;
                    z3 = true;
                }
                for (Reference reference : xMLSignatureUnmarshalXMLSignature.getSignedInfo().getReferences()) {
                    if (!reference.validate(dOMValidateContext)) {
                        LOGGER.info("La referencia '" + reference.getURI() + "' de la firma es invalida");
                        z2 = false;
                        z3 = true;
                    }
                }
                if (z3) {
                    arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_MATCH_DATA));
                }
            } catch (XMLSignatureException e) {
                LOGGER.log(Level.WARNING, "La firma no esta formada correctamente: " + e, (Throwable) e);
                z2 = false;
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.BAD_BUILD_SIGN, e));
            } catch (Exception e2) {
                LOGGER.log(Level.WARNING, "No se ha podido validar la firma: " + e2, (Throwable) e2);
                z2 = false;
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.UNKOWN_ERROR, e2));
            }
            if (!ISignatureFormatDetector.FORMAT_XADES_B_LEVEL.equals(str) && !ISignatureFormatDetector.FORMAT_UNRECOGNIZED.equals(str) && !ISignatureFormatDetector.FORMAT_XADES_B_B_LEVEL.equals(str) && !ISignatureFormatDetector.FORMAT_XADES_BES.equals(str) && !ISignatureFormatDetector.FORMAT_XADES_EPES.equals(str)) {
                z2 = false;
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.SIGN_PROFILE_NOT_CHECKED));
            }
            if (z2) {
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.OK, null));
            }
        } else {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.CANT_VALIDATE_EXTERNALLY_DETACHED));
        }
        return arrayList;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/ValidateXMLSignature$KeyValueKeySelector.class */
    static final class KeyValueKeySelector extends KeySelector {
        KeyValueKeySelector() {
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.KeySelectorException */
        public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod algorithmMethod, XMLCryptoContext xMLCryptoContext) throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Objeto KeyInfo nulo");
            }
            List content = keyInfo.getContent();
            try {
                xMLCryptoContext.setURIDereferencer(new CustomUriDereferencer());
            } catch (Exception e) {
                ValidateXMLSignature.LOGGER.warning("No se ha podido instalar un dereferenciador a medida: " + e);
            }
            for (int i = 0; i < content.size(); i++) {
                X509Data x509Data = (XMLStructure) content.get(i);
                if (x509Data instanceof KeyValue) {
                    try {
                        return new SimpleKeySelectorResult(((KeyValue) x509Data).getPublicKey());
                    } catch (KeyException e2) {
                        throw new KeySelectorException(e2);
                    }
                }
                if (x509Data instanceof X509Data) {
                    for (Object obj : x509Data.getContent()) {
                        if (obj instanceof Certificate) {
                            return new SimpleKeySelectorResult(((Certificate) obj).getPublicKey());
                        }
                    }
                }
            }
            throw new KeySelectorException("No se ha encontrado la clave publica dentro del XML firmado");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/ValidateXMLSignature$SimpleKeySelectorResult.class */
    private static final class SimpleKeySelectorResult implements KeySelectorResult {
        private final PublicKey pk;

        SimpleKeySelectorResult(PublicKey publicKey) {
            this.pk = publicKey;
        }

        public Key getKey() {
            return this.pk;
        }
    }
}
