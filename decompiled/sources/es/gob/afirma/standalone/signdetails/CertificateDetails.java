package es.gob.afirma.standalone.signdetails;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/signdetails/CertificateDetails.class */
public class CertificateDetails {
    private String name;
    private String issuerName;
    private String expirationDate;
    private Properties validityResult = new Properties();
    private final List<CertificateDetails> subCertDetails = new ArrayList();
    private boolean correctValidation;

    public CertificateDetails(X509Certificate x509Certificate) {
        String string;
        this.name = AOUtil.getCN(x509Certificate);
        this.issuerName = AOUtil.getCN(x509Certificate.getIssuerX500Principal().getName());
        this.expirationDate = new SimpleDateFormat("dd-MM-yyyy").format(x509Certificate.getNotAfter()).toString();
        this.correctValidation = false;
        try {
            x509Certificate.checkValidity();
            string = SimpleAfirmaMessages.getString("ValidationInfoDialog.40");
            this.correctValidation = true;
        } catch (CertificateExpiredException e) {
            string = SimpleAfirmaMessages.getString("ValidationInfoDialog.2");
        } catch (CertificateNotYetValidException e2) {
            string = SimpleAfirmaMessages.getString("ValidationInfoDialog.3");
        } catch (Exception e3) {
            string = SimpleAfirmaMessages.getString("ValidationInfoDialog.4");
        }
        this.validityResult.put("Validacion", string);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getIssuerName() {
        return this.issuerName;
    }

    public void setIssuerName(String str) {
        this.issuerName = str;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(String str) {
        this.expirationDate = str;
    }

    public Properties getValidityResult() {
        return this.validityResult;
    }

    public void setValidityResult(Properties properties) {
        this.validityResult = properties;
    }

    public List<CertificateDetails> getSubCertDetails() {
        return this.subCertDetails;
    }

    public boolean isCorrectValidation() {
        return this.correctValidation;
    }
}
