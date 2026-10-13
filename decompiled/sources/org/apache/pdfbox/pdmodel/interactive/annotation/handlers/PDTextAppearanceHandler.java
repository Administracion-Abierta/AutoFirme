package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import com.aowagie.text.ElementTags;
import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationText;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/PDTextAppearanceHandler.class */
public class PDTextAppearanceHandler extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDTextAppearanceHandler.class);
    private static final Set<String> SUPPORTED_NAMES = new HashSet();

    static {
        SUPPORTED_NAMES.add("Note");
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_INSERT);
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_CROSS);
        SUPPORTED_NAMES.add("Help");
        SUPPORTED_NAMES.add("Circle");
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_PARAGRAPH);
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_NEW_PARAGRAPH);
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_CHECK);
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_STAR);
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_RIGHT_ARROW);
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_RIGHT_POINTER);
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_CROSS_HAIRS);
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_UP_ARROW);
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_UP_LEFT_ARROW);
        SUPPORTED_NAMES.add("Comment");
        SUPPORTED_NAMES.add(PDAnnotationText.NAME_KEY);
    }

    public PDTextAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDTextAppearanceHandler(PDAnnotation annotation, PDDocument document) {
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
        PDAnnotationText annotation = (PDAnnotationText) getAnnotation();
        if (!SUPPORTED_NAMES.contains(annotation.getName())) {
            return;
        }
        PDAppearanceContentStream contentStream = null;
        try {
            contentStream = getNormalAppearanceAsContentStream();
            PDColor bgColor = getColor();
            if (bgColor == null) {
                contentStream.setNonStrokingColor(1.0f);
            } else {
                contentStream.setNonStrokingColor(bgColor);
            }
            setOpacity(contentStream, annotation.getConstantOpacity());
            String annotationTypeName = annotation.getName();
            if ("Note".equals(annotationTypeName)) {
                drawNote(annotation, contentStream);
            } else if (PDAnnotationText.NAME_CROSS.equals(annotationTypeName)) {
                drawCross(annotation, contentStream);
            } else if ("Circle".equals(annotationTypeName)) {
                drawCircles(annotation, contentStream);
            } else if (PDAnnotationText.NAME_INSERT.equals(annotationTypeName)) {
                drawInsert(annotation, contentStream);
            } else if ("Help".equals(annotationTypeName)) {
                drawHelp(annotation, contentStream);
            } else if (PDAnnotationText.NAME_PARAGRAPH.equals(annotationTypeName)) {
                drawParagraph(annotation, contentStream);
            } else if (PDAnnotationText.NAME_NEW_PARAGRAPH.equals(annotationTypeName)) {
                drawNewParagraph(annotation, contentStream);
            } else if (PDAnnotationText.NAME_STAR.equals(annotationTypeName)) {
                drawStar(annotation, contentStream);
            } else if (PDAnnotationText.NAME_CHECK.equals(annotationTypeName)) {
                drawCheck(annotation, contentStream);
            } else if (PDAnnotationText.NAME_RIGHT_ARROW.equals(annotationTypeName)) {
                drawRightArrow(annotation, contentStream);
            } else if (PDAnnotationText.NAME_RIGHT_POINTER.equals(annotationTypeName)) {
                drawRightPointer(annotation, contentStream);
            } else if (PDAnnotationText.NAME_CROSS_HAIRS.equals(annotationTypeName)) {
                drawCrossHairs(annotation, contentStream);
            } else if (PDAnnotationText.NAME_UP_ARROW.equals(annotationTypeName)) {
                drawUpArrow(annotation, contentStream);
            } else if (PDAnnotationText.NAME_UP_LEFT_ARROW.equals(annotationTypeName)) {
                drawUpLeftArrow(annotation, contentStream);
            } else if ("Comment".equals(annotationTypeName)) {
                drawComment(annotation, contentStream);
            } else if (PDAnnotationText.NAME_KEY.equals(annotationTypeName)) {
                drawKey(annotation, contentStream);
            }
        } catch (IOException e) {
            LOG.error(e);
        } finally {
            IOUtils.closeQuietly(contentStream);
        }
    }

    private PDRectangle adjustRectAndBBox(PDAnnotationText annotation, float width, float height) {
        PDRectangle rect = getRectangle();
        if (!annotation.isNoZoom()) {
            rect.setUpperRightX(rect.getLowerLeftX() + width);
            rect.setLowerLeftY(rect.getUpperRightY() - height);
            annotation.setRectangle(rect);
        }
        if (!annotation.getCOSObject().containsKey(COSName.F)) {
            annotation.setNoRotate(true);
            annotation.setNoZoom(true);
        }
        PDRectangle bbox = new PDRectangle(width, height);
        annotation.getNormalAppearanceStream().setBBox(bbox);
        return bbox;
    }

    private void drawNote(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 18.0f, 20.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.61f);
        contentStream.addRect(1.0f, 1.0f, bbox.getWidth() - 2.0f, bbox.getHeight() - 2.0f);
        contentStream.moveTo(bbox.getWidth() / 4.0f, (bbox.getHeight() / 7.0f) * 2.0f);
        contentStream.lineTo(((bbox.getWidth() * 3.0f) / 4.0f) - 1.0f, (bbox.getHeight() / 7.0f) * 2.0f);
        contentStream.moveTo(bbox.getWidth() / 4.0f, (bbox.getHeight() / 7.0f) * 3.0f);
        contentStream.lineTo(((bbox.getWidth() * 3.0f) / 4.0f) - 1.0f, (bbox.getHeight() / 7.0f) * 3.0f);
        contentStream.moveTo(bbox.getWidth() / 4.0f, (bbox.getHeight() / 7.0f) * 4.0f);
        contentStream.lineTo(((bbox.getWidth() * 3.0f) / 4.0f) - 1.0f, (bbox.getHeight() / 7.0f) * 4.0f);
        contentStream.moveTo(bbox.getWidth() / 4.0f, (bbox.getHeight() / 7.0f) * 5.0f);
        contentStream.lineTo(((bbox.getWidth() * 3.0f) / 4.0f) - 1.0f, (bbox.getHeight() / 7.0f) * 5.0f);
        contentStream.fillAndStroke();
    }

    private void drawCircles(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 20.0f, 20.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.saveGraphicsState();
        contentStream.setLineWidth(1.0f);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setAlphaSourceFlag(false);
        gs.setStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setNonStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(1.0f);
        drawCircle(contentStream, bbox.getWidth() / 2.0f, bbox.getHeight() / 2.0f, 6.36f);
        contentStream.fill();
        contentStream.restoreGraphicsState();
        contentStream.setLineWidth(0.59f);
        drawCircle(contentStream, bbox.getWidth() / 2.0f, bbox.getHeight() / 2.0f, 6.36f);
        drawCircle2(contentStream, bbox.getWidth() / 2.0f, bbox.getHeight() / 2.0f, 9.756f);
        contentStream.fillAndStroke();
    }

    private void drawInsert(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 17.0f, 20.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(0);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.moveTo((bbox.getWidth() / 2.0f) - 1.0f, bbox.getHeight() - 2.0f);
        contentStream.lineTo(1.0f, 1.0f);
        contentStream.lineTo(bbox.getWidth() - 2.0f, 1.0f);
        contentStream.closeAndFillAndStroke();
    }

    private void drawCross(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 19.0f, 19.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        float small = min / 10.0f;
        float large = min / 5.0f;
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.moveTo(small, large);
        contentStream.lineTo(large, small);
        contentStream.lineTo(min / 2.0f, (min / 2.0f) - small);
        contentStream.lineTo(min - large, small);
        contentStream.lineTo(min - small, large);
        contentStream.lineTo((min / 2.0f) + small, min / 2.0f);
        contentStream.lineTo(min - small, min - large);
        contentStream.lineTo(min - large, min - small);
        contentStream.lineTo(min / 2.0f, (min / 2.0f) + small);
        contentStream.lineTo(large, min - small);
        contentStream.lineTo(small, min - large);
        contentStream.lineTo((min / 2.0f) - small, min / 2.0f);
        contentStream.closeAndFillAndStroke();
    }

    private void drawHelp(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 20.0f, 20.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.saveGraphicsState();
        contentStream.setLineWidth(1.0f);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setAlphaSourceFlag(false);
        gs.setStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setNonStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(1.0f);
        drawCircle2(contentStream, min / 2.0f, min / 2.0f, (min / 2.0f) - 1.0f);
        contentStream.fill();
        contentStream.restoreGraphicsState();
        contentStream.saveGraphicsState();
        contentStream.transform(Matrix.getScaleInstance((0.001f * min) / 2.25f, (0.001f * min) / 2.25f));
        contentStream.transform(Matrix.getTranslateInstance(500.0f, 375.0f));
        GeneralPath path = PDType1Font.HELVETICA_BOLD.getPath("question");
        addPath(contentStream, path);
        contentStream.restoreGraphicsState();
        drawCircle2(contentStream, min / 2.0f, min / 2.0f, (min / 2.0f) - 1.0f);
        contentStream.fillAndStroke();
    }

    private void drawParagraph(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 20.0f, 20.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.saveGraphicsState();
        contentStream.setLineWidth(1.0f);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setAlphaSourceFlag(false);
        gs.setStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setNonStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(1.0f);
        drawCircle2(contentStream, min / 2.0f, min / 2.0f, (min / 2.0f) - 1.0f);
        contentStream.fill();
        contentStream.restoreGraphicsState();
        contentStream.saveGraphicsState();
        contentStream.transform(Matrix.getScaleInstance((0.001f * min) / 3.0f, (0.001f * min) / 3.0f));
        contentStream.transform(Matrix.getTranslateInstance(850.0f, 900.0f));
        GeneralPath path = PDType1Font.HELVETICA.getPath(ElementTags.PARAGRAPH);
        addPath(contentStream, path);
        contentStream.restoreGraphicsState();
        contentStream.fillAndStroke();
        drawCircle(contentStream, min / 2.0f, min / 2.0f, (min / 2.0f) - 1.0f);
        contentStream.stroke();
    }

    private void drawNewParagraph(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        adjustRectAndBBox(annotation, 13.0f, 20.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(0);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.moveTo(6.4995f, 20.0f);
        contentStream.lineTo(0.295f, 7.287f);
        contentStream.lineTo(12.705f, 7.287f);
        contentStream.closeAndFillAndStroke();
        contentStream.transform(Matrix.getScaleInstance(0.004f, 0.004f));
        contentStream.transform(Matrix.getTranslateInstance(200.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO));
        addPath(contentStream, PDType1Font.HELVETICA_BOLD.getPath("N"));
        contentStream.transform(Matrix.getTranslateInstance(1300.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO));
        addPath(contentStream, PDType1Font.HELVETICA_BOLD.getPath("P"));
        contentStream.fill();
    }

    private void drawStar(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 20.0f, 19.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.transform(Matrix.getScaleInstance((0.001f * min) / 0.8f, (0.001f * min) / 0.8f));
        GeneralPath path = PDType1Font.ZAPF_DINGBATS.getPath("a35");
        addPath(contentStream, path);
        contentStream.fillAndStroke();
    }

    private void drawCheck(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 20.0f, 19.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.transform(Matrix.getScaleInstance((0.001f * min) / 0.8f, (0.001f * min) / 0.8f));
        contentStream.transform(Matrix.getTranslateInstance(ColumnText.GLOBAL_SPACE_CHAR_RATIO, 50.0f));
        GeneralPath path = PDType1Font.ZAPF_DINGBATS.getPath("a20");
        addPath(contentStream, path);
        contentStream.fillAndStroke();
    }

    private void drawRightPointer(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 20.0f, 17.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.transform(Matrix.getScaleInstance((0.001f * min) / 0.8f, (0.001f * min) / 0.8f));
        contentStream.transform(Matrix.getTranslateInstance(ColumnText.GLOBAL_SPACE_CHAR_RATIO, 50.0f));
        GeneralPath path = PDType1Font.ZAPF_DINGBATS.getPath("a174");
        addPath(contentStream, path);
        contentStream.fillAndStroke();
    }

    private void drawCrossHairs(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 20.0f, 20.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(0);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.61f);
        contentStream.transform(Matrix.getScaleInstance((0.001f * min) / 1.5f, (0.001f * min) / 1.5f));
        contentStream.transform(Matrix.getTranslateInstance(ColumnText.GLOBAL_SPACE_CHAR_RATIO, 50.0f));
        GeneralPath path = PDType1Font.SYMBOL.getPath("circleplus");
        addPath(contentStream, path);
        contentStream.fillAndStroke();
    }

    private void drawUpArrow(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        adjustRectAndBBox(annotation, 17.0f, 20.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.moveTo(1.0f, 7.0f);
        contentStream.lineTo(5.0f, 7.0f);
        contentStream.lineTo(5.0f, 1.0f);
        contentStream.lineTo(12.0f, 1.0f);
        contentStream.lineTo(12.0f, 7.0f);
        contentStream.lineTo(16.0f, 7.0f);
        contentStream.lineTo(8.5f, 19.0f);
        contentStream.closeAndFillAndStroke();
    }

    private void drawUpLeftArrow(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        adjustRectAndBBox(annotation, 17.0f, 17.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.transform(Matrix.getRotateInstance(Math.toRadians(45.0d), 8.0f, -4.0f));
        contentStream.moveTo(1.0f, 7.0f);
        contentStream.lineTo(5.0f, 7.0f);
        contentStream.lineTo(5.0f, 1.0f);
        contentStream.lineTo(12.0f, 1.0f);
        contentStream.lineTo(12.0f, 7.0f);
        contentStream.lineTo(16.0f, 7.0f);
        contentStream.lineTo(8.5f, 19.0f);
        contentStream.closeAndFillAndStroke();
    }

    private void drawRightArrow(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = adjustRectAndBBox(annotation, 20.0f, 20.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.saveGraphicsState();
        contentStream.setLineWidth(1.0f);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setAlphaSourceFlag(false);
        gs.setStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setNonStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(1.0f);
        drawCircle2(contentStream, min / 2.0f, min / 2.0f, (min / 2.0f) - 1.0f);
        contentStream.fill();
        contentStream.restoreGraphicsState();
        contentStream.saveGraphicsState();
        contentStream.transform(Matrix.getScaleInstance((0.001f * min) / 1.3f, (0.001f * min) / 1.3f));
        contentStream.transform(Matrix.getTranslateInstance(200.0f, 300.0f));
        GeneralPath path = PDType1Font.ZAPF_DINGBATS.getPath("a160");
        addPath(contentStream, path);
        contentStream.restoreGraphicsState();
        drawCircle(contentStream, min / 2.0f, min / 2.0f, (min / 2.0f) - 1.0f);
        contentStream.fillAndStroke();
    }

    private void drawComment(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        adjustRectAndBBox(annotation, 18.0f, 18.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(200.0f);
        contentStream.saveGraphicsState();
        contentStream.setLineWidth(1.0f);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setAlphaSourceFlag(false);
        gs.setStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setNonStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(1.0f);
        contentStream.addRect(0.3f, 0.3f, 17.4f, 17.4f);
        contentStream.fill();
        contentStream.restoreGraphicsState();
        contentStream.transform(Matrix.getScaleInstance(0.003f, 0.003f));
        contentStream.transform(Matrix.getTranslateInstance(500.0f, -300.0f));
        contentStream.moveTo(2549.0f, 5269.0f);
        contentStream.curveTo(1307.0f, 5269.0f, 300.0f, 4451.0f, 300.0f, 3441.0f);
        contentStream.curveTo(300.0f, 3023.0f, 474.0f, 2640.0f, 764.0f, 2331.0f);
        contentStream.curveTo(633.0f, 1985.0f, 361.0f, 1691.0f, 357.0f, 1688.0f);
        contentStream.curveTo(299.0f, 1626.0f, 283.0f, 1537.0f, 316.0f, 1459.0f);
        contentStream.curveTo(350.0f, 1382.0f, 426.0f, 1332.0f, 510.0f, 1332.0f);
        contentStream.curveTo(1051.0f, 1332.0f, 1477.0f, 1558.0f, 1733.0f, 1739.0f);
        contentStream.curveTo(1987.0f, 1659.0f, 2261.0f, 1613.0f, 2549.0f, 1613.0f);
        contentStream.curveTo(3792.0f, 1613.0f, 4799.0f, 2431.0f, 4799.0f, 3441.0f);
        contentStream.curveTo(4799.0f, 4451.0f, 3792.0f, 5269.0f, 2549.0f, 5269.0f);
        contentStream.closePath();
        contentStream.moveTo(-400.0f, 400.0f);
        contentStream.lineTo(-400.0f, 6200.0f);
        contentStream.lineTo(5400.0f, 6200.0f);
        contentStream.lineTo(5400.0f, 400.0f);
        contentStream.closeAndFillAndStroke();
    }

    private void drawKey(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        adjustRectAndBBox(annotation, 13.0f, 18.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(200.0f);
        contentStream.transform(Matrix.getScaleInstance(0.003f, 0.003f));
        contentStream.transform(Matrix.getRotateInstance(Math.toRadians(45.0d), 2500.0f, -800.0f));
        contentStream.moveTo(4799.0f, 4004.0f);
        contentStream.curveTo(4799.0f, 3149.0f, 4107.0f, 2457.0f, 3253.0f, 2457.0f);
        contentStream.curveTo(3154.0f, 2457.0f, 3058.0f, 2466.0f, 2964.0f, 2484.0f);
        contentStream.lineTo(2753.0f, 2246.0f);
        contentStream.curveTo(2713.0f, 2201.0f, 2656.0f, 2175.0f, 2595.0f, 2175.0f);
        contentStream.lineTo(2268.0f, 2175.0f);
        contentStream.lineTo(2268.0f, 1824.0f);
        contentStream.curveTo(2268.0f, 1707.0f, 2174.0f, 1613.0f, 2057.0f, 1613.0f);
        contentStream.lineTo(1706.0f, 1613.0f);
        contentStream.lineTo(1706.0f, 1261.0f);
        contentStream.curveTo(1706.0f, 1145.0f, 1611.0f, 1050.0f, 1495.0f, 1050.0f);
        contentStream.lineTo(510.0f, 1050.0f);
        contentStream.curveTo(394.0f, 1050.0f, 300.0f, 1145.0f, 300.0f, 1261.0f);
        contentStream.lineTo(300.0f, 1947.0f);
        contentStream.curveTo(300.0f, 2003.0f, 322.0f, 2057.0f, 361.0f, 2097.0f);
        contentStream.lineTo(1783.0f, 3519.0f);
        contentStream.curveTo(1733.0f, 3671.0f, 1706.0f, 3834.0f, 1706.0f, 4004.0f);
        contentStream.curveTo(1706.0f, 4858.0f, 2398.0f, 5550.0f, 3253.0f, 5550.0f);
        contentStream.curveTo(4109.0f, 5550.0f, 4799.0f, 4860.0f, 4799.0f, 4004.0f);
        contentStream.closePath();
        contentStream.moveTo(3253.0f, 4425.0f);
        contentStream.curveTo(3253.0f, 4192.0f, 3441.0f, 4004.0f, 3674.0f, 4004.0f);
        contentStream.curveTo(3907.0f, 4004.0f, 4096.0f, 4192.0f, 4096.0f, 4425.0f);
        contentStream.curveTo(4096.0f, 4658.0f, 3907.0f, 4847.0f, 3674.0f, 4847.0f);
        contentStream.curveTo(3441.0f, 4847.0f, 3253.0f, 4658.0f, 3253.0f, 4425.0f);
        contentStream.fillAndStroke();
    }

    private void addPath(PDAppearanceContentStream contentStream, GeneralPath path) throws IOException {
        double curX = 0.0d;
        double curY = 0.0d;
        PathIterator it = path.getPathIterator(new AffineTransform());
        double[] coords = new double[6];
        while (!it.isDone()) {
            int type = it.currentSegment(coords);
            switch (type) {
                case 0:
                    contentStream.moveTo((float) coords[0], (float) coords[1]);
                    curX = coords[0];
                    curY = coords[1];
                    break;
                case 1:
                    contentStream.lineTo((float) coords[0], (float) coords[1]);
                    curX = coords[0];
                    curY = coords[1];
                    break;
                case 2:
                    double cp1x = curX + (0.6666666666666666d * (coords[0] - curX));
                    double cp1y = curY + (0.6666666666666666d * (coords[1] - curY));
                    double cp2x = coords[2] + (0.6666666666666666d * (coords[0] - coords[2]));
                    double cp2y = coords[3] + (0.6666666666666666d * (coords[1] - coords[3]));
                    contentStream.curveTo((float) cp1x, (float) cp1y, (float) cp2x, (float) cp2y, (float) coords[2], (float) coords[3]);
                    curX = coords[2];
                    curY = coords[3];
                    break;
                case 3:
                    contentStream.curveTo((float) coords[0], (float) coords[1], (float) coords[2], (float) coords[3], (float) coords[4], (float) coords[5]);
                    curX = coords[4];
                    curY = coords[5];
                    break;
                case 4:
                    contentStream.closePath();
                    break;
            }
            it.next();
        }
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateRolloverAppearance() {
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateDownAppearance() {
    }
}
