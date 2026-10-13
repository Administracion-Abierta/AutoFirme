package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGSegmentImageInputStream.class */
final class JPEGSegmentImageInputStream extends ImageInputStreamImpl {
    private final ImageInputStream stream;
    private final JPEGSegmentWarningListener warningListener;
    private final ComponentIdSet componentIds;
    private final List<Segment> segments;
    private int currentSegment;
    private Segment segment;

    JPEGSegmentImageInputStream(ImageInputStream imageInputStream, JPEGSegmentWarningListener jPEGSegmentWarningListener) {
        this.componentIds = new ComponentIdSet();
        this.segments = new ArrayList(64);
        this.currentSegment = -1;
        this.stream = (ImageInputStream) Validate.notNull(imageInputStream, "stream");
        this.warningListener = (JPEGSegmentWarningListener) Validate.notNull(jPEGSegmentWarningListener, "warningListener");
    }

    JPEGSegmentImageInputStream(ImageInputStream imageInputStream) {
        this(imageInputStream, JPEGSegmentWarningListener.NULL_LISTENER);
    }

    private void processWarningOccured(String str) {
        this.warningListener.warningOccurred(str);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private Segment fetchSegment() throws IIOException, IOException {
        if (this.currentSegment == -1) {
            streamInit();
        } else {
            this.segment = this.segments.get(this.currentSegment);
        }
        if (this.streamPos >= this.segment.end()) {
            int i = this.currentSegment;
            while (true) {
                i++;
                if (i < this.segments.size()) {
                    this.currentSegment = i;
                    this.segment = this.segments.get(this.currentSegment);
                    if (this.streamPos >= this.segment.start && this.streamPos < this.segment.end()) {
                        this.segment.seek(this.stream, this.streamPos);
                        return this.segment;
                    }
                } else {
                    this.stream.seek(this.segment.realEnd());
                    while (true) {
                        int i2 = 0;
                        int unsignedByte = this.stream.readUnsignedByte();
                        while (!JPEGSegmentUtil.isKnownJPEGMarker(unsignedByte)) {
                            int unsignedByte2 = unsignedByte & 255;
                            while (unsignedByte2 != 255) {
                                unsignedByte2 = this.stream.readUnsignedByte();
                                i2++;
                            }
                            unsignedByte = 65280 | this.stream.readUnsignedByte();
                            while (unsignedByte == 65535) {
                                unsignedByte = 65280 | this.stream.readUnsignedByte();
                                i2++;
                            }
                        }
                        if (i2 != 0) {
                            processWarningOccured(String.format("Corrupt JPEG data: %d extraneous bytes before marker 0x%02x", Integer.valueOf(i2), Integer.valueOf(unsignedByte & 255)));
                        }
                        long streamPosition = this.stream.getStreamPosition() - 2;
                        boolean zIsAppSegmentMarker = isAppSegmentMarker(unsignedByte);
                        boolean z = unsignedByte == 65518 && isAppSegmentWithId("Adobe", this.stream);
                        boolean z2 = unsignedByte == 65505 && isAppSegmentWithId("Exif", this.stream);
                        if (zIsAppSegmentMarker && !z2 && !z) {
                            this.stream.seek(streamPosition + 2 + ((long) this.stream.readUnsignedShort()));
                        } else {
                            if (unsignedByte == 65497) {
                                this.segment = new Segment(unsignedByte, streamPosition, this.segment.end(), 2L);
                            } else {
                                long unsignedShort = 2 + this.stream.readUnsignedShort();
                                if (z && unsignedShort != 16) {
                                    this.segment = new AdobeAPP14Replacement(streamPosition, this.segment.end(), unsignedShort, this.stream);
                                } else if (unsignedByte == 65499) {
                                    int i3 = this.stream.read();
                                    if ((i3 & 16) == 16) {
                                        processWarningOccured("16 bit DQT encountered");
                                        this.segment = new DownsampledDQTReplacement(streamPosition, this.segment.end(), unsignedShort, i3, this.stream);
                                    } else {
                                        this.segment = new Segment(unsignedByte, streamPosition, this.segment.end(), unsignedShort);
                                    }
                                } else if (isSOFMarker(unsignedByte)) {
                                    this.segment = new ReplacementSegment(unsignedByte, streamPosition, this.segment.end(), unsignedShort, readReplaceDuplicateSOFnComponentIds(unsignedByte, unsignedShort));
                                } else if (unsignedByte == 65498) {
                                    this.segment = new ReplacementSegment(unsignedByte, streamPosition, this.segment.end(), unsignedShort, readReplaceDuplicateSOSComponentSelectors(unsignedShort));
                                } else {
                                    this.segment = new Segment(unsignedByte, streamPosition, this.segment.end(), unsignedShort);
                                }
                            }
                            this.segments.add(this.segment);
                            this.currentSegment = this.segments.size() - 1;
                            if (unsignedByte == 65498) {
                                this.segments.add(new Segment(-1, this.segment.realEnd(), this.segment.end(), Long.MAX_VALUE - this.segment.realEnd()));
                            }
                            if (this.streamPos >= this.segment.start && this.streamPos < this.segment.end()) {
                                break;
                            }
                            this.stream.seek(this.segment.realEnd());
                        }
                    }
                    this.segment.seek(this.stream, this.streamPos);
                    break;
                }
            }
        } else if (this.streamPos < this.segment.start) {
            int i4 = this.currentSegment;
            while (true) {
                i4--;
                if (i4 < 0) {
                    break;
                }
                this.currentSegment = i4;
                this.segment = this.segments.get(this.currentSegment);
                if (this.streamPos >= this.segment.start && this.streamPos < this.segment.end()) {
                    this.segment.seek(this.stream, this.streamPos);
                    break;
                }
            }
        } else {
            this.segment.seek(this.stream, this.streamPos);
        }
        return this.segment;
    }

    private byte[] readReplaceDuplicateSOSComponentSelectors(long j) throws IOException {
        byte[] segment = readSegment(JPEG.SOS, (int) j, this.stream);
        ComponentIdSet componentIdSet = new ComponentIdSet();
        boolean z = false;
        for (int i = 5; i < j - 3; i += 2) {
            int i2 = segment[i] & 255;
            if (!componentIdSet.add(i2)) {
                processWarningOccured(String.format("Duplicate component ID %d in SOS", Integer.valueOf(i2)));
                z = true;
            }
        }
        if (z) {
            int i3 = 0;
            for (int i4 = 5; i3 < this.componentIds.size() && i4 < j - 3; i4 += 2) {
                segment[i4] = (byte) this.componentIds.get(i3);
                i3++;
            }
        }
        return segment;
    }

    private byte[] readReplaceDuplicateSOFnComponentIds(int i, long j) throws IOException {
        byte[] segment = readSegment(i, (int) j, this.stream);
        for (int i2 = 10; i2 < j; i2 += 3) {
            int i3 = segment[i2] & 255;
            if (!this.componentIds.add(i3)) {
                processWarningOccured(String.format("Duplicate component ID %d in SOF", Integer.valueOf(i3)));
                do {
                    i3++;
                    if (this.componentIds.size() >= 4 || this.componentIds.add(i3)) {
                        break;
                    }
                } while (i3 < 255);
                segment[i2] = (byte) i3;
            }
        }
        return segment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] readSegment(int i, int i2, ImageInputStream imageInputStream) throws IOException {
        byte[] bArr = new byte[i2];
        bArr[0] = (byte) ((i >> 8) & 255);
        bArr[1] = (byte) (i & 255);
        bArr[2] = (byte) (((i2 - 2) >> 8) & 255);
        bArr[3] = (byte) ((i2 - 2) & 255);
        imageInputStream.readFully(bArr, 4, i2 - 4);
        return bArr;
    }

    private static boolean isAppSegmentWithId(String str, ImageInputStream imageInputStream) throws IOException {
        Validate.notNull(str, "segmentId");
        imageInputStream.mark();
        try {
            byte[] bArr = new byte[Math.min(str.length() + 1, imageInputStream.readUnsignedShort() - 2)];
            imageInputStream.readFully(bArr);
            return str.equals(asNullTerminatedAsciiString(bArr, 0));
        } finally {
            imageInputStream.reset();
        }
    }

    static String asNullTerminatedAsciiString(byte[] bArr, int i) {
        for (int i2 = 0; i2 < bArr.length - i; i2++) {
            if (bArr[i + i2] == 0 || i2 > 255) {
                return asAsciiString(bArr, i, i + i2);
            }
        }
        return null;
    }

    static String asAsciiString(byte[] bArr, int i, int i2) {
        return new String(bArr, i, i2, StandardCharsets.US_ASCII);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private void streamInit() throws IIOException, IOException {
        long streamPosition = this.stream.getStreamPosition();
        try {
            int unsignedShort = this.stream.readUnsignedShort();
            if (unsignedShort != 65496) {
                throw new IIOException(String.format("Not a JPEG stream (starts with: 0x%04x, expected SOI: 0x%04x)", Integer.valueOf(unsignedShort), Integer.valueOf(JPEG.SOI)));
            }
            this.segment = new Segment(unsignedShort, streamPosition, 0L, 2L);
            this.segments.add(this.segment);
            this.currentSegment = this.segments.size() - 1;
        } catch (EOFException e) {
            throw new IIOException(String.format("Not a JPEG stream (short stream. expected SOI: 0x%04x)", Integer.valueOf(JPEG.SOI)), e);
        }
    }

    static boolean isAppSegmentMarker(int i) {
        return i >= 65504 && i <= 65519;
    }

    static boolean isSOFMarker(int i) {
        switch (i) {
            case JPEG.SOF0 /* 65472 */:
            case JPEG.SOF1 /* 65473 */:
            case JPEG.SOF2 /* 65474 */:
            case JPEG.SOF3 /* 65475 */:
            case JPEG.SOF5 /* 65477 */:
            case JPEG.SOF6 /* 65478 */:
            case JPEG.SOF7 /* 65479 */:
            case JPEG.SOF9 /* 65481 */:
            case JPEG.SOF10 /* 65482 */:
            case JPEG.SOF11 /* 65483 */:
            case JPEG.SOF13 /* 65485 */:
            case JPEG.SOF14 /* 65486 */:
            case JPEG.SOF15 /* 65487 */:
                return true;
            case JPEG.DHT /* 65476 */:
            case 65480:
            case JPEG.DAC /* 65484 */:
            default:
                return false;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private void repositionAsNecessary() throws IIOException, IOException {
        if (this.segment == null || this.streamPos < this.segment.start || this.streamPos >= this.segment.end()) {
            try {
                fetchSegment();
            } catch (EOFException e) {
                this.segments.add(new Segment(0, this.segment.realEnd(), this.segment.end(), 4294967294L - this.segment.realEnd()));
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public int read() throws IIOException, IOException {
        this.bitOffset = 0;
        repositionAsNecessary();
        int i = this.segment.read(this.stream);
        if (i != -1) {
            this.streamPos++;
        }
        return i;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public int read(byte[] bArr, int i, int i2) throws IIOException, IOException {
        int i3;
        this.bitOffset = 0;
        int i4 = 0;
        while (true) {
            i3 = i4;
            if (i3 >= i2) {
                break;
            }
            repositionAsNecessary();
            long jEnd = this.segment.end() - this.streamPos;
            int i5 = jEnd <= 0 ? -1 : this.segment.read(this.stream, bArr, i + i3, (int) Math.min(i2 - i3, jEnd));
            if (i5 == -1) {
                if (i3 != 0) {
                    break;
                }
                return -1;
            }
            this.streamPos += (long) i5;
            i4 = i3 + i5;
        }
        return i3;
    }

    @Deprecated
    protected void finalize() {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGSegmentImageInputStream$Segment.class */
    static class Segment {
        final int marker;
        final long realStart;
        final long start;
        final long length;

        Segment(int i, long j, long j2, long j3) {
            this.marker = i;
            this.realStart = j;
            this.start = j2;
            this.length = j3;
        }

        long realEnd() {
            return this.realStart + this.length;
        }

        long end() {
            return this.start + this.length;
        }

        public void seek(ImageInputStream imageInputStream, long j) throws IOException {
            imageInputStream.seek((this.realStart + j) - this.start);
        }

        public int read(ImageInputStream imageInputStream) throws IOException {
            return imageInputStream.read();
        }

        public int read(ImageInputStream imageInputStream, byte[] bArr, int i, int i2) throws IOException {
            return imageInputStream.read(bArr, i, i2);
        }

        public String toString() {
            return String.format("0x%04x[%d-%d]", Integer.valueOf(this.marker), Long.valueOf(this.realStart), Long.valueOf(realEnd()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGSegmentImageInputStream$AdobeAPP14Replacement.class */
    static final class AdobeAPP14Replacement extends ReplacementSegment {
        AdobeAPP14Replacement(long j, long j2, long j3, ImageInputStream imageInputStream) throws IOException {
            super(JPEG.APP14, j, j2, j3, createMarkerFixedLength(imageInputStream));
        }

        private static byte[] createMarkerFixedLength(ImageInputStream imageInputStream) throws IOException {
            return JPEGSegmentImageInputStream.readSegment(JPEG.APP14, 16, imageInputStream);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGSegmentImageInputStream$DownsampledDQTReplacement.class */
    static final class DownsampledDQTReplacement extends ReplacementSegment {
        DownsampledDQTReplacement(long j, long j2, long j3, int i, ImageInputStream imageInputStream) throws IOException {
            super(JPEG.DQT, j, j2, j3, createMarkerFixedLength((int) j3, i, imageInputStream));
        }

        private static byte[] createMarkerFixedLength(int i, int i2, ImageInputStream imageInputStream) throws IOException {
            int i3 = i / 128;
            int i4 = 2 + (65 * i3);
            byte[] bArr = new byte[i];
            bArr[0] = -1;
            bArr[1] = -37;
            bArr[2] = (byte) ((i4 >> 8) & 255);
            bArr[3] = (byte) (i4 & 255);
            bArr[4] = (byte) (i2 & 15);
            imageInputStream.readFully(bArr, 5, bArr.length - 5);
            int i5 = 4;
            int i6 = 4;
            for (int i7 = 0; i7 < i3; i7++) {
                int i8 = i5;
                int i9 = i5 + 1;
                int i10 = i6;
                int i11 = i6 + 1;
                bArr[i8] = (byte) (bArr[i10] & 15);
                for (int i12 = 0; i12 < 64; i12++) {
                    bArr[i9 + i12] = bArr[i11 + 1 + (i12 * 2)];
                }
                i5 = i9 + 64;
                i6 = i11 + 128;
            }
            return Arrays.copyOfRange(bArr, 0, i4 + 2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGSegmentImageInputStream$ReplacementSegment.class */
    static class ReplacementSegment extends Segment {
        final long realLength;
        final byte[] data;
        int pos;

        ReplacementSegment(int i, long j, long j2, long j3, byte[] bArr) {
            super(i, j, j2, bArr.length);
            this.realLength = j3;
            this.data = bArr;
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.JPEGSegmentImageInputStream.Segment
        long realEnd() {
            return this.realStart + this.realLength;
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.JPEGSegmentImageInputStream.Segment
        public void seek(ImageInputStream imageInputStream, long j) throws IOException {
            this.pos = (int) (j - this.start);
            super.seek(imageInputStream, j);
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.JPEGSegmentImageInputStream.Segment
        public int read(ImageInputStream imageInputStream) {
            if (this.data.length <= this.pos) {
                return -1;
            }
            byte[] bArr = this.data;
            int i = this.pos;
            this.pos = i + 1;
            return bArr[i] & 255;
        }

        @Override // com.twelvemonkeys.imageio.plugins.jpeg.JPEGSegmentImageInputStream.Segment
        public int read(ImageInputStream imageInputStream, byte[] bArr, int i, int i2) {
            int length = this.data.length - this.pos;
            if (length <= 0) {
                return -1;
            }
            int iMin = Math.min(length, i2);
            System.arraycopy(this.data, this.pos, bArr, i, iMin);
            this.pos += iMin;
            return iMin;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGSegmentImageInputStream$ComponentIdSet.class */
    static final class ComponentIdSet {
        final int[] values = new int[4];
        int size;

        ComponentIdSet() {
        }

        boolean add(int i) {
            if (contains(i) || this.size >= this.values.length) {
                return false;
            }
            int[] iArr = this.values;
            int i2 = this.size;
            this.size = i2 + 1;
            iArr[i2] = i;
            return true;
        }

        boolean contains(int i) {
            for (int i2 = 0; i2 < this.size; i2++) {
                if (this.values[i2] == i) {
                    return true;
                }
            }
            return false;
        }

        int size() {
            return this.size;
        }

        int get(int i) {
            return this.values[i];
        }

        public String toString() {
            return Arrays.toString(Arrays.copyOf(this.values, this.size));
        }
    }
}
