package es.gob.afirma.signers.xades;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/EscapeHelper.class */
final class EscapeHelper {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private EscapeHelper() {
    }

    static List<Certificate> getEscapedCertificates(List<Certificate> list) {
        if (list == null) {
            return new ArrayList(0);
        }
        ArrayList arrayList = new ArrayList(list.size());
        for (int i = 0; i < list.size(); i++) {
            arrayList.add(i, new EscapedCertificate((X509Certificate) list.get(i)));
        }
        return arrayList;
    }

    static String escapeLdapName(String str) {
        try {
            LdapName ldapName = new LdapName(str);
            StringBuilder sb = new StringBuilder();
            for (Rdn rdn : ldapName.getRdns()) {
                sb.append(rdn.getType());
                sb.append('=');
                sb.append(escapeCharacters(Rdn.escapeValue(rdn.getValue())));
                sb.append(",");
            }
            sb.append("<EOF>");
            return sb.toString().replace(",<EOF>", "");
        } catch (InvalidNameException e) {
            LOGGER.warning("No ha sido posible 'escapar' el nombre X.500 " + str + ": " + e);
            return str;
        }
    }

    private static String escapeCharacters(String str) {
        return str.replace("á", "\\C3\\A1").replace("é", "\\C3\\A9").replace("í", "\\C3\\AD").replace("ó", "\\C3\\B3").replace("ú", "\\C3\\BA").replace("Á", "\\C3\\81").replace("É", "\\C3\\89").replace("Í", "\\C3\\8D").replace("Ó", "\\C3\\93").replace("Ú", "\\C3\\9A").replace("ñ", "\\C3\\B1").replace("Ñ", "\\C3\\91").replace("ç", "\\C3\\A7").replace("Ç", "\\C3\\87").replace("à", "\\C3\\A0").replace("è", "\\C3\\A8").replace("ì", "\\C3\\AC").replace("ò", "\\C3\\B2").replace("ù", "\\C3\\B9").replace("À", "\\C3\\80").replace("È", "\\C3\\88").replace("Ì", "\\C3\\8C").replace("Ò", "\\C3\\92").replace("Ù", "\\C3\\99").replace("ü", "\\C3\\BC").replace("Ü", "\\C3\\9C").replace("¿", "\\C2\\BF").replace("¡", "\\C2\\A1").replace("µ", "\\C2\\B5").replace("º", "\\C2\\BA").replace("ª", "\\C2\\AA");
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/EscapeHelper$EscapedCertificate.class */
    private static class EscapedCertificate extends X509Certificate {
        private final X509Certificate cert;

        private static X500Principal escapePrincipal(Principal principal) {
            return new X500Principal(EscapeHelper.escapeLdapName(principal.toString()));
        }

        EscapedCertificate(X509Certificate x509Certificate) {
            if (x509Certificate == null) {
                throw new IllegalArgumentException("El certificado original no puede ser nulo");
            }
            this.cert = x509Certificate;
        }

        @Override // java.security.cert.X509Certificate
        public Principal getSubjectDN() {
            return escapePrincipal(this.cert.getSubjectDN());
        }

        @Override // java.security.cert.X509Certificate
        public X500Principal getSubjectX500Principal() {
            return escapePrincipal(this.cert.getSubjectX500Principal());
        }

        @Override // java.security.cert.X509Certificate
        public Principal getIssuerDN() {
            return escapePrincipal(this.cert.getIssuerDN());
        }

        @Override // java.security.cert.X509Certificate
        public X500Principal getIssuerX500Principal() {
            return escapePrincipal(this.cert.getIssuerX500Principal());
        }

        @Override // java.security.cert.X509Extension
        public Set<String> getCriticalExtensionOIDs() {
            return this.cert.getCriticalExtensionOIDs();
        }

        @Override // java.security.cert.X509Extension
        public byte[] getExtensionValue(String str) {
            return this.cert.getExtensionValue(str);
        }

        @Override // java.security.cert.X509Extension
        public Set<String> getNonCriticalExtensionOIDs() {
            return this.cert.getNonCriticalExtensionOIDs();
        }

        @Override // java.security.cert.X509Extension
        public boolean hasUnsupportedCriticalExtension() {
            return this.cert.hasUnsupportedCriticalExtension();
        }

        @Override // java.security.cert.X509Certificate
        public void checkValidity() throws CertificateNotYetValidException, CertificateExpiredException {
            this.cert.checkValidity();
        }

        @Override // java.security.cert.X509Certificate
        public void checkValidity(Date date) throws CertificateNotYetValidException, CertificateExpiredException {
            this.cert.checkValidity(date);
        }

        @Override // java.security.cert.X509Certificate
        public int getBasicConstraints() {
            return this.cert.getBasicConstraints();
        }

        @Override // java.security.cert.X509Certificate
        public boolean[] getIssuerUniqueID() {
            return this.cert.getIssuerUniqueID();
        }

        @Override // java.security.cert.X509Certificate
        public boolean[] getKeyUsage() {
            return this.cert.getKeyUsage();
        }

        @Override // java.security.cert.X509Certificate
        public Date getNotAfter() {
            return this.cert.getNotAfter();
        }

        @Override // java.security.cert.X509Certificate
        public Date getNotBefore() {
            return this.cert.getNotBefore();
        }

        @Override // java.security.cert.X509Certificate
        public BigInteger getSerialNumber() {
            return this.cert.getSerialNumber();
        }

        @Override // java.security.cert.X509Certificate
        public String getSigAlgName() {
            return this.cert.getSigAlgName();
        }

        @Override // java.security.cert.X509Certificate
        public String getSigAlgOID() {
            return this.cert.getSigAlgOID();
        }

        @Override // java.security.cert.X509Certificate
        public byte[] getSigAlgParams() {
            return this.cert.getSigAlgParams();
        }

        @Override // java.security.cert.X509Certificate
        public byte[] getSignature() {
            return this.cert.getSignature();
        }

        @Override // java.security.cert.X509Certificate
        public boolean[] getSubjectUniqueID() {
            return this.cert.getSubjectUniqueID();
        }

        @Override // java.security.cert.X509Certificate
        public byte[] getTBSCertificate() throws CertificateEncodingException {
            return this.cert.getTBSCertificate();
        }

        @Override // java.security.cert.X509Certificate
        public int getVersion() {
            return this.cert.getVersion();
        }

        @Override // java.security.cert.Certificate
        public byte[] getEncoded() throws CertificateEncodingException {
            return this.cert.getEncoded();
        }

        @Override // java.security.cert.Certificate
        public PublicKey getPublicKey() {
            return this.cert.getPublicKey();
        }

        @Override // java.security.cert.Certificate
        public String toString() {
            return this.cert.toString();
        }

        @Override // java.security.cert.Certificate
        public void verify(PublicKey publicKey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CertificateException, NoSuchProviderException {
            this.cert.verify(publicKey);
        }

        @Override // java.security.cert.Certificate
        public void verify(PublicKey publicKey, String str) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CertificateException, NoSuchProviderException {
            this.cert.verify(publicKey, str);
        }
    }
}
