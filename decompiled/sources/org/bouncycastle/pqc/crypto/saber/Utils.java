package org.bouncycastle.pqc.crypto.saber;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/saber/Utils.class */
class Utils {
    private final int SABER_N;
    private final int SABER_L;
    private final int SABER_ET;
    private final int SABER_POLYBYTES;
    private final int SABER_EP;
    private final int SABER_KEYBYTES;
    private final boolean usingEffectiveMasking;

    public Utils(SABEREngine sABEREngine) {
        this.SABER_N = sABEREngine.getSABER_N();
        this.SABER_L = sABEREngine.getSABER_L();
        this.SABER_ET = sABEREngine.getSABER_ET();
        this.SABER_POLYBYTES = sABEREngine.getSABER_POLYBYTES();
        this.SABER_EP = sABEREngine.getSABER_EP();
        this.SABER_KEYBYTES = sABEREngine.getSABER_KEYBYTES();
        this.usingEffectiveMasking = sABEREngine.usingEffectiveMasking;
    }

    public void POLT2BS(byte[] bArr, int i, short[] sArr) {
        if (this.SABER_ET == 3) {
            short s = 0;
            while (true) {
                short s2 = s;
                if (s2 >= this.SABER_N / 8) {
                    return;
                }
                short s3 = (short) (3 * s2);
                short s4 = (short) (8 * s2);
                bArr[i + s3 + 0] = (byte) ((sArr[s4 + 0] & 7) | ((sArr[s4 + 1] & 7) << 3) | ((sArr[s4 + 2] & 3) << 6));
                bArr[i + s3 + 1] = (byte) (((sArr[s4 + 2] >> 2) & 1) | ((sArr[s4 + 3] & 7) << 1) | ((sArr[s4 + 4] & 7) << 4) | ((sArr[s4 + 5] & 1) << 7));
                bArr[i + s3 + 2] = (byte) (((sArr[s4 + 5] >> 1) & 3) | ((sArr[s4 + 6] & 7) << 2) | ((sArr[s4 + 7] & 7) << 5));
                s = (short) (s2 + 1);
            }
        } else if (this.SABER_ET == 4) {
            short s5 = 0;
            while (true) {
                short s6 = s5;
                if (s6 >= this.SABER_N / 2) {
                    return;
                }
                short s7 = (short) (2 * s6);
                bArr[i + s6] = (byte) ((sArr[s7] & 15) | ((sArr[s7 + 1] & 15) << 4));
                s5 = (short) (s6 + 1);
            }
        } else {
            if (this.SABER_ET != 6) {
                return;
            }
            short s8 = 0;
            while (true) {
                short s9 = s8;
                if (s9 >= this.SABER_N / 4) {
                    return;
                }
                short s10 = (short) (3 * s9);
                short s11 = (short) (4 * s9);
                bArr[i + s10 + 0] = (byte) ((sArr[s11 + 0] & 63) | ((sArr[s11 + 1] & 3) << 6));
                bArr[i + s10 + 1] = (byte) (((sArr[s11 + 1] >> 2) & 15) | ((sArr[s11 + 2] & 15) << 4));
                bArr[i + s10 + 2] = (byte) (((sArr[s11 + 2] >> 4) & 3) | ((sArr[s11 + 3] & 63) << 2));
                s8 = (short) (s9 + 1);
            }
        }
    }

