package org.apache.pdfbox.pdmodel.encryption;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/encryption/PDEncryption.class */
public class PDEncryption implements COSObjectable {
    public static final int VERSION0_UNDOCUMENTED_UNSUPPORTED = 0;
    public static final int VERSION1_40_BIT_ALGORITHM = 1;
    public static final int VERSION2_VARIABLE_LENGTH_ALGORITHM = 2;
    public static final int VERSION3_UNPUBLISHED_ALGORITHM = 3;
    public static final int VERSION4_SECURITY_HANDLER = 4;
    public static final String DEFAULT_NAME = "Standard";
    public static final int DEFAULT_LENGTH = 40;
    public static final int DEFAULT_VERSION = 0;
    private final COSDictionary dictionary;
    private SecurityHandler securityHandler;

    public PDEncryption() {
        this.dictionary = new COSDictionary();
    }

    public PDEncryption(COSDictionary dictionary) {
        this.dictionary = dictionary;
        this.securityHandler = SecurityHandlerFactory.INSTANCE.newSecurityHandlerForFilter(getFilter());
    }

    public SecurityHandler getSecurityHandler() throws IOException {
        if (this.securityHandler == null) {
            throw new IOException("No security handler for filter " + getFilter());
        }
        return this.securityHandler;
    }

    public void setSecurityHandler(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
    }

    public boolean hasSecurityHandler() {
        return this.securityHandler == null;
    }

