package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Document.class */
public class Document implements DocListener {
    private static final String ITEXT = "iText";
    private static final String RELEASE = "2.1.7";
    private static final String ITEXT_VERSION = "Cliente @firma";
    public static boolean compress = true;
    public static boolean plainRandomAccess = false;
    public static float wmfFontCorrection = 0.86f;
    private final ArrayList listeners;
    protected boolean open;
    protected boolean close;
    protected Rectangle pageSize;
    protected float marginLeft;
    protected float marginRight;
    protected float marginTop;
    protected float marginBottom;
    protected boolean marginMirroring;
    protected boolean marginMirroringTopBottom;
    private String javaScript_onLoad;
    private String javaScript_onUnLoad;
    private String htmlStyleClass;
    protected int pageN;
    protected HeaderFooter header;
    protected HeaderFooter footer;
    private int chapternumber;

    public Document() {
        this(PageSize.A4);
    }

    public Document(Rectangle pageSize) {
        this(pageSize, 36.0f, 36.0f, 36.0f, 36.0f);
    }

    public Document(Rectangle pageSize, float marginLeft, float marginRight, float marginTop, float marginBottom) {
        this.listeners = new ArrayList();
        this.marginLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.marginRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.marginTop = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.marginBottom = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.marginMirroring = false;
        this.marginMirroringTopBottom = false;
        this.javaScript_onLoad = null;
        this.javaScript_onUnLoad = null;
        this.htmlStyleClass = null;
        this.pageN = 0;
        this.header = null;
        this.footer = null;
        this.chapternumber = 0;
        this.pageSize = pageSize;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
    }

    public void addDocListener(DocListener listener) {
        this.listeners.add(listener);
    }

    public void removeDocListener(DocListener listener) {
        this.listeners.remove(listener);
    }

    @Override // com.aowagie.text.ElementListener
    public boolean add(Element element) throws DocumentException {
        if (this.close) {
            throw new DocumentException("The document has been closed. You can't add any Elements.");
        }
        if (!this.open && element.isContent()) {
            throw new DocumentException("The document is not open yet; you can only add Meta information.");
        }
        boolean success = false;
        if (element instanceof ChapterAutoNumber) {
            this.chapternumber = ((ChapterAutoNumber) element).setAutomaticNumber(this.chapternumber);
        }
        for (DocListener listener : this.listeners) {
            success |= listener.add(element);
        }
        if (element instanceof LargeElement) {
            LargeElement e = (LargeElement) element;
            if (!e.isComplete()) {
                e.flushContent();
            }
        }
        return success;
    }

    @Override // com.aowagie.text.DocListener
    public void open() {
        if (!this.close) {
            this.open = true;
        }
        for (DocListener listener : this.listeners) {
            listener.setPageSize(this.pageSize);
            listener.setMargins(this.marginLeft, this.marginRight, this.marginTop, this.marginBottom);
            listener.open();
        }
    }

    @Override // com.aowagie.text.DocListener
    public boolean setPageSize(Rectangle pageSize) {
        this.pageSize = pageSize;
        for (DocListener listener : this.listeners) {
            listener.setPageSize(pageSize);
        }
        return true;
    }

