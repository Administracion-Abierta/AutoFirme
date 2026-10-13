package es.gob.afirma.signers.cades.asic;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.signers.AOCoSigner;
import es.gob.afirma.core.signers.AOCounterSigner;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.signers.asic.ASiCUtil;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import es.gob.afirma.signers.cades.CAdESExtraParams;
import es.gob.afirma.signers.xades.asic.XAdESASiCExtraParams;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/asic/AOCAdESASiCSSigner.class */
public final class AOCAdESASiCSSigner implements AOSigner {
    private static final Logger LOGGER = Logger.getLogger("es.agob.afirma");

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        Properties properties2 = properties != null ? properties : new Properties();
        properties2.put(CAdESExtraParams.MODE, "explicit");
        return ASiCUtil.createSContainer(new AOCAdESSigner().sign(bArr, str, privateKey, certificateArr, properties2), bArr, ASiCUtil.ENTRY_NAME_BINARY_SIGNATURE, properties2.getProperty(XAdESASiCExtraParams.ASICS_FILENAME));
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) throws IOException, AOInvalidFormatException {
        return getSignersStructure(bArr, null, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws IOException, AOInvalidFormatException {
        return new AOCAdESSigner().getSignersStructure(ASiCUtil.getASiCSBinarySignature(bArr), z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) throws IOException {
        try {
            return new AOCAdESASiCSSigner().isSign(ASiCUtil.getASiCSBinarySignature(bArr));
        } catch (Exception e) {
            return false;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) throws IOException {
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
    public String getSignedName(String str, String str2) {
        return str + (str2 != null ? str2 : "") + ".asics";
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws IOException, AOInvalidFormatException {
        return ASiCUtil.getASiCSData(bArr);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws IOException, AOInvalidFormatException {
        return getData(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws IOException, AOException {
        return new AOCAdESSigner().getSignInfo(ASiCUtil.getASiCSBinarySignature(bArr));
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws IOException, AOException {
        return getSignInfo(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        try {
            return ((AOCoSigner) Class.forName("es.gob.afirma.signers.multi.cades.asic.AOCAdESASiCSCoSigner").getConstructor(new Class[0]).newInstance(new Object[0])).cosign(bArr, bArr2, str, privateKey, certificateArr, properties);
        } catch (ClassNotFoundException e) {
            throw new AOException("No se ha encontrado la clase de cofirmas CAdES ASiC: " + e, e);
        } catch (IllegalAccessException e2) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES ASiC: " + e2, e2);
        } catch (IllegalArgumentException e3) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES ASiC: " + e3, e3);
        } catch (InstantiationException e4) {
            throw new AOException("Error al instanciar la clase de cofirmas CAdES: " + e4, e4);
        } catch (NoSuchMethodException e5) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES ASiC por falta de un constructor por defecto sin parametros: " + e5, e5);
        } catch (SecurityException e6) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES ASiC por motivos de seguridad: " + e6, e6);
        } catch (InvocationTargetException e7) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES ASiC: " + e7, e7);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        return cosign(null, bArr, str, privateKey, certificateArr, properties);
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        try {
            return ((AOCounterSigner) Class.forName("es.gob.afirma.signers.multi.cades.asic.AOCAdESASiCSCounterSigner").getConstructor(new Class[0]).newInstance(new Object[0])).countersign(bArr, str, counterSignTarget, objArr, privateKey, certificateArr, properties);
        } catch (ClassNotFoundException e) {
            throw new AOException("No se ha encontrado la clase de cofirmas CAdES ASiC: " + e, e);
        } catch (IllegalAccessException e2) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES ASiC: " + e2, e2);
        } catch (IllegalArgumentException e3) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES ASiC: " + e3, e3);
        } catch (InstantiationException e4) {
            throw new AOException("Error al instanciar la clase de cofirmas CAdES ASiC: " + e4, e4);
        } catch (NoSuchMethodException e5) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES ASiC por falta de un constructor por defecto sin parametros: " + e5, e5);
        } catch (SecurityException e6) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES ASiC por motivos de seguridad: " + e6, e6);
        } catch (InvocationTargetException e7) {
            throw new AOException("No se ha podido instanciar la clase de cofirmas CAdES ASiC: " + e7, e7);
        }
    }
}
