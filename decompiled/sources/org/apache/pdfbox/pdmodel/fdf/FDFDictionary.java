package org.apache.pdfbox.pdmodel.fdf;

import com.aowagie.text.html.Markup;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDSimpleFileSpecification;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFDictionary.class */
public class FDFDictionary implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(FDFDictionary.class);
    private COSDictionary fdf;

    public FDFDictionary() {
        this.fdf = new COSDictionary();
    }

    public FDFDictionary(COSDictionary fdfDictionary) {
        this.fdf = fdfDictionary;
    }

    public FDFDictionary(Element fdfXML) {
        this();
        NodeList nodeList = fdfXML.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element child = (Element) node;
                if (child.getTagName().equals(OperatorName.FILL_NON_ZERO)) {
                    PDSimpleFileSpecification fs = new PDSimpleFileSpecification();
                    fs.setFile(child.getAttribute("href"));
                    setFile(fs);
                } else if (child.getTagName().equals("ids")) {
                    COSArray ids = new COSArray();
                    String original = child.getAttribute("original");
                    String modified = child.getAttribute("modified");
                    try {
                        ids.add((COSBase) COSString.parseHex(original));
                    } catch (IOException e) {
                        LOG.warn("Error parsing ID entry for attribute 'original' [" + original + "]. ID entry ignored.", e);
                    }
                    try {
                        ids.add((COSBase) COSString.parseHex(modified));
                    } catch (IOException e2) {
                        LOG.warn("Error parsing ID entry for attribute 'modified' [" + modified + "]. ID entry ignored.", e2);
                    }
                    setID(ids);
                } else if (child.getTagName().equals("fields")) {
                    NodeList fields = child.getChildNodes();
                    List<FDFField> fieldList = new ArrayList<>();
                    for (int f = 0; f < fields.getLength(); f++) {
                        Node currentNode = fields.item(f);
                        if ((currentNode instanceof Element) && ((Element) currentNode).getTagName().equals("field")) {
                            try {
                                fieldList.add(new FDFField((Element) fields.item(f)));
                            } catch (IOException e3) {
                                LOG.warn("Error parsing field entry [" + currentNode.getNodeValue() + "]. Field ignored.", e3);
                            }
                        }
                    }
                    setFields(fieldList);
                } else if (child.getTagName().equals("annots")) {
                    NodeList annots = child.getChildNodes();
                    List<FDFAnnotation> annotList = new ArrayList<>();
                    for (int j = 0; j < annots.getLength(); j++) {
                        Node annotNode = annots.item(j);
                        if (annotNode instanceof Element) {
                            Element annot = (Element) annotNode;
                            String annotationName = annot.getNodeName();
                            try {
                                if (annotationName.equals("text")) {
                                    annotList.add(new FDFAnnotationText(annot));
                                } else if (annotationName.equals("caret")) {
                                    annotList.add(new FDFAnnotationCaret(annot));
                                } else if (annotationName.equals("freetext")) {
                                    annotList.add(new FDFAnnotationFreeText(annot));
                                } else if (annotationName.equals("fileattachment")) {
                                    annotList.add(new FDFAnnotationFileAttachment(annot));
                                } else if (annotationName.equals("highlight")) {
                                    annotList.add(new FDFAnnotationHighlight(annot));
                                } else if (annotationName.equals("ink")) {
                                    annotList.add(new FDFAnnotationInk(annot));
                                } else if (annotationName.equals("line")) {
                                    annotList.add(new FDFAnnotationLine(annot));
                                } else if (annotationName.equals("link")) {
                                    annotList.add(new FDFAnnotationLink(annot));
                                } else if (annotationName.equals("circle")) {
                                    annotList.add(new FDFAnnotationCircle(annot));
                                } else if (annotationName.equals("square")) {
                                    annotList.add(new FDFAnnotationSquare(annot));
                                } else if (annotationName.equals("polygon")) {
                                    annotList.add(new FDFAnnotationPolygon(annot));
                                } else if (annotationName.equals("polyline")) {
                                    annotList.add(new FDFAnnotationPolyline(annot));
                                } else if (annotationName.equals("sound")) {
                                    annotList.add(new FDFAnnotationSound(annot));
                                } else if (annotationName.equals("squiggly")) {
                                    annotList.add(new FDFAnnotationSquiggly(annot));
                                } else if (annotationName.equals("stamp")) {
                                    annotList.add(new FDFAnnotationStamp(annot));
                                } else if (annotationName.equals("strikeout")) {
                                    annotList.add(new FDFAnnotationStrikeOut(annot));
                                } else if (annotationName.equals(Markup.CSS_VALUE_UNDERLINE)) {
                                    annotList.add(new FDFAnnotationUnderline(annot));
                                } else {
                                    LOG.warn("Unknown or unsupported annotation type '" + annotationName + OperatorName.SHOW_TEXT_LINE);
                                }
                            } catch (IOException e4) {
                                LOG.warn("Error parsing annotation information [" + annot.getNodeValue() + "]. Annotation ignored", e4);
                            }
                        }
                    }
                    setAnnotations(annotList);
                }
            }
        }
    }

    public void writeXML(Writer output) throws IOException {
        PDFileSpecification fs = getFile();
        if (fs != null) {
            output.write("<f href=\"" + fs.getFile() + "\" />\n");
        }
        COSArray ids = getID();
        if (ids != null) {
            COSString original = (COSString) ids.getObject(0);
            COSString modified = (COSString) ids.getObject(1);
            output.write("<ids original=\"" + original.toHexString() + "\" ");
            output.write("modified=\"" + modified.toHexString() + "\" />\n");
        }
        List<FDFField> fields = getFields();
        if (fields != null && fields.size() > 0) {
            output.write("<fields>\n");
            for (FDFField field : fields) {
                field.writeXML(output);
            }
            output.write("</fields>\n");
        }
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.fdf;
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.fdf.getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.fdf.setItem(COSName.F, fs);
    }

    public COSArray getID() {
        return (COSArray) this.fdf.getDictionaryObject(COSName.ID);
    }

    public void setID(COSArray id) {
        this.fdf.setItem(COSName.ID, (COSBase) id);
    }

    public List<FDFField> getFields() {
        List<FDFField> retval = null;
        COSArray fieldArray = (COSArray) this.fdf.getDictionaryObject(COSName.FIELDS);
        if (fieldArray != null) {
            List<FDFField> fields = new ArrayList<>();
            for (int i = 0; i < fieldArray.size(); i++) {
                fields.add(new FDFField((COSDictionary) fieldArray.getObject(i)));
            }
            retval = new COSArrayList<>(fields, fieldArray);
        }
        return retval;
    }

    public void setFields(List<FDFField> fields) {
        this.fdf.setItem(COSName.FIELDS, (COSBase) COSArrayList.converterToCOSArray(fields));
    }

    public String getStatus() {
        return this.fdf.getString(COSName.STATUS);
    }

    public void setStatus(String status) {
        this.fdf.setString(COSName.STATUS, status);
    }

    public List<FDFPage> getPages() {
        List<FDFPage> retval = null;
        COSArray pageArray = (COSArray) this.fdf.getDictionaryObject(COSName.PAGES);
        if (pageArray != null) {
            List<FDFPage> pages = new ArrayList<>();
            for (int i = 0; i < pageArray.size(); i++) {
                pages.add(new FDFPage((COSDictionary) pageArray.get(i)));
            }
            retval = new COSArrayList<>(pages, pageArray);
        }
        return retval;
    }

    public void setPages(List<FDFPage> pages) {
        this.fdf.setItem(COSName.PAGES, (COSBase) COSArrayList.converterToCOSArray(pages));
    }

    public String getEncoding() {
        String encoding = this.fdf.getNameAsString(COSName.ENCODING);
        if (encoding == null) {
            encoding = "PDFDocEncoding";
        }
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.fdf.setName(COSName.ENCODING, encoding);
    }

    public List<FDFAnnotation> getAnnotations() throws IOException {
        List<FDFAnnotation> retval = null;
        COSArray annotArray = (COSArray) this.fdf.getDictionaryObject(COSName.ANNOTS);
        if (annotArray != null) {
            List<FDFAnnotation> annots = new ArrayList<>();
            for (int i = 0; i < annotArray.size(); i++) {
                annots.add(FDFAnnotation.create((COSDictionary) annotArray.getObject(i)));
            }
            retval = new COSArrayList<>(annots, annotArray);
        }
        return retval;
    }

    public void setAnnotations(List<FDFAnnotation> annots) {
        this.fdf.setItem(COSName.ANNOTS, (COSBase) COSArrayList.converterToCOSArray(annots));
    }

    public COSStream getDifferences() {
        return (COSStream) this.fdf.getDictionaryObject(COSName.DIFFERENCES);
    }

    public void setDifferences(COSStream diff) {
        this.fdf.setItem(COSName.DIFFERENCES, (COSBase) diff);
    }

    public String getTarget() {
        return this.fdf.getString(COSName.TARGET);
    }

    public void setTarget(String target) {
        this.fdf.setString(COSName.TARGET, target);
    }

    public List<PDFileSpecification> getEmbeddedFDFs() throws IOException {
        List<PDFileSpecification> retval = null;
        COSArray embeddedArray = (COSArray) this.fdf.getDictionaryObject(COSName.EMBEDDED_FDFS);
        if (embeddedArray != null) {
            List<PDFileSpecification> embedded = new ArrayList<>();
            for (int i = 0; i < embeddedArray.size(); i++) {
                embedded.add(PDFileSpecification.createFS(embeddedArray.get(i)));
            }
            retval = new COSArrayList<>(embedded, embeddedArray);
        }
        return retval;
    }

    public void setEmbeddedFDFs(List<PDFileSpecification> embedded) {
        this.fdf.setItem(COSName.EMBEDDED_FDFS, (COSBase) COSArrayList.converterToCOSArray(embedded));
    }

    public FDFJavaScript getJavaScript() {
        FDFJavaScript fs = null;
        COSDictionary dic = (COSDictionary) this.fdf.getDictionaryObject(COSName.JAVA_SCRIPT);
        if (dic != null) {
            fs = new FDFJavaScript(dic);
        }
        return fs;
    }

    public void setJavaScript(FDFJavaScript js) {
        this.fdf.setItem(COSName.JAVA_SCRIPT, js);
    }
}
