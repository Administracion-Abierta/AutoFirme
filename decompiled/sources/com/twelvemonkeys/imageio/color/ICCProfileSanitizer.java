package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.lang.SystemUtil;
import java.awt.color.ICC_Profile;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ICCProfileSanitizer.class */
interface ICCProfileSanitizer {
    void fixProfile(ICC_Profile iCC_Profile);

    boolean validationAltersProfileHeader();

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ICCProfileSanitizer$Factory.class */
    public static class Factory {
        static ICCProfileSanitizer get() {
            ICCProfileSanitizer lCMSSanitizerStrategy;
            String property = System.getProperty("sun.java2d.cmm");
            if ("sun.java2d.cmm.kcms.KcmsServiceProvider".equals(property) && SystemUtil.isClassAvailable("sun.java2d.cmm.kcms.CMM")) {
                lCMSSanitizerStrategy = new KCMSSanitizerStrategy();
            } else if (("sun.java2d.cmm.lcms.LcmsServiceProvider".equals(property) && SystemUtil.isClassAvailable("sun.java2d.cmm.lcms.LCMS")) || SystemUtil.isClassAvailable("java.util.stream.Stream") || (SystemUtil.isClassAvailable("java.lang.invoke.CallSite") && !SystemUtil.isClassAvailable("sun.java2d.cmm.kcms.CMM"))) {
                lCMSSanitizerStrategy = new LCMSSanitizerStrategy();
            } else {
                lCMSSanitizerStrategy = new KCMSSanitizerStrategy();
            }
            if (ColorSpaces.DEBUG) {
                System.out.println("ICC ProfileCleaner instance: " + lCMSSanitizerStrategy.getClass().getName());
            }
            return lCMSSanitizerStrategy;
        }
    }
}
