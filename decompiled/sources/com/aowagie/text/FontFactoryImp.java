package com.aowagie.text;

import com.aowagie.text.html.Markup;
import com.aowagie.text.pdf.BaseFont;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/FontFactoryImp.class */
public class FontFactoryImp {
    private static String[] TTFamilyOrder = {"3", "1", "1033", "3", "0", "1033", "1", "0", "0", "0", "3", "0"};
    private final Properties trueTypeFonts = new Properties();
    private final Hashtable fontFamilies = new Hashtable();
    public String defaultEncoding = "Cp1252";
    public boolean defaultEmbedding = false;

    public FontFactoryImp() {
        this.trueTypeFonts.setProperty("Courier".toLowerCase(), "Courier");
        this.trueTypeFonts.setProperty("Courier-Bold".toLowerCase(), "Courier-Bold");
        this.trueTypeFonts.setProperty("Courier-Oblique".toLowerCase(), "Courier-Oblique");
        this.trueTypeFonts.setProperty("Courier-BoldOblique".toLowerCase(), "Courier-BoldOblique");
        this.trueTypeFonts.setProperty("Helvetica".toLowerCase(), "Helvetica");
        this.trueTypeFonts.setProperty("Helvetica-Bold".toLowerCase(), "Helvetica-Bold");
        this.trueTypeFonts.setProperty("Helvetica-Oblique".toLowerCase(), "Helvetica-Oblique");
        this.trueTypeFonts.setProperty("Helvetica-BoldOblique".toLowerCase(), "Helvetica-BoldOblique");
        this.trueTypeFonts.setProperty("Symbol".toLowerCase(), "Symbol");
        this.trueTypeFonts.setProperty("Times-Roman".toLowerCase(), "Times-Roman");
        this.trueTypeFonts.setProperty("Times-Bold".toLowerCase(), "Times-Bold");
        this.trueTypeFonts.setProperty("Times-Italic".toLowerCase(), "Times-Italic");
        this.trueTypeFonts.setProperty("Times-BoldItalic".toLowerCase(), "Times-BoldItalic");
        this.trueTypeFonts.setProperty("ZapfDingbats".toLowerCase(), "ZapfDingbats");
        ArrayList tmp = new ArrayList();
        tmp.add("Courier");
        tmp.add("Courier-Bold");
        tmp.add("Courier-Oblique");
        tmp.add("Courier-BoldOblique");
        this.fontFamilies.put("Courier".toLowerCase(), tmp);
        ArrayList tmp2 = new ArrayList();
        tmp2.add("Helvetica");
        tmp2.add("Helvetica-Bold");
        tmp2.add("Helvetica-Oblique");
        tmp2.add("Helvetica-BoldOblique");
        this.fontFamilies.put("Helvetica".toLowerCase(), tmp2);
        ArrayList tmp3 = new ArrayList();
        tmp3.add("Symbol");
        this.fontFamilies.put("Symbol".toLowerCase(), tmp3);
        ArrayList tmp4 = new ArrayList();
        tmp4.add("Times-Roman");
        tmp4.add("Times-Bold");
        tmp4.add("Times-Italic");
        tmp4.add("Times-BoldItalic");
        this.fontFamilies.put(FontFactory.TIMES.toLowerCase(), tmp4);
        this.fontFamilies.put("Times-Roman".toLowerCase(), tmp4);
        ArrayList tmp5 = new ArrayList();
        tmp5.add("ZapfDingbats");
        this.fontFamilies.put("ZapfDingbats".toLowerCase(), tmp5);
    }

