package org.mozilla.javascript.optimizer;

import java.util.Map;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.NodeTransformer;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.ast.ScriptNode;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/optimizer/OptTransformer.class */
class OptTransformer extends NodeTransformer {
    private Map<String, OptFunctionNode> possibleDirectCalls;
    private ObjArray directCallTargets;

    OptTransformer(Map<String, OptFunctionNode> possibleDirectCalls, ObjArray directCallTargets) {
        this.possibleDirectCalls = possibleDirectCalls;
        this.directCallTargets = directCallTargets;
    }

    @Override // org.mozilla.javascript.NodeTransformer
    protected void visitNew(Node node, ScriptNode tree) {
        detectDirectCall(node, tree);
        super.visitNew(node, tree);
    }

    @Override // org.mozilla.javascript.NodeTransformer
    protected void visitCall(Node node, ScriptNode tree) {
        detectDirectCall(node, tree);
        super.visitCall(node, tree);
    }

    private void detectDirectCall(Node node, ScriptNode tree) {
        OptFunctionNode ofn;
        if (tree.getType() == 110) {
            Node left = node.getFirstChild();
            int argCount = 0;
            Node arg = left.getNext();
            while (arg != null) {
                arg = arg.getNext();
                argCount++;
            }
            if (argCount == 0) {
                OptFunctionNode.get(tree).itsContainsCalls0 = true;
            }
            if (this.possibleDirectCalls != null) {
                String targetName = null;
                if (left.getType() == 39) {
                    targetName = left.getString();
                } else if (left.getType() == 33) {
                    targetName = left.getFirstChild().getNext().getString();
                } else if (left.getType() == 34) {
                    throw Kit.codeBug();
                }
                if (targetName != null && (ofn = this.possibleDirectCalls.get(targetName)) != null && argCount == ofn.fnode.getParamCount() && !ofn.fnode.requiresActivation() && argCount <= 32) {
                    node.putProp(9, ofn);
                    if (!ofn.isTargetOfDirectCall()) {
                        int index = this.directCallTargets.size();
                        this.directCallTargets.add(ofn);
                        ofn.setDirectTargetIndex(index);
                    }
                }
            }
        }
    }
}
