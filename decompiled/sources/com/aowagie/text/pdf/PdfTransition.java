package com.aowagie.text.pdf;

import com.twelvemonkeys.imageio.metadata.tiff.TIFF;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfTransition.class */
public class PdfTransition {
    private static final int SPLITVOUT = 1;
    private static final int SPLITHOUT = 2;
    private static final int SPLITVIN = 3;
    private static final int SPLITHIN = 4;
    private static final int BLINDV = 5;
    private static final int BLINDH = 6;
    private static final int INBOX = 7;
    private static final int OUTBOX = 8;
    private static final int LRWIPE = 9;
    private static final int RLWIPE = 10;
    private static final int BTWIPE = 11;
    private static final int TBWIPE = 12;
    private static final int DISSOLVE = 13;
    private static final int LRGLITTER = 14;
    private static final int TBGLITTER = 15;
    private static final int DGLITTER = 16;
    private final int duration;
    private final int type;

    public PdfTransition() {
        this(6);
    }

    private PdfTransition(int type) {
        this(type, 1);
    }

    private PdfTransition(int type, int duration) {
        this.duration = duration;
        this.type = type;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getType() {
        return this.type;
    }

    public PdfDictionary getTransitionDictionary() {
        PdfDictionary trans = new PdfDictionary(PdfName.TRANS);
        switch (this.type) {
            case 1:
                trans.put(PdfName.S, PdfName.SPLIT);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DM, PdfName.V);
                trans.put(PdfName.M, PdfName.O);
                break;
            case 2:
                trans.put(PdfName.S, PdfName.SPLIT);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DM, PdfName.H);
                trans.put(PdfName.M, PdfName.O);
                break;
            case 3:
                trans.put(PdfName.S, PdfName.SPLIT);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DM, PdfName.V);
                trans.put(PdfName.M, PdfName.I);
                break;
            case 4:
                trans.put(PdfName.S, PdfName.SPLIT);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DM, PdfName.H);
                trans.put(PdfName.M, PdfName.I);
                break;
            case 5:
                trans.put(PdfName.S, PdfName.BLINDS);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DM, PdfName.V);
                break;
            case 6:
                trans.put(PdfName.S, PdfName.BLINDS);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DM, PdfName.H);
                break;
            case 7:
                trans.put(PdfName.S, PdfName.BOX);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.M, PdfName.I);
                break;
            case 8:
                trans.put(PdfName.S, PdfName.BOX);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.M, PdfName.O);
                break;
            case 9:
                trans.put(PdfName.S, PdfName.WIPE);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DI, new PdfNumber(0));
                break;
            case 10:
                trans.put(PdfName.S, PdfName.WIPE);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DI, new PdfNumber(180));
                break;
            case 11:
                trans.put(PdfName.S, PdfName.WIPE);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DI, new PdfNumber(90));
                break;
            case 12:
                trans.put(PdfName.S, PdfName.WIPE);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DI, new PdfNumber(TIFF.TAG_IMAGE_DESCRIPTION));
                break;
            case 13:
                trans.put(PdfName.S, PdfName.DISSOLVE);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                break;
            case 14:
                trans.put(PdfName.S, PdfName.GLITTER);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DI, new PdfNumber(0));
                break;
            case 15:
                trans.put(PdfName.S, PdfName.GLITTER);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DI, new PdfNumber(TIFF.TAG_IMAGE_DESCRIPTION));
                break;
            case 16:
                trans.put(PdfName.S, PdfName.GLITTER);
                trans.put(PdfName.D, new PdfNumber(this.duration));
                trans.put(PdfName.DI, new PdfNumber(TIFF.TAG_ARTIST));
                break;
        }
        return trans;
    }
}
