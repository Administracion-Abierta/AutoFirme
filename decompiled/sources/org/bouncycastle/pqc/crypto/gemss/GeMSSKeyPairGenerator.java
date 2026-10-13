package org.bouncycastle.pqc.crypto.gemss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/GeMSSKeyPairGenerator.class */
public class GeMSSKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;
    private GeMSSParameters parameters;

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.random = keyGenerationParameters.getRandom();
        this.parameters = ((GeMSSKeyGenerationParameters) keyGenerationParameters).getParameters();
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        GeMSSEngine engine = this.parameters.getEngine();
        byte[] bArrSec_rand = sec_rand(engine.SIZE_SEED_SK);
        int i = (2 + engine.HFEDegJ + ((engine.HFEDegI * (engine.HFEDegI + 1)) >>> 1) + (engine.NB_MONOMIAL_VINEGAR - 1) + ((engine.HFEDegI + 1) * engine.HFEv)) * engine.NB_WORD_GFqn;
        int i2 = ((i + (engine.LTRIANGULAR_NV_SIZE << 1)) + (engine.LTRIANGULAR_N_SIZE << 1)) << 3;
        Pointer pointer = new Pointer(i2 >>> 3);
        byte[] bArr = new byte[i2];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(engine.ShakeBitStrength);
        sHAKEDigest.update(bArrSec_rand, 0, engine.SIZE_SEED_SK);
        sHAKEDigest.doFinal(bArr, 0, i2);
        byte[] bArr2 = new byte[engine.SIZE_SEED_SK];
        byte[] bArr3 = new byte[((engine.NB_MONOMIAL_PK * engine.HFEm) + 7) >> 3];
        System.arraycopy(bArrSec_rand, 0, bArr2, 0, bArr2.length);
        pointer.fill(0, bArr, 0, bArr.length);
        engine.cleanMonicHFEv_gf2nx(pointer);
        Pointer pointer2 = new Pointer(engine.NB_MONOMIAL_PK * engine.NB_WORD_GFqn);
        if (engine.HFEDeg > 34) {
            engine.genSecretMQS_gf2_opt(pointer2, pointer);
        }
        Pointer pointer3 = new Pointer(engine.MATRIXnv_SIZE);
        Pointer pointer4 = new Pointer(pointer3);
        Pointer pointer5 = new Pointer(pointer, i);
        Pointer pointer6 = new Pointer(pointer5, engine.LTRIANGULAR_NV_SIZE);
        engine.cleanLowerMatrix(pointer5, GeMSSEngine.FunctionParams.NV);
        engine.cleanLowerMatrix(pointer6, GeMSSEngine.FunctionParams.NV);
        engine.invMatrixLU_gf2(pointer3, pointer5, pointer6, GeMSSEngine.FunctionParams.NV);
        if (engine.HFEDeg > 34) {
            engine.changeVariablesMQS64_gf2(pointer2, pointer3);
        } else if (engine.interpolateHFE_FS_ref(pointer2, pointer, pointer3) != 0) {
            throw new IllegalArgumentException("Error");
        }
        pointer5.move(engine.LTRIANGULAR_NV_SIZE << 1);
        pointer6.changeIndex(pointer5.getIndex() + engine.LTRIANGULAR_N_SIZE);
        engine.cleanLowerMatrix(pointer5, GeMSSEngine.FunctionParams.N);
        engine.cleanLowerMatrix(pointer6, GeMSSEngine.FunctionParams.N);
        engine.invMatrixLU_gf2(pointer4, pointer5, pointer6, GeMSSEngine.FunctionParams.N);
        if (engine.HFEmr8 != 0) {
            PointerUnion pointerUnion = new PointerUnion((engine.NB_MONOMIAL_PK * engine.NB_BYTES_GFqm) + ((8 - (engine.NB_BYTES_GFqm & 7)) & 7));
            for (int i3 = (engine.NB_BYTES_GFqm & 7) != 0 ? 1 : 0; i3 < engine.NB_MONOMIAL_PK; i3++) {
                engine.vecMatProduct(pointerUnion, pointer2, pointer4, GeMSSEngine.FunctionParams.M);
                pointer2.move(engine.NB_WORD_GFqn);
                pointerUnion.moveNextBytes(engine.NB_BYTES_GFqm);
            }
            if ((engine.NB_BYTES_GFqm & 7) != 0) {
                Pointer pointer7 = new Pointer(engine.NB_WORD_GF2m);
                engine.vecMatProduct(pointer7, pointer2, pointer4, GeMSSEngine.FunctionParams.M);
                for (int i4 = 0; i4 < engine.NB_WORD_GF2m; i4++) {
                    pointerUnion.set(i4, pointer7.get(i4));
                }
            }
            pointerUnion.indexReset();
            byte[] bArr4 = new byte[engine.HFEmr8 * engine.NB_BYTES_EQUATION];
            engine.convMQS_one_to_last_mr8_equations_gf2(bArr4, pointerUnion);
            pointerUnion.indexReset();
            if (engine.HFENr8 == 0 || engine.HFEmr8 <= 1) {
                engine.convMQS_one_eq_to_hybrid_rep8_comp_gf2(bArr3, pointerUnion, bArr4);
            } else {
                engine.convMQS_one_eq_to_hybrid_rep8_uncomp_gf2(bArr3, pointerUnion, bArr4);
            }
        } else {
            PointerUnion pointerUnion2 = new PointerUnion(engine.NB_WORD_GF2m << 3);
            int bytesMove = 0;
            for (int i5 = 0; i5 < engine.NB_MONOMIAL_PK; i5++) {
                engine.vecMatProduct(pointerUnion2, pointer2, pointer4, GeMSSEngine.FunctionParams.M);
                bytesMove = pointerUnion2.toBytesMove(bArr3, bytesMove, engine.NB_BYTES_GFqm);
                pointerUnion2.indexReset();
                pointer2.move(engine.NB_WORD_GFqn);
            }
        }
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new GeMSSPublicKeyParameters(this.parameters, bArr3), (AsymmetricKeyParameter) new GeMSSPrivateKeyParameters(this.parameters, bArr2));
    }

    private byte[] sec_rand(int i) {
        byte[] bArr = new byte[i];
        this.random.nextBytes(bArr);
        return bArr;
    }
}
