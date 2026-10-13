package com.ctc.wstx.util;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.dtd.DTDEventListener;
import com.ctc.wstx.sr.InputProblemReporter;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.CharArrayBase64Decoder;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.validation.XMLValidator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/TextBuffer.class */
public final class TextBuffer {
    static final int DEF_INITIAL_BUFFER_SIZE = 500;
    static final int MAX_SEGMENT_LENGTH = 262144;
    static final int INT_SPACE = 32;
    private final ReaderConfig mConfig;
    private char[] mInputBuffer;
    private int mInputStart;
    private int mInputLen;
    private boolean mHasSegments = false;
    private ArrayList<char[]> mSegments;
    private int mSegmentSize;
    private char[] mCurrentSegment;
    private int mCurrentSize;
    private String mResultString;
    private char[] mResultArray;
    public static final int MAX_INDENT_SPACES = 32;
    public static final int MAX_INDENT_TABS = 8;
    private static final String sIndSpaces = "\n                                 ";
    private static final char[] sIndSpacesArray = sIndSpaces.toCharArray();
    private static final String[] sIndSpacesStrings = new String[sIndSpacesArray.length];
    private static final String sIndTabs = "\n\t\t\t\t\t\t\t\t\t";
    private static final char[] sIndTabsArray = sIndTabs.toCharArray();
    private static final String[] sIndTabsStrings = new String[sIndTabsArray.length];

    private TextBuffer(ReaderConfig cfg) {
        this.mConfig = cfg;
    }

    public static TextBuffer createRecyclableBuffer(ReaderConfig cfg) {
        return new TextBuffer(cfg);
    }

    public static TextBuffer createTemporaryBuffer() {
        return new TextBuffer(null);
    }

    public void recycle(boolean force) {
        if (this.mConfig != null && this.mCurrentSegment != null) {
            if (force) {
                resetWithEmpty();
            } else {
                if (this.mInputStart < 0 && this.mSegmentSize + this.mCurrentSize > 0) {
                    return;
                }
                if (this.mSegments != null && this.mSegments.size() > 0) {
                    this.mSegments.clear();
                    this.mSegmentSize = 0;
                }
            }
            char[] buf = this.mCurrentSegment;
            this.mCurrentSegment = null;
            this.mConfig.freeMediumCBuffer(buf);
        }
    }

    public void resetWithEmpty() {
        this.mInputBuffer = null;
        this.mInputStart = -1;
        this.mInputLen = 0;
        this.mResultString = null;
        this.mResultArray = null;
        if (this.mHasSegments) {
            clearSegments();
        }
        this.mCurrentSize = 0;
    }

    public void resetWithEmptyString() {
        this.mInputBuffer = null;
        this.mInputStart = -1;
        this.mInputLen = 0;
        this.mResultString = "";
        this.mResultArray = null;
        if (this.mHasSegments) {
            clearSegments();
        }
        this.mCurrentSize = 0;
    }

    public void resetWithShared(char[] buf, int start, int len) {
        this.mInputBuffer = buf;
        this.mInputStart = start;
        this.mInputLen = len;
        this.mResultString = null;
        this.mResultArray = null;
        if (this.mHasSegments) {
            clearSegments();
        }
    }

    public void resetWithCopy(char[] buf, int start, int len) {
        this.mInputBuffer = null;
        this.mInputStart = -1;
        this.mInputLen = 0;
        this.mResultString = null;
        this.mResultArray = null;
        if (this.mHasSegments) {
            clearSegments();
        }
        if (this.mCurrentSegment == null) {
            this.mCurrentSegment = allocBuffer(len);
        }
        this.mSegmentSize = 0;
        this.mCurrentSize = 0;
        append(buf, start, len);
    }

    public void resetInitialized() {
        resetWithEmpty();
        if (this.mCurrentSegment == null) {
            this.mCurrentSegment = allocBuffer(0);
        }
    }

    private final char[] allocBuffer(int needed) {
        char[] buf;
        int size = Math.max(needed, 500);
        if (this.mConfig != null && (buf = this.mConfig.allocMediumCBuffer(size)) != null) {
            return buf;
        }
        return new char[size];
    }

