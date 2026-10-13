package org.apache.pdfbox.pdmodel.interactive.viewerpreferences;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/viewerpreferences/PDViewerPreferences.class */
public class PDViewerPreferences implements COSObjectable {
    public static final String NON_FULL_SCREEN_PAGE_MODE_USE_NONE = "UseNone";
    public static final String NON_FULL_SCREEN_PAGE_MODE_USE_OUTLINES = "UseOutlines";
    public static final String NON_FULL_SCREEN_PAGE_MODE_USE_THUMBS = "UseThumbs";
    public static final String NON_FULL_SCREEN_PAGE_MODE_USE_OPTIONAL_CONTENT = "UseOC";
    public static final String READING_DIRECTION_L2R = "L2R";
    public static final String READING_DIRECTION_R2L = "R2L";
    public static final String BOUNDARY_MEDIA_BOX = "MediaBox";
    public static final String BOUNDARY_CROP_BOX = "CropBox";
    public static final String BOUNDARY_BLEED_BOX = "BleedBox";
    public static final String BOUNDARY_TRIM_BOX = "TrimBox";
    public static final String BOUNDARY_ART_BOX = "ArtBox";
    private final COSDictionary prefs;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/viewerpreferences/PDViewerPreferences$BOUNDARY.class */
    public enum BOUNDARY {
        MediaBox,
        CropBox,
        BleedBox,
        TrimBox,
        ArtBox
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/viewerpreferences/PDViewerPreferences$DUPLEX.class */
    public enum DUPLEX {
        Simplex,
        DuplexFlipShortEdge,
        DuplexFlipLongEdge
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/viewerpreferences/PDViewerPreferences$NON_FULL_SCREEN_PAGE_MODE.class */
    public enum NON_FULL_SCREEN_PAGE_MODE {
        UseNone,
        UseOutlines,
        UseThumbs,
        UseOC
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/viewerpreferences/PDViewerPreferences$PRINT_SCALING.class */
    public enum PRINT_SCALING {
        None,
        AppDefault
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/viewerpreferences/PDViewerPreferences$READING_DIRECTION.class */
    public enum READING_DIRECTION {
        L2R,
        R2L
    }

    public PDViewerPreferences(COSDictionary dic) {
        this.prefs = dic;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.prefs;
    }

    public boolean hideToolbar() {
        return this.prefs.getBoolean(COSName.HIDE_TOOLBAR, false);
    }

    public void setHideToolbar(boolean value) {
        this.prefs.setBoolean(COSName.HIDE_TOOLBAR, value);
    }

    public boolean hideMenubar() {
        return this.prefs.getBoolean(COSName.HIDE_MENUBAR, false);
    }

    public void setHideMenubar(boolean value) {
        this.prefs.setBoolean(COSName.HIDE_MENUBAR, value);
    }

    public boolean hideWindowUI() {
        return this.prefs.getBoolean(COSName.HIDE_WINDOWUI, false);
    }

    public void setHideWindowUI(boolean value) {
        this.prefs.setBoolean(COSName.HIDE_WINDOWUI, value);
    }

    public boolean fitWindow() {
        return this.prefs.getBoolean(COSName.FIT_WINDOW, false);
    }

    public void setFitWindow(boolean value) {
        this.prefs.setBoolean(COSName.FIT_WINDOW, value);
    }

    public boolean centerWindow() {
        return this.prefs.getBoolean(COSName.CENTER_WINDOW, false);
    }

    public void setCenterWindow(boolean value) {
        this.prefs.setBoolean(COSName.CENTER_WINDOW, value);
    }

    public boolean displayDocTitle() {
        return this.prefs.getBoolean(COSName.DISPLAY_DOC_TITLE, false);
    }

    public void setDisplayDocTitle(boolean value) {
        this.prefs.setBoolean(COSName.DISPLAY_DOC_TITLE, value);
    }

    public String getNonFullScreenPageMode() {
        return this.prefs.getNameAsString(COSName.NON_FULL_SCREEN_PAGE_MODE, NON_FULL_SCREEN_PAGE_MODE.UseNone.toString());
    }

    public void setNonFullScreenPageMode(NON_FULL_SCREEN_PAGE_MODE value) {
        this.prefs.setName(COSName.NON_FULL_SCREEN_PAGE_MODE, value.toString());
    }

    public void setNonFullScreenPageMode(String value) {
        this.prefs.setName(COSName.NON_FULL_SCREEN_PAGE_MODE, value);
    }

    public String getReadingDirection() {
        return this.prefs.getNameAsString(COSName.DIRECTION, READING_DIRECTION.L2R.toString());
    }

    public void setReadingDirection(READING_DIRECTION value) {
        this.prefs.setName(COSName.DIRECTION, value.toString());
    }

    public void setReadingDirection(String value) {
        this.prefs.setName(COSName.DIRECTION, value);
    }

    public String getViewArea() {
        return this.prefs.getNameAsString(COSName.VIEW_AREA, BOUNDARY.CropBox.toString());
    }

    public void setViewArea(String value) {
        this.prefs.setName(COSName.VIEW_AREA, value);
    }

    public void setViewArea(BOUNDARY value) {
        this.prefs.setName(COSName.VIEW_AREA, value.toString());
    }

    public String getViewClip() {
        return this.prefs.getNameAsString(COSName.VIEW_CLIP, BOUNDARY.CropBox.toString());
    }

    public void setViewClip(BOUNDARY value) {
        this.prefs.setName(COSName.VIEW_CLIP, value.toString());
    }

    public void setViewClip(String value) {
        this.prefs.setName(COSName.VIEW_CLIP, value);
    }

    public String getPrintArea() {
        return this.prefs.getNameAsString(COSName.PRINT_AREA, BOUNDARY.CropBox.toString());
    }

    public void setPrintArea(String value) {
        this.prefs.setName(COSName.PRINT_AREA, value);
    }

    public void setPrintArea(BOUNDARY value) {
        this.prefs.setName(COSName.PRINT_AREA, value.toString());
    }

    public String getPrintClip() {
        return this.prefs.getNameAsString(COSName.PRINT_CLIP, BOUNDARY.CropBox.toString());
    }

    public void setPrintClip(String value) {
        this.prefs.setName(COSName.PRINT_CLIP, value);
    }

    public void setPrintClip(BOUNDARY value) {
        this.prefs.setName(COSName.PRINT_CLIP, value.toString());
    }

    public String getDuplex() {
        return this.prefs.getNameAsString(COSName.DUPLEX);
    }

    public void setDuplex(DUPLEX value) {
        this.prefs.setName(COSName.DUPLEX, value.toString());
    }

    public String getPrintScaling() {
        return this.prefs.getNameAsString(COSName.PRINT_SCALING, PRINT_SCALING.AppDefault.toString());
    }

    public void setPrintScaling(PRINT_SCALING value) {
        this.prefs.setName(COSName.PRINT_SCALING, value.toString());
    }
}
