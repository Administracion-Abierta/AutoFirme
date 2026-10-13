package es.gob.afirma.standalone.plugins;

import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/SignDataProcessor.class */
public abstract class SignDataProcessor {
    private final int protocolVersion;

    public abstract void setCipherKey(byte[] bArr);

    public abstract List<SignOperation> preProcess(SignOperation signOperation);

    public abstract StringBuilder postProcess(List<SignResult> list, SignOperation signOperation) throws EncryptingException, PluginControlledException;

    public abstract boolean checkTrigger(SignOperation signOperation);

    public SignDataProcessor(int i) {
        this.protocolVersion = i;
    }

    public boolean isErrorsAllowed() {
        return false;
    }

    public final int getProtocolVersion() {
        return this.protocolVersion;
    }
}
