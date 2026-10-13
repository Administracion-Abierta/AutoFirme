package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLine;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/PDLineAppearanceHandler.class */
public class PDLineAppearanceHandler extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDLineAppearanceHandler.class);
    static final int FONT_SIZE = 9;

    public PDLineAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDLineAppearanceHandler(PDAnnotation annotation, PDDocument document) {
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
        float yOffset;
        PDAnnotationLine annotation = (PDAnnotationLine) getAnnotation();
        PDRectangle rect = annotation.getRectangle();
        float[] pathsArray = annotation.getLine();
        if (pathsArray == null) {
            return;
        }
        AnnotationBorder ab = AnnotationBorder.getAnnotationBorder(annotation, annotation.getBorderStyle());
        PDColor color = annotation.getColor();
        if (color == null || color.getComponents().length == 0) {
            return;
        }
        float ll = annotation.getLeaderLineLength();
        float lle = annotation.getLeaderLineExtensionLength();
        float llo = annotation.getLeaderLineOffsetLength();
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
        if (ll < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            llo = -llo;
            lle = -lle;
        }
        float lineEndingSize = ((double) ab.width) < 1.0E-5d ? 1.0f : ab.width;
        rect.setLowerLeftX(Math.min(minX - Math.max(lineEndingSize * 10.0f, Math.abs((llo + ll) + lle)), rect.getLowerLeftX()));
        rect.setLowerLeftY(Math.min(minY - Math.max(lineEndingSize * 10.0f, Math.abs((llo + ll) + lle)), rect.getLowerLeftY()));
        rect.setUpperRightX(Math.max(maxX + Math.max(lineEndingSize * 10.0f, Math.abs(llo + ll + lle)), rect.getUpperRightX()));
        rect.setUpperRightY(Math.max(maxY + Math.max(lineEndingSize * 10.0f, Math.abs(llo + ll + lle)), rect.getUpperRightY()));
        annotation.setRectangle(rect);
        PDAppearanceContentStream cs = null;
        try {
            cs = getNormalAppearanceAsContentStream();
            setOpacity(cs, annotation.getConstantOpacity());
            boolean hasStroke = cs.setStrokingColorOnDemand(color);
            if (ab.dashArray != null) {
                cs.setLineDashPattern(ab.dashArray, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            }
            cs.setLineWidth(ab.width);
            float x1 = pathsArray[0];
            float y1 = pathsArray[1];
            float x2 = pathsArray[2];
            float y2 = pathsArray[3];
            float y3 = llo + ll;
            String contents = annotation.getContents();
            if (contents == null) {
                contents = "";
            }
            cs.saveGraphicsState();
            double angle = Math.atan2(y2 - y1, x2 - x1);
            cs.transform(Matrix.getRotateInstance(angle, x1, y1));
            float lineLength = (float) Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
            cs.moveTo(ColumnText.GLOBAL_SPACE_CHAR_RATIO, llo);
            cs.lineTo(ColumnText.GLOBAL_SPACE_CHAR_RATIO, llo + ll + lle);
            cs.moveTo(lineLength, llo);
            cs.lineTo(lineLength, llo + ll + lle);
            if (annotation.getCaption() && !contents.isEmpty()) {
                PDType1Font font = PDType1Font.HELVETICA;
                float contentLength = 0.0f;
                try {
                    contentLength = (font.getStringWidth(annotation.getContents()) / 1000.0f) * 9.0f;
                } catch (IllegalArgumentException ex) {
                    LOG.error("line text '" + annotation.getContents() + "' can't be shown", ex);
                }
                float xOffset = (lineLength - contentLength) / 2.0f;
                String captionPositioning = annotation.getCaptionPositioning();
                if (SHORT_STYLES.contains(annotation.getStartPointEndingStyle())) {
                    cs.moveTo(lineEndingSize, y3);
                } else {
                    cs.moveTo(ColumnText.GLOBAL_SPACE_CHAR_RATIO, y3);
                }
                if ("Top".equals(captionPositioning)) {
                    yOffset = 1.908f;
                } else {
                    yOffset = -2.6f;
                    cs.lineTo(xOffset - lineEndingSize, y3);
                    cs.moveTo((lineLength - xOffset) + lineEndingSize, y3);
                }
                if (SHORT_STYLES.contains(annotation.getEndPointEndingStyle())) {
                    cs.lineTo(lineLength - lineEndingSize, y3);
                } else {
                    cs.lineTo(lineLength, y3);
                }
                cs.drawShape(lineEndingSize, hasStroke, false);
                float captionHorizontalOffset = annotation.getCaptionHorizontalOffset();
                float captionVerticalOffset = annotation.getCaptionVerticalOffset();
                if (contentLength > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    cs.beginText();
                    cs.setFont(font, 9.0f);
                    cs.newLineAtOffset(xOffset + captionHorizontalOffset, y3 + yOffset + captionVerticalOffset);
                    cs.showText(annotation.getContents());
                    cs.endText();
                }
                if (Float.compare(captionVerticalOffset, ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0) {
                    cs.moveTo(ColumnText.GLOBAL_SPACE_CHAR_RATIO + (lineLength / 2.0f), y3);
                    cs.lineTo(ColumnText.GLOBAL_SPACE_CHAR_RATIO + (lineLength / 2.0f), y3 + captionVerticalOffset);
                    cs.drawShape(lineEndingSize, hasStroke, false);
                }
            } else {
                if (SHORT_STYLES.contains(annotation.getStartPointEndingStyle())) {
                    cs.moveTo(lineEndingSize, y3);
                } else {
                    cs.moveTo(ColumnText.GLOBAL_SPACE_CHAR_RATIO, y3);
                }
                if (SHORT_STYLES.contains(annotation.getEndPointEndingStyle())) {
                    cs.lineTo(lineLength - lineEndingSize, y3);
                } else {
                    cs.lineTo(lineLength, y3);
                }
                cs.drawShape(lineEndingSize, hasStroke, false);
            }
            cs.restoreGraphicsState();
            boolean hasBackground = cs.setNonStrokingColorOnDemand(annotation.getInteriorColor());
            if (ab.width < 1.0E-5d) {
                hasStroke = false;
            }
            if (!"None".equals(annotation.getStartPointEndingStyle())) {
                cs.saveGraphicsState();
                if (ANGLED_STYLES.contains(annotation.getStartPointEndingStyle())) {
                    cs.transform(Matrix.getRotateInstance(angle, x1, y1));
                    drawStyle(annotation.getStartPointEndingStyle(), cs, ColumnText.GLOBAL_SPACE_CHAR_RATIO, y3, lineEndingSize, hasStroke, hasBackground, false);
                } else {
                    float xx1 = x1 - ((float) (((double) y3) * Math.sin(angle)));
                    float yy1 = y1 + ((float) (((double) y3) * Math.cos(angle)));
                    drawStyle(annotation.getStartPointEndingStyle(), cs, xx1, yy1, lineEndingSize, hasStroke, hasBackground, false);
                }
                cs.restoreGraphicsState();
            }
            if (!"None".equals(annotation.getEndPointEndingStyle())) {
                if (ANGLED_STYLES.contains(annotation.getEndPointEndingStyle())) {
                    cs.transform(Matrix.getRotateInstance(angle, x2, y2));
                    drawStyle(annotation.getEndPointEndingStyle(), cs, ColumnText.GLOBAL_SPACE_CHAR_RATIO, y3, lineEndingSize, hasStroke, hasBackground, true);
                } else {
                    float xx2 = x2 - ((float) (((double) y3) * Math.sin(angle)));
                    float yy2 = y2 + ((float) (((double) y3) * Math.cos(angle)));
                    drawStyle(annotation.getEndPointEndingStyle(), cs, xx2, yy2, lineEndingSize, hasStroke, hasBackground, true);
                }
            }
        } catch (IOException ex2) {
            LOG.error(ex2);
        } finally {
            IOUtils.closeQuietly(cs);
        }
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateRolloverAppearance() {
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateDownAppearance() {
    }
}