    @Override // com.aowagie.text.DocListener
    public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        for (DocListener listener : this.listeners) {
            listener.setMargins(marginLeft, marginRight, marginTop, marginBottom);
        }
        return true;
    }

    @Override // com.aowagie.text.DocListener
    public boolean newPage() {
        if (!this.open || this.close) {
            return false;
        }
        for (DocListener listener : this.listeners) {
            listener.newPage();
        }
        return true;
    }

    @Override // com.aowagie.text.DocListener
    public void setHeader(HeaderFooter header) {
        this.header = header;
        for (DocListener listener : this.listeners) {
            listener.setHeader(header);
        }
    }

    @Override // com.aowagie.text.DocListener
    public void resetHeader() {
        this.header = null;
        for (DocListener listener : this.listeners) {
            listener.resetHeader();
        }
    }

    @Override // com.aowagie.text.DocListener
    public void setFooter(HeaderFooter footer) {
        this.footer = footer;
        for (DocListener listener : this.listeners) {
            listener.setFooter(footer);
        }
    }

    @Override // com.aowagie.text.DocListener
    public void resetFooter() {
        this.footer = null;
        for (DocListener listener : this.listeners) {
            listener.resetFooter();
        }
    }

    @Override // com.aowagie.text.DocListener
    public void resetPageCount() {
        this.pageN = 0;
        for (DocListener listener : this.listeners) {
            listener.resetPageCount();
        }
    }

    @Override // com.aowagie.text.DocListener
    public void setPageCount(int pageN) {
        this.pageN = pageN;
        for (DocListener listener : this.listeners) {
            listener.setPageCount(pageN);
        }
    }

    public int getPageNumber() {
        return this.pageN;
    }

    @Override // com.aowagie.text.DocListener
    public void close() {
        if (!this.close) {
            this.open = false;
            this.close = true;
        }
        for (DocListener listener : this.listeners) {
            listener.close();
        }
    }

    public boolean addHeader(String name, String content) {
        try {
            return add(new Header(name, content));
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addTitle(String title) {
        try {
            return add(new Meta(1, title));
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addSubject(String subject) {
        try {
            return add(new Meta(2, subject));
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addKeywords(String keywords) {
        try {
            return add(new Meta(3, keywords));
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addAuthor(String author) {
        try {
            return add(new Meta(4, author));
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addCreator(String creator) {
        try {
            return add(new Meta(7, creator));
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addProducer() {
        try {
            return add(new Meta(5, getVersion()));
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addCreationDate() {
        return addCreationDate(new GregorianCalendar());
    }

    public boolean addCreationDate(Calendar globalDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            return add(new Meta(6, sdf.format(globalDate.getTime())));
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public float leftMargin() {
        return this.marginLeft;
    }

    public float rightMargin() {
        return this.marginRight;
    }

    public float topMargin() {
        return this.marginTop;
    }

    public float bottomMargin() {
        return this.marginBottom;
    }

    public float left() {
        return this.pageSize.getLeft(this.marginLeft);
    }

    public float right() {
        return this.pageSize.getRight(this.marginRight);
    }

    public float top() {
        return this.pageSize.getTop(this.marginTop);
    }

    public float bottom() {
        return this.pageSize.getBottom(this.marginBottom);
    }

    public float left(float margin) {
        return this.pageSize.getLeft(this.marginLeft + margin);
    }

    public float right(float margin) {
        return this.pageSize.getRight(this.marginRight + margin);
    }

    public float top(float margin) {
        return this.pageSize.getTop(this.marginTop + margin);
    }

    public float bottom(float margin) {
        return this.pageSize.getBottom(this.marginBottom + margin);
    }

    public Rectangle getPageSize() {
        return this.pageSize;
    }

    public boolean isOpen() {
        return this.open;
    }

    public static final String getProduct() {
        return ITEXT;
    }

    public static final String getRelease() {
        return RELEASE;
    }

    public static final String getVersion() {
        return ITEXT_VERSION;
    }

    public void setJavaScript_onLoad(String code) {
        this.javaScript_onLoad = code;
    }

    public String getJavaScript_onLoad() {
        return this.javaScript_onLoad;
    }

    public void setJavaScript_onUnLoad(String code) {
        this.javaScript_onUnLoad = code;
    }

    public String getJavaScript_onUnLoad() {
        return this.javaScript_onUnLoad;
    }

    public void setHtmlStyleClass(String htmlStyleClass) {
        this.htmlStyleClass = htmlStyleClass;
    }

    public String getHtmlStyleClass() {
        return this.htmlStyleClass;
    }

    @Override // com.aowagie.text.DocListener
    public boolean setMarginMirroring(boolean marginMirroring) {
        this.marginMirroring = marginMirroring;
        for (DocListener listener : this.listeners) {
            listener.setMarginMirroring(marginMirroring);
        }
        return true;
    }

    @Override // com.aowagie.text.DocListener
    public boolean setMarginMirroringTopBottom(boolean marginMirroringTopBottom) {
        this.marginMirroringTopBottom = marginMirroringTopBottom;
        for (DocListener listener : this.listeners) {
            listener.setMarginMirroringTopBottom(marginMirroringTopBottom);
        }
        return true;
    }

    public boolean isMarginMirroring() {
        return this.marginMirroring;
    }
}
