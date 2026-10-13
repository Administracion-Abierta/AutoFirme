package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsExtensionsUtils.class */
public class TlsExtensionsUtils {
    public static final Integer EXT_encrypt_then_mac = Integers.valueOf(22);
    public static final Integer EXT_extended_master_secret = Integers.valueOf(23);
    public static final Integer EXT_heartbeat = Integers.valueOf(15);
    public static final Integer EXT_max_fragment_length = Integers.valueOf(1);
    public static final Integer EXT_padding = Integers.valueOf(21);
    public static final Integer EXT_server_name = Integers.valueOf(0);
    public static final Integer EXT_status_request = Integers.valueOf(5);
    public static final Integer EXT_truncated_hmac = Integers.valueOf(4);

    public static Hashtable ensureExtensionsInitialised(Hashtable extensions) {
        return extensions == null ? new Hashtable() : extensions;
    }

    public static void addEncryptThenMACExtension(Hashtable extensions) {
        extensions.put(EXT_encrypt_then_mac, createEncryptThenMACExtension());
    }

    public static void addExtendedMasterSecretExtension(Hashtable extensions) {
        extensions.put(EXT_extended_master_secret, createExtendedMasterSecretExtension());
    }

    public static void addHeartbeatExtension(Hashtable extensions, HeartbeatExtension heartbeatExtension) throws IOException {
        extensions.put(EXT_heartbeat, createHeartbeatExtension(heartbeatExtension));
    }

    public static void addMaxFragmentLengthExtension(Hashtable extensions, short maxFragmentLength) throws IOException {
        extensions.put(EXT_max_fragment_length, createMaxFragmentLengthExtension(maxFragmentLength));
    }

    public static void addPaddingExtension(Hashtable extensions, int dataLength) throws IOException {
        extensions.put(EXT_padding, createPaddingExtension(dataLength));
    }

    public static void addServerNameExtension(Hashtable extensions, ServerNameList serverNameList) throws IOException {
        extensions.put(EXT_server_name, createServerNameExtension(serverNameList));
    }

    public static void addStatusRequestExtension(Hashtable extensions, CertificateStatusRequest statusRequest) throws IOException {
        extensions.put(EXT_status_request, createStatusRequestExtension(statusRequest));
    }

    public static void addTruncatedHMacExtension(Hashtable extensions) {
        extensions.put(EXT_truncated_hmac, createTruncatedHMacExtension());
    }

    public static HeartbeatExtension getHeartbeatExtension(Hashtable extensions) throws IOException {
        byte[] extensionData = TlsUtils.getExtensionData(extensions, EXT_heartbeat);
        if (extensionData == null) {
            return null;
        }
        return readHeartbeatExtension(extensionData);
    }

    public static short getMaxFragmentLengthExtension(Hashtable extensions) throws IOException {
        byte[] extensionData = TlsUtils.getExtensionData(extensions, EXT_max_fragment_length);
        if (extensionData == null) {
            return (short) -1;
        }
        return readMaxFragmentLengthExtension(extensionData);
    }

    public static int getPaddingExtension(Hashtable extensions) throws IOException {
        byte[] extensionData = TlsUtils.getExtensionData(extensions, EXT_padding);
        if (extensionData == null) {
            return -1;
        }
        return readPaddingExtension(extensionData);
    }

    public static ServerNameList getServerNameExtension(Hashtable extensions) throws IOException {
        byte[] extensionData = TlsUtils.getExtensionData(extensions, EXT_server_name);
        if (extensionData == null) {
            return null;
        }
        return readServerNameExtension(extensionData);
    }

    public static CertificateStatusRequest getStatusRequestExtension(Hashtable extensions) throws IOException {
        byte[] extensionData = TlsUtils.getExtensionData(extensions, EXT_status_request);
        if (extensionData == null) {
            return null;
        }
        return readStatusRequestExtension(extensionData);
    }

    public static boolean hasEncryptThenMACExtension(Hashtable extensions) throws IOException {
        byte[] extensionData = TlsUtils.getExtensionData(extensions, EXT_encrypt_then_mac);
        if (extensionData == null) {
            return false;
        }
        return readEncryptThenMACExtension(extensionData);
    }

    public static boolean hasExtendedMasterSecretExtension(Hashtable extensions) throws IOException {
        byte[] extensionData = TlsUtils.getExtensionData(extensions, EXT_extended_master_secret);
        if (extensionData == null) {
            return false;
        }
        return readExtendedMasterSecretExtension(extensionData);
    }