    public void BS2POLT(byte[] bArr, int i, short[] sArr) {
        if (this.SABER_ET == 3) {
            short s = 0;
            while (true) {
                short s2 = s;
                if (s2 >= this.SABER_N / 8) {
                    return;
                }
                short s3 = (short) (3 * s2);
                short s4 = (short) (8 * s2);
                sArr[s4 + 0] = (short) (bArr[i + s3 + 0] & 7);
                sArr[s4 + 1] = (short) ((bArr[(i + s3) + 0] >> 3) & 7);
                sArr[s4 + 2] = (short) (((bArr[(i + s3) + 0] >> 6) & 3) | ((bArr[(i + s3) + 1] & 1) << 2));
                sArr[s4 + 3] = (short) ((bArr[(i + s3) + 1] >> 1) & 7);
                sArr[s4 + 4] = (short) ((bArr[(i + s3) + 1] >> 4) & 7);
                sArr[s4 + 5] = (short) (((bArr[(i + s3) + 1] >> 7) & 1) | ((bArr[(i + s3) + 2] & 3) << 1));
                sArr[s4 + 6] = (short) ((bArr[(i + s3) + 2] >> 2) & 7);
                sArr[s4 + 7] = (short) ((bArr[(i + s3) + 2] >> 5) & 7);
                s = (short) (s2 + 1);
            }
        } else if (this.SABER_ET == 4) {
            short s5 = 0;
            while (true) {
                short s6 = s5;
                if (s6 >= this.SABER_N / 2) {
                    return;
                }
                short s7 = (short) (2 * s6);
                sArr[s7] = (short) (bArr[i + s6] & 15);
                sArr[s7 + 1] = (short) ((bArr[i + s6] >> 4) & 15);
                s5 = (short) (s6 + 1);
            }
        } else {
            if (this.SABER_ET != 6) {
                return;
            }
            short s8 = 0;
            while (true) {
                short s9 = s8;
                if (s9 >= this.SABER_N / 4) {
                    return;
                }
                short s10 = (short) (3 * s9);
                short s11 = (short) (4 * s9);
                sArr[s11 + 0] = (short) (bArr[i + s10 + 0] & 63);
                sArr[s11 + 1] = (short) (((bArr[(i + s10) + 0] >> 6) & 3) | ((bArr[(i + s10) + 1] & 15) << 2));
                sArr[s11 + 2] = (short) (((bArr[(i + s10) + 1] & 255) >> 4) | ((bArr[(i + s10) + 2] & 3) << 4));
                sArr[s11 + 3] = (short) ((bArr[(i + s10) + 2] & 255) >> 2);
                s8 = (short) (s9 + 1);
            }
        }
    }

    private void POLq2BS(byte[] bArr, int i, short[] sArr) {
        if (this.usingEffectiveMasking) {
            short s = 0;
            while (true) {
                short s2 = s;
                if (s2 >= this.SABER_N / 2) {
                    return;
                }
                short s3 = (short) (3 * s2);
                short s4 = (short) (2 * s2);
                bArr[i + s3 + 0] = (byte) (sArr[s4 + 0] & 255);
                bArr[i + s3 + 1] = (byte) (((sArr[s4 + 0] >> 8) & 15) | ((sArr[s4 + 1] & 15) << 4));
                bArr[i + s3 + 2] = (byte) ((sArr[s4 + 1] >> 4) & 255);
                s = (short) (s2 + 1);
            }
        } else {
            short s5 = 0;
            while (true) {
                short s6 = s5;
                if (s6 >= this.SABER_N / 8) {
                    return;
                }
                short s7 = (short) (13 * s6);
                short s8 = (short) (8 * s6);
                bArr[i + s7 + 0] = (byte) (sArr[s8 + 0] & 255);
                bArr[i + s7 + 1] = (byte) (((sArr[s8 + 0] >> 8) & 31) | ((sArr[s8 + 1] & 7) << 5));
                bArr[i + s7 + 2] = (byte) ((sArr[s8 + 1] >> 3) & 255);
                bArr[i + s7 + 3] = (byte) (((sArr[s8 + 1] >> 11) & 3) | ((sArr[s8 + 2] & 63) << 2));
                bArr[i + s7 + 4] = (byte) (((sArr[s8 + 2] >> 6) & 127) | ((sArr[s8 + 3] & 1) << 7));
                bArr[i + s7 + 5] = (byte) ((sArr[s8 + 3] >> 1) & 255);
                bArr[i + s7 + 6] = (byte) (((sArr[s8 + 3] >> 9) & 15) | ((sArr[s8 + 4] & 15) << 4));
                bArr[i + s7 + 7] = (byte) ((sArr[s8 + 4] >> 4) & 255);
                bArr[i + s7 + 8] = (byte) (((sArr[s8 + 4] >> 12) & 1) | ((sArr[s8 + 5] & 127) << 1));
                bArr[i + s7 + 9] = (byte) (((sArr[s8 + 5] >> 7) & 63) | ((sArr[s8 + 6] & 3) << 6));
                bArr[i + s7 + 10] = (byte) ((sArr[s8 + 6] >> 2) & 255);
                bArr[i + s7 + 11] = (byte) (((sArr[s8 + 6] >> 10) & 7) | ((sArr[s8 + 7] & 31) << 3));
                bArr[i + s7 + 12] = (byte) ((sArr[s8 + 7] >> 5) & 255);
                s5 = (short) (s6 + 1);
            }
        }
    }

