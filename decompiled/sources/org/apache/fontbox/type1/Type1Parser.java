package org.apache.fontbox.type1;

import com.ctc.wstx.cfg.XmlConsts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.afm.AFMParser;
import org.apache.fontbox.encoding.BuiltInEncoding;
import org.apache.fontbox.encoding.StandardEncoding;
import org.apache.pdfbox.pdfparser.BaseParser;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/type1/Type1Parser.class */
final class Type1Parser {
    private static final int EEXEC_KEY = 55665;
    private static final int CHARSTRING_KEY = 4330;
    private Type1Lexer lexer;
    private Type1Font font;

    Type1Parser() {
    }

    public Type1Font parse(byte[] segment1, byte[] segment2) throws IOException {
        this.font = new Type1Font(segment1, segment2);
        parseASCII(segment1);
        if (segment2.length > 0) {
            parseBinary(segment2);
        }
        return this.font;
    }

    private void parseASCII(byte[] bytes) throws IOException {
        Token token;
        if (bytes.length == 0) {
            throw new IOException("ASCII segment of type 1 font is empty");
        }
        if (bytes.length < 2 || (bytes[0] != 37 && bytes[1] != 33)) {
            throw new IOException("Invalid start of ASCII segment of type 1 font");
        }
        this.lexer = new Type1Lexer(bytes);
        if (this.lexer.peekToken().getText().equals("FontDirectory")) {
            read(Token.NAME, "FontDirectory");
            read(Token.LITERAL);
            read(Token.NAME, "known");
            read(Token.START_PROC);
            readProcVoid();
            read(Token.START_PROC);
            readProcVoid();
            read(Token.NAME, "ifelse");
        }
        int length = read(Token.INTEGER).intValue();
        read(Token.NAME, "dict");
        readMaybe(Token.NAME, "dup");
        read(Token.NAME, "begin");
        for (int i = 0; i < length && (token = this.lexer.peekToken()) != null && (token.getKind() != Token.NAME || (!token.getText().equals("currentdict") && !token.getText().equals("end"))); i++) {
            String key = read(Token.LITERAL).getText();
            if (key.equals("FontInfo") || key.equals("Fontinfo")) {
                readFontInfo(readSimpleDict());
            } else if (key.equals("Metrics")) {
                readSimpleDict();
            } else if (key.equals("Encoding")) {
                readEncoding();
            } else {
                readSimpleValue(key);
            }
        }
        readMaybe(Token.NAME, "currentdict");
        read(Token.NAME, "end");
        read(Token.NAME, "currentfile");
        read(Token.NAME, "eexec");
    }

    private void readSimpleValue(String key) throws IOException {
        List<Token> value = readDictValue();
        if (key.equals(AFMParser.FONT_NAME)) {
            this.font.fontName = value.get(0).getText();
            return;
        }
        if (key.equals("PaintType")) {
            this.font.paintType = value.get(0).intValue();
            return;
        }
        if (key.equals("FontType")) {
            this.font.fontType = value.get(0).intValue();
            return;
        }
        if (key.equals("FontMatrix")) {
            this.font.fontMatrix = arrayToNumbers(value);
            return;
        }
        if (key.equals(AFMParser.FONT_BBOX)) {
            this.font.fontBBox = arrayToNumbers(value);
            return;
        }
        if (key.equals("UniqueID")) {
            this.font.uniqueID = value.get(0).intValue();
        } else if (key.equals("StrokeWidth")) {
            this.font.strokeWidth = value.get(0).floatValue();
        } else if (key.equals("FID")) {
            this.font.fontID = value.get(0).getText();
        }
    }

