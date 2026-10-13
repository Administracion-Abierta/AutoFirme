package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/DataOrValueExp.class */
public interface DataOrValueExp {
    StringPair getName();

    Datatype getType();
}
