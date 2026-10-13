package com.ctc.wstx.shaded.msv_core.writer.relaxng;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.writer.XMLWriter;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/writer/relaxng/Context.class */
public interface Context {
    void writeNameClass(NameClass nameClass);

    String getTargetNamespace();

    XMLWriter getWriter();
}
