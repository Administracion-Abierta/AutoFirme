package com.ctc.wstx.dtd;

import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDIdRefsAttr.class */
public final class DTDIdRefsAttr extends DTDAttribute {
    public DTDIdRefsAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDIdRefsAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public int getValueType() {
        return 4;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            start++;
        }
        if (start >= end) {
            return reportValidationProblem(v, "Empty IDREFS value");
        }
        do {
            end--;
            if (end <= start) {
                break;
            }
        } while (WstxInputData.isSpaceChar(cbuf[end]));
        ElementIdMap m = v.getIdMap();
        Location loc = v.getLocation();
        String idStr = null;
        StringBuilder sb = null;
        while (start <= end) {
            char c = cbuf[start];
            if (!WstxInputData.isNameStartChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                return reportInvalidChar(v, c, "not valid as the first IDREFS character");
            }
            int hash = c;
            int i = start + 1;
            while (i <= end) {
                char c2 = cbuf[i];
                if (WstxInputData.isSpaceChar(c2)) {
                    break;
                }
                if (!WstxInputData.isNameChar(c2, this.mCfgNsAware, this.mCfgXml11)) {
                    return reportInvalidChar(v, c2, "not valid as an IDREFS character");
                }
                hash = (hash * 31) + c2;
                i++;
            }
            ElementId id = m.addReferenced(cbuf, start, i - start, hash, loc, v.getElemName(), this.mName);
            start = i + 1;
            if (normalize) {
                if (idStr == null) {
                    idStr = id.getId();
                } else {
                    if (sb == null) {
                        sb = new StringBuilder(idStr);
                    }
                    idStr = id.getId();
                    sb.append(' ');
                    sb.append(idStr);
                }
            }
            while (start <= end && WstxInputData.isSpaceChar(cbuf[start])) {
                start++;
            }
        }
        if (normalize) {
            if (sb != null) {
                idStr = sb.toString();
            }
            return idStr;
        }
        return null;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String def = validateDefaultNames(rep, normalize);
        if (normalize) {
            this.mDefValue.setValue(def);
        }
    }
}
