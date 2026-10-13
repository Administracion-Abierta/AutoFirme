package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler;
import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/IVerifier.class */
public interface IVerifier extends VerifierHandler {
    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler
    boolean isValid();

    Object getCurrentElementType();

    Datatype[] getLastCharacterType();

    void setPanicMode(boolean z);

    Locator getLocator();

    ErrorHandler getErrorHandler();

    void setErrorHandler(ErrorHandler errorHandler);
}
