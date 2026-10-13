package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/image/PDImage.class */
public interface PDImage extends COSObjectable {
    BufferedImage getImage() throws IOException;

    WritableRaster getRawRaster() throws IOException;

    BufferedImage getRawImage() throws IOException;

    BufferedImage getImage(Rectangle rectangle, int i) throws IOException;

    BufferedImage getStencilImage(Paint paint) throws IOException;

    InputStream createInputStream() throws IOException;

    InputStream createInputStream(List<String> list) throws IOException;

    InputStream createInputStream(DecodeOptions decodeOptions) throws IOException;

    boolean isEmpty();

    boolean isStencil();

    void setStencil(boolean z);

    int getBitsPerComponent();

    void setBitsPerComponent(int i);

    PDColorSpace getColorSpace() throws IOException;

    void setColorSpace(PDColorSpace pDColorSpace);

    int getHeight();

    void setHeight(int i);

    int getWidth();

    void setWidth(int i);

    void setDecode(COSArray cOSArray);

    COSArray getDecode();

    boolean getInterpolate();

    void setInterpolate(boolean z);

    String getSuffix();
}
