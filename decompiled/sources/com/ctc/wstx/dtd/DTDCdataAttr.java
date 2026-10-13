package com.ctc.wstx.dtd;

import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DTDCdataAttr.class */
public final class DTDCdataAttr extends DTDAttribute {
    public DTDCdataAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDCdataAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.codehaus.stax2.validation.XMLValidationException] */
    @Override // com.ctc.wstx.dtd.DTDAttribute
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) {
        return null;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public String normalize(DTDValidatorBase v, char[] cbuf, int start, int end) {
        return null;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public void normalizeDefault() {
    }
}
