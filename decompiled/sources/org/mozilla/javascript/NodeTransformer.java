package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NodeTransformer.class */
public class NodeTransformer {
    private ObjArray loops;
    private ObjArray loopEnds;
    private boolean hasFinally;

    public final void transform(ScriptNode tree, CompilerEnvirons env) {
        transform(tree, false, env);
    }

    public final void transform(ScriptNode tree, boolean inStrictMode, CompilerEnvirons env) {
        boolean useStrictMode = inStrictMode;
        if (env.getLanguageVersion() >= 200 && tree.isInStrictMode()) {
            useStrictMode = true;
        }
        transformCompilationUnit(tree, useStrictMode);
        for (int i = 0; i != tree.getFunctionCount(); i++) {
            FunctionNode fn = tree.getFunctionNode(i);
            transform(fn, useStrictMode, env);
        }
    }

    private void transformCompilationUnit(ScriptNode tree, boolean inStrictMode) {
        this.loops = new ObjArray();
        this.loopEnds = new ObjArray();
        this.hasFinally = false;
        boolean createScopeObjects = tree.getType() != 110 || ((FunctionNode) tree).requiresActivation();
        tree.flattenSymbolTable(!createScopeObjects);
        transformCompilationUnit_r(tree, tree, tree, createScopeObjects, inStrictMode);
    }

