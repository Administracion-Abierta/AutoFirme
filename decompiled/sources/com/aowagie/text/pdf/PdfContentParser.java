package com.aowagie.text.pdf;

import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfContentParser.class */
public class PdfContentParser {
    static final int COMMAND_TYPE = 200;
    private PRTokeniser tokeniser;

    public PdfContentParser(PRTokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public ArrayList parse(ArrayList ls) throws IOException {
        PdfObject ob;
        if (ls == null) {
            ls = new ArrayList();
        } else {
            ls.clear();
        }
        do {
            ob = readPRObject();
            if (ob == null) {
                break;
            }
            ls.add(ob);
        } while (ob.type() != 200);
        return ls;
    }

    public PRTokeniser getTokeniser() {
        return this.tokeniser;
    }

    public void setTokeniser(PRTokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    private PdfDictionary readDictionary() throws IOException {
        PdfDictionary dic = new PdfDictionary();
        while (nextValidToken()) {
            if (this.tokeniser.getTokenType() != 8) {
                if (this.tokeniser.getTokenType() != 3) {
                    throw new IOException("Dictionary key is not a name.");
                }
                PdfName name = new PdfName(this.tokeniser.getStringValue(), false);
                PdfObject obj = readPRObject();
                int type = obj.type();
                if ((-type) == 8) {
                    throw new IOException("Unexpected '>>'");
                }
                if ((-type) == 6) {
                    throw new IOException("Unexpected ']'");
                }
                dic.put(name, obj);
            } else {
                return dic;
            }
        }
        throw new IOException("Unexpected end of file.");
    }

    private PdfArray readArray() throws IOException {
        PdfArray array = new PdfArray();
        while (true) {
            PdfObject obj = readPRObject();
            int type = obj.type();
            if ((-type) != 6) {
                if ((-type) == 8) {
                    throw new IOException("Unexpected '>>'");
                }
                array.add(obj);
            } else {
                return array;
            }
        }
    }

    PdfObject readPRObject() throws IOException {
        if (!nextValidToken()) {
            return null;
        }
        int type = this.tokeniser.getTokenType();
        switch (type) {
            case 1:
                return new PdfNumber(this.tokeniser.getStringValue());
            case 2:
                PdfString str = new PdfString(this.tokeniser.getStringValue(), null).setHexWriting(this.tokeniser.isHexString());
                return str;
            case 3:
                return new PdfName(this.tokeniser.getStringValue(), false);
            case 4:
            case 6:
            case 8:
            case 9:
            default:
                return new PdfLiteral(-type, this.tokeniser.getStringValue());
            case 5:
                return readArray();
            case 7:
                PdfDictionary dic = readDictionary();
                return dic;
            case 10:
                return new PdfLiteral(200, this.tokeniser.getStringValue());
        }
    }

    private boolean nextValidToken() throws IOException {
        while (this.tokeniser.nextToken()) {
            if (this.tokeniser.getTokenType() != 4) {
                return true;
            }
        }
        return false;
    }
}
