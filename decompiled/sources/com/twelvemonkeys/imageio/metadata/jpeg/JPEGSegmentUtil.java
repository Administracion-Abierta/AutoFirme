package com.twelvemonkeys.imageio.metadata.jpeg;

import com.twelvemonkeys.imageio.color.ColorProfiles;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.psd.PSD;
import com.twelvemonkeys.imageio.metadata.psd.PSDReader;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import com.twelvemonkeys.imageio.metadata.xmp.XMP;
import com.twelvemonkeys.imageio.metadata.xmp.XMPReader;
import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream;
import com.twelvemonkeys.lang.Validate;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/jpeg/JPEGSegmentUtil.class */
public final class JPEGSegmentUtil {
    public static final List<String> ALL_IDS = Collections.unmodifiableList(new AllIdsList());
    public static final Map<Integer, List<String>> ALL_SEGMENTS = Collections.unmodifiableMap(new AllSegmentsMap());
    public static final Map<Integer, List<String>> APP_SEGMENTS = Collections.unmodifiableMap(new AllAppSegmentsMap());

    private JPEGSegmentUtil() {
    }

    public static List<JPEGSegment> readSegments(ImageInputStream imageInputStream, int i, String str) throws IOException {
        return readSegments(imageInputStream, Collections.singletonMap(Integer.valueOf(i), str != null ? Collections.singletonList(str) : ALL_IDS));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public static List<JPEGSegment> readSegments(ImageInputStream imageInputStream, Map<Integer, List<String>> map) throws IIOException, IOException {
        JPEGSegment segment;
        readSOI((ImageInputStream) Validate.notNull(imageInputStream, "stream"));
        List<JPEGSegment> listEmptyList = Collections.emptyList();
        do {
            try {
                segment = readSegment(imageInputStream, map);
                if (isRequested(segment, map)) {
                    if (listEmptyList == Collections.EMPTY_LIST) {
                        listEmptyList = new ArrayList();
                    }
                    listEmptyList.add(segment);
                }
            } catch (EOFException e) {
            }
        } while (!isImageDone(segment));
        return listEmptyList;
    }

    private static boolean isRequested(JPEGSegment jPEGSegment, Map<Integer, List<String>> map) {
        return map.containsKey(Integer.valueOf(jPEGSegment.marker)) && ((jPEGSegment.identifier() == null && map.get(Integer.valueOf(jPEGSegment.marker)) == null) || containsSafe(jPEGSegment, map));
    }

    private static boolean containsSafe(JPEGSegment jPEGSegment, Map<Integer, List<String>> map) {
        List<String> list = map.get(Integer.valueOf(jPEGSegment.marker));
        return list != null && list.contains(jPEGSegment.identifier());
    }

    private static boolean isImageDone(JPEGSegment jPEGSegment) {
        return jPEGSegment.marker == 65498 || jPEGSegment.marker == 65497 || jPEGSegment.marker == 65496;
    }

    static String asNullTerminatedAsciiString(byte[] bArr, int i) {
        for (int i2 = 0; i2 < bArr.length - i; i2++) {
            if (bArr[i + i2] < 20 || i2 > 255) {
                return asAsciiString(bArr, i, i + i2);
            }
        }
        return null;
    }

    static String asAsciiString(byte[] bArr, int i, int i2) {
        return new String(bArr, i, i2, StandardCharsets.US_ASCII);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    static void readSOI(ImageInputStream imageInputStream) throws IIOException, IOException {
        if (imageInputStream.readUnsignedShort() != 65496) {
            throw new IIOException("Not a JPEG stream");
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    static JPEGSegment readSegment(ImageInputStream imageInputStream, Map<Integer, List<String>> map) throws IIOException, IOException {
        byte[] byteArray;
        int unsignedByte = imageInputStream.readUnsignedByte();
        while (!isKnownJPEGMarker(unsignedByte)) {
            while (unsignedByte != 255) {
                unsignedByte = imageInputStream.readUnsignedByte();
            }
            int i = 65280;
            int unsignedByte2 = imageInputStream.readUnsignedByte();
            while (true) {
                unsignedByte = i | unsignedByte2;
                if (unsignedByte == 65535) {
                    i = 65280;
                    unsignedByte2 = imageInputStream.readUnsignedByte();
                }
            }
        }
        if (((unsignedByte >> 8) & 255) != 255) {
            throw new IIOException(String.format("Bad marker: %04x", Integer.valueOf(unsignedByte)));
        }
        int unsignedShort = imageInputStream.readUnsignedShort();
        if (map.containsKey(Integer.valueOf(unsignedByte))) {
            byteArray = new byte[Math.max(0, unsignedShort - 2)];
            imageInputStream.readFully(byteArray);
        } else if (JPEGSegment.isAppSegmentMarker(unsignedByte)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(32);
            while (true) {
                int i2 = imageInputStream.read();
                if (i2 <= 0) {
                    break;
                }
                byteArrayOutputStream.write(i2);
            }
            byteArray = byteArrayOutputStream.toByteArray();
            imageInputStream.skipBytes((unsignedShort - 3) - byteArray.length);
        } else {
            byteArray = null;
            imageInputStream.skipBytes(unsignedShort - 2);
        }
        return new JPEGSegment(unsignedByte, byteArray, unsignedShort);
    }

    public static boolean isKnownJPEGMarker(int i) {
        switch (i) {
            case 65281:
            case JPEG.SOF0 /* 65472 */:
            case JPEG.SOF1 /* 65473 */:
            case JPEG.SOF2 /* 65474 */:
            case JPEG.SOF3 /* 65475 */:
            case JPEG.DHT /* 65476 */:
            case JPEG.SOF5 /* 65477 */:
            case JPEG.SOF6 /* 65478 */:
            case JPEG.SOF7 /* 65479 */:
            case JPEG.SOF9 /* 65481 */:
            case JPEG.SOF10 /* 65482 */:
            case JPEG.SOF11 /* 65483 */:
            case JPEG.DAC /* 65484 */:
            case JPEG.SOF13 /* 65485 */:
            case JPEG.SOF14 /* 65486 */:
            case JPEG.SOF15 /* 65487 */:
            case JPEG.SOI /* 65496 */:
            case JPEG.EOI /* 65497 */:
            case JPEG.SOS /* 65498 */:
            case JPEG.DQT /* 65499 */:
            case JPEG.DNL /* 65500 */:
            case JPEG.DRI /* 65501 */:
            case JPEG.DHP /* 65502 */:
            case JPEG.EXP /* 65503 */:
            case JPEG.APP0 /* 65504 */:
            case 65505:
            case 65506:
            case 65507:
            case 65508:
            case 65509:
            case 65510:
            case 65511:
            case 65512:
            case 65513:
            case 65514:
            case JPEG.APP11 /* 65515 */:
            case JPEG.APP12 /* 65516 */:
            case JPEG.APP13 /* 65517 */:
            case JPEG.APP14 /* 65518 */:
            case JPEG.APP15 /* 65519 */:
            case JPEG.SOF55 /* 65527 */:
            case JPEG.LSE /* 65528 */:
            case JPEG.COM /* 65534 */:
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/jpeg/JPEGSegmentUtil$AllIdsList.class */
    private static class AllIdsList extends ArrayList<String> {
        private AllIdsList() {
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            return "[All ids]";
        }

        @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean contains(Object obj) {
            return true;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/jpeg/JPEGSegmentUtil$AllSegmentsMap.class */
    private static class AllSegmentsMap extends HashMap<Integer, List<String>> {
        private AllSegmentsMap() {
        }

        @Override // java.util.AbstractMap
        public String toString() {
            return "{All segments}";
        }

        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public List<String> get(Object obj) {
            if ((obj instanceof Integer) && JPEGSegment.isAppSegmentMarker(((Integer) obj).intValue())) {
                return JPEGSegmentUtil.ALL_IDS;
            }
            return null;
        }

        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public boolean containsKey(Object obj) {
            return true;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/jpeg/JPEGSegmentUtil$AllAppSegmentsMap.class */
    private static class AllAppSegmentsMap extends HashMap<Integer, List<String>> {
        private AllAppSegmentsMap() {
        }

        @Override // java.util.AbstractMap
        public String toString() {
            return "{All APPn segments}";
        }

        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public List<String> get(Object obj) {
            if (containsKey(obj)) {
                return JPEGSegmentUtil.ALL_IDS;
            }
            return null;
        }

        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public boolean containsKey(Object obj) {
            return (obj instanceof Integer) && JPEGSegment.isAppSegmentMarker(((Integer) obj).intValue());
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public static void main(String[] strArr) throws IIOException, IOException {
        for (String str : strArr) {
            if (strArr.length > 1) {
                System.out.println("File: " + str);
                System.out.println("------");
            }
            for (JPEGSegment jPEGSegment : readSegments(ImageIO.createImageInputStream(new File(str)), ALL_SEGMENTS)) {
                System.err.println("segment: " + jPEGSegment);
                if ("Exif".equals(jPEGSegment.identifier())) {
                    System.err.println("EXIF: " + new TIFFReader().read(new ByteArrayImageInputStream(jPEGSegment.data, jPEGSegment.offset() + 1, jPEGSegment.length() - 1)));
                } else if (XMP.NS_XAP.equals(jPEGSegment.identifier())) {
                    System.err.println("XMP: " + new XMPReader().read(new ByteArrayImageInputStream(jPEGSegment.data, jPEGSegment.offset(), jPEGSegment.length())));
                    System.err.println(TIFFReader.HexDump.dump(jPEGSegment.data));
                } else if ("Photoshop 3.0".equals(jPEGSegment.identifier())) {
                    Directory directory = new PSDReader().read(new ByteArrayImageInputStream(jPEGSegment.data, jPEGSegment.offset(), jPEGSegment.length()));
                    Entry entryById = directory.getEntryById(Integer.valueOf(PSD.RES_ICC_PROFILE));
                    if (entryById != null) {
                        System.err.println("ICC Profile: " + ColorProfiles.createProfile((byte[]) entryById.getValue()));
                    }
                    System.err.println("PSD: " + directory);
                    System.err.println(TIFFReader.HexDump.dump(jPEGSegment.data));
                } else if (!"ICC_PROFILE".equals(jPEGSegment.identifier())) {
                    System.err.println(TIFFReader.HexDump.dump(jPEGSegment.data));
                }
            }
            if (strArr.length > 1) {
                System.out.println("------");
                System.out.println();
            }
        }
    }
}
