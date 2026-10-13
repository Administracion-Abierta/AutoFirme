package org.bouncycastle.math.raw;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/raw/Nat512.class */
public abstract class Nat512 {
    public static void mul(int[] iArr, int[] iArr2, int[] iArr3) {
        Nat256.mul(iArr, iArr2, iArr3);
        Nat256.mul(iArr, 8, iArr2, 8, iArr3, 16);
        int iAddToEachOther = Nat256.addToEachOther(iArr3, 8, iArr3, 16);
        int iAddTo = iAddToEachOther + Nat256.addTo(iArr3, 24, iArr3, 16, iAddToEachOther + Nat256.addTo(iArr3, 0, iArr3, 8, 0));
        int[] iArrCreate = Nat256.create();
        int[] iArrCreate2 = Nat256.create();
        boolean z = Nat256.diff(iArr, 8, iArr, 0, iArrCreate, 0) != Nat256.diff(iArr2, 8, iArr2, 0, iArrCreate2, 0);
        int[] iArrCreateExt = Nat256.createExt();
        Nat256.mul(iArrCreate, iArrCreate2, iArrCreateExt);
        Nat.addWordAt(32, iAddTo + (z ? Nat.addTo(16, iArrCreateExt, 0, iArr3, 8) : Nat.subFrom(16, iArrCreateExt, 0, iArr3, 8)), iArr3, 24);
    }

    public static void square(int[] iArr, int[] iArr2) {
        Nat256.square(iArr, iArr2);
        Nat256.square(iArr, 8, iArr2, 16);
        int iAddToEachOther = Nat256.addToEachOther(iArr2, 8, iArr2, 16);
        int iAddTo = iAddToEachOther + Nat256.addTo(iArr2, 24, iArr2, 16, iAddToEachOther + Nat256.addTo(iArr2, 0, iArr2, 8, 0));
        int[] iArrCreate = Nat256.create();
        Nat256.diff(iArr, 8, iArr, 0, iArrCreate, 0);
        int[] iArrCreateExt = Nat256.createExt();
        Nat256.square(iArrCreate, iArrCreateExt);
        Nat.addWordAt(32, iAddTo + Nat.subFrom(16, iArrCreateExt, 0, iArr2, 8), iArr2, 24);
    }
}
