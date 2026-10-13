package nu.xom.jaxen;

import java.util.Iterator;
import java.util.LinkedList;
import nu.xom.jaxen.expr.DefaultXPathFactory;
import nu.xom.jaxen.expr.Expr;
import nu.xom.jaxen.expr.FilterExpr;
import nu.xom.jaxen.expr.FunctionCallExpr;
import nu.xom.jaxen.expr.LocationPath;
import nu.xom.jaxen.expr.Predicate;
import nu.xom.jaxen.expr.Predicated;
import nu.xom.jaxen.expr.Step;
import nu.xom.jaxen.expr.XPathExpr;
import nu.xom.jaxen.expr.XPathFactory;
import nu.xom.jaxen.saxpath.XPathHandler;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/JaxenHandler.class */
public class JaxenHandler implements XPathHandler {
    private XPathExpr xpath;
    protected boolean simplified;
    protected LinkedList stack = new LinkedList();
    private XPathFactory xpathFactory = new DefaultXPathFactory();

    public void setXPathFactory(XPathFactory xPathFactory) {
        this.xpathFactory = xPathFactory;
    }

    public XPathFactory getXPathFactory() {
        return this.xpathFactory;
    }

    public XPathExpr getXPathExpr() {
        return getXPathExpr(true);
    }

