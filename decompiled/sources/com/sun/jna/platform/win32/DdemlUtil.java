package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32Util.MessageLoopThread.Handler;
import com.sun.jna.win32.W32APIOptions;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil.class */
public abstract class DdemlUtil {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$AdvdataHandler.class */
    public interface AdvdataHandler {
        int onAdvdata(int i, int i2, Ddeml.HCONV hconv, Ddeml.HSZ hsz, Ddeml.HSZ hsz2, Ddeml.HDDEDATA hddedata);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$AdvreqHandler.class */
    public interface AdvreqHandler {
        Ddeml.HDDEDATA onAdvreq(int i, int i2, Ddeml.HCONV hconv, Ddeml.HSZ hsz, Ddeml.HSZ hsz2, int i3);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$AdvstartHandler.class */
    public interface AdvstartHandler {
        boolean onAdvstart(int i, int i2, Ddeml.HCONV hconv, Ddeml.HSZ hsz, Ddeml.HSZ hsz2);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$AdvstopHandler.class */
    public interface AdvstopHandler {
        void onAdvstop(int i, int i2, Ddeml.HCONV hconv, Ddeml.HSZ hsz, Ddeml.HSZ hsz2);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$ConnectConfirmHandler.class */
    public interface ConnectConfirmHandler {
        void onConnectConfirm(int i, Ddeml.HCONV hconv, Ddeml.HSZ hsz, Ddeml.HSZ hsz2, boolean z);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$ConnectHandler.class */
    public interface ConnectHandler {
        boolean onConnect(int i, Ddeml.HSZ hsz, Ddeml.HSZ hsz2, Ddeml.CONVCONTEXT convcontext, boolean z);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$DisconnectHandler.class */
    public interface DisconnectHandler {
        void onDisconnect(int i, Ddeml.HCONV hconv, boolean z);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$ErrorHandler.class */
    public interface ErrorHandler {
        void onError(int i, Ddeml.HCONV hconv, int i2);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$ExecuteHandler.class */
    public interface ExecuteHandler {
        int onExecute(int i, Ddeml.HCONV hconv, Ddeml.HSZ hsz, Ddeml.HDDEDATA hddedata);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$IDdeClient.class */
    public interface IDdeClient extends Closeable {
        Integer getInstanceIdentitifier();

        void initialize(int i) throws DdemlException;

        Ddeml.HSZ createStringHandle(String str) throws DdemlException;

        String queryString(Ddeml.HSZ hsz) throws DdemlException;

        boolean freeStringHandle(Ddeml.HSZ hsz);

        boolean keepStringHandle(Ddeml.HSZ hsz);

        void nameService(Ddeml.HSZ hsz, int i) throws DdemlException;

        void nameService(String str, int i) throws DdemlException;

        int getLastError();

        IDdeConnection connect(Ddeml.HSZ hsz, Ddeml.HSZ hsz2, Ddeml.CONVCONTEXT convcontext);

        IDdeConnection connect(String str, String str2, Ddeml.CONVCONTEXT convcontext);

        Ddeml.HDDEDATA createDataHandle(Pointer pointer, int i, int i2, Ddeml.HSZ hsz, int i3, int i4);

        void freeDataHandle(Ddeml.HDDEDATA hddedata);

        Ddeml.HDDEDATA addData(Ddeml.HDDEDATA hddedata, Pointer pointer, int i, int i2);

        int getData(Ddeml.HDDEDATA hddedata, Pointer pointer, int i, int i2);

        Pointer accessData(Ddeml.HDDEDATA hddedata, WinDef.DWORDByReference dWORDByReference);

        void unaccessData(Ddeml.HDDEDATA hddedata);

        void postAdvise(Ddeml.HSZ hsz, Ddeml.HSZ hsz2);

        void postAdvise(String str, String str2);

        void abandonTransactions();

        IDdeConnectionList connectList(Ddeml.HSZ hsz, Ddeml.HSZ hsz2, IDdeConnectionList iDdeConnectionList, Ddeml.CONVCONTEXT convcontext);

        IDdeConnectionList connectList(String str, String str2, IDdeConnectionList iDdeConnectionList, Ddeml.CONVCONTEXT convcontext);

        boolean enableCallback(int i);

        boolean uninitialize();

        IDdeConnection wrap(Ddeml.HCONV hconv);

        void registerAdvstartHandler(AdvstartHandler advstartHandler);

        void unregisterAdvstartHandler(AdvstartHandler advstartHandler);

        void registerAdvstopHandler(AdvstopHandler advstopHandler);

        void unregisterAdvstopHandler(AdvstopHandler advstopHandler);

        void registerConnectHandler(ConnectHandler connectHandler);

        void unregisterConnectHandler(ConnectHandler connectHandler);

        void registerAdvReqHandler(AdvreqHandler advreqHandler);

        void unregisterAdvReqHandler(AdvreqHandler advreqHandler);

        void registerRequestHandler(RequestHandler requestHandler);

        void unregisterRequestHandler(RequestHandler requestHandler);

        void registerWildconnectHandler(WildconnectHandler wildconnectHandler);

        void unregisterWildconnectHandler(WildconnectHandler wildconnectHandler);

        void registerAdvdataHandler(AdvdataHandler advdataHandler);

        void unregisterAdvdataHandler(AdvdataHandler advdataHandler);

        void registerExecuteHandler(ExecuteHandler executeHandler);

        void unregisterExecuteHandler(ExecuteHandler executeHandler);

        void registerPokeHandler(PokeHandler pokeHandler);

        void unregisterPokeHandler(PokeHandler pokeHandler);

        void registerConnectConfirmHandler(ConnectConfirmHandler connectConfirmHandler);

        void unregisterConnectConfirmHandler(ConnectConfirmHandler connectConfirmHandler);

        void registerDisconnectHandler(DisconnectHandler disconnectHandler);

        void unregisterDisconnectHandler(DisconnectHandler disconnectHandler);

        void registerErrorHandler(ErrorHandler errorHandler);

        void unregisterErrorHandler(ErrorHandler errorHandler);

        void registerRegisterHandler(RegisterHandler registerHandler);

        void unregisterRegisterHandler(RegisterHandler registerHandler);

        void registerXactCompleteHandler(XactCompleteHandler xactCompleteHandler);

        void unregisterXactCompleteHandler(XactCompleteHandler xactCompleteHandler);

        void registerUnregisterHandler(UnregisterHandler unregisterHandler);

        void unregisterUnregisterHandler(UnregisterHandler unregisterHandler);

        void registerMonitorHandler(MonitorHandler monitorHandler);

        void unregisterMonitorHandler(MonitorHandler monitorHandler);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$IDdeConnection.class */
    public interface IDdeConnection extends Closeable {
        Ddeml.HCONV getConv();

