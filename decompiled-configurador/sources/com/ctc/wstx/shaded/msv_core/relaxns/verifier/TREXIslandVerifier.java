package com.ctc.wstx.shaded.msv_core.relaxns.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.AnyOtherElementExp;
import com.ctc.wstx.shaded.msv_core.verifier.Verifier;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ComplexAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.SimpleAcceptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/relaxns/verifier/TREXIslandVerifier.class */
class TREXIslandVerifier extends Verifier implements IslandVerifier {
    protected Dispatcher dispatcher;
    protected final Map rule2exp;
    private String lastNamaespaceUri;
    private String lastLocalName;
    private String lastQName;
    private Set unparsedEntities;

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier
    public void setDispatcher(Dispatcher disp) {
        this.dispatcher = disp;
        this.errorHandler = new ErrorHandlerAdaptor(disp);
    }

    TREXIslandVerifier(RulesAcceptor initialAcceptor) {
        super(null, null);
        this.rule2exp = new HashMap();
        this.current = initialAcceptor;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Verifier, com.ctc.wstx.shaded.msv_core.verifier.AbstractVerifier, org.xml.sax.ContentHandler
    public void startElement(String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(namespaceUri, localName, qName, atts);
        if (this.current instanceof SimpleAcceptor) {
            SimpleAcceptor sa = (SimpleAcceptor) this.current;
            if (sa.owner instanceof ExternalElementExp) {
                switchToChildIsland(new ExternalElementExp[]{(ExternalElementExp) sa.owner}, namespaceUri, localName, qName, atts);
                return;
            } else {
                if (sa.owner instanceof AnyOtherElementExp) {
                    switchToAnyOtherElement(new AnyOtherElementExp[]{(AnyOtherElementExp) sa.owner}, namespaceUri, localName, qName, atts);
                    return;
                }
                return;
            }
        }
        if (this.current instanceof ComplexAcceptor) {
            ComplexAcceptor ca = (ComplexAcceptor) this.current;
            Vector vec = null;
            for (int i = 0; i < ca.owners.length; i++) {
                if (ca.owners[i] instanceof ExternalElementExp) {
                    if (vec == null) {
                        vec = new Vector();
                    }
                    vec.add(ca.owners[i]);
                }
            }
            if (vec != null) {
                ExternalElementExp[] exps = new ExternalElementExp[vec.size()];
                vec.toArray(exps);
                switchToChildIsland(exps, namespaceUri, localName, qName, atts);
                return;
            }
            for (int i2 = 0; i2 < ca.owners.length; i2++) {
                if (ca.owners[i2] instanceof AnyOtherElementExp) {
                    if (vec == null) {
                        vec = new Vector();
                    }
                    vec.add(ca.owners[i2]);
                }
            }
            if (vec != null) {
                AnyOtherElementExp[] exps2 = new AnyOtherElementExp[vec.size()];
                vec.toArray(exps2);
                switchToAnyOtherElement(exps2, namespaceUri, localName, qName, atts);
                return;
            }
            return;
        }
        throw new Error();
    }

    protected void switchToChildIsland(ExternalElementExp[] exps, String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        this.lastNamaespaceUri = namespaceUri;
        this.lastLocalName = localName;
        this.lastQName = qName;
        ElementDecl[] rules = new ElementDecl[exps.length];
        for (int i = 0; i < exps.length; i++) {
            rules[i] = exps[i].rule;
            this.rule2exp.put(rules[i], exps[i]);
        }
        if (this.rule2exp.size() != rules.length) {
            throw new Error();
        }
        IslandSchema is = this.dispatcher.getSchemaProvider().getSchemaByNamespace(namespaceUri);
        IslandVerifier iv = is.createNewVerifier(namespaceUri, rules);
        this.dispatcher.switchVerifier(iv);
        iv.startElement(namespaceUri, localName, qName, atts);
    }

    protected void switchToAnyOtherElement(AnyOtherElementExp[] exps, String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        for (int i = 0; i < exps.length; i++) {
            this.rule2exp.put(exps[i], exps[i]);
        }
        IslandVerifier iv = new AnyOtherElementVerifier(exps);
        this.dispatcher.switchVerifier(iv);
        this.lastNamaespaceUri = namespaceUri;
        this.lastLocalName = localName;
        this.lastQName = qName;
        iv.startElement(namespaceUri, localName, qName, atts);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier
    public void endChildIsland(String childURI, ElementDecl[] ruleSet) throws SAXException {
        ElementExp[] exps = new ElementExp[ruleSet.length];
        for (int i = 0; i < ruleSet.length; i++) {
            exps[i] = (ElementExp) this.rule2exp.get(ruleSet[i]);
            if (exps[i] == null) {
                throw new Error();
            }
        }
        Expression[] epsilons = new Expression[exps.length];
        for (int i2 = 0; i2 < epsilons.length; i2++) {
            epsilons[i2] = Expression.epsilon;
        }
        this.current = new ComplexAcceptor((REDocumentDeclaration) this.docDecl, ruleSet.length == 0 ? Expression.nullSet : Expression.epsilon, epsilons, exps);
        super.endElement(this.lastNamaespaceUri, this.lastLocalName, this.lastQName);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier
    public ElementDecl[] endIsland() {
        return ((RulesAcceptor) this.current).getSatisfiedElementDecls();
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.AbstractVerifier, com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public boolean isUnparsedEntity(String entityName) {
        if (this.unparsedEntities == null) {
            this.unparsedEntities = new HashSet();
            int len = this.dispatcher.countUnparsedEntityDecls();
            for (int i = 0; i < len; i++) {
                this.unparsedEntities.add(this.dispatcher.getUnparsedEntityDecl(i).name);
            }
        }
        return this.unparsedEntities.contains(entityName);
    }
}
