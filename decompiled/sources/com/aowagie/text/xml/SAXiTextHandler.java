package com.aowagie.text.xml;

import com.aowagie.text.Anchor;
import com.aowagie.text.Annotation;
import com.aowagie.text.BadElementException;
import com.aowagie.text.Cell;
import com.aowagie.text.Chapter;
import com.aowagie.text.Chunk;
import com.aowagie.text.DocListener;
import com.aowagie.text.DocumentException;
import com.aowagie.text.Element;
import com.aowagie.text.ElementTags;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Font;
import com.aowagie.text.Image;
import com.aowagie.text.List;
import com.aowagie.text.ListItem;
import com.aowagie.text.Meta;
import com.aowagie.text.PageSize;
import com.aowagie.text.Paragraph;
import com.aowagie.text.Rectangle;
import com.aowagie.text.Section;
import com.aowagie.text.Table;
import com.aowagie.text.TextElementArray;
import com.aowagie.text.factories.ElementFactory;
import com.aowagie.text.html.HtmlTagMap;
import com.aowagie.text.pdf.BaseFont;
import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.draw.LineSeparator;
import com.aowagie.text.xml.simpleparser.EntitiesToSymbol;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Stack;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/SAXiTextHandler.class */
class SAXiTextHandler extends DefaultHandler {
    private final DocListener document;
    private final Stack stack;
    private Chunk currentChunk;
    private boolean ignore;
    private boolean controlOpenClose;
    private float topMargin;
    private float rightMargin;
    private float leftMargin;
    private float bottomMargin;
    protected HashMap myTags;
    private BaseFont bf;

    SAXiTextHandler(DocListener document) {
        this.currentChunk = null;
        this.ignore = false;
        this.controlOpenClose = true;
        this.topMargin = 36.0f;
        this.rightMargin = 36.0f;
        this.leftMargin = 36.0f;
        this.bottomMargin = 36.0f;
        this.bf = null;
        this.document = document;
        this.stack = new Stack();
    }

    private SAXiTextHandler(DocListener document, HtmlTagMap myTags) {
        this(document);
        this.myTags = myTags;
    }

    SAXiTextHandler(DocListener document, HashMap myTags) {
        this(document);
        this.myTags = myTags;
    }

