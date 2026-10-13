package org.codehaus.stax2.ri;

import javax.xml.stream.XMLStreamConstants;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/Stax2Util.class */
public final class Stax2Util implements XMLStreamConstants {
    private Stax2Util() {
    }

    public static String eventTypeDesc(int type) {
        switch (type) {
            case 1:
                return "START_ELEMENT";
            case 2:
                return "END_ELEMENT";
            case 3:
                return "PROCESSING_INSTRUCTION";
            case 4:
                return "CHARACTERS";
            case 5:
                return "COMMENT";
            case 6:
                return "SPACE";
            case 7:
                return "START_DOCUMENT";
            case 8:
                return "END_DOCUMENT";
            case 9:
                return "ENTITY_REFERENCE";
            case 10:
            default:
                return "[" + type + "]";
            case 11:
                return "DTD";
            case 12:
                return "CDATA";
        }
    }

    public static String trimSpaces(String lexical) {
        int end = lexical.length();
        int start = 0;
        while (start < end) {
            if (_isSpace(lexical.charAt(start))) {
                start++;
            } else {
                int end2 = end - 1;
                if (!_isSpace(lexical.charAt(end2))) {
                    return start == 0 ? lexical : lexical.substring(start);
                }
                do {
                    end2--;
                    if (end2 <= start) {
                        break;
                    }
                } while (_isSpace(lexical.charAt(end2)));
                return lexical.substring(start, end2 + 1);
            }
        }
        return null;
    }

    private static final boolean _isSpace(char c) {
        return c <= ' ';
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/Stax2Util$TextBuffer.class */
    public static final class TextBuffer {
        private String mText = null;
        private StringBuffer mBuilder = null;

        public void reset() {
            this.mText = null;
            this.mBuilder = null;
        }

        public void append(String text) {
            int len = text.length();
            if (len > 0) {
                if (this.mText != null) {
                    this.mBuilder = new StringBuffer(this.mText.length() + len);
                    this.mBuilder.append(this.mText);
                    this.mText = null;
                }
                if (this.mBuilder != null) {
                    this.mBuilder.append(text);
                } else {
                    this.mText = text;
                }
            }
        }

        public String get() {
            if (this.mText != null) {
                return this.mText;
            }
            if (this.mBuilder != null) {
                return this.mBuilder.toString();
            }
            return "";
        }

        public boolean isEmpty() {
            return this.mText == null && this.mBuilder == null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/Stax2Util$ByteAggregator.class */
    public static final class ByteAggregator {
        private static final byte[] NO_BYTES = new byte[0];
        private static final int INITIAL_BLOCK_SIZE = 500;
        static final int DEFAULT_BLOCK_ARRAY_SIZE = 100;
        private byte[][] mBlocks;
        private int mBlockCount;
        private int mTotalLen;
        private byte[] mSpareBlock;

        public byte[] startAggregation() {
            this.mTotalLen = 0;
            this.mBlockCount = 0;
            byte[] result = this.mSpareBlock;
            if (result == null) {
                result = new byte[500];
            } else {
                this.mSpareBlock = null;
            }
            return result;
        }

        public byte[] addFullBlock(byte[] block) {
            int blockLen = block.length;
            if (this.mBlocks == null) {
                this.mBlocks = new byte[100][];
            } else {
                int oldLen = this.mBlocks.length;
                if (this.mBlockCount >= oldLen) {
                    byte[][] old = this.mBlocks;
                    this.mBlocks = new byte[oldLen + oldLen][];
                    System.arraycopy(old, 0, this.mBlocks, 0, oldLen);
                }
            }
            this.mBlocks[this.mBlockCount] = block;
            this.mBlockCount++;
            this.mTotalLen += blockLen;
            int newSize = Math.max(this.mTotalLen >> 1, 1000);
            return new byte[newSize];
        }

        public byte[] aggregateAll(byte[] lastBlock, int lastLen) {
            int totalLen = this.mTotalLen + lastLen;
            if (totalLen == 0) {
                return NO_BYTES;
            }
            byte[] result = new byte[totalLen];
            int offset = 0;
            if (this.mBlocks != null) {
                for (int i = 0; i < this.mBlockCount; i++) {
                    byte[] block = this.mBlocks[i];
                    int len = block.length;
                    System.arraycopy(block, 0, result, offset, len);
                    offset += len;
                }
            }
            System.arraycopy(lastBlock, 0, result, offset, lastLen);
            this.mSpareBlock = lastBlock;
            int offset2 = offset + lastLen;
            if (offset2 != totalLen) {
                throw new RuntimeException("Internal error: total len assumed to be " + totalLen + ", copied " + offset2 + " bytes");
            }
            return result;
        }
    }
}
