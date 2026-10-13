package org.ini4j;

import com.ctc.wstx.cfg.XmlConsts;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDPrintFieldAttributeObject;
import org.ini4j.spi.IniHandler;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/ConfigParser.class */
public class ConfigParser implements Serializable {
    private static final long serialVersionUID = 9118857036229164353L;
    private PyIni _ini;

    public ConfigParser() {
        this(Collections.EMPTY_MAP);
    }

    public ConfigParser(Map<String, String> defaults) {
        this._ini = new PyIni(defaults);
    }

    public boolean getBoolean(String section, String option) throws InterpolationException, NoOptionException, NoSectionException {
        boolean ret;
        String value = get(section, option);
        if ("1".equalsIgnoreCase(value) || XmlConsts.XML_SA_YES.equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || PDPrintFieldAttributeObject.CHECKED_STATE_ON.equalsIgnoreCase(value)) {
            ret = true;
        } else if ("0".equalsIgnoreCase(value) || XmlConsts.XML_SA_NO.equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) || PDPrintFieldAttributeObject.CHECKED_STATE_OFF.equalsIgnoreCase(value)) {
            ret = false;
        } else {
            throw new IllegalArgumentException(value);
        }
        return ret;
    }

    public double getDouble(String section, String option) throws InterpolationException, NoOptionException, NoSectionException {
        return Double.parseDouble(get(section, option));
    }

    public float getFloat(String section, String option) throws InterpolationException, NoOptionException, NoSectionException {
        return Float.parseFloat(get(section, option));
    }

    public int getInt(String section, String option) throws InterpolationException, NoOptionException, NoSectionException {
        return Integer.parseInt(get(section, option));
    }

    public long getLong(String section, String option) throws InterpolationException, NoOptionException, NoSectionException {
        return Long.parseLong(get(section, option));
    }

    public void addSection(String section) throws DuplicateSectionException {
        if (this._ini.containsKey(section)) {
            throw new DuplicateSectionException(section);
        }
        if ("DEFAULT".equalsIgnoreCase(section)) {
            throw new IllegalArgumentException(section);
        }
        this._ini.add(section);
    }

    public Map<String, String> defaults() {
        return this._ini.getDefaults();
    }

    public String get(String section, String option) throws InterpolationException, NoOptionException, NoSectionException {
        return get(section, option, false, Collections.EMPTY_MAP);
    }

    public String get(String section, String option, boolean raw) throws InterpolationException, NoOptionException, NoSectionException {
        return get(section, option, raw, Collections.EMPTY_MAP);
    }

    public String get(String sectionName, String optionName, boolean raw, Map<String, String> variables) throws InterpolationException, NoOptionException, NoSectionException {
        String value = requireOption(sectionName, optionName);
        if (!raw && value != null && value.indexOf(37) >= 0) {
            value = this._ini.fetch(sectionName, optionName, variables);
        }
        return value;
    }

    public boolean hasOption(String sectionName, String optionName) {
        Profile.Section section = this._ini.get(sectionName);
        return section != null && section.containsKey(optionName);
    }

    public boolean hasSection(String sectionName) {
        return this._ini.containsKey(sectionName);
    }

    public List<Map.Entry<String, String>> items(String sectionName) throws InterpolationMissingOptionException, NoSectionException {
        return items(sectionName, false, Collections.EMPTY_MAP);
    }

    public List<Map.Entry<String, String>> items(String sectionName, boolean raw) throws InterpolationMissingOptionException, NoSectionException {
        return items(sectionName, raw, Collections.EMPTY_MAP);
    }

    public List<Map.Entry<String, String>> items(String sectionName, boolean raw, Map<String, String> variables) throws InterpolationMissingOptionException, NoSectionException {
        Map<String, String> ret;
        Profile.Section section = requireSection(sectionName);
        if (raw) {
            ret = new HashMap<>(section);
        } else {
            ret = new HashMap<>();
            for (String key : section.keySet()) {
                ret.put(key, this._ini.fetch(section, key, variables));
            }
        }
        return new ArrayList(ret.entrySet());
    }

    public List<String> options(String sectionName) throws NoSectionException {
        requireSection(sectionName);
        return new ArrayList(this._ini.get(sectionName).keySet());
    }

    public void read(String... filenames) throws IOException {
        for (String filename : filenames) {
            read(new File(filename));
        }
    }

    public void read(Reader reader) throws IOException {
        try {
            this._ini.load(reader);
        } catch (InvalidFileFormatException x) {
            throw new ParsingException(x);
        }
    }

    public void read(URL url) throws IOException {
        try {
            this._ini.load(url);
        } catch (InvalidFileFormatException x) {
            throw new ParsingException(x);
        }
    }

    public void read(File file) throws IOException {
        try {
            this._ini.load(new FileReader(file));
        } catch (InvalidFileFormatException x) {
            throw new ParsingException(x);
        }
    }

    public void read(InputStream stream) throws IOException {
        try {
            this._ini.load(stream);
        } catch (InvalidFileFormatException x) {
            throw new ParsingException(x);
        }
    }

    public boolean removeOption(String sectionName, String optionName) throws NoSectionException {
        Profile.Section section = requireSection(sectionName);
        boolean ret = section.containsKey(optionName);
        section.remove(optionName);
        return ret;
    }

    public boolean removeSection(String sectionName) {
        boolean ret = this._ini.containsKey(sectionName);
        this._ini.remove(sectionName);
        return ret;
    }

    public List<String> sections() {
        return new ArrayList(this._ini.keySet());
    }

    public void set(String sectionName, String optionName, Object value) throws NoSectionException {
        Profile.Section section = requireSection(sectionName);
        if (value == null) {
            section.remove(optionName);
        } else {
            section.put((Object) optionName, (Object) value.toString());
        }
    }

    public void write(Writer writer) throws IOException {
        this._ini.store(writer);
    }

    public void write(OutputStream stream) throws IOException {
        this._ini.store(stream);
    }

    public void write(File file) throws IOException {
        this._ini.store(new FileWriter(file));
    }

    protected Ini getIni() {
        return this._ini;
    }

    private String requireOption(String sectionName, String optionName) throws NoOptionException, NoSectionException {
        Profile.Section section = requireSection(sectionName);
        String option = (String) section.get(optionName);
        if (option == null) {
            throw new NoOptionException(optionName);
        }
        return option;
    }

    private Profile.Section requireSection(String sectionName) throws NoSectionException {
        Profile.Section section = this._ini.get(sectionName);
        if (section == null) {
            throw new NoSectionException(sectionName);
        }
        return section;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/ConfigParser$ConfigParserException.class */
    public static class ConfigParserException extends Exception {
        private static final long serialVersionUID = -6845546313519392093L;

        public ConfigParserException(String message) {
            super(message);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/ConfigParser$DuplicateSectionException.class */
    public static final class DuplicateSectionException extends ConfigParserException {
        private static final long serialVersionUID = -5244008445735700699L;

        private DuplicateSectionException(String message) {
            super(message);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/ConfigParser$InterpolationException.class */
    public static class InterpolationException extends ConfigParserException {
        private static final long serialVersionUID = 8924443303158546939L;

        protected InterpolationException(String message) {
            super(message);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/ConfigParser$InterpolationMissingOptionException.class */
    public static final class InterpolationMissingOptionException extends InterpolationException {
        private static final long serialVersionUID = 2903136975820447879L;

        private InterpolationMissingOptionException(String message) {
            super(message);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/ConfigParser$NoOptionException.class */
    public static final class NoOptionException extends ConfigParserException {
        private static final long serialVersionUID = 8460082078809425858L;

        private NoOptionException(String message) {
            super(message);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/ConfigParser$NoSectionException.class */
    public static final class NoSectionException extends ConfigParserException {
        private static final long serialVersionUID = 8553627727493146118L;

        private NoSectionException(String message) {
            super(message);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/ConfigParser$ParsingException.class */
    public static final class ParsingException extends IOException {
        private static final long serialVersionUID = -5395990242007205038L;

        private ParsingException(Throwable cause) {
            super(cause.getMessage());
            initCause(cause);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/ConfigParser$PyIni.class */
    static class PyIni extends Ini {
        private static final char SUBST_CHAR = '%';
        private static final Pattern EXPRESSION = Pattern.compile("(?<!\\\\)\\%\\(([^\\)]+)\\)");
        private static final int G_OPTION = 1;
        protected static final String DEFAULT_SECTION_NAME = "DEFAULT";
        private static final long serialVersionUID = -7152857626328996122L;
        private final Map<String, String> _defaults;
        private Profile.Section _defaultSection;

        public PyIni(Map<String, String> defaults) {
            this._defaults = defaults;
            Config cfg = getConfig().m2267clone();
            cfg.setEscape(false);
            cfg.setMultiOption(false);
            cfg.setMultiSection(false);
            cfg.setLowerCaseOption(true);
            cfg.setLowerCaseSection(true);
            super.setConfig(cfg);
        }

        @Override // org.ini4j.Ini, org.ini4j.Configurable
        public void setConfig(Config value) {
        }

        public Map<String, String> getDefaults() {
            return this._defaults;
        }

        @Override // org.ini4j.BasicProfile, org.ini4j.Profile
        public Profile.Section add(String name) {
            Profile.Section section;
            if (DEFAULT_SECTION_NAME.equalsIgnoreCase(name)) {
                if (this._defaultSection == null) {
                    this._defaultSection = newSection(name);
                }
                section = this._defaultSection;
            } else {
                section = super.add(name);
            }
            return section;
        }

        public String fetch(String sectionName, String optionName, Map<String, String> variables) throws InterpolationMissingOptionException {
            return fetch(get(sectionName), optionName, variables);
        }

        protected Profile.Section getDefaultSection() {
            return this._defaultSection;
        }

        protected String fetch(Profile.Section section, String optionName, Map<String, String> variables) throws InterpolationMissingOptionException {
            String value = (String) section.get(optionName);
            if (value != null && value.indexOf(37) >= 0) {
                StringBuilder buffer = new StringBuilder(value);
                resolve(buffer, section, variables);
                value = buffer.toString();
            }
            return value;
        }

        protected void resolve(StringBuilder buffer, Profile.Section owner, Map<String, String> vars) throws InterpolationMissingOptionException {
            Matcher m = EXPRESSION.matcher(buffer);
            while (m.find()) {
                String optionName = m.group(1);
                String value = (String) owner.get(optionName);
                if (value == null) {
                    value = vars.get(optionName);
                }
                if (value == null) {
                    value = this._defaults.get(optionName);
                }
                if (value == null && this._defaultSection != null) {
                    value = (String) this._defaultSection.get(optionName);
                }
                if (value == null) {
                    throw new InterpolationMissingOptionException(optionName);
                }
                buffer.replace(m.start(), m.end(), value);
                m.reset(buffer);
            }
        }

        @Override // org.ini4j.BasicProfile
        protected void store(IniHandler formatter) {
            formatter.startIni();
            if (this._defaultSection != null) {
                store(formatter, this._defaultSection);
            }
            for (Profile.Section s : values()) {
                store(formatter, s);
            }
            formatter.endIni();
        }

        @Override // org.ini4j.Ini, org.ini4j.BasicProfile
        protected void store(IniHandler formatter, Profile.Section section) {
            formatter.startSection(section.getName());
            for (String name : section.keySet()) {
                formatter.handleOption(name, (String) section.get(name));
            }
            formatter.endSection();
        }
    }
}
