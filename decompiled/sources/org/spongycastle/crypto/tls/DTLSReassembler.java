package org.spongycastle.crypto.tls;

import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSReassembler.class */
class DTLSReassembler {
    private short msg_type;
    private byte[] body;
    private Vector missing = new Vector();

    DTLSReassembler(short msg_type, int length) {
        this.msg_type = msg_type;
        this.body = new byte[length];
        this.missing.addElement(new Range(0, length));
    }

    short getMsgType() {
        return this.msg_type;
    }

    byte[] getBodyIfComplete() {
        if (this.missing.isEmpty()) {
            return this.body;
        }
        return null;
    }

    void contributeFragment(short msg_type, int length, byte[] buf, int off, int fragment_offset, int fragment_length) {
        int fragment_end = fragment_offset + fragment_length;
        if (this.msg_type != msg_type || this.body.length != length || fragment_end > length) {
            return;
        }
        if (fragment_length == 0) {
            if (fragment_offset == 0 && !this.missing.isEmpty()) {
                Range firstRange = (Range) this.missing.firstElement();
                if (firstRange.getEnd() == 0) {
                    this.missing.removeElementAt(0);
                    return;
                }
                return;
            }
            return;
        }
        int i = 0;
        while (i < this.missing.size()) {
            Range range = (Range) this.missing.elementAt(i);
            if (range.getStart() < fragment_end) {
                if (range.getEnd() > fragment_offset) {
                    int copyStart = Math.max(range.getStart(), fragment_offset);
                    int copyEnd = Math.min(range.getEnd(), fragment_end);
                    int copyLength = copyEnd - copyStart;
                    System.arraycopy(buf, (off + copyStart) - fragment_offset, this.body, copyStart, copyLength);
                    if (copyStart == range.getStart()) {
                        if (copyEnd == range.getEnd()) {
                            int i2 = i;
                            i--;
                            this.missing.removeElementAt(i2);
                        } else {
                            range.setStart(copyEnd);
                        }
                    } else {
                        if (copyEnd != range.getEnd()) {
                            i++;
                            this.missing.insertElementAt(new Range(copyEnd, range.getEnd()), i);
                        }
                        range.setEnd(copyStart);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    void reset() {
        this.missing.removeAllElements();
        this.missing.addElement(new Range(0, this.body.length));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSReassembler$Range.class */
    private static class Range {
        private int start;
        private int end;

        Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return this.start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return this.end;
        }

        public void setEnd(int end) {
            this.end = end;
        }
    }
}
