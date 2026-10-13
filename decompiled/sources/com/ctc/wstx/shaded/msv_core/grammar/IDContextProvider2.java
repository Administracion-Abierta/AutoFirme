package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/IDContextProvider2.class */
public interface IDContextProvider2 extends ValidationContext {
    void onID(Datatype datatype, StringToken stringToken);
}
