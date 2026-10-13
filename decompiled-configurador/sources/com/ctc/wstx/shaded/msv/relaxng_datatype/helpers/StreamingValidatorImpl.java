package com.ctc.wstx.shaded.msv.relaxng_datatype.helpers;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeStreamingValidator;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/helpers/StreamingValidatorImpl.class */
public final class StreamingValidatorImpl implements DatatypeStreamingValidator {
    private final StringBuffer buffer = new StringBuffer();
    private final Datatype baseType;
    private final ValidationContext context;

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeStreamingValidator
    public void addCharacters(char[] cArr, int i, int i2) {
        this.buffer.append(cArr, i, i2);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeStreamingValidator
    public boolean isValid() {
        return this.baseType.isValid(this.buffer.toString(), this.context);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeStreamingValidator
    public void checkValid() throws DatatypeException {
        this.baseType.checkValid(this.buffer.toString(), this.context);
    }

    public StreamingValidatorImpl(Datatype datatype, ValidationContext validationContext) {
        this.baseType = datatype;
        this.context = validationContext;
    }
}
