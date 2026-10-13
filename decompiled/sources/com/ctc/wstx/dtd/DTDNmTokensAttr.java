package com.ctc.wstx.dtd;

import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DTDNmTokensAttr.class */
public final class DTDNmTokensAttr extends DTDAttribute {
    public DTDNmTokensAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDNmTokensAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public int getValueType() {
        return 9;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            start++;
        }
        if (start >= end) {
            return reportValidationProblem(v, "Empty NMTOKENS value");
        }
        if (!normalize) {
            while (start < end) {
                char c = cbuf[start];
                if (WstxInputData.isSpaceChar(c) || WstxInputData.isNameChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                    start++;
                } else {
                    return reportInvalidChar(v, c, "not valid as NMTOKENS character");
                }
            }
            return null;
        }
        do {
            end--;
            if (end <= start) {
                break;
            }
        } while (WstxInputData.isSpaceChar(cbuf[end]));
        StringBuilder sb = null;
        while (start <= end) {
            int i = start;
            while (i <= end) {
                char c2 = cbuf[i];
                if (WstxInputData.isSpaceChar(c2)) {
                    break;
                }
                if (WstxInputData.isNameChar(c2, this.mCfgNsAware, this.mCfgXml11)) {
                    i++;
                } else {
                    return reportInvalidChar(v, c2, "not valid as an NMTOKENS character");
                }
            }
            if (sb == null) {
                sb = new StringBuilder((end - start) + 1);
            } else {
                sb.append(' ');
            }
            sb.append(cbuf, start, i - start);
            start = i + 1;
            while (start <= end && WstxInputData.isSpaceChar(cbuf[start])) {
                start++;
            }
        }
        if (sb == null) {
            return null;
        }
        return sb.toString();
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        char c;
        String defValue = this.mDefValue.getValue();
        int len = defValue.length();
        StringBuilder sb = null;
        int count = 0;
        int i = 0;
        loop0: while (true) {
            int start = i;
            if (start >= len) {
                break;
            }
            char cCharAt = defValue.charAt(start);
            while (WstxInputData.isSpaceChar(c)) {
                start++;
                if (start >= len) {
                    break loop0;
                } else {
                    cCharAt = defValue.charAt(start);
                }
            }
            int i2 = start + 1;
            do {
                i2++;
                if (i2 >= len) {
                    break;
                } else {
                    c = defValue.charAt(i2);
                }
            } while (!WstxInputData.isSpaceChar(c));
            count++;
            String token = defValue.substring(start, i2);
            int illegalIx = WstxInputData.findIllegalNmtokenChar(token, this.mCfgNsAware, this.mCfgXml11);
            if (illegalIx >= 0) {
                reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character #" + illegalIx + " (" + WstxInputData.getCharDesc(defValue.charAt(illegalIx)) + ") not a valid NMTOKENS character");
            }
            if (normalize) {
                if (sb == null) {
                    sb = new StringBuilder((i2 - start) + 32);
                } else {
                    sb.append(' ');
                }
                sb.append(token);
            }
            i = i2 + 1;
        }
        if (count == 0) {
            reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid NMTOKENS value");
        } else if (sb != null) {
            this.mDefValue.setValue(sb.toString());
        }
    }
}
