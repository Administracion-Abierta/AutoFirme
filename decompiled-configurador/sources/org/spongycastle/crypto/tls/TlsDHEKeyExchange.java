package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.util.io.TeeInputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsDHEKeyExchange.class */
public class TlsDHEKeyExchange extends TlsDHKeyExchange {
    protected TlsSignerCredentials serverCredentials;

    public TlsDHEKeyExchange(int keyExchange, Vector supportedSignatureAlgorithms, DHParameters dhParameters) {
        super(keyExchange, supportedSignatureAlgorithms, dhParameters);
        this.serverCredentials = null;
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCredentials(TlsCredentials serverCredentials) throws IOException {
        if (!(serverCredentials instanceof TlsSignerCredentials)) {
            throw new TlsFatalAlert((short) 80);
        }
        processServerCertificate(serverCredentials.getCertificate());
        this.serverCredentials = (TlsSignerCredentials) serverCredentials;
    }

    @Override // org.spongycastle.crypto.tls.TlsDHKeyExchange, org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generateServerKeyExchange() throws IOException {
        if (this.dhParameters == null) {
            throw new TlsFatalAlert((short) 80);
        }
        DigestInputBuffer buf = new DigestInputBuffer();
        this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.dhParameters, buf);
        SignatureAndHashAlgorithm signatureAndHashAlgorithm = TlsUtils.getSignatureAndHashAlgorithm(this.context, this.serverCredentials);
        Digest d = TlsUtils.createHash(signatureAndHashAlgorithm);
        SecurityParameters securityParameters = this.context.getSecurityParameters();
        d.update(securityParameters.clientRandom, 0, securityParameters.clientRandom.length);
        d.update(securityParameters.serverRandom, 0, securityParameters.serverRandom.length);
        buf.updateDigest(d);
        byte[] hash = new byte[d.getDigestSize()];
        d.doFinal(hash, 0);
        byte[] signature = this.serverCredentials.generateCertificateSignature(hash);
        DigitallySigned signed_params = new DigitallySigned(signatureAndHashAlgorithm, signature);
        signed_params.encode(buf);
        return buf.toByteArray();
    }

    @Override // org.spongycastle.crypto.tls.TlsDHKeyExchange, org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerKeyExchange(InputStream input) throws IOException {
        SecurityParameters securityParameters = this.context.getSecurityParameters();
        SignerInputBuffer buf = new SignerInputBuffer();
        InputStream teeIn = new TeeInputStream(input, buf);
        ServerDHParams dhParams = ServerDHParams.parse(teeIn);
        DigitallySigned signed_params = parseSignature(input);
        Signer signer = initVerifyer(this.tlsSigner, signed_params.getAlgorithm(), securityParameters);
        buf.updateSigner(signer);
        if (!signer.verifySignature(signed_params.getSignature())) {
            throw new TlsFatalAlert((short) 51);
        }
        this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(dhParams.getPublicKey());
        this.dhParameters = validateDHParameters(this.dhAgreePublicKey.getParameters());
    }

    protected Signer initVerifyer(TlsSigner tlsSigner, SignatureAndHashAlgorithm algorithm, SecurityParameters securityParameters) {
        Signer signer = tlsSigner.createVerifyer(algorithm, this.serverPublicKey);
        signer.update(securityParameters.clientRandom, 0, securityParameters.clientRandom.length);
        signer.update(securityParameters.serverRandom, 0, securityParameters.serverRandom.length);
        return signer;
    }
}
