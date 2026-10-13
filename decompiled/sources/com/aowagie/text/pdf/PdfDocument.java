package com.aowagie.text.pdf;

import com.aowagie.text.Anchor;
import com.aowagie.text.Annotation;
import com.aowagie.text.BadElementException;
import com.aowagie.text.Chunk;
import com.aowagie.text.Document;
import com.aowagie.text.DocumentException;
import com.aowagie.text.Element;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Font;
import com.aowagie.text.HeaderFooter;
import com.aowagie.text.Image;
import com.aowagie.text.List;
import com.aowagie.text.ListItem;
import com.aowagie.text.MarkedObject;
import com.aowagie.text.MarkedSection;
import com.aowagie.text.Meta;
import com.aowagie.text.Paragraph;
import com.aowagie.text.Phrase;
import com.aowagie.text.Rectangle;
import com.aowagie.text.Section;
import com.aowagie.text.SimpleTable;
import com.aowagie.text.Table;
import com.aowagie.text.pdf.collection.PdfCollection;
import com.aowagie.text.pdf.draw.DrawInterface;
import com.aowagie.text.pdf.internal.PdfAnnotationsImp;
import com.aowagie.text.pdf.internal.PdfViewerPreferencesImp;
import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfDocument.class */
class PdfDocument extends Document {
    private PdfWriter writer;
    private PdfContentByte text;
    private PdfContentByte graphics;
    private float leading;
    private int alignment;
    private float currentHeight;
    protected boolean isSectionTitle;
    protected int leadingCount;
    protected PdfAction anchorAction;
    private int textEmptySize;
    private byte[] xmpMetadata;
    private float nextMarginLeft;
    private float nextMarginRight;
    private float nextMarginTop;
    private float nextMarginBottom;
    private boolean firstPageEvent;
    private PdfLine line;
    private ArrayList lines;
    private int lastElementType;
    private static final String hangingPunctuation = ".,;:'";
    private final Indentation indentation;
    private PdfInfo info;
    private PdfOutline rootOutline;
    private PdfOutline currentOutline;
    private final PdfViewerPreferencesImp viewerPreferences;
    protected PdfPageLabels pageLabels;
    private final TreeMap localDestinations;
    private int jsCounter;
    private final HashMap documentLevelJS;
    private static final DecimalFormat SIXTEEN_DIGITS = new DecimalFormat("0000000000000000");
    private final HashMap documentFileAttachment;
    private String openActionName;
    private PdfAction openActionAction;
    private PdfDictionary additionalActions;
    private PdfCollection collection;
    private PdfAnnotationsImp annotationsImp;
    private int markPoint;
    private Rectangle nextPageSize;
    private HashMap thisBoxSize;
    private final HashMap boxSize;
    private boolean pageEmpty;
    private int duration;
    private PdfTransition transition;
    private PdfDictionary pageAA;
    private PdfIndirectReference thumb;
    private PageResources pageResources;
    private boolean strictImageSequence;
    private float imageEnd;
    private Image imageWait;

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfDocument$PdfInfo.class */
    static class PdfInfo extends PdfDictionary {
        PdfInfo() {
            addProducer();
            addCreationDate();
        }

