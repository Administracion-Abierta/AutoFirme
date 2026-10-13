package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/PDUnderlineAppearanceHandler.class */
public class PDUnderlineAppearanceHandler extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDUnderlineAppearanceHandler.class);

    public PDUnderlineAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDUnderlineAppearanceHandler(PDAnnotation annotation, PDDocument document) {
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
        PDAnnotationTextMarkup annotation = (PDAnnotationTextMarkup) getAnnotation();
        PDRectangle rect = annotation.getRectangle();
        float[] pathsArray = annotation.getQuadPoints();
        if (pathsArray == null) {
            return;
        }
        AnnotationBorder ab = AnnotationBorder.getAnnotationBorder(annotation, annotation.getBorderStyle());
        PDColor color = annotation.getColor();
        if (color == null || color.getComponents().length == 0) {
            return;
        }
        if (Float.compare(ab.width, ColumnText.GLOBAL_SPACE_CHAR_RATIO) == 0) {
            ab.width = 1.5f;
        }
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        for (int i = 0; i < pathsArray.length / 2; i++) {
            float x = pathsArray[i * 2];
            float y = pathsArray[(i * 2) + 1];
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
        rect.setLowerLeftX(Math.min(minX - (ab.width / 2.0f), rect.getLowerLeftX()));
        rect.setLowerLeftY(Math.min(minY - (ab.width / 2.0f), rect.getLowerLeftY()));
        rect.setUpperRightX(Math.max(maxX + (ab.width / 2.0f), rect.getUpperRightX()));
        rect.setUpperRightY(Math.max(maxY + (ab.width / 2.0f), rect.getUpperRightY()));
        annotation.setRectangle(rect);
        PDAppearanceContentStream cs = null;
        try {
            try {
                cs = getNormalAppearanceAsContentStream();
                setOpacity(cs, annotation.getConstantOpacity());
                cs.setStrokingColor(color);
                if (ab.dashArray != null) {
                    cs.setLineDashPattern(ab.dashArray, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                }
                cs.setLineWidth(ab.width);
                for (int i2 = 0; i2 < pathsArray.length / 8; i2++) {
                    float len0 = (float) Math.sqrt(Math.pow(pathsArray[i2 * 8] - pathsArray[(i2 * 8) + 4], 2.0d) + Math.pow(pathsArray[(i2 * 8) + 1] - pathsArray[(i2 * 8) + 5], 2.0d));
                    float x0 = pathsArray[(i2 * 8) + 4];
                    float y0 = pathsArray[(i2 * 8) + 5];
                    if (Float.compare(len0, ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0) {
                        x0 += (((pathsArray[i2 * 8] - pathsArray[(i2 * 8) + 4]) / len0) * len0) / 7.0f;
                        y0 += ((pathsArray[(i2 * 8) + 1] - pathsArray[(i2 * 8) + 5]) / len0) * (len0 / 7.0f);
                    }
                    float len1 = (float) Math.sqrt(Math.pow(pathsArray[(i2 * 8) + 2] - pathsArray[(i2 * 8) + 6], 2.0d) + Math.pow(pathsArray[(i2 * 8) + 3] - pathsArray[(i2 * 8) + 7], 2.0d));
                    float x1 = pathsArray[(i2 * 8) + 6];
                    float y1 = pathsArray[(i2 * 8) + 7];
                    if (Float.compare(len1, ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0) {
                        x1 += (((pathsArray[(i2 * 8) + 2] - pathsArray[(i2 * 8) + 6]) / len1) * len1) / 7.0f;
                        y1 += (((pathsArray[(i2 * 8) + 3] - pathsArray[(i2 * 8) + 7]) / len1) * len1) / 7.0f;
                    }
                    cs.moveTo(x0, y0);
                    cs.lineTo(x1, y1);
                }
                cs.stroke();
                IOUtils.closeQuietly(cs);
            } catch (IOException ex) {
                LOG.error(ex);
                IOUtils.closeQuietly(cs);
            }
        } catch (Throwable th) {
            IOUtils.closeQuietly(cs);
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
