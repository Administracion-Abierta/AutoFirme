package com.aowagie.text.pdf.codec;

import com.aowagie.text.pdf.RandomAccessFileOrArray;
import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/JBIG2SegmentReader.class */
class JBIG2SegmentReader {
    private static final int PAGE_INFORMATION = 48;
    private static final int END_OF_PAGE = 49;
    private static final int END_OF_FILE = 51;
    private final RandomAccessFileOrArray ra;
    private boolean sequential;
    private boolean number_of_pages_known;
    private final SortedMap segments = new TreeMap();
    private final SortedMap pages = new TreeMap();
    private final SortedSet globals = new TreeSet();
    private int number_of_pages = -1;
    private boolean read = false;

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/JBIG2SegmentReader$JBIG2Segment.class */
    private static class JBIG2Segment implements Comparable {
        private final int segmentNumber;
        private long dataLength;
        private int page;
        private int[] referredToSegmentNumbers;
        private boolean[] segmentRetentionFlags;
        private int type;
        private boolean deferredNonRetain;
        private int countOfReferredToSegments;
        private byte[] data;
        private byte[] headerData;
        private boolean page_association_size;
        private int page_association_offset;

        private JBIG2Segment(int segment_number) {
            this.dataLength = -1L;
            this.page = -1;
            this.referredToSegmentNumbers = null;
            this.segmentRetentionFlags = null;
            this.type = -1;
            this.deferredNonRetain = false;
            this.countOfReferredToSegments = -1;
            this.data = null;
            this.headerData = null;
            this.page_association_size = false;
            this.page_association_offset = -1;
            this.segmentNumber = segment_number;
        }

        @Override // java.lang.Comparable
        public int compareTo(Object o) {
            return compareTo((JBIG2Segment) o);
        }

