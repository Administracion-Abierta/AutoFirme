package com.ctc.wstx.shaded.msv_core.reader.util;

import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/util/IgnoreController.class */
public class IgnoreController implements GrammarReaderController {
    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
    public void warning(Locator[] locs, String errorMessage) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
    public void error(Locator[] locs, String errorMessage, Exception nestedException) {
    }

    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String p, String s) {
        return null;
    }
}
