package nu.xom.jaxen.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultLocationPath.class */
abstract class DefaultLocationPath extends DefaultExpr implements LocationPath {
    private List steps = new LinkedList();

    DefaultLocationPath() {
    }

    @Override // nu.xom.jaxen.expr.LocationPath
    public void addStep(Step step) {
        getSteps().add(step);
    }

    @Override // nu.xom.jaxen.expr.LocationPath
    public List getSteps() {
        return this.steps;
    }

    @Override // nu.xom.jaxen.expr.DefaultExpr, nu.xom.jaxen.expr.Expr
    public Expr simplify() {
        Iterator it = getSteps().iterator();
        while (it.hasNext()) {
            ((Step) it.next()).simplify();
        }
        return this;
    }

    @Override // nu.xom.jaxen.expr.Expr
    public String getText() {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator it = getSteps().iterator();
        while (it.hasNext()) {
            stringBuffer.append(((Step) it.next()).getText());
            if (it.hasNext()) {
                stringBuffer.append("/");
            }
        }
        return stringBuffer.toString();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator it = getSteps().iterator();
        while (it.hasNext()) {
            stringBuffer.append(it.next().toString());
            if (it.hasNext()) {
                stringBuffer.append("/");
            }
        }
        return stringBuffer.toString();
    }

    public boolean isAbsolute() {
        return false;
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        List nodeSet = context.getNodeSet();
        List arrayList = new ArrayList(nodeSet);
        ContextSupport contextSupport = context.getContextSupport();
        Context context2 = new Context(contextSupport);
        for (Step step : getSteps()) {
            context2.setNodeSet(arrayList);
            arrayList = step.evaluate(context2);
            if (isReverseAxis(step)) {
                Collections.reverse(arrayList);
            }
        }
        if (getSteps().size() > 1 || nodeSet.size() > 1) {
            Collections.sort(arrayList, new NodeComparator(contextSupport.getNavigator()));
        }
        return arrayList;
    }

    private boolean isReverseAxis(Step step) {
        int axis = step.getAxis();
        return axis == 8 || axis == 6 || axis == 4 || axis == 13;
    }
}
