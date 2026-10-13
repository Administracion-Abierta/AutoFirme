package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultPathExpr.class */
class DefaultPathExpr extends DefaultExpr implements PathExpr {
    private static final long serialVersionUID = -6593934674727004281L;
    private Expr filterExpr;
    private LocationPath locationPath;

    DefaultPathExpr(Expr expr, LocationPath locationPath) {
        this.filterExpr = expr;
        this.locationPath = locationPath;
    }

    @Override // nu.xom.jaxen.expr.PathExpr
    public Expr getFilterExpr() {
        return this.filterExpr;
    }

    @Override // nu.xom.jaxen.expr.PathExpr
    public void setFilterExpr(Expr expr) {
        this.filterExpr = expr;
    }

    @Override // nu.xom.jaxen.expr.PathExpr
    public LocationPath getLocationPath() {
        return this.locationPath;
    }

    public String toString() {
        return getLocationPath() != null ? new StringBuffer().append("[(DefaultPathExpr): ").append(getFilterExpr()).append(", ").append(getLocationPath()).append("]").toString() : new StringBuffer().append("[(DefaultPathExpr): ").append(getFilterExpr()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public String getText() {
        StringBuffer stringBuffer = new StringBuffer();
        if (getFilterExpr() != null) {
            stringBuffer.append(getFilterExpr().getText());
        }
        if (getLocationPath() != null) {
            if (!getLocationPath().getSteps().isEmpty()) {
                stringBuffer.append("/");
            }
            stringBuffer.append(getLocationPath().getText());
        }
        return stringBuffer.toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultExpr, nu.xom.jaxen.expr.Expr
    public Expr simplify() {
        if (getFilterExpr() != null) {
            setFilterExpr(getFilterExpr().simplify());
        }
        if (getLocationPath() != null) {
            getLocationPath().simplify();
        }
        if (getFilterExpr() == null && getLocationPath() == null) {
            return null;
        }
        if (getLocationPath() == null) {
            return getFilterExpr();
        }
        return getFilterExpr() == null ? getLocationPath() : this;
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        Object objEvaluate = null;
        Context context2 = null;
        if (getFilterExpr() != null) {
            objEvaluate = getFilterExpr().evaluate(context);
            context2 = new Context(context.getContextSupport());
            context2.setNodeSet(convertToList(objEvaluate));
        }
        return getLocationPath() != null ? getLocationPath().evaluate(context2) : objEvaluate;
    }
}
