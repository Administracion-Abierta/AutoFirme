package org.mozilla.javascript.ast;

import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Node;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/FunctionNode.class */
public class FunctionNode extends ScriptNode {
    public static final int FUNCTION_STATEMENT = 1;
    public static final int FUNCTION_EXPRESSION = 2;
    public static final int FUNCTION_EXPRESSION_STATEMENT = 3;
    public static final int ARROW_FUNCTION = 4;
    private static final List<AstNode> NO_PARAMS = Collections.unmodifiableList(new ArrayList());
    private Name functionName;
    private List<AstNode> params;
    private AstNode body;
    private boolean isExpressionClosure;
    private Form functionForm;
    private int lp;
    private int rp;
    private int functionType;
    private boolean needsActivation;
    private boolean isGenerator;
    private boolean isES6Generator;
    private List<Node> generatorResumePoints;
    private Map<Node, int[]> liveLocals;
    private AstNode memberExprNode;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/FunctionNode$Form.class */
    public enum Form {
        FUNCTION,
        GETTER,
        SETTER,
        METHOD
    }

    public FunctionNode() {
        this.functionForm = Form.FUNCTION;
        this.lp = -1;
        this.rp = -1;
        this.type = 110;
    }

    public FunctionNode(int pos) {
        super(pos);
        this.functionForm = Form.FUNCTION;
        this.lp = -1;
        this.rp = -1;
        this.type = 110;
    }

    public FunctionNode(int pos, Name name) {
        super(pos);
        this.functionForm = Form.FUNCTION;
        this.lp = -1;
        this.rp = -1;
        this.type = 110;
        setFunctionName(name);
    }

    public Name getFunctionName() {
        return this.functionName;
    }

    public void setFunctionName(Name name) {
        this.functionName = name;
        if (name != null) {
            name.setParent(this);
        }
    }

    public String getName() {
        return this.functionName != null ? this.functionName.getIdentifier() : "";
    }

    public List<AstNode> getParams() {
        return this.params != null ? this.params : NO_PARAMS;
    }

    public void setParams(List<AstNode> params) {
        if (params == null) {
            this.params = null;
            return;
        }
        if (this.params != null) {
            this.params.clear();
        }
        for (AstNode param : params) {
            addParam(param);
        }
    }

    public void addParam(AstNode param) {
        assertNotNull(param);
        if (this.params == null) {
            this.params = new ArrayList();
        }
        this.params.add(param);
        param.setParent(this);
    }

    public boolean isParam(AstNode node) {
        if (this.params == null) {
            return false;
        }
        return this.params.contains(node);
    }

    public AstNode getBody() {
        return this.body;
    }

    public void setBody(AstNode body) {
        assertNotNull(body);
        this.body = body;
        if (Boolean.TRUE.equals(body.getProp(25))) {
            setIsExpressionClosure(true);
        }
        int absEnd = body.getPosition() + body.getLength();
        body.setParent(this);
        setLength(absEnd - this.position);
        setEncodedSourceBounds(this.position, absEnd);
    }

    public int getLp() {
        return this.lp;
    }

    public void setLp(int lp) {
        this.lp = lp;
    }

    public int getRp() {
        return this.rp;
    }

    public void setRp(int rp) {
        this.rp = rp;
    }

    public void setParens(int lp, int rp) {
        this.lp = lp;
        this.rp = rp;
    }

    public boolean isExpressionClosure() {
        return this.isExpressionClosure;
    }

    public void setIsExpressionClosure(boolean isExpressionClosure) {
        this.isExpressionClosure = isExpressionClosure;
    }

    public boolean requiresActivation() {
        return this.needsActivation;
    }

    public void setRequiresActivation() {
        this.needsActivation = true;
    }

    public boolean isGenerator() {
        return this.isGenerator;
    }

    public void setIsGenerator() {
        this.isGenerator = true;
    }

    public boolean isES6Generator() {
        return this.isES6Generator;
    }

    public void setIsES6Generator() {
        this.isES6Generator = true;
        this.isGenerator = true;
    }

    public void addResumptionPoint(Node target) {
        if (this.generatorResumePoints == null) {
            this.generatorResumePoints = new ArrayList();
        }
        this.generatorResumePoints.add(target);
    }

    public List<Node> getResumptionPoints() {
        return this.generatorResumePoints;
    }

    public Map<Node, int[]> getLiveLocals() {
        return this.liveLocals;
    }

    public void addLiveLocals(Node node, int[] locals) {
        if (this.liveLocals == null) {
            this.liveLocals = new HashMap();
        }
        this.liveLocals.put(node, locals);
    }

    @Override // org.mozilla.javascript.ast.ScriptNode
    public int addFunction(FunctionNode fnNode) {
        int result = super.addFunction(fnNode);
        if (getFunctionCount() > 0) {
            this.needsActivation = true;
        }
        return result;
    }

    public int getFunctionType() {
        return this.functionType;
    }

    public void setFunctionType(int type) {
        this.functionType = type;
    }

    public boolean isMethod() {
        return this.functionForm == Form.GETTER || this.functionForm == Form.SETTER || this.functionForm == Form.METHOD;
    }

    public boolean isGetterMethod() {
        return this.functionForm == Form.GETTER;
    }

    public boolean isSetterMethod() {
        return this.functionForm == Form.SETTER;
    }

    public boolean isNormalMethod() {
        return this.functionForm == Form.METHOD;
    }

    public void setFunctionIsGetterMethod() {
        this.functionForm = Form.GETTER;
    }

    public void setFunctionIsSetterMethod() {
        this.functionForm = Form.SETTER;
    }

    public void setFunctionIsNormalMethod() {
        this.functionForm = Form.METHOD;
    }

    public void setMemberExprNode(AstNode node) {
        this.memberExprNode = node;
        if (node != null) {
            node.setParent(this);
        }
    }

    public AstNode getMemberExprNode() {
        return this.memberExprNode;
    }

    @Override // org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        boolean isArrow = this.functionType == 4;
        if (!isMethod()) {
            sb.append(makeIndent(depth));
            if (!isArrow) {
                sb.append("function");
            }
        }
        if (this.functionName != null) {
            sb.append(" ");
            sb.append(this.functionName.toSource(0));
        }
        if (this.params == null) {
            sb.append("() ");
        } else if (isArrow && this.lp == -1) {
            printList(this.params, sb);
            sb.append(" ");
        } else {
            sb.append("(");
            printList(this.params, sb);
            sb.append(") ");
        }
        if (isArrow) {
            sb.append("=> ");
        }
        if (this.isExpressionClosure) {
            AstNode body = getBody();
            if (body.getLastChild() instanceof ReturnStatement) {
                sb.append(((ReturnStatement) body.getLastChild()).getReturnValue().toSource(0));
                if (this.functionType == 1) {
                    sb.append(PolicyIdFilter.OID_SEPARATOR);
                }
            } else {
                sb.append(" ");
                sb.append(body.toSource(0));
            }
        } else {
            sb.append(getBody().toSource(depth).trim());
        }
        if (this.functionType == 1 || isMethod()) {
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.ScriptNode, org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            if (this.functionName != null) {
                this.functionName.visit(v);
            }
            for (AstNode param : getParams()) {
                param.visit(v);
            }
            getBody().visit(v);
            if (!this.isExpressionClosure && this.memberExprNode != null) {
                this.memberExprNode.visit(v);
            }
        }
    }
}
