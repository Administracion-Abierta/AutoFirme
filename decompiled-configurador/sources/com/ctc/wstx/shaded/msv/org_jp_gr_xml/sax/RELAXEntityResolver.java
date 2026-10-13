package com.ctc.wstx.shaded.msv.org_jp_gr_xml.sax;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_jp_gr_xml/sax/RELAXEntityResolver.class */
public class RELAXEntityResolver extends SimpleEntityResolver {
    public RELAXEntityResolver() {
        String s = getClass().getResource("/com/ctc/wstx/shaded/msv/org_jp_gr_xml/lib/relaxCore.dtd").toExternalForm();
        String s1 = getClass().getResource("/com/ctc/wstx/shaded/msv/org_jp_gr_xml/lib/relaxNamespace.dtd").toExternalForm();
        String s2 = getClass().getResource("/com/ctc/wstx/shaded/msv/org_jp_gr_xml/lib/relax.dtd").toExternalForm();
        addSystemId("http://www.xml.gr.jp/relax/core1/relaxCore.dtd", s);
        addSystemId("relaxCore.dtd", s);
        addSystemId("relaxNamespace.dtd", s1);
        addSystemId("relax.dtd", s2);
        addPublicId("-//RELAX//DTD RELAX Core 1.0//JA", s);
        addPublicId("-//RELAX//DTD RELAX Namespace 1.0//JA", s1);
        addPublicId("-//RELAX//DTD RELAX Grammar 1.0//JA", s2);
    }
}
