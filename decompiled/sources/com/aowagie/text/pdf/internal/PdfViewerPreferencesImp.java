package com.aowagie.text.pdf.internal;

import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfBoolean;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfNumber;
import com.aowagie.text.pdf.PdfObject;
import com.aowagie.text.pdf.PdfReader;
import com.aowagie.text.pdf.interfaces.PdfViewerPreferences;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/internal/PdfViewerPreferencesImp.class */
public class PdfViewerPreferencesImp implements PdfViewerPreferences {
    private static final PdfName[] VIEWER_PREFERENCES = {PdfName.HIDETOOLBAR, PdfName.HIDEMENUBAR, PdfName.HIDEWINDOWUI, PdfName.FITWINDOW, PdfName.CENTERWINDOW, PdfName.DISPLAYDOCTITLE, PdfName.NONFULLSCREENPAGEMODE, PdfName.DIRECTION, PdfName.VIEWAREA, PdfName.VIEWCLIP, PdfName.PRINTAREA, PdfName.PRINTCLIP, PdfName.PRINTSCALING, PdfName.DUPLEX, PdfName.PICKTRAYBYPDFSIZE, PdfName.PRINTPAGERANGE, PdfName.NUMCOPIES};
    private static final PdfName[] NONFULLSCREENPAGEMODE_PREFERENCES = {PdfName.USENONE, PdfName.USEOUTLINES, PdfName.USETHUMBS, PdfName.USEOC};
    private static final PdfName[] DIRECTION_PREFERENCES = {PdfName.L2R, PdfName.R2L};
    private static final PdfName[] PAGE_BOUNDARIES = {PdfName.MEDIABOX, PdfName.CROPBOX, PdfName.BLEEDBOX, PdfName.TRIMBOX, PdfName.ARTBOX};
    private static final PdfName[] PRINTSCALING_PREFERENCES = {PdfName.APPDEFAULT, PdfName.NONE};
    private static final PdfName[] DUPLEX_PREFERENCES = {PdfName.SIMPLEX, PdfName.DUPLEXFLIPSHORTEDGE, PdfName.DUPLEXFLIPLONGEDGE};
    private int pageLayoutAndMode = 0;
    private final PdfDictionary viewerPreferences = new PdfDictionary();
    private static final int viewerPreferencesMask = 16773120;

    public int getPageLayoutAndMode() {
        return this.pageLayoutAndMode;
    }

