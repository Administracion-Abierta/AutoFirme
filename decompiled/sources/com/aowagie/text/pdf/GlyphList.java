package com.aowagie.text.pdf;

import com.aowagie.text.pdf.fonts.FontsResourceAnchor;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/GlyphList.class */
class GlyphList {
    private static HashMap unicode2names = new HashMap();
    private static HashMap names2unicode = new HashMap();

    GlyphList() {
    }

    static {
        InputStream is = null;
        try {
            try {
                InputStream is2 = BaseFont.getResourceStream("com/lowagie/text/pdf/fonts/glyphlist.txt", new FontsResourceAnchor().getClass().getClassLoader());
                if (is2 == null) {
                    throw new Exception("glyphlist.txt not found as resource. (It must exist as resource in the package com.aowagie.text.pdf.fonts)");
                }
                byte[] buf = new byte[1024];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while (true) {
                    int size = is2.read(buf);
                    if (size < 0) {
                        break;
                    } else {
                        out.write(buf, 0, size);
                    }
                }
                is2.close();
                InputStream is3 = null;
                String s = PdfEncodings.convertToString(out.toByteArray(), null);
                StringTokenizer tk = new StringTokenizer(s, "\r\n");
                while (tk.hasMoreTokens()) {
                    String line = tk.nextToken();
                    if (!line.startsWith("#")) {
                        StringTokenizer t2 = new StringTokenizer(line, " ;\r\n\t\f");
                        if (t2.hasMoreTokens()) {
                            String name = t2.nextToken();
                            if (t2.hasMoreTokens()) {
                                String hex = t2.nextToken();
                                Integer num = Integer.valueOf(hex, 16);
                                unicode2names.put(num, name);
                                names2unicode.put(name, new int[]{num.intValue()});
                            }
                        }
                    }
                }
                if (0 != 0) {
                    try {
                        is3.close();
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e2) {
                System.err.println("glyphlist.txt loading error: " + e2.getMessage());
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (Exception e3) {
                    }
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    is.close();
                } catch (Exception e4) {
                }
            }
            throw th;
        }
    }

    public static int[] nameToUnicode(String name) {
        return (int[]) names2unicode.get(name);
    }

    public static String unicodeToName(int num) {
        return (String) unicode2names.get(Integer.valueOf(num));
    }
}
