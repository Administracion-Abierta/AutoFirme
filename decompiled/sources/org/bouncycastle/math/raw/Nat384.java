package org.bouncycastle.math.raw;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/raw/Nat384.class */
public abstract class Nat384 {
    public static void mul(int[] iArr, int[] iArr2, int[] iArr3) {
        Nat192.mul(iArr, iArr2, iArr3);
        Nat192.mul(iArr, 6, iArr2, 6, iArr3, 12);
        int iAddToEachOther = Nat192.addToEachOther(iArr3, 6, iArr3, 12);
        int iAddTo = iAddToEachOther + Nat192.addTo(iArr3, 18, iArr3, 12, iAddToEachOther + Nat192.addTo(iArr3, 0, iArr3, 6, 0));
        int[] iArrCreate = Nat192.create();
        int[] iArrCreate2 = Nat192.create();
        boolean z = Nat192.diff(iArr, 6, iArr, 0, iArrCreate, 0) != Nat192.diff(iArr2, 6, iArr2, 0, iArrCreate2, 0);
        int[] iArrCreateExt = Nat192.createExt();
        Nat192.mul(iArrCreate, iArrCreate2, iArrCreateExt);
        Nat.addWordAt(24, iAddTo + (z ? Nat.addTo(12, iArrCreateExt, 0, iArr3, 6) : Nat.subFrom(12, iArrCreateExt, 0, iArr3, 6)), iArr3, 18);
    }

    public static void square(int[] iArr, int[] iArr2) {
        Nat192.square(iArr, iArr2);
        Nat192.square(iArr, 6, iArr2, 12);
        int iAddToEachOther = Nat192.addToEachOther(iArr2, 6, iArr2, 12);
        int iAddTo = iAddToEachOther + Nat192.addTo(iArr2, 18, iArr2, 12, iAddToEachOther + Nat192.addTo(iArr2, 0, iArr2, 6, 0));
        int[] iArrCreate = Nat192.create();
        Nat192.diff(iArr, 6, iArr, 0, iArrCreate, 0);
        int[] iArrCreateExt = Nat192.createExt();
        Nat192.square(iArrCreate, iArrCreateExt);
        Nat.addWordAt(24, iAddTo + Nat.subFrom(12, iArrCreateExt, 0, iArr2, 6), iArr2, 18);
    }
}
