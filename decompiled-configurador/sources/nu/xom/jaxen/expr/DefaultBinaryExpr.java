package nu.xom.jaxen.expr;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultBinaryExpr.class */
abstract class DefaultBinaryExpr extends DefaultExpr implements BinaryExpr {
    private Expr lhs;
    private Expr rhs;

    DefaultBinaryExpr(Expr expr, Expr expr2) {
        this.lhs = expr;
        this.rhs = expr2;
    }

    @Override // nu.xom.jaxen.expr.BinaryExpr
    public Expr getLHS() {
        return this.lhs;
    }

    @Override // nu.xom.jaxen.expr.BinaryExpr
    public Expr getRHS() {
        return this.rhs;
    }

    public void setLHS(Expr expr) {
        this.lhs = expr;
    }

    public void setRHS(Expr expr) {
        this.rhs = expr;
    }

    public abstract String getOperator();

    @Override // nu.xom.jaxen.expr.Expr
    public String getText() {
        return new StringBuffer().append("(").append(getLHS().getText()).append(" ").append(getOperator()).append(" ").append(getRHS().getText()).append(")").toString();
    }

    public String toString() {
        return new StringBuffer().append("[").append(getClass().getName()).append(": ").append(getLHS()).append(", ").append(getRHS()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultExpr, nu.xom.jaxen.expr.Expr
    public Expr simplify() {
        setLHS(getLHS().simplify());
        setRHS(getRHS().simplify());
        return this;
    }
}
