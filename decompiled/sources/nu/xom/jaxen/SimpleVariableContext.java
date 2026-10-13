package nu.xom.jaxen;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/SimpleVariableContext.class */
public class SimpleVariableContext implements VariableContext, Serializable {
    private static final long serialVersionUID = 961322093794516518L;
    private Map variables = new HashMap();

    public void setVariableValue(String str, String str2, Object obj) {
        this.variables.put(new QualifiedName(str, str2), obj);
    }

    public void setVariableValue(String str, Object obj) {
        this.variables.put(new QualifiedName(null, str), obj);
    }

    @Override // nu.xom.jaxen.VariableContext
    public Object getVariableValue(String str, String str2, String str3) throws UnresolvableException {
        QualifiedName qualifiedName = new QualifiedName(str, str3);
        if (this.variables.containsKey(qualifiedName)) {
            return this.variables.get(qualifiedName);
        }
        throw new UnresolvableException(new StringBuffer().append("Variable ").append(qualifiedName.getClarkForm()).toString());
    }
}
