package org.spongycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/HeartbeatMessage.class */
public class HeartbeatMessage {
    protected short type;
    protected byte[] payload;
    protected int paddingLength;

    public HeartbeatMessage(short type, byte[] payload, int paddingLength) {
        if (!HeartbeatMessageType.isValid(type)) {
            throw new IllegalArgumentException("'type' is not a valid HeartbeatMessageType value");
        }
        if (payload == null || payload.length >= 65536) {
            throw new IllegalArgumentException("'payload' must have length < 2^16");
        }
        if (paddingLength < 16) {
            throw new IllegalArgumentException("'paddingLength' must be at least 16");
        }
        this.type = type;
        this.payload = payload;
        this.paddingLength = paddingLength;
    }

    public void encode(TlsContext context, OutputStream output) throws IOException {
        TlsUtils.writeUint8(this.type, output);
        TlsUtils.checkUint16(this.payload.length);
        TlsUtils.writeUint16(this.payload.length, output);
        output.write(this.payload);
        byte[] padding = new byte[this.paddingLength];
        context.getNonceRandomGenerator().nextBytes(padding);
        output.write(padding);
    }

    public static HeartbeatMessage parse(InputStream input) throws IOException {
        short type = TlsUtils.readUint8(input);
        if (!HeartbeatMessageType.isValid(type)) {
            throw new TlsFatalAlert((short) 47);
        }
        int payload_length = TlsUtils.readUint16(input);
        PayloadBuffer buf = new PayloadBuffer();
        Streams.pipeAll(input, buf);
        byte[] payload = buf.toTruncatedByteArray(payload_length);
        if (payload == null) {
            return null;
        }
        int padding_length = buf.size() - payload.length;
        return new HeartbeatMessage(type, payload, padding_length);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/HeartbeatMessage$PayloadBuffer.class */
    static class PayloadBuffer extends ByteArrayOutputStream {
        PayloadBuffer() {
        }

        byte[] toTruncatedByteArray(int payloadLength) {
            int minimumCount = payloadLength + 16;
            if (this.count < minimumCount) {
                return null;
            }
            return Arrays.copyOf(this.buf, payloadLength);
        }
    }
}
