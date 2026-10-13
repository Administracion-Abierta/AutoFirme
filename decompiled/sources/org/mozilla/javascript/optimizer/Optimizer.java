package org.mozilla.javascript.optimizer;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.ast.ScriptNode;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/optimizer/Optimizer.class */
class Optimizer {
    static final int NoType = 0;
    static final int NumberType = 1;
    static final int AnyType = 3;
    private boolean inDirectCallFunction;
    OptFunctionNode theFunction;
    private boolean parameterUsedInNumberContext;

    Optimizer() {
    }

    void optimize(ScriptNode scriptOrFn) {
        int functionCount = scriptOrFn.getFunctionCount();
        for (int i = 0; i != functionCount; i++) {
            OptFunctionNode f = OptFunctionNode.get(scriptOrFn, i);
            optimizeFunction(f);
        }
    }

    private void optimizeFunction(OptFunctionNode theFunction) {
        if (theFunction.fnode.requiresActivation()) {
            return;
        }
        this.inDirectCallFunction = theFunction.isTargetOfDirectCall();
        this.theFunction = theFunction;
        ObjArray statementsArray = new ObjArray();
        buildStatementList_r(theFunction.fnode, statementsArray);
        Node[] theStatementNodes = new Node[statementsArray.size()];
        statementsArray.toArray(theStatementNodes);
        Block.runFlowAnalyzes(theFunction, theStatementNodes);
        if (!theFunction.fnode.requiresActivation()) {
            this.parameterUsedInNumberContext = false;
            for (Node theStatementNode : theStatementNodes) {
                rewriteForNumberVariables(theStatementNode, 1);
            }
            theFunction.setParameterNumberContext(this.parameterUsedInNumberContext);
        }
    }

    private void markDCPNumberContext(Node n) {
        if (this.inDirectCallFunction && n.getType() == 55) {
            int varIndex = this.theFunction.getVarIndex(n);
            if (this.theFunction.isParameter(varIndex)) {
                this.parameterUsedInNumberContext = true;
            }
        }
    }

    private boolean convertParameter(Node n) {
        if (this.inDirectCallFunction && n.getType() == 55) {
            int varIndex = this.theFunction.getVarIndex(n);
            if (this.theFunction.isParameter(varIndex)) {
                n.removeProp(8);
                return true;
            }
            return false;
        }
        return false;
    }

