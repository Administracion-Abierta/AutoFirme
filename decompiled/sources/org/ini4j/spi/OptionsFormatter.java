package org.ini4j.spi;

import java.io.PrintWriter;
import java.io.Writer;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/OptionsFormatter.class */
public class OptionsFormatter extends AbstractFormatter implements OptionsHandler {
    @Override // org.ini4j.spi.AbstractFormatter, org.ini4j.spi.HandlerBase
    public /* bridge */ /* synthetic */ void handleOption(String x0, String x1) {
        super.handleOption(x0, x1);
    }

    @Override // org.ini4j.spi.AbstractFormatter, org.ini4j.spi.HandlerBase
    public /* bridge */ /* synthetic */ void handleComment(String x0) {
        super.handleComment(x0);
    }

    public static OptionsFormatter newInstance(Writer out, Config config) {
        OptionsFormatter instance = newInstance();
        instance.setOutput(out instanceof PrintWriter ? (PrintWriter) out : new PrintWriter(out));
        instance.setConfig(config);
        return instance;
    }

    @Override // org.ini4j.spi.OptionsHandler
    public void endOptions() {
        getOutput().flush();
    }

    @Override // org.ini4j.spi.OptionsHandler
    public void startOptions() {
    }

    private static OptionsFormatter newInstance() {
        return (OptionsFormatter) ServiceFinder.findService(OptionsFormatter.class);
    }
}
