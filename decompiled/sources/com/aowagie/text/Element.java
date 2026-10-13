package com.aowagie.text;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Element.class */
public interface Element {
    public static final int HEADER = 0;
    public static final int TITLE = 1;
    public static final int SUBJECT = 2;
    public static final int KEYWORDS = 3;
    public static final int AUTHOR = 4;
    public static final int PRODUCER = 5;
    public static final int CREATIONDATE = 6;
    public static final int CREATOR = 7;
    public static final int CHUNK = 10;
    public static final int PHRASE = 11;
    public static final int PARAGRAPH = 12;
    public static final int SECTION = 13;
    public static final int LIST = 14;
    public static final int LISTITEM = 15;
    public static final int CHAPTER = 16;
    public static final int ANCHOR = 17;
    public static final int CELL = 20;
    public static final int ROW = 21;
    public static final int TABLE = 22;
    public static final int PTABLE = 23;
    public static final int ANNOTATION = 29;
    public static final int RECTANGLE = 30;
    public static final int JPEG = 32;
    public static final int JPEG2000 = 33;
    public static final int IMGRAW = 34;
    public static final int IMGTEMPLATE = 35;
    public static final int JBIG2 = 36;
    public static final int MULTI_COLUMN_TEXT = 40;
    public static final int MARKED = 50;
    public static final int YMARK = 55;
    public static final int ALIGN_UNDEFINED = -1;
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;
    public static final int ALIGN_JUSTIFIED = 3;
    public static final int ALIGN_TOP = 4;
    public static final int ALIGN_MIDDLE = 5;
    public static final int ALIGN_BOTTOM = 6;
    public static final int ALIGN_BASELINE = 7;
    public static final int ALIGN_JUSTIFIED_ALL = 8;
    public static final int CCITTG4 = 256;
    public static final int CCITTG3_1D = 257;
    public static final int CCITTG3_2D = 258;
    public static final int CCITT_BLACKIS1 = 1;
    public static final int CCITT_ENCODEDBYTEALIGN = 2;
    public static final int CCITT_ENDOFLINE = 4;
    public static final int CCITT_ENDOFBLOCK = 8;

    boolean process(ElementListener elementListener);

    int type();

    boolean isContent();

    boolean isNestable();

    ArrayList getChunks();

    String toString();
}
