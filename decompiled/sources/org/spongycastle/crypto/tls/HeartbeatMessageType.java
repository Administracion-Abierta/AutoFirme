package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/HeartbeatMessageType.class */
public class HeartbeatMessageType {
    public static final short heartbeat_request = 1;
    public static final short heartbeat_response = 2;

    public static boolean isValid(short heartbeatMessageType) {
        return heartbeatMessageType >= 1 && heartbeatMessageType <= 2;
    }
}
