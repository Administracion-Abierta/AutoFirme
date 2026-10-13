package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/PDPolylineAppearanceHandler.class */
public class PDPolylineAppearanceHandler extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDPolylineAppearanceHandler.class);

    public PDPolylineAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDPolylineAppearanceHandler(PDAnnotation annotation, PDDocument document) {
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
        PDRectangle rect = annotation.getRectangle();
        float[] pathsArray = annotation.getVertices();
        if (pathsArray == null || pathsArray.length < 4) {
            return;
        }
        AnnotationBorder ab = AnnotationBorder.getAnnotationBorder(annotation, annotation.getBorderStyle());
        PDColor color = annotation.getColor();
        if (color == null || color.getComponents().length == 0 || Float.compare(ab.width, ColumnText.GLOBAL_SPACE_CHAR_RATIO) == 0) {
            return;
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
        rect.setLowerLeftX(Math.min(minX - (ab.width * 10.0f), rect.getLowerLeftX()));
        rect.setLowerLeftY(Math.min(minY - (ab.width * 10.0f), rect.getLowerLeftY()));
        rect.setUpperRightX(Math.max(maxX + (ab.width * 10.0f), rect.getUpperRightX()));
        rect.setUpperRightY(Math.max(maxY + (ab.width * 10.0f), rect.getUpperRightY()));
        annotation.setRectangle(rect);
        PDAppearanceContentStream cs = null;
        try {
            try {
                cs = getNormalAppearanceAsContentStream();
                boolean hasBackground = cs.setNonStrokingColorOnDemand(annotation.getInteriorColor());
                setOpacity(cs, annotation.getConstantOpacity());
                boolean hasStroke = cs.setStrokingColorOnDemand(color);
                if (ab.dashArray != null) {
                    cs.setLineDashPattern(ab.dashArray, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                }
                cs.setLineWidth(ab.width);
                for (int i2 = 0; i2 < pathsArray.length / 2; i2++) {
                    float x2 = pathsArray[i2 * 2];
                    float y2 = pathsArray[(i2 * 2) + 1];
                    if (i2 == 0) {
                        if (SHORT_STYLES.contains(annotation.getStartPointEndingStyle())) {
                            float x1 = pathsArray[2];
                            float y1 = pathsArray[3];
                            float len = (float) Math.sqrt(Math.pow(x2 - x1, 2.0d) + Math.pow(y2 - y1, 2.0d));
                            if (Float.compare(len, ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0) {
                                x2 += ((x1 - x2) / len) * ab.width;
                                y2 += ((y1 - y2) / len) * ab.width;
                            }
                        }
                        cs.moveTo(x2, y2);
                    } else {
                        if (i2 == (pathsArray.length / 2) - 1 && SHORT_STYLES.contains(annotation.getEndPointEndingStyle())) {
                            float x0 = pathsArray[pathsArray.length - 4];
                            float y0 = pathsArray[pathsArray.length - 3];
                            float len2 = (float) Math.sqrt(Math.pow(x0 - x2, 2.0d) + Math.pow(y0 - y2, 2.0d));
                            if (Float.compare(len2, ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0) {
                                x2 -= ((x2 - x0) / len2) * ab.width;
                                y2 -= ((y2 - y0) / len2) * ab.width;
                            }
                        }
                        cs.lineTo(x2, y2);
                    }
                }
                cs.stroke();
                if (!"None".equals(annotation.getStartPointEndingStyle())) {
                    float x3 = pathsArray[2];
                    float y3 = pathsArray[3];
                    float x4 = pathsArray[0];
                    float y4 = pathsArray[1];
                    cs.saveGraphicsState();
                    if (ANGLED_STYLES.contains(annotation.getStartPointEndingStyle())) {
                        double angle = Math.atan2(y3 - y4, x3 - x4);
                        cs.transform(Matrix.getRotateInstance(angle, x4, y4));
                    } else {
                        cs.transform(Matrix.getTranslateInstance(x4, y4));
                    }
                    drawStyle(annotation.getStartPointEndingStyle(), cs, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ab.width, hasStroke, hasBackground, false);
                    cs.restoreGraphicsState();
                }
                if (!"None".equals(annotation.getEndPointEndingStyle())) {
                    float x5 = pathsArray[pathsArray.length - 4];
                    float y5 = pathsArray[pathsArray.length - 3];
                    float x6 = pathsArray[pathsArray.length - 2];
                    float y6 = pathsArray[pathsArray.length - 1];
                    if (ANGLED_STYLES.contains(annotation.getEndPointEndingStyle())) {
                        double angle2 = Math.atan2(y6 - y5, x6 - x5);
                        cs.transform(Matrix.getRotateInstance(angle2, x6, y6));
                    } else {
                        cs.transform(Matrix.getTranslateInstance(x6, y6));
                    }
                    drawStyle(annotation.getEndPointEndingStyle(), cs, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ab.width, hasStroke, hasBackground, true);
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

    float getLineWidth() {
        PDAnnotationMarkup annotation = (PDAnnotationMarkup) getAnnotation();
        PDBorderStyleDictionary bs = annotation.getBorderStyle();
        if (bs != null) {
            return bs.getWidth();
        }
        COSArray borderCharacteristics = annotation.getBorder();
        if (borderCharacteristics.size() >= 3) {
            COSBase base = borderCharacteristics.getObject(2);
            if (base instanceof COSNumber) {
                return ((COSNumber) base).floatValue();
            }
            return 1.0f;
        }
        return 1.0f;
    }
}
