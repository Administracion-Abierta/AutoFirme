package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.core.signers.AOSignConstants;
import es.uji.crypto.xades.jxades.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignaturePolicyIdentifierImpl.class */
public class SignaturePolicyIdentifierImpl implements SignaturePolicyIdentifier {
    private boolean implied;
    private String sigPolicyId;
    private String description;
    private String sigPolicyQualifierSPURI;
    private String sigPolicyHashBase64;
    private String sigPolicyHashHashAlgorithm;

    public SignaturePolicyIdentifierImpl(boolean implied) {
        this.implied = implied;
    }

    private byte[] inputStreamToByteArray(InputStream in) throws IOException {
        byte[] buffer = new byte[2048];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            int length = in.read(buffer);
            if (length >= 0) {
                baos.write(buffer, 0, length);
            } else {
                return baos.toByteArray();
            }
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public void setIdentifier(String identifier) throws NoSuchAlgorithmException, IOException {
        setIdentifier(identifier, null, null);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public void setIdentifier(String identifier, String hashBase64, String hashAlgorithm) throws NoSuchAlgorithmException, IOException {
        if (hashBase64 == null || "".equals(hashBase64) || hashAlgorithm == null || "".equals(hashAlgorithm)) {
            URLConnection conn = new URL(identifier).openConnection();
            byte[] data = inputStreamToByteArray(conn.getInputStream());
            MessageDigest md = MessageDigest.getInstance(AOSignConstants.DIGEST_ALGORITHM_SHA1);
            md.update(data);
            this.sigPolicyHashBase64 = Base64.encodeBytes(md.digest());
            this.sigPolicyHashHashAlgorithm = "http://www.w3.org/2000/09/xmldsig#sha1";
        } else {
            this.sigPolicyHashBase64 = hashBase64;
            this.sigPolicyHashHashAlgorithm = hashAlgorithm;
        }
        this.sigPolicyId = identifier;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public boolean isImplied() {
        return this.implied;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public void setImplied(boolean implied) {
        this.implied = implied;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public String getIdentifier() {
        return this.sigPolicyId;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public String getHashBase64() {
        return this.sigPolicyHashBase64;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public String getDescription() {
        return this.description;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public void setDescription(String description) {
        this.description = description;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public String getQualifier() {
        return this.sigPolicyQualifierSPURI;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public void setQualifier(String qualifier) {
        this.sigPolicyQualifierSPURI = qualifier;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignaturePolicyIdentifier
    public String getHashAlgorithm() {
        return this.sigPolicyHashHashAlgorithm;
    }
}
