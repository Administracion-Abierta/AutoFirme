package org.mozilla.javascript.optimizer;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.ast.Jump;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/optimizer/Block.class */
class Block {
    private Block[] itsSuccessors;
    private Block[] itsPredecessors;
    private int itsStartNodeIndex;
    private int itsEndNodeIndex;
    private int itsBlockID;
    private BitSet itsLiveOnEntrySet;
    private BitSet itsLiveOnExitSet;
    private BitSet itsUseBeforeDefSet;
    private BitSet itsNotDefSet;
    static final boolean DEBUG = false;
    private static int debug_blockCount;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/optimizer/Block$FatBlock.class */
    private static class FatBlock {
        private ObjToIntMap successors;
        private ObjToIntMap predecessors;
        Block realBlock;

        private FatBlock() {
            this.successors = new ObjToIntMap();
            this.predecessors = new ObjToIntMap();
        }

        private static Block[] reduceToArray(ObjToIntMap map) {
            Block[] result = null;
            if (!map.isEmpty()) {
                result = new Block[map.size()];
                int i = 0;
                ObjToIntMap.Iterator iter = map.newIterator();
                iter.start();
                while (!iter.done()) {
                    FatBlock fb = (FatBlock) iter.getKey();
                    int i2 = i;
                    i++;
                    result[i2] = fb.realBlock;
                    iter.next();
                }
            }
            return result;
        }

        void addSuccessor(FatBlock b) {
            this.successors.put(b, 0);
        }

        void addPredecessor(FatBlock b) {
            this.predecessors.put(b, 0);
        }

        Block[] getSuccessors() {
            return reduceToArray(this.successors);
        }

        Block[] getPredecessors() {
            return reduceToArray(this.predecessors);
        }
    }

    Block(int startNodeIndex, int endNodeIndex) {
        this.itsStartNodeIndex = startNodeIndex;
        this.itsEndNodeIndex = endNodeIndex;
    }

    static void runFlowAnalyzes(OptFunctionNode fn, Node[] statementNodes) {
        int paramCount = fn.fnode.getParamCount();
        int varCount = fn.fnode.getParamAndVarCount();
        int[] varTypes = new int[varCount];
        for (int i = 0; i != paramCount; i++) {
            varTypes[i] = 3;
        }
        for (int i2 = paramCount; i2 != varCount; i2++) {
            varTypes[i2] = 0;
        }
        Block[] theBlocks = buildBlocks(statementNodes);
        reachingDefDataFlow(fn, statementNodes, theBlocks, varTypes);
        typeFlow(fn, statementNodes, theBlocks, varTypes);
        for (int i3 = paramCount; i3 != varCount; i3++) {
            if (varTypes[i3] == 1) {
                fn.setIsNumberVar(i3);
            }
        }
    }

    private static Block[] buildBlocks(Node[] statementNodes) {
        Map<Node, FatBlock> theTargetBlocks = new HashMap<>();
        ObjArray theBlocks = new ObjArray();
        int beginNodeIndex = 0;
        for (int i = 0; i < statementNodes.length; i++) {
            switch (statementNodes[i].getType()) {
                case 5:
                case 6:
                case 7:
                    FatBlock fb = newFatBlock(beginNodeIndex, i);
                    if (statementNodes[beginNodeIndex].getType() == 132) {
                        theTargetBlocks.put(statementNodes[beginNodeIndex], fb);
                    }
                    theBlocks.add(fb);
                    beginNodeIndex = i + 1;
                    break;
                case 132:
                    if (i != beginNodeIndex) {
                        FatBlock fb2 = newFatBlock(beginNodeIndex, i - 1);
                        if (statementNodes[beginNodeIndex].getType() == 132) {
                            theTargetBlocks.put(statementNodes[beginNodeIndex], fb2);
                        }
                        theBlocks.add(fb2);
                        beginNodeIndex = i;
                    }
                    break;
            }
        }
        if (beginNodeIndex != statementNodes.length) {
            FatBlock fb3 = newFatBlock(beginNodeIndex, statementNodes.length - 1);
            if (statementNodes[beginNodeIndex].getType() == 132) {
                theTargetBlocks.put(statementNodes[beginNodeIndex], fb3);
            }
            theBlocks.add(fb3);
        }
        for (int i2 = 0; i2 < theBlocks.size(); i2++) {
            FatBlock fb4 = (FatBlock) theBlocks.get(i2);
            Node blockEndNode = statementNodes[fb4.realBlock.itsEndNodeIndex];
            int blockEndNodeType = blockEndNode.getType();
            if (blockEndNodeType != 5 && i2 < theBlocks.size() - 1) {
                FatBlock fallThruTarget = (FatBlock) theBlocks.get(i2 + 1);
                fb4.addSuccessor(fallThruTarget);
                fallThruTarget.addPredecessor(fb4);
            }
            if (blockEndNodeType == 7 || blockEndNodeType == 6 || blockEndNodeType == 5) {
                Node target = ((Jump) blockEndNode).target;
                FatBlock branchTargetBlock = theTargetBlocks.get(target);
                target.putProp(6, branchTargetBlock.realBlock);
                fb4.addSuccessor(branchTargetBlock);
                branchTargetBlock.addPredecessor(fb4);
            }
        }
        Block[] result = new Block[theBlocks.size()];
        for (int i3 = 0; i3 < theBlocks.size(); i3++) {
            FatBlock fb5 = (FatBlock) theBlocks.get(i3);
            Block b = fb5.realBlock;
            b.itsSuccessors = fb5.getSuccessors();
            b.itsPredecessors = fb5.getPredecessors();
            b.itsBlockID = i3;
            result[i3] = b;
        }
        return result;
    }