    private void readEncoding() throws IOException {
        if (this.lexer.peekToken().getKind() == Token.NAME) {
            String name = this.lexer.nextToken().getText();
            if (name.equals("StandardEncoding")) {
                this.font.encoding = StandardEncoding.INSTANCE;
                readMaybe(Token.NAME, "readonly");
                read(Token.NAME, BaseParser.DEF);
                return;
            }
            throw new IOException("Unknown encoding: " + name);
        }
        read(Token.INTEGER).intValue();
        readMaybe(Token.NAME, "array");
        while (true) {
            if (this.lexer.peekToken().getKind() == Token.NAME && (this.lexer.peekToken().getText().equals("dup") || this.lexer.peekToken().getText().equals("readonly") || this.lexer.peekToken().getText().equals(BaseParser.DEF))) {
                break;
            } else {
                this.lexer.nextToken();
            }
        }
        Map<Integer, String> codeToName = new HashMap<>();
        while (this.lexer.peekToken().getKind() == Token.NAME && this.lexer.peekToken().getText().equals("dup")) {
            read(Token.NAME, "dup");
            int code = read(Token.INTEGER).intValue();
            String name2 = read(Token.LITERAL).getText();
            read(Token.NAME, "put");
            codeToName.put(Integer.valueOf(code), name2);
        }
        this.font.encoding = new BuiltInEncoding(codeToName);
        readMaybe(Token.NAME, "readonly");
        read(Token.NAME, BaseParser.DEF);
    }

    private List<Number> arrayToNumbers(List<Token> value) throws IOException {
        List<Number> numbers = new ArrayList<>();
        int size = value.size() - 1;
        for (int i = 1; i < size; i++) {
            Token token = value.get(i);
            if (token.getKind() == Token.REAL) {
                numbers.add(Float.valueOf(token.floatValue()));
            } else if (token.getKind() == Token.INTEGER) {
                numbers.add(Integer.valueOf(token.intValue()));
            } else {
                throw new IOException("Expected INTEGER or REAL but got " + token + " at array position " + i);
            }
        }
        return numbers;
    }

    private void readFontInfo(Map<String, List<Token>> fontInfo) {
        for (Map.Entry<String, List<Token>> entry : fontInfo.entrySet()) {
            String key = entry.getKey();
            List<Token> value = entry.getValue();
            if (key.equals(XmlConsts.XML_DECL_KW_VERSION)) {
                this.font.version = value.get(0).getText();
            } else if (key.equals(AFMParser.NOTICE)) {
                this.font.notice = value.get(0).getText();
            } else if (key.equals(AFMParser.FULL_NAME)) {
                this.font.fullName = value.get(0).getText();
            } else if (key.equals(AFMParser.FAMILY_NAME)) {
                this.font.familyName = value.get(0).getText();
            } else if (key.equals(AFMParser.WEIGHT)) {
                this.font.weight = value.get(0).getText();
            } else if (key.equals(AFMParser.ITALIC_ANGLE)) {
                this.font.italicAngle = value.get(0).floatValue();
            } else if (key.equals("isFixedPitch")) {
                this.font.isFixedPitch = value.get(0).booleanValue();
            } else if (key.equals(AFMParser.UNDERLINE_POSITION)) {
                this.font.underlinePosition = value.get(0).floatValue();
            } else if (key.equals(AFMParser.UNDERLINE_THICKNESS)) {
                this.font.underlineThickness = value.get(0).floatValue();
            }
        }
    }

    private Map<String, List<Token>> readSimpleDict() throws IOException {
        Map<String, List<Token>> dict = new HashMap<>();
        int length = read(Token.INTEGER).intValue();
        read(Token.NAME, "dict");
        readMaybe(Token.NAME, "dup");
        read(Token.NAME, "begin");
        for (int i = 0; i < length && this.lexer.peekToken() != null; i++) {
            if (this.lexer.peekToken().getKind() == Token.NAME && !this.lexer.peekToken().getText().equals("end")) {
                read(Token.NAME);
            }
            if (this.lexer.peekToken() == null || (this.lexer.peekToken().getKind() == Token.NAME && this.lexer.peekToken().getText().equals("end"))) {
                break;
            }
            String key = read(Token.LITERAL).getText();
            List<Token> value = readDictValue();
            dict.put(key, value);
        }
        read(Token.NAME, "end");
        readMaybe(Token.NAME, "readonly");
        read(Token.NAME, BaseParser.DEF);
        return dict;
    }

    private List<Token> readDictValue() throws IOException {
        List<Token> value = readValue();
        readDef();
        return value;
    }

