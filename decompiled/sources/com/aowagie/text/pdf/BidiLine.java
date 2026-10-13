package com.aowagie.text.pdf;

import com.aowagie.text.Chunk;
import com.aowagie.text.Utilities;
import com.sun.jna.platform.win32.WinError;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/BidiLine.class */
public class BidiLine {
    protected int runDirection;
    protected int pieceSize;
    protected char[] text;
    protected PdfChunk[] detailChunks;
    protected int totalTextLength;
    protected byte[] orderLevels;
    protected int[] indexChars;
    protected ArrayList chunks;
    protected int indexChunk;
    protected int indexChunkChar;
    protected int currentChar;
    protected int storedRunDirection;
    protected char[] storedText;
    protected PdfChunk[] storedDetailChunks;
    protected int storedTotalTextLength;
    protected byte[] storedOrderLevels;
    protected int[] storedIndexChars;
    protected int storedIndexChunk;
    protected int storedIndexChunkChar;
    protected int storedCurrentChar;
    protected boolean shortStore;
    protected static final IntHashtable mirrorChars = new IntHashtable();
    protected int arabicOptions;

    static {
        mirrorChars.put(40, 41);
        mirrorChars.put(41, 40);
        mirrorChars.put(60, 62);
        mirrorChars.put(62, 60);
        mirrorChars.put(91, 93);
        mirrorChars.put(93, 91);
        mirrorChars.put(123, 125);
        mirrorChars.put(125, 123);
        mirrorChars.put(171, 187);
        mirrorChars.put(187, 171);
        mirrorChars.put(WinError.ERROR_DS_AFFECTS_MULTIPLE_DSAS, WinError.ERROR_DS_SERVER_DOWN);
        mirrorChars.put(WinError.ERROR_DS_SERVER_DOWN, WinError.ERROR_DS_AFFECTS_MULTIPLE_DSAS);
        mirrorChars.put(WinError.ERROR_DS_SORT_CONTROL_MISSING, WinError.ERROR_DS_OFFSET_RANGE_ERROR);
        mirrorChars.put(WinError.ERROR_DS_OFFSET_RANGE_ERROR, WinError.ERROR_DS_SORT_CONTROL_MISSING);
        mirrorChars.put(WinError.ERROR_DS_ATT_NOT_DEF_FOR_CLASS, WinError.ERROR_DS_ATT_ALREADY_EXISTS);
        mirrorChars.put(WinError.ERROR_DS_ATT_ALREADY_EXISTS, WinError.ERROR_DS_ATT_NOT_DEF_FOR_CLASS);
        mirrorChars.put(WinError.ERROR_DS_OBJ_NOT_FOUND, WinError.ERROR_DS_ALIASED_OBJ_MISSING);
        mirrorChars.put(WinError.ERROR_DS_ALIASED_OBJ_MISSING, WinError.ERROR_DS_OBJ_NOT_FOUND);
        mirrorChars.put(8712, 8715);
        mirrorChars.put(8713, 8716);
        mirrorChars.put(8714, 8717);
        mirrorChars.put(8715, 8712);
        mirrorChars.put(8716, 8713);
        mirrorChars.put(8717, 8714);
        mirrorChars.put(8725, 10741);
        mirrorChars.put(8764, 8765);
        mirrorChars.put(8765, 8764);
        mirrorChars.put(8771, 8909);
        mirrorChars.put(8786, 8787);
        mirrorChars.put(8787, 8786);
        mirrorChars.put(8788, 8789);
        mirrorChars.put(8789, 8788);
        mirrorChars.put(8804, 8805);
        mirrorChars.put(8805, 8804);
        mirrorChars.put(8806, 8807);
        mirrorChars.put(8807, 8806);
        mirrorChars.put(8808, 8809);
        mirrorChars.put(8809, 8808);
        mirrorChars.put(8810, 8811);
        mirrorChars.put(8811, 8810);
        mirrorChars.put(8814, 8815);
        mirrorChars.put(8815, 8814);
        mirrorChars.put(8816, 8817);
        mirrorChars.put(8817, 8816);
        mirrorChars.put(8818, 8819);
        mirrorChars.put(8819, 8818);
        mirrorChars.put(8820, 8821);
        mirrorChars.put(8821, 8820);
        mirrorChars.put(8822, 8823);
        mirrorChars.put(8823, 8822);
        mirrorChars.put(8824, 8825);
        mirrorChars.put(8825, 8824);
        mirrorChars.put(8826, 8827);
        mirrorChars.put(8827, 8826);
        mirrorChars.put(8828, 8829);
        mirrorChars.put(8829, 8828);
        mirrorChars.put(8830, 8831);
        mirrorChars.put(8831, 8830);
        mirrorChars.put(8832, 8833);
        mirrorChars.put(8833, 8832);
        mirrorChars.put(8834, 8835);
        mirrorChars.put(8835, 8834);
        mirrorChars.put(8836, 8837);
        mirrorChars.put(8837, 8836);
        mirrorChars.put(8838, 8839);
        mirrorChars.put(8839, 8838);
        mirrorChars.put(8840, 8841);
        mirrorChars.put(8841, 8840);
        mirrorChars.put(8842, 8843);
        mirrorChars.put(8843, 8842);
        mirrorChars.put(8847, 8848);
        mirrorChars.put(8848, 8847);
        mirrorChars.put(8849, 8850);
        mirrorChars.put(8850, 8849);
        mirrorChars.put(8856, 10680);
        mirrorChars.put(8866, 8867);
        mirrorChars.put(8867, 8866);
        mirrorChars.put(8870, 10974);
        mirrorChars.put(8872, 10980);
        mirrorChars.put(8873, 10979);
        mirrorChars.put(8875, 10981);
        mirrorChars.put(8880, 8881);
        mirrorChars.put(8881, 8880);
        mirrorChars.put(8882, 8883);
        mirrorChars.put(8883, 8882);
        mirrorChars.put(8884, 8885);
        mirrorChars.put(8885, 8884);
        mirrorChars.put(8886, 8887);
        mirrorChars.put(8887, 8886);
        mirrorChars.put(8905, 8906);
        mirrorChars.put(8906, 8905);
        mirrorChars.put(8907, 8908);
        mirrorChars.put(8908, 8907);
        mirrorChars.put(8909, 8771);
        mirrorChars.put(8912, 8913);
        mirrorChars.put(8913, 8912);
        mirrorChars.put(8918, 8919);
        mirrorChars.put(8919, 8918);
        mirrorChars.put(8920, 8921);
        mirrorChars.put(8921, 8920);
        mirrorChars.put(8922, 8923);
        mirrorChars.put(8923, 8922);
        mirrorChars.put(8924, 8925);
        mirrorChars.put(8925, 8924);
        mirrorChars.put(8926, 8927);
        mirrorChars.put(8927, 8926);
        mirrorChars.put(8928, 8929);
        mirrorChars.put(8929, 8928);
        mirrorChars.put(8930, 8931);
        mirrorChars.put(8931, 8930);
        mirrorChars.put(8932, 8933);
        mirrorChars.put(8933, 8932);
        mirrorChars.put(8934, 8935);
        mirrorChars.put(8935, 8934);
        mirrorChars.put(8936, 8937);
        mirrorChars.put(8937, 8936);
        mirrorChars.put(8938, 8939);
        mirrorChars.put(8939, 8938);
        mirrorChars.put(8940, 8941);
        mirrorChars.put(8941, 8940);
        mirrorChars.put(8944, 8945);
        mirrorChars.put(8945, 8944);
        mirrorChars.put(8946, 8954);
        mirrorChars.put(8947, 8955);
        mirrorChars.put(8948, 8956);
        mirrorChars.put(8950, 8957);
        mirrorChars.put(8951, 8958);
        mirrorChars.put(8954, 8946);
        mirrorChars.put(8955, 8947);
        mirrorChars.put(8956, 8948);
        mirrorChars.put(8957, 8950);
        mirrorChars.put(8958, 8951);
        mirrorChars.put(8968, 8969);
        mirrorChars.put(8969, 8968);
        mirrorChars.put(8970, 8971);
        mirrorChars.put(8971, 8970);
        mirrorChars.put(WinError.DNS_ERROR_RCODE_FORMAT_ERROR, WinError.DNS_ERROR_RCODE_SERVER_FAILURE);
        mirrorChars.put(WinError.DNS_ERROR_RCODE_SERVER_FAILURE, WinError.DNS_ERROR_RCODE_FORMAT_ERROR);
        mirrorChars.put(10088, 10089);
        mirrorChars.put(10089, 10088);
        mirrorChars.put(10090, WinError.WSASYSNOTREADY);
        mirrorChars.put(WinError.WSASYSNOTREADY, 10090);
        mirrorChars.put(WinError.WSAVERNOTSUPPORTED, WinError.WSANOTINITIALISED);
        mirrorChars.put(WinError.WSANOTINITIALISED, WinError.WSAVERNOTSUPPORTED);
        mirrorChars.put(10094, 10095);
        mirrorChars.put(10095, 10094);
        mirrorChars.put(10096, 10097);
        mirrorChars.put(10097, 10096);
        mirrorChars.put(10098, 10099);
        mirrorChars.put(10099, 10098);
        mirrorChars.put(10100, WinError.WSAEDISCON);
        mirrorChars.put(WinError.WSAEDISCON, 10100);
        mirrorChars.put(10197, 10198);
        mirrorChars.put(10198, 10197);
        mirrorChars.put(10205, 10206);
        mirrorChars.put(10206, 10205);
        mirrorChars.put(10210, 10211);
        mirrorChars.put(10211, 10210);
        mirrorChars.put(10212, 10213);
        mirrorChars.put(10213, 10212);
        mirrorChars.put(10214, 10215);
        mirrorChars.put(10215, 10214);
        mirrorChars.put(10216, 10217);
        mirrorChars.put(10217, 10216);
        mirrorChars.put(10218, 10219);
        mirrorChars.put(10219, 10218);
        mirrorChars.put(10627, 10628);
        mirrorChars.put(10628, 10627);
        mirrorChars.put(10629, 10630);
        mirrorChars.put(10630, 10629);
        mirrorChars.put(10631, 10632);
        mirrorChars.put(10632, 10631);
        mirrorChars.put(10633, 10634);
        mirrorChars.put(10634, 10633);
        mirrorChars.put(10635, 10636);
        mirrorChars.put(10636, 10635);
        mirrorChars.put(10637, 10640);
        mirrorChars.put(10638, 10639);
        mirrorChars.put(10639, 10638);
        mirrorChars.put(10640, 10637);
        mirrorChars.put(10641, 10642);
        mirrorChars.put(10642, 10641);
        mirrorChars.put(10643, 10644);
        mirrorChars.put(10644, 10643);
        mirrorChars.put(10645, 10646);
        mirrorChars.put(10646, 10645);
        mirrorChars.put(10647, 10648);
        mirrorChars.put(10648, 10647);
        mirrorChars.put(10680, 8856);
        mirrorChars.put(10688, 10689);
        mirrorChars.put(10689, 10688);
        mirrorChars.put(10692, 10693);
        mirrorChars.put(10693, 10692);
        mirrorChars.put(10703, 10704);
        mirrorChars.put(10704, 10703);
        mirrorChars.put(10705, 10706);
        mirrorChars.put(10706, 10705);
        mirrorChars.put(10708, 10709);
        mirrorChars.put(10709, 10708);
        mirrorChars.put(10712, 10713);
        mirrorChars.put(10713, 10712);
        mirrorChars.put(10714, 10715);
        mirrorChars.put(10715, 10714);
        mirrorChars.put(10741, 8725);
        mirrorChars.put(10744, 10745);
        mirrorChars.put(10745, 10744);
        mirrorChars.put(10748, 10749);
        mirrorChars.put(10749, 10748);
        mirrorChars.put(10795, 10796);
        mirrorChars.put(10796, 10795);
        mirrorChars.put(10797, 10796);
        mirrorChars.put(10798, 10797);
        mirrorChars.put(10804, 10805);
        mirrorChars.put(10805, 10804);
        mirrorChars.put(10812, 10813);
        mirrorChars.put(10813, 10812);
        mirrorChars.put(10852, 10853);
        mirrorChars.put(10853, 10852);
        mirrorChars.put(10873, 10874);
        mirrorChars.put(10874, 10873);
        mirrorChars.put(10877, 10878);
        mirrorChars.put(10878, 10877);
        mirrorChars.put(10879, 10880);
        mirrorChars.put(10880, 10879);
        mirrorChars.put(10881, 10882);
        mirrorChars.put(10882, 10881);
        mirrorChars.put(10883, 10884);
        mirrorChars.put(10884, 10883);
        mirrorChars.put(10891, 10892);
        mirrorChars.put(10892, 10891);
        mirrorChars.put(10897, 10898);
        mirrorChars.put(10898, 10897);
        mirrorChars.put(10899, 10900);
        mirrorChars.put(10900, 10899);
        mirrorChars.put(10901, 10902);
        mirrorChars.put(10902, 10901);
        mirrorChars.put(10903, 10904);
        mirrorChars.put(10904, 10903);
        mirrorChars.put(10905, 10906);
        mirrorChars.put(10906, 10905);
        mirrorChars.put(10907, 10908);
        mirrorChars.put(10908, 10907);
        mirrorChars.put(10913, 10914);
        mirrorChars.put(10914, 10913);
        mirrorChars.put(10918, 10919);
        mirrorChars.put(10919, 10918);
        mirrorChars.put(10920, 10921);
        mirrorChars.put(10921, 10920);
        mirrorChars.put(10922, 10923);
        mirrorChars.put(10923, 10922);
        mirrorChars.put(10924, 10925);
        mirrorChars.put(10925, 10924);
        mirrorChars.put(10927, 10928);
        mirrorChars.put(10928, 10927);
        mirrorChars.put(10931, 10932);
        mirrorChars.put(10932, 10931);
        mirrorChars.put(10939, 10940);
        mirrorChars.put(10940, 10939);
        mirrorChars.put(10941, 10942);
        mirrorChars.put(10942, 10941);
        mirrorChars.put(10943, 10944);
        mirrorChars.put(10944, 10943);
        mirrorChars.put(10945, 10946);
        mirrorChars.put(10946, 10945);
        mirrorChars.put(10947, 10948);
        mirrorChars.put(10948, 10947);
        mirrorChars.put(10949, 10950);
        mirrorChars.put(10950, 10949);
        mirrorChars.put(10957, 10958);
        mirrorChars.put(10958, 10957);
        mirrorChars.put(10959, 10960);
        mirrorChars.put(10960, 10959);
        mirrorChars.put(10961, 10962);
        mirrorChars.put(10962, 10961);
        mirrorChars.put(10963, 10964);
        mirrorChars.put(10964, 10963);
        mirrorChars.put(10965, 10966);
        mirrorChars.put(10966, 10965);
        mirrorChars.put(10974, 8870);
        mirrorChars.put(10979, 8873);
        mirrorChars.put(10980, 8872);
        mirrorChars.put(10981, 8875);
        mirrorChars.put(10988, 10989);
        mirrorChars.put(10989, 10988);
        mirrorChars.put(10999, 11000);
        mirrorChars.put(11000, 10999);
        mirrorChars.put(WinError.WSAHOST_NOT_FOUND, WinError.WSATRY_AGAIN);
        mirrorChars.put(WinError.WSATRY_AGAIN, WinError.WSAHOST_NOT_FOUND);
        mirrorChars.put(12296, 12297);
        mirrorChars.put(12297, 12296);
        mirrorChars.put(12298, 12299);
        mirrorChars.put(12299, 12298);
        mirrorChars.put(12300, 12301);
        mirrorChars.put(12301, 12300);
        mirrorChars.put(12302, 12303);
        mirrorChars.put(12303, 12302);
        mirrorChars.put(12304, 12305);
        mirrorChars.put(12305, 12304);
        mirrorChars.put(12308, 12309);
        mirrorChars.put(12309, 12308);
        mirrorChars.put(12310, 12311);
        mirrorChars.put(12311, 12310);
        mirrorChars.put(12312, 12313);
        mirrorChars.put(12313, 12312);
        mirrorChars.put(12314, 12315);
        mirrorChars.put(12315, 12314);
        mirrorChars.put(65288, 65289);
        mirrorChars.put(65289, 65288);
        mirrorChars.put(65308, 65310);
        mirrorChars.put(65310, 65308);
        mirrorChars.put(65339, 65341);
        mirrorChars.put(65341, 65339);
        mirrorChars.put(65371, 65373);
        mirrorChars.put(65373, 65371);
        mirrorChars.put(65375, 65376);
        mirrorChars.put(65376, 65375);
        mirrorChars.put(65378, 65379);
        mirrorChars.put(65379, 65378);
    }

