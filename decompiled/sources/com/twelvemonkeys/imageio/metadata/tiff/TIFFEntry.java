package com.twelvemonkeys.imageio.metadata.tiff;

import com.twelvemonkeys.imageio.metadata.AbstractEntry;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.exif.EXIF;
import com.twelvemonkeys.lang.Validate;
import java.lang.reflect.Array;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/tiff/TIFFEntry.class */
public final class TIFFEntry extends AbstractEntry {
    private final short type;

    public TIFFEntry(int i, Object obj) {
        this(i, guessType(obj), obj);
    }

    public TIFFEntry(int i, short s, Object obj) {
        super(Integer.valueOf(i), obj);
        if (s < 1 || s >= TIFF.TYPE_NAMES.length) {
            throw new IllegalArgumentException(String.format("Illegal TIFF type: %s", Short.valueOf(s)));
        }
        this.type = s;
    }

    public short getType() {
        return this.type;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractEntry, com.twelvemonkeys.imageio.metadata.Entry
    public String getFieldName() {
        switch (((Integer) getIdentifier()).intValue()) {
            case 254:
                return "SubfileType";
            case 256:
                return "ImageWidth";
            case 257:
                return "ImageHeight";
            case 258:
                return "BitsPerSample";
            case 259:
                return "Compression";
            case TIFF.TAG_PHOTOMETRIC_INTERPRETATION /* 262 */:
                return "PhotometricInterpretation";
            case 266:
                return "FillOrder";
            case TIFF.TAG_DOCUMENT_NAME /* 269 */:
                return "DocumentName";
            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                return "ImageDescription";
            case TIFF.TAG_MAKE /* 271 */:
                return "Make";
            case 272:
                return "Model";
            case TIFF.TAG_STRIP_OFFSETS /* 273 */:
                return "StripOffsets";
            case 274:
                return "Orientation";
            case 277:
                return "SamplesPerPixel";
            case 278:
                return "RowsPerStrip";
            case TIFF.TAG_STRIP_BYTE_COUNTS /* 279 */:
                return "StripByteCounts";
            case 282:
                return "XResolution";
            case TIFF.TAG_Y_RESOLUTION /* 283 */:
                return "YResolution";
            case TIFF.TAG_PLANAR_CONFIGURATION /* 284 */:
                return "PlanarConfiguration";
            case TIFF.TAG_PAGE_NAME /* 285 */:
                return "PageName";
            case TIFF.TAG_RESOLUTION_UNIT /* 296 */:
                return "ResolutionUnit";
            case TIFF.TAG_PAGE_NUMBER /* 297 */:
                return "PageNumber";
            case TIFF.TAG_SOFTWARE /* 305 */:
                return "Software";
            case 306:
                return "DateTime";
            case TIFF.TAG_ARTIST /* 315 */:
                return "Artist";
            case TIFF.TAG_HOST_COMPUTER /* 316 */:
                return "HostComputer";
            case 317:
                return "Predictor";
            case 320:
                return "ColorMap";
            case TIFF.TAG_TILE_WIDTH /* 322 */:
                return "TileWidth";
            case TIFF.TAG_TILE_HEIGTH /* 323 */:
                return "TileHeight";
            case TIFF.TAG_TILE_OFFSETS /* 324 */:
                return "TileOffsets";
            case TIFF.TAG_TILE_BYTE_COUNTS /* 325 */:
                return "TileByteCounts";
            case TIFF.TAG_SUB_IFD /* 330 */:
                return "SubIFD";
            case TIFF.TAG_INK_SET /* 332 */:
                return "InkSet";
            case TIFF.TAG_INK_NAMES /* 333 */:
                return "InkNames";
            case TIFF.TAG_EXTRA_SAMPLES /* 338 */:
                return "ExtraSamples";
            case TIFF.TAG_SAMPLE_FORMAT /* 339 */:
                return "SampleFormat";
            case TIFF.TAG_JPEG_TABLES /* 347 */:
                return "JPEGTables";
            case TIFF.TAG_JPEG_INTERCHANGE_FORMAT /* 513 */:
                return "JPEGInterchangeFormat";
            case TIFF.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH /* 514 */:
                return "JPEGInterchangeFormatLength";
            case TIFF.TAG_YCBCR_COEFFICIENTS /* 529 */:
                return "YCbCrCoefficients";
            case TIFF.TAG_YCBCR_SUB_SAMPLING /* 530 */:
                return "YCbCrSubSampling";
            case TIFF.TAG_YCBCR_POSITIONING /* 531 */:
                return "YCbCrPositioning";
            case 532:
                return "ReferenceBlackWhite";
            case 700:
                return "XMP";
            case TIFF.TAG_COPYRIGHT /* 33432 */:
                return "Copyright";
            case EXIF.TAG_EXPOSURE_TIME /* 33434 */:
                return "ExposureTime";
            case EXIF.TAG_F_NUMBER /* 33437 */:
                return "FNUmber";
            case TIFF.TAG_IPTC /* 33723 */:
                return "IPTC";
            case TIFF.TAG_PHOTOSHOP /* 34377 */:
                return "Adobe";
            case TIFF.TAG_EXIF_IFD /* 34665 */:
                return "EXIF";
            case TIFF.TAG_ICC_PROFILE /* 34675 */:
                return "ICCProfile";
            case EXIF.TAG_EXPOSURE_PROGRAM /* 34850 */:
                return "ExposureProgram";
            case TIFF.TAG_GPS_IFD /* 34853 */:
                return "GPS";
            case EXIF.TAG_ISO_SPEED_RATINGS /* 34855 */:
                return "ISOSpeedRatings";
            case EXIF.TAG_EXIF_VERSION /* 36864 */:
                return "ExifVersion";
            case EXIF.TAG_DATE_TIME_ORIGINAL /* 36867 */:
                return "DateTimeOriginal";
            case EXIF.TAG_DATE_TIME_DIGITIZED /* 36868 */:
                return "DateTimeDigitized";
            case EXIF.TAG_COMPONENTS_CONFIGURATION /* 37121 */:
                return "ComponentsConfiguration";
            case EXIF.TAG_COMPRESSED_BITS_PER_PIXEL /* 37122 */:
                return "CompressedBitsPerPixel";
            case EXIF.TAG_SHUTTER_SPEED_VALUE /* 37377 */:
                return "ShutterSpeedValue";
            case EXIF.TAG_APERTURE_VALUE /* 37378 */:
                return "ApertureValue";
            case EXIF.TAG_BRIGHTNESS_VALUE /* 37379 */:
                return "BrightnessValue";
            case EXIF.TAG_EXPOSURE_BIAS_VALUE /* 37380 */:
                return "ExposureBiasValue";
            case EXIF.TAG_MAX_APERTURE_VALUE /* 37381 */:
                return "MaxApertureValue";
            case EXIF.TAG_SUBJECT_DISTANCE /* 37382 */:
                return "SubjectDistance";
            case EXIF.TAG_METERING_MODE /* 37383 */:
                return "MeteringMode";
            case EXIF.TAG_LIGHT_SOURCE /* 37384 */:
                return "LightSource";
            case EXIF.TAG_FLASH /* 37385 */:
                return "Flash";
            case EXIF.TAG_FOCAL_LENGTH /* 37386 */:
                return "FocalLength";
            case EXIF.TAG_IMAGE_NUMBER /* 37393 */:
                return "ImageNumber";
            case EXIF.TAG_MAKER_NOTE /* 37500 */:
                return "MakerNote";
            case EXIF.TAG_USER_COMMENT /* 37510 */:
                return "UserComment";
            case TIFF.TAG_PHOTOSHOP_IMAGE_SOURCE_DATA /* 37724 */:
                return "ImageSourceData";
            case EXIF.TAG_FLASHPIX_VERSION /* 40960 */:
                return "FlashpixVersion";
            case EXIF.TAG_COLOR_SPACE /* 40961 */:
                return "ColorSpace";
            case EXIF.TAG_PIXEL_X_DIMENSION /* 40962 */:
                return "PixelXDimension";
            case EXIF.TAG_PIXEL_Y_DIMENSION /* 40963 */:
                return "PixelYDimension";
            case TIFF.TAG_INTEROP_IFD /* 40965 */:
                return "Interoperability";
            case EXIF.TAG_SENSING_METHOD /* 41495 */:
                return "SensingMethod";
            case EXIF.TAG_FILE_SOURCE /* 41728 */:
                return "FileSource";
            case EXIF.TAG_SCENE_TYPE /* 41729 */:
                return "SceneType";
            case EXIF.TAG_CFA_PATTERN /* 41730 */:
                return "CFAPattern";
            case EXIF.TAG_CUSTOM_RENDERED /* 41985 */:
                return "CustomRendered";
            case EXIF.TAG_EXPOSURE_MODE /* 41986 */:
                return "ExposureMode";
            case EXIF.TAG_WHITE_BALANCE /* 41987 */:
                return "WhiteBalance";
            case EXIF.TAG_DIGITAL_ZOOM_RATIO /* 41988 */:
                return "DigitalZoomRatio";
            case EXIF.TAG_FOCAL_LENGTH_IN_35_MM_FILM /* 41989 */:
                return "FocalLengthIn35mmFilm";
            case EXIF.TAG_SCENE_CAPTURE_TYPE /* 41990 */:
                return "SceneCaptureType";
            case EXIF.TAG_GAIN_CONTROL /* 41991 */:
                return "GainControl";
            case EXIF.TAG_CONTRAST /* 41992 */:
                return "Contrast";
            case EXIF.TAG_SATURATION /* 41993 */:
                return "Saturation";
            case EXIF.TAG_SHARPNESS /* 41994 */:
                return "Sharpness";
            case EXIF.TAG_IMAGE_UNIQUE_ID /* 42016 */:
                return "ImageUniqueID";
            default:
                return null;
        }
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractEntry, com.twelvemonkeys.imageio.metadata.Entry
    public String getTypeName() {
        return TIFF.TYPE_NAMES[this.type];
    }

    static short getType(Entry entry) {
        if (entry instanceof TIFFEntry) {
            return ((TIFFEntry) entry).getType();
        }
        Validate.notNull(entry, "entry");
        String typeName = entry.getTypeName();
        if (typeName != null) {
            for (int i = 1; i < TIFF.TYPE_NAMES.length; i++) {
                if (typeName.equals(TIFF.TYPE_NAMES[i])) {
                    return (short) i;
                }
            }
        }
        return guessType(entry.getValue());
    }

    private static short guessType(Object obj) {
        Object objNotNull = Validate.notNull(obj);
        boolean zIsArray = objNotNull.getClass().isArray();
        if (zIsArray) {
            objNotNull = Array.get(objNotNull, 0);
        }
        if (objNotNull instanceof Byte) {
            return (short) 1;
        }
        if (objNotNull instanceof Short) {
            if (!zIsArray && ((Short) objNotNull).shortValue() < 127) {
                return (short) 1;
            }
            return (short) 3;
        }
        if (objNotNull instanceof Integer) {
            if (!zIsArray && ((Integer) objNotNull).intValue() < 32767) {
                return (short) 3;
            }
            return (short) 4;
        }
        if ((objNotNull instanceof Long) && !zIsArray && ((Long) objNotNull).longValue() < 2147483647L) {
            return (short) 4;
        }
        if (objNotNull instanceof Rational) {
            return (short) 5;
        }
        if (objNotNull instanceof String) {
            return (short) 2;
        }
        throw new UnsupportedOperationException(String.format("Method guessType not implemented for type %s", objNotNull.getClass()));
    }

    static long getValueLength(int i, long j) {
        if (i > 0 && i < TIFF.TYPE_LENGTHS.length) {
            return ((long) TIFF.TYPE_LENGTHS[i]) * j;
        }
        return -1L;
    }
}
