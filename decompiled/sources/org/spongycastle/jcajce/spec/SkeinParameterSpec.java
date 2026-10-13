package org.spongycastle.jcajce.spec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.spec.AlgorithmParameterSpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/spec/SkeinParameterSpec.class */
public class SkeinParameterSpec implements AlgorithmParameterSpec {
    public static final int PARAM_TYPE_KEY = 0;
    public static final int PARAM_TYPE_CONFIG = 4;
    public static final int PARAM_TYPE_PERSONALISATION = 8;
    public static final int PARAM_TYPE_PUBLIC_KEY = 12;
    public static final int PARAM_TYPE_KEY_IDENTIFIER = 16;
    public static final int PARAM_TYPE_NONCE = 20;
    public static final int PARAM_TYPE_MESSAGE = 48;
    public static final int PARAM_TYPE_OUTPUT = 63;
    private Map parameters;

    public SkeinParameterSpec() {
        this(new HashMap());
    }

    private SkeinParameterSpec(Map parameters) {
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    public Map getParameters() {
        return this.parameters;
    }

    public byte[] getKey() {
        return Arrays.clone((byte[]) this.parameters.get(Integers.valueOf(0)));
    }

    public byte[] getPersonalisation() {
        return Arrays.clone((byte[]) this.parameters.get(Integers.valueOf(8)));
    }

    public byte[] getPublicKey() {
        return Arrays.clone((byte[]) this.parameters.get(Integers.valueOf(12)));
    }

    public byte[] getKeyIdentifier() {
        return Arrays.clone((byte[]) this.parameters.get(Integers.valueOf(16)));
    }

    public byte[] getNonce() {
        return Arrays.clone((byte[]) this.parameters.get(Integers.valueOf(20)));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/spec/SkeinParameterSpec$Builder.class */
    public static class Builder {
        private Map parameters = new HashMap();

        public Builder() {
        }

        public Builder(SkeinParameterSpec params) {
            for (Integer key : params.parameters.keySet()) {
                this.parameters.put(key, params.parameters.get(key));
            }
        }

        public Builder set(int type, byte[] value) {
            if (value == null) {
                throw new IllegalArgumentException("Parameter value must not be null.");
            }
            if (type != 0 && (type <= 4 || type >= 63 || type == 48)) {
                throw new IllegalArgumentException("Parameter types must be in the range 0,5..47,49..62.");
            }
            if (type == 4) {
                throw new IllegalArgumentException("Parameter type 4 is reserved for internal use.");
            }
            this.parameters.put(Integers.valueOf(type), value);
            return this;
        }

        public Builder setKey(byte[] key) {
            return set(0, key);
        }

        public Builder setPersonalisation(byte[] personalisation) {
            return set(8, personalisation);
        }

        public Builder setPersonalisation(Date date, String emailAddress, String distinguisher) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(bout, "UTF-8");
                DateFormat format = new SimpleDateFormat("YYYYMMDD");
                out.write(format.format(date));
                out.write(" ");
                out.write(emailAddress);
                out.write(" ");
                out.write(distinguisher);
                out.close();
                return set(8, bout.toByteArray());
            } catch (IOException e) {
                throw new IllegalStateException("Byte I/O failed: " + e);
            }
        }

        public Builder setPersonalisation(Date date, Locale dateLocale, String emailAddress, String distinguisher) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(bout, "UTF-8");
                DateFormat format = new SimpleDateFormat("YYYYMMDD", dateLocale);
                out.write(format.format(date));
                out.write(" ");
                out.write(emailAddress);
                out.write(" ");
                out.write(distinguisher);
                out.close();
                return set(8, bout.toByteArray());
            } catch (IOException e) {
                throw new IllegalStateException("Byte I/O failed: " + e);
            }
        }

        public Builder setPublicKey(byte[] publicKey) {
            return set(12, publicKey);
        }

        public Builder setKeyIdentifier(byte[] keyIdentifier) {
            return set(16, keyIdentifier);
        }

        public Builder setNonce(byte[] nonce) {
            return set(20, nonce);
        }

        public SkeinParameterSpec build() {
            return new SkeinParameterSpec(this.parameters);
        }
    }
}