    public BidiLine() {
        this.pieceSize = 256;
        this.text = new char[this.pieceSize];
        this.detailChunks = new PdfChunk[this.pieceSize];
        this.totalTextLength = 0;
        this.orderLevels = new byte[this.pieceSize];
        this.indexChars = new int[this.pieceSize];
        this.chunks = new ArrayList();
        this.indexChunk = 0;
        this.indexChunkChar = 0;
        this.currentChar = 0;
        this.storedText = new char[0];
        this.storedDetailChunks = new PdfChunk[0];
        this.storedTotalTextLength = 0;
        this.storedOrderLevels = new byte[0];
        this.storedIndexChars = new int[0];
        this.storedIndexChunk = 0;
        this.storedIndexChunkChar = 0;
        this.storedCurrentChar = 0;
    }

    public BidiLine(BidiLine org2) {
        this.pieceSize = 256;
        this.text = new char[this.pieceSize];
        this.detailChunks = new PdfChunk[this.pieceSize];
        this.totalTextLength = 0;
        this.orderLevels = new byte[this.pieceSize];
        this.indexChars = new int[this.pieceSize];
        this.chunks = new ArrayList();
        this.indexChunk = 0;
        this.indexChunkChar = 0;
        this.currentChar = 0;
        this.storedText = new char[0];
        this.storedDetailChunks = new PdfChunk[0];
        this.storedTotalTextLength = 0;
        this.storedOrderLevels = new byte[0];
        this.storedIndexChars = new int[0];
        this.storedIndexChunk = 0;
        this.storedIndexChunkChar = 0;
        this.storedCurrentChar = 0;
        this.runDirection = org2.runDirection;
        this.pieceSize = org2.pieceSize;
        this.text = (char[]) org2.text.clone();
        this.detailChunks = (PdfChunk[]) org2.detailChunks.clone();
        this.totalTextLength = org2.totalTextLength;
        this.orderLevels = (byte[]) org2.orderLevels.clone();
        this.indexChars = (int[]) org2.indexChars.clone();
        this.chunks = new ArrayList(org2.chunks);
        this.indexChunk = org2.indexChunk;
        this.indexChunkChar = org2.indexChunkChar;
        this.currentChar = org2.currentChar;
        this.storedRunDirection = org2.storedRunDirection;
        this.storedText = (char[]) org2.storedText.clone();
        this.storedDetailChunks = (PdfChunk[]) org2.storedDetailChunks.clone();
        this.storedTotalTextLength = org2.storedTotalTextLength;
        this.storedOrderLevels = (byte[]) org2.storedOrderLevels.clone();
        this.storedIndexChars = (int[]) org2.storedIndexChars.clone();
        this.storedIndexChunk = org2.storedIndexChunk;
        this.storedIndexChunkChar = org2.storedIndexChunkChar;
        this.storedCurrentChar = org2.storedCurrentChar;
        this.shortStore = org2.shortStore;
        this.arabicOptions = org2.arabicOptions;
    }

