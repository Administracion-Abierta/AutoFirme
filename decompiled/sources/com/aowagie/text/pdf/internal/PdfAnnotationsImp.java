package com.aowagie.text.pdf.internal;

import com.aowagie.text.Annotation;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Rectangle;
import com.aowagie.text.pdf.PdfAcroForm;
import com.aowagie.text.pdf.PdfAction;
import com.aowagie.text.pdf.PdfAnnotation;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfFileSpecification;
import com.aowagie.text.pdf.PdfFormField;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfObject;
import com.aowagie.text.pdf.PdfRectangle;
import com.aowagie.text.pdf.PdfString;
import com.aowagie.text.pdf.PdfWriter;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/internal/PdfAnnotationsImp.class */
public class PdfAnnotationsImp {
    private final PdfAcroForm acroForm;
    private ArrayList annotations;
    private ArrayList delayedAnnotations = new ArrayList();

    public PdfAnnotationsImp(PdfWriter writer) {
        this.acroForm = new PdfAcroForm(writer);
    }

    public boolean hasValidAcroForm() {
        return this.acroForm.isValid();
    }

    public PdfAcroForm getAcroForm() {
        return this.acroForm;
    }

    public void setSigFlags(int f) {
        this.acroForm.setSigFlags(f);
    }

    public void addCalculationOrder(PdfFormField formField) {
        this.acroForm.addCalculationOrder(formField);
    }

    public void addAnnotation(PdfAnnotation annot) {
        if (annot.isForm()) {
            PdfFormField field = (PdfFormField) annot;
            if (field.getParent() == null) {
                addFormFieldRaw(field);
                return;
            }
            return;
        }
        this.annotations.add(annot);
    }

    public void addPlainAnnotation(PdfAnnotation annot) {
        this.annotations.add(annot);
    }

    private void addFormFieldRaw(PdfFormField field) {
        this.annotations.add(field);
        ArrayList kids = field.getKids();
        if (kids != null) {
            for (int k = 0; k < kids.size(); k++) {
                addFormFieldRaw((PdfFormField) kids.get(k));
            }
        }
    }

    public boolean hasUnusedAnnotations() {
        return !this.annotations.isEmpty();
    }

    public void resetAnnotations() {
        this.annotations = this.delayedAnnotations;
        this.delayedAnnotations = new ArrayList();
    }

    public PdfArray rotateAnnotations(PdfWriter writer, Rectangle pageSize) {
        PdfRectangle rect;
        HashMap templates;
        PdfArray array = new PdfArray();
        int rotation = pageSize.getRotation() % 360;
        int currentPage = writer.getCurrentPageNumber();
        for (int k = 0; k < this.annotations.size(); k++) {
            PdfAnnotation dic = (PdfAnnotation) this.annotations.get(k);
            int page = dic.getPlaceInPage();
            if (page > currentPage) {
                this.delayedAnnotations.add(dic);
            } else {
                if (dic.isForm()) {
                    if (!dic.isUsed() && (templates = dic.getTemplates()) != null) {
                        this.acroForm.addFieldTemplates(templates);
                    }
                    PdfFormField field = (PdfFormField) dic;
                    if (field.getParent() == null) {
                        this.acroForm.addDocumentField(field.getIndirectReference());
                    }
                }
                if (dic.isAnnotation()) {
                    array.add(dic.getIndirectReference());
                    if (!dic.isUsed() && (rect = (PdfRectangle) dic.get(PdfName.RECT)) != null) {
                        switch (rotation) {
                            case 90:
                                dic.put(PdfName.RECT, new PdfRectangle(pageSize.getTop() - rect.bottom(), rect.left(), pageSize.getTop() - rect.top(), rect.right()));
                                break;
                            case 180:
                                dic.put(PdfName.RECT, new PdfRectangle(pageSize.getRight() - rect.left(), pageSize.getTop() - rect.bottom(), pageSize.getRight() - rect.right(), pageSize.getTop() - rect.top()));
                                break;
                            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                                dic.put(PdfName.RECT, new PdfRectangle(rect.bottom(), pageSize.getRight() - rect.left(), rect.top(), pageSize.getRight() - rect.right()));
                                break;
                        }
                    }
                }
                if (dic.isUsed()) {
                    continue;
                } else {
                    dic.setUsed();
                    try {
                        writer.addToBody(dic, dic.getIndirectReference());
                    } catch (IOException e) {
                        throw new ExceptionConverter(e);
                    }
                }
            }
        }
        return array;
    }

    public static PdfAnnotation convertAnnotation(PdfWriter writer, Annotation annot, Rectangle defaultRect) throws IOException {
        PdfFileSpecification fs;
        switch (annot.annotationType()) {
            case 1:
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((URL) annot.attributes().get("url")));
            case 2:
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String) annot.attributes().get("file")));
            case 3:
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String) annot.attributes().get("file"), (String) annot.attributes().get("destination")));
            case 4:
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String) annot.attributes().get("file"), ((Integer) annot.attributes().get("page")).intValue()));
            case 5:
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction(((Integer) annot.attributes().get("named")).intValue()));
            case 6:
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String) annot.attributes().get(Annotation.APPLICATION), (String) annot.attributes().get(Annotation.PARAMETERS), (String) annot.attributes().get(Annotation.OPERATION), (String) annot.attributes().get(Annotation.DEFAULTDIR)));
            case 7:
                boolean[] sparams = (boolean[]) annot.attributes().get(Annotation.PARAMETERS);
                String fname = (String) annot.attributes().get("file");
                String mimetype = (String) annot.attributes().get(Annotation.MIMETYPE);
                if (sparams[0]) {
                    fs = PdfFileSpecification.fileEmbedded(writer, fname, fname, null);
                } else {
                    fs = PdfFileSpecification.fileExtern(writer, fname);
                }
                PdfAnnotation ann = PdfAnnotation.createScreen(writer, new Rectangle(annot.llx(), annot.lly(), annot.urx(), annot.ury()), fname, fs, mimetype, sparams[1]);
                return ann;
            default:
                return new PdfAnnotation(writer, defaultRect.getLeft(), defaultRect.getBottom(), defaultRect.getRight(), defaultRect.getTop(), new PdfString(annot.title(), PdfObject.TEXT_UNICODE), new PdfString(annot.content(), PdfObject.TEXT_UNICODE));
        }
    }
}
