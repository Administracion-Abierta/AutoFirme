package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/PDCaretAppearanceHandler.class */
public class PDCaretAppearanceHandler extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDCaretAppearanceHandler.class);

    public PDCaretAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDCaretAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateAppearanceStreams() {
        generateNormalAppearance();
        generateRolloverAppearance();
        generateDownAppearance();
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateNormalAppearance() {
        PDAnnotationMarkup annotation = (PDAnnotationMarkup) getAnnotation();
        PDAppearanceContentStream contentStream = null;
        try {
            try {
                contentStream = getNormalAppearanceAsContentStream();
                contentStream.setStrokingColor(getColor());
                contentStream.setNonStrokingColor(getColor());
                setOpacity(contentStream, annotation.getConstantOpacity());
                PDRectangle rect = getRectangle();
                PDRectangle bbox = new PDRectangle(rect.getWidth(), rect.getHeight());
                if (!annotation.getCOSObject().containsKey(COSName.RD)) {
                    float rd = Math.min(rect.getHeight() / 10.0f, 5.0f);
                    annotation.setRectDifferences(rd);
                    bbox = new PDRectangle(-rd, -rd, rect.getWidth() + (2.0f * rd), rect.getHeight() + (2.0f * rd));
                    Matrix matrix = annotation.getNormalAppearanceStream().getMatrix();
                    matrix.transformPoint(rd, rd);
                    annotation.getNormalAppearanceStream().setMatrix(matrix.createAffineTransform());
                    PDRectangle rect2 = new PDRectangle(rect.getLowerLeftX() - rd, rect.getLowerLeftY() - rd, rect.getWidth() + (2.0f * rd), rect.getHeight() + (2.0f * rd));
                    annotation.setRectangle(rect2);
                }
                annotation.getNormalAppearanceStream().setBBox(bbox);
                float halfX = rect.getWidth() / 2.0f;
                float halfY = rect.getHeight() / 2.0f;
                contentStream.moveTo(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                contentStream.curveTo(halfX, ColumnText.GLOBAL_SPACE_CHAR_RATIO, halfX, halfY, halfX, rect.getHeight());
                contentStream.curveTo(halfX, halfY, halfX, ColumnText.GLOBAL_SPACE_CHAR_RATIO, rect.getWidth(), ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                contentStream.closePath();
                contentStream.fill();
                IOUtils.closeQuietly(contentStream);
            } catch (IOException e) {
                LOG.error(e);
                IOUtils.closeQuietly(contentStream);
            }
        } catch (Throwable th) {
            IOUtils.closeQuietly(contentStream);
            throw th;
        }
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateRolloverAppearance() {
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateDownAppearance() {
    }
}