    private void BS2POLq(byte[] bArr, int i, short[] sArr) {
        if (this.usingEffectiveMasking) {
            short s = 0;
            while (true) {
                short s2 = s;
                if (s2 >= this.SABER_N / 2) {
                    return;
                }
                short s3 = (short) (3 * s2);
                short s4 = (short) (2 * s2);
                sArr[s4 + 0] = (short) ((bArr[i + s3 + 0] & 255) | ((bArr[(i + s3) + 1] & 15) << 8));
                sArr[s4 + 1] = (short) (((bArr[(i + s3) + 1] >> 4) & 15) | ((bArr[(i + s3) + 2] & 255) << 4));
                s = (short) (s2 + 1);
            }
        } else {
            short s5 = 0;
            while (true) {
                short s6 = s5;
                if (s6 >= this.SABER_N / 8) {
                    return;
                }
                short s7 = (short) (13 * s6);
                short s8 = (short) (8 * s6);
                sArr[s8 + 0] = (short) ((bArr[i + s7 + 0] & 255) | ((bArr[(i + s7) + 1] & 31) << 8));
                sArr[s8 + 1] = (short) (((bArr[(i + s7) + 1] >> 5) & 7) | ((bArr[(i + s7) + 2] & 255) << 3) | ((bArr[(i + s7) + 3] & 3) << 11));
                sArr[s8 + 2] = (short) (((bArr[(i + s7) + 3] >> 2) & 63) | ((bArr[(i + s7) + 4] & 127) << 6));
                sArr[s8 + 3] = (short) (((bArr[(i + s7) + 4] >> 7) & 1) | ((bArr[(i + s7) + 5] & 255) << 1) | ((bArr[(i + s7) + 6] & 15) << 9));
                sArr[s8 + 4] = (short) (((bArr[(i + s7) + 6] >> 4) & 15) | ((bArr[(i + s7) + 7] & 255) << 4) | ((bArr[(i + s7) + 8] & 1) << 12));
                sArr[s8 + 5] = (short) (((bArr[(i + s7) + 8] >> 1) & 127) | ((bArr[(i + s7) + 9] & 63) << 7));
                sArr[s8 + 6] = (short) (((bArr[(i + s7) + 9] >> 6) & 3) | ((bArr[(i + s7) + 10] & 255) << 2) | ((bArr[(i + s7) + 11] & 7) << 10));
                sArr[s8 + 7] = (short) (((bArr[(i + s7) + 11] >> 3) & 31) | ((bArr[(i + s7) + 12] & 255) << 5));
                s5 = (short) (s6 + 1);
            }
        }
    }

    private void POLp2BS(byte[] bArr, int i, short[] sArr) {
        short s = 0;
        while (true) {
            short s2 = s;
            if (s2 >= this.SABER_N / 4) {
                return;
            }
            short s3 = (short) (5 * s2);
            short s4 = (short) (4 * s2);
            bArr[i + s3 + 0] = (byte) (sArr[s4 + 0] & 255);
            bArr[i + s3 + 1] = (byte) (((sArr[s4 + 0] >> 8) & 3) | ((sArr[s4 + 1] & 63) << 2));
            bArr[i + s3 + 2] = (byte) (((sArr[s4 + 1] >> 6) & 15) | ((sArr[s4 + 2] & 15) << 4));
            bArr[i + s3 + 3] = (byte) (((sArr[s4 + 2] >> 4) & 63) | ((sArr[s4 + 3] & 3) << 6));
            bArr[i + s3 + 4] = (byte) ((sArr[s4 + 3] >> 2) & 255);
            s = (short) (s2 + 1);
        }
    }

