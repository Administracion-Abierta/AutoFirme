package org.apache.pdfbox.pdfparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObjectKey;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/XrefTrailerResolver.class */
public class XrefTrailerResolver {
    private final Map<Long, XrefTrailerObj> bytePosToXrefMap = new HashMap();
    private XrefTrailerObj curXrefTrailerObj = null;
    private XrefTrailerObj resolvedXrefTrailer = null;
    private static final Log LOG = LogFactory.getLog(XrefTrailerResolver.class);

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/XrefTrailerResolver$XRefType.class */
    public enum XRefType {
        TABLE,
        STREAM
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/XrefTrailerResolver$XrefTrailerObj.class */
    private static class XrefTrailerObj {
        protected COSDictionary trailer;
        private XRefType xrefType;
        private final Map<COSObjectKey, Long> xrefTable;

        private XrefTrailerObj() {
            this.trailer = null;
            this.xrefTable = new HashMap();
            this.xrefType = XRefType.TABLE;
        }

        public void reset() {
            this.xrefTable.clear();
        }
    }

    public final COSDictionary getFirstTrailer() {
        if (this.bytePosToXrefMap.isEmpty()) {
            return null;
        }
        Set<Long> offsets = this.bytePosToXrefMap.keySet();
        SortedSet<Long> sortedOffset = new TreeSet<>(offsets);
        return this.bytePosToXrefMap.get(sortedOffset.first()).trailer;
    }

    public final COSDictionary getLastTrailer() {
        if (this.bytePosToXrefMap.isEmpty()) {
            return null;
        }
        Set<Long> offsets = this.bytePosToXrefMap.keySet();
        SortedSet<Long> sortedOffset = new TreeSet<>(offsets);
        return this.bytePosToXrefMap.get(sortedOffset.last()).trailer;
    }

    public final int getTrailerCount() {
        return this.bytePosToXrefMap.size();
    }

    public void nextXrefObj(long startBytePos, XRefType type) {
        this.curXrefTrailerObj = new XrefTrailerObj();
        this.bytePosToXrefMap.put(Long.valueOf(startBytePos), this.curXrefTrailerObj);
        this.curXrefTrailerObj.xrefType = type;
    }

    public XRefType getXrefType() {
        if (this.resolvedXrefTrailer == null) {
            return null;
        }
        return this.resolvedXrefTrailer.xrefType;
    }

    public void setXRef(COSObjectKey objKey, long offset) {
        if (this.curXrefTrailerObj != null) {
            if (!this.curXrefTrailerObj.xrefTable.containsKey(objKey)) {
                this.curXrefTrailerObj.xrefTable.put(objKey, Long.valueOf(offset));
                return;
            }
            return;
        }
        LOG.warn("Cannot add XRef entry for '" + objKey.getNumber() + "' because XRef start was not signalled.");
    }

    public void setTrailer(COSDictionary trailer) {
        if (this.curXrefTrailerObj == null) {
            LOG.warn("Cannot add trailer because XRef start was not signalled.");
        } else {
            this.curXrefTrailerObj.trailer = trailer;
        }
    }

    public COSDictionary getCurrentTrailer() {
        return this.curXrefTrailerObj.trailer;
    }

    public void setStartxref(long startxrefBytePosValue) {
        if (this.resolvedXrefTrailer != null) {
            LOG.warn("Method must be called only ones with last startxref value.");
            return;
        }
        this.resolvedXrefTrailer = new XrefTrailerObj();
        this.resolvedXrefTrailer.trailer = new COSDictionary();
        XrefTrailerObj curObj = this.bytePosToXrefMap.get(Long.valueOf(startxrefBytePosValue));
        List<Long> xrefSeqBytePos = new ArrayList<>();
        if (curObj != null) {
            this.resolvedXrefTrailer.xrefType = curObj.xrefType;
            xrefSeqBytePos.add(Long.valueOf(startxrefBytePosValue));
            while (curObj.trailer != null) {
                long prevBytePos = curObj.trailer.getLong(COSName.PREV, -1L);
                if (prevBytePos == -1) {
                    break;
                }
                curObj = this.bytePosToXrefMap.get(Long.valueOf(prevBytePos));
                if (curObj == null) {
                    LOG.warn("Did not found XRef object pointed to by 'Prev' key at position " + prevBytePos);
                    break;
                } else {
                    xrefSeqBytePos.add(Long.valueOf(prevBytePos));
                    if (xrefSeqBytePos.size() >= this.bytePosToXrefMap.size()) {
                        break;
                    }
                }
            }
            Collections.reverse(xrefSeqBytePos);
        } else {
            LOG.warn("Did not found XRef object at specified startxref position " + startxrefBytePosValue);
            xrefSeqBytePos.addAll(this.bytePosToXrefMap.keySet());
            Collections.sort(xrefSeqBytePos);
        }
        for (Long bPos : xrefSeqBytePos) {
            XrefTrailerObj curObj2 = this.bytePosToXrefMap.get(bPos);
            if (curObj2.trailer != null) {
                this.resolvedXrefTrailer.trailer.addAll(curObj2.trailer);
            }
            this.resolvedXrefTrailer.xrefTable.putAll(curObj2.xrefTable);
        }
    }

    public COSDictionary getTrailer() {
        if (this.resolvedXrefTrailer == null) {
            return null;
        }
        return this.resolvedXrefTrailer.trailer;
    }

    public Map<COSObjectKey, Long> getXrefTable() {
        if (this.resolvedXrefTrailer == null) {
            return null;
        }
        return this.resolvedXrefTrailer.xrefTable;
    }

    public Set<Long> getContainedObjectNumbers(int objstmObjNr) {
        if (this.resolvedXrefTrailer == null) {
            return null;
        }
        Set<Long> refObjNrs = new HashSet<>();
        long cmpVal = -objstmObjNr;
        for (Map.Entry<COSObjectKey, Long> xrefEntry : this.resolvedXrefTrailer.xrefTable.entrySet()) {
            if (xrefEntry.getValue().longValue() == cmpVal) {
                refObjNrs.add(Long.valueOf(xrefEntry.getKey().getNumber()));
            }
        }
        return refObjNrs;
    }

    protected void reset() {
        for (XrefTrailerObj trailerObj : this.bytePosToXrefMap.values()) {
            trailerObj.reset();
        }
        this.curXrefTrailerObj = null;
        this.resolvedXrefTrailer = null;
    }
}
