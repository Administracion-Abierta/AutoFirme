package org.apache.commons.codec.language.bm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.codec.Resources;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/bm/Lang.class */
public class Lang {
    private static final Map<NameType, Lang> LANGS = new EnumMap(NameType.class);
    private static final String LANGUAGE_RULES_RN = "org/apache/commons/codec/language/bm/%s_lang.txt";
    private final Languages languages;
    private final List<LangRule> rules;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/bm/Lang$LangRule.class */
    private static final class LangRule {
        private final boolean acceptOnMatch;
        private final Set<String> languages;
        private final Pattern pattern;

        private LangRule(Pattern pattern, Set<String> languages, boolean acceptOnMatch) {
            this.pattern = pattern;
            this.languages = languages;
            this.acceptOnMatch = acceptOnMatch;
        }

        public boolean matches(String txt) {
            return this.pattern.matcher(txt).find();
        }
    }

    static {
        for (NameType s : NameType.values()) {
            LANGS.put(s, loadFromResource(String.format(LANGUAGE_RULES_RN, s.getName()), Languages.getInstance(s)));
        }
    }

    public static Lang instance(NameType nameType) {
        return LANGS.get(nameType);
    }

    public static Lang loadFromResource(String languageRulesResourceName, Languages languages) {
        List<LangRule> rules = new ArrayList<>();
        Scanner scanner = new Scanner(Resources.getInputStream(languageRulesResourceName), ResourceConstants.ENCODING);
        boolean inExtendedComment = false;
        while (scanner.hasNextLine()) {
            try {
                String rawLine = scanner.nextLine();
                String line = rawLine;
                if (inExtendedComment) {
                    if (line.endsWith("*/")) {
                        inExtendedComment = false;
                    }
                } else if (line.startsWith("/*")) {
                    inExtendedComment = true;
                } else {
                    int cmtI = line.indexOf("//");
                    if (cmtI >= 0) {
                        line = line.substring(0, cmtI);
                    }
                    String line2 = line.trim();
                    if (!line2.isEmpty()) {
                        String[] parts = line2.split("\\s+");
                        if (parts.length != 3) {
                            throw new IllegalArgumentException("Malformed line '" + rawLine + "' in language resource '" + languageRulesResourceName + "'");
                        }
                        Pattern pattern = Pattern.compile(parts[0]);
                        String[] langs = parts[1].split("\\+");
                        boolean accept = parts[2].equals("true");
                        rules.add(new LangRule(pattern, new HashSet(Arrays.asList(langs)), accept));
                    }
                }
            } catch (Throwable th) {
                try {
                    scanner.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        scanner.close();
        return new Lang(rules, languages);
    }

    private Lang(List<LangRule> rules, Languages languages) {
        this.rules = Collections.unmodifiableList(rules);
        this.languages = languages;
    }

    public String guessLanguage(String text) {
        Languages.LanguageSet ls = guessLanguages(text);
        return ls.isSingleton() ? ls.getAny() : Languages.ANY;
    }

    public Languages.LanguageSet guessLanguages(String input) {
        String text = input.toLowerCase(Locale.ENGLISH);
        Set<String> langs = new HashSet<>(this.languages.getLanguages());
        this.rules.forEach(rule -> {
            if (rule.matches(text)) {
                if (rule.acceptOnMatch) {
                    langs.retainAll(rule.languages);
                } else {
                    langs.removeAll(rule.languages);
                }
            }
        });
        Languages.LanguageSet ls = Languages.LanguageSet.from(langs);
        return ls.equals(Languages.NO_LANGUAGES) ? Languages.ANY_LANGUAGE : ls;
    }
}
