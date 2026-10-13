package org.java_websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/AbstractWrappedByteChannel.class */
@Deprecated
public class AbstractWrappedByteChannel implements WrappedByteChannel {
    private final ByteChannel channel;

    @Deprecated
    public AbstractWrappedByteChannel(ByteChannel towrap) {
        this.channel = towrap;
    }

    @Deprecated
    public AbstractWrappedByteChannel(WrappedByteChannel towrap) {
        this.channel = towrap;
    }

    @Override // java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer dst) throws IOException {
        return this.channel.read(dst);
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return this.channel.isOpen();
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.channel.close();
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(ByteBuffer src) throws IOException {
        return this.channel.write(src);
    }

    @Override // org.java_websocket.WrappedByteChannel
    public boolean isNeedWrite() {
        return (this.channel instanceof WrappedByteChannel) && ((WrappedByteChannel) this.channel).isNeedWrite();
    }

    @Override // org.java_websocket.WrappedByteChannel
    public void writeMore() throws IOException {
        if (this.channel instanceof WrappedByteChannel) {
            ((WrappedByteChannel) this.channel).writeMore();
        }
    }

    @Override // org.java_websocket.WrappedByteChannel
    public boolean isNeedRead() {
        return (this.channel instanceof WrappedByteChannel) && ((WrappedByteChannel) this.channel).isNeedRead();
    }

    @Override // org.java_websocket.WrappedByteChannel
    public int readMore(ByteBuffer dst) throws IOException {
        if (this.channel instanceof WrappedByteChannel) {
            return ((WrappedByteChannel) this.channel).readMore(dst);
        }
        return 0;
    }

    @Override // org.java_websocket.WrappedByteChannel
    public boolean isBlocking() {
        if (this.channel instanceof SocketChannel) {
            return ((SocketChannel) this.channel).isBlocking();
        }
        if (this.channel instanceof WrappedByteChannel) {
            return ((WrappedByteChannel) this.channel).isBlocking();
        }
        return false;
    }
}
