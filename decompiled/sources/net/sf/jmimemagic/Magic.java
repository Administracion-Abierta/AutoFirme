package net.sf.jmimemagic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:net/sf/jmimemagic/Magic.class */
public final class Magic {
    private static boolean initialized = false;
    private static MagicParser magicParser = null;
    private static HashMap<String, ArrayList<MagicMatcher>> hintMap = new HashMap<>();

    private static void addHint(String extension, MagicMatcher matcher) {
        if (hintMap.containsKey(extension)) {
            hintMap.get(extension).add(matcher);
            return;
        }
        ArrayList<MagicMatcher> a = new ArrayList<>();
        a.add(matcher);
        hintMap.put(extension, a);
    }

    public static synchronized void initialize() throws MagicParseException {
        if (!initialized) {
            magicParser = new MagicParser();
            magicParser.initialize();
            for (MagicMatcher matcher : magicParser.getMatchers()) {
                String ext = matcher.getMatch().getExtension();
                if (ext != null && !ext.trim().equals("")) {
                    addHint(ext, matcher);
                } else if (matcher.getMatch().getType().equals("detector")) {
                    String[] exts = matcher.getDetectorExtensions();
                    for (String ext2 : exts) {
                        addHint(ext2, matcher);
                    }
                }
            }
            initialized = true;
        }
    }

    public static MagicMatch getMagicMatch(byte[] data) throws MagicMatchNotFoundException, MagicParseException, MagicException {
        return getMagicMatch(data, false);
    }

    public static MagicMatch getMagicMatch(byte[] data, boolean onlyMimeMatch) throws MagicMatchNotFoundException, MagicParseException, MagicException {
        if (!initialized) {
            initialize();
        }
        Collection<MagicMatcher> matchers = magicParser.getMatchers();
        for (MagicMatcher matcher : matchers) {
            try {
                MagicMatch match = matcher.test(data, onlyMimeMatch);
                if (match != null) {
                    return match;
                }
            } catch (Throwable e) {
                throw new MagicException(e);
            }
        }
        throw new MagicMatchNotFoundException();
    }
}
