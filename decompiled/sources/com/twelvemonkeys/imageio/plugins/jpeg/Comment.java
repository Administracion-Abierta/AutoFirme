package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/Comment.class */
class Comment extends Segment {
    final String comment;

    private Comment(String str) {
        super(JPEG.COM);
        this.comment = str;
    }

    public String toString() {
        return "COM[" + this.comment + "]";
    }

    public static Segment read(DataInput dataInput, int i) throws IOException {
        byte[] bArr = new byte[i - 2];
        dataInput.readFully(bArr);
        return new Comment(new String(bArr, StandardCharsets.UTF_8));
    }
}