    private int rewriteForNumberVariables(Node n, int desired) {
        switch (n.getType()) {
            case 9:
            case 10:
            case 11:
            case 18:
            case 19:
            case 22:
            case 23:
            case 24:
            case 25:
                Node lChild = n.getFirstChild();
                Node rChild = lChild.getNext();
                int lType = rewriteForNumberVariables(lChild, 1);
                int rType = rewriteForNumberVariables(rChild, 1);
                markDCPNumberContext(lChild);
                markDCPNumberContext(rChild);
                if (lType == 1) {
                    if (rType == 1) {
                        n.putIntProp(8, 0);
                        return 1;
                    }
                    if (!convertParameter(rChild)) {
                        n.removeChild(rChild);
                        n.addChildToBack(new Node(151, rChild));
                        n.putIntProp(8, 0);
                        return 1;
                    }
                    return 1;
                }
                if (rType == 1) {
                    if (!convertParameter(lChild)) {
                        n.removeChild(lChild);
                        n.addChildToFront(new Node(151, lChild));
                        n.putIntProp(8, 0);
                        return 1;
                    }
                    return 1;
                }
                if (!convertParameter(lChild)) {
                    n.removeChild(lChild);
                    n.addChildToFront(new Node(151, lChild));
                }
                if (!convertParameter(rChild)) {
                    n.removeChild(rChild);
                    n.addChildToBack(new Node(151, rChild));
                }
                n.putIntProp(8, 0);
                return 1;
            case 14:
            case 15:
            case 16:
            case 17:
                Node lChild2 = n.getFirstChild();
                Node rChild2 = lChild2.getNext();
                int lType2 = rewriteForNumberVariables(lChild2, 1);
                int rType2 = rewriteForNumberVariables(rChild2, 1);
                markDCPNumberContext(lChild2);
                markDCPNumberContext(rChild2);
                if (convertParameter(lChild2)) {
                    if (!convertParameter(rChild2) && rType2 == 1) {
                        n.putIntProp(8, 2);
                        return 0;
                    }
                    return 0;
                }
                if (convertParameter(rChild2)) {
                    if (lType2 == 1) {
                        n.putIntProp(8, 1);
                        return 0;
                    }
                    return 0;
                }
                if (lType2 == 1) {
                    if (rType2 == 1) {
                        n.putIntProp(8, 0);
                        return 0;
                    }
                    n.putIntProp(8, 1);
                    return 0;
                }
                if (rType2 == 1) {
                    n.putIntProp(8, 2);
                    return 0;
                }
                return 0;
            case 21:
                Node lChild3 = n.getFirstChild();
                Node rChild3 = lChild3.getNext();
                int lType3 = rewriteForNumberVariables(lChild3, 1);
                int rType3 = rewriteForNumberVariables(rChild3, 1);
                if (convertParameter(lChild3)) {
                    if (!convertParameter(rChild3) && rType3 == 1) {
                        n.putIntProp(8, 2);
                        return 0;
                    }
                    return 0;
                }
                if (convertParameter(rChild3)) {
                    if (lType3 == 1) {
                        n.putIntProp(8, 1);
                        return 0;
                    }
                    return 0;
                }
                if (lType3 == 1) {
                    if (rType3 == 1) {
                        n.putIntProp(8, 0);
                        return 1;
                    }
                    n.putIntProp(8, 1);
                    return 0;
                }
                if (rType3 == 1) {
                    n.putIntProp(8, 2);
                    return 0;
                }
                return 0;
            case 36:
                Node arrayBase = n.getFirstChild();
                Node arrayIndex = arrayBase.getNext();
                int baseType = rewriteForNumberVariables(arrayBase, 1);
                if (baseType == 1 && !convertParameter(arrayBase)) {
                    n.removeChild(arrayBase);
                    n.addChildToFront(new Node(150, arrayBase));
                }
                int indexType = rewriteForNumberVariables(arrayIndex, 1);
                if (indexType == 1 && !convertParameter(arrayIndex)) {
                    n.putIntProp(8, 2);
                    return 0;
                }
                return 0;
            case 37:
            case 141:
                Node arrayBase2 = n.getFirstChild();
                Node arrayIndex2 = arrayBase2.getNext();
                Node rValue = arrayIndex2.getNext();
                int baseType2 = rewriteForNumberVariables(arrayBase2, 1);
                if (baseType2 == 1 && !convertParameter(arrayBase2)) {
                    n.removeChild(arrayBase2);
                    n.addChildToFront(new Node(150, arrayBase2));
                }
                int indexType2 = rewriteForNumberVariables(arrayIndex2, 1);
                if (indexType2 == 1 && !convertParameter(arrayIndex2)) {
                    n.putIntProp(8, 1);
                }
                int rValueType = rewriteForNumberVariables(rValue, 1);
                if (rValueType == 1 && !convertParameter(rValue)) {
                    n.removeChild(rValue);
                    n.addChildToBack(new Node(150, rValue));
                    return 0;
                }
                return 0;
            case 38:
                Node child = n.getFirstChild();
                rewriteAsObjectChildren(child, child.getFirstChild());
                Node child2 = child.getNext();
                OptFunctionNode target = (OptFunctionNode) n.getProp(9);
                if (target != null) {
                    while (child2 != null) {
                        if (rewriteForNumberVariables(child2, 1) == 1) {
                            markDCPNumberContext(child2);
                        }
                        child2 = child2.getNext();
                    }
                    return 0;
                }
                rewriteAsObjectChildren(n, child2);
                return 0;
            case 40:
                n.putIntProp(8, 0);
                return 1;
            case 55:
                int varIndex = this.theFunction.getVarIndex(n);
                if (this.inDirectCallFunction && this.theFunction.isParameter(varIndex) && desired == 1) {
                    n.putIntProp(8, 0);
                    return 1;
                }
                if (this.theFunction.isNumberVar(varIndex)) {
                    n.putIntProp(8, 0);
                    return 1;
                }
                return 0;
            case 56:
            case 157:
                Node rChild4 = n.getFirstChild().getNext();
                int rType4 = rewriteForNumberVariables(rChild4, 1);
                int varIndex2 = this.theFunction.getVarIndex(n);
                if (this.inDirectCallFunction && this.theFunction.isParameter(varIndex2)) {
                    if (rType4 == 1) {
                        if (!convertParameter(rChild4)) {
                            n.putIntProp(8, 0);
                            return 1;
                        }
                        markDCPNumberContext(rChild4);
                        return 0;
                    }
                    return rType4;
                }
                if (this.theFunction.isNumberVar(varIndex2)) {
                    if (rType4 != 1) {
                        n.removeChild(rChild4);
                        n.addChildToBack(new Node(151, rChild4));
                    }
                    n.putIntProp(8, 0);
                    markDCPNumberContext(rChild4);
                    return 1;
                }
                if (rType4 == 1 && !convertParameter(rChild4)) {
                    n.removeChild(rChild4);
                    n.addChildToBack(new Node(150, rChild4));
                    return 0;
                }
                return 0;
            case 107:
            case 108:
                Node child3 = n.getFirstChild();
                int type = rewriteForNumberVariables(child3, 1);
                if (child3.getType() == 55) {
                    if (type == 1 && !convertParameter(child3)) {
                        n.putIntProp(8, 0);
                        markDCPNumberContext(child3);
                        return 1;
                    }
                    return 0;
                }
                if (child3.getType() == 36 || child3.getType() == 33) {
                    return type;
                }
                return 0;
            case 134:
                if (rewriteForNumberVariables(n.getFirstChild(), 1) == 1) {
                    n.putIntProp(8, 0);
                    return 0;
                }
                return 0;
            default:
                rewriteAsObjectChildren(n, n.getFirstChild());
                return 0;
        }
    }

    private void rewriteAsObjectChildren(Node n, Node child) {
        while (child != null) {
            Node nextChild = child.getNext();
            int type = rewriteForNumberVariables(child, 0);
            if (type == 1 && !convertParameter(child)) {
                n.removeChild(child);
                Node nuChild = new Node(150, child);
                if (nextChild == null) {
                    n.addChildToBack(nuChild);
                } else {
                    n.addChildBefore(nuChild, nextChild);
                }
            }
            child = nextChild;
        }
    }

    private static void buildStatementList_r(Node node, ObjArray statements) {
        int type = node.getType();
        if (type == 130 || type == 142 || type == 133 || type == 110) {
            Node firstChild = node.getFirstChild();
            while (true) {
                Node child = firstChild;
                if (child != null) {
                    buildStatementList_r(child, statements);
                    firstChild = child.getNext();
                } else {
                    return;
                }
            }
        } else {
            statements.add(node);
        }
    }
}