    private static FatBlock newFatBlock(int startNodeIndex, int endNodeIndex) {
        FatBlock fb = new FatBlock();
        fb.realBlock = new Block(startNodeIndex, endNodeIndex);
        return fb;
    }

    private static String toString(Block[] blockList, Node[] statementNodes) {
        return null;
    }

    private static void reachingDefDataFlow(OptFunctionNode fn, Node[] statementNodes, Block[] theBlocks, int[] varTypes) {
        Block[] pred;
        for (Block block : theBlocks) {
            block.initLiveOnEntrySets(fn, statementNodes);
        }
        boolean[] visit = new boolean[theBlocks.length];
        boolean[] doneOnce = new boolean[theBlocks.length];
        int vIndex = theBlocks.length - 1;
        boolean needRescan = false;
        visit[vIndex] = true;
        while (true) {
            if (visit[vIndex] || !doneOnce[vIndex]) {
                doneOnce[vIndex] = true;
                visit[vIndex] = false;
                if (theBlocks[vIndex].doReachedUseDataFlow() && (pred = theBlocks[vIndex].itsPredecessors) != null) {
                    for (Block block2 : pred) {
                        int index = block2.itsBlockID;
                        visit[index] = true;
                        needRescan |= index > vIndex;
                    }
                }
            }
            if (vIndex == 0) {
                if (needRescan) {
                    vIndex = theBlocks.length - 1;
                    needRescan = false;
                } else {
                    theBlocks[0].markAnyTypeVariables(varTypes);
                    return;
                }
            } else {
                vIndex--;
            }
        }
    }

    private static void typeFlow(OptFunctionNode fn, Node[] statementNodes, Block[] theBlocks, int[] varTypes) {
        Block[] succ;
        boolean[] visit = new boolean[theBlocks.length];
        boolean[] doneOnce = new boolean[theBlocks.length];
        int vIndex = 0;
        boolean needRescan = false;
        visit[0] = true;
        while (true) {
            if (visit[vIndex] || !doneOnce[vIndex]) {
                doneOnce[vIndex] = true;
                visit[vIndex] = false;
                if (theBlocks[vIndex].doTypeFlow(fn, statementNodes, varTypes) && (succ = theBlocks[vIndex].itsSuccessors) != null) {
                    for (Block block : succ) {
                        int index = block.itsBlockID;
                        visit[index] = true;
                        needRescan |= index < vIndex;
                    }
                }
            }
            if (vIndex == theBlocks.length - 1) {
                if (needRescan) {
                    vIndex = 0;
                    needRescan = false;
                } else {
                    return;
                }
            } else {
                vIndex++;
            }
        }
    }

    private static boolean assignType(int[] varTypes, int index, int type) {
        int prev = varTypes[index];
        int i = varTypes[index] | type;
        varTypes[index] = i;
        return prev != i;
    }

    private void markAnyTypeVariables(int[] varTypes) {
        for (int i = 0; i != varTypes.length; i++) {
            if (this.itsLiveOnEntrySet.get(i)) {
                assignType(varTypes, i, 3);
            }
        }
    }

    private void lookForVariableAccess(OptFunctionNode fn, Node n) {
        switch (n.getType()) {
            case 55:
                int varIndex = fn.getVarIndex(n);
                if (!this.itsNotDefSet.get(varIndex)) {
                    this.itsUseBeforeDefSet.set(varIndex);
                }
                break;
            case 56:
            case 157:
                Node lhs = n.getFirstChild();
                Node rhs = lhs.getNext();
                lookForVariableAccess(fn, rhs);
                this.itsNotDefSet.set(fn.getVarIndex(n));
                break;
            case 107:
            case 108:
                Node child = n.getFirstChild();
                if (child.getType() == 55) {
                    int varIndex2 = fn.getVarIndex(child);
                    if (!this.itsNotDefSet.get(varIndex2)) {
                        this.itsUseBeforeDefSet.set(varIndex2);
                    }
                    this.itsNotDefSet.set(varIndex2);
                } else {
                    lookForVariableAccess(fn, child);
                }
                break;
            case 138:
                int varIndex3 = fn.fnode.getIndexForNameNode(n);
                if (varIndex3 > -1 && !this.itsNotDefSet.get(varIndex3)) {
                    this.itsUseBeforeDefSet.set(varIndex3);
                    break;
                }
                break;
            default:
                Node firstChild = n.getFirstChild();
                while (true) {
                    Node child2 = firstChild;
                    if (child2 != null) {
                        lookForVariableAccess(fn, child2);
                        firstChild = child2.getNext();
                    }
                    break;
                }
                break;
        }
    }

