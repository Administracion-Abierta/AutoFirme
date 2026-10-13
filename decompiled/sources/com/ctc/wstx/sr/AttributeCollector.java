package com.ctc.wstx.sr;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.sw.XmlWriter;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.InternCache;
import com.ctc.wstx.util.StringUtil;
import com.ctc.wstx.util.StringVector;
import com.ctc.wstx.util.TextBuilder;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.CharArrayBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/AttributeCollector.class */
public final class AttributeCollector {
    protected static final int INT_SPACE = 32;
    protected static final int LONG_ATTR_LIST_LEN = 4;
    protected static final int EXP_ATTR_COUNT = 12;
    protected static final int EXP_NS_COUNT = 6;
    protected static final int XMLID_IX_DISABLED = -2;
    protected static final int XMLID_IX_NONE = -1;
    protected static final InternCache sInternCache = InternCache.getInstance();
    protected final String mXmlIdPrefix;
    protected final String mXmlIdLocalName;
    protected Attribute[] mAttributes;
    protected int mAttrCount;
    protected int mNonDefCount;
    protected Attribute[] mNamespaces;
    protected int mNsCount;
    protected int mXmlIdAttrIndex;
    protected int mAttrHashSize;
    protected int mAttrSpillEnd;
    protected int mMaxAttributesPerElement;
    protected boolean mDefaultNsDeclared = false;
    protected TextBuilder mValueBuilder = null;
    private final TextBuilder mNamespaceBuilder = new TextBuilder(6);
    protected int[] mAttrMap = null;

    protected AttributeCollector(ReaderConfig cfg, boolean nsAware) {
        this.mXmlIdAttrIndex = cfg.willDoXmlIdTyping() ? -1 : -2;
        if (nsAware) {
            this.mXmlIdPrefix = "xml";
            this.mXmlIdLocalName = ElementTags.ID;
        } else {
            this.mXmlIdPrefix = null;
            this.mXmlIdLocalName = "xml:id";
        }
        this.mMaxAttributesPerElement = cfg.getMaxAttributesPerElement();
    }

    public void reset() {
        if (this.mNsCount > 0) {
            this.mNamespaceBuilder.reset();
            this.mDefaultNsDeclared = false;
            this.mNsCount = 0;
        }
        if (this.mAttrCount > 0) {
            this.mValueBuilder.reset();
            this.mAttrCount = 0;
            if (this.mXmlIdAttrIndex >= 0) {
                this.mXmlIdAttrIndex = -1;
            }
        }
    }

    public void normalizeSpacesInValue(int index) {
        char[] attrCB = this.mValueBuilder.getCharBuffer();
        String normValue = StringUtil.normalizeSpaces(attrCB, getValueStartOffset(index), getValueStartOffset(index + 1));
        if (normValue != null) {
            this.mAttributes[index].setValue(normValue);
        }
    }

    protected int getNsCount() {
        return this.mNsCount;
    }

    public boolean hasDefaultNs() {
        return this.mDefaultNsDeclared;
    }

    public final int getCount() {
        return this.mAttrCount;
    }

    public int getSpecifiedCount() {
        return this.mNonDefCount;
    }

    public String getNsPrefix(int index) {
        if (index < 0 || index >= this.mNsCount) {
            throwIndex(index);
        }
        return this.mNamespaces[index].mLocalName;
    }

    public String getNsURI(int index) {
        if (index < 0 || index >= this.mNsCount) {
            throwIndex(index);
        }
        return this.mNamespaces[index].mNamespaceURI;
    }