        private PdfInfo(Calendar globalDate) {
            addProducer();
            addCreationDate(globalDate);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addTitle(String title) {
            put(PdfName.TITLE, new PdfString(title, PdfObject.TEXT_UNICODE));
        }

        void addSubject(String subject) {
            put(PdfName.SUBJECT, new PdfString(subject, PdfObject.TEXT_UNICODE));
        }

        void addKeywords(String keywords) {
            put(PdfName.KEYWORDS, new PdfString(keywords, PdfObject.TEXT_UNICODE));
        }

        void addAuthor(String author) {
            put(PdfName.AUTHOR, new PdfString(author, PdfObject.TEXT_UNICODE));
        }

        void addCreator(String creator) {
            put(PdfName.CREATOR, new PdfString(creator, PdfObject.TEXT_UNICODE));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addProducer() {
            put(PdfName.PRODUCER, new PdfString(Document.getVersion()));
        }

        private void addCreationDate(Calendar globalDate) {
            PdfString date = new PdfDate(globalDate);
            put(PdfName.CREATIONDATE, date);
            put(PdfName.MODDATE, date);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addCreationDate() {
            PdfString date = new PdfDate(new GregorianCalendar());
            put(PdfName.CREATIONDATE, date);
            put(PdfName.MODDATE, date);
        }

        void addkey(String key, String value) {
            if (key.equals("Producer") || key.equals("CreationDate")) {
                return;
            }
            put(new PdfName(key), new PdfString(value, PdfObject.TEXT_UNICODE));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfDocument$PdfCatalog.class */
    static class PdfCatalog extends PdfDictionary {
        private final PdfWriter writer;

        private PdfCatalog(PdfIndirectReference pages, PdfWriter writer) {
            super(CATALOG);
            this.writer = writer;
            put(PdfName.PAGES, pages);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addNames(TreeMap localDestinations, HashMap documentLevelJS, HashMap documentFileAttachment, PdfWriter writer) {
            if (localDestinations.isEmpty() && documentLevelJS.isEmpty() && documentFileAttachment.isEmpty()) {
                return;
            }
            try {
                PdfDictionary names = new PdfDictionary();
                if (!localDestinations.isEmpty()) {
                    PdfArray ar = new PdfArray();
                    for (Map.Entry entry : localDestinations.entrySet()) {
                        String name = (String) entry.getKey();
                        Object[] obj = (Object[]) entry.getValue();
                        if (obj[2] != null) {
                            PdfIndirectReference ref = (PdfIndirectReference) obj[1];
                            ar.add(new PdfString(name, null));
                            ar.add(ref);
                        }
                    }
                    if (ar.size() > 0) {
                        PdfDictionary dests = new PdfDictionary();
                        dests.put(PdfName.NAMES, ar);
                        names.put(PdfName.DESTS, writer.addToBody(dests).getIndirectReference());
                    }
                }
                if (!documentLevelJS.isEmpty()) {
                    PdfDictionary tree = PdfNameTree.writeTree(documentLevelJS, writer);
                    names.put(PdfName.JAVASCRIPT, writer.addToBody(tree).getIndirectReference());
                }
                if (!documentFileAttachment.isEmpty()) {
                    names.put(PdfName.EMBEDDEDFILES, writer.addToBody(PdfNameTree.writeTree(documentFileAttachment, writer)).getIndirectReference());
                }
                if (names.size() > 0) {
                    put(PdfName.NAMES, writer.addToBody(names).getIndirectReference());
                }
            } catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setOpenAction(PdfAction action) {
            put(PdfName.OPENACTION, action);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAdditionalActions(PdfDictionary actions) {
            try {
                put(PdfName.AA, this.writer.addToBody(actions).getIndirectReference());
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    PdfDocument() {
        this(new GregorianCalendar());
    }

    PdfDocument(Calendar globalDate) {
        this.leading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.alignment = 0;
        this.currentHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.isSectionTitle = false;
        this.leadingCount = 0;
        this.anchorAction = null;
        this.xmpMetadata = null;
        this.firstPageEvent = true;
        this.line = null;
        this.lines = new ArrayList();
        this.lastElementType = -1;
        this.indentation = new Indentation();
        this.info = new PdfInfo();
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.localDestinations = new TreeMap();
        this.documentLevelJS = new HashMap();
        this.documentFileAttachment = new HashMap();
        this.nextPageSize = null;
        this.thisBoxSize = new LinkedHashMap();
        this.boxSize = new LinkedHashMap();
        this.pageEmpty = true;
        this.duration = -1;
        this.transition = null;
        this.pageAA = null;
        this.strictImageSequence = false;
        this.imageEnd = -1.0f;
        this.imageWait = null;
        addProducer();
        addCreationDate(globalDate);
        this.info = new PdfInfo(globalDate);
    }

    void addWriter(PdfWriter writer) throws DocumentException {
        if (this.writer == null) {
            this.writer = writer;
            this.annotationsImp = new PdfAnnotationsImp(writer);
            return;
        }
        throw new DocumentException("You can only add a writer to a PdfDocument once.");
    }

    public float getLeading() {
        return this.leading;
    }

    void setLeading(float leading) {
        this.leading = leading;
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.ElementListener
    public boolean add(Element element) throws DocumentException {
        MarkedObject mo;
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        try {
            switch (element.type()) {
                case 0:
                    this.info.addkey(((Meta) element).getName(), ((Meta) element).getContent());
                    this.lastElementType = element.type();
                    return true;
                case 1:
                    this.info.addTitle(((Meta) element).getContent());
                    this.lastElementType = element.type();
                    return true;
                case 2:
                    this.info.addSubject(((Meta) element).getContent());
                    this.lastElementType = element.type();
                    return true;
                case 3:
                    this.info.addKeywords(((Meta) element).getContent());
                    this.lastElementType = element.type();
                    return true;
                case 4:
                    this.info.addAuthor(((Meta) element).getContent());
                    this.lastElementType = element.type();
                    return true;
                case 5:
                    this.info.addProducer();
                    this.lastElementType = element.type();
                    return true;
                case 6:
                    this.info.addCreationDate();
                    this.lastElementType = element.type();
                    return true;
                case 7:
                    this.info.addCreator(((Meta) element).getContent());
                    this.lastElementType = element.type();
                    return true;
                case 8:
                case 9:
                case 18:
                case 19:
                case 20:
                case 21:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 31:
                case 37:
                case 38:
                case 39:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 51:
                case 52:
                case 53:
                case 54:
                default:
                    return false;
                case 10:
                    if (this.line == null) {
                        carriageReturn();
                    }
                    PdfChunk chunk = new PdfChunk((Chunk) element, this.anchorAction);
                    while (true) {
                        PdfChunk overflow = this.line.add(chunk);
                        if (overflow != null) {
                            carriageReturn();
                            chunk = overflow;
                            chunk.trimFirstSpace();
                        } else {
                            this.pageEmpty = false;
                            if (chunk.isAttribute(Chunk.NEWPAGE)) {
                                newPage();
                            }
                            this.lastElementType = element.type();
                            return true;
                        }
                    }
                    break;
                case 11:
                    this.leadingCount++;
                    this.leading = ((Phrase) element).getLeading();
                    element.process(this);
                    this.leadingCount--;
                    this.lastElementType = element.type();
                    return true;
                case 12:
                    this.leadingCount++;
                    Paragraph paragraph = (Paragraph) element;
                    addSpacing(paragraph.getSpacingBefore(), this.leading, paragraph.getFont());
                    this.alignment = paragraph.getAlignment();
                    this.leading = paragraph.getTotalLeading();
                    carriageReturn();
                    if (this.currentHeight + this.line.height() + this.leading > indentTop() - indentBottom()) {
                        newPage();
                    }
                    this.indentation.indentLeft += paragraph.getIndentationLeft();
                    this.indentation.indentRight += paragraph.getIndentationRight();
                    carriageReturn();
                    PdfPageEvent pageEvent = this.writer.getPageEvent();
                    if (pageEvent != null && !this.isSectionTitle) {
                        pageEvent.onParagraph(this.writer, this, indentTop() - this.currentHeight);
                    }
                    if (paragraph.getKeepTogether()) {
                        carriageReturn();
                        PdfPTable table = new PdfPTable(1);
                        table.setWidthPercentage(100.0f);
                        PdfPCell cell = new PdfPCell();
                        cell.addElement(paragraph);
                        cell.setBorder(0);
                        cell.setPadding(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                        table.addCell(cell);
                        this.indentation.indentLeft -= paragraph.getIndentationLeft();
                        this.indentation.indentRight -= paragraph.getIndentationRight();
                        add(table);
                        this.indentation.indentLeft += paragraph.getIndentationLeft();
                        this.indentation.indentRight += paragraph.getIndentationRight();
                    } else {
                        this.line.setExtraIndent(paragraph.getFirstLineIndent());
                        element.process(this);
                        carriageReturn();
                        addSpacing(paragraph.getSpacingAfter(), paragraph.getTotalLeading(), paragraph.getFont());
                    }
                    if (pageEvent != null && !this.isSectionTitle) {
                        pageEvent.onParagraphEnd(this.writer, this, indentTop() - this.currentHeight);
                    }
                    this.alignment = 0;
                    this.indentation.indentLeft -= paragraph.getIndentationLeft();
                    this.indentation.indentRight -= paragraph.getIndentationRight();
                    carriageReturn();
                    this.leadingCount--;
                    this.lastElementType = element.type();
                    return true;
                case 13:
                case 16:
                    Section section = (Section) element;
                    PdfPageEvent pageEvent2 = this.writer.getPageEvent();
                    boolean hasTitle = section.isNotAddedYet() && section.getTitle() != null;
                    if (section.isTriggerNewPage()) {
                        newPage();
                    }
                    if (hasTitle) {
                        float fith = indentTop() - this.currentHeight;
                        int rotation = this.pageSize.getRotation();
                        if (rotation == 90 || rotation == 180) {
                            fith = this.pageSize.getHeight() - fith;
                        }
                        PdfDestination destination = new PdfDestination(2, fith);
                        while (this.currentOutline.level() >= section.getDepth()) {
                            this.currentOutline = this.currentOutline.parent();
                        }
                        PdfOutline outline = new PdfOutline(this.currentOutline, destination, section.getBookmarkTitle(), section.isBookmarkOpen());
                        this.currentOutline = outline;
                    }
                    carriageReturn();
                    this.indentation.sectionIndentLeft += section.getIndentationLeft();
                    this.indentation.sectionIndentRight += section.getIndentationRight();
                    if (section.isNotAddedYet() && pageEvent2 != null) {
                        if (element.type() == 16) {
                            pageEvent2.onChapter(this.writer, this, indentTop() - this.currentHeight, section.getTitle());
                        } else {
                            pageEvent2.onSection(this.writer, this, indentTop() - this.currentHeight, section.getDepth(), section.getTitle());
                        }
                    }
                    if (hasTitle) {
                        this.isSectionTitle = true;
                        add(section.getTitle());
                        this.isSectionTitle = false;
                    }
                    this.indentation.sectionIndentLeft += section.getIndentation();
                    element.process(this);
                    flushLines();
                    this.indentation.sectionIndentLeft -= section.getIndentationLeft() + section.getIndentation();
                    this.indentation.sectionIndentRight -= section.getIndentationRight();
                    if (section.isComplete() && pageEvent2 != null) {
                        if (element.type() == 16) {
                            pageEvent2.onChapterEnd(this.writer, this, indentTop() - this.currentHeight);
                        } else {
                            pageEvent2.onSectionEnd(this.writer, this, indentTop() - this.currentHeight);
                        }
                    }
                    this.lastElementType = element.type();
                    return true;
                case 14:
                    List list = (List) element;
                    if (list.isAlignindent()) {
                        list.normalizeIndentation();
                    }
                    this.indentation.listIndentLeft += list.getIndentationLeft();
                    this.indentation.indentRight += list.getIndentationRight();
                    element.process(this);
                    this.indentation.listIndentLeft -= list.getIndentationLeft();
                    this.indentation.indentRight -= list.getIndentationRight();
                    carriageReturn();
                    this.lastElementType = element.type();
                    return true;
                case 15:
                    this.leadingCount++;
                    ListItem listItem = (ListItem) element;
                    addSpacing(listItem.getSpacingBefore(), this.leading, listItem.getFont());
                    this.alignment = listItem.getAlignment();
                    this.indentation.listIndentLeft += listItem.getIndentationLeft();
                    this.indentation.indentRight += listItem.getIndentationRight();
                    this.leading = listItem.getTotalLeading();
                    carriageReturn();
                    this.line.setListItem(listItem);
                    element.process(this);
                    addSpacing(listItem.getSpacingAfter(), listItem.getTotalLeading(), listItem.getFont());
                    if (this.line.hasToBeJustified()) {
                        this.line.resetAlignment();
                    }
                    carriageReturn();
                    this.indentation.listIndentLeft -= listItem.getIndentationLeft();
                    this.indentation.indentRight -= listItem.getIndentationRight();
                    this.leadingCount--;
                    this.lastElementType = element.type();
                    return true;
                case 17:
                    this.leadingCount++;
                    Anchor anchor = (Anchor) element;
                    String url = anchor.getReference();
                    this.leading = anchor.getLeading();
                    if (url != null) {
                        this.anchorAction = new PdfAction(url);
                    }
                    element.process(this);
                    this.anchorAction = null;
                    this.leadingCount--;
                    this.lastElementType = element.type();
                    return true;
                case 22:
                    if (element instanceof SimpleTable) {
                        PdfPTable ptable = ((SimpleTable) element).createPdfPTable();
                        if (ptable.size() > ptable.getHeaderRows()) {
                            ensureNewLine();
                            flushLines();
                            addPTable(ptable);
                            this.pageEmpty = false;
                        }
                    } else if (element instanceof Table) {
                        try {
                            PdfPTable ptable2 = ((Table) element).createPdfPTable();
                            if (ptable2.size() > ptable2.getHeaderRows()) {
                                ensureNewLine();
                                flushLines();
                                addPTable(ptable2);
                                this.pageEmpty = false;
                                break;
                            }
                        } catch (BadElementException e) {
                            float offset = ((Table) element).getOffset();
                            if (Float.isNaN(offset)) {
                                offset = this.leading;
                            }
                            carriageReturn();
                            this.lines.add(new PdfLine(indentLeft(), indentRight(), this.alignment, offset));
                            this.currentHeight += offset;
                            addPdfTable((Table) element);
                        }
                    } else {
                        return false;
                    }
                    this.lastElementType = element.type();
                    return true;
                case 23:
                    PdfPTable ptable3 = (PdfPTable) element;
                    if (ptable3.size() > ptable3.getHeaderRows()) {
                        ensureNewLine();
                        flushLines();
                        addPTable(ptable3);
                        this.pageEmpty = false;
                        newLine();
                    }
                    this.lastElementType = element.type();
                    return true;
                case 29:
                    if (this.line == null) {
                        carriageReturn();
                    }
                    Annotation annot = (Annotation) element;
                    Rectangle rect = new Rectangle(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                    if (this.line != null) {
                        rect = new Rectangle(annot.llx(indentRight() - this.line.widthLeft()), annot.lly(indentTop() - this.currentHeight), annot.urx((indentRight() - this.line.widthLeft()) + 20.0f), annot.ury((indentTop() - this.currentHeight) - 20.0f));
                    }
                    PdfAnnotation an = PdfAnnotationsImp.convertAnnotation(this.writer, annot, rect);
                    this.annotationsImp.addPlainAnnotation(an);
                    this.pageEmpty = false;
                    this.lastElementType = element.type();
                    return true;
                case 30:
                    Rectangle rectangle = (Rectangle) element;
                    this.graphics.rectangle(rectangle);
                    this.pageEmpty = false;
                    this.lastElementType = element.type();
                    return true;
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                    add((Image) element);
                    this.lastElementType = element.type();
                    return true;
                case 40:
                    ensureNewLine();
                    flushLines();
                    MultiColumnText multiText = (MultiColumnText) element;
                    float height = multiText.write(this.writer.getDirectContent(), this, indentTop() - this.currentHeight);
                    this.currentHeight += height;
                    this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, (-1.0f) * height);
                    this.pageEmpty = false;
                    this.lastElementType = element.type();
                    return true;
                case 50:
                    if ((element instanceof MarkedSection) && (mo = ((MarkedSection) element).getTitle()) != null) {
                        mo.process(this);
                    }
                    ((MarkedObject) element).process(this);
                    this.lastElementType = element.type();
                    return true;
                case 55:
                    DrawInterface zh = (DrawInterface) element;
                    zh.draw(this.graphics, indentLeft(), indentBottom(), indentRight(), indentTop(), (indentTop() - this.currentHeight) - (this.leadingCount > 0 ? this.leading : ColumnText.GLOBAL_SPACE_CHAR_RATIO));
                    this.pageEmpty = false;
                    this.lastElementType = element.type();
                    return true;
            }
        } catch (Exception e2) {
            throw new DocumentException(e2);
        }
        throw new DocumentException(e2);
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public void open() {
        if (!this.open) {
            super.open();
            this.writer.open();
            this.rootOutline = new PdfOutline(this.writer);
            this.currentOutline = this.rootOutline;
        }
        try {
            initPage();
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public void close() {
        if (this.close) {
            return;
        }
        try {
            boolean wasImage = this.imageWait != null;
            newPage();
            if (this.imageWait != null || wasImage) {
                newPage();
            }
            if (this.annotationsImp.hasUnusedAnnotations()) {
                throw new RuntimeException("Not all annotations could be added to the document (the document doesn't have enough pages).");
            }
            PdfPageEvent pageEvent = this.writer.getPageEvent();
            if (pageEvent != null) {
                pageEvent.onCloseDocument(this.writer, this);
            }
            super.close();
            this.writer.addLocalDestinations(this.localDestinations);
            calculateOutlineCount();
            writeOutlines();
            this.writer.close();
        } catch (Exception e) {
            throw ExceptionConverter.convertException(e);
        }
    }

    public void setXmpMetadata(byte[] xmpMetadata) {
        this.xmpMetadata = xmpMetadata;
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public boolean newPage() {
        this.lastElementType = -1;
        if (this.writer == null || (this.writer.getDirectContent().size() == 0 && this.writer.getDirectContentUnder().size() == 0 && (this.pageEmpty || this.writer.isPaused()))) {
            setNewPageSizeAndMargins();
            return false;
        }
        if (!this.open || this.close) {
            throw new RuntimeException("The document isn't open.");
        }
        PdfPageEvent pageEvent = this.writer.getPageEvent();
        if (pageEvent != null) {
            pageEvent.onEndPage(this.writer, this);
        }
        super.newPage();
        this.indentation.imageIndentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.indentation.imageIndentRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        try {
            flushLines();
            int rotation = this.pageSize.getRotation();
            if (this.writer.isPdfX()) {
                if (this.thisBoxSize.containsKey("art") && this.thisBoxSize.containsKey("trim")) {
                    throw new PdfXConformanceException("Only one of ArtBox or TrimBox can exist in the page.");
                }
                if (!this.thisBoxSize.containsKey("art") && !this.thisBoxSize.containsKey("trim")) {
                    if (this.thisBoxSize.containsKey("crop")) {
                        this.thisBoxSize.put("trim", this.thisBoxSize.get("crop"));
                    } else {
                        this.thisBoxSize.put("trim", new PdfRectangle(this.pageSize, this.pageSize.getRotation()));
                    }
                }
            }
            this.pageResources.addDefaultColorDiff(this.writer.getDefaultColorspace());
            if (this.writer.isRgbTransparencyBlending()) {
                PdfDictionary dcs = new PdfDictionary();
                dcs.put(PdfName.CS, PdfName.DEVICERGB);
                this.pageResources.addDefaultColorDiff(dcs);
            }
            PdfDictionary resources = this.pageResources.getResources();
            PdfPage page = new PdfPage(new PdfRectangle(this.pageSize, rotation), this.thisBoxSize, resources, rotation);
            page.put(PdfName.TABS, this.writer.getTabs());
            if (this.xmpMetadata != null) {
                PdfStream xmp = new PdfStream(this.xmpMetadata);
                xmp.put(PdfName.TYPE, PdfName.METADATA);
                xmp.put(PdfName.SUBTYPE, PdfName.XML);
                PdfEncryption crypto = this.writer.getEncryption();
                if (crypto != null && !crypto.isMetadataEncrypted()) {
                    PdfArray ar = new PdfArray();
                    ar.add(PdfName.CRYPT);
                    xmp.put(PdfName.FILTER, ar);
                }
                page.put(PdfName.METADATA, this.writer.addToBody(xmp).getIndirectReference());
            }
            if (this.transition != null) {
                page.put(PdfName.TRANS, this.transition.getTransitionDictionary());
                this.transition = null;
            }
            if (this.duration > 0) {
                page.put(PdfName.DUR, new PdfNumber(this.duration));
                this.duration = 0;
            }
            if (this.pageAA != null) {
                page.put(PdfName.AA, this.writer.addToBody(this.pageAA).getIndirectReference());
                this.pageAA = null;
            }
            if (this.thumb != null) {
                page.put(PdfName.THUMB, this.thumb);
                this.thumb = null;
            }
            if (this.writer.getUserunit() > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                page.put(PdfName.USERUNIT, new PdfNumber(this.writer.getUserunit()));
            }
            if (this.annotationsImp.hasUnusedAnnotations()) {
                PdfArray array = this.annotationsImp.rotateAnnotations(this.writer, this.pageSize);
                if (array.size() != 0) {
                    page.put(PdfName.ANNOTS, array);
                }
            }
            if (this.writer.isTagged()) {
                page.put(PdfName.STRUCTPARENTS, new PdfNumber(this.writer.getCurrentPageNumber() - 1));
            }
            if (this.text.size() > this.textEmptySize) {
                this.text.endText();
            } else {
                this.text = null;
            }
            this.writer.add(page, new PdfContents(this.writer.getDirectContentUnder(), this.graphics, this.text, this.writer.getDirectContent(), this.pageSize));
            initPage();
            return true;
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        } catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public boolean setPageSize(Rectangle pageSize) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        this.nextPageSize = new Rectangle(pageSize);
        return true;
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        this.nextMarginLeft = marginLeft;
        this.nextMarginRight = marginRight;
        this.nextMarginTop = marginTop;
        this.nextMarginBottom = marginBottom;
        return true;
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public boolean setMarginMirroring(boolean MarginMirroring) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        return super.setMarginMirroring(MarginMirroring);
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public boolean setMarginMirroringTopBottom(boolean MarginMirroringTopBottom) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        return super.setMarginMirroringTopBottom(MarginMirroringTopBottom);
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public void setPageCount(int pageN) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setPageCount(pageN);
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public void resetPageCount() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetPageCount();
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public void setHeader(HeaderFooter header) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setHeader(header);
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public void resetHeader() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetHeader();
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public void setFooter(HeaderFooter footer) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setFooter(footer);
    }

    @Override // com.aowagie.text.Document, com.aowagie.text.DocListener
    public void resetFooter() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetFooter();
    }

    private void initPage() throws DocumentException {
        this.pageN++;
        this.annotationsImp.resetAnnotations();
        this.pageResources = new PageResources();
        this.writer.resetContent();
        this.graphics = new PdfContentByte(this.writer);
        this.text = new PdfContentByte(this.writer);
        this.text.reset();
        this.text.beginText();
        this.textEmptySize = this.text.size();
        this.markPoint = 0;
        setNewPageSizeAndMargins();
        this.imageEnd = -1.0f;
        this.indentation.imageIndentRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.indentation.imageIndentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.indentation.indentBottom = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.indentation.indentTop = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.currentHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.thisBoxSize = new LinkedHashMap(this.boxSize);
        if (this.pageSize.getBackgroundColor() != null || this.pageSize.hasBorders() || this.pageSize.getBorderColor() != null) {
            add(this.pageSize);
        }
        float oldleading = this.leading;
        int oldAlignment = this.alignment;
        doFooter();
        this.text.moveText(left(), top());
        doHeader();
        this.pageEmpty = true;
        try {
            if (this.imageWait != null) {
                add(this.imageWait);
                this.imageWait = null;
            }
            this.leading = oldleading;
            this.alignment = oldAlignment;
            carriageReturn();
            PdfPageEvent pageEvent = this.writer.getPageEvent();
            if (pageEvent != null) {
                if (this.firstPageEvent) {
                    pageEvent.onOpenDocument(this.writer, this);
                }
                pageEvent.onStartPage(this.writer, this);
            }
            this.firstPageEvent = false;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private void newLine() throws DocumentException {
        this.lastElementType = -1;
        carriageReturn();
        if (this.lines != null && !this.lines.isEmpty()) {
            this.lines.add(this.line);
            this.currentHeight += this.line.height();
        }
        this.line = new PdfLine(indentLeft(), indentRight(), this.alignment, this.leading);
    }

    private void carriageReturn() {
        if (this.lines == null) {
            this.lines = new ArrayList();
        }
        if (this.line != null) {
            if (this.currentHeight + this.line.height() + this.leading < indentTop() - indentBottom()) {
                if (this.line.size() > 0) {
                    this.currentHeight += this.line.height();
                    this.lines.add(this.line);
                    this.pageEmpty = false;
                }
            } else {
                newPage();
            }
        }
        if (this.imageEnd > -1.0f && this.currentHeight > this.imageEnd) {
            this.imageEnd = -1.0f;
            this.indentation.imageIndentRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentation.imageIndentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        this.line = new PdfLine(indentLeft(), indentRight(), this.alignment, this.leading);
    }

    public float getVerticalPosition(boolean ensureNewLine) {
        if (ensureNewLine) {
            ensureNewLine();
        }
        return (top() - this.currentHeight) - this.indentation.indentTop;
    }

    private void ensureNewLine() {
        try {
            if (this.lastElementType == 11 || this.lastElementType == 10) {
                newLine();
                flushLines();
            }
        } catch (DocumentException ex) {
            throw new ExceptionConverter(ex);
        }
    }

    private float flushLines() throws DocumentException {
        if (this.lines == null) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        if (this.line != null && this.line.size() > 0) {
            this.lines.add(this.line);
            this.line = new PdfLine(indentLeft(), indentRight(), this.alignment, this.leading);
        }
        if (this.lines.isEmpty()) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        Object[] currentValues = new Object[2];
        PdfFont currentFont = null;
        float displacement = 0.0f;
        Float lastBaseFactor = Float.valueOf(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        currentValues[1] = lastBaseFactor;
        for (PdfLine l : this.lines) {
            float moveTextX = (l.indentLeft() - indentLeft()) + this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.sectionIndentLeft;
            this.text.moveText(moveTextX, -l.height());
            if (l.listSymbol() != null) {
                ColumnText.showTextAligned(this.graphics, 0, new Phrase(l.listSymbol()), this.text.getXTLM() - l.listIndent(), this.text.getYTLM(), ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            }
            currentValues[0] = currentFont;
            writeLineToContent(l, this.text, this.graphics, currentValues, this.writer.getSpaceCharRatio());
            currentFont = (PdfFont) currentValues[0];
            displacement += l.height();
            this.text.moveText(-moveTextX, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        }
        this.lines = new ArrayList();
        return displacement;
    }

    void writeLineToContent(PdfLine line, PdfContentByte text, PdfContentByte graphics, Object[] currentValues, float ratio) throws DocumentException {
        int lastIdx;
        float width;
        PdfFont currentFont = (PdfFont) currentValues[0];
        float lastBaseFactor = ((Float) currentValues[1]).floatValue();
        float hangingCorrection = 0.0f;
        float lastHScale = Float.NaN;
        float baseWordSpacing = 0.0f;
        float baseCharacterSpacing = 0.0f;
        float glueWidth = 0.0f;
        int numberOfSpaces = line.numberOfSpaces();
        int lineLen = line.GetLineLengthUtf32();
        boolean isJustified = line.hasToBeJustified() && (numberOfSpaces != 0 || lineLen > 1);
        int separatorCount = line.getSeparatorCount();
        if (separatorCount > 0) {
            glueWidth = line.widthLeft() / separatorCount;
        } else if (isJustified) {
            if (line.isNewlineSplit() && line.widthLeft() >= lastBaseFactor * (((ratio * numberOfSpaces) + lineLen) - 1.0f)) {
                if (line.isRTL()) {
                    text.moveText(line.widthLeft() - (lastBaseFactor * (((ratio * numberOfSpaces) + lineLen) - 1.0f)), ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                }
                baseWordSpacing = ratio * lastBaseFactor;
                baseCharacterSpacing = lastBaseFactor;
            } else {
                float width2 = line.widthLeft();
                PdfChunk last = line.getChunk(line.size() - 1);
                if (last != null) {
                    String s = last.toString();
                    if (s.length() > 0) {
                        char c = s.charAt(s.length() - 1);
                        if (hangingPunctuation.indexOf(c) >= 0) {
                            width2 += last.font().width(c) * 0.4f;
                            hangingCorrection = width2 - width2;
                        }
                    }
                }
                float baseFactor = width2 / (((ratio * numberOfSpaces) + lineLen) - 1.0f);
                baseWordSpacing = ratio * baseFactor;
                baseCharacterSpacing = baseFactor;
                lastBaseFactor = baseFactor;
            }
        }
        int lastChunkStroke = line.getLastStrokeChunk();
        int chunkStrokeIdx = 0;
        float xMarker = text.getXTLM();
        float yMarker = text.getYTLM();
        boolean adjustMatrix = false;
        float tabPosition = 0.0f;
        Iterator j = line.iterator();
        while (j.hasNext()) {
            PdfChunk chunk = (PdfChunk) j.next();
            Color color = chunk.color();
            float hScale = 1.0f;
            if (chunkStrokeIdx <= lastChunkStroke) {
                if (isJustified) {
                    width = chunk.getWidthCorrected(baseCharacterSpacing, baseWordSpacing);
                } else {
                    width = chunk.width();
                }
                if (chunk.isStroked()) {
                    PdfChunk nextChunk = line.getChunk(chunkStrokeIdx + 1);
                    if (chunk.isSeparator()) {
                        width = glueWidth;
                        Object[] sep = (Object[]) chunk.getAttribute(Chunk.SEPARATOR);
                        DrawInterface di = (DrawInterface) sep[0];
                        Boolean vertical = (Boolean) sep[1];
                        float fontSize = chunk.font().size();
                        float ascender = chunk.font().getFont().getFontDescriptor(1, fontSize);
                        float descender = chunk.font().getFont().getFontDescriptor(3, fontSize);
                        if (vertical.booleanValue()) {
                            di.draw(graphics, xMarker, yMarker + descender, xMarker + line.getOriginalWidth(), ascender - descender, yMarker);
                        } else {
                            di.draw(graphics, xMarker, yMarker + descender, xMarker + width, ascender - descender, yMarker);
                        }
                    }
                    if (chunk.isTab()) {
                        Object[] tab = (Object[]) chunk.getAttribute(Chunk.TAB);
                        DrawInterface di2 = (DrawInterface) tab[0];
                        float tabPosition2 = ((Float) tab[1]).floatValue() + ((Float) tab[3]).floatValue();
                        float fontSize2 = chunk.font().size();
                        float ascender2 = chunk.font().getFont().getFontDescriptor(1, fontSize2);
                        float descender2 = chunk.font().getFont().getFontDescriptor(3, fontSize2);
                        if (tabPosition2 > xMarker) {
                            di2.draw(graphics, xMarker, yMarker + descender2, tabPosition2, ascender2 - descender2, yMarker);
                        }
                        float tmp = xMarker;
                        xMarker = tabPosition2;
                        tabPosition = tmp;
                    }
                    if (chunk.isAttribute(Chunk.BACKGROUND)) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.BACKGROUND)) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        float fontSize3 = chunk.font().size();
                        float ascender3 = chunk.font().getFont().getFontDescriptor(1, fontSize3);
                        float descender3 = chunk.font().getFont().getFontDescriptor(3, fontSize3);
                        Object[] bgr = (Object[]) chunk.getAttribute(Chunk.BACKGROUND);
                        graphics.setColorFill((Color) bgr[0]);
                        float[] extra = (float[]) bgr[1];
                        graphics.rectangle(xMarker - extra[0], ((yMarker + descender3) - extra[1]) + chunk.getTextRise(), (width - subtract) + extra[0] + extra[2], (ascender3 - descender3) + extra[1] + extra[3]);
                        graphics.fill();
                        graphics.setGrayFill(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                    }
                    if (chunk.isAttribute(Chunk.UNDERLINE)) {
                        float subtract2 = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.UNDERLINE)) {
                            subtract2 = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract2 += hangingCorrection;
                        }
                        Object[][] unders = (Object[][]) chunk.getAttribute(Chunk.UNDERLINE);
                        for (Object[] obj : unders) {
                            Color scolor = (Color) obj[0];
                            float[] ps = (float[]) obj[1];
                            if (scolor == null) {
                                scolor = color;
                            }
                            if (scolor != null) {
                                graphics.setColorStroke(scolor);
                            }
                            float fsize = chunk.font().size();
                            graphics.setLineWidth(ps[0] + (fsize * ps[1]));
                            float shift = ps[2] + (fsize * ps[3]);
                            int cap2 = (int) ps[4];
                            if (cap2 != 0) {
                                graphics.setLineCap(cap2);
                            }
                            graphics.moveTo(xMarker, yMarker + shift);
                            graphics.lineTo((xMarker + width) - subtract2, yMarker + shift);
                            graphics.stroke();
                            if (scolor != null) {
                                graphics.resetGrayStroke();
                            }
                            if (cap2 != 0) {
                                graphics.setLineCap(0);
                            }
                        }
                        graphics.setLineWidth(1.0f);
                    }
                    if (chunk.isAttribute(Chunk.ACTION)) {
                        float subtract3 = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.ACTION)) {
                            subtract3 = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract3 += hangingCorrection;
                        }
                        text.addAnnotation(new PdfAnnotation(this.writer, xMarker, yMarker, (xMarker + width) - subtract3, yMarker + chunk.font().size(), (PdfAction) chunk.getAttribute(Chunk.ACTION)));
                    }
                    if (chunk.isAttribute(Chunk.REMOTEGOTO)) {
                        float subtract4 = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.REMOTEGOTO)) {
                            subtract4 = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract4 += hangingCorrection;
                        }
                        Object[] obj2 = (Object[]) chunk.getAttribute(Chunk.REMOTEGOTO);
                        String filename = (String) obj2[0];
                        if (obj2[1] instanceof String) {
                            remoteGoto(filename, (String) obj2[1], xMarker, yMarker, (xMarker + width) - subtract4, yMarker + chunk.font().size());
                        } else {
                            remoteGoto(filename, ((Integer) obj2[1]).intValue(), xMarker, yMarker, (xMarker + width) - subtract4, yMarker + chunk.font().size());
                        }
                    }
                    if (chunk.isAttribute(Chunk.LOCALGOTO)) {
                        float subtract5 = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.LOCALGOTO)) {
                            subtract5 = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract5 += hangingCorrection;
                        }
                        localGoto((String) chunk.getAttribute(Chunk.LOCALGOTO), xMarker, yMarker, (xMarker + width) - subtract5, yMarker + chunk.font().size());
                    }
                    if (chunk.isAttribute(Chunk.LOCALDESTINATION)) {
                        float subtract6 = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.LOCALDESTINATION)) {
                            subtract6 = 0.0f;
                        }
                        if (nextChunk == null) {
                            float f = subtract6 + hangingCorrection;
                        }
                        localDestination((String) chunk.getAttribute(Chunk.LOCALDESTINATION), new PdfDestination(0, xMarker, yMarker + chunk.font().size(), ColumnText.GLOBAL_SPACE_CHAR_RATIO));
                    }
                    if (chunk.isAttribute(Chunk.GENERICTAG)) {
                        float subtract7 = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.GENERICTAG)) {
                            subtract7 = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract7 += hangingCorrection;
                        }
                        Rectangle rect = new Rectangle(xMarker, yMarker, (xMarker + width) - subtract7, yMarker + chunk.font().size());
                        PdfPageEvent pev = this.writer.getPageEvent();
                        if (pev != null) {
                            pev.onGenericTag(this.writer, this, rect, (String) chunk.getAttribute(Chunk.GENERICTAG));
                        }
                    }
                    if (chunk.isAttribute(Chunk.PDFANNOTATION)) {
                        float subtract8 = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.PDFANNOTATION)) {
                            subtract8 = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract8 += hangingCorrection;
                        }
                        float fontSize4 = chunk.font().size();
                        float ascender4 = chunk.font().getFont().getFontDescriptor(1, fontSize4);
                        float descender4 = chunk.font().getFont().getFontDescriptor(3, fontSize4);
                        PdfAnnotation annot = PdfFormField.shallowDuplicate((PdfAnnotation) chunk.getAttribute(Chunk.PDFANNOTATION));
                        annot.put(PdfName.RECT, new PdfRectangle(xMarker, yMarker + descender4, (xMarker + width) - subtract8, yMarker + ascender4));
                        text.addAnnotation(annot);
                    }
                    float[] params = (float[]) chunk.getAttribute(Chunk.SKEW);
                    Float hs = (Float) chunk.getAttribute(Chunk.HSCALE);
                    if (params != null || hs != null) {
                        float b = 0.0f;
                        float c2 = 0.0f;
                        if (params != null) {
                            b = params[0];
                            c2 = params[1];
                        }
                        if (hs != null) {
                            hScale = hs.floatValue();
                        }
                        text.setTextMatrix(hScale, b, c2, 1.0f, xMarker, yMarker);
                    }
                    if (chunk.isImage()) {
                        Image image = chunk.getImage();
                        float[] matrix = image.matrix();
                        matrix[4] = (xMarker + chunk.getImageOffsetX()) - matrix[4];
                        matrix[5] = (yMarker + chunk.getImageOffsetY()) - matrix[5];
                        graphics.addImage(image, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
                        text.moveText(((xMarker + lastBaseFactor) + image.getScaledWidth()) - text.getXTLM(), ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                    }
                }
                xMarker += width;
                chunkStrokeIdx++;
            }
            if (chunk.font().compareTo(currentFont) != 0) {
                currentFont = chunk.font();
                text.setFontAndSize(currentFont.getFont(), currentFont.size());
            }
            float rise = 0.0f;
            Object[] textRender = (Object[]) chunk.getAttribute(Chunk.TEXTRENDERMODE);
            int tr = 0;
            float strokeWidth = 1.0f;
            Color strokeColor = null;
            Float fr = (Float) chunk.getAttribute(Chunk.SUBSUPSCRIPT);
            if (textRender != null) {
                tr = ((Integer) textRender[0]).intValue() & 3;
                if (tr != 0) {
                    text.setTextRenderingMode(tr);
                }
                if (tr == 1 || tr == 2) {
                    strokeWidth = ((Float) textRender[1]).floatValue();
                    if (strokeWidth != 1.0f) {
                        text.setLineWidth(strokeWidth);
                    }
                    strokeColor = (Color) textRender[2];
                    if (strokeColor == null) {
                        strokeColor = color;
                    }
                    if (strokeColor != null) {
                        text.setColorStroke(strokeColor);
                    }
                }
            }
            if (fr != null) {
                rise = fr.floatValue();
            }
            if (color != null) {
                text.setColorFill(color);
            }
            if (rise != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                text.setTextRise(rise);
            }
            if (chunk.isImage()) {
                adjustMatrix = true;
            } else if (chunk.isHorizontalSeparator()) {
                PdfTextArray array = new PdfTextArray();
                array.add((((-glueWidth) * 1000.0f) / chunk.font.size()) / hScale);
                text.showText(array);
            } else if (chunk.isTab()) {
                PdfTextArray array2 = new PdfTextArray();
                array2.add((((tabPosition - xMarker) * 1000.0f) / chunk.font.size()) / hScale);
                text.showText(array2);
            } else if (isJustified && numberOfSpaces > 0 && chunk.isSpecialEncoding()) {
                if (hScale != lastHScale) {
                    lastHScale = hScale;
                    text.setWordSpacing(baseWordSpacing / hScale);
                    text.setCharacterSpacing(baseCharacterSpacing / hScale);
                }
                String s2 = chunk.toString();
                int idx = s2.indexOf(32);
                if (idx < 0) {
                    text.showText(s2);
                } else {
                    float spaceCorrection = (((-baseWordSpacing) * 1000.0f) / chunk.font.size()) / hScale;
                    PdfTextArray textArray = new PdfTextArray(s2.substring(0, idx));
                    while (true) {
                        lastIdx = idx;
                        int iIndexOf = s2.indexOf(32, lastIdx + 1);
                        idx = iIndexOf;
                        if (iIndexOf < 0) {
                            break;
                        }
                        textArray.add(spaceCorrection);
                        textArray.add(s2.substring(lastIdx, idx));
                    }
                    textArray.add(spaceCorrection);
                    textArray.add(s2.substring(lastIdx));
                    text.showText(textArray);
                }
            } else {
                if (isJustified && hScale != lastHScale) {
                    lastHScale = hScale;
                    text.setWordSpacing(baseWordSpacing / hScale);
                    text.setCharacterSpacing(baseCharacterSpacing / hScale);
                }
                text.showText(chunk.toString());
            }
            if (rise != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                text.setTextRise(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            }
            if (color != null) {
                text.resetRGBColorFill();
            }
            if (tr != 0) {
                text.setTextRenderingMode(0);
            }
            if (strokeColor != null) {
                text.resetRGBColorStroke();
            }
            if (strokeWidth != 1.0f) {
                text.setLineWidth(1.0f);
            }
            if (chunk.isAttribute(Chunk.SKEW) || chunk.isAttribute(Chunk.HSCALE)) {
                adjustMatrix = true;
                text.setTextMatrix(xMarker, yMarker);
            }
        }
        if (isJustified) {
            text.setWordSpacing(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            text.setCharacterSpacing(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            if (line.isNewlineSplit()) {
                lastBaseFactor = 0.0f;
            }
        }
        if (adjustMatrix) {
            text.moveText(xMarker - text.getXTLM(), ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        }
        currentValues[0] = currentFont;
        currentValues[1] = Float.valueOf(lastBaseFactor);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfDocument$Indentation.class */
    private static class Indentation {
        private float indentLeft;
        private float sectionIndentLeft;
        private float listIndentLeft;
        private float imageIndentLeft;
        private float indentRight;
        private float sectionIndentRight;
        private float imageIndentRight;
        private float indentTop;
        private float indentBottom;

        private Indentation() {
            this.indentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.sectionIndentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.listIndentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.imageIndentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.sectionIndentRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.imageIndentRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentTop = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentBottom = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
    }

    private float indentLeft() {
        return left(this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.imageIndentLeft + this.indentation.sectionIndentLeft);
    }

    private float indentRight() {
        return right(this.indentation.indentRight + this.indentation.sectionIndentRight + this.indentation.imageIndentRight);
    }

    private float indentTop() {
        return top(this.indentation.indentTop);
    }

    float indentBottom() {
        return bottom(this.indentation.indentBottom);
    }

    protected void addSpacing(float extraspace, float oldleading, Font f) {
        if (extraspace == ColumnText.GLOBAL_SPACE_CHAR_RATIO || this.pageEmpty || this.currentHeight + this.line.height() + this.leading > indentTop() - indentBottom()) {
            return;
        }
        this.leading = extraspace;
        carriageReturn();
        if (f.isUnderlined() || f.isStrikethru()) {
            f = new Font(f);
            int style = f.getStyle();
            f.setStyle(style & (-5) & (-9));
        }
        Chunk space = new Chunk(" ", f);
        space.process(this);
        carriageReturn();
        this.leading = oldleading;
    }

    PdfInfo getInfo() {
        return this.info;
    }

    PdfCatalog getCatalog(PdfIndirectReference pages) {
        PdfCatalog catalog = new PdfCatalog(pages, this.writer);
        if (this.rootOutline.getKids().size() > 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
            catalog.put(PdfName.OUTLINES, this.rootOutline.indirectReference());
        }
        this.writer.getPdfVersion().addToCatalog(catalog);
        this.viewerPreferences.addToCatalog(catalog);
        if (this.pageLabels != null) {
            catalog.put(PdfName.PAGELABELS, this.pageLabels.getDictionary(this.writer));
        }
        catalog.addNames(this.localDestinations, getDocumentLevelJS(), this.documentFileAttachment, this.writer);
        if (this.openActionName != null) {
            PdfAction action = getLocalGotoAction(this.openActionName);
            catalog.setOpenAction(action);
        } else if (this.openActionAction != null) {
            catalog.setOpenAction(this.openActionAction);
        }
        if (this.additionalActions != null) {
            catalog.setAdditionalActions(this.additionalActions);
        }
        if (this.collection != null) {
            catalog.put(PdfName.COLLECTION, this.collection);
        }
        if (this.annotationsImp.hasValidAcroForm()) {
            try {
                catalog.put(PdfName.ACROFORM, this.writer.addToBody(this.annotationsImp.getAcroForm()).getIndirectReference());
            } catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }
        return catalog;
    }

    void addOutline(PdfOutline outline, String name) {
        localDestination(name, outline.getPdfDestination());
    }

    public PdfOutline getRootOutline() {
        return this.rootOutline;
    }

    private void calculateOutlineCount() {
        if (this.rootOutline.getKids().size() == 0) {
            return;
        }
        traverseOutlineCount(this.rootOutline);
    }

    private void traverseOutlineCount(PdfOutline outline) {
        ArrayList kids = outline.getKids();
        PdfOutline parent = outline.parent();
        if (kids.isEmpty()) {
            if (parent != null) {
                parent.setCount(parent.getCount() + 1);
                return;
            }
            return;
        }
        for (int k = 0; k < kids.size(); k++) {
            traverseOutlineCount((PdfOutline) kids.get(k));
        }
        if (parent != null) {
            if (outline.isOpen()) {
                parent.setCount(outline.getCount() + parent.getCount() + 1);
            } else {
                parent.setCount(parent.getCount() + 1);
                outline.setCount(-outline.getCount());
            }
        }
    }

    private void writeOutlines() throws IOException {
        if (this.rootOutline.getKids().size() == 0) {
            return;
        }
        outlineTree(this.rootOutline);
        this.writer.addToBody(this.rootOutline, this.rootOutline.indirectReference());
    }

    private void outlineTree(PdfOutline outline) throws IOException {
        outline.setIndirectReference(this.writer.getPdfIndirectReference());
        if (outline.parent() != null) {
            outline.put(PdfName.PARENT, outline.parent().indirectReference());
        }
        ArrayList kids = outline.getKids();
        int size = kids.size();
        for (int k = 0; k < size; k++) {
            outlineTree((PdfOutline) kids.get(k));
        }
        for (int k2 = 0; k2 < size; k2++) {
            if (k2 > 0) {
                ((PdfOutline) kids.get(k2)).put(PdfName.PREV, ((PdfOutline) kids.get(k2 - 1)).indirectReference());
            }
            if (k2 < size - 1) {
                ((PdfOutline) kids.get(k2)).put(PdfName.NEXT, ((PdfOutline) kids.get(k2 + 1)).indirectReference());
            }
        }
        if (size > 0) {
            outline.put(PdfName.FIRST, ((PdfOutline) kids.get(0)).indirectReference());
            outline.put(PdfName.LAST, ((PdfOutline) kids.get(size - 1)).indirectReference());
        }
        for (int k3 = 0; k3 < size; k3++) {
            PdfOutline kid = (PdfOutline) kids.get(k3);
            this.writer.addToBody(kid, kid.indirectReference());
        }
    }

    void setViewerPreferences(int preferences) {
        this.viewerPreferences.setViewerPreferences(preferences);
    }

    void addViewerPreference(PdfName key, PdfObject value) {
        this.viewerPreferences.addViewerPreference(key, value);
    }

    void setPageLabels(PdfPageLabels pageLabels) {
        this.pageLabels = pageLabels;
    }

    private void localGoto(String name, float llx, float lly, float urx, float ury) {
        PdfAction action = getLocalGotoAction(name);
        this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action));
    }

    private void remoteGoto(String filename, String name, float llx, float lly, float urx, float ury) {
        this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, new PdfAction(filename, name)));
    }

    private void remoteGoto(String filename, int page, float llx, float lly, float urx, float ury) {
        addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, new PdfAction(filename, page)));
    }

    void setAction(PdfAction action, float llx, float lly, float urx, float ury) {
        addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action));
    }

    private PdfAction getLocalGotoAction(String name) {
        PdfAction action;
        Object[] obj = (Object[]) this.localDestinations.get(name);
        if (obj == null) {
            obj = new Object[3];
        }
        if (obj[0] == null) {
            if (obj[1] == null) {
                obj[1] = this.writer.getPdfIndirectReference();
            }
            action = new PdfAction((PdfIndirectReference) obj[1]);
            obj[0] = action;
            this.localDestinations.put(name, obj);
        } else {
            action = (PdfAction) obj[0];
        }
        return action;
    }

    private boolean localDestination(String name, PdfDestination destination) {
        Object[] obj = (Object[]) this.localDestinations.get(name);
        if (obj == null) {
            obj = new Object[3];
        }
        if (obj[2] != null) {
            return false;
        }
        obj[2] = destination;
        this.localDestinations.put(name, obj);
        destination.addPage(this.writer.getCurrentPage());
        return true;
    }

    void addJavaScript(PdfAction js) {
        if (js.get(PdfName.JS) == null) {
            throw new RuntimeException("Only JavaScript actions are allowed.");
        }
        try {
            HashMap map = this.documentLevelJS;
            DecimalFormat decimalFormat = SIXTEEN_DIGITS;
            int i = this.jsCounter;
            this.jsCounter = i + 1;
            map.put(decimalFormat.format(i), this.writer.addToBody(js).getIndirectReference());
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    void addJavaScript(String name, PdfAction js) {
        if (js.get(PdfName.JS) == null) {
            throw new RuntimeException("Only JavaScript actions are allowed.");
        }
        try {
            this.documentLevelJS.put(name, this.writer.addToBody(js).getIndirectReference());
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    HashMap getDocumentLevelJS() {
        return this.documentLevelJS;
    }

    void addFileAttachment(String description, PdfFileSpecification fs) throws IOException {
        if (description == null) {
            PdfString desc = (PdfString) fs.get(PdfName.DESC);
            if (desc == null) {
                description = "";
            } else {
                description = PdfEncodings.convertToString(desc.getBytes(), null);
            }
        }
        fs.addDescription(description, true);
        if (description.length() == 0) {
            description = "Unnamed";
        }
        String fn = PdfEncodings.convertToString(new PdfString(description, PdfObject.TEXT_UNICODE).getBytes(), null);
        int k = 0;
        while (this.documentFileAttachment.containsKey(fn)) {
            k++;
            fn = PdfEncodings.convertToString(new PdfString(description + " " + k, PdfObject.TEXT_UNICODE).getBytes(), null);
        }
        this.documentFileAttachment.put(fn, fs.getReference());
    }

    HashMap getDocumentFileAttachment() {
        return this.documentFileAttachment;
    }

    void setOpenAction(String name) {
        this.openActionName = name;
        this.openActionAction = null;
    }

    void setOpenAction(PdfAction action) {
        this.openActionAction = action;
        this.openActionName = null;
    }

    void addAdditionalAction(PdfName actionType, PdfAction action) {
        if (this.additionalActions == null) {
            this.additionalActions = new PdfDictionary();
        }
        if (action == null) {
            this.additionalActions.remove(actionType);
        } else {
            this.additionalActions.put(actionType, action);
        }
        if (this.additionalActions.size() == 0) {
            this.additionalActions = null;
        }
    }

    public void setCollection(PdfCollection collection) {
        this.collection = collection;
    }

    PdfAcroForm getAcroForm() {
        return this.annotationsImp.getAcroForm();
    }

    void setSigFlags(int f) {
        this.annotationsImp.setSigFlags(f);
    }

    void addCalculationOrder(PdfFormField formField) {
        this.annotationsImp.addCalculationOrder(formField);
    }

    void addAnnotation(PdfAnnotation annot) {
        this.pageEmpty = false;
        this.annotationsImp.addAnnotation(annot);
    }

    int getMarkPoint() {
        return this.markPoint;
    }

    void incMarkPoint() {
        this.markPoint++;
    }

    void setCropBoxSize(Rectangle crop) {
        setBoxSize("crop", crop);
    }

    private void setBoxSize(String boxName, Rectangle size) {
        if (size == null) {
            this.boxSize.remove(boxName);
        } else {
            this.boxSize.put(boxName, new PdfRectangle(size));
        }
    }

    private void setNewPageSizeAndMargins() {
        this.pageSize = this.nextPageSize;
        if (this.marginMirroring && (getPageNumber() & 1) == 0) {
            this.marginRight = this.nextMarginLeft;
            this.marginLeft = this.nextMarginRight;
        } else {
            this.marginLeft = this.nextMarginLeft;
            this.marginRight = this.nextMarginRight;
        }
        if (this.marginMirroringTopBottom && (getPageNumber() & 1) == 0) {
            this.marginTop = this.nextMarginBottom;
            this.marginBottom = this.nextMarginTop;
        } else {
            this.marginTop = this.nextMarginTop;
            this.marginBottom = this.nextMarginBottom;
        }
    }

    Rectangle getBoxSize(String boxName) {
        PdfRectangle r = (PdfRectangle) this.thisBoxSize.get(boxName);
        if (r != null) {
            return r.getRectangle();
        }
        return null;
    }

    void setPageEmpty(boolean pageEmpty) {
        this.pageEmpty = pageEmpty;
    }

    void setDuration(int seconds) {
        if (seconds > 0) {
            this.duration = seconds;
        } else {
            this.duration = -1;
        }
    }

    void setTransition(PdfTransition transition) {
        this.transition = transition;
    }

    void setPageAction(PdfName actionType, PdfAction action) {
        if (this.pageAA == null) {
            this.pageAA = new PdfDictionary();
        }
        this.pageAA.put(actionType, action);
    }

    void setThumbnail(Image image) throws DocumentException {
        this.thumb = this.writer.getImageReference(this.writer.addDirectImageSimple(image));
    }

    PageResources getPageResources() {
        return this.pageResources;
    }

    boolean isStrictImageSequence() {
        return this.strictImageSequence;
    }

    void setStrictImageSequence(boolean strictImageSequence) {
        this.strictImageSequence = strictImageSequence;
    }

    public void clearTextWrap() {
        float tmpHeight = this.imageEnd - this.currentHeight;
        if (this.line != null) {
            tmpHeight += this.line.height();
        }
        if (this.imageEnd > -1.0f && tmpHeight > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            carriageReturn();
            this.currentHeight += tmpHeight;
        }
    }

    private void add(Image image) throws DocumentException {
        if (image.hasAbsoluteY()) {
            this.graphics.addImage(image);
            this.pageEmpty = false;
            return;
        }
        if (this.currentHeight != ColumnText.GLOBAL_SPACE_CHAR_RATIO && (indentTop() - this.currentHeight) - image.getScaledHeight() < indentBottom()) {
            if (!this.strictImageSequence && this.imageWait == null) {
                this.imageWait = image;
                return;
            }
            newPage();
            if (this.currentHeight != ColumnText.GLOBAL_SPACE_CHAR_RATIO && (indentTop() - this.currentHeight) - image.getScaledHeight() < indentBottom()) {
                this.imageWait = image;
                return;
            }
        }
        this.pageEmpty = false;
        if (image == this.imageWait) {
            this.imageWait = null;
        }
        boolean textwrap = (image.getAlignment() & 4) == 4 && (image.getAlignment() & 1) != 1;
        boolean underlying = (image.getAlignment() & 8) == 8;
        float diff = this.leading / 2.0f;
        if (textwrap) {
            diff += this.leading;
        }
        float lowerleft = ((indentTop() - this.currentHeight) - image.getScaledHeight()) - diff;
        float[] mt = image.matrix();
        float startPosition = indentLeft() - mt[4];
        if ((image.getAlignment() & 2) == 2) {
            startPosition = (indentRight() - image.getScaledWidth()) - mt[4];
        }
        if ((image.getAlignment() & 1) == 1) {
            startPosition = (indentLeft() + (((indentRight() - indentLeft()) - image.getScaledWidth()) / 2.0f)) - mt[4];
        }
        if (image.hasAbsoluteX()) {
            startPosition = image.getAbsoluteX();
        }
        if (textwrap) {
            if (this.imageEnd < ColumnText.GLOBAL_SPACE_CHAR_RATIO || this.imageEnd < this.currentHeight + image.getScaledHeight() + diff) {
                this.imageEnd = this.currentHeight + image.getScaledHeight() + diff;
            }
            if ((image.getAlignment() & 2) == 2) {
                this.indentation.imageIndentRight += image.getScaledWidth() + image.getIndentationLeft();
            } else {
                this.indentation.imageIndentLeft += image.getScaledWidth() + image.getIndentationRight();
            }
        } else if ((image.getAlignment() & 2) == 2) {
            startPosition -= image.getIndentationRight();
        } else if ((image.getAlignment() & 1) == 1) {
            startPosition += image.getIndentationLeft() - image.getIndentationRight();
        } else {
            startPosition += image.getIndentationLeft();
        }
        this.graphics.addImage(image, mt[0], mt[1], mt[2], mt[3], startPosition, lowerleft - mt[5]);
        if (!textwrap && !underlying) {
            this.currentHeight += image.getScaledHeight() + diff;
            flushLines();
            this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, -(image.getScaledHeight() + diff));
            newLine();
        }
    }

    void addPTable(PdfPTable ptable) throws DocumentException {
        ColumnText ct = new ColumnText(this.writer.getDirectContent());
        if (ptable.getKeepTogether() && !fitsPage(ptable, ColumnText.GLOBAL_SPACE_CHAR_RATIO) && this.currentHeight > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            newPage();
        }
        if (this.currentHeight > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            Paragraph p = new Paragraph();
            p.setLeading(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            ct.addElement(p);
        }
        ct.addElement(ptable);
        boolean he = ptable.isHeadersInEvent();
        ptable.setHeadersInEvent(true);
        int loop = 0;
        while (true) {
            ct.setSimpleColumn(indentLeft(), indentBottom(), indentRight(), indentTop() - this.currentHeight);
            int status = ct.go();
            if ((status & 1) != 0) {
                this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, (ct.getYLine() - indentTop()) + this.currentHeight);
                this.currentHeight = indentTop() - ct.getYLine();
                break;
            }
            if (indentTop() - this.currentHeight == ct.getYLine()) {
                loop++;
            } else {
                loop = 0;
            }
            if (loop == 3) {
                add(new Paragraph("ERROR: Infinite table loop"));
                break;
            }
            newPage();
        }
        ptable.setHeadersInEvent(he);
    }

    private boolean fitsPage(PdfPTable table, float margin) {
        if (!table.isLockedWidth()) {
            float totalWidth = ((indentRight() - indentLeft()) * table.getWidthPercentage()) / 100.0f;
            table.setTotalWidth(totalWidth);
        }
        ensureNewLine();
        return table.getTotalHeight() + ((this.currentHeight > ColumnText.GLOBAL_SPACE_CHAR_RATIO ? 1 : (this.currentHeight == ColumnText.GLOBAL_SPACE_CHAR_RATIO ? 0 : -1)) > 0 ? table.spacingBefore() : ColumnText.GLOBAL_SPACE_CHAR_RATIO) <= ((indentTop() - this.currentHeight) - indentBottom()) - margin;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfDocument$RenderingContext.class */
    private static class RenderingContext {
        private float pagetop;
        private float oldHeight;
        private PdfContentByte cellGraphics;
        private float lostTableBottom;
        private float maxCellBottom;
        private Map rowspanMap;
        private final Map pageMap;
        private PdfTable table;

        private RenderingContext() {
            this.pagetop = -1.0f;
            this.oldHeight = -1.0f;
            this.cellGraphics = null;
            this.pageMap = new LinkedHashMap();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int consumeRowspan(PdfCell c) {
            if (c.rowspan() == 1) {
                return 1;
            }
            Integer i = (Integer) this.rowspanMap.get(c);
            if (i == null) {
                i = Integer.valueOf(c.rowspan());
            }
            Integer i2 = Integer.valueOf(i.intValue() - 1);
            this.rowspanMap.put(c, i2);
            if (i2.intValue() < 1) {
                return 1;
            }
            return i2.intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int currentRowspan(PdfCell c) {
            Integer i = (Integer) this.rowspanMap.get(c);
            if (i == null) {
                return c.rowspan();
            }
            return i.intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int cellRendered(PdfCell cell, int pageNumber) {
            Integer i;
            Integer i2 = (Integer) this.pageMap.get(cell);
            if (i2 == null) {
                i = 1;
            } else {
                i = Integer.valueOf(i2.intValue() + 1);
            }
            this.pageMap.put(cell, i);
            Integer pageInteger = Integer.valueOf(pageNumber);
            Set set = (Set) this.pageMap.get(pageInteger);
            if (set == null) {
                set = new HashSet();
                this.pageMap.put(pageInteger, set);
            }
            set.add(cell);
            return i.intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int numCellRendered(PdfCell cell) {
            Integer i = (Integer) this.pageMap.get(cell);
            if (i == null) {
                i = 0;
            }
            return i.intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isCellRenderedOnPage(PdfCell cell, int pageNumber) {
            Integer pageInteger = Integer.valueOf(pageNumber);
            Set set = (Set) this.pageMap.get(pageInteger);
            if (set != null) {
                return set.contains(cell);
            }
            return false;
        }
    }

    private void addPdfTable(Table t) throws DocumentException {
        flushLines();
        PdfTable table = new PdfTable(t, indentLeft(), indentRight(), indentTop() - this.currentHeight);
        RenderingContext ctx = new RenderingContext();
        ctx.pagetop = indentTop();
        ctx.oldHeight = this.currentHeight;
        ctx.cellGraphics = new PdfContentByte(this.writer);
        ctx.rowspanMap = new LinkedHashMap();
        ctx.table = table;
        ArrayList headercells = table.getHeaderCells();
        ArrayList cells = table.getCells();
        ArrayList rows = extractRows(cells, ctx);
        boolean isContinue = false;
        while (!cells.isEmpty()) {
            ctx.lostTableBottom = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            Iterator iterator = rows.iterator();
            boolean z = false;
            while (true) {
                boolean atLeastOneFits = z;
                if (!iterator.hasNext()) {
                    break;
                }
                ArrayList row = (ArrayList) iterator.next();
                analyzeRow(rows, ctx);
                renderCells(ctx, row, table.hasToFitPageCells() & atLeastOneFits);
                if (!mayBeRemoved(row)) {
                    break;
                }
                consumeRowspan(row, ctx);
                iterator.remove();
                z = true;
            }
            cells.clear();
            Set opt = new HashSet();
            Iterator iterator2 = rows.iterator();
            while (iterator2.hasNext()) {
                for (PdfCell cell : (ArrayList) iterator2.next()) {
                    if (!opt.contains(cell)) {
                        cells.add(cell);
                        opt.add(cell);
                    }
                }
            }
            Rectangle tablerec = new Rectangle(table);
            tablerec.setBorder(table.getBorder());
            tablerec.setBorderWidth(table.getBorderWidth());
            tablerec.setBorderColor(table.getBorderColor());
            tablerec.setBackgroundColor(table.getBackgroundColor());
            PdfContentByte under = this.writer.getDirectContentUnder();
            under.rectangle(tablerec.rectangle(top(), indentBottom()));
            under.add(ctx.cellGraphics);
            tablerec.setBackgroundColor(null);
            Rectangle tablerec2 = tablerec.rectangle(top(), indentBottom());
            tablerec2.setBorder(table.getBorder());
            under.rectangle(tablerec2);
            ctx.cellGraphics = new PdfContentByte(null);
            if (!rows.isEmpty()) {
                isContinue = true;
                this.graphics.setLineWidth(table.getBorderWidth());
                if (0 != 0 && (table.getBorder() & 2) == 2) {
                    Color tColor = table.getBorderColor();
                    if (tColor != null) {
                        this.graphics.setColorStroke(tColor);
                    }
                    this.graphics.moveTo(table.getLeft(), Math.max(table.getBottom(), indentBottom()));
                    this.graphics.lineTo(table.getRight(), Math.max(table.getBottom(), indentBottom()));
                    this.graphics.stroke();
                    if (tColor != null) {
                        this.graphics.resetRGBColorStroke();
                    }
                }
                this.pageEmpty = false;
                float difference = ctx.lostTableBottom;
                newPage();
                float heightCorrection = 0.0f;
                boolean somethingAdded = false;
                if (this.currentHeight > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    heightCorrection = 6.0f;
                    this.currentHeight += 6.0f;
                    somethingAdded = true;
                    newLine();
                    flushLines();
                    this.indentation.indentTop = this.currentHeight - this.leading;
                    this.currentHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
                } else {
                    flushLines();
                }
                int size = headercells.size();
                if (size > 0) {
                    float oldTop = ((PdfCell) headercells.get(0)).getTop(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                    for (int i = 0; i < size; i++) {
                        PdfCell cell2 = (PdfCell) headercells.get(i);
                        cell2.setTop((indentTop() - oldTop) + cell2.getTop(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
                        cell2.setBottom((indentTop() - oldTop) + cell2.getBottom(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
                        ctx.pagetop = cell2.getBottom();
                        ctx.cellGraphics.rectangle(cell2.rectangle(indentTop(), indentBottom()));
                        ArrayList<Image> images = cell2.getImages(indentTop(), indentBottom());
                        for (Image image : images) {
                            this.graphics.addImage(image);
                        }
                        this.lines = cell2.getLines(indentTop(), indentBottom());
                        float cellTop = cell2.getTop(indentTop());
                        this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, cellTop - heightCorrection);
                        float cellDisplacement = (flushLines() - cellTop) + heightCorrection;
                        this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, cellDisplacement);
                    }
                    this.currentHeight = (indentTop() - ctx.pagetop) + table.cellspacing();
                    this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, (ctx.pagetop - indentTop()) - this.currentHeight);
                } else if (somethingAdded) {
                    ctx.pagetop = indentTop();
                    this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, -table.cellspacing());
                }
                ctx.oldHeight = this.currentHeight - heightCorrection;
                int size2 = Math.min(cells.size(), table.columns());
                for (int i2 = 0; i2 < size2; i2++) {
                    PdfCell cell3 = (PdfCell) cells.get(i2);
                    if (cell3.getTop(-table.cellspacing()) > ctx.lostTableBottom) {
                        float newBottom = (ctx.pagetop - difference) + cell3.getBottom();
                        float neededHeight = cell3.remainingHeight();
                        if (newBottom > ctx.pagetop - neededHeight) {
                            difference += newBottom - (ctx.pagetop - neededHeight);
                        }
                    }
                }
                int size3 = cells.size();
                table.setTop(indentTop());
                table.setBottom((ctx.pagetop - difference) + table.getBottom(table.cellspacing()));
                for (int i3 = 0; i3 < size3; i3++) {
                    PdfCell cell4 = (PdfCell) cells.get(i3);
                    float newBottom2 = (ctx.pagetop - difference) + cell4.getBottom();
                    float newTop = (ctx.pagetop - difference) + cell4.getTop(-table.cellspacing());
                    if (newTop > indentTop() - this.currentHeight) {
                        newTop = indentTop() - this.currentHeight;
                    }
                    cell4.setTop(newTop);
                    cell4.setBottom(newBottom2);
                }
            }
        }
        float tableHeight = table.getTop() - table.getBottom();
        if (isContinue) {
            this.currentHeight = tableHeight;
            this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, -(tableHeight - (ctx.oldHeight * 2.0f)));
        } else {
            this.currentHeight = ctx.oldHeight + tableHeight;
            this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, -tableHeight);
        }
        this.pageEmpty = false;
    }

    private void analyzeRow(ArrayList rows, RenderingContext ctx) {
        ctx.maxCellBottom = indentBottom();
        ArrayList row = (ArrayList) rows.get(0);
        int maxRowspan = 1;
        Iterator iterator = row.iterator();
        while (iterator.hasNext()) {
            maxRowspan = Math.max(ctx.currentRowspan((PdfCell) iterator.next()), maxRowspan);
        }
        int rowIndex = 0 + maxRowspan;
        boolean useTop = true;
        if (rowIndex == rows.size()) {
            rowIndex = rows.size() - 1;
            useTop = false;
        }
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return;
        }
        ArrayList<PdfCell> row2 = (ArrayList) rows.get(rowIndex);
        for (PdfCell cell : row2) {
            Rectangle cellRect = cell.rectangle(ctx.pagetop, indentBottom());
            if (useTop) {
                ctx.maxCellBottom = Math.max(ctx.maxCellBottom, cellRect.getTop());
            } else if (ctx.currentRowspan(cell) == 1) {
                ctx.maxCellBottom = Math.max(ctx.maxCellBottom, cellRect.getBottom());
            }
        }
    }

    private boolean mayBeRemoved(ArrayList row) {
        Iterator iterator = row.iterator();
        boolean zMayBeRemoved = true;
        while (true) {
            boolean mayBeRemoved = zMayBeRemoved;
            if (iterator.hasNext()) {
                PdfCell cell = (PdfCell) iterator.next();
                zMayBeRemoved = mayBeRemoved & cell.mayBeRemoved();
            } else {
                return mayBeRemoved;
            }
        }
    }

    private void consumeRowspan(ArrayList row, RenderingContext ctx) {
        Iterator iterator = row.iterator();
        while (iterator.hasNext()) {
            PdfCell c = (PdfCell) iterator.next();
            ctx.consumeRowspan(c);
        }
    }

    private ArrayList extractRows(ArrayList cells, RenderingContext ctx) {
        PdfCell previousCell = null;
        ArrayList rows = new ArrayList();
        ArrayList arrayList = new ArrayList();
        Iterator iterator = cells.iterator();
        while (iterator.hasNext()) {
            PdfCell cell = (PdfCell) iterator.next();
            boolean isAdded = false;
            boolean isEndOfRow = !iterator.hasNext();
            boolean isCurrentCellPartOfRow = !iterator.hasNext();
            if (previousCell != null && cell.getLeft() <= previousCell.getLeft()) {
                isEndOfRow = true;
                isCurrentCellPartOfRow = false;
            }
            if (isCurrentCellPartOfRow) {
                arrayList.add(cell);
                isAdded = true;
            }
            if (isEndOfRow) {
                if (!arrayList.isEmpty()) {
                    rows.add(arrayList);
                }
                arrayList = new ArrayList();
            }
            if (!isAdded) {
                arrayList.add(cell);
            }
            previousCell = cell;
        }
        if (!arrayList.isEmpty()) {
            rows.add(arrayList);
        }
        for (int i = rows.size() - 1; i >= 0; i--) {
            ArrayList row = (ArrayList) rows.get(i);
            for (int j = 0; j < row.size(); j++) {
                PdfCell c = (PdfCell) row.get(j);
                int rowspan = c.rowspan();
                for (int k = 1; k < rowspan && rows.size() < i + k; k++) {
                    ArrayList spannedRow = (ArrayList) rows.get(i + k);
                    if (spannedRow.size() > j) {
                        spannedRow.add(j, c);
                    }
                }
            }
        }
        return rows;
    }

    private void renderCells(RenderingContext ctx, java.util.List cells, boolean hasToFit) throws DocumentException {
        if (hasToFit) {
            Iterator iterator = cells.iterator();
            while (iterator.hasNext()) {
                PdfCell cell = (PdfCell) iterator.next();
                if (!cell.isHeader() && cell.getBottom() < indentBottom()) {
                    return;
                }
            }
        }
        Iterator iterator2 = cells.iterator();
        while (iterator2.hasNext()) {
            PdfCell cell2 = (PdfCell) iterator2.next();
            if (!ctx.isCellRenderedOnPage(cell2, getPageNumber())) {
                float correction = 0.0f;
                if (ctx.numCellRendered(cell2) >= 1) {
                    correction = 1.0f;
                }
                this.lines = cell2.getLines(ctx.pagetop, indentBottom() - correction);
                if (this.lines != null && !this.lines.isEmpty()) {
                    float cellTop = cell2.getTop(ctx.pagetop - ctx.oldHeight);
                    this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, cellTop);
                    float cellDisplacement = flushLines() - cellTop;
                    this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, cellDisplacement);
                    if (ctx.oldHeight + cellDisplacement > this.currentHeight) {
                        this.currentHeight = ctx.oldHeight + cellDisplacement;
                    }
                    ctx.cellRendered(cell2, getPageNumber());
                }
                float indentBottom = Math.max(cell2.getBottom(), indentBottom());
                Rectangle tableRect = ctx.table.rectangle(ctx.pagetop, indentBottom());
                float indentBottom2 = Math.max(tableRect.getBottom(), indentBottom);
                Rectangle cellRect = cell2.rectangle(tableRect.getTop(), indentBottom2);
                if (cellRect.getHeight() > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    ctx.lostTableBottom = indentBottom2;
                    ctx.cellGraphics.rectangle(cellRect);
                }
                ArrayList<Image> images = cell2.getImages(ctx.pagetop, indentBottom());
                for (Image image : images) {
                    this.graphics.addImage(image);
                }
            }
        }
    }

    float bottom(Table table) {
        PdfTable tmp = new PdfTable(table, indentLeft(), indentRight(), indentTop() - this.currentHeight);
        return tmp.getBottom();
    }

    private void doFooter() throws DocumentException {
        if (this.footer != null) {
            float tmpIndentLeft = this.indentation.indentLeft;
            float tmpIndentRight = this.indentation.indentRight;
            float tmpListIndentLeft = this.indentation.listIndentLeft;
            float tmpImageIndentLeft = this.indentation.imageIndentLeft;
            float tmpImageIndentRight = this.indentation.imageIndentRight;
            this.indentation.indentLeft = this.indentation.indentRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentation.listIndentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentation.imageIndentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentation.imageIndentRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.footer.setPageNumber(this.pageN);
            this.leading = this.footer.paragraph().getTotalLeading();
            add(this.footer.paragraph());
            this.indentation.indentBottom = this.currentHeight;
            this.text.moveText(left(), indentBottom());
            flushLines();
            this.text.moveText(-left(), -bottom());
            this.footer.setTop(bottom(this.currentHeight));
            this.footer.setBottom(bottom() - (0.75f * this.leading));
            this.footer.setLeft(left());
            this.footer.setRight(right());
            this.graphics.rectangle(this.footer);
            this.indentation.indentBottom = this.currentHeight + (this.leading * 2.0f);
            this.currentHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentation.indentLeft = tmpIndentLeft;
            this.indentation.indentRight = tmpIndentRight;
            this.indentation.listIndentLeft = tmpListIndentLeft;
            this.indentation.imageIndentLeft = tmpImageIndentLeft;
            this.indentation.imageIndentRight = tmpImageIndentRight;
        }
    }

    private void doHeader() throws DocumentException {
        if (this.header != null) {
            float tmpIndentLeft = this.indentation.indentLeft;
            float tmpIndentRight = this.indentation.indentRight;
            float tmpListIndentLeft = this.indentation.listIndentLeft;
            float tmpImageIndentLeft = this.indentation.imageIndentLeft;
            float tmpImageIndentRight = this.indentation.imageIndentRight;
            this.indentation.indentLeft = this.indentation.indentRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentation.listIndentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentation.imageIndentLeft = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentation.imageIndentRight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.header.setPageNumber(this.pageN);
            this.leading = this.header.paragraph().getTotalLeading();
            this.text.moveText(ColumnText.GLOBAL_SPACE_CHAR_RATIO, this.leading);
            add(this.header.paragraph());
            newLine();
            this.indentation.indentTop = this.currentHeight - this.leading;
            this.header.setTop(top() + this.leading);
            this.header.setBottom(indentTop() + ((this.leading * 2.0f) / 3.0f));
            this.header.setLeft(left());
            this.header.setRight(right());
            this.graphics.rectangle(this.header);
            flushLines();
            this.currentHeight = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.indentation.indentLeft = tmpIndentLeft;
            this.indentation.indentRight = tmpIndentRight;
            this.indentation.listIndentLeft = tmpListIndentLeft;
            this.indentation.imageIndentLeft = tmpImageIndentLeft;
            this.indentation.imageIndentRight = tmpImageIndentRight;
        }
    }
}