    private List<Token> readValue() throws IOException {
        List<Token> value = new ArrayList<>();
        Token token = this.lexer.nextToken();
        if (this.lexer.peekToken() == null) {
            return value;
        }
        value.add(token);
        if (token.getKind() == Token.START_ARRAY) {
            int openArray = 1;
            while (this.lexer.peekToken() != null) {
                if (this.lexer.peekToken().getKind() == Token.START_ARRAY) {
                    openArray++;
                }
                Token token2 = this.lexer.nextToken();
                value.add(token2);
                if (token2.getKind() == Token.END_ARRAY) {
                    openArray--;
                    if (openArray == 0) {
                    }
                }
            }
            return value;
        }
        if (token.getKind() == Token.START_PROC) {
            value.addAll(readProc());
        } else if (token.getKind() == Token.START_DICT) {
            read(Token.END_DICT);
            return value;
        }
        readPostScriptWrapper(value);
        return value;
    }

    private void readPostScriptWrapper(List<Token> value) throws IOException {
        if (this.lexer.peekToken().getText().equals("systemdict")) {
            read(Token.NAME, "systemdict");
            read(Token.LITERAL, "internaldict");
            read(Token.NAME, "known");
            read(Token.START_PROC);
            readProcVoid();
            read(Token.START_PROC);
            readProcVoid();
            read(Token.NAME, "ifelse");
            read(Token.START_PROC);
            read(Token.NAME, "pop");
            value.clear();
            value.addAll(readValue());
            read(Token.END_PROC);
            read(Token.NAME, "if");
        }
    }

    private List<Token> readProc() throws IOException {
        List<Token> value = new ArrayList<>();
        int openProc = 1;
        while (true) {
            if (this.lexer.peekToken().getKind() == Token.START_PROC) {
                openProc++;
            }
            Token token = this.lexer.nextToken();
            value.add(token);
            if (token.getKind() == Token.END_PROC) {
                openProc--;
                if (openProc == 0) {
                    break;
                }
            }
        }
        Token executeonly = readMaybe(Token.NAME, "executeonly");
        if (executeonly != null) {
            value.add(executeonly);
        }
        return value;
    }

    private void readProcVoid() throws IOException {
        int openProc = 1;
        while (true) {
            if (this.lexer.peekToken().getKind() == Token.START_PROC) {
                openProc++;
            }
            Token token = this.lexer.nextToken();
            if (token.getKind() == Token.END_PROC) {
                openProc--;
                if (openProc == 0) {
                    readMaybe(Token.NAME, "executeonly");
                    return;
                }
            }
        }
    }

    private void parseBinary(byte[] bytes) throws IOException {
        byte[] decrypted;
        Token peekToken;
        if (isBinary(bytes)) {
            decrypted = decrypt(bytes, EEXEC_KEY, 4);
        } else {
            decrypted = decrypt(hexToBinary(bytes), EEXEC_KEY, 4);
        }
        this.lexer = new Type1Lexer(decrypted);
        Token tokenPeekToken = this.lexer.peekToken();
        while (true) {
            peekToken = tokenPeekToken;
            if (peekToken == null || peekToken.getText().equals(StandardStructureTypes.PRIVATE)) {
                break;
            }
            this.lexer.nextToken();
            tokenPeekToken = this.lexer.peekToken();
        }
        if (peekToken == null) {
            throw new IOException("/Private token not found");
        }
        read(Token.LITERAL, StandardStructureTypes.PRIVATE);
        int length = read(Token.INTEGER).intValue();
        read(Token.NAME, "dict");
        readMaybe(Token.NAME, "dup");
        read(Token.NAME, "begin");
        int lenIV = 4;
        for (int i = 0; i < length && this.lexer.peekToken() != null && this.lexer.peekToken().getKind() == Token.LITERAL; i++) {
            String key = read(Token.LITERAL).getText();
            if ("Subrs".equals(key)) {
                readSubrs(lenIV);
            } else if ("OtherSubrs".equals(key)) {
                readOtherSubrs();
            } else if ("lenIV".equals(key)) {
                lenIV = readDictValue().get(0).intValue();
            } else if ("ND".equals(key)) {
                read(Token.START_PROC);
                readMaybe(Token.NAME, "noaccess");
                read(Token.NAME, BaseParser.DEF);
                read(Token.END_PROC);
                readMaybe(Token.NAME, "executeonly");
                readMaybe(Token.NAME, "readonly");
                read(Token.NAME, BaseParser.DEF);
            } else if ("NP".equals(key)) {
                read(Token.START_PROC);
                readMaybe(Token.NAME, "noaccess");
                read(Token.NAME);
                read(Token.END_PROC);
                readMaybe(Token.NAME, "executeonly");
                readMaybe(Token.NAME, "readonly");
                read(Token.NAME, BaseParser.DEF);
            } else if ("RD".equals(key)) {
                read(Token.START_PROC);
                readProcVoid();
                readMaybe(Token.NAME, "bind");
                readMaybe(Token.NAME, "executeonly");
                readMaybe(Token.NAME, "readonly");
                read(Token.NAME, BaseParser.DEF);
            } else {
                readPrivate(key, readDictValue());
            }
        }
        while (true) {
            if (this.lexer.peekToken().getKind() != Token.LITERAL || !this.lexer.peekToken().getText().equals("CharStrings")) {
                this.lexer.nextToken();
            } else {
                read(Token.LITERAL, "CharStrings");
                readCharStrings(lenIV);
                return;
            }
        }
    }

