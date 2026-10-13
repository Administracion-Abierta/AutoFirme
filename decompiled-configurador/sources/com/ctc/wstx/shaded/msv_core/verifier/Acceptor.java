package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/Acceptor.class */
public interface Acceptor {
    public static final int STRING_PROHIBITED = 0;
    public static final int STRING_IGNORE = 1;
    public static final int STRING_STRICT = 2;

    Acceptor createChildAcceptor(StartTagInfo startTagInfo, StringRef stringRef);

    boolean onAttribute2(String str, String str2, String str3, String str4, IDContextProvider2 iDContextProvider2, StringRef stringRef, DatatypeRef datatypeRef);

    boolean onAttribute(String str, String str2, String str3, String str4, IDContextProvider iDContextProvider, StringRef stringRef, DatatypeRef datatypeRef);

    boolean onEndAttributes(StartTagInfo startTagInfo, StringRef stringRef);

    boolean onText2(String str, IDContextProvider2 iDContextProvider2, StringRef stringRef, DatatypeRef datatypeRef);

    boolean onText(String str, IDContextProvider iDContextProvider, StringRef stringRef, DatatypeRef datatypeRef);

    boolean stepForward(Acceptor acceptor, StringRef stringRef);

    boolean isAcceptState(StringRef stringRef);

    Object getOwnerType();

    Acceptor createClone();

    int getStringCareLevel();
}
