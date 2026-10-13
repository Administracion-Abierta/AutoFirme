package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/PDSignature.class */
public class PDSignature implements COSObjectable {
    private final COSDictionary dictionary;
    public static final COSName FILTER_ADOBE_PPKLITE = COSName.ADOBE_PPKLITE;
    public static final COSName FILTER_ENTRUST_PPKEF = COSName.ENTRUST_PPKEF;
    public static final COSName FILTER_CICI_SIGNIT = COSName.CICI_SIGNIT;
    public static final COSName FILTER_VERISIGN_PPKVS = COSName.VERISIGN_PPKVS;
    public static final COSName SUBFILTER_ADBE_X509_RSA_SHA1 = COSName.ADBE_X509_RSA_SHA1;
    public static final COSName SUBFILTER_ADBE_PKCS7_DETACHED = COSName.ADBE_PKCS7_DETACHED;
    public static final COSName SUBFILTER_ETSI_CADES_DETACHED = COSName.getPDFName(AOSignConstants.PADES_SUBFILTER_BES);
    public static final COSName SUBFILTER_ADBE_PKCS7_SHA1 = COSName.ADBE_PKCS7_SHA1;

    public PDSignature() {
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.TYPE, (COSBase) COSName.SIG);
    }

    public PDSignature(COSDictionary dict) {
        this.dictionary = dict;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public void setType(COSName type) {
        this.dictionary.setItem(COSName.TYPE, (COSBase) type);
    }

    public void setFilter(COSName filter) {
        this.dictionary.setItem(COSName.FILTER, (COSBase) filter);
    }

    public void setSubFilter(COSName subfilter) {
        this.dictionary.setItem(COSName.SUB_FILTER, (COSBase) subfilter);
    }

    public void setName(String name) {
        this.dictionary.setString(COSName.NAME, name);
    }

    public void setLocation(String location) {
        this.dictionary.setString(COSName.LOCATION, location);
    }

    public void setReason(String reason) {
        this.dictionary.setString(COSName.REASON, reason);
    }

    public void setContactInfo(String contactInfo) {
        this.dictionary.setString(COSName.CONTACT_INFO, contactInfo);
    }

    public void setSignDate(Calendar cal) {
        this.dictionary.setDate(COSName.M, cal);
    }

    public String getFilter() {
        return this.dictionary.getNameAsString(COSName.FILTER);
    }

    public String getSubFilter() {
        return this.dictionary.getNameAsString(COSName.SUB_FILTER);
    }

    public String getName() {
        return this.dictionary.getString(COSName.NAME);
    }

    public String getLocation() {
        return this.dictionary.getString(COSName.LOCATION);
    }

    public String getReason() {
        return this.dictionary.getString(COSName.REASON);
    }

    public String getContactInfo() {
        return this.dictionary.getString(COSName.CONTACT_INFO);
    }

    public Calendar getSignDate() {
        return this.dictionary.getDate(COSName.M);
    }

    public void setByteRange(int[] range) {
        if (range.length != 4) {
            return;
        }
        COSArray ary = new COSArray();
        for (int i : range) {
            ary.add((COSBase) COSInteger.get(i));
        }
        this.dictionary.setItem(COSName.BYTERANGE, (COSBase) ary);
        ary.setDirect(true);
    }

    public int[] getByteRange() {
        COSArray byteRange = this.dictionary.getCOSArray(COSName.BYTERANGE);
        if (byteRange == null) {
            return new int[0];
        }
        int[] ary = new int[byteRange.size()];
        for (int i = 0; i < ary.length; i++) {
            ary[i] = byteRange.getInt(i);
        }
        return ary;
    }

    public byte[] getContents() {
        COSBase base = this.dictionary.getDictionaryObject(COSName.CONTENTS);
        if (base instanceof COSString) {
            return ((COSString) base).getBytes();
        }
        return new byte[0];
    }

    public byte[] getContents(InputStream pdfFile) throws IOException {
        int[] byteRange = getByteRange();
        int begin = byteRange[0] + byteRange[1] + 1;
        int len = byteRange[2] - begin;
        return getConvertedContents(new COSFilterInputStream(pdfFile, new int[]{begin, len}));
    }

    public byte[] getContents(byte[] pdfFile) throws IOException {
        int[] byteRange = getByteRange();
        int begin = byteRange[0] + byteRange[1] + 1;
        int len = byteRange[2] - begin;
        return getConvertedContents(new ByteArrayInputStream(pdfFile, begin, len));
    }

    private byte[] getConvertedContents(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        while (true) {
            int readLen = is.read(buffer);
            if (readLen != -1) {
                int writeLen = readLen;
                int start = 0;
                if (buffer[0] == 60 || buffer[0] == 40) {
                    start = 0 + 1;
                    writeLen--;
                }
                if (buffer[readLen - 1] == 62 || buffer[readLen - 1] == 41) {
                    writeLen--;
                }
                baos.write(buffer, start, writeLen);
            } else {
                is.close();
                return COSString.parseHex(baos.toString("ISO-8859-1")).getBytes();
            }
        }
    }

    public void setContents(byte[] bytes) {
        COSString string = new COSString(bytes);
        string.setForceHexForm(true);
        this.dictionary.setItem(COSName.CONTENTS, (COSBase) string);
    }

    public byte[] getSignedContent(InputStream pdfFile) throws IOException {
        COSFilterInputStream fis = null;
        try {
            COSFilterInputStream fis2 = new COSFilterInputStream(pdfFile, getByteRange());
            byte[] byteArray = fis2.toByteArray();
            if (fis2 != null) {
            }
            return byteArray;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    public byte[] getSignedContent(byte[] pdfFile) throws IOException {
        COSFilterInputStream fis = null;
        try {
            COSFilterInputStream fis2 = new COSFilterInputStream(pdfFile, getByteRange());
            byte[] byteArray = fis2.toByteArray();
            if (fis2 != null) {
            }
            return byteArray;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    public PDPropBuild getPropBuild() {
        PDPropBuild propBuild = null;
        COSDictionary propBuildDic = this.dictionary.getCOSDictionary(COSName.PROP_BUILD);
        if (propBuildDic != null) {
            propBuild = new PDPropBuild(propBuildDic);
        }
        return propBuild;
    }

    public void setPropBuild(PDPropBuild propBuild) {
        this.dictionary.setItem(COSName.PROP_BUILD, propBuild);
    }
}
