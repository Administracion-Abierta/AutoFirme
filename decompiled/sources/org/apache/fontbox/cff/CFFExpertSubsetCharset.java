package org.apache.fontbox.cff;

import com.aowagie.text.pdf.BaseFont;
import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.iptc.IPTC;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFExpertSubsetCharset.class */
public final class CFFExpertSubsetCharset extends CFFCharset {
    private static final int CHAR_CODE = 0;
    private static final int CHAR_NAME = 1;
    private static final Object[][] CFF_EXPERT_SUBSET_CHARSET_TABLE = {new Object[]{0, BaseFont.notdef}, new Object[]{1, "space"}, new Object[]{Integer.valueOf(WinError.ERROR_PIPE_BUSY), "dollaroldstyle"}, new Object[]{Integer.valueOf(WinError.ERROR_NO_DATA), "dollarsuperior"}, new Object[]{235, "parenleftsuperior"}, new Object[]{236, "parenrightsuperior"}, new Object[]{237, "twodotenleader"}, new Object[]{238, "onedotenleader"}, new Object[]{13, "comma"}, new Object[]{14, "hyphen"}, new Object[]{15, "period"}, new Object[]{99, "fraction"}, new Object[]{239, "zerooldstyle"}, new Object[]{240, "oneoldstyle"}, new Object[]{241, "twooldstyle"}, new Object[]{242, "threeoldstyle"}, new Object[]{243, "fouroldstyle"}, new Object[]{244, "fiveoldstyle"}, new Object[]{245, "sixoldstyle"}, new Object[]{246, "sevenoldstyle"}, new Object[]{247, "eightoldstyle"}, new Object[]{248, "nineoldstyle"}, new Object[]{27, "colon"}, new Object[]{28, "semicolon"}, new Object[]{249, "commasuperior"}, new Object[]{250, "threequartersemdash"}, new Object[]{251, "periodsuperior"}, new Object[]{253, "asuperior"}, new Object[]{254, "bsuperior"}, new Object[]{255, "centsuperior"}, new Object[]{256, "dsuperior"}, new Object[]{257, "esuperior"}, new Object[]{258, "isuperior"}, new Object[]{259, "lsuperior"}, new Object[]{260, "msuperior"}, new Object[]{261, "nsuperior"}, new Object[]{Integer.valueOf(TIFF.TAG_PHOTOMETRIC_INTERPRETATION), "osuperior"}, new Object[]{263, "rsuperior"}, new Object[]{264, "ssuperior"}, new Object[]{265, "tsuperior"}, new Object[]{266, "ff"}, new Object[]{109, "fi"}, new Object[]{110, "fl"}, new Object[]{Integer.valueOf(WinError.ERROR_DIRECTORY), "ffi"}, new Object[]{268, "ffl"}, new Object[]{Integer.valueOf(TIFF.TAG_DOCUMENT_NAME), "parenleftinferior"}, new Object[]{Integer.valueOf(TIFF.TAG_IMAGE_DESCRIPTION), "parenrightinferior"}, new Object[]{272, "hyphensuperior"}, new Object[]{300, "colonmonetary"}, new Object[]{301, "onefitted"}, new Object[]{Integer.valueOf(WinError.ERROR_DISK_TOO_FRAGMENTED), "rupiah"}, new Object[]{Integer.valueOf(TIFF.TAG_SOFTWARE), "centoldstyle"}, new Object[]{314, "figuredash"}, new Object[]{Integer.valueOf(TIFF.TAG_ARTIST), "hypheninferior"}, new Object[]{158, "onequarter"}, new Object[]{155, "onehalf"}, new Object[]{163, "threequarters"}, new Object[]{320, "oneeighth"}, new Object[]{321, "threeeighths"}, new Object[]{Integer.valueOf(TIFF.TAG_TILE_WIDTH), "fiveeighths"}, new Object[]{Integer.valueOf(TIFF.TAG_TILE_HEIGTH), "seveneighths"}, new Object[]{Integer.valueOf(TIFF.TAG_TILE_OFFSETS), "onethird"}, new Object[]{Integer.valueOf(TIFF.TAG_TILE_BYTE_COUNTS), "twothirds"}, new Object[]{326, "zerosuperior"}, new Object[]{150, "onesuperior"}, new Object[]{164, "twosuperior"}, new Object[]{169, "threesuperior"}, new Object[]{327, "foursuperior"}, new Object[]{328, "fivesuperior"}, new Object[]{329, "sixsuperior"}, new Object[]{Integer.valueOf(TIFF.TAG_SUB_IFD), "sevensuperior"}, new Object[]{331, "eightsuperior"}, new Object[]{Integer.valueOf(TIFF.TAG_INK_SET), "ninesuperior"}, new Object[]{Integer.valueOf(TIFF.TAG_INK_NAMES), "zeroinferior"}, new Object[]{Integer.valueOf(TIFF.TAG_NUMBER_OF_INKS), "oneinferior"}, new Object[]{335, "twoinferior"}, new Object[]{336, "threeinferior"}, new Object[]{337, "fourinferior"}, new Object[]{Integer.valueOf(TIFF.TAG_EXTRA_SAMPLES), "fiveinferior"}, new Object[]{Integer.valueOf(TIFF.TAG_SAMPLE_FORMAT), "sixinferior"}, new Object[]{340, "seveninferior"}, new Object[]{341, "eightinferior"}, new Object[]{Integer.valueOf(TIFF.TAG_TRANSFER_RANGE), "nineinferior"}, new Object[]{343, "centinferior"}, new Object[]{344, "dollarinferior"}, new Object[]{345, "periodinferior"}, new Object[]{Integer.valueOf(IPTC.TAG_CODED_CHARACTER_SET), "commainferior"}};
    private static final CFFExpertSubsetCharset INSTANCE = new CFFExpertSubsetCharset();

    static {
        int gid = 0;
        for (Object[] charsetEntry : CFF_EXPERT_SUBSET_CHARSET_TABLE) {
            int i = gid;
            gid++;
            INSTANCE.addSID(i, ((Integer) charsetEntry[0]).intValue(), charsetEntry[1].toString());
        }
    }

    private CFFExpertSubsetCharset() {
        super(false);
    }

    public static CFFExpertSubsetCharset getInstance() {
        return INSTANCE;
    }
}
