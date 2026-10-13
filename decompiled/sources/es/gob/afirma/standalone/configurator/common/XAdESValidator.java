package es.gob.afirma.standalone.configurator.common;

import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signers.xml.dereference.CustomUriDereferencer;
import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/configurator/common/XAdESValidator.class */
public class XAdESValidator {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    public static void validate(byte[] bArr, boolean z) throws InvalidSignatureException, CertificateException, AOInvalidFormatException {
        try {
            NodeList elementsByTagNameNS = SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(bArr)).getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
            if (elementsByTagNameNS.getLength() == 0) {
                throw new AOInvalidFormatException("No se encontro la firma de los datos");
            }
            for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
                try {
                    DOMValidateContext dOMValidateContext = new DOMValidateContext(new KeyValueKeySelector(), elementsByTagNameNS.item(i));
                    XMLSignature xMLSignatureUnmarshalXMLSignature = Utils.getDOMFactory().unmarshalXMLSignature(dOMValidateContext);
                    if (!xMLSignatureUnmarshalXMLSignature.validate(dOMValidateContext)) {
                        throw new InvalidSignatureException("La firma es invalida");
                    }
                    if (!xMLSignatureUnmarshalXMLSignature.getSignatureValue().validate(dOMValidateContext)) {
                        throw new InvalidSignatureException("El valor de la firma es invalido");
                    }
                    if (z) {
                        for (X509Data x509Data : XMLSignatureFactory.getInstance("DOM").unmarshalXMLSignature(dOMValidateContext).getKeyInfo().getContent()) {
                            if (x509Data instanceof X509Data) {
                                List content = x509Data.getContent();
                                for (int i2 = 0; i2 < content.size(); i2++) {
                                    if (content.get(i2) instanceof X509Certificate) {
                                        ((X509Certificate) content.get(i2)).checkValidity();
                                    }
                                }
                            }
                        }
                    }
                    for (Reference reference : xMLSignatureUnmarshalXMLSignature.getSignedInfo().getReferences()) {
                        if (!reference.validate(dOMValidateContext)) {
                            throw new InvalidSignatureException("La referencia '" + reference.getURI() + "' de la firma es invalida");
                        }
                    }
                } catch (Exception e) {
                    throw new InvalidSignatureException("No se ha podido validar la firma", e);
                }
            }
        } catch (Exception e2) {
            throw new AOInvalidFormatException("La firma no es XML", e2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/configurator/common/XAdESValidator$KeyValueKeySelector.class */
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
                XAdESValidator.LOGGER.warning("No se ha podido instalar un dereferenciador a medida: " + e);
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

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/configurator/common/XAdESValidator$SimpleKeySelectorResult.class */
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
