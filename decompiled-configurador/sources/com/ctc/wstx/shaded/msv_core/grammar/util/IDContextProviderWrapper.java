package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/util/IDContextProviderWrapper.class */
public final class IDContextProviderWrapper implements IDContextProvider2 {
    private final IDContextProvider core;

    public static IDContextProvider2 create(IDContextProvider core) {
        if (core == null) {
            return null;
        }
        return new IDContextProviderWrapper(core);
    }

    private IDContextProviderWrapper(IDContextProvider _core) {
        this.core = _core;
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public String getBaseUri() {
        return this.core.getBaseUri();
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public boolean isNotation(String arg0) {
        return this.core.isNotation(arg0);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public boolean isUnparsedEntity(String arg0) {
        return this.core.isUnparsedEntity(arg0);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2
    public void onID(Datatype datatype, StringToken token) {
        this.core.onID(datatype, token.literal);
    }

    @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public String resolveNamespacePrefix(String arg0) {
        return this.core.resolveNamespacePrefix(arg0);
    }
}