    public void setControlOpenClose(boolean controlOpenClose) {
        this.controlOpenClose = controlOpenClose;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String uri, String lname, String name, Attributes attrs) {
        Properties attributes = new Properties();
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                String attribute = attrs.getQName(i);
                attributes.setProperty(attribute, attrs.getValue(i));
            }
        }
        handleStartingTags(name, attributes);
    }

    void handleStartingTags(String name, Properties attributes) {
        TextElementArray current;
        if (this.ignore || ElementTags.IGNORE.equals(name)) {
            this.ignore = true;
            return;
        }
        if (this.currentChunk != null) {
            try {
                current = (TextElementArray) this.stack.pop();
            } catch (EmptyStackException e) {
                if (this.bf == null) {
                    current = new Paragraph("", new Font());
                } else {
                    current = new Paragraph("", new Font(this.bf));
                }
            }
            current.add(this.currentChunk);
            this.stack.push(current);
            this.currentChunk = null;
        }
        if (ElementTags.CHUNK.equals(name)) {
            this.currentChunk = ElementFactory.getChunk(attributes);
            if (this.bf != null) {
                this.currentChunk.setFont(new Font(this.bf));
                return;
            }
            return;
        }
        if (ElementTags.ENTITY.equals(name)) {
            Font f = new Font();
            if (this.currentChunk != null) {
                handleEndingTags(ElementTags.CHUNK);
                f = this.currentChunk.getFont();
            }
            this.currentChunk = EntitiesToSymbol.get(attributes.getProperty(ElementTags.ID), f);
            return;
        }
        if (ElementTags.PHRASE.equals(name)) {
            this.stack.push(ElementFactory.getPhrase(attributes));
            return;
        }
        if (ElementTags.ANCHOR.equals(name)) {
            this.stack.push(ElementFactory.getAnchor(attributes));
            return;
        }
        if (ElementTags.PARAGRAPH.equals(name) || "title".equals(name)) {
            this.stack.push(ElementFactory.getParagraph(attributes));
            return;
        }
        if (ElementTags.LIST.equals(name)) {
            this.stack.push(ElementFactory.getList(attributes));
            return;
        }
        if (ElementTags.LISTITEM.equals(name)) {
            this.stack.push(ElementFactory.getListItem(attributes));
            return;
        }
        if (ElementTags.CELL.equals(name)) {
            this.stack.push(ElementFactory.getCell(attributes));
            return;
        }
        if (ElementTags.TABLE.equals(name)) {
            Table table = ElementFactory.getTable(attributes);
            float[] widths = table.getProportionalWidths();
            for (int i = 0; i < widths.length; i++) {
                if (widths[i] == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    widths[i] = 100.0f / widths.length;
                }
            }
            try {
                table.setWidths(widths);
                this.stack.push(table);
                return;
            } catch (BadElementException bee) {
                throw new ExceptionConverter(bee);
            }
        }
        if (ElementTags.SECTION.equals(name)) {
            Element previous = (Element) this.stack.pop();
            try {
                Section section = ElementFactory.getSection((Section) previous, attributes);
                this.stack.push(previous);
                this.stack.push(section);
                return;
            } catch (ClassCastException cce) {
                throw new ExceptionConverter(cce);
            }
        }
        if (ElementTags.CHAPTER.equals(name)) {
            this.stack.push(ElementFactory.getChapter(attributes));
            return;
        }
        if ("image".equals(name)) {
            try {
                Image img = ElementFactory.getImage(attributes);
                try {
                    addImage(img);
                    return;
                } catch (EmptyStackException e2) {
                    try {
                        this.document.add(img);
                        return;
                    } catch (DocumentException de) {
                        throw new ExceptionConverter(de);
                    }
                }
            } catch (Exception e3) {
                throw new ExceptionConverter(e3);
            }
        }
        if (ElementTags.ANNOTATION.equals(name)) {
            Annotation annotation = ElementFactory.getAnnotation(attributes);
            try {
                try {
                    TextElementArray current2 = (TextElementArray) this.stack.pop();
                    try {
                        current2.add(annotation);
                    } catch (Exception e4) {
                        this.document.add(annotation);
                    }
                    this.stack.push(current2);
                    return;
                } catch (EmptyStackException e5) {
                    this.document.add(annotation);
                    return;
                }
            } catch (DocumentException de2) {
                throw new ExceptionConverter(de2);
            }
        }
        if (isNewline(name)) {
            try {
                TextElementArray current3 = (TextElementArray) this.stack.pop();
                current3.add(Chunk.NEWLINE);
                this.stack.push(current3);
                return;
            } catch (EmptyStackException e6) {
                if (this.currentChunk == null) {
                    try {
                        this.document.add(Chunk.NEWLINE);
                        return;
                    } catch (DocumentException de3) {
                        throw new ExceptionConverter(de3);
                    }
                }
                this.currentChunk.append("\n");
                return;
            }
        }
        if (isNewpage(name)) {
            try {
                TextElementArray current4 = (TextElementArray) this.stack.pop();
                Chunk newPage = new Chunk("");
                newPage.setNewPage();
                if (this.bf != null) {
                    newPage.setFont(new Font(this.bf));
                }
                current4.add(newPage);
                this.stack.push(current4);
                return;
            } catch (EmptyStackException e7) {
                this.document.newPage();
                return;
            }
        }
        if (ElementTags.HORIZONTALRULE.equals(name)) {
            LineSeparator hr = new LineSeparator(1.0f, 100.0f, null, 1, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            try {
                TextElementArray current5 = (TextElementArray) this.stack.pop();
                current5.add(hr);
                this.stack.push(current5);
                return;
            } catch (EmptyStackException e8) {
                try {
                    this.document.add(hr);
                    return;
                } catch (DocumentException de4) {
                    throw new ExceptionConverter(de4);
                }
            }
        }
        if (isDocumentRoot(name)) {
            Rectangle pageSize = null;
            String orientation = null;
            for (Object element : attributes.keySet()) {
                String key = (String) element;
                String value = attributes.getProperty(key);
                try {
                    if (ElementTags.LEFT.equalsIgnoreCase(key)) {
                        this.leftMargin = Float.parseFloat(value + OperatorName.FILL_NON_ZERO);
                    }
                    if (ElementTags.RIGHT.equalsIgnoreCase(key)) {
                        this.rightMargin = Float.parseFloat(value + OperatorName.FILL_NON_ZERO);
                    }
                    if (ElementTags.TOP.equalsIgnoreCase(key)) {
                        this.topMargin = Float.parseFloat(value + OperatorName.FILL_NON_ZERO);
                    }
                    if (ElementTags.BOTTOM.equalsIgnoreCase(key)) {
                        this.bottomMargin = Float.parseFloat(value + OperatorName.FILL_NON_ZERO);
                    }
                    if (ElementTags.PAGE_SIZE.equals(key)) {
                        try {
                            Field pageSizeField = PageSize.class.getField(value);
                            pageSize = (Rectangle) pageSizeField.get(null);
                        } catch (Exception ex) {
                            throw new ExceptionConverter(ex);
                        }
                    } else if (ElementTags.ORIENTATION.equals(key)) {
                        try {
                            if ("landscape".equals(value)) {
                                orientation = "landscape";
                            }
                        } catch (Exception ex2) {
                            throw new ExceptionConverter(ex2);
                        }
                    } else {
                        try {
                            this.document.add(new Meta(key, value));
                        } catch (DocumentException de5) {
                            throw new ExceptionConverter(de5);
                        }
                    }
                } catch (Exception ex3) {
                    throw new ExceptionConverter(ex3);
                }
            }
            if (pageSize != null) {
                if ("landscape".equals(orientation)) {
                    pageSize = pageSize.rotate();
                }
                this.document.setPageSize(pageSize);
            }
            this.document.setMargins(this.leftMargin, this.rightMargin, this.topMargin, this.bottomMargin);
            if (this.controlOpenClose) {
                this.document.open();
            }
        }
    }

    private void addImage(Image img) throws EmptyStackException {
        Object current = this.stack.pop();
        if ((current instanceof Chapter) || (current instanceof Section) || (current instanceof Cell)) {
            ((TextElementArray) current).add(img);
            this.stack.push(current);
            return;
        }
        Stack newStack = new Stack();
        while (!(current instanceof Chapter) && !(current instanceof Section) && !(current instanceof Cell)) {
            newStack.push(current);
            if (current instanceof Anchor) {
                img.setAnnotation(new Annotation(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ((Anchor) current).getReference()));
            }
            current = this.stack.pop();
        }
        ((TextElementArray) current).add(img);
        this.stack.push(current);
        while (!newStack.empty()) {
            this.stack.push(newStack.pop());
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ch, int start, int length) {
        characters(ch, start, length);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] ch, int start, int length) {
        if (this.ignore) {
            return;
        }
        String content = new String(ch, start, length);
        if (content.trim().length() == 0 && content.indexOf(32) < 0) {
            return;
        }
        StringBuffer buf = new StringBuffer();
        int len = content.length();
        boolean newline = false;
        for (int i = 0; i < len; i++) {
            char character = content.charAt(i);
            switch (character) {
                case '\t':
                case '\r':
                    break;
                case '\n':
                    if (i > 0) {
                        newline = true;
                        buf.append(' ');
                    }
                    break;
                case ' ':
                    if (!newline) {
                        buf.append(character);
                    }
                    break;
                default:
                    newline = false;
                    buf.append(character);
                    break;
            }
        }
        if (this.currentChunk == null) {
            if (this.bf == null) {
                this.currentChunk = new Chunk(buf.toString());
                return;
            } else {
                this.currentChunk = new Chunk(buf.toString(), new Font(this.bf));
                return;
            }
        }
        this.currentChunk.append(buf.toString());
    }

    public void setBaseFont(BaseFont bf) {
        this.bf = bf;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String uri, String lname, String name) {
        handleEndingTags(name);
    }

    void handleEndingTags(String name) {
        TextElementArray current;
        Element element;
        if (ElementTags.IGNORE.equals(name)) {
            this.ignore = false;
            return;
        }
        if (this.ignore || isNewpage(name) || ElementTags.ANNOTATION.equals(name) || "image".equals(name) || isNewline(name)) {
            return;
        }
        try {
            if ("title".equals(name)) {
                Paragraph current2 = (Paragraph) this.stack.pop();
                if (this.currentChunk != null) {
                    current2.add(this.currentChunk);
                    this.currentChunk = null;
                }
                Section previous = (Section) this.stack.pop();
                previous.setTitle(current2);
                this.stack.push(previous);
                return;
            }
            if (this.currentChunk != null) {
                try {
                    current = (TextElementArray) this.stack.pop();
                } catch (EmptyStackException e) {
                    current = new Paragraph();
                }
                current.add(this.currentChunk);
                this.stack.push(current);
                this.currentChunk = null;
            }
            if (ElementTags.CHUNK.equals(name)) {
                return;
            }
            if (ElementTags.PHRASE.equals(name) || ElementTags.ANCHOR.equals(name) || ElementTags.LIST.equals(name) || ElementTags.PARAGRAPH.equals(name)) {
                Element current3 = (Element) this.stack.pop();
                try {
                    TextElementArray previous2 = (TextElementArray) this.stack.pop();
                    previous2.add(current3);
                    this.stack.push(previous2);
                    return;
                } catch (EmptyStackException e2) {
                    this.document.add(current3);
                    return;
                }
            }
            if (ElementTags.LISTITEM.equals(name)) {
                ListItem listItem = (ListItem) this.stack.pop();
                List list = (List) this.stack.pop();
                list.add(listItem);
                this.stack.push(list);
            }
            if (ElementTags.TABLE.equals(name)) {
                Table table = (Table) this.stack.pop();
                try {
                    TextElementArray previous3 = (TextElementArray) this.stack.pop();
                    previous3.add(table);
                    this.stack.push(previous3);
                    return;
                } catch (EmptyStackException e3) {
                    this.document.add(table);
                    return;
                }
            }
            if (ElementTags.ROW.equals(name)) {
                ArrayList<Cell> cells = new ArrayList();
                int columns = 0;
                while (true) {
                    element = (Element) this.stack.pop();
                    if (element.type() != 20) {
                        break;
                    }
                    Cell cell = (Cell) element;
                    columns += cell.getColspan();
                    cells.add(cell);
                }
                Table table2 = (Table) element;
                if (table2.getColumns() < columns) {
                    table2.addColumns(columns - table2.getColumns());
                }
                Collections.reverse(cells);
                float[] cellWidths = new float[columns];
                boolean[] cellNulls = new boolean[columns];
                for (int i = 0; i < columns; i++) {
                    cellWidths[i] = 0.0f;
                    cellNulls[i] = true;
                }
                float total = 0.0f;
                int j = 0;
                for (Cell cell2 : cells) {
                    String width = cell2.getWidthAsString();
                    if (cell2.getWidth() == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                        if (cell2.getColspan() == 1 && cellWidths[j] == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                            try {
                                cellWidths[j] = 100.0f / columns;
                                total += cellWidths[j];
                            } catch (Exception e4) {
                            }
                        } else if (cell2.getColspan() == 1) {
                            cellNulls[j] = false;
                        }
                    } else if (cell2.getColspan() == 1 && width.endsWith("%")) {
                        try {
                            cellWidths[j] = Float.parseFloat(width.substring(0, width.length() - 1) + OperatorName.FILL_NON_ZERO);
                            total += cellWidths[j];
                        } catch (Exception e5) {
                        }
                    }
                    j += cell2.getColspan();
                    table2.addCell(cell2);
                }
                float[] widths = table2.getProportionalWidths();
                if (widths.length == columns) {
                    float left = 0.0f;
                    for (int i2 = 0; i2 < columns; i2++) {
                        if (cellNulls[i2] && widths[i2] != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                            left += widths[i2];
                            cellWidths[i2] = widths[i2];
                        }
                    }
                    if (100.0d >= total) {
                        for (int i3 = 0; i3 < widths.length; i3++) {
                            if (cellWidths[i3] == ColumnText.GLOBAL_SPACE_CHAR_RATIO && widths[i3] != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                                cellWidths[i3] = (widths[i3] / left) * (100.0f - total);
                            }
                        }
                    }
                    table2.setWidths(cellWidths);
                }
                this.stack.push(table2);
            }
            if (ElementTags.CELL.equals(name)) {
                return;
            }
            if (ElementTags.SECTION.equals(name)) {
                this.stack.pop();
                return;
            }
            if (ElementTags.CHAPTER.equals(name)) {
                this.document.add((Element) this.stack.pop());
                return;
            }
            if (!isDocumentRoot(name)) {
                return;
            }
            while (true) {
                try {
                    Element element2 = (Element) this.stack.pop();
                    try {
                        TextElementArray previous4 = (TextElementArray) this.stack.pop();
                        previous4.add(element2);
                        this.stack.push(previous4);
                    } catch (EmptyStackException e6) {
                        this.document.add(element2);
                    }
                } catch (EmptyStackException e7) {
                    if (this.controlOpenClose) {
                        this.document.close();
                        return;
                    }
                    return;
                }
            }
            throw new ExceptionConverter(de);
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    private boolean isNewpage(String tag) {
        return ElementTags.NEWPAGE.equals(tag);
    }

    private boolean isNewline(String tag) {
        return ElementTags.NEWLINE.equals(tag);
    }

    private boolean isDocumentRoot(String tag) {
        return ElementTags.ITEXT.equals(tag);
    }
}
