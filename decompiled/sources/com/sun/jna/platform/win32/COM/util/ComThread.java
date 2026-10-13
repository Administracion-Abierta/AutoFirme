package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinNT;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/ComThread.class */
public class ComThread {
    private static ThreadLocal<Boolean> isCOMThread = new ThreadLocal<>();
    ExecutorService executor;
    Runnable firstTask;
    boolean requiresInitialisation;
    long timeoutMilliseconds;
    Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public ComThread(String threadName, long timeoutMilliseconds, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this(threadName, timeoutMilliseconds, uncaughtExceptionHandler, 0);
    }

    public ComThread(final String threadName, long timeoutMilliseconds, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, final int coinitialiseExFlag) {
        this.requiresInitialisation = true;
        this.timeoutMilliseconds = timeoutMilliseconds;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        this.firstTask = new Runnable() { // from class: com.sun.jna.platform.win32.COM.util.ComThread.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    WinNT.HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, coinitialiseExFlag);
                    ComThread.isCOMThread.set(true);
                    COMUtils.checkRC(hr);
                    ComThread.this.requiresInitialisation = false;
                } catch (Throwable t) {
                    ComThread.this.uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
                }
            }
        };
        this.executor = Executors.newSingleThreadExecutor(new ThreadFactory() { // from class: com.sun.jna.platform.win32.COM.util.ComThread.2
            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable r) {
                if (!ComThread.this.requiresInitialisation) {
                    throw new RuntimeException("ComThread executor has a problem.");
                }
                Thread thread = new Thread(r, threadName);
                thread.setDaemon(true);
                thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { // from class: com.sun.jna.platform.win32.COM.util.ComThread.2.1
                    @Override // java.lang.Thread.UncaughtExceptionHandler
                    public void uncaughtException(Thread t, Throwable e) {
                        ComThread.this.requiresInitialisation = true;
                        ComThread.this.uncaughtExceptionHandler.uncaughtException(t, e);
                    }
                });
                return thread;
            }
        });
    }

    public void terminate(long timeoutMilliseconds) {
        try {
            this.executor.submit(new Runnable() { // from class: com.sun.jna.platform.win32.COM.util.ComThread.3
                @Override // java.lang.Runnable
                public void run() {
                    Ole32.INSTANCE.CoUninitialize();
                }
            }).get(timeoutMilliseconds, TimeUnit.MILLISECONDS);
            this.executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        } catch (TimeoutException e3) {
            this.executor.shutdownNow();
        }
    }

    protected void finalize() throws Throwable {
        if (!this.executor.isShutdown()) {
            terminate(100L);
        }
    }

    static void setComThread(boolean value) {
        isCOMThread.set(Boolean.valueOf(value));
    }

    public <T> T execute(Callable<T> task) throws ExecutionException, InterruptedException, TimeoutException {
        Boolean comThread = isCOMThread.get();
        if (comThread == null) {
            comThread = false;
        }
        if (comThread.booleanValue()) {
            try {
                return task.call();
            } catch (Exception ex) {
                throw new ExecutionException(ex);
            }
        }
        if (this.requiresInitialisation) {
            this.executor.execute(this.firstTask);
        }
        return this.executor.submit(task).get(this.timeoutMilliseconds, TimeUnit.MILLISECONDS);
    }
}
