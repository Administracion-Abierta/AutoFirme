package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.encodings.PKCS1Encoding;
import org.spongycastle.crypto.engines.RSABlindedEngine;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsRSAUtils.class */
public class TlsRSAUtils {
    public static byte[] generateEncryptedPreMasterSecret(TlsContext context, RSAKeyParameters rsaServerPublicKey, OutputStream output) throws IOException {
        byte[] premasterSecret = new byte[48];
        context.getSecureRandom().nextBytes(premasterSecret);
        TlsUtils.writeVersion(context.getClientVersion(), premasterSecret, 0);
        PKCS1Encoding encoding = new PKCS1Encoding(new RSABlindedEngine());
        encoding.init(true, new ParametersWithRandom(rsaServerPublicKey, context.getSecureRandom()));
        try {
            byte[] encryptedPreMasterSecret = encoding.processBlock(premasterSecret, 0, premasterSecret.length);
            if (TlsUtils.isSSL(context)) {
                output.write(encryptedPreMasterSecret);
            } else {
                TlsUtils.writeOpaque16(encryptedPreMasterSecret, output);
            }
            return premasterSecret;
        } catch (InvalidCipherTextException e) {
            throw new TlsFatalAlert((short) 80, e);
        }
    }

    public static byte[] safeDecryptPreMasterSecret(TlsContext context, RSAKeyParameters rsaServerPrivateKey, byte[] encryptedPreMasterSecret) {
        ProtocolVersion clientVersion = context.getClientVersion();
        byte[] fallback = new byte[48];
        context.getSecureRandom().nextBytes(fallback);
        byte[] M = Arrays.clone(fallback);
        try {
            PKCS1Encoding encoding = new PKCS1Encoding(new RSABlindedEngine(), fallback);
            encoding.init(false, new ParametersWithRandom(rsaServerPrivateKey, context.getSecureRandom()));
            M = encoding.processBlock(encryptedPreMasterSecret, 0, encryptedPreMasterSecret.length);
        } catch (Exception e) {
        }
        if (0 == 0 || !clientVersion.isEqualOrEarlierVersionOf(ProtocolVersion.TLSv10)) {
            int correct = (clientVersion.getMajorVersion() ^ (M[0] & 255)) | (clientVersion.getMinorVersion() ^ (M[1] & 255));
            int correct2 = correct | (correct >> 1);
            int correct3 = correct2 | (correct2 >> 2);
            int mask = (((correct3 | (correct3 >> 4)) & 1) - 1) ^ (-1);
            for (int i = 0; i < 48; i++) {
                M[i] = (byte) ((M[i] & (mask ^ (-1))) | (fallback[i] & mask));
            }
        }
        return M;
    }
}