    private void readPrivate(String key, List<Token> value) throws IOException {
        if (key.equals("BlueValues")) {
            this.font.blueValues = arrayToNumbers(value);
            return;
        }
        if (key.equals("OtherBlues")) {
            this.font.otherBlues = arrayToNumbers(value);
            return;
        }
        if (key.equals("FamilyBlues")) {
            this.font.familyBlues = arrayToNumbers(value);
            return;
        }
        if (key.equals("FamilyOtherBlues")) {
            this.font.familyOtherBlues = arrayToNumbers(value);
            return;
        }
        if (key.equals("BlueScale")) {
            this.font.blueScale = value.get(0).floatValue();
            return;
        }
        if (key.equals("BlueShift")) {
            this.font.blueShift = value.get(0).intValue();
            return;
        }
        if (key.equals("BlueFuzz")) {
            this.font.blueFuzz = value.get(0).intValue();
            return;
        }
        if (key.equals(AFMParser.STD_HW)) {
            this.font.stdHW = arrayToNumbers(value);
            return;
        }
        if (key.equals(AFMParser.STD_VW)) {
            this.font.stdVW = arrayToNumbers(value);
            return;
        }
        if (key.equals("StemSnapH")) {
            this.font.stemSnapH = arrayToNumbers(value);
            return;
        }
        if (key.equals("StemSnapV")) {
            this.font.stemSnapV = arrayToNumbers(value);
        } else if (key.equals("ForceBold")) {
            this.font.forceBold = value.get(0).booleanValue();
        } else if (key.equals("LanguageGroup")) {
            this.font.languageGroup = value.get(0).intValue();
        }
    }

    private void readSubrs(int lenIV) throws IOException {
        int length = read(Token.INTEGER).intValue();
        for (int i = 0; i < length; i++) {
            this.font.subrs.add(null);
        }
        read(Token.NAME, "array");
        for (int i2 = 0; i2 < length && this.lexer.peekToken() != null && this.lexer.peekToken().getKind() == Token.NAME && this.lexer.peekToken().getText().equals("dup"); i2++) {
            read(Token.NAME, "dup");
            Token index = read(Token.INTEGER);
            read(Token.INTEGER);
            Token charstring = read(Token.CHARSTRING);
            this.font.subrs.set(index.intValue(), decrypt(charstring.getData(), 4330, lenIV));
            readPut();
        }
        readDef();
    }

    private void readOtherSubrs() throws IOException {
        if (this.lexer.peekToken().getKind() == Token.START_ARRAY) {
            readValue();
            readDef();
            return;
        }
        int length = read(Token.INTEGER).intValue();
        read(Token.NAME, "array");
        for (int i = 0; i < length; i++) {
            read(Token.NAME, "dup");
            read(Token.INTEGER);
            readValue();
            readPut();
        }
        readDef();
    }

