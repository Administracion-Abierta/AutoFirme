package es.gob.jmulticard.jse.provider.rsacipher;

import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/rsacipher/RSAPadding.class */
final class RSAPadding {
    static final int PAD_BLOCKTYPE_1 = 1;
    static final int PAD_BLOCKTYPE_2 = 2;
    static final int PAD_NONE = 3;
    static final int PAD_OAEP_MGF1 = 4;
    private final int type;
    private final int paddedSize;
    private SecureRandom random;
    private final int maxDataSize;
    private MessageDigest md;
    private MessageDigest mgfMd;
    private byte[] lHash;
    private static final Map<String, byte[]> EMPTY_HASHES = Collections.synchronizedMap(new HashMap());

    static RSAPadding createInstance(int type, int paddedSize, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        return new RSAPadding(type, paddedSize, random, null);
    }

    static RSAPadding createInstance(int type, int paddedSize, SecureRandom random, OAEPParameterSpec spec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        return new RSAPadding(type, paddedSize, random, spec);
    }

    private RSAPadding(int paddingType, int sizeAfterPadding, SecureRandom randomSrc, OAEPParameterSpec spec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.type = paddingType;
        this.paddedSize = sizeAfterPadding;
        this.random = randomSrc;
        if (sizeAfterPadding < 64) {
            throw new InvalidKeyException("El tamano tras el relleno debe ser de al menos 64 octetos, y es de " + sizeAfterPadding + " octetos");
        }
        switch (paddingType) {
            case 1:
            case 2:
                this.maxDataSize = sizeAfterPadding - 11;
                return;
            case 3:
                this.maxDataSize = sizeAfterPadding;
                return;
            case 4:
                String mdName = "SHA-1";
                String mgfMdName = "SHA-1";
                byte[] digestInput = null;
                if (spec != null) {
                    try {
                        mdName = spec.getDigestAlgorithm();
                        String mgfName = spec.getMGFAlgorithm();
                        if (!"MGF1".equalsIgnoreCase(mgfName)) {
                            throw new InvalidAlgorithmParameterException("Unsupported MGF algo: " + mgfName);
                        }
                        mgfMdName = ((MGF1ParameterSpec) spec.getMGFParameters()).getDigestAlgorithm();
                        PSource pSrc = spec.getPSource();
                        String pSrcAlgo = pSrc.getAlgorithm();
                        if (!"PSpecified".equalsIgnoreCase(pSrcAlgo)) {
                            throw new InvalidAlgorithmParameterException("Unsupported pSource algo: " + pSrcAlgo);
                        }
                        digestInput = ((PSource.PSpecified) pSrc).getValue();
                    } catch (NoSuchAlgorithmException e) {
                        throw new InvalidKeyException("Digest " + mdName + " not available", e);
                    }
                }
                this.md = MessageDigest.getInstance(mdName);
                this.mgfMd = MessageDigest.getInstance(mgfMdName);
                this.lHash = getInitialHash(this.md, digestInput);
                int digestLen = this.lHash.length;
                this.maxDataSize = (sizeAfterPadding - 2) - (2 * digestLen);
                if (this.maxDataSize <= 0) {
                    throw new InvalidKeyException("Key is too short for encryption using OAEPPadding with " + mdName + " and MGF1" + mgfMdName);
                }
                return;
            default:
                throw new InvalidKeyException("Tipo de relleno invalido: " + paddingType);
        }
    }

    private static byte[] getInitialHash(MessageDigest md, byte[] digestInput) {
        if (digestInput == null || digestInput.length == 0) {
            String digestName = md.getAlgorithm();
            return EMPTY_HASHES.computeIfAbsent(digestName, k -> {
                return md.digest();
            });
        }
        return md.digest(digestInput);
    }

    int getMaxDataSize() {
        return this.maxDataSize;
    }

    byte[] pad(byte[] data, int ofs, int len) throws BadPaddingException {
        return pad(RSACore.convert(data, ofs, len));
    }

    private byte[] pad(byte[] data) throws BadPaddingException {
        if (data.length > this.maxDataSize) {
            throw new BadPaddingException("Los datos deben ser de tamano inferior a " + (this.maxDataSize + 1) + " octetos");
        }
        switch (this.type) {
            case 1:
            case 2:
                return padV15(data);
            case 3:
                return data;
            case 4:
                return padOaep(data);
            default:
                throw new AssertionError();
        }
    }

    byte[] unpad(byte[] padded) throws BadPaddingException {
        if (padded.length != this.paddedSize) {
            throw new BadPaddingException("El tamano de los datos rellenos no es valido");
        }
        switch (this.type) {
            case 1:
            case 2:
                return unpadV15(padded);
            case 3:
                return padded;
            case 4:
                return unpadOAEP(padded);
            default:
                throw new AssertionError();
        }
    }

