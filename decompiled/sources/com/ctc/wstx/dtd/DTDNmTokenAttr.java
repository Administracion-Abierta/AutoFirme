package com.ctc.wstx.dtd;

import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DTDNmTokenAttr.class */
public final class DTDNmTokenAttr extends DTDAttribute {
    public DTDNmTokenAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDNmTokenAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public int getValueType() {
        return 8;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        int len;
        int origLen = end - start;
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            start++;
        }
        if (start >= end) {
            return reportValidationProblem(v, "Empty NMTOKEN value");
        }
        do {
            end--;
            if (end <= start) {
                break;
            }
        } while (WstxInputData.isSpaceChar(cbuf[end]));
        for (int i = start; i <= end; i++) {
            char c = cbuf[i];
            if (!WstxInputData.isNameChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                return reportInvalidChar(v, c, "not valid NMTOKEN character");
            }
        }
        if (normalize && (len = (end - start) + 1) != origLen) {
            return new String(cbuf, start, len);
        }
        return null;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String def = validateDefaultNmToken(rep, normalize);
        if (normalize) {
            this.mDefValue.setValue(def);
        }
    }
}
