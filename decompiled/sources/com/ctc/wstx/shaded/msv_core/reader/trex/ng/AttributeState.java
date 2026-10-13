package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/AttributeState.class */
public class AttributeState extends com.ctc.wstx.shaded.msv_core.reader.trex.AttributeState {
    private static final String infosetURI = "http://www.w3.org/2000/xmlns";

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassAndExpressionState, com.ctc.wstx.shaded.msv_core.reader.ExpressionState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        super.endSelf();
        final RELAXNGReader reader = (RELAXNGReader) this.reader;
        reader.restrictionChecker.checkNameClass(this.nameClass);
        this.nameClass.visit(new NameClassVisitor() { // from class: com.ctc.wstx.shaded.msv_core.reader.trex.ng.AttributeState.1
            @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
            public Object onAnyName(AnyNameClass nc) {
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
            public Object onSimple(SimpleNameClass nc) {
                if (nc.namespaceURI.equals(AttributeState.infosetURI)) {
                    reader.reportError(RELAXNGReader.ERR_INFOSET_URI_ATTRIBUTE);
                }
                if (nc.namespaceURI.length() == 0 && nc.localName.equals(CanonicalizerBase.XMLNS)) {
                    reader.reportError(RELAXNGReader.ERR_XMLNS_ATTRIBUTE);
                    return null;
                }
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
            public Object onNsName(NamespaceNameClass nc) {
                if (nc.namespaceURI.equals(AttributeState.infosetURI)) {
                    reader.reportError(RELAXNGReader.ERR_INFOSET_URI_ATTRIBUTE);
                    return null;
                }
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
            public Object onNot(NotNameClass nc) {
                nc.child.visit(this);
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
            public Object onDifference(DifferenceNameClass nc) {
                nc.nc1.visit(this);
                nc.nc2.visit(this);
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor
            public Object onChoice(ChoiceNameClass nc) {
                nc.nc1.visit(this);
                nc.nc2.visit(this);
                return null;
            }
        });
    }
}
