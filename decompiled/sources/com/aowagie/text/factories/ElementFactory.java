package com.aowagie.text.factories;

import com.aowagie.text.Anchor;
import com.aowagie.text.Annotation;
import com.aowagie.text.BadElementException;
import com.aowagie.text.Cell;
import com.aowagie.text.ChapterAutoNumber;
import com.aowagie.text.Chunk;
import com.aowagie.text.ElementTags;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.FontFactory;
import com.aowagie.text.Image;
import com.aowagie.text.List;
import com.aowagie.text.ListItem;
import com.aowagie.text.Paragraph;
import com.aowagie.text.Phrase;
import com.aowagie.text.Rectangle;
import com.aowagie.text.Section;
import com.aowagie.text.Table;
import com.aowagie.text.Utilities;
import com.aowagie.text.html.Markup;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/factories/ElementFactory.class */
public class ElementFactory {
    public static Chunk getChunk(Properties attributes) {
        Chunk chunk = new Chunk();
        chunk.setFont(FontFactory.getFont(attributes));
        String value = attributes.getProperty(ElementTags.ITEXT);
        if (value != null) {
            chunk.append(value);
        }
        String value2 = attributes.getProperty(ElementTags.LOCALGOTO);
        if (value2 != null) {
            chunk.setLocalGoto(value2);
        }
        String value3 = attributes.getProperty(ElementTags.REMOTEGOTO);
        if (value3 != null) {
            String page = attributes.getProperty("page");
            if (page != null) {
                chunk.setRemoteGoto(value3, Integer.parseInt(page));
            } else {
                String destination = attributes.getProperty("destination");
                if (destination != null) {
                    chunk.setRemoteGoto(value3, destination);
                }
            }
        }
        String value4 = attributes.getProperty(ElementTags.LOCALDESTINATION);
        if (value4 != null) {
            chunk.setLocalDestination(value4);
        }
        String value5 = attributes.getProperty(ElementTags.SUBSUPSCRIPT);
        if (value5 != null) {
            chunk.setTextRise(Float.parseFloat(value5 + OperatorName.FILL_NON_ZERO));
        }
        String value6 = attributes.getProperty(Markup.CSS_KEY_VERTICALALIGN);
        if (value6 != null && value6.endsWith("%")) {
            float p = Float.parseFloat(value6.substring(0, value6.length() - 1) + OperatorName.FILL_NON_ZERO) / 100.0f;
            chunk.setTextRise(p * chunk.getFont().getSize());
        }
        String value7 = attributes.getProperty(ElementTags.GENERICTAG);
        if (value7 != null) {
            chunk.setGenericTag(value7);
        }
        String value8 = attributes.getProperty(ElementTags.BACKGROUNDCOLOR);
        if (value8 != null) {
            chunk.setBackground(Markup.decodeColor(value8));
        }
        return chunk;
    }

    public static Phrase getPhrase(Properties attributes) {
        Phrase phrase = new Phrase();
        phrase.setFont(FontFactory.getFont(attributes));
        String value = attributes.getProperty(ElementTags.LEADING);
        if (value != null) {
            phrase.setLeading(Float.parseFloat(value + OperatorName.FILL_NON_ZERO));
        }
        String value2 = attributes.getProperty(Markup.CSS_KEY_LINEHEIGHT);
        if (value2 != null) {
            phrase.setLeading(Markup.parseLength(value2, 12.0f));
        }
        String value3 = attributes.getProperty(ElementTags.ITEXT);
        if (value3 != null) {
            Chunk chunk = new Chunk(value3);
            String value4 = attributes.getProperty(ElementTags.GENERICTAG);
            if (value4 != null) {
                chunk.setGenericTag(value4);
            }
            phrase.add(chunk);
        }
        return phrase;
    }

    public static Anchor getAnchor(Properties attributes) {
        Anchor anchor = new Anchor(getPhrase(attributes));
        String value = attributes.getProperty("name");
        if (value != null) {
            anchor.setName(value);
        }
        String value2 = (String) attributes.remove(ElementTags.REFERENCE);
        if (value2 != null) {
            anchor.setReference(value2);
        }
        return anchor;
    }

    public static Paragraph getParagraph(Properties attributes) {
        Paragraph paragraph = new Paragraph(getPhrase(attributes));
        String value = attributes.getProperty(ElementTags.ALIGN);
        if (value != null) {
            paragraph.setAlignment(value);
        }
        String value2 = attributes.getProperty(ElementTags.INDENTATIONLEFT);
        if (value2 != null) {
            paragraph.setIndentationLeft(Float.parseFloat(value2 + OperatorName.FILL_NON_ZERO));
        }
        String value3 = attributes.getProperty(ElementTags.INDENTATIONRIGHT);
        if (value3 != null) {
            paragraph.setIndentationRight(Float.parseFloat(value3 + OperatorName.FILL_NON_ZERO));
        }
        return paragraph;
    }

