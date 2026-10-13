package org.spongycastle.pqc.crypto.gmss;

import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/gmss/GMSSParameters.class */
public class GMSSParameters {
    private int numOfLayers;
    private int[] heightOfTrees;
    private int[] winternitzParameter;
    private int[] K;

    public GMSSParameters(int layers, int[] heightOfTrees, int[] winternitzParameter, int[] K) throws IllegalArgumentException {
        init(layers, heightOfTrees, winternitzParameter, K);
    }

    private void init(int layers, int[] heightOfTrees, int[] winternitzParameter, int[] K) throws IllegalArgumentException {
        boolean valid = true;
        String errMsg = "";
        this.numOfLayers = layers;
        if (this.numOfLayers != winternitzParameter.length || this.numOfLayers != heightOfTrees.length || this.numOfLayers != K.length) {
            valid = false;
            errMsg = "Unexpected parameterset format";
        }
        for (int i = 0; i < this.numOfLayers; i++) {
            if (K[i] < 2 || (heightOfTrees[i] - K[i]) % 2 != 0) {
                valid = false;
                errMsg = "Wrong parameter K (K >= 2 and H-K even required)!";
            }
            if (heightOfTrees[i] < 4 || winternitzParameter[i] < 2) {
                valid = false;
                errMsg = "Wrong parameter H or w (H > 3 and w > 1 required)!";
            }
        }
        if (valid) {
            this.heightOfTrees = Arrays.clone(heightOfTrees);
            this.winternitzParameter = Arrays.clone(winternitzParameter);
            this.K = Arrays.clone(K);
            return;
        }
        throw new IllegalArgumentException(errMsg);
    }

    public GMSSParameters(int keySize) throws IllegalArgumentException {
        if (keySize <= 10) {
            int[] defh = {10};
            int[] defw = {3};
            int[] defk = {2};
            init(defh.length, defh, defw, defk);
            return;
        }
        if (keySize <= 20) {
            int[] defh2 = {10, 10};
            int[] defw2 = {5, 4};
            int[] defk2 = {2, 2};
            init(defh2.length, defh2, defw2, defk2);
            return;
        }
        int[] defh3 = {10, 10, 10, 10};
        int[] defw3 = {9, 9, 9, 3};
        int[] defk3 = {2, 2, 2, 2};
        init(defh3.length, defh3, defw3, defk3);
    }

    public int getNumOfLayers() {
        return this.numOfLayers;
    }

    public int[] getHeightOfTrees() {
        return Arrays.clone(this.heightOfTrees);
    }

    public int[] getWinternitzParameter() {
        return Arrays.clone(this.winternitzParameter);
    }

    public int[] getK() {
        return Arrays.clone(this.K);
    }
}