    private final void clearSegments() {
        this.mHasSegments = false;
        this.mSegments.clear();
        this.mSegmentSize = 0;
        this.mCurrentSize = 0;
    }

    public void resetWithIndentation(int indCharCount, char indChar) {
        String text;
        this.mInputStart = 0;
        this.mInputLen = indCharCount + 1;
        if (indChar == '\t') {
            this.mInputBuffer = sIndTabsArray;
            text = sIndTabsStrings[indCharCount];
            if (text == null) {
                String[] strArr = sIndTabsStrings;
                String strSubstring = sIndTabs.substring(0, this.mInputLen);
                text = strSubstring;
                strArr[indCharCount] = strSubstring;
            }
        } else {
            this.mInputBuffer = sIndSpacesArray;
            text = sIndSpacesStrings[indCharCount];
            if (text == null) {
                String[] strArr2 = sIndSpacesStrings;
                String strSubstring2 = sIndSpaces.substring(0, this.mInputLen);
                text = strSubstring2;
                strArr2[indCharCount] = strSubstring2;
            }
        }
        this.mResultString = text;
        this.mResultArray = null;
        if (this.mSegments != null && this.mSegments.size() > 0) {
            this.mSegments.clear();
            this.mSegmentSize = 0;
            this.mCurrentSize = 0;
        }
    }

    public int size() {
        if (this.mInputStart >= 0) {
            return this.mInputLen;
        }
        return this.mSegmentSize + this.mCurrentSize;
    }

    public int getTextStart() {
        if (this.mInputStart >= 0) {
            return this.mInputStart;
        }
        return 0;
    }

    public char[] getTextBuffer() {
        if (this.mInputStart >= 0) {
            return this.mInputBuffer;
        }
        if (this.mSegments == null || this.mSegments.size() == 0) {
            return this.mCurrentSegment;
        }
        return contentsAsArray();
    }

    public void decode(TypedValueDecoder tvd) throws IllegalArgumentException {
        char[] buf;
        int start;
        int end;
        if (this.mInputStart >= 0) {
            buf = this.mInputBuffer;
            start = this.mInputStart;
            end = start + this.mInputLen;
        } else {
            buf = getTextBuffer();
            start = 0;
            end = this.mSegmentSize + this.mCurrentSize;
        }
        while (start < end) {
            if (StringUtil.isSpace(buf[start])) {
                start++;
            } else {
                do {
                    end--;
                    if (end <= start) {
                        break;
                    }
                } while (StringUtil.isSpace(buf[end]));
                tvd.decode(buf, start, end + 1);
                return;
            }
        }
        tvd.handleEmptyValue();
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.codehaus.stax2.typed.TypedXMLStreamException] */
    public int decodeElements(TypedArrayDecoder tad, InputProblemReporter rep) {
        int count = 0;
        if (this.mInputStart < 0) {
            if (this.mHasSegments) {
                this.mInputBuffer = buildResultArray();
                this.mInputLen = this.mInputBuffer.length;
                clearSegments();
            } else {
                this.mInputBuffer = this.mCurrentSegment;
                this.mInputLen = this.mCurrentSize;
            }
            this.mInputStart = 0;
        }
        int ptr = this.mInputStart;
        int end = ptr + this.mInputLen;
        char[] buf = this.mInputBuffer;
        int start = ptr;
        loop0: while (ptr < end) {
            while (buf[ptr] <= ' ') {
                try {
                    try {
                        ptr++;
                        if (ptr >= end) {
                            break loop0;
                        }
                    } catch (IllegalArgumentException iae) {
                        Location loc = rep.getLocation();
                        String lexical = new String(buf, start, (ptr - start) - 1);
                        throw new TypedXMLStreamException(lexical, iae.getMessage(), loc, iae);
                    }
                } catch (Throwable th) {
                    this.mInputStart = ptr;
                    this.mInputLen = end - ptr;
                    throw th;
                }
            }
            start = ptr;
            int ptr2 = ptr + 1;
            while (ptr2 < end && buf[ptr2] > ' ') {
                ptr2++;
            }
            count++;
            int tokenEnd = ptr2;
            ptr = ptr2 + 1;
            if (tad.decodeValue(buf, start, tokenEnd)) {
                break;
            }
        }
        this.mInputStart = ptr;
        this.mInputLen = end - ptr;
        return count;
    }

