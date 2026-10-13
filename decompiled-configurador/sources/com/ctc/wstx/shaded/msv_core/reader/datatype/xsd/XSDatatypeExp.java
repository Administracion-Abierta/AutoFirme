package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FinalComponent;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TypeIncubator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/XSDatatypeExp.class */
public class XSDatatypeExp extends ReferenceExp implements GrammarReader.BackPatch {
    private final String namespaceUri;
    private XSDatatype dt;
    private ExpressionPool pool;
    private transient State ownerState;
    private transient Renderer renderer;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/XSDatatypeExp$Renderer.class */
    public interface Renderer {
        XSDatatype render(RenderingContext renderingContext) throws DatatypeException;
    }

    public XSDatatypeExp(XSDatatype dt, ExpressionPool _pool) {
        super(dt.getName());
        this.namespaceUri = dt.getNamespaceUri();
        this.dt = dt;
        this.pool = _pool;
        this.ownerState = null;
        this.renderer = null;
        this.exp = _pool.createData(dt);
    }

    public XSDatatypeExp(String nsUri, String typeName, GrammarReader reader, Renderer _renderer) {
        super(typeName);
        this.namespaceUri = nsUri;
        this.dt = null;
        this.ownerState = reader.getCurrentState();
        this.renderer = _renderer;
        this.pool = reader.pool;
        reader.addBackPatchJob(this);
    }

    private XSDatatypeExp(String nsUri, String localName) {
        super(localName);
        this.namespaceUri = nsUri;
    }

    public XSTypeIncubator createIncubator() {
        if (isLateBind()) {
            return new LazyTypeIncubator(this, this.ownerState.reader);
        }
        return new XSTypeIncubator() { // from class: com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp.1
            private final TypeIncubator core;

            {
                this.core = new TypeIncubator(XSDatatypeExp.this.dt);
            }

            @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator
            public void addFacet(String name, String value, boolean fixed, ValidationContext context) throws DatatypeException {
                this.core.addFacet(name, value, fixed, context);
            }

            @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator
            public XSDatatypeExp derive(String uri, String localName) throws DatatypeException {
                return new XSDatatypeExp(this.core.derive(uri, localName), XSDatatypeExp.this.pool);
            }
        };
    }

    public XSDatatype getCreatedType() {
        if (this.dt == null) {
            throw new IllegalStateException();
        }
        return this.dt;
    }

    public XSDatatype getType(RenderingContext context) {
        if (this.dt != null) {
            return this.dt;
        }
        if (context == null) {
            context = new RenderingContext();
        }
        if (context.callStack.contains(this)) {
            Vector locs = new Vector();
            for (int i = 0; i < context.callStack.size(); i++) {
                locs.add(((XSDatatypeExp) context.callStack.get(i)).ownerState.getLocation());
            }
            this.ownerState.reader.reportError((Locator[]) locs.toArray(new Locator[0]), GrammarReader.ERR_RECURSIVE_DATATYPE, (Object[]) null);
            return StringType.theInstance;
        }
        context.callStack.push(this);
        try {
            this.dt = this.renderer.render(context);
        } catch (DatatypeException e) {
            this.ownerState.reader.reportError(GrammarReader.ERR_BAD_TYPE, new Object[]{e}, (Exception) e, new Locator[]{this.ownerState.getLocation()});
            this.dt = StringType.theInstance;
        }
        context.callStack.pop();
        if (this.dt == null) {
            throw new Error();
        }
        this.exp = this.pool.createData(this.dt);
        return this.dt;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.BackPatch
    public void patch() {
        getType(null);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.BackPatch
    public State getOwnerState() {
        return this.ownerState;
    }

    public final boolean isLateBind() {
        return this.dt == null;
    }

    public XSDatatypeExp getClone() {
        XSDatatypeExp t = new XSDatatypeExp(this.namespaceUri, this.name);
        t.redefine(this);
        return t;
    }

    public void redefine(XSDatatypeExp rhs) {
        this.exp = rhs.exp;
        this.dt = rhs.dt;
        this.pool = rhs.pool;
        this.ownerState = rhs.ownerState;
        this.renderer = rhs.renderer;
        if (this.ownerState != null) {
            this.ownerState.reader.addBackPatchJob(this);
        }
    }

    public XSDatatypeExp createFinalizedType(final int finalValue, GrammarReader reader) {
        if (finalValue == 0) {
            return this;
        }
        if (!isLateBind()) {
            return new XSDatatypeExp(new FinalComponent((XSDatatypeImpl) this.dt, finalValue), this.pool);
        }
        return new XSDatatypeExp(this.namespaceUri, this.name, reader, new Renderer() { // from class: com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp.2
            @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp.Renderer
            public XSDatatype render(RenderingContext context) throws DatatypeException {
                return new FinalComponent((XSDatatypeImpl) XSDatatypeExp.this.getType(context), finalValue);
            }
        });
    }

    public static XSDatatypeExp makeList(final String nsUri, final String typeName, final XSDatatypeExp itemType, GrammarReader reader) throws DatatypeException {
        if (!itemType.isLateBind()) {
            return new XSDatatypeExp(DatatypeFactory.deriveByList(nsUri, typeName, itemType.dt), reader.pool);
        }
        return new XSDatatypeExp(nsUri, typeName, reader, new Renderer() { // from class: com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp.3
            @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp.Renderer
            public XSDatatype render(RenderingContext context) throws DatatypeException {
                return DatatypeFactory.deriveByList(nsUri, typeName, itemType.getType(context));
            }
        });
    }

    public static XSDatatypeExp makeUnion(final String typeNameUri, final String typeName, final Collection members, GrammarReader reader) throws DatatypeException {
        final XSDatatype[] m = new XSDatatype[members.size()];
        int i = 0;
        Iterator itr = members.iterator();
        while (itr.hasNext()) {
            XSDatatypeExp item = (XSDatatypeExp) itr.next();
            if (item.isLateBind()) {
                return new XSDatatypeExp(typeNameUri, typeName, reader, new Renderer() { // from class: com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp.4
                    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp.Renderer
                    public XSDatatype render(RenderingContext context) throws DatatypeException {
                        int i2 = 0;
                        Iterator itr2 = members.iterator();
                        while (itr2.hasNext()) {
                            int i3 = i2;
                            i2++;
                            m[i3] = ((XSDatatypeExp) itr2.next()).getType(context);
                        }
                        return DatatypeFactory.deriveByUnion(typeNameUri, typeName, m);
                    }
                });
            }
            int i2 = i;
            i++;
            m[i2] = item.dt;
        }
        return new XSDatatypeExp(DatatypeFactory.deriveByUnion(typeNameUri, typeName, m), reader.pool);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/XSDatatypeExp$RenderingContext.class */
    public static class RenderingContext {
        private final Stack callStack = new Stack();

        RenderingContext() {
        }
    }
}
