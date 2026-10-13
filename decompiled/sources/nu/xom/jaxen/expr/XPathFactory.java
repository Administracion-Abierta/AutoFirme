package nu.xom.jaxen.expr;

import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/XPathFactory.class */
public interface XPathFactory {
    XPathExpr createXPath(Expr expr) throws JaxenException;

    PathExpr createPathExpr(FilterExpr filterExpr, LocationPath locationPath) throws JaxenException;

    LocationPath createRelativeLocationPath() throws JaxenException;

    LocationPath createAbsoluteLocationPath() throws JaxenException;

    BinaryExpr createOrExpr(Expr expr, Expr expr2) throws JaxenException;

    BinaryExpr createAndExpr(Expr expr, Expr expr2) throws JaxenException;

    BinaryExpr createEqualityExpr(Expr expr, Expr expr2, int i) throws JaxenException;

    BinaryExpr createRelationalExpr(Expr expr, Expr expr2, int i) throws JaxenException;

    BinaryExpr createAdditiveExpr(Expr expr, Expr expr2, int i) throws JaxenException;

    BinaryExpr createMultiplicativeExpr(Expr expr, Expr expr2, int i) throws JaxenException;

    Expr createUnaryExpr(Expr expr, int i) throws JaxenException;

    UnionExpr createUnionExpr(Expr expr, Expr expr2) throws JaxenException;

    FilterExpr createFilterExpr(Expr expr) throws JaxenException;

    FunctionCallExpr createFunctionCallExpr(String str, String str2) throws JaxenException;

    NumberExpr createNumberExpr(int i) throws JaxenException;

    NumberExpr createNumberExpr(double d) throws JaxenException;

    LiteralExpr createLiteralExpr(String str) throws JaxenException;

    VariableReferenceExpr createVariableReferenceExpr(String str, String str2) throws JaxenException;

    Step createNameStep(int i, String str, String str2) throws JaxenException;

    Step createAllNodeStep(int i) throws JaxenException;

    Step createCommentNodeStep(int i) throws JaxenException;

    Step createTextNodeStep(int i) throws JaxenException;

    Step createProcessingInstructionNodeStep(int i, String str) throws JaxenException;

    Predicate createPredicate(Expr expr) throws JaxenException;

    PredicateSet createPredicateSet() throws JaxenException;
}
