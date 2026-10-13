package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import com.aowagie.text.pdf.ColumnText;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.util.Charsets;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderEffectDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.layout.AppearanceStyle;
import org.apache.pdfbox.pdmodel.interactive.annotation.layout.PlainText;
import org.apache.pdfbox.pdmodel.interactive.annotation.layout.PlainTextFormatter;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/PDFreeTextAppearanceHandler.class */
public class PDFreeTextAppearanceHandler extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDFreeTextAppearanceHandler.class);
    private static final Pattern COLOR_PATTERN = Pattern.compile(".*color\\:\\s*\\#([0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]).*");
    private float fontSize;
    private COSName fontName;

    public PDFreeTextAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
        this.fontSize = 10.0f;
        this.fontName = COSName.HELV;
    }

    public PDFreeTextAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
        this.fontSize = 10.0f;
        this.fontName = COSName.HELV;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateAppearanceStreams() {
        generateNormalAppearance();
        generateRolloverAppearance();
        generateDownAppearance();
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateNormalAppearance() {
        float[] pathsArray;
        PDRectangle borderBox;
        float xOffset;
        float yOffset;
        float clipY;
        PDResources defaultResources;
        PDFont defaultResourcesFont;
        PDAnnotationMarkup annotation = (PDAnnotationMarkup) getAnnotation();
        if (PDAnnotationMarkup.IT_FREE_TEXT_CALLOUT.equals(annotation.getIntent())) {
            pathsArray = annotation.getCallout();
            if (pathsArray == null || (pathsArray.length != 4 && pathsArray.length != 6)) {
                pathsArray = new float[0];
            }
        } else {
            pathsArray = new float[0];
        }
        AnnotationBorder ab = AnnotationBorder.getAnnotationBorder(annotation, annotation.getBorderStyle());
        try {
            try {
                PDAppearanceContentStream cs = getNormalAppearanceAsContentStream(true);
                boolean hasBackground = cs.setNonStrokingColorOnDemand(annotation.getColor());
                setOpacity(cs, annotation.getConstantOpacity());
                PDColor strokingColor = extractNonStrokingColor(annotation);
                boolean hasStroke = cs.setStrokingColorOnDemand(strokingColor);
                PDColor textColor = strokingColor;
                String defaultStyleString = annotation.getDefaultStyleString();
                if (defaultStyleString != null) {
                    Matcher m = COLOR_PATTERN.matcher(defaultStyleString);
                    if (m.find()) {
                        int color = Integer.parseInt(m.group(1), 16);
                        float r = ((color >> 16) & 255) / 255.0f;
                        float g = ((color >> 8) & 255) / 255.0f;
                        float b = (color & 255) / 255.0f;
                        textColor = new PDColor(new float[]{r, g, b}, PDDeviceRGB.INSTANCE);
                    }
                }
                if (ab.dashArray != null) {
                    cs.setLineDashPattern(ab.dashArray, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                }
                cs.setLineWidth(ab.width);
                for (int i = 0; i < pathsArray.length / 2; i++) {
                    float x = pathsArray[i * 2];
                    float y = pathsArray[(i * 2) + 1];
                    if (i == 0) {
                        if (SHORT_STYLES.contains(annotation.getLineEndingStyle())) {
                            float x1 = pathsArray[2];
                            float y1 = pathsArray[3];
                            float len = (float) Math.sqrt(Math.pow(x - x1, 2.0d) + Math.pow(y - y1, 2.0d));
                            if (Float.compare(len, ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0) {
                                x += ((x1 - x) / len) * ab.width;
                                y += ((y1 - y) / len) * ab.width;
                            }
                        }
                        cs.moveTo(x, y);
                    } else {
                        cs.lineTo(x, y);
                    }
                }
                if (pathsArray.length > 0) {
                    cs.stroke();
                }
                if (PDAnnotationMarkup.IT_FREE_TEXT_CALLOUT.equals(annotation.getIntent()) && !"None".equals(annotation.getLineEndingStyle()) && pathsArray.length >= 4) {
                    float x2 = pathsArray[2];
                    float y2 = pathsArray[3];
                    float x3 = pathsArray[0];
                    float y3 = pathsArray[1];
                    cs.saveGraphicsState();
                    if (ANGLED_STYLES.contains(annotation.getLineEndingStyle())) {
                        double angle = Math.atan2(y2 - y3, x2 - x3);
                        cs.transform(Matrix.getRotateInstance(angle, x3, y3));
                    } else {
                        cs.transform(Matrix.getTranslateInstance(x3, y3));
                    }
                    drawStyle(annotation.getLineEndingStyle(), cs, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ab.width, hasStroke, hasBackground, false);
                    cs.restoreGraphicsState();
                }
                PDBorderEffectDictionary borderEffect = annotation.getBorderEffect();
                if (borderEffect != null && borderEffect.getStyle().equals("C")) {
                    borderBox = applyRectDifferences(getRectangle(), annotation.getRectDifferences());
                    CloudyBorder cloudyBorder = new CloudyBorder(cs, borderEffect.getIntensity(), ab.width, getRectangle());
                    cloudyBorder.createCloudyRectangle(annotation.getRectDifference());
                    annotation.setRectangle(cloudyBorder.getRectangle());
                    annotation.setRectDifference(cloudyBorder.getRectDifference());
                    PDAppearanceStream appearanceStream = annotation.getNormalAppearanceStream();
                    appearanceStream.setBBox(cloudyBorder.getBBox());
                    appearanceStream.setMatrix(cloudyBorder.getMatrix());
                } else {
                    borderBox = applyRectDifferences(getRectangle(), annotation.getRectDifferences());
                    annotation.getNormalAppearanceStream().setBBox(borderBox);
                    PDRectangle paddedRectangle = getPaddedRectangle(borderBox, ab.width / 2.0f);
                    cs.addRect(paddedRectangle.getLowerLeftX(), paddedRectangle.getLowerLeftY(), paddedRectangle.getWidth(), paddedRectangle.getHeight());
                }
                cs.drawShape(ab.width, hasStroke, hasBackground);
                int rotation = annotation.getCOSObject().getInt(COSName.ROTATE, 0);
                cs.transform(Matrix.getRotateInstance(Math.toRadians(rotation), ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO));
                float width = (rotation == 90 || rotation == 270) ? borderBox.getHeight() : borderBox.getWidth();
                PDFont font = PDType1Font.HELVETICA;
                float clipWidth = width - (ab.width * 4.0f);
                float clipHeight = (rotation == 90 || rotation == 270) ? borderBox.getWidth() - (ab.width * 4.0f) : borderBox.getHeight() - (ab.width * 4.0f);
                extractFontDetails(annotation);
                if (this.document != null && this.document.getDocumentCatalog().getAcroForm() != null && (defaultResources = this.document.getDocumentCatalog().getAcroForm().getDefaultResources()) != null && (defaultResourcesFont = defaultResources.getFont(this.fontName)) != null) {
                    font = defaultResourcesFont;
                }
                switch (rotation) {
                    case 0:
                    default:
                        xOffset = borderBox.getLowerLeftX() + (ab.width * 2.0f);
                        yOffset = (borderBox.getUpperRightY() - (ab.width * 2.0f)) - (0.7896f * this.fontSize);
                        clipY = borderBox.getLowerLeftY() + (ab.width * 2.0f);
                        break;
                    case 90:
                        xOffset = borderBox.getLowerLeftY() + (ab.width * 2.0f);
                        yOffset = ((-borderBox.getLowerLeftX()) - (ab.width * 2.0f)) - (0.7896f * this.fontSize);
                        clipY = (-borderBox.getUpperRightX()) + (ab.width * 2.0f);
                        break;
                    case 180:
                        xOffset = (-borderBox.getUpperRightX()) + (ab.width * 2.0f);
                        yOffset = ((-borderBox.getLowerLeftY()) - (ab.width * 2.0f)) - (0.7896f * this.fontSize);
                        clipY = (-borderBox.getUpperRightY()) + (ab.width * 2.0f);
                        break;
                    case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                        xOffset = (-borderBox.getUpperRightY()) + (ab.width * 2.0f);
                        yOffset = (borderBox.getUpperRightX() - (ab.width * 2.0f)) - (0.7896f * this.fontSize);
                        clipY = borderBox.getLowerLeftX() + (ab.width * 2.0f);
                        break;
                }
                cs.addRect(xOffset, clipY, clipWidth, clipHeight);
                cs.clip();
                if (annotation.getContents() != null) {
                    cs.beginText();
                    cs.setFont(font, this.fontSize);
                    cs.setNonStrokingColor(textColor.getComponents());
                    AppearanceStyle appearanceStyle = new AppearanceStyle();
                    appearanceStyle.setFont(font);
                    appearanceStyle.setFontSize(this.fontSize);
                    PlainTextFormatter formatter = new PlainTextFormatter.Builder(cs).style(appearanceStyle).text(new PlainText(annotation.getContents())).width(width - (ab.width * 4.0f)).wrapLines(true).initialOffset(xOffset, yOffset).build();
                    try {
                        try {
                            formatter.format();
                            cs.endText();
                        } catch (IllegalArgumentException ex) {
                            throw new IOException(ex);
                        }
                    } catch (Throwable th) {
                        cs.endText();
                        throw th;
                    }
                }
                if (pathsArray.length > 0) {
                    PDRectangle rect = getRectangle();
                    float minX = Float.MAX_VALUE;
                    float minY = Float.MAX_VALUE;
                    float maxX = Float.MIN_VALUE;
                    float maxY = Float.MIN_VALUE;
                    for (int i2 = 0; i2 < pathsArray.length / 2; i2++) {
                        float x4 = pathsArray[i2 * 2];
                        float y4 = pathsArray[(i2 * 2) + 1];
                        minX = Math.min(minX, x4);
                        minY = Math.min(minY, y4);
                        maxX = Math.max(maxX, x4);
                        maxY = Math.max(maxY, y4);
                    }
                    rect.setLowerLeftX(Math.min(minX - (ab.width * 10.0f), rect.getLowerLeftX()));
                    rect.setLowerLeftY(Math.min(minY - (ab.width * 10.0f), rect.getLowerLeftY()));
                    rect.setUpperRightX(Math.max(maxX + (ab.width * 10.0f), rect.getUpperRightX()));
                    rect.setUpperRightY(Math.max(maxY + (ab.width * 10.0f), rect.getUpperRightY()));
                    annotation.setRectangle(rect);
                    annotation.getNormalAppearanceStream().setBBox(getRectangle());
                }
                IOUtils.closeQuietly(cs);
            } catch (Throwable th2) {
                IOUtils.closeQuietly(null);
                throw th2;
            }
        } catch (IOException ex2) {
            LOG.error(ex2);
            IOUtils.closeQuietly(null);
        }
    }

    private PDColor extractNonStrokingColor(PDAnnotationMarkup annotation) {
        PDColor strokingColor = new PDColor(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO}, PDDeviceGray.INSTANCE);
        String defaultAppearance = annotation.getDefaultAppearance();
        if (defaultAppearance == null) {
            return strokingColor;
        }
        try {
            PDFStreamParser parser = new PDFStreamParser(defaultAppearance.getBytes(Charsets.US_ASCII));
            COSArray arguments = new COSArray();
            COSArray colors = null;
            Operator graphicOp = null;
            for (Object token = parser.parseNextToken(); token != null; token = parser.parseNextToken()) {
                if (token instanceof Operator) {
                    Operator op = (Operator) token;
                    String name = op.getName();
                    if (OperatorName.NON_STROKING_GRAY.equals(name) || OperatorName.NON_STROKING_RGB.equals(name) || OperatorName.NON_STROKING_CMYK.equals(name)) {
                        graphicOp = op;
                        colors = arguments;
                    }
                    arguments = new COSArray();
                } else {
                    arguments.add((COSBase) token);
                }
            }
            if (graphicOp != null) {
                String graphicOpName = graphicOp.getName();
                if (OperatorName.NON_STROKING_GRAY.equals(graphicOpName)) {
                    strokingColor = new PDColor(colors, PDDeviceGray.INSTANCE);
                } else if (OperatorName.NON_STROKING_RGB.equals(graphicOpName)) {
                    strokingColor = new PDColor(colors, PDDeviceRGB.INSTANCE);
                } else if (OperatorName.NON_STROKING_CMYK.equals(graphicOpName)) {
                    strokingColor = new PDColor(colors, PDDeviceCMYK.INSTANCE);
                }
            }
        } catch (IOException ex) {
            LOG.warn("Problem parsing /DA, will use default black", ex);
        }
        return strokingColor;
    }

    private void extractFontDetails(PDAnnotationMarkup annotation) {
        String defaultAppearance = annotation.getDefaultAppearance();
        if (defaultAppearance == null && this.document != null && this.document.getDocumentCatalog().getAcroForm() != null) {
            defaultAppearance = this.document.getDocumentCatalog().getAcroForm().getDefaultAppearance();
        }
        if (defaultAppearance == null) {
            return;
        }
        try {
            PDFStreamParser parser = new PDFStreamParser(defaultAppearance.getBytes(Charsets.US_ASCII));
            COSArray arguments = new COSArray();
            COSArray fontArguments = new COSArray();
            for (Object token = parser.parseNextToken(); token != null; token = parser.parseNextToken()) {
                if (token instanceof Operator) {
                    Operator op = (Operator) token;
                    String name = op.getName();
                    if (OperatorName.SET_FONT_AND_SIZE.equals(name)) {
                        fontArguments = arguments;
                    }
                    arguments = new COSArray();
                } else {
                    arguments.add((COSBase) token);
                }
            }
            if (fontArguments.size() >= 2) {
                COSBase base = fontArguments.get(0);
                if (base instanceof COSName) {
                    this.fontName = (COSName) base;
                }
                COSBase base2 = fontArguments.get(1);
                if (base2 instanceof COSNumber) {
                    this.fontSize = ((COSNumber) base2).floatValue();
                }
            }
        } catch (IOException ex) {
            LOG.warn("Problem parsing /DA, will use default 'Helv 10'", ex);
        }
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateRolloverAppearance() {
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler
    public void generateDownAppearance() {
    }
}
