package es.gob.afirma.signers.cades;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.signers.AOCoSigner;
import es.gob.afirma.core.signers.AOCounterSigner;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.pkcs7.ObtainContentSignedData;
import es.gob.afirma.signers.pkcs7.ReadNodesTree;
import es.gob.afirma.signers.pkcs7.SCChecker;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/AOCAdESSigner.class */
public final class AOCAdESSigner implements AOSigner {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        if (certificateArr == null || certificateArr.length < 1) {
            throw new IllegalArgumentException("La cadena de certificados debe contener al menos un elemento");
        }
        Properties extraParams = getExtraParams(properties);
        checkAlgorithm(str, extraParams);
        new SCChecker().checkSpongyCastle();
        noticeIncompatibleConfig(str, extraParams);
        try {
            return applyTimeStamp(GenCAdESEPESSignedData.generateSignedData(str, privateKey, certificateArr, CAdESParameters.load(bArr, str, extraParams)), extraParams);
        } catch (AOCancelledOperationException e) {
            throw e;
        } catch (AOException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new AOException("Error al generar la firma CAdES: " + e3, e3);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        Properties extraParams = getExtraParams(properties);
        checkAlgorithm(str, extraParams);
        new SCChecker().checkSpongyCastle();
        try {
            return ((AOCoSigner) Class.forName("es.gob.afirma.signers.multi.cades.AOCAdESCoSigner").getConstructor(new Class[0]).newInstance(new Object[0])).cosign(bArr, bArr2, str, privateKey, certificateArr, extraParams);
        } catch (ClassNotFoundException e) {
            throw new AOException("No se ha encontrado la clase de cofirmas CAdES: " + e, e);
        } catch (IllegalAccessException e2) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES por acceso ilegal: " + e2, e2);
        } catch (IllegalArgumentException e3) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES: " + e3, e3);
        } catch (InstantiationException e4) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES: " + e4, e4);
        } catch (NoSuchMethodException e5) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES por falta de un constructor por defecto sin parametros: " + e5, e5);
        } catch (SecurityException e6) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES por falta de permisos: " + e6, e6);
        } catch (InvocationTargetException e7) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES por error en la invocacion al constructor: " + e7, e7);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        Properties extraParams = getExtraParams(properties);
        checkAlgorithm(str, extraParams);
        new SCChecker().checkSpongyCastle();
        try {
            return ((AOCoSigner) Class.forName("es.gob.afirma.signers.multi.cades.AOCAdESCoSigner").getConstructor(new Class[0]).newInstance(new Object[0])).cosign(bArr, str, privateKey, certificateArr, extraParams);
        } catch (IOException e) {
            throw new AOException("Error en tratamiento de datos para la cofirma: " + e, e);
        } catch (ClassNotFoundException e2) {
            throw new AOException("No se ha encontrado la clase de cofirmas CAdES: " + e2, e2);
        } catch (IllegalAccessException e3) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES por acceso ilegal: " + e3, e3);
        } catch (IllegalArgumentException e4) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES: " + e4, e4);
        } catch (InstantiationException e5) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES: " + e5, e5);
        } catch (NoSuchMethodException e6) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES por falta de un constructor por defecto sin parametros: " + e6, e6);
        } catch (SecurityException e7) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES por falta de permisos: " + e7, e7);
        } catch (InvocationTargetException e8) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES por error en la invocacion al constructor: " + e8, e8);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        Properties extraParams = getExtraParams(properties);
        checkAlgorithm(str, extraParams);
        new SCChecker().checkSpongyCastle();
        try {
            return ((AOCounterSigner) Class.forName("es.gob.afirma.signers.multi.cades.AOCAdESCounterSigner").getConstructor(new Class[0]).newInstance(new Object[0])).countersign(bArr, str, counterSignTarget, objArr, privateKey, certificateArr, extraParams);
        } catch (IOException e) {
            throw new AOException("Error en el tratamiemto de datos durante la contrafirma: " + e, e);
        } catch (ClassNotFoundException e2) {
            throw new AOException("No se ha encontrado el contrafirmador: " + e2, e2);
        } catch (IllegalAccessException e3) {
            throw new AOException("No ha permisos para invocar al contrafirmador: " + e3, e3);
        } catch (IllegalArgumentException e4) {
            throw new AOException("No se ha podido instanciar el contrafirmador: " + e4, e4);
        } catch (InstantiationException e5) {
            throw new AOException("Error al instanciar el contrafirmador: " + e5, e5);
        } catch (NoSuchMethodException e6) {
            throw new AOException("No se ha podido instanciar el contrafirmador por falta de un constructor por defecto sin parametros: " + e6, e6);
        } catch (SecurityException e7) {
            throw new AOException("No se ha podido instanciar el contrafirmador por motivos de seguridad: " + e7, e7);
        } catch (InvocationTargetException e8) {
            throw new AOException("No se ha podido instanciar el contrafirmador: " + e8, e8);
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws IOException, AOInvalidFormatException {
        new SCChecker().checkSpongyCastle();
        if (!CAdESValidator.isCAdESValid(bArr, false)) {
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un objeto de firma CAdES");
        }
        try {
            return new ReadNodesTree().readNodesTree(bArr, z);
        } catch (Exception e) {
            LOGGER.severe("No se ha podido obtener el arbol de firmantes de la firma, se devolvera null: " + e);
            return null;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) throws IOException, AOInvalidFormatException {
        return getSignersStructure(bArr, null, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        new SCChecker().checkSpongyCastle();
        return CAdESValidator.isCAdESSignedData(bArr, true);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        return isSign(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        return true;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws IOException, AOInvalidFormatException {
        return getData(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws IOException, AOInvalidFormatException {
        if (bArr == null) {
            throw new IllegalArgumentException("Se han introducido datos nulos para su comprobacion");
        }
        new SCChecker().checkSpongyCastle();
        if (!CAdESValidator.isCAdESValid(bArr, false)) {
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un objeto de firma");
        }
        return ObtainContentSignedData.obtainData(bArr);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        return str + (str2 != null ? str2 : "") + ".csig";
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws AOInvalidFormatException {
        return getSignInfo(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws AOInvalidFormatException {
        if (bArr == null) {
            throw new IllegalArgumentException("No se han introducido datos para analizar");
        }
        if (!isSign(bArr)) {
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un objeto de firma");
        }
        return new AOSignInfo("CAdES");
    }

    private static void checkAlgorithm(String str, Properties properties) throws AOException {
        if (str == null) {
            throw new IllegalArgumentException("El algoritmo de firma no puede ser nulo");
        }
        if (str.toUpperCase(Locale.US).startsWith("MD")) {
            throw new AOException("CAdES no permite huellas digitales MD2 o MD5 (Decision 130/2011 CE)");
        }
        if (AOSignConstants.SIGN_PROFILE_BASELINE.equalsIgnoreCase(properties.getProperty("profile")) && AOSignConstants.isSHA1SignatureAlgorithm(str)) {
            LOGGER.warning("El algoritmo '" + str + "' no esta recomendado para su uso en las firmas baseline por no cumplir los requisitos de seguridad actuales");
        }
    }

    private static Properties getExtraParams(Properties properties) {
        return properties != null ? (Properties) properties.clone() : new Properties();
    }

    private static void noticeIncompatibleConfig(String str, Properties properties) {
        if (properties != null && properties.containsKey("precalculatedHashAlgorithm") && properties.containsKey(CAdESExtraParams.MODE)) {
            LOGGER.warning("Se ignorara el parametro 'mode' por haberse proporcionado tambien el parametro 'precalculatedHashAlgorithm'. La firma sera explicita.");
            properties.remove(CAdESExtraParams.MODE);
        }
        if (str != null && properties != null && AOSignConstants.isSHA2SignatureAlgorithm(str) && properties.containsKey(CAdESExtraParams.SIGNING_CERTIFICATE_V2)) {
            LOGGER.warning("Se ignorara la propiedad 'signingCertificateV2' porque las firmas SHA2 siempre usan SigningCertificateV2");
            properties.remove(CAdESExtraParams.SIGNING_CERTIFICATE_V2);
        }
    }

    private static byte[] applyTimeStamp(byte[] bArr, Properties properties) {
        try {
            Class<?> cls = Class.forName("es.gob.afirma.signers.tsp.pkcs7.TsaParams");
            Object objNewInstance = cls.getConstructor(Properties.class).newInstance(properties);
            byte[] bArr2 = null;
            try {
                Class<?> cls2 = Class.forName("es.gob.afirma.signers.tsp.pkcs7.CMSTimestamper");
                bArr2 = (byte[]) cls2.getMethod("addTimestamp", byte[].class, String.class, GregorianCalendar.class).invoke(cls2.getConstructor(cls).newInstance(objNewInstance), bArr, (String) cls2.getMethod("getTsaHashAlgorithm", new Class[0]).invoke(objNewInstance, new Object[0]), new GregorianCalendar());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "No se ha podido aplicar el sello de tiempo", (Throwable) e);
            }
            return bArr2 != null ? bArr2 : bArr;
        } catch (ClassNotFoundException e2) {
            LOGGER.info("No se han encontrado las bibliotecas de sello de tiempo, por lo que no se comprobara si se requeria agregar");
            return bArr;
        } catch (Exception e3) {
            return bArr;
        }
    }
}
