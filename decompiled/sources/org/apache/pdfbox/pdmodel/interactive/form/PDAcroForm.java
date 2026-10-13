package org.apache.pdfbox.pdmodel.interactive.form;

import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.fdf.FDFCatalog;
import org.apache.pdfbox.pdmodel.fdf.FDFDictionary;
import org.apache.pdfbox.pdmodel.fdf.FDFDocument;
import org.apache.pdfbox.pdmodel.fdf.FDFField;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDAcroForm.class */
public final class PDAcroForm implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(PDAcroForm.class);
    private static final int FLAG_SIGNATURES_EXIST = 1;
    private static final int FLAG_APPEND_ONLY = 2;
    private final PDDocument document;
    private final COSDictionary dictionary;
    private Map<String, PDField> fieldCache;
    private ScriptingHandler scriptingHandler;

    public PDAcroForm(PDDocument doc) {
        this.document = doc;
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.FIELDS, (COSBase) new COSArray());
    }

    public PDAcroForm(PDDocument doc, COSDictionary form) {
        this.document = doc;
        this.dictionary = form;
    }

    PDDocument getDocument() {
        return this.document;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public void importFDF(FDFDocument fdf) throws IOException {
        List<FDFField> fields = fdf.getCatalog().getFDF().getFields();
        if (fields != null) {
            for (FDFField field : fields) {
                PDField docField = getField(field.getPartialFieldName());
                if (docField != null) {
                    docField.importFDF(field);
                }
            }
        }
    }

    public FDFDocument exportFDF() throws IOException {
        FDFDocument fdf = new FDFDocument();
        FDFCatalog catalog = fdf.getCatalog();
        FDFDictionary fdfDict = new FDFDictionary();
        catalog.setFDF(fdfDict);
        List<FDFField> fdfFields = new ArrayList<>();
        List<PDField> fields = getFields();
        for (PDField field : fields) {
            fdfFields.add(field.exportFDF());
        }
        fdfDict.setID(this.document.getDocument().getDocumentID());
        if (!fdfFields.isEmpty()) {
            fdfDict.setFields(fdfFields);
        }
        return fdf;
    }

    public void flatten() throws IOException {
        if (xfaIsDynamic()) {
            LOG.warn("Flatten for a dynamix XFA form is not supported");
            return;
        }
        List<PDField> fields = new ArrayList<>();
        for (PDField field : getFieldTree()) {
            fields.add(field);
        }
        flatten(fields, false);
    }

    public void flatten(List<PDField> fields, boolean refreshAppearances) throws IOException {
        if (fields.isEmpty()) {
            return;
        }
        if (!refreshAppearances && getNeedAppearances()) {
            LOG.warn("acroForm.getNeedAppearances() returns true, visual field appearances may not have been set");
            LOG.warn("call acroForm.refreshAppearances() or use the flatten() method with refreshAppearances parameter");
        }
        if (xfaIsDynamic()) {
            LOG.warn("Flatten for a dynamix XFA form is not supported");
            return;
        }
        if (refreshAppearances) {
            refreshAppearances(fields);
        }
        Map<COSDictionary, Set<COSDictionary>> pagesWidgetsMap = buildPagesWidgetsMap(fields);
        for (PDPage page : this.document.getPages()) {
            Set<COSDictionary> widgetsForPageMap = pagesWidgetsMap.get(page.getCOSObject());
            boolean isContentStreamWrapped = false;
            List<PDAnnotation> annotations = new ArrayList<>();
            for (PDAnnotation annotation : page.getAnnotations()) {
                if (widgetsForPageMap == null || !widgetsForPageMap.contains(annotation.getCOSObject())) {
                    annotations.add(annotation);
                } else if (isVisibleAnnotation(annotation)) {
                    PDPageContentStream contentStream = new PDPageContentStream(this.document, page, PDPageContentStream.AppendMode.APPEND, true, !isContentStreamWrapped);
                    try {
                        isContentStreamWrapped = true;
                        PDAppearanceStream appearanceStream = annotation.getNormalAppearanceStream();
                        PDFormXObject fieldObject = new PDFormXObject(appearanceStream.getCOSObject());
                        contentStream.saveGraphicsState();
                        Matrix transformationMatrix = resolveTransformationMatrix(annotation, appearanceStream);
                        contentStream.transform(transformationMatrix);
                        contentStream.drawForm(fieldObject);
                        contentStream.restoreGraphicsState();
                        contentStream.close();
                    } catch (Throwable th) {
                        contentStream.close();
                        throw th;
                    }
                } else {
                    continue;
                }
            }
            page.setAnnotations(annotations);
        }
        removeFields(fields);
        this.dictionary.removeItem(COSName.XFA);
    }

    private boolean isVisibleAnnotation(PDAnnotation annotation) {
        PDAppearanceStream normalAppearanceStream;
        PDRectangle bbox;
        return (annotation.isInvisible() || annotation.isHidden() || (normalAppearanceStream = annotation.getNormalAppearanceStream()) == null || (bbox = normalAppearanceStream.getBBox()) == null || bbox.getWidth() <= ColumnText.GLOBAL_SPACE_CHAR_RATIO || bbox.getHeight() <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) ? false : true;
    }

    public void refreshAppearances() throws IOException {
        for (PDField field : getFieldTree()) {
            if (field instanceof PDTerminalField) {
                ((PDTerminalField) field).constructAppearances();
            }
        }
    }

    public void refreshAppearances(List<PDField> fields) throws IOException {
        for (PDField field : fields) {
            if (field instanceof PDTerminalField) {
                ((PDTerminalField) field).constructAppearances();
            }
        }
    }

    public List<PDField> getFields() {
        PDField field;
        COSArray cosFields = this.dictionary.getCOSArray(COSName.FIELDS);
        if (cosFields == null) {
            return Collections.emptyList();
        }
        List<PDField> pdFields = new ArrayList<>();
        for (int i = 0; i < cosFields.size(); i++) {
            COSDictionary element = (COSDictionary) cosFields.getObject(i);
            if (element != null && (field = PDField.fromDictionary(this, element, null)) != null) {
                pdFields.add(field);
            }
        }
        return new COSArrayList(pdFields, cosFields);
    }

    public void setFields(List<PDField> fields) {
        this.dictionary.setItem(COSName.FIELDS, (COSBase) COSArrayList.converterToCOSArray(fields));
    }

    public Iterator<PDField> getFieldIterator() {
        return new PDFieldTree(this).iterator();
    }

    public PDFieldTree getFieldTree() {
        return new PDFieldTree(this);
    }

    public void setCacheFields(boolean cache) {
        if (cache) {
            this.fieldCache = new HashMap();
            for (PDField field : getFieldTree()) {
                this.fieldCache.put(field.getFullyQualifiedName(), field);
            }
            return;
        }
        this.fieldCache = null;
    }

    public boolean isCachingFields() {
        return this.fieldCache != null;
    }

    public PDField getField(String fullyQualifiedName) {
        if (this.fieldCache != null) {
            return this.fieldCache.get(fullyQualifiedName);
        }
        for (PDField field : getFieldTree()) {
            if (field.getFullyQualifiedName().equals(fullyQualifiedName)) {
                return field;
            }
        }
        return null;
    }

    public String getDefaultAppearance() {
        return this.dictionary.getString(COSName.DA, "");
    }

    public void setDefaultAppearance(String daValue) {
        this.dictionary.setString(COSName.DA, daValue);
    }

    public boolean getNeedAppearances() {
        return this.dictionary.getBoolean(COSName.NEED_APPEARANCES, false);
    }

    public void setNeedAppearances(Boolean value) {
        this.dictionary.setBoolean(COSName.NEED_APPEARANCES, value.booleanValue());
    }

    public PDResources getDefaultResources() {
        PDResources retval = null;
        COSBase base = this.dictionary.getDictionaryObject(COSName.DR);
        if (base instanceof COSDictionary) {
            retval = new PDResources((COSDictionary) base, this.document.getResourceCache());
        }
        return retval;
    }

    public void setDefaultResources(PDResources dr) {
        this.dictionary.setItem(COSName.DR, dr);
    }

    public boolean hasXFA() {
        return this.dictionary.containsKey(COSName.XFA);
    }

    public boolean xfaIsDynamic() {
        return hasXFA() && getFields().isEmpty();
    }

    public PDXFAResource getXFA() {
        PDXFAResource xfa = null;
        COSBase base = this.dictionary.getDictionaryObject(COSName.XFA);
        if (base != null) {
            xfa = new PDXFAResource(base);
        }
        return xfa;
    }

    public void setXFA(PDXFAResource xfa) {
        this.dictionary.setItem(COSName.XFA, xfa);
    }

    public int getQ() {
        int retval = 0;
        COSNumber number = (COSNumber) this.dictionary.getDictionaryObject(COSName.Q);
        if (number != null) {
            retval = number.intValue();
        }
        return retval;
    }

    public void setQ(int q) {
        this.dictionary.setInt(COSName.Q, q);
    }

    public boolean isSignaturesExist() {
        return this.dictionary.getFlag(COSName.SIG_FLAGS, 1);
    }

    public void setSignaturesExist(boolean signaturesExist) {
        this.dictionary.setFlag(COSName.SIG_FLAGS, 1, signaturesExist);
    }

    public boolean isAppendOnly() {
        return this.dictionary.getFlag(COSName.SIG_FLAGS, 2);
    }

    public void setAppendOnly(boolean appendOnly) {
        this.dictionary.setFlag(COSName.SIG_FLAGS, 2, appendOnly);
    }

    public ScriptingHandler getScriptingHandler() {
        return this.scriptingHandler;
    }

    public void setScriptingHandler(ScriptingHandler scriptingHandler) {
        this.scriptingHandler = scriptingHandler;
    }

    private Matrix resolveTransformationMatrix(PDAnnotation annotation, PDAppearanceStream appearanceStream) {
        Rectangle2D transformedAppearanceBox = getTransformedAppearanceBBox(appearanceStream);
        PDRectangle annotationRect = annotation.getRectangle();
        Matrix transformationMatrix = new Matrix();
        transformationMatrix.translate((float) (((double) annotationRect.getLowerLeftX()) - transformedAppearanceBox.getX()), (float) (((double) annotationRect.getLowerLeftY()) - transformedAppearanceBox.getY()));
        transformationMatrix.scale((float) (((double) annotationRect.getWidth()) / transformedAppearanceBox.getWidth()), (float) (((double) annotationRect.getHeight()) / transformedAppearanceBox.getHeight()));
        return transformationMatrix;
    }

    private Rectangle2D getTransformedAppearanceBBox(PDAppearanceStream appearanceStream) {
        Matrix appearanceStreamMatrix = appearanceStream.getMatrix();
        PDRectangle appearanceStreamBBox = appearanceStream.getBBox();
        GeneralPath transformedAppearanceBox = appearanceStreamBBox.transform(appearanceStreamMatrix);
        return transformedAppearanceBox.getBounds2D();
    }

    private Map<COSDictionary, Set<COSDictionary>> buildPagesWidgetsMap(List<PDField> fields) throws IOException {
        Map<COSDictionary, Set<COSDictionary>> pagesAnnotationsMap = new HashMap<>();
        boolean hasMissingPageRef = false;
        for (PDField field : fields) {
            List<PDAnnotationWidget> widgets = field.getWidgets();
            for (PDAnnotationWidget widget : widgets) {
                PDPage page = widget.getPage();
                if (page != null) {
                    fillPagesAnnotationMap(pagesAnnotationsMap, page, widget);
                } else {
                    hasMissingPageRef = true;
                }
            }
        }
        if (!hasMissingPageRef) {
            return pagesAnnotationsMap;
        }
        LOG.warn("There has been a widget with a missing page reference, will check all page annotations");
        for (PDPage page2 : this.document.getPages()) {
            for (PDAnnotation annotation : page2.getAnnotations()) {
                if (annotation instanceof PDAnnotationWidget) {
                    fillPagesAnnotationMap(pagesAnnotationsMap, page2, (PDAnnotationWidget) annotation);
                }
            }
        }
        return pagesAnnotationsMap;
    }

    private void fillPagesAnnotationMap(Map<COSDictionary, Set<COSDictionary>> pagesAnnotationsMap, PDPage page, PDAnnotationWidget widget) {
        if (pagesAnnotationsMap.get(page.getCOSObject()) == null) {
            Set<COSDictionary> widgetsForPage = new HashSet<>();
            widgetsForPage.add(widget.getCOSObject());
            pagesAnnotationsMap.put(page.getCOSObject(), widgetsForPage);
            return;
        }
        pagesAnnotationsMap.get(page.getCOSObject()).add(widget.getCOSObject());
    }

    private void removeFields(List<PDField> fields) {
        COSBase dictionaryObject;
        for (PDField field : fields) {
            if (field.getParent() == null) {
                dictionaryObject = this.dictionary.getDictionaryObject(COSName.FIELDS);
            } else {
                dictionaryObject = field.getParent().getCOSObject().getDictionaryObject(COSName.KIDS);
            }
            COSArray array = (COSArray) dictionaryObject;
            array.removeObject(field.getCOSObject());
        }
    }
}
