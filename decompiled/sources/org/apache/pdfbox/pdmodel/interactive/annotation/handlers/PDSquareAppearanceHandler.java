package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

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
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationSquareCircle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderEffectDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/PDSquareAppearanceHandler.class */
public class PDSquareAppearanceHandler extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDSquareAppearanceHandler.class);

    public PDSquareAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDSquareAppearanceHandler(PDAnnotation annotation, PDDocument document) {
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
        float lineWidth = getLineWidth();
        PDAnnotationSquareCircle annotation = (PDAnnotationSquareCircle) getAnnotation();
        PDAppearanceContentStream contentStream = null;
        try {
            try {
                contentStream = getNormalAppearanceAsContentStream();
                boolean hasStroke = contentStream.setStrokingColorOnDemand(getColor());
                boolean hasBackground = contentStream.setNonStrokingColorOnDemand(annotation.getInteriorColor());
                setOpacity(contentStream, annotation.getConstantOpacity());
                contentStream.setBorderLine(lineWidth, annotation.getBorderStyle(), annotation.getBorder());
                PDBorderEffectDictionary borderEffect = annotation.getBorderEffect();
                if (borderEffect != null && borderEffect.getStyle().equals("C")) {
                    CloudyBorder cloudyBorder = new CloudyBorder(contentStream, borderEffect.getIntensity(), lineWidth, getRectangle());
                    cloudyBorder.createCloudyRectangle(annotation.getRectDifference());
                    annotation.setRectangle(cloudyBorder.getRectangle());
                    annotation.setRectDifference(cloudyBorder.getRectDifference());
                    PDAppearanceStream appearanceStream = annotation.getNormalAppearanceStream();
                    appearanceStream.setBBox(cloudyBorder.getBBox());
                    appearanceStream.setMatrix(cloudyBorder.getMatrix());
                } else {
                    PDRectangle borderBox = handleBorderBox(annotation, lineWidth);
                    contentStream.addRect(borderBox.getLowerLeftX(), borderBox.getLowerLeftY(), borderBox.getWidth(), borderBox.getHeight());
                }
                contentStream.drawShape(lineWidth, hasStroke, hasBackground);
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
