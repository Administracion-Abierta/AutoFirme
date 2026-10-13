package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.Vector;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSProtocol.class */
public abstract class DTLSProtocol {
    protected final SecureRandom secureRandom;

    protected DTLSProtocol(SecureRandom secureRandom) {
        if (secureRandom == null) {
            throw new IllegalArgumentException("'secureRandom' cannot be null");
        }
        this.secureRandom = secureRandom;
    }

    protected void processFinished(byte[] body, byte[] expected_verify_data) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(body);
        byte[] verify_data = TlsUtils.readFully(expected_verify_data.length, buf);
        TlsProtocol.assertEmpty(buf);
        if (!Arrays.constantTimeAreEqual(expected_verify_data, verify_data)) {
            throw new TlsFatalAlert((short) 40);
        }
    }

    protected static void applyMaxFragmentLengthExtension(DTLSRecordLayer recordLayer, short maxFragmentLength) throws IOException {
        if (maxFragmentLength >= 0) {
            if (!MaxFragmentLength.isValid(maxFragmentLength)) {
                throw new TlsFatalAlert((short) 80);
            }
            int plainTextLimit = 1 << (8 + maxFragmentLength);
            recordLayer.setPlaintextLimit(plainTextLimit);
        }
    }

    protected static short evaluateMaxFragmentLengthExtension(boolean resumedSession, Hashtable clientExtensions, Hashtable serverExtensions, short alertDescription) throws IOException {
        short maxFragmentLength = TlsExtensionsUtils.getMaxFragmentLengthExtension(serverExtensions);
        if (maxFragmentLength >= 0 && (!MaxFragmentLength.isValid(maxFragmentLength) || (!resumedSession && maxFragmentLength != TlsExtensionsUtils.getMaxFragmentLengthExtension(clientExtensions)))) {
            throw new TlsFatalAlert(alertDescription);
        }
        return maxFragmentLength;
    }

    protected static byte[] generateCertificate(Certificate certificate) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        certificate.encode(buf);
        return buf.toByteArray();
    }

    protected static byte[] generateSupplementalData(Vector supplementalData) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        TlsProtocol.writeSupplementalData(buf, supplementalData);
        return buf.toByteArray();
    }

    protected static void validateSelectedCipherSuite(int selectedCipherSuite, short alertDescription) throws IOException {
        switch (TlsUtils.getEncryptionAlgorithm(selectedCipherSuite)) {
            case 1:
            case 2:
                throw new TlsFatalAlert(alertDescription);
            default:
                return;
        }
    }
}
