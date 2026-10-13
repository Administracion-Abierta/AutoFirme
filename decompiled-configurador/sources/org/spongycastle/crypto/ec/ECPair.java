package org.spongycastle.crypto.ec;

import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/ec/ECPair.class */
public class ECPair {
    private final ECPoint x;
    private final ECPoint y;

    public ECPair(ECPoint x, ECPoint y) {
        this.x = x;
        this.y = y;
    }

    public ECPoint getX() {
        return this.x;
    }

    public ECPoint getY() {
        return this.y;
    }

    public boolean equals(ECPair other) {
        return other.getX().equals(getX()) && other.getY().equals(getY());
    }

    public boolean equals(Object other) {
        if (other instanceof ECPair) {
            return equals((ECPair) other);
        }
        return false;
    }

    public int hashCode() {
        return this.x.hashCode() + (37 * this.y.hashCode());
    }
}
