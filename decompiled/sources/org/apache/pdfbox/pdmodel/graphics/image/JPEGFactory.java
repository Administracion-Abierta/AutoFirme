package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.MissingImageReaderException;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/image/JPEGFactory.class */
public final class JPEGFactory {
    private static final Log LOG = LogFactory.getLog(JPEGFactory.class);

    private JPEGFactory() {
    }

    public static PDImageXObject createFromStream(PDDocument document, InputStream stream) throws IOException {
        return createFromByteArray(document, IOUtils.toByteArray(stream));
    }

    public static PDImageXObject createFromByteArray(PDDocument document, byte[] byteArray) throws IOException {
        PDColorSpace colorSpace;
        ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);
        Dimensions meta = retrieveDimensions(byteStream);
        switch (meta.numComponents) {
            case 1:
                colorSpace = PDDeviceGray.INSTANCE;
                break;
            case 2:
            default:
                throw new UnsupportedOperationException("number of data elements not supported: " + meta.numComponents);
            case 3:
                colorSpace = PDDeviceRGB.INSTANCE;
                break;
            case 4:
                colorSpace = PDDeviceCMYK.INSTANCE;
                break;
        }
        PDImageXObject pdImage = new PDImageXObject(document, byteStream, COSName.DCT_DECODE, meta.width, meta.height, 8, colorSpace);
        if (colorSpace instanceof PDDeviceCMYK) {
            COSArray decode = new COSArray();
            decode.add((COSBase) COSInteger.ONE);
            decode.add((COSBase) COSInteger.ZERO);
            decode.add((COSBase) COSInteger.ONE);
            decode.add((COSBase) COSInteger.ZERO);
            decode.add((COSBase) COSInteger.ONE);
            decode.add((COSBase) COSInteger.ZERO);
            decode.add((COSBase) COSInteger.ONE);
            decode.add((COSBase) COSInteger.ZERO);
            pdImage.setDecode(decode);
        }
        return pdImage;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/image/JPEGFactory$Dimensions.class */
    private static class Dimensions {
        private int width;
        private int height;
        private int numComponents;

        private Dimensions() {
        }
    }

