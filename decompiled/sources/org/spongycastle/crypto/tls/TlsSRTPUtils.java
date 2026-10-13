package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsSRTPUtils.class */
public class TlsSRTPUtils {
    public static final Integer EXT_use_srtp = Integers.valueOf(14);

    public static void addUseSRTPExtension(Hashtable extensions, UseSRTPData useSRTPData) throws IOException {
        extensions.put(EXT_use_srtp, createUseSRTPExtension(useSRTPData));
    }

    public static UseSRTPData getUseSRTPExtension(Hashtable extensions) throws IOException {
        byte[] extensionData = TlsUtils.getExtensionData(extensions, EXT_use_srtp);
        if (extensionData == null) {
            return null;
        }
        return readUseSRTPExtension(extensionData);
    }

    public static byte[] createUseSRTPExtension(UseSRTPData useSRTPData) throws IOException {
        if (useSRTPData == null) {
            throw new IllegalArgumentException("'useSRTPData' cannot be null");
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        TlsUtils.writeUint16ArrayWithUint16Length(useSRTPData.getProtectionProfiles(), buf);
        TlsUtils.writeOpaque8(useSRTPData.getMki(), buf);
        return buf.toByteArray();
    }

    public static UseSRTPData readUseSRTPExtension(byte[] extensionData) throws IOException {
        if (extensionData == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        ByteArrayInputStream buf = new ByteArrayInputStream(extensionData);
        int length = TlsUtils.readUint16(buf);
        if (length < 2 || (length & 1) != 0) {
            throw new TlsFatalAlert((short) 50);
        }
        int[] protectionProfiles = TlsUtils.readUint16Array(length / 2, buf);
        byte[] mki = TlsUtils.readOpaque8(buf);
        TlsProtocol.assertEmpty(buf);
        return new UseSRTPData(protectionProfiles, mki);
    }
}