    public boolean isEmpty() {
        return this.currentChar >= this.totalTextLength && this.indexChunk >= this.chunks.size();
    }

    public void clearChunks() {
        this.chunks.clear();
        this.totalTextLength = 0;
        this.currentChar = 0;
    }

    public boolean getParagraph(int runDirection) {
        this.runDirection = runDirection;
        this.currentChar = 0;
        this.totalTextLength = 0;
        boolean hasText = false;
        while (this.indexChunk < this.chunks.size()) {
            PdfChunk ck = (PdfChunk) this.chunks.get(this.indexChunk);
            BaseFont bf = ck.font().getFont();
            String s = ck.toString();
            int len = s.length();
            while (this.indexChunkChar < len) {
                char c = s.charAt(this.indexChunkChar);
                char uniC = (char) bf.getUnicodeEquivalent(c);
                if (uniC == '\r' || uniC == '\n') {
                    if (uniC == '\r' && this.indexChunkChar + 1 < len && s.charAt(this.indexChunkChar + 1) == '\n') {
                        this.indexChunkChar++;
                    }
                    this.indexChunkChar++;
                    if (this.indexChunkChar >= len) {
                        this.indexChunkChar = 0;
                        this.indexChunk++;
                    }
                    hasText = true;
                    if (this.totalTextLength != 0) {
                        break;
                    }
                    this.detailChunks[0] = ck;
                    break;
                }
                addPiece(c, ck);
                this.indexChunkChar++;
            }
            if (hasText) {
                break;
            }
            this.indexChunkChar = 0;
            this.indexChunk++;
        }
        if (this.totalTextLength == 0) {
            return hasText;
        }
        this.totalTextLength = trimRight(0, this.totalTextLength - 1) + 1;
        if (this.totalTextLength == 0) {
            return true;
        }
        if (runDirection == 2 || runDirection == 3) {
            if (this.orderLevels.length < this.totalTextLength) {
                this.orderLevels = new byte[this.pieceSize];
                this.indexChars = new int[this.pieceSize];
            }
            ArabicLigaturizer.processNumbers(this.text, 0, this.totalTextLength, this.arabicOptions);
            BidiOrder order = new BidiOrder(this.text, 0, this.totalTextLength, (byte) (runDirection == 3 ? 1 : 0));
            byte[] od = order.getLevels();
            for (int k = 0; k < this.totalTextLength; k++) {
                this.orderLevels[k] = od[k];
                this.indexChars[k] = k;
            }
            doArabicShapping();
            mirrorGlyphs();
        }
        this.totalTextLength = trimRightEx(0, this.totalTextLength - 1) + 1;
        return true;
    }

