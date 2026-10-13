package es.gob.afirma.standalone;

import es.gob.afirma.core.misc.AOFileUtils;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import es.gob.afirma.signers.cms.AOCMSSigner;
import es.gob.afirma.signers.odf.AOODFSigner;
import es.gob.afirma.signers.ooxml.AOOOXMLSigner;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.xades.AOFacturaESigner;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signers.xmldsig.AOXMLDSigSigner;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/DataAnalizerUtil.class */
public final class DataAnalizerUtil {
    private DataAnalizerUtil() {
    }

    public static X509Certificate isCertificate(byte[] bArr) throws CertificateException {
        if (bArr == null || bArr.length < 1) {
            throw new CertificateException("Los datos eran nulos o vacios");
        }
        CertificateFactory certificateFactory = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
        try {
            return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(bArr));
        } catch (Exception e) {
            try {
                return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decode(new String(bArr).replace("%0A", "").replace("%2F", "/").replace("%2B", Marker.ANY_NON_NULL_MARKER).replace("%3D", "="))));
            } catch (Exception e2) {
                throw new CertificateException("Los datos proporcionados no son un certificado X.509");
            }
        }
    }

    public static boolean isXML(byte[] bArr) {
        return AOFileUtils.isXML(bArr);
    }

    public static boolean isSignedXML(byte[] bArr) {
        try {
            return new AOXAdESSigner().isSign(bArr) || new AOXMLDSigSigner().isSign(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPDF(byte[] bArr) {
        try {
            return new AOPDFSigner().isValidDataFile(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSignedPDF(byte[] bArr) {
        try {
            return new AOPDFSigner().isSign(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSignedBinary(byte[] bArr) {
        try {
            return new AOCMSSigner().isSign(bArr) || new AOCAdESSigner().isSign(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFacturae(byte[] bArr) {
        try {
            return new AOFacturaESigner().isValidDataFile(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSignedFacturae(byte[] bArr) {
        try {
            return new AOFacturaESigner().isSign(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isODF(byte[] bArr) {
        try {
            return new AOODFSigner().isValidDataFile(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSignedODF(byte[] bArr) {
        try {
            return new AOODFSigner().isSign(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isOOXML(byte[] bArr) {
        try {
            return new AOOOXMLSigner().isValidDataFile(bArr);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSignedOOXML(byte[] bArr) {
        try {
            return new AOOOXMLSigner().isSign(bArr);
        } catch (Exception e) {
            return false;
        }
    }
}
