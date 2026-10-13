package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.lang.Validate;
import java.awt.color.ICC_Profile;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/LCMSSanitizerStrategy.class */
final class LCMSSanitizerStrategy implements ICCProfileSanitizer {
    LCMSSanitizerStrategy() {
    }

    @Override // com.twelvemonkeys.imageio.color.ICCProfileSanitizer
    public void fixProfile(ICC_Profile iCC_Profile) {
        Validate.notNull(iCC_Profile, "profile");
    }

    @Override // com.twelvemonkeys.imageio.color.ICCProfileSanitizer
    public boolean validationAltersProfileHeader() {
        return true;
    }
}
