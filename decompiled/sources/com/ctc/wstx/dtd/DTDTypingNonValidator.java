package com.ctc.wstx.dtd;

import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.PrefixedName;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DTDTypingNonValidator.class */
public class DTDTypingNonValidator extends DTDValidatorBase {
    protected boolean mHasAttrDefaults;
    protected BitSet mCurrDefaultAttrs;
    protected boolean mHasNormalizableAttrs;
    protected BitSet mTmpDefaultAttrs;

    public DTDTypingNonValidator(DTDSubset schema, ValidationContext ctxt, boolean hasNsDefaults, Map<PrefixedName, DTDElement> elemSpecs, Map<String, EntityDecl> genEntities) {
        super(schema, ctxt, hasNsDefaults, elemSpecs, genEntities);
        this.mHasAttrDefaults = false;
        this.mCurrDefaultAttrs = null;
        this.mHasNormalizableAttrs = false;
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase
    public final boolean reallyValidating() {
        return false;
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase
    public void setAttrValueNormalization(boolean state) {
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public void validateElementStart(String localName, String uri, String prefix) throws XMLStreamException {
        this.mTmpKey.reset(prefix, localName);
        DTDElement elem = this.mElemSpecs.get(this.mTmpKey);
        int elemCount = this.mElemCount;
        this.mElemCount = elemCount + 1;
        if (elemCount >= this.mElems.length) {
            this.mElems = (DTDElement[]) DataUtil.growArrayBy50Pct(this.mElems);
        }
        DTDElement[] dTDElementArr = this.mElems;
        this.mCurrElem = elem;
        dTDElementArr[elemCount] = elem;
        this.mAttrCount = 0;
        this.mIdAttrIndex = -2;
        if (elem == null) {
            this.mCurrAttrDefs = NO_ATTRS;
            this.mHasAttrDefaults = false;
            this.mCurrDefaultAttrs = null;
            this.mHasNormalizableAttrs = false;
            return;
        }
        this.mCurrAttrDefs = elem.getAttributes();
        if (this.mCurrAttrDefs == null) {
            this.mCurrAttrDefs = NO_ATTRS;
            this.mHasAttrDefaults = false;
            this.mCurrDefaultAttrs = null;
            this.mHasNormalizableAttrs = false;
            return;
        }
        this.mHasNormalizableAttrs = this.mNormAttrs || elem.attrsNeedValidation();
        this.mHasAttrDefaults = elem.hasAttrDefaultValues();
        if (this.mHasAttrDefaults) {
            int specCount = elem.getSpecialCount();
            BitSet bs = this.mTmpDefaultAttrs;
            if (bs == null) {
                BitSet bitSet = new BitSet(specCount);
                bs = bitSet;
                this.mTmpDefaultAttrs = bitSet;
            } else {
                bs.clear();
            }
            this.mCurrDefaultAttrs = bs;
            return;
        }
        this.mCurrDefaultAttrs = null;
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public String validateAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        int specIndex;
        DTDAttribute attr = this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        int index = this.mAttrCount;
        this.mAttrCount = index + 1;
        if (index >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[]) DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        this.mAttrSpecs[index] = attr;
        if (attr != null) {
            if (this.mHasAttrDefaults && (specIndex = attr.getSpecialIndex()) >= 0) {
                this.mCurrDefaultAttrs.set(specIndex);
            }
            if (this.mHasNormalizableAttrs) {
            }
            return null;
        }
        return null;
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public String validateAttribute(String localName, String uri, String prefix, char[] valueChars, int valueStart, int valueEnd) throws XMLStreamException {
        int specIndex;
        DTDAttribute attr = this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        int index = this.mAttrCount;
        this.mAttrCount = index + 1;
        if (index >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[]) DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        this.mAttrSpecs[index] = attr;
        if (attr != null) {
            if (this.mHasAttrDefaults && (specIndex = attr.getSpecialIndex()) >= 0) {
                this.mCurrDefaultAttrs.set(specIndex);
            }
            if (this.mHasNormalizableAttrs) {
                return attr.normalize(this, valueChars, valueStart, valueEnd);
            }
            return null;
        }
        return null;
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public int validateElementAndAttributes() throws XMLStreamException {
        DTDElement elem = this.mCurrElem;
        if (elem == null) {
            return 4;
        }
        if (this.mHasAttrDefaults) {
            BitSet specBits = this.mCurrDefaultAttrs;
            int specCount = elem.getSpecialCount();
            int iNextClearBit = specBits.nextClearBit(0);
            while (true) {
                int ix = iNextClearBit;
                if (ix >= specCount) {
                    break;
                }
                List<DTDAttribute> specAttrs = elem.getSpecialAttrs();
                DTDAttribute attr = specAttrs.get(ix);
                if (attr.hasDefaultValue()) {
                    doAddDefaultValue(attr);
                }
                iNextClearBit = specBits.nextClearBit(ix + 1);
            }
        }
        return elem.getAllowedContentIfSpace();
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public int validateElementEnd(String localName, String uri, String prefix) throws XMLStreamException {
        int ix = this.mElemCount - 1;
        this.mElemCount = ix;
        this.mElems[ix] = null;
        if (ix < 1) {
            return 4;
        }
        DTDElement elem = this.mElems[ix - 1];
        if (elem == null) {
            return 4;
        }
        return this.mElems[ix - 1].getAllowedContentIfSpace();
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public void validationCompleted(boolean eod) {
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase
    protected ElementIdMap getIdMap() {
        ExceptionUtil.throwGenericInternal();
        return null;
    }
}
