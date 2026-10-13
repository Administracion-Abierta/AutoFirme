package com.ctc.wstx.shaded.msv_core.grammar.relaxng.datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relaxng/datatype/DatatypeBuilderImpl.class */
class DatatypeBuilderImpl implements DatatypeBuilder {
    private final Datatype baseType;
    protected static final String ERR_PARAMETER_UNSUPPORTED = "DataTypeBuilderImpl.ParameterUnsupported";

    DatatypeBuilderImpl(Datatype baseType) {
        this.baseType = baseType;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder
    public Datatype createDatatype() {
        return this.baseType;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder
    public void addParameter(String name, String value, ValidationContext context) throws DatatypeException {
        throw new DatatypeException(localize(ERR_PARAMETER_UNSUPPORTED, null));
    }

    protected String localize(String propertyName, Object[] args) {
        String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.grammar.relaxng.Messages").getString(propertyName);
        return MessageFormat.format(format, args);
    }
}
