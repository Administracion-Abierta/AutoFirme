package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/util/NameClassSimplifier.class */
public class NameClassSimplifier {
    /* JADX WARN: Code duplicated, block: B:68:0x0085 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:74:0x0018 A[SYNTHETIC] */
    public static NameClass simplify(NameClass nc) {
        Set<StringPair> possibleNames = PossibleNamesCollector.calc(nc);
        Set<String> uris = new HashSet();
        Iterator itr = possibleNames.iterator();
        while (itr.hasNext()) {
            StringPair name = (StringPair) itr.next();
            if (name.localName != PossibleNamesCollector.MAGIC) {
                if (nc.accepts(name) == nc.accepts(name.namespaceURI, PossibleNamesCollector.MAGIC)) {
                    itr.remove();
                } else if (name.namespaceURI != PossibleNamesCollector.MAGIC) {
                    uris.add(name.namespaceURI);
                }
            } else if (name.namespaceURI != PossibleNamesCollector.MAGIC && nc.accepts(name) == nc.accepts(PossibleNamesCollector.MAGIC, PossibleNamesCollector.MAGIC)) {
                itr.remove();
            } else if (name.namespaceURI != PossibleNamesCollector.MAGIC) {
                uris.add(name.namespaceURI);
            }
        }
        if (!nc.accepts(PossibleNamesCollector.MAGIC, PossibleNamesCollector.MAGIC)) {
            possibleNames.remove(new StringPair(PossibleNamesCollector.MAGIC, PossibleNamesCollector.MAGIC));
        }
        NameClass result = null;
        for (String uri : uris) {
            NameClass local = null;
            for (StringPair name2 : possibleNames) {
                if (name2.namespaceURI.equals(uri) && name2.localName != PossibleNamesCollector.MAGIC) {
                    local = local == null ? new SimpleNameClass(name2) : new ChoiceNameClass(local, new SimpleNameClass(name2));
                }
            }
            if (possibleNames.contains(new StringPair(uri, PossibleNamesCollector.MAGIC))) {
                if (local == null) {
                    local = new NamespaceNameClass(uri);
                } else {
                    local = new DifferenceNameClass(new NamespaceNameClass(uri), local);
                }
            }
            if (local != null) {
                result = result == null ? local : new ChoiceNameClass(result, local);
            }
        }
        if (nc.accepts(PossibleNamesCollector.MAGIC, PossibleNamesCollector.MAGIC)) {
            result = result == null ? NameClass.ALL : new NotNameClass(result);
        }
        if (result == null) {
            result = AnyNameClass.NONE;
        }
        return result;
    }
}
