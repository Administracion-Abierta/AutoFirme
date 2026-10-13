package nu.xom.jaxen.expr;

import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.expr.iter.IterableAncestorAxis;
import nu.xom.jaxen.expr.iter.IterableAncestorOrSelfAxis;
import nu.xom.jaxen.expr.iter.IterableAttributeAxis;
import nu.xom.jaxen.expr.iter.IterableAxis;
import nu.xom.jaxen.expr.iter.IterableChildAxis;
import nu.xom.jaxen.expr.iter.IterableDescendantAxis;
import nu.xom.jaxen.expr.iter.IterableDescendantOrSelfAxis;
import nu.xom.jaxen.expr.iter.IterableFollowingAxis;
import nu.xom.jaxen.expr.iter.IterableFollowingSiblingAxis;
import nu.xom.jaxen.expr.iter.IterableNamespaceAxis;
import nu.xom.jaxen.expr.iter.IterableParentAxis;
import nu.xom.jaxen.expr.iter.IterablePrecedingAxis;
import nu.xom.jaxen.expr.iter.IterablePrecedingSiblingAxis;
import nu.xom.jaxen.expr.iter.IterableSelfAxis;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultXPathFactory.class */
public class DefaultXPathFactory implements XPathFactory {
    @Override // nu.xom.jaxen.expr.XPathFactory
    public XPathExpr createXPath(Expr expr) throws JaxenException {
        return new DefaultXPathExpr(expr);
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public PathExpr createPathExpr(FilterExpr filterExpr, LocationPath locationPath) throws JaxenException {
        return new DefaultPathExpr(filterExpr, locationPath);
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public LocationPath createRelativeLocationPath() throws JaxenException {
        return new DefaultRelativeLocationPath();
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public LocationPath createAbsoluteLocationPath() throws JaxenException {
        return new DefaultAbsoluteLocationPath();
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public BinaryExpr createOrExpr(Expr expr, Expr expr2) throws JaxenException {
        return new DefaultOrExpr(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public BinaryExpr createAndExpr(Expr expr, Expr expr2) throws JaxenException {
        return new DefaultAndExpr(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public BinaryExpr createEqualityExpr(Expr expr, Expr expr2, int i) throws JaxenException {
        switch (i) {
            case 1:
                return new DefaultEqualsExpr(expr, expr2);
            case 2:
                return new DefaultNotEqualsExpr(expr, expr2);
            default:
                throw new JaxenException(new StringBuffer().append("Unhandled operator in createEqualityExpr(): ").append(i).toString());
        }
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public BinaryExpr createRelationalExpr(Expr expr, Expr expr2, int i) throws JaxenException {
        switch (i) {
            case 3:
                return new DefaultLessThanExpr(expr, expr2);
            case 4:
                return new DefaultLessThanEqualExpr(expr, expr2);
            case 5:
                return new DefaultGreaterThanExpr(expr, expr2);
            case 6:
                return new DefaultGreaterThanEqualExpr(expr, expr2);
            default:
                throw new JaxenException(new StringBuffer().append("Unhandled operator in createRelationalExpr(): ").append(i).toString());
        }
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public BinaryExpr createAdditiveExpr(Expr expr, Expr expr2, int i) throws JaxenException {
        switch (i) {
            case 7:
                return new DefaultPlusExpr(expr, expr2);
            case 8:
                return new DefaultMinusExpr(expr, expr2);
            default:
                throw new JaxenException(new StringBuffer().append("Unhandled operator in createAdditiveExpr(): ").append(i).toString());
        }
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public BinaryExpr createMultiplicativeExpr(Expr expr, Expr expr2, int i) throws JaxenException {
        switch (i) {
            case 9:
                return new DefaultMultiplyExpr(expr, expr2);
            case 10:
                return new DefaultModExpr(expr, expr2);
            case 11:
                return new DefaultDivExpr(expr, expr2);
            default:
                throw new JaxenException(new StringBuffer().append("Unhandled operator in createMultiplicativeExpr(): ").append(i).toString());
        }
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public Expr createUnaryExpr(Expr expr, int i) throws JaxenException {
        switch (i) {
            case 12:
                return new DefaultUnaryExpr(expr);
            default:
                return expr;
        }
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public UnionExpr createUnionExpr(Expr expr, Expr expr2) throws JaxenException {
        return new DefaultUnionExpr(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public FilterExpr createFilterExpr(Expr expr) throws JaxenException {
        return new DefaultFilterExpr(expr, createPredicateSet());
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public FunctionCallExpr createFunctionCallExpr(String str, String str2) throws JaxenException {
        return new DefaultFunctionCallExpr(str, str2);
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public NumberExpr createNumberExpr(int i) throws JaxenException {
        return new DefaultNumberExpr(new Double(i));
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public NumberExpr createNumberExpr(double d) throws JaxenException {
        return new DefaultNumberExpr(new Double(d));
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public LiteralExpr createLiteralExpr(String str) throws JaxenException {
        return new DefaultLiteralExpr(str);
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public VariableReferenceExpr createVariableReferenceExpr(String str, String str2) throws JaxenException {
        return new DefaultVariableReferenceExpr(str, str2);
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public Step createNameStep(int i, String str, String str2) throws JaxenException {
        return new DefaultNameStep(getIterableAxis(i), str, str2, createPredicateSet());
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public Step createTextNodeStep(int i) throws JaxenException {
        return new DefaultTextNodeStep(getIterableAxis(i), createPredicateSet());
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public Step createCommentNodeStep(int i) throws JaxenException {
        return new DefaultCommentNodeStep(getIterableAxis(i), createPredicateSet());
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public Step createAllNodeStep(int i) throws JaxenException {
        return new DefaultAllNodeStep(getIterableAxis(i), createPredicateSet());
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public Step createProcessingInstructionNodeStep(int i, String str) throws JaxenException {
        return new DefaultProcessingInstructionNodeStep(getIterableAxis(i), str, createPredicateSet());
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public Predicate createPredicate(Expr expr) throws JaxenException {
        return new DefaultPredicate(expr);
    }

    protected IterableAxis getIterableAxis(int i) throws JaxenException {
        switch (i) {
            case 1:
                return new IterableChildAxis(i);
            case 2:
                return new IterableDescendantAxis(i);
            case 3:
                return new IterableParentAxis(i);
            case 4:
                return new IterableAncestorAxis(i);
            case 5:
                return new IterableFollowingSiblingAxis(i);
            case 6:
                return new IterablePrecedingSiblingAxis(i);
            case 7:
                return new IterableFollowingAxis(i);
            case 8:
                return new IterablePrecedingAxis(i);
            case 9:
                return new IterableAttributeAxis(i);
            case 10:
                return new IterableNamespaceAxis(i);
            case 11:
                return new IterableSelfAxis(i);
            case 12:
                return new IterableDescendantOrSelfAxis(i);
            case 13:
                return new IterableAncestorOrSelfAxis(i);
            default:
                throw new JaxenException(new StringBuffer().append("Unrecognized axis code: ").append(i).toString());
        }
    }

    @Override // nu.xom.jaxen.expr.XPathFactory
    public PredicateSet createPredicateSet() throws JaxenException {
        return new PredicateSet();
    }
}