    public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, Color color) {
        return getFont(fontname, encoding, embedded, size, style, color, true);
    }

    public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, Color color, boolean cached) {
        if (fontname == null) {
            return new Font(-1, size, style, color);
        }
        String lowercasefontname = fontname.toLowerCase();
        ArrayList<String> tmp = (ArrayList) this.fontFamilies.get(lowercasefontname);
        if (tmp != null) {
            int s = style == -1 ? 0 : style;
            int fs = 0;
            boolean found = false;
            for (String f : tmp) {
                String lcf = f.toLowerCase();
                fs = 0;
                if (lcf.toLowerCase().indexOf(Markup.CSS_VALUE_BOLD) != -1) {
                    fs = 0 | 1;
                }
                if (lcf.toLowerCase().indexOf(Markup.CSS_VALUE_ITALIC) != -1 || lcf.toLowerCase().indexOf(Markup.CSS_VALUE_OBLIQUE) != -1) {
                    fs |= 2;
                }
                if ((s & 3) == fs) {
                    fontname = f;
                    found = true;
                    break;
                }
            }
            if (style != -1 && found) {
                style &= fs ^ (-1);
            }
        }
        BaseFont basefont = null;
        try {
            try {
                basefont = BaseFont.createFont(fontname, encoding, embedded, cached, null, null, true);
            } catch (DocumentException e) {
            }
            if (basefont == null) {
                String fontname2 = this.trueTypeFonts.getProperty(fontname.toLowerCase());
                if (fontname2 == null) {
                    return new Font(-1, size, style, color);
                }
                basefont = BaseFont.createFont(fontname2, encoding, embedded, cached, null, null);
            }
            return new Font(basefont, size, style, color);
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        } catch (IOException e2) {
            return new Font(-1, size, style, color);
        } catch (NullPointerException e3) {
            return new Font(-1, size, style, color);
        }
    }

    public Font getFont(Properties attributes) {
        String fontname = null;
        String encoding = this.defaultEncoding;
        boolean embedded = this.defaultEmbedding;
        float size = -1.0f;
        int style = 0;
        Color color = null;
        String value = attributes.getProperty(Markup.HTML_ATTR_STYLE);
        if (value != null && value.length() > 0) {
            Properties styleAttributes = Markup.parseAttributes(value);
            if (styleAttributes.isEmpty()) {
                attributes.put(Markup.HTML_ATTR_STYLE, value);
            } else {
                fontname = styleAttributes.getProperty(Markup.CSS_KEY_FONTFAMILY);
                if (fontname != null) {
                    while (fontname.indexOf(44) != -1) {
                        String tmp = fontname.substring(0, fontname.indexOf(44));
                        if (isRegistered(tmp)) {
                            fontname = tmp;
                        } else {
                            fontname = fontname.substring(fontname.indexOf(44) + 1);
                        }
                    }
                }
                String value2 = styleAttributes.getProperty(Markup.CSS_KEY_FONTSIZE);
                if (value2 != null) {
                    size = Markup.parseLength(value2);
                }
                String value3 = styleAttributes.getProperty(Markup.CSS_KEY_FONTWEIGHT);
                if (value3 != null) {
                    style = 0 | Font.getStyleValue(value3);
                }
                String value4 = styleAttributes.getProperty(Markup.CSS_KEY_FONTSTYLE);
                if (value4 != null) {
                    style |= Font.getStyleValue(value4);
                }
                String value5 = styleAttributes.getProperty("color");
                if (value5 != null) {
                    color = Markup.decodeColor(value5);
                }
                attributes.putAll(styleAttributes);
                Enumeration e = styleAttributes.keys();
                while (e.hasMoreElements()) {
                    Object o = e.nextElement();
                    attributes.put(o, styleAttributes.get(o));
                }
            }
        }
        String value6 = attributes.getProperty("encoding");
        if (value6 != null) {
            encoding = value6;
        }
        if ("true".equals(attributes.getProperty("embedded"))) {
            embedded = true;
        }
        String value7 = attributes.getProperty(ElementTags.FONT);
        if (value7 != null) {
            fontname = value7;
        }
        String value8 = attributes.getProperty(ElementTags.SIZE);
        if (value8 != null) {
            size = Markup.parseLength(value8);
        }
        String value9 = attributes.getProperty(Markup.HTML_ATTR_STYLE);
        if (value9 != null) {
            style |= Font.getStyleValue(value9);
        }
        String value10 = attributes.getProperty(ElementTags.STYLE);
        if (value10 != null) {
            style |= Font.getStyleValue(value10);
        }
        String r = attributes.getProperty(ElementTags.RED);
        String g = attributes.getProperty(ElementTags.GREEN);
        String b = attributes.getProperty(ElementTags.BLUE);
        if (r != null || g != null || b != null) {
            int red = 0;
            int green = 0;
            int blue = 0;
            if (r != null) {
                red = Integer.parseInt(r);
            }
            if (g != null) {
                green = Integer.parseInt(g);
            }
            if (b != null) {
                blue = Integer.parseInt(b);
            }
            color = new Color(red, green, blue);
        } else {
            String value11 = attributes.getProperty("color");
            if (value11 != null) {
                color = Markup.decodeColor(value11);
            }
        }
        if (fontname == null) {
            return getFont(null, encoding, embedded, size, style, color);
        }
        return getFont(fontname, encoding, embedded, size, style, color);
    }

    public Font getFont(String fontname, String encoding, boolean embedded, float size, int style) {
        return getFont(fontname, encoding, embedded, size, style, null);
    }

    public Font getFont(String fontname, String encoding, boolean embedded, float size) {
        return getFont(fontname, encoding, embedded, size, -1, null);
    }

    public Font getFont(String fontname, String encoding, boolean embedded) {
        return getFont(fontname, encoding, embedded, -1.0f, -1, null);
    }

    public Font getFont(String fontname, String encoding, float size, int style, Color color) {
        return getFont(fontname, encoding, this.defaultEmbedding, size, style, color);
    }

    public Font getFont(String fontname, String encoding, float size, int style) {
        return getFont(fontname, encoding, this.defaultEmbedding, size, style, null);
    }

    public Font getFont(String fontname, String encoding, float size) {
        return getFont(fontname, encoding, this.defaultEmbedding, size, -1, null);
    }

    public Font getFont(String fontname, float size, Color color) {
        return getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, -1, color);
    }

    public Font getFont(String fontname, String encoding) {
        return getFont(fontname, encoding, this.defaultEmbedding, -1.0f, -1, null);
    }

    public Font getFont(String fontname, float size, int style, Color color) {
        return getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, style, color);
    }

    public Font getFont(String fontname, float size, int style) {
        return getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, style, null);
    }

    public Font getFont(String fontname, float size) {
        return getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, -1, null);
    }

    public Font getFont(String fontname) {
        return getFont(fontname, this.defaultEncoding, this.defaultEmbedding, -1.0f, -1, null);
    }

    public void registerFamily(String familyName, String fullName, String path) {
        if (path != null) {
            this.trueTypeFonts.setProperty(fullName, path);
        }
        ArrayList tmp = (ArrayList) this.fontFamilies.get(familyName);
        if (tmp == null) {
            ArrayList tmp2 = new ArrayList();
            tmp2.add(fullName);
            this.fontFamilies.put(familyName, tmp2);
            return;
        }
        int fullNameLength = fullName.length();
        boolean inserted = false;
        for (int j = 0; j < tmp.size(); j++) {
            if (((String) tmp.get(j)).length() >= fullNameLength) {
                tmp.add(j, fullName);
                inserted = true;
                break;
            }
        }
        if (!inserted) {
            tmp.add(fullName);
        }
    }

    public void register(String path) {
        register(path, null);
    }

    public void register(String path, String alias) {
        try {
            if (path.toLowerCase().endsWith(".ttf") || path.toLowerCase().endsWith(".otf") || path.toLowerCase().indexOf(".ttc,") > 0) {
                Object[] allNames = BaseFont.getAllFontNames(path, "Cp1252", null);
                this.trueTypeFonts.setProperty(((String) allNames[0]).toLowerCase(), path);
                if (alias != null) {
                    this.trueTypeFonts.setProperty(alias.toLowerCase(), path);
                }
                for (String[] strArr : (String[][]) allNames[2]) {
                    this.trueTypeFonts.setProperty(strArr[3].toLowerCase(), path);
                }
                String familyName = null;
                String[][] names = (String[][]) allNames[1];
                int k = 0;
                while (k < TTFamilyOrder.length) {
                    for (String[] name : names) {
                        if (TTFamilyOrder[k].equals(name[0]) && TTFamilyOrder[k + 1].equals(name[1]) && TTFamilyOrder[k + 2].equals(name[2])) {
                            familyName = name[3].toLowerCase();
                            k = TTFamilyOrder.length;
                            break;
                        }
                    }
                    k += 3;
                }
                if (familyName != null) {
                    Object lastName = "";
                    String[][] names2 = (String[][]) allNames[2];
                    for (String[] name2 : names2) {
                        for (int k2 = 0; k2 < TTFamilyOrder.length; k2 += 3) {
                            if (TTFamilyOrder[k2].equals(name2[0]) && TTFamilyOrder[k2 + 1].equals(name2[1]) && TTFamilyOrder[k2 + 2].equals(name2[2])) {
                                String fullName = name2[3];
                                if (!fullName.equals(lastName)) {
                                    lastName = fullName;
                                    registerFamily(familyName, fullName, null);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (path.toLowerCase().endsWith(".ttc")) {
                if (alias != null) {
                    System.err.println("class FontFactory: You can't define an alias for a true type collection.");
                }
                String[] names3 = BaseFont.enumerateTTCNames(path);
                for (int i = 0; i < names3.length; i++) {
                    register(path + "," + i);
                }
            } else if (path.toLowerCase().endsWith(".afm") || path.toLowerCase().endsWith(".pfm")) {
                BaseFont bf = BaseFont.createFont(path, "Cp1252", false);
                String fullName2 = bf.getFullFontName()[0][3].toLowerCase();
                String familyName2 = bf.getFamilyFontName()[0][3].toLowerCase();
                String psName = bf.getPostscriptFontName().toLowerCase();
                registerFamily(familyName2, fullName2, null);
                this.trueTypeFonts.setProperty(psName, path);
                this.trueTypeFonts.setProperty(fullName2, path);
            }
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        } catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    public int registerDirectory(String dir) {
        return registerDirectory(dir, false);
    }

    public int registerDirectory(String dir, boolean scanSubdirectories) {
        String[] files;
        int count = 0;
        try {
            File file = new File(dir);
            if (!file.exists() || !file.isDirectory() || (files = file.list()) == null) {
                return 0;
            }
            for (String file2 : files) {
                try {
                    File file3 = new File(dir, file2);
                    if (file3.isDirectory()) {
                        if (scanSubdirectories) {
                            count += registerDirectory(file3.getAbsolutePath(), true);
                        }
                    } else {
                        String name = file3.getPath();
                        String suffix = name.length() < 4 ? null : name.substring(name.length() - 4).toLowerCase();
                        if (".afm".equals(suffix) || ".pfm".equals(suffix)) {
                            File pfb = new File(name.substring(0, name.length() - 4) + ".pfb");
                            if (pfb.exists()) {
                                register(name, null);
                                count++;
                            }
                        } else if (".ttf".equals(suffix) || ".otf".equals(suffix) || ".ttc".equals(suffix)) {
                            register(name, null);
                            count++;
                        }
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e2) {
        }
        return count;
    }

    public int registerDirectories() {
        int count = 0 + registerDirectory("c:/windows/fonts");
        return count + registerDirectory("c:/winnt/fonts") + registerDirectory("d:/windows/fonts") + registerDirectory("d:/winnt/fonts") + registerDirectory("/usr/share/X11/fonts", true) + registerDirectory("/usr/X/lib/X11/fonts", true) + registerDirectory("/usr/openwin/lib/X11/fonts", true) + registerDirectory("/usr/share/fonts", true) + registerDirectory("/usr/X11R6/lib/X11/fonts", true) + registerDirectory("/Library/Fonts") + registerDirectory("/System/Library/Fonts");
    }

    public Set getRegisteredFonts() {
        return Utilities.getKeySet(this.trueTypeFonts);
    }

    public Set getRegisteredFamilies() {
        return Utilities.getKeySet(this.fontFamilies);
    }

    public boolean isRegistered(String fontname) {
        return this.trueTypeFonts.containsKey(fontname.toLowerCase());
    }
}
