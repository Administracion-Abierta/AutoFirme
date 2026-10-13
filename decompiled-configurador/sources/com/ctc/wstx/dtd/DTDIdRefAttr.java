package com.ctc.wstx.dtd;

import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDIdRefAttr.class */
public final class DTDIdRefAttr extends DTDAttribute {
    public DTDIdRefAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDIdRefAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public int getValueType() {
        return 3;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            start++;
        }
        if (start >= end) {
            return reportValidationProblem(v, "Empty IDREF value");
        }
        do {
            end--;
            if (end <= start) {
                break;
            }
        } while (WstxInputData.isSpaceChar(cbuf[end]));
        char c = cbuf[start];
        if (!WstxInputData.isNameStartChar(c, this.mCfgNsAware, this.mCfgXml11)) {
            return reportInvalidChar(v, c, "not valid as the first IDREF character");
        }
        int hash = c;
        for (int i = start + 1; i <= end; i++) {
            char c2 = cbuf[i];
            if (!WstxInputData.isNameChar(c2, this.mCfgNsAware, this.mCfgXml11)) {
                return reportInvalidChar(v, c2, "not valid as an IDREF character");
            }
            hash = (hash * 31) + c2;
        }
        ElementIdMap m = v.getIdMap();
        Location loc = v.getLocation();
        ElementId id = m.addReferenced(cbuf, start, (end - start) + 1, hash, loc, v.getElemName(), this.mName);
        if (normalize) {
            return id.getId();
        }
        return null;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String def = validateDefaultName(rep, normalize);
        if (normalize) {
            this.mDefValue.setValue(def);
        }
    }
}
