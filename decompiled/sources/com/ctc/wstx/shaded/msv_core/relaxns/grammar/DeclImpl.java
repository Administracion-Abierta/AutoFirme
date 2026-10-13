package com.ctc.wstx.shaded.msv_core.relaxns.grammar;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import java.io.Serializable;
import org.xml.sax.SAXNotRecognizedException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/grammar/DeclImpl.class */
public class DeclImpl implements ElementDecl, AttributesDecl, Serializable {
    public final Expression exp;
    protected final String name;

    public DeclImpl(ReferenceExp exp) {
        this(exp.name, exp.exp);
    }

    public DeclImpl(String name, Expression exp) {
        this.exp = exp;
        this.name = name;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl
    public String getName() {
        return this.name;
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