    private static Dimensions retrieveDimensions(ByteArrayInputStream stream) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
        ImageReader reader = null;
        while (readers.hasNext()) {
            reader = readers.next();
            if (reader.canReadRaster()) {
                break;
            }
        }
        if (reader == null) {
            throw new MissingImageReaderException("Cannot read JPEG image: a suitable JAI I/O image filter is not installed");
        }
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(stream);
            reader.setInput(iis);
            Dimensions meta = new Dimensions();
            meta.width = reader.getWidth(0);
            meta.height = reader.getHeight(0);
            try {
                meta.numComponents = getNumComponentsFromImageMetadata(reader);
                if (meta.numComponents == 0) {
                    LOG.warn("No image metadata, will decode image and use raster size");
                } else {
                    if (iis != null) {
                        iis.close();
                    }
                    stream.reset();
                    reader.dispose();
                    return meta;
                }
            } catch (IOException ex) {
                LOG.warn("Error reading image metadata, will decode image and use raster size", ex);
            }
            ImageIO.setUseCache(false);
            Raster raster = reader.readRaster(0, (ImageReadParam) null);
            meta.numComponents = raster.getNumDataElements();
            if (iis != null) {
                iis.close();
            }
            stream.reset();
            reader.dispose();
            return meta;
        } catch (Throwable th) {
            if (iis != null) {
                iis.close();
            }
            stream.reset();
            reader.dispose();
            throw th;
        }
    }

    private static int getNumComponentsFromImageMetadata(ImageReader reader) throws IOException {
        Element root;
        IIOMetadata imageMetadata = reader.getImageMetadata(0);
        if (imageMetadata == null || (root = (Element) imageMetadata.getAsTree("javax_imageio_jpeg_image_1.0")) == null) {
            return 0;
        }
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            String numFrameComponents = xpath.evaluate("markerSequence/sof/@numFrameComponents", root);
            if (numFrameComponents.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(numFrameComponents);
        } catch (NumberFormatException ex) {
            LOG.warn(ex.getMessage(), ex);
            return 0;
        } catch (XPathExpressionException ex2) {
            LOG.warn(ex2.getMessage(), ex2);
            return 0;
        }
    }

    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image) throws IOException {
        return createFromImage(document, image, 0.75f);
    }

    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image, float quality) throws IOException {
        return createFromImage(document, image, quality, 72);
    }

    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image, float quality, int dpi) throws IOException {
        return createJPEG(document, image, quality, dpi);
    }

    private static BufferedImage getAlphaImage(BufferedImage image) {
        if (!image.getColorModel().hasAlpha()) {
            return null;
        }
        if (image.getTransparency() == 2) {
            throw new UnsupportedOperationException("BITMASK Transparency JPEG compression is not useful, use LosslessImageFactory instead");
        }
        WritableRaster alphaRaster = image.getAlphaRaster();
        if (alphaRaster == null) {
            return null;
        }
        BufferedImage alphaImage = new BufferedImage(image.getWidth(), image.getHeight(), 10);
        alphaImage.setData(alphaRaster);
        return alphaImage;
    }

    private static PDImageXObject createJPEG(PDDocument document, BufferedImage image, float quality, int dpi) throws IOException {
        BufferedImage awtColorImage = getColorImage(image);
        byte[] encoded = encodeImageToJPEGStream(awtColorImage, quality, dpi);
        ByteArrayInputStream encodedByteStream = new ByteArrayInputStream(encoded);
        PDImageXObject pdImage = new PDImageXObject(document, encodedByteStream, COSName.DCT_DECODE, awtColorImage.getWidth(), awtColorImage.getHeight(), 8, getColorSpaceFromAWT(awtColorImage));
        BufferedImage awtAlphaImage = getAlphaImage(image);
        if (awtAlphaImage != null) {
            PDImage xAlpha = createFromImage(document, awtAlphaImage, quality);
            pdImage.getCOSObject().setItem(COSName.SMASK, xAlpha);
        }
        return pdImage;
    }

    private static ImageWriter getJPEGImageWriter() throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
        while (writers.hasNext()) {
            ImageWriter writer = writers.next();
            if (writer != null) {
                if (writer.getDefaultWriteParam() instanceof JPEGImageWriteParam) {
                    return writer;
                }
                writer.dispose();
            }
        }
        throw new IOException("No ImageWriter found for JPEG format");
    }

    private static byte[] encodeImageToJPEGStream(BufferedImage image, float quality, int dpi) throws IOException {
        ImageWriter imageWriter = getJPEGImageWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = null;
        try {
            ImageOutputStream ios2 = ImageIO.createImageOutputStream(baos);
            imageWriter.setOutput(ios2);
            ImageWriteParam jpegParam = imageWriter.getDefaultWriteParam();
            jpegParam.setCompressionMode(2);
            jpegParam.setCompressionQuality(quality);
            ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(image);
            IIOMetadata metadata = imageWriter.getDefaultImageMetadata(imageTypeSpecifier, jpegParam);
            Element tree = (Element) metadata.getAsTree("javax_imageio_jpeg_image_1.0");
            Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
            String dpiString = Integer.toString(dpi);
            jfif.setAttribute("Xdensity", dpiString);
            jfif.setAttribute("Ydensity", dpiString);
            jfif.setAttribute("resUnits", "1");
            imageWriter.write(metadata, new IIOImage(image, (List) null, (IIOMetadata) null), jpegParam);
            byte[] byteArray = baos.toByteArray();
            if (ios2 != null) {
            }
            return byteArray;
        } finally {
            if (ios != null) {
                ios.close();
            }
            imageWriter.dispose();
        }
    }

    private static PDColorSpace getColorSpaceFromAWT(BufferedImage awtImage) {
        if (awtImage.getColorModel().getNumComponents() == 1) {
            return PDDeviceGray.INSTANCE;
        }
        ColorSpace awtColorSpace = awtImage.getColorModel().getColorSpace();
        if ((awtColorSpace instanceof ICC_ColorSpace) && !awtColorSpace.isCS_sRGB()) {
            throw new UnsupportedOperationException("ICC color spaces not implemented");
        }
        switch (awtColorSpace.getType()) {
            case 5:
                return PDDeviceRGB.INSTANCE;
            case 6:
                return PDDeviceGray.INSTANCE;
            case 7:
            case 8:
            default:
                throw new UnsupportedOperationException("color space not implemented: " + awtColorSpace.getType());
            case 9:
                return PDDeviceCMYK.INSTANCE;
        }
    }

    private static BufferedImage getColorImage(BufferedImage image) {
        if (!image.getColorModel().hasAlpha()) {
            return image;
        }
        if (image.getColorModel().getColorSpace().getType() != 5) {
            throw new UnsupportedOperationException("only RGB color spaces are implemented");
        }
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), 5);
        return new ColorConvertOp((RenderingHints) null).filter(image, rgbImage);
    }
}
