package es.gob.afirma.keystores.jmulticard.ui;

import java.util.TimerTask;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/jmulticard/ui/ResetCacheTimerTask.class */
final class ResetCacheTimerTask extends TimerTask {
    private final transient CacheElement element;

    ResetCacheTimerTask(CacheElement cacheElement) {
        this.element = cacheElement;
    }

    @Override // java.util.TimerTask, java.lang.Runnable
    public void run() {
        if (this.element != null) {
            this.element.reset();
        }
    }
}
