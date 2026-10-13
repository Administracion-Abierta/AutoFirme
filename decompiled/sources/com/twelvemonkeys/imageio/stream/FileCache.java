package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/stream/FileCache.class */
final class FileCache implements Cache {
    static final int BLOCK_SIZE = 8192;
    private final FileChannel cache;
    private final ReadableByteChannel channel;

    FileCache(InputStream inputStream, File file) throws IOException {
        this(Channels.newChannel((InputStream) Validate.notNull(inputStream, "stream")), file);
    }

    public FileCache(ReadableByteChannel readableByteChannel, File file) throws IOException {
        Path pathCreateTempFile;
        this.channel = (ReadableByteChannel) Validate.notNull(readableByteChannel, "channel");
        Validate.isTrue(file == null || file.isDirectory(), file, "%s is not a directory");
        if (file == null) {
            pathCreateTempFile = Files.createTempFile("imageio", ".tmp", new FileAttribute[0]);
        } else {
            pathCreateTempFile = Files.createTempFile(file.toPath(), "imageio", ".tmp", new FileAttribute[0]);
        }
        this.cache = FileChannel.open(pathCreateTempFile, StandardOpenOption.DELETE_ON_CLOSE, StandardOpenOption.READ, StandardOpenOption.WRITE);
    }

    void fetch() throws IOException {
        while (this.cache.position() >= this.cache.size() && this.cache.transferFrom(this.channel, this.cache.size(), Math.max(this.cache.position() - this.cache.size(), 8192L)) > 0) {
        }
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return this.channel.isOpen();
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.cache.close();
    }

    @Override // java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer byteBuffer) throws IOException {
        fetch();
        if (this.cache.position() >= this.cache.size()) {
            return -1;
        }
        return this.cache.read(byteBuffer);
    }

    @Override // java.nio.channels.SeekableByteChannel
    public long position() throws IOException {
        return this.cache.position();
    }

    @Override // java.nio.channels.SeekableByteChannel
    public SeekableByteChannel position(long j) throws IOException {
        this.cache.position(j);
        return this;
    }

    @Override // java.nio.channels.SeekableByteChannel
    public long size() {
        return -1L;
    }

    @Override // java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer byteBuffer) {
        throw new NonWritableChannelException();
    }

    @Override // java.nio.channels.SeekableByteChannel
    public SeekableByteChannel truncate(long j) {
        throw new NonWritableChannelException();
    }

    @Override // com.twelvemonkeys.imageio.stream.Cache
    public void flushBefore(long j) {
    }
}
