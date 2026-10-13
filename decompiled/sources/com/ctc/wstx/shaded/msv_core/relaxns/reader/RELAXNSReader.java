package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchemaReader;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.RELAXGrammar;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.relax.RELAXCoreIslandSchemaReader;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.trex.TREXIslandSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/RELAXNSReader.class */
public class RELAXNSReader extends RELAXReader {
    public static final String RELAXNamespaceNamespace = "http://www.xml.gr.jp/xmlns/relaxNamespace";
    public final RELAXGrammar grammar;
    protected SchemaProvider schemaProvider;
    public static final String WRN_ILLEGAL_RELAXNAMESPACE_VERSION = "RELAXNSReader.Warning.IllegalRelaxNamespaceVersion";
    public static final String ERR_TOPLEVEL_PARTICLE_MUST_BE_RELAX_CORE = "RELAXNSReader.TopLevelParticleMustBeRelaxCore";
    public static final String ERR_INLINEMODULE_NOT_FOUND = "RELAXNSReader.InlineModuleNotFound";
    public static final String ERR_UNKNOWN_LANGUAGE = "RELAXNSReader.UnknownLanguage";
    public static final String ERR_NAMESPACE_COLLISION = "RELAXNSReader.NamespaceCollision";

    public static RELAXGrammar parse(String moduleURL, SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool) {
        RELAXNSReader reader = new RELAXNSReader(controller, factory, pool);
        reader.parse(moduleURL);
        return reader.getResult();
    }

    public static RELAXGrammar parse(InputSource module, SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool) {
        RELAXNSReader reader = new RELAXNSReader(controller, factory, pool);
        reader.parse(module);
        return reader.getResult();
    }

    public RELAXNSReader(GrammarReaderController controller, SAXParserFactory parserFactory, ExpressionPool pool) {
        super(controller, parserFactory, new RELAXReader.StateFactory(), pool, new RootGrammarState());
        this.grammar = new RELAXGrammar(pool);
    }

    public final RELAXGrammar getResult() {
        if (this.controller.hadError()) {
            return null;
        }
        return this.grammar;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    public Grammar getResultAsGrammar() {
        return getResult();
    }

    public final SchemaProvider getSchemaProvider() {
        if (this.controller.hadError()) {
            return null;
        }
        return this.schemaProvider;
    }

    public IslandSchemaReader getIslandSchemaReader(String language, String expectedTargetNamespace) {
        try {
            if (language.equals(RELAXReader.RELAXCoreNamespace)) {
                return new RELAXCoreIslandSchemaReader(this.controller, this.parserFactory, this.pool, expectedTargetNamespace);
            }
            if (language.equals("http://www.thaiopensource.com/trex")) {
                return new TREXIslandSchemaReader(new TREXGrammarReader(this.controller, this.parserFactory, new TREXGrammarReader.StateFactory(), this.pool));
            }
            return null;
        } catch (ParserConfigurationException e) {
            this.controller.error(e, (Locator) null);
            return null;
        } catch (SAXException e2) {
            this.controller.error(e2, (Locator) null);
            return null;
        }
    }

    public Datatype resolveDataType(String typeName) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected boolean isGrammarElement(StartTagInfo tag) {
        return RELAXNamespaceNamespace.equals(tag.namespaceURI) && !tag.localName.equals(ElementTags.ANNOTATION);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader
    protected Expression resolveElementRef(String namespace, String label) {
        return resolveRef(namespace, label, "ref");
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader
    protected Expression resolveHedgeRef(String namespace, String label) {
        return resolveRef(namespace, label, "hedgeRef");
    }

    private Expression resolveRef(String namespace, String label, String tagName) {
        if (namespace == null) {
            reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, tagName, "namespace");
            return Expression.nullSet;
        }
        return new ExternalElementExp(this.pool, namespace, label, new LocatorImpl(getLocator()));
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader, com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected String localizeMessage(String propertyName, Object[] args) {
        return super.localizeMessage(propertyName, args);
    }
}
