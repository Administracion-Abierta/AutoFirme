package com.ctc.wstx.shaded.msv_core.datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/ErrorDatatypeLibrary.class */
public class ErrorDatatypeLibrary implements DatatypeLibrary, DatatypeBuilder {
    public static final ErrorDatatypeLibrary theInstance = new ErrorDatatypeLibrary();

    private ErrorDatatypeLibrary() {
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary
    public Datatype createDatatype(String name) {
        return StringType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary
    public DatatypeBuilder createDatatypeBuilder(String name) {
        return this;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder
    public Datatype createDatatype() {
        return StringType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder
    public void addParameter(String name, String value, ValidationContext context) {
    }
}
