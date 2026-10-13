package com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/relaxns/grammar/relax/AnyOtherElementExp.class */
public class AnyOtherElementExp extends ElementExp implements ElementDecl {
    public NameClass nameClass;
    public transient Locator source;
    public final String includeNamespace;
    public final String excludeNamespace;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ElementExp, com.ctc.wstx.shaded.msv_core.grammar.NameClassAndExpression
    public NameClass getNameClass() {
        return this.nameClass;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl
    public String getName() {
        return "anyOtherElement:" + this.nameClass;
    }

    public AnyOtherElementExp(Locator loc, String includeNamespace, String excludeNamespace) {
        super(Expression.nullSet, true);
        this.source = loc;
        this.includeNamespace = includeNamespace;
        this.excludeNamespace = excludeNamespace;
        if (includeNamespace == null && excludeNamespace == null) {
            throw new IllegalArgumentException();
        }
        if (includeNamespace != null && excludeNamespace != null) {
            throw new IllegalArgumentException();
        }
    }

    protected void wrapUp(Grammar owner, Expression pseudoContentModel, SchemaProvider provider, ErrorHandler errorHandler) throws SAXException {
        StringTokenizer st;
        if (this.includeNamespace != null) {
            st = new StringTokenizer(this.includeNamespace);
        } else {
            st = new StringTokenizer(this.excludeNamespace);
        }
        NameClass nc = null;
        while (st.hasMoreTokens()) {
            String uri = st.nextToken();
            if (uri.equals("##local")) {
                uri = "";
            }
            if (provider.getSchemaByNamespace(uri) != null) {
                errorHandler.warning(new SAXParseException(Localizer.localize(Localizer.WRN_ANYOTHER_NAMESPACE_IGNORED, uri), this.source));
            } else {
                NamespaceNameClass nsnc = new NamespaceNameClass(uri);
                nc = nc == null ? nsnc : new ChoiceNameClass(nc, nsnc);
            }
        }
        if (this.excludeNamespace != null) {
            Iterator itr = provider.iterateNamespace();
            while (itr.hasNext()) {
                NamespaceNameClass nsnc2 = new NamespaceNameClass((String) itr.next());
                nc = nc == null ? nsnc2 : new ChoiceNameClass(nc, nsnc2);
            }
            nc = new NotNameClass(nc);
        }
        this.nameClass = nc;
        this.contentModel = owner.getPool().createMixed(owner.getPool().createZeroOrMore(owner.getPool().createChoice(this, pseudoContentModel)));
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl
    public boolean getFeature(String feature) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(feature);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl
    public Object getProperty(String property) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(property);
    }
}
