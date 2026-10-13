package com.ctc.wstx.shaded.msv_core.reader;

import org.xml.sax.EntityResolver;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/GrammarReaderController.class */
public interface GrammarReaderController extends EntityResolver {
    void warning(Locator[] locatorArr, String str);

    void error(Locator[] locatorArr, String str, Exception exc);
}
