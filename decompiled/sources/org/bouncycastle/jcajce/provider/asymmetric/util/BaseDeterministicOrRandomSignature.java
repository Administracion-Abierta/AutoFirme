package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.spec.ContextParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/util/BaseDeterministicOrRandomSignature.class */
public abstract class BaseDeterministicOrRandomSignature extends Signature {
    private final JcaJceHelper helper;
    private final AlgorithmParameterSpec originalSpec;
    protected AlgorithmParameters engineParams;
    protected ContextParameterSpec paramSpec;
    protected AsymmetricKeyParameter keyParams;
    protected boolean isInitState;

    protected BaseDeterministicOrRandomSignature(String str) {
        super(str);
        this.helper = new BCJcaJceHelper();
        this.isInitState = true;
        this.originalSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
    }

    @Override // java.security.SignatureSpi
    protected final void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        verifyInit(publicKey);
        this.paramSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
        this.isInitState = true;
        reInit();
    }

    protected abstract void verifyInit(PublicKey publicKey) throws InvalidKeyException;

    @Override // java.security.SignatureSpi
    protected final void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        signInit(privateKey, null);
        this.paramSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
        this.isInitState = true;
        reInit();
    }

    @Override // java.security.SignatureSpi
    protected final void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        signInit(privateKey, secureRandom);
        this.paramSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
        this.isInitState = true;
        reInit();
    }

    protected abstract void signInit(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException;

    @Override // java.security.SignatureSpi
    protected final void engineUpdate(byte b) throws SignatureException {
        this.isInitState = false;
        updateEngine(b);
    }

    protected abstract void updateEngine(byte b) throws SignatureException;

    @Override // java.security.SignatureSpi
    protected final void engineUpdate(byte[] bArr, int i, int i2) throws SignatureException {
        this.isInitState = false;
        updateEngine(bArr, i, i2);
    }

    protected abstract void updateEngine(byte[] bArr, int i, int i2) throws SignatureException;

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec == null) {
            if (this.originalSpec == null) {
                return;
            } else {
                algorithmParameterSpec = this.originalSpec;
            }
        }
        if (!this.isInitState) {
            throw new ProviderException("cannot call setParameter in the middle of update");
        }
        if (!(algorithmParameterSpec instanceof ContextParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown AlgorithmParameterSpec in signature");
        }
        this.paramSpec = (ContextParameterSpec) algorithmParameterSpec;
        reInit();
    }

    private void reInit() {
        CipherParameters parametersWithContext = this.keyParams;
        if (!this.keyParams.isPrivate()) {
            if (this.paramSpec != null) {
                parametersWithContext = new ParametersWithContext(parametersWithContext, this.paramSpec.getContext());
            }
            reInitialize(false, parametersWithContext);
        } else {
            if (this.appRandom != null) {
                parametersWithContext = new ParametersWithRandom(parametersWithContext, this.appRandom);
            }
            if (this.paramSpec != null) {
                parametersWithContext = new ParametersWithContext(parametersWithContext, this.paramSpec.getContext());
            }
            reInitialize(true, parametersWithContext);
        }
    }

    protected abstract void reInitialize(boolean z, CipherParameters cipherParameters);

    @Override // java.security.SignatureSpi
    protected final AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null && this.paramSpec != ContextParameterSpec.EMPTY_CONTEXT_SPEC) {
            try {
                this.engineParams = this.helper.createAlgorithmParameters("CONTEXT");
                this.engineParams.init(this.paramSpec);
            } catch (Exception e) {
                throw Exceptions.illegalStateException(e.toString(), e);
            }
        }
        return this.engineParams;
    }

    @Override // java.security.SignatureSpi
    protected final void engineSetParameter(String str, Object obj) {
        throw new UnsupportedOperationException("SetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected final Object engineGetParameter(String str) {
        throw new UnsupportedOperationException("GetParameter unsupported");
    }
}