    public PdfDictionary getViewerPreferences() {
        return this.viewerPreferences;
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfViewerPreferences
    public void setViewerPreferences(int preferences) {
        this.pageLayoutAndMode |= preferences;
        if ((preferences & viewerPreferencesMask) != 0) {
            this.pageLayoutAndMode = (-16773121) & this.pageLayoutAndMode;
            if ((preferences & 4096) != 0) {
                this.viewerPreferences.put(PdfName.HIDETOOLBAR, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 8192) != 0) {
                this.viewerPreferences.put(PdfName.HIDEMENUBAR, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 16384) != 0) {
                this.viewerPreferences.put(PdfName.HIDEWINDOWUI, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 32768) != 0) {
                this.viewerPreferences.put(PdfName.FITWINDOW, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 65536) != 0) {
                this.viewerPreferences.put(PdfName.CENTERWINDOW, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 131072) != 0) {
                this.viewerPreferences.put(PdfName.DISPLAYDOCTITLE, PdfBoolean.PDFTRUE);
            }
            if ((preferences & 262144) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USENONE);
            } else if ((preferences & 524288) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USEOUTLINES);
            } else if ((preferences & 1048576) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USETHUMBS);
            } else if ((preferences & 2097152) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USEOC);
            }
            if ((preferences & 4194304) != 0) {
                this.viewerPreferences.put(PdfName.DIRECTION, PdfName.L2R);
            } else if ((preferences & 8388608) != 0) {
                this.viewerPreferences.put(PdfName.DIRECTION, PdfName.R2L);
            }
            if ((preferences & 16777216) != 0) {
                this.viewerPreferences.put(PdfName.PRINTSCALING, PdfName.NONE);
            }
        }
    }

    private int getIndex(PdfName key) {
        for (int i = 0; i < VIEWER_PREFERENCES.length; i++) {
            if (VIEWER_PREFERENCES[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isPossibleValue(PdfName value, PdfName[] accepted) {
        for (PdfName element : accepted) {
            if (element.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfViewerPreferences
    public void addViewerPreference(PdfName key, PdfObject value) {
        switch (getIndex(key)) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 14:
                if (value instanceof PdfBoolean) {
                    this.viewerPreferences.put(key, value);
                }
                break;
            case 6:
                if ((value instanceof PdfName) && isPossibleValue((PdfName) value, NONFULLSCREENPAGEMODE_PREFERENCES)) {
                    this.viewerPreferences.put(key, value);
                    break;
                }
                break;
            case 7:
                if ((value instanceof PdfName) && isPossibleValue((PdfName) value, DIRECTION_PREFERENCES)) {
                    this.viewerPreferences.put(key, value);
                    break;
                }
                break;
            case 8:
            case 9:
            case 10:
            case 11:
                if ((value instanceof PdfName) && isPossibleValue((PdfName) value, PAGE_BOUNDARIES)) {
                    this.viewerPreferences.put(key, value);
                    break;
                }
                break;
            case 12:
                if ((value instanceof PdfName) && isPossibleValue((PdfName) value, PRINTSCALING_PREFERENCES)) {
                    this.viewerPreferences.put(key, value);
                    break;
                }
                break;
            case 13:
                if ((value instanceof PdfName) && isPossibleValue((PdfName) value, DUPLEX_PREFERENCES)) {
                    this.viewerPreferences.put(key, value);
                    break;
                }
                break;
            case 15:
                if (value instanceof PdfArray) {
                    this.viewerPreferences.put(key, value);
                }
                break;
            case 16:
                if (value instanceof PdfNumber) {
                    this.viewerPreferences.put(key, value);
                }
                break;
        }
    }

    public void addToCatalog(PdfDictionary catalog) {
        catalog.remove(PdfName.PAGELAYOUT);
        if ((this.pageLayoutAndMode & 1) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.SINGLEPAGE);
        } else if ((this.pageLayoutAndMode & 2) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.ONECOLUMN);
        } else if ((this.pageLayoutAndMode & 4) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.TWOCOLUMNLEFT);
        } else if ((this.pageLayoutAndMode & 8) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.TWOCOLUMNRIGHT);
        } else if ((this.pageLayoutAndMode & 16) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.TWOPAGELEFT);
        } else if ((this.pageLayoutAndMode & 32) != 0) {
            catalog.put(PdfName.PAGELAYOUT, PdfName.TWOPAGERIGHT);
        }
        catalog.remove(PdfName.PAGEMODE);
        if ((this.pageLayoutAndMode & 64) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USENONE);
        } else if ((this.pageLayoutAndMode & 128) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
        } else if ((this.pageLayoutAndMode & 256) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USETHUMBS);
        } else if ((this.pageLayoutAndMode & 512) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.FULLSCREEN);
        } else if ((this.pageLayoutAndMode & 1024) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USEOC);
        } else if ((this.pageLayoutAndMode & 2048) != 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USEATTACHMENTS);
        }
        catalog.remove(PdfName.VIEWERPREFERENCES);
        if (this.viewerPreferences.size() > 0) {
            catalog.put(PdfName.VIEWERPREFERENCES, this.viewerPreferences);
        }
    }

    public static PdfViewerPreferencesImp getViewerPreferences(PdfDictionary catalog) {
        PdfViewerPreferencesImp preferences = new PdfViewerPreferencesImp();
        int prefs = 0;
        PdfObject obj = PdfReader.getPdfObjectRelease(catalog.get(PdfName.PAGELAYOUT));
        if (obj != null && obj.isName()) {
            PdfName name = (PdfName) obj;
            if (name.equals(PdfName.SINGLEPAGE)) {
                prefs = 0 | 1;
            } else if (name.equals(PdfName.ONECOLUMN)) {
                prefs = 0 | 2;
            } else if (name.equals(PdfName.TWOCOLUMNLEFT)) {
                prefs = 0 | 4;
            } else if (name.equals(PdfName.TWOCOLUMNRIGHT)) {
                prefs = 0 | 8;
            } else if (name.equals(PdfName.TWOPAGELEFT)) {
                prefs = 0 | 16;
            } else if (name.equals(PdfName.TWOPAGERIGHT)) {
                prefs = 0 | 32;
            }
        }
        PdfObject obj2 = PdfReader.getPdfObjectRelease(catalog.get(PdfName.PAGEMODE));
        if (obj2 != null && obj2.isName()) {
            PdfName name2 = (PdfName) obj2;
            if (name2.equals(PdfName.USENONE)) {
                prefs |= 64;
            } else if (name2.equals(PdfName.USEOUTLINES)) {
                prefs |= 128;
            } else if (name2.equals(PdfName.USETHUMBS)) {
                prefs |= 256;
            } else if (name2.equals(PdfName.FULLSCREEN)) {
                prefs |= 512;
            } else if (name2.equals(PdfName.USEOC)) {
                prefs |= 1024;
            } else if (name2.equals(PdfName.USEATTACHMENTS)) {
                prefs |= 2048;
            }
        }
        preferences.setViewerPreferences(prefs);
        PdfObject obj3 = PdfReader.getPdfObjectRelease(catalog.get(PdfName.VIEWERPREFERENCES));
        if (obj3 != null && obj3.isDictionary()) {
            PdfDictionary vp = (PdfDictionary) obj3;
            for (PdfName element : VIEWER_PREFERENCES) {
                preferences.addViewerPreference(element, PdfReader.getPdfObjectRelease(vp.get(element)));
            }
        }
        return preferences;
    }
}
