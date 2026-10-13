package nu.xom.jaxen.expr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultFunctionCallExpr.class */
public class DefaultFunctionCallExpr extends DefaultExpr implements FunctionCallExpr {
    private static final long serialVersionUID = -4747789292572193708L;
    private String prefix;
    private String functionName;
    private List parameters = new ArrayList();

    public DefaultFunctionCallExpr(String str, String str2) {
        this.prefix = str;
        this.functionName = str2;
    }

    @Override // nu.xom.jaxen.expr.FunctionCallExpr
    public void addParameter(Expr expr) {
        this.parameters.add(expr);
    }

    @Override // nu.xom.jaxen.expr.FunctionCallExpr
    public List getParameters() {
        return this.parameters;
    }

    @Override // nu.xom.jaxen.expr.FunctionCallExpr
    public String getPrefix() {
        return this.prefix;
    }

    @Override // nu.xom.jaxen.expr.FunctionCallExpr
    public String getFunctionName() {
        return this.functionName;
    }

    @Override // nu.xom.jaxen.expr.Expr
    public String getText() {
        StringBuffer stringBuffer = new StringBuffer();
        String prefix = getPrefix();
        if (prefix != null && prefix.length() > 0) {
            stringBuffer.append(prefix);
            stringBuffer.append(":");
        }
        stringBuffer.append(getFunctionName());
        stringBuffer.append("(");
        Iterator it = getParameters().iterator();
        while (it.hasNext()) {
            stringBuffer.append(((Expr) it.next()).getText());
            if (it.hasNext()) {
                stringBuffer.append(", ");
            }
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultExpr, nu.xom.jaxen.expr.Expr
    public Expr simplify() {
        List parameters = getParameters();
        int size = parameters.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            arrayList.add(((Expr) parameters.get(i)).simplify());
        }
        this.parameters = arrayList;
        return this;
    }

    public String toString() {
        return getPrefix() == null ? new StringBuffer().append("[(DefaultFunctionCallExpr): ").append(getFunctionName()).append("(").append(getParameters()).append(") ]").toString() : new StringBuffer().append("[(DefaultFunctionCallExpr): ").append(getPrefix()).append(":").append(getFunctionName()).append("(").append(getParameters()).append(") ]").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        String prefix = getPrefix();
        String strTranslateNamespacePrefixToUri = null;
        if (prefix != null && !"".equals(prefix)) {
            strTranslateNamespacePrefixToUri = context.translateNamespacePrefixToUri(prefix);
        }
        return context.getFunction(strTranslateNamespacePrefixToUri, prefix, getFunctionName()).call(context, evaluateParams(context));
    }

    public List evaluateParams(Context context) throws JaxenException {
        List parameters = getParameters();
        int size = parameters.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            arrayList.add(((Expr) parameters.get(i)).evaluate(context));
        }
        return arrayList;
    }
}