    public static ListItem getListItem(Properties attributes) {
        ListItem item = new ListItem(getParagraph(attributes));
        return item;
    }

    public static List getList(Properties attributes) {
        List list = new List();
        list.setNumbered(Utilities.checkTrueOrFalse(attributes, ElementTags.NUMBERED));
        list.setLettered(Utilities.checkTrueOrFalse(attributes, ElementTags.LETTERED));
        list.setLowercase(Utilities.checkTrueOrFalse(attributes, ElementTags.LOWERCASE));
        list.setAutoindent(Utilities.checkTrueOrFalse(attributes, ElementTags.AUTO_INDENT_ITEMS));
        list.setAlignindent(Utilities.checkTrueOrFalse(attributes, ElementTags.ALIGN_INDENTATION_ITEMS));
        String value = attributes.getProperty(ElementTags.FIRST);
        if (value != null) {
            char character = value.charAt(0);
            if (Character.isLetter(character)) {
                list.setFirst(character);
            } else {
                list.setFirst(Integer.parseInt(value));
            }
        }
        String value2 = attributes.getProperty(ElementTags.LISTSYMBOL);
        if (value2 != null) {
            list.setListSymbol(new Chunk(value2, FontFactory.getFont(attributes)));
        }
        String value3 = attributes.getProperty(ElementTags.INDENTATIONLEFT);
        if (value3 != null) {
            list.setIndentationLeft(Float.parseFloat(value3 + OperatorName.FILL_NON_ZERO));
        }
        String value4 = attributes.getProperty(ElementTags.INDENTATIONRIGHT);
        if (value4 != null) {
            list.setIndentationRight(Float.parseFloat(value4 + OperatorName.FILL_NON_ZERO));
        }
        String value5 = attributes.getProperty(ElementTags.SYMBOLINDENT);
        if (value5 != null) {
            list.setSymbolIndent(Float.parseFloat(value5));
        }
        return list;
    }

    public static Cell getCell(Properties attributes) {
        Cell cell = new Cell();
        cell.setHorizontalAlignment(attributes.getProperty(ElementTags.HORIZONTALALIGN));
        cell.setVerticalAlignment(attributes.getProperty(ElementTags.VERTICALALIGN));
        String value = attributes.getProperty(ElementTags.WIDTH);
        if (value != null) {
            cell.setWidth(value);
        }
        String value2 = attributes.getProperty(ElementTags.COLSPAN);
        if (value2 != null) {
            cell.setColspan(Integer.parseInt(value2));
        }
        String value3 = attributes.getProperty(ElementTags.ROWSPAN);
        if (value3 != null) {
            cell.setRowspan(Integer.parseInt(value3));
        }
        String value4 = attributes.getProperty(ElementTags.LEADING);
        if (value4 != null) {
            cell.setLeading(Float.parseFloat(value4 + OperatorName.FILL_NON_ZERO));
        }
        cell.setHeader(Utilities.checkTrueOrFalse(attributes, ElementTags.HEADER));
        if (Utilities.checkTrueOrFalse(attributes, ElementTags.NOWRAP)) {
            cell.setMaxLines(1);
        }
        setRectangleProperties(cell, attributes);
        return cell;
    }

