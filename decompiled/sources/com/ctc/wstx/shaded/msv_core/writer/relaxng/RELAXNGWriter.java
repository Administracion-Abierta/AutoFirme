package com.ctc.wstx.shaded.msv_core.writer.relaxng;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionCloner;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import com.ctc.wstx.shaded.msv_core.grammar.util.PossibleNamesCollector;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDVocabulary;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import com.ctc.wstx.shaded.msv_core.writer.GrammarWriter;
import com.ctc.wstx.shaded.msv_core.writer.SAXRuntimeException;
import com.ctc.wstx.shaded.msv_core.writer.XMLWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/writer/relaxng/RELAXNGWriter.class */
public class RELAXNGWriter implements GrammarWriter, Context {
    protected Grammar grammar;
    protected Map exp2name;
    protected String defaultNs;
    protected NameClassVisitor nameClassWriter;
    protected XMLWriter writer = new XMLWriter();
    protected SmartPatternWriter patternWriter = new SmartPatternWriter(this);

    @Override // com.ctc.wstx.shaded.msv_core.writer.relaxng.Context
    public XMLWriter getWriter() {
        return this.writer;
    }

    @Override // com.ctc.wstx.shaded.msv_core.writer.GrammarWriter
    public void setDocumentHandler(DocumentHandler handler) {
        this.writer.setDocumentHandler(handler);
    }

    @Override // com.ctc.wstx.shaded.msv_core.writer.GrammarWriter
    public void write(Grammar g) throws SAXException {
        write(g, sniffDefaultNs(g.getTopLevel()));
    }