    public String getPrefix(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            throwIndex(index);
        }
        return this.mAttributes[index].mPrefix;
    }

    public String getLocalName(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            throwIndex(index);
        }
        return this.mAttributes[index].mLocalName;
    }

    public String getURI(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            throwIndex(index);
        }
        return this.mAttributes[index].mNamespaceURI;
    }

    public QName getQName(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            throwIndex(index);
        }
        return this.mAttributes[index].getQName();
    }

    public final String getValue(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            throwIndex(index);
        }
        String full = this.mValueBuilder.getAllValues();
        Attribute attr = this.mAttributes[index];
        int index2 = index + 1;
        if (index2 < this.mAttrCount) {
            int endOffset = this.mAttributes[index2].mValueStartOffset;
            return attr.getValue(full, endOffset);
        }
        return attr.getValue(full);
    }

    public String getValue(String nsURI, String localName) {
        int hashSize = this.mAttrHashSize;
        if (hashSize == 0) {
            return null;
        }
        int hash = localName.hashCode();
        if (nsURI != null) {
            if (nsURI.length() == 0) {
                nsURI = null;
            } else {
                hash ^= nsURI.hashCode();
            }
        }
        int ix = this.mAttrMap[hash & (hashSize - 1)];
        if (ix == 0) {
            return null;
        }
        int ix2 = ix - 1;
        if (this.mAttributes[ix2].hasQName(nsURI, localName)) {
            return getValue(ix2);
        }
        int len = this.mAttrSpillEnd;
        for (int i = hashSize; i < len; i += 2) {
            if (this.mAttrMap[i] == hash) {
                int ix3 = this.mAttrMap[i + 1];
                if (this.mAttributes[ix3].hasQName(nsURI, localName)) {
                    return getValue(ix3);
                }
            }
        }
        return null;
    }

    public String getValueByLocalName(String localName) {
        switch (this.mAttrCount) {
            case 0:
                return null;
            case 1:
                if (this.mAttributes[0].hasLocalName(localName)) {
                    return getValue(0);
                }
                return null;
            case 2:
                if (this.mAttributes[0].hasLocalName(localName)) {
                    return getValue(0);
                }
                if (this.mAttributes[1].hasLocalName(localName)) {
                    return getValue(1);
                }
                return null;
            case 3:
                if (this.mAttributes[0].hasLocalName(localName)) {
                    return getValue(0);
                }
                if (this.mAttributes[1].hasLocalName(localName)) {
                    return getValue(1);
                }
                if (this.mAttributes[2].hasLocalName(localName)) {
                    return getValue(2);
                }
                return null;
            case 4:
                if (this.mAttributes[0].hasLocalName(localName)) {
                    return getValue(0);
                }
                if (this.mAttributes[1].hasLocalName(localName)) {
                    return getValue(1);
                }
                if (this.mAttributes[2].hasLocalName(localName)) {
                    return getValue(2);
                }
                if (this.mAttributes[3].hasLocalName(localName)) {
                    return getValue(3);
                }
                return null;
            default:
                int end = this.mAttrCount;
                for (int i = 0; i < end; i++) {
                    if (this.mAttributes[i].hasLocalName(localName)) {
                        return getValue(i);
                    }
                }
                return null;
        }
    }

    public int getMaxAttributesPerElement() {
        return this.mMaxAttributesPerElement;
    }

    public void setMaxAttributesPerElement(int maxAttributesPerElement) {
        this.mMaxAttributesPerElement = maxAttributesPerElement;
    }

    public int findIndex(String localName) {
        return findIndex(null, localName);
    }

    public int findIndex(String nsURI, String localName) {
        int hashSize = this.mAttrHashSize;
        if (hashSize == 0) {
            return -1;
        }
        int hash = localName.hashCode();
        if (nsURI != null) {
            if (nsURI.length() == 0) {
                nsURI = null;
            } else {
                hash ^= nsURI.hashCode();
            }
        }
        int ix = this.mAttrMap[hash & (hashSize - 1)];
        if (ix == 0) {
            return -1;
        }
        int ix2 = ix - 1;
        if (this.mAttributes[ix2].hasQName(nsURI, localName)) {
            return ix2;
        }
        int len = this.mAttrSpillEnd;
        for (int i = hashSize; i < len; i += 2) {
            if (this.mAttrMap[i] == hash) {
                int ix3 = this.mAttrMap[i + 1];
                if (this.mAttributes[ix3].hasQName(nsURI, localName)) {
                    return ix3;
                }
            }
        }
        return -1;
    }

    public final boolean isSpecified(int index) {
        return index < this.mNonDefCount;
    }

    public final int getXmlIdAttrIndex() {
        return this.mXmlIdAttrIndex;
    }

    public final void decodeValue(int index, TypedValueDecoder tvd) throws IllegalArgumentException {
        if (index < 0 || index >= this.mAttrCount) {
            throwIndex(index);
        }
        char[] buf = this.mValueBuilder.getCharBuffer();
        int end = getValueStartOffset(index + 1);
        for (int start = this.mAttributes[index].mValueStartOffset; start < end; start++) {
            if (!StringUtil.isSpace(buf[start])) {
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

    public final int decodeValues(int index, TypedArrayDecoder tad, InputProblemReporter rep) throws XMLStreamException {
        if (index < 0 || index >= this.mAttrCount) {
            throwIndex(index);
        }
        return decodeValues(tad, rep, this.mValueBuilder.getCharBuffer(), this.mAttributes[index].mValueStartOffset, getValueStartOffset(index + 1));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public final byte[] decodeBinary(int index, Base64Variant v, CharArrayBase64Decoder dec, InputProblemReporter rep) throws XMLStreamException {
        if (index < 0 || index >= this.mAttrCount) {
            throwIndex(index);
        }
        Attribute attr = this.mAttributes[index];
        char[] cbuf = this.mValueBuilder.getCharBuffer();
        int start = attr.mValueStartOffset;
        int end = getValueStartOffset(index + 1);
        int len = end - start;
        dec.init(v, true, cbuf, start, len, null);
        try {
            return dec.decodeCompletely();
        } catch (IllegalArgumentException iae) {
            String lexical = new String(cbuf, start, len);
            throw new TypedXMLStreamException(lexical, iae.getMessage(), rep.getLocation(), iae);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private static final int decodeValues(TypedArrayDecoder tad, InputProblemReporter rep, char[] buf, int ptr, int end) throws XMLStreamException {
        int start = ptr;
        int count = 0;
        loop0: while (ptr < end) {
            while (buf[ptr] <= ' ') {
                try {
                    ptr++;
                    if (ptr >= end) {
                        break loop0;
                    }
                } catch (IllegalArgumentException iae) {
                    Location loc = rep.getLocation();
                    String lexical = new String(buf, start, ptr - start);
                    throw new TypedXMLStreamException(lexical, iae.getMessage(), loc, iae);
                }
            }
            start = ptr;
            int ptr2 = ptr + 1;
            while (ptr2 < end && buf[ptr2] > ' ') {
                ptr2++;
            }
            int tokenEnd = ptr2;
            ptr = ptr2 + 1;
            count++;
            if (tad.decodeValue(buf, start, tokenEnd) && !checkExpand(tad)) {
                break;
            }
        }
        return count;
    }

    private static final boolean checkExpand(TypedArrayDecoder tad) {
        if (tad instanceof ValueDecoderFactory.BaseArrayDecoder) {
            ((ValueDecoderFactory.BaseArrayDecoder) tad).expand();
            return true;
        }
        return false;
    }

    protected int getValueStartOffset(int index) {
        if (index < this.mAttrCount) {
            return this.mAttributes[index].mValueStartOffset;
        }
        return this.mValueBuilder.getCharSize();
    }

    protected char[] getSharedValueBuffer() {
        return this.mValueBuilder.getCharBuffer();
    }

    protected Attribute resolveNamespaceDecl(int index, boolean internURI) {
        String uri;
        Attribute ns = this.mNamespaces[index];
        String full = this.mNamespaceBuilder.getAllValues();
        if (this.mNsCount == 0) {
            uri = full;
        } else {
            int index2 = index + 1;
            if (index2 < this.mNsCount) {
                int endOffset = this.mNamespaces[index2].mValueStartOffset;
                uri = ns.getValue(full, endOffset);
            } else {
                uri = ns.getValue(full);
            }
        }
        if (internURI && uri.length() > 0) {
            uri = sInternCache.intern(uri);
        }
        ns.mNamespaceURI = uri;
        return ns;
    }

    public ElemAttrs buildAttrOb() {
        int count = this.mAttrCount;
        if (count == 0) {
            return null;
        }
        String[] raw = new String[count << 2];
        for (int i = 0; i < count; i++) {
            Attribute attr = this.mAttributes[i];
            int ix = i << 2;
            raw[ix] = attr.mLocalName;
            raw[ix + 1] = attr.mNamespaceURI;
            raw[ix + 2] = attr.mPrefix;
            raw[ix + 3] = getValue(i);
        }
        if (count < 4) {
            return new ElemAttrs(raw, this.mNonDefCount);
        }
        int amapLen = this.mAttrMap.length;
        int[] amap = new int[amapLen];
        System.arraycopy(this.mAttrMap, 0, amap, 0, amapLen);
        return new ElemAttrs(raw, this.mNonDefCount, amap, this.mAttrHashSize, this.mAttrSpillEnd);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void validateAttribute(int index, XMLValidator vld) throws XMLStreamException {
        Attribute attr = this.mAttributes[index];
        String normValue = vld.validateAttribute(attr.mLocalName, attr.mNamespaceURI, attr.mPrefix, this.mValueBuilder.getCharBuffer(), getValueStartOffset(index), getValueStartOffset(index + 1));
        if (normValue != null) {
            attr.setValue(normValue);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public final TextBuilder getAttrBuilder(String attrPrefix, String attrLocalName) throws XMLStreamException {
        if (this.mAttrCount == 0) {
            if (this.mAttributes == null) {
                allocBuffers();
            }
            this.mAttributes[0] = new Attribute(attrPrefix, attrLocalName, 0);
        } else {
            int valueStart = this.mValueBuilder.getCharSize();
            if (this.mAttrCount >= this.mAttributes.length) {
                if (this.mAttrCount + this.mNsCount >= this.mMaxAttributesPerElement) {
                    throw new XMLStreamException("Attribute limit (" + this.mMaxAttributesPerElement + ") exceeded");
                }
                this.mAttributes = (Attribute[]) DataUtil.growArrayToAtMost(this.mAttributes, this.mMaxAttributesPerElement);
            }
            Attribute curr = this.mAttributes[this.mAttrCount];
            if (curr == null) {
                this.mAttributes[this.mAttrCount] = new Attribute(attrPrefix, attrLocalName, valueStart);
            } else {
                curr.reset(attrPrefix, attrLocalName, valueStart);
            }
        }
        this.mAttrCount++;
        if (attrLocalName == this.mXmlIdLocalName && attrPrefix == this.mXmlIdPrefix && this.mXmlIdAttrIndex != -2) {
            this.mXmlIdAttrIndex = this.mAttrCount - 1;
        }
        return this.mValueBuilder;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public int addDefaultAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        int attrIndex = this.mAttrCount;
        if (attrIndex < 1) {
            initHashArea();
        }
        int hash = localName.hashCode();
        if (uri != null && uri.length() > 0) {
            hash ^= uri.hashCode();
        }
        int index = hash & (this.mAttrHashSize - 1);
        int[] map = this.mAttrMap;
        if (map[index] == 0) {
            map[index] = attrIndex + 1;
        } else {
            int currIndex = map[index] - 1;
            int spillIndex = this.mAttrSpillEnd;
            int[] map2 = spillAttr(uri, localName, map, currIndex, spillIndex, hash, this.mAttrHashSize);
            if (map2 == null) {
                return -1;
            }
            int spillIndex2 = spillIndex + 1;
            map2[spillIndex2] = attrIndex;
            this.mAttrMap = map2;
            this.mAttrSpillEnd = spillIndex2 + 1;
        }
        getAttrBuilder(prefix, localName);
        Attribute attr = this.mAttributes[this.mAttrCount - 1];
        attr.mNamespaceURI = uri;
        attr.setValue(value);
        return this.mAttrCount - 1;
    }

    public final void setNormalizedValue(int index, String value) {
        this.mAttributes[index].setValue(value);
    }

    public TextBuilder getDefaultNsBuilder() throws XMLStreamException {
        if (this.mDefaultNsDeclared) {
            return null;
        }
        this.mDefaultNsDeclared = true;
        return getNsBuilder(null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public TextBuilder getNsBuilder(String prefix) throws XMLStreamException {
        if (this.mNsCount == 0) {
            if (this.mNamespaces == null) {
                this.mNamespaces = new Attribute[Math.min(6, this.mMaxAttributesPerElement)];
            }
            this.mNamespaces[0] = new Attribute(null, prefix, 0);
        } else {
            int len = this.mNsCount;
            if (prefix != null) {
                for (int i = 0; i < len; i++) {
                    if (prefix == this.mNamespaces[i].mLocalName) {
                        return null;
                    }
                }
            }
            if (len >= this.mNamespaces.length) {
                if (this.mAttrCount + this.mNsCount >= this.mMaxAttributesPerElement) {
                    throw new XMLStreamException("Attribute limit (" + this.mMaxAttributesPerElement + ") exceeded");
                }
                this.mNamespaces = (Attribute[]) DataUtil.growArrayToAtMost(this.mNamespaces, this.mMaxAttributesPerElement);
            }
            int uriStart = this.mNamespaceBuilder.getCharSize();
            Attribute curr = this.mNamespaces[len];
            if (curr == null) {
                this.mNamespaces[len] = new Attribute(null, prefix, uriStart);
            } else {
                curr.reset(null, prefix, uriStart);
            }
        }
        this.mNsCount++;
        return this.mNamespaceBuilder;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public int resolveNamespaces(InputProblemReporter rep, StringVector ns) throws XMLStreamException {
        int attrCount = this.mAttrCount;
        this.mNonDefCount = attrCount;
        if (attrCount < 1) {
            this.mAttrSpillEnd = 0;
            this.mAttrHashSize = 0;
            return this.mXmlIdAttrIndex;
        }
        for (int i = 0; i < attrCount; i++) {
            Attribute attr = this.mAttributes[i];
            String prefix = attr.mPrefix;
            if (prefix != null) {
                if (prefix == "xml") {
                    attr.mNamespaceURI = "http://www.w3.org/XML/1998/namespace";
                } else {
                    String uri = ns.findLastFromMap(prefix);
                    if (uri == null) {
                        rep.throwParseError(ErrorConsts.ERR_NS_UNDECLARED_FOR_ATTR, prefix, attr.mLocalName);
                    }
                    attr.mNamespaceURI = uri;
                }
            }
        }
        int[] map = this.mAttrMap;
        int hashCount = 4;
        while (hashCount < attrCount + (attrCount >> 2)) {
            hashCount += hashCount;
        }
        this.mAttrHashSize = hashCount;
        int min = hashCount + (hashCount >> 4);
        if (map == null || map.length < min) {
            map = new int[min];
        } else {
            Arrays.fill(map, 0, hashCount, 0);
        }
        int mask = hashCount - 1;
        int spillIndex = hashCount;
        for (int i2 = 0; i2 < attrCount; i2++) {
            Attribute attr2 = this.mAttributes[i2];
            String name = attr2.mLocalName;
            int hash = name.hashCode();
            String uri2 = attr2.mNamespaceURI;
            if (uri2 != null) {
                hash ^= uri2.hashCode();
            }
            int index = hash & mask;
            if (map[index] == 0) {
                map[index] = i2 + 1;
            } else {
                int currIndex = map[index] - 1;
                map = spillAttr(uri2, name, map, currIndex, spillIndex, hash, hashCount);
                if (map == null) {
                    throwDupAttr(rep, currIndex);
                } else {
                    int spillIndex2 = spillIndex + 1;
                    map[spillIndex2] = i2;
                    spillIndex = spillIndex2 + 1;
                }
            }
        }
        this.mAttrSpillEnd = spillIndex;
        this.mAttrMap = map;
        return this.mXmlIdAttrIndex;
    }

    protected void throwIndex(int index) {
        throw new IllegalArgumentException("Invalid index " + index + "; current element has only " + getCount() + " attributes");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Deprecated
    public void writeAttribute(int index, XmlWriter xw) throws XMLStreamException, IOException {
        writeAttribute(index, xw, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeAttribute(int index, XmlWriter xw, XMLValidator validator) throws XMLStreamException, IOException {
        Attribute attr = this.mAttributes[index];
        String ln = attr.mLocalName;
        String prefix = attr.mPrefix;
        String value = getValue(index);
        if (prefix == null || prefix.length() == 0) {
            xw.writeAttribute(ln, value);
        } else {
            xw.writeAttribute(prefix, ln, value);
        }
        if (validator != null) {
            validator.validateAttribute(ln, attr.mNamespaceURI, prefix, value);
        }
    }

    protected final void allocBuffers() {
        if (this.mAttributes == null) {
            this.mAttributes = new Attribute[Math.min(8, this.mMaxAttributesPerElement)];
        }
        if (this.mValueBuilder == null) {
            this.mValueBuilder = new TextBuilder(12);
        }
    }

    private int[] spillAttr(String uri, String name, int[] map, int currIndex, int spillIndex, int hash, int hashCount) {
        Attribute oldAttr = this.mAttributes[currIndex];
        if (oldAttr.mLocalName == name) {
            String currURI = oldAttr.mNamespaceURI;
            if (currURI == uri) {
                return null;
            }
            if (currURI != null && currURI.equals(uri)) {
                return null;
            }
        }
        if (spillIndex + 1 >= map.length) {
            map = DataUtil.growArrayBy(map, 8);
        }
        for (int j = hashCount; j < spillIndex; j += 2) {
            if (map[j] == hash) {
                int currIndex2 = map[j + 1];
                Attribute attr = this.mAttributes[currIndex2];
                if (attr.mLocalName != name) {
                    continue;
                } else {
                    String currURI2 = attr.mNamespaceURI;
                    if (currURI2 == uri) {
                        return null;
                    }
                    if (currURI2 != null && currURI2.equals(uri)) {
                        return null;
                    }
                }
            }
        }
        map[spillIndex] = hash;
        return map;
    }

    private void initHashArea() {
        this.mAttrSpillEnd = 4;
        this.mAttrHashSize = 4;
        if (this.mAttrMap == null || this.mAttrMap.length < this.mAttrHashSize) {
            this.mAttrMap = new int[this.mAttrHashSize + 1];
        }
        int[] iArr = this.mAttrMap;
        int[] iArr2 = this.mAttrMap;
        int[] iArr3 = this.mAttrMap;
        this.mAttrMap[3] = 0;
        iArr3[2] = 0;
        iArr2[1] = 0;
        iArr[0] = 0;
        allocBuffers();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void throwDupAttr(InputProblemReporter rep, int index) throws XMLStreamException {
        rep.throwParseError("Duplicate attribute '" + getQName(index) + "'.");
    }
}
