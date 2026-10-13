package org.java_websocket.util;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import org.java_websocket.exceptions.InvalidDataException;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/util/Charsetfunctions.class */
public class Charsetfunctions {
    private static final CodingErrorAction codingErrorAction = CodingErrorAction.REPORT;
    private static final int[] utf8d = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 0, 1, 2, 3, 5, 8, 7, 1, 1, 1, 4, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    private Charsetfunctions() {
    }

    public static byte[] utf8Bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] asciiBytes(String s) {
        return s.getBytes(StandardCharsets.US_ASCII);
    }

    public static String stringAscii(byte[] bytes) {
        return stringAscii(bytes, 0, bytes.length);
    }

    public static String stringAscii(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, StandardCharsets.US_ASCII);
    }

    public static String stringUtf8(byte[] bytes) throws InvalidDataException {
        return stringUtf8(ByteBuffer.wrap(bytes));
    }

    public static String stringUtf8(ByteBuffer bytes) throws InvalidDataException {
        CharsetDecoder decode = StandardCharsets.UTF_8.newDecoder();
        decode.onMalformedInput(codingErrorAction);
        decode.onUnmappableCharacter(codingErrorAction);
        try {
            bytes.mark();
            String s = decode.decode(bytes).toString();
            bytes.reset();
            return s;
        } catch (CharacterCodingException e) {
            throw new InvalidDataException(1007, e);
        }
    }

    public static boolean isValidUTF8(ByteBuffer data, int off) {
        int len = data.remaining();
        if (len < off) {
            return false;
        }
        int state = 0;
        for (int i = off; i < len; i++) {
            state = utf8d[256 + (state << 4) + utf8d[255 & data.get(i)]];
            if (state == 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidUTF8(ByteBuffer data) {
        return isValidUTF8(data, 0);
    }
}
