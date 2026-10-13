package es.gob.jmulticard.jse.provider;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/JMulticardKeyStoreImpl.class */
public final class JMulticardKeyStoreImpl extends DnieKeyStoreImpl {
    @Override // es.gob.jmulticard.jse.provider.DnieKeyStoreImpl
    protected boolean isAnotherCardsAllowed() {
        return true;
    }
}
