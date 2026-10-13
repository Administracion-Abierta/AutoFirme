package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.x500.X500Name;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/CertificateRequest.class */
public class CertificateRequest {
    protected short[] certificateTypes;
    protected Vector supportedSignatureAlgorithms;
    protected Vector certificateAuthorities;

    public CertificateRequest(short[] certificateTypes, Vector supportedSignatureAlgorithms, Vector certificateAuthorities) {
        this.certificateTypes = certificateTypes;
        this.supportedSignatureAlgorithms = supportedSignatureAlgorithms;
        this.certificateAuthorities = certificateAuthorities;
    }

    public short[] getCertificateTypes() {
        return this.certificateTypes;
    }

    public Vector getSupportedSignatureAlgorithms() {
        return this.supportedSignatureAlgorithms;
    }

    public Vector getCertificateAuthorities() {
        return this.certificateAuthorities;
    }

    public void encode(OutputStream output) throws IOException {
        if (this.certificateTypes == null || this.certificateTypes.length == 0) {
            TlsUtils.writeUint8(0, output);
        } else {
            TlsUtils.writeUint8ArrayWithUint8Length(this.certificateTypes, output);
        }
        if (this.supportedSignatureAlgorithms != null) {
            TlsUtils.encodeSupportedSignatureAlgorithms(this.supportedSignatureAlgorithms, false, output);
        }
        if (this.certificateAuthorities == null || this.certificateAuthorities.isEmpty()) {
            TlsUtils.writeUint16(0, output);
            return;
        }
        Vector derEncodings = new Vector(this.certificateAuthorities.size());
        int totalLength = 0;
        for (int i = 0; i < this.certificateAuthorities.size(); i++) {
            X500Name certificateAuthority = (X500Name) this.certificateAuthorities.elementAt(i);
            byte[] derEncoding = certificateAuthority.getEncoded(ASN1Encoding.DER);
            derEncodings.addElement(derEncoding);
            totalLength += derEncoding.length + 2;
        }
        TlsUtils.checkUint16(totalLength);
        TlsUtils.writeUint16(totalLength, output);
        for (int i2 = 0; i2 < derEncodings.size(); i2++) {
            TlsUtils.writeOpaque16((byte[]) derEncodings.elementAt(i2), output);
        }
    }

    public static CertificateRequest parse(TlsContext context, InputStream input) throws IOException {
        int numTypes = TlsUtils.readUint8(input);
        short[] certificateTypes = new short[numTypes];
        for (int i = 0; i < numTypes; i++) {
            certificateTypes[i] = TlsUtils.readUint8(input);
        }
        Vector supportedSignatureAlgorithms = null;
        if (TlsUtils.isTLSv12(context)) {
            supportedSignatureAlgorithms = TlsUtils.parseSupportedSignatureAlgorithms(false, input);
        }
        Vector certificateAuthorities = new Vector();
        byte[] certAuthData = TlsUtils.readOpaque16(input);
        ByteArrayInputStream bis = new ByteArrayInputStream(certAuthData);
        while (bis.available() > 0) {
            byte[] derEncoding = TlsUtils.readOpaque16(bis);
            ASN1Primitive asn1 = TlsUtils.readDERObject(derEncoding);
            certificateAuthorities.addElement(X500Name.getInstance(asn1));
        }
        return new CertificateRequest(certificateTypes, supportedSignatureAlgorithms, certificateAuthorities);
    }
}
