package com.twelvemonkeys.imageio.stream;

import java.nio.channels.SeekableByteChannel;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/Cache.class */
interface Cache extends SeekableByteChannel {
    void flushBefore(long j);
}
