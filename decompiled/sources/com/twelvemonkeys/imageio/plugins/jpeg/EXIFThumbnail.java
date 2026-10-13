package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.color.YCbCrConverter;
import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/EXIFThumbnail.class */
final class EXIFThumbnail {
    private EXIFThumbnail() {
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    static ThumbnailReader from(EXIF exif, CompoundDirectory compoundDirectory, ImageReader imageReader) throws IIOException, IOException {
        if (exif != null && compoundDirectory != null && compoundDirectory.directoryCount() >= 2) {
            ImageInputStream imageInputStreamExifData = exif.exifData();
            Directory directory = compoundDirectory.getDirectory(1);
            Entry entryById = directory.getEntryById(259);
            int iIntValue = entryById == null ? 6 : ((Number) entryById.getValue()).intValue();
            switch (iIntValue) {
                case 1:
                    return createUncompressedThumbnailReader(imageInputStreamExifData, directory);
                case 6:
                    return createJPEGThumbnailReader(exif, imageReader, imageInputStreamExifData, directory);
                default:
                    throw new IIOException("EXIF IFD with unknown thumbnail compression (expected 1 or 6): " + iIntValue);
            }
        }
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private static ThumbnailReader.UncompressedThumbnailReader createUncompressedThumbnailReader(ImageInputStream imageInputStream, Directory directory) throws IIOException, IOException {
        Entry entryById = directory.getEntryById(Integer.valueOf(TIFF.TAG_STRIP_OFFSETS));
        Entry entryById2 = directory.getEntryById(256);
        Entry entryById3 = directory.getEntryById(257);
        if (entryById != null && entryById2 != null && entryById3 != null) {
            Entry entryById4 = directory.getEntryById(258);
            Entry entryById5 = directory.getEntryById(277);
            Entry entryById6 = directory.getEntryById(Integer.valueOf(TIFF.TAG_PHOTOMETRIC_INTERPRETATION));
            int iIntValue = ((Number) entryById2.getValue()).intValue();
            int iIntValue2 = ((Number) entryById3.getValue()).intValue();
            if (entryById4 != null && !Arrays.equals((int[]) entryById4.getValue(), new int[]{8, 8, 8})) {
                throw new IIOException("Unknown BitsPerSample value for uncompressed EXIF thumbnail (expected [8, 8, 8]): " + entryById4.getValueAsString());
            }
            if (entryById5 != null && ((Number) entryById5.getValue()).intValue() != 3) {
                throw new IIOException("Unknown SamplesPerPixel value for uncompressed EXIF thumbnail (expected 3): " + entryById5.getValueAsString());
            }
            int iIntValue3 = entryById6 != null ? ((Number) entryById6.getValue()).intValue() : 2;
            long jLongValue = ((Number) entryById.getValue()).longValue();
            int i = iIntValue * iIntValue2 * 3;
            if (jLongValue >= 0 && jLongValue + ((long) i) <= imageInputStream.length()) {
                imageInputStream.seek(jLongValue);
                byte[] bArr = new byte[i];
                imageInputStream.readFully(bArr);
                switch (iIntValue3) {
                    case 2:
                        break;
                    case 6:
                        for (int i2 = 0; i2 < i; i2 += 3) {
                            YCbCrConverter.convertJPEGYCbCr2RGB(bArr, bArr, i2);
                        }
                        break;
                    default:
                        throw new IIOException("Unknown PhotometricInterpretation value for uncompressed EXIF thumbnail (expected 2 or 6): " + iIntValue3);
                }
                return new ThumbnailReader.UncompressedThumbnailReader(iIntValue, iIntValue2, bArr);
            }
        }
        throw new IIOException("EXIF IFD with empty or incomplete uncompressed thumbnail");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private static ThumbnailReader.JPEGThumbnailReader createJPEGThumbnailReader(EXIF exif, ImageReader imageReader, ImageInputStream imageInputStream, Directory directory) throws IIOException, IOException {
        Entry entryById = directory.getEntryById(Integer.valueOf(TIFF.TAG_JPEG_INTERCHANGE_FORMAT));
        if (entryById != null) {
            Entry entryById2 = directory.getEntryById(Integer.valueOf(TIFF.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH));
            long jLongValue = ((Number) entryById.getValue()).longValue();
            long jLongValue2 = entryById2 != null ? ((Number) entryById2.getValue()).longValue() : -1L;
            if (jLongValue2 > 0 && jLongValue + jLongValue2 <= exif.data.length) {
                imageInputStream.seek(jLongValue);
                imageInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
                if (imageInputStream.readUnsignedShort() == 65496) {
                    return new ThumbnailReader.JPEGThumbnailReader(imageReader, imageInputStream, jLongValue);
                }
            }
        }
        throw new IIOException("EXIF IFD with empty or incomplete JPEG thumbnail");
    }
}
