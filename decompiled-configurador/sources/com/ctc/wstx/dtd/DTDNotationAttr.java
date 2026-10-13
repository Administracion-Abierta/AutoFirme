package com.ctc.wstx.dtd;

import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.WordResolver;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDNotationAttr.class */
public final class DTDNotationAttr extends DTDAttribute {
    final WordResolver mEnumValues;

    public DTDNotationAttr(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11, WordResolver enumValues) {
        super(name, defValue, specIndex, nsAware, xml11);
        this.mEnumValues = enumValues;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public DTDAttribute cloneWith(int specIndex) {
        return new DTDNotationAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11, this.mEnumValues);
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public int getValueType() {
        return 7;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public boolean typeIsNotation() {
        return true;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public String validate(DTDValidatorBase v, char[] cbuf, int start, int end, boolean normalize) throws XMLStreamException {
        String ok = validateEnumValue(cbuf, start, end, normalize, this.mEnumValues);
        if (ok == null) {
            String val = new String(cbuf, start, end - start);
            return reportValidationProblem(v, "Invalid notation value '" + val + "': has to be one of (" + this.mEnumValues + ")");
        }
        return ok;
    }

    @Override // com.ctc.wstx.dtd.DTDAttribute
    public void validateDefault(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String def = validateDefaultName(rep, normalize);
        String shared = this.mEnumValues.find(def);
        if (shared == null) {
            reportValidationProblem(rep, "Invalid default value '" + def + "': has to be one of (" + this.mEnumValues + ")");
        }
        if (normalize) {
            this.mDefValue.setValue(shared);
        }
    }
}
