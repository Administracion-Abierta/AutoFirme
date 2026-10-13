package es.gob.afirma.standalone.protocol;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/Version.class */
public class Version {
    private final List<Integer> versionParts = new ArrayList();
    private final String aditionalText;

    public Version(String str) throws IllegalArgumentException {
        if (str == null) {
            this.versionParts.add(0);
            this.aditionalText = "";
            return;
        }
        String[] strArrSplit = str.split("\\.");
        for (int i = 0; i < strArrSplit.length - 1; i++) {
            this.versionParts.add(Integer.valueOf(strArrSplit[i]));
        }
        int i2 = -1;
        String str2 = strArrSplit[strArrSplit.length - 1];
        for (int i3 = 0; i3 < str2.length() && i2 < 0; i3++) {
            if (!Character.isDigit(str2.charAt(i3))) {
                i2 = i3;
            }
        }
        if (i2 > 0) {
            this.versionParts.add(Integer.valueOf(str2.substring(0, i2)));
            this.aditionalText = str2.substring(i2);
        } else {
            this.versionParts.add(Integer.valueOf(str2));
            this.aditionalText = "";
        }
    }

    public boolean greaterThan(String str) throws IllegalArgumentException {
        return greaterThan(new Version(str));
    }

    public boolean greaterThan(Version version) {
        for (int i = 0; this.versionParts.size() > i && version.getVersionParts().size() > i; i++) {
            int iIntValue = this.versionParts.get(i).intValue();
            int iIntValue2 = version.getVersionParts().get(i).intValue();
            if (iIntValue > iIntValue2) {
                return true;
            }
            if (iIntValue < iIntValue2) {
                return false;
            }
        }
        if (this.versionParts.size() > version.getVersionParts().size()) {
            return true;
        }
        if (this.versionParts.size() < version.getVersionParts().size() || this.aditionalText.equalsIgnoreCase(version.getAditionalText())) {
            return false;
        }
        if ((this.aditionalText.isEmpty() || !Character.isSpaceChar(this.aditionalText.charAt(0))) && version.getAditionalText().length() > 0 && Character.isSpaceChar(version.getAditionalText().charAt(0))) {
            return true;
        }
        if ((version.getAditionalText().isEmpty() || !Character.isSpaceChar(version.getAditionalText().charAt(0))) && this.aditionalText.length() > 0 && Character.isSpaceChar(this.aditionalText.charAt(0))) {
            return false;
        }
        if (!this.aditionalText.isEmpty() || Character.isSpaceChar(version.getAditionalText().charAt(0))) {
            return (version.getAditionalText().isEmpty() && !Character.isSpaceChar(this.aditionalText.charAt(0))) || this.aditionalText.toLowerCase().compareTo(version.getAditionalText().toLowerCase()) > 0;
        }
        return false;
    }

    public List<Integer> getVersionParts() {
        if (this.versionParts != null) {
            return new ArrayList(this.versionParts);
        }
        return null;
    }

    public String getAditionalText() {
        return this.aditionalText;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.versionParts.size(); i++) {
            if (i > 0) {
                sb.append('.');
            }
            sb.append(this.versionParts.get(i).intValue());
        }
        sb.append(this.aditionalText);
        return sb.toString();
    }
}
