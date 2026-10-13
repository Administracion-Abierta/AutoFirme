package org.apache.pdfbox.multipdf;

import com.aowagie.text.pdf.ColumnText;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/multipdf/LayerUtility.class */
public class LayerUtility {
    private static final boolean DEBUG = true;
    private final PDDocument targetDoc;
    private final PDFCloneUtility cloner;
    private static final Log LOG = LogFactory.getLog(LayerUtility.class);
    private static final Set<String> PAGE_TO_FORM_FILTER = new HashSet(Arrays.asList(PDAnnotationMarkup.RT_GROUP, "LastModified", "Metadata"));

    public LayerUtility(PDDocument document) {
        this.targetDoc = document;
        this.cloner = new PDFCloneUtility(document);
    }

    public PDDocument getDocument() {
        return this.targetDoc;
    }

    public void wrapInSaveRestore(PDPage page) throws IOException {
        COSStream saveGraphicsStateStream = getDocument().getDocument().createCOSStream();
        OutputStream saveStream = saveGraphicsStateStream.createOutputStream();
        saveStream.write("q\n".getBytes("ISO-8859-1"));
        saveStream.close();
        COSStream restoreGraphicsStateStream = getDocument().getDocument().createCOSStream();
        OutputStream restoreStream = restoreGraphicsStateStream.createOutputStream();
        restoreStream.write("Q\n".getBytes("ISO-8859-1"));
        restoreStream.close();
        COSDictionary pageDictionary = page.getCOSObject();
        COSBase contents = pageDictionary.getDictionaryObject(COSName.CONTENTS);
        if (contents instanceof COSStream) {
            COSStream contentsStream = (COSStream) contents;
            COSArray array = new COSArray();
            array.add((COSBase) saveGraphicsStateStream);
            array.add((COSBase) contentsStream);
            array.add((COSBase) restoreGraphicsStateStream);
            pageDictionary.setItem(COSName.CONTENTS, (COSBase) array);
            return;
        }
        if (contents instanceof COSArray) {
            COSArray contentsArray = (COSArray) contents;
            contentsArray.add(0, saveGraphicsStateStream);
            contentsArray.add((COSBase) restoreGraphicsStateStream);
            return;
        }
        throw new IOException("Contents are unknown type: " + contents.getClass().getName());
    }

    public PDFormXObject importPageAsForm(PDDocument sourceDoc, int pageNumber) throws IOException {
        PDPage page = sourceDoc.getPage(pageNumber);
        return importPageAsForm(sourceDoc, page);
    }

    public PDFormXObject importPageAsForm(PDDocument sourceDoc, PDPage page) throws IOException {
        importOcProperties(sourceDoc);
        PDStream newStream = new PDStream(this.targetDoc, page.getContents(), COSName.FLATE_DECODE);
        PDFormXObject form = new PDFormXObject(newStream);
        PDResources pageRes = page.getResources();
        PDResources formRes = new PDResources();
        this.cloner.cloneMerge(pageRes, formRes);
        form.setResources(formRes);
        transferDict(page.getCOSObject(), form.getCOSObject(), PAGE_TO_FORM_FILTER);
        Matrix matrix = form.getMatrix();
        AffineTransform at = matrix.createAffineTransform();
        PDRectangle mediaBox = page.getMediaBox();
        PDRectangle cropBox = page.getCropBox();
        PDRectangle viewBox = cropBox != null ? cropBox : mediaBox;
        int rotation = page.getRotation();
        at.translate(mediaBox.getLowerLeftX() - viewBox.getLowerLeftX(), mediaBox.getLowerLeftY() - viewBox.getLowerLeftY());
        switch (rotation) {
            case 90:
                at.scale(viewBox.getWidth() / viewBox.getHeight(), viewBox.getHeight() / viewBox.getWidth());
                at.translate(0.0d, viewBox.getWidth());
                at.rotate(-1.5707963267948966d);
                break;
            case 180:
                at.translate(viewBox.getWidth(), viewBox.getHeight());
                at.rotate(-3.141592653589793d);
                break;
            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                at.scale(viewBox.getWidth() / viewBox.getHeight(), viewBox.getHeight() / viewBox.getWidth());
                at.translate(viewBox.getHeight(), 0.0d);
                at.rotate(-4.71238898038469d);
                break;
        }
        at.translate(-viewBox.getLowerLeftX(), -viewBox.getLowerLeftY());
        if (!at.isIdentity()) {
            form.setMatrix(at);
        }
        BoundingBox bbox = new BoundingBox();
        bbox.setLowerLeftX(viewBox.getLowerLeftX());
        bbox.setLowerLeftY(viewBox.getLowerLeftY());
        bbox.setUpperRightX(viewBox.getUpperRightX());
        bbox.setUpperRightY(viewBox.getUpperRightY());
        form.setBBox(new PDRectangle(bbox));
        return form;
    }

    public PDOptionalContentGroup appendFormAsLayer(PDPage targetPage, PDFormXObject form, AffineTransform transform, String layerName) throws IOException {
        PDDocumentCatalog catalog = this.targetDoc.getDocumentCatalog();
        PDOptionalContentProperties ocprops = catalog.getOCProperties();
        if (ocprops == null) {
            ocprops = new PDOptionalContentProperties();
            catalog.setOCProperties(ocprops);
        }
        if (ocprops.hasGroup(layerName)) {
            throw new IllegalArgumentException("Optional group (layer) already exists: " + layerName);
        }
        PDRectangle cropBox = targetPage.getCropBox();
        if ((cropBox.getLowerLeftX() < ColumnText.GLOBAL_SPACE_CHAR_RATIO || cropBox.getLowerLeftY() < ColumnText.GLOBAL_SPACE_CHAR_RATIO) && transform.isIdentity()) {
            LOG.warn("Negative cropBox " + cropBox + " and identity transform may make your form invisible");
        }
        PDOptionalContentGroup layer = new PDOptionalContentGroup(layerName);
        ocprops.addGroup(layer);
        PDPageContentStream contentStream = new PDPageContentStream(this.targetDoc, targetPage, PDPageContentStream.AppendMode.APPEND, false);
        contentStream.beginMarkedContent(COSName.OC, layer);
        contentStream.saveGraphicsState();
        contentStream.transform(new Matrix(transform));
        contentStream.drawForm(form);
        contentStream.restoreGraphicsState();
        contentStream.endMarkedContent();
        contentStream.close();
        return layer;
    }

    private void transferDict(COSDictionary orgDict, COSDictionary targetDict, Set<String> filter) throws IOException {
        for (Map.Entry<COSName, COSBase> entry : orgDict.entrySet()) {
            COSName key = entry.getKey();
            if (filter.contains(key.getName())) {
                targetDict.setItem(key, this.cloner.cloneForNewDocument(entry.getValue()));
            }
        }
    }

    private void importOcProperties(PDDocument srcDoc) throws IOException {
        PDDocumentCatalog srcCatalog = srcDoc.getDocumentCatalog();
        PDOptionalContentProperties srcOCProperties = srcCatalog.getOCProperties();
        if (srcOCProperties == null) {
            return;
        }
        PDDocumentCatalog dstCatalog = this.targetDoc.getDocumentCatalog();
        PDOptionalContentProperties dstOCProperties = dstCatalog.getOCProperties();
        if (dstOCProperties == null) {
            dstCatalog.setOCProperties(new PDOptionalContentProperties((COSDictionary) this.cloner.cloneForNewDocument(srcOCProperties)));
        } else {
            this.cloner.cloneMerge(srcOCProperties, dstOCProperties);
        }
    }
}
