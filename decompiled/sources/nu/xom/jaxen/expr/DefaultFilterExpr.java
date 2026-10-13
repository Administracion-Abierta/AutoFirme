package nu.xom.jaxen.expr;

import java.util.ArrayList;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultFilterExpr.class */
public class DefaultFilterExpr extends DefaultExpr implements FilterExpr, Predicated {
    private static final long serialVersionUID = -549640659288005735L;
    private Expr expr;
    private PredicateSet predicates;

    public DefaultFilterExpr(PredicateSet predicateSet) {
        this.predicates = predicateSet;
    }

    public DefaultFilterExpr(Expr expr, PredicateSet predicateSet) {
        this.expr = expr;
        this.predicates = predicateSet;
    }

    @Override // nu.xom.jaxen.expr.Predicated
    public void addPredicate(Predicate predicate) {
        this.predicates.addPredicate(predicate);
    }

    @Override // nu.xom.jaxen.expr.Predicated
    public List getPredicates() {
        return this.predicates.getPredicates();
    }

    @Override // nu.xom.jaxen.expr.Predicated
    public PredicateSet getPredicateSet() {
        return this.predicates;
    }

    @Override // nu.xom.jaxen.expr.FilterExpr
    public Expr getExpr() {
        return this.expr;
    }

    public String toString() {
        return new StringBuffer().append("[(DefaultFilterExpr): expr: ").append(this.expr).append(" predicates: ").append(this.predicates).append(" ]").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public String getText() {
        return new StringBuffer().append(this.expr != null ? this.expr.getText() : "").append(this.predicates.getText()).toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultExpr, nu.xom.jaxen.expr.Expr
    public Expr simplify() {
        this.predicates.simplify();
        if (this.expr != null) {
            this.expr = this.expr.simplify();
        }
        return this.predicates.getPredicates().size() == 0 ? getExpr() : this;
    }

    @Override // nu.xom.jaxen.expr.FilterExpr
    public boolean asBoolean(Context context) throws JaxenException {
        Object objEvaluate;
        if (this.expr != null) {
            objEvaluate = this.expr.evaluate(context);
        } else {
            List nodeSet = context.getNodeSet();
            ArrayList arrayList = new ArrayList(nodeSet.size());
            arrayList.addAll(nodeSet);
            objEvaluate = arrayList;
        }
        if (objEvaluate instanceof Boolean) {
            return ((Boolean) objEvaluate).booleanValue();
        }
        if (objEvaluate instanceof List) {
            return getPredicateSet().evaluateAsBoolean((List) objEvaluate, context.getContextSupport());
        }
        return false;
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        Object objEvaluate = getExpr().evaluate(context);
        if (objEvaluate instanceof List) {
            objEvaluate = getPredicateSet().evaluatePredicates((List) objEvaluate, context.getContextSupport());
        }
        return objEvaluate;
    }
}
