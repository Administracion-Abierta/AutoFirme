package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.pdf.crypto.ARCFOUREncryption;
import com.aowagie.text.pdf.crypto.IVGenerator;
import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.platform.win32.Winspool;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfEncryption.class */
class PdfEncryption {
    private static final int STANDARD_ENCRYPTION_40 = 2;
    private static final int STANDARD_ENCRYPTION_128 = 3;
    public static final int AES_256_V3 = 6;
    private static final int AES_128 = 4;
    private byte[] key;
    private int keySize;
    private byte[] mkey;
    private final byte[] extra;
    private MessageDigest md5;
    private byte[] ownerKey;
    byte[] userKey;
    private PdfPublicKeySecurityHandler publicKeyHandler;
    int permissions;
    private byte[] documentID;
    private int revision;
    private final ARCFOUREncryption arcfour;
    private int keyLength;
    byte[] oeKey;
    byte[] ueKey;
    byte[] perms;
    private boolean encryptMetadata;
    private boolean embeddedFilesOnly;
    private int cryptoMode;
    private static final byte[] pad = {40, -65, 78, 94, 78, 117, -118, 65, 100, 0, 78, 86, -1, -6, 1, 8, 46, 46, 0, -74, -48, 104, 62, -128, 47, 12, -87, -2, 100, 83, 105, 122};
    private static final byte[] salt = {115, 65, 108, 84};
    private static final byte[] metadataPad = {-1, -1, -1, -1};
    private static long seq = System.currentTimeMillis();

    public PdfEncryption() {
        this.extra = new byte[5];
        this.ownerKey = new byte[32];
        this.userKey = new byte[32];
        this.publicKeyHandler = null;
        this.arcfour = new ARCFOUREncryption();
        try {
            this.md5 = MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
            this.publicKeyHandler = new PdfPublicKeySecurityHandler();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public PdfEncryption(PdfEncryption enc) {
        this();
        if (enc.mkey != null) {
            this.mkey = (byte[]) enc.mkey.clone();
        }
        this.ownerKey = (byte[]) enc.ownerKey.clone();
        this.userKey = (byte[]) enc.userKey.clone();
        this.permissions = enc.permissions;
        if (enc.documentID != null) {
            this.documentID = (byte[]) enc.documentID.clone();
        }
        this.revision = enc.revision;
        this.keyLength = enc.keyLength;
        this.encryptMetadata = enc.encryptMetadata;
        this.embeddedFilesOnly = enc.embeddedFilesOnly;
        this.publicKeyHandler = enc.publicKeyHandler;
        if (enc.ueKey != null) {
            this.ueKey = (byte[]) enc.ueKey.clone();
        }
        if (enc.oeKey != null) {
            this.oeKey = (byte[]) enc.oeKey.clone();
        }
        if (enc.perms != null) {
            this.perms = (byte[]) enc.perms.clone();
        }
        if (enc.key != null) {
            this.key = (byte[]) enc.key.clone();
        }
        this.keySize = enc.keySize;
    }

    public void setCryptoMode(int mode, int kl) {
        this.cryptoMode = mode;
        this.encryptMetadata = (mode & 8) == 0;
        this.embeddedFilesOnly = (mode & 24) != 0;
        switch (mode & 7) {
            case 0:
                this.encryptMetadata = true;
                this.embeddedFilesOnly = false;
                this.keyLength = 40;
                this.revision = 2;
                return;
            case 1:
                this.embeddedFilesOnly = false;
                if (kl > 0) {
                    this.keyLength = kl;
                } else {
                    this.keyLength = 128;
                }
                this.revision = 3;
                return;
            case 2:
                this.keyLength = 128;
                this.revision = 4;
                return;
            case 3:
            default:
                throw new IllegalArgumentException("No valid encryption mode");
            case 4:
                this.keyLength = 256;
                this.keySize = 32;
                this.revision = 6;
                return;
        }
    }

    public int getCryptoMode() {
        return this.cryptoMode;
    }

    public boolean isMetadataEncrypted() {
        return this.encryptMetadata;
    }

    public boolean isEmbeddedFilesOnly() {
        return this.embeddedFilesOnly;
    }

    private byte[] padPassword(byte[] userPassword) {
        byte[] userPad = new byte[32];
        if (userPassword == null) {
            System.arraycopy(pad, 0, userPad, 0, 32);
        } else {
            System.arraycopy(userPassword, 0, userPad, 0, Math.min(userPassword.length, 32));
            if (userPassword.length < 32) {
                System.arraycopy(pad, 0, userPad, userPassword.length, 32 - userPassword.length);
            }
        }
        return userPad;
    }

    private byte[] computeOwnerKey(byte[] userPad, byte[] ownerPad) {
        byte[] ownerKey = new byte[32];
        byte[] digest = this.md5.digest(ownerPad);
        if (this.revision == 3 || this.revision == 4) {
            byte[] mkey = new byte[this.keyLength / 8];
            for (int k = 0; k < 50; k++) {
                System.arraycopy(this.md5.digest(digest), 0, digest, 0, mkey.length);
            }
            System.arraycopy(userPad, 0, ownerKey, 0, 32);
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < mkey.length; j++) {
                    mkey[j] = (byte) (digest[j] ^ i);
                }
                this.arcfour.prepareARCFOURKey(mkey);
                this.arcfour.encryptARCFOUR(ownerKey);
            }
        } else {
            this.arcfour.prepareARCFOURKey(digest, 0, 5);
            this.arcfour.encryptARCFOUR(userPad, ownerKey);
        }
        return ownerKey;
    }

