package es.gob.afirma.signvalidation;

import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.xades.AOFacturaESigner;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/SignValiderFactory.class */
public final class SignValiderFactory {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String SIGNER_VALIDER_CLASS_BINARY = "es.gob.afirma.signvalidation.ValidateBinarySignature";
    private static final String SIGNER_VALIDER_CLASS_PDF = "es.gob.afirma.signvalidation.ValidatePdfSignature";
    private static final String SIGNER_VALIDER_CLASS_XML = "es.gob.afirma.signvalidation.ValidateXMLSignature";

    private SignValiderFactory() {
    }

    public static SignValider getSignValider(byte[] bArr) throws IllegalArgumentException {
        String str;
        if (bArr == null || bArr.length < 1) {
            throw new IllegalArgumentException("No se han indicado datos de firma");
        }
        if (DataAnalizerUtil.isPDF(bArr)) {
            str = SIGNER_VALIDER_CLASS_PDF;
        } else if (DataAnalizerUtil.isFacturae(bArr) || DataAnalizerUtil.isXML(bArr)) {
            str = SIGNER_VALIDER_CLASS_XML;
        } else {
            if (DataAnalizerUtil.isOOXML(bArr)) {
                LOGGER.warning("No hay un validador para OOXML");
                return null;
            }
            if (DataAnalizerUtil.isODF(bArr)) {
                LOGGER.warning("No hay un validador para ODF");
                return null;
            }
            str = SIGNER_VALIDER_CLASS_BINARY;
        }
        try {
            return (SignValider) Class.forName(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            LOGGER.severe("No se ha podido instanciar el validador '" + str + "': " + e);
            return null;
        }
    }

    public static SignValider getSignValider(AOSigner aOSigner) throws IllegalArgumentException {
        String str = null;
        if (aOSigner instanceof AOCAdESSigner) {
            str = SIGNER_VALIDER_CLASS_BINARY;
        } else if ((aOSigner instanceof AOXAdESSigner) || (aOSigner instanceof AOFacturaESigner)) {
            str = SIGNER_VALIDER_CLASS_XML;
        } else if (aOSigner instanceof AOPDFSigner) {
            str = SIGNER_VALIDER_CLASS_PDF;
        }
        SignValider signValider = null;
        if (str != null) {
            try {
                signValider = (SignValider) Class.forName(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                LOGGER.severe("No se ha podido instanciar el validador '" + str + "': " + e);
            }
        }
        return signValider;
    }
}