    public void addChunk(PdfChunk chunk) {
        this.chunks.add(chunk);
    }

    public void addChunks(ArrayList chunks) {
        this.chunks.addAll(chunks);
    }

    public void addPiece(char c, PdfChunk chunk) {
        if (this.totalTextLength >= this.pieceSize) {
            char[] tempText = this.text;
            PdfChunk[] tempDetailChunks = this.detailChunks;
            this.pieceSize *= 2;
            this.text = new char[this.pieceSize];
            this.detailChunks = new PdfChunk[this.pieceSize];
            System.arraycopy(tempText, 0, this.text, 0, this.totalTextLength);
            System.arraycopy(tempDetailChunks, 0, this.detailChunks, 0, this.totalTextLength);
        }
        this.text[this.totalTextLength] = c;
        PdfChunk[] pdfChunkArr = this.detailChunks;
        int i = this.totalTextLength;
        this.totalTextLength = i + 1;
        pdfChunkArr[i] = chunk;
    }

    public void save() {
        if (this.indexChunk > 0) {
            if (this.indexChunk >= this.chunks.size()) {
                this.chunks.clear();
            } else {
                this.indexChunk--;
                while (this.indexChunk >= 0) {
                    this.chunks.remove(this.indexChunk);
                    this.indexChunk--;
                }
            }
            this.indexChunk = 0;
        }
        this.storedRunDirection = this.runDirection;
        this.storedTotalTextLength = this.totalTextLength;
        this.storedIndexChunk = this.indexChunk;
        this.storedIndexChunkChar = this.indexChunkChar;
        this.storedCurrentChar = this.currentChar;
        this.shortStore = this.currentChar < this.totalTextLength;
        if (!this.shortStore) {
            if (this.storedText.length < this.totalTextLength) {
                this.storedText = new char[this.totalTextLength];
                this.storedDetailChunks = new PdfChunk[this.totalTextLength];
            }
            System.arraycopy(this.text, 0, this.storedText, 0, this.totalTextLength);
            System.arraycopy(this.detailChunks, 0, this.storedDetailChunks, 0, this.totalTextLength);
        }
        if (this.runDirection == 2 || this.runDirection == 3) {
            if (this.storedOrderLevels.length < this.totalTextLength) {
                this.storedOrderLevels = new byte[this.totalTextLength];
                this.storedIndexChars = new int[this.totalTextLength];
            }
            System.arraycopy(this.orderLevels, this.currentChar, this.storedOrderLevels, this.currentChar, this.totalTextLength - this.currentChar);
            System.arraycopy(this.indexChars, this.currentChar, this.storedIndexChars, this.currentChar, this.totalTextLength - this.currentChar);
        }
    }

