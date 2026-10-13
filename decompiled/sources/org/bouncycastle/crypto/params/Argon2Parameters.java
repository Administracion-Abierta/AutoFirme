package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/Argon2Parameters.class */
public class Argon2Parameters {
    public static final int ARGON2_d = 0;
    public static final int ARGON2_i = 1;
    public static final int ARGON2_id = 2;
    public static final int ARGON2_VERSION_10 = 16;
    public static final int ARGON2_VERSION_13 = 19;
    private static final int DEFAULT_ITERATIONS = 3;
    private static final int DEFAULT_MEMORY_COST = 12;
    private static final int DEFAULT_LANES = 1;
    private static final int DEFAULT_TYPE = 1;
    private static final int DEFAULT_VERSION = 19;
    private final byte[] salt;
    private final byte[] secret;
    private final byte[] additional;
    private final int iterations;
    private final int memory;
    private final int lanes;
    private final int version;
    private final int type;
    private final CharToByteConverter converter;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/Argon2Parameters$Builder.class */
    public static class Builder {
        private byte[] salt;
        private byte[] secret;
        private byte[] additional;
        private int iterations;
        private int memory;
        private int lanes;
        private int version;
        private final int type;
        private CharToByteConverter converter;

        public Builder() {
            this(1);
        }

        public Builder(int i) {
            this.converter = PasswordConverter.UTF8;
            this.type = i;
            this.lanes = 1;
            this.memory = 4096;
            this.iterations = 3;
            this.version = 19;
        }

        public Builder withParallelism(int i) {
            this.lanes = i;
            return this;
        }

        public Builder withSalt(byte[] bArr) {
            this.salt = Arrays.clone(bArr);
            return this;
        }

        public Builder withSecret(byte[] bArr) {
            this.secret = Arrays.clone(bArr);
            return this;
        }

        public Builder withAdditional(byte[] bArr) {
            this.additional = Arrays.clone(bArr);
            return this;
        }

        public Builder withIterations(int i) {
            this.iterations = i;
            return this;
        }

        public Builder withMemoryAsKB(int i) {
            this.memory = i;
            return this;
        }

        public Builder withMemoryPowOfTwo(int i) {
            this.memory = 1 << i;
            return this;
        }

        public Builder withVersion(int i) {
            this.version = i;
            return this;
        }

        public Builder withCharToByteConverter(CharToByteConverter charToByteConverter) {
            this.converter = charToByteConverter;
            return this;
        }

        public Argon2Parameters build() {
            return new Argon2Parameters(this.type, this.salt, this.secret, this.additional, this.iterations, this.memory, this.lanes, this.version, this.converter);
        }

        public void clear() {
            Arrays.clear(this.salt);
            Arrays.clear(this.secret);
            Arrays.clear(this.additional);
        }
    }

    private Argon2Parameters(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, int i2, int i3, int i4, int i5, CharToByteConverter charToByteConverter) {
        this.salt = Arrays.clone(bArr);
        this.secret = Arrays.clone(bArr2);
        this.additional = Arrays.clone(bArr3);
        this.iterations = i2;
        this.memory = i3;
        this.lanes = i4;
        this.version = i5;
        this.type = i;
        this.converter = charToByteConverter;
    }

    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }

    public byte[] getSecret() {
        return Arrays.clone(this.secret);
    }

    public byte[] getAdditional() {
        return Arrays.clone(this.additional);
    }

    public int getIterations() {
        return this.iterations;
    }

    public int getMemory() {
        return this.memory;
    }

    public int getLanes() {
        return this.lanes;
    }

    public int getVersion() {
        return this.version;
    }

    public int getType() {
        return this.type;
    }

    public CharToByteConverter getCharToByteConverter() {
        return this.converter;
    }

    public void clear() {
        Arrays.clear(this.salt);
        Arrays.clear(this.secret);
        Arrays.clear(this.additional);
    }
}