    public void initBinaryChunks(Base64Variant v, CharArrayBase64Decoder dec, boolean firstChunk) {
        if (this.mInputStart < 0) {
            dec.init(v, firstChunk, this.mCurrentSegment, 0, this.mCurrentSize, this.mSegments);
        } else {
            dec.init(v, firstChunk, this.mInputBuffer, this.mInputStart, this.mInputLen, null);
        }
    }

    public String contentsAsString() {
        if (this.mResultString == null) {
            if (this.mResultArray != null) {
                this.mResultString = new String(this.mResultArray);
            } else if (this.mInputStart >= 0) {
                if (this.mInputLen < 1) {
                    this.mResultString = "";
                    return "";
                }
                this.mResultString = new String(this.mInputBuffer, this.mInputStart, this.mInputLen);
            } else {
                int segLen = this.mSegmentSize;
                int currLen = this.mCurrentSize;
                if (segLen == 0) {
                    this.mResultString = currLen == 0 ? "" : new String(this.mCurrentSegment, 0, currLen);
                } else {
                    StringBuilder sb = new StringBuilder(segLen + currLen);
                    if (this.mSegments != null) {
                        int len = this.mSegments.size();
                        for (int i = 0; i < len; i++) {
                            char[] curr = this.mSegments.get(i);
                            sb.append(curr, 0, curr.length);
                        }
                    }
                    sb.append(this.mCurrentSegment, 0, this.mCurrentSize);
                    this.mResultString = sb.toString();
                }
            }
        }
        return this.mResultString;
    }