    public static Table getTable(Properties attributes) {
        Table table;
        try {
            String value = attributes.getProperty(ElementTags.WIDTHS);
            if (value != null) {
                StringTokenizer widthTokens = new StringTokenizer(value, PolicyIdFilter.OID_SEPARATOR);
                ArrayList values = new ArrayList();
                while (widthTokens.hasMoreTokens()) {
                    values.add(widthTokens.nextToken());
                }
                table = new Table(values.size());
                float[] widths = new float[table.getColumns()];
                for (int i = 0; i < values.size(); i++) {
                    widths[i] = Float.parseFloat(((String) values.get(i)) + OperatorName.FILL_NON_ZERO);
                }
                table.setWidths(widths);
            } else {
                try {
                    table = new Table(Integer.parseInt(attributes.getProperty(ElementTags.COLUMNS)));
                } catch (Exception e) {
                    table = new Table(1);
                }
            }
            table.setBorder(15);
            table.setBorderWidth(1.0f);
            table.getDefaultCell().setBorder(15);
            String value2 = attributes.getProperty(ElementTags.LASTHEADERROW);
            if (value2 != null) {
                table.setLastHeaderRow(Integer.parseInt(value2));
            }
            String value3 = attributes.getProperty(ElementTags.ALIGN);
            if (value3 != null) {
                table.setAlignment(value3);
            }
            String value4 = attributes.getProperty(ElementTags.CELLSPACING);
            if (value4 != null) {
                table.setSpacing(Float.parseFloat(value4 + OperatorName.FILL_NON_ZERO));
            }
            String value5 = attributes.getProperty(ElementTags.CELLPADDING);
            if (value5 != null) {
                table.setPadding(Float.parseFloat(value5 + OperatorName.FILL_NON_ZERO));
            }
            String value6 = attributes.getProperty(ElementTags.OFFSET);
            if (value6 != null) {
                table.setOffset(Float.parseFloat(value6 + OperatorName.FILL_NON_ZERO));
            }
            String value7 = attributes.getProperty(ElementTags.WIDTH);
            if (value7 != null) {
                if (value7.endsWith("%")) {
                    table.setWidth(Float.parseFloat(value7.substring(0, value7.length() - 1) + OperatorName.FILL_NON_ZERO));
                } else {
                    table.setWidth(Float.parseFloat(value7 + OperatorName.FILL_NON_ZERO));
                    table.setLocked(true);
                }
            }
            table.setTableFitsPage(Utilities.checkTrueOrFalse(attributes, ElementTags.TABLEFITSPAGE));
            table.setCellsFitPage(Utilities.checkTrueOrFalse(attributes, ElementTags.CELLSFITPAGE));
            table.setConvert2pdfptable(Utilities.checkTrueOrFalse(attributes, ElementTags.CONVERT2PDFP));
            setRectangleProperties(table, attributes);
            return table;
        } catch (BadElementException e2) {
            throw new ExceptionConverter(e2);
        }
    }

    private static void setRectangleProperties(Rectangle rect, Properties attributes) {
        String value = attributes.getProperty(ElementTags.BORDERWIDTH);
        if (value != null) {
            rect.setBorderWidth(Float.parseFloat(value + OperatorName.FILL_NON_ZERO));
        }
        int border = 0;
        if (Utilities.checkTrueOrFalse(attributes, ElementTags.LEFT)) {
            border = 0 | 4;
        }
        if (Utilities.checkTrueOrFalse(attributes, ElementTags.RIGHT)) {
            border |= 8;
        }
        if (Utilities.checkTrueOrFalse(attributes, ElementTags.TOP)) {
            border |= 1;
        }
        if (Utilities.checkTrueOrFalse(attributes, ElementTags.BOTTOM)) {
            border |= 2;
        }
        rect.setBorder(border);
        String r = attributes.getProperty(ElementTags.RED);
        String g = attributes.getProperty(ElementTags.GREEN);
        String b = attributes.getProperty(ElementTags.BLUE);
        if (r != null || g != null || b != null) {
            int red = 0;
            int green = 0;
            int blue = 0;
            if (r != null) {
                red = Integer.parseInt(r);
            }
            if (g != null) {
                green = Integer.parseInt(g);
            }
            if (b != null) {
                blue = Integer.parseInt(b);
            }
            rect.setBorderColor(new Color(red, green, blue));
        } else {
            rect.setBorderColor(Markup.decodeColor(attributes.getProperty(ElementTags.BORDERCOLOR)));
        }
        String r2 = (String) attributes.remove(ElementTags.BGRED);
        String g2 = (String) attributes.remove(ElementTags.BGGREEN);
        String b2 = (String) attributes.remove(ElementTags.BGBLUE);
        String value2 = attributes.getProperty(ElementTags.BACKGROUNDCOLOR);
        if (r2 == null && g2 == null && b2 == null) {
            if (value2 != null) {
                rect.setBackgroundColor(Markup.decodeColor(value2));
                return;
            }
            String value3 = attributes.getProperty(ElementTags.GRAYFILL);
            if (value3 != null) {
                rect.setGrayFill(Float.parseFloat(value3 + OperatorName.FILL_NON_ZERO));
                return;
            }
            return;
        }
        int red2 = 0;
        int green2 = 0;
        int blue2 = 0;
        if (r2 != null) {
            red2 = Integer.parseInt(r2);
        }
        if (g2 != null) {
            green2 = Integer.parseInt(g2);
        }
        if (b2 != null) {
            blue2 = Integer.parseInt(b2);
        }
        rect.setBackgroundColor(new Color(red2, green2, blue2));
    }

    public static ChapterAutoNumber getChapter(Properties attributes) {
        ChapterAutoNumber chapter = new ChapterAutoNumber("");
        setSectionParameters(chapter, attributes);
        return chapter;
    }

    public static Section getSection(Section parent, Properties attributes) {
        Section section = parent.addSection("");
        setSectionParameters(section, attributes);
        return section;
    }

