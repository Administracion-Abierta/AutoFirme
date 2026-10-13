package org.apache.pdfbox.cos;

import java.io.Closeable;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.FilterFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessInputStream;
import org.apache.pdfbox.io.RandomAccessOutputStream;
import org.apache.pdfbox.io.ScratchFile;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSStream.class */
public class COSStream extends COSDictionary implements Closeable {
    private RandomAccess randomAccess;
    private final ScratchFile scratchFile;
    private boolean isWriting;
    private static final Log LOG = LogFactory.getLog(COSStream.class);

    public COSStream() {
        this(ScratchFile.getMainMemoryOnlyInstance());
    }

    public COSStream(ScratchFile scratchFile) {
        setInt(COSName.LENGTH, 0);
        this.scratchFile = scratchFile != null ? scratchFile : ScratchFile.getMainMemoryOnlyInstance();
    }

    private void checkClosed() throws IOException {
        if (this.randomAccess != null && this.randomAccess.isClosed()) {
            throw new IOException("COSStream has been closed and cannot be read. Perhaps its enclosing PDDocument has been closed?");
        }
    }

    @Deprecated
    public InputStream getFilteredStream() throws IOException {
        return createRawInputStream();
    }

    private void ensureRandomAccessExists(boolean forInputStream) throws IOException {
        if (this.randomAccess == null) {
            if (forInputStream && LOG.isDebugEnabled()) {
                LOG.debug("Create InputStream called without data being written before to stream.");
            }
            this.randomAccess = this.scratchFile.createBuffer();
        }
    }

    public InputStream createRawInputStream() throws IOException {
        checkClosed();
        if (this.isWriting) {
            throw new IllegalStateException("Cannot read while there is an open stream writer");
        }
        ensureRandomAccessExists(true);
        return new RandomAccessInputStream(this.randomAccess);
    }

    @Deprecated
    public InputStream getUnfilteredStream() throws IOException {
        return createInputStream();
    }

    public COSInputStream createInputStream() throws IOException {
        return createInputStream(DecodeOptions.DEFAULT);
    }

    public COSInputStream createInputStream(DecodeOptions options) throws IOException {
        checkClosed();
        if (this.isWriting) {
            throw new IllegalStateException("Cannot read while there is an open stream writer");
        }
        ensureRandomAccessExists(true);
        InputStream input = new RandomAccessInputStream(this.randomAccess);
        return COSInputStream.create(getFilterList(), this, input, this.scratchFile, options);
    }

    @Deprecated
    public OutputStream createUnfilteredStream() throws IOException {
        return createOutputStream();
    }

    public OutputStream createOutputStream() throws IOException {
        return createOutputStream(null);
    }

    public OutputStream createOutputStream(COSBase filters) throws IOException {
        checkClosed();
        if (this.isWriting) {
            throw new IllegalStateException("Cannot have more than one open stream writer.");
        }
        if (filters != null) {
            setItem(COSName.FILTER, filters);
        }
        IOUtils.closeQuietly(this.randomAccess);
        this.randomAccess = this.scratchFile.createBuffer();
        OutputStream randomOut = new RandomAccessOutputStream(this.randomAccess);
        OutputStream cosOut = new COSOutputStream(getFilterList(), this, randomOut, this.scratchFile);
        this.isWriting = true;
        return new FilterOutputStream(cosOut) { // from class: org.apache.pdfbox.cos.COSStream.1
            @Override // java.io.FilterOutputStream, java.io.OutputStream
            public void write(byte[] b, int off, int len) throws IOException {
                this.out.write(b, off, len);
            }

            @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                super.close();
                COSStream.this.setInt(COSName.LENGTH, (int) COSStream.this.randomAccess.length());
                COSStream.this.isWriting = false;
            }
        };
    }

    @Deprecated
    public OutputStream createFilteredStream() throws IOException {
        return createRawOutputStream();
    }

    public OutputStream createRawOutputStream() throws IOException {
        checkClosed();
        if (this.isWriting) {
            throw new IllegalStateException("Cannot have more than one open stream writer.");
        }
        IOUtils.closeQuietly(this.randomAccess);
        this.randomAccess = this.scratchFile.createBuffer();
        OutputStream out = new RandomAccessOutputStream(this.randomAccess);
        this.isWriting = true;
        return new FilterOutputStream(out) { // from class: org.apache.pdfbox.cos.COSStream.2
            @Override // java.io.FilterOutputStream, java.io.OutputStream
            public void write(byte[] b, int off, int len) throws IOException {
                this.out.write(b, off, len);
            }

            @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                super.close();
                COSStream.this.setInt(COSName.LENGTH, (int) COSStream.this.randomAccess.length());
                COSStream.this.isWriting = false;
            }
        };
    }

    private List<Filter> getFilterList() throws IOException {
        List<Filter> filterList;
        COSBase filters = getFilters();
        if (filters instanceof COSName) {
            filterList = new ArrayList<>(1);
            filterList.add(FilterFactory.INSTANCE.getFilter((COSName) filters));
        } else if (filters instanceof COSArray) {
            COSArray filterArray = (COSArray) filters;
            filterList = new ArrayList<>(filterArray.size());
            for (int i = 0; i < filterArray.size(); i++) {
                COSBase base = filterArray.get(i);
                if (!(base instanceof COSName)) {
                    throw new IOException("Forbidden type in filter array: " + (base == null ? "null" : base.getClass().getName()));
                }
                filterList.add(FilterFactory.INSTANCE.getFilter((COSName) base));
            }
        } else {
            filterList = new ArrayList<>();
        }
        return filterList;
    }

    public long getLength() {
        if (this.isWriting) {
            throw new IllegalStateException("There is an open OutputStream associated with this COSStream. It must be closed before querying the length of this COSStream.");
        }
        return getInt(COSName.LENGTH, 0);
    }

    public COSBase getFilters() {
        return getDictionaryObject(COSName.FILTER);
    }

    @Deprecated
    public void setFilters(COSBase filters) throws IOException {
        setItem(COSName.FILTER, filters);
    }

    @Deprecated
    public String getString() {
        return toTextString();
    }

    public String toTextString() {
        InputStream input = null;
        try {
            try {
                input = createInputStream();
                byte[] array = IOUtils.toByteArray(input);
                IOUtils.closeQuietly(input);
                COSString string = new COSString(array);
                return string.getString();
            } catch (IOException e) {
                LOG.debug("An exception occurred trying to get the content - returning empty string instead", e);
                IOUtils.closeQuietly(input);
                return "";
            }
        } catch (Throwable th) {
            IOUtils.closeQuietly(input);
            throw th;
        }
    }

    @Override // org.apache.pdfbox.cos.COSDictionary, org.apache.pdfbox.cos.COSBase
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromStream(this);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.randomAccess != null) {
            this.randomAccess.close();
        }
    }
}