    /* JADX WARN: Code duplicated, block: B:120:0x04b5  */
    /* JADX WARN: Code duplicated, block: B:122:0x04ca  */
    /* JADX WARN: Code duplicated, block: B:127:0x04f7  */
    /* JADX WARN: Code duplicated, block: B:128:0x04fd  */
    /* JADX WARN: Code duplicated, block: B:168:0x0607  */
    /* JADX WARN: Code duplicated, block: B:170:0x060e  */
    /* JADX WARN: Code duplicated, block: B:171:0x0615  */
    /* JADX WARN: Code duplicated, block: B:173:0x0626  */
    /* JADX WARN: Code duplicated, block: B:181:0x063f  */
    /* JADX WARN: Code duplicated, block: B:183:0x0653  */
    /* JADX WARN: Code duplicated, block: B:185:0x0661  */
    /* JADX WARN: Code duplicated, block: B:186:0x066c  */
    /* JADX WARN: Code duplicated, block: B:190:0x067a  */
    /* JADX WARN: Code duplicated, block: B:201:0x06da  */
    /* JADX WARN: Code duplicated, block: B:202:0x06e2  */
    /* JADX WARN: Code duplicated, block: B:228:0x0516 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:229:0x0630 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:248:0x050b A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:249:0x04d5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:251:0x04b0 A[SYNTHETIC] */
    private void transformCompilationUnit_r(ScriptNode tree, Node parent, Scope scope, boolean createScopeObjects, boolean inStrictMode) {
        Node nameSource;
        Scope defining;
        Node result;
        Node cursor;
        Node n;
        int i;
        Scope scope2;
        Node unwind;
        Node node = null;
        while (true) {
            Node previous = null;
            if (node == null) {
                node = parent.getFirstChild();
            } else {
                previous = node;
                node = node.getNext();
            }
            if (node != null) {
                int type = node.getType();
                if (createScopeObjects && ((type == 130 || type == 133 || type == 158) && (node instanceof Scope))) {
                    Scope newScope = (Scope) node;
                    if (newScope.getSymbolTable() != null) {
                        Node let = new Node(type == 158 ? 159 : 154);
                        Node innerLet = new Node(154);
                        let.addChildToBack(innerLet);
                        for (String name : newScope.getSymbolTable().keySet()) {
                            innerLet.addChildToBack(Node.newString(39, name));
                        }
                        newScope.setSymbolTable(null);
                        Node oldNode = node;
                        node = replaceCurrent(parent, previous, node, let);
                        type = node.getType();
                        let.addChildToBack(oldNode);
                    }
                }
                switch (type) {
                    case 3:
                    case 132:
                        if (!this.loopEnds.isEmpty() && this.loopEnds.peek() == node) {
                            this.loopEnds.pop();
                            this.loops.pop();
                        }
                        Node node2 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node2, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 4:
                        boolean isGenerator = tree.getType() == 110 && ((FunctionNode) tree).isGenerator();
                        if (isGenerator) {
                            node.putIntProp(20, 1);
                        }
                        if (this.hasFinally) {
                            Node unwindBlock = null;
                            for (int i2 = this.loops.size() - 1; i2 >= 0; i2--) {
                                Node n2 = (Node) this.loops.get(i2);
                                int elemtype = n2.getType();
                                if (elemtype == 82 || elemtype == 124) {
                                    if (elemtype == 82) {
                                        Jump jsrnode = new Jump(136);
                                        Node jsrtarget = ((Jump) n2).getFinally();
                                        jsrnode.target = jsrtarget;
                                        unwind = jsrnode;
                                    } else {
                                        unwind = new Node(3);
                                    }
                                    if (unwindBlock == null) {
                                        unwindBlock = new Node(130, node.getLineno());
                                    }
                                    unwindBlock.addChildToBack(unwind);
                                }
                            }
                            if (unwindBlock != null) {
                                Node returnNode = node;
                                Node returnExpr = returnNode.getFirstChild();
                                node = replaceCurrent(parent, previous, node, unwindBlock);
                                if (returnExpr == null || isGenerator) {
                                    unwindBlock.addChildToBack(returnNode);
                                } else {
                                    Node store = new Node(135, returnExpr);
                                    unwindBlock.addChildToFront(store);
                                    unwindBlock.addChildToBack(new Node(65));
                                    transformCompilationUnit_r(tree, store, scope, createScopeObjects, inStrictMode);
                                }
                            }
                            break;
                        }
                        Node node3 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node3, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 7:
                    case 32:
                        Node child = node.getFirstChild();
                        if (type == 7) {
                            while (child.getType() == 26) {
                                child = child.getFirstChild();
                            }
                            if (child.getType() == 12 || child.getType() == 13) {
                                Node first = child.getFirstChild();
                                Node last = child.getLastChild();
                                if (first.getType() == 39 && first.getString().equals("undefined")) {
                                    child = last;
                                } else if (last.getType() == 39 && last.getString().equals("undefined")) {
                                    child = first;
                                }
                            }
                        }
                        if (child.getType() == 33) {
                            child.setType(34);
                        }
                        Node node4 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node4, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 8:
                        if (inStrictMode) {
                            node.setType(74);
                        }
                        if (!createScopeObjects) {
                            if (type == 39) {
                                nameSource = node;
                            } else {
                                nameSource = node.getFirstChild();
                                if (nameSource.getType() != 49) {
                                    if (type != 31) {
                                        throw Kit.codeBug();
                                    }
                                }
                            }
                            if (nameSource.getScope() == null) {
                                String name2 = nameSource.getString();
                                defining = scope.getDefiningScope(name2);
                                if (defining != null) {
                                    nameSource.setScope(defining);
                                    if (type == 39) {
                                        node.setType(55);
                                    } else if (type != 8 || type == 74) {
                                        node.setType(56);
                                        nameSource.setType(41);
                                    } else if (type == 156) {
                                        node.setType(157);
                                        nameSource.setType(41);
                                    } else if (type == 31) {
                                        node = replaceCurrent(parent, previous, node, new Node(44));
                                    } else {
                                        throw Kit.codeBug();
                                    }
                                }
                            }
                        }
                        Node node5 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node5, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 30:
                        visitNew(node, tree);
                        Node node6 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node6, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 31:
                    case 39:
                    case 156:
                        if (!createScopeObjects) {
                            if (type == 39) {
                                nameSource = node;
                            } else {
                                nameSource = node.getFirstChild();
                                if (nameSource.getType() != 49) {
                                    if (type != 31) {
                                        throw Kit.codeBug();
                                    }
                                }
                            }
                            if (nameSource.getScope() == null) {
                                String name3 = nameSource.getString();
                                defining = scope.getDefiningScope(name3);
                                if (defining != null) {
                                    nameSource.setScope(defining);
                                    if (type == 39) {
                                        node.setType(55);
                                    } else if (type != 8) {
                                        node.setType(56);
                                        nameSource.setType(41);
                                    } else {
                                        node.setType(56);
                                        nameSource.setType(41);
                                    }
                                }
                            }
                        }
                        Node node7 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node7, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 38:
                        visitCall(node, tree);
                        Node node8 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node8, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 73:
                    case 166:
                        ((FunctionNode) tree).addResumptionPoint(node);
                        Node node9 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node9, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 82:
                        Node finallytarget = ((Jump) node).getFinally();
                        if (finallytarget != null) {
                            this.hasFinally = true;
                            this.loops.push(node);
                            this.loopEnds.push(finallytarget);
                        }
                        Node node10 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node10, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 115:
                    case 131:
                    case 133:
                        this.loops.push(node);
                        this.loopEnds.push(((Jump) node).target);
                        Node node11 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node11, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 121:
                    case 122:
                        Jump jump = (Jump) node;
                        Jump jumpStatement = jump.getJumpStatement();
                        if (jumpStatement == null) {
                            Kit.codeBug();
                        }
                        int i3 = this.loops.size();
                        while (true) {
                            if (i3 == 0) {
                                throw Kit.codeBug();
                            }
                            i3--;
                            Node n3 = (Node) this.loops.get(i3);
                            if (n3 != jumpStatement) {
                                int elemtype2 = n3.getType();
                                if (elemtype2 == 124) {
                                    previous = addBeforeCurrent(parent, previous, node, new Node(3));
                                } else if (elemtype2 == 82) {
                                    Jump tryNode = (Jump) n3;
                                    Jump jsrFinally = new Jump(136);
                                    jsrFinally.target = tryNode.getFinally();
                                    previous = addBeforeCurrent(parent, previous, node, jsrFinally);
                                }
                            } else {
                                if (type == 121) {
                                    jump.target = jumpStatement.target;
                                } else {
                                    jump.target = jumpStatement.getContinue();
                                }
                                jump.setType(5);
                            }
                        }
                        Node node12 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node12, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 123:
                    case 155:
                        result = new Node(130);
                        cursor = node.getFirstChild();
                        while (cursor != null) {
                            n = cursor;
                            cursor = cursor.getNext();
                            if (n.getType() == 39) {
                                if (!n.hasChildren()) {
                                    Node init = n.getFirstChild();
                                    n.removeChild(init);
                                    n.setType(49);
                                    if (type == 155) {
                                        i = 156;
                                    } else {
                                        i = 8;
                                    }
                                    n = new Node(i, n, init);
                                }
                            } else if (n.getType() != 159) {
                                throw Kit.codeBug();
                            }
                            Node pop = new Node(134, n, node.getLineno());
                            result.addChildToBack(pop);
                        }
                        node = replaceCurrent(parent, previous, node, result);
                        Node node13 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node13, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 124:
                        this.loops.push(node);
                        Node leave = node.getNext();
                        if (leave.getType() != 3) {
                            Kit.codeBug();
                        }
                        this.loopEnds.push(leave);
                        Node node14 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node14, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 138:
                        Scope defining2 = scope.getDefiningScope(node.getString());
                        if (defining2 != null) {
                            node.setScope(defining2);
                        }
                        Node node15 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node15, scope2, createScopeObjects, inStrictMode);
                        break;
                    case 154:
                    case 159:
                        if (node.getFirstChild().getType() == 154) {
                            boolean createWith = tree.getType() != 110 || ((FunctionNode) tree).requiresActivation();
                            node = visitLet(createWith, parent, previous, node);
                        } else {
                            result = new Node(130);
                            cursor = node.getFirstChild();
                            while (cursor != null) {
                                n = cursor;
                                cursor = cursor.getNext();
                                if (n.getType() == 39) {
                                    if (!n.hasChildren()) {
                                        Node init2 = n.getFirstChild();
                                        n.removeChild(init2);
                                        n.setType(49);
                                        if (type == 155) {
                                            i = 156;
                                        } else {
                                            i = 8;
                                        }
                                        n = new Node(i, n, init2);
                                    }
                                } else if (n.getType() != 159) {
                                    throw Kit.codeBug();
                                }
                                Node pop2 = new Node(134, n, node.getLineno());
                                result.addChildToBack(pop2);
                            }
                            node = replaceCurrent(parent, previous, node, result);
                        }
                        Node node16 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node16, scope2, createScopeObjects, inStrictMode);
                        break;
                    default:
                        Node node17 = node;
                        if (node instanceof Scope) {
                            scope2 = (Scope) node;
                        } else {
                            scope2 = scope;
                        }
                        transformCompilationUnit_r(tree, node17, scope2, createScopeObjects, inStrictMode);
                        break;
                }
            } else {
                return;
            }
        }
    }

    protected void visitNew(Node node, ScriptNode tree) {
    }

    protected void visitCall(Node node, ScriptNode tree) {
    }

    protected Node visitLet(boolean createWith, Node parent, Node previous, Node scopeNode) {
        Node result;
        Node vars = scopeNode.getFirstChild();
        Node body = vars.getNext();
        scopeNode.removeChild(vars);
        scopeNode.removeChild(body);
        boolean isExpression = scopeNode.getType() == 159;
        if (createWith) {
            result = replaceCurrent(parent, previous, scopeNode, new Node(isExpression ? 160 : 130));
            ArrayList<Object> list = new ArrayList<>();
            Node objectLiteral = new Node(67);
            Node firstChild = vars.getFirstChild();
            while (true) {
                Node v = firstChild;
                if (v != null) {
                    Node current = v;
                    if (current.getType() == 159) {
                        List<?> destructuringNames = (List) current.getProp(22);
                        Node c = current.getFirstChild();
                        if (c.getType() != 154) {
                            throw Kit.codeBug();
                        }
                        if (isExpression) {
                            body = new Node(90, c.getNext(), body);
                        } else {
                            body = new Node(130, new Node(134, c.getNext()), body);
                        }
                        if (destructuringNames != null) {
                            list.addAll(destructuringNames);
                            for (int i = 0; i < destructuringNames.size(); i++) {
                                objectLiteral.addChildToBack(new Node(127, Node.newNumber(0.0d)));
                            }
                        }
                        current = c.getFirstChild();
                    }
                    if (current.getType() != 39) {
                        throw Kit.codeBug();
                    }
                    list.add(ScriptRuntime.getIndexObject(current.getString()));
                    Node init = current.getFirstChild();
                    if (init == null) {
                        init = new Node(127, Node.newNumber(0.0d));
                    }
                    objectLiteral.addChildToBack(init);
                    firstChild = v.getNext();
                } else {
                    objectLiteral.putProp(12, list.toArray());
                    result.addChildToBack(new Node(2, objectLiteral));
                    result.addChildToBack(new Node(124, body));
                    result.addChildToBack(new Node(3));
                    break;
                }
            }
        } else {
            result = replaceCurrent(parent, previous, scopeNode, new Node(isExpression ? 90 : 130));
            Node newVars = new Node(90);
            Node firstChild2 = vars.getFirstChild();
            while (true) {
                Node v2 = firstChild2;
                if (v2 != null) {
                    Node current2 = v2;
                    if (current2.getType() == 159) {
                        Node c2 = current2.getFirstChild();
                        if (c2.getType() != 154) {
                            throw Kit.codeBug();
                        }
                        if (isExpression) {
                            body = new Node(90, c2.getNext(), body);
                        } else {
                            body = new Node(130, new Node(134, c2.getNext()), body);
                        }
                        Scope.joinScopes((Scope) current2, (Scope) scopeNode);
                        current2 = c2.getFirstChild();
                    }
                    if (current2.getType() != 39) {
                        throw Kit.codeBug();
                    }
                    Node stringNode = Node.newString(current2.getString());
                    stringNode.setScope((Scope) scopeNode);
                    Node init2 = current2.getFirstChild();
                    if (init2 == null) {
                        init2 = new Node(127, Node.newNumber(0.0d));
                    }
                    newVars.addChildToBack(new Node(56, stringNode, init2));
                    firstChild2 = v2.getNext();
                } else {
                    if (isExpression) {
                        result.addChildToBack(newVars);
                        scopeNode.setType(90);
                        result.addChildToBack(scopeNode);
                        scopeNode.addChildToBack(body);
                        if (!(body instanceof Scope)) {
                            break;
                        }
                        Scope scopeParent = ((Scope) body).getParentScope();
                        ((Scope) body).setParentScope((Scope) scopeNode);
                        ((Scope) scopeNode).setParentScope(scopeParent);
                        break;
                    }
                    result.addChildToBack(new Node(134, newVars));
                    scopeNode.setType(130);
                    result.addChildToBack(scopeNode);
                    scopeNode.addChildrenToBack(body);
                    if (!(body instanceof Scope)) {
                        break;
                    }
                    Scope scopeParent2 = ((Scope) body).getParentScope();
                    ((Scope) body).setParentScope((Scope) scopeNode);
                    ((Scope) scopeNode).setParentScope(scopeParent2);
                    break;
                }
            }
        }
        return result;
    }

    private static Node addBeforeCurrent(Node parent, Node previous, Node current, Node toAdd) {
        if (previous == null) {
            if (current != parent.getFirstChild()) {
                Kit.codeBug();
            }
            parent.addChildToFront(toAdd);
        } else {
            if (current != previous.getNext()) {
                Kit.codeBug();
            }
            parent.addChildAfter(toAdd, previous);
        }
        return toAdd;
    }

    private static Node replaceCurrent(Node parent, Node previous, Node current, Node replacement) {
        if (previous == null) {
            if (current != parent.getFirstChild()) {
                Kit.codeBug();
            }
            parent.replaceChild(current, replacement);
        } else if (previous.next == current) {
            parent.replaceChildAfter(previous, replacement);
        } else {
            parent.replaceChild(current, replacement);
        }
        return replacement;
    }
}
