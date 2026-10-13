package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDIdAttr.class */
public final class DTDIdAttr extends DTDAttribute {
    public DTDIdAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDIdAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public int getValueType() {
        return 2;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public boolean typeIsId() {
        return true;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            start++;
        }
        if (start >= end) {
            return reportValidationProblem(v, "Empty ID value");
        }
        do {
            end--;
            if (end <= start) {
                break;
            }
        } while (WstxInputData.isSpaceChar(cbuf[end]));
        char c = cbuf[start];
        if (!WstxInputData.isNameStartChar(c, this.mCfgNsAware, this.mCfgXml11)) {
            return reportInvalidChar(v, c, "not valid as the first ID character");
        }
        int hash = c;
        for (int i = start + 1; i <= end; i++) {
            char c2 = cbuf[i];
            if (!WstxInputData.isNameChar(c2, this.mCfgNsAware, this.mCfgXml11)) {
                return reportInvalidChar(v, c2, "not valid as an ID character");
            }
            hash = (hash * 31) + c2;
        }
        ElementIdMap m = v.getIdMap();
        PrefixedName elemName = v.getElemName();
        Location loc = v.getLocation();
        ElementId id = m.addDefined(cbuf, start, (end - start) + 1, hash, loc, elemName, this.mName);
        if (id.getLocation() != loc) {
            return reportValidationProblem(v, "Duplicate id '" + id.getId() + "', first declared at " + id.getLocation());
        }
        if (normalize) {
            return id.getId();
        }
        return null;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public void validateDefault(InputProblemReporter rep, boolean normalize) {
        throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
    }
}
