package org.ini4j.spi;

import com.ctc.wstx.io.CharsetNames;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.ini4j.Registry;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/RegEscapeTool.class */
public class RegEscapeTool extends EscapeTool {
    private static final RegEscapeTool INSTANCE = (RegEscapeTool) ServiceFinder.findService(RegEscapeTool.class);
    private static final Charset HEX_CHARSET = Charset.forName(CharsetNames.CS_UTF16LE);
    private static final int LOWER_DIGIT = 15;
    private static final int UPPER_DIGIT = 240;
    private static final int DIGIT_SIZE = 4;

    public static final RegEscapeTool getInstance() {
        return INSTANCE;
    }

    public TypeValuesPair decode(String raw) {
        String[] values;
        Registry.Type type = type(raw);
        String value = type == Registry.Type.REG_SZ ? unquote(raw) : raw.substring(type.toString().length() + 1);
        switch (type) {
            case REG_EXPAND_SZ:
            case REG_MULTI_SZ:
                value = bytes2string(binary(value));
                break;
            case REG_DWORD:
                value = String.valueOf(Long.parseLong(value, 16));
                break;
        }
        if (type == Registry.Type.REG_MULTI_SZ) {
            values = splitMulti(value);
        } else {
            values = new String[]{value};
        }
        return new TypeValuesPair(type, values);
    }

    public String encode(TypeValuesPair data) {
        String ret = null;
        if (data.getType() == Registry.Type.REG_SZ) {
            ret = quote(data.getValues()[0]);
        } else if (data.getValues()[0] != null) {
            ret = encode(data.getType(), data.getValues());
        }
        return ret;
    }

    byte[] binary(String value) {
        byte[] bytes = new byte[value.length()];
        int idx = 0;
        int shift = 4;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!Character.isWhitespace(c)) {
                if (c == ',') {
                    idx++;
                    shift = 4;
                } else {
                    int digit = Character.digit(c, 16);
                    if (digit >= 0) {
                        int i2 = idx;
                        bytes[i2] = (byte) (bytes[i2] | (digit << shift));
                        shift = 0;
                    }
                }
            }
        }
        return Arrays.copyOfRange(bytes, 0, idx + 1);
    }

    String encode(Registry.Type type, String[] values) {
        StringBuilder buff = new StringBuilder();
        buff.append(type.toString());
        buff.append(':');
        switch (type) {
            case REG_EXPAND_SZ:
                buff.append(hexadecimal(values[0]));
                break;
            case REG_MULTI_SZ:
                for (String str : values) {
                    buff.append(hexadecimal(str));
                    buff.append(',');
                }
                buff.append("00,00");
                break;
            case REG_DWORD:
                buff.append(String.format("%08x", Long.valueOf(Long.parseLong(values[0]))));
                break;
            default:
                buff.append(values[0]);
                break;
        }
        return buff.toString();
    }

    String hexadecimal(String value) {
        StringBuilder buff = new StringBuilder();
        if (value != null && value.length() != 0) {
            byte[] bytes = string2bytes(value);
            for (int i = 0; i < bytes.length; i++) {
                buff.append(Character.forDigit((bytes[i] & 240) >> 4, 16));
                buff.append(Character.forDigit(bytes[i] & 15, 16));
                buff.append(',');
            }
            buff.append("00,00");
        }
        return buff.toString();
    }

    Registry.Type type(String raw) {
        Registry.Type type;
        if (raw.charAt(0) == '\"') {
            type = Registry.Type.REG_SZ;
        } else {
            int idx = raw.indexOf(58);
            type = idx < 0 ? Registry.Type.REG_SZ : Registry.Type.fromString(raw.substring(0, idx));
        }
        return type;
    }

    private String bytes2string(byte[] bytes) {
        String str;
        try {
            str = new String(bytes, 0, bytes.length - 2, HEX_CHARSET);
        } catch (NoSuchMethodError e) {
            try {
                str = new String(bytes, 0, bytes.length, HEX_CHARSET.name());
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return str;
    }

    private String[] splitMulti(String value) {
        int len = value.length();
        int n = 0;
        int iIndexOf = value.indexOf(0, 0);
        while (true) {
            int end = iIndexOf;
            if (end < 0) {
                break;
            }
            n++;
            int start = end + 1;
            if (start >= len) {
                break;
            }
            iIndexOf = value.indexOf(0, start);
        }
        String[] values = new String[n];
        int start2 = 0;
        for (int i = 0; i < n; i++) {
            int end2 = value.indexOf(0, start2);
            values[i] = value.substring(start2, end2);
            start2 = end2 + 1;
        }
        return values;
    }

    private byte[] string2bytes(String value) {
        byte[] bytes;
        try {
            bytes = value.getBytes(HEX_CHARSET);
        } catch (NoSuchMethodError e) {
            try {
                bytes = value.getBytes(HEX_CHARSET.name());
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return bytes;
    }
}