    public void write(Grammar g, String _defaultNs) throws SAXException {
        this.defaultNs = _defaultNs;
        this.grammar = g;
        final Set nodes = new HashSet();
        final Set<Expression> heads = new HashSet();
        g.getTopLevel().visit(new ExpressionWalker() { // from class: com.ctc.wstx.shaded.msv_core.writer.relaxng.RELAXNGWriter.1
            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onElement(ElementExp exp) {
                if (nodes.contains(exp)) {
                    heads.add(exp);
                } else {
                    nodes.add(exp);
                    super.onElement(exp);
                }
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onRef(ReferenceExp exp) {
                if (nodes.contains(exp)) {
                    heads.add(exp);
                } else {
                    nodes.add(exp);
                    super.onRef(exp);
                }
            }
        });
        Map name2exp = new HashMap();
        int cnt = 0;
        for (Expression exp : heads) {
            if (exp instanceof ReferenceExp) {
                ReferenceExp rexp = (ReferenceExp) exp;
                if (rexp.name == null) {
                    while (name2exp.containsKey("anonymous" + cnt)) {
                        cnt++;
                    }
                    name2exp.put("anonymous" + cnt, exp);
                } else if (name2exp.containsKey(rexp.name)) {
                    int i = 2;
                    while (name2exp.containsKey(rexp.name + i)) {
                        i++;
                    }
                    name2exp.put(rexp.name + i, exp);
                } else {
                    name2exp.put(rexp.name, exp);
                }
            } else if (exp instanceof ElementExp) {
                ElementExp eexp = (ElementExp) exp;
                NameClass nc = eexp.getNameClass();
                if ((nc instanceof SimpleNameClass) && !name2exp.containsKey(((SimpleNameClass) nc).localName)) {
                    name2exp.put(((SimpleNameClass) nc).localName, exp);
                } else {
                    while (name2exp.containsKey("element" + cnt)) {
                        cnt++;
                    }
                    name2exp.put("element" + cnt, exp);
                }
            } else {
                throw new Error();
            }
        }
        this.exp2name = new HashMap();
        for (String name : name2exp.keySet()) {
            this.exp2name.put(name2exp.get(name), name);
        }
        this.nameClassWriter = createNameClassWriter();
        try {
            DocumentHandler handler = this.writer.getDocumentHandler();
            handler.setDocumentLocator(new LocatorImpl());
            handler.startDocument();
            if (this.defaultNs != null) {
                this.writer.start("grammar", new String[]{"ns", this.defaultNs, CanonicalizerBase.XMLNS, RELAXNGReader.RELAXNGNamespace, "datatypeLibrary", XSDVocabulary.XMLSchemaNamespace});
            } else {
                this.writer.start("grammar", new String[]{CanonicalizerBase.XMLNS, RELAXNGReader.RELAXNGNamespace, "datatypeLibrary", XSDVocabulary.XMLSchemaNamespace});
            }
            this.writer.start("start");
            writeIsland(g.getTopLevel());
            this.writer.end("start");
            for (Expression exp2 : this.exp2name.keySet()) {
                String name2 = (String) this.exp2name.get(exp2);
                if (exp2 instanceof ReferenceExp) {
                    exp2 = ((ReferenceExp) exp2).exp;
                }
                this.writer.start("define", new String[]{"name", name2});
                writeIsland(exp2);
                this.writer.end("define");
            }
            this.writer.end("grammar");
            handler.endDocument();
        } catch (SAXRuntimeException sw) {
            throw sw.e;
        }
    }

    protected void writeIsland(Expression exp) {
        if (exp instanceof ElementExp) {
            this.patternWriter.writeElement((ElementExp) exp);
        } else {
            this.patternWriter.visitUnary(exp);
        }
    }

    protected String sniffDefaultNs(Expression exp) {
        return (String) exp.visit(new ExpressionVisitor() { // from class: com.ctc.wstx.shaded.msv_core.writer.relaxng.RELAXNGWriter.2
            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onElement(ElementExp exp2) {
                return sniff(exp2.getNameClass());
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onAttribute(AttributeExp exp2) {
                return sniff(exp2.nameClass);
            }

            protected String sniff(NameClass nc) {
                if (nc instanceof SimpleNameClass) {
                    return ((SimpleNameClass) nc).namespaceURI;
                }
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onChoice(ChoiceExp exp2) {
                return onBinExp(exp2);
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onSequence(SequenceExp exp2) {
                return onBinExp(exp2);
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onInterleave(InterleaveExp exp2) {
                return onBinExp(exp2);
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onConcur(ConcurExp exp2) {
                return onBinExp(exp2);
            }

            public Object onBinExp(BinaryExp exp2) {
                Object o = exp2.exp1.visit(this);
                if (o == null) {
                    o = exp2.exp2.visit(this);
                }
                return o;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onMixed(MixedExp exp2) {
                return exp2.exp.visit(this);
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onOneOrMore(OneOrMoreExp exp2) {
                return exp2.exp.visit(this);
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onRef(ReferenceExp exp2) {
                return exp2.exp.visit(this);
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onOther(OtherExp exp2) {
                return exp2.exp.visit(this);
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onNullSet() {
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onEpsilon() {
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onAnyString() {
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onData(DataExp exp2) {
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onValue(ValueExp exp2) {
                return null;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
            public Object onList(ListExp exp2) {
                return null;
            }
        });
    }

    @Override // com.ctc.wstx.shaded.msv_core.writer.relaxng.Context
    public String getTargetNamespace() {
        return this.defaultNs;
    }

    @Override // com.ctc.wstx.shaded.msv_core.writer.relaxng.Context
    public void writeNameClass(NameClass src) {
        Set names = PossibleNamesCollector.calc(src);
        StringPair[] values = (StringPair[]) names.toArray(new StringPair[names.size()]);
        Set uriset = new HashSet();
        for (StringPair stringPair : values) {
            uriset.add(stringPair.namespaceURI);
        }
        NameClass r = null;
        String[] uris = (String[]) uriset.toArray(new String[uriset.size()]);
        for (int i = 0; i < uris.length; i++) {
            if (uris[i] != PossibleNamesCollector.MAGIC) {
                NameClass tmp = null;
                for (int j = 0; j < values.length; j++) {
                    if (values[j].namespaceURI.equals(uris[i]) && values[j].localName != PossibleNamesCollector.MAGIC && src.accepts(values[j]) != src.accepts(uris[i], PossibleNamesCollector.MAGIC)) {
                        tmp = tmp == null ? new SimpleNameClass(values[j]) : new ChoiceNameClass(tmp, new SimpleNameClass(values[j]));
                    }
                }
                if (src.accepts(uris[i], PossibleNamesCollector.MAGIC) != src.accepts(PossibleNamesCollector.MAGIC, PossibleNamesCollector.MAGIC)) {
                    if (tmp == null) {
                        tmp = new NamespaceNameClass(uris[i]);
                    } else {
                        tmp = new DifferenceNameClass(new NamespaceNameClass(uris[i]), tmp);
                    }
                }
                r = r == null ? tmp : new ChoiceNameClass(r, tmp);
            }
        }
        if (src.accepts(PossibleNamesCollector.MAGIC, PossibleNamesCollector.MAGIC)) {
            if (r == null) {
                r = NameClass.ALL;
            } else {
                r = new DifferenceNameClass(NameClass.ALL, r);
            }
        } else if (r == null) {
            this.writer.element("anyName");
            this.writer.element("notAllowed");
            return;
        }
        r.visit(this.nameClassWriter);
    }

    protected NameClassVisitor createNameClassWriter() {
        return new NameClassWriter(this);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/writer/relaxng/RELAXNGWriter$SmartPatternWriter.class */
    class SmartPatternWriter extends PatternWriter {
        SmartPatternWriter(Context context) {
            super(context);
        }

        @Override // com.ctc.wstx.shaded.msv_core.writer.relaxng.PatternWriter, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
        public void onOther(OtherExp exp) {
            exp.exp.visit(this);
        }

        @Override // com.ctc.wstx.shaded.msv_core.writer.relaxng.PatternWriter, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
        public void onRef(ReferenceExp exp) {
            String uniqueName = (String) RELAXNGWriter.this.exp2name.get(exp);
            if (uniqueName != null) {
                this.writer.element("ref", new String[]{"name", uniqueName});
            } else {
                exp.exp.visit(this);
            }
        }

        @Override // com.ctc.wstx.shaded.msv_core.writer.relaxng.PatternWriter, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
        public void onElement(ElementExp exp) {
            String uniqueName = (String) RELAXNGWriter.this.exp2name.get(exp);
            if (uniqueName != null) {
                this.writer.element("ref", new String[]{"name", uniqueName});
            } else {
                writeElement(exp);
            }
        }

        @Override // com.ctc.wstx.shaded.msv_core.writer.relaxng.PatternWriter, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
        public void onAttribute(AttributeExp exp) {
            if ((exp.nameClass instanceof SimpleNameClass) && ((SimpleNameClass) exp.nameClass).namespaceURI.equals("")) {
                this.writer.start("attribute", new String[]{"name", ((SimpleNameClass) exp.nameClass).localName});
            } else {
                this.writer.start("attribute");
                this.context.writeNameClass(exp.nameClass);
            }
            if (exp.exp != Expression.anyString) {
                visitUnary(exp.exp);
            }
            this.writer.end("attribute");
        }

        protected void writeElement(ElementExp exp) {
            NameClass nc = exp.getNameClass();
            if ((nc instanceof SimpleNameClass) && ((SimpleNameClass) nc).namespaceURI.equals(RELAXNGWriter.this.defaultNs)) {
                this.writer.start("element", new String[]{"name", ((SimpleNameClass) nc).localName});
            } else {
                this.writer.start("element");
                RELAXNGWriter.this.writeNameClass(exp.getNameClass());
            }
            visitUnary(simplify(exp.contentModel));
            this.writer.end("element");
        }

        public Expression simplify(Expression exp) {
            return exp.visit(new ExpressionCloner(RELAXNGWriter.this.grammar.getPool()) { // from class: com.ctc.wstx.shaded.msv_core.writer.relaxng.RELAXNGWriter.SmartPatternWriter.1
                @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
                public Expression onRef(ReferenceExp exp2) {
                    if (RELAXNGWriter.this.exp2name.containsKey(exp2)) {
                        return exp2;
                    }
                    return exp2.exp.visit(this);
                }

                @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
                public Expression onOther(OtherExp exp2) {
                    return exp2.exp.visit(this);
                }

                @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
                public Expression onElement(ElementExp exp2) {
                    return exp2;
                }

                @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
                public Expression onAttribute(AttributeExp exp2) {
                    return exp2;
                }
            });
        }
    }
}
