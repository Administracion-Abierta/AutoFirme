package org.ini4j.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/OptionsParser.class */
public class OptionsParser extends AbstractParser {
    private static final String COMMENTS = "!#";
    private static final String OPERATORS = ":=";

    public OptionsParser() {
        super(OPERATORS, COMMENTS);
    }

    public static OptionsParser newInstance() {
        return (OptionsParser) ServiceFinder.findService(OptionsParser.class);
    }

    public static OptionsParser newInstance(Config config) {
        OptionsParser instance = newInstance();
        instance.setConfig(config);
        return instance;
    }

    public void parse(InputStream input, OptionsHandler handler) throws IOException {
        parse(newIniSource(input, handler), handler);
    }

    public void parse(Reader input, OptionsHandler handler) throws IOException {
        parse(newIniSource(input, handler), handler);
    }

    public void parse(URL input, OptionsHandler handler) throws IOException {
        parse(newIniSource(input, handler), handler);
    }

    private void parse(IniSource source, OptionsHandler handler) throws IOException {
        handler.startOptions();
        String line = source.readLine();
        while (true) {
            String line2 = line;
            if (line2 != null) {
                parseOptionLine(line2, handler, source.getLineNumber());
                line = source.readLine();
            } else {
                handler.endOptions();
                return;
            }
        }
    }
}