    public void restore() {
        this.runDirection = this.storedRunDirection;
        this.totalTextLength = this.storedTotalTextLength;
        this.indexChunk = this.storedIndexChunk;
        this.indexChunkChar = this.storedIndexChunkChar;
        this.currentChar = this.storedCurrentChar;
        if (!this.shortStore) {
            System.arraycopy(this.storedText, 0, this.text, 0, this.totalTextLength);
            System.arraycopy(this.storedDetailChunks, 0, this.detailChunks, 0, this.totalTextLength);
        }
        if (this.runDirection == 2 || this.runDirection == 3) {
            System.arraycopy(this.storedOrderLevels, this.currentChar, this.orderLevels, this.currentChar, this.totalTextLength - this.currentChar);
            System.arraycopy(this.storedIndexChars, this.currentChar, this.indexChars, this.currentChar, this.totalTextLength - this.currentChar);
        }
    }

    public void mirrorGlyphs() {
        int mirror;
        for (int k = 0; k < this.totalTextLength; k++) {
            if ((this.orderLevels[k] & 1) == 1 && (mirror = mirrorChars.get(this.text[k])) != 0) {
                this.text[k] = (char) mirror;
            }
        }
    }

    public void doArabicShapping() {
        char c;
        char c2;
        int src = 0;
        int dest = 0;
        while (true) {
            if (src < this.totalTextLength && ((c2 = this.text[src]) < 1536 || c2 > 1791)) {
                if (src != dest) {
                    this.text[dest] = this.text[src];
                    this.detailChunks[dest] = this.detailChunks[src];
                    this.orderLevels[dest] = this.orderLevels[src];
                }
                src++;
                dest++;
            } else {
                if (src >= this.totalTextLength) {
                    this.totalTextLength = dest;
                    return;
                }
                int startArabicIdx = src;
                do {
                    src++;
                    if (src >= this.totalTextLength || (c = this.text[src]) < 1536) {
                        break;
                    }
                } while (c <= 1791);
                int arabicWordSize = src - startArabicIdx;
                int size = ArabicLigaturizer.arabic_shape(this.text, startArabicIdx, arabicWordSize, this.text, dest, arabicWordSize, this.arabicOptions);
                if (startArabicIdx != dest) {
                    for (int k = 0; k < size; k++) {
                        this.detailChunks[dest] = this.detailChunks[startArabicIdx];
                        int i = dest;
                        dest++;
                        int i2 = startArabicIdx;
                        startArabicIdx++;
                        this.orderLevels[i] = this.orderLevels[i2];
                    }
                } else {
                    dest += size;
                }
            }
        }
    }

