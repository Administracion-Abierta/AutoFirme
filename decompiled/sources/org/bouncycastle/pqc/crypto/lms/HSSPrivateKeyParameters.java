package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/HSSPrivateKeyParameters.class */
public class HSSPrivateKeyParameters extends LMSKeyParameters implements LMSContextBasedSigner {
    private final int l;
    private final boolean isShard;
    private List<LMSPrivateKeyParameters> keys;
    private List<LMSSignature> sig;
    private final long indexLimit;
    private long index;
    private HSSPublicKeyParameters publicKey;

    public HSSPrivateKeyParameters(LMSPrivateKeyParameters lMSPrivateKeyParameters, long j, long j2) {
        super(true);
        this.index = 0L;
        this.l = 1;
        this.keys = Collections.singletonList(lMSPrivateKeyParameters);
        this.sig = Collections.emptyList();
        this.index = j;
        this.indexLimit = j2;
        this.isShard = false;
        resetKeyToIndex();
    }

    public HSSPrivateKeyParameters(int i, List<LMSPrivateKeyParameters> list, List<LMSSignature> list2, long j, long j2) {
        super(true);
        this.index = 0L;
        this.l = i;
        this.keys = Collections.unmodifiableList(list);
        this.sig = Collections.unmodifiableList(list2);
        this.index = j;
        this.indexLimit = j2;
        this.isShard = false;
        resetKeyToIndex();
    }

    private HSSPrivateKeyParameters(int i, List<LMSPrivateKeyParameters> list, List<LMSSignature> list2, long j, long j2, boolean z) {
        super(true);
        this.index = 0L;
        this.l = i;
        this.keys = Collections.unmodifiableList(list);
        this.sig = Collections.unmodifiableList(list2);
        this.index = j;
        this.indexLimit = j2;
        this.isShard = z;
    }

    public static HSSPrivateKeyParameters getInstance(byte[] bArr, byte[] bArr2) throws IOException {
        HSSPrivateKeyParameters hSSPrivateKeyParameters = getInstance(bArr);
        hSSPrivateKeyParameters.publicKey = HSSPublicKeyParameters.getInstance(bArr2);
        return hSSPrivateKeyParameters;
    }

