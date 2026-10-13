package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/PDSeedValueCertificate.class */
public class PDSeedValueCertificate implements COSObjectable {
    public static final int FLAG_SUBJECT = 1;
    public static final int FLAG_ISSUER = 2;
    public static final int FLAG_OID = 4;
    public static final int FLAG_SUBJECT_DN = 8;
    public static final int FLAG_KEY_USAGE = 32;
    public static final int FLAG_URL = 64;
    private final COSDictionary dictionary;

    public PDSeedValueCertificate() {
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.TYPE, (COSBase) COSName.SV_CERT);
        this.dictionary.setDirect(true);
    }

    public PDSeedValueCertificate(COSDictionary dict) {
        this.dictionary = dict;
        this.dictionary.setDirect(true);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public boolean isSubjectRequired() {
        return this.dictionary.getFlag(COSName.FF, 1);
    }

    public void setSubjectRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 1, flag);
    }

    public boolean isIssuerRequired() {
        return this.dictionary.getFlag(COSName.FF, 2);
    }

    public void setIssuerRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 2, flag);
    }

    public boolean isOIDRequired() {
        return this.dictionary.getFlag(COSName.FF, 4);
    }

    public void setOIDRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 4, flag);
    }

    public boolean isSubjectDNRequired() {
        return this.dictionary.getFlag(COSName.FF, 8);
    }

    public void setSubjectDNRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 8, flag);
    }

    public boolean isKeyUsageRequired() {
        return this.dictionary.getFlag(COSName.FF, 32);
    }

    public void setKeyUsageRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 32, flag);
    }

    public boolean isURLRequired() {
        return this.dictionary.getFlag(COSName.FF, 64);
    }

    public void setURLRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 64, flag);
    }

    public List<byte[]> getSubject() {
        COSArray array = this.dictionary.getCOSArray(COSName.SUBJECT);
        if (array != null) {
            return getListOfByteArraysFromCOSArray(array);
        }
        return null;
    }

    public void setSubject(List<byte[]> subjects) {
        this.dictionary.setItem(COSName.SUBJECT, (COSBase) convertListOfByteArraysToCOSArray(subjects));
    }

    public void addSubject(byte[] subject) {
        COSArray array = this.dictionary.getCOSArray(COSName.SUBJECT);
        if (array == null) {
            array = new COSArray();
        }
        array.add((COSBase) new COSString(subject));
        this.dictionary.setItem(COSName.SUBJECT, (COSBase) array);
    }

    public void removeSubject(byte[] subject) {
        COSArray array = this.dictionary.getCOSArray(COSName.SUBJECT);
        if (array != null) {
            array.remove(new COSString(subject));
        }
    }

    public List<Map<String, String>> getSubjectDN() {
        COSArray cosArray = this.dictionary.getCOSArray(COSName.SUBJECT_DN);
        if (cosArray != null) {
            List<? extends COSBase> subjectDNList = cosArray.toList();
            List<Map<String, String>> result = new LinkedList<>();
            for (COSBase subjectDNItem : subjectDNList) {
                if (subjectDNItem instanceof COSDictionary) {
                    COSDictionary subjectDNItemDict = (COSDictionary) subjectDNItem;
                    Map<String, String> subjectDNMap = new HashMap<>();
                    for (COSName key : subjectDNItemDict.keySet()) {
                        subjectDNMap.put(key.getName(), subjectDNItemDict.getString(key));
                    }
                    result.add(subjectDNMap);
                }
            }
            return result;
        }
        return null;
    }

    public void setSubjectDN(List<Map<String, String>> subjectDN) {
        List<COSDictionary> subjectDNDict = new LinkedList<>();
        for (Map<String, String> subjectDNItem : subjectDN) {
            COSDictionary dict = new COSDictionary();
            for (Map.Entry<String, String> entry : subjectDNItem.entrySet()) {
                dict.setItem(entry.getKey(), (COSBase) new COSString(entry.getValue()));
            }
            subjectDNDict.add(dict);
        }
        this.dictionary.setItem(COSName.SUBJECT_DN, (COSBase) COSArrayList.converterToCOSArray(subjectDNDict));
    }

    public List<String> getKeyUsage() {
        COSArray array = this.dictionary.getCOSArray(COSName.KEY_USAGE);
        if (array != null) {
            List<String> keyUsageExtensions = new LinkedList<>();
            for (COSBase item : array) {
                if (item instanceof COSString) {
                    keyUsageExtensions.add(((COSString) item).getString());
                }
            }
            return keyUsageExtensions;
        }
        return null;
    }

    public void setKeyUsage(List<String> keyUsageExtensions) {
        this.dictionary.setItem(COSName.KEY_USAGE, (COSBase) COSArrayList.converterToCOSArray(keyUsageExtensions));
    }

    public void addKeyUsage(String keyUsageExtension) {
        for (int c = 0; c < keyUsageExtension.length(); c++) {
            if ("01X".indexOf(keyUsageExtension.charAt(c)) == -1) {
                throw new IllegalArgumentException("characters can only be 0, 1, X");
            }
        }
        COSArray array = this.dictionary.getCOSArray(COSName.KEY_USAGE);
        if (array == null) {
            array = new COSArray();
        }
        array.add((COSBase) new COSString(keyUsageExtension));
        this.dictionary.setItem(COSName.KEY_USAGE, (COSBase) array);
    }

    public void addKeyUsage(char digitalSignature, char nonRepudiation, char keyEncipherment, char dataEncipherment, char keyAgreement, char keyCertSign, char cRLSign, char encipherOnly, char decipherOnly) {
        String string = "" + digitalSignature + nonRepudiation + keyEncipherment + dataEncipherment + keyAgreement + keyCertSign + cRLSign + encipherOnly + decipherOnly;
        addKeyUsage(string);
    }

    public void removeKeyUsage(String keyUsageExtension) {
        COSArray array = this.dictionary.getCOSArray(COSName.KEY_USAGE);
        if (array != null) {
            array.remove(new COSString(keyUsageExtension));
        }
    }

    public List<byte[]> getIssuer() {
        COSArray array = this.dictionary.getCOSArray(COSName.ISSUER);
        if (array != null) {
            return getListOfByteArraysFromCOSArray(array);
        }
        return null;
    }

    public void setIssuer(List<byte[]> issuers) {
        this.dictionary.setItem(COSName.ISSUER, (COSBase) convertListOfByteArraysToCOSArray(issuers));
    }

    public void addIssuer(byte[] issuer) {
        COSArray array = this.dictionary.getCOSArray(COSName.ISSUER);
        if (array == null) {
            array = new COSArray();
        }
        array.add((COSBase) new COSString(issuer));
        this.dictionary.setItem(COSName.ISSUER, (COSBase) array);
    }

    public void removeIssuer(byte[] issuer) {
        COSArray array = this.dictionary.getCOSArray(COSName.ISSUER);
        if (array != null) {
            array.remove(new COSString(issuer));
        }
    }

    public List<byte[]> getOID() {
        COSArray array = this.dictionary.getCOSArray(COSName.OID);
        if (array != null) {
            return getListOfByteArraysFromCOSArray(array);
        }
        return null;
    }

    public void setOID(List<byte[]> oidByteStrings) {
        this.dictionary.setItem(COSName.OID, (COSBase) convertListOfByteArraysToCOSArray(oidByteStrings));
    }

    public void addOID(byte[] oid) {
        COSArray array = this.dictionary.getCOSArray(COSName.OID);
        if (array == null) {
            array = new COSArray();
        }
        array.add((COSBase) new COSString(oid));
        this.dictionary.setItem(COSName.OID, (COSBase) array);
    }

    public void removeOID(byte[] oid) {
        COSArray array = this.dictionary.getCOSArray(COSName.OID);
        if (array != null) {
            array.remove(new COSString(oid));
        }
    }

    public String getURL() {
        return this.dictionary.getString(COSName.URL);
    }

    public void setURL(String url) {
        this.dictionary.setString(COSName.URL, url);
    }

    public String getURLType() {
        return this.dictionary.getNameAsString(COSName.URL_TYPE);
    }

    public void setURLType(String urlType) {
        this.dictionary.setName(COSName.URL_TYPE, urlType);
    }

    private static List<byte[]> getListOfByteArraysFromCOSArray(COSArray array) {
        List<byte[]> result = new LinkedList<>();
        for (COSBase item : array) {
            if (item instanceof COSString) {
                result.add(((COSString) item).getBytes());
            }
        }
        return result;
    }

    private static COSArray convertListOfByteArraysToCOSArray(List<byte[]> strings) {
        COSArray array = new COSArray();
        for (byte[] string : strings) {
            array.add((COSBase) new COSString(string));
        }
        return array;
    }
}