        private int compareTo(JBIG2Segment s) {
            return this.segmentNumber - s.segmentNumber;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/JBIG2SegmentReader$JBIG2Page.class */
    public static class JBIG2Page {
        private final JBIG2SegmentReader sr;
        private final SortedMap segs;
        int pageBitmapWidth;
        int pageBitmapHeight;

        private JBIG2Page(int page, JBIG2SegmentReader sr) {
            this.segs = new TreeMap();
            this.pageBitmapWidth = -1;
            this.pageBitmapHeight = -1;
            this.sr = sr;
        }

        public byte[] getData(boolean for_embedding) throws IOException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (Integer sn : this.segs.keySet()) {
                JBIG2Segment s = (JBIG2Segment) this.segs.get(sn);
                if (!for_embedding || (s.type != 51 && s.type != 49)) {
                    if (for_embedding) {
                        byte[] headerData_emb = JBIG2SegmentReader.copyByteArray(s.headerData);
                        if (s.page_association_size) {
                            headerData_emb[s.page_association_offset] = 0;
                            headerData_emb[s.page_association_offset + 1] = 0;
                            headerData_emb[s.page_association_offset + 2] = 0;
                            headerData_emb[s.page_association_offset + 3] = 1;
                        } else {
                            headerData_emb[s.page_association_offset] = 1;
                        }
                        os.write(headerData_emb);
                    } else {
                        os.write(s.headerData);
                    }
                    os.write(s.data);
                }
            }
            os.close();
            return os.toByteArray();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addSegment(JBIG2Segment s) {
            this.segs.put(Integer.valueOf(s.segmentNumber), s);
        }
    }

    public JBIG2SegmentReader(RandomAccessFileOrArray ra) throws IOException {
        this.ra = ra;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] copyByteArray(byte[] b) {
        byte[] bc = new byte[b.length];
        System.arraycopy(b, 0, bc, 0, b.length);
        return bc;
    }

    public void read() throws IOException {
        JBIG2Segment tmp;
        if (this.read) {
            throw new IllegalStateException("already attempted a read() on this Jbig2 File");
        }
        this.read = true;
        readFileHeader();
        if (!this.sequential) {
            do {
                tmp = readHeader();
                this.segments.put(Integer.valueOf(tmp.segmentNumber), tmp);
            } while (tmp.type != 51);
            Iterator segs = this.segments.keySet().iterator();
            while (segs.hasNext()) {
                readSegment((JBIG2Segment) this.segments.get(segs.next()));
            }
            return;
        }
        do {
            JBIG2Segment tmp2 = readHeader();
            readSegment(tmp2);
            this.segments.put(Integer.valueOf(tmp2.segmentNumber), tmp2);
        } while (this.ra.getFilePointer() < this.ra.length());
    }

    private void readSegment(JBIG2Segment s) throws IOException {
        int ptr = this.ra.getFilePointer();
        if (s.dataLength != 4294967295L) {
            byte[] data = new byte[(int) s.dataLength];
            this.ra.read(data);
            s.data = data;
            if (s.type == 48) {
                int last = this.ra.getFilePointer();
                this.ra.seek(ptr);
                int page_bitmap_width = this.ra.readInt();
                int page_bitmap_height = this.ra.readInt();
                this.ra.seek(last);
                JBIG2Page p = (JBIG2Page) this.pages.get(Integer.valueOf(s.page));
                if (p == null) {
                    throw new IllegalStateException("referring to widht/height of page we havent seen yet? " + s.page);
                }
                p.pageBitmapWidth = page_bitmap_width;
                p.pageBitmapHeight = page_bitmap_height;
            }
        }
    }

    private JBIG2Segment readHeader() throws IOException {
        int segment_page_association;
        int ptr = this.ra.getFilePointer();
        int segment_number = this.ra.readInt();
        JBIG2Segment s = new JBIG2Segment(segment_number);
        int segment_header_flags = this.ra.read();
        boolean deferred_non_retain = (segment_header_flags & 128) == 128;
        s.deferredNonRetain = deferred_non_retain;
        boolean page_association_size = (segment_header_flags & 64) == 64;
        int segment_type = segment_header_flags & 63;
        s.type = segment_type;
        int referred_to_byte0 = this.ra.read();
        int count_of_referred_to_segments = (referred_to_byte0 & 224) >> 5;
        boolean[] segment_retention_flags = null;
        if (count_of_referred_to_segments == 7) {
            this.ra.seek(this.ra.getFilePointer() - 1);
            count_of_referred_to_segments = this.ra.readInt() & 536870911;
            segment_retention_flags = new boolean[count_of_referred_to_segments + 1];
            int i = 0;
            int referred_to_current_byte = 0;
            do {
                int j = i % 8;
                if (j == 0) {
                    referred_to_current_byte = this.ra.read();
                }
                segment_retention_flags[i] = (((1 << j) & referred_to_current_byte) >> j) == 1;
                i++;
            } while (i <= count_of_referred_to_segments);
        } else if (count_of_referred_to_segments <= 4) {
            segment_retention_flags = new boolean[count_of_referred_to_segments + 1];
            int referred_to_byte1 = referred_to_byte0 & 31;
            for (int i2 = 0; i2 <= count_of_referred_to_segments; i2++) {
                segment_retention_flags[i2] = (((1 << i2) & referred_to_byte1) >> i2) == 1;
            }
        } else if (count_of_referred_to_segments == 5 || count_of_referred_to_segments == 6) {
            throw new IllegalStateException("count of referred-to segments had bad value in header for segment " + segment_number + " starting at " + ptr);
        }
        s.segmentRetentionFlags = segment_retention_flags;
        s.countOfReferredToSegments = count_of_referred_to_segments;
        int[] referred_to_segment_numbers = new int[count_of_referred_to_segments + 1];
        for (int i3 = 1; i3 <= count_of_referred_to_segments; i3++) {
            if (segment_number <= 256) {
                referred_to_segment_numbers[i3] = this.ra.read();
            } else if (segment_number <= 65536) {
                referred_to_segment_numbers[i3] = this.ra.readUnsignedShort();
            } else {
                referred_to_segment_numbers[i3] = (int) this.ra.readUnsignedInt();
            }
        }
        s.referredToSegmentNumbers = referred_to_segment_numbers;
        int page_association_offset = this.ra.getFilePointer() - ptr;
        if (page_association_size) {
            segment_page_association = this.ra.readInt();
        } else {
            segment_page_association = this.ra.read();
        }
        if (segment_page_association >= 0) {
            s.page = segment_page_association;
            s.page_association_size = page_association_size;
            s.page_association_offset = page_association_offset;
            if (segment_page_association > 0 && !this.pages.containsKey(Integer.valueOf(segment_page_association))) {
                this.pages.put(Integer.valueOf(segment_page_association), new JBIG2Page(segment_page_association, this));
            }
            if (segment_page_association > 0) {
                ((JBIG2Page) this.pages.get(Integer.valueOf(segment_page_association))).addSegment(s);
            } else {
                this.globals.add(s);
            }
            long segment_data_length = this.ra.readUnsignedInt();
            s.dataLength = segment_data_length;
            int end_ptr = this.ra.getFilePointer();
            this.ra.seek(ptr);
            byte[] header_data = new byte[end_ptr - ptr];
            this.ra.read(header_data);
            s.headerData = header_data;
            return s;
        }
        throw new IllegalStateException("page " + segment_page_association + " invalid for segment " + segment_number + " starting at " + ptr);
    }

    private void readFileHeader() throws IOException {
        this.ra.seek(0);
        byte[] idstring = new byte[8];
        this.ra.read(idstring);
        byte[] refidstring = {-105, 74, 66, 50, 13, 10, 26, 10};
        for (int i = 0; i < idstring.length; i++) {
            if (idstring[i] != refidstring[i]) {
                throw new IllegalStateException("file header idstring not good at byte " + i);
            }
        }
        int fileheaderflags = this.ra.read();
        this.sequential = (fileheaderflags & 1) == 1;
        this.number_of_pages_known = (fileheaderflags & 2) == 0;
        if ((fileheaderflags & DHCPOptions.OPTION_PROXY_AUTODISCOVERY) != 0) {
            throw new IllegalStateException("file header flags bits 2-7 not 0");
        }
        if (this.number_of_pages_known) {
            this.number_of_pages = this.ra.readInt();
        }
    }

    private int numberOfPages() {
        return this.pages.size();
    }

    JBIG2Page getPage(int page) {
        return (JBIG2Page) this.pages.get(Integer.valueOf(page));
    }

    byte[] getGlobal(boolean for_embedding) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            for (JBIG2Segment s : this.globals) {
                if (!for_embedding || (s.type != 51 && s.type != 49)) {
                    os.write(s.headerData);
                    os.write(s.data);
                }
            }
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (os.size() <= 0) {
            return null;
        }
        return os.toByteArray();
    }

    public String toString() {
        if (this.read) {
            return "Jbig2SegmentReader: number of pages: " + numberOfPages();
        }
        return "Jbig2SegmentReader in indeterminate state.";
    }
}
