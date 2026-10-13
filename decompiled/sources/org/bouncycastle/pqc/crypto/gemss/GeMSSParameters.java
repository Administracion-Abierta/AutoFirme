package org.bouncycastle.pqc.crypto.gemss;

import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.twelvemonkeys.imageio.metadata.iptc.IPTC;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/GeMSSParameters.class */
public class GeMSSParameters {
    public static final GeMSSParameters gemss128 = new GeMSSParameters("gemss128", 128, 174, 12, 12, 4, TIFF.TAG_JPEG_INTERCHANGE_FORMAT, 9, 0);
    public static final GeMSSParameters gemss192 = new GeMSSParameters("gemss192", 192, 265, 20, 22, 4, TIFF.TAG_JPEG_INTERCHANGE_FORMAT, 9, 0);
    public static final GeMSSParameters gemss256 = new GeMSSParameters("gemss256", 256, 354, 33, 30, 4, TIFF.TAG_JPEG_INTERCHANGE_FORMAT, 9, 0);
    public static final GeMSSParameters bluegemss128 = new GeMSSParameters("bluegemss128", 128, 175, 14, 13, 4, 129, 7, 0);
    public static final GeMSSParameters bluegemss192 = new GeMSSParameters("bluegemss192", 192, 265, 23, 22, 4, 129, 7, 0);
    public static final GeMSSParameters bluegemss256 = new GeMSSParameters("bluegemss256", 256, 358, 32, 34, 4, 129, 7, 0);
    public static final GeMSSParameters redgemss128 = new GeMSSParameters("redgemss128", 128, 177, 15, 15, 4, 17, 4, 0);
    public static final GeMSSParameters redgemss192 = new GeMSSParameters("redgemss192", 192, 266, 25, 23, 4, 17, 4, 0);
    public static final GeMSSParameters redgemss256 = new GeMSSParameters("redgemss256", 256, 358, 35, 34, 4, 17, 4, 0);
    public static final GeMSSParameters whitegemss128 = new GeMSSParameters("whitegemss128", 128, 175, 12, 12, 3, TIFF.TAG_JPEG_INTERCHANGE_FORMAT, 9, 0);
    public static final GeMSSParameters whitegemss192 = new GeMSSParameters("whitegemss192", 192, 268, 21, 21, 3, TIFF.TAG_JPEG_INTERCHANGE_FORMAT, 9, 0);
    public static final GeMSSParameters whitegemss256 = new GeMSSParameters("whitegemss256", 256, 364, 29, 31, 3, TIFF.TAG_JPEG_INTERCHANGE_FORMAT, 9, 0);
    public static final GeMSSParameters cyangemss128 = new GeMSSParameters("cyangemss128", 128, 177, 13, 14, 3, 129, 7, 0);
    public static final GeMSSParameters cyangemss192 = new GeMSSParameters("cyangemss192", 192, TIFF.TAG_IMAGE_DESCRIPTION, 22, 23, 3, 129, 7, 0);
    public static final GeMSSParameters cyangemss256 = new GeMSSParameters("cyangemss256", 256, 364, 32, 31, 3, 129, 7, 0);
    public static final GeMSSParameters magentagemss128 = new GeMSSParameters("magentagemss128", 128, 178, 15, 15, 3, 17, 4, 0);
    public static final GeMSSParameters magentagemss192 = new GeMSSParameters("magentagemss192", 192, TIFF.TAG_MAKE, 24, 24, 3, 17, 4, 0);
    public static final GeMSSParameters magentagemss256 = new GeMSSParameters("magentagemss256", 256, 366, 33, 33, 3, 17, 4, 0);
    public static final GeMSSParameters fgemss128 = new GeMSSParameters("fgemss128", 128, 266, 11, 10, 1, 129, 7, 0);
    public static final GeMSSParameters fgemss192 = new GeMSSParameters("fgemss192", 192, WinError.ERROR_PROCESS_MODE_ALREADY_BACKGROUND, 18, 18, 1, WinError.ERROR_MULTIPLE_FAULT_VIOLATION, 9, 7);
    public static final GeMSSParameters fgemss256 = new GeMSSParameters("fgemss256", 256, 537, 26, 25, 1, WinError.ERROR_SINGLE_INSTANCE_APP, 10, 7);
    public static final GeMSSParameters dualmodems128 = new GeMSSParameters("dualmodems128", 128, 266, 11, 10, 1, 129, 7, 0);
    public static final GeMSSParameters dualmodems192 = new GeMSSParameters("dualmodems192", 192, WinError.ERROR_PROCESS_MODE_ALREADY_BACKGROUND, 18, 18, 1, 129, 7, 0);
    public static final GeMSSParameters dualmodems256 = new GeMSSParameters("dualmodems256", 256, WinError.ERROR_INVALID_UNWIND_TARGET, 32, 32, 1, 129, 7, 0);
    private static final Integer gemss_128 = Integers.valueOf(257);
    private static final Integer gemss_192 = Integers.valueOf(258);
    private static final Integer gemss_256 = Integers.valueOf(259);
    private static final Integer bluegemss_128 = Integers.valueOf(TIFF.TAG_JPEG_INTERCHANGE_FORMAT);
    private static final Integer bluegemss_192 = Integers.valueOf(TIFF.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
    private static final Integer bluegemss_256 = Integers.valueOf(IPTC.TAG_OBJECT_TYPE_REFERENCE);
    private static final Integer redgemss_128 = Integers.valueOf(WinError.ERROR_COMPRESSION_DISABLED);
    private static final Integer redgemss_192 = Integers.valueOf(WinError.ERROR_CANTFETCHBACKWARDS);
    private static final Integer redgemss_256 = Integers.valueOf(WinError.ERROR_CANTSCROLLBACKWARDS);
    private static final Integer whitegemss_128 = Integers.valueOf(1025);
    private static final Integer whitegemss_192 = Integers.valueOf(1026);
    private static final Integer whitegemss_256 = Integers.valueOf(1027);
    private static final Integer cyangemss_128 = Integers.valueOf(1281);
    private static final Integer cyangemss_192 = Integers.valueOf(1282);
    private static final Integer cyangemss_256 = Integers.valueOf(1283);
    private static final Integer magentagemss_128 = Integers.valueOf(WinNT.WIN32_WINNT_WIN7);
    private static final Integer magentagemss_192 = Integers.valueOf(WinNT.WIN32_WINNT_WIN8);
    private static final Integer magentagemss_256 = Integers.valueOf(WinNT.WIN32_WINNT_WINBLUE);
    private static final Integer fgemss_128 = Integers.valueOf(WinError.ERROR_ACCOUNT_EXPIRED);
    private static final Integer fgemss_192 = Integers.valueOf(WinError.ERROR_REDIRECTOR_HAS_OPEN_HANDLES);
    private static final Integer fgemss_256 = Integers.valueOf(WinError.ERROR_PRINTER_DRIVER_ALREADY_INSTALLED);
    private static final Integer dualmodems_128 = Integers.valueOf(2049);
    private static final Integer dualmodems_192 = Integers.valueOf(2050);
    private static final Integer dualmodems_256 = Integers.valueOf(2051);
    private static final Map<Integer, GeMSSParameters> oidToParams = new HashMap();
    private static final Map<GeMSSParameters, Integer> paramsToOid = new HashMap();
    private final String name;
    private final GeMSSEngine engine;

    private GeMSSParameters(String str, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.name = str;
        this.engine = new GeMSSEngine(i, i2, i3, i4, i5, i6, i7, i8);
    }

    public String getName() {
        return this.name;
    }

    public static GeMSSParameters getParams(Integer num) {
        return oidToParams.get(num);
    }

    public static Integer getID(GeMSSParameters geMSSParameters) {
        return paramsToOid.get(geMSSParameters);
    }

    public byte[] getEncoded() {
        return Pack.intToBigEndian(getID(this).intValue());
    }

    public GeMSSEngine getEngine() {
        return this.engine;
    }

    static {
        oidToParams.put(gemss_128, gemss128);
        oidToParams.put(gemss_192, gemss192);
        oidToParams.put(gemss_256, gemss256);
        oidToParams.put(bluegemss_128, bluegemss128);
        oidToParams.put(bluegemss_192, bluegemss192);
        oidToParams.put(bluegemss_256, bluegemss256);
        oidToParams.put(redgemss_128, redgemss128);
        oidToParams.put(redgemss_192, redgemss192);
        oidToParams.put(redgemss_256, redgemss256);
        oidToParams.put(whitegemss_128, whitegemss128);
        oidToParams.put(whitegemss_192, whitegemss192);
        oidToParams.put(whitegemss_256, whitegemss256);
        oidToParams.put(cyangemss_128, cyangemss128);
        oidToParams.put(cyangemss_192, cyangemss192);
        oidToParams.put(cyangemss_256, cyangemss256);
        oidToParams.put(magentagemss_128, magentagemss128);
        oidToParams.put(magentagemss_192, magentagemss192);
        oidToParams.put(magentagemss_256, magentagemss256);
        oidToParams.put(fgemss_128, fgemss128);
        oidToParams.put(fgemss_192, fgemss192);
        oidToParams.put(fgemss_256, fgemss256);
        oidToParams.put(dualmodems_128, dualmodems128);
        oidToParams.put(dualmodems_192, dualmodems192);
        oidToParams.put(dualmodems_256, dualmodems256);
        paramsToOid.put(gemss128, gemss_128);
        paramsToOid.put(gemss192, gemss_192);
        paramsToOid.put(gemss256, gemss_256);
        paramsToOid.put(bluegemss128, bluegemss_128);
        paramsToOid.put(bluegemss192, bluegemss_192);
        paramsToOid.put(bluegemss256, bluegemss_256);
        paramsToOid.put(redgemss128, redgemss_128);
        paramsToOid.put(redgemss192, redgemss_192);
        paramsToOid.put(redgemss256, redgemss_256);
        paramsToOid.put(whitegemss128, whitegemss_128);
        paramsToOid.put(whitegemss192, whitegemss_192);
        paramsToOid.put(whitegemss256, whitegemss_256);
        paramsToOid.put(cyangemss128, cyangemss_128);
        paramsToOid.put(cyangemss192, cyangemss_192);
        paramsToOid.put(cyangemss256, cyangemss_256);
        paramsToOid.put(magentagemss128, magentagemss_128);
        paramsToOid.put(magentagemss192, magentagemss_192);
        paramsToOid.put(magentagemss256, magentagemss_256);
        paramsToOid.put(fgemss128, fgemss_128);
        paramsToOid.put(fgemss192, fgemss_192);
        paramsToOid.put(fgemss256, fgemss_256);
        paramsToOid.put(dualmodems128, dualmodems_128);
        paramsToOid.put(dualmodems192, dualmodems_192);
        paramsToOid.put(dualmodems256, dualmodems_256);
    }
}
