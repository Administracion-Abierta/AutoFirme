package com.ctc.wstx.shaded.msv.relaxng_datatype.helpers;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/helpers/ParameterlessDatatypeBuilder.class */
public final class ParameterlessDatatypeBuilder implements DatatypeBuilder {
    private final Datatype baseType;

    public ParameterlessDatatypeBuilder(Datatype datatype) {
        this.baseType = datatype;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder
    public void addParameter(String str, String str2, ValidationContext validationContext) throws DatatypeException {
        throw new DatatypeException();
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder
    public Datatype createDatatype() throws DatatypeException {
        return this.baseType;
    }
}