    private byte[] padV15(byte[] data) {
        int b;
        byte[] padded = new byte[this.paddedSize];
        System.arraycopy(data, 0, padded, this.paddedSize - data.length, data.length);
        int psSize = (this.paddedSize - 3) - data.length;
        int k = 0 + 1;
        padded[0] = 0;
        int k2 = k + 1;
        padded[k] = (byte) this.type;
        if (this.type != 1) {
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            byte[] r = new byte[64];
            int i = -1;
            while (true) {
                int i2 = psSize;
                psSize--;
                if (i2 <= 0) {
                    break;
                }
                do {
                    if (i < 0) {
                        this.random.nextBytes(r);
                        i = r.length - 1;
                    }
                    int i3 = i;
                    i--;
                    b = r[i3] & 255;
                } while (b == 0);
                int i4 = k2;
                k2++;
                padded[i4] = (byte) b;
            }
        } else {
            while (true) {
                int i5 = psSize;
                psSize--;
                if (i5 <= 0) {
                    break;
                }
                int i6 = k2;
                k2++;
                padded[i6] = -1;
            }
        }
        return padded;
    }

    private byte[] unpadV15(byte[] padded) throws BadPaddingException {
        int k = 0 + 1;
        if (padded[0] != 0) {
            throw new BadPaddingException("Los datos rellenos no empiezan en cero");
        }
        int p = 0;
        while (k < padded.length) {
            int i = k;
            k++;
            int b = padded[i] & 255;
            if (b == 0 && p == 0) {
                p = k;
            }
            if (k == padded.length && p == 0) {
                throw new BadPaddingException("Error en la retirada del relleno PKCS#1 v1.5");
            }
            if (this.type == 1 && b != 255 && p == 0) {
                throw new BadPaddingException("Error en la retirada del relleno PKCS#1 v1.5 tipo 1 (firma)");
            }
        }
        int n = padded.length - p;
        if (n > this.maxDataSize) {
            throw new BadPaddingException("El tamano de los datos rellenos (" + n + ") excede del maximo permitido");
        }
        byte[] padding = new byte[p];
        System.arraycopy(padded, 0, padding, 0, p);
        byte[] data = new byte[n];
        System.arraycopy(padded, p, data, 0, n);
        return data;
    }

    private byte[] padOaep(byte[] message) throws BadPaddingException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        int hLen = this.lHash.length;
        byte[] seed = new byte[hLen];
        this.random.nextBytes(seed);
        byte[] encodedMessage = new byte[this.paddedSize];
        System.arraycopy(seed, 0, encodedMessage, 1, hLen);
        int dbStart = hLen + 1;
        int dbLen = encodedMessage.length - dbStart;
        int mStart = this.paddedSize - message.length;
        System.arraycopy(this.lHash, 0, encodedMessage, dbStart, hLen);
        encodedMessage[mStart - 1] = 1;
        System.arraycopy(message, 0, encodedMessage, mStart, message.length);
        mgf1(encodedMessage, 1, hLen, encodedMessage, dbStart, dbLen);
        mgf1(encodedMessage, dbStart, dbLen, encodedMessage, 1, hLen);
        return encodedMessage;
    }

    private byte[] unpadOAEP(byte[] padded) throws BadPaddingException {
        boolean bp = false;
        int hLen = this.lHash.length;
        if (padded[0] != 0) {
            bp = true;
        }
        int dbStart = hLen + 1;
        int dbLen = padded.length - dbStart;
        mgf1(padded, dbStart, dbLen, padded, 1, hLen);
        mgf1(padded, 1, hLen, padded, dbStart, dbLen);
        for (int i = 0; i < hLen; i++) {
            if (this.lHash[i] != padded[dbStart + i]) {
                bp = true;
                break;
            }
        }
        int padStart = dbStart + hLen;
        int onePos = -1;
        for (int i2 = padStart; i2 < padded.length; i2++) {
            byte b = padded[i2];
            if (onePos == -1) {
                if (b == 1) {
                    onePos = i2;
                } else if (b != 0) {
                    bp = true;
                }
            }
        }
        if (onePos == -1) {
            bp = true;
            onePos = padded.length - 1;
        }
        int mStart = onePos + 1;
        byte[] tmp = new byte[mStart - padStart];
        System.arraycopy(padded, padStart, tmp, 0, tmp.length);
        byte[] m = new byte[padded.length - mStart];
        System.arraycopy(padded, mStart, m, 0, m.length);
        BadPaddingException bpe = new BadPaddingException("Error en la retiarada del relleno PKCS#1 v2.1 OAEP");
        if (bp) {
            throw bpe;
        }
        return m;
    }

    private void mgf1(byte[] seed, int seedOfs, int seedLen, byte[] out, int outOffset, int maskLength) throws BadPaddingException {
        int maskLen = maskLength;
        int outOfs = outOffset;
        byte[] counter = new byte[4];
        byte[] digest = new byte[this.mgfMd.getDigestLength()];
        while (maskLen > 0) {
            this.mgfMd.update(seed, seedOfs, seedLen);
            this.mgfMd.update(counter);
            try {
                this.mgfMd.digest(digest, 0, digest.length);
                int i = 0;
                while (i < digest.length && maskLen > 0) {
                    int i2 = outOfs;
                    outOfs++;
                    int i3 = i;
                    i++;
                    out[i2] = (byte) (out[i2] ^ digest[i3]);
                    maskLen--;
                }
                if (maskLen > 0) {
                    int i4 = counter.length - 1;
                    while (true) {
                        int i5 = i4;
                        byte b = (byte) (counter[i5] + 1);
                        counter[i5] = b;
                        if (b != 0 || i4 <= 0) {
                            break;
                        } else {
                            i4--;
                        }
                    }
                }
            } catch (DigestException e) {
                throw new BadPaddingException(e.toString());
            }
        }
    }
}
