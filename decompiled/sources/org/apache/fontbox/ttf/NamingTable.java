package org.apache.fontbox.ttf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.util.Charsets;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/NamingTable.class */
public class NamingTable extends TTFTable {
    public static final String TAG = "name";
    private List<NameRecord> nameRecords;
    private Map<Integer, Map<Integer, Map<Integer, Map<Integer, String>>>> lookupTable;
    private String fontFamily;
    private String fontSubFamily;
    private String psName;

    NamingTable(TrueTypeFont font) {
        super(font);
        this.fontFamily = null;
        this.fontSubFamily = null;
        this.psName = null;
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        data.readUnsignedShort();
        int numberOfNameRecords = data.readUnsignedShort();
        data.readUnsignedShort();
        this.nameRecords = new ArrayList(numberOfNameRecords);
        for (int i = 0; i < numberOfNameRecords; i++) {
            NameRecord nr = new NameRecord();
            nr.initData(ttf, data);
            this.nameRecords.add(nr);
        }
        for (NameRecord nr2 : this.nameRecords) {
            if (nr2.getStringOffset() > getLength()) {
                nr2.setString(null);
            } else {
                data.seek(getOffset() + 6 + ((long) (numberOfNameRecords * 2 * 6)) + ((long) nr2.getStringOffset()));
                int platform = nr2.getPlatformId();
                int encoding = nr2.getPlatformEncodingId();
                Charset charset = Charsets.ISO_8859_1;
                if ((platform == 3 && (encoding == 0 || encoding == 1)) || platform == 0) {
                    charset = Charsets.UTF_16;
                } else if (platform == 2) {
                    switch (encoding) {
                        case 0:
                            charset = Charsets.US_ASCII;
                            break;
                        case 1:
                            charset = Charsets.ISO_10646;
                            break;
                        case 2:
                            charset = Charsets.ISO_8859_1;
                            break;
                    }
                }
                String string = data.readString(nr2.getStringLength(), charset);
                nr2.setString(string);
            }
        }
        this.lookupTable = new HashMap(this.nameRecords.size());
        for (NameRecord nr3 : this.nameRecords) {
            Map<Integer, Map<Integer, Map<Integer, String>>> platformLookup = this.lookupTable.get(Integer.valueOf(nr3.getNameId()));
            if (platformLookup == null) {
                platformLookup = new HashMap();
                this.lookupTable.put(Integer.valueOf(nr3.getNameId()), platformLookup);
            }
            Map<Integer, Map<Integer, String>> encodingLookup = platformLookup.get(Integer.valueOf(nr3.getPlatformId()));
            if (encodingLookup == null) {
                encodingLookup = new HashMap();
                platformLookup.put(Integer.valueOf(nr3.getPlatformId()), encodingLookup);
            }
            Map<Integer, String> languageLookup = encodingLookup.get(Integer.valueOf(nr3.getPlatformEncodingId()));
            if (languageLookup == null) {
                languageLookup = new HashMap();
                encodingLookup.put(Integer.valueOf(nr3.getPlatformEncodingId()), languageLookup);
            }
            languageLookup.put(Integer.valueOf(nr3.getLanguageId()), nr3.getString());
        }
        this.fontFamily = getEnglishName(1);
        this.fontSubFamily = getEnglishName(2);
        this.psName = getName(6, 1, 0, 0);
        if (this.psName == null) {
            this.psName = getName(6, 3, 1, 1033);
        }
        if (this.psName != null) {
            this.psName = this.psName.trim();
        }
        this.initialized = true;
    }

    private String getEnglishName(int nameId) {
        for (int i = 4; i >= 0; i--) {
            String nameUni = getName(nameId, 0, i, 0);
            if (nameUni != null) {
                return nameUni;
            }
        }
        String nameWin = getName(nameId, 3, 1, 1033);
        if (nameWin != null) {
            return nameWin;
        }
        return getName(nameId, 1, 0, 0);
    }

    public String getName(int nameId, int platformId, int encodingId, int languageId) {
        Map<Integer, Map<Integer, String>> encodings;
        Map<Integer, String> languages;
        Map<Integer, Map<Integer, Map<Integer, String>>> platforms = this.lookupTable.get(Integer.valueOf(nameId));
        if (platforms == null || (encodings = platforms.get(Integer.valueOf(platformId))) == null || (languages = encodings.get(Integer.valueOf(encodingId))) == null) {
            return null;
        }
        return languages.get(Integer.valueOf(languageId));
    }

    public List<NameRecord> getNameRecords() {
        return this.nameRecords;
    }

    public String getFontFamily() {
        return this.fontFamily;
    }

    public String getFontSubFamily() {
        return this.fontSubFamily;
    }

    public String getPostScriptName() {
        return this.psName;
    }
}
