package com.ctc.wstx.shaded.msv.org_isorelax.verifier;

import org.xml.sax.ContentHandler;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_isorelax/verifier/VerifierHandler.class */
public interface VerifierHandler extends ContentHandler {
    boolean isValid() throws IllegalStateException;
}
