package org.spongycastle.crypto.examples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.engines.DESedeEngine;
import org.spongycastle.crypto.generators.DESedeKeyGenerator;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/examples/DESExample.class */
public class DESExample {
    private boolean encrypt;
    private PaddedBufferedBlockCipher cipher;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private byte[] key;

    public static void main(String[] args) {
        boolean encrypt = true;
        if (args.length < 2) {
            DESExample de = new DESExample();
            System.err.println("Usage: java " + de.getClass().getName() + " infile outfile [keyfile]");
            System.exit(1);
        }
        String keyfile = "deskey.dat";
        String infile = args[0];
        String outfile = args[1];
        if (args.length > 2) {
            encrypt = false;
            keyfile = args[2];
        }
        DESExample de2 = new DESExample(infile, outfile, keyfile, encrypt);
        de2.process();
    }

    public DESExample() {
        this.encrypt = true;
        this.cipher = null;
        this.in = null;
        this.out = null;
        this.key = null;
    }

    public DESExample(String infile, String outfile, String keyfile, boolean encrypt) {
        this.encrypt = true;
        this.cipher = null;
        this.in = null;
        this.out = null;
        this.key = null;
        this.encrypt = encrypt;
        try {
            this.in = new BufferedInputStream(new FileInputStream(infile));
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found [" + infile + "]");
            System.exit(1);
        }
        try {
            this.out = new BufferedOutputStream(new FileOutputStream(outfile));
        } catch (IOException e2) {
            System.err.println("Output file not created [" + outfile + "]");
            System.exit(1);
        }
        if (encrypt) {
            SecureRandom sr = null;
            try {
                try {
                    sr = new SecureRandom();
                    sr.setSeed("www.bouncycastle.org".getBytes());
                } catch (Exception e3) {
                    System.err.println("Hmmm, no SHA1PRNG, you need the Sun implementation");
                    System.exit(1);
                }
                KeyGenerationParameters kgp = new KeyGenerationParameters(sr, 192);
                DESedeKeyGenerator kg = new DESedeKeyGenerator();
                kg.init(kgp);
                this.key = kg.generateKey();
                BufferedOutputStream keystream = new BufferedOutputStream(new FileOutputStream(keyfile));
                byte[] keyhex = Hex.encode(this.key);
                keystream.write(keyhex, 0, keyhex.length);
                keystream.flush();
                keystream.close();
                return;
            } catch (IOException e4) {
                System.err.println("Could not decryption create key file [" + keyfile + "]");
                System.exit(1);
                return;
            }
        }
        try {
            BufferedInputStream keystream2 = new BufferedInputStream(new FileInputStream(keyfile));
            int len = keystream2.available();
            byte[] keyhex2 = new byte[len];
            keystream2.read(keyhex2, 0, len);
            this.key = Hex.decode(keyhex2);
        } catch (IOException e5) {
            System.err.println("Decryption key file not found, or not valid [" + keyfile + "]");
            System.exit(1);
        }
    }

    private void process() {
        this.cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESedeEngine()));
        if (this.encrypt) {
            performEncrypt(this.key);
        } else {
            performDecrypt(this.key);
        }
        try {
            this.in.close();
            this.out.flush();
            this.out.close();
        } catch (IOException closing) {
            System.err.println("exception closing resources: " + closing.getMessage());
        }
    }

    private void performEncrypt(byte[] key) {
        this.cipher.init(true, new KeyParameter(key));
        int outBlockSize = this.cipher.getOutputSize(47);
        byte[] inblock = new byte[47];
        byte[] outblock = new byte[outBlockSize];
        while (true) {
            try {
                int inL = this.in.read(inblock, 0, 47);
                if (inL > 0) {
                    int outL = this.cipher.processBytes(inblock, 0, inL, outblock, 0);
                    if (outL > 0) {
                        byte[] rv = Hex.encode(outblock, 0, outL);
                        this.out.write(rv, 0, rv.length);
                        this.out.write(10);
                    }
                } else {
                    try {
                        break;
                    } catch (CryptoException e) {
                    }
                }
            } catch (IOException ioeread) {
                ioeread.printStackTrace();
                return;
            }
        }
        int outL2 = this.cipher.doFinal(outblock, 0);
        if (outL2 > 0) {
            byte[] rv2 = Hex.encode(outblock, 0, outL2);
            this.out.write(rv2, 0, rv2.length);
            this.out.write(10);
        }
    }

    private void performDecrypt(byte[] key) {
        this.cipher.init(false, new KeyParameter(key));
        BufferedReader br = new BufferedReader(new InputStreamReader(this.in));
        byte[] outblock = null;
        while (true) {
            try {
                String rv = br.readLine();
                if (rv != null) {
                    byte[] inblock = Hex.decode(rv);
                    outblock = new byte[this.cipher.getOutputSize(inblock.length)];
                    int outL = this.cipher.processBytes(inblock, 0, inblock.length, outblock, 0);
                    if (outL > 0) {
                        this.out.write(outblock, 0, outL);
                    }
                } else {
                    try {
                        break;
                    } catch (CryptoException e) {
                    }
                }
            } catch (IOException ioeread) {
                ioeread.printStackTrace();
                return;
            }
        }
        int outL2 = this.cipher.doFinal(outblock, 0);
        if (outL2 > 0) {
            this.out.write(outblock, 0, outL2);
        }
    }
}
