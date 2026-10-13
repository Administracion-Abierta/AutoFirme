package org.spongycastle.crypto.tls;

import java.math.BigInteger;
import java.util.Vector;
import org.spongycastle.crypto.agreement.srp.SRP6StandardGroups;
import org.spongycastle.crypto.params.SRP6GroupParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/DefaultTlsSRPGroupVerifier.class */
public class DefaultTlsSRPGroupVerifier implements TlsSRPGroupVerifier {
    protected static final Vector DEFAULT_GROUPS = new Vector();
    protected Vector groups;

    static {
        DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_1024);
        DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_1536);
        DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_2048);
        DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_3072);
        DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_4096);
        DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_6144);
        DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_8192);
    }

    public DefaultTlsSRPGroupVerifier() {
        this(DEFAULT_GROUPS);
    }

    public DefaultTlsSRPGroupVerifier(Vector groups) {
        this.groups = groups;
    }

    @Override // org.spongycastle.crypto.tls.TlsSRPGroupVerifier
    public boolean accept(SRP6GroupParameters group) {
        for (int i = 0; i < this.groups.size(); i++) {
            if (areGroupsEqual(group, (SRP6GroupParameters) this.groups.elementAt(i))) {
                return true;
            }
        }
        return false;
    }

    protected boolean areGroupsEqual(SRP6GroupParameters a, SRP6GroupParameters b) {
        return a == b || (areParametersEqual(a.getN(), b.getN()) && areParametersEqual(a.getG(), b.getG()));
    }

    protected boolean areParametersEqual(BigInteger a, BigInteger b) {
        return a == b || a.equals(b);
    }
}
