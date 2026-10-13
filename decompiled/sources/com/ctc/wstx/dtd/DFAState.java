package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DFAState.class */
public final class DFAState {
    final int mIndex;
    final boolean mAccepting;
    BitSet mTokenSet;
    HashMap<PrefixedName, DFAState> mNext = new HashMap<>();

    public DFAState(int index, BitSet tokenSet) {
        this.mIndex = index;
        this.mAccepting = tokenSet.get(0);
        this.mTokenSet = tokenSet;
    }

    public static DFAState constructDFA(ContentSpec rootSpec) {
        ModelNode modelRoot = rootSpec.rewrite();
        TokenModel eofToken = TokenModel.getNullToken();
        ConcatModel dummyRoot = new ConcatModel(modelRoot, eofToken);
        ArrayList<TokenModel> tokens = new ArrayList<>();
        tokens.add(eofToken);
        dummyRoot.indexTokens(tokens);
        int flen = tokens.size();
        BitSet[] followPos = new BitSet[flen];
        PrefixedName[] tokenNames = new PrefixedName[flen];
        for (int i = 0; i < flen; i++) {
            followPos[i] = new BitSet(flen);
            tokenNames[i] = tokens.get(i).getName();
        }
        dummyRoot.calcFollowPos(followPos);
        BitSet initial = new BitSet(flen);
        dummyRoot.addFirstPos(initial);
        DFAState firstState = new DFAState(0, initial);
        ArrayList<DFAState> stateList = new ArrayList<>();
        stateList.add(firstState);
        HashMap<BitSet, DFAState> stateMap = new HashMap<>();
        stateMap.put(initial, firstState);
        int i2 = 0;
        while (i2 < stateList.size()) {
            int i3 = i2;
            i2++;
            DFAState curr = stateList.get(i3);
            curr.calcNext(tokenNames, followPos, stateList, stateMap);
        }
        return firstState;
    }

    public boolean isAcceptingState() {
        return this.mAccepting;
    }

    public int getIndex() {
        return this.mIndex;
    }

    public DFAState findNext(PrefixedName elemName) {
        return this.mNext.get(elemName);
    }

    public TreeSet<PrefixedName> getNextNames() {
        TreeSet<PrefixedName> names = new TreeSet<>();
        for (PrefixedName n : this.mNext.keySet()) {
            names.add(n);
        }
        return names;
    }

    public void calcNext(PrefixedName[] tokenNames, BitSet[] tokenFPs, List<DFAState> stateList, Map<BitSet, DFAState> stateMap) {
        int first = -1;
        BitSet tokenSet = (BitSet) this.mTokenSet.clone();
        this.mTokenSet = null;
        while (true) {
            int iNextSetBit = tokenSet.nextSetBit(first + 1);
            first = iNextSetBit;
            if (iNextSetBit >= 0) {
                PrefixedName tokenName = tokenNames[first];
                if (tokenName != null) {
                    BitSet nextGroup = (BitSet) tokenFPs[first].clone();
                    int second = first;
                    while (true) {
                        int iNextSetBit2 = tokenSet.nextSetBit(second + 1);
                        second = iNextSetBit2;
                        if (iNextSetBit2 <= 0) {
                            break;
                        } else if (tokenNames[second] == tokenName) {
                            tokenSet.clear(second);
                            nextGroup.or(tokenFPs[second]);
                        }
                    }
                    DFAState next = stateMap.get(nextGroup);
                    if (next == null) {
                        next = new DFAState(stateList.size(), nextGroup);
                        stateList.add(next);
                        stateMap.put(nextGroup, next);
                    }
                    this.mNext.put(tokenName, next);
                }
            } else {
                return;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State #" + this.mIndex + ":\n");
        sb.append("  Accepting: " + this.mAccepting);
        sb.append("\n  Next states:\n");
        for (Map.Entry<PrefixedName, DFAState> en : this.mNext.entrySet()) {
            sb.append(en.getKey());
            sb.append(" -> ");
            DFAState next = en.getValue();
            sb.append(next.getIndex());
            sb.append("\n");
        }
        return sb.toString();
    }
}
