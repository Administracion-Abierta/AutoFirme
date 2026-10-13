package es.gob.afirma.signvalidation;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import es.gob.afirma.signers.cms.AOCMSSigner;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.CMSProcessableByteArray;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.CMSSignerDigestMismatchException;
import org.spongycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.spongycastle.cms.SignerInformation;
import org.spongycastle.cms.SignerInformationVerifier;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.bc.BcDigestCalculatorProvider;
import org.spongycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/ValidateBinarySignature.class */
public final class ValidateBinarySignature extends SignValider {
    @Override // es.gob.afirma.signvalidation.SignValider
    public List<SignValidity> validate(byte[] bArr) throws IOException {
        return validate(bArr, null, true);
    }

    @Override // es.gob.afirma.signvalidation.SignValider
    public List<SignValidity> validate(byte[] bArr, boolean z) throws IOException {
        return validate(bArr, null, z);
    }

    @Override // es.gob.afirma.signvalidation.SignValider
    public List<SignValidity> validate(byte[] bArr, Properties properties) throws IOException {
        return validate(bArr, null, true);
    }

    public static List<SignValidity> validate(byte[] bArr, byte[] bArr2) throws IOException {
        return validate(bArr, bArr2, true);
    }

    public static List<SignValidity> validate(byte[] bArr, byte[] bArr2, boolean z) throws IOException {
        byte[] data;
        ArrayList arrayList = new ArrayList();
        if (bArr == null) {
            throw new IllegalArgumentException("La firma a validar no puede ser nula");
        }
        AOSigner aOCAdESSigner = new AOCAdESSigner();
        if (!aOCAdESSigner.isSign(bArr)) {
            aOCAdESSigner = new AOCMSSigner();
            if (!aOCAdESSigner.isSign(bArr)) {
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_SIGN));
                return arrayList;
            }
        }
        byte[] bArr3 = null;
        if (bArr2 != null) {
            data = bArr2;
        } else {
            try {
                data = aOCAdESSigner.getData(bArr);
            } catch (AOInvalidFormatException e) {
                Logger.getLogger("es.gob.afirma").info("Se ha pedido validar una firma como CAdES, pero no es CAdES: " + e);
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_SIGN, e));
            } catch (AOException e2) {
                Logger.getLogger("es.gob.afirma").info("Se encontraron datos en la firma y no se pudieron extraer: " + e2);
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.UNKOWN_ERROR));
            }
        }
        bArr3 = data;
        if (bArr3 == null) {
            Logger.getLogger("es.gob.afirma").info("Se ha pedido validar una firma explicita sin proporcionar los datos firmados");
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.NO_DATA));
        }
        try {
            verifySignatures(bArr, bArr3, z, arrayList);
        } catch (CMSSignerDigestMismatchException e3) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_MATCH_DATA, e3));
        } catch (Exception e4) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, null, e4));
        }
        return checkLongStandingValiditySign(checkValidityKOPriority(arrayList));
    }

    private static void verifySignatures(byte[] bArr, byte[] bArr2, boolean z, List<SignValidity> list) throws OperatorCreationException, IOException, CertificateException, CMSException {
        CMSSignedData cMSSignedData;
        if (bArr2 == null) {
            cMSSignedData = new CMSSignedData(bArr);
        } else {
            cMSSignedData = new CMSSignedData(new CMSProcessableByteArray(bArr2), bArr);
        }
        Store<X509CertificateHolder> certificates = cMSSignedData.getCertificates();
        boolean z2 = cMSSignedData.getSignedContent() != null;
        CertificateFactory certificateFactory = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
        for (SignerInformation signerInformation : cMSSignedData.getSignerInfos().getSigners()) {
            for (SignValidity signValidity : verifySign(signerInformation, certificates, certificateFactory, z, SignatureFormatDetectorPadesCades.resolveASN1Format(cMSSignedData, signerInformation), z2)) {
                if (SignValidity.SIGN_DETAIL_TYPE.UNKNOWN.equals(signValidity.getValidity())) {
                    list.add(0, signValidity);
                } else {
                    list.add(signValidity);
                }
            }
        }
    }

    public static List<SignValidity> verifySign(Object obj, Store<X509CertificateHolder> store, CertificateFactory certificateFactory, boolean z, String str, boolean z2) {
        ArrayList arrayList = new ArrayList();
        try {
            SignerInformation signerInformation = (SignerInformation) obj;
            X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(store.getMatches(new CertHolderBySignerIdSelector(signerInformation.getSID())).iterator().next().getEncoded()));
            if (z) {
                x509Certificate.checkValidity();
            }
            if (!signerInformation.verify(new SignerInformationVerifier(new DefaultCMSSignatureAlgorithmNameGenerator(), new DefaultSignatureAlgorithmIdentifierFinder(), new JcaContentVerifierProviderBuilder().setProvider(new BouncyCastleProvider()).build(x509Certificate), new BcDigestCalculatorProvider()))) {
                throw new CMSException("Firma no valida");
            }
        } catch (CertificateExpiredException e) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CERTIFICATE_EXPIRED, e));
        } catch (CertificateNotYetValidException e2) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CERTIFICATE_NOT_VALID_YET, e2));
        } catch (CMSSignerDigestMismatchException e3) {
            if (z2) {
                arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.NO_MATCH_DATA, e3));
            }
        } catch (Exception e4) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.CANT_VALIDATE_CERT, e4));
        }
        if (!ISignatureFormatDetector.FORMAT_CADES_BES.equals(str) && !ISignatureFormatDetector.FORMAT_UNRECOGNIZED.equals(str) && !ISignatureFormatDetector.FORMAT_CADES_B_LEVEL.equals(str) && !ISignatureFormatDetector.FORMAT_CADES_B_B_LEVEL.equals(str) && !"CMS".equals(str) && !ISignatureFormatDetector.FORMAT_CADES_EPES.equals(str)) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.SIGN_PROFILE_NOT_CHECKED));
        }
        if (arrayList.size() == 0) {
            arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.OK, null));
        }
        return arrayList;
    }
}
