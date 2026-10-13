package com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.relaxng.RELAXNGGrammar;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import com.ctc.wstx.shaded.msv_core.util.LightStack;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/comp/RELAXNGCompReader.class */
public class RELAXNGCompReader extends RELAXNGReader {
    private final Map defaultedAttributes;
    public static final String AnnotationNamespace = "http://relaxng.org/ns/compatibility/annotations/1.0";
    private final LightStack lastRNGElement;
    private boolean inAnnotation;
    public static final String CERR_ANN_CHILD_ELEMENT = "RELAXNGReader.Compatibility.Annotation.ChildElement";
    public static final String CERR_ANN_MISPLACED = "RELAXNGReader.Compatibility.Annotation.Misplaced";
    public static final String CERR_ANN_INVALID_ATTRIBUTE = "RELAXNGReader.Compatibility.Annotation.InvalidAttribute";

    public static TREXGrammar parse(String grammarURL, GrammarReaderController controller) {
        RELAXNGCompReader reader = new RELAXNGCompReader(controller);
        reader.parse(grammarURL);
        return reader.getResult();
    }

    public static TREXGrammar parse(InputSource grammar, GrammarReaderController controller) {
        RELAXNGCompReader reader = new RELAXNGCompReader(controller);
        reader.parse(grammar);
        return reader.getResult();
    }

    public RELAXNGCompReader(GrammarReaderController controller) {
        this(controller, createParserFactory(), new ExpressionPool());
    }

    public RELAXNGCompReader(GrammarReaderController controller, SAXParserFactory parserFactory, ExpressionPool pool) {
        this(controller, parserFactory, new StateFactory(), pool);
    }

    public RELAXNGCompReader(GrammarReaderController controller, SAXParserFactory parserFactory, StateFactory stateFactory, ExpressionPool pool) {
        super(controller, parserFactory, stateFactory, pool);
        this.defaultedAttributes = new HashMap();
        this.lastRNGElement = new LightStack();
        this.inAnnotation = false;
        this.lastRNGElement.push(null);
    }

    protected final void addDefaultValue(AttributeExp exp, String value) {
        setDeclaredLocationOf(exp);
        if (this.defaultedAttributes.put(exp, value) != null) {
            throw new Error();
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader
    protected TREXGrammar getGrammar() {
        return this.grammar;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader, com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader, com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected String localizeMessage(String propertyName, Object[] args) {
        try {
            String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.Messages").getString(propertyName);
            return MessageFormat.format(format, args);
        } catch (Exception e) {
            return super.localizeMessage(propertyName, args);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/comp/RELAXNGCompReader$StateFactory.class */
    public static class StateFactory extends RELAXNGReader.StateFactory {
        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader.StateFactory, com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State attribute(State parent, StartTagInfo tag) {
            return new CompAttributeState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public TREXGrammar createGrammar(ExpressionPool pool, TREXGrammar parent) {
            return new RELAXNGGrammar(pool, parent);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader, com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader
    public void wrapUp() {
        super.wrapUp();
        if (!this.controller.hadError()) {
            new DefAttCompatibilityChecker(this, this.defaultedAttributes).test();
            new IDCompatibilityChecker(this).test();
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader, com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader, org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startElement(String uri, String local, String qname, Attributes atts) throws SAXException {
        super.startElement(uri, local, qname, atts);
        if (this.inAnnotation) {
            reportWarning(CERR_ANN_CHILD_ELEMENT, (Object[]) null, new Locator[]{getLocator()});
            ((RELAXNGGrammar) this.grammar).isAnnotationCompatible = false;
        }
        if (uri.equals(AnnotationNamespace) && local.equals("annotation")) {
            for (int i = 0; i < atts.getLength(); i++) {
                String attUri = atts.getURI(i);
                if (attUri.equals("") || attUri.equals(AnnotationNamespace) || attUri.equals(RELAXNGReader.RELAXNGNamespace)) {
                    reportWarning(CERR_ANN_INVALID_ATTRIBUTE, new Object[]{atts.getQName(i)}, new Locator[]{getLocator()});
                    ((RELAXNGGrammar) this.grammar).isAnnotationCompatible = false;
                    break;
                }
            }
            if (this.lastRNGElement.size() != 0 && this.lastRNGElement.top() != null && !"value".equals(this.lastRNGElement.top()) && !"param".equals(this.lastRNGElement.top()) && !"name".equals(this.lastRNGElement.top())) {
                reportWarning(CERR_ANN_MISPLACED, new Object[]{this.lastRNGElement.top()}, new Locator[]{getLocator()});
                ((RELAXNGGrammar) this.grammar).isAnnotationCompatible = false;
            }
            this.inAnnotation = true;
        }
        this.lastRNGElement.push(null);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader, com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader, org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endElement(String uri, String local, String qname) throws SAXException {
        super.endElement(uri, local, qname);
        this.inAnnotation = false;
        this.lastRNGElement.pop();
        if (uri.equals(RELAXNGReader.RELAXNGNamespace)) {
            this.lastRNGElement.pop();
            this.lastRNGElement.push(local);
        }
    }
}
