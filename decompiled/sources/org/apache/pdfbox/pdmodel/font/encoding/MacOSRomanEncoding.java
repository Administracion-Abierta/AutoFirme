package org.apache.pdfbox.pdmodel.font.encoding;

import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/encoding/MacOSRomanEncoding.class */
public class MacOSRomanEncoding extends MacRomanEncoding {
    private static final Object[][] MAC_OS_ROMAN_ENCODING_TABLE = {new Object[]{255, "notequal"}, new Object[]{260, "infinity"}, new Object[]{Integer.valueOf(TIFF.TAG_PHOTOMETRIC_INTERPRETATION), "lessequal"}, new Object[]{263, "greaterequal"}, new Object[]{266, "partialdiff"}, new Object[]{Integer.valueOf(WinError.ERROR_DIRECTORY), "summation"}, new Object[]{Integer.valueOf(TIFF.TAG_IMAGE_DESCRIPTION), "product"}, new Object[]{Integer.valueOf(TIFF.TAG_MAKE), "pi"}, new Object[]{272, "integral"}, new Object[]{Integer.valueOf(WinError.ERROR_EAS_DIDNT_FIT), "Omega"}, new Object[]{Integer.valueOf(WinError.ERROR_DELETE_PENDING), "radical"}, new Object[]{Integer.valueOf(TIFF.TAG_SOFTWARE), "approxequal"}, new Object[]{306, "Delta"}, new Object[]{327, "lozenge"}, new Object[]{Integer.valueOf(TIFF.TAG_INK_NAMES), "Euro"}, new Object[]{360, "apple"}};
    public static final MacOSRomanEncoding INSTANCE = new MacOSRomanEncoding();

    public MacOSRomanEncoding() {
        for (Object[] encodingEntry : MAC_OS_ROMAN_ENCODING_TABLE) {
            add(((Integer) encodingEntry[0]).intValue(), encodingEntry[1].toString());
        }
    }

    @Override // org.apache.pdfbox.pdmodel.font.encoding.MacRomanEncoding, org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return null;
    }
}
