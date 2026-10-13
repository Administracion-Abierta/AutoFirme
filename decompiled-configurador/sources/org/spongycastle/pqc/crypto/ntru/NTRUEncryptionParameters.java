package org.spongycastle.pqc.crypto.ntru;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA512Digest;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/ntru/NTRUEncryptionParameters.class */
public class NTRUEncryptionParameters implements Cloneable {
    public int N;
    public int q;
    public int df;
    public int df1;
    public int df2;
    public int df3;
    public int dr;
    public int dr1;
    public int dr2;
    public int dr3;
    public int dg;
    int llen;
    public int maxMsgLenBytes;
    public int db;
    public int bufferLenBits;
    int bufferLenTrits;
    public int dm0;
    public int pkLen;
    public int c;
    public int minCallsR;
    public int minCallsMask;
    public boolean hashSeed;
    public byte[] oid;
    public boolean sparse;
    public boolean fastFp;
    public int polyType;
    public Digest hashAlg;

    public NTRUEncryptionParameters(int N, int q, int df, int dm0, int db, int c, int minCallsR, int minCallsMask, boolean hashSeed, byte[] oid, boolean sparse, boolean fastFp, Digest hashAlg) {
        this.N = N;
        this.q = q;
        this.df = df;
        this.db = db;
        this.dm0 = dm0;
        this.c = c;
        this.minCallsR = minCallsR;
        this.minCallsMask = minCallsMask;
        this.hashSeed = hashSeed;
        this.oid = oid;
        this.sparse = sparse;
        this.fastFp = fastFp;
        this.polyType = 0;
        this.hashAlg = hashAlg;
        init();
    }

    public NTRUEncryptionParameters(int N, int q, int df1, int df2, int df3, int dm0, int db, int c, int minCallsR, int minCallsMask, boolean hashSeed, byte[] oid, boolean sparse, boolean fastFp, Digest hashAlg) {
        this.N = N;
        this.q = q;
        this.df1 = df1;
        this.df2 = df2;
        this.df3 = df3;
        this.db = db;
        this.dm0 = dm0;
        this.c = c;
        this.minCallsR = minCallsR;
        this.minCallsMask = minCallsMask;
        this.hashSeed = hashSeed;
        this.oid = oid;
        this.sparse = sparse;
        this.fastFp = fastFp;
        this.polyType = 1;
        this.hashAlg = hashAlg;
        init();
    }

    private void init() {
        this.dr = this.df;
        this.dr1 = this.df1;
        this.dr2 = this.df2;
        this.dr3 = this.df3;
        this.dg = this.N / 3;
        this.llen = 1;
        this.maxMsgLenBytes = (((((this.N * 3) / 2) / 8) - this.llen) - (this.db / 8)) - 1;
        this.bufferLenBits = (((((this.N * 3) / 2) + 7) / 8) * 8) + 1;
        this.bufferLenTrits = this.N - 1;
        this.pkLen = this.db;
    }

