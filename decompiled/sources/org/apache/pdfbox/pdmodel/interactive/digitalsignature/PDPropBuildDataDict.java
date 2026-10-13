package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/PDPropBuildDataDict.class */
public class PDPropBuildDataDict implements COSObjectable {
    private final COSDictionary dictionary;

    public PDPropBuildDataDict() {
        this.dictionary = new COSDictionary();
        this.dictionary.setDirect(true);
    }

    public PDPropBuildDataDict(COSDictionary dict) {
        this.dictionary = dict;
        this.dictionary.setDirect(true);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public String getName() {
        return this.dictionary.getNameAsString(COSName.NAME);
    }

    public void setName(String name) {
        this.dictionary.setName(COSName.NAME, name);
    }

    public String getDate() {
        return this.dictionary.getString(COSName.DATE);
    }

    public void setDate(String date) {
        this.dictionary.setString(COSName.DATE, date);
    }

    public void setVersion(String applicationVersion) {
        this.dictionary.setString("REx", applicationVersion);
    }

    public String getVersion() {
        return this.dictionary.getString("REx");
    }

    public long getRevision() {
        return this.dictionary.getLong(COSName.R);
    }

    public void setRevision(long revision) {
        this.dictionary.setLong(COSName.R, revision);
    }

    public long getMinimumRevision() {
        return this.dictionary.getLong(COSName.V);
    }

    public void setMinimumRevision(long revision) {
        this.dictionary.setLong(COSName.V, revision);
    }

    public boolean getPreRelease() {
        return this.dictionary.getBoolean(COSName.PRE_RELEASE, false);
    }

    public void setPreRelease(boolean preRelease) {
        this.dictionary.setBoolean(COSName.PRE_RELEASE, preRelease);
    }

    public String getOS() {
        COSBase cosBase = this.dictionary.getItem(COSName.OS);
        if (cosBase instanceof COSArray) {
            return ((COSArray) cosBase).getName(0);
        }
        return this.dictionary.getString(COSName.OS);
    }

    public void setOS(String os) {
        if (os == null) {
            this.dictionary.removeItem(COSName.OS);
            return;
        }
        COSBase osArray = this.dictionary.getItem(COSName.OS);
        if (!(osArray instanceof COSArray)) {
            osArray = new COSArray();
            osArray.setDirect(true);
            this.dictionary.setItem(COSName.OS, osArray);
        }
        ((COSArray) osArray).add(0, COSName.getPDFName(os));
    }

    public boolean getNonEFontNoWarn() {
        return this.dictionary.getBoolean(COSName.NON_EFONT_NO_WARN, true);
    }

    public void setNonEFontNoWarn(boolean noEmbedFontWarning) {
        this.dictionary.setBoolean(COSName.NON_EFONT_NO_WARN, noEmbedFontWarning);
    }

    public boolean getTrustedMode() {
        return this.dictionary.getBoolean(COSName.TRUSTED_MODE, false);
    }

    public void setTrustedMode(boolean trustedMode) {
        this.dictionary.setBoolean(COSName.TRUSTED_MODE, trustedMode);
    }
}
