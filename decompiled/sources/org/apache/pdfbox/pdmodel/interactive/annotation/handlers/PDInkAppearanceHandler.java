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
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/PDInkAppearanceHandler.class */
public class PDInkAppearanceHandler extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDInkAppearanceHandler.class);

    public PDInkAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDInkAppearanceHandler(PDAnnotation annotation, PDDocument document) {
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
        PDAnnotationMarkup ink = (PDAnnotationMarkup) getAnnotation();
        PDColor color = ink.getColor();
        if (color == null || color.getComponents().length == 0) {
            return;
        }
        AnnotationBorder ab = AnnotationBorder.getAnnotationBorder(ink, ink.getBorderStyle());
        if (Float.compare(ab.width, ColumnText.GLOBAL_SPACE_CHAR_RATIO) == 0) {
            return;
        }
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        for (float[] pathArray : ink.getInkList()) {
            int nPoints = pathArray.length / 2;
            for (int i = 0; i < nPoints; i++) {
                float x = pathArray[i * 2];
                float y = pathArray[(i * 2) + 1];
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }
        PDRectangle rect = ink.getRectangle();
        rect.setLowerLeftX(Math.min(minX - (ab.width * 2.0f), rect.getLowerLeftX()));
        rect.setLowerLeftY(Math.min(minY - (ab.width * 2.0f), rect.getLowerLeftY()));
        rect.setUpperRightX(Math.max(maxX + (ab.width * 2.0f), rect.getUpperRightX()));
        rect.setUpperRightY(Math.max(maxY + (ab.width * 2.0f), rect.getUpperRightY()));
        ink.setRectangle(rect);
        PDAppearanceContentStream cs = null;
        try {
            try {
                cs = getNormalAppearanceAsContentStream();
                setOpacity(cs, ink.getConstantOpacity());
                cs.setStrokingColor(color);
                if (ab.dashArray != null) {
                    cs.setLineDashPattern(ab.dashArray, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                }
                cs.setLineWidth(ab.width);
                for (float[] pathArray2 : ink.getInkList()) {
                    int nPoints2 = pathArray2.length / 2;
                    for (int i2 = 0; i2 < nPoints2; i2++) {
                        float x2 = pathArray2[i2 * 2];
                        float y2 = pathArray2[(i2 * 2) + 1];
                        if (i2 == 0) {
                            cs.moveTo(x2, y2);
                        } else {
                            cs.lineTo(x2, y2);
                        }
                    }
                    cs.stroke();
                }
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
