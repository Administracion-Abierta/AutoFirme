package org.apache.pdfbox.cos;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/PDFDocEncoding.class */
final class PDFDocEncoding {
    private static final char REPLACEMENT_CHARACTER = 65533;
    private static final int[] CODE_TO_UNI = new int[256];
    private static final Map<Character, Integer> UNI_TO_CODE = new HashMap(256);

    static {
        for (int i = 0; i < 256; i++) {
            if ((i <= 23 || i >= 32) && ((i <= 126 || i >= 161) && i != 173)) {
                set(i, (char) i);
            }
        }
        set(24, (char) 728);
        set(25, (char) 711);
        set(26, (char) 710);
        set(27, (char) 729);
        set(28, (char) 733);
        set(29, (char) 731);
        set(30, (char) 730);
        set(31, (char) 732);
        set(127, (char) 65533);
        set(128, (char) 8226);
        set(129, (char) 8224);
        set(130, (char) 8225);
        set(131, (char) 8230);
        set(132, (char) 8212);
        set(133, (char) 8211);
        set(134, (char) 402);
        set(135, (char) 8260);
        set(136, (char) 8249);
        set(137, (char) 8250);
        set(138, (char) 8722);
        set(139, (char) 8240);
        set(140, (char) 8222);
        set(141, (char) 8220);
        set(142, (char) 8221);
        set(143, (char) 8216);
        set(144, (char) 8217);
        set(145, (char) 8218);
        set(146, (char) 8482);
        set(147, (char) 64257);
        set(148, (char) 64258);
        set(149, (char) 321);
        set(150, (char) 338);
        set(151, (char) 352);
        set(152, (char) 376);
        set(153, (char) 381);
        set(154, (char) 305);
        set(155, (char) 322);
        set(156, (char) 339);
        set(157, (char) 353);
        set(158, (char) 382);
        set(159, (char) 65533);
        set(160, (char) 8364);
    }

    private PDFDocEncoding() {
    }

    private static void set(int code, char unicode) {
        CODE_TO_UNI[code] = unicode;
        UNI_TO_CODE.put(Character.valueOf(unicode), Integer.valueOf(code));
    }

    public static String toString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            if ((b & 255) >= CODE_TO_UNI.length) {
                sb.append('?');
            } else {
                sb.append((char) CODE_TO_UNI[b & 255]);
            }
        }
        return sb.toString();
    }

    public static byte[] getBytes(String text) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (char c : text.toCharArray()) {
            Integer code = UNI_TO_CODE.get(Character.valueOf(c));
            if (code == null) {
                out.write(0);
            } else {
                out.write(code.intValue());
            }
        }
        return out.toByteArray();
    }

    public static boolean containsChar(char character) {
        return UNI_TO_CODE.containsKey(Character.valueOf(character));
    }
}