        void execute(String str, int i, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        void poke(Pointer pointer, int i, Ddeml.HSZ hsz, int i2, int i3, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        void poke(Pointer pointer, int i, String str, int i2, int i3, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        Ddeml.HDDEDATA request(Ddeml.HSZ hsz, int i, int i2, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        Ddeml.HDDEDATA request(String str, int i, int i2, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        Ddeml.HDDEDATA clientTransaction(Pointer pointer, int i, Ddeml.HSZ hsz, int i2, int i3, int i4, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        Ddeml.HDDEDATA clientTransaction(Pointer pointer, int i, String str, int i2, int i3, int i4, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        void advstart(Ddeml.HSZ hsz, int i, int i2, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        void advstart(String str, int i, int i2, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        void advstop(Ddeml.HSZ hsz, int i, int i2, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        void advstop(String str, int i, int i2, WinDef.DWORDByReference dWORDByReference, BaseTSD.DWORD_PTR dword_ptr);

        void abandonTransaction(int i);

        void abandonTransactions();

        void impersonateClient();

        @Override // java.io.Closeable, java.lang.AutoCloseable
        void close();

        void reconnect();

        boolean enableCallback(int i);

        void setUserHandle(int i, BaseTSD.DWORD_PTR dword_ptr) throws DdemlException;

        Ddeml.CONVINFO queryConvInfo(int i) throws DdemlException;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$IDdeConnectionList.class */
    public interface IDdeConnectionList extends Closeable {
        Ddeml.HCONVLIST getHandle();

        IDdeConnection queryNextServer(IDdeConnection iDdeConnection);

        @Override // java.io.Closeable, java.lang.AutoCloseable
        void close();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$MonitorHandler.class */
    public interface MonitorHandler {
        void onMonitor(int i, Ddeml.HDDEDATA hddedata, int i2);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$PokeHandler.class */
    public interface PokeHandler {
        int onPoke(int i, int i2, Ddeml.HCONV hconv, Ddeml.HSZ hsz, Ddeml.HSZ hsz2, Ddeml.HDDEDATA hddedata);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$RegisterHandler.class */
    public interface RegisterHandler {
        void onRegister(int i, Ddeml.HSZ hsz, Ddeml.HSZ hsz2);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$RequestHandler.class */
    public interface RequestHandler {
        Ddeml.HDDEDATA onRequest(int i, int i2, Ddeml.HCONV hconv, Ddeml.HSZ hsz, Ddeml.HSZ hsz2);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$UnregisterHandler.class */
    public interface UnregisterHandler {
        void onUnregister(int i, Ddeml.HSZ hsz, Ddeml.HSZ hsz2);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$WildconnectHandler.class */
    public interface WildconnectHandler {
        List<Ddeml.HSZPAIR> onWildconnect(int i, Ddeml.HSZ hsz, Ddeml.HSZ hsz2, Ddeml.CONVCONTEXT convcontext, boolean z);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$XactCompleteHandler.class */
    public interface XactCompleteHandler {
        void onXactComplete(int i, int i2, Ddeml.HCONV hconv, Ddeml.HSZ hsz, Ddeml.HSZ hsz2, Ddeml.HDDEDATA hddedata, BaseTSD.ULONG_PTR ulong_ptr, BaseTSD.ULONG_PTR ulong_ptr2);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$StandaloneDdeClient.class */
    public static class StandaloneDdeClient implements IDdeClient, Closeable {
        private final User32Util.MessageLoopThread messageLoop = new User32Util.MessageLoopThread();
        private final IDdeClient ddeClient = new DdeClient();
        private final IDdeClient clientDelegate;

        public StandaloneDdeClient() {
            User32Util.MessageLoopThread messageLoopThread = this.messageLoop;
            messageLoopThread.getClass();
            IDdeClient messageLoopHandler = (IDdeClient) Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[]{IDdeClient.class}, messageLoopThread.new Handler(this.ddeClient));
            this.clientDelegate = (IDdeClient) Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[]{IDdeClient.class}, new MessageLoopWrapper(this.messageLoop, messageLoopHandler));
            this.messageLoop.setDaemon(true);
            this.messageLoop.start();
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public Integer getInstanceIdentitifier() {
            return this.ddeClient.getInstanceIdentitifier();
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void initialize(int afCmd) throws DdemlException {
            this.clientDelegate.initialize(afCmd);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public Ddeml.HSZ createStringHandle(String value) throws DdemlException {
            return this.clientDelegate.createStringHandle(value);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void nameService(Ddeml.HSZ name, int afCmd) throws DdemlException {
            this.clientDelegate.nameService(name, afCmd);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public int getLastError() {
            return this.clientDelegate.getLastError();
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public IDdeConnection connect(Ddeml.HSZ service, Ddeml.HSZ topic, Ddeml.CONVCONTEXT convcontext) {
            return this.clientDelegate.connect(service, topic, convcontext);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public String queryString(Ddeml.HSZ value) throws DdemlException {
            return this.clientDelegate.queryString(value);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public Ddeml.HDDEDATA createDataHandle(Pointer pSrc, int cb, int cbOff, Ddeml.HSZ hszItem, int wFmt, int afCmd) {
            return this.clientDelegate.createDataHandle(pSrc, cb, cbOff, hszItem, wFmt, afCmd);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void freeDataHandle(Ddeml.HDDEDATA hData) {
            this.clientDelegate.freeDataHandle(hData);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA hData, Pointer pSrc, int cb, int cbOff) {
            return this.clientDelegate.addData(hData, pSrc, cb, cbOff);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public int getData(Ddeml.HDDEDATA hData, Pointer pDst, int cbMax, int cbOff) {
            return this.clientDelegate.getData(hData, pDst, cbMax, cbOff);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public Pointer accessData(Ddeml.HDDEDATA hData, WinDef.DWORDByReference pcbDataSize) {
            return this.clientDelegate.accessData(hData, pcbDataSize);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unaccessData(Ddeml.HDDEDATA hData) {
            this.clientDelegate.unaccessData(hData);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void postAdvise(Ddeml.HSZ hszTopic, Ddeml.HSZ hszItem) {
            this.clientDelegate.postAdvise(hszTopic, hszItem);
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.clientDelegate.uninitialize();
            this.messageLoop.exit();
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public boolean freeStringHandle(Ddeml.HSZ value) {
            return this.clientDelegate.freeStringHandle(value);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public boolean keepStringHandle(Ddeml.HSZ value) {
            return this.clientDelegate.keepStringHandle(value);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void abandonTransactions() {
            this.clientDelegate.abandonTransactions();
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public IDdeConnectionList connectList(Ddeml.HSZ service, Ddeml.HSZ topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
            return this.clientDelegate.connectList(service, topic, existingList, ctx);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public boolean enableCallback(int wCmd) {
            return this.clientDelegate.enableCallback(wCmd);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public IDdeConnection wrap(Ddeml.HCONV conv) {
            return this.clientDelegate.wrap(conv);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public IDdeConnection connect(String service, String topic, Ddeml.CONVCONTEXT convcontext) {
            return this.clientDelegate.connect(service, topic, convcontext);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public boolean uninitialize() {
            return this.clientDelegate.uninitialize();
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void postAdvise(String hszTopic, String hszItem) {
            this.clientDelegate.postAdvise(hszTopic, hszItem);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public IDdeConnectionList connectList(String service, String topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
            return this.clientDelegate.connectList(service, topic, existingList, ctx);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void nameService(String name, int afCmd) throws DdemlException {
            this.clientDelegate.nameService(name, afCmd);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerAdvstartHandler(AdvstartHandler handler) {
            this.clientDelegate.registerAdvstartHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterAdvstartHandler(AdvstartHandler handler) {
            this.clientDelegate.unregisterAdvstartHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerAdvstopHandler(AdvstopHandler handler) {
            this.clientDelegate.registerAdvstopHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterAdvstopHandler(AdvstopHandler handler) {
            this.clientDelegate.unregisterAdvstopHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerConnectHandler(ConnectHandler handler) {
            this.clientDelegate.registerConnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterConnectHandler(ConnectHandler handler) {
            this.clientDelegate.unregisterConnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerAdvReqHandler(AdvreqHandler handler) {
            this.clientDelegate.registerAdvReqHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterAdvReqHandler(AdvreqHandler handler) {
            this.clientDelegate.unregisterAdvReqHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerRequestHandler(RequestHandler handler) {
            this.clientDelegate.registerRequestHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterRequestHandler(RequestHandler handler) {
            this.clientDelegate.unregisterRequestHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerWildconnectHandler(WildconnectHandler handler) {
            this.clientDelegate.registerWildconnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterWildconnectHandler(WildconnectHandler handler) {
            this.clientDelegate.unregisterWildconnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerAdvdataHandler(AdvdataHandler handler) {
            this.clientDelegate.registerAdvdataHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterAdvdataHandler(AdvdataHandler handler) {
            this.clientDelegate.unregisterAdvdataHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerExecuteHandler(ExecuteHandler handler) {
            this.clientDelegate.registerExecuteHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterExecuteHandler(ExecuteHandler handler) {
            this.clientDelegate.unregisterExecuteHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerPokeHandler(PokeHandler handler) {
            this.clientDelegate.registerPokeHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterPokeHandler(PokeHandler handler) {
            this.clientDelegate.unregisterPokeHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerConnectConfirmHandler(ConnectConfirmHandler handler) {
            this.clientDelegate.registerConnectConfirmHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterConnectConfirmHandler(ConnectConfirmHandler handler) {
            this.clientDelegate.unregisterConnectConfirmHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerDisconnectHandler(DisconnectHandler handler) {
            this.clientDelegate.registerDisconnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterDisconnectHandler(DisconnectHandler handler) {
            this.clientDelegate.unregisterDisconnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerErrorHandler(ErrorHandler handler) {
            this.clientDelegate.registerErrorHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterErrorHandler(ErrorHandler handler) {
            this.clientDelegate.unregisterErrorHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerRegisterHandler(RegisterHandler handler) {
            this.clientDelegate.registerRegisterHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterRegisterHandler(RegisterHandler handler) {
            this.clientDelegate.unregisterRegisterHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerXactCompleteHandler(XactCompleteHandler handler) {
            this.clientDelegate.registerXactCompleteHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterXactCompleteHandler(XactCompleteHandler handler) {
            this.clientDelegate.unregisterXactCompleteHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerUnregisterHandler(UnregisterHandler handler) {
            this.clientDelegate.registerUnregisterHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterUnregisterHandler(UnregisterHandler handler) {
            this.clientDelegate.unregisterUnregisterHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerMonitorHandler(MonitorHandler handler) {
            this.clientDelegate.registerMonitorHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterMonitorHandler(MonitorHandler handler) {
            this.clientDelegate.unregisterMonitorHandler(handler);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$MessageLoopWrapper.class */
    private static class MessageLoopWrapper implements InvocationHandler {
        private final Object delegate;
        private final User32Util.MessageLoopThread loopThread;

        public MessageLoopWrapper(User32Util.MessageLoopThread thread, Object delegate) {
            this.loopThread = thread;
            this.delegate = delegate;
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
            try {
                Object result = method.invoke(this.delegate, args);
                Class<?> wrapClass = null;
                if (result instanceof IDdeConnection) {
                    wrapClass = IDdeConnection.class;
                } else if (result instanceof IDdeConnectionList) {
                    wrapClass = IDdeConnectionList.class;
                } else if (result instanceof IDdeClient) {
                    wrapClass = IDdeClient.class;
                }
                if (wrapClass != null && method.getReturnType().isAssignableFrom(wrapClass)) {
                    result = wrap(result, wrapClass);
                }
                return result;
            } catch (InvocationTargetException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof Exception) {
                    throw ((Exception) cause);
                }
                throw ex;
            }
        }

        private <V> V wrap(V v, Class cls) {
            User32Util.MessageLoopThread messageLoopThread = this.loopThread;
            messageLoopThread.getClass();
            return (V) Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[]{cls}, new MessageLoopWrapper(this.loopThread, Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[]{cls}, messageLoopThread.new Handler(v))));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$DdeConnection.class */
    public static class DdeConnection implements IDdeConnection {
        private Ddeml.HCONV conv;
        private final IDdeClient client;

        public DdeConnection(IDdeClient client, Ddeml.HCONV conv) {
            this.conv = conv;
            this.client = client;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public Ddeml.HCONV getConv() {
            return this.conv;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void abandonTransaction(int transactionId) {
            boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(this.client.getInstanceIdentitifier().intValue(), this.conv, transactionId);
            if (!result) {
                throw DdemlException.create(this.client.getLastError());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void abandonTransactions() {
            boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(this.client.getInstanceIdentitifier().intValue(), this.conv, 0);
            if (!result) {
                throw DdemlException.create(this.client.getLastError());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public Ddeml.HDDEDATA clientTransaction(Pointer data, int dataLength, Ddeml.HSZ item, int wFmt, int transaction, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            if (timeout == -1 && result == null) {
                result = new WinDef.DWORDByReference();
            }
            Ddeml.HDDEDATA returnData = Ddeml.INSTANCE.DdeClientTransaction(data, dataLength, this.conv, item, wFmt, transaction, timeout, result);
            if (returnData == null) {
                throw DdemlException.create(this.client.getLastError());
            }
            if (userHandle != null) {
                if (timeout != -1) {
                    setUserHandle(-1, userHandle);
                } else {
                    setUserHandle(result.getValue().intValue(), userHandle);
                }
            }
            return returnData;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public Ddeml.HDDEDATA clientTransaction(Pointer data, int dataLength, String item, int wFmt, int transaction, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            Ddeml.HSZ itemHSZ = null;
            try {
                Ddeml.HSZ itemHSZ2 = this.client.createStringHandle(item);
                Ddeml.HDDEDATA hddedataClientTransaction = clientTransaction(data, dataLength, itemHSZ2, wFmt, transaction, timeout, result, userHandle);
                IDdeClient iDdeClient = this.client;
                return hddedataClientTransaction;
            } finally {
                this.client.freeStringHandle(itemHSZ);
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void poke(Pointer data, int dataLength, Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            clientTransaction(data, dataLength, item, wFmt, Ddeml.XTYP_POKE, timeout, result, userHandle);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void poke(Pointer data, int dataLength, String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            Ddeml.HSZ itemHSZ = null;
            try {
                Ddeml.HSZ itemHSZ2 = this.client.createStringHandle(item);
                poke(data, dataLength, itemHSZ2, wFmt, timeout, result, userHandle);
                IDdeClient iDdeClient = this.client;
            } finally {
                this.client.freeStringHandle(itemHSZ);
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public Ddeml.HDDEDATA request(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            return clientTransaction(Pointer.NULL, 0, item, wFmt, 8368, timeout, result, userHandle);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public Ddeml.HDDEDATA request(String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            Ddeml.HSZ itemHSZ = null;
            try {
                Ddeml.HSZ itemHSZ2 = this.client.createStringHandle(item);
                Ddeml.HDDEDATA hddedataRequest = request(itemHSZ2, wFmt, timeout, result, userHandle);
                IDdeClient iDdeClient = this.client;
                return hddedataRequest;
            } finally {
                this.client.freeStringHandle(itemHSZ);
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void execute(String executeString, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            Memory mem = new Memory((executeString.length() * 2) + 2);
            mem.setWideString(0L, executeString);
            clientTransaction(mem, (int) mem.size(), (Ddeml.HSZ) null, 0, Ddeml.XTYP_EXECUTE, timeout, result, userHandle);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void advstart(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            clientTransaction(Pointer.NULL, 0, item, wFmt, Ddeml.XTYP_ADVSTART, timeout, result, userHandle);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void advstart(String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            Ddeml.HSZ itemHSZ = null;
            try {
                Ddeml.HSZ itemHSZ2 = this.client.createStringHandle(item);
                advstart(itemHSZ2, wFmt, timeout, result, userHandle);
                IDdeClient iDdeClient = this.client;
            } finally {
                this.client.freeStringHandle(itemHSZ);
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void advstop(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            clientTransaction(Pointer.NULL, 0, item, wFmt, Ddeml.XTYP_ADVSTOP, timeout, result, userHandle);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void advstop(String item, int wFmt, int timeout, WinDef.DWORDByReference result, BaseTSD.DWORD_PTR userHandle) {
            Ddeml.HSZ itemHSZ = null;
            try {
                Ddeml.HSZ itemHSZ2 = this.client.createStringHandle(item);
                advstop(itemHSZ2, wFmt, timeout, result, userHandle);
                IDdeClient iDdeClient = this.client;
            } finally {
                this.client.freeStringHandle(itemHSZ);
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void impersonateClient() {
            boolean result = Ddeml.INSTANCE.DdeImpersonateClient(this.conv);
            if (!result) {
                throw DdemlException.create(this.client.getLastError());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            boolean result = Ddeml.INSTANCE.DdeDisconnect(this.conv);
            if (!result) {
                throw DdemlException.create(this.client.getLastError());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void reconnect() {
            Ddeml.HCONV newConv = Ddeml.INSTANCE.DdeReconnect(this.conv);
            if (newConv != null) {
                this.conv = newConv;
                return;
            }
            throw DdemlException.create(this.client.getLastError());
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public boolean enableCallback(int wCmd) {
            boolean result = Ddeml.INSTANCE.DdeEnableCallback(this.client.getInstanceIdentitifier().intValue(), this.conv, wCmd);
            if (!result && wCmd == 2) {
                throw DdemlException.create(this.client.getLastError());
            }
            return result;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public void setUserHandle(int id, BaseTSD.DWORD_PTR hUser) throws DdemlException {
            boolean result = Ddeml.INSTANCE.DdeSetUserHandle(this.conv, id, hUser);
            if (!result) {
                throw DdemlException.create(this.client.getLastError());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnection
        public Ddeml.CONVINFO queryConvInfo(int idTransaction) throws DdemlException {
            Ddeml.CONVINFO convInfo = new Ddeml.CONVINFO();
            convInfo.cb = convInfo.size();
            convInfo.ConvCtxt.cb = convInfo.ConvCtxt.size();
            convInfo.write();
            int result = Ddeml.INSTANCE.DdeQueryConvInfo(this.conv, idTransaction, convInfo);
            if (result == 0) {
                throw DdemlException.create(this.client.getLastError());
            }
            return convInfo;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$DdeConnectionList.class */
    public static class DdeConnectionList implements IDdeConnectionList {
        private final IDdeClient client;
        private final Ddeml.HCONVLIST convList;

        public DdeConnectionList(IDdeClient client, Ddeml.HCONVLIST convList) {
            this.convList = convList;
            this.client = client;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnectionList
        public Ddeml.HCONVLIST getHandle() {
            return this.convList;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnectionList
        public IDdeConnection queryNextServer(IDdeConnection prevConnection) {
            Ddeml.HCONV conv = Ddeml.INSTANCE.DdeQueryNextServer(this.convList, prevConnection != null ? prevConnection.getConv() : null);
            if (conv != null) {
                return new DdeConnection(this.client, conv);
            }
            return null;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeConnectionList, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            boolean result = Ddeml.INSTANCE.DdeDisconnectList(this.convList);
            if (!result) {
                throw DdemlException.create(this.client.getLastError());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$DdeClient.class */
    public static class DdeClient implements IDdeClient {
        private Integer idInst;
        private final DdeAdapter ddeAdapter = new DdeAdapter();

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public Integer getInstanceIdentitifier() {
            return this.idInst;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void initialize(int afCmd) throws DdemlException {
            WinDef.DWORDByReference pidInst = new WinDef.DWORDByReference();
            Integer result = Integer.valueOf(Ddeml.INSTANCE.DdeInitialize(pidInst, this.ddeAdapter, afCmd, 0));
            if (result.intValue() != 0) {
                throw DdemlException.create(result.intValue());
            }
            this.idInst = Integer.valueOf(pidInst.getValue().intValue());
            if (this.ddeAdapter instanceof DdeAdapter) {
                this.ddeAdapter.setInstanceIdentifier(this.idInst.intValue());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public Ddeml.HSZ createStringHandle(String value) throws DdemlException {
            int codePage;
            if (value == null) {
                return null;
            }
            if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
                codePage = 1200;
            } else {
                codePage = 1004;
            }
            Ddeml.HSZ handle = Ddeml.INSTANCE.DdeCreateStringHandle(this.idInst.intValue(), value, codePage);
            if (handle == null) {
                throw DdemlException.create(getLastError());
            }
            return handle;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void nameService(Ddeml.HSZ name, int afCmd) throws DdemlException {
            Ddeml.HDDEDATA handle = Ddeml.INSTANCE.DdeNameService(this.idInst.intValue(), name, new Ddeml.HSZ(), afCmd);
            if (handle == null) {
                throw DdemlException.create(getLastError());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void nameService(String name, int afCmd) throws DdemlException {
            Ddeml.HSZ nameHSZ = null;
            try {
                Ddeml.HSZ nameHSZ2 = createStringHandle(name);
                nameService(nameHSZ2, afCmd);
            } finally {
                freeStringHandle(nameHSZ);
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public int getLastError() {
            return Ddeml.INSTANCE.DdeGetLastError(this.idInst.intValue());
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public IDdeConnection connect(Ddeml.HSZ service, Ddeml.HSZ topic, Ddeml.CONVCONTEXT convcontext) {
            Ddeml.HCONV hconv = Ddeml.INSTANCE.DdeConnect(this.idInst.intValue(), service, topic, convcontext);
            if (hconv == null) {
                throw DdemlException.create(getLastError());
            }
            return new DdeConnection(this, hconv);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public IDdeConnection connect(String service, String topic, Ddeml.CONVCONTEXT convcontext) {
            Ddeml.HSZ serviceHSZ = null;
            Ddeml.HSZ topicHSZ = null;
            try {
                Ddeml.HSZ serviceHSZ2 = createStringHandle(service);
                topicHSZ = createStringHandle(topic);
                IDdeConnection iDdeConnectionConnect = connect(serviceHSZ2, topicHSZ, convcontext);
                freeStringHandle(topicHSZ);
                return iDdeConnectionConnect;
            } finally {
                freeStringHandle(topicHSZ);
                freeStringHandle(serviceHSZ);
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public String queryString(Ddeml.HSZ value) throws DdemlException {
            int codePage;
            int byteWidth;
            if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
                codePage = 1200;
                byteWidth = 2;
            } else {
                codePage = 1004;
                byteWidth = 1;
            }
            Memory buffer = new Memory(257 * byteWidth);
            try {
                Ddeml.INSTANCE.DdeQueryString(this.idInst.intValue(), value, buffer, 256, codePage);
                if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
                    return buffer.getWideString(0L);
                }
                return buffer.getString(0L);
            } finally {
                buffer.valid();
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public Ddeml.HDDEDATA createDataHandle(Pointer pSrc, int cb, int cbOff, Ddeml.HSZ hszItem, int wFmt, int afCmd) {
            Ddeml.HDDEDATA returnData = Ddeml.INSTANCE.DdeCreateDataHandle(this.idInst.intValue(), pSrc, cb, cbOff, hszItem, wFmt, afCmd);
            if (returnData == null) {
                throw DdemlException.create(getLastError());
            }
            return returnData;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void freeDataHandle(Ddeml.HDDEDATA hData) {
            boolean result = Ddeml.INSTANCE.DdeFreeDataHandle(hData);
            if (!result) {
                throw DdemlException.create(getLastError());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA hData, Pointer pSrc, int cb, int cbOff) {
            Ddeml.HDDEDATA newHandle = Ddeml.INSTANCE.DdeAddData(hData, pSrc, cb, cbOff);
            if (newHandle == null) {
                throw DdemlException.create(getLastError());
            }
            return newHandle;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public int getData(Ddeml.HDDEDATA hData, Pointer pDst, int cbMax, int cbOff) {
            int result = Ddeml.INSTANCE.DdeGetData(hData, pDst, cbMax, cbOff);
            int errorCode = getLastError();
            if (errorCode != 0) {
                throw DdemlException.create(errorCode);
            }
            return result;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public Pointer accessData(Ddeml.HDDEDATA hData, WinDef.DWORDByReference pcbDataSize) {
            Pointer result = Ddeml.INSTANCE.DdeAccessData(hData, pcbDataSize);
            if (result == null) {
                throw DdemlException.create(getLastError());
            }
            return result;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unaccessData(Ddeml.HDDEDATA hData) {
            boolean result = Ddeml.INSTANCE.DdeUnaccessData(hData);
            if (!result) {
                throw DdemlException.create(getLastError());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void postAdvise(Ddeml.HSZ hszTopic, Ddeml.HSZ hszItem) {
            boolean result = Ddeml.INSTANCE.DdePostAdvise(this.idInst.intValue(), hszTopic, hszItem);
            if (!result) {
                throw DdemlException.create(getLastError());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void postAdvise(String topic, String item) {
            Ddeml.HSZ itemHSZ = null;
            Ddeml.HSZ topicHSZ = null;
            try {
                topicHSZ = createStringHandle(topic);
                Ddeml.HSZ itemHSZ2 = createStringHandle(item);
                postAdvise(topicHSZ, itemHSZ2);
                freeStringHandle(topicHSZ);
            } finally {
                freeStringHandle(topicHSZ);
                freeStringHandle(itemHSZ);
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public boolean freeStringHandle(Ddeml.HSZ value) {
            if (value == null) {
                return true;
            }
            return Ddeml.INSTANCE.DdeFreeStringHandle(this.idInst.intValue(), value);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public boolean keepStringHandle(Ddeml.HSZ value) {
            return Ddeml.INSTANCE.DdeKeepStringHandle(this.idInst.intValue(), value);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void abandonTransactions() {
            boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(this.idInst.intValue(), null, 0);
            if (!result) {
                throw DdemlException.create(getLastError());
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public IDdeConnectionList connectList(Ddeml.HSZ service, Ddeml.HSZ topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
            Ddeml.HCONVLIST convlist = Ddeml.INSTANCE.DdeConnectList(this.idInst.intValue(), service, topic, existingList != null ? existingList.getHandle() : null, ctx);
            if (convlist == null) {
                throw DdemlException.create(getLastError());
            }
            return new DdeConnectionList(this, convlist);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public IDdeConnectionList connectList(String service, String topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
            Ddeml.HSZ serviceHSZ = null;
            Ddeml.HSZ topicHSZ = null;
            try {
                Ddeml.HSZ serviceHSZ2 = createStringHandle(service);
                topicHSZ = createStringHandle(topic);
                IDdeConnectionList iDdeConnectionListConnectList = connectList(serviceHSZ2, topicHSZ, existingList, ctx);
                freeStringHandle(topicHSZ);
                return iDdeConnectionListConnectList;
            } finally {
                freeStringHandle(topicHSZ);
                freeStringHandle(serviceHSZ);
            }
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public boolean enableCallback(int wCmd) {
            boolean result = Ddeml.INSTANCE.DdeEnableCallback(this.idInst.intValue(), null, wCmd);
            if (!result && wCmd != 2) {
                int errorCode = getLastError();
                if (errorCode != 0) {
                    throw DdemlException.create(getLastError());
                }
            }
            return result;
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public boolean uninitialize() {
            return Ddeml.INSTANCE.DdeUninitialize(this.idInst.intValue());
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            uninitialize();
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public IDdeConnection wrap(Ddeml.HCONV hconv) {
            return new DdeConnection(this, hconv);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterDisconnectHandler(DisconnectHandler handler) {
            this.ddeAdapter.unregisterDisconnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerAdvstartHandler(AdvstartHandler handler) {
            this.ddeAdapter.registerAdvstartHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterAdvstartHandler(AdvstartHandler handler) {
            this.ddeAdapter.unregisterAdvstartHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerAdvstopHandler(AdvstopHandler handler) {
            this.ddeAdapter.registerAdvstopHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterAdvstopHandler(AdvstopHandler handler) {
            this.ddeAdapter.unregisterAdvstopHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerConnectHandler(ConnectHandler handler) {
            this.ddeAdapter.registerConnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterConnectHandler(ConnectHandler handler) {
            this.ddeAdapter.unregisterConnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerAdvReqHandler(AdvreqHandler handler) {
            this.ddeAdapter.registerAdvReqHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterAdvReqHandler(AdvreqHandler handler) {
            this.ddeAdapter.unregisterAdvReqHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerRequestHandler(RequestHandler handler) {
            this.ddeAdapter.registerRequestHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterRequestHandler(RequestHandler handler) {
            this.ddeAdapter.unregisterRequestHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerWildconnectHandler(WildconnectHandler handler) {
            this.ddeAdapter.registerWildconnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterWildconnectHandler(WildconnectHandler handler) {
            this.ddeAdapter.unregisterWildconnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerAdvdataHandler(AdvdataHandler handler) {
            this.ddeAdapter.registerAdvdataHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterAdvdataHandler(AdvdataHandler handler) {
            this.ddeAdapter.unregisterAdvdataHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerExecuteHandler(ExecuteHandler handler) {
            this.ddeAdapter.registerExecuteHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterExecuteHandler(ExecuteHandler handler) {
            this.ddeAdapter.unregisterExecuteHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerPokeHandler(PokeHandler handler) {
            this.ddeAdapter.registerPokeHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterPokeHandler(PokeHandler handler) {
            this.ddeAdapter.unregisterPokeHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerConnectConfirmHandler(ConnectConfirmHandler handler) {
            this.ddeAdapter.registerConnectConfirmHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterConnectConfirmHandler(ConnectConfirmHandler handler) {
            this.ddeAdapter.unregisterConnectConfirmHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerDisconnectHandler(DisconnectHandler handler) {
            this.ddeAdapter.registerDisconnectHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerErrorHandler(ErrorHandler handler) {
            this.ddeAdapter.registerErrorHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterErrorHandler(ErrorHandler handler) {
            this.ddeAdapter.unregisterErrorHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerRegisterHandler(RegisterHandler handler) {
            this.ddeAdapter.registerRegisterHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterRegisterHandler(RegisterHandler handler) {
            this.ddeAdapter.unregisterRegisterHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerXactCompleteHandler(XactCompleteHandler handler) {
            this.ddeAdapter.registerXactCompleteHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterXactCompleteHandler(XactCompleteHandler handler) {
            this.ddeAdapter.xactCompleteXactCompleteHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerUnregisterHandler(UnregisterHandler handler) {
            this.ddeAdapter.registerUnregisterHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterUnregisterHandler(UnregisterHandler handler) {
            this.ddeAdapter.unregisterUnregisterHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void registerMonitorHandler(MonitorHandler handler) {
            this.ddeAdapter.registerMonitorHandler(handler);
        }

        @Override // com.sun.jna.platform.win32.DdemlUtil.IDdeClient
        public void unregisterMonitorHandler(MonitorHandler handler) {
            this.ddeAdapter.unregisterMonitorHandler(handler);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$DdeAdapter.class */
    public static class DdeAdapter implements Ddeml.DdeCallback {
        private static final Logger LOG = Logger.getLogger(DdeAdapter.class.getName());
        private int idInst;
        private final List<AdvstartHandler> advstartHandler = new CopyOnWriteArrayList();
        private final List<AdvstopHandler> advstopHandler = new CopyOnWriteArrayList();
        private final List<ConnectHandler> connectHandler = new CopyOnWriteArrayList();
        private final List<AdvreqHandler> advReqHandler = new CopyOnWriteArrayList();
        private final List<RequestHandler> requestHandler = new CopyOnWriteArrayList();
        private final List<WildconnectHandler> wildconnectHandler = new CopyOnWriteArrayList();
        private final List<AdvdataHandler> advdataHandler = new CopyOnWriteArrayList();
        private final List<ExecuteHandler> executeHandler = new CopyOnWriteArrayList();
        private final List<PokeHandler> pokeHandler = new CopyOnWriteArrayList();
        private final List<ConnectConfirmHandler> connectConfirmHandler = new CopyOnWriteArrayList();
        private final List<DisconnectHandler> disconnectHandler = new CopyOnWriteArrayList();
        private final List<ErrorHandler> errorHandler = new CopyOnWriteArrayList();
        private final List<RegisterHandler> registerHandler = new CopyOnWriteArrayList();
        private final List<XactCompleteHandler> xactCompleteHandler = new CopyOnWriteArrayList();
        private final List<UnregisterHandler> unregisterHandler = new CopyOnWriteArrayList();
        private final List<MonitorHandler> monitorHandler = new CopyOnWriteArrayList();

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$DdeAdapter$BlockException.class */
        public static class BlockException extends RuntimeException {
        }

        public void setInstanceIdentifier(int idInst) {
            this.idInst = idInst;
        }

        @Override // com.sun.jna.platform.win32.Ddeml.DdeCallback
        public WinDef.PVOID ddeCallback(int wType, int wFmt, Ddeml.HCONV hConv, Ddeml.HSZ hsz1, Ddeml.HSZ hsz2, Ddeml.HDDEDATA hData, BaseTSD.ULONG_PTR lData1, BaseTSD.ULONG_PTR lData2) {
            try {
                switch (wType) {
                    case Ddeml.XTYP_ADVSTART /* 4144 */:
                        boolean booleanResult = onAdvstart(wType, wFmt, hConv, hsz1, hsz2);
                        return new WinDef.PVOID(Pointer.createConstant(new WinDef.BOOL(booleanResult).intValue()));
                    case Ddeml.XTYP_CONNECT /* 4194 */:
                        Ddeml.CONVCONTEXT convcontext = null;
                        if (lData1.toPointer() != null) {
                            convcontext = new Ddeml.CONVCONTEXT(new Pointer(lData1.longValue()));
                        }
                        boolean booleanResult2 = onConnect(wType, hsz1, hsz2, convcontext, (lData2 == null || lData2.intValue() == 0) ? false : true);
                        return new WinDef.PVOID(Pointer.createConstant(new WinDef.BOOL(booleanResult2).intValue()));
                    case 8226:
                        int count = lData1.intValue() & 65535;
                        Ddeml.HDDEDATA data = onAdvreq(wType, wFmt, hConv, hsz1, hsz2, count);
                        if (data == null) {
                            return new WinDef.PVOID();
                        }
                        return new WinDef.PVOID(data.getPointer());
                    case 8368:
                        Ddeml.HDDEDATA data2 = onRequest(wType, wFmt, hConv, hsz1, hsz2);
                        if (data2 == null) {
                            return new WinDef.PVOID();
                        }
                        return new WinDef.PVOID(data2.getPointer());
                    case 8418:
                        Ddeml.CONVCONTEXT convcontext2 = null;
                        if (lData1.toPointer() != null) {
                            convcontext2 = new Ddeml.CONVCONTEXT(new Pointer(lData1.longValue()));
                        }
                        Ddeml.HSZPAIR[] hszPairs = onWildconnect(wType, hsz1, hsz2, convcontext2, (lData2 == null || lData2.intValue() == 0) ? false : true);
                        if (hszPairs == null || hszPairs.length == 0) {
                            return new WinDef.PVOID();
                        }
                        int size = 0;
                        for (Ddeml.HSZPAIR hp : hszPairs) {
                            hp.write();
                            size += hp.size();
                        }
                        return new WinDef.PVOID(Ddeml.INSTANCE.DdeCreateDataHandle(this.idInst, hszPairs[0].getPointer(), size, 0, null, wFmt, 0).getPointer());
                    case 16400:
                        int intResult = onAdvdata(wType, wFmt, hConv, hsz1, hsz2, hData);
                        return new WinDef.PVOID(Pointer.createConstant(intResult));
                    case Ddeml.XTYP_EXECUTE /* 16464 */:
                        int intResult2 = onExecute(wType, hConv, hsz1, hData);
                        Ddeml.INSTANCE.DdeFreeDataHandle(hData);
                        return new WinDef.PVOID(Pointer.createConstant(intResult2));
                    case Ddeml.XTYP_POKE /* 16528 */:
                        int intResult3 = onPoke(wType, wFmt, hConv, hsz1, hsz2, hData);
                        return new WinDef.PVOID(Pointer.createConstant(intResult3));
                    case 32770:
                        onError(wType, hConv, (int) (lData2.longValue() & 65535));
                        break;
                    case Ddeml.XTYP_ADVSTOP /* 32832 */:
                        onAdvstop(wType, wFmt, hConv, hsz1, hsz2);
                        break;
                    case Ddeml.XTYP_CONNECT_CONFIRM /* 32882 */:
                        onConnectConfirm(wType, hConv, hsz1, hsz2, (lData2 == null || lData2.intValue() == 0) ? false : true);
                        break;
                    case Ddeml.XTYP_XACT_COMPLETE /* 32896 */:
                        onXactComplete(wType, wFmt, hConv, hsz1, hsz2, hData, lData1, lData2);
                        break;
                    case Ddeml.XTYP_REGISTER /* 32930 */:
                        onRegister(wType, hsz1, hsz2);
                        break;
                    case Ddeml.XTYP_DISCONNECT /* 32962 */:
                        onDisconnect(wType, hConv, (lData2 == null || lData2.intValue() == 0) ? false : true);
                        break;
                    case Ddeml.XTYP_UNREGISTER /* 32978 */:
                        onUnregister(wType, hsz1, hsz2);
                        break;
                    case Ddeml.XTYP_MONITOR /* 33010 */:
                        onMonitor(wType, hData, lData2.intValue());
                        break;
                    default:
                        LOG.log(Level.FINE, String.format("Not implemented Operation - Transaction type: 0x%X (%s)", Integer.valueOf(wType), null));
                        break;
                }
            } catch (BlockException e) {
                return new WinDef.PVOID(Pointer.createConstant(-1));
            } catch (Throwable ex) {
                LOG.log(Level.WARNING, "Exception in DDECallback", ex);
            }
            return new WinDef.PVOID();
        }

        public void registerAdvstartHandler(AdvstartHandler handler) {
            this.advstartHandler.add(handler);
        }

        public void unregisterAdvstartHandler(AdvstartHandler handler) {
            this.advstartHandler.remove(handler);
        }

        private boolean onAdvstart(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
            boolean oneHandlerTrue = false;
            for (AdvstartHandler handler : this.advstartHandler) {
                if (handler.onAdvstart(transactionType, dataFormat, hconv, topic, item)) {
                    oneHandlerTrue = true;
                }
            }
            return oneHandlerTrue;
        }

        public void registerAdvstopHandler(AdvstopHandler handler) {
            this.advstopHandler.add(handler);
        }

        public void unregisterAdvstopHandler(AdvstopHandler handler) {
            this.advstopHandler.remove(handler);
        }

        private void onAdvstop(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
            for (AdvstopHandler handler : this.advstopHandler) {
                handler.onAdvstop(transactionType, dataFormat, hconv, topic, item);
            }
        }

        public void registerConnectHandler(ConnectHandler handler) {
            this.connectHandler.add(handler);
        }

        public void unregisterConnectHandler(ConnectHandler handler) {
            this.connectHandler.remove(handler);
        }

        private boolean onConnect(int transactionType, Ddeml.HSZ topic, Ddeml.HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
            boolean oneHandlerTrue = false;
            for (ConnectHandler handler : this.connectHandler) {
                if (handler.onConnect(transactionType, topic, service, convcontext, sameInstance)) {
                    oneHandlerTrue = true;
                }
            }
            return oneHandlerTrue;
        }

        public void registerAdvReqHandler(AdvreqHandler handler) {
            this.advReqHandler.add(handler);
        }

        public void unregisterAdvReqHandler(AdvreqHandler handler) {
            this.advReqHandler.remove(handler);
        }

        private Ddeml.HDDEDATA onAdvreq(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item, int count) {
            for (AdvreqHandler handler : this.advReqHandler) {
                Ddeml.HDDEDATA result = handler.onAdvreq(transactionType, dataFormat, hconv, topic, item, count);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

        public void registerRequestHandler(RequestHandler handler) {
            this.requestHandler.add(handler);
        }

        public void unregisterRequestHandler(RequestHandler handler) {
            this.requestHandler.remove(handler);
        }

        private Ddeml.HDDEDATA onRequest(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
            for (RequestHandler handler : this.requestHandler) {
                Ddeml.HDDEDATA result = handler.onRequest(transactionType, dataFormat, hconv, topic, item);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

        public void registerWildconnectHandler(WildconnectHandler handler) {
            this.wildconnectHandler.add(handler);
        }

        public void unregisterWildconnectHandler(WildconnectHandler handler) {
            this.wildconnectHandler.remove(handler);
        }

        private Ddeml.HSZPAIR[] onWildconnect(int transactionType, Ddeml.HSZ topic, Ddeml.HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
            List<Ddeml.HSZPAIR> hszpairs = new ArrayList<>(1);
            for (WildconnectHandler handler : this.wildconnectHandler) {
                hszpairs.addAll(handler.onWildconnect(transactionType, topic, service, convcontext, sameInstance));
            }
            return (Ddeml.HSZPAIR[]) hszpairs.toArray(new Ddeml.HSZPAIR[0]);
        }

        public void registerAdvdataHandler(AdvdataHandler handler) {
            this.advdataHandler.add(handler);
        }

        public void unregisterAdvdataHandler(AdvdataHandler handler) {
            this.advdataHandler.remove(handler);
        }

        private int onAdvdata(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item, Ddeml.HDDEDATA hdata) {
            for (AdvdataHandler handler : this.advdataHandler) {
                int result = handler.onAdvdata(transactionType, dataFormat, hconv, topic, item, hdata);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }

        public void registerExecuteHandler(ExecuteHandler handler) {
            this.executeHandler.add(handler);
        }

        public void unregisterExecuteHandler(ExecuteHandler handler) {
            this.executeHandler.remove(handler);
        }

        private int onExecute(int transactionType, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HDDEDATA commandString) {
            for (ExecuteHandler handler : this.executeHandler) {
                int result = handler.onExecute(transactionType, hconv, topic, commandString);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }

        public void registerPokeHandler(PokeHandler handler) {
            this.pokeHandler.add(handler);
        }

        public void unregisterPokeHandler(PokeHandler handler) {
            this.pokeHandler.remove(handler);
        }

        private int onPoke(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item, Ddeml.HDDEDATA hdata) {
            for (PokeHandler handler : this.pokeHandler) {
                int result = handler.onPoke(transactionType, dataFormat, hconv, topic, item, hdata);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }

        public void registerConnectConfirmHandler(ConnectConfirmHandler handler) {
            this.connectConfirmHandler.add(handler);
        }

        public void unregisterConnectConfirmHandler(ConnectConfirmHandler handler) {
            this.connectConfirmHandler.remove(handler);
        }

        private void onConnectConfirm(int transactionType, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ service, boolean sameInstance) {
            for (ConnectConfirmHandler handler : this.connectConfirmHandler) {
                handler.onConnectConfirm(transactionType, hconv, topic, service, sameInstance);
            }
        }

        public void registerDisconnectHandler(DisconnectHandler handler) {
            this.disconnectHandler.add(handler);
        }

        public void unregisterDisconnectHandler(DisconnectHandler handler) {
            this.disconnectHandler.remove(handler);
        }

        private void onDisconnect(int transactionType, Ddeml.HCONV hconv, boolean sameInstance) {
            for (DisconnectHandler handler : this.disconnectHandler) {
                handler.onDisconnect(transactionType, hconv, sameInstance);
            }
        }

        public void registerErrorHandler(ErrorHandler handler) {
            this.errorHandler.add(handler);
        }

        public void unregisterErrorHandler(ErrorHandler handler) {
            this.errorHandler.remove(handler);
        }

        private void onError(int transactionType, Ddeml.HCONV hconv, int errorCode) {
            for (ErrorHandler handler : this.errorHandler) {
                handler.onError(transactionType, hconv, errorCode);
            }
        }

        public void registerRegisterHandler(RegisterHandler handler) {
            this.registerHandler.add(handler);
        }

        public void unregisterRegisterHandler(RegisterHandler handler) {
            this.registerHandler.remove(handler);
        }

        private void onRegister(int transactionType, Ddeml.HSZ baseServiceName, Ddeml.HSZ instanceSpecificServiceName) {
            for (RegisterHandler handler : this.registerHandler) {
                handler.onRegister(transactionType, baseServiceName, instanceSpecificServiceName);
            }
        }

        public void registerXactCompleteHandler(XactCompleteHandler handler) {
            this.xactCompleteHandler.add(handler);
        }

        public void xactCompleteXactCompleteHandler(XactCompleteHandler handler) {
            this.xactCompleteHandler.remove(handler);
        }

        private void onXactComplete(int transactionType, int dataFormat, Ddeml.HCONV hConv, Ddeml.HSZ topic, Ddeml.HSZ item, Ddeml.HDDEDATA hdata, BaseTSD.ULONG_PTR transactionIdentifier, BaseTSD.ULONG_PTR statusFlag) {
            for (XactCompleteHandler handler : this.xactCompleteHandler) {
                handler.onXactComplete(transactionType, dataFormat, hConv, topic, item, hdata, transactionIdentifier, statusFlag);
            }
        }

        public void registerUnregisterHandler(UnregisterHandler handler) {
            this.unregisterHandler.add(handler);
        }

        public void unregisterUnregisterHandler(UnregisterHandler handler) {
            this.unregisterHandler.remove(handler);
        }

        private void onUnregister(int transactionType, Ddeml.HSZ baseServiceName, Ddeml.HSZ instanceSpecificServiceName) {
            for (UnregisterHandler handler : this.unregisterHandler) {
                handler.onUnregister(transactionType, baseServiceName, instanceSpecificServiceName);
            }
        }

        public void registerMonitorHandler(MonitorHandler handler) {
            this.monitorHandler.add(handler);
        }

        public void unregisterMonitorHandler(MonitorHandler handler) {
            this.monitorHandler.remove(handler);
        }

        private void onMonitor(int transactionType, Ddeml.HDDEDATA hdata, int dwData2) {
            for (MonitorHandler handler : this.monitorHandler) {
                handler.onMonitor(transactionType, hdata, dwData2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/DdemlUtil$DdemlException.class */
    public static class DdemlException extends RuntimeException {
        private static final Map<Integer, String> ERROR_CODE_MAP;
        private final int errorCode;

        static {
            Map<Integer, String> errorCodeMapBuilder = new HashMap<>();
            for (Field f : Ddeml.class.getFields()) {
                String name = f.getName();
                if (name.startsWith("DMLERR_") && !name.equals("DMLERR_FIRST") && !name.equals("DMLERR_LAST")) {
                    try {
                        errorCodeMapBuilder.put(Integer.valueOf(f.getInt(null)), name);
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    } catch (IllegalArgumentException ex2) {
                        throw new RuntimeException(ex2);
                    }
                }
            }
            ERROR_CODE_MAP = Collections.unmodifiableMap(errorCodeMapBuilder);
        }

        public static DdemlException create(int errorCode) {
            String errorName = ERROR_CODE_MAP.get(Integer.valueOf(errorCode));
            Object[] objArr = new Object[2];
            objArr[0] = errorName != null ? errorName : "";
            objArr[1] = Integer.valueOf(errorCode);
            return new DdemlException(errorCode, String.format("%s (Code: 0x%X)", objArr));
        }

        public DdemlException(int errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return this.errorCode;
        }
    }
}
