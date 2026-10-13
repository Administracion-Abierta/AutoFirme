package com.ctc.wstx.shaded.msv.org_jp_gr_xml.sax;

import java.io.StringReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_jp_gr_xml/sax/DTDSkipper.class */
public class DTDSkipper implements EntityResolver {
    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String s, String s1) {
        if (!s1.endsWith(".dtd")) {
            return null;
        }
        StringReader stringreader = new StringReader("");
        InputSource inputsource = new InputSource(stringreader);
        return inputsource;
    }
}
