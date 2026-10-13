package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IComEnumVariantIterator.class */
public class IComEnumVariantIterator implements Iterable<Variant.VARIANT>, Iterator<Variant.VARIANT>, Closeable {
    private Variant.VARIANT nextValue;
    private EnumVariant backingIteration;

    public static IComEnumVariantIterator wrap(com.sun.jna.platform.win32.COM.util.IDispatch dispatch) {
        PointerByReference pbr = new PointerByReference();
        IUnknown unknwn = (IUnknown) dispatch.getProperty(IUnknown.class, OaIdl.DISPID_NEWENUM, new Object[0]);
        unknwn.QueryInterface(EnumVariant.REFIID, pbr);
        unknwn.Release();
        EnumVariant variant = new EnumVariant(pbr.getValue());
        return new IComEnumVariantIterator(variant);
    }

    public IComEnumVariantIterator(EnumVariant backingIteration) {
        this.backingIteration = backingIteration;
        retrieveNext();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.nextValue != null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Iterator
    public Variant.VARIANT next() {
        Variant.VARIANT current = this.nextValue;
        retrieveNext();
        return current;
    }

    private void retrieveNext() {
        if (this.backingIteration == null) {
            return;
        }
        Variant.VARIANT[] variants = this.backingIteration.Next(1);
        if (variants.length == 0) {
            close();
        } else {
            this.nextValue = variants[0];
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.backingIteration != null) {
            this.nextValue = null;
            this.backingIteration.Release();
            this.backingIteration = null;
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override // java.lang.Iterable
    public Iterator<Variant.VARIANT> iterator() {
        return this;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
