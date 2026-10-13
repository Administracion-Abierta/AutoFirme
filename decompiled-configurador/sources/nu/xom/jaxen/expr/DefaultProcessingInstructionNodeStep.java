package nu.xom.jaxen.expr;

import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.expr.iter.IterableAxis;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultProcessingInstructionNodeStep.class */
public class DefaultProcessingInstructionNodeStep extends DefaultStep implements ProcessingInstructionNodeStep {
    private static final long serialVersionUID = -4825000697808126927L;
    private String name;

    public DefaultProcessingInstructionNodeStep(IterableAxis iterableAxis, String str, PredicateSet predicateSet) {
        super(iterableAxis, predicateSet);
        this.name = str;
    }

    @Override // nu.xom.jaxen.expr.ProcessingInstructionNodeStep
    public String getName() {
        return this.name;
    }

    @Override // nu.xom.jaxen.expr.DefaultStep, nu.xom.jaxen.expr.Step
    public String getText() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getAxisName());
        stringBuffer.append("::processing-instruction(");
        String name = getName();
        if (name != null && name.length() != 0) {
            stringBuffer.append("'");
            stringBuffer.append(name);
            stringBuffer.append("'");
        }
        stringBuffer.append(")");
        stringBuffer.append(super.getText());
        return stringBuffer.toString();
    }

    @Override // nu.xom.jaxen.expr.Step
    public boolean matches(Object obj, ContextSupport contextSupport) {
        Navigator navigator = contextSupport.getNavigator();
        if (!navigator.isProcessingInstruction(obj)) {
            return false;
        }
        String name = getName();
        if (name == null || name.length() == 0) {
            return true;
        }
        return name.equals(navigator.getProcessingInstructionTarget(obj));
    }
}