    public StringBuilder contentsAsStringBuilder(int extraSpace) {
        if (this.mResultString != null) {
            return new StringBuilder(this.mResultString);
        }
        if (this.mResultArray != null) {
            StringBuilder sb = new StringBuilder(this.mResultArray.length + extraSpace);
            sb.append(this.mResultArray, 0, this.mResultArray.length);
            return sb;
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen < 1) {
                return new StringBuilder();
            }
            StringBuilder sb2 = new StringBuilder(this.mInputLen + extraSpace);
            sb2.append(this.mInputBuffer, this.mInputStart, this.mInputLen);
            return sb2;
        }
        int segLen = this.mSegmentSize;
        int currLen = this.mCurrentSize;
        StringBuilder sb3 = new StringBuilder(segLen + currLen + extraSpace);
        if (this.mSegments != null) {
            int len = this.mSegments.size();
            for (int i = 0; i < len; i++) {
                char[] curr = this.mSegments.get(i);
                sb3.append(curr, 0, curr.length);
            }
        }
        sb3.append(this.mCurrentSegment, 0, currLen);
        return sb3;
    }

    public void contentsToStringBuilder(StringBuilder sb) {
        if (this.mResultString != null) {
            sb.append(this.mResultString);
            return;
        }
        if (this.mResultArray != null) {
            sb.append(this.mResultArray);
            return;
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen > 0) {
                sb.append(this.mInputBuffer, this.mInputStart, this.mInputLen);
                return;
            }
            return;
        }
        if (this.mSegments != null) {
            int len = this.mSegments.size();
            for (int i = 0; i < len; i++) {
                char[] curr = this.mSegments.get(i);
                sb.append(curr, 0, curr.length);
            }
        }
        sb.append(this.mCurrentSegment, 0, this.mCurrentSize);
    }

    public char[] contentsAsArray() {
        char[] result = this.mResultArray;
        if (result == null) {
            char[] cArrBuildResultArray = buildResultArray();
            result = cArrBuildResultArray;
            this.mResultArray = cArrBuildResultArray;
        }
        return result;
    }

    public int contentsToArray(int srcStart, char[] dst, int dstStart, int len) {
        int i;
        if (this.mInputStart >= 0) {
            int amount = this.mInputLen - srcStart;
            if (amount > len) {
                amount = len;
            } else if (amount < 0) {
                amount = 0;
            }
            if (amount > 0) {
                System.arraycopy(this.mInputBuffer, this.mInputStart + srcStart, dst, dstStart, amount);
            }
            return amount;
        }
        int totalAmount = 0;
        if (this.mSegments != null) {
            int segc = this.mSegments.size();
            for (int i2 = 0; i2 < segc; i2++) {
                char[] segment = this.mSegments.get(i2);
                int segLen = segment.length;
                int amount2 = segLen - srcStart;
                if (amount2 < 1) {
                    i = srcStart - segLen;
                } else {
                    if (amount2 >= len) {
                        System.arraycopy(segment, srcStart, dst, dstStart, len);
                        return totalAmount + len;
                    }
                    System.arraycopy(segment, srcStart, dst, dstStart, amount2);
                    totalAmount += amount2;
                    dstStart += amount2;
                    len -= amount2;
                    i = 0;
                }
                srcStart = i;
            }
        }
        if (len > 0) {
            int maxAmount = this.mCurrentSize - srcStart;
            if (len > maxAmount) {
                len = maxAmount;
            }
            if (len > 0) {
                System.arraycopy(this.mCurrentSegment, srcStart, dst, dstStart, len);
                totalAmount += len;
            }
        }
        return totalAmount;
    }

    public int rawContentsTo(Writer w) throws IOException {
        if (this.mResultArray != null) {
            w.write(this.mResultArray);
            return this.mResultArray.length;
        }
        if (this.mResultString != null) {
            w.write(this.mResultString);
            return this.mResultString.length();
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen > 0) {
                w.write(this.mInputBuffer, this.mInputStart, this.mInputLen);
            }
            return this.mInputLen;
        }
        int rlen = 0;
        if (this.mSegments != null) {
            int len = this.mSegments.size();
            for (int i = 0; i < len; i++) {
                char[] ch = this.mSegments.get(i);
                w.write(ch);
                rlen += ch.length;
            }
        }
        if (this.mCurrentSize > 0) {
            w.write(this.mCurrentSegment, 0, this.mCurrentSize);
            rlen += this.mCurrentSize;
        }
        return rlen;
    }

    @Deprecated
    public Reader rawContentsViaReader() throws IOException {
        if (this.mResultArray != null) {
            return new CharArrayReader(this.mResultArray);
        }
        if (this.mResultString != null) {
            return new StringReader(this.mResultString);
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen > 0) {
                return new CharArrayReader(this.mInputBuffer, this.mInputStart, this.mInputLen);
            }
            return new StringReader("");
        }
        if (this.mSegments == null || this.mSegments.size() == 0) {
            return new CharArrayReader(this.mCurrentSegment, 0, this.mCurrentSize);
        }
        return new BufferReader(this.mSegments, this.mCurrentSegment, this.mCurrentSize);
    }

    public boolean isAllWhitespace() {
        if (this.mInputStart >= 0) {
            char[] buf = this.mInputBuffer;
            int i = this.mInputStart;
            int last = i + this.mInputLen;
            while (i < last) {
                if (buf[i] <= ' ') {
                    i++;
                } else {
                    return false;
                }
            }
            return true;
        }
        if (this.mSegments != null) {
            int len = this.mSegments.size();
            for (int i2 = 0; i2 < len; i2++) {
                char[] buf2 = this.mSegments.get(i2);
                for (char c : buf2) {
                    if (c > ' ') {
                        return false;
                    }
                }
            }
        }
        char[] buf3 = this.mCurrentSegment;
        int len2 = this.mCurrentSize;
        for (int i3 = 0; i3 < len2; i3++) {
            if (buf3[i3] > ' ') {
                return false;
            }
        }
        return true;
    }

    public boolean equalsString(String str) {
        char[] seg;
        int expLen = str.length();
        if (this.mInputStart >= 0) {
            if (this.mInputLen != expLen) {
                return false;
            }
            for (int i = 0; i < expLen; i++) {
                if (str.charAt(i) != this.mInputBuffer[this.mInputStart + i]) {
                    return false;
                }
            }
            return true;
        }
        if (expLen != size()) {
            return false;
        }
        if (this.mSegments == null || this.mSegments.size() == 0) {
            seg = this.mCurrentSegment;
        } else {
            seg = contentsAsArray();
        }
        for (int i2 = 0; i2 < expLen; i2++) {
            if (seg[i2] != str.charAt(i2)) {
                return false;
            }
        }
        return true;
    }

    public void fireSaxCharacterEvents(ContentHandler h) throws SAXException {
        if (this.mResultArray != null) {
            h.characters(this.mResultArray, 0, this.mResultArray.length);
            return;
        }
        if (this.mInputStart >= 0) {
            h.characters(this.mInputBuffer, this.mInputStart, this.mInputLen);
            return;
        }
        if (this.mSegments != null) {
            int len = this.mSegments.size();
            for (int i = 0; i < len; i++) {
                char[] ch = this.mSegments.get(i);
                h.characters(ch, 0, ch.length);
            }
        }
        if (this.mCurrentSize > 0) {
            h.characters(this.mCurrentSegment, 0, this.mCurrentSize);
        }
    }

    public void fireSaxSpaceEvents(ContentHandler h) throws SAXException {
        if (this.mResultArray != null) {
            h.ignorableWhitespace(this.mResultArray, 0, this.mResultArray.length);
            return;
        }
        if (this.mInputStart >= 0) {
            h.ignorableWhitespace(this.mInputBuffer, this.mInputStart, this.mInputLen);
            return;
        }
        if (this.mSegments != null) {
            int len = this.mSegments.size();
            for (int i = 0; i < len; i++) {
                char[] ch = this.mSegments.get(i);
                h.ignorableWhitespace(ch, 0, ch.length);
            }
        }
        if (this.mCurrentSize > 0) {
            h.ignorableWhitespace(this.mCurrentSegment, 0, this.mCurrentSize);
        }
    }

    public void fireSaxCommentEvent(LexicalHandler h) throws SAXException {
        if (this.mResultArray != null) {
            h.comment(this.mResultArray, 0, this.mResultArray.length);
            return;
        }
        if (this.mInputStart >= 0) {
            h.comment(this.mInputBuffer, this.mInputStart, this.mInputLen);
        } else if (this.mSegments != null && this.mSegments.size() > 0) {
            char[] ch = contentsAsArray();
            h.comment(ch, 0, ch.length);
        } else {
            h.comment(this.mCurrentSegment, 0, this.mCurrentSize);
        }
    }

    public void fireDtdCommentEvent(DTDEventListener l) {
        if (this.mResultArray != null) {
            l.dtdComment(this.mResultArray, 0, this.mResultArray.length);
            return;
        }
        if (this.mInputStart >= 0) {
            l.dtdComment(this.mInputBuffer, this.mInputStart, this.mInputLen);
        } else if (this.mSegments != null && this.mSegments.size() > 0) {
            char[] ch = contentsAsArray();
            l.dtdComment(ch, 0, ch.length);
        } else {
            l.dtdComment(this.mCurrentSegment, 0, this.mCurrentSize);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void validateText(XMLValidator vld, boolean lastSegment) throws XMLStreamException {
        if (this.mInputStart >= 0) {
            vld.validateText(this.mInputBuffer, this.mInputStart, this.mInputStart + this.mInputLen, lastSegment);
        } else {
            vld.validateText(contentsAsString(), lastSegment);
        }
    }

    public void ensureNotShared() {
        if (this.mInputStart >= 0) {
            unshare(16);
        }
    }

    public void append(char c) {
        if (this.mInputStart >= 0) {
            unshare(16);
        }
        this.mResultString = null;
        this.mResultArray = null;
        char[] curr = this.mCurrentSegment;
        if (this.mCurrentSize >= curr.length) {
            expand(1);
            curr = this.mCurrentSegment;
        }
        int i = this.mCurrentSize;
        this.mCurrentSize = i + 1;
        curr[i] = c;
    }

    public void append(char[] c, int start, int len) {
        if (this.mInputStart >= 0) {
            unshare(len);
        }
        this.mResultString = null;
        this.mResultArray = null;
        char[] curr = this.mCurrentSegment;
        int max = curr.length - this.mCurrentSize;
        if (max >= len) {
            System.arraycopy(c, start, curr, this.mCurrentSize, len);
            this.mCurrentSize += len;
            return;
        }
        if (max > 0) {
            System.arraycopy(c, start, curr, this.mCurrentSize, max);
            start += max;
            len -= max;
        }
        expand(len);
        System.arraycopy(c, start, this.mCurrentSegment, 0, len);
        this.mCurrentSize = len;
    }

    public void append(String str) {
        int len = str.length();
        if (this.mInputStart >= 0) {
            unshare(len);
        }
        this.mResultString = null;
        this.mResultArray = null;
        char[] curr = this.mCurrentSegment;
        int max = curr.length - this.mCurrentSize;
        if (max >= len) {
            str.getChars(0, len, curr, this.mCurrentSize);
            this.mCurrentSize += len;
            return;
        }
        if (max > 0) {
            str.getChars(0, max, curr, this.mCurrentSize);
            len -= max;
        }
        expand(len);
        str.getChars(max, max + len, this.mCurrentSegment, 0);
        this.mCurrentSize = len;
    }

    public char[] getCurrentSegment() {
        if (this.mInputStart >= 0) {
            unshare(1);
        } else {
            char[] curr = this.mCurrentSegment;
            if (curr == null) {
                this.mCurrentSegment = allocBuffer(0);
            } else if (this.mCurrentSize >= curr.length) {
                expand(1);
            }
        }
        return this.mCurrentSegment;
    }

    public int getCurrentSegmentSize() {
        return this.mCurrentSize;
    }

    public void setCurrentLength(int len) {
        this.mCurrentSize = len;
    }

    public char[] finishCurrentSegment() {
        if (this.mSegments == null) {
            this.mSegments = new ArrayList<>();
        }
        this.mHasSegments = true;
        this.mSegments.add(this.mCurrentSegment);
        int oldLen = this.mCurrentSegment.length;
        this.mSegmentSize += oldLen;
        char[] curr = new char[calcNewSize(oldLen)];
        this.mCurrentSize = 0;
        this.mCurrentSegment = curr;
        return curr;
    }

    private int calcNewSize(int latestSize) {
        int incr = latestSize < 8000 ? latestSize : latestSize >> 1;
        int size = latestSize + incr;
        return Math.min(size, 262144);
    }

    public String toString() {
        return contentsAsString();
    }

    public void unshare(int needExtra) {
        int len = this.mInputLen;
        this.mInputLen = 0;
        char[] inputBuf = this.mInputBuffer;
        this.mInputBuffer = null;
        int start = this.mInputStart;
        this.mInputStart = -1;
        int needed = len + needExtra;
        if (this.mCurrentSegment == null || needed > this.mCurrentSegment.length) {
            this.mCurrentSegment = allocBuffer(needed);
        }
        if (len > 0) {
            System.arraycopy(inputBuf, start, this.mCurrentSegment, 0, len);
        }
        this.mSegmentSize = 0;
        this.mCurrentSize = len;
    }

    private void expand(int roomNeeded) {
        if (this.mSegments == null) {
            this.mSegments = new ArrayList<>();
        }
        char[] curr = this.mCurrentSegment;
        this.mHasSegments = true;
        this.mSegments.add(curr);
        int oldLen = curr.length;
        this.mSegmentSize += oldLen;
        int newSize = Math.max(roomNeeded, calcNewSize(oldLen));
        this.mCurrentSize = 0;
        this.mCurrentSegment = new char[newSize];
    }

    private char[] buildResultArray() {
        char[] result;
        if (this.mResultString != null) {
            return this.mResultString.toCharArray();
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen < 1) {
                return DataUtil.getEmptyCharArray();
            }
            result = new char[this.mInputLen];
            System.arraycopy(this.mInputBuffer, this.mInputStart, result, 0, this.mInputLen);
        } else {
            int size = size();
            if (size < 1) {
                return DataUtil.getEmptyCharArray();
            }
            int offset = 0;
            result = new char[size];
            if (this.mSegments != null) {
                int len = this.mSegments.size();
                for (int i = 0; i < len; i++) {
                    char[] curr = this.mSegments.get(i);
                    int currLen = curr.length;
                    System.arraycopy(curr, 0, result, offset, currLen);
                    offset += currLen;
                }
            }
            System.arraycopy(this.mCurrentSegment, 0, result, offset, this.mCurrentSize);
        }
        return result;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/TextBuffer$BufferReader.class */
    private static final class BufferReader extends Reader {
        ArrayList<char[]> _segments;
        char[] _currentSegment;
        final int _currentLength;
        int _segmentIndex = 0;
        int _currentOffset = 0;
        int _segmentOffset = 0;

        public BufferReader(ArrayList<char[]> segs, char[] currSeg, int currSegLen) {
            this._segments = segs;
            this._currentSegment = currSeg;
            this._currentLength = currSegLen;
        }

        @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this._segments = null;
            this._currentSegment = null;
        }

        @Override // java.io.Reader
        public void mark(int x) throws IOException {
            throw new IOException("mark() not supported");
        }

        @Override // java.io.Reader
        public boolean markSupported() {
            return false;
        }

        @Override // java.io.Reader
        public int read(char[] cbuf, int offset, int len) {
            if (len < 1) {
                return 0;
            }
            while (this._segments != null) {
                char[] curr = this._segments.get(this._segmentIndex);
                int max = curr.length - this._segmentOffset;
                if (len <= max) {
                    System.arraycopy(curr, this._segmentOffset, cbuf, offset, len);
                    this._segmentOffset += len;
                    return (offset + len) - offset;
                }
                if (max > 0) {
                    System.arraycopy(curr, this._segmentOffset, cbuf, offset, max);
                    offset += max;
                    len -= max;
                }
                int i = this._segmentIndex + 1;
                this._segmentIndex = i;
                if (i >= this._segments.size()) {
                    this._segments = null;
                } else {
                    this._segmentOffset = 0;
                }
            }
            if (len > 0 && this._currentSegment != null) {
                int max2 = this._currentLength - this._currentOffset;
                if (len >= max2) {
                    len = max2;
                    System.arraycopy(this._currentSegment, this._currentOffset, cbuf, offset, len);
                    this._currentSegment = null;
                } else {
                    System.arraycopy(this._currentSegment, this._currentOffset, cbuf, offset, len);
                    this._currentOffset += len;
                }
                offset += len;
            }
            if (offset == offset) {
                return -1;
            }
            return offset - offset;
        }

        @Override // java.io.Reader
        public boolean ready() {
            return true;
        }

        @Override // java.io.Reader
        public void reset() throws IOException {
            throw new IOException("reset() not supported");
        }

        @Override // java.io.Reader
        public long skip(long amount) {
            if (amount < 0) {
                return 0L;
            }
            while (this._segments != null) {
                char[] curr = this._segments.get(this._segmentIndex);
                int max = curr.length - this._segmentOffset;
                if (max >= amount) {
                    this._segmentOffset += (int) amount;
                    return amount;
                }
                amount -= (long) max;
                int i = this._segmentIndex + 1;
                this._segmentIndex = i;
                if (i >= this._segments.size()) {
                    this._segments = null;
                } else {
                    this._segmentOffset = 0;
                }
            }
            if (amount > 0 && this._currentSegment != null) {
                int max2 = this._currentLength - this._currentOffset;
                if (amount >= max2) {
                    amount -= (long) max2;
                    this._currentSegment = null;
                } else {
                    amount = 0;
                    this._currentOffset += (int) 0;
                }
            }
            if (amount == amount) {
                return -1L;
            }
            return amount - amount;
        }
    }
}