    private void readCharStrings(int lenIV) throws IOException {
        int length = read(Token.INTEGER).intValue();
        read(Token.NAME, "dict");
        read(Token.NAME, "dup");
        read(Token.NAME, "begin");
        for (int i = 0; i < length && this.lexer.peekToken() != null && (this.lexer.peekToken().getKind() != Token.NAME || !this.lexer.peekToken().getText().equals("end")); i++) {
            String name = read(Token.LITERAL).getText();
            read(Token.INTEGER);
            Token charstring = read(Token.CHARSTRING);
            this.font.charstrings.put(name, decrypt(charstring.getData(), 4330, lenIV));
            readDef();
        }
        read(Token.NAME, "end");
    }

    private void readDef() throws IOException {
        readMaybe(Token.NAME, "readonly");
        readMaybe(Token.NAME, "noaccess");
        Token token = read(Token.NAME);
        if (token.getText().equals("ND") || token.getText().equals("|-")) {
            return;
        }
        if (token.getText().equals("noaccess")) {
            token = read(Token.NAME);
        }
        if (token.getText().equals(BaseParser.DEF)) {
        } else {
            throw new IOException("Found " + token + " but expected ND");
        }
    }

    private void readPut() throws IOException {
        readMaybe(Token.NAME, "readonly");
        Token token = read(Token.NAME);
        if (token.getText().equals("NP") || token.getText().equals("|")) {
            return;
        }
        if (token.getText().equals("noaccess")) {
            token = read(Token.NAME);
        }
        if (token.getText().equals("put")) {
        } else {
            throw new IOException("Found " + token + " but expected NP");
        }
    }

    private Token read(Token.Kind kind) throws IOException {
        Token token = this.lexer.nextToken();
        if (token == null || token.getKind() != kind) {
            throw new IOException("Found " + token + " but expected " + kind);
        }
        return token;
    }

    private void read(Token.Kind kind, String name) throws IOException {
        Token token = read(kind);
        if (!token.getText().equals(name)) {
            throw new IOException("Found " + token + " but expected " + name);
        }
    }

    private Token readMaybe(Token.Kind kind, String name) throws IOException {
        Token token = this.lexer.peekToken();
        if (token != null && token.getKind() == kind && token.getText().equals(name)) {
            return this.lexer.nextToken();
        }
        return null;
    }

    private byte[] decrypt(byte[] cipherBytes, int r, int n) {
        if (n == -1) {
            return cipherBytes;
        }
        if (cipherBytes.length == 0 || n > cipherBytes.length) {
            return new byte[0];
        }
        byte[] plainBytes = new byte[cipherBytes.length - n];
        for (int i = 0; i < cipherBytes.length; i++) {
            int cipher = cipherBytes[i] & 255;
            int plain = cipher ^ (r >> 8);
            if (i >= n) {
                plainBytes[i - n] = (byte) plain;
            }
            r = (((cipher + r) * 52845) + 22719) & 65535;
        }
        return plainBytes;
    }

    private boolean isBinary(byte[] bytes) {
        if (bytes.length < 4) {
            return true;
        }
        for (int i = 0; i < 4; i++) {
            byte by = bytes[i];
            if (by != 10 && by != 13 && by != 32 && by != 9 && Character.digit((char) by, 16) == -1) {
                return true;
            }
        }
        return false;
    }

    private byte[] hexToBinary(byte[] bytes) {
        int len = 0;
        for (byte by : bytes) {
            if (Character.digit((char) by, 16) != -1) {
                len++;
            }
        }
        byte[] res = new byte[len / 2];
        int r = 0;
        int prev = -1;
        for (byte by2 : bytes) {
            int digit = Character.digit((char) by2, 16);
            if (digit != -1) {
                if (prev == -1) {
                    prev = digit;
                } else {
                    int i = r;
                    r++;
                    res[i] = (byte) ((prev * 16) + digit);
                    prev = -1;
                }
            }
        }
        return res;
    }
}
