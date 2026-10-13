package org.spongycastle.util.encoders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/encoders/UrlBase64.class */
public class UrlBase64 {
    private static final Encoder encoder = new UrlBase64Encoder();

    public static byte[] encode(byte[] data) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            encoder.encode(data, 0, data.length, bOut);
            return bOut.toByteArray();
        } catch (Exception e) {
            throw new EncoderException("exception encoding URL safe base64 data: " + e.getMessage(), e);
        }
    }

    public static int encode(byte[] data, OutputStream out) throws IOException {
        return encoder.encode(data, 0, data.length, out);
    }

    public static byte[] decode(byte[] data) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            encoder.decode(data, 0, data.length, bOut);
            return bOut.toByteArray();
        } catch (Exception e) {
            throw new DecoderException("exception decoding URL safe base64 string: " + e.getMessage(), e);
        }
    }

    public static int decode(byte[] data, OutputStream out) throws IOException {
        return encoder.decode(data, 0, data.length, out);
    }

    public static byte[] decode(String data) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            encoder.decode(data, bOut);
            return bOut.toByteArray();
        } catch (Exception e) {
            throw new DecoderException("exception decoding URL safe base64 string: " + e.getMessage(), e);
        }
    }

    public static int decode(String data, OutputStream out) throws IOException {
        return encoder.decode(data, out);
    }
}
