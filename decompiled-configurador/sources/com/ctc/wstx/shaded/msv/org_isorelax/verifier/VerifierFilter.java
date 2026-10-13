package com.ctc.wstx.shaded.msv.org_isorelax.verifier;

import org.xml.sax.XMLFilter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_isorelax/verifier/VerifierFilter.class */
public interface VerifierFilter extends XMLFilter {
    boolean isValid() throws IllegalStateException;
}