    private void setupGlobalEncryptionKey(byte[] documentID, byte[] userPad, byte[] ownerKey, int permissions) {
        this.documentID = documentID;
        this.ownerKey = ownerKey;
        this.permissions = permissions;
        this.mkey = new byte[this.keyLength / 8];
        this.md5.reset();
        this.md5.update(userPad);
        this.md5.update(ownerKey);
        byte[] ext = {(byte) permissions, (byte) (permissions >> 8), (byte) (permissions >> 16), (byte) (permissions >> 24)};
        this.md5.update(ext, 0, 4);
        if (documentID != null) {
            this.md5.update(documentID);
        }
        if (!this.encryptMetadata) {
            this.md5.update(metadataPad);
        }
        byte[] digest = new byte[this.mkey.length];
        System.arraycopy(this.md5.digest(), 0, digest, 0, this.mkey.length);
        if (this.revision == 3 || this.revision == 4) {
            for (int k = 0; k < 50; k++) {
                System.arraycopy(this.md5.digest(digest), 0, digest, 0, this.mkey.length);
            }
        }
        System.arraycopy(digest, 0, this.mkey, 0, this.mkey.length);
    }

    private void setupUserKey() {
        if (this.revision == 3 || this.revision == 4) {
            this.md5.update(pad);
            byte[] digest = this.md5.digest(this.documentID);
            System.arraycopy(digest, 0, this.userKey, 0, 16);
            for (int k = 16; k < 32; k++) {
                this.userKey[k] = 0;
            }
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < this.mkey.length; j++) {
                    digest[j] = (byte) (this.mkey[j] ^ i);
                }
                this.arcfour.prepareARCFOURKey(digest, 0, this.mkey.length);
                this.arcfour.encryptARCFOUR(this.userKey, 0, 16);
            }
            return;
        }
        this.arcfour.prepareARCFOURKey(this.mkey);
        this.arcfour.encryptARCFOUR(pad, this.userKey);
    }

    public void setupAllKeys(byte[] userPassword, byte[] ownerPassword, int permissions) {
        if (ownerPassword == null || ownerPassword.length == 0) {
            ownerPassword = this.md5.digest(createDocumentId());
        }
        int permissions2 = (permissions | ((this.revision == 3 || this.revision == 4 || this.revision == 6) ? -3904 : -64)) & (-4);
        this.permissions = permissions2;
        this.documentID = createDocumentId();
        if (this.revision < 6) {
            byte[] userPad = padPassword(userPassword);
            byte[] ownerPad = padPassword(ownerPassword);
            this.ownerKey = computeOwnerKey(userPad, ownerPad);
            setupByUserPad(this.documentID, userPad, this.ownerKey, permissions2);
            return;
        }
        try {
            this.key = IVGenerator.getIV(32);
            this.keySize = 32;
            computeUAndUeAlg8(userPassword);
            computeOAndOeAlg9(ownerPassword);
            computePermsAlg10(permissions2);
        } catch (GeneralSecurityException e) {
            throw new ExceptionConverter(e);
        }
    }

    public static byte[] createDocumentId() {
        try {
            MessageDigest md5 = MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
            long time = System.currentTimeMillis();
            long mem = Runtime.getRuntime().freeMemory();
            StringBuilder sbAppend = new StringBuilder().append(time).append(Marker.ANY_NON_NULL_MARKER).append(mem).append(Marker.ANY_NON_NULL_MARKER);
            long j = seq;
            seq = j + 1;
            String s = sbAppend.append(j).toString();
            return md5.digest(s.getBytes());
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void setupByUserPassword(byte[] documentID, byte[] userPassword, byte[] ownerKey, int permissions) {
        setupByUserPad(documentID, padPassword(userPassword), ownerKey, permissions);
    }

    public void setupByUserPassword(byte[] documentID, byte[] userPassword, byte[] uValue, byte[] ueValue, byte[] oValue, byte[] oeValue, int permissions) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        byte[] hashAlg2B = hashAlg2B(userPassword, Arrays.copyOfRange(uValue, 40, 48), null);
        cipher.init(2, new SecretKeySpec(hashAlg2B, "AES"), new IvParameterSpec(new byte[16]));
        this.key = cipher.update(ueValue, 0, ueValue.length);
        this.ownerKey = oValue;
        this.userKey = uValue;
        this.documentID = documentID;
        this.permissions = permissions;
    }

    private void setupByUserPad(byte[] documentID, byte[] userPad, byte[] ownerKey, int permissions) {
        setupGlobalEncryptionKey(documentID, userPad, ownerKey, permissions);
        setupUserKey();
    }

    public void setupByOwnerPassword(byte[] documentID, byte[] ownerPassword, byte[] userKey, byte[] ownerKey, int permissions) {
        setupByOwnerPad(documentID, padPassword(ownerPassword), userKey, ownerKey, permissions);
    }

    public void setupByOwnerPassword(byte[] documentID, byte[] ownerPassword, byte[] uValue, byte[] ueValue, byte[] oValue, byte[] oeValue, int permissions) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        byte[] hashAlg2B = hashAlg2B(ownerPassword, Arrays.copyOfRange(oValue, 40, 48), uValue);
        cipher.init(2, new SecretKeySpec(hashAlg2B, "AES"), new IvParameterSpec(new byte[16]));
        this.key = cipher.update(oeValue, 0, oeValue.length);
        this.ownerKey = oValue;
        this.userKey = uValue;
        this.documentID = documentID;
        this.permissions = permissions;
    }

    private void setupByOwnerPad(byte[] documentID, byte[] ownerPad, byte[] userKey, byte[] ownerKey, int permissions) {
        byte[] userPad = computeOwnerKey(ownerKey, ownerPad);
        setupGlobalEncryptionKey(documentID, userPad, ownerKey, permissions);
        setupUserKey();
    }

    public boolean decryptAndCheckPerms(byte[] permsValue) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(2, new SecretKeySpec(this.key, "AES"), new IvParameterSpec(new byte[16]));
        byte[] decPerms = cipher.update(permsValue, 0, permsValue.length);
        this.permissions = (decPerms[0] & 255) | ((decPerms[1] & 255) << 8) | ((decPerms[2] & 255) << 16) | ((decPerms[2] & 255) << 24);
        this.encryptMetadata = decPerms[8] == 84;
        return decPerms[9] == 97 && decPerms[10] == 100 && decPerms[11] == 98;
    }

    public void setupByEncryptionKey(byte[] key, int keylength) {
        this.mkey = new byte[keylength / 8];
        System.arraycopy(key, 0, this.mkey, 0, this.mkey.length);
    }

    public void setHashKey(int number, int generation) {
        if (this.revision >= 6) {
            return;
        }
        this.md5.reset();
        this.extra[0] = (byte) number;
        this.extra[1] = (byte) (number >> 8);
        this.extra[2] = (byte) (number >> 16);
        this.extra[3] = (byte) generation;
        this.extra[4] = (byte) (generation >> 8);
        this.md5.update(this.mkey);
        this.md5.update(this.extra);
        if (this.revision == 4) {
            this.md5.update(salt);
        }
        this.key = this.md5.digest();
        this.keySize = this.mkey.length + 5;
        if (this.keySize > 16) {
            this.keySize = 16;
        }
    }

    public static PdfObject createInfoId(byte[] id) {
        ByteBuffer buf = new ByteBuffer(90);
        buf.append('[').append('<');
        for (int k = 0; k < 16; k++) {
            buf.appendHex(id[k]);
        }
        buf.append('>').append('<');
        byte[] id2 = createDocumentId();
        for (int k2 = 0; k2 < 16; k2++) {
            buf.appendHex(id2[k2]);
        }
        buf.append('>').append(']');
        return new PdfLiteral(buf.toByteArray());
    }

    public PdfDictionary getEncryptionDictionary() {
        PdfDictionary dic = new PdfDictionary();
        if (this.publicKeyHandler.getRecipientsSize() > 0) {
            dic.put(PdfName.FILTER, PdfName.PUBSEC);
            dic.put(PdfName.R, new PdfNumber(this.revision));
            try {
                PdfArray recipients = this.publicKeyHandler.getEncodedRecipients();
                if (this.revision == 2) {
                    dic.put(PdfName.V, new PdfNumber(1));
                    dic.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S4);
                    dic.put(PdfName.RECIPIENTS, recipients);
                } else if (this.revision == 3 && this.encryptMetadata) {
                    dic.put(PdfName.V, new PdfNumber(2));
                    dic.put(PdfName.LENGTH, new PdfNumber(128));
                    dic.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S4);
                    dic.put(PdfName.RECIPIENTS, recipients);
                } else if (this.revision == 6) {
                    if (!this.encryptMetadata) {
                        dic.put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
                    }
                    dic.put(PdfName.V, new PdfNumber(5));
                    dic.put(PdfName.OE, new PdfLiteral(PdfContentByte.escapeString(this.oeKey)));
                    dic.put(PdfName.UE, new PdfLiteral(PdfContentByte.escapeString(this.ueKey)));
                    dic.put(PdfName.PERMS, new PdfLiteral(PdfContentByte.escapeString(this.perms)));
                    dic.put(PdfName.LENGTH, new PdfNumber(256));
                    PdfDictionary stdcf = new PdfDictionary();
                    stdcf.put(PdfName.LENGTH, new PdfNumber(32));
                    if (this.embeddedFilesOnly) {
                        stdcf.put(PdfName.AUTHEVENT, PdfName.EFOPEN);
                        dic.put(PdfName.EFF, PdfName.STDCF);
                        dic.put(PdfName.STRF, PdfName.IDENTITY);
                        dic.put(PdfName.STMF, PdfName.IDENTITY);
                    } else {
                        stdcf.put(PdfName.AUTHEVENT, PdfName.DOCOPEN);
                        dic.put(PdfName.STRF, PdfName.STDCF);
                        dic.put(PdfName.STMF, PdfName.STDCF);
                    }
                    stdcf.put(PdfName.CFM, PdfName.AESV3);
                    PdfDictionary cf = new PdfDictionary();
                    cf.put(PdfName.STDCF, stdcf);
                    dic.put(PdfName.CF, cf);
                } else {
                    dic.put(PdfName.R, new PdfNumber(4));
                    dic.put(PdfName.V, new PdfNumber(4));
                    dic.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S5);
                    PdfDictionary stdcf2 = new PdfDictionary();
                    stdcf2.put(PdfName.RECIPIENTS, recipients);
                    if (!this.encryptMetadata) {
                        stdcf2.put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
                    }
                    if (this.revision == 4) {
                        stdcf2.put(PdfName.CFM, PdfName.AESV2);
                    } else {
                        stdcf2.put(PdfName.CFM, PdfName.V2);
                    }
                    PdfDictionary cf2 = new PdfDictionary();
                    cf2.put(PdfName.DEFAULTCRYPTFILTER, stdcf2);
                    dic.put(PdfName.CF, cf2);
                    if (this.embeddedFilesOnly) {
                        dic.put(PdfName.EFF, PdfName.DEFAULTCRYPTFILTER);
                        dic.put(PdfName.STRF, PdfName.IDENTITY);
                        dic.put(PdfName.STMF, PdfName.IDENTITY);
                    } else {
                        dic.put(PdfName.STRF, PdfName.DEFAULTCRYPTFILTER);
                        dic.put(PdfName.STMF, PdfName.DEFAULTCRYPTFILTER);
                    }
                }
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-1");
                    md.update(this.publicKeyHandler.getSeed());
                    for (int i = 0; i < this.publicKeyHandler.getRecipientsSize(); i++) {
                        byte[] encodedRecipient = this.publicKeyHandler.getEncodedRecipient(i);
                        md.update(encodedRecipient);
                    }
                    if (!this.encryptMetadata) {
                        md.update(new byte[]{-1, -1, -1, -1});
                    }
                    byte[] mdResult = md.digest();
                    setupByEncryptionKey(mdResult, this.keyLength);
                } catch (Exception f) {
                    throw new ExceptionConverter(f);
                }
            } catch (Exception f2) {
                throw new ExceptionConverter(f2);
            }
        } else {
            dic.put(PdfName.FILTER, PdfName.STANDARD);
            dic.put(PdfName.O, new PdfLiteral(PdfContentByte.escapeString(this.ownerKey)));
            dic.put(PdfName.U, new PdfLiteral(PdfContentByte.escapeString(this.userKey)));
            dic.put(PdfName.P, new PdfNumber(this.permissions));
            dic.put(PdfName.R, new PdfNumber(this.revision));
            if (this.revision == 2) {
                dic.put(PdfName.V, new PdfNumber(1));
            } else if (this.revision == 3 && this.encryptMetadata) {
                dic.put(PdfName.V, new PdfNumber(2));
                dic.put(PdfName.LENGTH, new PdfNumber(128));
            } else {
                if (!this.encryptMetadata) {
                    dic.put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
                }
                dic.put(PdfName.R, new PdfNumber(4));
                dic.put(PdfName.V, new PdfNumber(4));
                dic.put(PdfName.LENGTH, new PdfNumber(128));
                PdfDictionary stdcf3 = new PdfDictionary();
                stdcf3.put(PdfName.LENGTH, new PdfNumber(16));
                if (this.embeddedFilesOnly) {
                    stdcf3.put(PdfName.AUTHEVENT, PdfName.EFOPEN);
                    dic.put(PdfName.EFF, PdfName.STDCF);
                    dic.put(PdfName.STRF, PdfName.IDENTITY);
                    dic.put(PdfName.STMF, PdfName.IDENTITY);
                } else {
                    stdcf3.put(PdfName.AUTHEVENT, PdfName.DOCOPEN);
                    dic.put(PdfName.STRF, PdfName.STDCF);
                    dic.put(PdfName.STMF, PdfName.STDCF);
                }
                if (this.revision == 4) {
                    stdcf3.put(PdfName.CFM, PdfName.AESV2);
                } else {
                    stdcf3.put(PdfName.CFM, PdfName.V2);
                }
                PdfDictionary cf3 = new PdfDictionary();
                cf3.put(PdfName.STDCF, stdcf3);
                dic.put(PdfName.CF, cf3);
            }
        }
        return dic;
    }

    public PdfObject getFileID() {
        return createInfoId(this.documentID);
    }

    public OutputStreamEncryption getEncryptionStream(OutputStream os) {
        return new OutputStreamEncryption(os, this.key, 0, this.keySize, this.revision);
    }

    int calculateStreamSize(int n) {
        if (this.revision == 4 || this.revision == 6) {
            return (n & 2147483632) + 32;
        }
        return n;
    }

    byte[] encryptByteArray(byte[] b) {
        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            OutputStreamEncryption os2 = getEncryptionStream(ba);
            os2.write(b);
            os2.finish();
            return ba.toByteArray();
        } catch (IOException ex) {
            throw new ExceptionConverter(ex);
        }
    }

    public StandardDecryption getDecryptor() {
        return new StandardDecryption(this.key, 0, this.keySize, this.revision);
    }

    public byte[] decryptByteArray(byte[] b) {
        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            StandardDecryption dec = getDecryptor();
            byte[] b2 = dec.update(b, 0, b.length);
            if (b2 != null) {
                ba.write(b2);
            }
            byte[] b3 = dec.finish();
            if (b3 != null) {
                ba.write(b3);
            }
            return ba.toByteArray();
        } catch (IOException ex) {
            throw new ExceptionConverter(ex);
        }
    }

    public void addRecipient(Certificate cert, int permission) {
        this.documentID = createDocumentId();
        this.publicKeyHandler.addRecipient(new PdfPublicKeyRecipient(cert, permission));
    }

    void computeUAndUeAlg8(byte[] userPassword) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        if (userPassword == null) {
            userPassword = new byte[0];
        } else if (userPassword.length > 127) {
            userPassword = Arrays.copyOf(userPassword, 127);
        }
        byte[] userSalts = IVGenerator.getIV(16);
        this.userKey = new byte[48];
        System.arraycopy(userSalts, 0, this.userKey, 32, 16);
        byte[] hashAlg2B = hashAlg2B(userPassword, Arrays.copyOf(userSalts, 8), null);
        System.arraycopy(hashAlg2B, 0, this.userKey, 0, 32);
        byte[] hashAlg2B2 = hashAlg2B(userPassword, Arrays.copyOfRange(userSalts, 8, 16), null);
        cipher.init(1, new SecretKeySpec(hashAlg2B2, "AES"), new IvParameterSpec(new byte[16]));
        this.ueKey = cipher.update(this.key, 0, this.keySize);
    }

    void computeOAndOeAlg9(byte[] ownerPassword) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        if (ownerPassword == null) {
            ownerPassword = new byte[0];
        } else if (ownerPassword.length > 127) {
            ownerPassword = Arrays.copyOf(ownerPassword, 127);
        }
        byte[] ownerSalts = IVGenerator.getIV(16);
        this.ownerKey = new byte[48];
        System.arraycopy(ownerSalts, 0, this.ownerKey, 32, 16);
        byte[] hashAlg2B = hashAlg2B(ownerPassword, Arrays.copyOf(ownerSalts, 8), this.userKey);
        System.arraycopy(hashAlg2B, 0, this.ownerKey, 0, 32);
        byte[] hashAlg2B2 = hashAlg2B(ownerPassword, Arrays.copyOfRange(ownerSalts, 8, 16), this.userKey);
        cipher.init(1, new SecretKeySpec(hashAlg2B2, "AES"), new IvParameterSpec(new byte[16]));
        this.oeKey = cipher.update(this.key, 0, this.keySize);
    }

    void computePermsAlg10(int permissions) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        byte[] rawPerms = new byte[16];
        rawPerms[0] = (byte) (permissions & 255);
        rawPerms[1] = (byte) ((permissions & 65280) >> 8);
        rawPerms[2] = (byte) ((permissions & Winspool.PRINTER_ENUM_ICONMASK) >> 16);
        rawPerms[3] = (byte) ((permissions & Ddeml.MF_MASK) >> 24);
        rawPerms[4] = -1;
        rawPerms[5] = -1;
        rawPerms[6] = -1;
        rawPerms[7] = -1;
        rawPerms[8] = (byte) (this.encryptMetadata ? 84 : 70);
        rawPerms[9] = 97;
        rawPerms[10] = 100;
        rawPerms[11] = 98;
        System.arraycopy(IVGenerator.getIV(4), 0, rawPerms, 12, 4);
        cipher.init(1, new SecretKeySpec(this.key, "AES"), new IvParameterSpec(new byte[16]));
        this.perms = cipher.update(rawPerms, 0, 16);
    }

    static byte[] hashAlg2B(byte[] input, byte[] salt2, byte[] userKey) throws GeneralSecurityException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        MessageDigest sha384 = MessageDigest.getInstance("SHA-384");
        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        if (userKey == null) {
            userKey = new byte[0];
        }
        sha256.update(input);
        sha256.update(salt2);
        sha256.update(userKey);
        byte[] k = sha256.digest();
        int round = 0;
        int lastEByte = 0;
        while (true) {
            if (round < 64 || lastEByte > round - 32) {
                int singleSequenceSize = input.length + k.length + userKey.length;
                byte[] k1 = new byte[singleSequenceSize * 64];
                System.arraycopy(input, 0, k1, 0, input.length);
                System.arraycopy(k, 0, k1, input.length, k.length);
                System.arraycopy(userKey, 0, k1, input.length + k.length, userKey.length);
                for (int i = 1; i < 64; i++) {
                    System.arraycopy(k1, 0, k1, singleSequenceSize * i, singleSequenceSize);
                }
                cipher.init(1, new SecretKeySpec(Arrays.copyOf(k, 16), "AES"), new IvParameterSpec(Arrays.copyOfRange(k, 16, 32)));
                byte[] e = cipher.update(k1, 0, k1.length);
                lastEByte = e[e.length - 1] & 255;
                switch (new BigInteger(1, Arrays.copyOf(e, 16)).remainder(BigInteger.valueOf(3L)).intValue()) {
                    case 0:
                        k = sha256.digest(e);
                        break;
                    case 1:
                        k = sha384.digest(e);
                        break;
                    case 2:
                        k = sha512.digest(e);
                        break;
                }
                round++;
            } else {
                return Arrays.copyOf(k, 32);
            }
        }
    }
}