    public NTRUEncryptionParameters(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        this.N = dis.readInt();
        this.q = dis.readInt();
        this.df = dis.readInt();
        this.df1 = dis.readInt();
        this.df2 = dis.readInt();
        this.df3 = dis.readInt();
        this.db = dis.readInt();
        this.dm0 = dis.readInt();
        this.c = dis.readInt();
        this.minCallsR = dis.readInt();
        this.minCallsMask = dis.readInt();
        this.hashSeed = dis.readBoolean();
        this.oid = new byte[3];
        dis.read(this.oid);
        this.sparse = dis.readBoolean();
        this.fastFp = dis.readBoolean();
        this.polyType = dis.read();
        String alg = dis.readUTF();
        if ("SHA-512".equals(alg)) {
            this.hashAlg = new SHA512Digest();
        } else if ("SHA-256".equals(alg)) {
            this.hashAlg = new SHA256Digest();
        }
        init();
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public NTRUEncryptionParameters m980clone() {
        if (this.polyType == 0) {
            return new NTRUEncryptionParameters(this.N, this.q, this.df, this.dm0, this.db, this.c, this.minCallsR, this.minCallsMask, this.hashSeed, this.oid, this.sparse, this.fastFp, this.hashAlg);
        }
        return new NTRUEncryptionParameters(this.N, this.q, this.df1, this.df2, this.df3, this.dm0, this.db, this.c, this.minCallsR, this.minCallsMask, this.hashSeed, this.oid, this.sparse, this.fastFp, this.hashAlg);
    }

    public int getMaxMessageLength() {
        return this.maxMsgLenBytes;
    }

    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(this.N);
        dos.writeInt(this.q);
        dos.writeInt(this.df);
        dos.writeInt(this.df1);
        dos.writeInt(this.df2);
        dos.writeInt(this.df3);
        dos.writeInt(this.db);
        dos.writeInt(this.dm0);
        dos.writeInt(this.c);
        dos.writeInt(this.minCallsR);
        dos.writeInt(this.minCallsMask);
        dos.writeBoolean(this.hashSeed);
        dos.write(this.oid);
        dos.writeBoolean(this.sparse);
        dos.writeBoolean(this.fastFp);
        dos.write(this.polyType);
        dos.writeUTF(this.hashAlg.getAlgorithmName());
    }

    public int hashCode() {
        int result = (31 * 1) + this.N;
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + this.bufferLenBits)) + this.bufferLenTrits)) + this.c)) + this.db)) + this.df)) + this.df1)) + this.df2)) + this.df3)) + this.dg)) + this.dm0)) + this.dr)) + this.dr1)) + this.dr2)) + this.dr3)) + (this.fastFp ? 1231 : 1237))) + (this.hashAlg == null ? 0 : this.hashAlg.getAlgorithmName().hashCode()))) + (this.hashSeed ? 1231 : 1237))) + this.llen)) + this.maxMsgLenBytes)) + this.minCallsMask)) + this.minCallsR)) + Arrays.hashCode(this.oid))) + this.pkLen)) + this.polyType)) + this.q)) + (this.sparse ? 1231 : 1237);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NTRUEncryptionParameters other = (NTRUEncryptionParameters) obj;
        if (this.N != other.N || this.bufferLenBits != other.bufferLenBits || this.bufferLenTrits != other.bufferLenTrits || this.c != other.c || this.db != other.db || this.df != other.df || this.df1 != other.df1 || this.df2 != other.df2 || this.df3 != other.df3 || this.dg != other.dg || this.dm0 != other.dm0 || this.dr != other.dr || this.dr1 != other.dr1 || this.dr2 != other.dr2 || this.dr3 != other.dr3 || this.fastFp != other.fastFp) {
            return false;
        }
        if (this.hashAlg == null) {
            if (other.hashAlg != null) {
                return false;
            }
        } else if (!this.hashAlg.getAlgorithmName().equals(other.hashAlg.getAlgorithmName())) {
            return false;
        }
        if (this.hashSeed != other.hashSeed || this.llen != other.llen || this.maxMsgLenBytes != other.maxMsgLenBytes || this.minCallsMask != other.minCallsMask || this.minCallsR != other.minCallsR || !Arrays.equals(this.oid, other.oid) || this.pkLen != other.pkLen || this.polyType != other.polyType || this.q != other.q || this.sparse != other.sparse) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder output = new StringBuilder("EncryptionParameters(N=" + this.N + " q=" + this.q);
        if (this.polyType == 0) {
            output.append(" polyType=SIMPLE df=" + this.df);
        } else {
            output.append(" polyType=PRODUCT df1=" + this.df1 + " df2=" + this.df2 + " df3=" + this.df3);
        }
        output.append(" dm0=" + this.dm0 + " db=" + this.db + " c=" + this.c + " minCallsR=" + this.minCallsR + " minCallsMask=" + this.minCallsMask + " hashSeed=" + this.hashSeed + " hashAlg=" + this.hashAlg + " oid=" + Arrays.toString(this.oid) + " sparse=" + this.sparse + ")");
        return output.toString();
    }
}
