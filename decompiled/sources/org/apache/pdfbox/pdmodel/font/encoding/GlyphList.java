package org.apache.pdfbox.pdmodel.font.encoding;

import com.aowagie.text.pdf.BaseFont;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.IOUtils;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/encoding/GlyphList.class */
public final class GlyphList {
    private static final Log LOG = LogFactory.getLog(GlyphList.class);
    private static final GlyphList DEFAULT = load("glyphlist.txt", 4281);
    private static final GlyphList ZAPF_DINGBATS = load("zapfdingbats.txt", 201);
    private final Map<String, String> nameToUnicode;
    private final Map<String, String> unicodeToName;
    private final Map<String, String> uniNameToUnicodeCache = new ConcurrentHashMap();

    static {
        try {
            String location = System.getProperty("glyphlist_ext");
            if (location != null) {
                throw new UnsupportedOperationException("glyphlist_ext is no longer supported, use GlyphList.DEFAULT.addGlyphs(Properties) instead");
            }
        } catch (SecurityException e) {
        }
    }

    private static GlyphList load(String filename, int numberOfEntries) {
        String path = "/org/apache/pdfbox/resources/glyphlist/" + filename;
        try {
            try {
                InputStream resourceAsStream = GlyphList.class.getResourceAsStream(path);
                if (resourceAsStream == null) {
                    throw new IOException("GlyphList '" + path + "' not found");
                }
                GlyphList glyphList = new GlyphList(resourceAsStream, numberOfEntries);
                IOUtils.closeQuietly(resourceAsStream);
                return glyphList;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            IOUtils.closeQuietly(null);
            throw th;
        }
    }

    public static GlyphList getAdobeGlyphList() {
        return DEFAULT;
    }

    public static GlyphList getZapfDingbats() {
        return ZAPF_DINGBATS;
    }

    public GlyphList(InputStream input, int numberOfEntries) throws IOException {
        this.nameToUnicode = new HashMap(numberOfEntries);
        this.unicodeToName = new HashMap(numberOfEntries);
        loadList(input);
    }

    public GlyphList(GlyphList glyphList, InputStream input) throws IOException {
        this.nameToUnicode = new HashMap(glyphList.nameToUnicode);
        this.unicodeToName = new HashMap(glyphList.unicodeToName);
        loadList(input);
    }

    private void loadList(InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input, "ISO-8859-1"));
        while (in.ready()) {
            try {
                String line = in.readLine();
                if (line != null && !line.startsWith("#")) {
                    String[] parts = line.split(PolicyIdFilter.OID_SEPARATOR);
                    if (parts.length < 2) {
                        throw new IOException("Invalid glyph list entry: " + line);
                    }
                    String name = parts[0];
                    String[] unicodeList = parts[1].split(" ");
                    if (this.nameToUnicode.containsKey(name)) {
                        LOG.warn("duplicate value for " + name + " -> " + parts[1] + " " + this.nameToUnicode.get(name));
                    }
                    int[] codePoints = new int[unicodeList.length];
                    int index = 0;
                    for (String hex : unicodeList) {
                        int i = index;
                        index++;
                        codePoints[i] = Integer.parseInt(hex, 16);
                    }
                    String string = new String(codePoints, 0, codePoints.length);
                    this.nameToUnicode.put(name, string);
                    boolean forceOverride = WinAnsiEncoding.INSTANCE.contains(name) || MacRomanEncoding.INSTANCE.contains(name) || MacExpertEncoding.INSTANCE.contains(name) || SymbolEncoding.INSTANCE.contains(name) || ZapfDingbatsEncoding.INSTANCE.contains(name);
                    if (!this.unicodeToName.containsKey(string) || forceOverride) {
                        this.unicodeToName.put(string, name);
                    }
                }
            } catch (Throwable th) {
                in.close();
                throw th;
            }
        }
        in.close();
    }

    public String codePointToName(int codePoint) {
        String name = this.unicodeToName.get(new String(new int[]{codePoint}, 0, 1));
        if (name == null) {
            return BaseFont.notdef;
        }
        return name;
    }

    public String sequenceToName(String unicodeSequence) {
        String name = this.unicodeToName.get(unicodeSequence);
        if (name == null) {
            return BaseFont.notdef;
        }
        return name;
    }

    public String toUnicode(String name) {
        if (name == null) {
            return null;
        }
        String unicode = this.nameToUnicode.get(name);
        if (unicode != null) {
            return unicode;
        }
        String unicode2 = this.uniNameToUnicodeCache.get(name);
        if (unicode2 == null) {
            if (name.indexOf(46) > 0) {
                unicode2 = toUnicode(name.substring(0, name.indexOf(46)));
            } else if (name.startsWith("uni") && name.length() == 7) {
                int nameLength = name.length();
                StringBuilder uniStr = new StringBuilder();
                for (int chPos = 3; chPos + 4 <= nameLength; chPos += 4) {
                    try {
                        int codePoint = Integer.parseInt(name.substring(chPos, chPos + 4), 16);
                        if (codePoint > 55295 && codePoint < 57344) {
                            LOG.warn("Unicode character name with disallowed code area: " + name);
                        } else {
                            uniStr.append((char) codePoint);
                        }
                    } catch (NumberFormatException e) {
                        LOG.warn("Not a number in Unicode character name: " + name);
                    }
                }
                unicode2 = uniStr.toString();
            } else if (name.startsWith("u") && name.length() == 5) {
                try {
                    int codePoint2 = Integer.parseInt(name.substring(1), 16);
                    if (codePoint2 > 55295 && codePoint2 < 57344) {
                        LOG.warn("Unicode character name with disallowed code area: " + name);
                    } else {
                        unicode2 = String.valueOf((char) codePoint2);
                    }
                } catch (NumberFormatException e2) {
                    LOG.warn("Not a number in Unicode character name: " + name);
                }
            }
            if (unicode2 != null) {
                this.uniNameToUnicodeCache.put(name, unicode2);
            }
        }
        return unicode2;
    }
}
