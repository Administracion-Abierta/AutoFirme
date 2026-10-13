package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/XSTypeIncubator.class */
public interface XSTypeIncubator {
    void addFacet(String str, String str2, boolean z, ValidationContext validationContext) throws DatatypeException;

    XSDatatypeExp derive(String str, String str2) throws DatatypeException;
}
