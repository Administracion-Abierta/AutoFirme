package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import com.aowagie.text.pdf.ColumnText;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
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
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/PDLinkAppearanceHandler.class */
public class PDLinkAppearanceHandler extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDLinkAppearanceHandler.class);

    public PDLinkAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDLinkAppearanceHandler(PDAnnotation annotation, PDDocument document) {
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
        PDBorderStyleDictionary borderStyleDic;
        PDAnnotationLink annotation = (PDAnnotationLink) getAnnotation();
        PDRectangle rect = annotation.getRectangle();
        if (rect == null) {
            return;
        }
        float lineWidth = getLineWidth();
        PDAppearanceContentStream contentStream = null;
        try {
            try {
                contentStream = getNormalAppearanceAsContentStream();
                PDColor color = annotation.getColor();
                if (color == null) {
                    color = new PDColor(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO}, PDDeviceGray.INSTANCE);
                }
                boolean hasStroke = contentStream.setStrokingColorOnDemand(color);
                contentStream.setBorderLine(lineWidth, annotation.getBorderStyle(), annotation.getBorder());
                PDRectangle borderEdge = getPaddedRectangle(getRectangle(), lineWidth / 2.0f);
                float[] pathsArray = annotation.getQuadPoints();
                if (pathsArray != null) {
                    for (int i = 0; i < pathsArray.length / 2; i++) {
                        if (!rect.contains(pathsArray[i * 2], pathsArray[(i * 2) + 1])) {
                            LOG.warn("At least one /QuadPoints entry (" + pathsArray[i * 2] + PolicyIdFilter.OID_SEPARATOR + pathsArray[(i * 2) + 1] + ") is outside of rectangle, " + rect + ", /QuadPoints are ignored and /Rect is used instead");
                            pathsArray = null;
                            break;
                        }
                    }
                }
                if (pathsArray == null) {
                    pathsArray = new float[]{borderEdge.getLowerLeftX(), borderEdge.getLowerLeftY(), borderEdge.getUpperRightX(), borderEdge.getLowerLeftY(), borderEdge.getUpperRightX(), borderEdge.getUpperRightY(), borderEdge.getLowerLeftX(), borderEdge.getUpperRightY()};
                }
                boolean underline = false;
                if (pathsArray.length >= 8 && (borderStyleDic = annotation.getBorderStyle()) != null) {
                    underline = PDBorderStyleDictionary.STYLE_UNDERLINE.equals(borderStyleDic.getStyle());
                }
                for (int of = 0; of + 7 < pathsArray.length; of += 8) {
                    contentStream.moveTo(pathsArray[of], pathsArray[of + 1]);
                    contentStream.lineTo(pathsArray[of + 2], pathsArray[of + 3]);
                    if (!underline) {
                        contentStream.lineTo(pathsArray[of + 4], pathsArray[of + 5]);
                        contentStream.lineTo(pathsArray[of + 6], pathsArray[of + 7]);
                        contentStream.closePath();
                    }
                }
                contentStream.drawShape(lineWidth, hasStroke, false);
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

    float getLineWidth() {
        PDAnnotationLink annotation = (PDAnnotationLink) getAnnotation();
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