    public static boolean hasTruncatedHMacExtension(Hashtable extensions) throws IOException {
        byte[] extensionData = TlsUtils.getExtensionData(extensions, EXT_truncated_hmac);
        if (extensionData == null) {
            return false;
        }
        return readTruncatedHMacExtension(extensionData);
    }

    public static byte[] createEmptyExtensionData() {
        return TlsUtils.EMPTY_BYTES;
    }

    public static byte[] createEncryptThenMACExtension() {
        return createEmptyExtensionData();
    }

    public static byte[] createExtendedMasterSecretExtension() {
        return createEmptyExtensionData();
    }

    public static byte[] createHeartbeatExtension(HeartbeatExtension heartbeatExtension) throws IOException {
        if (heartbeatExtension == null) {
            throw new TlsFatalAlert((short) 80);
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        heartbeatExtension.encode(buf);
        return buf.toByteArray();
    }

    public static byte[] createMaxFragmentLengthExtension(short maxFragmentLength) throws IOException {
        TlsUtils.checkUint8(maxFragmentLength);
        byte[] extensionData = new byte[1];
        TlsUtils.writeUint8(maxFragmentLength, extensionData, 0);
        return extensionData;
    }

    public static byte[] createPaddingExtension(int dataLength) throws IOException {
        TlsUtils.checkUint16(dataLength);
        return new byte[dataLength];
    }

    public static byte[] createServerNameExtension(ServerNameList serverNameList) throws IOException {
        if (serverNameList == null) {
            throw new TlsFatalAlert((short) 80);
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        serverNameList.encode(buf);
        return buf.toByteArray();
    }

    public static byte[] createStatusRequestExtension(CertificateStatusRequest statusRequest) throws IOException {
        if (statusRequest == null) {
            throw new TlsFatalAlert((short) 80);
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        statusRequest.encode(buf);
        return buf.toByteArray();
    }

    public static byte[] createTruncatedHMacExtension() {
        return createEmptyExtensionData();
    }

    private static boolean readEmptyExtensionData(byte[] extensionData) throws IOException {
        if (extensionData == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        if (extensionData.length != 0) {
            throw new TlsFatalAlert((short) 47);
        }
        return true;
    }

    public static boolean readEncryptThenMACExtension(byte[] extensionData) throws IOException {
        return readEmptyExtensionData(extensionData);
    }

    public static boolean readExtendedMasterSecretExtension(byte[] extensionData) throws IOException {
        return readEmptyExtensionData(extensionData);
    }

    public static HeartbeatExtension readHeartbeatExtension(byte[] extensionData) throws IOException {
        if (extensionData == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        ByteArrayInputStream buf = new ByteArrayInputStream(extensionData);
        HeartbeatExtension heartbeatExtension = HeartbeatExtension.parse(buf);
        TlsProtocol.assertEmpty(buf);
        return heartbeatExtension;
    }

    public static short readMaxFragmentLengthExtension(byte[] extensionData) throws IOException {
        if (extensionData == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        if (extensionData.length != 1) {
            throw new TlsFatalAlert((short) 50);
        }
        return TlsUtils.readUint8(extensionData, 0);
    }

    public static int readPaddingExtension(byte[] extensionData) throws IOException {
        if (extensionData == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        for (byte b : extensionData) {
            if (b != 0) {
                throw new TlsFatalAlert((short) 47);
            }
        }
        return extensionData.length;
    }

    public static ServerNameList readServerNameExtension(byte[] extensionData) throws IOException {
        if (extensionData == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        ByteArrayInputStream buf = new ByteArrayInputStream(extensionData);
        ServerNameList serverNameList = ServerNameList.parse(buf);
        TlsProtocol.assertEmpty(buf);
        return serverNameList;
    }

    public static CertificateStatusRequest readStatusRequestExtension(byte[] extensionData) throws IOException {
        if (extensionData == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        ByteArrayInputStream buf = new ByteArrayInputStream(extensionData);
        CertificateStatusRequest statusRequest = CertificateStatusRequest.parse(buf);
        TlsProtocol.assertEmpty(buf);
        return statusRequest;
    }

    public static boolean readTruncatedHMacExtension(byte[] extensionData) throws IOException {
        return readEmptyExtensionData(extensionData);
    }
}
