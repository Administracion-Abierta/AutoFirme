package com.twelvemonkeys.imageio.plugins.jpeg;

import java.io.DataInput;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/ICCProfile.class */
final class ICCProfile extends Application {
    private ICCProfile(byte[] bArr) {
        super(65506, "ICC_PROFILE", bArr);
    }

    @Override // com.twelvemonkeys.imageio.plugins.jpeg.Application
    public String toString() {
        return "ICC_PROFILE[" + ((int) this.data[12]) + "/" + ((int) this.data[13]) + " length: " + this.data.length + "]";
    }

    public static ICCProfile read(DataInput dataInput, int i) throws IOException {
        byte[] bArr = new byte[i - 2];
        dataInput.readFully(bArr);
        return new ICCProfile(bArr);
    }
}
