package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import java.io.IOException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/Sod.class */
public final class Sod extends DecoderObject {
    private final CryptoHelper cryptoHelper;
    private static final byte TAG = 119;
    private byte[] ldsSecurityObjectBytes = null;
    private LdsSecurityObject ldsSecurityObject = null;
    private X509Certificate[] certificateChain = null;

    public Sod(CryptoHelper ch) {
        this.cryptoHelper = ch;
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        Tlv tlv = new Tlv(getBytes());
        checkTag(tlv.getTag());
    }

    public void validateSignature() throws TlvException, SignatureException, IOException, CertificateException, Asn1Exception {
        Tlv tlv = new Tlv(getBytes());
        this.certificateChain = this.cryptoHelper.validateCmsSignature(tlv.getValue());
        this.ldsSecurityObjectBytes = this.cryptoHelper.getCmsSignatureSignedContent(tlv.getValue());
        this.ldsSecurityObject = new LdsSecurityObject();
        this.ldsSecurityObject.setDerValue(this.ldsSecurityObjectBytes);
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 119;
    }

    public byte[] getLdsSecurityObjectBytes() throws TlvException, SignatureException, IOException, CertificateException, Asn1Exception {
        if (this.ldsSecurityObjectBytes == null) {
            validateSignature();
        }
        return this.ldsSecurityObjectBytes;
    }

    public LdsSecurityObject getLdsSecurityObject() throws TlvException, SignatureException, IOException, CertificateException, Asn1Exception {
        if (this.ldsSecurityObject == null) {
            validateSignature();
        }
        return this.ldsSecurityObject;
    }

    public X509Certificate[] getCertificateChain() throws TlvException, SignatureException, IOException, CertificateException, Asn1Exception {
        if (this.certificateChain == null) {
            validateSignature();
        }
        return (X509Certificate[]) this.certificateChain.clone();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SOD ICAO");
        try {
            sb.append("\nFirmado por: " + getCertificateChain()[0].getSubjectX500Principal());
            sb.append("\n  Con huellas para los siguientes grupos de datos\n");
            for (DataGroupHash dgh : this.ldsSecurityObject.getDataGroupHashes()) {
                sb.append("    DG");
                sb.append(dgh.getDataGroupNumber());
                sb.append(" = ");
                sb.append(HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                sb.append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            JmcLogger.warning("No se ha podido obtener la cadena de certificados de firma del SOD: " + e);
            return sb.toString();
        }
    }
}
