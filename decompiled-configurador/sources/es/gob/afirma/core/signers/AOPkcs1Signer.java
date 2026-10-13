package es.gob.afirma.core.signers;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.keystores.AOCancelledSMOperationException;
import es.gob.afirma.core.keystores.AuthenticationException;
import es.gob.afirma.core.keystores.LockedKeyStoreException;
import es.gob.afirma.core.keystores.PinException;
import es.gob.afirma.core.util.tree.AOTreeModel;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/AOPkcs1Signer.class */
public final class AOPkcs1Signer implements AOSigner {
    private static final String PKCS1_FILE_SUFFIX = ".p1";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        Provider provider;
        if (str == null) {
            throw new IllegalArgumentException("Es necesario indicar el algoritmo de firma, no puede ser nulo");
        }
        LOGGER.fine("Se ha solicitado una firma '" + str + "' con una clave de tipo " + privateKey.getAlgorithm());
        if (properties != null) {
            provider = Security.getProvider(properties.getProperty("Provider." + privateKey.getClass().getName()));
        } else {
            provider = null;
        }
        try {
            String strComposeSignatureAlgorithmName = AOSignConstants.composeSignatureAlgorithmName(str, privateKey.getAlgorithm());
            Signature signature = provider != null ? Signature.getInstance(strComposeSignatureAlgorithmName, provider) : Signature.getInstance(strComposeSignatureAlgorithmName);
            try {
                signature.initSign(privateKey);
                try {
                    signature.update(bArr);
                    try {
                        byte[] bArrSign = signature.sign();
                        if (certificateArr != null && certificateArr.length > 0) {
                            try {
                                Signature signature2 = Signature.getInstance(strComposeSignatureAlgorithmName);
                                signature2.initVerify(certificateArr[0].getPublicKey());
                                signature2.update(bArr);
                                if (!signature2.verify(bArrSign)) {
                                    throw new AOException("El PKCS#1 de firma obtenido no se genero con el certificado indicado");
                                }
                            } catch (Exception e) {
                                throw new AOException("Error al verificar el PKCS#1 de la firma", e);
                            }
                        } else {
                            LOGGER.warning("No se ha proporcionado el certificado para comprobar la integridad del PKCS#1");
                        }
                        return bArrSign;
                    } catch (Exception e2) {
                        if ("es.gob.jmulticard.CancelledOperationException".equals(e2.getClass().getName())) {
                            throw new AOCancelledSMOperationException("Cancelacion del dialogo de JMulticard");
                        }
                        if ("es.gob.jmulticard.jse.provider.SignatureAuthException".equals(e2.getClass().getName())) {
                            if ("es.gob.jmulticard.card.AuthenticationModeLockedException".equals(e2.getCause().getClass().getName())) {
                                throw new LockedKeyStoreException("El almacen de claves esta bloqueado", e2);
                            }
                            if ("es.gob.jmulticard.card.BadPinException".equals(e2.getCause().getClass().getName())) {
                                throw new PinException("La contrasena del almacen o certificado es incorrecta", e2);
                            }
                            throw new AuthenticationException("Ocurrio un error de autenticacion al utilizar la clave de firma", e2);
                        }
                        throw new AOException("Error durante el proceso de firma PKCS#1: " + e2, e2);
                    }
                } catch (Exception e3) {
                    throw new AOException("Error al configurar los datos a firmar: " + e3, e3);
                }
            } catch (Exception e4) {
                throw new AOException("Error al inicializar la firma con la clave privada para el algoritmo '" + str + "': " + e4, e4);
            }
        } catch (NoSuchAlgorithmException e5) {
            throw new AOException("No se soporta el algoritmo de firma (" + str + "): " + e5, e5);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta la multifirma de las firmas NONE");
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta la multifirma de las firmas NONE");
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) {
        throw new UnsupportedOperationException("No se soporta la multifirma de las firmas NONE");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) {
        throw new UnsupportedOperationException("No se puede obtener la estructura de firmantes en PKCS#1");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) {
        throw new UnsupportedOperationException("No se puede obtener la estructura de firmantes en PKCS#1");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) {
        return isSign(bArr);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        return false;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        return bArr != null && bArr.length > 0;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        String str3 = str2 != null ? str2 : "";
        if (str == null) {
            return "signature.p1";
        }
        if (str.toLowerCase(Locale.US).endsWith(PKCS1_FILE_SUFFIX)) {
            return str.substring(0, str.length() - PKCS1_FILE_SUFFIX.length()) + str3 + PKCS1_FILE_SUFFIX;
        }
        return str + str3 + PKCS1_FILE_SUFFIX;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) {
        throw new UnsupportedOperationException("No se pueden obtener los datos firmados en PKCS#1");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) {
        throw new UnsupportedOperationException("No se pueden obtener los datos firmados en PKCS#1");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) {
        throw new UnsupportedOperationException("No se puede obtener informacion de las firmas PKCS#1");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws AOException {
        throw new UnsupportedOperationException("No se puede obtener informacion de las firmas PKCS#1");
    }
}
