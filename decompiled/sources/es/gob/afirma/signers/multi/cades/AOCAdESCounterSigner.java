package es.gob.afirma.signers.multi.cades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.SigningLTSException;
import es.gob.afirma.core.signers.AOCounterSigner;
import es.gob.afirma.core.signers.AOSimpleSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.signers.cades.CAdESParameters;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/multi/cades/AOCAdESCounterSigner.class */
public class AOCAdESCounterSigner implements AOCounterSigner {
    private final AOSimpleSigner ss;
    private final Date date;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    public AOCAdESCounterSigner() {
        this.ss = null;
        this.date = null;
    }

    public AOCAdESCounterSigner(AOSimpleSigner aOSimpleSigner, Date date) {
        if (aOSimpleSigner == null) {
            throw new IllegalArgumentException("El firmador PKCS#1 no puede ser nulo");
        }
        if (date == null) {
            LOGGER.warning("Se ha establecido una fecha nula, se usara la actual");
        }
        this.ss = aOSimpleSigner;
        this.date = date;
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public final byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        CounterSignTarget counterSignTarget2;
        Properties extraParams = getExtraParams(properties);
        String property = extraParams.getProperty("allowSignLTSignature");
        if (property == null || !Boolean.parseBoolean(property)) {
            try {
                CAdESMultiUtil.checkUnsupportedAttributes(bArr);
            } catch (SigningLTSException e) {
                if (property != null) {
                    throw new AOException(e.getMessage());
                }
                throw e;
            }
        }
        CAdESParameters cAdESParametersLoad = CAdESParameters.load(null, str, extraParams);
        if (this.date != null) {
            cAdESParametersLoad.setSigningTime(this.date);
        }
        CAdESCounterSigner cAdESCounterSigner = new CAdESCounterSigner(this.ss);
        if (counterSignTarget == CounterSignTarget.NODES || counterSignTarget == CounterSignTarget.SIGNERS) {
            throw new AOException("No se soporta la firma de nodos individuales");
        }
        if (counterSignTarget != null) {
            counterSignTarget2 = counterSignTarget;
        } else {
            try {
                counterSignTarget2 = CounterSignTarget.LEAFS;
            } catch (AOException e2) {
                throw e2;
            } catch (NoSuchAlgorithmException e3) {
                throw new AOException("Algoritmo de firma o huella digital no soportado", e3);
            } catch (CertificateException e4) {
                throw new AOException("Error generando la Contrafirma CAdES", e4);
            } catch (Exception e5) {
                throw new AOException("Error generando la contrafirma CAdES: " + e5, e5);
            }
        }
        return cAdESCounterSigner.counterSign(str, bArr, counterSignTarget2, privateKey, certificateArr, cAdESParametersLoad);
    }

    private static Properties getExtraParams(Properties properties) {
        return properties != null ? (Properties) properties.clone() : new Properties();
    }
}