    public XPathExpr getXPathExpr(boolean z) {
        if (z && !this.simplified) {
            this.xpath.simplify();
            this.simplified = true;
        }
        return this.xpath;
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startXPath() {
        this.simplified = false;
        pushFrame();
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endXPath() throws JaxenException {
        this.xpath = getXPathFactory().createXPath((Expr) pop());
        popFrame();
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startPathExpr() {
        pushFrame();
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endPathExpr() throws JaxenException {
        LocationPath locationPath;
        FilterExpr filterExpr;
        if (stackSize() == 2) {
            locationPath = (LocationPath) pop();
            filterExpr = (FilterExpr) pop();
        } else {
            Object objPop = pop();
            if (objPop instanceof LocationPath) {
                locationPath = (LocationPath) objPop;
                filterExpr = null;
            } else {
                locationPath = null;
                filterExpr = (FilterExpr) objPop;
            }
        }
        popFrame();
        push(getXPathFactory().createPathExpr(filterExpr, locationPath));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startAbsoluteLocationPath() throws JaxenException {
        pushFrame();
        push(getXPathFactory().createAbsoluteLocationPath());
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endAbsoluteLocationPath() throws JaxenException {
        endLocationPath();
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startRelativeLocationPath() throws JaxenException {
        pushFrame();
        push(getXPathFactory().createRelativeLocationPath());
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endRelativeLocationPath() throws JaxenException {
        endLocationPath();
    }

    protected void endLocationPath() throws JaxenException {
        LocationPath locationPath = (LocationPath) peekFrame().removeFirst();
        addSteps(locationPath, popFrame().iterator());
        push(locationPath);
    }

    protected void addSteps(LocationPath locationPath, Iterator it) {
        while (it.hasNext()) {
            locationPath.addStep((Step) it.next());
        }
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startNameStep(int i, String str, String str2) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createNameStep(i, str, str2));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endNameStep() {
        endStep();
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startTextNodeStep(int i) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createTextNodeStep(i));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endTextNodeStep() {
        endStep();
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startCommentNodeStep(int i) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createCommentNodeStep(i));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endCommentNodeStep() {
        endStep();
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startAllNodeStep(int i) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createAllNodeStep(i));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endAllNodeStep() {
        endStep();
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startProcessingInstructionNodeStep(int i, String str) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createProcessingInstructionNodeStep(i, str));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endProcessingInstructionNodeStep() {
        endStep();
    }

    protected void endStep() {
        Step step = (Step) peekFrame().removeFirst();
        addPredicates(step, popFrame().iterator());
        push(step);
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startPredicate() {
        pushFrame();
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endPredicate() throws JaxenException {
        Predicate predicateCreatePredicate = getXPathFactory().createPredicate((Expr) pop());
        popFrame();
        push(predicateCreatePredicate);
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startFilterExpr() {
        pushFrame();
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endFilterExpr() throws JaxenException {
        FilterExpr filterExprCreateFilterExpr = getXPathFactory().createFilterExpr((Expr) peekFrame().removeFirst());
        addPredicates(filterExprCreateFilterExpr, popFrame().iterator());
        push(filterExprCreateFilterExpr);
    }

    protected void addPredicates(Predicated predicated, Iterator it) {
        while (it.hasNext()) {
            predicated.addPredicate((Predicate) it.next());
        }
    }

    protected void returnExpr() {
        Expr expr = (Expr) pop();
        popFrame();
        push(expr);
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startOrExpr() {
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endOrExpr(boolean z) throws JaxenException {
        if (z) {
            Expr expr = (Expr) pop();
            push(getXPathFactory().createOrExpr((Expr) pop(), expr));
        }
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startAndExpr() {
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endAndExpr(boolean z) throws JaxenException {
        if (z) {
            Expr expr = (Expr) pop();
            push(getXPathFactory().createAndExpr((Expr) pop(), expr));
        }
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startEqualityExpr() {
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endEqualityExpr(int i) throws JaxenException {
        if (i != 0) {
            Expr expr = (Expr) pop();
            push(getXPathFactory().createEqualityExpr((Expr) pop(), expr, i));
        }
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startRelationalExpr() {
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endRelationalExpr(int i) throws JaxenException {
        if (i != 0) {
            Expr expr = (Expr) pop();
            push(getXPathFactory().createRelationalExpr((Expr) pop(), expr, i));
        }
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startAdditiveExpr() {
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endAdditiveExpr(int i) throws JaxenException {
        if (i != 0) {
            Expr expr = (Expr) pop();
            push(getXPathFactory().createAdditiveExpr((Expr) pop(), expr, i));
        }
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startMultiplicativeExpr() {
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endMultiplicativeExpr(int i) throws JaxenException {
        if (i != 0) {
            Expr expr = (Expr) pop();
            push(getXPathFactory().createMultiplicativeExpr((Expr) pop(), expr, i));
        }
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startUnaryExpr() {
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endUnaryExpr(int i) throws JaxenException {
        if (i != 0) {
            push(getXPathFactory().createUnaryExpr((Expr) pop(), i));
        }
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startUnionExpr() {
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endUnionExpr(boolean z) throws JaxenException {
        if (z) {
            Expr expr = (Expr) pop();
            push(getXPathFactory().createUnionExpr((Expr) pop(), expr));
        }
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void number(int i) throws JaxenException {
        push(getXPathFactory().createNumberExpr(i));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void number(double d) throws JaxenException {
        push(getXPathFactory().createNumberExpr(d));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void literal(String str) throws JaxenException {
        push(getXPathFactory().createLiteralExpr(str));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void variableReference(String str, String str2) throws JaxenException {
        push(getXPathFactory().createVariableReferenceExpr(str, str2));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void startFunction(String str, String str2) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createFunctionCallExpr(str, str2));
    }

    @Override // nu.xom.jaxen.saxpath.XPathHandler
    public void endFunction() {
        FunctionCallExpr functionCallExpr = (FunctionCallExpr) peekFrame().removeFirst();
        addParameters(functionCallExpr, popFrame().iterator());
        push(functionCallExpr);
    }

    protected void addParameters(FunctionCallExpr functionCallExpr, Iterator it) {
        while (it.hasNext()) {
            functionCallExpr.addParameter((Expr) it.next());
        }
    }

    protected int stackSize() {
        return peekFrame().size();
    }

    protected void push(Object obj) {
        peekFrame().addLast(obj);
    }

    protected Object pop() {
        return peekFrame().removeLast();
    }

    protected boolean canPop() {
        return peekFrame().size() > 0;
    }

    protected void pushFrame() {
        this.stack.addLast(new LinkedList());
    }

    protected LinkedList popFrame() {
        return (LinkedList) this.stack.removeLast();
    }

    protected LinkedList peekFrame() {
        return (LinkedList) this.stack.getLast();
    }
}