    public PdfLine processLine(float leftX, float width, int alignment, int runDirection, int arabicOptions) {
        HyphenationEvent he;
        int[] word;
        int uniC;
        float charWidth;
        this.arabicOptions = arabicOptions;
        save();
        boolean isRTL = runDirection == 3;
        if (this.currentChar >= this.totalTextLength) {
            boolean hasText = getParagraph(runDirection);
            if (!hasText) {
                return null;
            }
            if (this.totalTextLength == 0) {
                ArrayList ar = new ArrayList();
                ar.add(new PdfChunk("", this.detailChunks[0]));
                return new PdfLine(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, alignment, true, ar, isRTL);
            }
        }
        int lastSplit = -1;
        if (this.currentChar != 0) {
            this.currentChar = trimLeftEx(this.currentChar, this.totalTextLength - 1);
        }
        int oldCurrentChar = this.currentChar;
        PdfChunk lastValidChunk = null;
        boolean surrogate = false;
        while (this.currentChar < this.totalTextLength) {
            PdfChunk ck = this.detailChunks[this.currentChar];
            surrogate = Utilities.isSurrogatePair(this.text, this.currentChar);
            if (surrogate) {
                uniC = ck.getUnicodeEquivalent(Utilities.convertToUtf32(this.text, this.currentChar));
            } else {
                uniC = ck.getUnicodeEquivalent(this.text[this.currentChar]);
            }
            if (!PdfChunk.noPrint(uniC)) {
                if (surrogate) {
                    charWidth = ck.getCharWidth(uniC);
                } else {
                    charWidth = ck.getCharWidth(this.text[this.currentChar]);
                }
                boolean splitChar = ck.isExtSplitCharacter(oldCurrentChar, this.currentChar, this.totalTextLength, this.text, this.detailChunks);
                if (splitChar && Character.isWhitespace((char) uniC)) {
                    lastSplit = this.currentChar;
                }
                if (width - charWidth < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    break;
                }
                if (splitChar) {
                    lastSplit = this.currentChar;
                }
                width -= charWidth;
                lastValidChunk = ck;
                if (ck.isTab()) {
                    Object[] tab = (Object[]) ck.getAttribute(Chunk.TAB);
                    float tabPosition = ((Float) tab[1]).floatValue();
                    boolean newLine = ((Boolean) tab[2]).booleanValue();
                    if (newLine && tabPosition < width - width) {
                        return new PdfLine(ColumnText.GLOBAL_SPACE_CHAR_RATIO, width, width, alignment, true, createArrayOfPdfChunks(oldCurrentChar, this.currentChar - 1), isRTL);
                    }
                    this.detailChunks[this.currentChar].adjustLeft(leftX);
                    width -= tabPosition;
                }
                if (surrogate) {
                    this.currentChar++;
                }
            }
            this.currentChar++;
        }
        if (lastValidChunk == null) {
            this.currentChar++;
            if (surrogate) {
                this.currentChar++;
            }
            return new PdfLine(ColumnText.GLOBAL_SPACE_CHAR_RATIO, width, ColumnText.GLOBAL_SPACE_CHAR_RATIO, alignment, false, createArrayOfPdfChunks(this.currentChar - 1, this.currentChar - 1), isRTL);
        }
        if (this.currentChar >= this.totalTextLength) {
            return new PdfLine(ColumnText.GLOBAL_SPACE_CHAR_RATIO, width, width, alignment, true, createArrayOfPdfChunks(oldCurrentChar, this.totalTextLength - 1), isRTL);
        }
        int newCurrentChar = trimRightEx(oldCurrentChar, this.currentChar - 1);
        if (newCurrentChar < oldCurrentChar) {
            return new PdfLine(ColumnText.GLOBAL_SPACE_CHAR_RATIO, width, width, alignment, false, createArrayOfPdfChunks(oldCurrentChar, this.currentChar - 1), isRTL);
        }
        if (newCurrentChar == this.currentChar - 1 && (he = (HyphenationEvent) lastValidChunk.getAttribute(Chunk.HYPHENATION)) != null && (word = getWord(oldCurrentChar, newCurrentChar)) != null) {
            float testWidth = width + getWidth(word[0], this.currentChar - 1);
            String pre = he.getHyphenatedWordPre(new String(this.text, word[0], word[1] - word[0]), lastValidChunk.font().getFont(), lastValidChunk.font().size(), testWidth);
            String post = he.getHyphenatedWordPost();
            if (pre.length() > 0) {
                PdfChunk extra = new PdfChunk(pre, lastValidChunk);
                this.currentChar = word[1] - post.length();
                return new PdfLine(ColumnText.GLOBAL_SPACE_CHAR_RATIO, width, testWidth - lastValidChunk.font().width(pre), alignment, false, createArrayOfPdfChunks(oldCurrentChar, word[0] - 1, extra), isRTL);
            }
        }
        if (lastSplit == -1 || lastSplit >= newCurrentChar) {
            return new PdfLine(ColumnText.GLOBAL_SPACE_CHAR_RATIO, width, width + getWidth(newCurrentChar + 1, this.currentChar - 1), alignment, false, createArrayOfPdfChunks(oldCurrentChar, newCurrentChar), isRTL);
        }
        this.currentChar = lastSplit + 1;
        int newCurrentChar2 = trimRightEx(oldCurrentChar, lastSplit);
        if (newCurrentChar2 < oldCurrentChar) {
            newCurrentChar2 = this.currentChar - 1;
        }
        return new PdfLine(ColumnText.GLOBAL_SPACE_CHAR_RATIO, width, width - getWidth(oldCurrentChar, newCurrentChar2), alignment, false, createArrayOfPdfChunks(oldCurrentChar, newCurrentChar2), isRTL);
    }

