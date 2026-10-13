package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.io.DataInput;
import java.io.IOException;
import javax.imageio.IIOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/RestartInterval.class */
class RestartInterval extends Segment {
    final int interval;

    private RestartInterval(int i) {
        super(JPEG.DRI);
        this.interval = i;
    }

    public String toString() {
        return "DRI[" + this.interval + "]";
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public static RestartInterval read(DataInput dataInput, int i) throws IIOException, IOException {
        if (i != 4) {
            throw new IIOException("Unexpected length of DRI segment: " + i);
        }
        return new RestartInterval(dataInput.readUnsignedShort());
    }
}
