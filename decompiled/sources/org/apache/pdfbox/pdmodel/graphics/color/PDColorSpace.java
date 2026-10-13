package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ComponentColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.MissingResourceException;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDColorSpace.class */
public abstract class PDColorSpace implements COSObjectable {
    private final ColorConvertOp colorConvertOp = new ColorConvertOp((RenderingHints) null);
    protected COSArray array;

    public abstract String getName();

    public abstract int getNumberOfComponents();

    public abstract float[] getDefaultDecode(int i);

    public abstract PDColor getInitialColor();

    public abstract float[] toRGB(float[] fArr) throws IOException;

    public abstract BufferedImage toRGBImage(WritableRaster writableRaster) throws IOException;

    public abstract BufferedImage toRawImage(WritableRaster writableRaster) throws IOException;

    public static PDColorSpace create(COSBase colorSpace) throws IOException {
        return create(colorSpace, null);
    }

    public static PDColorSpace create(COSBase colorSpace, PDResources resources) throws IOException {
        return create(colorSpace, resources, false);
    }

    public static PDColorSpace create(COSBase colorSpace, PDResources resources, boolean wasDefault) throws IOException {
        if (colorSpace instanceof COSObject) {
            return createFromCOSObject((COSObject) colorSpace, resources);
        }
        if (colorSpace instanceof COSName) {
            COSName name = (COSName) colorSpace;
            if (resources != null) {
                COSName defaultName = null;
                if (name.equals(COSName.DEVICECMYK) && resources.hasColorSpace(COSName.DEFAULT_CMYK)) {
                    defaultName = COSName.DEFAULT_CMYK;
                } else if (name.equals(COSName.DEVICERGB) && resources.hasColorSpace(COSName.DEFAULT_RGB)) {
                    defaultName = COSName.DEFAULT_RGB;
                } else if (name.equals(COSName.DEVICEGRAY) && resources.hasColorSpace(COSName.DEFAULT_GRAY)) {
                    defaultName = COSName.DEFAULT_GRAY;
                }
                if (resources.hasColorSpace(defaultName) && !wasDefault) {
                    return resources.getColorSpace(defaultName, true);
                }
            }
            if (name == COSName.DEVICECMYK) {
                return PDDeviceCMYK.INSTANCE;
            }
            if (name == COSName.DEVICERGB) {
                return PDDeviceRGB.INSTANCE;
            }
            if (name == COSName.DEVICEGRAY) {
                return PDDeviceGray.INSTANCE;
            }
            if (name == COSName.PATTERN) {
                return new PDPattern(resources);
            }
            if (resources != null) {
                if (!resources.hasColorSpace(name)) {
                    throw new MissingResourceException("Missing color space: " + name.getName());
                }
                return resources.getColorSpace(name);
            }
            throw new MissingResourceException("Unknown color space: " + name.getName());
        }
        if (colorSpace instanceof COSArray) {
            COSArray array = (COSArray) colorSpace;
            if (array.size() == 0) {
                throw new IOException("Colorspace array is empty");
            }
            COSBase base = array.getObject(0);
            if (!(base instanceof COSName)) {
                throw new IOException("First element in colorspace array must be a name");
            }
            COSName name2 = (COSName) base;
            if (name2 == COSName.CALGRAY) {
                return new PDCalGray(array);
            }
            if (name2 == COSName.CALRGB) {
                return new PDCalRGB(array);
            }
            if (name2 == COSName.DEVICEN) {
                return new PDDeviceN(array);
            }
            if (name2 == COSName.INDEXED) {
                return new PDIndexed(array, resources);
            }
            if (name2 == COSName.SEPARATION) {
                return new PDSeparation(array);
            }
            if (name2 == COSName.ICCBASED) {
                return PDICCBased.create(array, resources);
            }
            if (name2 == COSName.LAB) {
                return new PDLab(array);
            }
            if (name2 == COSName.PATTERN) {
                if (array.size() == 1) {
                    return new PDPattern(resources);
                }
                return new PDPattern(resources, create(array.get(1)));
            }
            if (name2 == COSName.DEVICECMYK || name2 == COSName.DEVICERGB || name2 == COSName.DEVICEGRAY) {
                return create(name2, resources, wasDefault);
            }
            throw new IOException("Invalid color space kind: " + name2);
        }
        if ((colorSpace instanceof COSDictionary) && ((COSDictionary) colorSpace).containsKey(COSName.COLORSPACE)) {
            COSBase base2 = ((COSDictionary) colorSpace).getDictionaryObject(COSName.COLORSPACE);
            if (base2 == colorSpace) {
                throw new IOException("Recursion in colorspace: " + ((COSDictionary) colorSpace).getItem(COSName.COLORSPACE) + " points to itself");
            }
            return create(base2, resources, wasDefault);
        }
        throw new IOException("Expected a name or array but got: " + colorSpace);
    }

    private static PDColorSpace createFromCOSObject(COSObject colorSpace, PDResources resources) throws IOException {
        if (resources != null && resources.getResourceCache() != null) {
            ResourceCache resourceCache = resources.getResourceCache();
            PDColorSpace cs = resourceCache.getColorSpace(colorSpace);
            if (cs != null) {
                return cs;
            }
        }
        PDColorSpace cs2 = create(colorSpace.getObject(), resources);
        if (resources != null && resources.getResourceCache() != null && cs2 != null) {
            ResourceCache resourceCache2 = resources.getResourceCache();
            resourceCache2.put(colorSpace, cs2);
        }
        return cs2;
    }

    protected final BufferedImage toRawImage(WritableRaster raster, ColorSpace awtColorSpace) {
        return new BufferedImage(new ComponentColorModel(awtColorSpace, false, false, 1, raster.getDataBuffer().getDataType()), raster, false, (Hashtable) null);
    }

    protected BufferedImage toRGBImageAWT(WritableRaster raster, ColorSpace colorSpace) {
        BufferedImage src = new BufferedImage(new ComponentColorModel(colorSpace, false, false, 1, raster.getDataBuffer().getDataType()), raster, false, (Hashtable) null);
        BufferedImage dest = new BufferedImage(raster.getWidth(), raster.getHeight(), 1);
        if (src.getWidth() == 1 || src.getHeight() == 1) {
            Graphics g2d = dest.getGraphics();
            g2d.drawImage(src, 0, 0, (ImageObserver) null);
            g2d.dispose();
            return dest;
        }
        this.colorConvertOp.filter(src, dest);
        return dest;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this.array;
    }
}
