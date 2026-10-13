package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.StringUtil;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDValidator.class */
public class DTDValidator extends DTDValidatorBase {
    protected boolean mReportDuplicateErrors;
    protected ElementIdMap mIdMap;
    protected StructValidator[] mValidators;
    protected BitSet mCurrSpecialAttrs;
    protected boolean mCurrHasAnyFixed;
    protected BitSet mTmpSpecialAttrs;

    public DTDValidator(DTDSubset schema, ValidationContext ctxt, boolean hasNsDefaults, Map<PrefixedName, DTDElement> elemSpecs, Map<String, EntityDecl> genEntities) {
        super(schema, ctxt, hasNsDefaults, elemSpecs, genEntities);
        this.mReportDuplicateErrors = false;
        this.mIdMap = null;
        this.mValidators = null;
        this.mCurrSpecialAttrs = null;
        this.mCurrHasAnyFixed = false;
        this.mValidators = new StructValidator[16];
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase
    public final boolean reallyValidating() {
        return true;
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public void validateElementStart(String localName, String uri, String prefix) throws XMLStreamException {
        this.mTmpKey.reset(prefix, localName);
        DTDElement elem = this.mElemSpecs.get(this.mTmpKey);
        int elemCount = this.mElemCount;
        this.mElemCount = elemCount + 1;
        if (elemCount >= this.mElems.length) {
            this.mElems = (DTDElement[]) DataUtil.growArrayBy50Pct(this.mElems);
            this.mValidators = (StructValidator[]) DataUtil.growArrayBy50Pct(this.mValidators);
        }
        DTDElement[] dTDElementArr = this.mElems;
        this.mCurrElem = elem;
        dTDElementArr[elemCount] = elem;
        if (elem == null || !elem.isDefined()) {
            reportValidationProblem(ErrorConsts.ERR_VLD_UNKNOWN_ELEM, this.mTmpKey.toString());
        }
        StructValidator pv = elemCount > 0 ? this.mValidators[elemCount - 1] : null;
        if (pv != null && elem != null) {
            String msg = pv.tryToValidate(elem.getName());
            if (msg != null) {
                int ix = msg.indexOf("$END");
                String pname = this.mElems[elemCount - 1].toString();
                if (ix >= 0) {
                    msg = msg.substring(0, ix) + "</" + pname + ">" + msg.substring(ix + 4);
                }
                reportValidationProblem("Validation error, encountered element <" + elem.getName() + "> as a child of <" + pname + ">: " + msg);
            }
        }
        this.mAttrCount = 0;
        this.mIdAttrIndex = -2;
        if (elem == null) {
            this.mValidators[elemCount] = null;
            this.mCurrAttrDefs = NO_ATTRS;
            this.mCurrHasAnyFixed = false;
            this.mCurrSpecialAttrs = null;
            return;
        }
        this.mValidators[elemCount] = elem.getValidator();
        this.mCurrAttrDefs = elem.getAttributes();
        if (this.mCurrAttrDefs == null) {
            this.mCurrAttrDefs = NO_ATTRS;
        }
        this.mCurrHasAnyFixed = elem.hasFixedAttrs();
        int specCount = elem.getSpecialCount();
        if (specCount == 0) {
            this.mCurrSpecialAttrs = null;
            return;
        }
        BitSet bs = this.mTmpSpecialAttrs;
        if (bs == null) {
            BitSet bitSet = new BitSet(specCount);
            bs = bitSet;
            this.mTmpSpecialAttrs = bitSet;
        } else {
            bs.clear();
        }
        this.mCurrSpecialAttrs = bs;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public String validateAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        int specIndex;
        DTDAttribute attr = this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        if (attr == null) {
            if (this.mCurrElem != null) {
                reportValidationProblem(ErrorConsts.ERR_VLD_UNKNOWN_ATTR, this.mCurrElem.toString(), this.mTmpKey.toString());
            }
            return value;
        }
        int index = this.mAttrCount;
        this.mAttrCount = index + 1;
        if (index >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[]) DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        this.mAttrSpecs[index] = attr;
        if (this.mCurrSpecialAttrs != null && (specIndex = attr.getSpecialIndex()) >= 0) {
            this.mCurrSpecialAttrs.set(specIndex);
        }
        String result = attr.validate(this, value, this.mNormAttrs);
        if (this.mCurrHasAnyFixed && attr.isFixed()) {
            String act = result == null ? value : result;
            String exp = attr.getDefaultValue(this.mContext, this);
            if (!act.equals(exp)) {
                reportValidationProblem("Value of attribute \"" + attr + "\" (element <" + this.mCurrElem + ">) not \"" + exp + "\" as expected, but \"" + act + "\"");
            }
        }
        return result;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public String validateAttribute(String localName, String uri, String prefix, char[] valueChars, int valueStart, int valueEnd) throws XMLStreamException {
        boolean match;
        int specIndex;
        DTDAttribute attr = this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        if (attr == null) {
            if (this.mCurrElem != null) {
                reportValidationProblem(ErrorConsts.ERR_VLD_UNKNOWN_ATTR, this.mCurrElem.toString(), this.mTmpKey.toString());
            }
            return new String(valueChars, valueStart, valueEnd - valueStart);
        }
        int index = this.mAttrCount;
        this.mAttrCount = index + 1;
        if (index >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[]) DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        this.mAttrSpecs[index] = attr;
        if (this.mCurrSpecialAttrs != null && (specIndex = attr.getSpecialIndex()) >= 0) {
            this.mCurrSpecialAttrs.set(specIndex);
        }
        String result = attr.validate(this, valueChars, valueStart, valueEnd, this.mNormAttrs);
        if (this.mCurrHasAnyFixed && attr.isFixed()) {
            String exp = attr.getDefaultValue(this.mContext, this);
            if (result == null) {
                match = StringUtil.matches(exp, valueChars, valueStart, valueEnd - valueStart);
            } else {
                match = exp.equals(result);
            }
            if (!match) {
                String act = result == null ? new String(valueChars, valueStart, valueEnd - valueStart) : result;
                reportValidationProblem("Value of #FIXED attribute \"" + attr + "\" (element <" + this.mCurrElem + ">) not \"" + exp + "\" as expected, but \"" + act + "\"");
            }
        }
        return result;
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public int validateElementAndAttributes() throws XMLStreamException {
        DTDElement elem = this.mCurrElem;
        if (elem == null) {
            return 4;
        }
        if (this.mCurrSpecialAttrs != null) {
            BitSet specBits = this.mCurrSpecialAttrs;
            int specCount = elem.getSpecialCount();
            int iNextClearBit = specBits.nextClearBit(0);
            while (true) {
                int ix = iNextClearBit;
                if (ix >= specCount) {
                    break;
                }
                List<DTDAttribute> specAttrs = elem.getSpecialAttrs();
                DTDAttribute attr = specAttrs.get(ix);
                if (attr.isRequired()) {
                    reportValidationProblem("Required attribute \"{0}\" missing from element <{1}>", attr, elem);
                } else {
                    doAddDefaultValue(attr);
                }
                iNextClearBit = specBits.nextClearBit(ix + 1);
            }
        }
        return elem.getAllowedContent();
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public int validateElementEnd(String localName, String uri, String prefix) throws XMLStreamException {
        String msg;
        int ix = this.mElemCount - 1;
        if (ix < 0) {
            return 1;
        }
        this.mElemCount = ix;
        DTDElement closingElem = this.mElems[ix];
        this.mElems[ix] = null;
        StructValidator v = this.mValidators[ix];
        this.mValidators[ix] = null;
        if (v != null && (msg = v.fullyValid()) != null) {
            reportValidationProblem("Validation error, element </" + closingElem + ">: " + msg);
        }
        if (ix < 1) {
            return 1;
        }
        DTDElement element = this.mElems[ix - 1];
        if (element == null) {
            return 4;
        }
        return element.getAllowedContent();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.dtd.DTDValidatorBase, org.codehaus.stax2.validation.XMLValidator
    public void validationCompleted(boolean eod) throws XMLStreamException {
        checkIdRefs();
    }

    @Override // com.ctc.wstx.dtd.DTDValidatorBase
    protected ElementIdMap getIdMap() {
        if (this.mIdMap == null) {
            this.mIdMap = new ElementIdMap();
        }
        return this.mIdMap;
    }

    protected void checkIdRefs() throws XMLStreamException {
        ElementId ref;
        if (this.mIdMap != null && (ref = this.mIdMap.getFirstUndefined()) != null) {
            reportValidationProblem("Undefined id '" + ref.getId() + "': referenced from element <" + ref.getElemName() + ">, attribute '" + ref.getAttrName() + "'", ref.getLocation());
        }
    }
}
