package com.twelvemonkeys.io;

import com.twelvemonkeys.util.regex.WildcardStringParser;
import java.io.File;
import java.io.FilenameFilter;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/FilenameMaskFilter.class */
@Deprecated
public class FilenameMaskFilter implements FilenameFilter {
    private String[] filenameMasksForInclusion;
    private String[] filenameMasksForExclusion;
    private boolean inclusion;

    public FilenameMaskFilter() {
        this.inclusion = true;
    }

    public FilenameMaskFilter(String str) {
        this.inclusion = true;
        setFilenameMasksForInclusion(new String[]{str});
    }

    public FilenameMaskFilter(String[] strArr) {
        this(strArr, false);
    }

    public FilenameMaskFilter(String str, boolean z) {
        this.inclusion = true;
        String[] strArr = {str};
        if (z) {
            setFilenameMasksForExclusion(strArr);
        } else {
            setFilenameMasksForInclusion(strArr);
        }
    }

    public FilenameMaskFilter(String[] strArr, boolean z) {
        this.inclusion = true;
        if (z) {
            setFilenameMasksForExclusion(strArr);
        } else {
            setFilenameMasksForInclusion(strArr);
        }
    }

    public void setFilenameMasksForInclusion(String[] strArr) {
        this.filenameMasksForInclusion = strArr;
    }

    public String[] getFilenameMasksForInclusion() {
        return (String[]) this.filenameMasksForInclusion.clone();
    }

    public void setFilenameMasksForExclusion(String[] strArr) {
        this.filenameMasksForExclusion = strArr;
        this.inclusion = false;
    }

    public String[] getFilenameMasksForExclusion() {
        return (String[]) this.filenameMasksForExclusion.clone();
    }

    @Override // java.io.FilenameFilter
    public boolean accept(File file, String str) {
        if (this.inclusion) {
            for (String str2 : this.filenameMasksForInclusion) {
                if (new WildcardStringParser(str2).parseString(str)) {
                    return true;
                }
            }
            return false;
        }
        for (String str3 : this.filenameMasksForExclusion) {
            if (new WildcardStringParser(str3).parseString(str)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.inclusion) {
            if (this.filenameMasksForInclusion == null) {
                sb.append("No filename masks set - property filenameMasksForInclusion is null!");
            } else {
                sb.append(this.filenameMasksForInclusion.length);
                sb.append(" filename mask(s) - ");
                for (int i = 0; i < this.filenameMasksForInclusion.length; i++) {
                    sb.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
                    sb.append(this.filenameMasksForInclusion[i]);
                    sb.append("\", \"");
                }
            }
        } else if (this.filenameMasksForExclusion == null) {
            sb.append("No filename masks set - property filenameMasksForExclusion is null!");
        } else {
            sb.append(this.filenameMasksForExclusion.length);
            sb.append(" exclusion filename mask(s) - ");
            for (int i2 = 0; i2 < this.filenameMasksForExclusion.length; i2++) {
                sb.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
                sb.append(this.filenameMasksForExclusion[i2]);
                sb.append("\", \"");
            }
        }
        return sb.toString();
    }
}