    @Deprecated
    public COSDictionary getCOSDictionary() {
        return this.dictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public void setFilter(String filter) {
        this.dictionary.setItem(COSName.FILTER, (COSBase) COSName.getPDFName(filter));
    }

    public final String getFilter() {
        return this.dictionary.getNameAsString(COSName.FILTER);
    }

    public String getSubFilter() {
        return this.dictionary.getNameAsString(COSName.SUB_FILTER);
    }

    public void setSubFilter(String subfilter) {
        this.dictionary.setName(COSName.SUB_FILTER, subfilter);
    }

    public void setVersion(int version) {
        this.dictionary.setInt(COSName.V, version);
    }

    public int getVersion() {
        return this.dictionary.getInt(COSName.V, 0);
    }

    public void setLength(int length) {
        this.dictionary.setInt(COSName.LENGTH, length);
    }

    public int getLength() {
        return this.dictionary.getInt(COSName.LENGTH, 40);
    }

    public void setRevision(int revision) {
        this.dictionary.setInt(COSName.R, revision);
    }

    public int getRevision() {
        return this.dictionary.getInt(COSName.R, 0);
    }

    public void setOwnerKey(byte[] o) throws IOException {
        this.dictionary.setItem(COSName.O, (COSBase) new COSString(o));
    }

    public byte[] getOwnerKey() throws IOException {
        byte[] o = null;
        COSString owner = (COSString) this.dictionary.getDictionaryObject(COSName.O);
        if (owner != null) {
            o = owner.getBytes();
        }
        return o;
    }

    public void setUserKey(byte[] u) throws IOException {
        this.dictionary.setItem(COSName.U, (COSBase) new COSString(u));
    }

    public byte[] getUserKey() throws IOException {
        byte[] u = null;
        COSString user = (COSString) this.dictionary.getDictionaryObject(COSName.U);
        if (user != null) {
            u = user.getBytes();
        }
        return u;
    }

    public void setOwnerEncryptionKey(byte[] oe) throws IOException {
        this.dictionary.setItem(COSName.OE, (COSBase) new COSString(oe));
    }

    public byte[] getOwnerEncryptionKey() throws IOException {
        byte[] oe = null;
        COSString ownerEncryptionKey = (COSString) this.dictionary.getDictionaryObject(COSName.OE);
        if (ownerEncryptionKey != null) {
            oe = ownerEncryptionKey.getBytes();
        }
        return oe;
    }

    public void setUserEncryptionKey(byte[] ue) throws IOException {
        this.dictionary.setItem(COSName.UE, (COSBase) new COSString(ue));
    }

    public byte[] getUserEncryptionKey() throws IOException {
        byte[] ue = null;
        COSString userEncryptionKey = (COSString) this.dictionary.getDictionaryObject(COSName.UE);
        if (userEncryptionKey != null) {
            ue = userEncryptionKey.getBytes();
        }
        return ue;
    }

    public void setPermissions(int permissions) {
        this.dictionary.setInt(COSName.P, permissions);
    }

    public int getPermissions() {
        return this.dictionary.getInt(COSName.P, 0);
    }

    public boolean isEncryptMetaData() {
        boolean encryptMetaData = true;
        COSBase value = this.dictionary.getDictionaryObject(COSName.ENCRYPT_META_DATA);
        if (value instanceof COSBoolean) {
            encryptMetaData = ((COSBoolean) value).getValue();
        }
        return encryptMetaData;
    }

    public void setRecipients(byte[][] recipients) throws IOException {
        COSArray array = new COSArray();
        for (byte[] recipient : recipients) {
            COSString recip = new COSString(recipient);
            array.add((COSBase) recip);
        }
        this.dictionary.setItem(COSName.RECIPIENTS, (COSBase) array);
        array.setDirect(true);
    }

    public int getRecipientsLength() {
        COSArray array = (COSArray) this.dictionary.getItem(COSName.RECIPIENTS);
        return array.size();
    }

    public COSString getRecipientStringAt(int i) {
        COSArray array = (COSArray) this.dictionary.getItem(COSName.RECIPIENTS);
        return (COSString) array.get(i);
    }

    public PDCryptFilterDictionary getStdCryptFilterDictionary() {
        return getCryptFilterDictionary(COSName.STD_CF);
    }

    public PDCryptFilterDictionary getDefaultCryptFilterDictionary() {
        return getCryptFilterDictionary(COSName.DEFAULT_CRYPT_FILTER);
    }

    public PDCryptFilterDictionary getCryptFilterDictionary(COSName cryptFilterName) {
        COSBase base = this.dictionary.getDictionaryObject(COSName.CF);
        if (base instanceof COSDictionary) {
            COSBase base2 = ((COSDictionary) base).getDictionaryObject(cryptFilterName);
            if (base2 instanceof COSDictionary) {
                return new PDCryptFilterDictionary((COSDictionary) base2);
            }
            return null;
        }
        return null;
    }

    public void setCryptFilterDictionary(COSName cryptFilterName, PDCryptFilterDictionary cryptFilterDictionary) {
        COSDictionary cfDictionary = this.dictionary.getCOSDictionary(COSName.CF);
        if (cfDictionary == null) {
            cfDictionary = new COSDictionary();
            this.dictionary.setItem(COSName.CF, (COSBase) cfDictionary);
        }
        cfDictionary.setDirect(true);
        cfDictionary.setItem(cryptFilterName, (COSBase) cryptFilterDictionary.getCOSObject());
    }

    public void setStdCryptFilterDictionary(PDCryptFilterDictionary cryptFilterDictionary) {
        cryptFilterDictionary.getCOSObject().setDirect(true);
        setCryptFilterDictionary(COSName.STD_CF, cryptFilterDictionary);
    }

    public void setDefaultCryptFilterDictionary(PDCryptFilterDictionary defaultFilterDictionary) {
        defaultFilterDictionary.getCOSObject().setDirect(true);
        setCryptFilterDictionary(COSName.DEFAULT_CRYPT_FILTER, defaultFilterDictionary);
    }

    public COSName getStreamFilterName() {
        COSName stmF = (COSName) this.dictionary.getDictionaryObject(COSName.STM_F);
        if (stmF == null) {
            stmF = COSName.IDENTITY;
        }
        return stmF;
    }

    public void setStreamFilterName(COSName streamFilterName) {
        this.dictionary.setItem(COSName.STM_F, (COSBase) streamFilterName);
    }

    public COSName getStringFilterName() {
        COSName strF = (COSName) this.dictionary.getDictionaryObject(COSName.STR_F);
        if (strF == null) {
            strF = COSName.IDENTITY;
        }
        return strF;
    }

    public void setStringFilterName(COSName stringFilterName) {
        this.dictionary.setItem(COSName.STR_F, (COSBase) stringFilterName);
    }

    public void setPerms(byte[] perms) throws IOException {
        this.dictionary.setItem(COSName.PERMS, (COSBase) new COSString(perms));
    }

    public byte[] getPerms() throws IOException {
        byte[] perms = null;
        COSString permsCosString = (COSString) this.dictionary.getDictionaryObject(COSName.PERMS);
        if (permsCosString != null) {
            perms = permsCosString.getBytes();
        }
        return perms;
    }

    public void removeV45filters() {
        this.dictionary.setItem(COSName.CF, (COSBase) null);
        this.dictionary.setItem(COSName.STM_F, (COSBase) null);
        this.dictionary.setItem(COSName.STR_F, (COSBase) null);
    }
}
