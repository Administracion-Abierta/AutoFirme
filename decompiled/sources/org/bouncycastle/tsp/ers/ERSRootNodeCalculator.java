package org.bouncycastle.tsp.ers;

import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/ERSRootNodeCalculator.class */
public interface ERSRootNodeCalculator {
    byte[] computeRootHash(DigestCalculator digestCalculator, PartialHashtree[] partialHashtreeArr);

    PartialHashtree[] computePathToRoot(DigestCalculator digestCalculator, PartialHashtree partialHashtree, int i);

    byte[] recoverRootHash(DigestCalculator digestCalculator, PartialHashtree[] partialHashtreeArr);
}
