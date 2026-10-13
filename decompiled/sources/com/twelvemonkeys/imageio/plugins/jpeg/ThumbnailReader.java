package com.twelvemonkeys.imageio.plugins.jpeg;

import com.aowagie.text.ElementTags;
import com.twelvemonkeys.lang.Validate;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/ThumbnailReader.class */
abstract class ThumbnailReader {
    public abstract BufferedImage read() throws IOException;

    public abstract int getWidth() throws IOException;

    public abstract int getHeight() throws IOException;

    ThumbnailReader() {
    }

    public IIOMetadata readMetadata() throws IOException {
        return null;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/ThumbnailReader$UncompressedThumbnailReader.class */
    static class UncompressedThumbnailReader extends ThumbnailReader {
        private final int width;
        private final int height;
        private final byte[] data;
        private final int offset;

        public UncompressedThumbnailReader(int i, int i2, byte[] bArr) {
            this(i, i2, bArr, 0);
        }

        public UncompressedThumbnailReader(int i, int i2, byte[] bArr, int i3) {
            this.width = ((Integer) Validate.isTrue(i > 0, Integer.valueOf(i), ElementTags.WIDTH)).intValue();
            this.height = ((Integer) Validate.isTrue(i2 > 0, Integer.valueOf(i2), "height")).intValue();
            this.data = (byte[]) Validate.notNull(bArr, "data");
            this.offset = ((Integer) Validate.isTrue(i3 >= 0 && i3 < bArr.length, Integer.valueOf(i3), ElementTags.OFFSET)).intValue();
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader
        public BufferedImage read() throws IOException {
            WritableRaster writableRasterCreateInterleavedRaster;
            ComponentColorModel componentColorModel;
            DataBufferByte dataBufferByte = new DataBufferByte(this.data, this.data.length, this.offset);
            if (this.data.length == this.width * this.height) {
                writableRasterCreateInterleavedRaster = Raster.createInterleavedRaster(dataBufferByte, this.width, this.height, this.width, 1, new int[]{0}, (Point) null);
                componentColorModel = new ComponentColorModel(ColorSpace.getInstance(1003), false, false, 1, 0);
            } else {
                writableRasterCreateInterleavedRaster = Raster.createInterleavedRaster(dataBufferByte, this.width, this.height, this.width * 3, 3, new int[]{0, 1, 2}, (Point) null);
                componentColorModel = new ComponentColorModel(ColorSpace.getInstance(1000), false, false, 1, 0);
            }
            return new BufferedImage(componentColorModel, writableRasterCreateInterleavedRaster, componentColorModel.isAlphaPremultiplied(), (Hashtable) null);
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader
        public int getWidth() throws IOException {
            return this.width;
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader
        public int getHeight() throws IOException {
            return this.height;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/ThumbnailReader$IndexedThumbnailReader.class */
    static class IndexedThumbnailReader extends ThumbnailReader {
        private final int width;
        private final int height;
        private final byte[] palette;
        private final int paletteOff;
        private final byte[] data;
        private final int dataOff;

        public IndexedThumbnailReader(int i, int i2, byte[] bArr, int i3, byte[] bArr2, int i4) {
            this.width = ((Integer) Validate.isTrue(i > 0, Integer.valueOf(i), ElementTags.WIDTH)).intValue();
            this.height = ((Integer) Validate.isTrue(i2 > 0, Integer.valueOf(i2), "height")).intValue();
            this.palette = (byte[]) Validate.notNull(bArr, "palette");
            this.paletteOff = ((Integer) Validate.isTrue(i3 >= 0 && i3 < bArr.length, Integer.valueOf(i3), "paletteOff")).intValue();
            this.data = (byte[]) Validate.notNull(bArr2, "data");
            this.dataOff = ((Integer) Validate.isTrue(i4 >= 0 && i4 < bArr2.length, Integer.valueOf(i4), "dataOff")).intValue();
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader
        public BufferedImage read() throws IOException {
            int[] iArr = new int[256];
            for (int i = 0; i < iArr.length; i++) {
                iArr[i] = ((this.palette[this.paletteOff + (3 * i)] & 255) << 16) | ((this.palette[(this.paletteOff + (3 * i)) + 1] & 255) << 8) | (this.palette[this.paletteOff + (3 * i) + 2] & 255);
            }
            IndexColorModel indexColorModel = new IndexColorModel(8, iArr.length, iArr, 0, false, -1, 0);
            return new BufferedImage(indexColorModel, Raster.createPackedRaster(new DataBufferByte(this.data, this.data.length - this.dataOff, this.dataOff), this.width, this.height, 8, (Point) null), indexColorModel.isAlphaPremultiplied(), (Hashtable) null);
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader
        public int getWidth() throws IOException {
            return this.width;
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader
        public int getHeight() throws IOException {
            return this.height;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/ThumbnailReader$JPEGThumbnailReader.class */
    static class JPEGThumbnailReader extends ThumbnailReader {
        private final ImageReader reader;
        private final ImageInputStream input;
        private final long offset;
        private Dimension dimension;

        public JPEGThumbnailReader(ImageReader imageReader, ImageInputStream imageInputStream, long j) {
            this.reader = (ImageReader) Validate.notNull(imageReader, "reader");
            this.input = (ImageInputStream) Validate.notNull(imageInputStream, "input");
            this.offset = ((Long) Validate.isTrue(j >= 0, Long.valueOf(j), ElementTags.OFFSET)).longValue();
        }

        private void initReader() throws IOException {
            if (this.reader.getInput() != this.input) {
                this.input.seek(this.offset);
                this.reader.setInput(this.input);
            }
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader
        public BufferedImage read() throws IOException {
            initReader();
            return this.reader.read(0, (ImageReadParam) null);
        }

        private Dimension readDimensions() throws IOException {
            if (this.dimension == null) {
                initReader();
                this.dimension = new Dimension(this.reader.getWidth(0), this.reader.getHeight(0));
            }
            return this.dimension;
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader
        public int getWidth() throws IOException {
            return readDimensions().width;
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader
        public int getHeight() throws IOException {
            return readDimensions().height;
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader
        public IIOMetadata readMetadata() throws IOException {
            initReader();
            return this.reader.getImageMetadata(0);
        }
    }
}
