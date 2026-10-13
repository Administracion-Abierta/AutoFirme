package com.ctc.wstx.dtd;

import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import java.util.StringTokenizer;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DTDEntitiesAttr.class */
public final class DTDEntitiesAttr extends DTDAttribute {
    public DTDEntitiesAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDEntitiesAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public int getValueType() {
        return 6;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            start++;
        }
        if (start >= end) {
            return reportValidationProblem(v, "Empty ENTITIES value");
        }
        do {
            end--;
            if (end <= start) {
                break;
            }
        } while (WstxInputData.isSpaceChar(cbuf[end]));
        String idStr = null;
        StringBuilder sb = null;
        while (start <= end) {
            char c = cbuf[start];
            if (!WstxInputData.isNameStartChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                return reportInvalidChar(v, c, "not valid as the first ENTITIES character");
            }
            int i = start + 1;
            while (i <= end) {
                char c2 = cbuf[i];
                if (WstxInputData.isSpaceChar(c2)) {
                    break;
                }
                if (WstxInputData.isNameChar(c2, this.mCfgNsAware, this.mCfgXml11)) {
                    i++;
                } else {
                    return reportInvalidChar(v, c2, "not valid as an ENTITIES character");
                }
            }
            EntityDecl ent = findEntityDecl(v, cbuf, start, i - start);
            start = i + 1;
            if (normalize) {
                if (idStr == null) {
                    idStr = ent.getName();
                } else {
                    if (sb == null) {
                        sb = new StringBuilder(idStr);
                    }
                    idStr = ent.getName();
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
        String normStr = validateDefaultNames(rep, true);
        if (normalize) {
            this.mDefValue.setValue(normStr);
        }
        StringTokenizer st = new StringTokenizer(normStr);
        MinimalDTDReader dtdr = (MinimalDTDReader) rep;
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            EntityDecl ent = dtdr.findEntity(str);
            checkEntity(rep, normStr, ent);
        }
    }
}
