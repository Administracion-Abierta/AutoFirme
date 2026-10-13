package com.ctc.wstx.dtd;

import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DTDEntityAttr.class */
public final class DTDEntityAttr extends DTDAttribute {
    public DTDEntityAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDEntityAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public int getValueType() {
        return 5;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            start++;
        }
        if (start >= end) {
            return reportValidationProblem(v, "Empty ENTITY value");
        }
        do {
            end--;
            if (end <= start) {
                break;
            }
        } while (WstxInputData.isSpaceChar(cbuf[end]));
        char c = cbuf[start];
        if (!WstxInputData.isNameStartChar(c, this.mCfgNsAware, this.mCfgXml11) && c != ':') {
            return reportInvalidChar(v, c, "not valid as the first ID character");
        }
        for (int i = start + 1; i <= end; i++) {
            char c2 = cbuf[i];
            if (!WstxInputData.isNameChar(c2, this.mCfgNsAware, this.mCfgXml11)) {
                return reportInvalidChar(v, c2, "not valid as an ID character");
            }
        }
        EntityDecl ent = findEntityDecl(v, cbuf, start, (end - start) + 1);
        if (normalize) {
            return ent.getName();
        }
        return null;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String normStr = validateDefaultName(rep, normalize);
        if (normalize) {
            this.mDefValue.setValue(normStr);
        }
        EntityDecl ent = ((MinimalDTDReader) rep).findEntity(normStr);
        checkEntity(rep, normStr, ent);
    }
}