    public void BS2POLp(byte[] bArr, int i, short[] sArr) {
        short s = 0;
        while (true) {
            short s2 = s;
            if (s2 >= this.SABER_N / 4) {
                return;
            }
            short s3 = (short) (5 * s2);
            short s4 = (short) (4 * s2);
            sArr[s4 + 0] = (short) ((bArr[i + s3 + 0] & 255) | ((bArr[(i + s3) + 1] & 3) << 8));
            sArr[s4 + 1] = (short) (((bArr[(i + s3) + 1] >> 2) & 63) | ((bArr[(i + s3) + 2] & 15) << 6));
            sArr[s4 + 2] = (short) (((bArr[(i + s3) + 2] >> 4) & 15) | ((bArr[(i + s3) + 3] & 63) << 4));
            sArr[s4 + 3] = (short) (((bArr[(i + s3) + 3] >> 6) & 3) | ((bArr[(i + s3) + 4] & 255) << 2));
            s = (short) (s2 + 1);
        }
    }

    public void POLVECq2BS(byte[] bArr, short[][] sArr) {
        byte b = 0;
        while (true) {
            byte b2 = b;
            if (b2 >= this.SABER_L) {
                return;
            }
            POLq2BS(bArr, b2 * this.SABER_POLYBYTES, sArr[b2]);
            b = (byte) (b2 + 1);
        }
    }

    public void BS2POLVECq(byte[] bArr, int i, short[][] sArr) {
        byte b = 0;
        while (true) {
            byte b2 = b;
            if (b2 >= this.SABER_L) {
                return;
            }
            BS2POLq(bArr, i + (b2 * this.SABER_POLYBYTES), sArr[b2]);
            b = (byte) (b2 + 1);
        }
    }

    public void POLVECp2BS(byte[] bArr, short[][] sArr) {
        byte b = 0;
        while (true) {
            byte b2 = b;
            if (b2 >= this.SABER_L) {
                return;
            }
            POLp2BS(bArr, b2 * ((this.SABER_EP * this.SABER_N) / 8), sArr[b2]);
            b = (byte) (b2 + 1);
        }
    }

    public void BS2POLVECp(byte[] bArr, short[][] sArr) {
        byte b = 0;
        while (true) {
            byte b2 = b;
            if (b2 >= this.SABER_L) {
                return;
            }
            BS2POLp(bArr, b2 * ((this.SABER_EP * this.SABER_N) / 8), sArr[b2]);
            b = (byte) (b2 + 1);
        }
    }

    public void BS2POLmsg(byte[] bArr, short[] sArr) {
        byte b = 0;
        while (true) {
            byte b2 = b;
            if (b2 >= this.SABER_KEYBYTES) {
                return;
            }
            byte b3 = 0;
            while (true) {
                byte b4 = b3;
                if (b4 < 8) {
                    sArr[(b2 * 8) + b4] = (short) ((bArr[b2] >> b4) & 1);
                    b3 = (byte) (b4 + 1);
                }
            }
            b = (byte) (b2 + 1);
        }
    }

    public void POLmsg2BS(byte[] bArr, short[] sArr) {
        byte b = 0;
        while (true) {
            byte b2 = b;
            if (b2 >= this.SABER_KEYBYTES) {
                return;
            }
            byte b3 = 0;
            while (true) {
                byte b4 = b3;
                if (b4 < 8) {
                    bArr[b2] = (byte) (bArr[b2] | ((sArr[(b2 * 8) + b4] & 1) << b4));
                    b3 = (byte) (b4 + 1);
                }
            }
            b = (byte) (b2 + 1);
        }
    }
}