    public float getWidth(int startIdx, int lastIdx) {
        float width = 0.0f;
        while (startIdx <= lastIdx) {
            boolean surrogate = Utilities.isSurrogatePair(this.text, startIdx);
            if (surrogate) {
                width += this.detailChunks[startIdx].getCharWidth(Utilities.convertToUtf32(this.text, startIdx));
                startIdx++;
            } else {
                char c = this.text[startIdx];
                PdfChunk ck = this.detailChunks[startIdx];
                if (!PdfChunk.noPrint(ck.getUnicodeEquivalent(c))) {
                    width += this.detailChunks[startIdx].getCharWidth(c);
                }
            }
            startIdx++;
        }
        return width;
    }

    public ArrayList createArrayOfPdfChunks(int startIdx, int endIdx) {
        return createArrayOfPdfChunks(startIdx, endIdx, null);
    }

    public ArrayList createArrayOfPdfChunks(int startIdx, int endIdx, PdfChunk extraPdfChunk) {
        boolean bidi = this.runDirection == 2 || this.runDirection == 3;
        if (bidi) {
            reorder(startIdx, endIdx);
        }
        ArrayList ar = new ArrayList();
        PdfChunk refCk = this.detailChunks[startIdx];
        StringBuffer buf = new StringBuffer();
        while (startIdx <= endIdx) {
            int idx = bidi ? this.indexChars[startIdx] : startIdx;
            char c = this.text[idx];
            PdfChunk ck = this.detailChunks[idx];
            if (!PdfChunk.noPrint(ck.getUnicodeEquivalent(c))) {
                if (ck.isImage() || ck.isSeparator() || ck.isTab()) {
                    if (buf.length() > 0) {
                        ar.add(new PdfChunk(buf.toString(), refCk));
                        buf = new StringBuffer();
                    }
                    ar.add(ck);
                } else if (ck == refCk) {
                    buf.append(c);
                } else {
                    if (buf.length() > 0) {
                        ar.add(new PdfChunk(buf.toString(), refCk));
                        buf = new StringBuffer();
                    }
                    if (!ck.isImage() && !ck.isSeparator() && !ck.isTab()) {
                        buf.append(c);
                    }
                    refCk = ck;
                }
            }
            startIdx++;
        }
        if (buf.length() > 0) {
            ar.add(new PdfChunk(buf.toString(), refCk));
        }
        if (extraPdfChunk != null) {
            ar.add(extraPdfChunk);
        }
        return ar;
    }

