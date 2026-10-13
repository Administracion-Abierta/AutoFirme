package com.ctc.wstx.shaded.msv_core.writer.relaxng;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.writer.XMLWriter;
import java.util.Stack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/writer/relaxng/NameClassWriter.class */
public class NameClassWriter implements NameClassVisitor {
    private final XMLWriter writer;
    private final String defaultNs;

    public NameClassWriter(Context ctxt) {
        this.writer = ctxt.getWriter();
        this.defaultNs = ctxt.getTargetNamespace();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onAnyName(AnyNameClass nc) {
        this.writer.element("anyName");
        return null;
    }

    protected void startWithNs(String name, String ns) {
        if (ns.equals(this.defaultNs)) {
            this.writer.start(name);
        } else {
            this.writer.start(name, new String[]{"ns", ns});
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onSimple(SimpleNameClass nc) {
        startWithNs("name", nc.namespaceURI);
        this.writer.characters(nc.localName);
        this.writer.end("name");
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onNsName(NamespaceNameClass nc) {
        startWithNs("nsName", nc.namespaceURI);
        this.writer.end("nsName");
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onNot(NotNameClass nc) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onChoice(ChoiceNameClass nc) {
        this.writer.start("choice");
        processChoice(nc);
        this.writer.end("choice");
        return null;
    }

    private void processChoice(ChoiceNameClass nc) {
        Stack s = new Stack();
        s.push(nc.nc1);
        s.push(nc.nc2);
        while (!s.empty()) {
            NameClass n = (NameClass) s.pop();
            if (n instanceof ChoiceNameClass) {
                s.push(((ChoiceNameClass) n).nc1);
                s.push(((ChoiceNameClass) n).nc2);
            } else {
                n.visit(this);
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
    public Object onDifference(DifferenceNameClass nc) {
        if (nc.nc1 instanceof AnyNameClass) {
            this.writer.start("anyName");
            this.writer.start("except");
            if (nc.nc2 instanceof ChoiceNameClass) {
                processChoice((ChoiceNameClass) nc.nc2);
            } else {
                nc.nc2.visit(this);
            }
            this.writer.end("except");
            this.writer.end("anyName");
            return null;
        }
        if (nc.nc1 instanceof NamespaceNameClass) {
            startWithNs("nsName", ((NamespaceNameClass) nc.nc1).namespaceURI);
            this.writer.start("except");
            if (nc.nc2 instanceof ChoiceNameClass) {
                processChoice((ChoiceNameClass) nc.nc2);
            } else {
                nc.nc2.visit(this);
            }
            this.writer.end("except");
            this.writer.end("nsName");
            return null;
        }
        throw new Error();
    }
}
