package org.apache.pdfbox.printing;

import com.aowagie.text.pdf.ColumnText;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterIOException;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.RenderDestination;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/printing/PDFPrintable.class */
public final class PDFPrintable implements Printable {
    private final PDPageTree pageTree;
    private final PDFRenderer renderer;
    private final boolean showPageBorder;
    private final Scaling scaling;
    private final float dpi;
    private final boolean center;
    private boolean subsamplingAllowed;
    private RenderingHints renderingHints;

    public PDFPrintable(PDDocument document) {
        this(document, Scaling.SHRINK_TO_FIT);
    }

    public PDFPrintable(PDDocument document, Scaling scaling) {
        this(document, scaling, false, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public PDFPrintable(PDDocument document, Scaling scaling, boolean showPageBorder) {
        this(document, scaling, showPageBorder, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public PDFPrintable(PDDocument document, Scaling scaling, boolean showPageBorder, float dpi) {
        this(document, scaling, showPageBorder, dpi, true);
    }

    public PDFPrintable(PDDocument document, Scaling scaling, boolean showPageBorder, float dpi, boolean center) {
        this(document, scaling, showPageBorder, dpi, center, new PDFRenderer(document));
    }

    public PDFPrintable(PDDocument document, Scaling scaling, boolean showPageBorder, float dpi, boolean center, PDFRenderer renderer) {
        this.subsamplingAllowed = false;
        this.renderingHints = null;
        this.pageTree = document.getPages();
        this.renderer = renderer;
        this.scaling = scaling;
        this.showPageBorder = showPageBorder;
        this.dpi = dpi;
        this.center = center;
    }

    public boolean isSubsamplingAllowed() {
        return this.subsamplingAllowed;
    }

    public void setSubsamplingAllowed(boolean subsamplingAllowed) {
        this.subsamplingAllowed = subsamplingAllowed;
    }

    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    public void setRenderingHints(RenderingHints renderingHints) {
        this.renderingHints = renderingHints;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.awt.print.PrinterIOException */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException, PrinterIOException {
        if (pageIndex < 0 || pageIndex >= this.pageTree.getCount()) {
            return 1;
        }
        try {
            Graphics2D graphics2D = (Graphics2D) graphics;
            PDPage page = this.pageTree.get(pageIndex);
            PDRectangle cropBox = getRotatedCropBox(page);
            double imageableWidth = pageFormat.getImageableWidth();
            double imageableHeight = pageFormat.getImageableHeight();
            double scale = 1.0d;
            if (this.scaling != Scaling.ACTUAL_SIZE) {
                double scaleX = imageableWidth / ((double) cropBox.getWidth());
                double scaleY = imageableHeight / ((double) cropBox.getHeight());
                scale = Math.min(scaleX, scaleY);
                if (scale > 1.0d && this.scaling == Scaling.SHRINK_TO_FIT) {
                    scale = 1.0d;
                }
                if (scale < 1.0d && this.scaling == Scaling.STRETCH_TO_FIT) {
                    scale = 1.0d;
                }
            }
            graphics2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            if (this.center) {
                graphics2D.translate((imageableWidth - (((double) cropBox.getWidth()) * scale)) / 2.0d, (imageableHeight - (((double) cropBox.getHeight()) * scale)) / 2.0d);
            }
            Graphics2D printerGraphics = null;
            BufferedImage image = null;
            if (this.dpi > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                float dpiScale = this.dpi / 72.0f;
                image = new BufferedImage((int) ((imageableWidth * ((double) dpiScale)) / scale), (int) ((imageableHeight * ((double) dpiScale)) / scale), 2);
                printerGraphics = graphics2D;
                graphics2D = image.createGraphics();
                printerGraphics.scale(scale / ((double) dpiScale), scale / ((double) dpiScale));
                scale = dpiScale;
            }
            AffineTransform transform = (AffineTransform) graphics2D.getTransform().clone();
            graphics2D.setBackground(Color.WHITE);
            this.renderer.setSubsamplingAllowed(this.subsamplingAllowed);
            this.renderer.setRenderingHints(this.renderingHints);
            this.renderer.renderPageToGraphics(pageIndex, graphics2D, (float) scale, (float) scale, RenderDestination.PRINT);
            if (this.showPageBorder) {
                graphics2D.setTransform(transform);
                graphics2D.setClip(0, 0, (int) imageableWidth, (int) imageableHeight);
                graphics2D.scale(scale, scale);
                graphics2D.setColor(Color.GRAY);
                graphics2D.setStroke(new BasicStroke(0.5f));
                graphics.drawRect(0, 0, (int) cropBox.getWidth(), (int) cropBox.getHeight());
            }
            if (printerGraphics != null) {
                printerGraphics.setBackground(Color.WHITE);
                printerGraphics.clearRect(0, 0, image.getWidth(), image.getHeight());
                printerGraphics.drawImage(image, 0, 0, (ImageObserver) null);
                graphics2D.dispose();
                return 0;
            }
            return 0;
        } catch (IOException e) {
            throw new PrinterIOException(e);
        }
    }

    static PDRectangle getRotatedCropBox(PDPage page) {
        PDRectangle cropBox = page.getCropBox();
        int rotationAngle = page.getRotation();
        if (rotationAngle == 90 || rotationAngle == 270) {
            return new PDRectangle(cropBox.getLowerLeftY(), cropBox.getLowerLeftX(), cropBox.getHeight(), cropBox.getWidth());
        }
        return cropBox;
    }

    static PDRectangle getRotatedMediaBox(PDPage page) {
        PDRectangle mediaBox = page.getMediaBox();
        int rotationAngle = page.getRotation();
        if (rotationAngle == 90 || rotationAngle == 270) {
            return new PDRectangle(mediaBox.getLowerLeftY(), mediaBox.getLowerLeftX(), mediaBox.getHeight(), mediaBox.getWidth());
        }
        return mediaBox;
    }
}
