package org.ini4j.spi;

import java.io.PrintWriter;
import java.io.Writer;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/IniFormatter.class */
public class IniFormatter extends AbstractFormatter implements IniHandler {
    @Override // org.ini4j.spi.AbstractFormatter, org.ini4j.spi.HandlerBase
    public /* bridge */ /* synthetic */ void handleOption(String x0, String x1) {
        super.handleOption(x0, x1);
    }

    @Override // org.ini4j.spi.AbstractFormatter, org.ini4j.spi.HandlerBase
    public /* bridge */ /* synthetic */ void handleComment(String x0) {
        super.handleComment(x0);
    }

    public static IniFormatter newInstance(Writer out, Config config) {
        IniFormatter instance = newInstance();
        instance.setOutput(out instanceof PrintWriter ? (PrintWriter) out : new PrintWriter(out));
        instance.setConfig(config);
        return instance;
    }

    @Override // org.ini4j.spi.IniHandler
    public void endIni() {
        getOutput().flush();
    }

    @Override // org.ini4j.spi.IniHandler
    public void endSection() {
        getOutput().print(getConfig().getLineSeparator());
    }

    @Override // org.ini4j.spi.IniHandler
    public void startIni() {
    }

    @Override // org.ini4j.spi.IniHandler
    public void startSection(String sectionName) {
        setHeader(false);
        if (!getConfig().isGlobalSection() || !sectionName.equals(getConfig().getGlobalSectionName())) {
            getOutput().print('[');
            getOutput().print(escapeKey(sectionName));
            getOutput().print(']');
            getOutput().print(getConfig().getLineSeparator());
        }
    }

    private static IniFormatter newInstance() {
        return (IniFormatter) ServiceFinder.findService(IniFormatter.class);
    }
}
