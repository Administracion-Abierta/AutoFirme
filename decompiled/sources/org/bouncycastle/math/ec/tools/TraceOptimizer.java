package org.bouncycastle.math.ec.tools;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/tools/TraceOptimizer.class */
public class TraceOptimizer {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final SecureRandom R = new SecureRandom();

    public static void main(String[] strArr) {
        TreeSet<String> treeSet = new TreeSet(enumToList(ECNamedCurveTable.getNames()));
        treeSet.addAll(enumToList(CustomNamedCurves.getNames()));
        for (String str : treeSet) {
            X9ECParametersHolder byNameLazy = CustomNamedCurves.getByNameLazy(str);
            if (byNameLazy == null) {
                byNameLazy = ECNamedCurveTable.getByNameLazy(str);
            }
            if (byNameLazy != null) {
                ECCurve curve = byNameLazy.getCurve();
                if (ECAlgorithms.isF2mCurve(curve)) {
                    System.out.print(str + ":");
                    implPrintNonZeroTraceBits(curve);
                }
            }
        }
    }

    public static void printNonZeroTraceBits(ECCurve eCCurve) {
        if (!ECAlgorithms.isF2mCurve(eCCurve)) {
            throw new IllegalArgumentException("Trace only defined over characteristic-2 fields");
        }
        implPrintNonZeroTraceBits(eCCurve);
    }

    public static void implPrintNonZeroTraceBits(ECCurve eCCurve) {
        int fieldSize = eCCurve.getFieldSize();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < fieldSize; i++) {
            if (0 != (i & 1) || 0 == i) {
                if (calculateTrace(eCCurve.fromBigInteger(ONE.shiftLeft(i))) != 0) {
                    arrayList.add(Integers.valueOf(i));
                    System.out.print(" " + i);
                }
            } else if (arrayList.contains(Integers.valueOf(i >>> 1))) {
                arrayList.add(Integers.valueOf(i));
                System.out.print(" " + i);
            }
        }
        System.out.println();
        for (int i2 = 0; i2 < 1000; i2++) {
            BigInteger bigInteger = new BigInteger(fieldSize, R);
            int iCalculateTrace = calculateTrace(eCCurve.fromBigInteger(bigInteger));
            int i3 = 0;
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                if (bigInteger.testBit(((Integer) arrayList.get(i4)).intValue())) {
                    i3 ^= 1;
                }
            }
            if (iCalculateTrace != i3) {
                throw new IllegalStateException("Optimized-trace sanity check failed");
            }
        }
    }

    private static int calculateTrace(ECFieldElement eCFieldElement) {
        int fieldSize = eCFieldElement.getFieldSize();
        int iNumberOfLeadingZeros = 31 - Integers.numberOfLeadingZeros(fieldSize);
        int i = 1;
        ECFieldElement eCFieldElementAdd = eCFieldElement;
        while (iNumberOfLeadingZeros > 0) {
            eCFieldElementAdd = eCFieldElementAdd.squarePow(i).add(eCFieldElementAdd);
            iNumberOfLeadingZeros--;
            i = fieldSize >>> iNumberOfLeadingZeros;
            if (0 != (i & 1)) {
                eCFieldElementAdd = eCFieldElementAdd.square().add(eCFieldElement);
            }
        }
        if (eCFieldElementAdd.isZero()) {
            return 0;
        }
        if (eCFieldElementAdd.isOne()) {
            return 1;
        }
        throw new IllegalStateException("Internal error in trace calculation");
    }

    private static List enumToList(Enumeration enumeration) {
        ArrayList arrayList = new ArrayList();
        while (enumeration.hasMoreElements()) {
            arrayList.add(enumeration.nextElement());
        }
        return arrayList;
    }
}