    private static void setSectionParameters(Section section, Properties attributes) {
        String value = attributes.getProperty(ElementTags.NUMBERDEPTH);
        if (value != null) {
            section.setNumberDepth(Integer.parseInt(value));
        }
        String value2 = attributes.getProperty(ElementTags.INDENT);
        if (value2 != null) {
            section.setIndentation(Float.parseFloat(value2 + OperatorName.FILL_NON_ZERO));
        }
        String value3 = attributes.getProperty(ElementTags.INDENTATIONLEFT);
        if (value3 != null) {
            section.setIndentationLeft(Float.parseFloat(value3 + OperatorName.FILL_NON_ZERO));
        }
        String value4 = attributes.getProperty(ElementTags.INDENTATIONRIGHT);
        if (value4 != null) {
            section.setIndentationRight(Float.parseFloat(value4 + OperatorName.FILL_NON_ZERO));
        }
    }

    public static Image getImage(Properties attributes) throws BadElementException, IOException {
        String value = attributes.getProperty("url");
        if (value == null) {
            throw new MalformedURLException("The URL of the image is missing.");
        }
        Image image = Image.getInstance(value);
        String value2 = attributes.getProperty(ElementTags.ALIGN);
        int align = 0;
        if (value2 != null) {
            if (ElementTags.ALIGN_LEFT.equalsIgnoreCase(value2)) {
                align = 0 | 0;
            } else if (ElementTags.ALIGN_RIGHT.equalsIgnoreCase(value2)) {
                align = 0 | 2;
            } else if ("Middle".equalsIgnoreCase(value2)) {
                align = 0 | 1;
            }
        }
        if ("true".equalsIgnoreCase(attributes.getProperty(ElementTags.UNDERLYING))) {
            align |= 8;
        }
        if ("true".equalsIgnoreCase(attributes.getProperty(ElementTags.TEXTWRAP))) {
            align |= 4;
        }
        image.setAlignment(align);
        String value3 = attributes.getProperty(ElementTags.ALT);
        if (value3 != null) {
            image.setAlt(value3);
        }
        String x = attributes.getProperty(ElementTags.ABSOLUTEX);
        String y = attributes.getProperty(ElementTags.ABSOLUTEY);
        if (x != null && y != null) {
            image.setAbsolutePosition(Float.parseFloat(x + OperatorName.FILL_NON_ZERO), Float.parseFloat(y + OperatorName.FILL_NON_ZERO));
        }
        String value4 = attributes.getProperty(ElementTags.PLAINWIDTH);
        if (value4 != null) {
            image.scaleAbsoluteWidth(Float.parseFloat(value4 + OperatorName.FILL_NON_ZERO));
        }
        String value5 = attributes.getProperty(ElementTags.PLAINHEIGHT);
        if (value5 != null) {
            image.scaleAbsoluteHeight(Float.parseFloat(value5 + OperatorName.FILL_NON_ZERO));
        }
        String value6 = attributes.getProperty(ElementTags.ROTATION);
        if (value6 != null) {
            image.setRotation(Float.parseFloat(value6 + OperatorName.FILL_NON_ZERO));
        }
        return image;
    }

    public static Annotation getAnnotation(Properties attributes) {
        float llx = 0.0f;
        float lly = 0.0f;
        float urx = 0.0f;
        float ury = 0.0f;
        String value = attributes.getProperty("llx");
        if (value != null) {
            llx = Float.parseFloat(value + OperatorName.FILL_NON_ZERO);
        }
        String value2 = attributes.getProperty("lly");
        if (value2 != null) {
            lly = Float.parseFloat(value2 + OperatorName.FILL_NON_ZERO);
        }
        String value3 = attributes.getProperty("urx");
        if (value3 != null) {
            urx = Float.parseFloat(value3 + OperatorName.FILL_NON_ZERO);
        }
        String value4 = attributes.getProperty("ury");
        if (value4 != null) {
            ury = Float.parseFloat(value4 + OperatorName.FILL_NON_ZERO);
        }
        String title = attributes.getProperty("title");
        String text = attributes.getProperty("content");
        if (title != null || text != null) {
            return new Annotation(title, text, llx, lly, urx, ury);
        }
        String value5 = attributes.getProperty("url");
        if (value5 != null) {
            return new Annotation(llx, lly, urx, ury, value5);
        }
        String value6 = attributes.getProperty("named");
        if (value6 != null) {
            return new Annotation(llx, lly, urx, ury, Integer.parseInt(value6));
        }
        String file = attributes.getProperty("file");
        String destination = attributes.getProperty("destination");
        String page = (String) attributes.remove("page");
        if (file != null) {
            if (destination != null) {
                return new Annotation(llx, lly, urx, ury, file, destination);
            }
            if (page != null) {
                return new Annotation(llx, lly, urx, ury, file, Integer.parseInt(page));
            }
        }
        return new Annotation("", "", llx, lly, urx, ury);
    }
}
