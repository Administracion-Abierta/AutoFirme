package org.spongycastle.crypto.tls;

import java.math.BigInteger;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.agreement.srp.SRP6VerifierGenerator;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.SRP6GroupParameters;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/SimulatedTlsSRPIdentityManager.class */
public class SimulatedTlsSRPIdentityManager implements TlsSRPIdentityManager {
    private static final byte[] PREFIX_PASSWORD = Strings.toByteArray("password");
    private static final byte[] PREFIX_SALT = Strings.toByteArray("salt");
    protected SRP6GroupParameters group;
    protected SRP6VerifierGenerator verifierGenerator;
    protected Mac mac;

    public static SimulatedTlsSRPIdentityManager getRFC5054Default(SRP6GroupParameters group, byte[] seedKey) {
        SRP6VerifierGenerator verifierGenerator = new SRP6VerifierGenerator();
        verifierGenerator.init(group, TlsUtils.createHash((short) 2));
        HMac mac = new HMac(TlsUtils.createHash((short) 2));
        mac.init(new KeyParameter(seedKey));
        return new SimulatedTlsSRPIdentityManager(group, verifierGenerator, mac);
    }

    public SimulatedTlsSRPIdentityManager(SRP6GroupParameters group, SRP6VerifierGenerator verifierGenerator, Mac mac) {
        this.group = group;
        this.verifierGenerator = verifierGenerator;
        this.mac = mac;
    }

    @Override // org.spongycastle.crypto.tls.TlsSRPIdentityManager
    public TlsSRPLoginParameters getLoginParameters(byte[] identity) {
        this.mac.update(PREFIX_SALT, 0, PREFIX_SALT.length);
        this.mac.update(identity, 0, identity.length);
        byte[] salt = new byte[this.mac.getMacSize()];
        this.mac.doFinal(salt, 0);
        this.mac.update(PREFIX_PASSWORD, 0, PREFIX_PASSWORD.length);
        this.mac.update(identity, 0, identity.length);
        byte[] password = new byte[this.mac.getMacSize()];
        this.mac.doFinal(password, 0);
        BigInteger verifier = this.verifierGenerator.generateVerifier(salt, identity, password);
        return new TlsSRPLoginParameters(this.group, verifier, salt);
    }
}
