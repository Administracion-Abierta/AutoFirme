package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/PDSeedValue.class */
public class PDSeedValue implements COSObjectable {
    public static final int FLAG_FILTER = 1;
    public static final int FLAG_SUBFILTER = 2;
    public static final int FLAG_V = 4;
    public static final int FLAG_REASON = 8;
    public static final int FLAG_LEGAL_ATTESTATION = 16;
    public static final int FLAG_ADD_REV_INFO = 32;
    public static final int FLAG_DIGEST_METHOD = 64;
    private final COSDictionary dictionary;

    public PDSeedValue() {
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.TYPE, (COSBase) COSName.SV);
        this.dictionary.setDirect(true);
    }

    public PDSeedValue(COSDictionary dict) {
        this.dictionary = dict;
        this.dictionary.setDirect(true);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public boolean isFilterRequired() {
        return getCOSObject().getFlag(COSName.FF, 1);
    }

    public void setFilterRequired(boolean flag) {
        getCOSObject().setFlag(COSName.FF, 1, flag);
    }

    public boolean isSubFilterRequired() {
        return getCOSObject().getFlag(COSName.FF, 2);
    }

    public void setSubFilterRequired(boolean flag) {
        getCOSObject().setFlag(COSName.FF, 2, flag);
    }

    public boolean isDigestMethodRequired() {
        return getCOSObject().getFlag(COSName.FF, 64);
    }

    public void setDigestMethodRequired(boolean flag) {
        getCOSObject().setFlag(COSName.FF, 64, flag);
    }

    public boolean isVRequired() {
        return getCOSObject().getFlag(COSName.FF, 4);
    }

    public void setVRequired(boolean flag) {
        getCOSObject().setFlag(COSName.FF, 4, flag);
    }

    public boolean isReasonRequired() {
        return getCOSObject().getFlag(COSName.FF, 8);
    }

    public void setReasonRequired(boolean flag) {
        getCOSObject().setFlag(COSName.FF, 8, flag);
    }

    public boolean isLegalAttestationRequired() {
        return getCOSObject().getFlag(COSName.FF, 16);
    }

    public void setLegalAttestationRequired(boolean flag) {
        getCOSObject().setFlag(COSName.FF, 16, flag);
    }

    public boolean isAddRevInfoRequired() {
        return getCOSObject().getFlag(COSName.FF, 32);
    }

    public void setAddRevInfoRequired(boolean flag) {
        getCOSObject().setFlag(COSName.FF, 32, flag);
    }

    public String getFilter() {
        return this.dictionary.getNameAsString(COSName.FILTER);
    }

    public void setFilter(COSName filter) {
        this.dictionary.setItem(COSName.FILTER, (COSBase) filter);
    }

    public List<String> getSubFilter() {
        List<String> retval = null;
        COSArray fields = (COSArray) this.dictionary.getDictionaryObject(COSName.SUB_FILTER);
        if (fields != null) {
            List<String> actuals = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                String element = fields.getName(i);
                if (element != null) {
                    actuals.add(element);
                }
            }
            retval = new COSArrayList<>(actuals, fields);
        }
        return retval;
    }

    public void setSubFilter(List<COSName> subfilter) {
        this.dictionary.setItem(COSName.SUB_FILTER, (COSBase) COSArrayList.converterToCOSArray(subfilter));
    }

    public List<String> getDigestMethod() {
        List<String> retval = null;
        COSArray fields = (COSArray) this.dictionary.getDictionaryObject(COSName.DIGEST_METHOD);
        if (fields != null) {
            List<String> actuals = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                String element = fields.getName(i);
                if (element != null) {
                    actuals.add(element);
                }
            }
            retval = new COSArrayList<>(actuals, fields);
        }
        return retval;
    }

    public void setDigestMethod(List<COSName> digestMethod) {
        for (COSName cosName : digestMethod) {
            if (!cosName.equals(COSName.DIGEST_SHA1) && !cosName.equals(COSName.DIGEST_SHA256) && !cosName.equals(COSName.DIGEST_SHA384) && !cosName.equals(COSName.DIGEST_SHA512) && !cosName.equals(COSName.DIGEST_RIPEMD160)) {
                throw new IllegalArgumentException("Specified digest " + cosName.getName() + " isn't allowed.");
            }
        }
        this.dictionary.setItem(COSName.DIGEST_METHOD, (COSBase) COSArrayList.converterToCOSArray(digestMethod));
    }

    public float getV() {
        return this.dictionary.getFloat(COSName.V);
    }

    public void setV(float minimumRequiredCapability) {
        this.dictionary.setFloat(COSName.V, minimumRequiredCapability);
    }

    public List<String> getReasons() {
        List<String> retval = null;
        COSArray fields = (COSArray) this.dictionary.getDictionaryObject(COSName.REASONS);
        if (fields != null) {
            List<String> actuals = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                String element = fields.getString(i);
                if (element != null) {
                    actuals.add(element);
                }
            }
            retval = new COSArrayList<>(actuals, fields);
        }
        return retval;
    }

    @Deprecated
    public void setReasonsd(List<String> reasons) {
        setReasons(reasons);
    }

    public void setReasons(List<String> reasons) {
        this.dictionary.setItem(COSName.REASONS, (COSBase) COSArrayList.converterToCOSArray(reasons));
    }

    public PDSeedValueMDP getMDP() {
        COSDictionary dict = this.dictionary.getCOSDictionary(COSName.MDP);
        PDSeedValueMDP mdp = null;
        if (dict != null) {
            mdp = new PDSeedValueMDP(dict);
        }
        return mdp;
    }

    public void setMPD(PDSeedValueMDP mdp) {
        if (mdp != null) {
            this.dictionary.setItem(COSName.MDP, (COSBase) mdp.getCOSObject());
        }
    }

    public PDSeedValueCertificate getSeedValueCertificate() {
        COSBase base = this.dictionary.getDictionaryObject(COSName.CERT);
        PDSeedValueCertificate certificate = null;
        if (base instanceof COSDictionary) {
            COSDictionary dict = (COSDictionary) base;
            certificate = new PDSeedValueCertificate(dict);
        }
        return certificate;
    }

    public void setSeedValueCertificate(PDSeedValueCertificate certificate) {
        this.dictionary.setItem(COSName.CERT, certificate);
    }

    public PDSeedValueTimeStamp getTimeStamp() {
        COSDictionary dict = this.dictionary.getCOSDictionary(COSName.TIME_STAMP);
        PDSeedValueTimeStamp timestamp = null;
        if (dict != null) {
            timestamp = new PDSeedValueTimeStamp(dict);
        }
        return timestamp;
    }

    public void setTimeStamp(PDSeedValueTimeStamp timestamp) {
        if (timestamp != null) {
            this.dictionary.setItem(COSName.TIME_STAMP, (COSBase) timestamp.getCOSObject());
        }
    }

    public List<String> getLegalAttestation() {
        List<String> retval = null;
        COSArray fields = (COSArray) this.dictionary.getDictionaryObject(COSName.LEGAL_ATTESTATION);
        if (fields != null) {
            List<String> actuals = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                String element = fields.getString(i);
                if (element != null) {
                    actuals.add(element);
                }
            }
            retval = new COSArrayList<>(actuals, fields);
        }
        return retval;
    }

    public void setLegalAttestation(List<String> legalAttestation) {
        this.dictionary.setItem(COSName.LEGAL_ATTESTATION, (COSBase) COSArrayList.converterToCOSArray(legalAttestation));
    }
}
