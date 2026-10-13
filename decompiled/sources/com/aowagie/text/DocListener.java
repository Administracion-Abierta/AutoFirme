package com.aowagie.text;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/DocListener.class */
public interface DocListener extends ElementListener {
    void open();

    void close();

    boolean newPage();

    boolean setPageSize(Rectangle rectangle);

    boolean setMargins(float f, float f2, float f3, float f4);

    boolean setMarginMirroring(boolean z);

    boolean setMarginMirroringTopBottom(boolean z);

    void setPageCount(int i);

    void resetPageCount();

    void setHeader(HeaderFooter headerFooter);

    void resetHeader();

    void setFooter(HeaderFooter headerFooter);

    void resetFooter();
}
