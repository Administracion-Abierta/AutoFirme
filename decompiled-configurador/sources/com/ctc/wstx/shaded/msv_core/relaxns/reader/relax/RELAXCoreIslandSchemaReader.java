package com.ctc.wstx.shaded.msv_core.relaxns.reader.relax;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchemaReader;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalAttributeExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.RELAXIslandSchema;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/relax/RELAXCoreIslandSchemaReader.class */
public class RELAXCoreIslandSchemaReader extends RELAXCoreReader implements IslandSchemaReader {
    private final Map externalElementExps;
    protected final Set pendingAnyOtherElements;

    public RELAXCoreIslandSchemaReader(GrammarReaderController controller, SAXParserFactory parserFactory, ExpressionPool pool, String expectedTargetnamespace) throws ParserConfigurationException, SAXException {
        super(controller, parserFactory, new StateFactory(), pool, expectedTargetnamespace);
        this.externalElementExps = new HashMap();
        this.pendingAnyOtherElements = new HashSet();
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/relax/RELAXCoreIslandSchemaReader$StateFactory.class */
    private static class StateFactory extends RELAXCoreReader.StateFactory {
        private StateFactory() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader.StateFactory
        public State interface_(State parent, StartTagInfo tag) {
            return new InterfaceStateEx();
        }
    }

    protected RELAXModule getModule() {
        return this.module;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader
    protected boolean canHaveOccurs(ExpressionState state) {
        return super.canHaveOccurs(state) || (state instanceof AnyOtherElementState);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchemaReader
    public final IslandSchema getSchema() {
        RELAXModule m = getResult();
        if (m == null) {
            return null;
        }
        return new RELAXIslandSchema(m, this.pendingAnyOtherElements);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader, com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader, com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    public State createExpressionChildState(State parent, StartTagInfo tag) {
        if (RELAXReader.RELAXCoreNamespace.equals(tag.namespaceURI)) {
            return tag.localName.equals("anyOtherElement") ? new AnyOtherElementState() : super.createExpressionChildState(parent, tag);
        }
        return null;
    }

    private ExternalElementExp getExtElementExp(String namespace, String label) {
        StringPair name = new StringPair(namespace, label);
        ExternalElementExp exp = (ExternalElementExp) this.externalElementExps.get(name);
        if (exp != null) {
            return exp;
        }
        ExternalElementExp exp2 = new ExternalElementExp(this.pool, namespace, label, new LocatorImpl(getLocator()));
        this.externalElementExps.put(name, exp2);
        return exp2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader, com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader
    protected Expression resolveElementRef(String namespace, String label) {
        if (namespace != null) {
            return getExtElementExp(namespace, label);
        }
        return super.resolveElementRef(namespace, label);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader, com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader
    protected Expression resolveHedgeRef(String namespace, String label) {
        if (namespace != null) {
            return getExtElementExp(namespace, label);
        }
        return super.resolveHedgeRef(namespace, label);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader
    protected Expression resolveAttPoolRef(String namespace, String label) {
        if (namespace != null) {
            return new ExternalAttributeExp(this.pool, namespace, label, new LocatorImpl(getLocator()));
        }
        return super.resolveAttPoolRef(namespace, label);
    }
}