    public static HSSPrivateKeyParameters getInstance(Object obj) throws IOException {
        if (obj instanceof HSSPrivateKeyParameters) {
            return (HSSPrivateKeyParameters) obj;
        }
        if (obj instanceof DataInputStream) {
            if (((DataInputStream) obj).readInt() != 0) {
                throw new IllegalStateException("unknown version for hss private key");
            }
            int i = ((DataInputStream) obj).readInt();
            long j = ((DataInputStream) obj).readLong();
            long j2 = ((DataInputStream) obj).readLong();
            boolean z = ((DataInputStream) obj).readBoolean();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < i; i2++) {
                arrayList.add(LMSPrivateKeyParameters.getInstance(obj));
            }
            for (int i3 = 0; i3 < i - 1; i3++) {
                arrayList2.add(LMSSignature.getInstance(obj));
            }
            return new HSSPrivateKeyParameters(i, arrayList, arrayList2, j, j2, z);
        }
        if (!(obj instanceof byte[])) {
            if (obj instanceof InputStream) {
                return getInstance(Streams.readAll((InputStream) obj));
            }
            throw new IllegalArgumentException("cannot parse " + obj);
        }
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(new ByteArrayInputStream((byte[]) obj));
            try {
                HSSPrivateKeyParameters hSSPrivateKeyParameters = getInstance(dataInputStream);
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                return hSSPrivateKeyParameters;
            } catch (Exception e) {
                LMSPrivateKeyParameters lMSPrivateKeyParameters = LMSPrivateKeyParameters.getInstance(obj);
                HSSPrivateKeyParameters hSSPrivateKeyParameters2 = new HSSPrivateKeyParameters(lMSPrivateKeyParameters, lMSPrivateKeyParameters.getIndex(), lMSPrivateKeyParameters.getIndexLimit());
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                return hSSPrivateKeyParameters2;
            }
        } catch (Throwable th) {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
            throw th;
        }
    }

    public int getL() {
        return this.l;
    }

    public synchronized long getIndex() {
        return this.index;
    }

    public synchronized LMSParameters[] getLMSParameters() {
        int size = this.keys.size();
        LMSParameters[] lMSParametersArr = new LMSParameters[size];
        for (int i = 0; i < size; i++) {
            LMSPrivateKeyParameters lMSPrivateKeyParameters = this.keys.get(i);
            lMSParametersArr[i] = new LMSParameters(lMSPrivateKeyParameters.getSigParameters(), lMSPrivateKeyParameters.getOtsParameters());
        }
        return lMSParametersArr;
    }

    synchronized void incIndex() {
        this.index++;
    }

    private static HSSPrivateKeyParameters makeCopy(HSSPrivateKeyParameters hSSPrivateKeyParameters) {
        try {
            return getInstance(hSSPrivateKeyParameters.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected void updateHierarchy(LMSPrivateKeyParameters[] lMSPrivateKeyParametersArr, LMSSignature[] lMSSignatureArr) {
        synchronized (this) {
            this.keys = Collections.unmodifiableList(Arrays.asList(lMSPrivateKeyParametersArr));
            this.sig = Collections.unmodifiableList(Arrays.asList(lMSSignatureArr));
        }
    }

    boolean isShard() {
        return this.isShard;
    }

    long getIndexLimit() {
        return this.indexLimit;
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSContextBasedSigner
    public long getUsagesRemaining() {
        return getIndexLimit() - getIndex();
    }

    LMSPrivateKeyParameters getRootKey() {
        return this.keys.get(0);
    }

    public HSSPrivateKeyParameters extractKeyShard(int i) {
        HSSPrivateKeyParameters hSSPrivateKeyParametersMakeCopy;
        synchronized (this) {
            try {
                if (i < 0) {
                    throw new IllegalArgumentException("usageCount cannot be negative");
                }
                if (i > this.indexLimit - this.index) {
                    throw new IllegalArgumentException("usageCount exceeds usages remaining in current leaf");
                }
                long j = this.index;
                long j2 = this.index + ((long) i);
                this.index = j2;
                hSSPrivateKeyParametersMakeCopy = makeCopy(new HSSPrivateKeyParameters(this.l, new ArrayList(getKeys()), new ArrayList(getSig()), j, j2, true));
                resetKeyToIndex();
            } catch (Throwable th) {
                throw th;
            }
        }
        return hSSPrivateKeyParametersMakeCopy;
    }

    synchronized List<LMSPrivateKeyParameters> getKeys() {
        return this.keys;
    }

    synchronized List<LMSSignature> getSig() {
        return this.sig;
    }

    void resetKeyToIndex() {
        List<LMSPrivateKeyParameters> keys = getKeys();
        long[] jArr = new long[keys.size()];
        long index = getIndex();
        for (int size = keys.size() - 1; size >= 0; size--) {
            LMSigParameters sigParameters = keys.get(size).getSigParameters();
            jArr[size] = index & ((long) ((1 << sigParameters.getH()) - 1));
            index >>>= sigParameters.getH();
        }
        boolean z = false;
        LMSPrivateKeyParameters[] lMSPrivateKeyParametersArr = (LMSPrivateKeyParameters[]) keys.toArray(new LMSPrivateKeyParameters[keys.size()]);
        LMSSignature[] lMSSignatureArr = (LMSSignature[]) this.sig.toArray(new LMSSignature[this.sig.size()]);
        LMSPrivateKeyParameters rootKey = getRootKey();
        if (lMSPrivateKeyParametersArr[0].getIndex() - 1 != jArr[0]) {
            lMSPrivateKeyParametersArr[0] = LMS.generateKeys(rootKey.getSigParameters(), rootKey.getOtsParameters(), (int) jArr[0], rootKey.getI(), rootKey.getMasterSecret());
            z = true;
        }
        for (int i = 1; i < jArr.length; i++) {
            LMSPrivateKeyParameters lMSPrivateKeyParameters = lMSPrivateKeyParametersArr[i - 1];
            int n = lMSPrivateKeyParameters.getOtsParameters().getN();
            byte[] bArr = new byte[16];
            byte[] bArr2 = new byte[n];
            SeedDerive seedDerive = new SeedDerive(lMSPrivateKeyParameters.getI(), lMSPrivateKeyParameters.getMasterSecret(), DigestUtil.getDigest(lMSPrivateKeyParameters.getOtsParameters()));
            seedDerive.setQ((int) jArr[i - 1]);
            seedDerive.setJ(-2);
            seedDerive.deriveSeed(bArr2, true);
            byte[] bArr3 = new byte[n];
            seedDerive.deriveSeed(bArr3, false);
            System.arraycopy(bArr3, 0, bArr, 0, bArr.length);
            boolean z2 = i < jArr.length - 1 ? jArr[i] == ((long) (lMSPrivateKeyParametersArr[i].getIndex() - 1)) : jArr[i] == ((long) lMSPrivateKeyParametersArr[i].getIndex());
            if (!(org.bouncycastle.util.Arrays.areEqual(bArr, lMSPrivateKeyParametersArr[i].getI()) && org.bouncycastle.util.Arrays.areEqual(bArr2, lMSPrivateKeyParametersArr[i].getMasterSecret()))) {
                lMSPrivateKeyParametersArr[i] = LMS.generateKeys(keys.get(i).getSigParameters(), keys.get(i).getOtsParameters(), (int) jArr[i], bArr, bArr2);
                lMSSignatureArr[i - 1] = LMS.generateSign(lMSPrivateKeyParametersArr[i - 1], lMSPrivateKeyParametersArr[i].getPublicKey().toByteArray());
                z = true;
            } else if (!z2) {
                lMSPrivateKeyParametersArr[i] = LMS.generateKeys(keys.get(i).getSigParameters(), keys.get(i).getOtsParameters(), (int) jArr[i], bArr, bArr2);
                z = true;
            }
        }
        if (z) {
            updateHierarchy(lMSPrivateKeyParametersArr, lMSSignatureArr);
        }
    }

    public synchronized HSSPublicKeyParameters getPublicKey() {
        return new HSSPublicKeyParameters(this.l, getRootKey().getPublicKey());
    }

    void replaceConsumedKey(int i) {
        LMOtsPrivateKey currentOTSKey = this.keys.get(i - 1).getCurrentOTSKey();
        int n = currentOTSKey.getParameter().getN();
        SeedDerive derivationFunction = currentOTSKey.getDerivationFunction();
        derivationFunction.setJ(-2);
        byte[] bArr = new byte[n];
        derivationFunction.deriveSeed(bArr, true);
        byte[] bArr2 = new byte[n];
        derivationFunction.deriveSeed(bArr2, false);
        byte[] bArr3 = new byte[16];
        System.arraycopy(bArr2, 0, bArr3, 0, bArr3.length);
        ArrayList arrayList = new ArrayList(this.keys);
        LMSPrivateKeyParameters lMSPrivateKeyParameters = this.keys.get(i);
        arrayList.set(i, LMS.generateKeys(lMSPrivateKeyParameters.getSigParameters(), lMSPrivateKeyParameters.getOtsParameters(), 0, bArr3, bArr));
        ArrayList arrayList2 = new ArrayList(this.sig);
        arrayList2.set(i - 1, LMS.generateSign((LMSPrivateKeyParameters) arrayList.get(i - 1), ((LMSPrivateKeyParameters) arrayList.get(i)).getPublicKey().toByteArray()));
        this.keys = Collections.unmodifiableList(arrayList);
        this.sig = Collections.unmodifiableList(arrayList2);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HSSPrivateKeyParameters hSSPrivateKeyParameters = (HSSPrivateKeyParameters) obj;
        if (this.l == hSSPrivateKeyParameters.l && this.isShard == hSSPrivateKeyParameters.isShard && this.indexLimit == hSSPrivateKeyParameters.indexLimit && this.index == hSSPrivateKeyParameters.index && this.keys.equals(hSSPrivateKeyParameters.keys)) {
            return this.sig.equals(hSSPrivateKeyParameters.sig);
        }
        return false;
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSKeyParameters, org.bouncycastle.util.Encodable
    public synchronized byte[] getEncoded() throws IOException {
        Composer composerBool = Composer.compose().u32str(0).u32str(this.l).u64str(this.index).u64str(this.indexLimit).bool(this.isShard);
        Iterator<LMSPrivateKeyParameters> it = this.keys.iterator();
        while (it.hasNext()) {
            composerBool.bytes(it.next());
        }
        Iterator<LMSSignature> it2 = this.sig.iterator();
        while (it2.hasNext()) {
            composerBool.bytes(it2.next());
        }
        return composerBool.build();
    }

    public int hashCode() {
        return (31 * ((31 * ((31 * ((31 * ((31 * this.l) + (this.isShard ? 1 : 0))) + this.keys.hashCode())) + this.sig.hashCode())) + ((int) (this.indexLimit ^ (this.indexLimit >>> 32))))) + ((int) (this.index ^ (this.index >>> 32)));
    }

    protected Object clone() throws CloneNotSupportedException {
        return makeCopy(this);
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSContextBasedSigner
    public LMSContext generateLMSContext() {
        LMSPrivateKeyParameters lMSPrivateKeyParameters;
        LMSSignedPubKey[] lMSSignedPubKeyArr;
        int l = getL();
        synchronized (this) {
            HSS.rangeTestKeys(this);
            List<LMSPrivateKeyParameters> keys = getKeys();
            List<LMSSignature> sig = getSig();
            lMSPrivateKeyParameters = getKeys().get(l - 1);
            lMSSignedPubKeyArr = new LMSSignedPubKey[l - 1];
            for (int i = 0; i < l - 1; i++) {
                lMSSignedPubKeyArr[i] = new LMSSignedPubKey(sig.get(i), keys.get(i + 1).getPublicKey());
            }
            incIndex();
        }
        return lMSPrivateKeyParameters.generateLMSContext().withSignedPublicKeys(lMSSignedPubKeyArr);
    }

    @Override // org.bouncycastle.pqc.crypto.lms.LMSContextBasedSigner
    public byte[] generateSignature(LMSContext lMSContext) {
        try {
            return HSS.generateSignature(getL(), lMSContext).getEncoded();
        } catch (IOException e) {
            throw new IllegalStateException("unable to encode signature: " + e.getMessage(), e);
        }
    }
}
