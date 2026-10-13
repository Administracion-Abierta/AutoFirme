package es.gob.afirma.standalone.crypto;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.swing.ImageIcon;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/crypto/DnieCertAnalyzer.class */
final class DnieCertAnalyzer extends CertAnalyzer {
    DnieCertAnalyzer() {
    }

    @Override // es.gob.afirma.standalone.crypto.CertAnalyzer
    public boolean isValidCert(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return false;
        }
        if (AOUtil.getCN(x509Certificate.getIssuerX500Principal().toString()).startsWith("AC DNIE") || AOUtil.getCN(x509Certificate.getIssuerX500Principal().toString()).startsWith("AC RAIZ DNIE")) {
            return true;
        }
        return false;
    }

    @Override // es.gob.afirma.standalone.crypto.CertAnalyzer
    public CertificateInfo analyzeCert(X509Certificate x509Certificate) {
        String str = null;
        try {
            String string = null;
            String string2 = null;
            for (Rdn rdn : new LdapName(x509Certificate.getSubjectX500Principal().toString()).getRdns()) {
                if (rdn.getType().equalsIgnoreCase("GIVENNAME")) {
                    string = rdn.getValue().toString();
                } else if (rdn.getType().equalsIgnoreCase("SURNAME")) {
                    string2 = rdn.getValue().toString();
                }
            }
            if (string != null || string2 != null) {
                if (string != null) {
                    str = string;
                }
                if (string2 != null) {
                    if (str != null) {
                        str = str + " " + string2;
                    } else {
                        str = string2;
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").warning("No se ha podido obtener el nombre del titular del DNIe: " + e);
        }
        return new CertificateInfo(x509Certificate, SimpleAfirmaMessages.getString("DnieCertAnalyzer.2") + (str != null ? " " + SimpleAfirmaMessages.getString("DnieCertAnalyzer.0") + " " + str : ""), new ImageIcon(getClass().getResource("/resources/dnie_cert_ico.png")), SimpleAfirmaMessages.getString("DnieCertAnalyzer.4"));
    }
}