    public int[] getWord(int startIdx, int idx) {
        int last = idx;
        int first = idx;
        while (last < this.totalTextLength && Character.isLetter(this.text[last])) {
            last++;
        }
        if (last == idx) {
            return null;
        }
        while (first >= startIdx && Character.isLetter(this.text[first])) {
            first--;
        }
        return new int[]{first + 1, last};
    }

    public int trimRight(int startIdx, int endIdx) {
        int idx = endIdx;
        while (idx >= startIdx) {
            char c = (char) this.detailChunks[idx].getUnicodeEquivalent(this.text[idx]);
            if (!isWS(c)) {
                break;
            }
            idx--;
        }
        return idx;
    }

    public int trimLeft(int startIdx, int endIdx) {
        int idx = startIdx;
        while (idx <= endIdx) {
            char c = (char) this.detailChunks[idx].getUnicodeEquivalent(this.text[idx]);
            if (!isWS(c)) {
                break;
            }
            idx++;
        }
        return idx;
    }

    public int trimRightEx(int startIdx, int endIdx) {
        int idx = endIdx;
        while (idx >= startIdx) {
            char c = (char) this.detailChunks[idx].getUnicodeEquivalent(this.text[idx]);
            if (!isWS(c) && !PdfChunk.noPrint(c)) {
                break;
            }
            idx--;
        }
        return idx;
    }

    public int trimLeftEx(int startIdx, int endIdx) {
        int idx = startIdx;
        while (idx <= endIdx) {
            char c = (char) this.detailChunks[idx].getUnicodeEquivalent(this.text[idx]);
            if (!isWS(c) && !PdfChunk.noPrint(c)) {
                break;
            }
            idx++;
        }
        return idx;
    }

    public void reorder(int start, int end) {
        byte maxLevel = this.orderLevels[start];
        byte minLevel = maxLevel;
        byte onlyOddLevels = maxLevel;
        byte onlyEvenLevels = maxLevel;
        for (int k = start + 1; k <= end; k++) {
            byte b = this.orderLevels[k];
            if (b > maxLevel) {
                maxLevel = b;
            } else if (b < minLevel) {
                minLevel = b;
            }
            onlyOddLevels = (byte) (onlyOddLevels & b);
            onlyEvenLevels = (byte) (onlyEvenLevels | b);
        }
        if ((onlyEvenLevels & 1) == 0) {
            return;
        }
        if ((onlyOddLevels & 1) == 1) {
            flip(start, end + 1);
            return;
        }
        byte minLevel2 = (byte) (minLevel | 1);
        while (maxLevel >= minLevel2) {
            int pstart = start;
            while (true) {
                if (pstart <= end && this.orderLevels[pstart] < maxLevel) {
                    pstart++;
                } else {
                    if (pstart > end) {
                        break;
                    }
                    int pend = pstart + 1;
                    while (pend <= end && this.orderLevels[pend] >= maxLevel) {
                        pend++;
                    }
                    flip(pstart, pend);
                    pstart = pend + 1;
                }
            }
            maxLevel = (byte) (maxLevel - 1);
        }
    }

    public void flip(int start, int end) {
        int mid = (start + end) / 2;
        while (true) {
            end--;
            if (start < mid) {
                int temp = this.indexChars[start];
                this.indexChars[start] = this.indexChars[end];
                this.indexChars[end] = temp;
                start++;
            } else {
                return;
            }
        }
    }

    public static boolean isWS(char c) {
        return c <= ' ';
    }
}