    private void initLiveOnEntrySets(OptFunctionNode fn, Node[] statementNodes) {
        int listLength = fn.getVarCount();
        this.itsUseBeforeDefSet = new BitSet(listLength);
        this.itsNotDefSet = new BitSet(listLength);
        this.itsLiveOnEntrySet = new BitSet(listLength);
        this.itsLiveOnExitSet = new BitSet(listLength);
        for (int i = this.itsStartNodeIndex; i <= this.itsEndNodeIndex; i++) {
            Node n = statementNodes[i];
            lookForVariableAccess(fn, n);
        }
        this.itsNotDefSet.flip(0, listLength);
    }

    private boolean doReachedUseDataFlow() {
        this.itsLiveOnExitSet.clear();
        if (this.itsSuccessors != null) {
            for (int i = 0; i < this.itsSuccessors.length; i++) {
                this.itsLiveOnExitSet.or(this.itsSuccessors[i].itsLiveOnEntrySet);
            }
        }
        return updateEntrySet(this.itsLiveOnEntrySet, this.itsLiveOnExitSet, this.itsUseBeforeDefSet, this.itsNotDefSet);
    }

    private static boolean updateEntrySet(BitSet entrySet, BitSet exitSet, BitSet useBeforeDef, BitSet notDef) {
        int card = entrySet.cardinality();
        entrySet.or(exitSet);
        entrySet.and(notDef);
        entrySet.or(useBeforeDef);
        return entrySet.cardinality() != card;
    }

    private static int findExpressionType(OptFunctionNode fn, Node n, int[] varTypes) {
        switch (n.getType()) {
            case 8:
            case 35:
            case 37:
            case 56:
            case 90:
            case 157:
                return findExpressionType(fn, n.getLastChild(), varTypes);
            case 9:
            case 10:
            case 11:
            case 18:
            case 19:
            case 20:
            case 22:
            case 23:
            case 24:
            case 25:
            case 27:
            case 28:
            case 29:
            case 107:
            case 108:
                return 1;
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 26:
            case 31:
            case 44:
            case 45:
            case 46:
            case 47:
            case 52:
            case 53:
            case 70:
                return 3;
            case 21:
                Node child = n.getFirstChild();
                int lType = findExpressionType(fn, child, varTypes);
                int rType = findExpressionType(fn, child.getNext(), varTypes);
                return lType | rType;
            case 30:
            case 38:
            case 71:
                return 3;
            case 32:
            case 41:
            case 138:
                return 3;
            case 33:
            case 36:
            case 39:
            case 43:
                return 3;
            case 34:
            case 49:
            case 50:
            case 51:
            case 54:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 68:
            case 69:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 104:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            default:
                return 3;
            case 40:
                return 1;
            case 42:
            case 48:
            case 66:
            case 67:
            case 158:
                return 3;
            case 55:
                return varTypes[fn.getVarIndex(n)];
            case 103:
                Node ifTrue = n.getFirstChild().getNext();
                Node ifFalse = ifTrue.getNext();
                int ifTrueType = findExpressionType(fn, ifTrue, varTypes);
                int ifFalseType = findExpressionType(fn, ifFalse, varTypes);
                return ifTrueType | ifFalseType;
            case 105:
            case 106:
                Node child2 = n.getFirstChild();
                int lType2 = findExpressionType(fn, child2, varTypes);
                int rType2 = findExpressionType(fn, child2.getNext(), varTypes);
                return lType2 | rType2;
            case 127:
                return 3;
        }
    }

    private static boolean findDefPoints(OptFunctionNode fn, Node n, int[] varTypes) {
        boolean result = false;
        Node first = n.getFirstChild();
        Node next = first;
        while (true) {
            Node next2 = next;
            if (next2 == null) {
                break;
            }
            result |= findDefPoints(fn, next2, varTypes);
            next = next2.getNext();
        }
        switch (n.getType()) {
            case 56:
            case 157:
                Node rValue = first.getNext();
                int theType = findExpressionType(fn, rValue, varTypes);
                int i = fn.getVarIndex(n);
                if (n.getType() != 56 || !fn.fnode.getParamAndVarConst()[i]) {
                    result |= assignType(varTypes, i, theType);
                }
                break;
            case 107:
            case 108:
                if (first.getType() == 55) {
                    int i2 = fn.getVarIndex(first);
                    if (!fn.fnode.getParamAndVarConst()[i2]) {
                        result |= assignType(varTypes, i2, 1);
                    }
                }
                break;
        }
        return result;
    }

    private boolean doTypeFlow(OptFunctionNode fn, Node[] statementNodes, int[] varTypes) {
        boolean changed = false;
        for (int i = this.itsStartNodeIndex; i <= this.itsEndNodeIndex; i++) {
            Node n = statementNodes[i];
            if (n != null) {
                changed |= findDefPoints(fn, n, varTypes);
            }
        }
        return changed;
    }

    private void printLiveOnEntrySet(OptFunctionNode fn) {
    }
}
