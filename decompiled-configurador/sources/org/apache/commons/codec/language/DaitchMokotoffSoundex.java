package org.apache.commons.codec.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.Resources;
import org.apache.commons.codec.StringEncoder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/DaitchMokotoffSoundex.class */
public class DaitchMokotoffSoundex implements StringEncoder {
    private static final String COMMENT = "//";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String MULTILINE_COMMENT_END = "*/";
    private static final String MULTILINE_COMMENT_START = "/*";
    private static final String RESOURCE_FILE = "org/apache/commons/codec/language/dmrules.txt";
    private static final int MAX_LENGTH = 6;
    private static final Map<Character, List<Rule>> RULES = new HashMap();
    private static final Map<Character, Character> FOLDINGS = new HashMap();
    private final boolean folding;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/DaitchMokotoffSoundex$Branch.class */
    private static final class Branch {
        private final StringBuilder builder;
        private String cachedString;
        private String lastReplacement;

        private Branch() {
            this.builder = new StringBuilder();
            this.lastReplacement = null;
            this.cachedString = null;
        }

        public Branch createBranch() {
            Branch branch = new Branch();
            branch.builder.append(toString());
            branch.lastReplacement = this.lastReplacement;
            return branch;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Branch)) {
                return false;
            }
            return toString().equals(((Branch) other).toString());
        }

        public void finish() {
            while (this.builder.length() < 6) {
                this.builder.append('0');
                this.cachedString = null;
            }
        }

        public int hashCode() {
            return toString().hashCode();
        }

        public void processNextReplacement(String replacement, boolean forceAppend) {
            boolean append = this.lastReplacement == null || !this.lastReplacement.endsWith(replacement) || forceAppend;
            if (append && this.builder.length() < 6) {
                this.builder.append(replacement);
                if (this.builder.length() > 6) {
                    this.builder.delete(6, this.builder.length());
                }
                this.cachedString = null;
            }
            this.lastReplacement = replacement;
        }

        public String toString() {
            if (this.cachedString == null) {
                this.cachedString = this.builder.toString();
            }
            return this.cachedString;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/DaitchMokotoffSoundex$Rule.class */
    private static final class Rule {
        private final String pattern;
        private final String[] replacementAtStart;
        private final String[] replacementBeforeVowel;
        private final String[] replacementDefault;

        protected Rule(String pattern, String replacementAtStart, String replacementBeforeVowel, String replacementDefault) {
            this.pattern = pattern;
            this.replacementAtStart = replacementAtStart.split("\\|");
            this.replacementBeforeVowel = replacementBeforeVowel.split("\\|");
            this.replacementDefault = replacementDefault.split("\\|");
        }

        public int getPatternLength() {
            return this.pattern.length();
        }

        public String[] getReplacements(String context, boolean atStart) {
            if (atStart) {
                return this.replacementAtStart;
            }
            int nextIndex = getPatternLength();
            boolean nextCharIsVowel = nextIndex < context.length() && isVowel(context.charAt(nextIndex));
            if (nextCharIsVowel) {
                return this.replacementBeforeVowel;
            }
            return this.replacementDefault;
        }

        private boolean isVowel(char ch) {
            return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
        }

        public boolean matches(String context) {
            return context.startsWith(this.pattern);
        }

        public String toString() {
            return String.format("%s=(%s,%s,%s)", this.pattern, Arrays.asList(this.replacementAtStart), Arrays.asList(this.replacementBeforeVowel), Arrays.asList(this.replacementDefault));
        }
    }

    static {
        Scanner scanner = new Scanner(Resources.getInputStream(RESOURCE_FILE), CharEncoding.UTF_8);
        try {
            parseRules(scanner, RESOURCE_FILE, RULES, FOLDINGS);
            scanner.close();
            RULES.forEach((k, v) -> {
                v.sort((rule1, rule2) -> {
                    return rule2.getPatternLength() - rule1.getPatternLength();
                });
            });
        } catch (Throwable th) {
            try {
                scanner.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private static void parseRules(Scanner scanner, String location, Map<Character, List<Rule>> ruleMapping, Map<Character, Character> asciiFoldings) {
        int currentLine = 0;
        boolean inMultilineComment = false;
        while (scanner.hasNextLine()) {
            currentLine++;
            String rawLine = scanner.nextLine();
            String line = rawLine;
            if (inMultilineComment) {
                if (line.endsWith(MULTILINE_COMMENT_END)) {
                    inMultilineComment = false;
                }
            } else if (line.startsWith(MULTILINE_COMMENT_START)) {
                inMultilineComment = true;
            } else {
                int cmtI = line.indexOf(COMMENT);
                if (cmtI >= 0) {
                    line = line.substring(0, cmtI);
                }
                String line2 = line.trim();
                if (line2.isEmpty()) {
                    continue;
                } else if (line2.contains("=")) {
                    String[] parts = line2.split("=");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Malformed folding statement split into " + parts.length + " parts: " + rawLine + " in " + location);
                    }
                    String leftCharacter = parts[0];
                    String rightCharacter = parts[1];
                    if (leftCharacter.length() != 1 || rightCharacter.length() != 1) {
                        throw new IllegalArgumentException("Malformed folding statement - patterns are not single characters: " + rawLine + " in " + location);
                    }
                    asciiFoldings.put(Character.valueOf(leftCharacter.charAt(0)), Character.valueOf(rightCharacter.charAt(0)));
                } else {
                    String[] parts2 = line2.split("\\s+");
                    if (parts2.length != 4) {
                        throw new IllegalArgumentException("Malformed rule statement split into " + parts2.length + " parts: " + rawLine + " in " + location);
                    }
                    try {
                        String pattern = stripQuotes(parts2[0]);
                        String replacement1 = stripQuotes(parts2[1]);
                        String replacement2 = stripQuotes(parts2[2]);
                        String replacement3 = stripQuotes(parts2[3]);
                        Rule r = new Rule(pattern, replacement1, replacement2, replacement3);
                        char patternKey = r.pattern.charAt(0);
                        List<Rule> rules = ruleMapping.computeIfAbsent(Character.valueOf(patternKey), k -> {
                            return new ArrayList();
                        });
                        rules.add(r);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalStateException("Problem parsing line '" + currentLine + "' in " + location, e);
                    }
                }
            }
        }
    }

    private static String stripQuotes(String str) {
        if (str.startsWith(DOUBLE_QUOTE)) {
            str = str.substring(1);
        }
        if (str.endsWith(DOUBLE_QUOTE)) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public DaitchMokotoffSoundex() {
        this(true);
    }

    public DaitchMokotoffSoundex(boolean folding) {
        this.folding = folding;
    }

    private String cleanup(String input) {
        StringBuilder sb = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (!Character.isWhitespace(ch)) {
                char ch2 = Character.toLowerCase(ch);
                Character character = FOLDINGS.get(Character.valueOf(ch2));
                if (this.folding && character != null) {
                    ch2 = character.charValue();
                }
                sb.append(ch2);
            }
        }
        return sb.toString();
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object obj) throws EncoderException {
        if (!(obj instanceof String)) {
            throw new EncoderException("Parameter supplied to DaitchMokotoffSoundex encode is not of type java.lang.String");
        }
        return encode((String) obj);
    }

    @Override // org.apache.commons.codec.StringEncoder
    public String encode(String source) {
        if (source == null) {
            return null;
        }
        return soundex(source, false)[0];
    }

    public String soundex(String source) {
        String[] branches = soundex(source, true);
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (String branch : branches) {
            sb.append(branch);
            index++;
            if (index < branches.length) {
                sb.append('|');
            }
        }
        return sb.toString();
    }

    private String[] soundex(String source, boolean branching) {
        if (source == null) {
            return null;
        }
        String input = cleanup(source);
        Set<Branch> currentBranches = new LinkedHashSet<>();
        currentBranches.add(new Branch());
        char lastChar = 0;
        int index = 0;
        while (index < input.length()) {
            char ch = input.charAt(index);
            if (!Character.isWhitespace(ch)) {
                String inputContext = input.substring(index);
                List<Rule> rules = RULES.get(Character.valueOf(ch));
                if (rules != null) {
                    List<Branch> nextBranches = branching ? new ArrayList<>() : Collections.emptyList();
                    for (Rule rule : rules) {
                        if (rule.matches(inputContext)) {
                            if (branching) {
                                nextBranches.clear();
                            }
                            String[] replacements = rule.getReplacements(inputContext, lastChar == 0);
                            boolean branchingRequired = replacements.length > 1 && branching;
                            for (Branch branch : currentBranches) {
                                for (String nextReplacement : replacements) {
                                    Branch nextBranch = branchingRequired ? branch.createBranch() : branch;
                                    boolean force = (lastChar == 'm' && ch == 'n') || (lastChar == 'n' && ch == 'm');
                                    nextBranch.processNextReplacement(nextReplacement, force);
                                    if (!branching) {
                                        break;
                                    }
                                    nextBranches.add(nextBranch);
                                }
                            }
                            if (branching) {
                                currentBranches.clear();
                                currentBranches.addAll(nextBranches);
                            }
                            index += rule.getPatternLength() - 1;
                            break;
                        }
                    }
                    lastChar = ch;
                }
            }
            index++;
        }
        String[] result = new String[currentBranches.size()];
        int index2 = 0;
        for (Branch branch2 : currentBranches) {
            branch2.finish();
            int i = index2;
            index2++;
            result[i] = branch2.toString();
        }
        return result;
    }
}
